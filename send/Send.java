package send;

import java.util.Scanner;
import java.io.*;
import java.nio.*;
import java.net.*;
//import java.lang.System;

public class Send {
  public static void send(String input_file, String dest_IP, int dest_port, String emulator_IP, int emulator_port) {
    try {
      //Primero convertimos a bytes todos los valores que necesitamos enviar
      Send hex = new Send();
      Send IP = new Send();
      Send file = new Send();

      //Convertimos el puerto destino tipo int a bytes
      byte[] dest_port_bytes = ByteBuffer.allocate(2).putShort((short) dest_port).array();
      hex.byteToHex(dest_port_bytes);

      //Convertimos el String con la IP destino a bytes
      byte[] dest_IP_bytes = IP.getIP(dest_IP);
      hex.byteToHex(dest_IP_bytes);

      byte[] input_file_bytes = file.getFile(input_file);
      System.out.println("Bytes: " + input_file_bytes.length);
      boolean exceed_size;
      if(input_file_bytes.length < 1467){
        exceed_size = false;
      }else{
        exceed_size = true;
      }
      System.out.println("Tamaño maximo de paquete excedido: " + exceed_size);
      int cont = 0;
      byte[] text_bytes;
      //Leemos el archivo y enviamos los paquetes
      for (int i=0; i<input_file_bytes.length; i=i+1466) {
        if(input_file_bytes.length-(1466*cont) < 1466){
          text_bytes = new byte[input_file_bytes.length-(1466*cont)];
          System.arraycopy(input_file_bytes, i, text_bytes, 0, input_file_bytes.length-(1466*cont));
        }else{
          text_bytes = new byte[1466];
          System.arraycopy(input_file_bytes, i, text_bytes, 0, 1466);
        }
        String text = new String(text_bytes);
        //System.out.println(text);
        cont++;
        try {
          String response = file.sendPacket(emulator_IP, emulator_port, dest_IP_bytes, dest_port_bytes, text_bytes);
        } catch(SocketTimeoutException eSocket) {
          System.out.println("\nTimeout\n");
        }
      }
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
    return;
  }

  private byte[] getIP(String IP_text){
    String[] IP_data = IP_text.split("\\."); //System.out.println("[" + dIP[0] + ", " + dIP[1] + ", " + dIP[2] + ", " + dIP[3] + "]");
    String data0 = IP_data[0];
    String data1 = IP_data[1];
    String data2 = IP_data[2];
    String data3 = IP_data[3];
    byte[] IP0 = ByteBuffer.allocate(2).putShort(Short.parseShort(data0)).array();
    byte[] IP1 = ByteBuffer.allocate(2).putShort(Short.parseShort(data1)).array();
    byte[] IP2 = ByteBuffer.allocate(2).putShort(Short.parseShort(data2)).array();
    byte[] IP3 = ByteBuffer.allocate(2).putShort(Short.parseShort(data3)).array();
    byte[] IP_bytes = ByteBuffer.allocate(4).put(IP0[1]).put(IP1[1])
      .put(IP2[1]).put(IP3[1]).array();

    return IP_bytes;
  }

  private byte[] getFile(String file_name) throws Exception{
    Scanner file = new Scanner(new File(file_name));
    int line_counter = 0;
    StringBuffer readed_text = new StringBuffer();
    while (file.hasNextLine()) {
      line_counter++;
      readed_text.append(file.nextLine()); //Leemos cada linea del archivo
      readed_text.append("\n");
    }
    readed_text.append("DLE");
    byte[] data_text = readed_text.toString().getBytes(); //Convertimos los datos tipo String a bytes
    //String text = new String(data_text);
    //System.out.println(text);
    System.out.println("Lineas: " + line_counter);

    file.close();

    return data_text;
  }

  private String sendPacket(String emulator_IP, int emulator_port, byte[] ip, byte[] port, byte[] text) throws Exception{
    DatagramSocket datagramSocket = new DatagramSocket();

    byte[] outData = ByteBuffer.allocate(1472).put(ip).put(port).put(text).array();
    InetAddress emulatorAddress = InetAddress.getByName(emulator_IP);
    DatagramPacket outPacket = new DatagramPacket(outData, outData.length, emulatorAddress, emulator_port);
    datagramSocket.send(outPacket);

    int time = 10000;
    byte[] inData = new byte[1472];
    datagramSocket.setSoTimeout(time);
    DatagramPacket inPacket = new DatagramPacket(inData, inData.length);
    datagramSocket.receive(inPacket);
    //byte[] inText = inData.getData();
    datagramSocket.close();

    String response = new String(inData);

    return response;
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
