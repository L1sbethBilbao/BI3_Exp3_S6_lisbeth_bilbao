package com.duoc.cinesmagenta.infrastructure.jdbc;

///// Lisbeth_Bilbao_Semana6

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ConnectionFactoryTest {

    @Test
    void ping_select1_ok_conMock() throws Exception {
        // Mocks de JDBC
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        // Stubs de comportamiento
        when(conn.prepareStatement("SELECT 1")).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);        // hay una fila
        when(rs.getInt(1)).thenReturn(1);        // primera columna = 1

        // Interceptar DriverManager.getConnection(...) para devolver nuestro conn mock
        try (MockedStatic<DriverManager> dm = mockStatic(DriverManager.class)) {
            dm.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(conn);

            // Código bajo prueba: NO tocar ConnectionFactory
            try (Connection c = ConnectionFactory.get();
                 PreparedStatement _ps = c.prepareStatement("SELECT 1");
                 ResultSet _rs = _ps.executeQuery()) {

                _rs.next();
                assertEquals(1, _rs.getInt(1));
            }

            // (Opcional) verificaciones
            dm.verify(() -> DriverManager.getConnection(anyString(), anyString(), anyString()));
            verify(conn).prepareStatement("SELECT 1");
            verify(ps).executeQuery();
            verify(rs).next();
            verify(rs).getInt(1);
        }
    }
}
