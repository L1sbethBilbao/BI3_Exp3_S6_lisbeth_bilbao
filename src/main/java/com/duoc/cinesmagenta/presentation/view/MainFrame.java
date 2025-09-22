package com.duoc.cinesmagenta.presentation.view;

/////Lisbeth_Bilbao_Semana6

import com.duoc.cinesmagenta.presentation.controller.MovieController;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame(MovieController controller) {
        super("Cine Magenta - Cartelera");
        setMinimumSize(new Dimension(960, 640));        // evita que se haga muy chico
        setPreferredSize(new Dimension(1200, 800));     // tamaño cómodo por defecto
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(720, 480);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JToolBar tb = new JToolBar();
        tb.setFloatable(false);             // opcional: fija la toolbar
        JButton btnAgregar = new JButton("Agregar");
        JButton btnListar = new JButton("Listar");
        btnAgregar.setFocusable(false);     // opcional: evita foco del botón
        btnListar.setFocusable(false);
        tb.add(btnAgregar);
        tb.add(btnListar);
        add(tb, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        add(content, BorderLayout.CENTER);

        // ⬇️ OPCIONAL (recomendado): mostrar el formulario al abrir
        content.add(new MovieFormPanel(controller), BorderLayout.CENTER);

        // Handlers
        btnAgregar.addActionListener(e -> {
            content.removeAll();
            content.add(new MovieFormPanel(controller), BorderLayout.CENTER);
            content.revalidate();
            content.repaint();
        });

        btnListar.addActionListener(e -> {
            content.removeAll();
            content.add(new MovieListPanel(controller), BorderLayout.CENTER);
            content.revalidate();
            content.repaint();
        });
    }
}