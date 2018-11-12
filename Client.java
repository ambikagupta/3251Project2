import java.net.*;
import java.io.*;


class Client {

	public static void main(String args[]) throws Exception {
		String userGuess; // user's guess for current turn
		String serverMsg = ""; // server message formatted for printing

		String serverIP = args[0];
		int serverPort = Integer.parseInt(args[1]);

		InetAddress addr = InetAddress.getByName(serverIP);

		int numIncorrect; // number of incorrectGuesses
		String incorrectGuesses; // incorrect guesses
	    int wordLength; // length of word
	    int numBlanks; // number of dashes in word
	    boolean myBool = true; // true while game is still running
		byte[] wordPacket; // byte array of word in progress
		byte[] guessPacket; // byte array of incorrect guesses
		byte[] packet; // byte array of guess to send to server
		int msgFlag; // message flag of server's message
		String wordInProgress; // word in progress
		String WIP_spaces; // word in progress wformatted for printing
		String IG_spaces; // incorrect guesses formatted for printing
		byte[] in_msg_Packet; // byte array of message from server



    	// socket creation
		Socket s = new Socket(addr, serverPort);
		DataInputStream in = new DataInputStream(s.getInputStream()); // input from server
	    DataOutputStream out = new DataOutputStream(s.getOutputStream()); // output to server
	    BufferedReader inUser = new BufferedReader(new InputStreamReader(System.in)); // input from user

    	System.out.println("Two Player? (y/n)");
    	String yn = inUser.readLine();    // Answer to Two Player question.

    	boolean checkYN = true; // user input validatioin

    	// validating user input from Two Player question (y or n)
	    while (checkYN) {
	      if (!(yn.equals("y") || yn.equals("n") || yn.equals("Y") || yn.equals("N"))) {
	        System.out.println("Two Player? (y/n)");
	        yn = inUser.readLine();    // Answer to Two Player question
	      } else {
	        checkYN = false;
	      }
	    }

	    // send multiplayer or single player
		if(yn.equals("y") || yn.equals("Y")) {
			out.writeByte(2);
	    } else {
			out.writeByte(0);
	    }


		/////////////////////////////////////////////////////////////// GAME LOOP /////////////////////////////////////////////////////////////////////////////////////////////


		while(myBool) {

			try{
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
					}
				}
			} catch (EOFException e) {
				s.close();
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


// convert message to byte array
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

		return packet;
	}
}
