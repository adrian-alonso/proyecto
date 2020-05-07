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
      if(input_file_bytes.length < 1463){
        exceed_size = false;
      }else{
        exceed_size = true;
      }
      System.out.println("Tamaño maximo de paquete excedido: " + exceed_size);
      int cont = 0;
      int ack_int = (int) Math.ceil(input_file_bytes.length/1462);
      System.out.println(ack_int);
      //byte ack = Byte.parseByte("00000110");
      //byte[] ack_packet = new byte[5];
      byte[] text_bytes;
      byte[] packet;
      //Leemos el archivo y enviamos los paquetes
      for (int i=0; i<input_file_bytes.length; i=i+1462) {
        if(input_file_bytes.length-(1462*cont) > 1462){
          text_bytes = new byte[1462];
          System.arraycopy(input_file_bytes, i, text_bytes, 0, 1462);
        }else{
          text_bytes = new byte[input_file_bytes.length-(1462*cont)];
          System.arraycopy(input_file_bytes, i, text_bytes, 0, input_file_bytes.length-(1462*cont));
        }
        String text = new String(text_bytes);
        //System.out.println(text);

        byte[] ack_num = ByteBuffer.allocate(4).putInt(ack_int).array();
        int intprueba = ((ack_num[0] & 0xFF) << 24) | ((ack_num[1] & 0xFF) << 16) | ((ack_num[2] & 0xFF) << 8) | ((ack_num[3] & 0xFF) <<0);
        System.out.println(intprueba);
        cont++;
        try {
          String response = file.sendPacket(emulator_IP, emulator_port, dest_IP_bytes, dest_port_bytes, text_bytes, ack_num);
          System.out.println(response);
        } catch(SocketTimeoutException eSocket) {
          System.out.println("\nTimeout\n");
        }
        ack_int = ack_int-1;
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
    /*File file = new File(file_name);
    InputStream data = new BufferedInputStream(new FileInputStream(file));
    byte[] data_text = new byte[file_name.length()];
    int numRead = data.read(data_text);*/

    //BufferedReaded file = new BufferedReaded(new OutputStreamWriter(new FileOutputStream(file_name),
    //readed_text.append("DLE");
    //byte[] data_text = readed_text.toString().getBytes(); //Convertimos los datos tipo String a bytes

    File file = new File(file_name);
    //init array with file length
    byte[] data_text = new byte[(int) file.length()];

    FileInputStream fis = new FileInputStream(file);
    fis.read(data_text); //read file into bytes[]
    fis.close();

    /*Scanner file = new Scanner(new File(file_name));
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

    file.close();*/

    return data_text;
  }

  private String sendPacket(String emulator_IP, int emulator_port, byte[] ip, byte[] port, byte[] text, byte[] ack_num) throws Exception{
    DatagramSocket datagramSocket = new DatagramSocket();

    int ack_int_recv;
    int ack_int = ((ack_num[0] & 0xFF) << 24) | ((ack_num[1] & 0xFF) << 16) | ((ack_num[2] & 0xFF) << 8) | ((ack_num[3] & 0xFF) <<0);
    byte[] ack_recv;
    boolean timeout;
    do {
      //do{
      int length = ip.length+port.length+ack_num.length+text.length;
      byte[] outData = ByteBuffer.allocate(length).put(ip).put(port).put(ack_num).put(text).array();
      InetAddress emulatorAddress = InetAddress.getByName(emulator_IP);
      DatagramPacket outPacket = new DatagramPacket(outData, outData.length, emulatorAddress, emulator_port);
      datagramSocket.send(outPacket);
      System.out.println("Sent.");

      int time = 50;
      byte[] inData = new byte[10];
      ack_recv = new byte[4];
      byte[] info_recv = new byte[6];

      datagramSocket.setSoTimeout(time);
      DatagramPacket inPacket = new DatagramPacket(inData, inData.length);
      try{
        datagramSocket.receive(inPacket);
      }catch (SocketTimeoutException e) {
        timeout=true;
      }
      inData = inPacket.getData();
      System.arraycopy(inData, 0, info_recv, 0, 6);
      System.arraycopy(inData, 6, ack_recv, 0, 4);
      String ack_recv_data = new String (ack_recv);
      ack_int_recv = ((ack_recv[0] & 0xFF) << 24) | ((ack_recv[1] & 0xFF) << 16) | ((ack_recv[2] & 0xFF) << 8) | ((ack_recv[3] & 0xFF) <<0);
      timeout=false;
      //ack_int_recv = ByteBuffer.wrap(ack_recv).getInt();
      System.out.println("Ack: "+ ack_int_recv+" : "+ack_int);
      //ack_int_recv = Integer.parseInt(ack_recv_data);
      //byte[] inText = inData.getData();
    //  }while(timeout=true);
  } while ((ack_int_recv!=ack_int) || (timeout==true));

    datagramSocket.close();

    //String response = new String(inData);
    String response = "OK.";

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
