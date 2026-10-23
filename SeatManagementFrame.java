import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SeatManagementFrame extends JFrame {

    private final List<Integer> showtimeIds = new ArrayList<>();
    private JComboBox<String> showtimeBox;
    private JPanel gridHolder;

    public SeatManagementFrame() {
        setTitle("Seat Management");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(UIComponents.createHeader("Seat Management", "THEATRE - Admin Panel",
                UIComponents.DASH_C1, UIComponents.DASH_C2), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        top.setBackground(Color.WHITE);
        top.add(new JLabel("Select Showtime:"));
        showtimeBox = new JComboBox<>();
        showtimeBox.setPreferredSize(new Dimension(400, 28));
        top.add(showtimeBox);
        JButton viewBtn = new JButton("VIEW SEATS");
        viewBtn.addActionListener(e -> showSeats());
        top.add(viewBtn);
        content.add(top, BorderLayout.NORTH);

        gridHolder = new JPanel();
        gridHolder.setBackground(Color.WHITE);
        content.add(new JScrollPane(gridHolder), BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);
        loadShowtimes();
    }

    private void loadShowtimes() {
        try {
            for (Showtime s : ShowtimeService.getAllShowtimes()) {
                showtimeIds.add(s.showtimeId);
                showtimeBox.addItem(s.movieName + " - " + s.showDate + " " + s.showTime + " (" + s.screen + ")");
            }
            if (!showtimeIds.isEmpty()) showSeats();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSeats() {
        gridHolder.removeAll();
        if (showtimeBox.getSelectedIndex() < 0) return;
        int showtimeId = showtimeIds.get(showtimeBox.getSelectedIndex());

        List<String> booked;
        try {
            booked = BookingService.getBookedSeats(showtimeId);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            booked = new ArrayList<>();
        }

        gridHolder.setLayout(new BoxLayout(gridHolder, BoxLayout.Y_AXIS));
        String[] rows = {"A", "B", "C", "D", "E", "F"};
        for (String r : rows) {
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
            rowPanel.setBackground(Color.WHITE);
            JLabel lbl = new JLabel(r);
            lbl.setPreferredSize(new Dimension(18, 30));
            rowPanel.add(lbl);
            for (int c = 1; c <= 10; c++) {
                String seatId = r + c;
                JLabel seat = new JLabel(String.valueOf(c), SwingConstants.CENTER);
                seat.setOpaque(true);
                seat.setPreferredSize(new Dimension(32, 30));
                seat.setForeground(Color.WHITE);
                seat.setBackground(booked.contains(seatId) ? UIComponents.RED : UIComponents.GREEN);
                rowPanel.add(seat);
            }
            gridHolder.add(rowPanel);
        }

        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        legend.setBackground(Color.WHITE);
        legend.add(legendItem("Available", UIComponents.GREEN));
        legend.add(legendItem("Booked", UIComponents.RED));
        gridHolder.add(legend);

        gridHolder.revalidate();
        gridHolder.repaint();
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
}