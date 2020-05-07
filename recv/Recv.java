package recv;

import java.util.Scanner;
import java.io.*;
import java.nio.*;
import java.net.*;

public class Recv {
  public static void recv(String output_file, int listen_port) {

    Recv getFile = new Recv();

    System.out.println("File: " + output_file + "\nPort: " + listen_port);

    try {
      DatagramSocket datagramSocket = new DatagramSocket(listen_port);
      byte[] packet = getFile.receivePacket(datagramSocket); //Archivo recibido
      datagramSocket.close();
      byte[] data = new byte[packet.length-6];
      /*int j = 0;
      for(int i=6; i<packet.length; i++){
        data[j] = packet[i];
        j++;
      }*/
      File file = new File(output_file);
      //BufferedWriter fileWriter = new BufferedWriter(new FileWriter(output_file));
      //BufferedWriter fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output_file), "UTF-8"));
      //PrintWriter out = new PrintWriter(bWriter);
      //String texto = new String(packet);
      //System.out.println(texto);
      //out.println(texto);
      FileOutputStream fileWriter = new FileOutputStream(file);
      fileWriter.write(packet);
      fileWriter.close();
    } catch(Exception e) {
      System.out.println(e);
    }

    return;
  }

  private byte[] receivePacket(DatagramSocket datagramSocket) throws Exception{

    /*byte[] outData = ByteBuffer.allocate(1472).put(ip).put(port).put(text).array();
    InetAddress emulatorAddress = InetAddress.getByName(emulator_IP);
    DatagramPacket outPacket = new DatagramPacket(outData, outData.length, emulatorAddress, emulator_port);
    datagramSocket.send(outPacket);*/

    byte[] inData = new byte[1472];
    byte[] data;
    byte[] info_send = new byte[6];
    byte[] ack_send = new byte[4];
    int ack_received;
    int ack_prev = -1;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
     do {
      //datagramSocket.setSoTimeout(5000);
      DatagramPacket inPacket = new DatagramPacket(inData, inData.length);
      datagramSocket.receive(inPacket);
      //inData = new byte[inPacket.getLength()];
      inData = inPacket.getData();
      //String received = new String (inData, 0, inPacket.getLength());
      //System.out.println(received);
      System.out.println(inPacket.getLength());
      System.arraycopy(inData, 0, info_send, 0, 6);
      System.arraycopy(inData, 6, ack_send, 0, 4);
      data = new byte[inPacket.getLength()-10];
      System.arraycopy(inData, 10, data, 0, inPacket.getLength()-10);

      //System.out.println(ByteBuffer.wrap(ack_send).getInt());
      System.out.println(ack_send);
      ack_received = ((ack_send[0] & 0xFF) << 24) | ((ack_send[1] & 0xFF) << 16) | ((ack_send[2] & 0xFF) << 8) | ((ack_send[3] & 0xFF) <<0);
      System.out.println(ack_received);
      if (ack_prev!=ack_received){
        bos.write(data, 0, data.length);
      }

      InetAddress emulatorAddress = inPacket.getAddress();
      int emulatorPort = inPacket.getPort();
      System.out.println(emulatorPort);
      byte[] outData = new byte[10];
      System.arraycopy(info_send, 0, outData, 0, 6);
      System.arraycopy(ack_send, 0, outData, 6, 4);
      DatagramPacket outPacket = new DatagramPacket(outData, outData.length, emulatorAddress, emulatorPort);
      datagramSocket.send(outPacket);
      System.out.println("Sent.");
      ack_prev = ack_received;
    } while(ack_received!=0);

    //String file = new String(inData);

    byte[] output = bos.toByteArray();
    return output;
  }

}
