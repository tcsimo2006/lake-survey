import java.util.*;

public class Partition<E>{
    private int clusterCount;
    private List<Node<E>> allNodes = new ArrayList<>();

    public Partition(){
        clusterCount = 0;
    }
    //Creates a new node and cluster by extension
    public Node<E> makeCluster(E x){
        Node<E> n = new Node<>(x);
        allNodes.add(n);
        clusterCount++;
        return n;
    }
    public List<Node<E>> getAllNodes(){
        return allNodes;
    }
    //return leader of given cluster
    public Node<E> find(Node<E> p){
        return p.leader;
    }
    //Merges 2 clusters
    public void union(Node<E> p, Node<E> q){
        Node<E> leaderp = find(p);
        Node<E> leaderq = find(q);
        if (leaderp == leaderq) return; //Makes sure they have a different leader

        if (leaderp.clusterSize < leaderq.clusterSize) { //makes sure p is always the larger one
            Node<E> temp = leaderp;
            leaderp = leaderq;
            leaderq = temp;
        }

        Node<E> tail = leaderp;
        while (tail.next != null) {
            tail = tail.next;
        }
        tail.next = leaderq;
        Node<E> cur = leaderq;
        while (cur!=null){
            cur.leader = leaderp;
            cur = cur.next;
        }

        leaderp.clusterSize+= leaderq.clusterSize;
        clusterCount--;
    }
    //returns the element of a given cluster
    public E elementE(Node<E> p){
        return p.element;
    }
    //returns the total amount of clusters
    public int numberOfClusters(){
        return clusterCount;
    }
    //returns the size of a cluster by finding the leader of given node
    public int clusterSize(Node<E> p){
        return find(p).clusterSize;
    }
    public List<Node<E>> clusterPositions(Node<E> p){
        Node<E> lead = find(p);
        List<Node<E>> result = new ArrayList<>();
        Node<E> cur = lead;
        while (cur!=null){
            result.add(cur);
            cur = cur.next;
        }
        return result;
    } 
    public List<Integer> clusterSizes() {
        Map<Node<E>, Integer> seen = new HashMap<>();
        List<Integer> sizes = new ArrayList<>();
        for (Node<E> node : allNodes) {
            Node<E> leader = node.leader;
            if (!seen.containsKey(leader)) {
                sizes.add(leader.clusterSize);
                seen.put(leader, 1);
            }
        }
        sizes.sort(Collections.reverseOrder());
        return sizes;
    }
    
}

