package cn.edu.wku.Locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class CLHLock implements Lock {

    // Initial tail
    private final AtomicReference<Node> tail = new AtomicReference<>(Node.DUMMY_NODE);
    private volatile Node current = Node.DUMMY_NODE;

    // The number of attempts in busy waiting before yielding
    private static final int SPIN_TIMES = 1000;

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
        while (true) {
            for (int i = 0; i < SPIN_TIMES; i++) {
                if (!node.getPrev().isLocked()) {
                    this.current = node;
                    return;
                }
            }
            // Increase the priority of this thread in CPU scheduling
            //  which tends to automatically decrease in Windows over time
            //  and cause performance issue
            Thread.yield();
        }
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
