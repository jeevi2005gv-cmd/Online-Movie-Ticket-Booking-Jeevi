import javax.swing.*;
import java.awt.*;

public class SettingsFrame extends JFrame {

    public SettingsFrame() {
        setTitle("Settings");
        setSize(450, 420);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(UIComponents.createHeader("Settings", "THEATRE - Admin Panel",
                UIComponents.DASH_C1, UIComponents.DASH_C2), BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel heading = new JLabel("Change Admin Password");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JPasswordField currentF = new JPasswordField();
        JPasswordField newF = new JPasswordField();
        JPasswordField confirmF = new JPasswordField();
        for (JPasswordField f : new JPasswordField[]{currentF, newF, confirmF}) {
            f.setMaximumSize(new Dimension(350, 36));
            f.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        JLabel msgLbl = new JLabel(" ");
        msgLbl.setForeground(UIComponents.RED);

        UIComponents.GradientButton saveBtn = new UIComponents.GradientButton(
                "SAVE PASSWORD", UIComponents.DASH_C1, UIComponents.DASH_C2);
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.setMaximumSize(new Dimension(200, 42));

        saveBtn.addActionListener(e -> {
            String current = new String(currentF.getPassword());
            String newPass = new String(newF.getPassword());
            String confirm = new String(confirmF.getPassword());

            if (current.isEmpty() || newPass.isEmpty()) {
                msgLbl.setForeground(UIComponents.RED);
                msgLbl.setText("All fields are required.");
                return;
            }
            if (!newPass.equals(confirm)) {
                msgLbl.setForeground(UIComponents.RED);
                msgLbl.setText("New password and confirm password do not match.");
                return;
            }

            try {
                boolean ok = AdminService.changePassword(SessionData.adminId, current, newPass);
                if (ok) {
                    msgLbl.setForeground(UIComponents.GREEN);
                    msgLbl.setText("Password updated successfully.");
                    currentF.setText(""); newF.setText(""); confirmF.setText("");
                } else {
                    msgLbl.setForeground(UIComponents.RED);
                    msgLbl.setText("Current password is incorrect.");
                }
            } catch (Exception ex) {
                msgLbl.setForeground(UIComponents.RED);
                msgLbl.setText("Database error: " + ex.getMessage());
            }
        });

        form.add(heading);
        form.add(Box.createVerticalStrut(20));
        form.add(msgLbl);
        form.add(Box.createVerticalStrut(10));
        form.add(new JLabel("Current Password"));
        form.add(currentF);
        form.add(Box.createVerticalStrut(10));
        form.add(new JLabel("New Password"));
        form.add(newF);
        form.add(Box.createVerticalStrut(10));
        form.add(new JLabel("Confirm New Password"));
        form.add(confirmF);
        form.add(Box.createVerticalStrut(20));
        form.add(saveBtn);

        add(form, BorderLayout.CENTER);
    }
}