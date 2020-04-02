package coursework;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import model.Fitness;
import model.Individual;
import model.NeuralNetwork;


public class ExampleEvolutionaryAlgorithm extends NeuralNetwork
{
	private String [] selection = Parameters.selection;
	private String [] crossover = Parameters.crossover;
	private String [] mutation = Parameters.mutation;
	private String [] diversity = Parameters.diversity;
	private String [] replacement = Parameters.replacement;

	@Override
	public void run()
	{
		runAlgorithm(selection[2], crossover[1], mutation[2], diversity[1], replacement[0]);
		//testAlgorithm();
	}

	public void testAlgorithm()
	{
		int times = 2;
		for (int i = 0; i <= times; i++)
		{
			System.out.println("--i: "+i+"\n");
			runAlgorithm(selection[i], crossover[i], mutation[i], diversity[i], replacement[i]);
		}

	}

	public void runAlgorithm(String selection, String crossover, String mutation, String diversity, String replace)
	{
		//System.out.println(selection + crossover+ mutation+ diversity+ replace);

		//Initialise a population of Individuals with random weights
		population = initialise();
		System.out.println("Population initialized: "+population.size());

		//Record a copy of the best Individual in the population
		best = getBest();
		System.out.println("Best From Initialisation " + best);


		// ISLAND MODELS
		/*
		ArrayList<Individual> island1 = new ArrayList<Individual>();
		ArrayList<Individual> island2 = new ArrayList<Individual>();
		ArrayList<Individual> island3 = new ArrayList<Individual>();
		ArrayList<ArrayList<Individual>> islands = new ArrayList<>();
		islands.add(island1);
		islands.add(island2);
		islands.add(island3);
		for (ArrayList<Individual> island : islands)
		{
			for (int i = 0; i < Parameters.popSize; ++i) {
				Individual individual = new Individual();
				individual.fitness = Fitness.evaluate(individual, this);
				island.add(individual);
			}
		}
		System.out.println("Island 1 size: "+island1.size());
		System.out.println("Island 2 size: "+island2.size());
		System.out.println("Island 3 size: "+island3.size());

		 */

		while (evaluations < Parameters.maxEvaluations)
		{
			// DIVERSITY
			if (diversity.equals("sawtooth") && evaluations % Parameters.reducePopSizeRate == 0){
				System.out.println("SAWTOOTH: Removing individual, pop size: "+population.size());
				removeIndividual(); // every 30 iterations, remove worst individual
				if (population.size() <= Parameters.minPopSize) {
					refillPopulation();
					System.out.println("Population refilled");
				}
			}

			// SELECTING OPTIONS
			Individual parent1 = selectingOptions(selection);
			Individual parent2 = selectingOptions(selection);

			// CROSSOVER OPTIONS
			ArrayList<Individual> children = crossoverOptions(crossover, parent1, parent2);

			// MUTATION OPTIONS
			mutationOptions(mutation, children);

			// Evaluate new children and replace with worst in population
			evaluateIndividuals(children);

			// REPLACEMENT OPTIONS
			replacementOptions(replace, children);

			best = getBest();
			outputStats();

			//Increment number of completed generations
		}

		// Hill climber
		//if (diversity.equals("hillclimber"))
		hill_climber(best, 20000);

		//save the trained network to disk
		saveNeuralNetwork();
	}



	/*******************************************************************
	 * 				     		USEFUL TOOLS						   *
	 *******************************************************************/

	private void evaluateIndividuals(ArrayList<Individual> individuals) {
		for (Individual individual : individuals) {
			individual.fitness = Fitness.evaluate(individual, this);
		}
	}

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

	private int getWorstIndex(ArrayList<Individual> individuals) {
		Individual worst = null;
		int idx = -1;
		for (int i = 0; i < individuals.size(); i++)
		{
			Individual individual = individuals.get(i);
			if (worst == null){
				worst = individual;
				idx = i;
			}
			else if (individual.fitness > worst.fitness){
				worst = individual;
				idx = i;
			}
		}
		return idx;
	}

	private Individual getWorst(ArrayList<Individual> individuals) {
		double worstFitness = 0;
		Individual worst = null;
		for(Individual individual : individuals){
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
	private Individual selectingOptions(String selection)
	{
		Individual parent = new Individual();
		if (selection.equals("select"))
			parent = select();
		if (selection.equals("roulette"))
			parent = rouletteSelection();
		if (selection.equals("tournament"))
			parent = tournamentSelection();

		return parent;
	}

	private Individual select()
	{
		Individual parent = population.get(Parameters.random.nextInt(population.size()));
		return parent.copy();
	}

	private Individual rouletteSelection()
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
	private ArrayList<Individual> crossoverOptions(String crossover, Individual parent1, Individual parent2) {
		ArrayList<Individual> children = new ArrayList<>();
		if (crossover.equals("reproduce"))
			children = reproduce(parent1, parent2);
		if (crossover.equals("uniform"))
			children = uniformCrossover(parent1, parent2);
		if (crossover.equals("doublepoint"))
			children = doublePointCrossover(parent1, parent2);

		return children;
	}

	private ArrayList<Individual> reproduce(Individual parent1, Individual parent2) {
		ArrayList<Individual> children = new ArrayList<>();
		children.add(parent1.copy());
		children.add(parent2.copy());
		return children;
	}

	private ArrayList<Individual> uniformCrossover(Individual parent1, Individual parent2) {
		ArrayList<Individual> children = new ArrayList<>();

		if(Parameters.random.nextDouble() > Parameters.crossoverProbability)
		{
			children.add(parent1);
			return children;
		}

		Individual child1 = new Individual();

		for(int i = 0; i < parent1.chromosome.length; i++)
		{
			if(Parameters.random.nextInt(1) <= 0.5)
				child1.chromosome[i] = parent1.chromosome[i];
			else
				child1.chromosome[i] = parent2.chromosome[i];
		}
		children.add(child1);
		return children;
	}

	private ArrayList<Individual> doublePointCrossover(Individual parent1, Individual parent2) {
		ArrayList<Individual> children = new ArrayList<>();
		// crossover probability
		if (Parameters.random.nextDouble() > Parameters.crossoverProbability)
		{
			children.add(parent1);
			return children;
		}

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

		children.add(child);
		return children;
	}

	/*******************************************************************
	 * 						MUTATION METHODS						   *
	 *******************************************************************/
	private void mutationOptions(String mutation, ArrayList<Individual> children) {
		if (mutation.equals("mutate")) mutate(children);
		if (mutation.equals("swap")) swapMutation(children);
		if (mutation.equals("constrained")) constrainedMutation(children);
	}

	private void mutate(ArrayList<Individual> individuals) {
		for(Individual individual : individuals)
		{
			for (int i = 0; i < individual.chromosome.length; i++)
			{
				if (Parameters.random.nextDouble() < Parameters.mutateRate)
				{
					if (Parameters.random.nextBoolean())
						individual.chromosome[i] += (Parameters.mutateChange); // need to experiment with this
					else
						individual.chromosome[i] -= (Parameters.mutateChange);
				}
			}
		}
	}

	private void swapMutation(ArrayList<Individual> individuals)
	{
		for(Individual child : individuals)
		{
			// mutation probability
			if(Parameters.random.nextDouble() < Parameters.mutationProbability)
			{
				int index1 = Parameters.random.nextInt(child.chromosome.length);
				int index2 = Parameters.random.nextInt(child.chromosome.length);
				double swap1 = child.chromosome[index1];
				double swap2 = child.chromosome[index2];
				double temp = swap1;
				child.chromosome[index1] = swap2;
				child.chromosome[index2] = temp;
			}
		}
	}

	private void constrainedMutation(ArrayList<Individual> individuals)
    {
        for(Individual individual : individuals)
        {
            for (int i = 0; i < individual.chromosome.length; i++)
            {
                if (Parameters.random.nextDouble() < Parameters.mutateRate)
                {
                    double before = individual.fitness;
                    if (Parameters.random.nextBoolean()) { // add mutation
                        individual.chromosome[i] += (Parameters.mutateChange);
                        individual.fitness = Fitness.evaluate(individual, this);
                        if (individual.fitness > before) individual.chromosome[i] -= (Parameters.mutateChange);
                    }
                    else { // subtract mutation
                        individual.chromosome[i] -= (Parameters.mutateChange);
                        individual.fitness = Fitness.evaluate(individual, this);
                        if (individual.fitness > before) individual.chromosome[i] += (Parameters.mutateChange);
                    }
                }
            }
        }
    }

	/*******************************************************************
	 * 						REPLACEMENT METHODS						   *
	 *******************************************************************/
	private void replacementOptions(String replace, ArrayList<Individual> children) {
		if (replace.equals("replaceWorst"))
			replaceWorst(children);
		if (replace.equals("tournament"))
			tournamentReplacement(children);
	}

	// FITNESS BASED
	private void replaceWorst(ArrayList<Individual> individuals) {
		for(Individual individual : individuals) {
			int idx = getWorstIndex(population);
			population.set(idx, individual);
		}
	}

	private void tournamentReplacement(ArrayList<Individual> children) {
		ArrayList<Individual> candidates = new ArrayList<Individual>();
		while (candidates.size() <= Parameters.tournamentSize -1) {
			Individual toAdd = population.get(Parameters.random.nextInt(population.size()));
			if (!candidates.contains(toAdd))
				candidates.add(toAdd);
		}

		// For every new child (2 new ones) replace them with the worsts in the candidates list from above
		for(Individual child : children) {
			int worst_idx = getWorstIndex(candidates);
			Individual worst_candidate = candidates.remove(worst_idx); // get and remove the worst candidate from the list
			int idx = population.indexOf(worst_candidate); // index of candidate in original population list
			population.set(idx, child); // replace worst in candidates with new child
		}
	}

	// RANDOM
	private void replaceRandom(ArrayList<Individual> children){
		for (Individual child : children){
			Individual random = population.get(Parameters.random.nextInt(population.size()));
			int randIndex = Parameters.random.nextInt(population.size());
			population.set(randIndex, random);
		}
	}

	// Future work - Aged based

	/*******************************************************************
	 * 						DIVERSITY METHODS						   *
	 *******************************************************************/
	// SAWTOOTH
	public void removeIndividual(){	population.remove(getWorst(population)); }

	private void refillPopulation() {
		// refill population with random seeds
		while(population.size() < Parameters.popSize)
		{
			Individual individual = new Individual();
			population.add(individual);
		}
	}

	// Hill Climber
	private Individual hill_climber(Individual i, int iterations) {
		System.out.println("\n---HILL CLIMBER---");
		Individual best = i.copy();
		Individual temp = i.copy(); // the best seed after running the EA
		double before = temp.fitness;

		for (int j = 0; j < iterations; j++)
		{
			// change the value of 1 value
			int mutationChange = Parameters.random.nextInt(1); // check
			int index = Parameters.random.nextInt(temp.chromosome.length);

			// ensure mutation is valid
			if (temp.chromosome[index] + mutationChange <= 1)
				temp.chromosome[index] = temp.chromosome[index] + mutationChange;

			// if after mutation, individual is better, now work on that one
			if (temp.fitness <  best.fitness)
			{
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
