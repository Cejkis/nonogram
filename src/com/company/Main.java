package com.company;

import java.util.ArrayList;

public class Main {

    static ArrayList<ArrayList<Integer>> horniLegenda;

    static ArrayList<ArrayList<Integer>> levaLegenda;
    static ArrayList<ArrayList<Integer>> velikostiMezer;

    static Integer ZmenenyRadek=0;
    static ArrayList<Integer> lepsiMezery;

    static boolean[][] tajenka;

    static int vyska = 5;
    static int sirka = 5;
    static int soucasnyFitness;
    static int nejnizsifitness;
    static int fitnessKandidata;

    public static void printPole() {

        for (int i = 0; i < vyska; i++) {
            printRadek(i, velikostiMezer.get(i));
        }

    }

    public static void printRadek(int i, ArrayList<Integer> Mezery) {

        ArrayList<Integer> policka = levaLegenda.get(i);
        System.out.print( i + "");

        if(policka.size() +1 != Mezery.size() ){

            System.out.println("*alert policek a mezer" + policka.size() +","+ Mezery.size());

        }

        for (int j = 0; j < Mezery.get(0); j++) {
            System.out.print(" ");
        }

        for (int j = 0; j < policka.size(); j++) {

            // vytisknu jedno policko
            for (int k = 0; k < policka.get(j); k++) {
                System.out.print("#");
            }

            for (int k = 0; k < Mezery.get(j + 1); k++) {
                System.out.print(" ");
            }

        }

        System.out.print("|");
        System.out.println();

    }

    public static void initializeVariables() {

        horniLegenda = new ArrayList<>(sirka);
        levaLegenda = new ArrayList<>(vyska);
        velikostiMezer = new ArrayList<>(vyska);

        tajenka = new boolean[vyska][sirka];

        for (int i = 0; i < sirka; i++) {
            horniLegenda.add(new ArrayList<Integer>());
        }

        for (int i = 0; i < vyska; i++) {
            levaLegenda.add(new ArrayList<Integer>());
        }

        for (int i = 0; i < vyska; i++) {
            velikostiMezer.add(new ArrayList<Integer>());
        }

        horniLegenda.get(0).add(1);
        horniLegenda.get(0).add(1);
        horniLegenda.get(1).add(1);
        horniLegenda.get(1).add(2);
        horniLegenda.get(2).add(3);
        horniLegenda.get(2).add(1);
        horniLegenda.get(3).add(2);
        horniLegenda.get(3).add(1);
        horniLegenda.get(4).add(1);
        horniLegenda.get(4).add(1);

        levaLegenda.get(0).add(1);
        levaLegenda.get(0).add(1);
        levaLegenda.get(1).add(3);
        levaLegenda.get(2).add(1);
        levaLegenda.get(2).add(2);
        levaLegenda.get(3).add(1);
        levaLegenda.get(3).add(1);
        levaLegenda.get(4).add(4);

    }

    // Vyplni mezery, mezery 1 a vycentrovana doprostred
    public static void vytvorMezery() {

        ArrayList<Integer> VelikostiPoli;
        ArrayList<Integer> Mezery;

        for (int i = 0; i < vyska; i++) { // i je radek


            VelikostiPoli = levaLegenda.get(i);
            Mezery = velikostiMezer.get(i);

            if (VelikostiPoli.isEmpty()) { // nebo 1? Nejspis nikdy nenastane
                Mezery.add(sirka);
            } else {

                int velikost = 0;

                for (int j = 0; j < VelikostiPoli.size(); j++) { //j je poradi cisla v radku
                    velikost += VelikostiPoli.get(j);
                }

                velikost += VelikostiPoli.size() - 1;

                double zbytek = sirka - velikost;

                Mezery.add((int) Math.ceil(zbytek / 2));

                for (int j = 0; j < VelikostiPoli.size() - 1; j++) {
                    Mezery.add(1);
                }

                Mezery.add((int) Math.floor(zbytek / 2));

            }
            if(VelikostiPoli.size() +1 != Mezery.size() ){

                System.out.println("ALERT policek a mezer" + VelikostiPoli.size() +","+ Mezery.size());

            }
        }

    }

    public static void vyplnCelouTajenkuPodleLegendyAMezer() {

        for (int i = 0; i < vyska; i++) { // i je radek
            VyplnRadekTajenky(i, velikostiMezer.get(i));
        }
    }

    public static void VyplnRadekTajenky(int radek, ArrayList<Integer> mezeryVRadku) {

        ArrayList<Integer> polickaVRadku = levaLegenda.get(radek);

        int pointer = 0; // ukazatel na pozici, kterou menim

        for (int j = 0; j < mezeryVRadku.get(0); j++) { // prvni mezera
            tajenka[radek][pointer] = false;
            pointer++;
        }

        for (int j = 0; j < polickaVRadku.size(); j++) { // pro vsehna policka

            for (int k = 0; k < polickaVRadku.get(j); k++) {
                tajenka[radek][pointer] = true;
                pointer++;
            }
            //   System.out.println("*" + radek + "*" + mezeryVRadku.get(j + 1));

            for (int k = 0; k < mezeryVRadku.get(j + 1); k++) {
                tajenka[radek][pointer] = false;
                pointer++;
            }
        }

    }

    // podle tajenky a legendy spocte needlemana pro jeden radek/sloupec
    public static int needlemanWunch(ArrayList<Integer> legenda, ArrayList<Integer> tajenka) {

        ArrayList<Integer> x = new ArrayList<>(legenda);
        ArrayList<Integer> y = new ArrayList<>(tajenka);

        x.add(0, 0);
        y.add(0, 0);

        int[][] H = new int[y.size()][x.size()];

        H[0][0] = 0;

        for (int i = 1; i < y.size(); i++) {
            H[i][0] = H[i - 1][0] - y.get(i);
        }

        for (int i = 1; i < x.size(); i++) {
            H[0][i] = H[0][i - 1] - x.get(i);
        }

        for (int j = 1; j < x.size(); j++) {
            for (int i = 1; i < y.size(); i++) {

                H[i][j] = Math.max(H[i - 1][j] - y.get(i),
                        Math.max(H[i][j - 1] - x.get(j),
                                H[i - 1][j - 1] - Math.abs(x.get(j) - y.get(i))));

            }
        }

        return H[y.size() - 1][x.size() - 1];
    }

    // vraci sloupec tajenky ve jako ve "sloucenem" tvaru
    public static ArrayList<Integer> arraylistFromPole(int sloupec) {

        ArrayList<Integer> a = new ArrayList();

        int kombo = 0;

        for (int i = 0; i < vyska; i++) {

            if (tajenka[i][sloupec]) {

                kombo++;
            } else {
                if (kombo != 0) {
                    a.add(kombo);
                }
                kombo = 0;
            }

        }

        if (kombo != 0) {
            a.add(kombo);
        }

        return a;
    }

    // spocte sumu needlemanu vsech sloupcu
    public static int spoctiFitness() {

        int suma = 0;

        for (int i = 0; i < horniLegenda.size(); i++) {
            suma += needlemanWunch(horniLegenda.get(i), arraylistFromPole(i));
        }

        return suma;
    }

    // vypise na sout hodnoty needlemana pro kazdy sloupec
    public static void vypisNeedlemanaProSloupce() {

        for (int i = 0; i < horniLegenda.size(); i++) {
            System.out.println("needleman " + i + ": " + needlemanWunch(horniLegenda.get(i), arraylistFromPole(i)));
        }

    }

    public static void prehazimMezery(int radek, ArrayList<Integer> IndexyMezerKtereMuzuUbrat) {

        ArrayList<Integer> noveMezery = velikostiMezer.get(radek);

        for (int i = 0; i < IndexyMezerKtereMuzuUbrat.size(); i++) { // IndexyMezerKtereMuzuUbrat obsahuje indexy mezer, ktere muzu ubrat
            for (int j = 0; j < noveMezery.size(); j++) { // velikosti mezer
                if (IndexyMezerKtereMuzuUbrat.get(i) == j) {
                    continue;
                }

                int indexZeKterehoUbiram = IndexyMezerKtereMuzuUbrat.get(i);

                noveMezery.set(indexZeKterehoUbiram, noveMezery.get(indexZeKterehoUbiram) - 1);
                noveMezery.set(j, noveMezery.get(j) + 1);

                // uprav radek v tajence
                // printRadek(radek, noveMezery);
                VyplnRadekTajenky(radek, noveMezery);

                // otestuj
                fitnessKandidata = spoctiFitness();

                if (fitnessKandidata >= nejnizsifitness && fitnessKandidata >= soucasnyFitness) {
                    nejnizsifitness = fitnessKandidata;

                    ZmenenyRadek = radek;
                    lepsiMezery = (ArrayList)noveMezery.clone();

                }

                //vrat
                noveMezery.set(indexZeKterehoUbiram, noveMezery.get(indexZeKterehoUbiram) + 1);
                noveMezery.set(j, noveMezery.get(j) - 1);
                VyplnRadekTajenky(radek, noveMezery);

            }
        }
    }

    public static void main(String[] args) {

        initializeVariables();
        vytvorMezery();

        vyplnCelouTajenkuPodleLegendyAMezer();

        soucasnyFitness = spoctiFitness();
        System.out.println("soucasny fitness:" + soucasnyFitness);
        nejnizsifitness = soucasnyFitness;

        ArrayList<Integer> MuzuUbrat = new ArrayList();

        for (int p = 0; p < 30; p++) { // opakovani optimalizace
            System.out.println("");
            System.out.println( p+1 + ". KOLO");
            printPole();

            for (int i = 0; i < vyska; i++) { // radek po radku

                ArrayList<Integer> mezeryVAktualnimRadku = velikostiMezer.get(i);

                if (mezeryVAktualnimRadku.isEmpty()) {
                    continue;
                }

                // System.out.println();
                // System.out.println("radek:" + i);

                MuzuUbrat.clear();

                // muzu vlozit do vsech mezer (???)
                // prvni mezera
                if (mezeryVAktualnimRadku.get(0) > 0) {
                    MuzuUbrat.add(0);
                }

                // posledni mezera
                if (mezeryVAktualnimRadku.size() > 1 && mezeryVAktualnimRadku.get(mezeryVAktualnimRadku.size() - 1) > 0) {
                    MuzuUbrat.add(mezeryVAktualnimRadku.size() - 1);
                }

                for (int j = 1; j < mezeryVAktualnimRadku.size() - 1; j++) { // uvnitr radku najdu mista na vkladani a vybirani

                    if (mezeryVAktualnimRadku.get(j) > 1) {
                        MuzuUbrat.add(j);
                    }
                }

                prehazimMezery(i, MuzuUbrat);

            }

            if (nejnizsifitness >= soucasnyFitness) {

                velikostiMezer.set(ZmenenyRadek,lepsiMezery);

                soucasnyFitness = nejnizsifitness;

                System.out.println();
                System.out.println("zlepsuju moje pole" + nejnizsifitness + " zmena v radku " + ZmenenyRadek);
                printPole();

                if (soucasnyFitness == 0) {
                    System.out.println("MAM SPRAVNY NONOGRAM!!!");
                    break;
                }

            }

        }
    }
}
