import java.io.*;
import java.text.*;
import java.util.*;
import java.net.*;

// Server class
public class Server {
	public static void main(String[] args) throws IOException {
		ArrayList<Thread> conns = new ArrayList<>();
		ArrayList<MultiPlayerConn> multi_conns_queue = new ArrayList<>();
		int port = Integer.parseInt(args[0]);
		// server is listening on port given by user
		ServerSocket ss = new ServerSocket(port);

		// running infinite loop for getting
		// client request
		while (true) {
			Socket s = null;

			try {
				// socket object to receive incoming client requests
				s = ss.accept();

				for (Iterator<Thread> citerator = conns.iterator(); citerator.hasNext();) {
					Thread t = citerator.next();
					//System.out.println("Thread: " + t.isAlive());
					if(t.isAlive() == false) {
						citerator.remove();
					}
				}

				System.out.println("A new client is connected : " + s);

				// obtaining input and out streams
				DataInputStream dis = new DataInputStream(s.getInputStream());
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());

				if(conns.size() == 3) {
					dos.writeBytes("server-overloaded" + "\n");
				} else {
					String line = dis.readLine();
					if(line.equals("2")) {
						System.out.println("Multiplayer Selected");
						if(multi_conns_queue.size() == 0) {
							System.out.println("There are no clients waiting for multi");
							MultiPlayerConn m = new MultiPlayerConn(s, dis, dos);
							multi_conns_queue.add(m);
							System.out.println("Add multi client to queue");
							dos.writeBytes("Waiting for other player!" + "\n");
						} else {
							System.out.println("There is a player waiting for multi");
							MultiPlayerConn m = multi_conns_queue.get(0);
								System.out.println("Assigning new multi thread for this client");
								multi_conns_queue.remove(m);

								// create a new thread object
								Thread t = new MultiClientHandler(m.s, m.in, m.out, s, dis, dos);
								conns.add(t);

								// Invoking the start() method
								t.start();
						}
					 } else {
						if(conns.size() == 3) {
							dos.writeBytes("server-overloaded" + "\n");
						} else {
							System.out.println("Assigning new thread for this client");

							// create a new thread object
							Thread t = new ClientHandler(s, dis, dos);
							conns.add(t);

							// Invoking the start() method
							t.start();
						}
					 }
				}
				// if(line.equals("2")) {
				// 	System.out.println("Multiplayer Selected");
				// 	if(multi_conns_queue.size() == 0) {
				// 		System.out.println("There are no clients waiting for multi");
				// 		if(conns.size() == 3) {
				// 			dos.writeBytes("10server-overloaded" + "\n");
				// 		} else {
				// 			MultiPlayerConn m = new MultiPlayerConn(s, dis, dos);
				// 			multi_conns_queue.add(m);
				// 			System.out.println("Add multi client to queue");
				// 			dos.writeBytes("25Waiting for other player!" + "\n");
				// 		}
				// 	} else {
				// 		System.out.println("There is a player waiting for multi");
				// 		MultiPlayerConn m = multi_conns_queue.get(0);
				// 		if(conns.size() == 3) {
				// 			m.out.writeBytes("1server-overloaded" + "\n");
				// 			multi_conns_queue.remove(m);
				// 			dos.writeBytes("1server-overloaded" + "\n");
				// 		} else {
				// 			System.out.println("Assigning new multi thread for this client");
				// 			multi_conns_queue.remove(m);
				//
				// 			// create a new thread object
				// 			Thread t = new MultiClientHandler(m.s, m.in, m.out, s, dis, dos);
				// 			conns.add(t);
				//
				// 			// Invoking the start() method
				// 			t.start();
				// 		}
				// 	}
				//  } else {
				// 	if(conns.size() == 3) {
				// 		dos.writeBytes("1server-overloaded" + "\n");
				// 	} else {
				// 		System.out.println("Assigning new thread for this client");
				//
				// 		// create a new thread object
				// 		Thread t = new ClientHandler(s, dis, dos);
				// 		conns.add(t);
				//
				// 		// Invoking the start() method
				// 		t.start();
				// 	}
				//  }


			}
			catch (Exception e){
				s.close();
				e.printStackTrace();
			}
		}
	}
}

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


	// Constructor
	public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos) {
		this.s = s;
		this.in = dis;
		this.out = dos;
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
			data = "" + g1.getWordInProgress() + g1.getIncorrectGuesses();
			out.writeBytes("0" + g1.getLength() + g1.getNumIncorrect() + data + "\n");
			System.out.println("Word in progress : " + g1.getWordInProgress());



			//////////////////////////////////////////// GAME LOOP ///////////////////////////////////////////////////////////// 


			// while player hasn't guessed word and doesn't have 6 incorrect guesses
			while(g1.getGameOver() == false) {
				

				// get client message and guess
				clientMsg = in.readLine();
				parts = clientMsg.split("");
				guess = parts[1];
				

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
				out.writeBytes("0" + g1.getLength() + g1.getNumIncorrect() + data + "\n");


				// if the client has guessed the word, send end game packets and ready for game over
				if (!(g1.getWordInProgress().contains("_"))) {
					g1.setGameOver(true);
					out.writeBytes("You Win!\n");
					out.writeBytes("Game Over!\n");

				// if the client has guessed incorrectly 6 time, send end game messages and ready for game over
				} else if (g1.getNumIncorrect() >= 6) {
					g1.setGameOver(true);
					out.writeBytes("You Lose :(\n");
					out.writeBytes("Game Over!\n");
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
}

class MultiClientHandler extends Thread {
	final DataInputStream in1;
	final DataOutputStream out1;
	final Socket s1;
	final Socket s2;
	final DataInputStream in2;
	final DataOutputStream out2;


	// Constructor
	public MultiClientHandler(Socket s1, DataInputStream dis1, DataOutputStream dos1, Socket s2, DataInputStream dis2, DataOutputStream dos2) {
		this.s1 = s1;
		this.in1 = dis1;
		this.out1 = dos1;
		this.s2 = s2;
		this.in2 = dis2;
		this.out2 = dos2;
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
		  	out1.writeBytes("Game Starting!\n");
		  	out2.writeBytes("Game Starting!\n");



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
				out2.writeBytes("Waiting on Player 1\n");


				// send game packet to Player 1
				out1.writeBytes("Your Turn!\n");
				data = g1.getWordInProgress() + g1.getIncorrectGuesses();
				out1.writeBytes("0" + g1.getLength() + g1.getNumIncorrect() + data + "\n");


				// get P1 message and guess
				clientMsg = in1.readLine();
				parts = clientMsg.split("");
				guess = parts[1];


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
          			out1.writeBytes("Incorrect!\n");
				} else {
					out1.writeBytes("Correct!\n");
				}


				// if P1 has guessed the word, send end game messages and ready for game over
				if (!(g1.getWordInProgress().contains("_"))) {
					g1.setGameOver(true);
					out1.writeBytes("You Win!\n");
					out1.writeBytes("Game Over!\n");

					// send last game packet to player 2
					data = g1.getWordInProgress() + g1.getIncorrectGuesses();
					out2.writeBytes("0" + g1.getLength() + g1.getNumIncorrect() + data + "\n");

					out2.writeBytes("You Win!\n");
					out2.writeBytes("Game Over!\n");


				// if P1 has lost the game, send end game messages and ready for game over
				} else if (g1.getNumIncorrect() >= 6) {
					g1.setGameOver(true);
					out1.writeBytes("You Lose :(\n");
					out1.writeBytes("Game Over!\n");

					// send last game packet to player 2
					data = g1.getWordInProgress() + g1.getIncorrectGuesses();
					out2.writeBytes("0" + g1.getLength() + g1.getNumIncorrect() + data + "\n");

					out2.writeBytes("You Lose :(\n");
					out2.writeBytes("Game Over!\n");
				}




				///////////////////////////////////////// player 2's turn /////////////////////////////////////////////////



				// send message to Player 1
				out1.writeBytes("Waiting on Player 2\n");


				// send game packet to P2
				out2.writeBytes("Your Turn!\n");
				data = g1.getWordInProgress() + g1.getIncorrectGuesses();
				out2.writeBytes("0" + g1.getLength() + g1.getNumIncorrect() + data + "\n");


				// get P2 message and guess
				clientMsg = in2.readLine();
				parts = clientMsg.split("");
				guess = parts[1];


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
          			out2.writeBytes("Incorrect!\n");
				} else {
					out2.writeBytes("Correct!\n");
				}


				// if P2 has guessed the word, send end game messages and ready for gme over 
				if (!(g1.getWordInProgress().contains("_"))) {
					g1.setGameOver(true);
					out1.writeBytes("You Win!\n");
					out1.writeBytes("Game Over!\n");

					// send last game packet to player 2
					data = g1.getWordInProgress() + g1.getIncorrectGuesses();
					out1.writeBytes("0" + g1.getLength() + g1.getNumIncorrect() + data + "\n");

					out2.writeBytes("You Win!\n");
					out2.writeBytes("Game Over!\n");


				// if P2 has lost the game, send end game messages and ready for game over
				} else if (g1.getNumIncorrect() >= 6) {
					g1.setGameOver(true);
					out1.writeBytes("You Lose!\n");
					out1.writeBytes("Game Over!\n");

					// send last game packet to player 2
					data = g1.getWordInProgress() + g1.getIncorrectGuesses();
					out1.writeBytes("0" + g1.getLength() + g1.getNumIncorrect() + data + "\n");

					out2.writeBytes("You Lose!\n");
					out2.writeBytes("Game Over!\n");
				}
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

}
