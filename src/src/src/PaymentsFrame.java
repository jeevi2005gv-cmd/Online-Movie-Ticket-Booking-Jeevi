import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PaymentsFrame extends JFrame {

    public PaymentsFrame() {
        setTitle("Payments");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(UIComponents.createHeader("Payments", "THEATRE - Admin Panel",
                UIComponents.DASH_C1, UIComponents.DASH_C2), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel totalLbl = new JLabel("Total Collected: \u20B90.00");
        totalLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        content.add(totalLbl, BorderLayout.NORTH);

        String[] cols = {"Booking ID", "Movie", "Payment Method", "Amount (\u20B9)", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(26);
        content.add(new JScrollPane(table), BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);

        double total = 0;
        try {
            List<BookingRecord> all = BookingService.getAllBookings();
            for (BookingRecord r : all) {
                model.addRow(new Object[]{r.bookingCode, r.movieName, r.paymentMethod,
                        String.format("%.2f", r.totalAmount), r.status});
                if ("Booked".equals(r.status)) total += r.totalAmount;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        totalLbl.setText("Total Collected: \u20B9" + String.format("%.2f", total));
    }
}