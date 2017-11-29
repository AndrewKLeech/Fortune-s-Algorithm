/* based on http://blog.ivank.net/fortunes-algorithm-and-implementation.html
*  explanation of fortunes algorithm and addapted from implimntation in c++ and action script
* */
public class VEvent implements Comparable<VEvent>{
    public VPoint point;
    public boolean placeEvent;

    public float y;
    public double key;

    public Parabola arch;

    public int value;

    public VEvent(VPoint point, boolean placeEvent){
        this.point = point;
        this.placeEvent = placeEvent;
        this.y = point.y;
        this.key = Math.random()*1000000000;
    }

    public int compareTo(VEvent other){
        boolean b1 = (y < other.y);
        return (b1?1:-1);
    }


    public boolean isPlaceEvent() {
        return placeEvent;
    }


    public Parabola getArch() {
        return arch;
    }

    public void setArch(Parabola arch) {
        this.arch = arch;
    }
}
