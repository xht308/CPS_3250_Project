package cn.edu.wku.Locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
@Deprecated
public class ClassicCLHLock implements Lock {

    // Initial tail
    private final AtomicReference<Node> tail = new AtomicReference<>(Node.DUMMY_NODE);
    private volatile Node current = Node.DUMMY_NODE;

    // Node of waiting threads
    private static class Node {
//        private final AtomicReference next = new AtomicReference<>();
        // The previous node
        private Node prev = null;
        // Completing status
        private volatile boolean isLocked;

        // Standard head node
        private static final Node DUMMY_NODE = new Node(false);

        Node() {
            this(true);
        }

        // For DUMMY construction
        private Node(boolean isLocked) {
            this.isLocked = isLocked;
        }

        public Node getPrev() {
            return prev;
        }

        void setPrev(Node prev) {
            this.prev = prev;
        }

        public boolean isLocked() {
            return isLocked;
        }

        void setLocked(boolean locked) {
            isLocked = locked;
        }
    }

    @Override
    public void lock() {
        Node node = new Node();
        // Add current thread to the waiting queue (busy waiting)
        Node tail = this.tail.get();
        while (!this.tail.compareAndSet(tail, node)) tail = this.tail.get();
        // Set prev
        node.setPrev(tail);
        // Monitor the status of the previous node
        //  once the previous node finished execution --> continue
        while (node.getPrev().isLocked());
        // Set the current node
        this.current = node;
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock() {
        Node node = new Node();
        Node tail = this.tail.get();
        if (!tail.isLocked() && this.tail.compareAndSet(tail, node)) {
            node.setPrev(tail);
            this.current = node;
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unlock() {
        unlock(current);
    }

    public void unlock(Node node) {
        node.setLocked(false);
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }
}
