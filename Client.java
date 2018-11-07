import java.net.*;
import java.io.*;

class Client {

	public static void main(String args[]) throws Exception {
		String userGuess;
		String serverMsg;

		String serverIP = args[0];
		int serverPort = Integer.parseInt(args[1]);

		InetAddress addr = InetAddress.getByName(serverIP);

		int numIncorrect;
		String incorrectGuesses;




		Socket s = new Socket(addr, serverPort);
		System.out.println("Ready to start game? (y/n)");
		BufferedReader inUser = new BufferedReader(new InputStreamReader(System.in)); //user InputStreamReader
		BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream())); //in from server
		String yn = inUser.readLine();    // ready to start game
		
		if(yn.equals("n") || yn.equals("N") || yn.equals("no") || yn.equals("No") || yn.equals("NO")) {
		  	// something needs to be sent to server??
		  	s.close();
		} else {
		  
			System.out.println("User selected yes");
			DataOutputStream out = new DataOutputStream(s.getOutputStream()); // message to send to server
			  
			System.out.println("Send empty message to server.");
			out.writeBytes("0\n"); // send 0 signaling to start the game


			// game setup
			serverMsg = in.readLine(); // read in message from server
			System.out.println("Reading message from server: " + serverMsg);
			String[] parts = serverMsg.split("");  // msg flag, word length, num incorrect, data

			int wordLength = Integer.parseInt(parts[1]);
			String wordInProg = "";
			for (int i = 3; i < 3 + wordLength; i++) {
				wordInProg += parts[i] + " ";
			}

			numIncorrect = 0;

			
			System.out.println(wordInProg);

			System.out.println("Incorrect Guesses: \n");
		  
		  
			boolean myBool = true;


		  	// start actual game
			while (myBool) {

				
				boolean checkInput = true; // check if user's input is valid
				
				System.out.print("Letter to Guess: ");
				userGuess = inUser.readLine();  // read in user's guess
				int checkCount;

				while (checkInput) {
					checkCount = 0;
					

					// if they enter more than one letter
					if (userGuess.length() > 1) {
						checkCount = 1;

					// if they enter a non-letter
					} else if ((int) userGuess.charAt(0) < 65 || (int) userGuess.charAt(0) > 122 || ((int) userGuess.charAt(0) > 90 && (int) userGuess.charAt(0) < 97)) {
						checkCount = 1;
					}

					if (checkCount == 1) {
						System.out.println("Error! Please guess one letter.");
						System.out.print("Letter to Guess: ");
						userGuess = inUser.readLine();
					} else {
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

				System.out.print("\n");
				out.writeBytes("1" + userGuess + "\n"); // send user's guess to server



				// receive server's response
				serverMsg = "";
				serverMsg = in.readLine(); // read in server's message
				parts = serverMsg.split(""); // msg flag, word length, num incorrect, data
				


				// if you won or if you lost
				// TODO - formatting for winning message - has to include the word
				if (!(parts[0].equals("0"))) {
					for (int i = 1; i < parts.length; i++) {
						System.out.print(parts[i]);
					}
					System.out.println("at end");
					myBool = false;

					// TODO how to end gracefully?
					s.close();


				// if we're still playing the game
				} else {
				  	
				  	numIncorrect = Integer.parseInt(parts[2]);
				  	

				  	// print out the word to guess
				  	for (int i = 3; i < 3 + wordLength; i++) {
				  		System.out.print(parts[i] + " ");
				  	}
				  	System.out.println(""); // line space
				  	

				  	// format and print out incorrect guesses
				  	incorrectGuesses = "";
				  	for (int i = 3 + wordLength; i < parts.length; i++) {
				  		incorrectGuesses = incorrectGuesses + parts[i] + " ";
				  	}
				  	System.out.println("Incorrect Guessses: " + incorrectGuesses + "\n");
				}
			}
		}
	}
}
