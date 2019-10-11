import java.util.* ; 

public class QueryEngine { 

  public void lookupQuery(String query_string, StatementIndex statement_index)  { //LinkedList<Array<String>> 

    Query new_query = new Query(); 
    new_query.parseQuery(query_string) ; 
   // LinkedList<Query> initial_results = lookupInitialStatements(new_query) ; 
   lookupInitialStatements(new_query, statement_index) ; 
    //    results = new LinkedList<Array<String>> (new_query.free_variables.size()) ; 
    //    for ( int i = 0 ; i < initial_results.size() ; i++ ) { 
    //      results.add(run_query(initial_results.lookup_query_internal(initial_results)) ; 
    //    } 
  }

  private void  lookupInitialStatements(Query query, StatementIndex statement_index) {  //LinkedList<Query>
    Statement first_statement = query.query_index.statementWithFewestVars() ;
    LinkedList<HashMap<String,String>> bindings = Unify(first_statement, statement_index) ; 
    query.bindings  = bindings ;
    query.showResults() ;
    }


  private static LinkedList<HashMap<String,String>> Unify (Statement query_statement, StatementIndex statement_index) {
    String subject   = query_statement.subject ; 
    String predicate = query_statement.predicate ; 
    String object    = query_statement.object ; 
    LinkedList<Statement> result = new LinkedList<Statement> () ; 

    if (!( isVariable(subject))) {
      //Subject is not a variable 
     LinkedList<Statement> subjectStatements = statement_index.index.get(1).get(subject) ; 
     result = UnifyPredicateAndObject(subject, predicate, object, subjectStatements) ; 
    } else if (!( isVariable(predicate))) {
      //Subject is a variable but predicate is not
      //System.out.println("Subject is a variable but predicate is not") ; //HERE?
      LinkedList<Statement> predicateStatements = statement_index.index.get(2).get(predicate) ; 
      result = UnifySubjectAndObject(subject, predicate, object, predicateStatements) ; 
    } else if (!(isVariable(object))) { 
      //Both subject and predicate are variables
      LinkedList<Statement> objectStatements = statement_index.index.get(3).get(object) ; 
      result = objectStatements  ; 
    } else { 
      result = statement_index.store ; 
    }
    LinkedList<HashMap<String,String>> bindings  = generateBindingLists(query_statement, result) ; 
    return bindings  ; 
  } 

  private static LinkedList<HashMap<String,String>> generateBindingLists (Statement query_statement, LinkedList<Statement> statements) { 
    LinkedList<HashMap<String,String>> bindings = new LinkedList<HashMap<String,String>> () ;
    String subject   = query_statement.subject  ; 
    String predicate = query_statement.predicate ; 
    String object    = query_statement.object  ; 
    for (int i = 0 ; i < statements.size() ; i++) { 
      HashMap<String,String> binding_list = new HashMap<String,String> () ;
      Statement statement = statements.get(i) ; 
      //@todo abstract these out.
      if (isVariable(subject)) {
        binding_list.put(subject,statement.subject) ; 
      }
      if (isVariable(predicate)) { 
        binding_list.put(predicate,statement.predicate) ; 
      }
      if  (isVariable(object)) { 
        binding_list.put(object,statement.object) ; 
      }
      bindings.add(binding_list)  ; 
    }
    return  bindings ; 
  }

  private static LinkedList<Statement> UnifyPredicateAndObject (String subject, String predicate, String object, LinkedList<Statement> statements) {
      return UnifyObjectOnPosition(subject, predicate, object, statements, 2) ; 
  }

  private static LinkedList<Statement> UnifySubjectAndObject (String subject, String predicate, String object, LinkedList<Statement> statements) {
      return UnifyObjectOnPosition(subject, predicate, object, statements, 1) ; 
  }

  private static LinkedList<Statement> UnifyObjectOnPosition(String subject, String predicate, String object, LinkedList<Statement> statements, int first_position) { 
    //System.out.println("Unifying " + subject + " " + predicate + " " + object + " on " + first_position) ;
    LinkedList<Statement> result = new LinkedList<Statement> () ;
    String item_to_check = new String () ; 
    if (first_position == 1) {
      item_to_check = subject ; 
    } else if (first_position == 2) { 
      item_to_check = predicate ; 
    }
    if ((isVariable(item_to_check)) && (isVariable(object))) {
      result = statements ; 
    } else if (!(isVariable(item_to_check)) && (isVariable(object))) { 
      result = getAllStatementsMatchingItemInPosition(item_to_check, statements, first_position) ; 
    } else if ((isVariable(item_to_check)) && !(isVariable(object))) { 
      //System.out.println(item_to_check + " is a variable but " + object + " is not ") ; 
      result = getAllStatementsMatchingItemInPosition(object,statements, 3) ; 
    } else {
      result = getStatementMatchingStatement(subject, predicate, object, statements) ; 
    } 
    return result ; 
  }

  private static LinkedList<Statement> getAllStatementsMatchingItemInPosition (String entity, LinkedList<Statement> statements, int position) { 
    LinkedList<Statement> result = new LinkedList<Statement>  () ; 
    for ( int i = 0 ; i < statements.size() ; i++)  { 
      Statement statement = statements.get(i) ; 
//      statement.show() ; 
//      System.out.println(statement.object) ; 
//      System.out.println(entity.equals(statement.object)) ;
      if (position == 1 && statement.subject.equals(entity)) {
        result.add(statement) ; 
      } else if (position == 2 && statement.predicate.equals(entity)) {
        result.add(statement) ; 
      } else if (position == 3 && statement.object.equals(entity)) {
//        System.out.println("Matching  on 3"); 
        result.add(statement) ; 
      } 
    } 
    //System.out.=println(result) ; 
    return result ; 
  }

  private static LinkedList<Statement> getStatementMatchingStatement (String subject, String predicate, String object,  LinkedList<Statement> statements) { 
    LinkedList<Statement> result = new LinkedList<Statement> () ;
    for (int i = 0 ; i < statements.size() ; i++) { 
      Statement statement = statements.get(i) ; 
      if (statement.subject == subject && statement.predicate == predicate && statement.object == object) {
        result.add(statement) ; 
        return result  ;
      }
    }
    //If we didn't get anything return the empty list
    return result ;
  } 

  public static boolean isVariable (String potential_variable) {  // @todo generalize this for every object, not just string
    return  potential_variable.matches("\\?[a-zA-Z1-9]+")  ;
  }

}











          

      





//    System.out.println(first_var) ; 
//    System.out.println(statement) ; 
//    statement.show() ; 

      

    


