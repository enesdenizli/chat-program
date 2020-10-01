package Server;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class Server extends JFrame{
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	
	//constructor
	public Server() {
		super("SERVER");
		userText = new JTextField();	// user text
		userText.setEditable(false);	// can't type if false if there is no connection
		userText.addActionListener(
					new ActionListener() {	// waits in the chat-box 
						@Override
						public void actionPerformed(ActionEvent event) {
							sendMessage(event.getActionCommand());		//this is the field the message is written, when connected to other user
							userText.setText("");	//when you hit enter and send message, field will be empty, basically resets the field
							
						}
					}
				);
				add(userText, BorderLayout.SOUTH);
				chatWindow = new JTextArea();
				add(new JScrollPane(chatWindow));
				setSize(400,200);
				setVisible(true);
	}
	
	// set up and run the server
	public void startRunning() {
		try {
			server = new ServerSocket(8081, 100);	// port number for chat room and backlog(number of user can enter) number
			while(true) {	// This loop will be running infinitely 
				try {
					waitForConnection();	// waits someone to connect to 
					setupStreams();		//this will setup input and output streams
					whileChatting();	//this will allow sending messages back and forth during chatting
				}catch(EOFException eofException) {		//connect and have conversation
					showMessage("\n Server ended the connection. "); 	// when server connection ends
				}finally {
					closeSession();
				}
			}
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	// wait for connection, then display connection info
	private void waitForConnection() throws IOException{
		showMessage(" Attempting to connect... \n"); // displayed when trying to connect
		connection = server.accept(); 	// Listens for a connection to be made to this socket and accepts it (server with client)
		showMessage(" Connected! You are connected to " + connection.getInetAddress().getHostName()); // displayed when connect and displays the client IP
	}
	
	// get stream to send and receive data
	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());	// creates pathway to SEND others who has 'connection' socket
		output.flush();	// just in case, flushes remained leftovers from buffer between connection (can be done ONLY by client)
		input = new ObjectInputStream(connection.getInputStream());	// creates pathway to RECEIVE others who has 'connection' socket
		showMessage("\n Streams are up and ready! \n");
	}
	
	//  during the chat conversation
	private void whileChatting() throws IOException {
		String message = " You are connected now!";
		sendMessage(message);
		ableToType(true);
		do { // have a conversation
			try {
				message = (String) input.readObject();	// 'input' is the socket where message is transferred through and it is viewed as object (message == ONLY String)
				showMessage("\n " + message);	// displays the message on a new line
			}catch(ClassNotFoundException classNotFoundException) {
				showMessage("\n Can't understand the message!");	// catches and displays when the message is not valid (can't convert to String)
			}
		}while(!message.equals(" CLIENT - END"));	// when "END" is written, conversation ends
	}
	
	// close streams and socket after you are done chatting
	private void closeSession() {
		showMessage("\n Closing session down... \n");
		ableToType(false);	// disables users to not be able to write with no connection
		try {	
			output.close();	// closes the output stream (to not to use extra memory on server)
			input.close();	// closes the input stream (to not to use extra memory on server)
			connection.close();	// closes the 'socket' (connection between computers)
		}catch(IOException ioException){	// just in case unsuccessful closing downs
			ioException.printStackTrace();
		}
	}
	
	// sends message to client
	private void sendMessage(String message) {	// takes the message as parameter
		try {	// in case the message couldn't be sent
			output.writeObject(" SERVER - " + message);	//  writes the client's message out from client
			output.flush();
			showMessage("\nSERVER - " + message);	// outputs the client's message on client
		}catch(IOException ioException) {
			chatWindow.append("\n Something went wrong during sending message!");	
		}
	}
	
	// updates chat window GUI
	private void showMessage(final String text) {
		SwingUtilities.invokeLater(	// allows you to create a thread that updates the part we want on GUI
			new Runnable() {
				public void run() {
					chatWindow.append(text); // adds the text at the end of the window as updated window
				}
			}
		);
	}
	
	// lets the user type stuff into their box
	private void ableToType(final boolean flag) {
		SwingUtilities.invokeLater(	// allows you to create a thread that updates the part we want on GUI
				new Runnable() {
					public void run() {
						userText.setEditable(flag); // enables/disables the user to type in
					}
				}
			);
		}
	
}
