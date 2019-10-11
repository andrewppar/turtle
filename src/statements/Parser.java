import java.text.CharacterIterator ; 
import java.text.StringCharacterIterator ; 
import java.util.* ; 

public class Parser {
  private HashMap<String,String> prefix_dictionary ; 
  private String prefix_key ; 
  private String current_object ; 
  private String current_subject ; 
  private String current_predicate ; 
  private String current_item ; 
  private Boolean within_uri ; 
  private Boolean within_prefix ; 
  private Boolean uri_has_been_prefixed ; 
  private Boolean within_comment ; 
  private Boolean within_prefix_dictionary_entry ; 
  private String  spaces_to_print ; 
  //Composite Objects
  private Boolean within_composite_entity ; 
  private CompositeEntity current_composite_entity ; 
  private String current_composite_predicate ; 
  private String current_composite_object ; 


  public void parse (String contents, StatementIndex statement_index) { 
    CharacterIterator iterator = new StringCharacterIterator (contents)  ;
    this.current_object    = ""; 
    this.current_subject   = ""; 
    this.current_predicate = ""; 
    this.current_item    = ""; 
    this.prefix_dictionary = new  HashMap<String,String> () ; 
    this.prefix_dictionary.put("owl","<http://www.w3.org/2002/07/owl#>") ; 
    this.prefix_dictionary.put("rdfs","<http://www.w3.org/2000/01/rdf-schema#>") ;


    this.within_comment    = false ; 
    this.within_uri =false  ;
    this.within_prefix=false; 
    this.uri_has_been_prefixed = false;
    this.within_prefix_dictionary_entry = false ; 
    this.spaces_to_print = "" ; 
    // @todo use this to print restrictions reasonably
    //Composite Entity 
    this.within_composite_entity  = false ; 
    this.current_composite_predicate = "" ; 
    this.current_composite_object = "" ; 
    this.current_composite_entity  = new CompositeEntity () ;
    while ( iterator.current() != CharacterIterator.DONE ) { 
      Character character = iterator.current() ; 
      //System.out.println("NEXT");
      //System.out.println(character) ; 
      //System.out.println("current_item") ; 
      //System.out.println(this.current_item)  ; 
      //System.out.println("current_subject")  ; 
      //System.out.println(this.current_subject)  ; 
      //System.out.println("current_predicate"); 
      //System.out.println(this.current_predicate); 
      //System.out.println("current_object")  ; 
      //System.out.println(this.current_object)  ; 
      //System.out.println("within_uri" ) ; 
      //System.out.println(this.within_uri) ; 
      //System.out.println("prefix_key"); 
      //System.out.println(this.prefix_key) ;
      //System.out.print("within_prefix    ") ; 
      //System.out.println(this.within_prefix) ;
      //System.out.println("within_composite_entity " + this.within_composite_entity) ;
      //System.out.println(this.current_composite_predicate) ; 
      //System.out.println(this.current_composite_object) ; 
      if ( this.within_comment ) { 
        //System.out.println("in  comment") ; 
        this.potentially_update_comment_state(character)  ; 
      } 
      else if ( this.within_prefix_dictionary_entry ) {
        //System.out.println("in prefix dictionary entry") ;
        this.potentially_update_prefix_entry(character)  ; 
      } 
      else if ( this.within_uri ) { 
        //System.out.println("in  uri") ; 
        this.update_current_uri(character, statement_index) ; 

      } 
      else if (this.current_item.equals("@prefix")) {
        //System.out.println("Found It") ;
        this.within_prefix_dictionary_entry = true ;  
        this.current_item = ""  ; 
      }
      else if (this.within_prefix ) { 
        this.update_prefix_state(character, statement_index) ;
      } 
      else {
        if (character == ']') {
          //System.out.println("Finished Composite Object"); 
          //System.out.println(this.current_composite_predicate) ; 
          //System.out.println(this.current_composite_object) ; 
          //System.out.println(this.current_composite_entity) ;  
          this.current_composite_entity.updateCompositeEntityArgs(this.current_composite_predicate, this.current_composite_object) ; 
          String composite_id = this.store_composite_entity(this.current_composite_entity, statement_index) ; 
          this.current_item = composite_id ; 
          this.current_object = this.current_item ;
          //System.out.println(this.current_item) ;
          //System.out.println(this.current_subject) ; 
          //System.out.println(this.current_predicate); 
          //System.out.println(this.current_object); 
          this.within_composite_entity  = false ; 
          this.update_parser_with_uri(' ', statement_index) ; 
          this.current_composite_entity = new CompositeEntity () ; 
          this.within_composite_entity = false ; 
        }
        else if (character == '[' ) {
          //System.out.println("Starting Composite Object") ; 
          this.within_composite_entity = true ; 
          //System.out.println(this.current_subject)  ; 
          //System.out.println(this.current_predicate) ; 
          //System.out.println(this.current_object) ; 
          //System.out.println("WITHIN_PREFIX?  " + this.within_prefix ) ; 
          this.current_item = "" ;
        }
        else if (character == ';' ) { 
          this.end_statement(character, statement_index) ; 
        }
        else if (character == ',') { 
          //System.out.println("Comma") ;
          this.end_statement(character, statement_index) ; 
        } 
        else if (character == '.') { 
          this.end_statement(character, statement_index) ; 
        }
        else if (character == '<') { 
          this.current_item = this.current_item + character ; 
          this.within_uri = true ; 
        } 
        else if (character == '#') { 
          this.within_comment = true ; 
        } 
        else if (character == ' '  || character  == '\n') {
          if  (this.current_item != "") {  
            //System.out.println("HERE") ; 
            this.update_parser_with_uri(character,statement_index) ; 
          }
        }
        else {
         // if (!(this.within_composite_entity)) { //It is pretty obscure why this has been added, document this bro.
         //   this.within_prefix = true  ; 
         // }

          this.within_prefix = true ; 
          this.current_item=this.current_item + character ; 
        }
      } 
      iterator.next () ; 
    }
  }

  private void potentially_update_comment_state (Character character) { 
    if (character == '\n') { 
      this.within_comment = false ; 

    }

  }

  private void potentially_update_prefix_entry(Character character) {
    if (this.within_uri) { 
      if (character == '>') { 
        this.within_uri = false ; 
        this.current_item = this.current_item + character ; 
        this.prefix_dictionary.put(this.prefix_key, this.current_item) ;  
        this.within_prefix_dictionary_entry =  false ; 
        this.prefix_key = "" ; 
        this.current_item = "" ; 
      }
      else {
        this.current_item = this.current_item + character  ; 
      }
    } 
    else if (character  == '<') {
      this.within_uri = true; 
      this.current_item = this.current_item +  character ;  
    }
    else if (character == ':')  {
      this.prefix_key = this.current_item ; 
      this.current_item = "" ; 
    } 
    else if (character == '.' || character == '\n') { 
      this.prefix_dictionary.put(this.prefix_key, this.current_item) ; 
      this.prefix_key = ""  ; 
      this.current_item = "" ; 
      this.within_prefix_dictionary_entry = false ;  
    }
    else if (character == ' ') {
    }
    else {
      this.current_item = this.current_item + character ; 

    }
  } 

  private void update_current_uri (Character character, StatementIndex statement_index) { 
    //System.out.println(this.current_item) ;
    this.current_item = this.current_item + character ; 
    if (character == '>' ) {
      //System.out.println("ANOTHER"); 
      this.update_parser_with_uri(character,statement_index) ; 
      this.within_uri=false  ;
      //System.out.println("IN") ; 
      //System.out.println(this.current_item) ; 

    }
  }


  private void update_parser_with_uri (Character character, StatementIndex statement_index)  { 
    if (this.within_composite_entity) {
      //System.out.println("Updating Parser") ; 
      //System.out.println(this.current_item) ;
      //System.out.println(this.current_composite_predicate) ; 
      //System.out.println(this.current_composite_object) ; 
      if (this.current_composite_predicate.equals("")) {
        //System.out.println(this.current_subject) ;
        //System.out.println("Adding Composite Predicate") ; 
        //System.out.println(this.current_item) ; 
        this.current_composite_predicate = this.current_item  ;
        this.current_item = "" ; 
      }
      else if (this.current_composite_object == "") { 
        //System.out.println(this.current_subject) ; 
        //System.out.println("Adding Composite Object") ; 
        this.current_composite_object = this.current_item ; 
        this.current_item = ""; 
      }
      else { 
        // Add composite statement to object
        this.current_composite_entity.updateCompositeEntityArgs(this.current_composite_predicate, this.current_composite_object) ; 
        //System.out.println("Composite Statement added to composite entity") ; 
        //System.out.println(this.current_composite_predicate) ; 
        //System.out.println(this.current_composite_object) ; 
        this.current_composite_predicate = "" ; 
        this.current_composite_object = "" ; 
      } 
    }
    else { 
      if  ( this.current_subject.equals("")) { 
        //if there's no subject set the subject to current_item
        //System.out.println("Adding Subject"); 
        this.current_subject  = this.current_item  ; 
        //System.out.println(this.current_subject) ; 
        this.current_item = ""  ; 
      }
      else if (this.current_predicate.equals("")) {
        // if there is  a subject but no predicate, set it to current_item
        //System.out.println("Adding Predicate"); 
        this.current_predicate = this.current_item ; 
        //System.out.println(this.current_predicate) ; 
        this.current_item = ""  ; 
      }
      else if (this.current_object.equals("")) { 
        // if there is a subject and predicate but no object set it to current_item
        //System.out.println("Adding Object"); 
        //System.out.println(this.current_item) ;
        this.current_object  = this.current_item  ; 
        //System.out.println(this.current_object) ; 
        this.current_item = ""  ; 
      }
      else { 
        // Index the statement and reset the parser
        Statement statement = new Statement () ; 
        statement.addArgs(this.current_subject, this.current_predicate, this.current_object) ; 
        //System.out.println("Adding Statemetn in UPDATE"); 
        //System.out.println(statement); 
        Boolean statement_added = statement_index.addStatement(statement) ; 
        this.reset(character) ; 
      }
    }
  }

  private void update_prefix_state (Character character, StatementIndex statement_index ) {  
        //System.out.println("HERE " + character) ;
        //System.out.println(this.current_item) ;
        //System.out.println(this.prefix_dictionary) ;
    if (this.current_item.equals("a") && character == ' ')  { 
          //System.out.println("WOW") ; 
      this.update_parser_with_uri(character,statement_index ) ; 
      this.within_prefix = false ; 
    } 
    else if ( character == ':' )  { 
      String expansion = this.prefix_dictionary.get(this.current_item) ; 
      if (expansion == null) { 
        throw new NullPointerException("No prefix defined for " + this.current_item) ; 
      }
      String real_prefix =  expansion.substring(0, (expansion.length() - 1)) ; 
      this.within_prefix  = false ; 
      this.current_item = real_prefix ; 
      this.uri_has_been_prefixed=true ; 
    }
    else if ( character == '.' || character == '\n' ) { 
      //System.out.println("errorish case") ;

      //This is kind of an error case, maybe 
      //we should throw an error here.
      this.within_prefix = false ; 
      this.current_item = "" ; 
    }
    else if (character == ' ' || character == ',' ) { // maybe this --> || character == ';' ) { 
      //System.out.println("space case") ;
      //System.out.println(this.uri_has_been_prefixed) ; 
      if (this.uri_has_been_prefixed) {
        this.current_item = this.current_item  + '>' ; // if this was generated from a prefix there's no > to read
        this.uri_has_been_prefixed = false ;
      }
      this.add_entry_to_state(this.current_item) ;  
      this.current_item = ""  ; 
      this.within_prefix = false ; 
    }
    else { 
      //System.out.println("cont") ;
      this.current_item = this.current_item + character   ; 
    } 
  } 

  private void add_entry_to_state (String entry) { 
    //System.out.println("Adding To Entry State") ;  

    // Handle the case where we are in a composite entity
    if (this.within_composite_entity ) { 
      if (this.current_composite_predicate == "" ) { 
        this.current_composite_predicate = entry ; 
      }
      else if  (this.current_composite_object == "") { 
        this.current_composite_object = entry ;

      }
      else { 
        //@todo Add an error 
      } 
    } 
    // we are not in a composite entity
    else { //(!(this.within_prefix)) { 
      if (this.current_subject.equals("")) { 
        //System.out.println("Not adding composite_entity"); 
        //System.out.println(this.current_subject) ; 
        this.current_subject = entry  ; 
      }
      else if (this.current_predicate.equals(""))  { 
        this.current_predicate = entry  ; 
      }
      else if (this.current_object.equals("")) {
        //System.out.println("Adding Object"); 
        this.current_object  = entry  ; 
      }
      else {
        //@todo add an error 
        //System.out.println("Traffic Jam: ") ; 
        //System.out.println("New object: "+entry) ; 
        //System.out.println("Subject: " + this.current_subject) ; 
        //System.out.println("Predicate: " + this.current_predicate) ; 
        //System.out.println("Object: " + this.current_object) ; 
      }

    }
    //else {
    //}
    }

    private void end_statement (Character character, StatementIndex statement_index) { 
      if ( this.within_composite_entity) {
        this.end_composite_statement() ; 
      } else {
        this.end_atomic_statement (character, statement_index) ; 
      } 
    }

    private void end_composite_statement () {
      //System.out.println("Ending Composite Statement") ; 
      //System.out.println(this.current_composite_predicate) ; 
      //System.out.println(this.current_composite_object) ; 
      CompositeEntityStatement composite_statement = new CompositeEntityStatement () ; 
      String pred = this.current_composite_predicate ; 
      String obj  = this.current_composite_object ; 
      this.current_composite_entity.updateCompositeEntityArgs(pred, obj) ; 
      this.current_composite_predicate = "" ; 
      this.current_composite_object = ""  ; 
    }

    private void end_atomic_statement (Character character, StatementIndex statement_index) { 
      String subject = this.current_subject ; 
      String predicate = this.current_predicate ; 
      String object = this.current_object ; 
      String composite_predicate = this.current_composite_predicate  ; 
      //System.out.println(this.current_subject) ; 
      //System.out.println(this.current_predicate) ; 
      //System.out.println(this.current_object) ; 
      if ( this.within_composite_entity ) { 
        this.index_store_predicate_arguments_for_complex_object(current_predicate, object) ; 
      } else  { 
        this.index_statement_from_args(subject,predicate,object,statement_index) ; 
      }
      this.reset(' ') ; 
      //System.out.println("Ending Statement") ; 
      if (character   == ';')  { 
        if ( this.within_composite_entity ) {
          //we should start over with the composite predicate
        }
        else {
          this.current_subject = subject ; 
        }
      }
      else if ( character == ',' ) { 
        //System.out.println("More Comma") ; 
        if ( this.within_composite_entity ) { 

        } else {
          this.current_subject = subject ; 
          this.current_predicate = predicate ; 
        }
      }
      else if (character == '.') {
      }
    }

    private void reset (Character character) { 
      //System.out.println("Resetting") ;
      this.current_subject = "" ; 
      this.current_predicate = "" ; 
      this.current_object = "" ; 
      this.current_composite_predicate = "" ;
      this.current_composite_object = "" ; 
      if (character == ' ' ) {
        this.current_item = ""  ; 
      } else { 
        this.current_item = "" + character ;  
      }
    }

    private void index_statement_from_args (String subject, String predicate, String object, StatementIndex statement_index) {
      Statement statement = new Statement () ; 
      statement.addArgs(subject, predicate, object) ; 
      statement_index.addStatement(statement) ; 
    } 

    private void index_store_predicate_arguments_for_complex_object (String predicate, String current_object) {
      CompositeEntity composite_entity = this.current_composite_entity ; 
      composite_entity.updateCompositeEntityArgs(predicate,current_object) ; 
    }

    private static String  store_composite_entity(CompositeEntity composite_entity, StatementIndex statement_index) { 

      return statement_index.addCompositeEntity(composite_entity) ; 
    }

  }









