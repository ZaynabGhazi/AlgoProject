import java.util.*;
import java.io.*;
public class Class implements Comparable<Class> {
  int id;
  int prof;
  Timeslot timeslot;
  Room room;
  HashSet<Student> interested_students;
  HashSet<Student> assigned_students;

  public Class(int id, int professor){
    this.id=id;
    this.prof=professor;
    this.assigned_students = new HashSet<>();
    this.interested_students = new HashSet<>();
  }
  public String toString(){
    return id+" "+room+" "+prof+" "+timeslot+printStudents();
  }
  private String printStudents(){
    String students="";
    for(Student student: interested_students){
      students+=student+" ";
    }
    return students;
  }
  @Override
  public int compareTo(Class c) {
    return this.interested_students.size() - ((Class) c).interested_students.size();
  }
}
