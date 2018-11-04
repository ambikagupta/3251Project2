import java.net.*;
import java.io.*;

class Client {

  public static void main(String args[]) throws Exception {
    String message;

    String serverIP = args[0];
    int serverPort = Integer.parseInt(args[1]);

    InetAddress addr = InetAddress.getByName(serverIP);

    Socket s = new Socket(addr, serverPort);
    System.out.println("Ready to start game? (y/n)");
    BufferedReader inUser = new BufferedReader(new InputStreamReader(System.in)); //user InputStreamReader
    BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream())); //in from server
    String yn = inUser.readLine();
    if(yn.equals("n") || yn.equals("N") || yn.equals("no") || yn.equals("No") || yn.equals("NO")) {

      s.close();
    } else {
      System.out.println("User selected yes");
      DataOutputStream out = new DataOutputStream(s.getOutputStream());
      System.out.println("Send empty message to server.");
      out.writeBytes("0," + "\n");
      message = in.readLine();
      System.out.println("Reading message from server: " + message);
      String[] parts = message.split(",");
      parts[3] = parts[3].replaceAll("_", "_ ");
      System.out.println(parts[3]);
      System.out.println("Incorrect Guesses: ");
      message = inUser.readLine();
      out.writeBytes("1," + message + "\n");

      message = in.readLine();
      parts = message.split(",");
      parts[3] = parts[3].replaceAll("_", "_ ");
      System.out.println(parts[3]);


      s.close();
    }
  }
}
