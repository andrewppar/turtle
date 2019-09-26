public class CompositeEntityStatement { 
  String predicate = new String () ; 
  String object    = new String () ; 

  public void  addArgs(String predicate, String object) { 
    this.predicate = predicate ; 
    this.object = object ;
    }
}


