import java.io.*;
import java.text.*;
import java.util.*;
import java.net.*;

// Server class
public class Server {
	public static void main(String[] args) throws IOException {
		//list which will hold the threads that have games running on them.
		ArrayList<Thread> conns = new ArrayList<>();
		//list of players in the queue for a multiplayer game
		ArrayList<MultiPlayerConn> multi_conns_queue = new ArrayList<>();
		//parse the port from the arguments passed in
		int port = Integer.parseInt(args[0]);
		byte[] packet;
		// server is listening on port given by user
		ServerSocket ss = new ServerSocket(port);

		// running infinite loop for getting
		// client request
		while (true) {
			Socket s = null;

			try {
				// socket object to receive incoming client requests
				s = ss.accept();

				//iterate through the list of threads and remove any that are not active
				//remove any thread which is not currently running a game
				for (Iterator<Thread> citerator = conns.iterator(); citerator.hasNext();) {
					Thread t = citerator.next();
					if(t.isAlive() == false) {
						citerator.remove();
					}
				}

				System.out.println("A new client is connected : " + s);

				// obtaining input and out streams
				DataInputStream dis = new DataInputStream(s.getInputStream());
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				//read input for if user wants two player or not
				int input = (int) dis.readByte();
				//if received a two user wants multi player otherwise single player
				if(input == 2) {
					System.out.println("Multiplayer Selected");
					//if there is no player / client waiting in the queue,
					//add this client to the queue
					if(multi_conns_queue.size() == 0) {
						System.out.println("There are no clients waiting for multi");
						//check to see if three games or threads are running,
						//if so send server overload message and close client socket.
						if(conns.size() == 3) {
							//dos.writeBytes("10server-overloaded" + "\n");
							packet = message_to_bytes("server-overload");
							dos.write(packet, 0, packet.length);
							s.close();
							//dos.writeBytes("" + getChar(15) + "server-overload");
						} else {
							//add multiplayer client to the queue
							MultiPlayerConn m = new MultiPlayerConn(s, dis, dos);
							multi_conns_queue.add(m);
							System.out.println("Add multi client to queue");
							//dos.writeBytes("25Waiting for other player!" + "\n");
							//dos.writeBytes("" + getChar(25) + "Waiting for other player!");
							packet = message_to_bytes("Waiting for other player!");
							dos.write(packet, 0, packet.length);
						}
					} else {
						//Multiplayer player waiting in queue for mulitplayer game
						System.out.println("There is a player waiting for multi");
						MultiPlayerConn m = multi_conns_queue.get(0);
						//check if server-overload occurs, if so close client socket,
						//and socket of player waiting in queue.
						if(conns.size() == 3) {
							//m.out.writeBytes("10server-overloaded" + "\n");
							//send server overload message to client waiting in queue
							//close socket
							packet = message_to_bytes("server-overload");
							m.out.write(packet, 0, packet.length);
							m.s.close();

							//m.out.writeBytes("" + getChar(15) + "server-overload");
							multi_conns_queue.remove(m);
							// dos.writeBytes("10server-overloaded" + "\n");
							//send server overload message and close socket
							packet = message_to_bytes("server-overload");
							dos.write(packet, 0, packet.length);
							s.close();
							//dos.writeBytes("" + getChar(15) + "server-overload");
						} else {
							//create and start new thread for the multiplayer game
							System.out.println("Assigning new multi thread for this client");
							multi_conns_queue.remove(m);

							// create a new thread object
							Thread t = new MultiClientHandler(m.s, m.in, m.out, s, dis, dos);
							conns.add(t);

							// Invoking the start() method
							t.start();
						}
					}
				 } else {
					 //single player thread setup
					 //check for server overload
					if(conns.size() == 3) {
						//dos.writeBytes("10server-overloaded" + "\n");
						//close socket and send message
						packet = message_to_bytes("server-overload");
						dos.write(packet, 0, packet.length);
						s.close();
						//dos.writeBytes("" + getChar(15) + "server-overload");
					} else {
						//create and start new thread for the game
						System.out.println("Assigning new thread for this client");

						// create a new thread object
						Thread t = new ClientHandler(s, dis, dos);
						conns.add(t);

						// Invoking the start() method
						t.start();
					}
				 }


			}
			catch (Exception e){
				s.close();
				e.printStackTrace();
			}
		}
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

	// convert message to bytes
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

//class to hold socket and input, output stream for client waiting for mulitplayer game
class MultiPlayerConn {
	DataInputStream in;
	DataOutputStream out;
	Socket s;

	public MultiPlayerConn(Socket s, DataInputStream in, DataOutputStream out) {
		this.s = s;
		this.in = in;
		this.out = out;
	}
}

// ClientHandler class
class ClientHandler extends Thread {
	final DataInputStream in;
	final DataOutputStream out;
	final Socket s;
	//final BufferedReader in;


	// Constructor
	public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos) throws IOException{
		this.s = s;
		this.in = dis;
		this.out = dos;
		//this.in = new BufferedReader(new InputStreamReader(s.getInputStream()));
	}

	@Override
	public void run() {
		String message; // server's message
		String clientMsg; // client's message
		String[] parts; // will hold client's message
		String guess = ""; // will hold client's guess
		String word = ""; // holder for assigned word
		String data = ""; // word in progress and incorrect guesses
		int numIncorrect = 0; // number of incorrect guesses
		String wordInProgress = ""; // blanks with correct letters user has guessed
		String incorrectGuesses = ""; // actual incorrect guesses
		Game g1 = null; // game object
		int numHits = 0; // number of hits with guess
		byte msgFlag = 0; // message flag in bytes
		byte[] guessArray;

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
		byte[] packet;


		try {


			//////////////////////////////////////////// SET UP FOR GAME /////////////////////////////////////////////////////////////


			// select random word
			int num = rand.nextInt(15);
			word = words[num];
			System.out.println("Selected Word: " + word);


			// create word in progress
			for(int i = 0; i < word.length(); i++) {
			  wordInProgress += "_";
			}


			// packet formatted as msg flag = 0, length = word.length, incorrectguesses = 0, data = _____
			g1 = new Game(word, s);

			// char flag = getChar(0);
			// char incorrectGuess = getChar(g1.getNumIncorrect());
			// char length = getChar(g1.getLength());
			data = g1.getWordInProgress() + g1.getIncorrectGuesses();
			packet = gamePacket_to_bytes(0, g1.getLength(), g1.getNumIncorrect(), data);
			out.write(packet, 0, packet.length);
			//out.writeBytes("0" + g1.getLength() + g1.getNumIncorrect() + data + "\n");
			System.out.println("Word in progress : " + g1.getWordInProgress());



			//////////////////////////////////////////// GAME LOOP /////////////////////////////////////////////////////////////


			// while player hasn't guessed word and doesn't have 6 incorrect guesses
			// while(g1.getGameOver() == false) {
			//
			//
			// 	// get client message and guess
			// 	clientMsg = in.readLine();
			// 	parts = clientMsg.split("");
			// 	guess = parts[1];
			//
			//
			// 	// check if client guessed correctly and update word in progress
			// 	numHits = 0; // number of hits with guess
			// 	for (int i = 0; i < g1.getLength(); i++) {
			// 		if (g1.getWord().indexOf(guess, i) == i) {
			// 			numHits++;
			// 			g1.setWordInProgress(g1.getWordInProgress().substring(0, i) + guess + g1.getWordInProgress().substring(i + 1));
			// 		}
			// 	}
			//
			// 	// if client guessed incorrectly, update number incorrect and incorrect guesses
			// 	if (numHits == 0) {
      //     			g1.setNumIncorrect(g1.getNumIncorrect() + 1);
      //     			g1.setIncorrectGuesses(g1.getIncorrectGuesses() + guess);
			// 	}
			//
			//
			// 	// send game packet to client with updated values
			// 	data = g1.getWordInProgress() + g1.getIncorrectGuesses();
			// 	out.writeBytes("0" + g1.getLength() + g1.getNumIncorrect() + data + "\n");
			//
			//
			// 	// if the client has guessed the word, send end game packets and ready for game over
			// 	if (!(g1.getWordInProgress().contains("_"))) {
			// 		g1.setGameOver(true);
			// 		out.writeBytes("You Win!\n");
			// 		out.writeBytes("Game Over!\n");
			//
			// 	// if the client has guessed incorrectly 6 time, send end game messages and ready for game over
			// 	} else if (g1.getNumIncorrect() >= 6) {
			// 		g1.setGameOver(true);
			// 		out.writeBytes("You Lose :(\n");
			// 		out.writeBytes("Game Over!\n");
			// 	}
			//
			// }
			while(g1.getGameOver() == false) {


				// get client message and guess

				msgFlag = in.readByte();

				guessArray = new byte[1];
				guessArray[0] = in.readByte();
				guess = new String(guessArray, "US-ASCII");


				// check if client guessed correctly and update word in progress
				numHits = 0; // number of hits with guess
				for (int i = 0; i < g1.getLength(); i++) {
					if (g1.getWord().indexOf(guess, i) == i) {
						numHits++;
						g1.setWordInProgress(g1.getWordInProgress().substring(0, i) + guess + g1.getWordInProgress().substring(i + 1));
					}
				}

				// if client guessed incorrectly, update number incorrect and incorrect guesses
				if (numHits == 0) {
          			g1.setNumIncorrect(g1.getNumIncorrect() + 1);
          			g1.setIncorrectGuesses(g1.getIncorrectGuesses() + guess);
				}


				// send game packet to client with updated values
				data = g1.getWordInProgress() + g1.getIncorrectGuesses();
				packet = gamePacket_to_bytes(0, g1.getLength(), g1.getNumIncorrect(), data);
				out.write(packet, 0, packet.length);


				// if the client has guessed the word, send end game packets and ready for game over
				if (!(g1.getWordInProgress().contains("_"))) {
					g1.setGameOver(true);
					packet = message_to_bytes("You Win!");
					out.write(packet, 0, packet.length);

					packet = message_to_bytes("Game Over!");
					out.write(packet, 0, packet.length);

					s.close();
					//out.writeBytes("" + getChar(8) + "You Win!");
					//out.writeBytes("" + getChar(10) + "Game Over!");

				// if the client has guessed incorrectly 6 time, send end game messages and ready for game over
				} else if (g1.getNumIncorrect() >= 6) {
					g1.setGameOver(true);

					packet = message_to_bytes("You Lose :(");
					out.write(packet, 0, packet.length);

					packet = message_to_bytes("Game Over!");
					out.write(packet, 0, packet.length);

					s.close();

					//out.writeBytes("" + getChar(11) + "You Lose :(");
					//out.writeBytes("" + getChar(10) + "Game Over!";
				}


			}
		} catch (IOException e) {
				e.printStackTrace();
			}

		try
		{
			// closing resources
			this.in.close();
			this.out.close();

		}catch(IOException e){
			e.printStackTrace();
		}
	}

	/// convert game packet to bytes
	public static byte[] gamePacket_to_bytes(int msgFlag, int wordLength, int numIncorrect, String data) throws UnsupportedEncodingException {
		// convert everything to bytes
		byte msgFlag_to_bytes = (byte) msgFlag;
		byte wordLength_to_bytes = (byte) wordLength;
		byte numIncorrect_to_bytes = (byte) numIncorrect;
		byte[] data_to_bytes = data.getBytes("UTF-8");

		// create game packet with length of bytes
		byte[] packet = new byte[3 + data_to_bytes.length];
		packet[0] = msgFlag_to_bytes;
		packet[1] = wordLength_to_bytes;
		packet[2] = numIncorrect_to_bytes;

		// copy all game packet bytes into byte array
		System.arraycopy(data_to_bytes, 0 , packet, 3, data_to_bytes.length);

		return packet;
	}

	// convert message to bytes
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

class MultiClientHandler extends Thread {
	final DataInputStream in1;
	final DataOutputStream out1;
	final Socket s1;
	final Socket s2;
	final DataInputStream in2;
	final DataOutputStream out2;
	//final BufferedReader in1;
	//final BufferedReader in2;


	// Constructor
	public MultiClientHandler(Socket s1, DataInputStream dis1, DataOutputStream dos1, Socket s2, DataInputStream dis2, DataOutputStream dos2) throws IOException{
		this.s1 = s1;
		this.in1 = dis1;
		this.out1 = dos1;
		//this.in1 = new BufferedReader(new InputStreamReader(s1.getInputStream()));
		this.s2 = s2;
		this.in2 = dis2;
		this.out2 = dos2;
		//this.in2 = new BufferedReader(new InputStreamReader(s2.getInputStream()));
	}

	@Override
	public void run() {
		String message; // server's message
		String clientMsg; // client's message
		String[] parts; // will hold client's message
		String guess; // will hold client's guess
		String word = ""; // holder for assigned word
		String data = ""; // word in progress and incorrect guesses
		int numIncorrect = 0; // number of incorrect guesses
		String wordInProgress = ""; // blanks with correct letters user has guessed
		String incorrectGuesses = ""; // actual incorrect guesses
		Game g1 = null; // game object
		int numHits = 0; // number of hits with guess
		int msg = 0; //int from read will be stored in this variable
		byte[] packet;
		byte msgFlag = 0; // message flag in bytes
		byte[] guessArray;


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


		try {


			// send starting messages to both clients
		  	//out1.writeBytes("Game Starting!\n");
		  	//out2.writeBytes("Game Starting!\n");

			packet = message_to_bytes("Game Starting!");
			out1.write(packet, 0, packet.length);
			out2.write(packet, 0, packet.length);
				// out1.writeBytes("" + getChar(14) + "Game Starting!");
				// out2.writeBytes("" + getChar(14) + "Game Starting!");



		  	//////////////////////////////////////////// SET UP FOR GAME /////////////////////////////////////////////////////////////


			// select random word
			int num = rand.nextInt(15);
			word = words[num];
			System.out.println("Selected Word: " + word);


			// create word in progress
			for(int i = 0; i < word.length(); i++) {
			  wordInProgress += "_";
			}


			// create new game
			g1 = new Game(word, s1);
			System.out.println("Word in progress : " + g1.getWordInProgress());




			//////////////////////////////////////////// GAME LOOP /////////////////////////////////////////////////////////////


			while(g1.getGameOver() == false) {


				///////////////////////////////////////// player 1's turn /////////////////////////////////////////////////

				//send message to Player 2
				//out2.writeBytes("Waiting on Player 1\n");

				packet = message_to_bytes("Waiting on Player 1");
				out2.write(packet, 0, packet.length);
				//out2.writeBytes("" + getChar(19) + "Waiting on Player 1");


				// send game packet to Player 1
				//out1.writeBytes("Your Turn!\n");
				packet = message_to_bytes("Your Turn!");
				out1.write(packet, 0, packet.length);
				//out1.writeBytes("" + getChar(10) + "Your Turn!");
				data = g1.getWordInProgress() + g1.getIncorrectGuesses();

				packet = gamePacket_to_bytes(0, g1.getLength(), g1.getNumIncorrect(), data);
				out1.write(packet, 0, packet.length);
				//out1.writeBytes("" + getChar(0) + getChar(g1.getLength()) + getChar(g1.getNumIncorrect()) + data);
				//out1.writeBytes("0" + g1.getLength() + g1.getNumIncorrect() + data + "\n");


				// get P1 message and guess
				// clientMsg = in1.readLine();
				// parts = clientMsg.split("");
				// guess = parts[1];

				msgFlag = in1.readByte();

				guessArray = new byte[1];
				guessArray[0] = in1.readByte();
				guess = new String(guessArray, "US-ASCII");


				// msg = in1.read();
				// guess = "" + getChar(in1.read());


				// check if P1 guessed correctly and update word in progress
				numHits = 0;
				//boolean incorrect = false;
				for (int i = 0; i < g1.getLength(); i++) {
					if (g1.getWord().indexOf(guess, i) == i) {
						numHits++;
						g1.setWordInProgress(g1.getWordInProgress().substring(0, i) + guess + g1.getWordInProgress().substring(i + 1));
					}
				}


				// if P1 guessed incorrectly, update number incorrect and incorrect guesses and send incorrect message to P1
				// otherwise, send correct message to P1
				if (numHits == 0) {
          			g1.setNumIncorrect(g1.getNumIncorrect() + 1);
          			g1.setIncorrectGuesses(g1.getIncorrectGuesses() + guess);
					packet = message_to_bytes("Incorrect!");
					//out2.write(packet, 0, packet.length);
					//out1.writeBytes("" + getChar(10) + "Incorrect!");
          			//out1.writeBytes("Incorrect!\n");
				} else {
					packet = message_to_bytes("Correct!");
					//out2.write(packet, 0, packet.length);
					//out1.writeBytes("" + getChar(8) + "Correct!");
					//out1.writeBytes("Correct!\n");
				}
				out1.write(packet, 0, packet.length);


				// if game over
				if (!(g1.getWordInProgress().contains("_")) || g1.getNumIncorrect() >= 6) {
					data = g1.getWordInProgress() + g1.getIncorrectGuesses();

					packet = gamePacket_to_bytes(0, g1.getLength(), g1.getNumIncorrect(), data);
					out1.write(packet, 0, packet.length);
					out2.write(packet, 0, packet.length);

					if (g1.getNumIncorrect() < 6) {
						packet = message_to_bytes("You Win!");
					} else {
						packet = message_to_bytes("You Lose!");
					}
					out1.write(packet, 0, packet.length);
					out2.write(packet, 0, packet.length);

					packet = message_to_bytes("Game Over!");
					out1.write(packet, 0, packet.length);
					s1.close();
					out2.write(packet, 0, packet.length);
					s2.close();

					g1.setGameOver(true);

				}



				///////////////////////////////////////// player 2's turn /////////////////////////////////////////////////

				if(g1.getGameOver() == false) {
					// send message to Player 1
					//out1.writeBytes("Waiting on Player 2\n");
					//out1.writeBytes("" + getChar(19) + "Waiting on Player 2");
					packet = message_to_bytes("Waiting on Player 2");
					out1.write(packet, 0, packet.length);


					// send game packet to P2
					//out2.writeBytes("Your Turn!\n");
					//out2.writeBytes("" + getChar(10) + "Your Turn!");
					packet = message_to_bytes("Your Turn!");
					out2.write(packet, 0, packet.length);

					data = g1.getWordInProgress() + g1.getIncorrectGuesses();

					packet = gamePacket_to_bytes(0, g1.getLength(), g1.getNumIncorrect(), data);
					out2.write(packet, 0, packet.length);
					// //out2.writeBytes("0" + g1.getLength() + g1.getNumIncorrect() + data + "\n");
					// out2.writeBytes("" + getChar(0) + getChar(g1.getLength()) + getChar(g1.getNumIncorrect()) + data);


					// get P2 message and guess
					// clientMsg = in2.readLine();
					// parts = clientMsg.split("");
					// guess = parts[1];
					// msg = in2.read();
					// guess = "" + getChar(in2.read());

					msgFlag = in2.readByte();

					guessArray = new byte[1];
					guessArray[0] = in2.readByte();
					guess = new String(guessArray, "US-ASCII");

					// check if P2 guessed correctly and update word in progress
					numHits = 0;
					for (int i = 0; i < g1.getLength(); i++) {
						if (g1.getWord().indexOf(guess, i) == i) {
							numHits++;
							g1.setWordInProgress(g1.getWordInProgress().substring(0, i) + guess + g1.getWordInProgress().substring(i + 1));
						}
					}


					// if P2 guessed incorrectly, update number incorrect and incorrect guesses and send incorrect message to P2
					// otherwise, send correct message to P2
					if (numHits == 0) {
	          			g1.setNumIncorrect(g1.getNumIncorrect() + 1);
	          			g1.setIncorrectGuesses(g1.getIncorrectGuesses() + guess);
	          			//out2.writeBytes("Incorrect!\n");
						packet = message_to_bytes("Incorrect!");
					} else {
						//out2.writeBytes("Correct!\n");
						packet = message_to_bytes("Correct!");
					}
					out2.write(packet, 0, packet.length);


					// if game over
					if (!(g1.getWordInProgress().contains("_")) || g1.getNumIncorrect() >= 6) {
						data = g1.getWordInProgress() + g1.getIncorrectGuesses();

						packet = gamePacket_to_bytes(0, g1.getLength(), g1.getNumIncorrect(), data);
						out1.write(packet, 0, packet.length);
						out2.write(packet, 0, packet.length);

						if (g1.getNumIncorrect() < 6) {
							packet = message_to_bytes("You Win!");
						} else {
							packet = message_to_bytes("You Lose!");
						}
						out1.write(packet, 0, packet.length);
						out2.write(packet, 0, packet.length);

						packet = message_to_bytes("Game Over!");
						out1.write(packet, 0, packet.length);
						s1.close();
						out2.write(packet, 0, packet.length);
						s2.close();

						g1.setGameOver(true);

					}
				}



				// // if P2 has guessed the word, send end game messages and ready for gme over
				// if (!(g1.getWordInProgress().contains("_"))) {
				// 	data = g1.getWordInProgress() + g1.getIncorrectGuesses();
				//
				// 	out2.writeBytes("" + getChar(0) + getChar(g1.getLength()) + getChar(g1.getNumIncorrect()) + data);
				// 	out2.writeBytes("" + getChar(8) + "You Win!");
				// 	out2.writeBytes("" + getChar(10) + "Game Over!");
				//
				// 	// out1.writeBytes("You Win!\n");
				// 	// out1.writeBytes("Game Over!\n");
				// 	out1.writeBytes("" + getChar(0) + getChar(g1.getLength()) + getChar(g1.getNumIncorrect()) + data);
				// 	out1.writeBytes("" + getChar(8) + "You Win!");
				// 	out1.writeBytes("" + getChar(10) + "Game Over!");
				//
				// 	// send last game packet to player 2
				// 	//out1.writeBytes("0" + g1.getLength() + g1.getNumIncorrect() + data + "\n");
				//
				// 	// out2.writeBytes("You Win!\n");
				// 	// out2.writeBytes("Game Over!\n");
				//
				// 	g1.setGameOver(true);
				//
				//
				// // if P2 has lost the game, send end game messages and ready for game over
				// } else if (g1.getNumIncorrect() >= 6) {
				// 	data = g1.getWordInProgress() + g1.getIncorrectGuesses();
				//
				// 	out2.writeBytes("" + getChar(0) + getChar(g1.getLength()) + getChar(g1.getNumIncorrect()) + data);
				// 	out2.writeBytes("" + getChar(11) + "You Lose :(");
				// 	out2.writeBytes("" + getChar(10) + "Game Over!");
				//
				// 	// out1.writeBytes("You Lose!\n");
				// 	// out1.writeBytes("Game Over!\n");
				// 	out1.writeBytes("" + getChar(0) + getChar(g1.getLength()) + getChar(g1.getNumIncorrect()) + data);
				// 	out1.writeBytes("" + getChar(11) + "You Lose :(");
				// 	out1.writeBytes("" + getChar(10) + "Game Over!");
				//
				// 	// send last game packet to player 2
				//
				// 	//out2.writeBytes("You Lose!\n");
				// 	//out2.writeBytes("Game Over!\n");
				// 	g1.setGameOver(true);
				// }
			}
		} catch (IOException e) {
				e.printStackTrace();
			}

		try
		{
			// closing resources
			this.in1.close();
			this.out1.close();
			this.in2.close();
			this.out2.close();

		}catch(IOException e){
			e.printStackTrace();
		}
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

	public static byte[] gamePacket_to_bytes(int msgFlag, int wordLength, int numIncorrect, String data) throws UnsupportedEncodingException {
		// convert everything to bytes
		byte msgFlag_to_bytes = (byte) msgFlag;
		byte wordLength_to_bytes = (byte) wordLength;
		byte numIncorrect_to_bytes = (byte) numIncorrect;
		byte[] data_to_bytes = data.getBytes("UTF-8");

		// create game packet with length of bytes
		byte[] packet = new byte[3 + data_to_bytes.length];
		packet[0] = msgFlag_to_bytes;
		packet[1] = wordLength_to_bytes;
		packet[2] = numIncorrect_to_bytes;

		// copy all game packet bytes into byte array
		System.arraycopy(data_to_bytes, 0 , packet, 3, data_to_bytes.length);

		return packet;
	}

	// convert message to bytes
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
