import java.util.* ; 

// Should  we  have within the  subject and object indexes also a predicate index? 

public class StatementIndex { 
  public LinkedList<Statement> store ; 
  // @todo investigate if the footprint of two indexes is too much
  public HashMap<Integer,HashMap<String,LinkedList<Statement>>> index  ; 
  public HashMap<String,HashMap<String,LinkedList<String>>>  predicateObjectIndex ; 
  public HashMap<CompositeEntity,String> compositeEntityIndex ;

  public void initialize () { 
    System.out.println ("Initializing Statement Store...") ; 
    this.initializeStore () ; 
    System.out.println ("Statement Store Initialized") ; 
    System.out.println("Initializing Statement Index...") ; 
    this.initializeIndex () ; 
    System.out.println("Statement Index Initialized") ; 
    System.out.println("Initializing Composite Object Index...") ; 
    this.initializeCompositeEntityIndex() ; 
    System.out.println("Composite Object Index Initialized") ; 
    System.out.println("") ;
  }

  public int statementCount () { 
    return this.store.size()  ; 
  }

  private void initializeIndex () { 
    this.index = new HashMap<Integer,HashMap<String,LinkedList<Statement>>> () ; 
    this.predicateObjectIndex = new HashMap<String,HashMap<String,LinkedList<String>>>  () ; 
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

  private void initializeCompositeEntityIndex (){ 
    this.compositeEntityIndex = new HashMap<CompositeEntity,String> () ; 
  }

  public Boolean addStatement (Statement state) {
    //Only update if we don't already have statement
    //return value is whether we updated
    if (this.store.contains(state)) {
      return false ;
    }
    else { 
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
      this.update_predicate_index(subject,predicate,object) ;
      return true ;
    } 
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

  private void update_predicate_index(String subject,  String predicate, String object) {
    //System.out.println(subject) ; 
    //System.out.println(predicate) ; 
    //System.out.println(object) ; 
    if (this.predicateObjectIndex.containsKey(predicate)) {
      //System.out.println("THERE") ;
      HashMap<String,LinkedList<String>> subjectObjectDictionary = this.predicateObjectIndex.get(predicate) ; 
      if (subjectObjectDictionary.containsKey(subject)) { 
        //System.out.println(subjectObjectDictionary) ;
        LinkedList<String> objectList = subjectObjectDictionary.get(subject) ;
        //System.out.println(objectList) ; 
        objectList.add(object) ; 
        subjectObjectDictionary.put(subject, objectList) ; }
      else {
        LinkedList<String> objectList = new LinkedList<String> () ; 
        objectList.add(object) ;
        subjectObjectDictionary.put(subject, objectList) ; 
      }
    }
    else { 
      HashMap<String,LinkedList<String>> newSubjectObjectDictionary = new HashMap<String,LinkedList<String>>  ()  ; 
      LinkedList<String> objectList = new LinkedList<String> () ; 
      objectList.add(object) ;
      newSubjectObjectDictionary.put(subject, objectList) ; 
      this.predicateObjectIndex.put(predicate, newSubjectObjectDictionary) ;

    }
  }

  public String addCompositeEntity (CompositeEntity object)  {
    //For now we are going to do the naive thing. Really we need
    //to czer these object so that the same two objects don't 
    //get different hashes. This is a @todo
    // @return the hash to use for the composite object 
    if ( this.compositeEntityIndex.containsKey(object))  {
      return this.compositeEntityIndex.get(object)  ; 
    }
    else {
      String uniqueID = "co:" + UUID.randomUUID().toString();
      this.compositeEntityIndex.put(object, uniqueID) ; 
      return uniqueID ; 
    } 
  } 
}


