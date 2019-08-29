import java.util.* ; 
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Test { 

  public static void main (String [] args) { 
    Test trial = new Test() ; 
    StatementIndex statement_index = new StatementIndex () ; 
    statement_index.initialize () ; 
    HashMap<String,LinkedList<Statement>> subject_index = statement_index.index.get(1) ; 
    HashMap<String,LinkedList<Statement>> predicate_index = statement_index.index.get(2) ; 
    HashMap<String,LinkedList<Statement>> object_index = statement_index.index.get(3) ; 
    String ttl = trial.readFileToString("test.ttl") ; 
    Parser parser =  new Parser() ; 
    parser.parse(ttl,statement_index); 
    ArrayList<String> prefix_statements = new ArrayList<String> () ;
    prefix_statements.add("test:<http://test.com/") ;
    prefix_statements.add("owl:<http://open_shit.com/") ; 
    Printer printer = new Printer () ; 
    printer.printIndexWithPrefixes(statement_index,prefix_statements) ; 
    //System.out.println(subject_index) ; 
    //System.out.println(predicate_index) ; 
    //System.out.println(object_index) ; 
    //    LinkedList<Statement> penny_subject=subject_index.get("<http://test.com/playsWith>") ; 
    //System.out.println(penny_subject) ; 
    //    for (int i = 0; i < statement_index.store.size(); i++) {
    //      //System.out.println(i) ; 
    //      Statement penny_statement = statement_index.store.get(i) ; 
    //       penny_statement.show(); 
    //      System.out.println(" ") ; 
    //
    //    }

    // "<http://test.com/Cat> a <http://owl/Class>;  <http:label.com> <http://alabel> ."

    //"<http://ttl.one> <http://ttl.pred> <http://ttl.obj>; <http://ttl.pred_two> <http://ttl.obj_two>. <http://ttl.stwo> <http://ttl.pred> <http://ttl:cat>, <http://ttl:feline>."





  }
  private static String readFileToString(String filePath)
  { String content = ""; 
    try {
      content = new String ( Files.readAllBytes( Paths.get(filePath) ) );
    }
    catch (IOException e)
    {
      e.printStackTrace();
    } 
    return content;
  }
} 
