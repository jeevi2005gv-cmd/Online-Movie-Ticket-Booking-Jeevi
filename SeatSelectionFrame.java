import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SeatSelectionFrame extends JFrame {

    private final List<String> bookedSeats = new ArrayList<>();
    private final List<String> selected = new ArrayList<>();
    private JLabel seatsListLbl, countLbl, totalLbl;
    private final Showtime showtime = SessionData.currentShowtime;

    public SeatSelectionFrame() {
        setTitle("Seat Selection");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new BorderLayout());
        top.add(UIComponents.createHeader("Seat Selection", "THEATRE - Seat Booking System",
                UIComponents.SEAT_C1, UIComponents.SEAT_C2), BorderLayout.NORTH);
        top.add(UIComponents.createStepIndicator(
                new String[]{"Movie", "Seats", "Payment", "Confirmation"}, 1), BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        loadBookedSeats();

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel seatGrid = new JPanel();
        seatGrid.setLayout(new BoxLayout(seatGrid, BoxLayout.Y_AXIS));
        seatGrid.setBackground(Color.WHITE);

        String[] rows = {"A", "B", "C", "D", "E", "F"};
        for (String r : rows) {
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
            rowPanel.setBackground(Color.WHITE);
            JLabel rowLbl = new JLabel(r);
            rowLbl.setPreferredSize(new Dimension(18, 30));
            rowLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            rowPanel.add(rowLbl);

            for (int c = 1; c <= 10; c++) {
                String seatId = r + c;
                JButton seatBtn = new JButton(String.valueOf(c));
                seatBtn.setPreferredSize(new Dimension(34, 32));
                seatBtn.setMargin(new Insets(0, 0, 0, 0));
                seatBtn.setForeground(Color.WHITE);
                seatBtn.setFocusPainted(false);

                if (bookedSeats.contains(seatId)) {
                    seatBtn.setBackground(UIComponents.RED);
                    seatBtn.setEnabled(false);
                } else {
                    seatBtn.setBackground(UIComponents.GREEN);
                    seatBtn.addActionListener(e -> toggleSeat(seatId, seatBtn));
                }
                rowPanel.add(seatBtn);
            }
            seatGrid.add(rowPanel);
        }

        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        legend.setBackground(Color.WHITE);
        legend.add(legendItem("Available", UIComponents.GREEN));
        legend.add(legendItem("Booked", UIComponents.RED));
        legend.add(legendItem("Selected", UIComponents.BLUE));
        seatGrid.add(legend);

        JPanel summary = new JPanel();
        summary.setLayout(new BoxLayout(summary, BoxLayout.Y_AXIS));
        summary.setBackground(new Color(255, 247, 236));
        summary.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(243, 224, 196)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        summary.setPreferredSize(new Dimension(220, 250));

        JLabel h = new JLabel("Selected Seats");
        h.setFont(new Font("Segoe UI", Font.BOLD, 15));
        seatsListLbl = new JLabel("None");
        seatsListLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        seatsListLbl.setForeground(UIComponents.SEAT_C1);
        countLbl = new JLabel("Total Seats: 0");
        totalLbl = new JLabel("Total: \u20B90.00");
        totalLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));

        summary.add(h);
        summary.add(Box.createVerticalStrut(10));
        summary.add(seatsListLbl);
        summary.add(Box.createVerticalStrut(10));
        summary.add(countLbl);
        summary.add(new JLabel("Price/seat: \u20B9" + String.format("%.2f", showtime.ticketPrice)));
        summary.add(Box.createVerticalStrut(10));
        summary.add(totalLbl);

        JPanel layout = new JPanel(new BorderLayout(20, 0));
        layout.setBackground(Color.WHITE);
        layout.add(new JScrollPane(seatGrid), BorderLayout.CENTER);
        layout.add(summary, BorderLayout.EAST);

        content.add(layout, BorderLayout.CENTER);

        JLabel errorLbl = new JLabel(" ", SwingConstants.CENTER);
        errorLbl.setForeground(UIComponents.RED);

        UIComponents.GradientButton confirmBtn = new UIComponents.GradientButton(
                "CONFIRM SEATS \u2192", UIComponents.SEAT_C1, UIComponents.SEAT_C2);
        confirmBtn.addActionListener(e -> {
            if (selected.isEmpty()) {
                errorLbl.setText("Please select at least one seat.");
                return;
            }
            SessionData.selectedSeats = new ArrayList<>(selected);
            new BookingDetailsFrame().setVisible(true);
            dispose();
        });

        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBackground(Color.WHITE);
        errorLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottom.add(errorLbl);
        bottom.add(confirmBtn);
        content.add(bottom, BorderLayout.SOUTH);

        add(content, BorderLayout.CENTER);
    }

    private JPanel legendItem(String text, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.setBackground(Color.WHITE);
        JLabel box = new JLabel("  ");
        box.setOpaque(true);
        box.setBackground(color);
        box.setPreferredSize(new Dimension(14, 14));
        p.add(box);
        p.add(new JLabel(text));
        return p;
    }

    private void toggleSeat(String seatId, JButton btn) {
        if (selected.contains(seatId)) {
            selected.remove(seatId);
            btn.setBackground(UIComponents.GREEN);
        } else {
            selected.add(seatId);
            btn.setBackground(UIComponents.BLUE);
        }
        seatsListLbl.setText(selected.isEmpty() ? "None" : String.join(", ", selected));
        countLbl.setText("Total Seats: " + selected.size());
        totalLbl.setText("Total: \u20B9" + String.format("%.2f", selected.size() * showtime.ticketPrice));
    }

    private void loadBookedSeats() {
        try {
            bookedSeats.addAll(BookingService.getBookedSeats(showtime.showtimeId));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}