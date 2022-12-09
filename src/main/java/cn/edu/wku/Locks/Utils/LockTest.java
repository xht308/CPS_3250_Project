package cn.edu.wku.Locks.Utils;

import cn.edu.wku.UIFrame;

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

    public double getTotalAmount() {
        return TotalAmount;
    }

    public double getTrails() {
        return Trails;
    }

    public int[] getProcessInGroup() {
        return ProcessInGroup;
    }

    public int[] getGroupsInTrail() {
        return GroupsInTrail;
    }

    public int getProcessInGroup(int i) {
        return ProcessInGroup[i];
    }

    public int getGroupsInTrail(int i) {
        return GroupsInTrail[i];
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

    public static void main(String[] args) {
        LockTest test = new LockTest();
        System.out.println("Base: " + test.getBase());
        test.computeProcess(test.getTotalAmount(), test.getTrails(), test.getBase());
        test.computeGroupsInTrail(test.getTotalAmount(), test.getTrails(), test.getProcessInGroup());
        for(int i = 0; i < test.getTrails(); i++){
            System.out.println("Process in one group of trail " + (i+1) + ": " + test.getProcessInGroup(i) +
                    "; # of Groups in this trail: " + test.getGroupsInTrail(i) +
                    ";  Total Amount of Process: " + test.getProcessInGroup(i) * test.getGroupsInTrail(i));
        }
    }
}
