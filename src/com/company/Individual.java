package com.company;

import java.util.ArrayList;

/**
 * Created by cejkis on 8.11.15.
 */


public class Individual implements  Comparable<Individual>{

    ArrayList<ArrayList<Integer>> gapSizes;
    boolean[][] legend;
    int fitness;
    int birth;

    public Individual(int generationNr) {
        legend = new boolean[Main.height][Main.width];
        gapSizes = new ArrayList<>();
        birth = generationNr;
    }

    public Individual(Individual s){
        legend = new boolean[Main.height][Main.width];
        gapSizes = new ArrayList<>();

        for (int i = 0; i < Main.height; i++) {
            gapSizes.add(new ArrayList<>());
        }
        for (int i = 0; i < s.gapSizes.size(); i++) {
            ArrayList<Integer> row = s.gapSizes.get(i) ;
            for (Integer number: row ){
                gapSizes.get(i).add(number);
            }
        }
        fillTableAccordingToLegendAndGaps();
        fitness = s.fitness;
        birth = s.birth;
    }

    public void basicInit(){
        for (int i = 0; i < Main.height; i++) {
            gapSizes.add(new ArrayList<>());
        }
        createSpaces();
        fillTableAccordingToLegendAndGaps();
    }

    public void printTable() {
        for (int i = 0; i < Main.height; i++) {
            printRow(i, gapSizes.get(i));
        }
    }

    public void printRow(int i, ArrayList<Integer> spaces) {
        ArrayList<Integer> fills = Main.leftLegend.get(i);

        for (int j = 0; j < spaces.get(0); j++) {
            System.out.print("  ");
        }

        for (int j = 0; j < fills.size(); j++) {
            // print one box
            for (int k = 0; k < fills.get(j); k++) {
                System.out.print("##");
            }

            for (int k = 0; k < spaces.get(j + 1); k++) {
                System.out.print("  ");
            }
        }
        System.out.print("|");
        System.out.println();
    }

    public void createSpaces() {
        ArrayList<Integer> boxSizes;
        ArrayList<Integer> spaces;
        int size;

        for (int rowNr = 0; rowNr < Main.height; rowNr++) {

            boxSizes = Main.leftLegend.get(rowNr);
            spaces = gapSizes.get(rowNr);

            if (boxSizes.isEmpty()) { // Probably never happens
                spaces.add(Main.width);
            } else {
                size = 0;
                for (Integer aVelikostiPoli : boxSizes) { //j is an order of number in a row
                    size += aVelikostiPoli;
                }

                size += boxSizes.size() - 1;
                double rest = Main.width - size;
                spaces.add((int) Math.ceil(rest / 2));

                for (int j = 0; j < boxSizes.size() - 1; j++) {
                    spaces.add(1);
                }
                spaces.add((int) Math.floor(rest / 2));
            }
        }
    }

    // counts sum of N-W for each column
    public int countFitness() {
        Main.fitnessCounted ++;

        int suma = 0;

        for (int i = 0; i < Main.width; i++) {
            suma += needlemanWunch(Main.upperLegend.get(i), arraylistFromTable(i));
        }
        return suma;
    }

    // According to legend and current state computes NW function for one row or column
    public static int needlemanWunch(ArrayList<Integer> legend, ArrayList<Integer> table) {
        ArrayList<Integer> x = new ArrayList<>(legend);
        ArrayList<Integer> y = new ArrayList<>(table);

        x.add(0, 0);
        y.add(0, 0);

        // helper matrix for needleman wunch algorithm
        int[][] h = new int[y.size()][x.size()];

        h[0][0] = 0;

        for (int i = 1; i < y.size(); i++) {
            h[i][0] = h[i - 1][0] - y.get(i);
        }

        for (int i = 1; i < x.size(); i++) {
            h[0][i] = h[0][i - 1] - x.get(i);
        }

        for (int j = 1; j < x.size(); j++) {
            for (int i = 1; i < y.size(); i++) {
                h[i][j] = Math.max(
                        h[i - 1][j] - y.get(i),
                        Math.max(
                                h[i][j - 1] - x.get(j),
                                h[i - 1][j - 1] - Math.abs(x.get(j) - y.get(i))
                        )
                );
            }
        }
        return h[y.size() - 1][x.size() - 1];
    }

    // returns column of table in merged format
    public ArrayList<Integer> arraylistFromTable(int columnNr) {
        ArrayList<Integer> result = new ArrayList<>();
        int streak = 0;

        for (int rowNr = 0; rowNr < Main.height; rowNr++) {
            if (legend[rowNr][columnNr]) {
                streak++;
            } else {
                if (streak != 0) {
                    result.add(streak);
                }
                streak = 0;
            }
        }

        if (streak != 0) {
            result.add(streak);
        }
        return result;
    }

    ArrayList<Integer> findWhatCanBeRemoved(int row) {
        ArrayList<Integer> spaceIndecesThatCanBeRemoved = new ArrayList<>();
        ArrayList<Integer> spacesInRow = gapSizes.get(row);

        // first space
        if (spacesInRow.get(0) > 0) {
            spaceIndecesThatCanBeRemoved.add(0);
        }

        // last space
        if (spacesInRow.size() > 1 && spacesInRow.get(spacesInRow.size() - 1) > 0) {
            spaceIndecesThatCanBeRemoved.add(spacesInRow.size() - 1);
        }

        // find places to insert and remove inside the row
        for (int j = 1; j < spacesInRow.size() - 1; j++) {
            if (spacesInRow.get(j) > 1) {
                spaceIndecesThatCanBeRemoved.add(j);
            }
        }
        return spaceIndecesThatCanBeRemoved;
    }

    public void swapSpaceInRow(int radek, ArrayList<Integer> spaces) {
        int i, j, sizeToRemove, indexToRemove;

        ArrayList<Integer> removeableSpacesIndeces = findWhatCanBeRemoved(radek);

        if (removeableSpacesIndeces.isEmpty()) return;

        do {
            i = (int) (Math.random() * removeableSpacesIndeces.size());
            j = (int) (Math.random() * spaces.size());
        } while (removeableSpacesIndeces.get(i) == j);
        //   System.out.println(i + " * " +j );

        // change spaces
        indexToRemove = removeableSpacesIndeces.get(i);

        // when removing from the first or the last gap, all space can be removed
        if (indexToRemove == 0 || indexToRemove == spaces.size() - 1) {
            sizeToRemove = (int) (Math.random() * (spaces.get(indexToRemove))) + 1;
        } else sizeToRemove = (int) (Math.random() * (spaces.get(indexToRemove) - 1)) + 1;

        spaces.set(indexToRemove, spaces.get(indexToRemove) - sizeToRemove);
        spaces.set(j, spaces.get(j) + sizeToRemove);
    }

    public void mutate() {
        int changedRow = (int) (Math.random() * Main.height);
        ArrayList<Integer> spacesBeingChanged = gapSizes.get(changedRow);

        if (spacesBeingChanged.size() <= 1) {
            return;
        }

        swapSpaceInRow(changedRow, spacesBeingChanged);
        fillTableRow(changedRow, spacesBeingChanged);
    }

    public void computeAndSetFitness(){
        fitness = countFitness();
    }

    // Fills gaps, boxes and one centred
    public void fillTableAccordingToLegendAndGaps() {
        for (int i = 0; i < Main.height; i++) { // i je radek
            fillTableRow(i, gapSizes.get(i));
        }
    }

    public void fillTableRow(int row, ArrayList<Integer> gapsInRow) {
        ArrayList<Integer> filledBoxes = Main.leftLegend.get(row);
        int pointer = 0; // to index that is being changed

        for (int j = 0; j < gapsInRow.get(0); j++) { // first gap
            legend[row][pointer] = false;
            pointer++;
        }
        //   System.out.println( row + " * " + filledBoxes.size() + " " + gapsInRow.size());

        for (int j = 0; j < filledBoxes.size(); j++) { // for all boxes
            for (int k = 0; k < filledBoxes.get(j); k++) {
                legend[row][pointer] = true;
                pointer++;
            }

            for (int k = 0; k < gapsInRow.get(j + 1); k++) {
                legend[row][pointer] = false;
                pointer++;
            }
        }
    }

    @Override
    public int compareTo(Individual individual) {
        return individual.fitness - fitness;
    }

    int bestFitnessEver;
    ArrayList<Integer> bestGaps;
    Integer changedRow;
    ArrayList<Integer> gapsBackup;

    public Individual localOptimization(int iterations){
        bestFitnessEver = fitness;
        int tolerance = 6;
        if (bestFitnessEver >= -60 ) tolerance = 4;
        if (bestFitnessEver >= -26 ) tolerance = 2;
        if (bestFitnessEver >= -2 ) tolerance = 0;

        Individual best = new Individual(this);

        // repeat optimization
        for (int p = 0; p < iterations; p++) {
            swapGaps();
            if (fitness >= bestFitnessEver - tolerance) {
                if(fitness >= best.fitness){
                    best = new Individual(this);
                }

                if (bestFitnessEver >= -60 ) tolerance = 4;
                if (bestFitnessEver >= -26 ) tolerance = 2;
                if (bestFitnessEver >= -2 ) tolerance = 0;

                if (fitness > bestFitnessEver){
                    bestFitnessEver = fitness;
                }

//                if (bestGaps != null) {
//                    gapSizes.set(changedRow, new ArrayList<Integer>(bestGaps));
//                    fillTableRow(changedRow, gapSizes.get(changedRow));
//                    fitness = candidatesFitness;
//                }

                if (fitness == 0) {
                    System.out.println("Got best nonogram through optimization");
                    return this;
                }
            } else {
                // return original values
                gapSizes.set(changedRow, gapsBackup);
                fillTableRow(changedRow, gapsBackup);
            }

//            if (p % 1000 == 0) {
//                System.out.println(p + ". round. fitness: "
//                        + fitness + " iterations without improvement " + iterationsWithoutImprovement);
//            }
        }
        return best;
    }

    public void swapGaps() {
        changedRow = (int) (Math.random() * Main.height);
        ArrayList<Integer> gapsToBeChanged = gapSizes.get(changedRow);
        gapsBackup = new ArrayList<>(gapSizes.get(changedRow));

        if (gapsToBeChanged.size() == 1) {
            return;
        }

        swapSpaceInRow(changedRow, gapsToBeChanged);
        fillTableRow(changedRow, gapsToBeChanged);
        fitness = countFitness();
        bestGaps = gapsToBeChanged;
    }
}
