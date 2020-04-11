package send;

import java.util.Scanner;
import java.io.*;
import java.nio.*;

public class Send {
  public static void send(String input_file, String dest_IP, int dest_port, String emulator_IP, int emulator_port) {

    try {

      Scanner file = new Scanner(new File(input_file));
      while (file.hasNextLine()) {
        String readed_data = file.nextLine(); //Leemos cada linea del archivo
        byte[] data = readed_data.getBytes(); //Convertimos los datos tipo String a bytes
        System.out.println(data);
        String dato = new String(data);
        System.out.println(dato);
      }
      file.close();

      Send hex = new Send();

      byte[] dport = ByteBuffer.allocate(2).putShort((short) dest_port).array(); //Convertimos los parametros tipo int a bytes
      byte[] eport = ByteBuffer.allocate(2).putShort((short) emulator_port).array();
      String a = new String(dport);
      System.out.println(a);
      hex.byteToHex(dport);
      hex.byteToHex(eport);

      String[] dIP = dest_IP.split("\\.");
      //System.out.println("[" + dIP[0] + ", " + dIP[1] + ", " + dIP[2] + ", " + dIP[3] + "]");
      String data0 = dIP[0];
      String data1 = dIP[1];
      String data2 = dIP[2];
      String data3 = dIP[3];
      byte[] dIP0 = ByteBuffer.allocate(2).putShort(Short.parseShort(data0)).array();
      byte[] dIP1 = ByteBuffer.allocate(2).putShort(Short.parseShort(data1)).array();
      byte[] dIP2 = ByteBuffer.allocate(2).putShort(Short.parseShort(data2)).array();
      byte[] dIP3 = ByteBuffer.allocate(2).putShort(Short.parseShort(data3)).array();
      hex.byteToHex(dIP0);

      //String result = convertByteToHex(data);
      //System.out.println(result);

    } catch(Exception e) {
      System.out.println("Archivo incorrecto.");
      System.out.println(e);
    }
    return;
  }

  private void byteToHex(byte[] bytes){
    for (byte b : bytes ) {
      System.out.format("0x%02X ", b);
    }
    System.out.println();
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
