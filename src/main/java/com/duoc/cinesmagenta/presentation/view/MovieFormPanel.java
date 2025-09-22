package com.duoc.cinesmagenta.presentation.view;


/////Lisbeth_Bilbao_Semana6

import com.duoc.cinesmagenta.application.dto.MovieRequest;
import com.duoc.cinesmagenta.presentation.controller.MovieController;

import javax.swing.*;

import java.awt.*;

public class MovieFormPanel extends JPanel {
    private final JTextField txtTitle = new JTextField(30);
    private final JTextField txtDirector = new JTextField(30);
    private final JSpinner spYear = new JSpinner(new SpinnerNumberModel(2024, 1888, 2100, 1));
    private final JSpinner spDuration = new JSpinner(new SpinnerNumberModel(90, 1, 999, 1));
    private final JComboBox<String> cboGenre = new JComboBox<>(new String[]{
            "ACCION","DRAMA","COMEDIA","AVENTURA","ANIMACION","CIENCIA_FICCION","TERROR","ROMANCE"
    });

    public MovieFormPanel(MovieController controller) {
        setLayout(new BorderLayout());

        // ----- Formulario -----
        JPanel form = new JPanel();
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel lblTitle    = new JLabel("Título:");
        JLabel lblDirector = new JLabel("Director:");
        JLabel lblYear     = new JLabel("Año:");
        JLabel lblDuration = new JLabel("Duración:");
        JLabel lblGenre    = new JLabel("Género:");


        Dimension spinSize = new Dimension(50, spYear.getPreferredSize().height);
        spYear.setEditor(new JSpinner.NumberEditor(spYear, "####"));
        spDuration.setPreferredSize(spinSize);

        GroupLayout gl = new GroupLayout(form);
        form.setLayout(gl);
        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);

        gl.setHorizontalGroup(
                gl.createSequentialGroup()
                        .addGroup(gl.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(lblTitle)
                                .addComponent(lblDirector)
                                .addComponent(lblYear)
                                .addComponent(lblDuration)
                                .addComponent(lblGenre))
                        .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING, true)
                                .addComponent(txtTitle)
                                .addComponent(txtDirector)
                                .addComponent(spYear, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(spDuration, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(cboGenre))
        );

        gl.setVerticalGroup(
                gl.createSequentialGroup()
                        .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblTitle).addComponent(txtTitle))
                        .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblDirector).addComponent(txtDirector))
                        .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblYear).addComponent(spYear))
                        .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblDuration).addComponent(spDuration))
                        .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblGenre).addComponent(cboGenre))
        );

        add(form, BorderLayout.CENTER);

        // ----- Botones -----
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave  = new JButton("Guardar");
        JButton btnClear = new JButton("Limpiar");
        actions.add(btnSave);
        actions.add(btnClear);
        add(actions, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> onSave(controller));
        btnClear.addActionListener(e -> onClear());
    }

    private void onSave(MovieController controller) {
        try {
            MovieRequest r = new MovieRequest();
            r.title = txtTitle.getText().trim();
            r.director = txtDirector.getText().trim();
            r.year = (Integer) spYear.getValue();
            r.durationMinutes = (Integer) spDuration.getValue();
            r.genre = (String) cboGenre.getSelectedItem();


            if (r.title.isEmpty() || r.director.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Completa todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            var saved = controller.create(r);
            JOptionPane.showMessageDialog(this, "Guardada id=" + saved.id, "OK", JOptionPane.INFORMATION_MESSAGE);
            onClear();
        } catch (Exception ex) {
            Throwable root = ex;
            while (root.getCause() != null) root = root.getCause();
            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + root.getClass().getSimpleName() + " - " + root.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
    }

    private void onClear() {
        txtTitle.setText("");
        txtDirector.setText("");
        spYear.setValue(2024);
        spDuration.setValue(90);
        cboGenre.setSelectedIndex(0);
        txtTitle.requestFocus();
    }
}