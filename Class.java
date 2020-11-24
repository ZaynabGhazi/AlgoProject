import java.util.*;
import java.io.*;
public class Class{
  //from file:
  int id;
  int prof_id;
  long duration;
  int num_meetings;
  String depart;
  int capacity;
  //course name = subject + catalog+' '+section
  String course_name;
  //assigned by algorithm:
  HashSet<Student> interested_students;
  Room room;
  Timeslot timeslot;
  HashSet<Student> assigned_students;

  public Class(int id,int professor){this.id=id;this.prof_id=professor;this.duration=0;this.num_meetings=1;}
  public String toString(){
    return id+" "+room+" "+prof_id+" "+timeslot+printStudents();
  }
  private String printStudents(){
    String students="";
    for(Student student: interested_students){
      students+=student+" ";
    }
    return students;
  }
}
