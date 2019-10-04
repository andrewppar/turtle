import  java.util.* ; 

public class CompositeEntity {

  LinkedList<CompositeEntityStatement>  args = new LinkedList<CompositeEntityStatement> () ; 

  public void addArgs (LinkedList<CompositeEntityStatement> args) {
    this.args = args ; 
  }

  public void updateCompositeEntityArgs(String predicate, String object) {
    LinkedList<CompositeEntityStatement> args = this.args ; 
    CompositeEntityStatement statement = new CompositeEntityStatement() ; 
    statement.addArgs(predicate, object) ; 
    args.add(statement) ; 
  } 

  public void print(int spaces) {
    //This should have access to the  prefix dictionary if it is going to print properly
    System.out.print("[   ") ; 
    LinkedList<CompositeEntityStatement> args = this.args ; 
//    System.out.println(this) ; 
//    System.out.println(args) ;
    for (int i = 0 ; i < args.size() ; i++) {
      CompositeEntityStatement composite_entity_statement = args.get(i) ; 
//    System.out.println(composite_entity_statement) ; 
      if ( i > 0 ) { 
        System.out.format("%"+spaces+"s", ""); 
      }
      composite_entity_statement.print() ; 
    }
    System.out.format("%"+spaces+"s", ""); 
    System.out.println("] ") ; 
  }
}
