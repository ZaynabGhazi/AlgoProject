import java.util.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
public class Timeslot{
  // '0' is SUN
  int day;
  // '12' hr format
  Date start_time;
  Date end_time;
  int course_id;
  //the duration is the duration of the class, and it should be in milliseconds
public Timeslot(int day, Date start_time, long duration){
  this.day=day;
  this.start_time=start_time;
  Calendar c = Calendar.getInstance();
  c.setTime(start_time);
  c.add(Calendar.MILLISECOND,(int)duration);
  this.end_time=c.getTime();
}
public String toString(){
  SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
  return day+" "+sdf.format(start_time);
}

}
