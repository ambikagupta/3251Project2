import java.net.*;
import java.io.*;

//This was Client2

class Client {

	public static void main(String args[]) throws Exception {
		String userGuess;
		String serverMsg;

		String serverIP = args[0];
		int serverPort = Integer.parseInt(args[1]);

		InetAddress addr = InetAddress.getByName(serverIP);

		int numIncorrect;
		String incorrectGuesses;
    int wordLength;
    int numBlanks;
    boolean myBool = true;
    String[] parts;



    // socket creation
		Socket s = new Socket(addr, serverPort);
		BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream())); //in from server
    DataOutputStream out = new DataOutputStream(s.getOutputStream()); // message to send to server
    BufferedReader inUser = new BufferedReader(new InputStreamReader(System.in)); //user InputStreamReader

    System.out.println("Two Player? (y/n)");
    String yn = inUser.readLine();    // Answer to Two Player question.

    boolean checkYN = true;

    // validating user input from Two Player question (y or n)
    while (checkYN) {
      if (!(yn.equals("y") || yn.equals("n") || yn.equals("Y") || yn.equals("N"))) {
        System.out.println("Two Player? (y/n)");
        yn = inUser.readLine();    // Anser to Two Player question
      } else {
        checkYN = false;
      }
    }

    // send multiplayer or single player
    if(yn.equals("y") || yn.equals("Y")) {
      out.writeBytes("2\n"); // send 2 signaling multiplayer
    } else {
      out.writeBytes("0\n"); // send 0 signaling single player
    }

    // GAME LOOP GOOD LUCK
    while(myBool) {
      serverMsg = in.readLine(); //read packet from server

      // IF GAME PACKET
      if(serverMsg.charAt(0) == '0') {
        parts = serverMsg.split(""); // msg flag, word length, num incorrect, data
        numIncorrect = Integer.parseInt(parts[2]);
        wordLength = Integer.parseInt(parts[1]);
        numBlanks = 0;

        // print out the word to guess
        for (int i = 3; i < 3 + wordLength; i++) {
          System.out.print(parts[i] + " ");
          // count the number of blanks left
          if (parts[i].equals("_")) {
            numBlanks++;
          }
        }

        System.out.println();

        // format and print out incorrect guesses
        incorrectGuesses = "";
        for (int i = 3 + wordLength; i < parts.length; i++) {
          incorrectGuesses = incorrectGuesses + parts[i] + " ";
        }
        System.out.println("Incorrect Guesses: " + incorrectGuesses);
        System.out.println("");

        //Print out letter guess and get user input for letter quess
        if (numBlanks != 0 && numIncorrect < 6) {
          System.out.print("Letter to Guess: ");
          userGuess = inUser.readLine();  // read in user's guess
          userGuess = checkInput(userGuess, inUser, numIncorrect, wordLength, parts); //check the input given by user
          out.writeBytes("1" + userGuess + "\n"); // send user's guess to server
        }

        // if message
      } else {
        System.out.println(serverMsg);
        if (serverMsg.contains("Game Over!") || serverMsg.contains("server-overload")) {
          myBool = false;
          s.close();
        }
      }
    }
    s.close();
	}

  public static String checkInput(String userGuess, BufferedReader inUser, int numIncorrect, int wordLength, String[] parts) throws Exception{

    boolean checkInput = true; // check if user's input is valid
    int checkCount;

    while (checkInput) {

      checkCount = 0; // which case to go to


      // if user presses enter without a character there
      if (userGuess.length() == 0) {
        checkCount = 3;

      // if user enters multiple characters
      } else if (userGuess.length() > 1) {
        checkCount = 1;

      // if they enter a non-letter
      } else if ((int) userGuess.charAt(0) < 65 || (int) userGuess.charAt(0) > 122 || ((int) userGuess.charAt(0) > 90 && (int) userGuess.charAt(0) < 97)) {
        checkCount = 1;
      }

      if (checkCount == 3) {
        System.out.print("Letter to Guess: ");
        userGuess = inUser.readLine();
      } else if (checkCount == 1) {
        System.out.println("Error! Please guess one letter.");
        System.out.print("Letter to Guess: ");
        userGuess = inUser.readLine();
      } else {

        // convert uppercase to lowercase
        if ((int) userGuess.charAt(0) < 97) {
          userGuess = userGuess.toLowerCase();
        }

        // check if guess has already been guessed before
        for (int i = 0; i < numIncorrect + wordLength; i++) {
          if (userGuess.equals(parts[3 + i])) {
            checkCount = 2;
          }
        }

        if (checkCount == 2) {
          System.out.println("Error! Letter \"" + userGuess + "\" has been guessed before, please guess another letter.");
          System.out.print("Letter to Guess: ");
          userGuess = inUser.readLine();
        } else {
          checkInput = false;
        }
      }
    }
    return userGuess;

  }

  public static String getChar_message(String s) {
    int length = s.length();
    //char c = (char)(length + '0');
		char c = (char)length;
    String s_new = c + s;
    return s_new;
  }

  public static char getChar(int i) {
    //char c = (char)(i & 0xFF);
		char c = (char)i;
    return c;
  }

	public static int char_to_int(char c) {
		int i = (int)c;
		return i;
	}
}
