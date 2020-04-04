package coursework;

import model.Fitness;
import model.Individual;
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
        NeuralNetwork ea = new ExampleEvolutionaryAlgorithm();
        ea.run();
    }

    @Override
    public void run()
    {
        System.out.println("Starting testing script");
        ExampleEvolutionaryAlgorithm ea = new ExampleEvolutionaryAlgorithm();
        HashMap<String, String> best_ones = new HashMap<String, String>();
        int times = 10;
        int operators = 2;
        for (int i = 1; i <= operators; i++) // for each initialisation mode
        {
            for (int j = 1; j <= times; j++) // run n times
            {
                System.out.println("\n\n--i: " + j + " out of " + times + "\n");
                System.out.println("Testing for initialisation " + diversity[i - 1]);
                String key_type = Integer.toString(j)+" "+diversity[i-1];
                best_ones.put(key_type, ea.runAlgorithm(initialisation[0], selection[2], crossover[1], mutation[2], diversity[i-1], replacement[0]).toString());
            }
        }

        System.out.println("-----Best results found-----");
        for (String i : best_ones.keySet()) {
            System.out.println(i + " - " + best_ones.get(i));
        }

        // export all results to file
        try(FileWriter fw = new FileWriter("results/diversity.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            for (String i : best_ones.keySet()) {
                out.println(i + " - " + best_ones.get(i));
            }

        }
        catch (Exception e) { e.printStackTrace(); }

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
