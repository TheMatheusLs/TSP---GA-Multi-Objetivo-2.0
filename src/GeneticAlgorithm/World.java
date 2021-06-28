package GeneticAlgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Helpers.WorldHelper;
import Helpers.MultiObjectiveHelper;

public class World {
    
    Random random = new Random();
    
    public ArrayList<Individual> populationOfIndividuals;

    List<Double> fitnessOverTimer;

    public int generationsCount = 0;
    public int noImprovementCount = 0;

    public boolean hasConverged(){
        return generationsCount > Config.maxGenerations || noImprovementCount > Config.maxNoImprovementCount;
    }

    public World(){

        populationOfIndividuals = new ArrayList<Individual>();
        fitnessOverTimer = new ArrayList<Double>();
    }

    public void Spawn(){
        this.populationOfIndividuals.addAll(WorldHelper.SpawnPopulation());
        
    }

    public void DoGeneration(){

        generationsCount++;

        // Create a list to hold our new offspring
        List<Individual> offspring = new ArrayList<Individual>();

        for (int i = 0; i < Config.populationCounts; i++){
            
            // Encontra os pais
            Individual parent_1 = getParent();
            Individual parent_2 = getParent();

            // Encontra dois indivíduos diferentes para serem os pais
            while (parent_1 == parent_2){
                parent_2 = getParent();
            }

            // Realiza o crossover
            List<Individual> offspringAB = getOffspring(parent_1, parent_2);
            
            // Realiza a mutação
            offspringAB = mutate(offspringAB.get(0), offspringAB.get(1));
            
            offspring.add(offspringAB.get(0));
            offspring.add(offspringAB.get(1));

        }

        // Adiciona os novos filhos na população de pais
        this.populationOfIndividuals.addAll(offspring);

        // Atualiza os Fitness de toda a população
        MultiObjectiveHelper.UpdatePopulationFitness(this.populationOfIndividuals);

        // Encontra os melhores novos indivíduos
        List<Individual> newPopulation = new ArrayList<Individual>();

        LOOP:for (List<Individual> individualRank : Utility.findAllRanksByDistance(this.populationOfIndividuals)){
            for (Individual individual : individualRank){
                if (!newPopulation.contains(individual))
                {
                    newPopulation.add(individual);
                }

                if (newPopulation.size() == Config.populationCounts){
                    break LOOP;
                }
            }
        }

        this.populationOfIndividuals.clear();

        for (Individual individual : newPopulation){
            this.populationOfIndividuals.add(individual);
        }
    }

    public Individual getBestIndividual()
    {
        // Sorteia um indivíduo presente na frente de pareto
        List<Individual> firstRank = Utility.findAllRanksByDistance(this.populationOfIndividuals).get(0);
        return firstRank.get(random.nextInt(firstRank.size()));
    }

    private List<Individual> mutate(Individual individualA, Individual individualB)
    {
        return WorldHelper.Mutate(individualA, individualB);
    }

    private List<Individual> getOffspring(Individual individualA, Individual individualB)
    {
        // Generate the offspring from our selected parents
        Individual offspringA = DoCrossover(individualA, individualB);
        Individual offspringB = DoCrossover(individualB, individualA);

        ArrayList<Individual> returnOffspring = new ArrayList<Individual>();
        returnOffspring.add(offspringA);
        returnOffspring.add(offspringB);

        return returnOffspring;
    }

    private Individual DoCrossover(Individual individualA, Individual individualB)
    {
        return WorldHelper.DoCrossover(individualA, individualB);
    }

    public Individual getParent(){
        // Grab two candidate parents from the population.
        List<Individual> candidates = WorldHelper.GetCandidateParents(this.populationOfIndividuals);

        // Perform the tournament selection
        return WorldHelper.tournamentSelection(candidates.get(0), candidates.get(1));
    }
}