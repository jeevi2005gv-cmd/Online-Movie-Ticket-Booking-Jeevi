import org.bson.Document;

import javax.swing.*;
import java.awt.*;

public class UserLoginFrame extends JFrame {

    public UserLoginFrame() {
        setTitle("User Login");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(UIComponents.createHeader("THEATRE", "Seat Booking System",
                UIComponents.LOGIN_C1, UIComponents.LOGIN_C2), BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel heading = new JLabel("\uD83D\uDC64 USER LOGIN", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        styleField(userField);
        styleField(passField);

        JLabel errorLbl = new JLabel(" ");
        errorLbl.setForeground(UIComponents.RED);
        errorLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        UIComponents.GradientButton loginBtn = new UIComponents.GradientButton(
                "LOGIN", UIComponents.LOGIN_C1, UIComponents.LOGIN_C2);
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(300, 42));

        JLabel demo = new JLabel("Demo login: jeevitha / 1234");
        demo.setForeground(Color.GRAY);
        demo.setAlignmentX(Component.CENTER_ALIGNMENT);
        demo.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                errorLbl.setText("Please enter both username and password.");
                return;
            }

            try {
                Document user = UserService.login(username, password);
                if (user != null) {
                    SessionData.userId = user.getInteger("userId");
                    SessionData.username = user.getString("username");
                    SessionData.fullName = user.getString("fullName");
                    SessionData.phone = user.getString("phone");
                    SessionData.email = user.getString("email");
                    SessionData.address = user.getString("address");
                    new MovieSelectionFrame().setVisible(true);
                    dispose();
                } else if (UserService.usernameExists(username)) {
                    errorLbl.setText("Incorrect password.");
                } else {
                    errorLbl.setText("Username not found.");
                }
            } catch (Exception ex) {
                errorLbl.setText("Database error: " + ex.getMessage());
            }
        });

        form.add(heading);
        form.add(Box.createVerticalStrut(20));
        form.add(errorLbl);
        form.add(Box.createVerticalStrut(5));
        form.add(new JLabel("Username"));
        form.add(userField);
        form.add(Box.createVerticalStrut(12));
        form.add(new JLabel("Password"));
        form.add(passField);
        form.add(Box.createVerticalStrut(20));
        form.add(loginBtn);
        form.add(Box.createVerticalStrut(15));
        form.add(demo);

        add(form, BorderLayout.CENTER);
    }

    private void styleField(JTextField field) {
        field.setMaximumSize(new Dimension(400, 38));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
    }
}