import java.util.*;

public class Graph {
    private final PriorityQueue<Node> close;
    private final PriorityQueue<Node> open;
    private final HashMap<Integer, Node> nodes;
    public Graph() {
        nodes = new HashMap<>();
        close = new PriorityQueue<>();
        open = new PriorityQueue<>();
    }

    public Node getNode(int id) {
        if (nodes.containsKey(id)) {
            return nodes.get(id);
        } else {
            return null;
        }
    }
    public void addNode(Node node) {
        nodes.put(node.getId(), node);
    }

    public void addEdge(int nodeId1, int nodeId2, double distance) {
        if (nodes.containsKey(nodeId1) && nodes.containsKey(nodeId2)) {
            nodes.get(nodeId1).addEdge(nodes.get(nodeId2), distance);
            nodes.get(nodeId2).addEdge(nodes.get(nodeId1), distance);
        }
    }

    public void removeEdge(int nodeId1, int nodeId2) {
        if (nodes.containsKey(nodeId1) && nodes.containsKey(nodeId2)) {
            nodes.get(nodeId1).removeEdge(nodes.get(nodeId2));
            nodes.get(nodeId2).removeEdge(nodes.get(nodeId1));
        }
    }

    public void removeNode(int nodeId) {
        if (nodes.containsKey(nodeId)) {
            Node temp = nodes.get(nodeId);
            for (Map.Entry<Integer, Node> entry : nodes.entrySet()) {
                entry.getValue().removeEdge(temp);
            }
        }
        nodes.remove(nodeId);
    }

    public double aStar(int startNodeId, int targetNodeId) {
        /* clear close và open, cũng như g và f của mọi node
         trước khi triển khai thuật toán (phòng trường hợp còn kết quả của lần chạy trước)*/
        close.clear();
        open.clear();
        for (Map.Entry<Integer, Node> entry : nodes.entrySet()) {
            entry.getValue().g = 0;
            entry.getValue().f = 0;
        }
        //vì nhập Id của node chứ không phải node, nên phải check xem có id trong tập node của graph hay không
        if (nodes.containsKey(startNodeId) && nodes.containsKey(targetNodeId)) {
            Node startNode = nodes.get(startNodeId);
            Node targetNode = nodes.get(targetNodeId);
            startNode.g = 0;
            //tính toán h của start đến target
            startNode.f = startNode.calculateHeuristic(targetNode);
            //add target vào open
            open.add(startNode);
            // kiểm tra open có trống hay k, thuật toán sẽ dừng nếu open trống và trả về không tìm được đừng đi
            while (!open.isEmpty()) {
                //peek là lấy ra phần tử ưu tiên đầu tiên của priority queue ( không xóa)
                Node currentNode = open.peek();
                //check xem current có phải target không, nếu có thì thuật toán thành công và dừng
                if (currentNode == targetNode) {
                    return currentNode.g;
                }

                //for này là duyệt qua toàn bộ neighbor của currentNode
                for (Map.Entry<Node, Double> neighborEntry : currentNode.getNeighbors().entrySet()) {
                    Node neighbor = neighborEntry.getKey();
                    //tình toán g của neighbor
                    double totalWeight = currentNode.g + neighborEntry.getValue();
                    //check xem nếu neighbor chưa có trong open và close thì add vào open, set g với f
                    if (!open.contains(neighbor) && !close.contains(neighbor)) {
                        neighbor.g = totalWeight;
                        neighbor.f = totalWeight + neighbor.calculateHeuristic(targetNode);
                        //set previous step cho neigbor thành current node
                        neighbor.setParent(currentNode);
                        open.add(neighbor);
                        //đây là trường hợp neighbor đã tồn tại trong open hoặc close
                    } else {
                        /*khi đã tồn tại trong open hoặc close, nghĩa là đã có đường đi đến neighbor trước
                        nên ta check xem đường mới này có ngắn hơn đường cũ không (bằng cách
                        so sánh g cũ và g mới (total weight))*/
                        if (totalWeight < neighbor.g) {
                            neighbor.g = totalWeight;
                            neighbor.f = totalWeight + neighbor.calculateHeuristic(targetNode);
                            //nếu mà đường mới đi đến neighbor ngắn hơn, thì nếu trong open sẵn rồi thì nó
                            //vẫn trong open thôi, và tự update lại vị trí trong priority queue, còn nếu trong close phải
                            //bỏ lại ra open để xét lại
                            if (close.contains(neighbor)) {
                                open.add(neighbor);
                                close.remove(neighbor);
                            }
                            //set previous step cho neigbor thành current node
                            neighbor.setParent(currentNode);
                        }
                    }
                }
                //sau khi duyệt xong hết neighbor thì cho current node vào close, xóa ở open
                open.remove(currentNode);
                close.add(currentNode);
            }

        }
        return -1;
    }


    public LinkedList<Node> getPath(int targetId)  {
        LinkedList<Node> p = new LinkedList<>();
        Node point = nodes.get(targetId);
        while (point != null) {
            p.addFirst(point);
            point = point.getParent();
        }
        return p;
    }
    public String printPath(int targetId) {
        StringBuilder pathStr = new StringBuilder();
       for (Node point: getPath(targetId)) {
            pathStr.append(" " + point.getId());
            point = point.getParent();
        }
        return String.valueOf(pathStr);
    }
}

