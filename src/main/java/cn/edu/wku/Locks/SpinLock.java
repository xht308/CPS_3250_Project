package cn.edu.wku.Locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.atomic.*;

public class SpinLock implements Lock  {

    // The lock indicator using atomic operations
    private final AtomicBoolean isLocked = new AtomicBoolean();

    // Construct a available spin lock
    public SpinLock() {

    }

    // Construct the lock with an initial status
    public SpinLock(boolean initialValue) {
        isLocked.set(initialValue);
    }

    // Check the status of the lock
    //  is available --> occupy the lock and continue
    //  not available --> perform busy waiting
    @Override
    public void lock() {
        while (isLocked.compareAndSet(false, true)) {}
    }

    // Check the status of the lock and whether the thread is interrupted
    //  This thread is interrupted --> throw exception
    //      Lock is available --> occupy the lock and continue
    //      not available --> perform busy waiting
    @Override
    public void lockInterruptibly() throws InterruptedException {
        while (!Thread.interrupted()) {
            if (isLocked.compareAndSet(false, true)) return;
        }
        throw new InterruptedException();
    }

    // Attempt to get the lock once with no thread blocking
    @Override
    public boolean tryLock() {
        return isLocked.compareAndSet(false, true);
    }

    // Attempt to get the lock with no thread blocking during a specific period of time
    //  A time-limited lockInterruptibly() method
    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        // Get the deadline of lock attempts
        long deadline = System.nanoTime() + unit.toNanos(time);
        do {
            // Check whether this thread is interrupted
            if (Thread.interrupted()) throw new InterruptedException();
            // Attempt to occupy the lock
            if (isLocked.compareAndSet(false, true)) return true;
        // Check the deadline
        } while (System.nanoTime() < deadline);
        // Fail to obtain the lock
        return false;
    }

    // Release the lock
    //  is occupied --> set it unoccupied
    //  is not occupied --> throw IllegalMonitorStateException
    @Override
    public void unlock() {
        if (!isLocked.compareAndSet(true, false)) throw new IllegalMonitorStateException();
    }

    // Not implemented --> Throw UnsupportedOperationException
    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }



}
