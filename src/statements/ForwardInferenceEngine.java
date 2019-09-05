import java.util.* ; 

public class ForwardInferenceEngine {

  public void performForwardInference  (StatementIndex  statement_index ) { 
    this.populateSubclasses(statement_index) ; 

  }


  private static void populateSubclasses (StatementIndex statement_index) { 
    //Get all of  the  rdf:type statements
    LinkedList<Statement> type_statements =  statement_index.index.get(2).get("a") ; 
    HashMap<String,HashMap<String,LinkedList<String>>> predicate_index  = statement_index.predicateObjectIndex ; 
    if (predicate_index.containsKey("<http://www.w3.org/2002/07/owl#subClassOf>")) {
      HashMap<String,LinkedList<String>> subject_object_dictionary = predicate_index.get("<http://www.w3.org/2002/07/owl#subClassOf>") ; 

      LinkedList<String> target_classes_surveyed = new LinkedList<String> () ; 
      System.out.println(type_statements) ;
      System.out.println(type_statements.size()) ; 
      int size = type_statements.size() ;

      //Get the  graph for those statements 
      for (int i = 0; i < size  ; i++) { 
        System.out.println("Base Class " + i ) ;
        Statement statement = type_statements.get(i);
        String subject = statement.subject ; 
        String target_class = statement.object ; 
        target_classes_surveyed = add_subclass_expressions_to_quiescence(subject, target_class, statement_index, target_classes_surveyed) ; 
      }
    } 
  }

  private static LinkedList<String> add_subclass_expressions_to_quiescence(String subject, String target_class, StatementIndex statement_index, LinkedList<String> classes_surveyed) { 
    System.out.println("Adding Recursive For") ; 
    System.out.println(subject) ; 
    System.out.println(target_class) ; 
    classes_surveyed.add(target_class) ; 
    //Depth first Adding of statements
    HashMap<String,HashMap<String,LinkedList<String>>> predicate_index  = statement_index.predicateObjectIndex ; 
    HashMap<String,LinkedList<String>> subject_object_dictionary = predicate_index.get("<http://www.w3.org/2002/07/owl#subClassOf>") ; 
    Statement new_statement = new Statement () ; 
    new_statement.addArgs(subject, "a", target_class) ;
    System.out.println(new_statement) ; 
    Boolean statement_added = statement_index.addStatement(new_statement) ; 
    //@todo handle looping taxonomies 
    System.out.println(subject_object_dictionary.containsKey(target_class)) ; 

    if ( statement_added  && subject_object_dictionary.containsKey(target_class)) {
      System.out.println(subject_object_dictionary) ;
      LinkedList<String> new_target_classes = subject_object_dictionary.get(target_class) ; 
      for (int i = 0 ; i < new_target_classes.size() ; i++) {
        String new_target_class  = new_target_classes.get(i) ; 
        //@todo investigate: This could be tail recursive, so it might be better to convert it to a while loop
        classes_surveyed = add_subclass_expressions_to_quiescence(subject, new_target_class, statement_index, classes_surveyed) ; 
      } 
    } 

    return classes_surveyed ;
  } 
}
