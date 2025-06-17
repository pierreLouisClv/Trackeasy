package com.trackeasy.app.UI;

import com.trackeasy.app.dao.VehicleDAO;
import com.trackeasy.app.entities.Conductor;
import com.trackeasy.app.entities.Vehicle;
import com.trackeasy.app.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class DrivingView extends JPanel {

    private JComboBox<Conductor> conductorComboBox;
    private JComboBox<String> locationComboBox;
    private JPanel vehiclePanel;

    public DrivingView() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel label = new JLabel("🚗 Driving View", SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        add(label, BorderLayout.NORTH);

        // Panel conducteur
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

        conductorComboBox = new JComboBox<>();
        conductorComboBox.setPreferredSize(new Dimension(200, 28));
        for (Conductor c : VehicleDAO.getAllConductors()) {
            conductorComboBox.addItem(c);
        }

        topPanel.add(new JLabel("Select Conductor:"));
        topPanel.add(conductorComboBox);

        JButton refreshButton = new JButton("Load Conductor State");
        styleButton(refreshButton);
        refreshButton.addActionListener(e -> refreshConductor());
        topPanel.add(refreshButton);

        add(topPanel, BorderLayout.NORTH);

        // Zone centrale (dynamique)
        vehiclePanel = new JPanel();
        vehiclePanel.setLayout(new BoxLayout(vehiclePanel, BoxLayout.Y_AXIS));
        add(vehiclePanel, BorderLayout.CENTER);
    }

    private void refreshConductor() {
        vehiclePanel.removeAll();

        Conductor selectedConductor = (Conductor) conductorComboBox.getSelectedItem();
        if (selectedConductor == null) return;

        Vehicle drivenVehicle = VehicleDAO.getVehicleDrivenBy(selectedConductor.getPersonID());

        if (drivenVehicle != null) {
            // Affichage véhicule en cours de route
            JPanel drivenPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
            drivenPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            drivenPanel.add(new JLabel("Currently driving: " + drivenVehicle.getBrand() + " - " + drivenVehicle.getColor()));

            JButton stopButton = new JButton("Stop Driving");
            styleButton(stopButton);
            stopButton.setBackground(new Color(220, 53, 69)); // rouge
            stopButton.addActionListener(e -> {
                VehicleDAO.removeDriver(drivenVehicle.getVehicleID());
                refreshConductor();
            });
            drivenPanel.add(stopButton);
            vehiclePanel.add(drivenPanel);
        } else {
            // Sélection de localisation
            JPanel locationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
            locationPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

            locationComboBox = new JComboBox<>(Constants.CITIES.toArray(new String[0]));
            locationComboBox.setPreferredSize(new Dimension(200, 28));
            locationPanel.add(new JLabel("Select Location:"));
            locationPanel.add(locationComboBox);

            JButton loadVehiclesBtn = new JButton("Show Available Vehicles");
            styleButton(loadVehiclesBtn);
            loadVehiclesBtn.addActionListener(this::loadVehicles);
            locationPanel.add(loadVehiclesBtn);
            vehiclePanel.add(locationPanel);
        }

        revalidate();
        repaint();
    }

    private void loadVehicles(ActionEvent e) {
        JPanel vehicleListPanel = new JPanel();
        vehicleListPanel.setLayout(new BoxLayout(vehicleListPanel, BoxLayout.Y_AXIS));
        vehicleListPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        String selectedLocation = (String) locationComboBox.getSelectedItem();
        Conductor selectedConductor = (Conductor) conductorComboBox.getSelectedItem();

        List<Vehicle> availableVehicles = VehicleDAO.getAvailableVehiclesAtLocation(selectedLocation);
        if (availableVehicles.isEmpty()) {
            JLabel noVehicleLabel = new JLabel("No vehicles available at this location.");
            noVehicleLabel.setForeground(Color.GRAY);
            vehicleListPanel.add(noVehicleLabel);
        } else {
            for (Vehicle v : availableVehicles) {
                JPanel vPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
                vPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));

                vPanel.add(new JLabel(v.getBrand() + " - " + v.getColor()));

                JButton driveButton = new JButton("Drive");
                styleButton(driveButton);
                driveButton.setBackground(new Color(40, 167, 69)); // vert
                driveButton.addActionListener(ev -> {
                    VehicleDAO.assignDriver(selectedConductor.getPersonID(), v.getVehicleID());
                    VehicleDAO.updateLocation(v.getVehicleID(), Constants.getNextCity(selectedLocation));
                    refreshConductor();
                });
                vPanel.add(driveButton);
                vehicleListPanel.add(vPanel);
            }
        }

        // Important : On efface les anciens véhicules avant d’ajouter les nouveaux
        if (vehiclePanel.getComponentCount() > 1) {
            vehiclePanel.remove(1);
        }
        vehiclePanel.add(vehicleListPanel);
        revalidate();
        repaint();
    }

    public void refresh() {
        // reload conductors in case DB changed
        conductorComboBox.removeAllItems();
        for (Conductor c : VehicleDAO.getAllConductors()) {
            conductorComboBox.addItem(c);
        }
        refreshConductor();
    }

    private void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.PLAIN, 13));
        button.setBackground(new Color(0, 123, 255));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }
}
