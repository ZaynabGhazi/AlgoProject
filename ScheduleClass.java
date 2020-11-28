import java.util.*;

public class ScheduleClass {
	public static void schedule(HashMap<String, Class> classMap, HashMap<Integer, List<Timeslot>> timeSlots,
			HashMap<Integer, HashMap<Integer, Integer>> conflicts) {
		// Sort by number of meeting times， and then by interest
		List<Class> classes = new ArrayList<>();
		for (Class c : classMap.values()) {
			classes.add(c);
		}
		Collections.sort(classes, new Comparator<Class>() {
			public int compare(Class c1, Class c2) {
				if (c1.num_meetings == c2.num_meetings) {
					return c2.interested_students.size() - c1.interested_students.size();
				}
				return c2.num_meetings - c1.num_meetings;
			}
		});

		for (Class c : classes) {
			Map<Integer, List<Timeslot>> slots = new HashMap<>();
			int numSlots = (int) Math.ceil(c.duration / 30.0);
			if (c.num_meetings == 5) {
				int[] assignSlot = calculateConflict(1, c, timeSlots.get(1), conflicts, numSlots);
				int startSlot = assignSlot[0];
				int minConflict = assignSlot[1];
				if (minConflict == Integer.MAX_VALUE) {
					System.out.println(c.id + " not able to be scheduled!");
					continue;
				}

				// update the course's time slots
				for (int i = 1; i <= 5; i++) {
					List<Timeslot> slotsOnADay = new ArrayList<>();
					for (int j = 0; j < numSlots; j++) {
						slotsOnADay.add(new Timeslot(1, startSlot + j));
					}
					slots.put(i, slotsOnADay);
				}
				c.timeslots = slots;

				// update each timeslot's course list and the number of available rooms
				for (int i = 1; i <= 5; i++) {
					for (int j = 0; j < numSlots; j++) {
						timeSlots.get(i).get(startSlot + j).courses.add(c);
						timeSlots.get(i).get(startSlot + j).numRooms.put(c.depart,
								timeSlots.get(i).get(startSlot + j).numRooms.get(c.depart) - 1);
					}
				}

			}

			else if (c.num_meetings == 4) {
				int[] assignSlot = calculateConflict(1, c, timeSlots.get(1), conflicts, numSlots);
				int startSlot = assignSlot[0];
				int minConflict = assignSlot[1];
				if (minConflict == Integer.MAX_VALUE) {
					System.out.println(c.id + " not able to be scheduled!");
					continue;
				}

				// update the course's time slots
				for (int i = 1; i <= 4; i++) {
					List<Timeslot> slotsOnADay = new ArrayList<>();
					for (int j = 0; j < numSlots; j++) {
						slotsOnADay.add(new Timeslot(1, startSlot + j));
					}
					slots.put(i, slotsOnADay);
				}
				c.timeslots = slots;

				// update each timeslot's course list and the number of available rooms
				for (int i = 1; i <= 4; i++) {
					for (int j = 0; j < numSlots; j++) {
						timeSlots.get(i).get(startSlot + j).courses.add(c);
						timeSlots.get(i).get(startSlot + j).numRooms.put(c.depart,
								timeSlots.get(i).get(startSlot + j).numRooms.get(c.depart) - 1);
					}
				}

			}

			else if (c.num_meetings == 3) {
				int[] assignSlot = calculateConflict(1, c, timeSlots.get(1), conflicts, numSlots);
				int startSlot = assignSlot[0];
				int minConflict = assignSlot[1];
				if (minConflict == Integer.MAX_VALUE) {
					System.out.println(c.id + " not able to be scheduled!");
					continue;
				}

				// update the course's time slots
				List<Timeslot> slotsOnMon = new ArrayList<>();
				List<Timeslot> slotsOnWed = new ArrayList<>();
				List<Timeslot> slotsOnFri = new ArrayList<>();
				for (int j = 0; j < numSlots; j++) {
					slotsOnMon.add(new Timeslot(1, startSlot + j));
					slotsOnWed.add(new Timeslot(3, startSlot + j));
					slotsOnFri.add(new Timeslot(5, startSlot + j));
				}
				slots.put(1, slotsOnMon);
				slots.put(3, slotsOnWed);
				slots.put(5, slotsOnFri);
				c.timeslots = slots;

				// update each timeslot's course list and the number of available rooms
				for (int j = 0; j < numSlots; j++) {
					timeSlots.get(1).get(startSlot + j).courses.add(c);
					timeSlots.get(3).get(startSlot + j).courses.add(c);
					timeSlots.get(5).get(startSlot + j).courses.add(c);
					timeSlots.get(1).get(startSlot + j).numRooms.put(c.depart,
							timeSlots.get(1).get(startSlot + j).numRooms.get(c.depart) - 1);
					timeSlots.get(3).get(startSlot + j).numRooms.put(c.depart,
							timeSlots.get(3).get(startSlot + j).numRooms.get(c.depart) - 1);
					timeSlots.get(5).get(startSlot + j).numRooms.put(c.depart,
							timeSlots.get(5).get(startSlot + j).numRooms.get(c.depart) - 1);
				}

			}

			else if (c.num_meetings == 2) {
				int[] assignSlotMonWed = calculateConflict(1, c, timeSlots.get(1), conflicts, numSlots);
				int[] assignSlotTuThu = calculateConflict(2, c, timeSlots.get(2), conflicts, numSlots);
				int conflictMonWed = assignSlotMonWed[1];
				int conflictTueThu = assignSlotTuThu[1];
				if (conflictMonWed == Integer.MAX_VALUE && conflictTueThu == Integer.MAX_VALUE) {
					System.out.println(c.id + " not able to be scheduled!");
					continue;
				}

				// pick Monday Wednesday
				if (conflictMonWed <= conflictTueThu) {
					int startSlot = assignSlotMonWed[0];
					List<Timeslot> slotsOnMon = new ArrayList<>();
					List<Timeslot> slotsOnWed = new ArrayList<>();
					for (int j = 0; j < numSlots; j++) {
						slotsOnMon.add(new Timeslot(1, startSlot + j));
						slotsOnWed.add(new Timeslot(3, startSlot + j));
					}
					slots.put(1, slotsOnMon);
					slots.put(3, slotsOnWed);
					c.timeslots = slots;
					for (int j = 0; j < numSlots; j++) {
						timeSlots.get(1).get(startSlot + j).courses.add(c);
						timeSlots.get(3).get(startSlot + j).courses.add(c);
						timeSlots.get(1).get(startSlot + j).numRooms.put(c.depart,
								timeSlots.get(1).get(startSlot + j).numRooms.get(c.depart) - 1);
						timeSlots.get(3).get(startSlot + j).numRooms.put(c.depart,
								timeSlots.get(3).get(startSlot + j).numRooms.get(c.depart) - 1);
					}

				}

				// Pick Tuesday Thursday
				else {
					int startSlot = assignSlotTuThu[0];
					List<Timeslot> slotsOnTu = new ArrayList<>();
					List<Timeslot> slotsOnThu = new ArrayList<>();
					for (int j = 0; j < numSlots; j++) {
						slotsOnTu.add(new Timeslot(2, startSlot + j));
						slotsOnThu.add(new Timeslot(4, startSlot + j));
					}
					slots.put(2, slotsOnTu);
					slots.put(4, slotsOnThu);
					c.timeslots = slots;
					for (int j = 0; j < numSlots; j++) {
						timeSlots.get(2).get(startSlot + j).courses.add(c);
						timeSlots.get(4).get(startSlot + j).courses.add(c);
						timeSlots.get(2).get(startSlot + j).numRooms.put(c.depart,
								timeSlots.get(1).get(startSlot + j).numRooms.get(c.depart) - 1);
						timeSlots.get(4).get(startSlot + j).numRooms.put(c.depart,
								timeSlots.get(3).get(startSlot + j).numRooms.get(c.depart) - 1);
					}

				}
			}

			// Only one meeting
			else {
				int minConflict = Integer.MAX_VALUE;
				int minDay = 1;
				int minStartSlot = 1;
				for (int i = 1; i <= 5; i++) {
					int[] assignSlot = calculateConflict(i, c, timeSlots.get(i), conflicts, numSlots);
					int conflict = assignSlot[1];
					if (conflict < minConflict) {
						minConflict = conflict;
						minDay = i;
						minStartSlot = assignSlot[0];
					}
				}
				if (minConflict == Integer.MAX_VALUE) {
					System.out.println(c.id + " not able to be scheduled!");
					continue;
				}

				// update the course's time slots
				List<Timeslot> slotsOnMinDay = new ArrayList<>();
				for (int j = 0; j < numSlots; j++) {
					slotsOnMinDay.add(new Timeslot(1, minStartSlot + j));
				}
				slots.put(minDay, slotsOnMinDay);
				c.timeslots = slots;

				// update each timeslot's course list and the number of available rooms
				for (int j = 0; j < numSlots; j++) {
					timeSlots.get(minDay).get(minStartSlot + j).courses.add(c);
					timeSlots.get(minDay).get(minStartSlot + j).numRooms.put(c.depart,
							timeSlots.get(1).get(minStartSlot + j).numRooms.get(c.depart) - 1);
				}

			}

		}

	}

	private static int[] calculateConflict(int day, Class c, List<Timeslot> timeSlots,
			HashMap<Integer, HashMap<Integer, Integer>> conflicts, int numSlots) {
		// return the id and #conflicts of the least conflict slot in a given day
		int minStartSlotId = 1;
		int minConflict = Integer.MAX_VALUE;
		for (int i = 0; i < timeSlots.size() - numSlots; i++) {
			int conflict = 0;
			for (int j = 0; j < numSlots; j++) {
				int numRooms = timeSlots.get(i + j).numRooms.get(c.depart);
				if (numRooms == 0) {
					conflict = Integer.MAX_VALUE;
					break; // if the slot has run out of all available room for the course, set conflict to
							// max
				}
				List<Class> courses = timeSlots.get(i + j).courses;
				for (Class course : courses) {
					if (course.prof_id == c.prof_id) {
						conflict = Integer.MAX_VALUE;
						break; // same professor, set conflict to max
					}
					if (conflicts.get(course.id).get(c.id) == null) {
						continue; // when the two courses have no common students, no need to update conflict
					} else {
						if (conflicts.get(course.id).get(c.id) == Integer.MAX_VALUE) {
							conflict = Integer.MAX_VALUE;
							break;
						}
						conflict += conflicts.get(course.id).get(c.id);
					}
				}
				if (conflict == Integer.MAX_VALUE) {
					break; // if conflict is already max, no need to check the rest of slots
				}
			}
			if (conflict < minConflict) {
				minConflict = conflict;
				minStartSlotId = i + 1; // id is 1 - 18, i is 0 - 17
			}
		}

		int[] result = { minStartSlotId, minConflict };
		return result;

	}

	public static void printSchedule(Map<String, Class> classMap) {
		List<Class> classes = new ArrayList<>();
		for (Class c : classMap.values()) {
			classes.add(c);
		}
		Collections.sort(classes, new Comparator<Class>() {
			public int compare(Class c1, Class c2) {
				if (c1.num_meetings == c2.num_meetings) {
					return c2.interested_students.size() - c1.interested_students.size();
				}
				return c2.num_meetings - c1.num_meetings;
			}
		});
		for (Class c : classes) {
			if (c.timeslots == null) {
				System.out.println(c.id + " cannot be scheduled");
				continue;
			}
			String days = "";
			int randomDay = -1;
			for (int day : c.timeslots.keySet()) {
				randomDay = day;
				days += convertDay(day);
			}
			if (c.timeslots.get(randomDay).size() != 0) {
				int startSlot = c.timeslots.get(randomDay).get(0).slot_id;
				double startTime = 8 + (startSlot - 1) * 0.5;
				String[] startEndTime = convertTime(startTime, c.duration);
				System.out.print(c.course_name + " " + days + " " + startEndTime[0] + "-" + startEndTime[1]);
				System.out.print(" Room:" + c.room + " ");
				System.out.print("Students:" + c.assigned_students);
				System.out.println();
			}
		}
	}

	// Debugger to check if any conflict professors
	public static void printSlots(Map<Integer, List<Timeslot>> slots) {
		for (int i = 1; i <= 5; i++) {
			List<Timeslot> slotsOnADay = slots.get(i);
			for (Timeslot slot : slotsOnADay) {
				List<Class> courses = slot.courses;
				System.out.print(slot.slot_id + ": ");
				Set<Integer> profs = new HashSet<>();
				for (Class c : courses) {
					if (profs.contains(c.prof_id)) {
						System.out.println("Oops!!! Same Prof in the same slot!");
					}
					profs.add(c.prof_id);
					System.out.print(c.course_name + " ");
				}
				System.out.println();

			}
		}

	}

	private static String[] convertTime(double startTime, int duration) {
		int hour = (int) startTime / 1;
		int minute = 0;
		String newStartTime = "";
		String newEndTime = "";
		String[] result = new String[2];
		if (startTime % 1 != 0) { //start time is either at x:00 or x:30, this is the case for x:30
			minute = 30; 
			newStartTime = hour + ":30";
		} else {// x:00
			minute = 0;
			newStartTime = hour + ":00";
		}

		int durationHour = duration / 60;
		int durationMin = duration % 60;
		int endMin = (minute + durationMin) % 60;
		String newEndMin = endMin + "";
		if (endMin == 0) {
			newEndMin += "0"; // to make minutes look like ":00" instead of ":0"
		}
		int endHr = (minute + durationMin) / 60 + hour + durationHour;
		newEndTime = endHr + ":" + newEndMin;
		result[0] = newStartTime;
		result[1] = newEndTime;
		return result;
	}

	private static String convertDay(int day) {
		Map<Integer, String> map = new HashMap<>();
		map.put(0, "Su");
		map.put(1, "Mo");
		map.put(2, "Tu");
		map.put(3, "We");
		map.put(4, "Th");
		map.put(5, "Fr");
		map.put(6, "Sa");
		return map.get(day);
	}
}
