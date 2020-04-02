package coursework;

import model.Fitness;
import model.Individual;
import model.NeuralNetwork;

import java.util.ArrayList;

public class IslandModels extends NeuralNetwork{

    public static void main(String[] args) {
        NeuralNetwork islands = new IslandModels();
        islands.run();
    }

    @Override
    public void run()
    {
        System.out.println("ISLAND MODELS");

        // instantiate class to use its methods
        ExampleEvolutionaryAlgorithm ea = new ExampleEvolutionaryAlgorithm();
        System.out.println("ISLAND MODELS 2");
        // ISLAND MODELS (one of them is the normal population)
        population = new ArrayList<Individual>();
        ArrayList<Individual> island2 = new ArrayList<Individual>();
        ArrayList<Individual> island3 = new ArrayList<Individual>();
        ArrayList<ArrayList<Individual>> islands = new ArrayList<>();
        islands.add(population);
        islands.add(island2);
        islands.add(island3);
        for (ArrayList<Individual> island : islands)
        {
            // initialise the populations
            for (int i = 0; i < Parameters.popSize; ++i) {
                Individual individual = new Individual();
                individual.fitness = Fitness.evaluate(individual, this);
                island.add(individual);
            }
            best = ea.getBest(island);
        }
        System.out.println("Island 1 size: "+population.size());
        System.out.println("Island 2 size: "+island2.size());
        System.out.println("Island 3 size: "+island3.size());

        Individual best_population;
        Individual best_island2;
        Individual best_island3;

        //run for max evaluations
        for(int gen = 0; gen < Parameters.maxEvaluations; gen++)
        {
            // EXCHANGE INDIVIDUALS
            if (gen % Parameters.reducePopSizeRate == 0)
            {
                System.out.println("Exchanging individuals");
                // find index of best individual
                int best_pop_idx = getBestIndex(population);
                int best_island2_idx = getBestIndex(island2);
                int best_island3_idx = getBestIndex(island3);
                System.out.println(best_pop_idx+", "+best_island2_idx+", "+best_island3_idx);
                // remove best individual from each population
                best_population = population.remove(best_pop_idx);
                best_island2 = island2.remove(best_island2_idx);
                best_island3 = island3.remove(best_island3_idx);
                // exchange individuals
                population.add(best_island3);
                island2.add(best_population);
                island3.add(best_island2);
            }

            //for (ArrayList<Individual> island : islands)
            for (int i = 0; i < islands.size(); i++)
            {
                ArrayList<Individual> island = islands.get(i);


                Individual parent1 = ea.selectingOptions(Parameters.selection[2], island); //tournament
                Individual parent2 = ea.selectingOptions(Parameters.selection[2], island);

                // CROSSOVER OPTIONS
                ArrayList<Individual> children = ea.crossoverOptions(Parameters.crossover[1], parent1, parent2); //uniform

                // MUTATION OPTIONS
                ea.mutationOptions(Parameters.mutation[2], children); // constrained

                // Evaluate new children and replace with worst in population
                ea.evaluateIndividuals(children);

                // REPLACEMENT OPTIONS
                ea.replacementOptions(Parameters.replacement[0], children, island); // replace worst
            }
            best = ea.getBest(population);
            //Increment number of completed generations
            outputStats();
        }
        saveNeuralNetwork();
    }


    public int getBestIndex(ArrayList<Individual> individuals) {
        Individual best = null;
        int idx = -1;
        for (int i = 0; i < individuals.size(); i++)
        {
            Individual individual = individuals.get(i);
            if (best == null){
                best = individual;
                idx = i;
            }
            else if (individual.fitness < best.fitness){
                best = individual;
                idx = i;
            }
        }
        return idx;
    }

    @Override
    public double activationFunction(double x)
    {
        if (Parameters.activation.equals("tanh")) {
            if (x < -20.0) return -1.0;
            else if (x > 20.0) return 1.0;
            return Math.tanh(x);
        }
        if (Parameters.activation.equals("relu"))
        {
            if (x > 0) return x;
            return -1;
        }
        if (Parameters.activation.equals("selu"))
        {
            if (x > 0) return x * 1.0507009;
            return 1.0507009 * (1.673263 * Math.pow(Math.E, x)) - 1.673263;
        }
        if (Parameters.activation.equals("step"))
        {
            if (x <= 0) return -1.00;
            return 1.0;
        }
        return x;
    }


}
