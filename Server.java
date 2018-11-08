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
					dos.writeBytes("1server-overloaded" + "\n");
				} else {
					String line = dis.readLine();
					if(line.equals("2")) {
						System.out.println("Multiplayer Selected");
						if(multi_conns_queue.size() == 0) {
							System.out.println("There are no clients waiting for multi");
							MultiPlayerConn m = new MultiPlayerConn(s, dis, dos);
							multi_conns_queue.add(m);
							System.out.println("Add multi client to queue");
							dos.writeBytes("25Waiting for other player!" + "\n");
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
							dos.writeBytes("1server-overloaded" + "\n");
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
		String message;
		String clientMsg;

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
		int numIncorrect = 0;
		String wordInProgress = ""; // blanks with correct letters user has guessed
		String incorrectGuesses = "";
    	ArrayList<Game> games = new ArrayList<>();
    	Game g1 = null;

		try {

		  	// reads in message to start game
		  	//clientMsg = in.readLine();

		  	// do you even need the next two lines??
		  	//System.out.println("Read the message: " + clientMsg);
		  	//String[] parts = clientMsg.split(""); // splits message into length and letter
				String[] parts;


		  	// set up for game
		  	// picks random word, creates header
		  	//if(parts[0].equals("0")) {

				//send the word that the player will be guessing.
				//System.out.println("Empty message sent, start game");

				// select random word
				int num = rand.nextInt(15);
				//System.out.println("Random Number selected: " + num);
				word = words[num];
				System.out.println("Selected Word: " + word);

				//length = word.length();
				length = Integer.toString(word.length());
				for(int i = 0; i < word.length(); i++) {
				  wordInProgress += "_";
				}

				// packet formatted as msg flag = 0, length = word.length, incorrectguesses = 0, data = _____
		        g1 = new Game(word, s);
				data = "" + g1.getWordInProgress() + g1.getIncorrectGuesses();
		        System.out.println(g1.getLength());
		        System.out.println(g1.getNumIncorrect());
		        String myStr = "0" + g1.getLength() + g1.getNumIncorrect() + data + "\n";
		        System.out.println("--------" + g1.getWordInProgress());
				out.writeBytes("0" + g1.getLength() + g1.getNumIncorrect() + data + "\n");
			//}


			while(g1.getGameOver() == false) {
				clientMsg = in.readLine();
				parts = clientMsg.split("");
				String guess = parts[1];
				int count = 0; // number of hits with guess
				for (int i = 0; i < g1.getLength(); i++) {
					if (g1.getWord().indexOf(guess, i) == i) {
						count++;
						g1.setWordInProgress(g1.getWordInProgress().substring(0, i) + guess + g1.getWordInProgress().substring(i + 1));
					}
				}

				// if it was incorrect guess
				if (count == 0) {
          			g1.setNumIncorrect(g1.getNumIncorrect() + 1);
          			g1.setIncorrectGuesses(g1.getIncorrectGuesses() + guess);
				}

				data = g1.getWordInProgress() + g1.getIncorrectGuesses();
				System.out.println(data);
				out.writeBytes("0" + g1.getLength() + g1.getNumIncorrect() + data + "\n");

				if (!(g1.getWordInProgress().contains("_"))) {
					g1.setGameOver(true);
					out.writeBytes("8You Win!\n");
					out.writeBytes("9GAME OVER\n");
				} else if (g1.getNumIncorrect() >= 6) {
					g1.setGameOver(true);
					out.writeBytes("9You Lose!\n");
					out.writeBytes("9GAME OVER\n");
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
		String message;
		String clientMsg;

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
		int numIncorrect = 0;
		String wordInProgress = ""; // blanks with correct letters user has guessed
		String incorrectGuesses = "";
    	ArrayList<Game> games = new ArrayList<>();
    	Game g1 = null;

		try {

		  	// reads in message to start game
		  	//clientMsg = in.readLine();

		  	// do you even need the next two lines??
		  	//System.out.println("Read the message: " + clientMsg);
		  	//String[] parts = clientMsg.split(""); // splits message into length and letter
		  	String[] parts;


		  	// set up for game
		  	// picks random word, creates header
		  	//if(parts[0].equals("0")) {

				//send the word that the player will be guessing.
				//System.out.println("Empty message sent, start game");

		  	//game start
		  	out1.writeBytes("14Game Starting!\n");
		  	out1.writeBytes("10Your Turn!\n");
		  	out2.writeBytes("14Game Starting!\n");
				// select random word
				int num = rand.nextInt(15);
				//System.out.println("Random Number selected: " + num);
				word = words[num];
				System.out.println("Selected Word: " + word);

				//length = word.length();
				length = Integer.toString(word.length());
				for(int i = 0; i < word.length(); i++) {
				  wordInProgress += "_";
				}

				// packet formatted as msg flag = 0, length = word.length, incorrectguesses = 0, data = _____
		        g1 = new Game(word, s1);
				data = "" + g1.getWordInProgress() + g1.getIncorrectGuesses();
		        System.out.println(g1.getLength());
		        System.out.println(g1.getNumIncorrect());
		        String myStr = "0" + g1.getLength() + g1.getNumIncorrect() + data + "\n";
		        System.out.println("--------" + g1.getWordInProgress());
				out1.writeBytes("0" + g1.getLength() + g1.getNumIncorrect() + data + "\n");

				//send packet to p2 saying waiting for p1
				out2.writeBytes("19Waiting on Player 1" + "\n");
			//}


			while(g1.getGameOver() == false) {
				

////////////////////////////////////////////////////////// player 1's turn /////////////////////////////////////////////////

				clientMsg = in1.readLine();
				parts = clientMsg.split("");
				String guess = parts[1];
				int count = 0; // number of hits with guess
				boolean incorrect = false;
				for (int i = 0; i < g1.getLength(); i++) {
					if (g1.getWord().indexOf(guess, i) == i) {
						count++;
						g1.setWordInProgress(g1.getWordInProgress().substring(0, i) + guess + g1.getWordInProgress().substring(i + 1));
					}
				}

				// if it was incorrect guess
				if (count == 0) {
          			g1.setNumIncorrect(g1.getNumIncorrect() + 1);
          			g1.setIncorrectGuesses(g1.getIncorrectGuesses() + guess);
          			incorrect = true;
				}

				if(incorrect == true) {
					out1.writeBytes("9INCORRECT\n");
				} else {
					out1.writeBytes("7CORRECT\n");
				}



				if (!(g1.getWordInProgress().contains("_"))) {
					g1.setGameOver(true);
					out1.writeBytes("8You Win!\n");
					out1.writeBytes("9GAME OVER\n");

					// send game packet to player 2
					data = g1.getWordInProgress() + g1.getIncorrectGuesses();
					out2.writeBytes("0" + g1.getLength() + g1.getNumIncorrect() + data + "\n");

					out2.writeBytes("8You Win!\n");
					out2.writeBytes("9GAME OVER\n");
				} else if (g1.getNumIncorrect() >= 6) {
					g1.setGameOver(true);
					out1.writeBytes("9You Lose!\n");
					out1.writeBytes("9GAME OVER\n");

					// send game packet to player 2
					data = g1.getWordInProgress() + g1.getIncorrectGuesses();
					System.out.println("number of incorrect guesses is: " + g1.getNumIncorrect());
					out2.writeBytes("0" + g1.getLength() + g1.getNumIncorrect() + data + "\n");

					out2.writeBytes("9You Lose!\n");
					out2.writeBytes("9GAME OVER\n");
				}



/////////////////////////////////////////////////// player 2's turn /////////////////////////////////////////////////////////////////////////////


				out1.writeBytes("19Waiting on Player 2\n");
				out2.writeBytes("10Your Turn!\n");
				data = g1.getWordInProgress() + g1.getIncorrectGuesses();
				System.out.println(data);
				out2.writeBytes("0" + g1.getLength() + g1.getNumIncorrect() + data + "\n");



				//Player 2 game play
				clientMsg = in2.readLine();
				parts = clientMsg.split("");
				guess = parts[1];
				count = 0; // number of hits with guess
				incorrect = false;
				for (int i = 0; i < g1.getLength(); i++) {
					if (g1.getWord().indexOf(guess, i) == i) {
						count++;
						g1.setWordInProgress(g1.getWordInProgress().substring(0, i) + guess + g1.getWordInProgress().substring(i + 1));
					}
				}

				// if it was incorrect guess
				if (count == 0) {
          			g1.setNumIncorrect(g1.getNumIncorrect() + 1);
          			g1.setIncorrectGuesses(g1.getIncorrectGuesses() + guess);
          			incorrect = true;
				}

				if(incorrect == true) {
					out2.writeBytes("9INCORRECT\n");
				} else {
					out2.writeBytes("7CORRECT\n");
				}

				if (!(g1.getWordInProgress().contains("_"))) {
					g1.setGameOver(true);
					out1.writeBytes("8You Win!\n");
					out1.writeBytes("9GAME OVER\n");
					out2.writeBytes("8You Win!\n");
					out2.writeBytes("9GAME OVER\n");
				} else if (g1.getNumIncorrect() >= 6) {
					g1.setGameOver(true);
					out1.writeBytes("9You Lose!\n");
					out1.writeBytes("9GAME OVER\n");
					out2.writeBytes("9You Lose!\n");
					out2.writeBytes("9GAME OVER\n");
				}

				out2.writeBytes("19Waiting on Player 1\n");
				out1.writeBytes("10Your Turn!\n");
				data = g1.getWordInProgress() + g1.getIncorrectGuesses();
				System.out.println(data);
				out1.writeBytes("0" + g1.getLength() + g1.getNumIncorrect() + data + "\n");

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
