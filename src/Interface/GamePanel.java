package Interface;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

import GeneticAlgorithm.Config;
import GeneticAlgorithm.Individual;
import GeneticAlgorithm.World;
import Helpers.TownHelper;

public class GamePanel extends JPanel{

	String nameID;
	List<Integer> currentSequence = new ArrayList<Integer>();
	ArrayList<City> populationCities = new ArrayList<City>();

	InfoDisplay infoDisplay;

	GamePanel(){

		this.nameID = "CitiesPanel";

        this.setFocusable(true);
		this.addKeyListener(new AL());
        this.setVisible(true);
	}

	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.draw(g);
    }

    public void draw(Graphics g) {

		drawSequence(g);
		drawCities(g);
		infoDisplay.draw(g);
		
		Toolkit.getDefaultToolkit().sync(); // I forgot to add this line of code in the video, it helps with the animation
	}

	private void drawCities(Graphics g) {
		for (City city: this.populationCities){
            city.draw(g);
        }
	}

	public void drawSequence(Graphics g){
		if (!this.currentSequence.isEmpty()){
            for (int i = 0; i < this.currentSequence.size(); i++){
                City citie_1 = this.populationCities.get(this.currentSequence.get(i));
                City citie_2 = this.populationCities.get(this.currentSequence.get((i+1)%Config.numberOfCities));
    
                Graphics2D g2d = (Graphics2D) g;
    
                g2d.setPaint(Color.BLACK); 
                g2d.setStroke(new BasicStroke (6.0f)); 
                g.drawLine(citie_1.getPosX() + 10, citie_1.getPosY() + 10, citie_2.getPosX() + 10, citie_2.getPosY() + 10);
            }
        }
	}
	
    public void initialize(World world) {

        for (int i = 0; i < Config.numberOfCities; i++){
			populationCities.add(new City((int)TownHelper.townPositions.get(i).X, (int)TownHelper.townPositions.get(i).Y));
            currentSequence.add(i);
        } 

		infoDisplay = new InfoDisplay(Config.spaceWidth, Config.spaceHeight);
    }

    public void updateSequence(World world) {
		// Desenha o caminho do melhor indivíduo
		Individual bestCurrentIndividual = world.getBestIndividual();

		currentSequence.clear();
        for (int IDCity : bestCurrentIndividual.sequence){
            currentSequence.add(IDCity);
        }

		// Atualiza o display
		infoDisplay.generations = world.generationsCount;
		infoDisplay.travelDistance = bestCurrentIndividual.distanceFitness;
		infoDisplay.travelTime = bestCurrentIndividual.timeFitness;
    }

	public class AL extends KeyAdapter{
		public void keyPressed(KeyEvent e) {
			
		}
		public void keyReleased(KeyEvent e) {
			
		}
	}
}