import java.io.*;
import java.util.Scanner;

public class ro1920send {
  public static void main(String[] args) {

    if(args.length==5){

      String input_file = args[0];
      String dest_IP = args[1];
      int dest_port = Integer.parseInt(args[2]);
      String emulator_IP = args[3];
      int emulator_port = Integer.parseInt(args[4]);

      try {
        Scanner archivo = new Scanner(new File(input_file));
        while (archivo.hasNextLine()) {
          //System.out.println(archivo.nextLine());
          
        }

      } catch(Exception e) {
        System.out.println("Archivo incorrecto.");
      }
    } else {
      System.out.println("Error de sintaxis. El formato introducido debe ser: ro1920send input_file dest_IP dest_port emulator_IP emulator_port");

    }
    return;
  }

}
