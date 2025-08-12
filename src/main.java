import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;


public class main extends JPanel implements ActionListener{
	JFrame frame;
	Timer t;
	
	public static void main(String[] args) {
		main drive = new main();
	}
	
	public main() {
		frame = new JFrame("Path Plotter");
		frame.setSize(900, 900);
		frame.add(this);
		
		t = new Timer(5,this);
		t.start();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	

	public void paint(Graphics g) {
		super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
		Field.drawField(g2, frame.getContentPane().getSize());
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}

}
