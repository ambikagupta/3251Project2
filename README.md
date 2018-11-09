# CS 3251 - Project 2: Hangman!

## Description of Our Ideas

In Server.java, we have a server that can handle multiple games and multiple users per game by creating a thread for each game. The server multithreads the game so that it can run multiple games at once with a max of three threads at a time. As each client connects to the server, it is asked if it would like to play with two players or as a single player. If the client requests a two player game, the server checks if there is another client waiting for a two player game. If so, it starts a multiplayer game with the two clients. If not, it stores the client while waiting for another client who would like to play a two player game

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
* To install Java
  - Follow the code below or find more information [here](https://thishosting.rocks/install-java-ubuntu/)
    ```
    apt-get install default-jdk
    ```

* To compile
  ```
  make
  ```

* To run
  - Open two terminals
    - The first terminal will be your Server terminal
    - The second terminal will be your Client terminal
  - In the Server terminal (MUST BE RUN FIRST)
    ```
    java Server [port number]
    ```
  - In the Client terminal
    ```
    java Client [IP address of Server] [port number]
    ```

* To clean
  ```
  make clean
  ```
