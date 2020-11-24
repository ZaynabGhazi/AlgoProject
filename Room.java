import java.util.*;
import java.io.*;
public class Room{
String id;
int cap;
public Room(String id, int cap){this.id=id;this.cap=cap;}
public String toString(){
  return id+" "+cap;
}
}
