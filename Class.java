import java.util.*;
import java.io.*;
public class Class implements Comparable<Class>{
  //from file:
  int id;
  int prof_id;
  int duration;
  int num_meetings;
  String depart;
  int capacity;
  //course name = subject + catalog+' '+section
  String course_name;
  //assigned by algorithm:
  HashSet<Student> interested_students;
  Room room;
  Map<Integer, List<Timeslot>> timeslots;
  HashSet<Student> assigned_students;
  
  String section;


  public Class(int id,int professor){this.id=id;this.prof_id=professor;this.duration=0;this.num_meetings=1;this.assigned_students=new HashSet<Student>();}
  public String toString(){
    return id+" "+room+" "+prof_id+" "+timeslots+printStudents();
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
    int slotsNeeded1 = Math.min(this.interested_students.size(), this.capacity);
    int slotsNeeded2 = Math.min(c.interested_students.size(), c.capacity);
    return slotsNeeded1 - slotsNeeded2;
  }

}
