package cn.edu.wku.Locks.Utils;

import cn.edu.wku.Locks.*;
import cn.edu.wku.WorkLoad.BoundedContainer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;

public class LockTest {
    private long TotalAmount;
    private long Trails;
    private int Base;
    private int[] ProcessInGroup; //store the # of process in each trails of group
    private int[] GroupsInTrail;
    DefaultCategoryDataset dataset = new DefaultCategoryDataset(); //用于存放运行时间，锁，trail

    public void setTotalAmount(long totalAmount) {
        TotalAmount = totalAmount;
    }

    public void setTrails(long trails) {
        Trails = trails;
        ProcessInGroup = new int[(int) trails];
        GroupsInTrail = new int[(int) trails];
    }

    public long getTotalAmount() {
        return TotalAmount;
    }

    public long getTrails() {
        return Trails;
    }

    public void setBase(long TotalAmount, long Trails) {
        Base = (int)(Math.pow(TotalAmount, 1.0/Trails));
    }

    public int getBase() {
        return Base;
    }

    public int[] getProcessInGroup() {
        return ProcessInGroup;
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


    //计算锁在所有trail内的运行时间，并将结果加入dataset
    public void builtDataset(Class<? extends Lock> lockClass, String Lock, long trail) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        for(int i = 0; i < trail; i++){
            dataset.addValue(BoundedContainer.test(ProcessInGroup[i], GroupsInTrail[i], lockClass), Lock, String.valueOf(i+1));
        }
    }
}
