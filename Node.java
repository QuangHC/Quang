import java.util.HashMap;

public class Node implements Comparable<Node> {
    private double x;
    private double y;
    public double f = Double.MAX_VALUE;
    public double g = Double.MAX_VALUE;
    private Node parent;

    private static int idAutoIncreasing = 0;
    private int id;
    private HashMap<Node, Double> neighbors;

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node(double x, double y) {
        this.id = idAutoIncreasing++;
        neighbors = new HashMap<Node, Double>();
        this.x = x;
        this.y = y;
        parent = null;
    }

    public void addEdge(Node target, double distance) {
        if (distance > 0) {
            neighbors.put(target, distance);
        }
    }

    public void removeEdge(Node target) {
        neighbors.remove(target);
    }

    public double calculateHeuristic(Node target) {
        return Math.sqrt(Math.pow(this.x - target.x, 2.0) + Math.pow(this.y - target.y, 2.0));
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public HashMap<Node, Double> getNeighbors() {
        return neighbors;
    }

    @Override
    public int compareTo(Node node) {
        return Double.compare(this.f, node.f);
    }
}