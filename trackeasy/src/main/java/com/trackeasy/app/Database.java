package com.trackeasy.app;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.List;
import java.util.Map;

public class Database {

    private static final String URL = "jdbc:sqlite:database.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite driver not found");
        }
    }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initialize() {
        try (Connection conn = connect()) {
            executeSQLScript(conn, "/Tables.sql");
            insertSampleData(conn, "/data.json");
        } catch (SQLException | IOException e) {
            System.err.println("Database initialization error: " + e.getMessage());
        }
    }

    private static void executeSQLScript(Connection conn, String resourcePath) throws IOException, SQLException {
        try (InputStream in = Database.class.getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            String[] statements = sb.toString().split(";");
            try (Statement stmt = conn.createStatement()) {
                for (String sql : statements) {
                    if (!sql.trim().isEmpty()) {
                        stmt.execute(sql);
                    }
                }
            }
        }
    }

    private static void insertSampleData(Connection conn, String resourcePath) throws IOException, SQLException {
        Gson gson = new Gson();
        try (InputStream in = Database.class.getResourceAsStream(resourcePath);
             Reader reader = new InputStreamReader(in)) {

            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> data = gson.fromJson(reader, type);

            List<Map<String, String>> persons = (List<Map<String, String>>) data.get("persons");
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO Person (PersonID, Firstname, Lastname, Id) VALUES (?, ?, ?, ?)")) {
                for (Map<String, String> person : persons) {
                    ps.setString(1, person.get("PersonID"));
                    ps.setString(2, person.get("Firstname"));
                    ps.setString(3, person.get("Lastname"));
                    ps.setString(4, person.get("Id"));
                    ps.executeUpdate();
                }
            }

            insertRoles(conn, "Fleetmanager", "FleetmanagerID", (List<String>) data.get("fleetmanagers"));
            insertRoles(conn, "Technician", "TechnicianID", (List<String>) data.get("technicians"));
            insertRoles(conn, "Conductor", "ConductorID", (List<String>) data.get("conductors"));
        }
    }

    private static void insertRoles(Connection conn, String table, String idField, List<String> ids) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO " + table + " (" + idField + ") VALUES (?)")) {
            for (String id : ids) {
                ps.setString(1, id);
                ps.executeUpdate();
            }
        }
    }
}
