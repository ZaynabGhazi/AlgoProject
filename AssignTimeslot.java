
import java.util.*;
public class AssignTimeslot {
	public static void scheduleClass(Class[] classes, Timeslot[] timeSlots, int[][] conflicts, int numRooms){
		Arrays.sort(classes, (Class a, Class b) -> b.interested_students.size() - a.interested_students.size());
		for(Class c: classes) {
			int minConflict = Integer.MAX_VALUE;
			Timeslot minConflictSlot = null;
			for (Timeslot t : timeSlots) {
				if(t.scheduled_classes.size()<numRooms) {
					int conflictsInSlot = 0;
					for(Class scheduled: t.scheduled_classes) {
						if (conflicts[scheduled.id-1][c.id-1] == Integer.MAX_VALUE || scheduled.prof == c.prof) {
							conflictsInSlot = Integer.MAX_VALUE;
							break;
						}
						else{
							conflictsInSlot += conflicts[scheduled.id-1][c.id-1];
						}
					}
					if(conflictsInSlot < minConflict) {
						minConflict = conflictsInSlot;
						minConflictSlot = t;
					}
				}
			}

			if(minConflictSlot!=null){
				minConflictSlot.scheduled_classes.add(c);
				c.timeslot = minConflictSlot;
			}
		}
	}
}
