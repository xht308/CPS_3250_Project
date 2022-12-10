package cn.edu.wku.Locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
@Deprecated
public class ClassicMCSLock implements Lock {

    // The last node in the waiting queue
    private final AtomicReference<Node> tail = new AtomicReference<>();
    // The current running node
    private volatile Node current = null;

    // Node data structure
    private static class Node {

        // Indicate whether the thread is ready to continue
        private volatile boolean isLocked;
        // The next node in the waiting queue
        private volatile Node next = null;

        public Node() {
            this(true);
        }

        public Node(boolean isLocked) {
            this.isLocked = isLocked;
        }

        public boolean isLocked() {
            return isLocked;
        }

        private void setLocked(boolean locked) {
            isLocked = locked;
        }

        // Deliver the lock to the next node
        private void unlockNext() {
            if (next == null) throw new NullPointerException();
            next.setLocked(false);
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }

    }

    // Obtain the lock
    @Override
    public void lock() {
        // Create node
        Node node = new Node();
        // Add node to the end of the queue
        Node tail = this.tail.get();
        while (!this.tail.compareAndSet(tail, node)) tail = this.tail.get();
        // Check the original tail
        //  empty queue --> continue execution
        if (tail == null) this.current = node;
        //  not empty --> set the next pointer of the previous node --> wait being unlocked
        else {
            tail.setNext(node);
            while (node.isLocked());
            // Set current
            this.current = node;
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock() {
        Node node = new Node();
        if (tail.compareAndSet(null, node)) {
            current = node;
            return true;
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        // Get the deadline of lock attempts
        long deadline = System.nanoTime() + unit.toNanos(time);
        // Get node object
        Node node = new Node();
        do {
            // Check whether this thread is interrupted
            if (Thread.interrupted()) throw new InterruptedException();
            // Attempt to occupy the lock
            if (tail.compareAndSet(null, node)) {
                current = node;
                return true;
            }
            // Check the deadline
        } while (System.nanoTime() < deadline);
        // Fail to obtain the lock
        return false;
    }

    @Override
    public void unlock() {
        unlock(current);
    }

    public void unlock(Node current) {
        // Check next waiting node
        if (current.getNext() == null) {
            // Ensure the node is the last in the queue
            //  is the tail --> no more nodes --> set tail null
            if (tail.compareAndSet(current, null)) return;
            //  not the tail --> there are other nodes --> wait the next pointer to update
            while (current.getNext() == null);
        }
        // Unlock the next node
        current.unlockNext();
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }
}
