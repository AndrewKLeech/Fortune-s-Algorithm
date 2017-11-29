/* based on http://blog.ivank.net/fortunes-algorithm-and-implementation.html
*  explanation of fortunes algorithm and addapted from implimntation in c++ and action script
* */
import java.util.*;


public class Voronoi {
    //ArrayList for all points
    private ArrayList<VPoint> vPoints = new ArrayList<>();
    //ArrayList for lines in voronoi (and used for sweep line)
    private ArrayList<VLine> lines = new ArrayList<>();
    private ArrayList<VEvent> events = new ArrayList<>();
    //from action script implementation
    private Parabola root;
    private VPoint firstPoint;

    private int height;
    private int width;


    //set number of test points
    private float sweepY;
    private float sweepLastY;
    private Random randomNumber = new Random();

    Voronoi() {

    }


    public ArrayList<VLine> getLines(ArrayList<VPoint> points, int width, int height) {
        root = null;
        this.vPoints = points;
        this.width = width;
        this.height = height;

        //tech. ref
        for (VPoint vPoint : vPoints) {
            VEvent event = new VEvent(vPoint, true);
            events.add(event);
        }

        sweepLastY = Float.MAX_VALUE; //or 0


        while (!events.isEmpty()) {
            System.out.println(("Items in list: " + events.size()));
            Collections.sort(events);
            VEvent event = events.remove(0);
            sweepY = event.point.y;
            if (event.isPlaceEvent()) {
                System.out.println("Create Parabola for " + event.point.x + ", " + event.point.y);
                createParabola(event.point);
            } else {
                removeParabola(event);
            }
            sweepLastY = event.y;

        }
        finishEdge(root);
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).neighbour != null) lines.get(i).start = lines.get(i).neighbour.end;
        }
        return lines;
    }


    private float euclideanDis(VPoint point1, VPoint point2) {
        float x, y, a, b;
        x = point1.x;
        y = point1.y;
        a = point2.x;
        b = point2.y;
        double dist;
        dist = Math.sqrt(Math.pow(x - a, 2) + Math.pow(y - b, 2));
        return (float) dist;
    }

    private void createParabola(VPoint point) {
        if (root == null) {
            root = new Parabola(point);
            firstPoint = point;
            return;
        }
        if (root.isLeaf && root.vPoint.y - point.y < 1) {
            root.isLeaf = false;
            root.left = new Parabola(firstPoint);
            root.right = new Parabola(point);
            VPoint s = new VPoint((point.x + firstPoint.x) / 2, height);
            //if point is left of firstPoint
            if (point.x > firstPoint.x) {
                //new VLine start = start, left = firstPoint, right = point
                root.line = new VLine(s, firstPoint, point);
            }
            //if point is right of firstPoint
            else {
                //new VLine start = start, left = point, right = firstPoint
                root.line = new VLine(s, point, firstPoint);
            }
            lines.add(root.line);
        }
        Parabola parabola = getParabolaByX(point.x);
        if (parabola.event != null) {
            events.remove(parabola.event);
            parabola.event = null;
        }
        //Set start points of lines to be the x of the point passed in
        //Call getY with the point the parabola was
        VPoint start = new VPoint(point.x, getY(parabola.vPoint, point.x));

        VLine lineLeft = new VLine(start, parabola.vPoint, point);
        VLine lineRight = new VLine(start, point, parabola.vPoint);

        lineLeft.neighbour = lineRight;
        lines.add(lineLeft);

        parabola.line = lineRight;
        parabola.isLeaf = false;

        Parabola parabola1 = new Parabola(parabola.vPoint);
        Parabola parabola2 = new Parabola(point);
        Parabola parabola3 = new Parabola(parabola.vPoint);

        parabola.setRight(parabola3);
        parabola.setLeft(new Parabola());
        parabola.left.line = lineLeft;

        parabola.left.setLeft(parabola1);
        parabola.left.setRight(parabola2);


        checkCircle(parabola1);
        checkCircle(parabola3);
    }

    private void removeParabola(VEvent event) {
        Parabola p1 = event.arch;

        Parabola xLeft = getLeftParent(p1);
        Parabola xRight = getRightParent(p1);

        Parabola p0 = getLeftChild(xLeft);
        Parabola p2 = getRightChild(xRight);

        if (p0.event != null) {
            events.remove(p0.event);
            p0.event = null;
        }

        if (p2.event != null) {
            events.remove(p2.event);
            p2.event = null;
        }

        VPoint p = new VPoint(event.point.x, getY(p1.vPoint, event.point.x));

        sweepLastY = event.point.y;

        xLeft.line.end = p;
        xRight.line.end = p;

        Parabola higher = null;
        Parabola parabola = p1;
        while (parabola != root) {

            parabola = parabola.parent;
            if (parabola == xLeft) {
                higher = xLeft;
            }
            if (parabola == xRight) {
                higher = xRight;
            }
        }
        if (higher == null) {
            higher = new Parabola();
        }
        higher.line = new VLine(p, p0.vPoint, p2.vPoint);

        lines.add(higher.line);

        Parabola gparent = p1.parent.parent;
        if (p1.parent.left == p1) {
            if (gparent.left == p1.parent) {
                gparent.left = p1.parent.right;
            } else {
                p1.parent.parent.right = p1.parent.right;
            }
        } else {
            if (gparent.left == p1.parent) {
                gparent.left = p1.parent.left;
            } else {
                gparent.right = p1.parent.left;
            }
        }
        checkCircle(p0);
        checkCircle(p2);
    }

    private void finishEdge(Parabola parabola) {
        float mx;
        if (parabola.line.direction.x > 0.0) {
            mx = Math.max(width, parabola.line.start.x + 10);
        } else {
            mx = (float) Math.min(0.0, parabola.line.start.x - 10);
        }
        parabola.line.end = new VPoint(mx, parabola.line.f * mx + parabola.line.g);

        if (!parabola.left.isLeaf) {
            finishEdge(parabola.left);
        }
        if (!parabola.right.isLeaf) {
            finishEdge(parabola.right);
        }
    }

    private float getXOfEdge(Parabola par, float y)
    {
        Parabola left = getLeftChild(par);
        Parabola right = getRightChild(par);

        VPoint p = left.vPoint;
        VPoint r = right.vPoint;

        float dp = 2 * (p.y - y);
        float a1 = 1 / dp;
        float b1 = -2 * p.x / dp;
        float c1 = y + dp / 4 + p.x * p.x / dp;

        dp = 2 * (r.y - y);
        float a2 = 1 / dp;
        float b2 = -2 * r.x / dp;
        float c2 = y + dp / 4 + r.x * r.x / dp;

        float a = a1 - a2;
        float b = b1 - b2;
        float c = c1 - c2;

        float disc = b * b - 4 * a * c;
        float x1 = (float) (-b + Math.sqrt(disc)) / (2 * a);
        float x2 = (float) (-b - Math.sqrt(disc)) / (2 * a);

        float ry;
        if (p.y < r.y) ry = Math.max(x1, x2);
        else ry = Math.min(x1, x2);

        return ry;
    }

    public Parabola getParabolaByX(float xx) {
        Parabola par = root;
        float x = 0;

        while (!par.isLeaf) {
            x = getXOfEdge(par, sweepY);
            if (x > xx) par = par.left;
            else par = par.right;
        }
        return par;
    }

    private float getY(VPoint p, float x) {
        float dp = 2 * (p.y - sweepY);
        float b1 = -2 * p.x / dp;
        float c1 = sweepY + dp / 4 + p.x * p.x / dp;

        return (x * x / dp + b1 * x + c1);
    }


    private void checkCircle(Parabola parabola) {
        Parabola lp = getLeftParent(parabola);
        Parabola rp = getRightParent(parabola);

        Parabola a = getLeftChild(lp);
        Parabola c = getRightChild(rp);

        if (a == null || c == null || a.vPoint == c.vPoint) return;

        VPoint s = getEdgeIntersection(lp.line, rp.line);
        if (s == null) return;

        float d = euclideanDis(a.vPoint, s);

        if (s.y - d >= sweepY) return;

        VEvent e = new VEvent(new VPoint(s.x, s.y - d), false);

        parabola.event = e;
        e.arch = parabola;
        events.add(e);
    }

    private VPoint getEdgeIntersection(VLine a, VLine b) {

        float x = (b.g - a.g) / (a.f - b.f);
        float y = a.f * x + a.g;
        
        if (Math.abs(x) + Math.abs(y) > 20 * width) {
            return null;
        } // parallel
        if (Math.abs(a.direction.x) < 0.01 && Math.abs(b.direction.x) < 0.01) {
            return null;
        }

        if ((x - a.start.x) / a.direction.x < 0) {
            return null;
        }
        if ((y - a.start.y) / a.direction.y < 0) {
            return null;
        }

        if ((x - b.start.x) / b.direction.x < 0) {
            return null;
        }
        if ((y - b.start.y) / b.direction.y < 0) {
            return null;
        }

        return new VPoint(x, y);
    }

    private Parabola getLeftParent(Parabola parabola) {
        Parabola par = parabola.parent;
        Parabola pLast = parabola;
        if (par.left != null) {
            while (par.left == pLast) {
                if (par.parent == null) return null;
                pLast = par;
                par = par.parent;
            }
        }
        return par;
    }

    private Parabola getRightParent(Parabola parabola) {
        Parabola par = parabola.parent;
        Parabola pLast = parabola;
        while (par.right == pLast) {
            if (par.parent == null) return null;
            pLast = par;
            par = par.parent;
        }
        return par;
    }

    private Parabola getLeftChild(Parabola parabola) {
        if (parabola == null) return null;
        Parabola par = parabola.left;
        while (!par.isLeaf) par = par.right;
        return par;
    }

    private Parabola getRightChild(Parabola parabola) {
        if (parabola == null) return null;
        Parabola par = parabola.right;
        while (!par.isLeaf) par = par.left;
        return par;
    }
}