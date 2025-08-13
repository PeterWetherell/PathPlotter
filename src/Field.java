import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

public class Field {
	public static final int NUM_TILES = 6;
	public static final double FEILD_LENGTH = 141;
	public static final double TILE_WIDTH = 24.3125;
	public static final double TILE_INTERLOCKING_EDGE_LENGTH = (TILE_WIDTH*NUM_TILES - FEILD_LENGTH)/(NUM_TILES+1);
	public static final double TILE_CENTER_WIDTH = TILE_WIDTH - 2 * TILE_INTERLOCKING_EDGE_LENGTH;
	
	public static int getTile(double x) {
		return (int)(x/(TILE_INTERLOCKING_EDGE_LENGTH + TILE_CENTER_WIDTH));
	}
	
	Robot r;
	Spline s;
	
	double elapsedTime;
	long last;
	
	public Field() {
		r = new Robot(this, new Pose2d(48,24,0));
        s = new Spline(0, -30, 0, 3)
        		.addPoint(30, 0, Math.PI/2)
        		.addPoint(0,30,Math.PI)
				.addPoint(-30, 0, -Math.PI/2)
				.addPoint(0, -30, 0)
				.setReversed(true)
				.addPoint(-30, 0, -Math.PI/2)
        		.addPoint(0,30,Math.PI)
        		.addPoint(30, 0, Math.PI/2)
				.addPoint(0, -30, 0);
        last = System.nanoTime();
	}
	
	int offset = 0;
	double pixelPerInch = 0;
	public int convertToPixels(double v) {
		return offset + (int)((v + FEILD_LENGTH/2.0)*pixelPerInch);
	}
	
	public void drawRectangle(Graphics2D g2, Pose2d p, double width, double heignt) {
		double a = 1, b = 1, cos = Math.cos(p.heading), sin = Math.sin(p.heading);
		for (int i = 0; i < 4; i ++) {
			switch(i) {
			case(0): 	a = 0.5; 	b = 0.5; 	break;
			case(1): 	a = 0.5; 	b = -0.5;	break;
			case(2): 	a = -0.5;	b = -0.5;	break;
			case(3): 	a = -0.5;	b = 0.5; 	break;
			}
			drawLine(
					g2,
					new Pose2d(
							p.x + a*heignt*cos - b*width*sin,
							p.y + b*width*cos + a*heignt*sin
					),
					new Pose2d(
							p.x - b*heignt*cos - a*width*sin,
							p.y + a*width*cos - b*heignt*sin
					)
			);
		}
	}
	
	public void drawLine(Graphics2D g2, Pose2d p1, Pose2d p2) {
		g2.drawLine(convertToPixels(-p1.y),convertToPixels(-p1.x),convertToPixels(-p2.y),convertToPixels(-p2.x));
	}
	
	public void drawField(Graphics2D g2, Dimension dim) {
        long start = System.nanoTime();
        elapsedTime += (start - last)/1.0e9;
        last = start;
		
		double x = dim.width;
		double y = dim.height;
		int maxSize = (int)Math.min(x, y);
		offset = (int)Math.min(maxSize * 0.2,10);
		int size = maxSize - 2*offset;
		g2.setColor(Color.GRAY);
		g2.fillRect(offset, offset, size, size);
		
		g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(5));
        g2.drawRect(offset, offset, size, size);

    	float[] dotPattern = {5f, 5f};
    	BasicStroke dotPattern1 = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10f, dotPattern, 0f);
    	BasicStroke dotPattern2 = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10f, dotPattern, 5f);
    	
        pixelPerInch = size/FEILD_LENGTH;
        for (int i = 1; i < NUM_TILES; i ++) {
        	double v = (i-NUM_TILES/2.0)*(TILE_INTERLOCKING_EDGE_LENGTH + TILE_CENTER_WIDTH); // Center of the line in inches
        	int l = (int)((FEILD_LENGTH/2.0 + v - TILE_INTERLOCKING_EDGE_LENGTH/2.0)*pixelPerInch); // Find the left side & convert to pixels
        	int r = (int)((FEILD_LENGTH/2.0 + v + TILE_INTERLOCKING_EDGE_LENGTH/2.0)*pixelPerInch); // Find the right side & convert to pixels
        	g2.setStroke(dotPattern1);
            g2.drawLine(offset+l, offset, offset+l, offset+size); // Vertical line
            g2.drawLine(offset, offset+l, offset+size, offset+l); // Horizontal

            g2.setStroke(dotPattern2);
            g2.drawLine(offset+r, offset, offset+r, offset+size); // Vertical line
            g2.drawLine(offset, offset+r, offset+size, offset+r); // Horizontal
        }
        
        double m = 4;
        double t = (elapsedTime*m)%s.poses.size();
        g2.setColor(Color.GREEN);
		g2.setStroke(new BasicStroke(2f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10f));
        for (int i = (int)t+1; i < s.poses.size()-1; i ++) {
        	drawLine(g2,s.poses.get(i),s.poses.get(i+1));
        }
        double k = t - (int)t;
        r.p = s.poses.get((int)t).clone();
        if ((int)t < s.poses.size()-1) {
        	Pose2d next = s.poses.get((int)t + 1);
        	r.p.x += (next.x - r.p.x)*k;
        	r.p.y += (next.y - r.p.y)*k;
        	r.p.heading += AngleUtil.clipAngle(next.heading - r.p.heading)*k;
        }
        r.update(g2);
	}
}
