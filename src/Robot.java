
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

public class Robot {
	public static final double ROBOT_WIDTH = 12;
	public static final double ROBOT_Height = 16;
	public static final double WHEEL_WIDTH = 1.49606;
	public static final double WHEEL_HEIGHT = 4.09449;
	public static final double WHEEL_POS_X = ROBOT_Height - WHEEL_HEIGHT * 1.25;
	public static final double WHEEL_POS_Y = ROBOT_WIDTH - WHEEL_WIDTH * 1.5;
	
	public Pose2d p;
	Field field;
	
	public Robot(Field field, Pose2d Start) {
		p = Start;
		this.field = field;
	}

	public void update(Graphics2D g2) {
		drawRobot(g2);
	}
	
	
	public void drawRobot(Graphics2D g2) {
		g2.setStroke(new BasicStroke(2f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10f));
		g2.setColor(Color.BLUE);
		field.drawRectangle(g2, p, ROBOT_WIDTH, ROBOT_Height);

		double a = 1, b = 1;
		for (int i = 0; i < 4; i ++) {
			switch(i) {
			case(0): a = 1; b = 1; break;
			case(1): a = 1; b = -1; break;
			case(2): a = -1; b = -1; break;
			case(3): a = -1; b = 1; break;
			}
			field.drawRectangle(
				g2,
				new Pose2d(
					p.x + a * WHEEL_POS_X/2 * Math.cos(p.heading) - b * WHEEL_POS_Y/2 * Math.sin(p.heading),
					p.y + b * WHEEL_POS_Y/2 * Math.cos(p.heading) + a * WHEEL_POS_X/2 * Math.sin(p.heading),
					p.heading
				),
				WHEEL_WIDTH,
				WHEEL_HEIGHT
			);
			drawOdo(g2);
		}
	}
	public void drawOdo(Graphics2D g2) {
		double a = Math.cos(p.heading) * WHEEL_WIDTH/2.0;
		double b = Math.sin(p.heading) * WHEEL_WIDTH/2.0;
		
		double r = (ROBOT_WIDTH - WHEEL_WIDTH * 1.5)/2.0;
		
		Pose2d leftOdo = new Pose2d(
				p.x - r*Math.sin(p.heading),
				p.y + r*Math.cos(p.heading)
			);
		field.drawLine(g2,new Pose2d(leftOdo.x - a, leftOdo.y - b),new Pose2d(leftOdo.x + a,leftOdo.y + b));
		
		Pose2d rightOdo = new Pose2d(
				p.x + r*Math.sin(p.heading),
				p.y - r*Math.cos(p.heading)
			);
		field.drawLine(g2,new Pose2d(rightOdo.x - a,rightOdo.y - b),new Pose2d(rightOdo.x + a,rightOdo.y + b));
		
		Pose2d backOdo = new Pose2d(
				p.x - r*Math.cos(p.heading),
				p.y - r*Math.sin(p.heading)
			);
		field.drawLine(g2,new Pose2d(backOdo.x + b,backOdo.y - a),new Pose2d(backOdo.x - b,backOdo.y + a));
	}
}
