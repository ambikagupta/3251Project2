import java.net.*;
import java.io.*;
import java.util.Random;
import java.util.ArrayList;

class Server{

  	public static void main(String args[]) throws Exception {
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

		int port = Integer.parseInt(args[0]); // port number

		ServerSocket ss = new ServerSocket(port);


		while(true) {
		  
			// create new connection
			Socket s = ss.accept();
			System.out.println("Accepted the connection");
			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream())); // messages coming in from client
			DataOutputStream out = new DataOutputStream(s.getOutputStream()); // message outgoing to clien

		  	// reads in message to start game
		  	clientMsg = in.readLine();
		  	
		  	// do you even need the next two lines??
		  	System.out.println("Read the message: " + clientMsg);
		  	String[] parts = clientMsg.split(""); // splits message into length and letter


		  	// set up for game
		  	// picks random word, creates header
		  	if(parts[0].equals("0")) {

				//send the word that the player will be guessing.
				System.out.println("Empty message sent, start game");
				
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
				data = wordInProgress + incorrectGuesses;
				out.writeBytes("0" + length + numIncorrect + data + "\n");
			}


			while (true) {
				clientMsg = in.readLine();
				parts = clientMsg.split("");
				String guess = parts[1];
				//System.out.println(guess);
				int count = 0;
				for (int i = 0; i < word.length(); i++) {
					if (word.indexOf(guess, i) == i) {
						count++;
						wordInProgress = wordInProgress.substring(0, i) + guess + wordInProgress.substring(i + 1);
						// might throw out of bounds error (i+1 in above line)
					}
				}
				//System.out.println(wordInProgress);

				if (count == 0) {
					numIncorrect++;
					incorrectGuesses += guess;
				}

				if (!(wordInProgress.contains("_"))) {
					out.writeBytes("8You Win!\n"); // how do I send the word back? should we send the word back first and then send another message?
				} else if (numIncorrect >= 6) {
					out.writeBytes("9You Lose!\n");
				} else {
					data = wordInProgress + incorrectGuesses;
					System.out.println(data);
					out.writeBytes("0" + length + numIncorrect + data + "\n");
					//System.out.println("in else");
				}
			} 

		  	// actual game play
			// message = in.readLine();
			// parts = message.split(","); // msg length, guess
			// String guess_1 = parts[1];
			// char guess = guess_1.charAt(0);
			  
			// // adds position of guess in word to arraylist
			// ArrayList<Integer> guessPosition = new ArrayList<>();
			// for(int i = 0; i < word.length(); i++) {
			// 	char c = word.charAt(i);
			// 	if(c == guess) {
			// 		guessPosition.add(i);
			// 	}
			// }

		  	// if the arraylist doesn't have anything in it, send the message
		 //  	if(guessPosition.size() == 0) {
			// 	out.writeBytes("0," + length + ",1," + data + "," + guess_1 + "\n");
		 //  	} else {
			// 	for(int i = 0; i < guessPosition.size(); i++) {
			//   	int pos = guessPosition.get(i);
			//   	String old_data = data;
			//   	data = "";
			//   	for(int j = 0; j < word.length(); j++) {
			// 		if(j == pos) {
			// 	  		data = data + guess;
			// 		} else {
			// 	  		data = data + old_data.charAt(j);
			// 		}
			//   	}
			// }
			// out.writeBytes("0," + length + ",0," + data + "\n");
		}
  	}
}
