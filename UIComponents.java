import javax.swing.*;
import java.awt.*;

/**
 * Shared look-and-feel used by every screen so the whole app matches
 * one consistent color theme (similar gradient headers as the design).
 */
public class UIComponents {

    // ---- gradient colors used per screen ----
    public static final Color HOME_C1 = new Color(43, 63, 214),   HOME_C2 = new Color(124, 58, 237);
    public static final Color LOGIN_C1 = new Color(106, 63, 240), LOGIN_C2 = new Color(155, 77, 224);
    public static final Color MOVIE_C1 = new Color(15, 157, 88),  MOVIE_C2 = new Color(52, 201, 141);
    public static final Color SEAT_C1 = new Color(245, 129, 31),  SEAT_C2 = new Color(242, 169, 10);
    public static final Color BOOKING_C1 = new Color(15, 179, 179), BOOKING_C2 = new Color(18, 201, 167);
    public static final Color PAYMENT_C1 = new Color(233, 30, 140), PAYMENT_C2 = new Color(255, 77, 125);
    public static final Color CONFIRM_C1 = new Color(124, 58, 237), CONFIRM_C2 = new Color(76, 42, 214);
    public static final Color ADMIN_C1 = new Color(245, 129, 31),  ADMIN_C2 = new Color(242, 169, 10);
    public static final Color DASH_C1 = new Color(91, 42, 214),    DASH_C2 = new Color(124, 58, 237);
    public static final Color REPORT_C1 = new Color(30, 111, 242), REPORT_C2 = new Color(43, 155, 242);

    public static final Color BG = new Color(242, 244, 248);
    public static final Color GREEN = new Color(76, 175, 80);
    public static final Color RED = new Color(229, 57, 53);
    public static final Color BLUE = new Color(30, 111, 242);
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    /** A JPanel that paints a left-to-right gradient background. */
    public static class GradientPanel extends JPanel {
        private final Color c1, c2;
        public GradientPanel(Color c1, Color c2) {
            this.c1 = c1;
            this.c2 = c2;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), 0, c2));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    /** A rounded, gradient-filled JButton. */
    public static class GradientButton extends JButton {
        private final Color c1, c2;
        public GradientButton(String text, Color c1, Color c2) {
            super(text);
            this.c1 = c1;
            this.c2 = c2;
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(160, 42));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), 0, c2));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Builds a header bar with a title/subtitle on a gradient background. */
    public static JPanel createHeader(String title, String subtitle, Color c1, Color c2) {
        GradientPanel header = new GradientPanel(c1, c2);
        header.setLayout(new BorderLayout());
        header.setPreferredSize(new Dimension(100, 70));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(TITLE_FONT);
        titleLbl.setForeground(Color.WHITE);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLbl.setForeground(new Color(255, 255, 255, 220));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(titleLbl);
        textPanel.add(subLbl);

        header.add(textPanel, BorderLayout.WEST);
        return header;
    }

    /** Builds a step-progress bar, e.g. [Movie] -> [Seats] -> [Payment] -> [Confirmation]. */
    public static JPanel createStepIndicator(String[] steps, int activeIndex) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        for (int i = 0; i < steps.length; i++) {
            JLabel circle = new JLabel(String.valueOf(i + 1), SwingConstants.CENTER);
            circle.setPreferredSize(new Dimension(26, 26));
            circle.setOpaque(true);
            circle.setFont(new Font("Segoe UI", Font.BOLD, 12));
            if (i < activeIndex) {
                circle.setBackground(GREEN);
                circle.setForeground(Color.WHITE);
                circle.setText("\u2713");
            } else if (i == activeIndex) {
                circle.setBackground(new Color(124, 58, 237));
                circle.setForeground(Color.WHITE);
            } else {
                circle.setBackground(new Color(228, 230, 236));
                circle.setForeground(new Color(120, 120, 120));
            }
            JLabel text = new JLabel(steps[i]);
            text.setFont(i == activeIndex ? new Font("Segoe UI", Font.BOLD, 13) : NORMAL_FONT);

            JPanel stepBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            stepBox.setOpaque(false);
            stepBox.add(circle);
            stepBox.add(text);
            panel.add(stepBox);
        }
        return panel;
    }

    public static JLabel sectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 16));
        return l;
    }
}