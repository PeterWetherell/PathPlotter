import java.util.ArrayList;

class SplinePose2d extends Pose2d {
    public final double radius;
    public final double power;

    public SplinePose2d(Pose2d p, double radius) {
        this(p.x, p.y, p.heading, radius, 1.0);
    }

    public SplinePose2d(Pose2d p, double radius, double power) {
        this(p.x, p.y, p.heading, radius, power);
    }

    public SplinePose2d(double x, double y, double heading, double radius, double power) {
        super(x, y, heading);
        this.radius = radius;
        this.power = power;
    }
}

public class Spline {
    public ArrayList<SplinePose2d> poses = new ArrayList<>();
    public final double inchesPerNewPointGenerated;
    public final double MAX_RADIUS = 150;
    private boolean reversed = false;


    public Spline(Pose2d p, double inchesPerNewPointGenerated) {
        poses.add(new SplinePose2d(p, 100));
        this.inchesPerNewPointGenerated = inchesPerNewPointGenerated;
    }

    public Spline(double x, double y, double heading, double inchesPerNewPointGenerated) {
        this(new Pose2d(x,y,heading), inchesPerNewPointGenerated);
    }

    double[] xCoefficents = new double[4];
    double[] yCoefficents = new double[4];

    public double findR(double time){
        // gets the velocity because the derivative of position = velocity
        double velX = xCoefficents[1] + 2.0*xCoefficents[2]*time + 3.0*xCoefficents[3]*time*time;
        double velY = yCoefficents[1] + 2.0*yCoefficents[2]*time + 3.0*yCoefficents[3]*time*time;

        // gets the acceleration which is second derivative of position
        double accelX = 2.0*xCoefficents[2] + 6.0*xCoefficents[3]*time;
        double accelY = 2.0*yCoefficents[2] + 6.0*yCoefficents[3]*time;
        if ((accelY * velX - accelX * velY) != 0) {
        	double radius = Math.pow(velX*velX + velY*velY, 1.5) / (accelY*velX - accelX*velY);
            return Math.min(Math.abs(radius),MAX_RADIUS)*Math.signum(radius);
        }
        return MAX_RADIUS; // straight line
    }

    public Spline addPoint(Pose2d p) {
        return this.addPoint(p, 1.0);
    }

    public Spline addPoint(Pose2d p, double power) { // https://www.desmos.com/calculator/yi3jovk0hp
        Pose2d end = p.clone();
        Pose2d start = poses.get(poses.size()-1).clone(); // when you add a new spline the last point becomes the starting point for the new spline
        if (reversed) {
            start.heading += Math.PI;
            end.heading += Math.PI;
        }

        double v1 = Math.sqrt(Math.pow((start.x - end.x),2) + Math.pow((start.y - end.y),2));
        double arbitraryVelocity = Math.sqrt(
        		Math.pow((start.x + v1*Math.cos(start.heading)) - (end.x + v1*Math.cos(end.heading)),2) + 
        		Math.pow((start.y + v1*Math.sin(start.heading)) - (end.y + v1*Math.sin(end.heading)),2)
        		);
        xCoefficents[0] = start.x;
        xCoefficents[1] = arbitraryVelocity * Math.cos(start.heading);
        xCoefficents[2] = 3*end.x - arbitraryVelocity*Math.cos(end.heading) - 2*xCoefficents[1] - 3*xCoefficents[0];
        xCoefficents[3] = end.x - xCoefficents[0] - xCoefficents[1] - xCoefficents[2];

        yCoefficents[0] = start.y;
        yCoefficents[1] = arbitraryVelocity * Math.sin(start.heading);
        yCoefficents[2] = 3*end.y - arbitraryVelocity*Math.sin(end.heading) - 2*yCoefficents[1] - 3*yCoefficents[0];
        yCoefficents[3] = end.y - yCoefficents[0] - yCoefficents[1] - yCoefficents[2];

        double firstR = findR(0);
        if (Double.isNaN(firstR) || Double.isInfinite(firstR)) {
            System.out.println("HOLY JESUS SOMETHING BAD HAPPENED (FIRST TEMPR IS BRICKED)");
        }

    	Pose2d point = new Pose2d(0,0,0);
    	Pose2d lastPoint = start.clone();
        for (double time = 0.0; time < 1.0; time+=0.001) {
            point.x = xCoefficents[0] + xCoefficents[1]*time + xCoefficents[2]*time*time + xCoefficents[3]*time*time*time;
            point.y = yCoefficents[0] + yCoefficents[1]*time + yCoefficents[2]*time*time + yCoefficents[3]*time*time*time;
            
            if(lastPoint.getDistanceFromPoint(point) > inchesPerNewPointGenerated) { // new point every x inches

                // gets the velocity because the derivative of position = velocity
                double velX = xCoefficents[1] + 2.0*xCoefficents[2]*time + 3.0*xCoefficents[3]*time*time;
                double velY = yCoefficents[1] + 2.0*yCoefficents[2]*time + 3.0*yCoefficents[3]*time*time;
                // heading is equal to the inverse tangent of velX and velY
                point.heading = Math.atan2(velY,velX) + (reversed ? Math.PI : 0);
                point.clipAngle();

                poses.add(new SplinePose2d(point.clone(), findR(time), power));
                System.out.println("pathIndex: " + poses.size() + " radius: " + findR(time));

                lastPoint = point.clone();
            }
        }

        poses.add(new SplinePose2d(p, findR(1.0), power));

        return this;
    }

    public Spline addPoint(double x, double y, double heading) {
        return this.addPoint(new Pose2d(x, y, heading), 1.0);
    }

    public Spline addPoint(double x, double y, double heading, double power) {
        return this.addPoint(new Pose2d(x, y, heading), power);
    }

    public Pose2d getLastPoint() {
        if (poses.size() > 0) {
            return poses.get(poses.size() - 1);
        }
        return null;
    }

    /**
     * Ideally the path should be behind it otherwise it would break
     * So if you do it wrong its your fault!
     * @param reversed
     * @return
     */
    public Spline setReversed(boolean reversed) {
        this.reversed = reversed;
        return this;
    }
    
}