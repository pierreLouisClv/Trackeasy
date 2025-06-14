package com.trackeasy.app.UI;

import com.trackeasy.app.entities.Technician;

import javax.swing.*;
import java.awt.*;

public class TechView extends JPanel {

    public TechView() {
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Technician View", SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(4, 2));

        JTextField trackerIDField = new JTextField();
        JTextField trackerTypeField = new JTextField();
        JTextField vehicleIDField = new JTextField();

        JButton installButton = new JButton("Install Tracker");

        formPanel.add(new JLabel("Tracker ID:"));
        formPanel.add(trackerIDField);

        formPanel.add(new JLabel("Tracker Type:"));
        formPanel.add(trackerTypeField);

        formPanel.add(new JLabel("Vehicle ID:"));
        formPanel.add(vehicleIDField);

        formPanel.add(new JLabel());
        formPanel.add(installButton);

        add(formPanel, BorderLayout.CENTER);

        installButton.addActionListener(e -> {
            String trackerID = trackerIDField.getText().trim();
            String type = trackerTypeField.getText().trim();
            String vehicleID = vehicleIDField.getText().trim();

            Technician technician = new Technician();
            technician.setPersonID("P2"); // ID du technicien dans le fichier JSON

            if (technician.installTracker(trackerID, type, vehicleID)) {
                JOptionPane.showMessageDialog(this, "Tracker installed successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to install tracker. Maybe the vehicle already has one?", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

}