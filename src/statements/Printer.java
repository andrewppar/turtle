import java.util.* ;

//@START we should print composite objects in a more verbose way. 

public class Printer { 

  // For now this doesn't have any slots
  // in the future we may want to select a portion 
  // of the index to print.
  // // @todo consider moving this over to the  statement index.t
  // 
  // I also think that this is the wrong name for the class. 
  // There are two sides of what can be done with a statement index: 
  // 1. We can parse a file into it
  // 2. We can use it to analyze ttl in different ways, e.g. 
  //    2a. Produce files for parallel consumption
  //    2b. Produce files that are as compressed wrt notation as possible
  //    2c. Analyze how different statements are related. 
  //    2d. Auto generate prefixes
  //
  // So I don't think Printer is the right name for this class 
  // I also don't think that the example methods described above belong
  // in Parser or StatementIndex. 
  //  
  private StatementIndex statement_index ; 
  private int spaces_to_print ; 
  public void printIndex(StatementIndex statement_index)  { 
    ArrayList<String> prefix_uris = new ArrayList<String> ()  ; 
    prefix_uris = null ; 
    HashMap<String, String> prefix_dictionary = new HashMap<String,String> () ; 
    this.printIndexInternals(statement_index, prefix_uris, prefix_dictionary) ; 
  } 

  public void printIndexWithPrefixes (StatementIndex statement_index, ArrayList<String> prefix_statements ) {
    //Maybe we should parse a string into prefix_statements, but for now, we can leave this as the interface. 
    ArrayList<String> prefix_uris = new ArrayList<String> () ; 
    HashMap<String,String> prefix_dictionary = new HashMap<String,String> () ; 
    for (int i = 0 ; i < prefix_statements.size() ; i++) { 
      String prefix_statement = prefix_statements.get(i) ;
      String[] prefix_uri_pair =  prefix_statement.split(":",2) ; 
      String prefix = prefix_uri_pair[0]; 
      String prefix_uri = prefix_uri_pair[1] ; 
      prefix_uris.add(prefix_uri) ; 
      prefix_dictionary.put(prefix_uri,prefix) ; 
    }
    Iterator prefix_iterator = prefix_dictionary.entrySet().iterator() ; 
    while (prefix_iterator.hasNext()) {
      HashMap.Entry prefix_entry = (Map.Entry)prefix_iterator.next() ; 
      String prefix_key = ((String) prefix_entry.getValue ()) ; 
      String prefix_value = ((String) prefix_entry.getKey ()) ; 
      System.out.println("@prefix " + prefix_key + ":" + prefix_value + " . " ) ;

    }
    System.out.println(" ") ; 



    this.printIndexInternals(statement_index, prefix_uris, prefix_dictionary) ; 


  } 

  private void printIndexInternals (StatementIndex statement_index, ArrayList<String> prefix_uris, HashMap<String,String> prefix_dictionary) { 
    this.spaces_to_print = 0 ; 
    HashMap<String,LinkedList<Statement>> subject_index = statement_index.index.get(1); 
    Iterator subject_iterator = subject_index.entrySet().iterator() ; 
    while (subject_iterator.hasNext()) {
      //Looping through subjects
      HashMap.Entry subject_entry = (Map.Entry)subject_iterator.next() ; 
      String subject = ((String) subject_entry.getKey ()); 
      this.spaces_to_print = (subject.length() + 1) ; 
      LinkedList<Statement> subject_indexed_statements = ((LinkedList<Statement>)subject_entry.getValue()) ; 
      HashMap predicate_index_for_subject = new HashMap<String,LinkedList<Statement>> () ; 
      //Populate the predicate_index_for_suject @todo put this in it's own method.
      for (int i = 0; i <  subject_indexed_statements.size(); i++) { 
        Statement statement = subject_indexed_statements.get(i)   ; 
        String pred = statement.predicate ;

        if ( predicate_index_for_subject.containsKey(pred)) { 
          LinkedList<Statement> predicate_statements = (LinkedList<Statement>) predicate_index_for_subject.get(pred) ; 
          predicate_statements.add(statement) ; 
        }
        else { 
          LinkedList<Statement> predicate_statements = new LinkedList<Statement> () ; 
          predicate_statements.add(statement) ;
          predicate_index_for_subject.put(pred,predicate_statements)  ; 
        } 
      } 
      this.potentially_print_string_with_prefixes(subject, prefix_uris, prefix_dictionary, statement_index) ; 
      Iterator predicate_iterator = predicate_index_for_subject.entrySet().iterator() ; 
      int count = 0  ; 
      while (predicate_iterator.hasNext()) {
        //Looping through predicates
        HashMap.Entry predicate_entry =  (Map.Entry)predicate_iterator.next() ; 
        String predicate = ((String)predicate_entry.getKey()); 
        //        System.out.println("HERE") ; 
        //        System.out.println(predicate) ;
        LinkedList<Statement> predicate_indexed_statements = ((LinkedList<Statement>)predicate_entry.getValue()); 
        if (count == 0 ) { 
          this.potentially_print_string_with_prefixes(predicate, prefix_uris, prefix_dictionary, statement_index) ; 
        }
        else { 
          System.out.format("%"+spaces_to_print+"s", ""); 
          System.out.print(predicate + " ") ;
        } 
        for (int i = 0 ; i < predicate_indexed_statements.size(); i++) { 
          Statement statement = predicate_indexed_statements.get(i) ; 
          String object = statement.object ; 
          if ( i == 0 ) {
            this.potentially_print_string_with_prefixes(object, prefix_uris, prefix_dictionary, statement_index) ; 
          }
          else {
            System.out.print(",") ;
            this.potentially_print_string_with_prefixes( object, prefix_uris, prefix_dictionary, statement_index) ; 
          }
        }
        if (predicate_iterator.hasNext()) { 
          System.out.println(" ; ") ; 
        }
        else {
          System.out.println(" . ")  ; 
          System.out.println(" ") ;  
        }

        count = count + 1 ;
      } 
    }

  }

  private void potentially_print_string_with_prefixes(String string_to_print, ArrayList<String> prefix_uris, HashMap<String,String> prefix_dictionary, StatementIndex statement_index) {
    if (string_is_composite_key(string_to_print)) { 
      this.print_composite_entity(string_to_print, statement_index, this.spaces_to_print)  ; 
    } 
    else {
      this.potentially_print_non_composite_entity_with_prefixes(string_to_print, prefix_uris, prefix_dictionary) ; 

    }
  }

  private Boolean string_is_composite_key (String string_to_print) {
    return string_to_print.matches("^co:.*");
  }

  private void print_composite_entity(String key, StatementIndex statement_index, int spaces) { 
    CompositeEntity composite_entity = statement_index.compositeEntityIndex.get(key) ; 
    //System.out.println(key) ; 
    //System.out.println(statement_index.compositeEntityIndex) ; 
    //System.out.println(composite_entity); 
    //System.out.println(spaces) ; 
    composite_entity.print(spaces) ; 
  } 

  private void potentially_print_non_composite_entity_with_prefixes(String string_to_print, ArrayList<String> prefix_uris, HashMap<String,String> prefix_dictionary) {
    if ( prefix_uris == null)  { 
      System.out.print(string_to_print + " ") ; 
    }
    else {  
      String longest_prefix_match = new String () ; 
      longest_prefix_match = "" ;
      for (int i = 0 ; i < prefix_uris.size() ; i++) {
        String potential_prefix = prefix_uris.get(i) ; 
        if ( string_to_print.indexOf(potential_prefix) == 0 ) {
          if ( longest_prefix_match.length() < potential_prefix.length () ) { 
            longest_prefix_match = potential_prefix  ;
          }
        }
      }
      if ( longest_prefix_match == "" ) { 
        System.out.print(string_to_print + " " ) ; 
      }
      else { 
        String result_string_with_carat = new String () ;
        String result_string = new String () ;
        String prefix = prefix_dictionary.get(longest_prefix_match) ; 
        //      System.out.println("longest_prefix_match") ; 
        //      System.out.println(longest_prefix_match) ; 
        //      System.out.println("prefix") ; 
        //      System.out.println(prefix) ; 

        result_string_with_carat = string_to_print.replace(longest_prefix_match, (prefix + ":"))  ; 
        result_string = result_string_with_carat.substring(0 , result_string_with_carat.length() - 1) ;

        System.out.print(result_string + " ") ; 
      } 
    } 
  }
}






