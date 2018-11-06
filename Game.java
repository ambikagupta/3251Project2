public class Game {

  public String word;
  public String length;
  public int numIncorrect;
  public String wordInProgress;
  public String incorrectGuesses;

  public Game(String word, String length, int numIncorrect, String wordInProgress, String incorrectGuesses) {
    this.word = word;
    this.length = length;
    this.numIncorrect = numIncorrect;
    this.wordInProgress = wordInProgress;
    this.incorrectGuesses = incorrectGuesses;
  }

}
