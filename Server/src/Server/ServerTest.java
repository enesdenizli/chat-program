package Server;

import javax.swing.JFrame;


public class ServerTest {

	public static void main(String[] args) {
		Server infection = new Server();
		infection.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // exits when closed
		infection.startRunning();	// starts running  
	}

}
