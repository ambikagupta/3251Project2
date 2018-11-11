import java.net.*;
import java.io.*;

//This was Client2

class Client {

	public static void main(String args[]) throws Exception {
		String userGuess;
		String serverMsg = "";

		String serverIP = args[0];
		int serverPort = Integer.parseInt(args[1]);

		InetAddress addr = InetAddress.getByName(serverIP);

		int numIncorrect;
		String incorrectGuesses;
	    int wordLength;
	    int numBlanks;
	    boolean myBool = true;
	    String[] parts;
		byte[] wordPacket;
		byte[] guessPacket;
		byte[] packet;
		int msgFlag;
		String wordInProgress;
		String WIP_spaces;
		String IG_spaces;
		byte[] in_msg_Packet;



    	// socket creation
		Socket s = new Socket(addr, serverPort);
		//BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream())); //in from server
		DataInputStream in = new DataInputStream(s.getInputStream()); //in from server
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
			//packet[0] = (byte) 2;
			out.writeByte(2);
	      //out.writeBytes("2\n"); // send 2 signaling multiplayer
				// char c = getChar(2);
				// String ss = "" + c;
				// out.writeBytes(ss);


	    } else {
			//packet[0] = (byte) 0;
			out.writeByte(0);
	    //   //out.writeBytes("0\n"); // send 0 signaling single player
		// 		char c = getChar(0);
		// 		String ss = "" + c;
		// 		out.writeBytes(ss);
	    }

	    // GAME LOOP GOOD LUCK
	    // while(myBool) {
	    //   serverMsg = in.readLine(); //read packet from server
			//
	    //   // IF GAME PACKET
	    //   if(serverMsg.charAt(0) == '0') {
	    //     parts = serverMsg.split(""); // msg flag, word length, num incorrect, data
	    //     numIncorrect = Integer.parseInt(parts[2]);
	    //     wordLength = Integer.parseInt(parts[1]);
	    //     numBlanks = 0;
			//
	    //     // print out the word to guess
	    //     for (int i = 3; i < 3 + wordLength; i++) {
	    //       System.out.print(parts[i] + " ");
	    //       // count the number of blanks left
	    //       if (parts[i].equals("_")) {
	    //         numBlanks++;
	    //       }
	    //     }
			//
	    //     System.out.println();
			//
	    //     // format and print out incorrect guesses
	    //     incorrectGuesses = "";
	    //     for (int i = 3 + wordLength; i < parts.length; i++) {
	    //       incorrectGuesses = incorrectGuesses + parts[i] + " ";
	    //     }
	    //     System.out.println("Incorrect Guesses: " + incorrectGuesses);
	    //     System.out.println("");
			//
	    //     //Print out letter guess and get user input for letter quess
	    //     if (numBlanks != 0 && numIncorrect < 6) {
	    //       System.out.print("Letter to Guess: ");
	    //       userGuess = inUser.readLine();  // read in user's guess
	    //       userGuess = checkInput(userGuess, inUser, numIncorrect, wordLength, parts); //check the input given by user
	    //       out.writeBytes("1" + userGuess + "\n"); // send user's guess to server
	    //     }
			//
	    //     // if message
	    //   } else {
	    //     System.out.println(serverMsg);
	    //     if (serverMsg.contains("Game Over!") || serverMsg.contains("server-overload")) {
	    //       myBool = false;
	    //       s.close();
	    //     }
	    //   }
	    // }

		while(myBool) {

			msgFlag = (int) in.readByte(); //read packet from server


			// IF GAME PACKET
			if(msgFlag == 0) {
				wordLength = (int) in.readByte();
				numIncorrect = (int) in.readByte();
				numBlanks = 0;

				// print out the word to guess

				wordPacket = new byte[wordLength];

				for (int i = 0; i < wordLength; i++) {
					wordPacket[i] = in.readByte();
				}

				wordInProgress = new String(wordPacket, "US-ASCII");
				WIP_spaces = "";
				// TODO potentially change wordLength to wordInProgress.length()
				for (int i = 0; i < wordLength; i++) {
					WIP_spaces += wordInProgress.charAt(i) + " ";

					if (wordInProgress.charAt(i) == '_') {
  				    	numBlanks++;
					}
				}

				System.out.println(WIP_spaces);
				System.out.println();

				// format and print out incorrect guesses
				guessPacket = new byte[numIncorrect];
				//incorrectGuesses = "";
				for (int i = 0; i < numIncorrect; i++) {
					guessPacket[i] = in.readByte();
				}

				incorrectGuesses = new String(guessPacket, "US-ASCII");
				IG_spaces = "";
				for (int i = 0; i < numIncorrect; i++) {
					IG_spaces += incorrectGuesses.charAt(i) + " ";
				}

				System.out.println("Incorrect Guesses: " + IG_spaces);
				System.out.println();

				//Print out letter guess and get user input for letter quess
				if (numBlanks != 0 && numIncorrect < 6) {
					System.out.print("Letter to Guess: ");
					userGuess = inUser.readLine();  // read in user's guess
				  	userGuess = checkInput(userGuess, inUser, numIncorrect, wordLength, incorrectGuesses, wordInProgress); //check the input given by user
					packet = message_to_bytes(userGuess);
					out.write(packet, 0, packet.length); // send user's guess to server
				}

			// if message
			} else {
				in_msg_Packet = new byte[msgFlag];
				for(int i = 0; i < msgFlag; i++) {
					in_msg_Packet[i] = in.readByte();
				}
				serverMsg = new String(in_msg_Packet, "US-ASCII");
				System.out.println(serverMsg);
				if (serverMsg.contains("Game Over!") || serverMsg.contains("server-overload")) {
					myBool = false;
			  		//s.close();
				}
			}
	    }
	    s.close();
	}

	public static String checkInput(String userGuess, BufferedReader inUser, int numIncorrect, int wordLength, String incorrectGuesses, String wordInProgress) throws Exception{

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
			if (incorrectGuesses.contains(userGuess) || wordInProgress.contains(userGuess)) {
				checkCount = 2;
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

  // public static char getChar(int i) {
  //   //char c = (char)(i & 0xFF);
  // char c = (char)i;
  //   return c;
  // }
  //
  // ublic static int char_to_int(char c) {
  // int i = (int)c;
  // return i;
  //

	public static byte[] message_to_bytes(String message) throws UnsupportedEncodingException {
		// convert everything to bytes
		int msgFlag = message.length();
		byte msgFlag_to_bytes = (byte) msgFlag;
		byte[] message_to_bytes = message.getBytes("UTF-8");

		// create game packet with length of bytes
		byte[] packet = new byte[1 + message_to_bytes.length];

		packet[0] = msgFlag_to_bytes;
		// copy all game packet bytes into byte array
		System.arraycopy(message_to_bytes, 0 , packet, 1, message_to_bytes.length);
		//System.arraycopy(message_to_bytes, 0, packet, msgFlag_to_bytes.length, message_to_bytes.length);

		return packet;
	}
}
