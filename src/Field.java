import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
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
	
	public static void drawField(Graphics2D g2, Dimension dim) {
		double x = dim.width;
		double y = dim.height;
		int maxSize = (int)Math.min(x, y);
		int offset = (int)Math.min(maxSize * 0.2,10);
		int size = maxSize - 2*offset;
		g2.setColor(Color.GRAY);
		g2.fillRect(offset, offset, size, size);
		
		g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(5));
        g2.drawRect(offset, offset, size, size);

    	float[] dotPattern = {5f, 5f};
    	BasicStroke dotPattern1 = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10f, dotPattern, 0f);
    	BasicStroke dotPattern2 = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10f, dotPattern, 5f);
    	
        double pixelPerInch = size/FEILD_LENGTH;
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
	}
}
