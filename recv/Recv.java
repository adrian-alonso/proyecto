package recv;

import java.util.Scanner;
import java.io.*;
import java.nio.*;
import java.net.*;

public class Recv {
  public static void recv(String output_file, int listen_port) {

    Recv file = new Recv();

    System.out.println("File: " + output_file + "\nPort: " + listen_port);

    try {
      byte[] text = file.receivePacket();
    } catch(Exception e) {
      System.out.println(e);
    }

    return;
  }

  private byte[] receivePacket() throws Exception{
    DatagramSocket datagramSocket = new DatagramSocket(3000);

    /*byte[] outData = ByteBuffer.allocate(1472).put(ip).put(port).put(text).array();
    InetAddress emulatorAddress = InetAddress.getByName(emulator_IP);
    DatagramPacket outPacket = new DatagramPacket(outData, outData.length, emulatorAddress, emulator_port);
    datagramSocket.send(outPacket);*/

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

    datagramSocket.close();

    String file = new String(inData);

    return inData;
  }

}
