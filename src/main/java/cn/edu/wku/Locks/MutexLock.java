package cn.edu.wku.Locks;

import cn.edu.wku.Locks.Utils.ConcurrentQueue;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MutexLock implements Lock {

//    // Indicate whether the lock is occupied
//    private final AtomicBoolean isLocked = new AtomicBoolean();

    // The waiting queue
    private final ConcurrentQueue queue = new ConcurrentQueue();

//    private final ConcurrentLinkedQueue<Thread> queue = new ConcurrentLinkedQueue<>();

    public MutexLock() {

    }

    @Override
    public void lock() {
        // Add the thread to the waiting queue
        queue.offer(Thread.currentThread());
        // is the first thread in the queue --> continue execution
        if (queue.peek() == Thread.currentThread()) return;
        // is not --> sleep until waken by other threads
        LockSupport.park();
        // Ignore interruptions here since it is not the emphasis of the project
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        // Add the thread to the waiting queue
        queue.offer(Thread.currentThread());
        // is the first thread in the queue --> continue execution
        if (queue.peek() == Thread.currentThread()) return;
        // is not --> sleep until waken by other threads
        LockSupport.park();
        if (Thread.interrupted()) throw new InterruptedException();
    }

    // Attempt to get the lock once with no thread blocking
    // [Unsupported] because the queue does not support removal
    @Override
    public boolean tryLock() {
        throw new UnsupportedOperationException();
//        return isLocked.compareAndSet(false, true);
    }

    // Attempt to get the lock with no thread blocking during a specific period of time
    //  in busy waiting pattern
    // [Unsupported] because the queue does not support removal
    @Override
    public boolean tryLock(long time, TimeUnit unit) {
        throw new UnsupportedOperationException();
//        // Get the deadline of lock attempts
//        long deadline = System.nanoTime() + unit.toNanos(time);
//        do {
//            // Check whether this thread is interrupted
//            if (Thread.interrupted()) throw new InterruptedException();
//            // Attempt to occupy the lock
//            if (isLocked.compareAndSet(false, true)) return true;
//            // Check the deadline
//        } while (System.nanoTime() < deadline);
//        // Fail to obtain the lock
//        return false;
    }

    // Deliver the lock to next waiting thread or release the lock when no waiting threads
    @Override
    public void unlock() {
        // Remove the current thread
        queue.poll();
        // Get the next thread
        Thread next = queue.peek();
        //  if next thread exist --> wake it up (deliver the lock)
        if (next != null) LockSupport.unpark(next);
    }

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
            if (queue.peek() != Thread.currentThread()) {
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
            if (queue.peek() != Thread.currentThread()) {
                // The lock is released for some reason --> remove the thread from the queue --> throw IllegalMonitorStateException
                if (!remove(thread)) {
                    // The thread is removed from the queue during the operation (signal() is called) --> recall signal() to pass to other waiting threads
                    signal();
                }
                throw new IllegalMonitorStateException();
            }
            else unlock();
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
