import java.util.ArrayList;

public class Flight {
    private String flightNumber;
    private ArrayList<Seat> seats;

    public Flight(String flightNumber) {
        this.flightNumber = flightNumber;
        seats = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            seats.add(new Seat(i));
        }
    }

    // Method to display seat details
    public void displaySeats() {
        for (Seat seat : seats) {
            System.out.println(seat);
        }
    }

    // Getter for seats
    public ArrayList<Seat> getSeats() {
        return seats;
    }

    // Method to book a seat
    public boolean bookSeat(int seatNumber, Customer customer) {
        Seat seat = getSeatByNumber(seatNumber);
        if (seat != null && seat.isAvailable()) {
            seat.setAvailable(false);
            seat.setCustomer(customer);
            return true;
        }
        return false;
    }

    // Method to cancel a booking
    public boolean cancelBooking(int seatNumber) {
        Seat seat = getSeatByNumber(seatNumber);
        if (seat != null && !seat.isAvailable()) {
            seat.setAvailable(true);
            seat.setCustomer(null);
            return true;
        }
        return false;
    }

    // Helper method to find a seat by number
    private Seat getSeatByNumber(int seatNumber) {
        for (Seat seat : seats) {
            if (seat.getSeatNumber() == seatNumber) {
                return seat;
            }
        }
        return null;
    }
}
