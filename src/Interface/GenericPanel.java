package Interface;

import java.awt.*;
import javax.swing.*;

public class GenericPanel extends JPanel{

    String nameID;

    public GenericPanel(Color bgColor, String name){

        this.nameID = name;

        this.setBackground(bgColor);

        this.setFocusable(true);
        this.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.draw(g);
    }

    public void draw(Graphics g) {

		g.setColor(Color.BLACK);
		g.setFont(new Font("Consolas", Font.PLAIN,24));
	
		g.drawString(this.nameID, this.getWidth() / 2, this.getHeight() / 2);

		Toolkit.getDefaultToolkit().sync(); // Ajuda na animação

	}
}
