package Client;

import javax.swing.JFrame;

public class ClientTest {

	public static void main(String[] args) {
		Client coronaVirus;	
		coronaVirus = new Client("192.168.1.74");		// The server's IP address, which we want to connect to (BU ADRES BENIM BILGISAYARIN ADRESI)
		coronaVirus.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		coronaVirus.startRunning();  // starts running
	}

}
