import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class Controller extends JFrame{
    private static int height = 500;
    private static int width = 500;
    private static int numberOfPoints = 5;
    private static BufferedImage img;

    //ArrayList for all points
    private ArrayList<VPoint> points = new ArrayList<>();
    private ArrayList<VLine> lines = new ArrayList<>();
    //Test points
    //1: One edge in top right dosnt end right
    int[] testx1 = {396, 327, 197, 238, 447};
    int[] testy1 = {80, 361, 362, 342, 340};
    //2: Should work
    int[] testx2 = {440, 127, 259, 243, 306};
    int[] testy2 = {226, 87, 92, 322, 458};
    //3: one edge at top not working (like test case 1)
    int[] testx3 = {197,48,357,171,3};
    int[] testy3 = {310,168,40,278,323};
    //4: Does not run
    int[] testx4 = {302,212,171,156,37};
    int[] testy4 = {343,170,342,459,10};
    //5: should work
    int[] testx5 = {227,473,351,385,280};
    int[] testy5 = {236,134,276,70,268};
    //6: should work
    int[] testx6 = {27,373,461,403,436};
    int[] testy6 = {333,157,272,179,91};

    //Make random unique points for test points
    private final int[] randX = new Random().ints(0, width).distinct().limit(numberOfPoints).toArray();
    private final int[] randY = new Random().ints(0, height).distinct().limit(numberOfPoints).toArray();


    Controller(){
        super("Contour Connector");
        setBounds(0, 0, width, height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        for (int i = 0; i < numberOfPoints; i++) {
            //change points that are bing passed to the constructor to one of the test cases or random eg. VPoint(testx1[i], testy1[i])
            VPoint vPoint = new VPoint(testx6[i], testy6[i]);
            System.out.println("Point " + i + ": x=" + vPoint.x + " y=" + vPoint.y);
            points.add(vPoint);
        }
        Voronoi voronoi = new Voronoi();
        lines = voronoi.getLines(points,width,height);
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setPaint(new Color(255, 255, 255));
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        //vis
        for (int i = 0; i < numberOfPoints; i++) {
            g.fill(new Ellipse2D.Double(Math.abs(points.get(i).x - 2.5), Math.abs(points.get(i).y - 2.5), 5, 5));
        }
        for (VLine line : lines) {
            System.out.println("hi");
            g.drawLine((int) line.start.x, (int) line.start.y, (int) line.end.x, (int) line.end.y);

        }
    }
    public void paint(Graphics g) {
        g.drawImage(img, 0, 0, this);
    }
    public static void main(String[] args){

        new Controller().setVisible(true);
    }


}
