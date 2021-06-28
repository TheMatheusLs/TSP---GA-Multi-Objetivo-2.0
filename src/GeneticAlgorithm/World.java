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

        LOOP_RANK:for (List<Individual> individualRank : Utility.findAllRanksByDistance(this.populationOfIndividuals)){
            LOOP_INDIVIDUAL:for (Individual individual : individualRank){
                for (Individual individualInNewPopulation: newPopulation){
                    if (Utility.isIndividualHasSameSequence(individualInNewPopulation, individual)){
                        continue LOOP_INDIVIDUAL;
                    }
                }

                newPopulation.add(individual);
                
                if (newPopulation.size() == Config.populationCounts){
                    break LOOP_RANK;
                }
            }
        }

        while (newPopulation.size() < Config.populationCounts){
            // Cria uma lista com os ranks temporários
            List<List<Individual>> individualRankTemp = Utility.findAllRanksByDistance(newPopulation);
            List<Individual> firstRankTemp = individualRankTemp.get(0);

            // Caso o tamanho da população não seja completado, cria clones dos inidivíduos dos primeiros front
            newPopulation.add(new Individual(firstRankTemp.get(random.nextInt(firstRankTemp.size())).sequence));
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
        List<Individual> candidates = WorldHelper.GetCandidateParents(this.populationOfIndividuals);

        return WorldHelper.tournamentSelection(candidates.get(0), candidates.get(1));
    }
}