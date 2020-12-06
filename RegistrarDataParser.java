import java.util.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;


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
  static HashMap<String, Class> classes;
  static HashMap<Integer,Student> students;
  static HashMap<String,Room> rooms;
  static HashMap<String, Set<Room>> departRooms;
  static HashMap<Integer,HashMap<Integer,Integer>> conflictMatrix;
  static HashMap <Integer, List<Timeslot>> timeslots;

  public static void main(String[] args) throws FileNotFoundException,ParseException{
    //initialize structures:
    classes = new HashMap<>();
    students = new HashMap<>();
    rooms = new HashMap<>();
    departRooms = new HashMap<>();
    conflictMatrix = new HashMap<>();
    timeslots = new HashMap<>();
    Scanner scanner = new Scanner(new File(args[0]));
    //skip header
    scanner.nextLine();
    while(scanner.hasNextLine()){
      String csv_line = scanner.nextLine();
      String[] fields = csv_line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
      updateClass(fields);
      updateStudent(fields);
      updateRooms(fields);
    }
    createConflictMatrix();
    generateTimeslot();
    int prefs = 0;
    for (Class c: classes.values())
      prefs += c.interested_students.size();
    ScheduleClass.schedule(classes, timeslots, conflictMatrix);

    Room[] allRooms = new Room[rooms.values().size()];
    int i = 0;
    for (Room r: rooms.values()) {
      allRooms[i] = r;
      i++;
    }
    ArrayList<Timeslot> allSlots = new ArrayList<>();
    for (List<Timeslot> slotLists: timeslots.values()) {
      for (Timeslot t: slotLists) {
        allSlots.add(t);
      }
    }
    ArrayList<Class> allClasses = new ArrayList<>();
    for (Class c: classes.values()) {
      allClasses.add(c);
    }
    AssignStudent.roomAndStudentAssignment(allRooms, allSlots, departRooms, allClasses);
    ScheduleClass.printSchedule(classes);
    int count = 0;
    for (Class c: classes.values()) {
      count += c.assigned_students.size();
    }
    System.out.println("Number of student preferences satisfied: " + count);
    System.out.println("Number of student preferences: " + prefs);
    System.out.println("% preferences satisfied: " + 100*count/(double)prefs);
  }
  public static void updateClass(String[] fields) throws ParseException{
	 String str = fields[COURSE_ID] + " " + fields[SECTION];
    if (!classes.containsKey(str)){
      Class course = new Class(Integer.parseInt(fields[COURSE_ID]),isNumeric(fields[PROF])?Integer.parseInt(fields[PROF]):0);
      course.interested_students = new HashSet<>();
      if (!fields[MEETINGS].isEmpty()){
    	course.num_meetings=fields[MEETINGS].indexOf('H')==-1?fields[MEETINGS].length():fields[MEETINGS].length()-1;
      }
      course.depart=fields[SUBJECT];
      course.course_name=fields[SUBJECT]+" "+fields[CATALOG]+"-"+fields[SECTION];
      course.section = fields[SECTION];
      //compute duration of class
      if (!fields[START].isEmpty()){
        Calendar c1 = Calendar.getInstance();
        c1.setTime(getTime(fields[START]));
        Calendar c2= Calendar.getInstance();
        c2.setTime(getTime(fields[END]));
        course.duration= ((int)c2.getTimeInMillis()-(int)c1.getTimeInMillis())/60000;
      }
      classes.put(str, course);

    }
  }
  public static void updateStudent(String[] fields){
    //only count towards capacity if enrolled and not waitlisted
	  String str = fields[COURSE_ID] + " " + fields[SECTION];
    if (fields[STATUS].equals("E")) {
    classes.get(str).capacity++;
    }

    if (!students.containsKey(Integer.parseInt(fields[STUDENT_ID]))){
      Student student = new Student(Integer.parseInt(fields[STUDENT_ID]));
      student.interested_classes = new HashSet<>();
      //Link students to classes
      student.interested_classes.add(classes.get(str));
      classes.get(str).interested_students.add(student);
      students.put(Integer.parseInt(fields[STUDENT_ID]),student);
    }
    else{
      Student student = students.get(Integer.parseInt(fields[STUDENT_ID]));
      student.interested_classes.add(classes.get(str));
      classes.get(str).interested_students.add(student);
    }
  }
  public static void updateRooms(String[] fields){
	  String str = fields[COURSE_ID] + " " + fields[SECTION];
    if (!departRooms.containsKey(fields[SUBJECT])){
      Set<Room> depart_rooms = new HashSet<>();
      departRooms.put(fields[SUBJECT],depart_rooms);
    }
    //A room's capacity is defined to be the maximum capacity from classes scheduled in it according to data file
    if (!rooms.containsKey(fields[ROOM])){
      rooms.put(fields[ROOM],new Room(fields[ROOM],classes.get(str).capacity));
    }
    departRooms.get(fields[SUBJECT]).add(rooms.get(fields[ROOM]));

  if (classes.get(str).capacity > rooms.get(fields[ROOM]).cap){
    rooms.get(fields[ROOM]).cap=classes.get(str).capacity;
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

  private static void generateTimeslot() {
	  for(int i = 0; i < 7; i++) {
		  List<Timeslot> list = new ArrayList<>();
		  for(int j = 1; j <=18; j++) {
			  Timeslot newSlot = new Timeslot(i, j);
			  Map<String, Integer> numRooms= new HashMap<>();
			  for(Map.Entry<String, Set<Room>> e: departRooms.entrySet()) {
				  numRooms.put(e.getKey(), e.getValue().size());
			  }
			  newSlot.numRooms = numRooms;
			  list.add(newSlot);
		  }
		  timeslots.put(i, list);
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
