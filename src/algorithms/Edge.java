package algorithms;

import java.awt.Point;

public class Edge {
    public Point p, q;
    public double sommePath;

    public Edge(Point p, Point q) {
        this.p = p;
        this.q = q;
    }

    public double distance() {
        return p.distance(q);
    }

}


