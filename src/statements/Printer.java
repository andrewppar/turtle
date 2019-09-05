import java.util.* ;


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
      System.out.println("prefix " + prefix_key + ":" + prefix_value + " . " ) ;

    }
    System.out.println(" ") ; 



    this.printIndexInternals(statement_index, prefix_uris, prefix_dictionary) ; 


  } 

  private void printIndexInternals (StatementIndex statement_index, ArrayList<String> prefix_uris, HashMap<String,String> prefix_dictionary) { 
    HashMap<String,LinkedList<Statement>> subject_index = statement_index.index.get(1); 
    Iterator subject_iterator = subject_index.entrySet().iterator() ; 
    while (subject_iterator.hasNext()) {
      //Looping through subjects
      HashMap.Entry subject_entry = (Map.Entry)subject_iterator.next() ; 
      String subject = ((String) subject_entry.getKey ()); 
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
      this.potentiallyPrintStringWithPrefixes(subject, prefix_uris, prefix_dictionary) ; 
      int spaces = (subject.length() + 1) ; 
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
          this.potentiallyPrintStringWithPrefixes(predicate, prefix_uris, prefix_dictionary) ; 
        }
        else { 
          System.out.format("%"+spaces+"s", ""); 
          System.out.print(predicate + " ") ;
        } 
        for (int i = 0 ; i < predicate_indexed_statements.size(); i++) { 
          Statement statement = predicate_indexed_statements.get(i) ; 
          String object = statement.object ; 
          if ( i == 0 ) {
            this.potentiallyPrintStringWithPrefixes(object, prefix_uris, prefix_dictionary) ; 
          }
          else {
            System.out.print(",") ;
            this.potentiallyPrintStringWithPrefixes( object, prefix_uris, prefix_dictionary) ; 
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

  private void potentiallyPrintStringWithPrefixes(String string_to_print, ArrayList<String> prefix_uris, HashMap<String,String> prefix_dictionary) {
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






