/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package som;

import java.io.IOException;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Leonardo Oliveira, Mariana Bispo, Otavio Tarelho
 */
public class SOM {

    private static double[][] w = new double[4][2];
    private static final double LEARNING = 0.5; //Learning rate at it = 0;
    private static double trainingActualRate = 0.5; //decay with iterations;
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
                {120.0, 1.60}
            };

    private static int[][] neuron = {{0, 1}, {2, 3}}; // Neurons
    private static int TIME = 1000 + (500 * w.length); // Maximum iterations
    private static int FTEMPO = 100000; // Time Constant
    private static int[][] tdBMU = new int[25][3]; // Winner neuron, and X, Y of neuron 
    private static final int DIMENSION = 2; // Neuron lattice dimension
    private static final double Neighborhood = 1.0; // Neighborhood Size at Time 0
    private static double NSize = 1.0; // Neighborhood Size chenges when time changes

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
        double menor = 0.0; //small distance

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

    public static void updateWeights(int it, int d) {

        for (int j = 0; j < 2; j++) {

            w[it][j] = w[it][j] + trainingActualRate * (_td[d][j] - w[it][j]);

        }
    }

    public static void updateWeightsNeighbors(int i, int d) {
        double influenceRate;

        for (int j = 0; j < 2; j++) {

            influenceRate = influenceRate(d, _td[d][0], _td[d][0], w[i][j], w[i][j]);
            w[i][j] = w[i][j] + trainingActualRate * influenceRate * (_td[d][j] - w[i][j]);

        }
    }

    public static void ChangeLearning(int it) {

        trainingActualRate = LEARNING * Math.exp((-1.0 * it) / FTEMPO);

    }

    public static void ChangeRNeighbohood(int it) {

        NSize = Neighborhood * Math.exp((-1.0 * it) / FTEMPO);

    }

    public static double influenceRate(int it, double x, double x1, double y, double y1) {

        double distance;
        distance = euclideanDistance(x, y, x1, y1);

        return Math.exp(-1.0 * Math.pow(distance, 2) / (2.0 * Math.pow(NSize, 2)));

    }

    public static void main(String[] args) throws IOException {
        int e = 0;

        weightsInit();

        while (e < TIME) {

            for (int i = 0; i < _td.length; i++) {

                BMU(i);
                setCoordinates(i);

            }

            ChangeRNeighbohood(e);
            ChangeLearning(e);
            e++;
        }

        int i = 0;
        while (i < 25) {

            System.out.println("Vencedor Final:" + (tdBMU[i][0] + 1) + " - X: " + tdBMU[i][1] + " Y: " + tdBMU[i][2]);
            i++;

        }

        
        XYSeriesCollection ds = new XYSeriesCollection();
        
        for(int f = 0; f < tdBMU.length -1; f++){
            XYSeries series = new XYSeries((f + 1)+ "- Neuronio " + (tdBMU[f][0] + 1));
            series.add(tdBMU[f][0], _td[f][0]);
            ds.addSeries(series);
        }
        
//        JFreeChart grafico = ChartFactory.createLineChart("Self Organizing Maps", "X", "Y", ds, PlotOrientation.VERTICAL, true, true, false);
        JFreeChart g = ChartFactory.createScatterPlot("Self Organizing Maps", "X", "Y", ds, PlotOrientation.VERTICAL, true, true, false);
        
        JFrame frame = new JFrame("IMC SOM");
        frame.add(getPanel(g));
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
