import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;

public class MainGUI {
    private Flight flight;
    private JFrame frame;

    public MainGUI() {
        flight = new Flight("FB101");
        frame = new JFrame("Airline Booking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1));

        JButton viewSeatsButton = new JButton("View Seats");
        JButton bookSeatButton = new JButton("Book a Seat");
        JButton cancelBookingButton = new JButton("Cancel Booking");
        JButton viewManifestButton = new JButton("View Passenger Manifest");
        JButton exitButton = new JButton("Exit");

        panel.add(viewSeatsButton);
        panel.add(bookSeatButton);
        panel.add(cancelBookingButton);
        panel.add(viewManifestButton);
        panel.add(exitButton);

        viewSeatsButton.addActionListener(e -> showSeatViewGUI());
        bookSeatButton.addActionListener(e -> bookSeat());
        cancelBookingButton.addActionListener(e -> cancelBooking());
        viewManifestButton.addActionListener(e -> showManifest());
        exitButton.addActionListener(e -> System.exit(0));

        frame.add(panel);
        frame.setVisible(true);
    }

    private void showSeats() {
        StringBuilder seatInfo = new StringBuilder("Seat Availability:\n");
        for (Seat seat : flight.getSeats()) {
            seatInfo.append(seat).append("\n");
        }
        JOptionPane.showMessageDialog(frame, seatInfo.toString(), "Seat Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void bookSeat() {
        showSeatSelectionGUI();
    }

    private void showSeatViewGUI() {
        JFrame seatFrame = new JFrame("View Seats");
        seatFrame.setSize(300, 300);
        seatFrame.setLayout(new GridLayout(5, 2, 10, 10)); // 5 rows, 2 columns like a small plane

        for (Seat seat : flight.getSeats()) {
            JButton seatButton = new JButton("Seat " + seat.getSeatNumber());

            if (seat.isAvailable()) {
                seatButton.setEnabled(false); // Grey out unbooked seats
                seatButton.setBackground(Color.GRAY);
            } else {
                seatButton.setBackground(Color.GREEN);
                seatButton.addActionListener(e -> showPassengerDetails(seat)); // Show details on click for booked seats
            }

            seatFrame.add(seatButton);
        }

        seatFrame.setVisible(true);
    }

    private void showPassengerDetails(Seat seat) {
        if (!seat.isAvailable()) {
            Customer customer = seat.getCustomer();
            String message = "Passenger Details:\n";
            message += "Name: " + customer.getName() + "\n";
            message += "Address: " + customer.getAddress() + "\n";
            message += "Phone: " + customer.getPhone();

            JOptionPane.showMessageDialog(frame, message, "Passenger Information", JOptionPane.INFORMATION_MESSAGE);
            savePassengerToCSV(seat.getSeatNumber(), customer); // Save to CSV
        } else {
            JOptionPane.showMessageDialog(frame, "Seat is not booked yet.", "No Booking", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void savePassengerToCSV(int seatNumber, Customer customer) {
        String csvFile = "passenger_details.csv";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile, true))) {
            // Create CSV header if the file is empty
            File file = new File(csvFile);
            if (file.length() == 0) {
                writer.write("SeatNumber,Name,Address,Phone");
                writer.newLine();  // Add a new line after header
            }
            
            // Write passenger data into CSV
            writer.write(seatNumber + "," + customer.getName() + "," + customer.getAddress() + "," + customer.getPhone());
            writer.newLine();
            JOptionPane.showMessageDialog(frame, "Passenger details saved to CSV.", "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error saving to CSV: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSeatSelectionGUI() {
        JFrame seatFrame = new JFrame("Select a Seat");
        seatFrame.setSize(300, 300);
        seatFrame.setLayout(new GridLayout(5, 2, 10, 10)); // 5 rows, 2 columns like a small plane

        for (Seat seat : flight.getSeats()) {
            JButton seatButton = new JButton("Seat " + seat.getSeatNumber());

            if (!seat.isAvailable()) {
                seatButton.setEnabled(false); // Grey out booked seats
                seatButton.setBackground(Color.GRAY);
            } else {
                seatButton.setBackground(Color.GREEN);
                seatButton.addActionListener(e -> {
                    seatFrame.dispose(); // Close seat selection window
                    enterCustomerDetails(seat.getSeatNumber());
                });
            }

            seatFrame.add(seatButton);
        }

        seatFrame.setVisible(true);
    }

    private void enterCustomerDetails(int seatNumber) {
        String name = JOptionPane.showInputDialog(frame, "Enter customer name:");
        if (name == null || name.trim().isEmpty()) return; // Cancel booking if no name

        String address = JOptionPane.showInputDialog(frame, "Enter customer address:");
        String phone = JOptionPane.showInputDialog(frame, "Enter customer phone:");

        boolean success = flight.bookSeat(seatNumber, new Customer(name, address, phone));
        JOptionPane.showMessageDialog(frame, success ? "Seat booked successfully!" : "Seat is not available!", "Booking Status", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cancelBooking() {
        try {
            int seatNumber = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter seat number to cancel:"));
            boolean cancelled = flight.cancelBooking(seatNumber);
            JOptionPane.showMessageDialog(frame, cancelled ? "Booking canceled!" : "No booking found for that seat.", "Cancellation Status", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showManifest() {
        // Collect booked seats
        ArrayList<Seat> bookedSeats = new ArrayList<>();
        for (Seat seat : flight.getSeats()) {
            if (!seat.isAvailable()) {
                bookedSeats.add(seat);
            }
        }

        // Sort by seat number
        bookedSeats.sort(Comparator.comparingInt(Seat::getSeatNumber));

        // Build manifest text
        StringBuilder manifest = new StringBuilder("Passenger Manifest:\n");
        for (Seat seat : bookedSeats) {
            manifest.append("Seat ").append(seat.getSeatNumber()).append(": ")
                    .append(seat.getCustomer()).append("\n");
        }

        // Show manifest in a dialog
        JOptionPane.showMessageDialog(frame, manifest.toString(), "Passenger Manifest", JOptionPane.INFORMATION_MESSAGE);

        // Ask if the user wants to print
        int choice = JOptionPane.showConfirmDialog(frame, "Do you want to print the manifest?", "Print", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            printManifest(manifest.toString());
        }
    }

    private void printManifest(String manifestText) {
        JTextArea textArea = new JTextArea(manifestText);
        try {
            textArea.print();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Printing failed: " + e.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainGUI::new);
    }
}
