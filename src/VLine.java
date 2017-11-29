/* based on http://blog.ivank.net/fortunes-algorithm-and-implementation.html
*  explanation of fortunes algorithm and addapted from implimntation in c++ and action script
* */
public class VLine {
    public VPoint start, end, direction, left, right;

    public float f,g;
    public VLine neighbour;
    public VLine(VPoint start, VPoint left, VPoint right){
        this.start = start;
        this.left = left;
        this.right = right;
        direction = new VPoint(right.y-left.y,-(right.x-left.x));
        f = (right.x - left.x) / (left.y - right.y);
        g = ( start.y - f*start.x);
    }
}
