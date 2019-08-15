import java.util.* ; 

public class StatementIndex { 
  private LinkedList<Statement> store ; 
  public  HashMap<Integer,HashMap<String,LinkedList<Statement>>> index  ; 

  public void main (String [] args) { 
    StatementIndex index = new StatementIndex () ; 
    System.out.println ("Initializing Statement Store...") ; 
    index.initializeStore () ; 
    System.out.println ("Statement Store Initialized") ; 
    System.out.println("Initializing Statement Index...") ; 
    index.initializeIndex () ; 
    System.out.println("Statement Index Initialized") ; 
  }

  public void start () { 
    System.out.println ("Initializing Statement Store...") ; 
    this.initializeStore () ; 
    System.out.println ("Statement Store Initialized") ; 
    System.out.println("Initializing Statement Index...") ; 
    this.initializeIndex () ; 
    System.out.println("Statement Index Initialized") ; 
  }

  private void initializeIndex () { 
    this.index = new HashMap<Integer,HashMap<String,LinkedList<Statement>>> () ;
    HashMap subjectIndex = new HashMap<String,LinkedList<Statement>> () ; 
    HashMap predicateIndex = new HashMap<String,LinkedList<Statement>> () ; 
    HashMap objectIndex = new HashMap<String,LinkedList<Statement>> () ; 
    this.index.put(1,subjectIndex) ; 
    this.index.put(2,predicateIndex) ; 
    this.index.put(3,objectIndex) ; 
  }

  private void initializeStore () { 
    this.store = new LinkedList<Statement> () ; 
  }

  public void addStatement (Statement state) {
    this.store.add(state); 
    String subject   = state.subject ; 
    String predicate = state.predicate ; 
    String object    = state.object ; 
    HashMap<String,LinkedList<Statement>> subject_index   = this.index.get(1) ; 
    HashMap<String,LinkedList<Statement>> predicate_index = this.index.get(2) ; 
    HashMap<String,LinkedList<Statement>> object_index    = this.index.get(3) ; 
    update_inner_index(subject_index,subject,state) ; 
    update_inner_index(predicate_index,predicate,state) ; 
    update_inner_index(object_index,object,state) ; 
  }

  private void update_inner_index(HashMap<String,LinkedList<Statement>> inner_index, String item, Statement state) {
    if (inner_index.containsKey(item)) { 
      LinkedList<Statement> index_statements=inner_index.get(item) ; 
      index_statements.add(state) ; 
    } else { 
     LinkedList<Statement> statements_for_item=new LinkedList<Statement>() ; 
     statements_for_item.add(state) ; 
     inner_index.put(item,statements_for_item) ; 
    }
  }

  }



