import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ShowTimeManagementFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private final List<Integer> movieIds = new ArrayList<>();
    private final List<String> movieNames = new ArrayList<>();

    public ShowTimeManagementFrame() {
        setTitle("Show Time Management");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(UIComponents.createHeader("Show Time Management", "THEATRE - Admin Panel",
                UIComponents.DASH_C1, UIComponents.DASH_C2), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        String[] cols = {"ID", "Movie", "Date", "Time", "Screen", "Price"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(26);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        content.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbar.setBackground(Color.WHITE);

        UIComponents.GradientButton addBtn = new UIComponents.GradientButton("+ ADD SHOWTIME", UIComponents.GREEN, new Color(52, 201, 141));
        UIComponents.GradientButton editBtn = new UIComponents.GradientButton("EDIT", UIComponents.BLUE, UIComponents.REPORT_C2);
        UIComponents.GradientButton deleteBtn = new UIComponents.GradientButton("DELETE", UIComponents.RED, new Color(200, 40, 40));
        JButton refreshBtn = new JButton("REFRESH");

        addBtn.addActionListener(e -> openForm(null));
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a showtime to edit."); return; }
            openForm((Integer) model.getValueAt(row, 0));
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a showtime to delete."); return; }
            int id = (Integer) model.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Delete this showtime?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    if (!ShowtimeService.deleteShowtime(id)) {
                        JOptionPane.showMessageDialog(this,
                                "Cannot delete: this showtime has bookings linked to it.");
                    }
                    loadShowtimes();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        refreshBtn.addActionListener(e -> loadShowtimes());

        toolbar.add(addBtn);
        toolbar.add(editBtn);
        toolbar.add(deleteBtn);
        toolbar.add(refreshBtn);
        content.add(toolbar, BorderLayout.SOUTH);

        add(content, BorderLayout.CENTER);
        loadMovieList();
        loadShowtimes();
    }

    private void loadMovieList() {
        movieIds.clear(); movieNames.clear();
        try {
            for (Document m : MovieService.getAllMovies()) {
                movieIds.add(m.getInteger("movieId"));
                movieNames.add(m.getString("movieName"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void loadShowtimes() {
        model.setRowCount(0);
        try {
            for (Showtime s : ShowtimeService.getAllShowtimes()) {
                model.addRow(new Object[]{s.showtimeId, s.movieName, s.showDate, s.showTime, s.screen, s.ticketPrice});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openForm(Integer id) {
        if (movieIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Add a movie first in Movie Management.");
            return;
        }
        JComboBox<String> movieBox = new JComboBox<>(movieNames.toArray(new String[0]));
        JTextField dateF = new JTextField("2026-05-25");
        JTextField timeF = new JTextField("10:30 AM");
        JTextField screenF = new JTextField("Screen 1");
        JTextField priceF = new JTextField("150.00");

        if (id != null) {
            Showtime s = ShowtimeService.getShowtimeById(id);
            if (s != null) {
                int idx = movieIds.indexOf(s.movieId);
                if (idx >= 0) movieBox.setSelectedIndex(idx);
                dateF.setText(s.showDate);
                timeF.setText(s.showTime);
                screenF.setText(s.screen);
                priceF.setText(String.valueOf(s.ticketPrice));
            }
        }

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.add(new JLabel("Movie:")); form.add(movieBox);
        form.add(new JLabel("Date (yyyy-mm-dd):")); form.add(dateF);
        form.add(new JLabel("Time (e.g. 10:30 AM):")); form.add(timeF);
        form.add(new JLabel("Screen:")); form.add(screenF);
        form.add(new JLabel("Ticket Price:")); form.add(priceF);

        int result = JOptionPane.showConfirmDialog(this, form,
                id == null ? "Add Showtime" : "Edit Showtime", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            double price;
            try { price = Double.parseDouble(priceF.getText().trim()); }
            catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Price must be a number."); return; }

            int selectedMovieId = movieIds.get(movieBox.getSelectedIndex());

            try {
                if (id == null) {
                    ShowtimeService.insertShowtime(selectedMovieId, dateF.getText().trim(),
                            timeF.getText().trim(), screenF.getText().trim(), price);
                } else {
                    ShowtimeService.updateShowtime(id, selectedMovieId, dateF.getText().trim(),
                            timeF.getText().trim(), screenF.getText().trim(), price);
                }
                loadShowtimes();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}