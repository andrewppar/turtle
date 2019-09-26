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
}
