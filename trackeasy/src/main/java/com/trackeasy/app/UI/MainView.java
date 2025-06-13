package com.trackeasy.app.UI;

import com.trackeasy.app.Database;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainView extends JFrame {
    private CardLayout cardLayout;
    private JPanel cards;

    // TO DO : TechView et DrivingView
    public MainView() {
        setTitle("Trackeasy");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        // Ajouter les sous-vues
        cards.add(new FleetView(), "Fleet");
        cards.add(new TechView(), "Tech");
        cards.add(new DrivingView(), "Drive");

        add(cards, BorderLayout.CENTER);

        // Menu de navigation simple
        JPanel menuPanel = new JPanel();
        JButton fleetBtn = new JButton("Fleet Manager");
        JButton techBtn = new JButton("Technician");
        JButton driveBtn = new JButton("Driver");

        fleetBtn.addActionListener(e -> cardLayout.show(cards, "Fleet"));
        techBtn.addActionListener(e -> cardLayout.show(cards, "Tech"));
        driveBtn.addActionListener(e -> cardLayout.show(cards, "Drive"));

        menuPanel.add(fleetBtn);
        menuPanel.add(techBtn);
        menuPanel.add(driveBtn);

        add(menuPanel, BorderLayout.NORTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Database.initialize(); // Appel l'init BDD au démarrage
            MainView view = new MainView();
            view.setVisible(true);
        });
    }
}
