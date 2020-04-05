package coursework;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import model.Fitness;
import model.Individual;
import model.NeuralNetwork;


public class ExampleEvolutionaryAlgorithm extends NeuralNetwork
{
    private String [] initialisation = Parameters.initialisation;
	private String [] selection = Parameters.selection;
	private String [] crossover = Parameters.crossover;
	private String [] mutation = Parameters.mutation;
	private String [] diversity = Parameters.diversity;
	private String [] replacement = Parameters.replacement;

	@Override
	public void run()
	{
		runAlgorithm(initialisation[0], selection[0], crossover[1], mutation[1], diversity[1], replacement[0]);
	}


	public Individual runAlgorithm(String initMode, String selection, String crossover, String mutation, String diversity, String replace)
	{
		//System.out.println("initMode: \t"+initMode);
		//Initialise a population of Individuals with random weights
		population = initialisingOptions(initMode);
		System.out.println("Population initialized: "+population.size());

		//Record a copy of the best Individual in the population
		best = getBest(population);
		System.out.println("Best From Initialisation " + best);
		System.out.println("maxEvaluations "+Parameters.maxEvaluations);

		for(int gen = 0; gen < Parameters.maxEvaluations; gen++)
		{
			System.out.print(gen+" ");

			// DIVERSITY
			if (diversity.equals("sawtooth") && gen % Parameters.reducePopSizeRate == 0){
				System.out.println("SAWTOOTH: Removing individual, pop size: "+population.size());
				removeIndividual(); // every 30 iterations, remove worst individual
				if (population.size() <= Parameters.minPopSize) {
					refillPopulation();
					System.out.println("Population refilled");
				}
			}

			// SELECTING OPTIONS
			Individual parent1 = selectingOptions(selection, population);
			Individual parent2 = selectingOptions(selection, population);

			// CROSSOVER OPTIONS
			ArrayList<Individual> children = crossoverOptions(crossover, parent1, parent2);

			// MUTATION OPTIONS
			mutationOptions(mutation, children);

			// Evaluate new children and replace with worst in population
			evaluateIndividuals(children);

			// REPLACEMENT OPTIONS
			replacementOptions(replace, children, population);

			best = getBest(population);
			outputStats();

			//Increment number of completed generations
		}

		// Hill climber
		//if (diversity.equals("hillclimber"))
		hill_climber(best, 10000);

		//save the trained network to disk
		saveNeuralNetwork();
		System.out.println("best in iteration: "+best.toString());
		return best;
	}



	/*******************************************************************
	 * 				     		USEFUL TOOLS						   *
	 *******************************************************************/

	public void evaluateIndividuals(ArrayList<Individual> individuals) {
		for (Individual individual : individuals) {
			individual.fitness = Fitness.evaluate(individual, this);
		}
	}

	public Individual getBest(ArrayList<Individual> individuals) {
		best = null;;
		for (Individual individual : individuals)
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

	public int getWorstIndex(ArrayList<Individual> individuals) {
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
     * 				    	INITIALISATION METHODS					   *
     *******************************************************************/
    public ArrayList<Individual> initialisingOptions(String initMode) {
        population = new ArrayList<>();
        if (initMode.equals("random"))  population = initialiseRandom();
        if (initMode.equals("best"))    population = initialiseBest();
        return population;
    }

    private ArrayList<Individual> initialiseRandom() {
        for (int i = 0; i < Parameters.popSize; ++i)
        {
            //chromosome weights are initialised randomly in the constructor
            Individual individual = new Individual();
            population.add(individual);
        }
        evaluateIndividuals(population);
        return population;
    }

    private ArrayList<Individual> initialiseBest() {
        for (int i = 0; i < Parameters.popSize; ++i)
        {
            Individual ind1 = new Individual();
            Individual ind2 = new Individual();

            ind1.fitness = Fitness.evaluate(ind1, this);
            ind2.fitness = Fitness.evaluate(ind2, this);

            if (ind1.fitness < ind2.fitness) population.add(ind1);
            else population.add(ind2);
        }
        return population;
    }

	/*******************************************************************
	 * 						SELECTION METHODS						   *
	 *******************************************************************/
	public Individual selectingOptions(String selection, ArrayList<Individual> individuals) {
		Individual parent = new Individual();
		if (selection.equals("select"))     parent = select(individuals);
		if (selection.equals("roulette"))   parent = rouletteSelection(individuals);
		if (selection.equals("tournament")) parent = tournamentSelection(individuals);
		return parent;
	}

	private Individual select(ArrayList<Individual> individuals) {
		Individual parent = individuals.get(Parameters.random.nextInt(individuals.size()));
		return parent.copy();
	}

	private Individual rouletteSelection(ArrayList<Individual> individuals) {
		Individual parent = new Individual();

		double total = 0;
		for(Individual i:individuals)
		{
			total += 1 / i.fitness;
			parent = i;
		}

		double spinner = total * ThreadLocalRandom.current().nextDouble(0, 1);

		double count = 0;
		for(Individual i:individuals)
		{
			count += 1 / i.fitness;
			if (count >= spinner)
				return i;
		}

		return parent;
	}

	private Individual tournamentSelection(ArrayList<Individual> individuals) {
		ArrayList<Individual> candidates = new ArrayList<Individual>();
		for(int i = 0; i < Parameters.tournamentSize; i++)
		{
			candidates.add(individuals.get(Parameters.random.nextInt(individuals.size())));
		}
		return getBest(individuals).copy();
	}

	/*******************************************************************
	 * 						CROSSOVER METHODS						   *
	 *******************************************************************/
	public ArrayList<Individual> crossoverOptions(String crossover, Individual parent1, Individual parent2) {
		ArrayList<Individual> children = new ArrayList<>();
		if (crossover.equals("reproduce"))      children = reproduce(parent1, parent2);
		if (crossover.equals("uniform"))        children = uniformCrossover(parent1, parent2);
		if (crossover.equals("doublepoint"))    children = doublePointCrossover(parent1, parent2);
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
	public void mutationOptions(String mutation, ArrayList<Individual> children) {
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
						individual.chromosome[i] += (Parameters.mutateChange);
					else
						individual.chromosome[i] -= (Parameters.mutateChange);
				}
			}
		}
	}

	private void swapMutation(ArrayList<Individual> individuals) {
		for(Individual child : individuals)
		{
			// mutation probability
			if(Parameters.random.nextDouble() < Parameters.mutateRate)
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

	private void constrainedMutation(ArrayList<Individual> individuals) {
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
	public void replacementOptions(String replace, ArrayList<Individual> children, ArrayList<Individual> individuals) {
		if (replace.equals("replaceWorst"))
			replaceWorst(children, individuals);
		if (replace.equals("tournament"))
			tournamentReplacement(children);
	}

	// FITNESS BASED
	private void replaceWorst(ArrayList<Individual> children, ArrayList<Individual> individuals) {
		for(Individual individual : children) {
			int idx = getWorstIndex(individuals);
			individuals.set(idx, individual);
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
	private void removeIndividual(){population.remove(getWorst(population)); }

	private void refillPopulation() {
		// refill population with random seeds
		while(population.size() < Parameters.popSize)
		{
			Individual individual = new Individual();
			population.add(individual);
		}
	}

	// Hill Climber
	private Individual hill_climber(Individual indv, int iterations) {
		System.out.println("\n---HILL CLIMBER---");
		Individual best_i = indv;
		//run for max evaluations
		for(int gen = 0; gen < iterations; gen++) {
			//mutate the best
			Individual candidate = mutateIndv(best_i);

			if (gen % 500 == 0) System.out.println(gen + " - "+best_i.fitness);

			//accept if better
			if(candidate.fitness < best_i.fitness) {
				best_i = candidate;
				System.out.println(candidate.fitness + " - "+best_i.fitness);
			}
		}
		return best_i;
	}

	private Individual mutateIndv(Individual mutateThis) {
		Individual indv = mutateThis.copy();
		for (int i = 0; i < indv.chromosome.length; i++) {
			if (Parameters.random.nextDouble() < Parameters.mutateRate) {
				if (Parameters.random.nextBoolean()) {
					indv.chromosome[i] += (Parameters.mutateChange);
				} else {
					indv.chromosome[i] -= (Parameters.mutateChange);
				}
			}
		}
		Fitness.evaluate(indv, this);
		return indv;
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
