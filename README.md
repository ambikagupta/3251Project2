# CS 3251 - Project 2: Hangman!


## Description of Our Ideas
* In Server.java, we have a server that can handle multiple games and multiple users per game by creating a thread for each game. The server uses multithreading so that it can run multiple games at once with a max of three threads at a time. As each client connects to the server, it is asked if it would like to play with two players or as a single player. If the client requests a two player game, the server checks if there is another client waiting for a two player game. If so, it starts a multiplayer game with the two clients. If not, it stores the client while waiting for another client who would like to play a two player game. Once the multiplayer client has a partner or a single player arrives, the server assigns a thread to each game and calls the correct game handler. In the game handlers, game play and exit conditions are defined.

* In Client.java, we have the client code that handles reading input and writing output to a user and to the server. Once called, it asks the user for a single or multiplayer game, and communicates the answer to the server. It then handles game play by checking if the data coming from the server is a game packet or a message, and then follows the appropriate steps. It also checks if exit conditions are met to end the client gracefully.

* In Game.java, we have defined a Game object to make handling each game easier. It holds values for each game such as the word, incorrect guesses, word in progress, and a game over boolean.



## Contributors and Contributions
**Ambika Gupta** and **Sahithi Puligundla**

In order to complete this project, we divided the work as we progressed. We met every day for a week and would pair program for a few hours. Then, we would go home and finish our objectives for the day.

* Creating TCP Connection: Sahithi
* Creating codebase: Sahithi
* One single-player game functionality: Ambika
* Multi-game functionality: Sahithi
* Multi-player game functionality: Ambika
* Makefile: Ambika



## How to Run the Code & Install Dependent Packages
* To install Java on Linux
  - Follow the code below or find more information [here](https://thishosting.rocks/install-java-ubuntu/)
    ```
    apt-get install default-jdk
    ```


* To compile (must be done locally as GaTech's CoC shuttle servers don't have Java)
  - Navigate to the project's directory
    ```
    make
    ```


* To run locally
  - Open two terminals in the project directory
    - The first terminal will be your Server terminal
    - The second terminal will be your Client terminal
  - In the Server terminal (MUST BE RUN FIRST)
    - If you want to default to port 8080
      ```
      make run
      ```
    - For any other port numbers
      ```
      java Server [port number]
      ```
  - In the Client terminal
    ```
    java Client [IP address of Server] [port number]
    ```


* To run on shuttle servers
  - Push files to shuttle server
    - Replace [agupta437] with your GT username
    - Replace [cc-shuttle1] with another shuttle server name if desired
    ```
    scp * agupta437@cc-shuttle1.cc.gatech.edu:/nethome/agupta437
    ```
  - Navigate into the shuttle servers
    - Replace [agupta437] with your GT username
    - Replace [cc-shuttle1] with whichever shuttle server you used for the previous step
    ```
    ssh agupta437@cc-shuttle1.cc.gatech.edu
    ```
  - Open another terminal and repeat the previous step
  - Run Server in the first terminal [MUST BE RUN FIRST]
    - java Server [port number]
  - Run client in the second terminal
    - java Client [IP address of Server] [port number]


* To remove .class files
  - If you run this locally, you will have to compile again
  - If you run this on the shuttle servers, you will have to push the .class files again to the shuttle servers
  ```
  make clean
  ```


Tips:
  - To find your IP address
    ```
    ifconfig
    ```
  - Make sure to use IPv4
