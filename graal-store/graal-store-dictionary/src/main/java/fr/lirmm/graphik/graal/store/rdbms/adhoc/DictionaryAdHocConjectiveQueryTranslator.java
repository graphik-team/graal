package fr.lirmm.graphik.graal.store.rdbms.adhoc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.graal.store.dictionary.DictionaryMapper;
import org.graal.store.dictionary.DictionnaryMappingException;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Constant;
import fr.lirmm.graphik.graal.api.core.Literal;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.store.rdbms.adhoc.AdHocRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.AbstractRdbmsConjunctiveQueryTranslator;
import fr.lirmm.graphik.graal.store.rdbms.adhoc.AdHocConjunctiveQueryTranslator;
import fr.lirmm.graphik.graal.store.rdbms.util.DBColumn;
import fr.lirmm.graphik.graal.store.rdbms.util.DBTable;
import fr.lirmm.graphik.graal.store.rdbms.util.SQLQuery;
import fr.lirmm.graphik.util.URIUtils;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;
import fr.lirmm.graphik.util.string.StringUtils;

public class DictionaryAdHocConjectiveQueryTranslator extends AdHocConjunctiveQueryTranslator {
	
	protected DictionaryMapper dictionaryMapper;
	protected AdHocRdbmsStore store;
	
	private final static String INTEGER_TYPE_NAME = "INT";
	
	public DictionaryAdHocConjectiveQueryTranslator(AdHocRdbmsStore store, DictionaryMapper mapper) {
		super(store);
		dictionaryMapper = mapper;
		this.store = store;
	}
	
	@Override
	public String translateCreateTable(DBTable table) {
		StringBuilder primaryKey = new StringBuilder("PRIMARY KEY (");
		StringBuilder query = new StringBuilder("CREATE TABLE ");
		query.append(StringUtils.escapeDoubleQuote(table.getName()));
		query.append(" (");

		boolean first = true;
		for (DBColumn col : table.getColumns()) {
			if (!first) {
				query.append(", ");
				primaryKey.append(", ");
			}
			query.append(col.getName()).append(" "+INTEGER_TYPE_NAME);
			primaryKey.append(col.getName());
			first = false;
		}
		primaryKey.append(")");
		query.append(',');
		query.append(primaryKey);
		query.append(");");
		return query.toString();
	}
	
	
	@Override
	public SQLQuery translateContainsQuery(Atom atom) throws AtomSetException {
		DBTable table = this.store.getPredicateTable(atom.getPredicate());
		if (table == null) {
			return SQLQuery.hasSchemaErrorInstance();
		}
		Term term;
		int termIndex = -1;
		StringBuilder query = new StringBuilder("SELECT 1 FROM ");
		query.append(table.getName());
		query.append(" WHERE ");

		Iterator<Term> terms = atom.getTerms().iterator();

		term = terms.next(); // TODO: FIX THIS => if arity = 0 -> crash ?!
		++termIndex;
		
		String termId = formatFromColumnType(null,term);
		query.append("term").append(termIndex).append(" = ").append(termId);

		while (terms.hasNext()) {
			term = terms.next();
			++termIndex;
			termId = formatFromColumnType(null,term);
			query.append(" AND ").append(AbstractRdbmsConjunctiveQueryTranslator.PREFIX_TERM_FIELD).append(termIndex)
			     .append(" = ")
			     .append(termId);
		}
		query.append(" LIMIT 1;");
		return new SQLQuery(query.toString());
	}
	
	@Override
	public String formatFromColumnType(DBColumn col, Term term) throws AtomSetException {
		if(term.isVariable()) {
			if(term.toString().startsWith("EE")) {
				return dictionaryMapper.mapExistentialVar(term).toString();
			}
			throw new DictionnaryMappingException("Error term"+term.toString()+" is a Literal");
		}
		
		if(term instanceof Literal) {
			Literal l = (Literal) term;
			if(l.getValue() instanceof Integer) {
				return ""+(Integer) l.getValue();
			}
			else {
				if(l.getDatatype().equals(URIUtils.XSD_INTEGER)) {
					return  ""+Integer.parseInt( (String) ((Literal) term).getValue());	
				}
				throw new DictionnaryMappingException("Error term value is not an integer"+term.toString());
			}
			
		}
		else if(term instanceof Constant ) {
			Constant cons = (Constant) term;
			if(cons.getIdentifier() instanceof Integer) {
				return ""+(Integer) cons.getIdentifier();
			}
		}
		throw new DictionnaryMappingException("Error term"+term.toString()+" is a Literal");
	}
	
	@Override
	public SQLQuery translate(ConjunctiveQuery cquery) throws AtomSetException {
		if (cquery.getAtomSet().isEmpty()) {
			return SQLQuery.emptyInstance();
		}
		AtomSet atomSet = cquery.getAtomSet();

		StringBuilder fields = new StringBuilder();
		StringBuilder tables = new StringBuilder();
		StringBuilder where = new StringBuilder();

		HashMap<Atom, String> tableNames = new HashMap<Atom, String>();
		HashMap<String, String> lastOccurrence = new HashMap<String, String>();

		ArrayList<String> constants = new ArrayList<String>();
		ArrayList<String> equivalences = new ArrayList<String>();
		TreeMap<Term, String> columns = new TreeMap<Term, String>();

		int count = -1;
		CloseableIterator<Atom> it = atomSet.iterator();
		try {
			while (it.hasNext()) {
				Atom atom = it.next();
				String tableName = "atom" + ++count;
				tableNames.put(atom, tableName);
			}

			// Create WHERE clause
			it = atomSet.iterator();
			while (it.hasNext()) {
				Atom atom = it.next();
				String currentAtom = tableNames.get(atom) + ".";

				int position = 0;
				for (Term term : atom.getTerms()) {
					String thisTerm = currentAtom
					                  + AbstractRdbmsConjunctiveQueryTranslator.PREFIX_TERM_FIELD
					                  + position;
//					System.out.println(term.getIdentifier().toString());
					if (term.isConstant()) {
						constants.add(thisTerm + " = " +formatFromColumnType(null,term));
					} else {
						String termIdStr = term.getIdentifier().toString();
						if (lastOccurrence.containsKey(termIdStr)) {
							equivalences.add(lastOccurrence.get(termIdStr) + " = " + thisTerm);
						}
						lastOccurrence.put(termIdStr, thisTerm);
						if (cquery.getAnswerVariables().contains(term))
							columns.put(term, thisTerm + " as " + termIdStr);
					}
					++position;
				}
			}
		} catch (IteratorException e) {
			throw new AtomSetException(e);
		}

		for (String equivalence : equivalences) {
			if (where.length() != 0)
				where.append(" AND ");

			where.append(equivalence);
		}

		for (String constant : constants) {
			if (where.length() != 0)
				where.append(" AND ");

			where.append(constant);
		}

		// Create FROM clause
		DBTable table = null;
		for (Map.Entry<Atom, String> entries : tableNames.entrySet()) {
			if (tables.length() != 0)
				tables.append(", ");

			table = store.getPredicateTableIfExist(entries.getKey().getPredicate());
			if (table == null)
				return SQLQuery.hasSchemaErrorInstance();
			else
				tables.append(table.getName());

			tables.append(" as ");
			tables.append(entries.getValue());
		}

		// Create SELECT clause
		for (Term t : cquery.getAnswerVariables()) {
			if (fields.length() != 0)
				fields.append(", ");

			if (t.isConstant()) {
				fields.append("'");
				fields.append(t.getIdentifier());
				fields.append("'");
			} else {
				fields.append(columns.get(t));
			}
		}

		StringBuilder query = new StringBuilder("SELECT DISTINCT ");
		if (fields.length() > 0)
			query.append(fields);
		else
			query.append("1");

		query.append(" FROM ");
		if (tables.length() > 0)
			query.append(tables);
		else
			query.append(AdHocRdbmsStore.TEST_TABLE_NAME);

		if (where.length() > 0)
			query.append(" WHERE ").append(where);
		return new SQLQuery(query.toString());
	}
	
}
