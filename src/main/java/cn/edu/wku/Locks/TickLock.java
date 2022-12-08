package cn.edu.wku.Locks;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class TickLock implements Lock {

    // Store the number of next ticket
    private final AtomicLong ticket = new AtomicLong(0);
    // Store the number of currently running thread
    private volatile long flag = 0;

    public TickLock() {

    }

    @Override
    public void lock() {
        // Get ticket number
        long myTicket = ticket.getAndIncrement();
        // Busy waiting
        while (myTicket != flag);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        // Get ticket number
        long myTicket = ticket.getAndIncrement();
        // Busy waiting
        while (!Thread.interrupted()) {
            if (myTicket == flag) return;
        }
        throw new InterruptedException();
    }

    @Override
    public boolean tryLock() {
        return ticket.compareAndSet(flag, flag+1);
    }

    @Override
    public boolean tryLock(long time, @NotNull TimeUnit unit) throws InterruptedException {
        // Get the deadline of lock attempts
        long deadline = System.nanoTime() + unit.toNanos(time);
        do {
            // Check whether this thread is interrupted
            if (Thread.interrupted()) throw new InterruptedException();
            // Attempt to occupy the lock
            if (ticket.compareAndSet(flag, flag+1)) return true;
            // Check the deadline
        } while (System.nanoTime() < deadline);
        // Fail to obtain the lock
        return false;
    }

    @Override
    public void unlock() {
        flag++;
    }

    @NotNull
    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }

}
