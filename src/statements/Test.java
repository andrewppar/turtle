import java.util.* ; 


public class Test { 

  public static void main (String [] args) { 
    StatementIndex statement_index = new StatementIndex () ; 
    statement_index.start () ; 
    System.out.println(statement_index) ; 
    HashMap<String,LinkedList<Statement>> subject_index = statement_index.index.get(1) ; 
    HashMap<String,LinkedList<Statement>> predicate_index = statement_index.index.get(2) ; 
    HashMap<String,LinkedList<Statement>> object_index = statement_index.index.get(3) ; 
    System.out.println(predicate_index) ; 
    Parser parser =  new Parser() ; 
    ///This one's buggy
    parser.parse("<http://ttl.one> <http://ttl.pred> <http://ttl.obj>; <http://ttl.pred_two> <http://ttl.obj_two>.  <http://ttl.two>  <http://ttl.pred_three> <http://obj_three>, <http://obj_four>.",statement_index); 
    System.out.println(subject_index) ; 
    System.out.println(predicate_index) ; 
     System.out.println(object_index) ; 

//"<http://ttl.one> <http://ttl.pred> <http://ttl.obj>; <http://ttl.pred_two> <http://ttl.obj_two>. <http://ttl.stwo> <http://ttl.pred> <http://ttl:cat>, <http://ttl:feline>."

    



  }
}
