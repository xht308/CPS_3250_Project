package cn.edu.wku;

//import org.jfree.chart.ui.RefineryUtilities;

import cn.edu.wku.Locks.Utils.ConcurrentQueue;

public class Main {
    public static void main(String[] args) {
        UIFrame UIFrame = new UIFrame();
        UIFrame.init();

        ConcurrentQueue<Thread> queue = new ConcurrentQueue<>();
        for (int i = 0; i < 10; i++) {
            new Thread(() -> queue.offer(Thread.currentThread())).start();
        }
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Thread thread = queue.poll();
        while (thread != null) {
            System.out.println(thread);
            thread = queue.poll();
        }
    }
}