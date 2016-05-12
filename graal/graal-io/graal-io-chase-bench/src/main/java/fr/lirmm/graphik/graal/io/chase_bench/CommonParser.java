package fr.lirmm.graphik.graal.io.chase_bench;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Constant;
import fr.lirmm.graphik.graal.api.core.Literal;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.factory.AtomFactory;
import fr.lirmm.graphik.graal.api.factory.PredicateFactory;
import fr.lirmm.graphik.graal.api.factory.RuleFactory;
import fr.lirmm.graphik.graal.api.factory.TermFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultPredicateFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultRuleFactory;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;

class CommonParser {
	protected final StreamTokenizer m_tokenizer;

	protected enum TokenType {
							  SYMBOL, INTEGER, DOUBLE, INVALID
	};

	protected RuleFactory ruleFactory = DefaultRuleFactory.instance();
	protected AtomFactory atomFactory = DefaultAtomFactory.instance();
	protected TermFactory termFactory = DefaultTermFactory.instance();
	protected PredicateFactory predicateFactory = DefaultPredicateFactory.instance();

	public CommonParser(Reader reader) throws IOException {
        m_tokenizer = new StreamTokenizer(reader);
        m_tokenizer.resetSyntax();
        m_tokenizer.commentChar('#');
        m_tokenizer.eolIsSignificant(false);
        m_tokenizer.whitespaceChars(' ', ' ');
        m_tokenizer.whitespaceChars('\r', '\r');
        m_tokenizer.whitespaceChars('\n', '\n');
        m_tokenizer.whitespaceChars('\t', '\t');
        m_tokenizer.wordChars('_', '_');
        m_tokenizer.wordChars('a', 'z');
        m_tokenizer.wordChars('A', 'Z');
        m_tokenizer.wordChars('0', '9');
        m_tokenizer.wordChars('.', '.');
        m_tokenizer.wordChars('+', '+');
        m_tokenizer.wordChars('-', '-');
		m_tokenizer.wordChars('?', '?');
        m_tokenizer.wordChars('<', '<');
        m_tokenizer.wordChars('>', '>');
        m_tokenizer.quoteChar('"');
        m_tokenizer.nextToken();
    }

	protected static boolean isFirstSymbolChar(char c) {
		return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || c == '-' || c == '_';
	}

	protected static boolean isSymbolChar(char c) {
		return isFirstSymbolChar(c) || ('0' <= c && c <= '9');
	}

	protected static TokenType getTokenType(String token) {
		boolean seenDot = false;
		boolean seenDigitBeforeDot = false;
		boolean seenDigitAfterDot = false;
		int index = 0;
		if (token.charAt(0) == '+' || token.charAt(0) == '-')
			++index;
		for (; index < token.length(); ++index) {
			char c = token.charAt(index);
			if (c == '.') {
				if (seenDot)
					return TokenType.INVALID;
				seenDot = true;
			} else if ('0' <= c && c <= '9') {
				if (seenDot)
					seenDigitAfterDot = true;
				else
					seenDigitBeforeDot = true;
			} else {
				if (!isFirstSymbolChar(token.charAt(0)))
					return TokenType.INVALID;
				for (int inner = 1; inner < token.length(); ++inner)
					if (!isSymbolChar(token.charAt(inner)))
						return TokenType.INVALID;
				return TokenType.SYMBOL;
			}
		}
		if (seenDot) {
			if (seenDigitAfterDot)
				return TokenType.DOUBLE;
			else
				return TokenType.INVALID;
		} else {
			if (seenDigitBeforeDot)
				return TokenType.INTEGER;
			else
				return TokenType.INVALID;
		}
	}

	public Term termFromWord(String token) throws IOException {
		if (token.startsWith("?"))
			return termFactory.createVariable(token.substring(1));
		else if (token.startsWith("_:"))
			return termFactory.createConstant(token); // ,
			                                                       // Constant.Type.LABELED_NULL);
		else {
			TokenType tokenType = getTokenType(token);
			switch (tokenType) {
				case SYMBOL:
					return termFactory.createConstant(token);
				case INTEGER:
					return termFactory.createLiteral(Integer.parseInt(token));
				case DOUBLE:
					return termFactory.createLiteral(Double.parseDouble(token));
				default:
					throw new IOException("Invalid term '" + token + "'.");
			}
		}
	}

	public Term parseTerm() throws IOException {
		if (m_tokenizer.ttype == StreamTokenizer.TT_WORD) {
			String token = m_tokenizer.sval;
			m_tokenizer.nextToken();
			return termFromWord(token);
		} else if (m_tokenizer.ttype == '"') {
			Literal l = termFactory.createLiteral(m_tokenizer.sval);
			m_tokenizer.nextToken();
			return l;
		} else
			throw new IOException("A term was expected at this point.");
	}

	public Atom parseAtom() throws IOException {
		if (m_tokenizer.ttype == StreamTokenizer.TT_WORD) {
			String token = m_tokenizer.sval;
			m_tokenizer.nextToken();
			List<Term> arguments = new ArrayList<Term>();
			if (m_tokenizer.ttype == '=') {
				m_tokenizer.nextToken();
				Term term1 = termFromWord(token);
				Term term2 = parseTerm();
				return atomFactory.create(Predicate.EQUALITY, term1, term2);
			} else {
				if (m_tokenizer.ttype != '(')
					throw new IOException("Expected '('.");
				m_tokenizer.nextToken();
				if (m_tokenizer.ttype != ')') {
					arguments.add(parseTerm());
					while (m_tokenizer.ttype == ',') {
						m_tokenizer.nextToken();
						arguments.add(parseTerm());
					}
					if (m_tokenizer.ttype != ')')
						throw new IOException("Expected ')'.");
				}
				m_tokenizer.nextToken();
				Predicate p = predicateFactory.create(token, arguments.size());
				return atomFactory.create(p, arguments);
			}
		} else {
			Term term1 = parseTerm();
			if (m_tokenizer.ttype != '=')
				throw new IOException("Expected '='.");
			m_tokenizer.nextToken();
			Term term2 = parseTerm();
			return atomFactory.create(Predicate.EQUALITY, term1, term2);
		}
	}

	public Object parseRuleOrAtom() throws IOException {
		List<Atom> before = new ArrayList<Atom>();
		if (m_tokenizer.ttype != StreamTokenizer.TT_WORD
		    || (!"<-".equals(m_tokenizer.sval) && !"->".equals(m_tokenizer.sval))) {
			Atom atom = parseAtom();
			if (m_tokenizer.ttype == StreamTokenizer.TT_WORD && ".".equals(m_tokenizer.sval)) {
				m_tokenizer.nextToken();
				return atom;
			}
			before.add(atom);
			while (m_tokenizer.ttype == ',' || m_tokenizer.ttype == '&') {
				m_tokenizer.nextToken();
				before.add(parseAtom());
			}
		}
		boolean leftToRight;
		if (m_tokenizer.ttype == StreamTokenizer.TT_WORD && "<-".equals(m_tokenizer.sval))
			leftToRight = false;
		else if (m_tokenizer.ttype == StreamTokenizer.TT_WORD && "->".equals(m_tokenizer.sval))
			leftToRight = true;
		else
			throw new IOException("Expected '<-' or '->'.");
		m_tokenizer.nextToken();
		List<Atom> after = new ArrayList<Atom>();
		if (m_tokenizer.ttype != '.') {
			after.add(parseAtom());
			while (m_tokenizer.ttype == ',' || m_tokenizer.ttype == '&') {
				m_tokenizer.nextToken();
				after.add(parseAtom());
			}
		}
		if (m_tokenizer.ttype != StreamTokenizer.TT_WORD || !".".equals(m_tokenizer.sval)) {
			System.out.println("Character: " + (char) m_tokenizer.ttype);
			throw new IOException("Expected '.' at the end of the rule.");
		}
		m_tokenizer.nextToken();
		Atom[] beforeAtoms = before.toArray(new Atom[before.size()]);
		Atom[] afterAtoms = after.toArray(new Atom[after.size()]);
		if (leftToRight)
			return ruleFactory.create(beforeAtoms, afterAtoms);
		else
			return ruleFactory.create(afterAtoms, beforeAtoms);
	}

	public void parse(InputProcessor inputProcessor) throws IOException {
		inputProcessor.startProcessing();
		try {
			while (m_tokenizer.ttype != StreamTokenizer.TT_EOF) {
				Object ruleOrAtom = parseRuleOrAtom();
				if (ruleOrAtom instanceof Rule)
					inputProcessor.processRule((Rule) ruleOrAtom);
				else if (ruleOrAtom instanceof Atom) {
					Atom atom = (Atom) ruleOrAtom;
					List<Object> argumentRawForms = new ArrayList<Object>();
					List<Constant.Type> argumentTypes = new ArrayList<Constant.Type>();
					for (int index = 0; index < atom.getPredicate().getArity(); ++index) {
						Constant constant = (Constant) atom.getTerm(index);
						argumentRawForms.add(constant.getIdentifier());
						argumentTypes.add(constant.getType());
					}
					inputProcessor.setFactPredicate(atom.getPredicate());
					inputProcessor.processFact(argumentRawForms, argumentTypes);
				}
			}
		} finally {
			inputProcessor.endProcessing();
		}
	}
}
