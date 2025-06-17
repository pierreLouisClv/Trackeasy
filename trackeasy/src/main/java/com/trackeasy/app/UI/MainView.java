package com.trackeasy.app.UI;

import com.trackeasy.app.Database;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {

    private CardLayout cardLayout;
    private JPanel cards;

    public MainView() {
        setTitle("Trackeasy");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 450);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        // Initialiser les sous-vues
        FleetView fleetView = new FleetView();
        TechView techView = new TechView();
        DrivingView drivingView = new DrivingView();

        // Ajouter les sous-vues
        cards.add(fleetView, "Fleet");
        cards.add(techView, "Tech");
        cards.add(drivingView, "Drive");

        add(cards, BorderLayout.CENTER);

        // Barre d'onglets
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ButtonGroup tabGroup = new ButtonGroup();

        JToggleButton fleetTab = new JToggleButton("Fleet Manager");
        JToggleButton techTab = new JToggleButton("Technician");
        JToggleButton driveTab = new JToggleButton("Driver");

        tabGroup.add(fleetTab);
        tabGroup.add(techTab);
        tabGroup.add(driveTab);

        fleetTab.setSelected(true); // Onglet par défaut

        // Styles basiques pour différencier
        Dimension buttonSize = new Dimension(130, 30);
        fleetTab.setPreferredSize(buttonSize);
        techTab.setPreferredSize(buttonSize);
        driveTab.setPreferredSize(buttonSize);

        fleetTab.addActionListener(e -> {
            fleetView.refresh();
            cardLayout.show(cards, "Fleet");
        });

        techTab.addActionListener(e -> {
            techView.refresh();
            cardLayout.show(cards, "Tech");
        });

        driveTab.addActionListener(e -> {
            drivingView.refresh();
            cardLayout.show(cards, "Drive");
        });

        tabPanel.add(fleetTab);
        tabPanel.add(techTab);
        tabPanel.add(driveTab);

        add(tabPanel, BorderLayout.NORTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Database.initialize();
            MainView view = new MainView();
            view.setVisible(true);
        });
    }
}
