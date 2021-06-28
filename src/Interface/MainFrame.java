package Interface;

import java.awt.*;
import java.io.IOException;

import javax.swing.*;

import GeneticAlgorithm.World;
import Helpers.TownHelper;

public class MainFrame extends JFrame implements Runnable{

    Thread TimeThread;

	GamePanel citiesPanel;
	ParetoPanel paretoPanel;
	GenericPanel evolutionPanel;
	
    boolean isRunMainFrame;

	World world;

	public MainFrame() throws IOException{

		// *** Interface gráfica
        citiesPanel = new GamePanel();   
		evolutionPanel = new GenericPanel(Color.RED, "Evolution");
		paretoPanel = new ParetoPanel();
        
        // Configura o Main Frame
        this.setPreferredSize(new Dimension(Settings.mainFrameWidth, Settings.mainFrameHeight));
        this.getContentPane().setLayout(new GridLayout()); 

        JSplitPane mainSplitPane = new JSplitPane();
        this.getContentPane().add(mainSplitPane);    // Adiciona um split panel

        // Configura o main splitPane:
        mainSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(Settings.mainFrameWidth - Settings.auxPanelSize);                    
		
		JSplitPane auxSplitPane = new JSplitPane();  // Cria um novo painel
		auxSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        auxSplitPane.setDividerLocation(Settings.auxPanelSize);                    
        auxSplitPane.setTopComponent(evolutionPanel); 
        auxSplitPane.setBottomComponent(paretoPanel); 
		
        mainSplitPane.setLeftComponent(citiesPanel); 
        mainSplitPane.setRightComponent(auxSplitPane);           

        this.setTitle("Interface Gráfica Genérica");

		this.setResizable(false);
		this.setBackground(Color.WHITE);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.pack();
		this.setVisible(true);

		this.setLocationRelativeTo(null);


		// *** Algoritmo Genético
		TownHelper.Initialize();

		world = new World();
		world.Spawn();

		citiesPanel.initialize(world); // Inicia a representação gráfica das cidades

		// *** Time Thread
        TimeThread = new Thread(this);
		TimeThread.start();
	}

    @Override
    public void run() {
        //game loop
		long lastTime = System.nanoTime();
		double amountOfTicks = Settings.FPS;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;

        isRunMainFrame = true;

		while(true && isRunMainFrame) {
			long now = System.nanoTime();
			delta += (now -lastTime)/ns;
			lastTime = now;
			if(delta >=1) {

				// Realiza o GA
				world.DoGeneration();

				// Atualiza o melhor inidivíduo
				citiesPanel.updateSequence(world);
				paretoPanel.UpdatePareto(world);

				// if (world.generationsCount % Settings.FPS == 0){
				// 	paretoPanel.hasUpdate = true;
				// 	paretoPanel.UpdatePareto(world);
				// 	//paretoPanel.repaint();
				// }
				
                evolutionPanel.nameID = String.valueOf(world.generationsCount);
				
				//citiesPanel.repaint();
				this.repaint();
				delta--;
			}
		}     
    }
}