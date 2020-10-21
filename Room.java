import java.util.*;
import java.io.*;
public class Room implements Comparable<Room> {
  int id;
  int cap;

  public Room(int id, int cap){
    this.id=id;this.cap=cap;
  }

  public String toString(){
    return id+" "+cap;
  }

  @Override
  public int compareTo(Room r) {
    int compareCap = ((Room) r).cap;
    return this.cap - compareCap;
  }
}


