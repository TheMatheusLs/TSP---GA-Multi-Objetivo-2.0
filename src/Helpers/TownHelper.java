package Helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ExtensionMethods.Vector2f;
import GeneticAlgorithm.Config;

public class TownHelper {
    private static final int minimumSpeedInPixels = 10;
    private static final int maximumSpeedInPixels = 100;
    private static final int speedRangeInPixels = maximumSpeedInPixels - minimumSpeedInPixels;
    private static Random random = new Random();

    public static List<Vector2f> townPositions = new ArrayList<Vector2f>();
    public static double[][] pathSpeedLimits;

    public static void Initialize()
    {
        PopulateTowns();
        PopulateSpeedLimits();
    }

    private static void PopulateSpeedLimits() {
        Random localRandom = new Random(42);
        pathSpeedLimits = new double[Config.numberOfCities][Config.numberOfCities];

        for (int fromTown = 0; fromTown < Config.numberOfCities; fromTown++)
        {
            for (int toTown = 0; toTown < Config.numberOfCities; toTown++)
            {
                // If our from town is our to town, no need to calculate a path
                if (fromTown == toTown)
                {
                    continue;
                }

                // Calculate the path distance as speed is distance dependent
                var pathDistance = townPositions.get(toTown).Distance(townPositions.get(fromTown));

                // Add the speed for this directional path
                pathSpeedLimits[fromTown][toTown] = (minimumSpeedInPixels + speedRangeInPixels * localRandom.nextDouble() * pathDistance / 1000);
            }
        }
    }

    private static void PopulateTowns() {
        if (Config.useRandomCities)
            {
                for(int i = 0; i < Config.numberOfCities; i++)
                {
                    // Note that random town placements can overlap
                    townPositions.add(generatRandomTownPosition());
                }
            }
            else
            {
                townPositions.add(new Vector2f(100, 100));
                townPositions.add(new Vector2f(200, 150));
                townPositions.add(new Vector2f(300, 200));
                townPositions.add(new Vector2f(200, 300));
                townPositions.add(new Vector2f(100, 200));
            }
    }

    private static Vector2f generatRandomTownPosition()
    {
        return new Vector2f(
            50 + (random.nextDouble() * (Config.spaceWidth - 100)),
            50 + (random.nextDouble() * (Config.spaceHeight - 100))
        );
    }
}
