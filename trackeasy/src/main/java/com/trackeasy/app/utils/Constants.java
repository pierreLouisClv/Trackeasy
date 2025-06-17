package com.trackeasy.app.utils;

import java.util.Arrays;
import java.util.List;

public class Constants {
        public static final List<String> CITIES = Arrays.asList("Paris", "Lyon", "Marseille", "Toulouse", "Nantes");

        public static String getNextCity(String currentLocation) {
                for (int i = 0; i < Constants.CITIES.size(); i++) {
                        if (Constants.CITIES.get(i).equals(currentLocation)) {
                                return Constants.CITIES.get((i + 1) % Constants.CITIES.size());
                        }
                }
        return Constants.CITIES.get(0); // fallback
    }

}
