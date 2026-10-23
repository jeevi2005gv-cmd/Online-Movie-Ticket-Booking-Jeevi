import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.PrinterException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookingReportFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField fromField, toField;
    private JComboBox<String> movieBox;
    private final List<Integer> movieIds = new ArrayList<>();

    public BookingReportFrame() {
        setTitle("Booking Report");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(UIComponents.createHeader("Booking Report", "THEATRE - Seat Booking System",
                UIComponents.REPORT_C1, UIComponents.REPORT_C2), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(20, 0));
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel filters = new JPanel();
        filters.setLayout(new BoxLayout(filters, BoxLayout.Y_AXIS));
        filters.setBackground(new Color(249, 250, 252));
        filters.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        filters.setPreferredSize(new Dimension(220, 300));

        JLabel fh = new JLabel("FILTERS");
        fh.setFont(new Font("Segoe UI", Font.BOLD, 15));

        fromField = new JTextField("2026-01-01");
        toField = new JTextField("2026-12-31");
        movieBox = new JComboBox<>();
        movieBox.addItem("All Movies");
        loadMovies();

        UIComponents.GradientButton viewBtn = new UIComponents.GradientButton(
                "VIEW REPORT", UIComponents.BLUE, UIComponents.REPORT_C2);
        viewBtn.addActionListener(e -> loadReport());

        filters.add(fh);
        filters.add(Box.createVerticalStrut(10));
        filters.add(new JLabel("From Date (yyyy-mm-dd)"));
        filters.add(fromField);
        filters.add(Box.createVerticalStrut(8));
        filters.add(new JLabel("To Date (yyyy-mm-dd)"));
        filters.add(toField);
        filters.add(Box.createVerticalStrut(8));
        filters.add(new JLabel("Movie"));
        filters.add(movieBox);
        filters.add(Box.createVerticalStrut(15));
        filters.add(viewBtn);

        content.add(filters, BorderLayout.WEST);

        JPanel reportPanel = new JPanel(new BorderLayout());
        reportPanel.setBackground(Color.WHITE);
        JLabel rh = new JLabel("BOOKING REPORT");
        rh.setFont(new Font("Segoe UI", Font.BOLD, 15));
        reportPanel.add(rh, BorderLayout.NORTH);

        String[] cols = {"Booking ID", "Movie", "Date", "Time", "Screen", "Seats", "Amount (\u20B9)", "Status"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(26);
        reportPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        actions.setBackground(Color.WHITE);
        UIComponents.GradientButton exportBtn = new UIComponents.GradientButton(
                "\uD83D\uDCBE EXPORT TO CSV", UIComponents.GREEN, new Color(52, 201, 141));
        exportBtn.addActionListener(e -> exportCsv());

        UIComponents.GradientButton printBtn = new UIComponents.GradientButton(
                "\uD83D\uDDA8 PRINT REPORT", UIComponents.DASH_C1, UIComponents.DASH_C2);
        printBtn.addActionListener(e -> {
            try {
                table.print();
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Print failed: " + ex.getMessage());
            }
        });

        actions.add(exportBtn);
        actions.add(printBtn);
        reportPanel.add(actions, BorderLayout.SOUTH);

        content.add(reportPanel, BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);

        loadReport();
    }

    private void loadMovies() {
        try {
            for (Document m : MovieService.getAllMovies()) {
                movieBox.addItem(m.getString("movieName"));
                movieIds.add(m.getInteger("movieId"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadReport() {
        model.setRowCount(0);
        String from = fromField.getText().trim();
        String to = toField.getText().trim();
        int movieIndex = movieBox.getSelectedIndex();
        Integer movieId = movieIndex > 0 ? movieIds.get(movieIndex - 1) : null;

        try {
            List<BookingRecord> rows = BookingService.getReport(from, to, movieId);
            for (BookingRecord r : rows) {
                model.addRow(new Object[]{r.bookingCode, r.movieName, r.showDate, r.showTime, r.screen,
                        r.seatNumbers, String.format("%.2f", r.totalAmount), r.status});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("booking_report.csv"));
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        try (FileWriter fw = new FileWriter(chooser.getSelectedFile())) {
            for (int c = 0; c < model.getColumnCount(); c++) {
                fw.write(model.getColumnName(c) + (c < model.getColumnCount() - 1 ? "," : "\n"));
            }
            for (int r = 0; r < model.getRowCount(); r++) {
                for (int c = 0; c < model.getColumnCount(); c++) {
                    fw.write(String.valueOf(model.getValueAt(r, c)));
                    fw.write(c < model.getColumnCount() - 1 ? "," : "\n");
                }
            }
            JOptionPane.showMessageDialog(this, "Report exported successfully.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
        }
    }
}