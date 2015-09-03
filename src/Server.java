import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

public class Server {
	private MovieTheater theater;
	ServerSocket serverSocket;
	DatagramSocket datagramSocket;
	
	public Server(int numberOfSeats, int tcpPort, int udpPort) throws IOException {
		theater = MovieTheater.INSTANCE;
		theater.setCapacity(numberOfSeats);
		serverSocket = new ServerSocket(tcpPort);
		datagramSocket = new DatagramSocket(udpPort);
	}
	
	public void startServer() {
		try {
			while ( true ) {
				Socket socket = serverSocket.accept();
				handleTCPClient(socket);
				socket.close();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	private String handleInput(String input) {
		String returnValue = "";
		StringTokenizer tokenizer = new StringTokenizer(input);
		String command = tokenizer.nextToken();
		if ( command == null ) {
			return returnValue;
		} else if ( "reserve".equals( command.toLowerCase() ) ) {
			returnValue = theater.reserve( tokenizer.nextToken() );
		} else if ( "bookSeat".equals( command.toLowerCase() ) ) {
			returnValue = theater.bookSeat( tokenizer.nextToken(), Integer.parseInt( tokenizer.nextToken() ) );
		} else if ( "search".equals( command.toLowerCase() ) ) {
			returnValue = theater.search( tokenizer.nextToken() );
		} else if ( "delete".equals( command.toLowerCase() ) ) {
			returnValue = theater.delete( tokenizer.nextToken() );
		}
		return returnValue;
	}
	
	private void handleUDPClient(DatagramPacket packet) {
		try {
			String returnValue = handleInput( new String( packet.getData() ) );
			byte[] bytes = new byte[ returnValue.length() ];
			DatagramPacket returnPacket = new DatagramPacket(
						bytes,
						bytes.length,
						packet.getAddress(),
						packet.getPort()
					);
			datagramSocket.send( returnPacket );
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	private void handleTCPClient(Socket socket) {
		try {
			BufferedReader bReader = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
			PrintWriter pWriter = new PrintWriter( socket.getOutputStream() );
			String input = bReader.readLine();
			pWriter.println( handleInput( input ) );
			pWriter.flush();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void main (String[] args) {
		int N;
	    int tcpPort;
	    int udpPort;
	    if (args.length != 3) {
	      System.out.println("ERROR: Provide 3 arguments");
		  System.out.println("\t(1) <N>: the total number of available seats");
		  System.out.println("\t\t\tassume the seat numbers are from 1 to N");
		  System.out.println("\t(2) <tcpPort>: the port number for TCP connection");
		  System.out.println("\t(3) <udpPort>: the port number for UDP connection");
		  System.exit(-1);
		}
	    N = Integer.parseInt(args[0]);
	    tcpPort = Integer.parseInt(args[1]);
	    udpPort = Integer.parseInt(args[2]);
	    
	    try {
	    	Server server = new Server(N, tcpPort, udpPort);
	    } catch (Exception e) {
	    	System.out.println(e.getMessage());
	    }
	}
}
