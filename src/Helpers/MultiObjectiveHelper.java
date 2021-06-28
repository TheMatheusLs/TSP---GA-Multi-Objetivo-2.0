package Helpers;

import java.util.ArrayList;
import java.util.List;

import ExtensionMethods.Vector2f;
import GeneticAlgorithm.Individual;
import GeneticAlgorithm.Utility;

public class MultiObjectiveHelper {

    public static void UpdatePopulationFitness(List<Individual> population)
    {
        for (Individual individual : population){
            individual.rank = -1;
            individual.crowdingDistance = -1;
        }

        normalizeFitnessValues(population);

        List<Individual> remainingToBeRanked = new ArrayList<Individual>();
        for (Individual individual : population){
            remainingToBeRanked.add(individual);
        }

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

            for (Individual individual: individualsInRank){
                remainingToBeRanked.remove(individual);
            }

            rank++;
        }

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
        for (Individual individualB : remainingToBeRanked){
            if (individualA == individualB){
                continue;
            }

            // Verifica se os indivíduos estão na mesma posição em X e Y 
            if ((individualA.NormalizedDistanceFitness == individualB.NormalizedDistanceFitness) && (individualA.NormalizedTimeFitness == individualB.NormalizedTimeFitness) ){
                continue;
                // Não há dominância. As duplicatas são retiradas em outro trecho do código
            }

            if ((individualB.distanceFitness <= individualA.distanceFitness) && (individualB.timeFitness <= individualA.timeFitness)){
                return false;
            }
        }

        return true;
    }

    private static void calculateCrowdingDistance(List<Individual> singleRank)
    {
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
            if (i == 0 || i == (individualsInFront - 1)){
                orderedIndividuals.get(i).crowdingDistance = Double.POSITIVE_INFINITY;
            } else {
                Individual current = orderedIndividuals.get(i);
                Individual left = orderedIndividuals.get(i - 1);
                Individual right = orderedIndividuals.get(i + 1);

                Vector2f currentPosition = new Vector2f(current.NormalizedTimeFitness, current.NormalizedDistanceFitness);
                Vector2f leftPosition = new Vector2f(left.NormalizedTimeFitness, left.NormalizedDistanceFitness);
                Vector2f rightPosition = new Vector2f(right.NormalizedTimeFitness, right.NormalizedDistanceFitness);

                double distanceLeft = currentPosition.Distance(leftPosition);
                double distanceRight = currentPosition.Distance(rightPosition);

                orderedIndividuals.get(i).crowdingDistance = distanceLeft + distanceRight;
            }
        }
    }
}
