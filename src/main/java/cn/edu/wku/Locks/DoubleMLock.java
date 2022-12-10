package cn.edu.wku.Locks;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class DoubleMLock implements Lock {

    // The number of attempts in busy waiting before yielding
    private static final int SPIN_TIMES = 1000;

    /* Coordinate Variables */

    // The max number of nodes in the MCS queue
    private final int MAX_SPINNING_NODES_NUM;
    // The current number of nodes in the MCS queue
    private final AtomicInteger spinningNodesNum = new AtomicInteger(0);

    // Get the number of CPU cores
    private static int getCPUCoreNum() {
        return Runtime.getRuntime().availableProcessors();
    }

    /* Mutex Lock */

    private final MutexLock mutexLock = new MutexLock();

    /* MCS Lock */

    private final MCSLock mcsLock = new MCSLock();

    //////////////////////////////////////////////////////////////////////////////////////////

    public DoubleMLock() {
        // JVM takes two threads
        //  one for the main method
        //  the other for Garbage Collection
        // Mutex Lock takes one thread to try adding nodes to MCS queue
        this(Math.max(getCPUCoreNum()-3, 1));
    }

    public DoubleMLock(int maxSpinningNodesNum) {
        if (maxSpinningNodesNum <= 0) throw new IllegalArgumentException();
        MAX_SPINNING_NODES_NUM = maxSpinningNodesNum;
    }

    @Override
    public void lock() {
        // Obtain Mutex Lock --> might sleep
        mutexLock.lock();
        // Add the thread to the MCS Lock
        // Busy wait the MCS queue to have more space
        boolean flag = true;
        while (flag) {
            for (int i = 0; i < SPIN_TIMES; i++) {
                if (spinningNodesNum.get() < MAX_SPINNING_NODES_NUM) {
                    // Find spare seats --> occupy
                    spinningNodesNum.getAndIncrement();
                    flag = false;
                    break;
                }
            }
            // Increase the priority of this thread in CPU scheduling
            //  which tends to automatically decrease in Windows over time
            //  and cause performance issue
            Thread.yield();
        }
        // Unlock the Mutex Lock --> let the next waiting thread to monitor the MCS Lock
        mutexLock.unlock();
        // Obtain MCS Lock --> then continue
        mcsLock.lock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        // Not supported by MCSLock
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock() {
        // Not supported by MutexLock
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        // Not supported by MutexLock
        throw new UnsupportedOperationException();
    }

    @Override
    public void unlock() {
        // Release MCS lock
        mcsLock.unlock();
        // Let in one thread from Mutex Lock
        spinningNodesNum.getAndDecrement();
    }

    @Override
    public Condition newCondition() {
        // Not supported by MCSLock
        throw new UnsupportedOperationException();
    }
}
