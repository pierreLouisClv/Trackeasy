package com.trackeasy.app.dao;

import com.trackeasy.app.Database;
import com.trackeasy.app.entities.Vehicle;
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
}
