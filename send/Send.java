package send;

import java.util.Scanner;
import java.io.*;
import java.nio.*;
import java.net.*;
import java.util.Date;
import java.sql.Timestamp;

public class Send {
  public static void send(String input_file, String dest_IP, int dest_port, String emulator_IP, int emulator_port) {
    try {
      //Primero convertimos a bytes todos los valores que necesitamos enviar
      Send hex = new Send();
      Send IP = new Send();
      Send file = new Send();

      long time_start = file.timestamp(); //Tomamos el tiempo de inicio del programa

      //Convertimos el puerto destino tipo int a bytes
      byte[] dest_port_bytes = ByteBuffer.allocate(2).putShort((short) dest_port).array();
      System.out.print("Puerto de destino (hex): ");
      hex.byteToHex(dest_port_bytes);

      //Convertimos el String con la IP destino a bytes
      byte[] dest_IP_bytes = IP.getIP(dest_IP);
      System.out.print("IP de destino (hex): ");
      hex.byteToHex(dest_IP_bytes);

      //Obtenemos los datos del fichero
      byte[] input_file_bytes = file.getFile(input_file);
      System.out.println("\nBytes a enviar: " + input_file_bytes.length);

      //Comprobamos el tamaño
      boolean exceed_size;
      if(input_file_bytes.length < 1463){
        exceed_size = false;
      }else{
        exceed_size = true;
      }
      System.out.println("Tamaño maximo de paquete excedido: " + exceed_size);

      int cont = 0;
      int ack_int = (int) Math.ceil(input_file_bytes.length/1462);
      System.out.println("Numero de paquetes a enviar: " + ack_int + '\n');
      byte[] text_bytes;
      byte[] packet;

      //Leemos el archivo y enviamos los paquetes
      for (int i=0; i<input_file_bytes.length; i=i+1462) {
        //Datos del fichero para los paquetes
        if(input_file_bytes.length-(1462*cont) > 1462){
          text_bytes = new byte[1462];
          System.arraycopy(input_file_bytes, i, text_bytes, 0, 1462);
        }else{
          text_bytes = new byte[input_file_bytes.length-(1462*cont)];
          System.arraycopy(input_file_bytes, i, text_bytes, 0, input_file_bytes.length-(1462*cont));
        }
        String text = new String(text_bytes);

        //ACK
        byte[] ack_num = ByteBuffer.allocate(4).putInt(ack_int).array();
        int intprueba = ((ack_num[0] & 0xFF) << 24) | ((ack_num[1] & 0xFF) << 16) | ((ack_num[2] & 0xFF) << 8) | ((ack_num[3] & 0xFF) <<0);

        cont++;
        //Enviamos el paquete
        try {
          file.sendPacket(emulator_IP, emulator_port, dest_IP_bytes, dest_port_bytes, text_bytes, ack_num);
        } catch(Exception eSocket) {
          System.out.println("\nExcepcion: \n" + eSocket + "\n");
        }
        ack_int = ack_int-1;
      }
      System.out.println("\nArchivo transmitido.");

      System.out.println("\nTiempo de inicio en milisegundos: " + time_start);
      Timestamp ts = new Timestamp(time_start);
      System.out.println("Sello temporal de incio: " + ts);
      long time_end = file.timestamp();
      System.out.println("\nTiempo actual en milisegundos: " + time_end);
      Timestamp ts_end = new Timestamp(time_end);
      System.out.println("Sello temporal actual: " + ts_end);

    } catch(Exception e) {
      System.out.println("\nArchivo incorrecto.\n");
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
    byte[] IP_bytes;
    byte[] IP0;
    byte[] IP1;
    byte[] IP2;
    byte[] IP3;
    byte IP_0 = 127;
    byte IP_1 = 0;
    byte IP_2 = 0;
    byte IP_3 = 1;
    if(IP_text.equals("localhost")){
      IP_bytes = ByteBuffer.allocate(4).put(IP_0).put(IP_1).put(IP_2).put(IP_3).array();
    }else{
      String[] IP_data = IP_text.split("\\.");
      String data0 = IP_data[0];
      String data1 = IP_data[1];
      String data2 = IP_data[2];
      String data3 = IP_data[3];
      IP0 = ByteBuffer.allocate(2).putShort(Short.parseShort(data0)).array();
      IP1 = ByteBuffer.allocate(2).putShort(Short.parseShort(data1)).array();
      IP2 = ByteBuffer.allocate(2).putShort(Short.parseShort(data2)).array();
      IP3 = ByteBuffer.allocate(2).putShort(Short.parseShort(data3)).array();
      IP_bytes = ByteBuffer.allocate(4).put(IP0[1]).put(IP1[1]).put(IP2[1]).put(IP3[1]).array();
    }

    return IP_bytes;
  }

  private byte[] getFile(String file_name) throws Exception{

    File file = new File(file_name);
    byte[] data_text = new byte[(int) file.length()];

    FileInputStream fis = new FileInputStream(file);
    fis.read(data_text); //leemos archivo en bytes[]
    fis.close();

    return data_text;
  }

  private void sendPacket(String emulator_IP, int emulator_port, byte[] ip, byte[] port, byte[] text, byte[] ack_num) throws Exception{
    DatagramSocket datagramSocket = new DatagramSocket();

    int ack_int_recv;
    int ack_int = ((ack_num[0] & 0xFF) << 24) | ((ack_num[1] & 0xFF) << 16) | ((ack_num[2] & 0xFF) << 8) | ((ack_num[3] & 0xFF) <<0);
    byte[] ack_recv;
    int time=45;
    boolean timeout;

    do {
      //Creamos paquete de datos
      int length = ip.length+port.length+ack_num.length+text.length;
      byte[] outData = ByteBuffer.allocate(length).put(ip).put(port).put(ack_num).put(text).array();

      //Creamos y enviamos el datagram packet
      InetAddress emulatorAddress = InetAddress.getByName(emulator_IP);
      DatagramPacket outPacket = new DatagramPacket(outData, outData.length, emulatorAddress, emulator_port);
      datagramSocket.send(outPacket);

      byte[] inData = new byte[10];
      ack_recv = new byte[4];
      byte[] info_recv = new byte[6];

      //Establecemos timeout y recibimos el asentimiento del receptor
      datagramSocket.setSoTimeout(time);
      DatagramPacket inPacket = new DatagramPacket(inData, inData.length);
      try{
        datagramSocket.receive(inPacket);
      }catch (SocketTimeoutException e) {
        timeout=true;
        System.out.print("\rTimeout.");
      }

      //Sacamos los datos del paquete recibido
      inData = inPacket.getData();
      System.arraycopy(inData, 0, info_recv, 0, 6);
      System.arraycopy(inData, 6, ack_recv, 0, 4);
      ack_int_recv = ((ack_recv[0] & 0xFF) << 24) | ((ack_recv[1] & 0xFF) << 16) | ((ack_recv[2] & 0xFF) << 8) | ((ack_recv[3] & 0xFF) <<0);
      System.out.print("\rAck: " + ack_int + "                 ");

      timeout=false;

    } while ((ack_int_recv!=ack_int) || (timeout==true)); //Comprobamos el ack y el timeout por si es necesario reenviar

    datagramSocket.close();

    return;
  }

  private long timestamp() {

    Date date= new Date();
    long time = date.getTime();

    return time;
  }

}
