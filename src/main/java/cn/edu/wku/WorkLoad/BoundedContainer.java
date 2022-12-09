package cn.edu.wku.WorkLoad;

import cn.edu.wku.Locks.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;

public class BoundedContainer<E> {
    public static final int DEFAULT_CAPACITY = 10;
    // Data container
    private final Object[] elements;
    // The lock object protecting the put and take operations
    //  ensures the modifications to the bounded container is mutually exclusive
    private final Lock lock;
    // Number of elements in the container
    // Help implement wrap around mechanism to utilize space
    private int elementCount;
    // Write pointer
    private int putIndex;
    // Read pointer
    private int takeIndex;

    public BoundedContainer() {
        // Use default settings
        //  using spinlock to synchronize the threads
        this(DEFAULT_CAPACITY, new SpinLock());
    }

    // Create a bounded container using a customized lock to do synchronization
    //  rely on java reflection
    public BoundedContainer(int capacity, Class<? extends Lock> lockClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        this(capacity, lockClass.getDeclaredConstructor().newInstance());
    }

    // Create a bounded container using a customized lock to do synchronization
    //  the lock instance need to be created in advance
    public BoundedContainer(int capacity, Lock lock) {
        // Initialize data structure
        elements = new Object[capacity];
        putIndex = 0;
        takeIndex = 0;
        elementCount = 0;
        // Initialize lock
        this.lock = lock;
    }

    // Put an element into the container
    //  enqueue
    public void put(E element) throws InterruptedException {
        while (true) {
            // Acquire Lock - Critical Section
//            System.out.println("Put Lock");
            lock.lock();
//            System.out.println("Put Locked");
            // Check queue size
            if (elementCount == elements.length) {
                // Full --> release lock and try again
                System.out.println("Put Unlock");
                lock.unlock();
                System.out.println("Put Unlocked");
                continue;
            }
            // Not full --> put the element
            elements[putIndex++] = element;
            System.out.println("put  " + element);
            // Warp Around
            if (putIndex == elements.length) putIndex = 0;
            // Add count
            elementCount++;
            // Release Lock - Exit critical section
//            System.out.println("Put Unlock");
            lock.unlock();
//            System.out.println("Put Unlocked");
            break;
        }
    }

    // Take an element from the container
    //  dequeue
    public E take() throws InterruptedException {
        while (true) {
            // Acquire Lock - Critical Section
//            System.out.println("Take Lock");
            lock.lock();
//            System.out.println("Take Locked");
            // Check queue size
            if (elementCount == 0) {
                // Empty --> release lock and try again
                System.out.println("Take Unlock");
                lock.unlock();
                System.out.println("Take Unlocked");
                continue;
            }
            // Not empty --> take one element
            E element = (E) elements[takeIndex++];
            System.out.println("take " + element);
            // Warp around
            if (takeIndex == elements.length) takeIndex = 0;
            // Decrease count
            elementCount--;
            // Release Lock - Exit critical section
//            System.out.println("Take Unlock");
            lock.unlock();
//            System.out.println("Take Unlocked");
            return element;
        }
    }

    // Testing
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // Create a bounded container with customized lock class
        BoundedContainer<Long> boundedContainer = new BoundedContainer<>(10, MutexLock.class);

        // The number of threads
        int putTakeOperationNum = 5000;
//        int takeThreadNum = 200;
//        int putThreadNum = 200;

        ArrayList<Thread> threads = new ArrayList<>();

        for (int i = 0; i < putTakeOperationNum; i++) {
            threads.add(new Thread(() -> {
                try {
                    boundedContainer.put(System.nanoTime());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }));
            threads.add(new Thread(() -> {
                try {
                    boundedContainer.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }));
        }

//        for (int i = 0; i < putThreadNum; i++) {
//
//        }

        Long start = System.currentTimeMillis();

        // Start the threads
        for (Thread thread: threads) thread.start();

        // Join the threads
        for (Thread thread: threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        Long end = System.currentTimeMillis();
        // Print the execution time
        System.out.println(end-start);
    }
}
