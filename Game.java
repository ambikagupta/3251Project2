import java.net.*;
import java.io.*;

public class Game {

	private String word;  // randomly selected word
	private int length; // length of word
  	private int numIncorrect;  // number of incorrect guesses
  	private String wordInProgress = ""; // current word in progress
  	private String incorrectGuesses; // string of incorrect guesses in order
  	private boolean gameOver; // "true" when game is over for whatever reason
  	private Socket s; // socket that game was created on
  	//private BufferedReader in; // messages coming in from client
	//private	DataOutputStream out; // message outgoing to clien

  	public Game(String word, Socket s) {
		this.word = word;
		this.length = word.length();
		this.numIncorrect = 0;

		for(int i = 0; i < word.length(); i++) {
		  	this.wordInProgress += "_";
		}

		this.incorrectGuesses = "";
		this.gameOver = false;
		this.s = s;
		//this.in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		//this.out = new DataOutputStream(s.getOutputStream());
  	}

  	public String getWord() {
  		return this.word;
  	}

  	public int getLength() {
  		return this.length;
  	}

  	public int getNumIncorrect() {
  		return this.numIncorrect;
  	}

  	public void setNumIncorrect(int numIncorrect) {
  		this.numIncorrect = numIncorrect;  // b/c logic is in methods
  	}

  	public String getWordInProgress() {
  		return this.wordInProgress;
  	}

  	public void setWordInProgress(String wordInProgress) {
  		this.wordInProgress = wordInProgress;
  	}

  	public String getIncorrectGuesses() {
  		return this.incorrectGuesses;
  	}

  	public void setIncorrectGuesses(String incorrectGuesses) {
  		this.incorrectGuesses = incorrectGuesses;
  	}

  	public boolean getGameOver() {
  		return this.gameOver;
  	}

  	public void setGameOver(boolean gameOver) {
  		this.gameOver = gameOver;
  	}

	// public BufferedReader getIn() {
  // 		return this.in;
  // 	}
  //
  // 	public DataOutputStream getOut() {
  // 		return this.out;
  // 	}

}
