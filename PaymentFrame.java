import javax.swing.*;
import java.awt.*;

public class PaymentFrame extends JFrame {

    public PaymentFrame() {
        setTitle("Payment");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new BorderLayout());
        top.add(UIComponents.createHeader("Payment", "THEATRE - Seat Booking System",
                UIComponents.PAYMENT_C1, UIComponents.PAYMENT_C2), BorderLayout.NORTH);
        top.add(UIComponents.createStepIndicator(
                new String[]{"Movie", "Seats", "Booking Details", "Payment", "Confirmation"}, 3), BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        Showtime s = SessionData.currentShowtime;
        int totalSeats = SessionData.selectedSeats.size();
        double total = totalSeats * s.ticketPrice;

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel cards = new JPanel(new GridLayout(1, 2, 20, 0));
        cards.setBackground(Color.WHITE);

        JPanel methodCard = new JPanel();
        methodCard.setLayout(new BoxLayout(methodCard, BoxLayout.Y_AXIS));
        methodCard.setBackground(new Color(249, 250, 252));
        methodCard.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JLabel mh = new JLabel("PAYMENT METHOD");
        mh.setFont(new Font("Segoe UI", Font.BOLD, 15));
        methodCard.add(mh);
        methodCard.add(Box.createVerticalStrut(10));

        ButtonGroup group = new ButtonGroup();
        String[] methods = {"UPI (GPay / PhonePe / Paytm)", "Credit / Debit Card", "Net Banking", "Wallet", "Cash (At Counter)"};
        String[] methodValues = {"UPI", "Card", "NetBanking", "Wallet", "Cash"};
        JRadioButton[] radios = new JRadioButton[methods.length];
        for (int i = 0; i < methods.length; i++) {
            radios[i] = new JRadioButton(methods[i]);
            radios[i].setBackground(new Color(249, 250, 252));
            if (i == 0) radios[i].setSelected(true);
            group.add(radios[i]);
            methodCard.add(radios[i]);
        }

        JPanel summaryCard = new JPanel();
        summaryCard.setLayout(new BoxLayout(summaryCard, BoxLayout.Y_AXIS));
        summaryCard.setBackground(new Color(249, 250, 252));
        summaryCard.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JLabel sh = new JLabel("PAYMENT SUMMARY");
        sh.setFont(new Font("Segoe UI", Font.BOLD, 15));
        summaryCard.add(sh);
        summaryCard.add(Box.createVerticalStrut(10));
        summaryCard.add(new JLabel("Movie: " + s.movieName));
        summaryCard.add(new JLabel("Date: " + s.showDate));
        summaryCard.add(new JLabel("Time: " + s.showTime));
        summaryCard.add(new JLabel("Screen: " + s.screen));
        summaryCard.add(new JLabel("Seats: " + String.join(", ", SessionData.selectedSeats)));
        JLabel totalLbl = new JLabel("Total Amount: \u20B9" + String.format("%.2f", total));
        totalLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        summaryCard.add(Box.createVerticalStrut(10));
        summaryCard.add(totalLbl);

        cards.add(methodCard);
        cards.add(summaryCard);
        content.add(cards, BorderLayout.CENTER);

        JLabel errorLbl = new JLabel(" ", SwingConstants.CENTER);
        errorLbl.setForeground(UIComponents.RED);

        UIComponents.GradientButton payBtn = new UIComponents.GradientButton(
                "\uD83D\uDD12 PAY NOW \u20B9" + String.format("%.2f", total),
                UIComponents.PAYMENT_C1, UIComponents.PAYMENT_C2);
        payBtn.setPreferredSize(new Dimension(220, 45));

        payBtn.addActionListener(e -> {
            String selectedMethod = "UPI";
            for (int i = 0; i < radios.length; i++) {
                if (radios[i].isSelected()) selectedMethod = methodValues[i];
            }
            try {
                String code = BookingService.insertBooking(SessionData.username, s.showtimeId,
                        SessionData.selectedSeats, s.ticketPrice, total, selectedMethod);
                SessionData.lastBookingCode = code;
                new ConfirmationFrame().setVisible(true);
                dispose();
            } catch (Exception ex) {
                errorLbl.setText("Payment could not be processed: " + ex.getMessage());
            }
        });

        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBackground(Color.WHITE);
        errorLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        payBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottom.add(errorLbl);
        bottom.add(payBtn);
        content.add(bottom, BorderLayout.SOUTH);

        add(content, BorderLayout.CENTER);
    }
}