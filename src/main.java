import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;


public class Main extends JPanel implements ActionListener{
	JFrame frame;
	Timer t;
	
	Field f;
	
	public static void main(String[] args) {
		new Main();
	}
	
	public Main() {
		frame = new JFrame("Path Plotter");
		frame.setSize(900, 900);
		frame.add(this);
		
		f = new Field();
		
		t = new Timer(5,this);
		t.start();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	

	public void paint(Graphics g) {
		super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
		f.drawField(g2, frame.getContentPane().getSize());
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}

}
