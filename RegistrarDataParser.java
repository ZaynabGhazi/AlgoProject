import java.util.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class RegistrarDataParser{
  //csv fields:
  public static final int   STUDENT_ID=0;
  public static final int   COURSE_ID=1;
  public static final int   SUBJECT=2;
  public static final int   CATALOG=3;
  public static final int   SECTION=4;
  public static final int   STATUS=5;
  public static final int   START=12;
  public static final int   END=14;
  public static final int   MEETINGS=15;
  public static final int   ROOM=16;
  public static final int   PROF=19;
  //general data structures:
  static HashMap<Integer,Class> classes;
  static  ArrayList<LinkedList<Timeslot>> timeslots;
  static HashMap<Integer,Student> students;
  static HashMap<String,Room> rooms;
  static HashMap<String,ArrayList<Room>> departRooms;
  static HashMap<Integer,HashMap<Integer,Integer>> conflictMatrix;

  public static void main(String[] args) throws FileNotFoundException,ParseException{
    //initialize structures:
    classes = new HashMap<>();
    students = new HashMap<>();
    rooms = new HashMap<>();
    departRooms = new HashMap<>();
    conflictMatrix = new HashMap<>();
    timeslots = new ArrayList<>();
    Scanner scanner = new Scanner(new File(args[0]));
    //skip header
    scanner.nextLine();
    while(scanner.hasNextLine()){
      String csv_line = scanner.nextLine();
      String[] fields = csv_line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
      updateClass(fields);
      updateStudent(fields);
      if (!fields[ROOM].isEmpty()) updateRooms(fields);
    }
    createConflictMatrix();
    generateTimeslots();
    //DEBUGGER: ROOM CAPACITIES
    /*for(String subject: departRooms.keySet()){
      ArrayList<Room> sub_rooms = departRooms.get(subject);
      for(int i=0; i<sub_rooms.size();i++) System.out.println(sub_rooms.get(i).id+" "+sub_rooms.get(i).cap);
    }*/
    //DEBUGGER: CONFLICT conflictMatrix
    /*for(Integer classId: conflictMatrix.keySet()){
      HashMap<Integer,Integer> conflicts = conflictMatrix.get(classId);
      for(Integer entry: conflicts.keySet()) System.out.println(entry+": "+conflicts.get(entry));
    }*/

  }
  public static void updateClass(String[] fields) throws ParseException{
    if (!classes.containsKey(Integer.parseInt(fields[COURSE_ID]))){
      Class course = new Class(Integer.parseInt(fields[COURSE_ID]),isNumeric(fields[PROF])?Integer.parseInt(fields[PROF]):0);
      course.interested_students = new HashSet<>();
      if (!fields[MEETINGS].isEmpty()){
        course.num_meetings=fields[MEETINGS].indexOf('H')==-1?fields[MEETINGS].length():fields[MEETINGS].length()-1;
      }
      course.depart=fields[SUBJECT];
      course.course_name=fields[SUBJECT]+" "+fields[CATALOG]+"-"+fields[SECTION];
      //compute duration of class
      if (!fields[START].isEmpty()){
        Calendar c1 = Calendar.getInstance();
        c1.setTime(getTime(fields[START]));
        Calendar c2= Calendar.getInstance();
        c2.setTime(getTime(fields[END]));
        course.duration= c2.getTimeInMillis()-c1.getTimeInMillis();
      }
      classes.put(Integer.parseInt(fields[COURSE_ID]),course);
    }
    //DEBUGGER:
    //int key = Integer.parseInt(fields[COURSE_ID]);
   //System.out.println(classes.get(key).course_name+" "+classes.get(key).capacity+" "+classes.get(key).duration+" "+classes.get(key).num_meetings+" "+classes.get(key).prof_id);
  }
  public static void updateStudent(String[] fields){
    //only count towards capacity if enrolled and not waitlisted
    if (fields[STATUS].equals("E"))
    classes.get(Integer.parseInt(fields[COURSE_ID])).capacity++;
    if (!students.containsKey(Integer.parseInt(fields[STUDENT_ID]))){
      Student student = new Student(Integer.parseInt(fields[STUDENT_ID]));
      student.interested_classes = new HashSet<>();
      //Link students to classes
      student.interested_classes.add(classes.get(Integer.parseInt(fields[COURSE_ID])));
      classes.get(Integer.parseInt(fields[COURSE_ID])).interested_students.add(student);
      students.put(Integer.parseInt(fields[STUDENT_ID]),student);
    }
    else{
      Student student = students.get(Integer.parseInt(fields[STUDENT_ID]));
      student.interested_classes.add(classes.get(Integer.parseInt(fields[COURSE_ID])));
      classes.get(Integer.parseInt(fields[COURSE_ID])).interested_students.add(student);
    }
    //DEBUGGER:
    //System.out.println(students.get(Integer.parseInt(fields[STUDENT_ID])).id);
    //for(Class course:students.get(Integer.parseInt(fields[STUDENT_ID])).interested_classes) System.out.print(course.course_name+" ");
  }
  public static void updateRooms(String[] fields){
    if (!departRooms.containsKey(fields[SUBJECT])){
      ArrayList<Room> depart_rooms = new ArrayList<>();
      departRooms.put(fields[SUBJECT],depart_rooms);
      }
    //A room's capacity is defined to be the maximu capacity from classes scheduled in it according to data file
    if (!rooms.containsKey(fields[ROOM])){
      rooms.put(fields[ROOM],new Room(fields[ROOM],classes.get(Integer.parseInt(fields[COURSE_ID])).capacity));
      departRooms.get(fields[SUBJECT]).add(rooms.get(fields[ROOM]));
    }
    else{
      if (classes.get(Integer.parseInt(fields[COURSE_ID])).capacity > rooms.get(fields[ROOM]).cap){
        rooms.get(fields[ROOM]).cap=classes.get(Integer.parseInt(fields[COURSE_ID])).capacity;
        }
    }
  }
  public static void createConflictMatrix(){
    conflictMatrix = new HashMap<>();
      for(Student student: students.values()){
        for(Class course1: student.interested_classes){
          if (!conflictMatrix.containsKey(course1.id)){
            HashMap<Integer,Integer> conflicts = new HashMap<>();
            conflictMatrix.put(course1.id,conflicts);
          }
          HashMap<Integer,Integer> conflicts = conflictMatrix.get(course1.id);
          for(Class course2: student.interested_classes){
          if (course1.id!=course2.id){
              if(course1.prof_id==course2.prof_id){
                conflicts.put(course2.id,Integer.MAX_VALUE);
                }
                else{
                  if (!conflicts.containsKey(course2.id)){
                    conflicts.put(course2.id,1);
                  }
                  else{
                    conflicts.put(course2.id,conflicts.get(course2.id)+1);
                  }
                }
            }
          }
        }
      }
  }
  /*
  * Utility methods
  */
  private static void generateTimeslots() throws ParseException{
    //'0' is SUN
    for (int i=0; i<7;i++){
      LinkedList<Timeslot> times = new LinkedList<>();
      Date starthour= getTime("8:00 AM");
      //current enhour is 5 PM (loop index)
      for(int j=1; j<=18;j++){
        Timeslot newest = new Timeslot(i,starthour,30*60000);
        times.add(newest);
        Calendar helper = Calendar.getInstance();
        helper.setTime(starthour);
        helper.add(Calendar.MILLISECOND,30*60000);
        starthour = helper.getTime();
      }
      timeslots.add(times);
      //DEBUGGER:
      //System.out.println(times);
    }
  }
  private static Date getTime(String date) throws ParseException {
       SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
       return formatter.parse(date);
 }
 public static boolean isNumeric(String strNum) {
    if (strNum == null) {
        return false;
    }
    try {
        double d = Double.parseDouble(strNum);
    } catch (NumberFormatException nfe) {
        return false;
    }
    return true;
}
}
