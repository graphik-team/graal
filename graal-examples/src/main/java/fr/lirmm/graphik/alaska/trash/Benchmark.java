package fr.lirmm.graphik.alaska.trash;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import parser.DatalogGrammar;
import parser.ParseException;
import parser.TERM_TYPE;
import parser.TermFactory;
import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.NegativeConstraint;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.store.rdbms.DefaultRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.IRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.NoConstraintRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.driver.MysqlDriver;
import fr.lirmm.graphik.graal.store.rdbms.driver.RdbmsDriver;
import fr.lirmm.graphik.obda.io.dlgp.AbstractDlgpListener;
import fr.lirmm.graphik.util.stream.ObjectReader;

/**
 * 
 */

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class Benchmark {
    
    private static String baseName = "store_alaska";
    
    
    private static LinkedList<Atom> buffer = new LinkedList<Atom>();

    static AbstractDlgpListener parser = new AbstractDlgpListener() {
                
        private LinkedList<Atom> set = buffer;

        @Override
        protected void createAtom(DefaultAtom atom) {
                this.set.add(atom);
 
        }

		@Override
		protected void createQuery(DefaultConjunctiveQuery query) {
		}

		@Override
		protected void createRule(DefaultRule basicRule) {
		}

		@Override
		protected void createNegConstraint(NegativeConstraint negativeConstraint) {
			// TODO implement this method
			throw new Error("This method isn't implemented");
		}
    };
    
    public static void main(String[] args) throws SQLException, FileNotFoundException, ParseException, AtomSetException {
    	
    	//File file = new File("/tmp/test.db");
    	//RdbmsDriver d = new SqliteDriver(file);
    	RdbmsDriver d = new MysqlDriver("localhost", baseName, "root", "root");
    	init(d.getConnection());
    	d = new MysqlDriver("localhost", baseName, "root", "root");
    	IRdbmsStore store = new NoConstraintRdbmsStore(d);
    	
    	//IWriteableStore store = new NoConstraintRdbmsStore(new MysqlDriver("localhost", baseName, "root",
         //       "root"));
    	
    	LinkedListAtomSet buffer = new LinkedListAtomSet();
       
        FileReader reader = new FileReader(args[0]);
        TermFactory fac = new TermFactory() {
            @Override
            public Term createTerm(TERM_TYPE termType, Object term) {
                return new Term(term, Term.Type.valueOf(termType.toString()));
            }
            
        };
        DatalogGrammar dlpGrammar = new DatalogGrammar(fac, reader);
        dlpGrammar.addParserListener(parser);
        dlpGrammar.document();
        
        
        //run add
        ThreadMXBean thread = ManagementFactory.getThreadMXBean();
        long nanos = thread.getCurrentThreadCpuTime();
        long time = System.nanoTime();
        
        store.add(buffer.iterator());
        
        System.out.println((System.nanoTime() - time)/1000000 + " " + (thread.getCurrentThreadCpuTime() - nanos)/1000000);
        
        
    }
    
    
    
 
    
    
    private static void init(Connection conn) throws SQLException {

       
        Statement stmt = conn.createStatement();
        
        String sql = "DROP DATABASE "+baseName;
        try {
        stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println(e);
        }
        sql = "CREATE DATABASE "+baseName;
        stmt.executeUpdate(sql);
    }
    
    
    
    
    
    public static void oldmain(String[] args ) throws FileNotFoundException, ParseException, AtomSetException, InterruptedException, SQLException{
    	Connection connection = null;
    	try {
            //init
    		IRdbmsStore store = new DefaultRdbmsStore(new MysqlDriver("localhost", baseName, "root",
                    "root"));
            
            String dbFile = "test.db";

            System.out.println("test");
    		Class.forName("org.sqlite.JDBC");
    		connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
    		//init(connection);
    		
    		//AbstractClassicRdbmsStore.createDatabaseSchema(connection);
    		//ClassicRdbmsStore store = new MysqlClassicRdbmsStore("localhost", baseName, "root",
             //       "root");
            System.out.println("test2");
            
            LinkedListAtomSet buffer = new LinkedListAtomSet();
           
            FileReader reader = new FileReader(args[0]);
            TermFactory fac = new TermFactory() {
                @Override
                public Term createTerm(TERM_TYPE termType, Object term) {
                    return new Term(term.toString(), Term.Type.valueOf(termType.toString()));
                }
                
            };
            DatalogGrammar dlpGrammar = new DatalogGrammar(fac, reader);
            dlpGrammar.addParserListener(parser);
            dlpGrammar.document();
            
            ObjectReader it = buffer.iterator();
            Atom[] array = new Atom[buffer.size()];
            int i = -1;
            for(Atom a : buffer) {
                array[++i] = a;
            }
            
            //run add
            ThreadMXBean thread = ManagementFactory.getThreadMXBean();
            long nanos = thread.getCurrentThreadCpuTime();
            long time = System.nanoTime();
            
           // store.add(it);
            
            System.out.println(System.nanoTime() - time);
            System.out.println((thread.getCurrentThreadCpuTime() - nanos));
            
            /*BasicStringFormat format = new BasicStringFormat();
            IQuery query = new ConjunctiveQuery(format.parse("p1(X,Y).p2(Y,Z)"));
            
            System.out.println("\n############\nQUERY\n#############\n");
            //run query
           thread = ManagementFactory.getThreadMXBean();
            nanos = thread.getCurrentThreadCpuTime();
            time = System.nanoTime();
            
            ISubstitutionReader subs = store.execute(query);
            int nbsub=0;
            for(ISubstitution sub : subs) {++nbsub;}
            
            System.out.println(System.nanoTime() - time);
            System.out.println("CPU TIME = " + (thread.getCurrentThreadCpuTime() - nanos));
            System.out.println(nbsub);*/
            
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
        	if(null != connection) {
					connection.close();

        	}
        }
        
    }
}
