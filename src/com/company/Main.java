package com.company;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Scanner;

public class Main {
    static ArrayList<ArrayList<Integer>> upperLegend;
    static ArrayList<ArrayList<Integer>> leftLegend;

    static int height, width;
    final static int ITERATIONS_NR = 5;
    final static int ISLANDS_NR = 5;
    final static int GENERATIONS = 1000000; // na tomto cisle nezalezi
    final static double CROSSINTERVAL = 0.001;
    final static double CATASTROPHE = 0.0002;
    final static boolean VISUAL_ENABLED = false;
    final static boolean CROWDING = true; // deterministic crowding
    static int fitnessCounted;
    final static int fitnessCountCeil = 200 * 50000; // 200 ohodnoceni ~ 1 generace

    static void readInput(String inputName) {
        Scanner in = null;
        Scanner rowScanner;

        try {
            in = new Scanner(new FileReader(inputName));
        } catch (FileNotFoundException ex) {
            System.out.println("Can't find file " + inputName);
        }

        String row = in.nextLine(); // to skip first line

        ArrayList<Integer> newRow;

        while (true) {
            row = in.nextLine();
            if (row.startsWith("columns")) {
                break;
            }

            newRow = new ArrayList<>();
            rowScanner = new Scanner(row);

            while (rowScanner.hasNextInt()) {
                newRow.add(rowScanner.nextInt());
            }
            leftLegend.add(newRow);
        }

        while (in.hasNext()) {
            row = in.nextLine();
            newRow = new ArrayList<>();

            rowScanner = new Scanner(row);
            while (rowScanner.hasNextInt()) {
                newRow.add(0, rowScanner.nextInt());
            }
            upperLegend.add(newRow);
        }
    }

    public static void main(String[] args) {
        upperLegend = new ArrayList<>();
        leftLegend = new ArrayList<>();

        readInput("25x20.txt");

        width = upperLegend.size();
        height = leftLegend.size();

        for (int iterace = 0; iterace < ITERATIONS_NR; iterace++) {
            System.out.println(iterace + ". " + new SimpleDateFormat("HH:mm:ss").format(new Date()));

            fitnessCounted = 0;
            Island[] islands = new Island[ISLANDS_NR];
            for (int i = 0; i < ISLANDS_NR; i++) {
                islands[i] = new Island(i);
            }

            for (int g = 0; g < GENERATIONS; g++) {
                if (g % 1000 == 0) {
                    System.out.println("generation " + g);
                }
                if (fitnessCountCeil < fitnessCounted) {
                    break;
                }

                for (int i = 0; i < ISLANDS_NR; i++) {
                    generation(islands, g, i);
                }
            }
            double sumBestScore = 0;
            double sumBestIndividual = 0;

            for (int i = 0; i < ISLANDS_NR; i++) {
                sumBestScore += islands[i].bestScore;
                sumBestIndividual += islands[i].bestIndividual.fitness;
                System.out.println(islands[i].bestScore);
            }
            System.out.println("Best Avg: " + sumBestScore / ISLANDS_NR);
            System.out.println("Current Avg: " + sumBestIndividual / ISLANDS_NR);
        }
        System.out.println(fitnessCounted);
    }

    public static void generation(Island[] Islands, int g, int i) {
        if (fitnessCountCeil < fitnessCounted) {
            return;
        }

        if (CROWDING) {
            Islands[i].optimiseCrowd(g);
        } else {
            Islands[i].optimise(g);
        }

        // Crossover to other islands
        if (Math.random() < CROSSINTERVAL && 1 < ISLANDS_NR) {
            System.out.println("Island tranfer from " + i);
            for (int j = 0; j < ISLANDS_NR; j++) {
                if (i == j) continue;
                Islands[j].population.add(new Individual(Islands[i].bestIndividual));
                Collections.sort(Islands[j].population);
            }
        }

        // Catastrophe - randomly deletes half of the population including the best individual
        if (Math.random() < CATASTROPHE) {
            System.out.println("Catastrophe on island " + i);

            if (!CROWDING) {
                Islands[i].population.remove(0);
                for (int j = 0; j < Island.populationSize / 2; j++) {
                    int rem = (int) (Math.random() * Islands[i].population.size());
                    Islands[i].population.remove(rem);
                }
            }

            for (int j = 0; j < Islands[i].population.size(); j++) {
                for (int k = 0; k < 5; k++) {
                    Islands[i].population.get(j).mutate();
                }
                Islands[i].population.get(j).computeAndSetFitness();
            }
        }
    }
}
