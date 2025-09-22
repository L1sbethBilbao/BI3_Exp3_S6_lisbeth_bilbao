package com.duoc.cinesmagenta.presentation.view;

/////Lisbeth_Bilbao_Semana6

import com.duoc.cinesmagenta.application.dto.MovieRequest;
import com.duoc.cinesmagenta.application.dto.MovieResponse;
import com.duoc.cinesmagenta.presentation.controller.MovieController;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

public class MovieListPanel extends JPanel {
    private final MovieController controller;
    private final MovieTableModel model = new MovieTableModel();
    private final JTable table = new JTable(model);
    private final JTextField txtSearch = new JTextField(22);

    public MovieListPanel(MovieController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());

        // Top bar (buscar + acciones)
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Buscar título:"));
        top.add(txtSearch);
        JButton btnSearch = new JButton("Buscar");
        JButton btnRefresh = new JButton("Refrescar");
        JButton btnSave = new JButton("Guardar cambios");
        JButton btnDelete = new JButton("Eliminar seleccionado");
        top.add(btnSearch);
        top.add(btnRefresh);
        top.add(btnSave);
        top.add(btnDelete);
        add(top, BorderLayout.NORTH);

        // Tabla
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        // Editor para género
        String[] genres = {"ACCION","DRAMA","COMEDIA","AVENTURA","ANIMACION","CIENCIA_FICCION","TERROR","ROMANCE"};
        TableColumn genreCol = table.getColumnModel().getColumn(5);
        genreCol.setCellEditor(new javax.swing.DefaultCellEditor(new JComboBox<>(genres)));

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Acciones
        btnRefresh.addActionListener(e -> loadAll());
        btnSearch.addActionListener(e -> onSearch());
        btnSave.addActionListener(e -> onSaveChanges());
        btnDelete.addActionListener(e -> onDelete());

        // Carga inicial
        loadAll();
    }

    private void loadAll() {
        try {
            List<MovieResponse> list = controller.listAll();
            model.setData(list);
        } catch (Exception ex) {
            //JOptionPane.showMessageDialog(this, "Error cargando lista: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            showMessage("Error cargando lista: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onSearch() {
        try {
            String like = txtSearch.getText().trim();
            List<MovieResponse> list = like.isEmpty() ? controller.listAll() : controller.search(like);
            model.setData(list);
        } catch (Exception ex) {
            //JOptionPane.showMessageDialog(this, "Error buscando: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            showMessage("Error buscando: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

        }
    }

    private void onSaveChanges() {
        try {
            var dirtyIdx = model.dirtyRows();
            if (dirtyIdx.isEmpty()) {
                //JOptionPane.showMessageDialog(this, "No hay cambios para guardar.");
                showMessage("No hay cambios para guardar.");
                return;
            }
            List<MovieResponse> toUpdate = new ArrayList<>();
            for (Integer row : dirtyIdx) toUpdate.add(model.getAt(row));

            int ok = 0, fail = 0;
            for (MovieResponse r : toUpdate) {
                try {
                    MovieRequest req = new MovieRequest();
                    req.id = r.id;
                    req.title = r.title;
                    req.director = r.director;
                    req.year = r.year;
                    req.durationMinutes = r.durationMinutes;
                    req.genre = r.genre; // debe coincidir exacto con el ENUM
                    boolean done = controller.update(req);
                    if (done) ok++; else fail++;
                } catch (Exception ex) {
                    fail++;
                }
            }
            //JOptionPane.showMessageDialog(this, "Actualizados: " + ok + " | Fallidos: " + fail);
            showMessage("Actualizados: " + ok + " | Fallidos: " + fail);
            loadAll();
        } catch (Exception ex) {
            //JOptionPane.showMessageDialog(this, "Error guardando cambios: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            showMessage("Error guardando cambios: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            //JOptionPane.showMessageDialog(this, "Selecciona una fila.");
            showMessage("Selecciona una fila.");
            return;
        }
        int modelRow = table.convertRowIndexToModel(row);
        MovieResponse r = model.getAt(modelRow);

        int confirm = confirm("¿Eliminar la película id=" + r.id + " (" + r.title + ")?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        /*
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar la película id=" + r.id + " (" + r.title + ")?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);*/
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            boolean ok = controller.delete(r.id);
            if (ok) {
                //JOptionPane.showMessageDialog(this, "Eliminado id=" + r.id);
                showMessage("Eliminado id=" + r.id);
                loadAll();
            } else {
                //JOptionPane.showMessageDialog(this, "No se pudo eliminar id=" + r.id, "Atención", JOptionPane.WARNING_MESSAGE);
                showMessage("No se pudo eliminar id=" + r.id, "Atención", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            //JOptionPane.showMessageDialog(this, "Error eliminando: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            showMessage("Error eliminando: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }
    protected void showMessage(String msg, String title, int type) {
        JOptionPane.showMessageDialog(this, msg, title, type);
    }
    protected int confirm(String msg, String title, int optionType) {
        return JOptionPane.showConfirmDialog(this, msg, title, optionType);
    }
}