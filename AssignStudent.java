import java.sql.Time;
import java.util.*;

public class AssignStudent {
    public static void roomAndStudentAssignment(Room[] rooms, ArrayList<Timeslot> timeSlots, HashMap<String,
            Set<Room>> departRooms, ArrayList<Class> classes){
        Arrays.sort(rooms);
        Set<Room> unavailableRooms = new HashSet<>();
        Set<Class> assignedClasses = new HashSet<>();
        for (Timeslot currentSlot: timeSlots) {
            List<Class> scheduledClasses = new ArrayList<>(currentSlot.courses);
            for (Class currentClass: scheduledClasses) {
                if (!assignedClasses.contains(currentClass)) {
                    Room appropriateRoom = null;
                    int diff = Integer.MAX_VALUE;
                    for (Room departRoom: departRooms.get(currentClass.depart)) {
                        if (departRoom.cap >= currentClass.capacity && departRoom.cap - currentClass.capacity < diff) {
                            diff = departRoom.cap - currentClass.capacity;
                            appropriateRoom = departRoom;
                        }
                    }
                    if (appropriateRoom != null) {
                        currentClass.room = appropriateRoom;
                        unavailableRooms.add(appropriateRoom);
                        assignedClasses.add(currentClass);

                        enrollStudents(currentClass, appropriateRoom);
                    }


                    removeConflicts(currentClass);
                }
            }

            int currentRoomIndex = 0;

            Collections.sort(scheduledClasses);

            for (Class currentClass: scheduledClasses) {
                if (!assignedClasses.contains(currentClass)) {
                    while (unavailableRooms.contains(rooms[currentRoomIndex])) {
                        currentRoomIndex++;
                    }
                    currentClass.room = rooms[currentRoomIndex];
                    assignedClasses.add(currentClass);
                    enrollStudents(currentClass, rooms[currentRoomIndex]);
                    currentRoomIndex ++;
                    removeConflicts(currentClass);
                }

            }
        }

        for (Class c: classes) {
            if (!assignedClasses.contains(c)) {
                if (c.room == null && c.timeslots != null) {
                    c.assigned_students = c.interested_students;
                }
            }
        }
    }

    private static void enrollStudents(Class currentClass, Room room) {
        int numEnrolledStudents = 0;
        for (Student student : currentClass.interested_students) {
            if (numEnrolledStudents < room.cap && numEnrolledStudents < currentClass.capacity) {
                student.enrolled_classes.add(currentClass);
                currentClass.assigned_students.add(student);
            } else {
                break;
            }
        }
    }

    private static void removeConflicts(Class currentClass) {
        for (Iterator<Student> i = currentClass.assigned_students.iterator(); i.hasNext(); ) {
            Student student = i.next();
            if (student.interested_classes.size() > 0) {
                for (Class interestedClass : student.interested_classes) {
                    if (interestedClass != currentClass && interestedClass.timeslots != null) {
                        for (Integer i1 : currentClass.timeslots.keySet()) {
                            if (interestedClass.timeslots.keySet().contains(i1)) {
                                for (Timeslot t1 : currentClass.timeslots.get(i1)) {
                                    for (Timeslot t2 : interestedClass.timeslots.get(i1)) {
                                        if (t1.day == t2.day && t1.slot_id == t2.slot_id) {
                                            interestedClass.interested_students.remove(student);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
