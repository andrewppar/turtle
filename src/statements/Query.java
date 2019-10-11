import java.util.* ;
import java.io.* ; 

public class Query {
  StatementIndex query_index; 
  ArrayList<String> variables ; 
  LinkedList<HashMap<String,String>> bindings ;

  public Query parseQuery (String query_string) { 
    Parser query_parser = new Parser () ; 
    StatementIndex query_index = new StatementIndex () ; 
    this.query_index = query_index ; 
    query_index.initialize () ;
    query_parser.parse(query_string, query_index) ; 
    this.variables = this.gatherFreeVariables(query_string) ; 
    return this ;
  }

  private ArrayList<String> gatherFreeVariables  (String query_string) { 
    String[] words = query_string.split(" ") ; 
    ArrayList<String> result = new ArrayList () ; 
    for ( int i = 0 ; i < words.length ; i++) { 
      String word = words[i]; 
      if ( isVariable(word)){
        result.add(word) ; 
      } 
    } 
    return result ; 
  } 
  //@todo figure out why I need this in multiple places.
  public static boolean isVariable (String potential_variable) {  // @todo generalize this for every object, not just string
    return  potential_variable.matches("\\?[a-zA-Z1-9]+")  ;
  }

  public void showResults () {
   ArrayList<String>  vars = this.variables ; 
    for (int i = 0 ; i < vars.size() ; i++) { 
      System.out.print(vars.get(i) + " ") ; 
    }
    System.out.println("") ; 
    System.out.println("----------------------------------------------------"); 
    LinkedList<HashMap<String,String>> binding_lists = this.bindings ; 
    for (int i = 0 ; i < binding_lists.size() ; i++) {
      HashMap<String,String>  binding = binding_lists.get(i) ; 
      for (int j = 0 ; j < vars.size() ; j++) { 
        String result = binding.get(vars.get(j)) ; 
        System.out.print(result + " ") ; 
      } 
      System.out.println(""); 
    }
  } 
}

