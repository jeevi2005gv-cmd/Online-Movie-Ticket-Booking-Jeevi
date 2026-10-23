import javax.swing.*;
import java.awt.*;

public class ConfirmationFrame extends JFrame {

    public ConfirmationFrame() {
        setTitle("Ticket Confirmation");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new BorderLayout());
        top.add(UIComponents.createHeader("Ticket Confirmation", "THEATRE - Seat Booking System",
                UIComponents.CONFIRM_C1, UIComponents.CONFIRM_C2), BorderLayout.NORTH);
        top.add(UIComponents.createStepIndicator(
                new String[]{"Movie", "Seats", "Booking Details", "Payment", "Confirmation"}, 4), BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        BookingRecord b = fetchBooking(SessionData.lastBookingCode);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel cards = new JPanel(new GridLayout(1, 2, 20, 0));
        cards.setBackground(Color.WHITE);

        JPanel leftCard = new JPanel();
        leftCard.setLayout(new BoxLayout(leftCard, BoxLayout.Y_AXIS));
        leftCard.setBackground(new Color(249, 250, 252));
        leftCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel icon = new JLabel("\u2713", SwingConstants.CENTER);
        icon.setOpaque(true);
        icon.setBackground(UIComponents.GREEN);
        icon.setForeground(Color.WHITE);
        icon.setFont(new Font("Segoe UI", Font.BOLD, 32));
        icon.setPreferredSize(new Dimension(70, 70));
        icon.setMaximumSize(new Dimension(70, 70));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel confirmed = new JLabel("BOOKING CONFIRMED!", SwingConstants.CENTER);
        confirmed.setFont(new Font("Segoe UI", Font.BOLD, 18));
        confirmed.setForeground(UIComponents.GREEN);
        confirmed.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel thanks = new JLabel("Thank you! Your ticket has been booked successfully.", SwingConstants.CENTER);
        thanks.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftCard.add(icon);
        leftCard.add(Box.createVerticalStrut(10));
        leftCard.add(confirmed);
        leftCard.add(Box.createVerticalStrut(6));
        leftCard.add(thanks);

        JPanel rightCard = new JPanel();
        rightCard.setLayout(new BoxLayout(rightCard, BoxLayout.Y_AXIS));
        rightCard.setBackground(new Color(249, 250, 252));
        rightCard.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        if (b != null) {
            JLabel idLbl = new JLabel("Booking ID: " + b.bookingCode);
            idLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
            rightCard.add(idLbl);
            rightCard.add(Box.createVerticalStrut(10));
            rightCard.add(new JLabel("Movie: " + b.movieName));
            rightCard.add(new JLabel("Date: " + b.showDate));
            rightCard.add(new JLabel("Time: " + b.showTime));
            rightCard.add(new JLabel("Screen: " + b.screen));
            rightCard.add(new JLabel("Seats: " + b.seatNumbers));
            JLabel amt = new JLabel("Total Amount: \u20B9" + String.format("%.2f", b.totalAmount));
            amt.setFont(new Font("Segoe UI", Font.BOLD, 14));
            rightCard.add(Box.createVerticalStrut(10));
            rightCard.add(amt);
        } else {
            rightCard.add(new JLabel("Booking details not found."));
        }

        cards.add(leftCard);
        cards.add(rightCard);
        content.add(cards, BorderLayout.CENTER);

        UIComponents.GradientButton homeBtn = new UIComponents.GradientButton(
                "\uD83C\uDFE0 HOME", UIComponents.CONFIRM_C1, UIComponents.CONFIRM_C2);
        homeBtn.addActionListener(e -> {
            SessionData.clearUser();
            new HomeFrame().setVisible(true);
            dispose();
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        bottom.setBackground(Color.WHITE);
        bottom.add(homeBtn);
        content.add(bottom, BorderLayout.SOUTH);

        add(content, BorderLayout.CENTER);
    }

    private BookingRecord fetchBooking(String code) {
        if (code == null) return null;
        try {
            return BookingService.getBookingByCode(code);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}