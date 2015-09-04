import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

	public static String doTCPRequest(String command, String host, int port) {
		String returnValue = "";
		Socket server = null;
		try {
			server = new Socket(host, port);
			BufferedReader bReader = new BufferedReader(new InputStreamReader(server.getInputStream()));
			PrintStream printStream = new PrintStream(server.getOutputStream());
			printStream.println(command);
			printStream.flush();
			returnValue = bReader.readLine();
		} catch (Exception e) {
			returnValue = e.getMessage();
		} finally {
			if ( server != null && !server.isClosed() ) {
				try { server.close(); } catch (Exception e) {}
			}
		}
		return returnValue;
	}
	
	public static String doUDPRequest(String command, String host, int port) {
		return "NOT IMPLEMENTED";
	}
	
  public static void main (String[] args) {
    String hostAddress;
    int tcpPort;
    int udpPort;

    if (args.length != 3) {
      System.out.println("ERROR: Provide 3 arguments");
      System.out.println("\t(1) <hostAddress>: the address of the server");
      System.out.println("\t(2) <tcpPort>: the port number for TCP connection");
      System.out.println("\t(3) <udpPort>: the port number for UDP connection");
      System.exit(-1);
    }

    hostAddress = args[0];
    tcpPort = Integer.parseInt(args[1]);
    udpPort = Integer.parseInt(args[2]);

    Scanner sc = new Scanner(System.in);
    while(sc.hasNextLine()) {
      String cmd = sc.nextLine();
      String[] tokens = cmd.split(" ");

      if (tokens[0].equals("reserve") && tokens.length >= 3) {
        System.out.println( 
        		( "T".equals( tokens[2] ) ? doTCPRequest( cmd, hostAddress, tcpPort ) : 
        			( "U".equals( tokens[2] ) ? doUDPRequest( cmd, hostAddress, udpPort ) : "ERROR: Bad command" ) ) );
      } else if (tokens[0].equals("bookSeat") && tokens.length >= 4) {
    	  System.out.println( 
          		( "T".equals( tokens[3] ) ? doTCPRequest( cmd, hostAddress, tcpPort ) : 
          			( "U".equals( tokens[3] ) ? doUDPRequest( cmd, hostAddress, udpPort ) : "ERROR: Bad command" ) ) );
      } else if (tokens[0].equals("search") && tokens.length >= 3) {
    	  System.out.println( 
          		( "T".equals( tokens[2] ) ? doTCPRequest( cmd, hostAddress, tcpPort ) : 
          			( "U".equals( tokens[2] ) ? doUDPRequest( cmd, hostAddress, udpPort ) : "ERROR: Bad command" ) ) );
      } else if (tokens[0].equals("delete") && tokens.length >= 3) {
    	  System.out.println( 
          		( "T".equals( tokens[2] ) ? doTCPRequest( cmd, hostAddress, tcpPort ) : 
          			( "U".equals( tokens[2] ) ? doUDPRequest( cmd, hostAddress, udpPort ) : "ERROR: Bad command" ) ) );
      } else {
        System.out.println("ERROR: No such command");
      }
    }
  }
}
