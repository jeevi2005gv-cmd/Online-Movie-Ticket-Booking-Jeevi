import javax.swing.*;
import java.awt.*;

public class BookingDetailsFrame extends JFrame {

    public BookingDetailsFrame() {
        setTitle("Booking Details");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new BorderLayout());
        top.add(UIComponents.createHeader("Booking Details", "THEATRE - Seat Booking System",
                UIComponents.BOOKING_C1, UIComponents.BOOKING_C2), BorderLayout.NORTH);
        top.add(UIComponents.createStepIndicator(
                new String[]{"Movie", "Seats", "Booking Details", "Payment", "Confirmation"}, 2), BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        Showtime s = SessionData.currentShowtime;
        int totalSeats = SessionData.selectedSeats.size();
        double total = totalSeats * s.ticketPrice;

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel cards = new JPanel(new GridLayout(1, 2, 20, 0));
        cards.setBackground(Color.WHITE);

        JPanel customerCard = card("CUSTOMER DETAILS");
        addRow(customerCard, "Name", SessionData.fullName);
        addRow(customerCard, "Phone", SessionData.phone);
        addRow(customerCard, "Email", SessionData.email);
        addRow(customerCard, "Address", SessionData.address);

        JPanel summaryCard = card("BOOKING SUMMARY");
        addRow(summaryCard, "Movie", s.movieName);
        addRow(summaryCard, "Date", s.showDate);
        addRow(summaryCard, "Time", s.showTime);
        addRow(summaryCard, "Screen", s.screen);
        addRow(summaryCard, "Seats", String.join(", ", SessionData.selectedSeats));
        addRow(summaryCard, "Ticket Price", "\u20B9" + String.format("%.2f", s.ticketPrice));
        addRow(summaryCard, "Total Seats", String.valueOf(totalSeats));
        addRow(summaryCard, "Total Amount", "\u20B9" + String.format("%.2f", total));

        cards.add(customerCard);
        cards.add(summaryCard);
        content.add(cards, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        buttons.setBackground(Color.WHITE);

        JButton backBtn = new JButton("BACK");
        backBtn.addActionListener(e -> {
            new SeatSelectionFrame().setVisible(true);
            dispose();
        });

        UIComponents.GradientButton proceedBtn = new UIComponents.GradientButton(
                "PROCEED TO PAYMENT", UIComponents.BLUE, UIComponents.REPORT_C2);
        proceedBtn.setPreferredSize(new Dimension(220, 42));
        proceedBtn.addActionListener(e -> {
            new PaymentFrame().setVisible(true);
            dispose();
        });

        buttons.add(backBtn);
        buttons.add(proceedBtn);
        content.add(buttons, BorderLayout.SOUTH);

        add(content, BorderLayout.CENTER);
    }

    private JPanel card(String title) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(new Color(249, 250, 252));
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JLabel h = new JLabel(title);
        h.setFont(new Font("Segoe UI", Font.BOLD, 15));
        p.add(h);
        p.add(Box.createVerticalStrut(10));
        return p;
    }

    private void addRow(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(new Color(249, 250, 252));
        row.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        JLabel l = new JLabel(label);
        JLabel v = new JLabel(value == null ? "-" : value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 13));
        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        panel.add(row);
    }
}