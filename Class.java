import java.util.*;
import java.io.*;
public class Class{
  int id;
  int prof;
  Timeslot timeslot;
  Room room;
  HashSet<Student> interested_students;
  HashSet<Student> assigned_students;

  public Class(int id,int professor){this.id=id;this.prof=professor;}
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
}
