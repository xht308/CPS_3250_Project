package cn.edu.wku.Locks;

import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

@Deprecated
public class ClassicSpinLock implements Lock  {

    // The lock indicator using atomic operations
    private final AtomicBoolean isLocked = new AtomicBoolean();

    // Construct a available spin lock
    public ClassicSpinLock() {

    }

    // Construct the lock with an initial status
    public ClassicSpinLock(boolean initialValue) {
        isLocked.set(initialValue);
    }

    // Check the status of the lock
    //  is available --> occupy the lock and continue
    //  not available --> perform busy waiting
    @Override
    public void lock() {
        while (!isLocked.compareAndSet(false, true));
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
        if (!isLocked.compareAndSet(true, false)) {
            throw new IllegalMonitorStateException();
        }
    }

    // Not implemented --> Throw UnsupportedOperationException
    @Override
    public Condition newCondition() {
        return new ConditionObject();
    }

    private static class WaitingThread {
        final Thread thread;
        volatile boolean signaled;

        WaitingThread() {
            this(Thread.currentThread());
        }

        WaitingThread(Thread thread) {
            this.thread = thread;
        }
    }

    private class ConditionObject extends ConcurrentLinkedQueue<WaitingThread> implements Condition {

        // Default constructor
        ConditionObject() {
        }

        // Ensure the lock associated with this condition is locked
        // Otherwise the thread cannot be blocked by the condition
        private void checkLock() {
            if (!isLocked.get()) {
                throw new IllegalMonitorStateException();
            }
        }

        // Add the thread to the condition queue
        //  if the lock is not currently held by the thread --> remove the thread from queue --> throw IllegalMonitorStateException
        //  if the lock is signaled by other thread during the execution of this thread --> recall the signal function (lock is not yet released)
        private void enqueueAndUnlock(WaitingThread thread) {
            // The enqueue operation need to be conducted before the release of the lock
            //  thus it is protected by the checkLock() method
            add(thread);
            // Release the lock
            if (!isLocked.compareAndSet(true, false)) {
                // The lock is released for some reason --> remove the lock from the queue --> throw IllegalMonitorStateException
                if (!remove(thread)) {
                    // The lock is removed from the queue during the operation (signal() is called) --> recall signal() to pass to other waiting threads
                    signal();
                }
                throw new IllegalMonitorStateException();
            }
        }

        // Put the current thread to sleeping state waiting to be signaled
        // After being signaled --> obtain the lock and continue execution
        @Override
        public void await() throws InterruptedException {
            // Check  the lock availability
            checkLock();
            // If interrupted --> throw
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            // Create the WaitingThread Object to be enqueued
            WaitingThread th = new WaitingThread(Thread.currentThread());
            // Indicate whether the thread finished its sleeping by signaling
            boolean failed = false;
            // Add the thread to the waiting queue
            enqueueAndUnlock(th);
            try {
                // Set the thread to sleep
                LockSupport.park(this);
                // If the thread is weakened by interruption
                if (Thread.interrupted()) {
                    failed = true;
                    throw new InterruptedException();
                }
            } catch (RuntimeException | Error e) {
                failed = true;
                throw e;
            } finally {
                // Obtain the lock to continue execution
                lock();
                // In case the thread is being signaled by others
                //  having been removed from the queue but not yet set signaled statement
                if (!th.signaled) {
                    // Situation proved
                    failed = !remove(th);
                }
                // The thread failed to stay in the sleeping state but being signaled by others --> pass the signal
                if (failed) {
                    signal();
                }
            }
        }

        @Override
        public void awaitUninterruptibly() {
            // Check the lock availability
            checkLock();
            // Create the WaitingThread Object to be enqueued
            WaitingThread th = new WaitingThread(Thread.currentThread());
            // Indicate whether the thread has been interrupted
            boolean interrupted = false;
            // Indicate whether the thread finished its sleeping by signaling
            boolean failed = false;
            // Add the thread to the waiting queue
            enqueueAndUnlock(th);
            try {
                // Keep the thread in sleeping state until it is signaled
                do {
                    // Set the thread to sleep
                    LockSupport.park(this);
                    // If the thread weakened up by interruption --> set it to sleep again
                    if (Thread.interrupted()) {
                        // save interrupt status so we can restore on exit
                        interrupted = true;
                    }
                    // loop until we've been signaled, ignoring wake-ups caused by interruption
                } while (!th.signaled);
            } catch (RuntimeException | Error e) {
                failed = true;
                throw e;
            } finally {
                // Obtain the lock to continue execution
                lock();
                // In case the thread is being signaled by others
                //  having been removed from the queue but not yet set signaled statement
                if (!th.signaled) {
                    // Situation proved
                    failed = !remove(th);
                }
                // The thread failed to stay in the sleeping state but being signaled by others --> pass the signal
                if (failed) {
                    signal();
                }
                // restore interrupt status on exit
                if (interrupted) {
                    th.thread.interrupt();
                }
            }
        }

        @Override
        public long awaitNanos(long nanosTimeout) throws InterruptedException {
            // Check  the lock availability
            checkLock();
            // If interrupted --> throw
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            long start = System.nanoTime();
            // Create the WaitingThread Object to be enqueued
            WaitingThread th = new WaitingThread(Thread.currentThread());
            // Indicate whether the thread finished its sleeping by signaling
            boolean failed = false;
            long ret;
            // Add the thread to the waiting queue
            enqueueAndUnlock(th);
            try {
                // Set the thread to sleep for the specific time period
                LockSupport.parkNanos(this, nanosTimeout);
                // If the thread is weakened by interruption
                if (Thread.interrupted()) {
                    failed = true;
                    throw new InterruptedException();
                }
                // Prepare the return value (leftover time)
                ret = nanosTimeout - (System.nanoTime() - start);
            } catch (RuntimeException | Error e) {
                failed = true;
                throw e;
            } finally {
                // Obtain the lock to continue execution
                lock();
                // In case the thread is being signaled by others
                //  having been removed from the queue but not yet set signaled statement
                if (!th.signaled) {
                    // Situation Proved
                    failed = !remove(th);
                }
                // The thread failed to stay in the sleeping state but being signaled by others --> pass the signal
                if (failed) {
                    signal();
                }
            }
            return ret;
        }

        @Override
        public boolean await(long time, TimeUnit unit) throws InterruptedException {
            return awaitNanos(unit.toNanos(time)) > 0;
        }

        @Override
        public boolean awaitUntil(Date deadline) throws InterruptedException {
            Date now = new Date();
            return awaitNanos(TimeUnit.MILLISECONDS.toNanos(deadline.getTime() - now.getTime())) > 0;
        }

        @Override
        public void signal() {
            // Check the lock availability
            checkLock();
            // Extract the thread from the waiting queue
            WaitingThread th = poll();
            if (th != null) {
                // Mark the thread as being signaled
                th.signaled = true;
                // Weaken the thread
                LockSupport.unpark(th.thread);
            }
        }

        @Override
        public void signalAll() {
            // Check the lock availability
            checkLock();
            // Extract all thread from the waiting queue
            while (true) {
                WaitingThread th = poll();
                if (th == null) {
                    return;
                }
                // Mark the thread as being signaled
                th.signaled = true;
                // Weaken the thread
                LockSupport.unpark(th.thread);
            }
        }
    }

}
