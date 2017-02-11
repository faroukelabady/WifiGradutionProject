/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package projectInterface;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

/**
 *
 * @author farouk
 */
public class Delay extends JFrame{

    JLabel commandSentLbl;
    JLabel gpsRequestLbl;
    JTextField commandSentTxt;
    JTextField gpsRequestTxt;
    JButton setDelayBtn;
    JButton defaultBtn;
    JPanel mainPnl;
//    static long commandDelay = 30;
//    static long gpsDelay = 1000;

    Delay(){
        super("Delay");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                PluginsFrame.removeTrace2();
                dispose();
            }
        });

        commandSentLbl = new JLabel("Delay between Commands(ms):");
        gpsRequestLbl = new JLabel("Delay between GPS Requests(ms):");
        commandSentTxt = new JTextField("30", 10);
        gpsRequestTxt = new JTextField("1000",10);
        setDelayBtn = new JButton("Set Delay");
        defaultBtn =  new JButton("Default Delay");
        setDelayBtn.setActionCommand("set");
        defaultBtn.setActionCommand("default");
        mainPnl = new JPanel();
        mainPnl.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = 0;
        mainPnl.add(commandSentLbl,g);
        g.gridx = 1;
        g.gridy = 0;
        mainPnl.add(commandSentTxt,g);
        g.gridx = 0;
        g.gridy = 1;
        mainPnl.add(gpsRequestLbl,g);
        g.gridx = 1;
        g.gridy = 1;
        mainPnl.add(gpsRequestTxt,g);
        g.gridx = 0;
        g.gridy = 2;
        g.fill = g.HORIZONTAL;
        mainPnl.add(setDelayBtn,g);
        g.gridx = 1;
        g.gridy = 2;
        mainPnl.add(defaultBtn,g);
        Container c = getContentPane();
        c.setLayout(new BorderLayout());
        c.add(mainPnl);
        setDelayBtn.addActionListener(new SetDelay());
        defaultBtn.addActionListener(new SetDelay());
        setSize(500, 500);
        pack();
        setResizable(false);
        //show();
        setVisible(true);
    }

    class SetDelay implements ActionListener{

        public void actionPerformed(ActionEvent e) {

            if(e.getActionCommand().equals("set")){
                try{
                WifiRobotGUI.commandDelay = Long.parseLong(commandSentTxt.getText());
                WifiRobotGUI.gpsDelay = Long.parseLong(gpsRequestTxt.getText());
                } catch(Exception ex){
                    JOptionPane.showMessageDialog(rootPane, "please confirm that"
                            + " the delay number is correct");
                }
            } else if(e.getActionCommand().equals("default")){
                WifiRobotGUI.commandDelay = 30;
                WifiRobotGUI.gpsDelay = 1000;
                commandSentTxt.setText(""+WifiRobotGUI.commandDelay);
                gpsRequestTxt.setText(""+WifiRobotGUI.gpsDelay);
            }
        }

    }


//    public static void main(String[] args){
//        Delay delay = new Delay();
//    }
}
