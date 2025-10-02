package com.company;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Čejkis on 20.04.2017.
 */
public class VizWindow extends JFrame {

    JTextArea[][] windows;
    JTextArea bestEver;
    JTextArea bestNow;
    int thickness = 8;

    public void printIndividual(Individual i, int g, int islandnr){
        boolean[][] taj = i.legend;

        for (int j = 0; j < windows.length; j++) {
            for (int k = 0; k < windows[0].length ; k++) {
                if (!taj[k][j]) {
                    windows[j][k].setBackground(Color.WHITE);
                } else {
                    windows[j][k].setBackground(Color.BLACK);
                }
            }
        }
        bestNow.setText(g + " " + i.fitness );
    }

    public void printBestEver(Individual i, int g, int islandnr){
        bestEver.setText(g + " " + i.fitness );
    }

    public VizWindow(int sirka, int vyska, int islandnr){
        JFrame frame = new JFrame("Vizual");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        windows = new JTextArea[sirka][vyska];

        for (int i = 0; i < sirka ; i++) {
            for (int j = 0; j <  vyska; j++) {
                //JLabel a = new JLabel("x");
                JTextArea a = new JTextArea("");
                a.setLocation(new Point(i * thickness, j * thickness));
                a.setSize(new Dimension(thickness, thickness));

                if (Math.random() < 0.5) {
                    a.setBackground(Color.white);
                } else {
                    a.setBackground(Color.BLACK);
                }

                windows[i][j] = a;
                frame.add(a);
            }
        }

        bestEver = new JTextArea("best fitness");
        bestEver.setLocation(new Point(0, vyska * thickness + thickness));
        bestEver.setSize(new Dimension(100,20));
        frame.add(bestEver);

        bestNow = new JTextArea("best fitness");
        bestNow.setLocation(new Point(120, vyska * thickness + thickness));
        bestNow.setSize(new Dimension(100,20));
        frame.add(bestNow);

        frame.setSize(new Dimension(thickness *sirka + thickness, thickness * vyska + 80));

        if (islandnr < 4) {
            frame.setLocation(islandnr * frame.getWidth(), 0);
        } else {
            frame.setLocation((islandnr - 4) * frame.getWidth(), thickness * vyska + 75);
        }
        frame.setVisible(true);
    }
}
