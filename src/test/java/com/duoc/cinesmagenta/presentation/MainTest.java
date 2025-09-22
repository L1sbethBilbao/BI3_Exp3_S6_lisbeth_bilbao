package com.duoc.cinesmagenta.presentation;

///// Lisbeth_Bilbao_Semana6

import com.duoc.cinesmagenta.presentation.view.MainFrame;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import javax.swing.SwingUtilities;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;

class MainTest {

    @Test
    void testMainRunsWithoutExceptions() {
        // 1) Interceptar SwingUtilities.invokeLater para ejecutar inline (o NO ejecutar)
        try (MockedStatic<SwingUtilities> swingMock = mockStatic(SwingUtilities.class)) {
            swingMock.when(() -> SwingUtilities.invokeLater(any(Runnable.class)))
                    .thenAnswer(inv -> {
                        // Opción A: Ejecutarlo inline (si mockeamos constructor, no hay UI real)
                        Runnable r = inv.getArgument(0);
                        r.run();
                        return null;

                        // Opción B (aún más segura): NO ejecutar el runnable y solo verificar que no lanza
                        // return null;
                    });

            // 2) Interceptar la construcción del JFrame para que NO corra el constructor real
            try (MockedConstruction<MainFrame> frameMock =
                         mockConstruction(MainFrame.class, (mock, context) -> {
                             // Evitar trabajo en setVisible
                             doNothing().when(mock).setVisible(true);
                         })) {

                assertDoesNotThrow(() -> Main.main(new String[]{}));


            }
        }
    }
}
