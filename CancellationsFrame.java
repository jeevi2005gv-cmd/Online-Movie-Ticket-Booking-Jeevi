import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CancellationsFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    public CancellationsFrame() {
        setTitle("Cancellations");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(UIComponents.createHeader("Cancellations", "THEATRE - Admin Panel",
                UIComponents.DASH_C1, UIComponents.DASH_C2), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        String[] cols = {"ID", "Booking ID", "Movie", "Date", "Seats", "Amount (\u20B9)"};
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
        UIComponents.GradientButton restoreBtn = new UIComponents.GradientButton("RESTORE BOOKING", UIComponents.GREEN, new Color(52, 201, 141));
        JButton refreshBtn = new JButton("REFRESH");

        restoreBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a cancelled booking to restore."); return; }
            int id = (Integer) model.getValueAt(row, 0);
            try {
                BookingService.updateStatus(id, "Booked");
                loadCancellations();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        refreshBtn.addActionListener(e -> loadCancellations());

        toolbar.add(restoreBtn);
        toolbar.add(refreshBtn);
        content.add(toolbar, BorderLayout.SOUTH);

        add(content, BorderLayout.CENTER);
        loadCancellations();
    }

    private void loadCancellations() {
        model.setRowCount(0);
        try {
            List<BookingRecord> cancelled = BookingService.getBookingsByStatus("Cancelled");
            for (BookingRecord r : cancelled) {
                model.addRow(new Object[]{r.id, r.bookingCode, r.movieName, r.showDate,
                        r.seatNumbers, String.format("%.2f", r.totalAmount)});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}