package com.trackeasy.app.dao;

import com.trackeasy.app.Database;
import com.trackeasy.app.entities.Conductor;
import com.trackeasy.app.entities.Person;
import com.trackeasy.app.entities.Vehicle;
import com.trackeasy.app.utils.Constants;

import java.sql.*;
import java.util.*;

public class VehicleDAO {

    public static List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        try (Connection conn = Database.connect()) {
            String query = """
                    SELECT v.VehicleID, v.Brand, v.Color, v.Location, v.ConductorID, COUNT(t.TrackerID) as TrackerCount
                    FROM Vehicle v
                    LEFT JOIN Tracker t ON t.TrackerID = v.TrackerID
                    GROUP BY v.VehicleID
                    """;
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String vehicleID = rs.getString("VehicleID");
                String brand = rs.getString("Brand");
                String color = rs.getString("Color");
                String location = rs.getString("Location");
                boolean running = rs.getString("ConductorID") != null;
                boolean hasTracker = rs.getInt("TrackerCount") > 0;

                Vehicle vehicle = new Vehicle(vehicleID, brand, color, location, hasTracker, running);
                vehicles.add(vehicle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles;
    }

    public static void addVehicle(Vehicle vehicle) {
        try (Connection conn = Database.connect()) {
            String insert = "INSERT INTO Vehicle (VehicleID, Brand, Color, Location) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(insert);
            ps.setString(1, vehicle.getVehicleID());
            ps.setString(2, vehicle.getBrand());
            ps.setString(3, vehicle.getColor());
            ps.setString(4, vehicle.getLocation());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateLocation(String vehicleID, String newLocation) {
        try (Connection conn = Database.connect()) {
            String update = "UPDATE Vehicle SET Location = ? WHERE VehicleID = ?";
            PreparedStatement ps = conn.prepareStatement(update);
            ps.setString(1, newLocation);
            ps.setString(2, vehicleID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static boolean vehicleHasTracker(String vehicleID) {
        try (Connection conn = Database.connect()) {
            String query = "SELECT TrackerID FROM Vehicle WHERE VehicleID = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, vehicleID);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getString("TrackerID") != null;
        } catch (SQLException e) {
            e.printStackTrace();
            return true; // Par sécurité, on évite d'écraser un tracker
        }
    }

    public static boolean installTracker(String trackerID, String type, String technicianID, String vehicleID) {
        if (vehicleHasTracker(vehicleID)) {
            return false;
        }

        try (Connection conn = Database.connect()) {
            // Étape 1 : Ajouter le tracker
            String insertTracker = "INSERT INTO Tracker (TrackerID, Type, TechnicianID) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertTracker)) {
                ps.setString(1, trackerID);
                ps.setString(2, type);
                ps.setString(3, technicianID);
                ps.executeUpdate();
            }

            // Étape 2 : Lier le tracker au véhicule
            String updateVehicle = "UPDATE Vehicle SET TrackerID = ? WHERE VehicleID = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateVehicle)) {
                ps.setString(1, trackerID);
                ps.setString(2, vehicleID);
                ps.executeUpdate();
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Vehicle> getVehiclesWithoutTracker() {
        List<Vehicle> vehicles = new ArrayList<>();
        try (Connection conn = Database.connect()) {
            String query = """
                    SELECT VehicleID, Brand, Color, Location FROM Vehicle 
                    WHERE TrackerID IS NULL
                    """;
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Vehicle v = new Vehicle(
                    rs.getString("VehicleID"),
                    rs.getString("Brand"),
                    rs.getString("Color"),
                    rs.getString("Location"),
                    false, false
                );
                vehicles.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles;
    }

    public static boolean addTracker(String trackerID, String type, String technicianID, String vehicleID) {
        try (Connection conn = Database.connect()) {
            conn.setAutoCommit(false);

            // Insert tracker
            String insertTracker = "INSERT INTO Tracker (TrackerID, Type, TechnicianID) VALUES (?, ?, ?)";
            PreparedStatement ps1 = conn.prepareStatement(insertTracker);
            ps1.setString(1, trackerID);
            ps1.setString(2, type);
            ps1.setString(3, technicianID);
            ps1.executeUpdate();

            // Update vehicle to assign tracker
            String updateVehicle = "UPDATE Vehicle SET TrackerID = ? WHERE VehicleID = ?";
            PreparedStatement ps2 = conn.prepareStatement(updateVehicle);
            ps2.setString(1, trackerID);
            ps2.setString(2, vehicleID);
            ps2.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Vehicle> getAvailableVehiclesAtLocation(String location) {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM Vehicle WHERE ConductorID IS NULL AND Location = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, location);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Vehicle v = new Vehicle();
                v.setVehicleID(rs.getString("VehicleID"));
                v.setBrand(rs.getString("Brand"));
                v.setColor(rs.getString("Color"));
                v.setLocation(rs.getString("Location"));
                vehicles.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles;
    }

    public static void assignDriver(String conductorID, String vehicleID) {
        String sql = "UPDATE Vehicle SET ConductorID = ? WHERE VehicleID = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, conductorID);
            pstmt.setString(2, vehicleID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeDriver(String vehicleID) {
        String sql = "UPDATE Vehicle SET ConductorID = NULL WHERE VehicleID = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vehicleID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void advanceVehicleLocation(String vehicleID) {
        String getSql = "SELECT Location FROM Vehicle WHERE VehicleID = ?";
        String updateSql = "UPDATE Vehicle SET Location = ? WHERE VehicleID = ?";

        try (Connection conn = Database.connect();
             PreparedStatement getStmt = conn.prepareStatement(getSql)) {

            getStmt.setString(1, vehicleID);
            ResultSet rs = getStmt.executeQuery();
            if (rs.next()) {
                String currentLocation = rs.getString("Location");
                String nextLocation = Constants.getNextCity(currentLocation);
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, nextLocation);
                    updateStmt.setString(2, vehicleID);
                    updateStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Vehicle getVehicleDrivenBy(String conductorID) {
        String sql = "SELECT * FROM Vehicle WHERE ConductorID = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, conductorID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Vehicle v = new Vehicle();
                v.setVehicleID(rs.getString("VehicleID"));
                v.setBrand(rs.getString("Brand"));
                v.setColor(rs.getString("Color"));
                v.setLocation(rs.getString("Location"));
                return v;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Conductor> getAllConductors() {
    List<Conductor> conductors = new ArrayList<>();

    String sql = """
        SELECT p.PersonID, p.Firstname, p.Lastname, p.Id 
        FROM Person p
        INNER JOIN Conductor c ON p.PersonID = c.ConductorID
        """;

    try (Connection conn = Database.connect();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            Conductor person = new Conductor();
            person.setPersonID(rs.getString("PersonID"));
            person.setFirstname(rs.getString("Firstname"));
            person.setLastname(rs.getString("Lastname"));
            person.setId(rs.getString("Id"));
            conductors.add(person);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return conductors;
}

}
