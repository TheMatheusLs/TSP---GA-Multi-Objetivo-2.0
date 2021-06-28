package GeneticAlgorithm;

import java.util.List;

import ExtensionMethods.Vector2f;
import Helpers.TownHelper;

public class Individual {

    public List<Integer> sequence;
    public int rank;
    public double distanceFitness;
    public double timeFitness;
    public double crowdingDistance;
    public double NormalizedDistanceFitness;
    public double NormalizedTimeFitness;


    public Individual(List<Integer> sequence){
        this.sequence = sequence;

        distanceFitness = GetTotalDistance();
        timeFitness = GetTotalTime();
    }


    private double GetTotalTime() {
        double totalTime = 0.0;

        for (int i = 0; i < sequence.size(); i++){
            Vector2f fromCitie = TownHelper.townPositions.get(sequence.get(i));
            Vector2f toCitie = TownHelper.townPositions.get(sequence.get((i + 1)%Config.numberOfCities));

            double x = fromCitie.X - toCitie.X;
            double y = fromCitie.Y - toCitie.Y;

            double d = Math.sqrt(x * x + y * y);

            totalTime += d / TownHelper.pathSpeedLimits[i][(i + 1)%Config.numberOfCities];
        }

        return totalTime;
    }


    private double GetTotalDistance() {
       
        double totalDistance = 0.0;

        for (int i = 0; i < sequence.size(); i++){
            Vector2f fromCitie = TownHelper.townPositions.get(sequence.get(i));
            Vector2f toCitie = TownHelper.townPositions.get(sequence.get((i + 1)%Config.numberOfCities));

            double x = fromCitie.X - toCitie.X;
            double y = fromCitie.Y - toCitie.Y;

            double d = Math.sqrt(x * x + y * y);

            totalDistance += d;
        }

        return totalDistance;
    }
}
