package cn.edu.wku.Locks;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class TicketLock implements Lock {

    // The number of attempts in busy waiting before yielding
    private static final int SPIN_TIMES = 1000;

    // Store the number of next ticket
    private final AtomicLong ticket = new AtomicLong(0);
    // Store the number of currently running thread
    private volatile long flag = 0;

    public TicketLock() {

    }

    @Override
    public void lock() {
        // Get ticket number
        long myTicket = ticket.getAndIncrement();
        // Busy waiting
        while (true) {
            for (int i = 0; i < SPIN_TIMES; i++) {
                // Check the process
                if (flag == myTicket) return;
            }
            // Increase the priority of this thread in CPU scheduling
            //  which tends to automatically decrease in Windows over time
            //  and cause performance issue
            Thread.yield();
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
//        // Get ticket number
//        long myTicket = ticket.getAndIncrement();
//        // Busy waiting
//        while (!Thread.interrupted()) {
//            if (myTicket == flag) return;
//        }
//        throw new InterruptedException();
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock() {
        return ticket.compareAndSet(flag, flag+1);
    }

    @Override
    public boolean tryLock(long time, @NotNull TimeUnit unit) throws InterruptedException {
        // Get the deadline of lock attempts
        long deadline = System.nanoTime() + unit.toNanos(time);
        int attemptCount = 0;
        do {
            // Check whether this thread is interrupted
            if (Thread.interrupted()) throw new InterruptedException();
            // Attempt to occupy the lock
            if (ticket.compareAndSet(flag, flag+1)) return true;
            // Check yield
            if (++attemptCount == SPIN_TIMES) {
                Thread.yield();
                attemptCount = 0;
            }
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
