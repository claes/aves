package se.eliga.aves.model;

import java.text.DecimalFormat;

/**
 * Created by vagrant on 1/5/16.
 */
public class BirdFormatter {

    private DecimalFormat numberFormatter;

    public BirdFormatter() {
        numberFormatter = new DecimalFormat("#,###");
        numberFormatter.setGroupingUsed(true);
    }

    public String getFormattedPopulation(Bird bird) {
        String population =
                numberFormatter.format(bird.getBestPopulationEstimate());
        switch (bird.getPopulationUnit()) {
            case PAIRS:
                population = population + " par";
                break;
            case CALLING_MALES:
                population = population + "\nlockande hanar";
                break;
            case BREEDING_FEMALES:
                population = population + "\nhäckande honor";
                break;
            case MALES:
                population = population + "\nhanar";
                break;
            case INDIVIDUALS:
                population = population + "\nindivider";
                break;

        }
        return population;
    }

    public String getFormattedPopulationRange(Bird bird) {
        return numberFormatter.format(bird.getMinPopulationEstimate()) + " - "  +
                numberFormatter.format(bird.getMaxPopulationEstimate());
    }

}
