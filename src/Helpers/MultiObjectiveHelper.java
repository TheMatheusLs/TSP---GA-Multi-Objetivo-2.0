package Helpers;

import java.util.ArrayList;
import java.util.List;

import ExtensionMethods.Vector2f;
import GeneticAlgorithm.Individual;
import GeneticAlgorithm.Utility;

public class MultiObjectiveHelper {

    public static void UpdatePopulationFitness(List<Individual> population)
    {
        // Clear the existing ranks and crowding distances
        for (Individual individual : population){
            individual.rank = -1;
            individual.crowdingDistance = -1;
        }

        normalizeFitnessValues(population);

        List<Individual> remainingToBeRanked = new ArrayList<Individual>();
        for (Individual individual : population){
            remainingToBeRanked.add(individual);
        }

        // remainingToBeRanked.get(0).NormalizedDistanceFitness = 0.2;
        // remainingToBeRanked.get(0).NormalizedTimeFitness = 0.6;

        // remainingToBeRanked.get(1).NormalizedDistanceFitness = 0.2;
        // remainingToBeRanked.get(1).NormalizedTimeFitness = 0.4;

        // remainingToBeRanked.get(2).NormalizedDistanceFitness = 0.4;
        // remainingToBeRanked.get(2).NormalizedTimeFitness = 0.4;

        // remainingToBeRanked.get(3).NormalizedDistanceFitness = 0.4;
        // remainingToBeRanked.get(3).NormalizedTimeFitness = 0.6;
        
        // remainingToBeRanked.get(4).NormalizedDistanceFitness = 0.4;
        // remainingToBeRanked.get(4).NormalizedTimeFitness = 0.2;

        // remainingToBeRanked.get(5).NormalizedDistanceFitness = 0.6;
        // remainingToBeRanked.get(5).NormalizedTimeFitness = 0.6;
        

        // Put every individual in the population into their fronts
        int rank = 1;
        while (!remainingToBeRanked.isEmpty()){

            List<Individual> individualsInRank = new ArrayList<Individual>();

            for (int i = 0; i < remainingToBeRanked.size(); i++){
                Individual individual = remainingToBeRanked.get(i);
                if (isNotDominated(individual, remainingToBeRanked))
                {
                    individual.rank = rank;
                    individualsInRank.add(individual);
                }
            }

            // Não foi encontrada nenhuma dominância
            // if (individualsInRank.isEmpty()){
            //     for (IndividualGA individual : remainingToBeRanked){
            //         individual.rank = rank;
            //         individualsInRank.add(individual);
            //     }
            // }

            for (Individual individual: individualsInRank){
                remainingToBeRanked.remove(individual);
            }

            rank++;
        }

        //For each rank, calculate the crowdding distance for each individual
        List<List<Individual>> ranks = Utility.findAllRanks(population);

        for (List<Individual> singleRank : ranks){
            calculateCrowdingDistance(singleRank);
        }

        // Verifica se toda a população tem o crowdingDistance diferente de -1
        for (Individual individual : population){
            if (individual.crowdingDistance == -1){
                System.out.println("Erro");
            }
        }
    }

    /// <summary>
    /// Calculations for crowsing distance must be done on normalized fitness values to stop
    /// biasing towards an objective with a larger absolute fitness value.
    /// </summary>
    /// <param name="population"></param>
    private static void normalizeFitnessValues(List<Individual> population)
    {
        double maxDistance = -1;
        double maxTime = -1;

        for (Individual individual : population){
            if (maxDistance < individual.distanceFitness){
                maxDistance = individual.distanceFitness;
            }
            if (maxTime < individual.timeFitness){
                maxTime = individual.timeFitness;
            }
        }

        for (Individual individual : population){
            individual.NormalizedDistanceFitness = individual.distanceFitness / maxDistance;
            individual.NormalizedTimeFitness = individual.timeFitness / maxTime;
        }
    }

    public static boolean isNotDominated(Individual individualA, List<Individual> remainingToBeRanked)
    {
        // Loop over each individual and check if it dominates this individual.
        for (Individual individualB : remainingToBeRanked){
            if (individualA == individualB){
                continue;
            }

            // Verifica se os indivíduos estão na mesma posição em X e Y 
            if ((individualA.NormalizedDistanceFitness == individualB.NormalizedDistanceFitness) && (individualA.NormalizedTimeFitness == individualB.NormalizedTimeFitness) ){
                continue;
                // Não há dominância
                //TODO: Eliminar o B && (individualA.sequence == individualB.sequence)
            }


            // If this individual is at least better than us in one objective and equal in another,
            // then we are dominated by this individual
            //if ((individualB.distanceFitness <= individualA.distanceFitness) && (individualB.timeFitness <= individualA.timeFitness)){
            if ((individualB.distanceFitness <= individualA.distanceFitness) && (individualB.timeFitness <= individualA.timeFitness)){
                return false;
            }
        }

        return true;
    }

    private static void calculateCrowdingDistance(List<Individual> singleRank)
    {
        // As we only have two objectives, ordering individuals along one front allows us to make assumptions
        // about the locations of the neighbouring individuals in the array.
        List<Individual> auxSingleRank = new ArrayList<Individual>();
        for (Individual auxIndividual : singleRank){
            auxSingleRank.add(auxIndividual);
        }

        List<Individual> orderedIndividuals = new ArrayList<Individual>();

        int individualsInFront = auxSingleRank.size();
        for (int index = 0; index < individualsInFront; index++){
            int bestIndividualIndex = 0;
            double MaxNormalizedDistanceFitness = -1;
            for (int nSolution = 0; nSolution < auxSingleRank.size(); nSolution++){
                if(auxSingleRank.get(nSolution).NormalizedDistanceFitness > MaxNormalizedDistanceFitness){
                    MaxNormalizedDistanceFitness = auxSingleRank.get(nSolution).NormalizedDistanceFitness;
                    bestIndividualIndex = nSolution;
                }
            }

            orderedIndividuals.add(auxSingleRank.get(bestIndividualIndex));

            auxSingleRank.remove(bestIndividualIndex);
        }

        for (int i = 0; i < individualsInFront; i++)
        {
            // If we are at the start or end of a front, it should have infinite crowding distance
            if (i == 0 || i == (individualsInFront - 1)){
                orderedIndividuals.get(i).crowdingDistance = Double.POSITIVE_INFINITY;
            } else {
                // Grab a reference to each individual to make the next section a bit cleaner.
                Individual current = orderedIndividuals.get(i);
                Individual left = orderedIndividuals.get(i - 1);
                Individual right = orderedIndividuals.get(i + 1);

                // Get the positions on the 2D fitness graph, where time is our X axis and distance is our Y.
                Vector2f currentPosition = new Vector2f(current.NormalizedTimeFitness, current.NormalizedDistanceFitness);
                Vector2f leftPosition = new Vector2f(left.NormalizedTimeFitness, left.NormalizedDistanceFitness);
                Vector2f rightPosition = new Vector2f(right.NormalizedTimeFitness, right.NormalizedDistanceFitness);

                // Calculate the distance to the neighbourn on each side
                double distanceLeft = currentPosition.Distance(leftPosition);
                double distanceRight = currentPosition.Distance(rightPosition);

                // Set the crowding distance for the current individual
                orderedIndividuals.get(i).crowdingDistance = distanceLeft + distanceRight;
            }
        }
    }
}
