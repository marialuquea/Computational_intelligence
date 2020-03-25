package coursework;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import model.Fitness;
import model.Individual;
import model.LunarParameters.DataSet;
import model.NeuralNetwork;

/**
 * Implements a basic Evolutionary Algorithm to train a Neural Network
 * 
 * You Can Use This Class to implement your EA or implement your own class that extends {@link NeuralNetwork} 
 * 
 */
public class ExampleEvolutionaryAlgorithm extends NeuralNetwork {

	@Override
	public void run() {
		//Initialise a population of Individuals with random weights
		System.out.println("Initialising weights...");
		population = initialise();

		//Record a copy of the best Individual in the population
		best = getBest();
		System.out.println("Best From Initialisation " + best);

		/**
		 * main EA processing loop
		 */		
		
		while (evaluations < Parameters.maxEvaluations) {

			/**
			 * this is a skeleton EA - you need to add the methods.
			 * You can also change the EA if you want 
			 * You must set the best Individual at the end of a run
			 * 
			 */

			// Select 2 Individuals from the current population. Currently returns random Individual
			Individual parent1 = RoulletteSelection();
			Individual parent2 = RoulletteSelection();

			// Generate a child by crossover. Not Implemented			
			ArrayList<Individual> children = reproduce(parent1, parent2);			
			
			//mutate the offspring
			mutate(children);
			
			// Evaluate the children
			evaluateIndividuals(children);			

			// Replace children in population
			replace(children);

			// check to see if the best has improved
			best = getBest();
			
			// Implemented in NN class. 
			outputStats();
			
			//Increment number of completed generations			
		}

		//save the trained network to disk
		saveNeuralNetwork();
	}

	/*******************************************************************
	 * 				     		USEFUL TOOLS						   *
	 *******************************************************************/

	// Sets the fitness of the individuals passes as parameters (whole population)
	private void evaluateIndividuals(ArrayList<Individual> individuals) {
		for (Individual individual : individuals) {
			individual.fitness = Fitness.evaluate(individual, this);
		}
	}

	// Returns a copy of the best individual in the population
	private Individual getBest() {
		best = null;;
		for (Individual individual : population)
		{
			if (best == null)
			{
				best = individual.copy();
			}
			else if (individual.fitness < best.fitness)
			{
				best = individual.copy();
			}
		}
		return best;
	}

	// Generates a randomly initialised population
	private ArrayList<Individual> initialise() {
		population = new ArrayList<>();
		for (int i = 0; i < Parameters.popSize; ++i)
		{
			//chromosome weights are initialised randomly in the constructor
			Individual individual = new Individual();
			population.add(individual);
		}
		evaluateIndividuals(population);
		return population;
	}

	// Returns the index of the worst member of the population
	private int getWorstIndex() {
		Individual worst = null;
		int idx = -1;
		for (int i = 0; i < population.size(); i++)
		{
			Individual individual = population.get(i);
			if (worst == null)
			{
				worst = individual;
				idx = i;
			} else if (individual.fitness > worst.fitness)
			{
				worst = individual;
				idx = i;
			}
		}
		return idx;
	}

	private Individual getWorst(ArrayList<Individual> aPopulation) {
		double worstFitness = 0;
		Individual worst = null;
		for(Individual individual : population){
			if(individual.fitness > worstFitness || worst == null){
				worst = individual;
				worstFitness = worst.fitness;
			}
		}
		return worst;
	}

	private void printPopulation() {
		for(Individual individual : population){
			System.out.println(individual);
		}
	}

	/*******************************************************************
	 * 						SELECTION METHODS						   *
	 *******************************************************************/
	private Individual select()
	{
		Individual parent = population.get(Parameters.random.nextInt(Parameters.popSize));
		return parent.copy();
	}

	private Individual RoulletteSelection()
	{
		Individual parent = new Individual();

		double total = 0;
		for(Individual i:population)
		{
			total += 1 / i.fitness;
			parent = i;
		}

		double spinner = total * ThreadLocalRandom.current().nextDouble(0, 1);

		double count = 0;
		for(Individual i:population)
		{
			count += 1 / i.fitness;
			if (count >= spinner)
				return i;
		}

		return parent;
	}

	private Individual tournamentSelection()
	{
		ArrayList<Individual> candidates = new ArrayList<Individual>();
		for(int i = 0; i < Parameters.tournamentSize; i++)
		{
			candidates.add(population.get(Parameters.random.nextInt(population.size())));
		}
		return getBest().copy();
	}

	/*******************************************************************
	 * 						CROSSOVER METHODS						   *
	 *******************************************************************/
	private ArrayList<Individual> reproduce(Individual parent1, Individual parent2) {
		ArrayList<Individual> children = new ArrayList<>();
		children.add(parent1.copy());
		children.add(parent2.copy());			
		return children;
	}

	private Individual UniformCrossover(Individual parent1, Individual parent2) {
		if(Parameters.random.nextDouble() > Parameters.crossoverProbability)
			return parent1;

		Individual child1 = new Individual();

		for(int i = 0; i < parent1.chromosome.length; i++)
		{
			if(Parameters.random.nextInt(1) <= 0.5)
				child1.chromosome[i] = parent1.chromosome[i];
			else
				child1.chromosome[i] = parent2.chromosome[i];
		}
		return child1;
	}

	private Individual doublePointCrossover(Individual parent1, Individual parent2) {
		// crossover probability
		if (Parameters.random.nextDouble() > Parameters.crossoverProbability)
			return parent1;

		Individual child = new Individual(); // new empty child

		// random crossover points
		int crossoverPoint1 = Parameters.random.nextInt(parent1.chromosome.length);
		int crossoverPoint2 = Parameters.random.nextInt(parent1.chromosome.length);

		// make sure first crossover point is before second one
		if (crossoverPoint1 > crossoverPoint2) {
			int temp = crossoverPoint1;
			crossoverPoint1 = crossoverPoint2;
			crossoverPoint2 = temp;
		}

		for (int i = 0; i < crossoverPoint1; i++)
			child.chromosome[i] = parent1.chromosome[i];

		for (int i = crossoverPoint1; i < crossoverPoint2; i++)
			child.chromosome[i] = parent2.chromosome[i];

		for (int i = crossoverPoint2; i < parent1.chromosome.length; i++)
			child.chromosome[i] = parent1.chromosome[i];

		return child;
	}

	/*******************************************************************
	 * 						MUTATION METHODS						   *
	 *******************************************************************/
	private void mutate(ArrayList<Individual> individuals) {		
		for(Individual individual : individuals) {
			for (int i = 0; i < individual.chromosome.length; i++) {
				if (Parameters.random.nextDouble() < Parameters.mutateRate) {
					if (Parameters.random.nextBoolean()) {
						individual.chromosome[i] += (Parameters.mutateChange);
					} else {
						individual.chromosome[i] -= (Parameters.mutateChange);
					}
				}
			}
		}		
	}

	private Individual swapMutation(Individual child){

		// mutation probability
		if(Parameters.random.nextDouble() > Parameters.mutationProbability)
			return child;

		int index1 = Parameters.random.nextInt(child.chromosome.length);
		int index2 = Parameters.random.nextInt(child.chromosome.length);
		double swap1 = child.chromosome[index1];
		double swap2 = child.chromosome[index2];
		double temp = swap1;
		child.chromosome[index1] = swap2;
		child.chromosome[index2] = temp;

		return child;
	}

	/*******************************************************************
	 * 						REPLACEMENT METHODS						   *
	 *******************************************************************/
	private void replace(ArrayList<Individual> individuals) {
		for(Individual individual : individuals) {
			int idx = getWorstIndex();		
			population.set(idx, individual);
		}		
	}

	/*******************************************************************
	 * 						DIVERSITY METHODS						   *
	 *******************************************************************/
	// DIVERSITY - SAWTOOTH & HILL CLIMBER
	public void removeIndividual(){	population.remove(getWorst(population)); }

	private void refillPopulation(int start, int bound) {
		// refill population with random seeds
		while(population.size() < Parameters.popSize){
			population2 = initialise(); // find out this
			Individual individual = new Individual();
			individual.initialise(start, bound);
			population.add(individual);
		}
	}

	/**
	 *  CHECK THIS FUNCTION - NOT FINISHED
	 * @param i
	 * @return
	 */
	private Individual hill_climber(Individual i){

		System.out.println("\n---HILL CLIMBER---");
		Individual best = i.copy();
		Individual temp = i.copy(); // the best seed after running the EA
		double before = temp.fitness;

		for (int j = 0; j < 100; j++)
		{
			// change the value of 1 pacing value
			int mutationChange = Parameters.random.nextInt(1); // check
			int index_pacing = Parameters.random.nextInt(temp.chromosome.length);

			// ensure mutation is valid
			if (temp.chromosome[index_pacing] + mutationChange <= 1)
				temp.chromosome[index_pacing] = temp.chromosome[index_pacing] + mutationChange;

			// if after mutation, individual is better, now work on that one
			if (temp.fitness <  best.fitness) {
				best = temp.copy();
				System.out.println("better: "+best.fitness+" at iteration "+j);
			}
			else
				temp = best.copy(); // forget about the mutated allele and work on first one
		}
		System.out.println("best: "+best.fitness);
		return best;
	}

	/*******************************************************************
	 * 						ACTIVATION FUNCTION 					   *
	 *******************************************************************/
	@Override
	public double activationFunction(double x)
	{
		if (x < -20.0)
		{
			return -1.0;
		} else if (x > 20.0)
		{
			return 1.0;
		}
		return Math.tanh(x);
	}
}
