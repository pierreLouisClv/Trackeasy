package com.trackeasy.app;

import com.trackeasy.app.UI.MainView;

/**
 * Hello world!
 */
public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            Database.initialize();
            MainView view = new MainView();
            view.setVisible(true);
        });
    }
}
