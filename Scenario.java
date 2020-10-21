import java.util.*;
import java.io.*;
public class Scenario{
Class[] classes;
Timeslot[] timeslots;
Student[] students;
Room[] rooms;
public Scenario(Class[] classes,Timeslot[] timeslots,Student[] students,Room[] rooms){
  this.classes = classes;
  this.timeslots = timeslots;
  this.students = students;
  this.rooms = rooms;
}
}
