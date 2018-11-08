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
		BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream())); //in from server
		if(in.ready() == true) {
			String over = in.readLine();
			System.out.println("");
			String [] parts = over.split("");
			String display = "";
			for(int i = 1; i < parts.length; i++) {
				display = display + parts[i];
			}
			System.out.println(display);
			System.out.println("");
			s.close();
		} else {
			System.out.println("Two Player? (y/n)");
			BufferedReader inUser = new BufferedReader(new InputStreamReader(System.in)); //user InputStreamReader
			String yn = inUser.readLine();    // ready to start game

			boolean checkYN = true;

			while (checkYN) {
				if (!(yn.equals("y") || yn.equals("n") || yn.equals("Y") || yn.equals("N"))) {
					System.out.println("Two Player? (y/n)");
					inUser = new BufferedReader(new InputStreamReader(System.in)); //user InputStreamReader
					yn = inUser.readLine();    // ready to start game
				} else {
					checkYN = false;
				}
			}


			// if user enters "yes" -> multiplayer game
			if(yn.equals("y") || yn.equals("Y")) {
			  	System.out.println("User selected yes");
				DataOutputStream out = new DataOutputStream(s.getOutputStream()); // message to send to server

				System.out.println("Send 2 to server.");
				out.writeBytes("2\n"); // send 2 signaling multiplayer


				// get "waiting on other player message"
				serverMsg = in.readLine(); // read in message from server
				System.out.println(serverMsg.substring(2));

				// get "game starting message"
				serverMsg = in.readLine();


				// get first turn message
				serverMsg = in.readLine();
				System.out.println(serverMsg.substring(2));

				serverMsg = in.readLine();



				// if not game packet, wait until "Your Turn!" for player 2
				if (serverMsg.charAt(0) != '0') {
					System.out.println("I should only be here if I'm player 2!!");
					System.out.println(serverMsg.substring(2));
					serverMsg = in.readLine();
				}

				String[] parts = serverMsg.split("");  // msg flag, word length, num incorrect, data

				int wordLength = Integer.parseInt(parts[1]);
				String wordInProg = "";
				for (int i = 3; i < 3 + wordLength; i++) {
					wordInProg += parts[i] + " ";
				}

				numIncorrect = Integer.parseInt(parts[2]);


				System.out.println(wordInProg);

				// format and print out incorrect guesses
				incorrectGuesses = "";
				for (int i = 3 + wordLength; i < parts.length; i++) {
					incorrectGuesses += parts[i] + " ";
				}
				System.out.println("Incorrect Guesses: " + incorrectGuesses);


				boolean myBool = true;



			  	// start 2 player game!
				while (myBool) {


					boolean checkInput = true; // check if user's input is valid

					System.out.print("Letter to Guess: ");
					userGuess = inUser.readLine();  // read in user's guess
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

					// System.out.print("\n");
					out.writeBytes("1" + userGuess + "\n"); // send user's guess to server



					// TODO CHECK THIS
					serverMsg = in.readLine();
					System.out.println(serverMsg.substring(1));		// CORRECT OR INCORRECT


					// if (numIncorrect == 6 || numBlanks == 0) {

					// 	// get you win or you lose message
					// 	serverMsg = "";
					// 	serverMsg = in.readLine();
					// 	parts = serverMsg.split("");
					// 	int len = Integer.parseInt(parts[0]);
					// 	for (int i = 1; i <= len; i++) {
					// 		System.out.print(parts[i]);
					// 	}

					// 	System.out.println();


					// 	// get game over message
					// 	serverMsg = "";
					// 	serverMsg = in.readLine();
					// 	parts = serverMsg.split("");
					// 	len = Integer.parseInt(parts[0]);
					// 	for (int i = 1; i <= len; i++) {
					// 		System.out.print(parts[i]);
					// 	}

					// 	myBool = false;

					// 	s.close();

					// } else {
					// 	// for formatting lololol
					// 	System.out.println();
					// }


					serverMsg = in.readLine();
					System.out.println(serverMsg.substring(2));		// WAITING ON P2 OR YOUR TURN


					// WAITING ON P2
					serverMsg = in.readLine();


					if (serverMsg.charAt(0) != '0') {
						System.out.println(serverMsg.substring(2));
						serverMsg = in.readLine();
					}


					// receive server's response
					//serverMsg = "";
					//serverMsg = in.readLine(); // read in server's message
					parts = serverMsg.split(""); // msg flag, word length, num incorrect, data


					numIncorrect = Integer.parseInt(parts[2]);
					int numBlanks = 0;


					// print out the word to guess
					for (int i = 3; i < 3 + wordLength; i++) {
						System.out.print(parts[i] + " ");

						// count the number of blanks left
						if (parts[i].equals("_")) {
							numBlanks++;
						}
					}

					System.out.println();

					if (numIncorrect == 6 || numBlanks == 0) {

						// get you win or you lose message
						serverMsg = "";
						serverMsg = in.readLine();
						parts = serverMsg.split("");
						int len = Integer.parseInt(parts[0]);
						for (int i = 1; i <= len; i++) {
							System.out.print(parts[i]);
						}

						System.out.println();


						// get game over message
						serverMsg = "";
						serverMsg = in.readLine();
						parts = serverMsg.split("");
						len = Integer.parseInt(parts[0]);
						for (int i = 1; i <= len; i++) {
							System.out.print(parts[i]);
						}

						myBool = false;

						s.close();

					} else {
						// for formatting lololol
						System.out.println();
					}


					if (myBool) {
						incorrectGuesses = "";
						for (int i = 3 + wordLength; i < parts.length; i++) {
							incorrectGuesses = incorrectGuesses + parts[i] + " ";
						}
						System.out.println("Incorrect Guesses: " + incorrectGuesses);
					}
					// format and print out incorrect guesses

					// if numIncorrect == 6 or solved word, lose game
					// if (numIncorrect == 6 || numBlanks == 0) {

					// 	// get you win or you lose message
					// 	serverMsg = "";
					// 	serverMsg = in.readLine();
					// 	parts = serverMsg.split("");
					// 	int len = Integer.parseInt(parts[0]);
					// 	for (int i = 1; i <= len; i++) {
					// 		System.out.print(parts[i]);
					// 	}

					// 	System.out.println();


					// 	// get game over message
					// 	serverMsg = "";
					// 	serverMsg = in.readLine();
					// 	parts = serverMsg.split("");
					// 	len = Integer.parseInt(parts[0]);
					// 	for (int i = 1; i <= len; i++) {
					// 		System.out.print(parts[i]);
					// 	}

					// 	myBool = false;

					// 	s.close();

					// } else {
					// 	// for formatting lololol
					// 	System.out.println();
					// }
				}



//////////////////////////////////-------------MULTI PLAYER ABOVE----------///////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////-------------SINGLE PLAYER BELOW----------///////////////////////////////////////////////////



			// if user enters "no" -> single player game
			} else {

				System.out.println("User selected no");
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

					// System.out.print("\n");
					out.writeBytes("1" + userGuess + "\n"); // send user's guess to server



					// receive server's response
					serverMsg = "";
					serverMsg = in.readLine(); // read in server's message
					parts = serverMsg.split(""); // msg flag, word length, num incorrect, data


					numIncorrect = Integer.parseInt(parts[2]);
					int numBlanks = 0;


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

					// if numIncorrect == 6 or solved word, lose game
					if (numIncorrect == 6 || numBlanks == 0) {

						// get you win or you lose message
						serverMsg = "";
						serverMsg = in.readLine();
						parts = serverMsg.split("");
						int len = Integer.parseInt(parts[0]);
						for (int i = 1; i <= len; i++) {
							System.out.print(parts[i]);
						}

						System.out.println();


						// get game over message
						serverMsg = "";
						serverMsg = in.readLine();
						parts = serverMsg.split("");
						len = Integer.parseInt(parts[0]);
						for (int i = 1; i <= len; i++) {
							System.out.print(parts[i]);
						}

						myBool = false;

						s.close();

					} else {
						// for formatting lololol
						System.out.println();
					}
				}
			}
		}
	}


	// public String validateGuess(String userGuess) {
	// 	boolean checkInput = true; // check if user's input is valid

	// 	// System.out.print("Letter to Guess: ");
	// 	// userGuess = inUser.readLine();  // read in user's guess
	// 	int checkCount;

	// 	while (checkInput) {

	// 		checkCount = 0; // which case to go to


	// 		// if user presses enter without a character there
	// 		if (userGuess.length() == 0) {
	// 			checkCount = 3;

	// 		// if user enters multiple characters
	// 		} else if (userGuess.length() > 1) {
	// 			checkCount = 1;

	// 		// if they enter a non-letter
	// 		} else if ((int) userGuess.charAt(0) < 65 || (int) userGuess.charAt(0) > 122 || ((int) userGuess.charAt(0) > 90 && (int) userGuess.charAt(0) < 97)) {
	// 			checkCount = 1;
	// 		}



	// 		if (checkCount == 3) {
	// 			System.out.print("Letter to Guess: ");
	// 			userGuess = inUser.readLine();
	// 		} else if (checkCount == 1) {
	// 			System.out.println("Error! Please guess one letter.");
	// 			System.out.print("Letter to Guess: ");
	// 			userGuess = inUser.readLine();
	// 		} else {

	// 			// convert uppercase to lowercase
	// 			if ((int) userGuess.charAt(0) < 97) {
	// 				userGuess = userGuess.toLowerCase();
	// 			}

	// 			// check if guess has already been guessed before
	// 			for (int i = 0; i < numIncorrect + wordLength; i++) {
	// 				if (userGuess.equals(parts[3 + i])) {
	// 					checkCount = 2;
	// 				}
	// 			}

	// 			if (checkCount == 2) {
	// 				System.out.println("Error! Letter \"" + userGuess + "\" has been guessed before, please guess another letter.");
	// 				System.out.print("Letter to Guess: ");
	// 				userGuess = inUser.readLine();
	// 			} else {
	// 				checkInput = false;
	// 				return userGuess;
	// 			}
	// 		}
	// 	}
	// }
}
