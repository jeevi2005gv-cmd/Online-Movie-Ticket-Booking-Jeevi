import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BookingManagementFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    public BookingManagementFrame() {
        setTitle("Booking Management");
        setSize(950, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(UIComponents.createHeader("Booking Management", "THEATRE - Admin Panel",
                UIComponents.DASH_C1, UIComponents.DASH_C2), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        String[] cols = {"ID", "Booking ID", "Movie", "Date", "Time", "Screen", "Seats", "Amount (\u20B9)", "Payment", "Status"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(26);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        content.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbar.setBackground(Color.WHITE);

        UIComponents.GradientButton cancelBtn = new UIComponents.GradientButton("CANCEL BOOKING", UIComponents.RED, new Color(200, 40, 40));
        UIComponents.GradientButton restoreBtn = new UIComponents.GradientButton("RESTORE BOOKING", UIComponents.GREEN, new Color(52, 201, 141));
        JButton refreshBtn = new JButton("REFRESH");

        cancelBtn.addActionListener(e -> updateStatus("Cancelled"));
        restoreBtn.addActionListener(e -> updateStatus("Booked"));
        refreshBtn.addActionListener(e -> loadBookings());

        toolbar.add(cancelBtn);
        toolbar.add(restoreBtn);
        toolbar.add(refreshBtn);
        content.add(toolbar, BorderLayout.SOUTH);

        add(content, BorderLayout.CENTER);
        loadBookings();
    }

    private void loadBookings() {
        model.setRowCount(0);
        try {
            List<BookingRecord> all = BookingService.getAllBookings();
            for (BookingRecord r : all) {
                model.addRow(new Object[]{r.id, r.bookingCode, r.movieName, r.showDate, r.showTime, r.screen,
                        r.seatNumbers, String.format("%.2f", r.totalAmount), r.paymentMethod, r.status});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStatus(String newStatus) {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a booking first."); return; }
        int id = (Integer) model.getValueAt(row, 0);

        try {
            BookingService.updateStatus(id, newStatus);
            loadBookings();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}