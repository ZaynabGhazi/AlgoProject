import java.util.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
public class Timeslot {
	  // '0' is SUN
	  int day;
	  int slot_id;
	  List<Class> courses;
	  Map<String, Integer> numRooms; //subject and the associated number of available rooms
	public Timeslot(int day, int slot_id){
		this.day = day;
		this.slot_id = slot_id;
		courses = new ArrayList<>();
		numRooms = new HashMap<>();
	}
//	public String toString(){
//	  SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//	  return day+" "+sdf.format(start_time);
//	}

}