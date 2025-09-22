package com.duoc.cinesmagenta.infrastructure.jdbc;

/////Lisbeth_Bilbao_Semana6

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class ConnectionFactory {
    private static String URL;
    private static String USER;
    private static String PASS;

    static {
        try (InputStream in = ConnectionFactory.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties p = new Properties();
            p.load(in);
            URL = p.getProperty("db.url");
            USER = p.getProperty("db.user");
            PASS = p.getProperty("db.password");
        } catch (Exception e) {
            throw new RuntimeException("No se pudo cargar db.properties", e);
        }
    }

    public static Connection get() throws Exception {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}