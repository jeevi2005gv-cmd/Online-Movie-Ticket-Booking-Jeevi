import org.bson.Document;

import javax.swing.*;
import java.awt.*;

public class AdminLoginFrame extends JFrame {

    public AdminLoginFrame() {
        setTitle("Admin Login");
        setSize(450, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(UIComponents.createHeader("Admin Login", "THEATRE - Seat Booking System",
                UIComponents.ADMIN_C1, UIComponents.ADMIN_C2), BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel heading = new JLabel("\u2699 ADMIN LOGIN", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        userField.setMaximumSize(new Dimension(400, 38));
        passField.setMaximumSize(new Dimension(400, 38));
        userField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel errorLbl = new JLabel(" ");
        errorLbl.setForeground(UIComponents.RED);
        errorLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        UIComponents.GradientButton loginBtn = new UIComponents.GradientButton(
                "LOGIN", UIComponents.ADMIN_C1, UIComponents.ADMIN_C2);
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(300, 42));

        JButton backBtn = new JButton("BACK TO HOME");
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> {
            new HomeFrame().setVisible(true);
            dispose();
        });

        JLabel demo = new JLabel("Demo login: admin / admin123");
        demo.setForeground(Color.GRAY);
        demo.setAlignmentX(Component.CENTER_ALIGNMENT);
        demo.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());

            try {
                Document admin = AdminService.login(username, password);
                if (admin != null) {
                    SessionData.adminId = admin.getInteger("adminId");
                    SessionData.adminUsername = username;
                    new AdminDashboardFrame().setVisible(true);
                    dispose();
                } else if (AdminService.usernameExists(username)) {
                    errorLbl.setText("Incorrect password.");
                } else {
                    errorLbl.setText("Admin username not found.");
                }
            } catch (Exception ex) {
                errorLbl.setText("Database error: " + ex.getMessage());
            }
        });

        form.add(heading);
        form.add(Box.createVerticalStrut(20));
        form.add(errorLbl);
        form.add(Box.createVerticalStrut(5));
        form.add(new JLabel("Admin Username"));
        form.add(userField);
        form.add(Box.createVerticalStrut(12));
        form.add(new JLabel("Password"));
        form.add(passField);
        form.add(Box.createVerticalStrut(20));
        form.add(loginBtn);
        form.add(Box.createVerticalStrut(10));
        form.add(backBtn);
        form.add(Box.createVerticalStrut(15));
        form.add(demo);

        add(form, BorderLayout.CENTER);
    }
}