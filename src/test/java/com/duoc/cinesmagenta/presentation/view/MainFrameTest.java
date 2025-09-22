package com.duoc.cinesmagenta.presentation.view;

///// Lisbeth_Bilbao_Semana6

import com.duoc.cinesmagenta.presentation.controller.MovieController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class MainFrameTest {

    private MainFrame frame;
    private MovieController controller;

    @BeforeEach
    void setUp() throws Exception {
        // Mock del controller para que MovieListPanel no toque datos reales
        controller = mock(MovieController.class);
        when(controller.listAll()).thenReturn(Collections.emptyList());
        when(controller.search(anyString())).thenReturn(Collections.emptyList());

        // Crear UI en EDT; no hacemos setVisible(true)
        SwingUtilities.invokeAndWait(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {}
            frame = new MainFrame(controller);
        });
    }

    @AfterEach
    void tearDown() throws Exception {
        if (frame != null) {
            SwingUtilities.invokeAndWait(() -> frame.dispose());
        }
    }

    @Test
    void arranca_mostrando_MovieFormPanel() throws Exception {
        AtomicReference<JComponent> found = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            found.set(findDescendant(frame.getContentPane(), MovieFormPanel.class));
        });
        assertNotNull(found.get(), "Debe mostrarse MovieFormPanel al iniciar");
    }

    @Test
    void toolbar_y_botones_existen() throws Exception {
        AtomicReference<JToolBar> tbRef = new AtomicReference<>();
        AtomicReference<JButton> agregarRef = new AtomicReference<>();
        AtomicReference<JButton> listarRef = new AtomicReference<>();

        SwingUtilities.invokeAndWait(() -> {
            JToolBar tb = findDescendant(frame.getContentPane(), JToolBar.class);
            tbRef.set(tb);
            agregarRef.set(findButtonByText(tb, "Agregar"));
            listarRef.set(findButtonByText(tb, "Listar"));
        });

        assertNotNull(tbRef.get(), "Debe existir la toolbar");
        assertNotNull(agregarRef.get(), "Debe existir botón Agregar");
        assertNotNull(listarRef.get(), "Debe existir botón Listar");
    }

    @Test
    void al_hacer_click_en_Listar_cambia_a_MovieListPanel_y_luego_vuelve_con_Agregar() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JToolBar tb = findDescendant(frame.getContentPane(), JToolBar.class);
            JButton btnListar = findButtonByText(tb, "Listar");
            JButton btnAgregar = findButtonByText(tb, "Agregar");
            assertNotNull(btnListar);
            assertNotNull(btnAgregar);

            // Ir a lista
            btnListar.doClick();
            assertNotNull(findDescendant(frame.getContentPane(), MovieListPanel.class),
                    "Debe mostrarse MovieListPanel tras 'Listar'");

            // Volver a formulario
            btnAgregar.doClick();
            assertNotNull(findDescendant(frame.getContentPane(), MovieFormPanel.class),
                    "Debe mostrarse MovieFormPanel tras 'Agregar'");
        });
    }

    // ------------ helpers ------------

    /** Busca recursivamente un descendiente del tipo dado. */
    @SuppressWarnings("unchecked")
    private static <T extends Component> T findDescendant(Container root, Class<T> type) {
        for (Component c : root.getComponents()) {
            if (type.isInstance(c)) return (T) c;
            if (c instanceof Container) {
                T found = findDescendant((Container) c, type);
                if (found != null) return found;
            }
        }
        return null;
    }

    /** Busca un JButton por su texto dentro de un contenedor. */
    private static JButton findButtonByText(Container root, String text) {
        for (Component c : root.getComponents()) {
            if (c instanceof JButton b && text.equals(b.getText())) return b;
            if (c instanceof Container child) {
                JButton found = findButtonByText(child, text);
                if (found != null) return found;
            }
        }
        return null;
    }
}
