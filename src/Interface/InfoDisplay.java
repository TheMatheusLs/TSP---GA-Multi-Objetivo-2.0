package Interface;

import java.awt.*;

public class InfoDisplay extends Rectangle{

	static int SPACE_WIDTH;
	static int SPACE_HEIGHT;

	double travelDistance = 0.0;
	double travelTime = 0.0;
	int generations = 0;
	
	InfoDisplay(int GAME_WIDTH, int GAME_HEIGHT){
		InfoDisplay.SPACE_WIDTH = GAME_WIDTH;
		InfoDisplay.SPACE_HEIGHT = GAME_HEIGHT;
	}

	public void draw(Graphics g) {
		draw(g, Color.BLACK);
	}

	public void draw(Graphics g, Color color) {
		g.setColor(color);
		g.setFont(new Font("Consolas", Font.PLAIN,24));
	
		g.drawString("Distância:" + String.format("%.2f", travelDistance), 30, 20);
		g.drawString("Tempo:" + String.format("%.2f", travelTime), 30, 40);
		g.drawString("Gerações:" + String.valueOf(generations), 600, 20);
	}
}