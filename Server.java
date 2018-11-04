import java.net.*;
import java.io.*;
import java.util.Random;
import java.util.ArrayList;

class Server{

  public static void main(String args[]) throws Exception {
    //String Lmessage;
    //String Umessage;
    String message;
    //String[] words = new String["plan", "puzzle", "academic", "joke", "jacket", "macaroni", "time", "volume", "delivery", "home", "handle", "sprinkle", "jump", "weight", "midnight"];
    String[] words = new String[15];
    words[0] = "plan";
    words[1] = "puzzle";
    words[2] = "academic";
    words[3] = "joke";
    words[4] = "jacket";
    words[5] = "macaroni";
    words[6] = "time";
    words[7] = "volume";
    words[8] = "delivery";
    words[9] = "home";
    words[10] = "handle";
    words[11] = "sprinkle";
    words[12] = "jump";
    words[13] = "weight";
    words[14] = "midnight";
    Random rand = new Random();
    String word = "";
    String length = "";
    String data = "";

    int port = Integer.parseInt(args[0]);

    ServerSocket ss = new ServerSocket(port);
    while(true) {
      Socket s = ss.accept();
      System.out.println("Accepted the connection");
      BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
      DataOutputStream out = new DataOutputStream(s.getOutputStream());

      message = in.readLine();
      System.out.println("Read the message: " + message);
      String[] parts = message.split(",");

      if(parts[0].equals("0")) {
        //send the word that the player will be guessing.
        System.out.println("Empty message sent, start game");
        int num = rand.nextInt(15);
        System.out.println("Random Number selected: " + num);
        word = words[num];
        System.out.println("Selected Word: " + word);
        length = Integer.toString(word.length());
        data = "";
        for(int i = 0; i < word.length(); i++) {
          data = data + "_ ";
        }
        //DataOutputStream out = new DataOutputStream(s.getOutputStream());
        out.writeBytes("0," + length + ",0," + data + "\n");
      }
      message = in.readLine();
      parts = message.split(",");
      String guess_1 = parts[1];
      char guess = guess_1.charAt(0);
      ArrayList<Integer> guessPosition = new ArrayList<>();
      for(int i = 0; i < word.length(); i++) {
        char c = word.charAt(i);
        if(c == guess) {
          guessPosition.add(i);
        }
      }
      if(guessPosition.size() == 0) {
        out.writeBytes("0," + length + ",1," + data + "," + guess_1 + "\n");
      } else {
        for(int i = 0; i < guessPosition.size(); i++) {
          int pos = guessPosition.get(i);
          String old_data = data;
          data = "";
          for(int j = 0; j < word.length(); j++) {
            if(j == pos) {
              data = data + guess;
            } else {
              data = data + old_data.charAt(j);
            }
          }
        }
        out.writeBytes("0," + length + ",0," + data + "\n");
      }
      //System.out.println("Connected to server");
      //BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream())); //in to Server
      //DataOutputStream out = new DataOutputStream(s.getOutputStream()); //out to client

      //Lmessage = in.readLine();
      //System.out.println("Received (Lowercase): " + Lmessage);

      //Umessage = Lmessage.toUpperCase() + "\n"; //to toUpperCase
      //out.writeBytes(Umessage); //sends
    }
  }
}
