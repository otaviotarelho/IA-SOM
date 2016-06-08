/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package som;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
/**
 *
 * @author otaviotarelho
 */

public class SOM {

    private static double[][] w = new double[4][2];
    private static double trainingRate = 0.5;
    private static double trainingActualRate = 0.5;
    private static double[][] _td
            = {
                {82.0, 1.80},
                {90.0, 1.75},
                {50.0, 1.70},
                {55.0, 1.80},
                {60.0, 1.70},
                {65.0, 1.80},
                {70.0, 1.90},
                {75.0, 1.50},
                {80.0, 1.90},
                {85.0, 1.90},
                {90.0, 1.70},
                {95.0, 1.75},
                {100.0, 1.80},
                {105.0, 2.00},
                {110.0, 2.10},
                {115.0, 1.80},
                {120.0, 1.90},
                {125.0, 2.00},
                {130.0, 2.10},
                {135.0, 1.85},
                {140.0, 2.00},
                {130.0, 1.70},
                {125.0, 1.80},
                {120.0, 1.60}};

    private static int[][] neuron = {{0, 1}, {2, 3}}; // neuronios e sua distancia
    private static int TIME = 1000 + (500 * w.length); // total maximo de iterações
    private static int FTEMPO = 10 * TIME;
    private static int[][] tdBMU = new int[25][3]; // Neuronio vencedor , X, Y;
    private static final int DIMENSION = 2;

    public static void weightsInit() {
        double[][] newWeights = new double[4][2];
        double verify;
        Random randomno = new Random();
        for (double[] newWeight : newWeights) {
            for (int j = 0; j < newWeights[0].length; j++) {
                verify = randomno.nextDouble();
                if (verify >= -0.5 && verify <= 0.5) {
                    newWeight[j] = verify;
                } else {
                    j--;
                }
            }
        }
        w = newWeights;
    }

    public static void BMU(int i) {
        double menor = 0.0;

        for (int j = 0; j < w.length; j++) {

            double distance = 0.0;
            //calculate euclidean distance
            distance = euclideanDistance(_td[i][0], _td[i][1], w[j][0], w[j][1]);

            if (j == 0) {
                menor = distance;
                tdBMU[i][0] = j;
            } // end if first weight
            else if (distance < menor) {
                menor = distance;
                tdBMU[i][0] = j;
            } // else weights
        }

        //Simplify it as best as possible
        //update BMU
        updateWeights(tdBMU[i][0], i);

        //update Neighbors
        switch (tdBMU[i][0]) {
            case 0:
                updateWeightsNeighbors(1, i);
                updateWeightsNeighbors(2, i);
                break;
            case 1:
                updateWeightsNeighbors(0, i);
                updateWeightsNeighbors(3, i);
                break;
            case 2:
                updateWeightsNeighbors(0, i);
                updateWeightsNeighbors(3, i);
                break;
            case 3:
                updateWeightsNeighbors(1, i);
                updateWeightsNeighbors(2, i);
                break;
        }

    }

    public static double euclideanDistance(double x1, double x2, double y1, double y2) {
        return Math.sqrt(Math.pow(x1 - y1, 2)) + Math.sqrt(Math.pow(x2 - y2, 2));
    }

    public static void updateWeights(int i, int d) {
        for (int j = 0; j < 2; j++) {
            w[i][j] += trainingRate * (_td[d][j] - w[i][j]);
        }
    }

    public static void updateWeightsNeighbors(int i, int d) {
        for (int j = 0; j < 2; j++) {
            w[i][j] += trainingRate * trainingActualRate * (_td[d][j] - w[i][j]);
        }
    }

    public static void changeFactor(int it) {
        trainingActualRate = trainingRate * Math.exp(-(it / FTEMPO));
    }

    public static void main(String[] args) throws IOException {
        int e = 0;
        weightsInit();

        System.out.println(Arrays.toString(w[0]));
        System.out.println(Arrays.toString(w[1]));
        System.out.println(Arrays.toString(w[2]));
        System.out.println(Arrays.toString(w[3]));

        while (e < TIME) {

            for (int i = 0; i < _td.length; i++) {
                BMU(i);
                setCoordinates(i);
            }
            changeFactor(e);
            e++;
        }

        System.out.println(Arrays.toString(w[0]));
        System.out.println(Arrays.toString(w[1]));
        System.out.println(Arrays.toString(w[2]));
        System.out.println(Arrays.toString(w[3]));

        int i = 0;
        while (i < 25) {
            System.out.println("Vencedor:" + (tdBMU[i][0] + 1) + " - X: " + tdBMU[i][1] + " Y: " + tdBMU[i][2]);
            i++;
        }
        XYSeries series = new XYSeries("Neuronio");
        XYSeriesCollection ds = new XYSeriesCollection();
        
        for(int f = 0; f < tdBMU[0].length; f++){
            ds.addValue(tdBMU[f][1], "Neuronio " + tdBMU[f][0], "" +tdBMU[f][2]);
            series.add(f, f);
        }
        
        JFreeChart grafico = ChartFactory.createLineChart("Self Organizing Maps", "X", "Y", ds, PlotOrientation.VERTICAL, true, true, false);
        JFreeChart g = ChartFactory.createScatterPlot("Self Organizing Maps", "X", "Y", ds, PlotOrientation.VERTICAL, true, true, false);
        
        JFrame frame = new JFrame("IMC SOM");
        frame.add(getPanel(grafico));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        
    }
    public static JPanel getPanel(JFreeChart grafico) {
        return new ChartPanel(grafico);
    }
    
    public static void setCoordinates(int it) {
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {

                if (tdBMU[it][0] == neuron[i][j]) {

                    tdBMU[it][1] = i;
                    tdBMU[it][2] = j;

                    break;
                }

            }
        }
    }

}
