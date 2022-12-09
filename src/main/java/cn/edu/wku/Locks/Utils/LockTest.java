package cn.edu.wku.Locks.Utils;

import cn.edu.wku.Locks.*;
import cn.edu.wku.WorkLoad.BoundedContainer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;

public class LockTest {
    private long TotalAmount;
    private long Trails = 1;
    private int Base = (int)(Math.pow(TotalAmount, 1.0/Trails));
    private int[] ProcessInGroup = new int[(int) Trails]; //store the # of process in each trails of group
    private int[] GroupsInTrail = new int[(int) Trails];
    DefaultCategoryDataset dataset = new DefaultCategoryDataset(); //用于存放运行时间，锁，trail

    public void setTotalAmount(long totalAmount) {
        TotalAmount = totalAmount;
    }

    public void setTrails(long trails) {
        Trails = trails;
    }

    public int getBase() {
        return Base;
    }

    public DefaultCategoryDataset getDataset() {
        return dataset;
    }

    public void computeProcess(double TotleAmount, double Trails, int Base){
        for(int i = 0; i < Trails; i++){
            ProcessInGroup[i] = (int)(TotleAmount/Math.pow(Base, i + 1));
        }
    }

    public void computeGroupsInTrail(double TotalAmount, double Trails, int[] ProcessInGroup){
        for(int i = 0; i < Trails; i++){
            GroupsInTrail[i] = (int)(TotalAmount/ProcessInGroup[i]);
        }
    }

    public BoundedContainer<Long> SpinBoundedContainer(boolean SpinFlag) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        BoundedContainer<Long> SpinBoundedContainer = new BoundedContainer<>(10, SpinLock.class);
        return SpinBoundedContainer;
    }

    public BoundedContainer<Long> MutexBoundedContainer(boolean MutexFlag) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        BoundedContainer<Long> MutexBoundedContainer = new BoundedContainer<>(10, MutexLock.class);
        return MutexBoundedContainer;
    }

    public BoundedContainer<Long> MCSBoundedContainer(boolean MCSFlag) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        BoundedContainer<Long> MCSBoundedContainer = new BoundedContainer<>(10, MCSLock.class);
        return MCSBoundedContainer;
    }

    public BoundedContainer<Long> CLHBoundedContainer(boolean CLHFlag) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        BoundedContainer<Long> CLHBoundedContainer = new BoundedContainer<>(10, CLHLock.class);
        return CLHBoundedContainer;
    }

    public BoundedContainer<Long> TicketBoundedContainer(boolean TicketFlag) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        BoundedContainer<Long> TicketBoundedContainer = new BoundedContainer<>(10, TicketLock.class);
        return TicketBoundedContainer;
    }

    //计算锁在所有trail内的运行时间，并将结果加入dataset
    public void builtDataset(long time, String Lock, long trail,int operationNumPerThread, int totalThreadsNum, BoundedContainer<Long> boundedContainer){
        for(int i = 1; i < trail + 1; i++){
            dataset.addValue(test(operationNumPerThread, totalThreadsNum, boundedContainer), Lock, String.valueOf(i));
        }
    }

    // 计算锁在一个trail内的运行时间
    public long test(int operationNumPerThread, int totalThreadsNum, BoundedContainer<Long> boundedContainer){
        ArrayList<Thread> threads = new ArrayList<>();

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

        for (int i = 0; i < totalThreadsNum; i++) {
            threads.add(new putThread());
            threads.add(new takeThread());
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
        return end-start;
    }
}
