public class Room implements Comparable<Room> {
  String id;
  int cap;
  public Room(String id, int cap){this.id=id;this.cap=cap;}
  public String toString(){
  return id+" "+cap;
}
  @Override
  public int compareTo(Room r) {
    int compareCap = ((Room) r).cap;
    return this.cap - compareCap;
  }
}
