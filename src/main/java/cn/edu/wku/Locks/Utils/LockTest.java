package cn.edu.wku.Locks.Utils;

import cn.edu.wku.Locks.MutexLock;
import cn.edu.wku.Locks.SpinLock;
import cn.edu.wku.UIFrame;
import cn.edu.wku.WorkLoad.BoundedContainer;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;

public class LockTest {
    UIFrame UI = new UIFrame();
    private double TotalAmount = UI.getTotalAmout();
    private double Trails = UI.getTrail();
//    private double TotalAmount = 3000;
//    private double Trails = 10;
    private int Base = (int)(Math.pow(TotalAmount, 1.0/Trails));
    private int[] ProcessInGroup = new int[(int)Trails]; //store the # of process in each trails of group
    private int[] GroupsInTrail = new int[(int)Trails];

    public int getBase() {
        return Base;
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

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
//        LockTest test = new LockTest();
//        System.out.println("Base: " + test.getBase());
//        test.computeProcess(test.getTotalAmount(), test.getTrails(), test.getBase());
//        test.computeGroupsInTrail(test.getTotalAmount(), test.getTrails(), test.getProcessInGroup());
//        for(int i = 0; i < test.getTrails(); i++){
//            System.out.println("Process in one group of trail " + (i+1) + ": " + test.getProcessInGroup(i) +
//                    "; # of Groups in this trail: " + test.getGroupsInTrail(i) +
//                    ";  Total Amount of Process: " + test.getProcessInGroup(i) * test.getGroupsInTrail(i));
//        }

//        if (UI.getSpinFlag()) {
//            Class SpinLockClass = SpinLock.class;
//            //启动10个读线程和10个写线程
//            BoundedContainer<Long> boundedContainer = new BoundedContainer<>(10, (Lock) SpinLockClass.getDeclaredConstructor().newInstance());
//        }
    }
}
