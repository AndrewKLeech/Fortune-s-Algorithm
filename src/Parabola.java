/* based on http://blog.ivank.net/fortunes-algorithm-and-implementation.html
*  explanation of fortunes algorithm and addapted from implimntation in c++ and action script
* */
public class Parabola {
    public VPoint vPoint;
    public VEvent event;
    public Parabola parent, left, right;
    public boolean isLeaf;
    public VLine line;

    Parabola() {
        this.vPoint = null;
        isLeaf = (vPoint != null);
    }

    Parabola(VPoint vPoint) {
        this.vPoint = vPoint;
        isLeaf = (vPoint != null);
    }
    public void setLeft(Parabola parabola)
    {
        left = parabola;
        parabola.parent = this;
    }
    public void setRight(Parabola parabola)
    {
        right = parabola;
        parabola.parent = this;
    }
}