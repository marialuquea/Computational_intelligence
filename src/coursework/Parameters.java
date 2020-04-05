package coursework;

import java.lang.reflect.Field;
import java.util.Random;
import model.LunarParameters;
import model.NeuralNetwork;
import model.LunarParameters.DataSet;

public class Parameters
{
	public static int [] numHidenns = {5, 10, 15, 20, 30};
	public static int numHidden = 10;
	private static int numGenes = calculateNumGenes();

	public static double [] minGenes = {-0.5, -0.75, -1.0, -2.0, -3.0, -4.0, -5.0, -6.0};
	public static double [] maxGenes = {0.5, 0.75, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
	public static double minGene = -3; // specifies minimum and maximum weight values
	public static double maxGene = 3;

	public static int [] popSizes = {20, 50, 75, 100, 125, 150};
	public static int popSize = 50;
	public static int maxEvaluations = 10000;

	public static int [] tournament_and_rate = {5, 7, 10, 15, 20};
	public static int tournamentSize = 7; // check which one to put in algorithm
	public static int reducePopSizeRate = 7;
	public static int [] minPopSizes = {20, 30, 40, 50};
	public static int minPopSize = 25;

	public static String [] activations = {"tanh", "relu", "selu", "step"};
	public static String activation = "tanh";

	// Parameters for mutation
	// Rate = probability of changing a gene
	// Change = the maximum +/- adjustment to the gene value
	public static double [] mutateRates = {0.01, 0.04, 0.08, 0.12, 0.2, 0.3, 0.5};
	public static double mutateRate = 0.04; // mutation rate for mutation operator
	public static double mutateChange = 0.1; // delta change for mutation operator
	public static double crossoverProbability = 0.5;


	public static String [] initialisation = {"random", "best"};
	public static String [] selection = {"select", "roulette", "tournament"};
	public static String [] crossover = {"reproduce", "uniform", "doublepoint"};
	public static String [] mutation = {"mutate", "swap", "constrained"};
	public static String [] diversity = {"hillclimber", "sawtooth"};
	public static String [] replacement = {"replaceWorst", "tournament", "random"};

	//Random number generator used throughout the application
	public static long seed = System.currentTimeMillis();
	public static Random random = new Random(seed);

	//set the NeuralNetwork class here to use your code from the GUI
	public static Class neuralNetworkClass = ExampleEvolutionaryAlgorithm.class;









	/**
	 * Do not change any methods that appear below here.
	 *
	 */

	public static int getNumGenes() {
		return numGenes;
	}


	private static int calculateNumGenes() {
		int num = (NeuralNetwork.numInput * numHidden) + (numHidden * NeuralNetwork.numOutput) + numHidden + NeuralNetwork.numOutput;
		return num;
	}

	public static int getNumHidden() {
		return numHidden;
	}

	public static void setHidden(int nHidden) {
		numHidden = nHidden;
		numGenes = calculateNumGenes();
	}

	public static String printParams() {
		String str = "";
		for(Field field : Parameters.class.getDeclaredFields()) {
			String name = field.getName();
			Object val = null;
			try {
				val = field.get(null);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str += name + " \t" + val + "\r\n";

		}
		return str;
	}

	public static void setDataSet(DataSet dataSet) {
		LunarParameters.setDataSet(dataSet);
	}

	public static DataSet getDataSet() {
		return LunarParameters.getDataSet();
	}

	public static void main(String[] args) {
		printParams();
	}
}
