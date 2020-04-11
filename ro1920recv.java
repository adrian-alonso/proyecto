import java.io.*;
import recv.*;

public class ro1920recv {
  public static void main(String[] args) {

    if(args.length==2){

      String output_file = args[0];
      int listen_port = Integer.parseInt(args[1]);
      Recv.recv(output_file, listen_port);

    } else {
      System.out.println("Error de sintaxis. El formato introducido debe ser: ro1920recv output_file listen_port");

    }
    return;
  }

}
