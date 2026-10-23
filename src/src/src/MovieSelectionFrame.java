import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MovieSelectionFrame extends JFrame {

    private List<Showtime> showtimes;
    private JTable table;

    public MovieSelectionFrame() {
        setTitle("Movie Selection");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new BorderLayout());
        top.add(UIComponents.createHeader("THEATRE", "Seat Booking System",
                UIComponents.MOVIE_C1, UIComponents.MOVIE_C2), BorderLayout.NORTH);
        top.add(UIComponents.createStepIndicator(
                new String[]{"Movie", "Seats", "Payment", "Confirmation"}, 0), BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        content.setBackground(Color.WHITE);
        content.add(UIComponents.sectionTitle("SELECT MOVIE"), BorderLayout.NORTH);

        String[] cols = {"Movie Name", "Language", "Genre", "Duration", "Date", "Time", "Screen"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        loadMovies(model);

        content.add(new JScrollPane(table), BorderLayout.CENTER);

        JLabel errorLbl = new JLabel(" ");
        errorLbl.setForeground(UIComponents.RED);
        errorLbl.setHorizontalAlignment(SwingConstants.CENTER);

        UIComponents.GradientButton proceedBtn = new UIComponents.GradientButton(
                "PROCEED \u2192", UIComponents.MOVIE_C1, UIComponents.MOVIE_C2);
        proceedBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                errorLbl.setText("Please select a movie.");
                return;
            }
            SessionData.currentShowtime = showtimes.get(row);
            new SeatSelectionFrame().setVisible(true);
            dispose();
        });

        JPanel bottom = new JPanel();
        bottom.setBackground(Color.WHITE);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        errorLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        proceedBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottom.add(errorLbl);
        bottom.add(Box.createVerticalStrut(8));
        bottom.add(proceedBtn);

        content.add(bottom, BorderLayout.SOUTH);
        add(content, BorderLayout.CENTER);
    }

    private void loadMovies(DefaultTableModel model) {
        try {
            showtimes = ShowtimeService.getOneShowtimePerMovie();
            for (Showtime s : showtimes) {
                model.addRow(new Object[]{s.movieName, s.language, s.genre, s.duration,
                        s.showDate, s.showTime, s.screen});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}