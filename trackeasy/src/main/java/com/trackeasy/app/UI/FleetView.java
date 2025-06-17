package com.trackeasy.app.UI;

import com.trackeasy.app.dao.VehicleDAO;
import com.trackeasy.app.entities.Vehicle;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.UUID;

public class FleetView extends JPanel {

    private DefaultTableModel model;
    private JTable table;
    private List<Vehicle> vehicles;

    public FleetView() {
        setLayout(new BorderLayout());

        // Titre et bouton dans une barre en haut
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Fleet Manager", SwingConstants.LEFT);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));

        JButton addButton = new JButton("+ Add a vehicle");
        addButton.setPreferredSize(new Dimension(140, 30));
        addButton.setFocusPainted(false);
        addButton.setBackground(new Color(14, 64, 141));
        addButton.setForeground(Color.WHITE);
        addButton.setFont(new Font("SansSerif", Font.PLAIN, 13));
        addButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        addButton.addActionListener(e -> openAddVehicleDialog());

        JPanel rightBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightBtnPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));
        rightBtnPanel.add(addButton);

        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(rightBtnPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Table setup
        model = new DefaultTableModel(new String[]{"Brand", "Color", "Has Tracker", "Running", "Action"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return column == 4;  // Seul le bouton est éditable
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(new Color(0xD6EAF8));
        table.setGridColor(Color.LIGHT_GRAY);

        // En-tête de colonne stylée
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setBackground(new Color(240, 240, 240));

        // Rendu boutons
        table.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));

        // Alternance de lignes
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                                                            Object value, boolean isSelected, boolean hasFocus,
                                                            int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(250, 250, 250) : new Color(235, 245, 255));
                }
                return c;
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        loadData();
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
        JOptionPane.showMessageDialog(this, "Vehicle location: " + v.getLocation());
    }

    // Bouton renderer
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            setForeground(Color.WHITE);
            setBackground(new Color(14, 64, 141));
            setFont(new Font("SansSerif", Font.PLAIN, 12));
            return this;
        }
    }

    // Bouton éditeur
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int selectedRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setForeground(Color.WHITE);
            button.setBackground(new Color(0, 123, 255));
            button.setFont(new Font("SansSerif", Font.PLAIN, 12));
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            selectedRow = row;
            button.setText(label);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed && "Locate".equals(label)) {
                locateVehicle(selectedRow);
            }
            isPushed = false;
            return label;
        }
    }

    public void refresh() {
        loadData();
    }
}
