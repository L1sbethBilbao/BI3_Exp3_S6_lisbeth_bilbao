package com.duoc.cinesmagenta.presentation.view;

///// Lisbeth_Bilbao_Semana6

import com.duoc.cinesmagenta.application.dto.MovieResponse;
import com.duoc.cinesmagenta.presentation.controller.MovieController;
import org.junit.jupiter.api.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MovieListPanelTest {

    // Subclase testeable: captura mensajes y permite simular respuesta de confirmación
    static class TestableMovieListPanel extends MovieListPanel {
        String lastMsg, lastTitle;
        int lastType = -1;
        int nextConfirm = JOptionPane.YES_OPTION; // configurable en cada test

        TestableMovieListPanel(MovieController c) { super(c); }

        @Override
        protected void showMessage(String msg) {
            this.lastMsg = msg; this.lastTitle = null; this.lastType = -1;
        }

        @Override
        protected void showMessage(String msg, String title, int type) {
            this.lastMsg = msg; this.lastTitle = title; this.lastType = type;
        }

        @Override
        protected int confirm(String msg, String title, int optionType) {
            // Guarda el último mensaje de confirmación por si quieres asertarlo
            this.lastMsg = msg; this.lastTitle = title; this.lastType = optionType;
            return nextConfirm;
        }
    }

    private MovieController controller;
    private TestableMovieListPanel panel;

    private static MovieResponse movie(int id, String title) {
        MovieResponse r = new MovieResponse();
        r.id = id; r.title = title; r.director = "X"; r.year = 2000; r.durationMinutes = 100; r.genre = "ACCION";
        return r;
    }

    @BeforeEach
    void setUp() throws Exception {
        controller = mock(MovieController.class);

        // Datos iniciales para loadAll() del constructor
        List<MovieResponse> initial = List.of(movie(1, "Uno"));
        when(controller.listAll()).thenReturn(initial);

        SwingUtilities.invokeAndWait(() -> {
            try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch (Exception ignored) {}
            panel = new TestableMovieListPanel(controller);
        });
    }

    @AfterEach
    void tearDown() {
        panel = null;
    }

    @Test
    void constructor_llama_loadAll_y_puebla_tabla() throws Exception {
        JTable table = findDescendant(panel, JTable.class);
        assertNotNull(table, "Debe existir JTable");
        assertEquals(1, table.getRowCount(), "Carga inicial con 1 fila");
        verify(controller, times(1)).listAll();
    }

    @Test
    void buscar_vacio_llama_listAll_de_nuevo() throws Exception {
        JButton btnBuscar = findButtonByText(panel, "Buscar");
        assertNotNull(btnBuscar);

        // deja el txtSearch vacío (ya lo está por defecto), y clic
        SwingUtilities.invokeAndWait(btnBuscar::doClick);

        verify(controller, times(2)).listAll(); // 1 del constructor + 1 de onSearch vacío
        verify(controller, never()).search(anyString());
    }

    @Test
    void buscar_con_texto_llama_search_y_actualiza_tabla() throws Exception {
        JButton btnBuscar = findButtonByText(panel, "Buscar");
        JTextField txtSearch = findDescendant(panel, JTextField.class);

        List<MovieResponse> results = List.of(movie(2, "Dos"), movie(3, "Tres"));
        when(controller.search(eq("ma"))).thenReturn(results);

        SwingUtilities.invokeAndWait(() -> {
            txtSearch.setText("ma");
            btnBuscar.doClick();
        });

        verify(controller, times(1)).search("ma");

        JTable table = findDescendant(panel, JTable.class);
        assertEquals(2, table.getRowCount(), "Debe cargar 2 resultados");
    }

    @Test
    void saveChanges_sin_cambios_muestra_mensaje_y_no_invoca_update() throws Exception {
        JButton btnSave = findButtonByText(panel, "Guardar cambios");
        SwingUtilities.invokeAndWait(btnSave::doClick);

        assertEquals("No hay cambios para guardar.", panel.lastMsg);
        verify(controller, never()).update(any());
    }

    @Test
    void delete_sin_seleccion_muestra_mensaje() throws Exception {
        JButton btnDelete = findButtonByText(panel, "Eliminar seleccionado");

        JTable table = findDescendant(panel, JTable.class);
        SwingUtilities.invokeAndWait(() -> {
            table.clearSelection();
            btnDelete.doClick();
        });

        assertEquals("Selecciona una fila.", panel.lastMsg);
        verify(controller, never()).delete(anyInt());
    }

    @Test
    void delete_con_seleccion_y_confirm_NO_no_elimina() throws Exception {
        JButton btnDelete = findButtonByText(panel, "Eliminar seleccionado");
        JTable table = findDescendant(panel, JTable.class);

        // Selecciona la primera fila
        SwingUtilities.invokeAndWait(() -> {
            table.setRowSelectionInterval(0, 0);
            panel.nextConfirm = JOptionPane.NO_OPTION; // simula que el usuario cancela
            btnDelete.doClick();
        });

        verify(controller, never()).delete(anyInt());
    }

    @Test
    void delete_con_seleccion_y_confirm_SI_elimina_y_recarga() throws Exception {
        JButton btnDelete = findButtonByText(panel, "Eliminar seleccionado");
        JTable table = findDescendant(panel, JTable.class);

        when(controller.delete(1)).thenReturn(true);
        // tras eliminar, loadAll() se vuelve a llamar; define nueva data:
        when(controller.listAll()).thenReturn(List.of()); // lista vacía post-eliminación

        SwingUtilities.invokeAndWait(() -> {
            table.setRowSelectionInterval(0, 0);
            panel.nextConfirm = JOptionPane.YES_OPTION;
            btnDelete.doClick();
        });

        verify(controller).delete(1);
        assertTrue(panel.lastMsg.contains("Eliminado id=1"));
        // listAll(): 1 en constructor + 1 después de eliminar
        verify(controller, times(2)).listAll();
    }

    @Test
    void delete_con_seleccion_pero_delete_false_muestra_warning() throws Exception {
        JButton btnDelete = findButtonByText(panel, "Eliminar seleccionado");
        JTable table = findDescendant(panel, JTable.class);

        when(controller.delete(1)).thenReturn(false);

        SwingUtilities.invokeAndWait(() -> {
            table.setRowSelectionInterval(0, 0);
            panel.nextConfirm = JOptionPane.YES_OPTION;
            btnDelete.doClick();
        });

        assertTrue(panel.lastMsg.contains("No se pudo eliminar id=1"));
        assertEquals("Atención", panel.lastTitle);
        assertEquals(JOptionPane.WARNING_MESSAGE, panel.lastType);
    }

    @Test
    void loadAll_exception_muestra_error() throws Exception {
        MovieController badController = mock(MovieController.class);
        when(badController.listAll()).thenThrow(new RuntimeException("falló"));

        AtomicReference<TestableMovieListPanel> ref = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> ref.set(new TestableMovieListPanel(badController)));

        assertTrue(ref.get().lastMsg.contains("Error cargando lista: falló"));
        assertEquals("Error", ref.get().lastTitle);
        assertTrue(
                ref.get().lastType == JOptionPane.ERROR_MESSAGE || ref.get().lastType == JOptionPane.PLAIN_MESSAGE,
                "Se esperaba ERROR_MESSAGE o PLAIN_MESSAGE, pero fue: " + ref.get().lastType
        );
    }

    // -------- helpers --------

    @SuppressWarnings("unchecked")
    private static <T extends Component> T findDescendant(Container root, Class<T> type) {
        for (Component c : root.getComponents()) {
            if (type.isInstance(c)) return (T) c;
            if (c instanceof Container child) {
                T f = findDescendant(child, type);
                if (f != null) return f;
            }
        }
        return null;
    }

    private static JButton findButtonByText(Container root, String text) {
        for (Component c : root.getComponents()) {
            if (c instanceof JButton b && text.equals(b.getText())) return b;
            if (c instanceof Container child) {
                JButton f = findButtonByText(child, text);
                if (f != null) return f;
            }
        }
        return null;
    }
}
