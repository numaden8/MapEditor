import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

/*
 * Created on 2006/12/10
 */

public class InfoPanel extends JPanel {
    // パネルサイズ
    public static final int WIDTH = 640;
    public static final int HEIGHT = 16;
    
    private String label = "";
    
    public InfoPanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }
    
    public void paintComponent(Graphics g) {
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        g.setColor(Color.BLACK);
        Font font = new Font("Ariel", Font.BOLD, 16);
        g.setFont(font);
        
        g.drawString(label, 8, 12);
    }
    
    public void setLabel(String str) {
        label = str;
    }
}
