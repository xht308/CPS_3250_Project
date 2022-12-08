package cn.edu.wku;

import org.jfree.chart.ui.UIUtils;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class UIFrame {
    private long TotalAmout = 1; //total amount of the process
    private long Trail = 1; //the number of trails
    private long ProcessInGroup; //number of process in a group

    Boolean SpinFlag = false; //Spin Lock is not choose
    Boolean MutexFlag = false; //Mutex Lock is not choose
    Boolean MCSFlag = false; //MCS Lock is not choose
    Boolean ImprovedMCSFlag = false; //Improved MCS Lock is not choose

    JFrame frame = new JFrame();

    public UIFrame() {

    }

    public long getTotalAmout() {
        return TotalAmout;
    }

    public void setTotalAmout(long totalAmout) {
        TotalAmout = totalAmout;
    }

    public long getTrail() {
        return Trail;
    }

    public void setTrail(long Trail) {
        Trail = Trail;
    }

    public boolean getSpinFlag() {
        return SpinFlag;
    }
    public boolean getMutexFlag() {
        return MutexFlag;
    }
    public boolean getMCSFlag() {
        return MCSFlag;
    }
    public boolean getImprovedMutexFlag() {
        return ImprovedMCSFlag;
    }

    public void init() {
        frame.setTitle("CPS3250 Group2 Final Project");
        frame.setSize(800,450);
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


//        /**show the number of process in a group*/
//        JLabel NumLabel = new JLabel("# of process in a group: ");
//        NumLabel.setBounds(50, 220, 600, 30);
//        NumLabel.setFont(new Font("Calibri", Font.BOLD, 20));
//        frame.add(NumLabel);


        /**require the total amount of process*/
        //Label
        JLabel AmountLabel = new JLabel("Total amount of process: ");
        AmountLabel.setBounds(50, 100, 220, 30);
        AmountLabel.setFont(new Font("Calibri", Font.BOLD, 20));
        frame.add(AmountLabel);

        //Text
        JTextField AmountText = new JTextField();
        AmountText.setBounds(270, 100, 100, 30);
        frame.add(AmountText);

        //Button
        JButton sure1 = new JButton("sure");
        sure1.setBounds(380,100,70,30);
        sure1.setFont(new Font("Calibri", Font.BOLD, 20));
        sure1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try{
                    TotalAmout = Long.parseLong(AmountText.getText());
//                	System.out.println(TotalAmout);
//                    if(TotalAmout != 1 && Trail != 1) {
//                        ProcessInGroup = TotalAmout / GroupNum;
////                        NumLabel.setText("# of process in a group: " + ProcessInGroup);
//                    }
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
////                	System.out.println(GroupNum);
//                    if(GroupNum > TotalAmout) {
//                        JOptionPane.showMessageDialog(frame, "illegal value");
//                    }
//                    else {
//                        if(TotalAmout != 1 && Trail != 1) {
//                            ProcessInGroup = (TotalAmout / GroupNum);
////                			System.out.println(ProcessInGroup);
////                            NumLabel.setText("# of process in a group: " + ProcessInGroup);
//                        }
//                    }
                }catch(NumberFormatException e1) {
                    JOptionPane.showMessageDialog(frame, "Plaese enter a number!");
                }
            }
        });
        frame.add(sure2);


        /**show the lock which is choosed*/
        JTextArea LockChoosed = new JTextArea();
        LockChoosed.setBounds(540, 240, 180, 80);
        frame.add(LockChoosed);

        /**buttons to choose the lock*/
        //button of SpinLock
        JButton SpinLock = new JButton("Spin Lock");
        SpinLock.setBounds(50,270,100,50);
        SpinLock.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!SpinFlag) {
                    LockChoosed.setText(LockChoosed.getText() + "Spin Lock is choosed\n");
                    SpinFlag = !SpinFlag;
                }else {
                    JOptionPane.showMessageDialog(frame, "This lock is already been choosed.");
                }
            }
        });
        frame.add(SpinLock);

        //button of Mutex Lock
        JButton MutexLock = new JButton("Mutex Lock");
        MutexLock.setBounds(160,270,100,50);
        MutexLock.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!MutexFlag) {
                    LockChoosed.setText(LockChoosed.getText() + "Mutex Lock is choosed\n");
                    MutexFlag = !MutexFlag;
                }else {
                    JOptionPane.showMessageDialog(frame, "This lock is already been choosed.");
                }
            }
        });
        frame.add(MutexLock);

        //Button of MCS Lock
        JButton MCSLock = new JButton("MCS Lock");
        MCSLock.setBounds(270,270,100,50);
        MCSLock.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!MCSFlag) {
                    LockChoosed.setText(LockChoosed.getText() + "MCS Lock is choosed\n");
                    MCSFlag = !MCSFlag;
                }else {
                    JOptionPane.showMessageDialog(frame, "This lock is already been choosed.");
                }
            }
        });
        frame.add(MCSLock);

        //Button of Improved MCS Lock
        JButton ImprovedMCSLock = new JButton("Improved MCS Lock");
        ImprovedMCSLock.setBounds(380,270,150,50);
        ImprovedMCSLock.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!ImprovedMCSFlag) {
                    LockChoosed.setText(LockChoosed.getText() + "Improved MCS Lock is choosed\n");
                    ImprovedMCSFlag = !ImprovedMCSFlag;
                }else {
                    JOptionPane.showMessageDialog(frame, "This lock is already been choosed.");
                }
            }
        });
        frame.add(ImprovedMCSLock);

        //Button of clear the choosed lock
        JButton Clear = new JButton("claer");
        Clear.setBounds(540,200,180,30);
        Clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LockChoosed.setText("");
                SpinFlag = false;
                MutexFlag = false;
                MCSFlag = false;
                ImprovedMCSFlag = false;
            }
        });
        frame.add(Clear);


        /**"Generate" button*/
        JButton button = new JButton("Generate");
        button.setBounds(540,100,180,60);
        button.setFont(new Font("Calibri", Font.BOLD, 20));
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                BarChartDemo1 demo = new BarChartDemo1("LineChart");
//                demo.checkLock(SpinFlag, MutexFlag,MCSFlag, ImprovedMCSFlag);
                demo.addData(100000, "MCSLock", "100");
                demo.addData(100, "MCSLock", "100000");
                demo.addData(100000, "SpinLock", "100000");
                demo.addData(100, "SpinLock", "100");
                demo.addData(50000, "MutexLock", "1000");
                demo.addData(50000, "MutexLock", "10000");
                demo.pack();
                UIUtils.centerFrameOnScreen(demo);
                demo.setVisible(true);
            }
        });
        frame.add(button);

        frame.setVisible(true);
    }
}
