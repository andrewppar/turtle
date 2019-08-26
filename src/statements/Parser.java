import java.text.CharacterIterator ; 
import java.text.StringCharacterIterator ; 
import java.util.* ; 

//@start  we need a  way to set  this.within_prefix to true, currently  it's always  false  no matter  what. 
public class Parser {
  private HashMap<String,String> prefix_dictionary ; 
  private String prefix_key ; 
  private String current_object ; 
  private String current_subject ; 
  private String current_predicate ; 
  private String current_item ; 
  private Boolean within_uri ; 
  private Boolean within_prefix ; 
  private Boolean within_comment ; 
  private Boolean within_prefix_dictionary_entry ; 

  public void parse (String contents, StatementIndex statement_index) { 
    CharacterIterator iterator = new StringCharacterIterator (contents)  ;
    this.current_object    = ""; 
    this.current_subject   = ""; 
    this.current_predicate = ""; 
    this.current_item    = ""; 
    this.prefix_dictionary = new  HashMap<String,String> () ; 
    this.within_comment    = false ; 
    this.within_uri =false  ;
    this.within_prefix=false; 
    this.within_prefix_dictionary_entry = false ; 
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
//System.out.println("within_prefix") ; 
//System.out.println(this.within_prefix) ;

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
      else if (this.current_item.equals("prefix")) {
//System.out.println("Found It") ;
        this.within_prefix_dictionary_entry = true ;  
        this.current_item = ""  ; 
      }
      else if (this.within_prefix ) { 
        this.update_prefix_state(character, statement_index) ;
      } 
      else {
        if (character == ';' ) { 
          this.end_statement(character, statement_index) ; 
        }
        else if (character == ',') { 
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
            this.update_parser_with_uri(character,statement_index) ; 
          }
        }
        else {
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
    this.current_item = this.current_item + character ; 
    if (character == '>' ) {
      this.update_parser_with_uri(character,statement_index) ; 
      this.within_uri=false  ;
      //        System.out.println("IN") ; 
      //        System.out.println(current_item) ; 

    }
  }

  private void update_parser_with_uri (Character character, StatementIndex statement_index)  { 
    if ( this.current_subject.equals("")) { 
      //if there's no subject set the subject to current_item
      //  System.out.println("Adding Subject"); 
      this.current_subject  = this.current_item  ; 
      this.current_item = ""  ; 
    }
    else if (this.current_predicate.equals("")) {
      // if there is  a subject but no predicate, set it to current_item
      //System.out.println("Adding Predicate"); 
      this.current_predicate = this.current_item ; 
      this.current_item = ""  ; 
    }
    else if (this.current_object.equals("")) { 
      // if there is a subject and predicate but no object set it to current_item
      //System.out.println("Adding Object"); 
      this.current_object  = this.current_item  ; 
      this.current_item = ""  ; 
    }
    else { 
      // Index the statement and reset the parser
      Statement statement = new Statement () ; 
      statement.addArgs(this.current_subject, this.current_predicate, this.current_object) ; 
      //System.out.println("Adding Statemetn in UPDATE"); 
      //System.out.println(statement); 
      statement_index.addStatement(statement) ; 
      this.reset(character) ; 
    }
  }

  private void update_prefix_state (Character character, StatementIndex statement_index ) {  
    if (this.current_item.equals("a") && character == ' ')  { 
      this.update_parser_with_uri(character,statement_index ) ; 
      this.within_prefix = false ; 
    } 
    else if ( character == ':' )  { 
      String expansion = this.prefix_dictionary.get(this.current_item) ; 
      String real_prefix =  expansion.substring(0, (expansion.length() - 1)) ; 
      this.within_prefix  = false ; 
      this.current_item = real_prefix ; 
    }
    else if ( character == '.' || character == '\n' ) { 
      //This is kind of an error case, maybe 
      //we should throw an error here.
      this.within_prefix = false ; 
      this.current_item = "" ; 
    }
    else if (character == ' ' ) { 
      this.current_item = this.current_item + '>' ; 
      this.add_entry_to_state(this.current_item) ; 
      this.current_item = ""  ; 
      this.within_prefix = false ; 
    }
    else { 
      this.current_item = this.current_item + character   ; 
    } 
  } 

  private void add_entry_to_state (String entry) { 
//System.out.println("Adding To Entry State") ;  
    if (this.current_subject.equals("")) { 
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
//System.out.println("Traffic Jam: ") ; 
//System.out.println("New object: "+entry) ; 
//System.out.println("Subject: " + this.current_subject) ; 
//System.out.println("Predicate: " + this.current_predicate) ; 
//System.out.println("Object: " + this.current_object) ; 
    }

  }

  private void end_statement(Character character, StatementIndex statement_index) { 
    String subject = this.current_subject ; 
    String predicate = this.current_predicate ; 
    String object = this.current_object ; 
    this.index_statement_from_args(subject,predicate,object,statement_index) ; 
    this.reset(' ') ; 
    if (character   == ';')  { 
      this.current_subject = subject ; 
    }
    else if ( character == ',' ) { 
      this.current_subject = subject ; 
      this.current_predicate = predicate ; 
    }
    else if (character == '.') {
    }
  }

  private void reset (Character character) { 
    this.current_subject = "" ; 
    this.current_predicate = "" ; 
    this.current_object = "" ; 
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





}










