import java.util.* ; 

public class Statement { 

  public String subject ; 
  public String predicate ; 
  public String object ;

  public void addArgs (String subject, String predicate, String object) {
    this.subject=subject; 
    this.predicate=predicate ; 
    this.object=object ;
  }


  public static void main(String[]  args) { 
    Statement s1 = new Statement () ; 
    s1.addArgs("Penny", "likesAsFriend", "Obi") ; 
    System.out.println(s1.subject) ;  
    System.out.println(s1.object) ;  
    System.out.println(s1.predicate) ;  
  }

  public void show() { 
    String subject   = this.subject ; 
    String predicate = this.predicate ; 
    String object    = this.object ; 
    System.out.println(subject + " " + predicate + " " + object + ".") ; 
  }

  public boolean containsLessVars(Statement statement) { 
    if (this.variableCount() < statement.variableCount()) {
      return true ; 
    } else { 
      return false ; 
    } 
  }

  private int variableCount () { 
    ArrayList<String> entities  = new ArrayList<String> (Arrays.asList(this.subject,this.predicate, this.object)) ; 
    int count = 0 ; 
    for (int i = 0 ; i < entities.size() ; i++) { 
      if (isVariable(entities.get(i))) { 
        count = count + 1 ; 
      } 
    } 
    return count ; 
  }

  public static boolean isVariable (String potential_variable) {  // @todo generalize this for every object, not just string
    return  potential_variable.matches("\\?[a-zA-Z1-9]+")  ;
  }


  @Override
  public boolean equals (Object o) { 
    if ( o == this) {
      return true ; 
    }
    if (!(o instanceof Statement))  {
      return false ; 
    }
    Statement s1 = (Statement) o  ;
      if (s1.subject.equals(this.subject) &&
          s1.predicate.equals(this.predicate) && 
          s1.object.equals(this.object))  {
        return true ; 
          }
      else {
        return false ;
      }
  }



}



