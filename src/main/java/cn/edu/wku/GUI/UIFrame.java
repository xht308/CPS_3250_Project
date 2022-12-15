package cn.edu.wku.GUI;

import cn.edu.wku.Locks.Utils.LockTest;
import org.jfree.chart.ui.UIUtils;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class UIFrame {
    private long TotalAmout = 1; //total amount of the operations
    private long Trail = 1; //the number of trails
    private Boolean SpinFlag = false; //Spin Lock is not chosen
    private Boolean MutexFlag = false; //Mutex Lock is not chosen
    private Boolean MCSFlag = false; //MCS Lock is not chosen
    private Boolean CLHFlag = false; //CLH Lock is not chosen
    private Boolean TicketFlag = false; //Ticket Lock is not chosen
    private Boolean DoubleMFlag = false; //DoubleM Lock is not chosen

    JFrame frame = new JFrame();

    public UIFrame() {

    }

    public long getTotalAmout() {
        return TotalAmout;
    }

    public long getTrail() {
        return Trail;
    }

    public void init() {
        frame.setTitle("CPS3250 Group2 Final Project");
        frame.setSize(750,450);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setFocusable(true);
        frame.setLayout(null);

        /**Title*/
        JLabel TitleLabel = new JLabel("Performance of Different Lock");
        TitleLabel.setBounds(250, 40, 250, 20);
        TitleLabel.setFont(new Font("Calibri", Font.BOLD, 20));
        frame.add(TitleLabel);


        /**require the total amount of operations*/
        //Label
        JLabel AmountLabel = new JLabel("Total amount of operations: ");
        AmountLabel.setBounds(50, 100, 250, 30);
        AmountLabel.setFont(new Font("Calibri", Font.BOLD, 20));
        frame.add(AmountLabel);

        //Text
        JTextField AmountText = new JTextField();
        AmountText.setBounds(290, 100, 100, 30);
        frame.add(AmountText);

        //Button
        JButton sure1 = new JButton("sure");
        sure1.setBounds(400,100,70,30);
        sure1.setFont(new Font("Calibri", Font.BOLD, 20));
        sure1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try{
                    TotalAmout = Long.parseLong(AmountText.getText());
                }catch(NumberFormatException e1) {
                    JOptionPane.showMessageDialog(frame, "Plaese enter a number!");
                }
            }
        });
        frame.add(sure1);


        /**require the number of trail*/
        //Label
        JLabel GroupLabel = new JLabel("Number of trails: ");
        GroupLabel.setBounds(50, 160, 150, 30);
        GroupLabel.setFont(new Font("Calibri", Font.BOLD, 20));
        frame.add(GroupLabel);

        //Text
        JTextField GroupText = new JTextField();
        GroupText.setBounds(200, 160, 100, 30);
        frame.add(GroupText);

        //Button
        JButton sure2 = new JButton("sure");
        sure2.setBounds(310,160,70,30);
        sure2.setFont(new Font("Calibri", Font.BOLD, 20));
        sure2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try{
                    Trail = Long.parseLong(GroupText.getText());
                }catch(NumberFormatException e1) {
                    JOptionPane.showMessageDialog(frame, "Plaese enter a number!");
                }
            }
        });
        frame.add(sure2);


        /**show the lock which is chosen*/
        JTextArea LockChoosed = new JTextArea();
        LockChoosed.setBounds(500, 240, 180, 90);
        frame.add(LockChoosed);

        /**buttons to choose the lock*/
        //button of SpinLock
        JButton SpinLock = new JButton("Spin Lock");
        SpinLock.setBounds(50,220,100,50);
        SpinLock.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!SpinFlag) {
                    LockChoosed.setText(LockChoosed.getText() + "Spin Lock is chosen\n");
                    SpinFlag = !SpinFlag;
                }else {
                    JOptionPane.showMessageDialog(frame, "This lock is already been chosen.");
                }
            }
        });
        frame.add(SpinLock);

        //button of Mutex Lock
        JButton MutexLock = new JButton("Mutex Lock");
        MutexLock.setBounds(160,220,100,50);
        MutexLock.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!MutexFlag) {
                    LockChoosed.setText(LockChoosed.getText() + "Mutex Lock is chosen\n");
                    MutexFlag = !MutexFlag;
                }else {
                    JOptionPane.showMessageDialog(frame, "This lock is already been chosen.");
                }
            }
        });
        frame.add(MutexLock);

        //Button of MCS Lock
        JButton MCSLock = new JButton("MCS Lock");
        MCSLock.setBounds(270,220,100,50);
        MCSLock.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!MCSFlag) {
                    LockChoosed.setText(LockChoosed.getText() + "MCS Lock is chosen\n");
                    MCSFlag = !MCSFlag;
                }else {
                    JOptionPane.showMessageDialog(frame, "This lock is already been chosen.");
                }
            }
        });
        frame.add(MCSLock);

        //button of CLH Lock
        JButton CLHLock = new JButton("CLH Lock");
        CLHLock.setBounds(50,280,100,50);
        CLHLock.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!CLHFlag) {
                    LockChoosed.setText(LockChoosed.getText() + "CLH Lock is chosen\n");
                    CLHFlag = !CLHFlag;
                }else {
                    JOptionPane.showMessageDialog(frame, "This lock is already been chosen.");
                }
            }
        });
        frame.add(CLHLock);

        //button of Ticket Lock
        JButton TicketLock = new JButton("Ticket Lock");
        TicketLock.setBounds(160,280,100,50);
        TicketLock.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!TicketFlag) {
                    LockChoosed.setText(LockChoosed.getText() + "Ticket Lock is chosen\n");
                    TicketFlag = !TicketFlag;
                }else {
                    JOptionPane.showMessageDialog(frame, "This lock is already been chosen.");
                }
            }
        });
        frame.add(TicketLock);

        //button of DoubleM Lock
        JButton DoubleMLock = new JButton("DoubleM Lock");
        DoubleMLock.setBounds(270,280,120,50);
        DoubleMLock.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!DoubleMFlag) {
                    LockChoosed.setText(LockChoosed.getText() + "DoubleM Lock is chosen\n");
                    DoubleMFlag = !DoubleMFlag;
                }else {
                    JOptionPane.showMessageDialog(frame, "This lock is already been chosen.");
                }
            }
        });
        frame.add(DoubleMLock);

        //Button of clear the chosen lock
        JButton Clear = new JButton("clear");
        Clear.setBounds(500,200,180,30);
        Clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LockChoosed.setText("");
                SpinFlag = false;
                MutexFlag = false;
                MCSFlag = false;
                CLHFlag = false;
                TicketFlag = false;
                DoubleMFlag = false;
            }
        });
        frame.add(Clear);


        /**"Generate" button*/
        JButton button = new JButton("Generate");
        button.setBounds(500,100,180,60);
        button.setFont(new Font("Calibri", Font.BOLD, 20));
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(getTotalAmout() == 1 && getTrail() == 1){
                    JOptionPane.showMessageDialog(frame, "Please make sure your entered.");
                } else if (LockChoosed.getText().equals("")) {
                    JOptionPane.showMessageDialog(frame, "Please choose the lock.");
                } else{
                    LockTest test = new LockTest();
                    test.setTotalAmount(getTotalAmout());
                    test.setTrails(getTrail());
                    test.setBase(test.getTotalAmount(), test.getTrails());
                    if(test.getBase() == 1){
                        JOptionPane.showMessageDialog(frame, "The trail is too large, please enter a smaller value.");
                    }else{
                        test.computeProcess(test.getTotalAmount(), test.getTrails(), test.getBase());
                        test.computeGroupsInTrail(test.getTotalAmount(), test.getTrails(), test.getProcessInGroup());

                        try {
                            if(SpinFlag){
                                test.builtDataset(cn.edu.wku.Locks.SpinLock.class, "Spin Lock", getTrail());
                            }
                            if(MutexFlag){
                                test.builtDataset(cn.edu.wku.Locks.MutexLock.class, "Mutex Lock", getTrail());
                            }
                            if(MCSFlag){
                                test.builtDataset(cn.edu.wku.Locks.MCSLock.class, "MCS Lock", getTrail());
                            }
                            if(CLHFlag){
                                test.builtDataset(cn.edu.wku.Locks.CLHLock.class, "CLH Lock", getTrail());
                            }
                            if(TicketFlag){
                                test.builtDataset(cn.edu.wku.Locks.TicketLock.class, "Ticket Lock", getTrail());
                            }
                            if(DoubleMFlag){
                                test.builtDataset(cn.edu.wku.Locks.DoubleMLock.class, "DoubleM Lock", getTrail());
                            }
                        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException |
                                 IllegalAccessException ex) {
                            throw new RuntimeException(ex);
                        }

                        BarChartFrame demo = new BarChartFrame("LineChart", test.getDataset());
                        demo.pack();
                        UIUtils.centerFrameOnScreen(demo);
                        demo.setVisible(true);
                    }
                }
            }
        });
        frame.add(button);

        frame.setVisible(true);
    }
}