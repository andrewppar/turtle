import java.util.* ; 


public class Test { 

  public static void main (String [] args) { 
    Statement state_1 = new Statement () ; 
    state_1.addArgs("Penny","loves","Obi") ; 
    Statement state_2 = new  Statement () ; 
    state_2.addArgs("Penny", "loves", "Mama") ; 
    Statement state_3 = new Statement () ; 
    state_3.addArgs("Obi","loves","Mama") ; 
    StatementIndex statement_index = new StatementIndex () ; 
    statement_index.start () ; 
    statement_index.addStatement(state_1) ; 
    statement_index.addStatement(state_2) ; 
    statement_index.addStatement(state_3) ; 
    System.out.println(statement_index) ; 
    System.out.println(state_1) ; 
    HashMap<String,LinkedList<Statement>> subject_index = statement_index.index.get(1) ; 
    System.out.println(subject_index.get("Penny")) ; 
    HashMap<String,LinkedList<Statement>> predicate_index = statement_index.index.get(2) ; 
    System.out.println(predicate_index) ; 
    System.out.println(predicate_index.get("loves")) ; 
    Parser parser =  new Parser() ; 
    parser.parse("<http://ttl.one> <http://ttl.pred> <http://ttl.obj>; <http://ttl.pred_two> <http://ttl.obj_two>",statement_index); 
    System.out.println(subject_index) ; 
    System.out.println(predicate_index) ; 



  }
}
