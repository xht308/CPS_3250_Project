package cn.edu.wku.Locks.Utils;

import java.util.concurrent.atomic.*;

public class ConcurrentQueue {

    private final AtomicReference<Node> head = new AtomicReference<>();
    private final AtomicReference<Node> tail = new AtomicReference<>();

    private static class Node {
        // The waiting thread the node is carrying
        private final Thread thread;

        // The waiting queue pointers
        // The waiting queue is protected by an optimistic lock (implemented in enqueue() and dequeue())
//        // The previous node in the linked list of waiting threads
//        private AtomicReference<Node> prev = new AtomicReference<>();
        // The next node in the liked list of waiting threads
        private final AtomicReference<Node> next = new AtomicReference<>();

        // Use the current thread as the waiting thread in the waiting queue
        Node() {
            this(Thread.currentThread());
        }

        // Specify a thread to be carried by the node
        Node(Thread thread) {
            this.thread = thread;
        }

        public Thread getThread() {
            return thread;
        }

//        public Node getPrev() {
//            return prev.get();
//        }
//
//        public void setPrev(Node prev) {
//            this.prev.set(prev);
//        }

        public Node getNext() {
            return next.get();
        }

        public void setNext(Node next) {
            this.next.set(next);
        }
    }

    // Add the thread to the waiting queue
    // Helper method
    public void offer(Thread thread) {
        enqueue(new Node(thread));
    }

    // Add the node to the waiting queue
    // Critical section (Can be accessed by multiple threads at a time)
    // Protected by the optimistic lock
    private void enqueue(Node node) {
        // Set the node as the last node (tail)
        Node tempTail = tail.get();
        while (!tail.compareAndSet(tempTail, node)) tempTail = tail.get();
//        // Contact the node to the previous node
//        node.setPrev(tempTail);
        // is the first node --> set head
        if (tempTail == null) head.set(node);
        // is not --> set the next pointer of the previous node
        else {
            tempTail.setNext(node);
            // In case the previous node is head and been removed during the process
            head.compareAndSet(null, node);
        }
    }

    // Extract a thread from the waiting queue
    public Thread poll() {
        Node temp = dequeue();
        return temp == null? null: temp.getThread();
    }


    // Extract a node from the waiting queue
    // Not a critical section (Only the unlock operation will access this method)
    private Node dequeue() {
        // Check if there is any elements in the queue --> null
        if (tail.get() == null) return null;
        // Obtain the head node
        Node ret = head.get();
        // Busy waiting to obtain the head node
        //  as it can temporarily be null (check enqueue())
        while (ret == null) ret = head.get();
        // Set tail pointer to null if it is the same as the head pointer
        tail.compareAndSet(ret, null);
        // Set head pointer to the next node
        head.compareAndSet(ret, ret.getNext());
        // Return the node
        return ret;
    }

    // Get the first thread in the queue
    public Thread peek() {
        Node temp = firstNode();
        return temp == null? null: temp.getThread();
    }

    // Get the first node in the queue
    private Node firstNode() {
        // Check if there is any elements in the queue --> null
        if (tail.get() == null) return null;
        // Obtain the head node
        Node ret = head.get();
        // Busy waiting to obtain the head node
        //  as it can temporarily be null (check enqueue())
        while (ret == null) ret = head.get();
        // Return
        return ret;
    }

}
