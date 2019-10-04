public class CompositeEntityStatement { 
  String predicate = new String () ; 
  String object    = new String () ; 

  public void  addArgs(String predicate, String object) { 
    this.predicate = predicate ; 
    this.object = object ;
    }

  public void print() { 
    System.out.print(this.predicate) ; 
    System.out.print(" ") ; 
    System.out.print(this.object) ; 
    System.out.println(" ; ") ; 


  }
}


