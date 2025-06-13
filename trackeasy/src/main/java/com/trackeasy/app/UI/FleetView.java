package com.trackeasy.app.UI;

import com.trackeasy.app.dao.VehicleDAO;
import com.trackeasy.app.entities.Vehicle;
import com.trackeasy.app.utils.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.UUID;
import java.util.Arrays;

public class FleetView extends JPanel {

    private DefaultTableModel model;
    private JTable table;
    private List<Vehicle> vehicles;

    public FleetView() {
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"Brand", "Color", "Has Tracker", "Running", "Action"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return column == 4;  // Seul le bouton est éditable
            }
        };

        table = new JTable(model);
        table.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));

        loadData();

        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton addButton = new JButton("Add Vehicle");
        addButton.addActionListener(e -> openAddVehicleDialog());
        add(addButton, BorderLayout.SOUTH);
    }

    private void loadData() {
        model.setRowCount(0);
        vehicles = VehicleDAO.getAllVehicles();
        for (Vehicle v : vehicles) {
            model.addRow(new Object[]{
                    v.getBrand(),
                    v.getColor(),
                    v.isHasTracker() ? "Yes" : "No",
                    v.isRunning() ? "Yes" : "No",
                    v.isHasTracker() ? "Locate" : ""
            });
        }
    }

    private void openAddVehicleDialog() {
        JTextField brandField = new JTextField();
        JTextField colorField = new JTextField();

        Object[] fields = {
                "Brand:", brandField,
                "Color:", colorField
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Add New Vehicle", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String brand = brandField.getText();
            String color = colorField.getText();

            String vehicleID = UUID.randomUUID().toString();
            Vehicle newVehicle = new Vehicle(vehicleID, brand, color, "Paris", false, false);
            VehicleDAO.addVehicle(newVehicle);
            loadData();
        }
    }

    private void locateVehicle(int row) {
        Vehicle v = vehicles.get(row);
        String newLocation = v.getLocation();
        JOptionPane.showMessageDialog(this, "Vehicle location: " + newLocation);
        loadData();
    }

    // Renderer du bouton
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    // Editor du bouton
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int selectedRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            selectedRow = row;
            button.setText(label);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed && label.equals("Locate")) {
                locateVehicle(selectedRow);
            }
            isPushed = false;
            return label;
        }
    }
}
