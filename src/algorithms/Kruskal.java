package algorithms;

import java.awt.Point;
import java.util.ArrayList;

public class Kruskal {


    public static Tree2D calculKruskal(ArrayList<Point> points, int edgeThreshold, ArrayList<Point> hitPoints,
            int[][] paths) {
        // KRUSKAL ALGORITHM, NOT OPTIMAL FOR STEINER!

        ArrayList<Edge> edges = kruskalSortedEdges(points, hitPoints, paths);

        ArrayList<Edge> kruskal = tagging(points, edges);

        return edgesToTree(kruskal, kruskal.get(0).p);
    }



    public static ArrayList<Edge> kruskalSortedEdges(ArrayList<Point> points, ArrayList<Point> hitPoints,
            int[][] paths) {
        Edge edge;
        ArrayList<Edge> edges = new ArrayList<Edge>();

        for (Point p : hitPoints) {
            for (Point q : hitPoints) {

                if (p.equals(q) || contains(edges, p, q))
                    continue;

                edge = new Edge(p, q);
                edge.sommePath = distanceTotaleDansG(p, q, paths, points);
                edges.add(edge);
            }
        }
        edges = sort(edges);
        return edges;
    }



    // public static ArrayList<Edge> ALTkruskalSortedEdges(ArrayList<Point> points, ArrayList<Point> hitPoints, int[][] paths) {
    //     Edge edge;
    //     Point mere= hitPoints.get(0);
    //     ArrayList<Edge> edges = new ArrayList<Edge>();

    //     for (Point p : hitPoints) {
    //         edge = new Edge(p, mere);
    //         edge.sommePath = distanceTotaleDansG(p, mere, paths, points);
    //         edges.add(edge);

    //     }
    //     edges = sort(edges);
    //     return edges;
    // }



    public static ArrayList<Edge> tagging(ArrayList<Point> points, ArrayList<Edge> edges) {
        ArrayList<Edge> kruskal = new ArrayList<Edge>();
        Edge current;
        NameTag forest = new NameTag(points);
        while (edges.size() != 0) {
            current = edges.remove(0);
            if (forest.tag(current.p) != forest.tag(current.q)) {
                kruskal.add(current);
                forest.reTag(forest.tag(current.p), forest.tag(current.q));
            }
        }
        return kruskal;

    }



    public static double distanceTotaleDansG(Point p, Point q, int[][] paths, ArrayList<Point> points) {
        int i = points.indexOf(p);
        int j = points.indexOf(q);
        if (i == j)
            return 0;

        int etape = paths[i][j];
        return points.get(i).distance(points.get(etape)) + distanceTotaleDansG(points.get(etape), q, paths, points);
    }




    public static boolean contains(ArrayList<Edge> edges, Point p, Point q) {
        for (Edge e : edges) {
            if (e.p.equals(p) && e.q.equals(q) || e.p.equals(q) && e.q.equals(p))
                return true;
        }
        return false;
    }



    public static Tree2D edgesToTree(ArrayList<Edge> edges, Point root) {
        ArrayList<Edge> remainder = new ArrayList<Edge>();
        ArrayList<Point> subTreeRoots = new ArrayList<Point>();
        Edge current;
        while (edges.size() != 0) {
            current = edges.remove(0);
            if (current.p.equals(root)) {
                subTreeRoots.add(current.q);
            } else {
                if (current.q.equals(root)) {
                    subTreeRoots.add(current.p);
                } else {
                    remainder.add(current);
                }
            }
        }

        ArrayList<Tree2D> subTrees = new ArrayList<Tree2D>();
        for (Point subTreeRoot : subTreeRoots)
            subTrees.add(edgesToTree((ArrayList<Edge>) remainder.clone(), subTreeRoot));

        return new Tree2D(root, subTrees);
    }



    public static ArrayList<Edge> sort(ArrayList<Edge> edges) {
        if (edges.size() == 1)
            return edges;

        ArrayList<Edge> left = new ArrayList<Edge>();
        ArrayList<Edge> right = new ArrayList<Edge>();
        int n = edges.size();
        for (int i = 0; i < n / 2; i++) {
            left.add(edges.remove(0));
        }
        while (edges.size() != 0) {
            right.add(edges.remove(0));
        }
        left = sort(left);
        right = sort(right);

        ArrayList<Edge> result = new ArrayList<Edge>();
        while (left.size() != 0 || right.size() != 0) {
            if (left.size() == 0) {
                result.add(right.remove(0));
                continue;
            }
            if (right.size() == 0) {
                result.add(left.remove(0));
                continue;
            }
            if (left.get(0).distance() < right.get(0).distance())
                result.add(left.remove(0));
            else
                result.add(right.remove(0));
        }
        return result;
    }




    public static ArrayList<Edge> transpoVersG(ArrayList<Point> points, int[][] paths, ArrayList<Edge> EdgesDansK) {

        ArrayList<Edge> EdgesDansG = new ArrayList<>();

        for (Edge edge : EdgesDansK) {
            int i = points.indexOf(edge.p);
            int j = points.indexOf(edge.q);
            ArrayList<Edge> EdgeUnfolded = new ArrayList<>();

            // ArrayList<Edge> edges = new ArrayList<>();

            int etape = i;
            int etapePlusUn = paths[etape][j];
            do {
                Edge edge2 = new Edge(points.get(etape), points.get(etapePlusUn));
                EdgeUnfolded.add(edge2);
                etape = etapePlusUn;
                etapePlusUn = paths[etapePlusUn][j];
            } while (etape != j);

            EdgesDansG.addAll(EdgeUnfolded);

        }

        return EdgesDansG;
    }
}




class NameTag {
    private ArrayList<Point> points;
    private int[] tag;

    protected NameTag(ArrayList<Point> points) {
        this.points = (ArrayList<Point>) points.clone();
        tag = new int[points.size()];
        for (int i = 0; i < points.size(); i++)
            tag[i] = i;
    }

    protected void reTag(int j, int k) {
        for (int i = 0; i < tag.length; i++)
            if (tag[i] == j)
                tag[i] = k;
    }

    protected int tag(Point p) {
        for (int i = 0; i < points.size(); i++)
            if (p.equals(points.get(i)))
                return tag[i];
        return 0xBADC0DE;
    }

}
