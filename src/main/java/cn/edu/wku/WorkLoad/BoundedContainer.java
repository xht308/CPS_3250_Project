package cn.edu.wku.WorkLoad;

import cn.edu.wku.Locks.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;

public class BoundedContainer<E> {
    public static final int DEFAULT_CAPACITY = 10;
    private final Object[] elements;//容器底层的数据结构
    private final Lock lock;//锁对象
    private int elementCount;//数组elements中的元素数量
    private int putIndex;//写指针
    private int takeIndex;//读指针

    public BoundedContainer() {
        // Use default settings
        this(DEFAULT_CAPACITY, new SpinLock());
    }

    public BoundedContainer(int capacity, Lock lock) {
        // Initialize data structure
        elements = new Object[capacity];
        putIndex = 0;
        takeIndex = 0;
        elementCount = 0;
        // Initialize lock
        this.lock = lock;
    }

    public BoundedContainer(int capacity, Class lockClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // Initialize data structure
        elements = new Object[capacity];
        putIndex = 0;
        takeIndex = 0;
        elementCount = 0;
        // Initialize lock
        this.lock = (Lock) lockClass.getDeclaredConstructor().newInstance();
    }

    /**
     * 放数据
     *
     * @param element
     * @throws InterruptedException
     */
    public void put(E element) throws InterruptedException {
        while (true) {
            // Acquire Lock - Critical Section
//            System.out.println("Put Lock");
            lock.lock();
//            System.out.println("Put Locked");
            // Check queue size
            if (elementCount == elements.length) {
                // Full --> release lock and try again
//                System.out.println("Put Unlock");
                lock.unlock();
//                System.out.println("Put Unlocked");
                continue;
            }
            // Not full --> put the element
            elements[putIndex++] = element;
            System.out.println("put");
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

    /**
     * 取数据
     *
     * @return
     * @throws InterruptedException
     */
    public E take() throws InterruptedException {
        while (true) {
            // Acquire Lock - Critical Section
//            System.out.println("Take Lock");
            lock.lock();
//            System.out.println("Take Locked");
            // Check queue size
            if (elementCount == 0) {
                // Empty --> release lock and try again
//                System.out.println("Take Unlock");
                lock.unlock();
//                System.out.println("Take Unlocked");
                continue;
            }
            // Not empty --> take one element
            E element = (E) elements[takeIndex++];
            System.out.println("take");
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

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class lockClass = MutexLock.class;
        //启动10个读线程和10个写线程
        BoundedContainer<String> boundedContainer = new BoundedContainer<>(10, (Lock) lockClass.getDeclaredConstructor().newInstance());

        int takeThreadNum = 5;
        int putThreadNum = 5;

        ArrayList<Thread> threads = new ArrayList<>();

        for (int i = 0; i < takeThreadNum; i++) {
            threads.add(new Thread(() -> {
                try {
                    boundedContainer.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }));
        }

        for (int i = 0; i < putThreadNum; i++) {
            threads.add(new Thread(() -> {
                try {
                    boundedContainer.put("hi");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }));
        }

        Long start = System.currentTimeMillis();

        for (Thread thread: threads) thread.start();

        for (Thread thread: threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        Long end = System.currentTimeMillis();
        System.out.println(end-start);
    }
}
