import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CustomerManagementFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    public CustomerManagementFrame() {
        setTitle("Customer Management");
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(UIComponents.createHeader("Customer Management", "THEATRE - Admin Panel",
                UIComponents.DASH_C1, UIComponents.DASH_C2), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        String[] cols = {"ID", "Username", "Full Name", "Phone", "Email", "Address"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(26);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        content.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbar.setBackground(Color.WHITE);
        UIComponents.GradientButton deleteBtn = new UIComponents.GradientButton("DELETE CUSTOMER", UIComponents.RED, new Color(200, 40, 40));
        JButton refreshBtn = new JButton("REFRESH");

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a customer to delete."); return; }
            int id = (Integer) model.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Delete this customer?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    if (!UserService.deleteUser(id)) {
                        JOptionPane.showMessageDialog(this,
                                "Cannot delete: this customer has bookings linked to their account.");
                    }
                    loadCustomers();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        refreshBtn.addActionListener(e -> loadCustomers());

        toolbar.add(deleteBtn);
        toolbar.add(refreshBtn);
        content.add(toolbar, BorderLayout.SOUTH);

        add(content, BorderLayout.CENTER);
        loadCustomers();
    }

    private void loadCustomers() {
        model.setRowCount(0);
        try {
            for (Document u : UserService.getAllUsers()) {
                model.addRow(new Object[]{u.getInteger("userId"), u.getString("username"), u.getString("fullName"),
                        u.getString("phone"), u.getString("email"), u.getString("address")});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}