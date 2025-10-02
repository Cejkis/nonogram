package com.company;

import java.util.*;

/**
 * Created by Čejkis on 19.04.2017.
 */
public class Island {

    static int populationSize = 200;
    static int selectionSize = 75;
    static int offspringSize = 200;

    public ArrayList<Individual> population;
    public Individual bestIndividual;

    static double probCrossoverWithBest = 0.0;
    static double probPopulationMutation = 1;
    static double probOffspringMutation = 1;

    final static int INTERVALS = 200;
    final static int LOCAL_SEARCH_ITERATIONS = 500;

    int bestScore = -1000000000;
    int islandNr;

    private VizWindow frame;

    public Island(int islandNr) {
        if (Main.VISUAL_ENABLED) {
            frame = new VizWindow(Main.width, Main.height, islandNr);
        }

        this.islandNr = islandNr;
        population = initPopulation(0);
    }

    public int computeDifference(Individual i1, Individual i2) {
        int diff = 0;

        for (int i = 0; i < Main.height; i++) {
            for (int j = 0; j < Main.width; j++) {
                if (i1.legend[i][j] != i2.legend[i][j]) {
                    diff++;
                }
            }
        }
        return diff;
    }

    public void optimiseCrowd(int g) {
        int bestScr = -1000000;

        for (Individual i: population){
            if( bestScr < i.fitness ){
                bestIndividual = i;
                bestScr = i.fitness;
            }
        }

        if (Main.VISUAL_ENABLED && g % 10 == 0) { // update viz every 10 generations
            frame.printIndividual(bestIndividual, g, islandNr);
        }
        if (bestIndividual.fitness > bestScore) {
            bestScore = bestIndividual.fitness;
            if (Main.VISUAL_ENABLED) {
                frame.printBestEver(bestIndividual, g, islandNr);
            }
        }

        // local optimization
        if (g % INTERVALS == 0 && g != 0) {
            for (int i = 0; i < population.size() ; i++) {
                if(Math.random() < 0.1)
                    population.set(i, population.get(i).localOptimalization(LOCAL_SEARCH_ITERATIONS));
            }

            if (bestIndividual.fitness >= -14) {
                bestIndividual = bestIndividual.localOptimalization(LOCAL_SEARCH_ITERATIONS * 5);
            } else {
                bestIndividual = bestIndividual.localOptimalization(LOCAL_SEARCH_ITERATIONS);
            }
        }

        // couple individuals, create offspring
        Collections.shuffle(population);

        for (int i = 0; i < populationSize; i += 2) {
            int n1 = i;
            int n2 = i + 1;

            Individual p1 = population.get(n1);
            Individual p2 = population.get(n2);

            Individual c1 = crossover(p1, p2, g);
            Individual c2 = crossover(p1, p2, g);

            // mutate c1?
            if (Math.random() < probOffspringMutation) {
                c1.mutate();
            }
            if (Math.random() < probOffspringMutation) {
                c2.mutate();
            }

            c1.computeAndSetFitness();
            c2.computeAndSetFitness();

            if (computeDifference(p1, c1) + computeDifference(p2, c2) < computeDifference(p1, c1) + computeDifference(p2, c2)) {
                if(c1.fitness >= p1.fitness){
                    population.set(n1,c1);
                }
                if(c2.fitness >= p2.fitness){
                    population.set(n2,c2);
                }
            } else{
                if(c2.fitness >= p1.fitness){
                    population.set(n1,c2);
                }
                if(c1.fitness >= p2.fitness){
                    population.set(n2,c1);
                }
            }
        }

        //if (g % 50 == 0) {
        // statistiky(populace);
        //  System.out.println(" V case " +(double)(System.nanoTime() - startTime) / 1000000000.0);
        //}
        //  System.out.println(" V case " +(double)(System.nanoTime() - startTime) / 1000000000.0);
        //  System.out.println("fitness spocteno " + fitnessCounted);

        if (population.get(0).fitness == 0) {
            stats(population);
            System.out.println("Solution found in generation " + g);
            population.get(0).printTable();
            System.exit(0);
        }
    }

    public void optimise(int g) {
        // keep the best individual
        bestIndividual = population.get(0);

        if (Main.VISUAL_ENABLED && g % 10 == 0) {
            frame.printIndividual(bestIndividual, g, islandNr);
        }

        // do local search from the best individual
//        if(g % INTERVALS == 0 && g != 0) {
//            for (int i = 0; i < population.size() ; i++) {
//                if(Math.random() < 0.1)
//                    population.set(i, population.get(i).localOptimalization(LOCAL_SEARCH_ITERATIONS));
//            }
//
//            if(bestIndividual.fitness >= -14) {
//                bestIndividual = bestIndividual.localOptimalization(LOCAL_SEARCH_ITERATIONS * 5);
//            } else {
//                bestIndividual = bestIndividual.localOptimalization(LOCAL_SEARCH_ITERATIONS);
//            }
//        }

        if (bestIndividual.fitness > bestScore) {
            bestScore = bestIndividual.fitness;
            if (Main.VISUAL_ENABLED) {
                frame.printBestEver(bestIndividual, g, islandNr);
            }
            //System.out.println(islandNr + " " + bestIndividual.birth);
        }

        // select parents, make offspring
        ArrayList<Individual> rodiceAsArray = new ArrayList<>(selectParents(population));
        ArrayList<Individual> offspring = new ArrayList<>();

        for (int i = 0; i < offspringSize; i++) {
            offspring.add(
                crossover(
                        rodiceAsArray.get((int) (Math.random() * selectionSize)),
                        rodiceAsArray.get((int) (Math.random() * selectionSize)),
                        g
                )
            );
        }

        // lets crossover few individuals with the best
        for (int i = 1; i < populationSize; i++) {
            if (Math.random() < probCrossoverWithBest)
                offspring.add(crossover(bestIndividual, population.get(i), g));
        }

        // offspring mutation
        for (Individual child : offspring) {
//            if (child.fitness == 0) {
//                System.out.println("** Solution found in generation " + g);
//                child.printTable();
//                return;
//            }
            if (Math.random() < probOffspringMutation) {
                child.mutate();
            }

            child.computeAndSetFitness();
        }

        // randomly mutate part of the old population
        for (Individual i : population) {
            if (i == bestIndividual) continue;

            if (Math.random() < probPopulationMutation) {
                i.mutate();
                i.computeAndSetFitness();
            }
            offspring.add(i);
        }

//         offspring.add(bestIndividual);

        population = offspring;

        //if (g % 50 == 0) {
        // stats(population);
        //  System.out.println("In time " + (double)(System.nanoTime() - startTime) / 1000000000.0);
        //}

        //  System.out.println("In time " + (double)(System.nanoTime() - startTime) / 1000000000.0);
        //  System.out.println("fitness counted " + fitnessCounted);

        Collections.sort(population);

        if (population.get(0).fitness == 0) {
            stats(population);
            System.out.println("Solution found in generation " + g);
            population.get(0).printTable();
            System.exit(0);
        }

        // removing the worst to keep the population size constant
        for (int i = population.size() - 1; i >= populationSize; i--) {
            population.remove(i);
        }
    }

    static Individual crossover2(Individual a, Individual b, int gen) {
        Individual c = new Individual(gen);

        int breakingPoint = (int) (Math.random() * Main.height - 2) + 1;

        for (int i = 0; i < breakingPoint; i++) {
            c.gapSizes.add(new ArrayList<>(a.gapSizes.get(i)));
        }

        for (int i = breakingPoint; i < Main.height; i++) {
            c.gapSizes.add(new ArrayList<>(b.gapSizes.get(i)));
        }

        c.fillTableAccordingToLegendAndGaps();
        return c;
    }

    static Individual crossover(Individual a, Individual b, int gen) {
        Individual c = new Individual(gen);

        for (int i = 0; i < Main.height; i++) {
            if (Math.random() > 0.5) {
                c.gapSizes.add(new ArrayList<>(a.gapSizes.get(i)));
            } else {
                c.gapSizes.add(new ArrayList<>(b.gapSizes.get(i)));
            }
        }

        c.fillTableAccordingToLegendAndGaps();
        return c;
    }

    public static void stats(ArrayList<Individual> populace) {
        //  double averageFitness = 0;
        int highestFitness = populace.get(0).fitness;
        System.out.print(highestFitness + ";");

//        int lowestFitness = populace.get(0).fitness;

        // int sum = 0;

//        for (Individual i : populace) {
//            sum += i.fitness;
//
//            if (i.fitness > highestFitness) {
//                highestFitness = i.fitness;
//            }
//            if (i.fitness < lowestFitness) {
//                lowestFitness = i.fitness;
//            }
//        }
    }

    // tournament method
    public static Set<Individual> selectParents(ArrayList<Individual> populace) {
        Set<Individual> parents = new HashSet<>();

        while (parents.size() != selectionSize) {
            Individual a = populace.get((int) (Math.random() * populace.size()));
            Individual b = populace.get((int) (Math.random() * populace.size()));

            if (a.fitness > b.fitness) {
                parents.add(a);
            } else {
                parents.add(b);
            }
        }
        return parents;
    }

    public static ArrayList<Individual> initPopulation(int g) {
        ArrayList<Individual> p = new ArrayList<>();

        for (int i = 0; i < populationSize; i++) {
            Individual j = new Individual(g);
            j.basicInit();

            for (int k = 0; k < Main.height * 5; k++) {
                j.mutate();
            }

            j.computeAndSetFitness();

            if (j.fitness == 0) {
                j.fitness = j.countFitness();
            }
            p.add(j);
        }
        return p;
    }
}
