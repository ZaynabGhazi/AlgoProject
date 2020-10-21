import java.io.*;
import java.util.*;
public class Input_parser{
  static Class[] classes;
  static Timeslot[] timeslots;
  static Student[] students;
  static Room[] rooms;
  public static void main(String[] args){
    String class_constr_file = args[0];
    String student_pref_file = args[1];
    //The two main methods:
    Scenario data = getScheduleScenario(class_constr_file, student_pref_file);
    int conflicts[][] = createConflictMatrix(data);
    
    //Create timeslots instances
    for(int i = 1; i <= timeslots.length; i++) {
    	timeslots[i-1] = new Timeslot(i);
    }

    //Make and print class schedules
    AssignTimeslot.scheduleClass(classes, timeslots, conflicts, rooms.length);
    AssignStudent.roomAndStudentAssignment(rooms, timeslots);
    int count = 0;
    for(int i = 0; i < timeslots.length; i++) {
      System.out.println("timeslot_id: " + timeslots[i].id + " ");
      HashSet<Class> classes = timeslots[i].scheduled_classes;
      for(Class c: classes) {
        System.out.println("class " + c.id + ": room " + c.room.id + ", students: " + c.assigned_students);
        count += c.assigned_students.size();
      }
      System.out.println();
    }
    System.out.println("Number of student preferences satisfied: " + count);

    //Checking conflicts:
    /*for(int i=0; i< conflicts.length;i++){
      for(int j=0; j<conflicts[i].length;j++){
        System.out.print(conflicts[i][j]+" ");
      }
      System.out.println();
    }
    //CHECKING CONTENT OF SCENARIO:
    /*System.out.println("Classes");
    for(int i=0; i< data.classes.length;i++){
      System.out.println(data.classes[i].id);
    }
    System.out.println("ROOMS");
      for(int i=0; i< data.rooms.length;i++){
        System.out.println(data.rooms[i]);
    }*/

  }
  /*
  * THIS IS THE MAIN METHOD YOU WANT TO USE
  */
  public static Scenario getScheduleScenario(String class_file,String student_file){
    Scenario scenario;
    try{
      File class_constr= new File(class_file);
      File student_pref = new File(student_file);
      readClassConstraints(class_constr);
      readStudentPref(student_pref);
      }
    catch(FileNotFoundException e){
      System.out.println("Error reading files.");
      e.printStackTrace();
    }
    return new Scenario(classes,timeslots,students,rooms);
  }

  public static void readClassConstraints(File file) throws FileNotFoundException{
    int count=0;
    Scanner sc = new Scanner(file);
    while(sc.hasNextLine()){
      String line = sc.nextLine();
      //class-times
      if (count==0){
        //System.out.println(line);
        timeslots= new Timeslot[Integer.parseInt(line.split("\\s+")[2])];
        rooms = new Room[Integer.parseInt(sc.nextLine().split("\\s+")[1])];
        while(count < rooms.length){
          String[] room_info = sc.nextLine().split("\\s+");
          rooms[count]=new Room(Integer.parseInt(room_info[0]),Integer.parseInt(room_info[1]));
          count++;
        }
        continue;
      }
      //classes and teachers
      if (line.startsWith("Classes")){
        count=0;
        classes = new Class[Integer.parseInt(line.split("\\s+")[1])];
        //skip teachers nextLine
        sc.nextLine();
        while (count<classes.length){
          String[] class_info = sc.nextLine().split("\\s+");
          classes[count++]=new Class(Integer.parseInt(class_info[0]),Integer.parseInt(class_info[1]));
        }
      }

    }
  }//end_readClassConstraints()
  public static void readStudentPref(File file) throws FileNotFoundException{
    Scanner sc = new Scanner(file);
    students = new Student[Integer.parseInt(sc.nextLine().split("\\s+")[1])];
    int count=0;
    while(sc.hasNextLine()){
      String[] student_info = sc.nextLine().split("\\s+");
      students[count]=new Student(Integer.parseInt(student_info[0]));
      students[count].interested_classes = new HashSet<Class>();
      for(int i=1;i<student_info.length;i++){
        students[count].interested_classes.add(classes[Integer.parseInt(student_info[i])-1]);
          classes[Integer.parseInt(student_info[i])-1].interested_students.add(students[count]);
      }
      //check
      /*System.out.print(students[count].id+" ");
      for(Class course:students[count].interested_classes){
        System.out.print(course.id+" ");
      }
      System.out.println();*/
      count++;
    }

  }//end_readStudentPref()
  public static int[][] createConflictMatrix(Scenario data){
    int[][] conflicts = new int[data.classes.length][data.classes.length];
    for(int j=0; j< data.students.length;j++){
      for(Class course1: data.students[j].interested_classes){
        for(Class course2: data.students[j].interested_classes){
          if (course1.id!=course2.id){
            if(course1.prof==course2.prof){
              conflicts[course1.id-1][course2.id-1]=Integer.MAX_VALUE;
              conflicts[course2.id-1][course1.id-1]=Integer.MAX_VALUE;
              }
              else{
                conflicts[course1.id-1][course2.id-1]+=1;
                conflicts[course2.id-1][course1.id-1]+=1;

              }

          }
        }
      }
    }
    return conflicts;
  }


}
