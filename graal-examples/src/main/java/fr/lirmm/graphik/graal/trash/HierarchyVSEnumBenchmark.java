package fr.lirmm.graphik.graal.trash;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.LinkedList;

/**
 * 
 */

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class HierarchyVSEnumBenchmark {

	private static interface Term {};
	private static interface Variable extends Term {};
	private static interface Constant extends Term {};
	private static class ExistentialVariable implements Variable {};
	private static class UniversalVariable implements Variable {} ;
	private static class Literal implements Constant {} ;
	private static class LabelisedConstant implements Constant {};
	
	private static final int LOOP = 9999999;
	private static enum Type {
		CONSTANT, 
		UNIVERSAL_VARIABLE, 
		EXISTENTIAL_VARIABLE, 
		LITERAL
	};
	
	private static class TypeTerm {
    	
		Type type;
    	TypeTerm(Type type) {
    		this.type = type;
    	}
    	
    	boolean isVariable() {
    		return type == Type.UNIVERSAL_VARIABLE || type == Type.EXISTENTIAL_VARIABLE;
    	}
    	
    	boolean isVariableEquals() {
    		return type.equals(Type.UNIVERSAL_VARIABLE) || type.equals(Type.EXISTENTIAL_VARIABLE);
    	}
    	
	};
	
	private static enum Type2 {
		CONSTANT(1), 
		UNIVERSAL_VARIABLE(2), 
		EXISTENTIAL_VARIABLE(4), 
		LITERAL(8);
		
		int value;

		Type2(int value) {
			this.value = value;
		}
	};
	
	private static class Type2Term {
    	
		Type2 type;
    	Type2Term(Type2 type) {
    		this.type = type;
    	}
    
    	
	};
	
	
	
	public static void main(String args[]) {
	
		 //run add
        ThreadMXBean thread = ManagementFactory.getThreadMXBean();
        long nanos;
        long a = 0, a2 = 0, a3 = 0, b =0, c= 0;
        
        for(int i = 0; i<99; ++i) {
            nanos = thread.getCurrentThreadCpuTime();
            a();
            a += (thread.getCurrentThreadCpuTime() - nanos)/1000;
            
            nanos = thread.getCurrentThreadCpuTime();
            a2();
            a2 += (thread.getCurrentThreadCpuTime() - nanos)/1000;
            
            nanos = thread.getCurrentThreadCpuTime();
            a3();
            a3+=(thread.getCurrentThreadCpuTime() - nanos)/1000;
            
            nanos = thread.getCurrentThreadCpuTime();
            b();
            b +=(thread.getCurrentThreadCpuTime() - nanos)/1000;
            
            nanos = thread.getCurrentThreadCpuTime();
            c();
            c +=(thread.getCurrentThreadCpuTime() - nanos)/1000;
            
            System.out.println("#############");
            System.out.println("==         " + a);
            System.out.println("equals     " + a2);
            System.out.println("&|         " + a3);
            System.out.println("instanceof " + b);
            System.out.println("isInstance " + c);
        }
        System.out.println("#### END ####");

	}
		
	public static void a() {
		LinkedList<TypeTerm> list = new LinkedList<TypeTerm>();
		for(int i=0; i<LOOP; ++i) {
			switch(i%4) {
			case 0:
				list.add(new TypeTerm(Type.CONSTANT));
				break;
			case 1:
				list.add(new TypeTerm(Type.UNIVERSAL_VARIABLE));
				break;
			case 2:
				list.add(new TypeTerm(Type.EXISTENTIAL_VARIABLE));
				break;
			case 3:
				list.add(new TypeTerm(Type.LITERAL));
				break;
			}
		}
		
		int j = 0;
		for(TypeTerm t : list) {
			if(t.isVariable()) {
				++j;
			}
		}
	}
	
	public static void a2() {
		LinkedList<TypeTerm> list = new LinkedList<TypeTerm>();
		for(int i=0; i<LOOP; ++i) {
			switch(i%4) {
			case 0:
				list.add(new TypeTerm(Type.CONSTANT));
				break;
			case 1:
				list.add(new TypeTerm(Type.UNIVERSAL_VARIABLE));
				break;
			case 2:
				list.add(new TypeTerm(Type.EXISTENTIAL_VARIABLE));
				break;
			case 3:
				list.add(new TypeTerm(Type.LITERAL));
				break;
			}
		}
		
		int j = 0;
		for(TypeTerm t : list) {
			if(t.isVariableEquals()) {
				++j;
			}
		}
	}
	
	public static void a3() {
		LinkedList<Type2Term> list = new LinkedList<Type2Term>();
		for(int i=0; i<LOOP; ++i) {
			switch(i%4) {
			case 0:
				list.add(new Type2Term(Type2.CONSTANT));
				break;
			case 1:
				list.add(new Type2Term(Type2.UNIVERSAL_VARIABLE));
				break;
			case 2:
				list.add(new Type2Term(Type2.EXISTENTIAL_VARIABLE));
				break;
			case 3:
				list.add(new Type2Term(Type2.LITERAL));
				break;
			}
		}
		
		int j = 0;
		for(Type2Term t : list) {
			if(((t.type.value & Type2.EXISTENTIAL_VARIABLE.value)| (t.type.value & Type2.UNIVERSAL_VARIABLE.value)) != 0) {
				++j;
			}
		}
	}
	

		
	public static void b() {
		LinkedList<Term> list = new LinkedList<Term>();
		for(int i=0; i<LOOP; ++i) {
			switch(i%4) {
			case 0:
				list.add(new LabelisedConstant());
				break;
			case 1:
				list.add(new UniversalVariable());
				break;
			case 2:
				list.add(new ExistentialVariable());
				break;
			case 3:
				list.add(new Literal());
				break;
			}
		}
		
			int j = 0;
			for(Term t : list) {
				if(t instanceof Variable) {
					++j;
				}
			}
	}
	
	public static void c() {
		LinkedList<Term> list = new LinkedList<Term>();
		for(int i=0; i<LOOP; ++i) {
			switch(i%4) {
			case 0:
				list.add(new LabelisedConstant());
				break;
			case 1:
				list.add(new UniversalVariable());
				break;
			case 2:
				list.add(new ExistentialVariable());
				break;
			case 3:
				list.add(new Literal());
				break;
			}
		}
		
			int j = 0;
			for(Term t : list) {
				if(Variable.class.isInstance(t)) {
					++j;
				}
			}
	}
	

}
