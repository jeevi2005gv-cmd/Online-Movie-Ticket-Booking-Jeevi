import javax.swing.*;
import java.awt.*;

public class HomeFrame extends JFrame {

    public HomeFrame() {
        setTitle("Theatre Seat Booking System - Home");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIComponents.BG);

        add(UIComponents.createHeader("Home / Welcome", "Theatre Seat Booking System",
                UIComponents.HOME_C1, UIComponents.HOME_C2), BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setBackground(Color.WHITE);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(50, 60, 50, 60));

        JLabel h1 = new JLabel("<html>Your Movie Experience<br><span style='color:#7C3AED;'>Starts Here!</span></html>");
        h1.setFont(new Font("Segoe UI", Font.BOLD, 30));

        JLabel p = new JLabel("<html><br>Book your favourite movies, choose your seats and enjoy<br>the show with ease and comfort.<br><br></html>");
        p.setFont(UIComponents.NORMAL_FONT);
        p.setForeground(new Color(90, 90, 90));

        JPanel buttonRow = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonRow.setOpaque(false);
        buttonRow.setMaximumSize(new Dimension(500, 50));
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        UIComponents.GradientButton userBtn = new UIComponents.GradientButton(
                "\uD83D\uDC64 USER LOGIN", UIComponents.HOME_C1, UIComponents.HOME_C2);
        UIComponents.GradientButton adminBtn = new UIComponents.GradientButton(
                "\u2699 ADMIN LOGIN", UIComponents.SEAT_C1, UIComponents.SEAT_C2);

        userBtn.addActionListener(e -> {
            new UserLoginFrame().setVisible(true);
            dispose();
        });
        adminBtn.addActionListener(e -> {
            new AdminLoginFrame().setVisible(true);
            dispose();
        });

        buttonRow.add(userBtn);
        buttonRow.add(adminBtn);

        h1.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        center.add(h1);
        center.add(p);
        center.add(buttonRow);

        add(center, BorderLayout.CENTER);
    }
}