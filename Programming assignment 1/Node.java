public class Node<E>{
    E element;
    Node<E> next;
    Node<E> leader;
    int clusterSize;

   public Node(E elem) {
        this.element = elem;
        this.next = null;
        this.leader = this;   // initially its own leader
        this.clusterSize = 1;
    }
}