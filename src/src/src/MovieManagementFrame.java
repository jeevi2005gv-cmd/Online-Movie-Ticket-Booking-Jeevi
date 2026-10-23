import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MovieManagementFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    public MovieManagementFrame() {
        setTitle("Movie Management");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(UIComponents.createHeader("Movie Management", "THEATRE - Admin Panel",
                UIComponents.DASH_C1, UIComponents.DASH_C2), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        String[] cols = {"ID", "Movie Name", "Language", "Genre", "Duration"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(26);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        content.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbar.setBackground(Color.WHITE);

        UIComponents.GradientButton addBtn = new UIComponents.GradientButton("+ ADD MOVIE", UIComponents.GREEN, new Color(52, 201, 141));
        UIComponents.GradientButton editBtn = new UIComponents.GradientButton("EDIT", UIComponents.BLUE, UIComponents.REPORT_C2);
        UIComponents.GradientButton deleteBtn = new UIComponents.GradientButton("DELETE", UIComponents.RED, new Color(200, 40, 40));
        JButton refreshBtn = new JButton("REFRESH");

        addBtn.addActionListener(e -> openForm(null));
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a movie to edit."); return; }
            openForm((Integer) model.getValueAt(row, 0));
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a movie to delete."); return; }
            int id = (Integer) model.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Delete this movie?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    if (!MovieService.deleteMovie(id)) {
                        JOptionPane.showMessageDialog(this,
                                "Cannot delete: this movie has showtimes linked to it.\n" +
                                "Delete its showtimes first in Show Time Management.");
                    }
                    loadMovies();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        refreshBtn.addActionListener(e -> loadMovies());

        toolbar.add(addBtn);
        toolbar.add(editBtn);
        toolbar.add(deleteBtn);
        toolbar.add(refreshBtn);
        content.add(toolbar, BorderLayout.SOUTH);

        add(content, BorderLayout.CENTER);
        loadMovies();
    }

    private void loadMovies() {
        model.setRowCount(0);
        try {
            for (Document m : MovieService.getAllMovies()) {
                model.addRow(new Object[]{m.getInteger("movieId"), m.getString("movieName"),
                        m.getString("language"), m.getString("genre"), m.getString("duration")});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openForm(Integer id) {
        JTextField nameF = new JTextField();
        JTextField langF = new JTextField("Tamil");
        JTextField genreF = new JTextField();
        JTextField durF = new JTextField("2D");

        if (id != null) {
            Document m = MovieService.getMovieById(id);
            if (m != null) {
                nameF.setText(m.getString("movieName"));
                langF.setText(m.getString("language"));
                genreF.setText(m.getString("genre"));
                durF.setText(m.getString("duration"));
            }
        }

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.add(new JLabel("Movie Name:")); form.add(nameF);
        form.add(new JLabel("Language:")); form.add(langF);
        form.add(new JLabel("Genre:")); form.add(genreF);
        form.add(new JLabel("Duration:")); form.add(durF);

        int result = JOptionPane.showConfirmDialog(this, form,
                id == null ? "Add Movie" : "Edit Movie", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            if (nameF.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Movie name is required.");
                return;
            }
            try {
                if (id == null) {
                    MovieService.insertMovie(nameF.getText().trim(), langF.getText().trim(),
                            genreF.getText().trim(), durF.getText().trim());
                } else {
                    MovieService.updateMovie(id, nameF.getText().trim(), langF.getText().trim(),
                            genreF.getText().trim(), durF.getText().trim());
                }
                loadMovies();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}