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


}



