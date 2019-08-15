import java.text.CharacterIterator ; 
import java.text.StringCharacterIterator ; 

public class Parser {
  private Character last_char ; 
  private String current_uri ; 
  private String current_subject ; 
  private String current_predicate ; 
  private String current_object ; 

  //Start HERE : This needs a new field that tracks whether we're inside a uri or not. If we are then we should ignore ';', '.', and ','. So look for '<' and '>'

  public void parse (String contents, StatementIndex statement_index) { 
    CharacterIterator iterator = new StringCharacterIterator (contents)  ; 
    while ( iterator.current() != CharacterIterator.DONE ) { 
      Character character = iterator.current() ; 
      if (character == ' ') { 
        if (this.last_char != character )  { 
          this.update_parser_with_uri(character,statement_index) ; 
        }
      }
      else if (character == ';' ) { 
        this.end_statement(character, statement_index) ; 
      }
      else if (character == ',') { 
        this.end_statement(character, statement_index) ; 
      } 
      else if (character == '.') { 
        this.end_statement(character, statement_index) ; 
      }
      else {
        this.current_uri=this.current_uri + character ; 
      }
      iterator.next () ; 
    }
  }

  private void update_parser_with_uri (Character character, StatementIndex statement_index)  { 
    if ( this.current_subject == "" ) { 
      //if there's no subject set the subject to current_uri
      this.current_subject  = this.current_uri  ; 
    }
    else if (this.current_predicate == "" ) {
      // if there is  a subject but no predicate, set it to current_uri
      this.current_predicate = this.current_uri ; 
    }
    else if (this.current_object == "" ) { 
      // if there is a subject and predicate but no object set it to current_uri
      this.current_object  = this.current_uri  ; 
    }
    else { 
      // Index the statement and reset the parser
      Statement statement = new Statement () ; 
      statement.addArgs(this.current_subject, this.current_predicate, this.current_object) ; 
      statement_index.addStatement(statement) ; 
      this.reset(character) ; 
    }
  }

  private void end_statement(Character character, StatementIndex statement_index) { 
    String subject = this.current_subject ; 
    String predicate = this.current_predicate ; 
    String object = this.current_object ; 
    this.index_statement_from_args(subject,predicate,object,statement_index) ; 
    this.reset(' ') ; 
    this.current_subject = subject ; 
    if ( character == ',' ) { 
      this.current_predicate = predicate ; 
    }
  }

  private void reset (Character character) { 
    this.current_subject = "" ; 
    this.current_predicate = "" ; 
    this.current_object = "" ; 
    if (character == ' ' ) {
      this.current_uri = ""  ; 
    } else { 
      this.current_uri = "" + character ;  
    }
  }

  private void index_statement_from_args (String subject, String predicate, String object, StatementIndex statement_index) {
    Statement statement = new Statement () ; 
    statement.addArgs(subject, predicate, object) ; 
    statement_index.addStatement(statement) ; 
  }





}










