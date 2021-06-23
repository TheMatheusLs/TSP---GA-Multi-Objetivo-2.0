package Interface;

import java.awt.*;
import java.util.*;

public class City extends Rectangle{

	Random random;

	int posX;
	int posY;
	
	City(int x, int y){
		super(x, y, Settings.citySize, Settings.citySize);

		this.posX = x;
        this.posY = y;
	}

	public int getPosX(){
		return posX;
	}

	public int getPosY(){
		return posY;
	}
	
	public void draw(Graphics g) {
		g.setColor(Color.BLUE);
		g.fillOval(x, y, height, width);
	}
}