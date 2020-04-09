import java.io.*;
import java.util.Scanner;
import send.*;

public class ro1920send {
  public static void main(String[] args) {

    if(args.length==5){

      String input_file = args[0];
      String dest_IP = args[1];
      int dest_port = Integer.parseInt(args[2]);
      String emulator_IP = args[3];
      int emulator_port = Integer.parseInt(args[4]);
      send.send(input_file, dest_IP, dest_port, emulator_IP, emulator_port);

    } else {
      System.out.println("Error de sintaxis. El formato introducido debe ser: ro1920send input_file dest_IP dest_port emulator_IP emulator_port");

    }
    return;
  }

  /*public static String convertByteToHex(byte[] bytes){
    StringBuilder result = new StringBuilder();

    for (byte temp : bytes){
      int decimal = (int) temp & 0xff;
      String hex = Integer.toHexString(decimal);
      result.append(hex);
    }
    return result.toString();
  }*/

}
