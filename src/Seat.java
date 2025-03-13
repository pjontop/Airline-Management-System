public class Seat {
    private int seatNumber;
    private boolean available;
    private Customer customer;

    public Seat(int seatNumber) {
        this.seatNumber = seatNumber;
        this.available = true;
        this.customer = null;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "Seat " + seatNumber + (available ? " (Available)" : " (Booked)");
    }
}
