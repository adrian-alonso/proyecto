package send;


public class Send {
  public static void send(String input_file, String dest_IP, int dest_port, String emulator_IP, int emulator_port) {
    try {
      Scanner file = new Scanner(new File(input_file));
      while (file.hasNextLine()) {
        //System.out.println(file.nextLine());
        String readed_data = file.nextLine();
        byte[] data = readed_data.getBytes();
        System.out.println(data);

        //String result = convertByteToHex(data);
        //System.out.println(result);
      }

    } catch(Exception e) {
      System.out.println("Archivo incorrecto.");
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
