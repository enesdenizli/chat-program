package Client;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame{
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;	 
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;
	
	// constructor
	public Client(String host) {
		super("CLIENT");
		serverIP = host; 
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					sendMessage(event.getActionCommand());	// sends it to other user 
					userText.setText("");	// resets to empty, same as server
				}
			}
		);
		add(userText, BorderLayout.SOUTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(400,200);
		setVisible(true);
	}
	
	// connects to server
	public void startRunning() {
		try {
			connectToServer();	// we just need to connect to server
			setupStreams();
			whileChatting();
		}catch(EOFException eofException) {
			showMessage("\n You terminated connection");
		}catch(IOException ioException) {
			ioException.printStackTrace();
		}finally {
			closeSession();
		}
	}
	
	// connects to server 
	private void connectToServer() throws IOException{
		showMessage(" Attempting to connect... \n");
		connection = new Socket(InetAddress.getByName(serverIP), 8081);	// passes the IP address to the server with port # '8180'
		showMessage(" Connected! Your name is  " + connection.getInetAddress().getHostName());	// displays a connection message
	}
	
	// setups input and output stream
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Streams are up and ready! \n");
	}
	
	// while chatting with server
	private void whileChatting() throws IOException{
		String message = " You are connected now!";
		ableToType(true);	// disables/enables typing
		do {
			try {
				message = (String) input.readObject();
				showMessage("\n " + message);
			}catch(ClassNotFoundException classNotFoundException) {
				showMessage("\n Can't understand the message!");
			}
		}while(!message.equals(" CLIENT - END"));	// till write 'END'
	}
	
	// closes the streams and sockets
	private void closeSession() {
		showMessage("\n Closing session down...");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	// sends messages to server
	private void sendMessage(String message) {
		try {
			output.writeObject(" CLIENT - " + message);
			output.flush();
			showMessage("\n CLIENT - " + message);		// displays on GUI
		}catch(IOException ioException) {
			chatWindow.append("\n Something went wrong during sending message!");
		}
	}
	
	// updates/changes the GUI as we type on text box
	private void showMessage(final String m) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					chatWindow.append(m);
				}
			}
		);
	}
	
	// gives user permission to type text into the text box
	private void ableToType(final boolean flag) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					  userText.setEditable(flag);;
				}
			}
		);
	}
	
	

}
