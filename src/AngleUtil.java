public class AngleUtil {
    public static double clipAngle(double angle) {
        while (Math.abs(angle) > Math.PI) {
            angle -= Math.PI * 2.0 * Math.signum(angle);
        }
        return angle;
    }

    public static double mirroredClipAngle(double angle) {
        while (angle > Math.PI / 2) {
                angle -= Math.PI;
            }
        while (angle < -Math.PI / 2) {
            angle += Math.PI;
        }
        return angle;
    }

    public static double mirroredClipAngleTolerence(double angle, double t) {
        while (angle > Math.PI / 2 + t) {
                angle -= Math.PI;
            }
        while (angle < -Math.PI / 2 - t) {
            angle += Math.PI;
        }
        return angle;
    }
}
