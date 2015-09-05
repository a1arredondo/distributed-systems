import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

public class Server {
	private Server.MovieTheater theater;
	ServerSocketChannel tcpChannel;
	DatagramChannel udpChannel;
	Selector selector;
	
	public Server(int numberOfSeats, int tcpPort, int udpPort) throws IOException {
		theater = Server.MovieTheater.INSTANCE;
		theater.setCapacity(numberOfSeats);
		selector = Selector.open();
		
		InetAddress address = InetAddress.getByName("127.0.0.1");
		SocketAddress tcpChannelPort = new InetSocketAddress(address, tcpPort);
		SocketAddress udpChannelPort = new InetSocketAddress(address, udpPort);
		
		tcpChannel = ServerSocketChannel.open();
		tcpChannel.socket().bind(tcpChannelPort);
		tcpChannel.configureBlocking(false);
		tcpChannel.register(selector, SelectionKey.OP_ACCEPT);
		
		udpChannel = DatagramChannel.open();
		udpChannel.socket().bind(udpChannelPort);
		udpChannel.configureBlocking(false);
		udpChannel.register(selector, SelectionKey.OP_READ);
	}
	
	public void startServer() {
		try {
			while ( true ) {
				selector.select();
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = keys.iterator();
				while ( iterator.hasNext() ) {
					SelectionKey key = iterator.next();
					iterator.remove();
					Channel channel = key.channel();
					if ( key.isAcceptable() && channel == tcpChannel ) {
						handleTCPClient();
					} else if ( key.isAcceptable() && channel == udpChannel ) {
						handleUDPClient();
					}
				}
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
		} else if ( "bookseat".equals( command.toLowerCase() ) ) {
			String name = tokenizer.nextToken();
			System.out.println(name);
			String seat = tokenizer.nextToken();
			System.out.println(seat);
			returnValue = theater.bookSeat( name, Integer.parseInt( seat ) );
		} else if ( "search".equals( command.toLowerCase() ) ) {
			returnValue = theater.search( tokenizer.nextToken() );
		} else if ( "delete".equals( command.toLowerCase() ) ) {
			returnValue = theater.delete( tokenizer.nextToken() );
		}
		return returnValue;
	}
	
	private void handleTCPClient() {
		SocketChannel channel = null;
		try {
			byte[] request = new byte[1024];
			byte[] response;
			
			channel = tcpChannel.accept();
			if ( channel != null ) {
				channel.read(ByteBuffer.wrap(request));
				String requestString = new String(request);
				response = handleInput(requestString).getBytes();
				channel.write(ByteBuffer.wrap(response));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if ( channel != null && channel.isOpen() ) {
				try { channel.close(); } catch (Exception e) {}
			}
		}
	}
	
	private void handleUDPClient() {
		try {
			byte[] request = new byte[1024];
			byte[] response;
			
			SocketAddress address = udpChannel.receive(ByteBuffer.wrap(request));
			String requestString = new String(request);
			response = handleInput(requestString).getBytes();
			udpChannel.send(ByteBuffer.wrap(response), address);
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
	    	server.startServer();
	    } catch (Exception e) {
	    	System.out.println(e.getMessage());
	    }
	}
	
	public enum MovieTheater {
		INSTANCE;
		
		private static String NO_RESERVATION_FOUND = "No reservation found for ";
		private static String SEAT_ALREADY_BOOKED = "Seat already booked against the name provided";
		private static String SOLD_OUT = "Sold out - No seat available";
		
		private String[] currentReservations;
		
		public void setCapacity(int capacity) {
			currentReservations = new String[capacity];
		}
		
		public String reserve(String name) {
			String message = SOLD_OUT;
			if ( !( NO_RESERVATION_FOUND + name ).equals( search( name ) ) ) {
				message = SEAT_ALREADY_BOOKED;
			} else {
				for (int i = 0; i < currentReservations.length; i++) {
					if ( currentReservations[i] == null || "".equals( currentReservations[i] ) ) {
						currentReservations[i] = name;
						message = String.valueOf( i );
						break;
					}
				}
			}
			return message;
		}
		
		public String bookSeat(String name, int seat) {
			String message = seat + " is not available";
			if ( !( NO_RESERVATION_FOUND + name ).equals( search( name ) ) ) {
				message = SEAT_ALREADY_BOOKED;
			} else if ( currentReservations[seat] == null || "".equals( currentReservations[seat] ) ) {
				currentReservations[seat] = name;
				message = String.valueOf( seat );
			}
			return message;
		}
		
		public String search(String name) {
			String message = NO_RESERVATION_FOUND + name;
			for (int i = 0; i < currentReservations.length; i++) {
				if ( name != null && name.equals( currentReservations[i] ) ) {
					message = String.valueOf( i );
				}
			}
			return message;
		}
		
		public String delete(String name) {
			String message = NO_RESERVATION_FOUND + name;
			String searchMessage = search( name );
			if ( !( NO_RESERVATION_FOUND + name ).equals( searchMessage ) ) {
				currentReservations[Integer.valueOf( searchMessage )] = null;
				message = searchMessage;
			}
			return message;
		}
	}
}
