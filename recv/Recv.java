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
      DatagramSocket datagramSocket = new DatagramSocket(3000);
      byte[] packet = getFile.receivePacket(datagramSocket); //Archivo recibido
      byte[] data = new byte[packet.length-6];
      int j = 0;
      for(int i=6; i<packet.length; i++){
        data[j] = packet[i];
        j++;
      }
      File file = new File(output_file);
      BufferedWriter bWriter = new BufferedWriter(new FileWriter(output_file));
      //BufferedWriter bWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output_file), "UTF-8"));
      //PrintWriter out = new PrintWriter(bWriter);
      String texto = new String(data);
      //System.out.println(texto);
      //out.println(texto);
      bWriter.write(texto);
      bWriter.close();
      datagramSocket.close();
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


    //while() {
      datagramSocket.setSoTimeout(5000);
      byte[] inData = new byte[1472];
      DatagramPacket inPacket = new DatagramPacket(inData, inData.length);
      datagramSocket.receive(inPacket);
      String received = new String(inData);
      System.out.println(received);

      InetAddress emulatorAddress = inPacket.getAddress();
      int emulatorPort = inPacket.getPort();
      System.out.println(emulatorPort);
      byte[] outData = "ack".getBytes();
      DatagramPacket outPacket = new DatagramPacket(outData, outData.length, emulatorAddress, emulatorPort);
      datagramSocket.send(outPacket);
    //}

    //String file = new String(inData);

    return inData;
  }

}
