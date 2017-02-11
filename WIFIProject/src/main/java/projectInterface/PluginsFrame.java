/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package projectInterface;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
/**
 *
 * @author farouk
 */
public class PluginsFrame extends JFrame {
    //Declare variables to be used
    private JButton addRemoveBtn,addRemoveBtn2;
    private JTextPane  descriptor,descriptor2;
    private JPanel pnl;
    IPCamera ip;
    Delay delay;
    static PluginsFrame pf;
    public PluginsFrame() {

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }


        });

        // intialize the GUI
        //setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        // Buttons
        addRemoveBtn = new JButton("Add");
        addRemoveBtn2 = new JButton("Add");
        // text panels
        descriptor = new JTextPane();
        descriptor2 = new JTextPane();
        // panels
        pnl = new JPanel();

        // organize the GUI
        Font font = new Font("Dialog", Font.BOLD, 14);
        descriptor.setFont(font);
        descriptor.setText("Add Camera Video Player");
        descriptor.setEditable(false);
        descriptor.setEnabled(false);
        descriptor2.setFont(font);
        descriptor2.setText("Show Delay Control");
        descriptor2.setEditable(false);
        descriptor2.setEnabled(false);
        //addRemoveBtn.setSize(100, 100);
        addRemoveBtn.setActionCommand("Add");
        addRemoveBtn2.setActionCommand("Add2");

        // st pnl to hold the fields
        pnl.setLayout(new GridLayout(2,2,5,5));
        pnl.add(descriptor);
        pnl.add(addRemoveBtn);
        pnl.add(descriptor2);
        pnl.add(addRemoveBtn2);

        // add listeners
        addRemoveBtn.addActionListener(new addRemoveBtnHandler());
        addRemoveBtn2.addActionListener(new addRemoveBtnHandler());
        Container c = getContentPane();
        c.setLayout(new BorderLayout());
        c.add(pnl,BorderLayout.NORTH);
        c.add(new JPanel(),BorderLayout.CENTER);
        setSize(400, 400);
        setResizable(true);
        setVisible(true);
    }

    static void removeTrace() {

        pf.addRemoveBtn.setActionCommand("Add");
        pf.addRemoveBtn.setText("Add");
        pf.descriptor.setEnabled(false);
    }
    static void removeTrace2() {

        pf.addRemoveBtn2.setActionCommand("Add2");
        pf.addRemoveBtn2.setText("Add");
        pf.descriptor2.setEnabled(false);
    }

//    public static void main(String[] args) {
//
//        pf = new PluginsFrame();
//    }

    class addRemoveBtnHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            Thread t = null;
            // = new IPCamera();
            if (e.getActionCommand().equals("Add")) {
                addRemoveBtn.setActionCommand("Remove");
                addRemoveBtn.setText("Remove");
                descriptor.setEnabled(true);
                ip = new IPCamera();
                t = new Thread(ip);
                t.start();
                
            } else if(e.getActionCommand().equals("Add2")){
                addRemoveBtn2.setActionCommand("Remove2");
                addRemoveBtn2.setText("Remove");
                descriptor2.setEnabled(true);
                delay = new Delay();
                
            } else if(e.getActionCommand().equals("Remove")){
                addRemoveBtn.setActionCommand("Add");
                addRemoveBtn.setText("Add");
                descriptor.setEnabled(false);
                ip.dispose();
            } else if(e.getActionCommand().equals("Remove2")){
                addRemoveBtn2.setActionCommand("Add2");
                addRemoveBtn2.setText("Add");
                descriptor2.setEnabled(false);
                delay.dispose();
            }
        }
    }

}
