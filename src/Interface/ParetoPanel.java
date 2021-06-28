package Interface;

import java.awt.*;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ExtensionMethods.Vector2f;
import GeneticAlgorithm.Individual;
import GeneticAlgorithm.World;

public class ParetoPanel extends JPanel{

    String nameID;

    private final int labelPadding = 20;

    // Colors
    private Color lineColor = new Color(255,255,254);
    private List<Color> paretoColor = new ArrayList<Color>();
    private Color gridColor = new Color(200, 200, 200, 200);

    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
    private static int pointWidth = 5;  // Tamanho do tracinho
    private final int padding = 20;
    

    private int numberYDivisions = 10;
    private int numberXDivisions = 10;

    List<Individual> individuals;

    double minDistance;
    double maxDistance;
    double minTime;
    double maxTime;


    boolean alreadyFindFitness = false;
    boolean hasUpdate = true;

    private BufferedImage image;
    Image img;

    public ParetoPanel() throws IOException{

        this.nameID = "Pareto";

        genParetoColor();
        this.individuals = new ArrayList<Individual>();

        // Adiciona a imagem
        image = ImageIO.read(new File("src\\Interface\\Resources\\Graph.png"));

        //img = new ImageIcon("src\\Interface\\Resources\\Graph.png").getImage();

        this.setFocusable(true);
        this.setVisible(true);
    }

    private void genParetoColor(){
        paretoColor.add(new Color(200, 0, 0));
        paretoColor.add(new Color(0, 200, 0));
        paretoColor.add(new Color(0, 0, 200));
        paretoColor.add(new Color(200, 0, 200));
        paretoColor.add(new Color(200, 200, 0));
        paretoColor.add(new Color(0, 200, 200));
    }

    public void UpdatePareto(World world){

        this.individuals.clear();

        for (Individual individual : world.populationOfIndividuals){
            this.individuals.add(individual);
        }
        hasUpdate = true;

        this.minDistance = Double.MAX_VALUE / 2;
        this.maxDistance = -1;
        this.minTime = Double.MAX_VALUE / 2;
        this.maxTime = -1;

        for (Individual individual : this.individuals){
            if (this.minDistance > individual.NormalizedDistanceFitness){
                this.minDistance = individual.NormalizedDistanceFitness;
            }
            if (this.maxDistance < individual.NormalizedDistanceFitness){
                this.maxDistance = individual.NormalizedDistanceFitness;
            }

            if (this.minTime > individual.NormalizedTimeFitness){
                this.minTime = individual.NormalizedTimeFitness;
            }
            if (this.maxTime < individual.NormalizedTimeFitness){
                this.maxTime = individual.NormalizedTimeFitness;
            }
        }
    }

    private void drawPareto(Graphics g){

        int minOffsetX = 40;
        int minOffsetY = 263;
        int maxOffsetX = 272;
        int maxOffsetY = 47;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        double diffTime = this.maxTime - this.minTime;
        double diffDistance = this.maxDistance - this.minDistance;

        if (!individuals.isEmpty()){

            for (Individual individual : this.individuals){
                int rank = individual.rank - 1;
                if (rank > paretoColor.size() - 1){
                    rank = paretoColor.size() - 1;
                }

                g2.setColor(paretoColor.get(rank));

                int posX = (int)((individual.NormalizedTimeFitness - this.minTime) / diffTime * (maxOffsetX - minOffsetX)) + minOffsetX;
                int posY = (int)((individual.NormalizedDistanceFitness - this.minDistance) / diffDistance * (maxOffsetY - minOffsetY)) + minOffsetY;

                Point visualPosition = new Point(posX, posY);

                int x = visualPosition.x - pointWidth / 2;
                int y = visualPosition.y - pointWidth / 2;
                int ovalW = pointWidth;
                int ovalH = pointWidth;
                g2.fillOval(x, y, ovalW, ovalH);
            }
        }
    }

    // private void drawLabels(Graphics g){
    //     Graphics2D g2 = (Graphics2D) g;
    //     g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    //     // Label Y
    //     for (int i = 0; i < numberYDivisions + 1; i++) {
    //         int x0 = padding + labelPadding;
    //         int x1 = pointWidth + padding + labelPadding;
    //         int y0 = getHeight() - ((i * (getHeight() - padding * 2 -
    //         		labelPadding)) / numberYDivisions + padding + labelPadding);
    //         int y1 = y0;
            
    //         g2.setColor(gridColor);
    //         g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding, y1);
    //         g2.setColor(Color.BLACK);

    //         String yLabel = String.format("%.2f", ((double)i / numberYDivisions));

    //         FontMetrics metrics = g2.getFontMetrics();
    //         int labelWidth = metrics.stringWidth(yLabel);
    //         g2.drawString(yLabel, x0 - labelWidth - 6, y0 + (metrics.getHeight() / 2) - 3);
            
    //         g2.drawLine(x0, y0, x1, y1);
    //     }

    //     // Label X
    //     for (int i = 0; i < numberXDivisions + 1; i++) {
    //         if (numberXDivisions > 1) {
    //             int x0 = i * (getWidth() - padding * 2 - labelPadding) / numberXDivisions + padding + labelPadding;
    //             int x1 = x0;
    //             int y0 = getHeight() - padding - labelPadding;
    //             int y1 = y0 - pointWidth;
                
    //             g2.setColor(gridColor);
    //             g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - pointWidth, x1, padding);
    //             g2.setColor(Color.BLACK);
    //             String xLabel = String.format("%.2f", ((double)i / numberXDivisions));
    //             FontMetrics metrics = g2.getFontMetrics();
    //             int labelWidth = metrics.stringWidth(xLabel);
    //             g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
                
    //             g2.drawLine(x0, y0, x1, y1);
    //         }
    //     }

    //     g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
    //     g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() - padding, getHeight() - padding - labelPadding);
    // }

    private Point mappingPoint(double xValue, double yValue){
        int x = (int) ((xValue * numberXDivisions) * (((double) getWidth() - (2 * padding) - labelPadding) / numberXDivisions) + padding + labelPadding);
        int y = (int)(getHeight() - (((yValue * numberYDivisions) * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding));

        return new Point(x, y);
        //return new Point(labelPadding + padding + (int)(xValue * 1), getHeight() - labelPadding - padding - (int)(xValue * 1));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(image, 0, 0, getWidth(), getHeight(), this);

        drawPareto(g); // Desenha os pontos do pareto

        // if (hasUpdate){
        //     hasUpdate = false;
        //     drawLabels(g); // Desenha os labels
        // }
    }
}
