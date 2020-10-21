import java.util.*;

public class AssignStudent {
    public static void roomAndStudentAssignment(Room[] rooms, Timeslot[] timeSlots){
        Arrays.sort(rooms);
        for (Timeslot currentSlot: timeSlots) {
            List<Class> scheduledClasses = new ArrayList<>(currentSlot.scheduled_classes);
            Collections.sort(scheduledClasses);
            int currentRoomIndex = 0;
            for (Class currentClass: scheduledClasses) {
                currentClass.room = rooms[currentRoomIndex];
                for (Iterator<Student> i = currentClass.interested_students.iterator(); i.hasNext();) {
                    Student student = i.next();
                    if (student.enrolled_classes.size() > 0) {
                        for (Class enrolledClass : student.enrolled_classes) {
                            if (enrolledClass.timeslot.equals(currentSlot)) {
                                i.remove();
                                break;
                            }
                        }
                    }
                }

                int numEnrolledStudents = 0;
                for (Student student : currentClass.interested_students) {
                    if (numEnrolledStudents < rooms[currentRoomIndex].cap) {
                        student.enrolled_classes.add(currentClass);
                        currentClass.assigned_students.add(student);
                    } else {
                        break;
                    }
                }
                currentRoomIndex ++;
            }
        }
    }
}
