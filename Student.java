import java.util.*;
import java.io.*;
public class Student{
  int id;
  HashSet<Class> interested_classes;
  HashSet<Class> enrolled_classes;
  public Student(int id){this.id=id; this.enrolled_classes = new HashSet<Class>();}
  public String toString(){
    return Integer.toString(id);
  }
}
