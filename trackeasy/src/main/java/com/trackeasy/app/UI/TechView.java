package com.trackeasy.app.UI;

import com.trackeasy.app.dao.VehicleDAO;
import com.trackeasy.app.entities.Vehicle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.UUID;

public class TechView extends JPanel {

    private JComboBox<Vehicle> vehicleComboBox;
    private JComboBox<String> trackerTypeComboBox;
    private JButton installButton;
    private JPanel formPanel;
    private JLabel noVehicleLabel;

    public TechView() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Message quand il n'y a pas de véhicule
        noVehicleLabel = new JLabel("No vehicles available for installation.", SwingConstants.CENTER);
        noVehicleLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        add(noVehicleLabel, BorderLayout.CENTER);

        formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        vehicleComboBox = new JComboBox<>();
        vehicleComboBox.setPreferredSize(new Dimension(200, 25));
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Select Vehicle:"), gbc);
        gbc.gridx = 1;
        formPanel.add(vehicleComboBox, gbc);

        trackerTypeComboBox = new JComboBox<>(new String[]{"GPS", "OBD", "Standalone"});
        trackerTypeComboBox.setPreferredSize(new Dimension(200, 25));
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Select Tracker Type:"), gbc);
        gbc.gridx = 1;
        formPanel.add(trackerTypeComboBox, gbc);

        installButton = new JButton("Install Tracker");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(installButton, gbc);

        add(formPanel, BorderLayout.CENTER);
        installButton.addActionListener(this::installTracker);

        loadVehicles();
    }

    private void loadVehicles() {
        vehicleComboBox.removeAllItems();
        List<Vehicle> vehicles = VehicleDAO.getVehiclesWithoutTracker();

        for (Vehicle v : vehicles) {
            vehicleComboBox.addItem(v);
        }

        boolean hasVehicles = !vehicles.isEmpty();
        formPanel.setVisible(hasVehicles);
        noVehicleLabel.setVisible(!hasVehicles);
    }

    private void installTracker(ActionEvent e) {
        Vehicle selectedVehicle = (Vehicle) vehicleComboBox.getSelectedItem();
        if (selectedVehicle == null) {
            JOptionPane.showMessageDialog(this, "No vehicle selected!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String trackerID = UUID.randomUUID().toString();
        String trackerType = (String) trackerTypeComboBox.getSelectedItem();
        String technicianID = "P2";  // hardcoded for now

        // Utilisation de SwingWorker pour garder l'UI responsive
        installButton.setEnabled(false);
        installButton.setText("Installing...");

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                Thread.sleep(3000);  // Simule l'installation
                return VehicleDAO.addTracker(trackerID, trackerType, technicianID, selectedVehicle.getVehicleID());
            }

            @Override
            protected void done() {
                try {
                    boolean result = get();
                    if (result) {
                        JOptionPane.showMessageDialog(TechView.this, "Installation succeed!");
                        loadVehicles();
                    } else {
                        JOptionPane.showMessageDialog(TechView.this, "Error during installation", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(TechView.this, "Unexpected error", "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    installButton.setEnabled(true);
                    installButton.setText("Install Tracker");
                }
            }
        };
        worker.execute();
    }

    public void refresh() {
        loadVehicles();
    }
}
