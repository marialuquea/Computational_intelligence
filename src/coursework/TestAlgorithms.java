package coursework;

import model.Fitness;
import model.Individual;
import model.LunarParameters;
import model.NeuralNetwork;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class TestAlgorithms extends NeuralNetwork
{
    private String [] initialisation = Parameters.initialisation;
    private String [] selection = Parameters.selection;
    private String [] crossover = Parameters.crossover;
    private String [] mutation = Parameters.mutation;
    private String [] diversity = Parameters.diversity;
    private String [] replacement = Parameters.replacement;

    public static void main(String[] args) {
        //NeuralNetwork ea = new ExampleEvolutionaryAlgorithm();
        //ea.run();

    }

    @Override
    public void run()
    {
        System.out.println("Starting testing script");
        ExampleEvolutionaryAlgorithm ea = new ExampleEvolutionaryAlgorithm();
        int times = 1;

        /*
        // NUMBER OF HIDDEN NODES - TESTING NOW SO DELETE LATER
        HashMap<String, String> best_ones = new HashMap<String, String>();
        int operators = 5;
        for (int i = 1; i <= operators; i++){ // for no of things to try
            for (int j = 1; j <= times; j++) { // run n times
                System.out.println("\n\n--i: " + j + " out of " + times + "\n");
                Parameters.numHidden = Parameters.numHidenns[i-1];
                System.out.println("Testing for number of hidden nodes " + Parameters.numHidden);
                String key_type = Integer.toString(j)+" "+Parameters.numHidden;
                best_ones.put(key_type, ea.runAlgorithm(initialisation[0], selection[0], crossover[1], mutation[1], diversity[1], replacement[0]).toString());
            }
        }
        for (String i : best_ones.keySet()) { System.out.println(i + " - " + best_ones.get(i)); }
        // export all results to file
        try(FileWriter fw = new FileWriter("results/num_hidden_nodes.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        { for (String i : best_ones.keySet()) { out.println(i + " - " + best_ones.get(i)); } }
        catch (Exception e) { e.printStackTrace(); }
         */



        // MIN MAX GENE
        HashMap<String, String> best_min_max_gene = new HashMap<String, String>();
        int operators2 = 8;
        for (int i = 1; i <= operators2; i++){ // for no of things to try
            for (int j = 1; j <= times; j++) { // run n times2
                System.out.println("\n\n--i: " + j + " out of " + times + "\n");
                Parameters.minGene = Parameters.minGenes[i-1];
                Parameters.maxGene = Parameters.maxGenes[i-1];
                System.out.println("Testing for min max genes " + Parameters.minGene + " - " + Parameters.maxGene);
                String key_type = Integer.toString(j)+" "+Parameters.maxGene;
                best_min_max_gene.put(key_type, ea.runAlgorithm(initialisation[0], selection[0], crossover[1], mutation[1], diversity[1], replacement[0]).toString());
            }
        }
        System.out.println("-----RESULTS FOR MIN MAX GENE-----");
        for (String i : best_min_max_gene.keySet()) { System.out.println(i + " - " + best_min_max_gene.get(i)); }
        // export all results to file
        try(FileWriter fw = new FileWriter("results/minmaxGene.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        { for (String i : best_min_max_gene.keySet()) out.println(i + " - " + best_min_max_gene.get(i));  }
        catch (Exception e) { e.printStackTrace(); }
        best_min_max_gene.clear();
        Parameters.minGene = -3;
        Parameters.maxGene = 3;




        // POP SIZE
        HashMap<String, String> best_pop_size = new HashMap<String, String>();
        int operators3 = 6;
        for (int i = 1; i <= operators3; i++){ // for no of things to try
            for (int j = 1; j <= times; j++) { // run n times
                System.out.println("\n\n--i: " + j + " out of " + times + "\n");
                Parameters.popSize = Parameters.popSizes[i-1];
                System.out.println("Testing for pop size " + Parameters.popSize);
                String key_type = Integer.toString(j)+" "+Parameters.popSize;
                best_pop_size.put(key_type, ea.runAlgorithm(initialisation[0], selection[0], crossover[1], mutation[1], diversity[1], replacement[0]).toString());
            }
        }
        System.out.println("-----RESULTS FOR POP SIZE-----");
        for (String i : best_pop_size.keySet()) { System.out.println(i + " - " + best_pop_size.get(i)); }
        // export all results to file
        try(FileWriter fw = new FileWriter("results/popSize.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        { for (String i : best_pop_size.keySet()) out.println(i + " - " + best_pop_size.get(i));  }
        catch (Exception e) { e.printStackTrace(); }
        best_pop_size.clear();
        Parameters.popSize = 50;




        // TOURNAMENT SIZE - selection and replacement
        HashMap<String, String> best_tournament = new HashMap<String, String>();
        int operators4 = 5;
        for (int i = 1; i <= operators4; i++){ // for no of things to try
            for (int j = 1; j <= times; j++) { // run n times
                System.out.println("\n\n--i: " + j + " out of " + times + "\n");
                Parameters.tournamentSize = Parameters.tournament_and_rate[i-1];
                Parameters.reducePopSizeRate = Parameters.tournament_and_rate[i-1];
                System.out.println("Testing for tournament size " + Parameters.tournamentSize);
                String key_type = Integer.toString(j)+" "+Parameters.tournamentSize;
                best_tournament.put(key_type, ea.runAlgorithm(initialisation[0], selection[2], crossover[1], mutation[1], diversity[1], replacement[1]).toString());
            }
        }
        System.out.println("-----RESULTS FOR TOURNAMENT SIZE-----");
        for (String i : best_tournament.keySet()) { System.out.println(i + " - " + best_tournament.get(i)); }
        // export all results to file
        try(FileWriter fw = new FileWriter("results/tournament.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        { for (String i : best_tournament.keySet()) out.println(i + " - " + best_tournament.get(i));  }
        catch (Exception e) { e.printStackTrace(); }
        best_tournament.clear();
        Parameters.tournamentSize = 7;
        Parameters.reducePopSizeRate = 7;




        // MIN POP SIZE - sawtooth
        HashMap<String, String> best_min_pop = new HashMap<String, String>();
        int operators5 = 4;
        for (int i = 1; i <= operators5; i++){ // for no of things to try
            for (int j = 1; j <= times; j++) { // run n times
                System.out.println("\n\n--i: " + j + " out of " + times + "\n");
                Parameters.minPopSize = Parameters.minPopSizes[i-1];
                System.out.println("Testing for min pop size " + Parameters.minPopSize);
                String key_type = Integer.toString(j)+" "+Parameters.minPopSize;
                best_tournament.put(key_type, ea.runAlgorithm(initialisation[0], selection[2], crossover[1], mutation[1], diversity[1], replacement[1]).toString());
            }
        }
        System.out.println("-----RESULTS FOR SAWTOOTH-----");
        for (String i : best_min_pop.keySet()) { System.out.println(i + " - " + best_min_pop.get(i)); }
        // export all results to file
        try(FileWriter fw = new FileWriter("results/min_pop_size.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        { for (String i : best_min_pop.keySet()) out.println(i + " - " + best_min_pop.get(i));  }
        catch (Exception e) { e.printStackTrace(); }
        best_min_pop.clear();
        Parameters.minPopSize = 25;




        // ACTIVATIONS
        HashMap<String, String> best_activation = new HashMap<String, String>();
        int operators6 = 4;
        for (int i = 1; i <= operators6; i++){ // for no of things to try
            for (int j = 1; j <= times; j++) { // run n times
                System.out.println("\n\n--i: " + j + " out of " + times + "\n");
                Parameters.activation = Parameters.activations[i-1];
                System.out.println("Testing for activation " + Parameters.activation);
                String key_type = Integer.toString(j)+" "+Parameters.activation;
                best_activation.put(key_type, ea.runAlgorithm(initialisation[0], selection[0], crossover[1], mutation[1], diversity[1], replacement[0]).toString());
            }
        }
        System.out.println("-----RESULTS FOR ACTIVATIONS-----");
        for (String i : best_activation.keySet()) { System.out.println(i + " - " + best_activation.get(i)); }
        // export all results to file
        try(FileWriter fw = new FileWriter("results/activation.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        { for (String i : best_activation.keySet()) out.println(i + " - " + best_activation.get(i));  }
        catch (Exception e) { e.printStackTrace(); }
        best_activation.clear();
        Parameters.activation = "tanh";



        // MUTATE RATE
        HashMap<String, String> best_mutate_rate = new HashMap<String, String>();
        int operators7 = 7;
        for (int i = 1; i <= operators7; i++){ // for no of things to try
            for (int j = 1; j <= times; j++) { // run n times
                System.out.println("\n\n--i: " + j + " out of " + times + "\n");
                Parameters.mutateRate = Parameters.mutateRates[i-1];
                System.out.println("Testing for mutate rate " + Parameters.mutateRate);
                String key_type = Integer.toString(j)+" "+Parameters.mutateRate;
                best_mutate_rate.put(key_type, ea.runAlgorithm(initialisation[0], selection[0], crossover[1], mutation[0], diversity[1], replacement[0]).toString());
            }
        }
        System.out.println("-----RESULTS FOR MUTATE RATE-----");
        for (String i : best_mutate_rate.keySet()) { System.out.println(i + " - " + best_mutate_rate.get(i)); }
        // export all results to file
        try(FileWriter fw = new FileWriter("results/mutate_rate.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        { for (String i : best_mutate_rate.keySet()) out.println(i + " - " + best_mutate_rate.get(i));  }
        catch (Exception e) { e.printStackTrace(); }
        best_mutate_rate.clear();
        Parameters.mutateRate = 0.04;




        // MUTATE CHANGE
        HashMap<String, String> best_mutate_change = new HashMap<String, String>();
        for (int i = 1; i <= operators7; i++){ // for no of things to try
            for (int j = 1; j <= times; j++) { // run n times
                System.out.println("\n\n--i: " + j + " out of " + times + "\n");
                Parameters.mutateChange = Parameters.mutateRates[i-1];
                System.out.println("Testing for mutate rate " + Parameters.mutateChange);
                String key_type = Integer.toString(j)+" "+Parameters.mutateChange;
                best_mutate_change.put(key_type, ea.runAlgorithm(initialisation[0], selection[0], crossover[1], mutation[0], diversity[1], replacement[0]).toString());
            }
        }
        System.out.println("-----RESULTS FOR MUTATE CHANGE-----");
        for (String i : best_mutate_change.keySet()) { System.out.println(i + " - " + best_mutate_change.get(i)); }
        // export all results to file
        try(FileWriter fw = new FileWriter("results/mutate_change.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        { for (String i : best_mutate_change.keySet()) out.println(i + " - " + best_mutate_change.get(i));  }
        catch (Exception e) { e.printStackTrace(); }
        best_mutate_change.clear();
        Parameters.mutateChange = 0.1;


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
