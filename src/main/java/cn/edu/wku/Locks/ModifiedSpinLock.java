package cn.edu.wku.Locks;

import java.util.concurrent.TimeUnit;

public class ModifiedSpinLock extends SpinLock {

    private volatile Thread lastThread = null;

    public ModifiedSpinLock() {

    }

    // Check the status of the lock
    //  is available --> occupy the lock and continue
    //  not available --> perform busy waiting
    @Override
    public void lock() {
        // Prevent repeated locking
        while (Thread.currentThread() == lastThread);
        // Continue locking
        super.lock();
    }

    // Check the status of the lock and whether the thread is interrupted
    //  This thread is interrupted --> throw exception
    //      Lock is available --> occupy the lock and continue
    //      not available --> perform busy waiting
    @Override
    public void lockInterruptibly() throws InterruptedException {
        // Prevent repeated locking
        while (Thread.currentThread() == lastThread) if (Thread.interrupted()) throw new InterruptedException();
        // Continue locking
        super.lockInterruptibly();
    }

    // Attempt to get the lock once with no thread blocking
    @Override
    public boolean tryLock() {
        if (Thread.currentThread() == lastThread) return false;
        return super.tryLock();
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
            if (tryLock()) return true;
            // Check the deadline
        } while (System.nanoTime() < deadline);
        // Fail to obtain the lock
        return false;
    }

    // Release the lock
    @Override
    public void unlock() {
        // Record this thread as the last thread executed
        lastThread = Thread.currentThread();
        // Continue unlocking
        super.unlock();
    }

}
