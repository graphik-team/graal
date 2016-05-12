package fr.lirmm.graphik.graal.io.chase_bench;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import fr.lirmm.graphik.graal.api.core.Constant;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.factory.DefaultPredicateFactory;

class CSVParser {
    protected static Pattern s_integerPattern = Pattern.compile("(\\+|-)?[0-9]+");
    protected static Pattern s_doublePattern = Pattern.compile("(\\+|-)?[0-9]*\\.[0-9]+");

    public CSVParser() {
    }

    protected boolean readNextField(Reader reader, StringBuilder field, boolean[] isQuoted, boolean[] isLast) throws IOException {
        int c = reader.read();
        if (c == -1)
            return false;
        else {
            field.delete(0, field.length());
            if (c == '"') {
                isQuoted[0] = true;
                c = reader.read();
                while (c != -1) {
                    if (c == '"' && (c = reader.read()) != '"') {
                        if (c == ',')
                            isLast[0] = false;
                        else {
                            isLast[0] = true;
                            if (c == '\r') {
                                c = reader.read();
                                if (c != '\n')
                                    throw new IOException("Invalid line terminator.");
                            }
                            else if (c != '\n')
                                throw new IOException("Quote character (\") encountered within a field.");
                        }
                        return true;
                    }
                    field.append((char)c);
                    c = reader.read();
                }
                throw new IOException("Unterminated string.");
            }
            else {
                isQuoted[0] = false;
                while (c != -1 && c != ',' && c != '\n' && c != '\r') {
                    field.append((char)c);
                    c = reader.read();
                }
                if (c == ',')
                    isLast[0] = false;
                else {
                    isLast[0] = true;
                    if (c == '\r') {
                        c = reader.read();
                        if (c != '\n')
                            throw new IOException("Invalid line terminator.");
                    }
                }
                return true;
            }
        }
    }

    public void parse(Reader reader, Predicate predicate, InputProcessor inputProcessor) throws IOException {
		List<Object> argumentRawForms = new ArrayList<Object>();
        List<Constant.Type> argumentTypes = new ArrayList<Constant.Type>();
        StringBuilder field = new StringBuilder();
        boolean[] isQuoted = new boolean[1];
        boolean[] isLast = new boolean[1];
        inputProcessor.setFactPredicate(predicate);
        while (readNextField(reader, field, isQuoted, isLast)) {
        	String token = field.toString();
            if (isQuoted[0]) {
				argumentTypes.add(Term.Type.LITERAL);
				argumentRawForms.add(token);
            }
            else if (s_integerPattern.matcher(token).matches()) {
				argumentRawForms.add(Integer.parseInt(token));
				argumentTypes.add(Constant.Type.LITERAL);
            }
            else if (s_doublePattern.matcher(token).matches()) {
				argumentRawForms.add(Double.parseDouble(token));
				argumentTypes.add(Term.Type.LITERAL);
            }
            else if (token.startsWith("_:")) {
				argumentRawForms.add(token);
				argumentTypes.add(Term.Type.CONSTANT);
            }
            else  {
				argumentRawForms.add(token);
				argumentTypes.add(Term.Type.CONSTANT);
            }
            if (isLast[0]) {
				inputProcessor.processFact(argumentRawForms, argumentTypes);
				argumentRawForms.clear();
                argumentTypes.clear();
            }
        }
    }

    protected static final String CSV = ".csv";

    public void parse(File directory, InputProcessor inputProcessor) throws IOException {
        final int CSVlength = CSV.length();
        File[] files = directory.listFiles(new FilenameFilter() {
            @Override
			public boolean accept(File dir, String name) {
                int length = name.length();
                if (length < CSVlength)
                    return false;
                for (int index = 0; index < CSVlength; ++index)
                    if (Character.toLowerCase(name.charAt(length - CSVlength + index)) != CSV.charAt(index))
                        return false;
                return true;
            }
        });
        inputProcessor.startProcessing();
        if (files != null) {
            for (File file : files) {
				Predicate predicate = DefaultPredicateFactory.instance().create(
				    file.getName().substring(0, file.getName().length() - CSVlength), 0);
                FileReader reader = new FileReader(file);
                try {
                    parse(reader, predicate, inputProcessor);
                }
                finally {
                    reader.close();
                }
            }
        }
        inputProcessor.endProcessing();
    }

}
