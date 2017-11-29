
public class VPoint implements Comparable<VPoint> {
    public float x;
    public float y;
    public Parabola parabola;

    public VPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public int compareTo(VPoint point) {

        float compareY = (point.y);
        return Math.round(this.y - compareY);
    }
}
