
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
		if ( ( NO_RESERVATION_FOUND + name ).equals( search( name ) ) ) {
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
		if ( ( NO_RESERVATION_FOUND + name ).equals( search( name ) ) ) {
			message = SEAT_ALREADY_BOOKED;
		} else if ( currentReservations[seat] == null || "".equals( currentReservations[seat] ) ) {
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
