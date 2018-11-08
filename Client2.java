import java.net.*;
import java.io.*;

class Client2 {

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

    // game limit checking
    if(in.ready() == true) {
			String over = in.readLine();
			System.out.println("");
			parts = over.split("");
			String display = "";
			for(int i = 1; i < parts.length; i++) {
				display = display + parts[i];
			}
			System.out.println(display);
			System.out.println("");
			s.close();
		} else {
			System.out.println("Two Player? (y/n)");
			String yn = inUser.readLine();    // ready to start game

			boolean checkYN = true;

      // validating user input
			while (checkYN) {
				if (!(yn.equals("y") || yn.equals("n") || yn.equals("Y") || yn.equals("N"))) {
					System.out.println("Two Player? (y/n)");
					inUser = new BufferedReader(new InputStreamReader(System.in)); //user InputStreamReader
					yn = inUser.readLine();    // ready to start game
				} else {
					checkYN = false;
				}
			}

      // send multiplayer or single player
      if(yn.equals("y") || yn.equals("Y")) {
				out.writeBytes("2\n"); // send 2 signaling multiplayer
      } else {
        out.writeBytes("0\n");
      }


      // GAME LOOP GOOD LUCK
      while(myBool) {
        serverMsg = in.readLine();

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

          if (numBlanks != 0 && numIncorrect < 6) {
            System.out.print("Letter to Guess: ");
  					userGuess = inUser.readLine();  // read in user's guess
            out.writeBytes("1" + userGuess + "\n"); // send user's guess to server
          }

        // if message
        } else {
          System.out.println(serverMsg);
          if (serverMsg.contains("GAME OVER")) {
            myBool = false;
            s.close();
          }
        }
      }
      s.close();
		}
	}
}
