package cn.edu.wku.WorkLoad;

import cn.edu.wku.Locks.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
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
        Long timeCost = test(250, 400, DoubleMLock.class);
        System.out.println(timeCost == null? "time out": timeCost);
    }

    public static Long test(int operationNumPerThread, int totalPutTakePairNum, Class<? extends Lock> lockClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return test(operationNumPerThread, totalPutTakePairNum, lockClass.getDeclaredConstructor().newInstance());
    }

    public static Long test(int operationNumPerThread, int totalPutTakePairNum, Class<? extends Lock> lockClass, long timeOut, TimeUnit unit) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return test(operationNumPerThread, totalPutTakePairNum, lockClass.getDeclaredConstructor().newInstance() ,timeOut, unit);
    }

    public static Long test(int operationNumPerThread, int totalThreadsNum, Lock lock) {
        // Having a default max testing time of 7s = 7000ms
        final long DEFAULT_TIME_OUT = 7000;
        final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MILLISECONDS;
        return test(operationNumPerThread, totalThreadsNum, lock, DEFAULT_TIME_OUT, DEFAULT_TIME_UNIT);
    }

    public static Long test(int operationNumPerThread, int totalThreadsNum, Lock lock, long timeOut, TimeUnit unit) {

        // Create container instance
        BoundedContainer<Long> boundedContainer = new BoundedContainer<>(DEFAULT_CAPACITY, lock);

        // Prepare threads
        ArrayList<Thread> threads = new ArrayList<>();
        // Two types of threads
        // Put n elements to the container
        class putThread extends Thread {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < operationNumPerThread; i++) {
                        boundedContainer.put(System.nanoTime());
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        // Take n elements from the container
        class takeThread extends Thread {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < operationNumPerThread; i++) {
                        boundedContainer.take();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        // Add threads to list
        for (int i = 0; i < totalThreadsNum; i++) {
            threads.add(new putThread());
            threads.add(new takeThread());
        }
        Collections.shuffle(threads);

        boolean timeOutFlag = false;

        // Start timing
        Long start = System.currentTimeMillis();

        // Start all threads
        for (Thread thread: threads) thread.start();

        long deadLine = start + unit.toMillis(timeOut);

        // Wait for all threads to finish
        for (Thread thread: threads) {
            try {
                while (thread.isAlive()) {
                    // If exceeds the time limit --> stop all threads
                    if (System.currentTimeMillis() > deadLine) {
                        thread.stop();
                        timeOutFlag = true;
                        break;
                    }
                    else thread.join(10);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // Stop timing
        Long end = System.currentTimeMillis();

        // Return time cost
        return timeOutFlag? null: (end-start);
    }
}
