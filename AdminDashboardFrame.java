import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboardFrame extends JFrame {

    public AdminDashboardFrame() {
        setTitle("Admin Dashboard");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(UIComponents.createHeader("Admin Dashboard", "THEATRE - Seat Booking System",
                UIComponents.DASH_C1, UIComponents.DASH_C2), BorderLayout.NORTH);

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(58, 33, 112));
        sidebar.setPreferredSize(new Dimension(210, 100));

        String[] menu = {"Dashboard", "Movie Management", "Show Time Management", "Seat Management",
                "Booking Management", "Customer Management", "Payments", "Cancellations", "Reports", "Settings"};
        for (String m : menu) {
            JButton b = new JButton(m);
            b.setForeground(Color.WHITE);
            b.setBackground(new Color(58, 33, 112));
            b.setBorderPainted(false);
            b.setFocusPainted(false);
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            b.setMaximumSize(new Dimension(210, 40));

            switch (m) {
                case "Movie Management":
                    b.addActionListener(e -> new MovieManagementFrame().setVisible(true));
                    break;
                case "Show Time Management":
                    b.addActionListener(e -> new ShowTimeManagementFrame().setVisible(true));
                    break;
                case "Seat Management":
                    b.addActionListener(e -> new SeatManagementFrame().setVisible(true));
                    break;
                case "Booking Management":
                    b.addActionListener(e -> new BookingManagementFrame().setVisible(true));
                    break;
                case "Customer Management":
                    b.addActionListener(e -> new CustomerManagementFrame().setVisible(true));
                    break;
                case "Payments":
                    b.addActionListener(e -> new PaymentsFrame().setVisible(true));
                    break;
                case "Cancellations":
                    b.addActionListener(e -> new CancellationsFrame().setVisible(true));
                    break;
                case "Reports":
                    b.addActionListener(e -> new BookingReportFrame().setVisible(true));
                    break;
                case "Settings":
                    b.addActionListener(e -> new SettingsFrame().setVisible(true));
                    break;
                case "Dashboard":
                    b.addActionListener(e -> {
                        new AdminDashboardFrame().setVisible(true);
                        dispose();
                    });
                    break;
            }
            sidebar.add(b);
        }
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(new Color(58, 33, 112));
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setHorizontalAlignment(SwingConstants.LEFT);
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.setMaximumSize(new Dimension(210, 40));
        logoutBtn.addActionListener(e -> {
            SessionData.clearAdmin();
            new HomeFrame().setVisible(true);
            dispose();
        });
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(logoutBtn);

        add(sidebar, BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(247, 248, 251));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel stats = new JPanel(new GridLayout(1, 4, 15, 0));
        stats.setBackground(new Color(247, 248, 251));

        long totalMovies = 0, totalShows = 0, totalBookings = 0;
        double totalCollection = 0;
        try {
            totalMovies = AdminService.totalMovies();
            totalShows = AdminService.totalShowtimes();
            totalBookings = BookingService.countBooked();
            totalCollection = BookingService.totalCollected();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        stats.add(statCard("Total Movies", String.valueOf(totalMovies), new Color(30, 111, 242)));
        stats.add(statCard("Total Shows", String.valueOf(totalShows), UIComponents.GREEN));
        stats.add(statCard("Total Bookings", String.valueOf(totalBookings), UIComponents.SEAT_C1));
        stats.add(statCard("Total Collection", "\u20B9" + String.format("%.2f", totalCollection), UIComponents.DASH_C1));

        main.add(stats, BorderLayout.NORTH);

        String[] cols = {"Booking ID", "Movie", "Date", "Time", "Screen", "Seats", "Amount (\u20B9)", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(26);

        try {
            List<BookingRecord> all = BookingService.getAllBookings();
            int limit = Math.min(10, all.size());
            for (int i = 0; i < limit; i++) {
                BookingRecord r = all.get(i);
                model.addRow(new Object[]{r.bookingCode, r.movieName, r.showDate, r.showTime, r.screen,
                        r.seatNumbers, String.format("%.2f", r.totalAmount), r.status});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel h = new JLabel("Recent Bookings");
        h.setFont(new Font("Segoe UI", Font.BOLD, 15));
        panel.add(h, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        main.add(panel, BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);
    }

    private JPanel statCard(String label, String value, Color color) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(color);
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 24));
        v.setForeground(Color.WHITE);
        JLabel l = new JLabel(label);
        l.setForeground(Color.WHITE);
        p.add(v);
        p.add(l);
        return p;
    }
}