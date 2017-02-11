/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.IOException;
import protocols.*;

/**
 *
 * @author farouk
 */
public class WifiRobotGUI extends JFrame implements Runnable {
    
    // declare variables to be used
    private JButton connectBtn, upBtn, downBtn, rightBtn, leftBtn, DisconnectBtn;
    private JButton addOnsBtn, getGpsDataBtn, trackGpsDataBtn;
    private JTextField IPTxt, portTxt;
    private JTextField timeTxt, latTxt, nsewTxt, longTxt, speedTxt, dateTxt;
    private JComboBox protocolsCbx;
    private JLabel IPLbl, portLbl, protocolsLbl;
    private JLabel timeLbl, latLbl, nsewLbl, longLbl, speedLbl, dateLbl;
    private JPanel txtHolderPnl, directionHolderPnl, protocolHolderPnl;
    private JPanel addOnsHolderPnl, gpsPnl;
    private JPanel[] dummeyPnl;
    private ConnectionProtocol protocol;
    private String[] dataOfGps;
    private char format = 'M';
    private char data = 0X30;
    private ImageIcon[] icons = new ImageIcon[8];
    static long commandDelay = 30;
    static long gpsDelay = 1000;
    public WifiRobotGUI() {

        // intialize the GUI
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {

                //setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
                dispose();
                if (protocol != null) {
                    protocol.closeConnection();
                }
                System.exit(1);
            }
        });

        String cr = System.getProperty("user.dir");
        icons[0] = new ImageIcon(cr + "\\onebit_30.png");
        icons[1] = new ImageIcon(cr + "\\onebit_28.png");
        icons[2] = new ImageIcon(cr + "\\onebit_27.png");
        icons[3] = new ImageIcon(cr + "\\onebit_29.png");
        icons[4] = new ImageIcon(cr + "\\arr_ani_03e.gif");
        icons[5] = new ImageIcon(cr + "\\arr_ani_04e.gif");
        icons[6] = new ImageIcon(cr + "\\arr_ani_01e.gif");
        icons[7] = new ImageIcon(cr + "\\arr_ani_02e.gif");
        // Buttons
        connectBtn = new JButton("Connect");
        DisconnectBtn = new JButton("DisConnect");
        upBtn = new JButton(icons[0]);
        downBtn = new JButton(icons[1]);
        rightBtn = new JButton(icons[2]);
        leftBtn = new JButton(icons[3]);
        addOnsBtn = new JButton("plugin");
        getGpsDataBtn = new JButton("Get GPS Data");
        trackGpsDataBtn = new JButton("Track GPS on GEarth");

        // text fields
        IPTxt = new JTextField("PLease enter the IP to connect to", 20);
        //IPTxt = new JTextField("192.168.1.102", 20);
        portTxt = new JTextField("please enter the port number", 20);
        //portTxt = new JTextField("7", 20);

        timeTxt = new JTextField("", 10);
        nsewTxt = new JTextField("", 10);
        dateTxt = new JTextField("", 10);
        latTxt = new JTextField("", 10);
        longTxt = new JTextField("", 10);
        speedTxt = new JTextField("", 10);

        // combobox
        protocolsCbx = new JComboBox();

        // labels
        IPLbl = new JLabel("IP Address:", JLabel.LEFT);
        portLbl = new JLabel("Port number:", JLabel.LEFT);
        protocolsLbl = new JLabel("Choose protocol:", JLabel.LEFT);
        timeLbl = new JLabel("time:", JLabel.LEFT);
        nsewLbl = new JLabel("N.S.E.W:", JLabel.LEFT);
        dateLbl = new JLabel("date:", JLabel.LEFT);
        latLbl = new JLabel("latitude:", JLabel.LEFT);
        longLbl = new JLabel("longtitude:", JLabel.LEFT);
        speedLbl = new JLabel("speed:", JLabel.LEFT);


        // panels
        txtHolderPnl = new JPanel();
        directionHolderPnl = new JPanel();
        protocolHolderPnl = new JPanel();
        addOnsHolderPnl = new JPanel();
        gpsPnl = new JPanel();
        dummeyPnl = new JPanel[10];
        for (int i = 0; i < dummeyPnl.length; i++) {
            dummeyPnl[i] = new JPanel();
        }

        // organize the GUI
        // set the buttons and gps text unselected
        // untill a connection has happend
        disableComponents();
        speedTxt.setEditable(false);
        timeTxt.setEditable(false);
        dateTxt.setEditable(false);
        nsewTxt.setEditable(false);
        longTxt.setEditable(false);
        latTxt.setEditable(false);
        //gpsPnl.setEnabled(false);
        //upBtn.setPressedIcon(upBtn.getPressedIcon());
        // set button sizes
        connectBtn.setSize(10, 10);
        upBtn.setSize(10, 10);
        downBtn.setSize(10, 10);
        rightBtn.setSize(10, 10);
        leftBtn.setSize(10, 10);
        upBtn.setActionCommand("up");
        downBtn.setActionCommand("down");
        rightBtn.setActionCommand("right");
        leftBtn.setActionCommand("left");
        addOnsBtn.setActionCommand("addOns");

        // set Combo box items
        protocolsCbx.addItem("UDP");
        protocolsCbx.addItem("TCP");
        //protocolsCbx.setSelectedItem("UDP");
        // first the txt holder panel
        txtHolderPnl.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = 0;
        g.fill = GridBagConstraints.HORIZONTAL;
        txtHolderPnl.add(IPLbl, g);
        g.gridy = 1;
        txtHolderPnl.add(IPTxt, g);
        g.gridy = 2;
        txtHolderPnl.add(portLbl, g);
        g.gridy = 3;
        txtHolderPnl.add(portTxt, g);
        g.fill = GridBagConstraints.NONE;
        g.anchor = GridBagConstraints.WEST;
        g.gridy = 4;
        txtHolderPnl.add(connectBtn, g);
        //g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.LINE_END;
        g.gridx = 0;
        g.gridy = 4;
        txtHolderPnl.add(DisconnectBtn, g);


        // second set the protocol holder panel
        protocolHolderPnl.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        protocolHolderPnl.add(protocolsLbl, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        protocolHolderPnl.add(protocolsCbx, gbc);

        // third set the direction buttons panel
        directionHolderPnl.setLayout(new GridLayout(2, 3, 2, 2));
        directionHolderPnl.add(dummeyPnl[0]);
        directionHolderPnl.add(upBtn);
        directionHolderPnl.add(dummeyPnl[1]);
        directionHolderPnl.add(leftBtn);
        directionHolderPnl.add(downBtn);
        directionHolderPnl.add(rightBtn);

        //fourth set the addOns and gpsPanel panel
        gpsPnl.setLayout(new GridBagLayout());
        GridBagConstraints e = new GridBagConstraints();
        e.anchor = GridBagConstraints.LINE_END;
        e.insets = new Insets(5, 5, 5, 5);
        e.gridx = 0;
        e.gridy = 0;
        gpsPnl.add(nsewLbl, e);
        e.gridx = 1;
        e.gridy = 0;
        gpsPnl.add(nsewTxt, e);
        e.anchor = GridBagConstraints.LINE_END;
        e.gridx = 2;
        e.gridy = 0;
        gpsPnl.add(timeLbl, e);
        e.gridx = 3;
        e.gridy = 0;
        gpsPnl.add(timeTxt, e);
        e.anchor = GridBagConstraints.LINE_END;
        e.gridx = 0;
        e.gridy = 1;
        gpsPnl.add(longLbl, e);
        e.gridx = 1;
        e.gridy = 1;
        gpsPnl.add(longTxt, e);
        e.anchor = GridBagConstraints.LINE_END;
        e.gridx = 2;
        e.gridy = 1;
        gpsPnl.add(latLbl, e);
        e.gridx = 3;
        e.gridy = 1;
        gpsPnl.add(latTxt, e);
        e.anchor = GridBagConstraints.LINE_END;
        e.gridx = 0;
        e.gridy = 2;
        gpsPnl.add(dateLbl, e);
        e.gridx = 1;
        e.gridy = 2;
        gpsPnl.add(dateTxt, e);
        e.anchor = GridBagConstraints.LINE_END;
        e.gridx = 2;
        e.gridy = 2;
        gpsPnl.add(speedLbl, e);
        e.gridx = 3;
        e.gridy = 2;
        gpsPnl.add(speedTxt, e);
        e.gridx = 1;
        e.gridy = 3;
        gpsPnl.add(getGpsDataBtn, e);
        e.gridx = 3;
        e.gridy = 3;
        gpsPnl.add(trackGpsDataBtn, e);

        addOnsHolderPnl.setLayout(new BorderLayout());
        addOnsHolderPnl.add(gpsPnl, BorderLayout.CENTER);
        addOnsHolderPnl.add(addOnsBtn, BorderLayout.SOUTH);
        // place the panels in the frames
        Container c = getContentPane();
        c.setLayout(new GridLayout(2, 2, 1, 1));
        c.add(txtHolderPnl);
        c.add(protocolHolderPnl);
        c.add(addOnsHolderPnl);
        c.add(directionHolderPnl);
        setSize(500, 500);
        pack();
        setResizable(false);
        //show();
        setVisible(true);



    }

    public void run() {
        try {
            protocol.recieveData();
        } catch (NullPointerException nue) {
            JOptionPane.showMessageDialog(rootPane, "cannot receive data"
                    + " no connection");
        } catch (IOException ex) {
            //Logger.getLogger(UDPConnection.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(new JRootPane(), "Connection receiving error " + ex.getMessage());
            System.exit(0);
        }
    }

    public static void main(String[] args) {

        WifiRobotGUI wrg = new WifiRobotGUI();

        wrg.addListeners();

    }

    public void delay() {

        long init = System.currentTimeMillis() + commandDelay;
        while (System.currentTimeMillis() < init) {
        }
    }

    public void setData(byte d) {
        data = (char) (data | d);
    }

    public void removeData(byte d) {
        data = (char) (data & ~d);
    }

    // enable the components after connection
    public void enableComponents() {
        upBtn.setEnabled(true);
        downBtn.setEnabled(true);
        rightBtn.setEnabled(true);
        leftBtn.setEnabled(true);
        addOnsBtn.setEnabled(true);
        //gpsPnl.setEnabled(true);
        getGpsDataBtn.setEnabled(true);
        //trackGpsDataBtn.setEnabled(true);
        timeLbl.setEnabled(true);
        timeTxt.setEnabled(true);
        dateLbl.setEnabled(true);
        dateTxt.setEnabled(true);
        longLbl.setEnabled(true);
        longTxt.setEnabled(true);
        latLbl.setEnabled(true);
        latTxt.setEnabled(true);
        nsewLbl.setEnabled(true);
        nsewTxt.setEnabled(true);
        speedLbl.setEnabled(true);
        speedTxt.setEnabled(true);
    }
    // disable the components after disconnection

    public void disableComponents() {
        upBtn.setEnabled(false);
        downBtn.setEnabled(false);
        rightBtn.setEnabled(false);
        leftBtn.setEnabled(false);
        addOnsBtn.setEnabled(false);
        getGpsDataBtn.setEnabled(false);
        trackGpsDataBtn.setEnabled(false);
        timeLbl.setEnabled(false);
        timeTxt.setEnabled(false);
        dateLbl.setEnabled(false);
        dateTxt.setEnabled(false);
        longLbl.setEnabled(false);
        longTxt.setEnabled(false);
        latLbl.setEnabled(false);
        latTxt.setEnabled(false);
        nsewLbl.setEnabled(false);
        nsewTxt.setEnabled(false);
        speedLbl.setEnabled(false);
        speedTxt.setEnabled(false);
    }
    // add event listeners to various GUI components

    public void addListeners() {

        // first add the text listener
        IPLbl.setFocusable(true);
        protocolsCbx.setSelectedIndex(0);
        IPTxt.addFocusListener(new TxtHandler());
        portTxt.addFocusListener(new TxtHandler());

        // second add buttons listeners
        connectBtn.addActionListener(new ConnectBtnHandler());
        DisconnectBtn.addActionListener(new ConnectBtnHandler());
        upBtn.addKeyListener(new DirectionBtnHandler());
        downBtn.addKeyListener(new DirectionBtnHandler());
        rightBtn.addKeyListener(new DirectionBtnHandler());
        leftBtn.addKeyListener(new DirectionBtnHandler());
        upBtn.addActionListener(new DirectionBtnHandler());
        downBtn.addActionListener(new DirectionBtnHandler());
        rightBtn.addActionListener(new DirectionBtnHandler());
        leftBtn.addActionListener(new DirectionBtnHandler());

        addOnsBtn.addActionListener(new addOnsBtnHandler());
        getGpsDataBtn.addActionListener(new GpsBtnHandler());
        trackGpsDataBtn.addActionListener(new GpsBtnHandler());
    }

    // define the inner classes that handles the events
    class DirectionBtnHandler implements KeyListener, ActionListener {

        public void keyPressed(KeyEvent e) {

            delay();
            if (e.getKeyCode() == KeyEvent.VK_UP) {

                upBtn.setIcon(icons[4]);
                setData((byte) 0X01);
                //protocol.sendData(forward);
                //System.out.println("hello");

            } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {

                downBtn.setIcon(icons[5]);
                setData((byte) 0X02);
                //protocol.sendData(reverse);
                //System.out.println("hello2");

            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {

                rightBtn.setIcon(icons[6]);
                setData((byte) 0X04);
                //protocol.sendData(right);
                //System.out.println("hello3");

            } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {

                leftBtn.setIcon(icons[7]);
                setData((byte) 0X08);
                //protocol.sendData(left);
                //System.out.println("hello4");
            }

            protocol.sendData(format, data);

        }

        public void keyTyped(KeyEvent e) {
            // throw new UnsupportedOperationException("Not supported yet.");
        }

        public void keyReleased(KeyEvent e) {
            delay();
            if (e.getKeyCode() == KeyEvent.VK_UP) {

                //upBtn.setIcon(icons[4]);
                removeData((byte) 0X01);
                upBtn.setIcon(icons[0]);
                //protocol.sendData(forward);
                //System.out.println("hello");

            } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {

                //downBtn.setIcon(icons[5]);
                removeData((byte) 0X02);
                downBtn.setIcon(icons[1]);
                //protocol.sendData(reverse);
                //System.out.println("hello2");

            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {

                //rightBtn.setIcon(icons[6]);
                removeData((byte) 0X04);
                rightBtn.setIcon(icons[2]);
                //protocol.sendData(right);
                //System.out.println("hello3");

            } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {

                //leftBtn.setIcon(icons[7]);
                removeData((byte) 0X08);
                leftBtn.setIcon(icons[3]);
                //protocol.sendData(left);
                //System.out.println("hello4");
            }
            protocol.sendData(format, data);
            //throw new UnsupportedOperationException("Not supported yet.");
        }

        public void actionPerformed(ActionEvent e) {

            delay();
            if (e.getActionCommand().equals("up")) {
                setData((byte) 0X01);
                //protocol.sendData(forward);
                //.out.println("hello");
            } else if (e.getActionCommand().equals("down")) {
                setData((byte) 0X02);
                //protocol.sendData(reverse);
                //System.out.println("hello2");
            } else if (e.getActionCommand().equals("right")) {
                setData((byte) 0X04);
                //protocol.sendData(right);
                //System.out.println("hello3");
            } else if (e.getActionCommand().equals("left")) {
                setData((byte) 0X08);
                //protocol.sendData(left);
                //System.out.println("hello4");
            }
            protocol.sendData(format, data);
        }
    }

    class TxtHandler implements FocusListener {

        public void focusGained(FocusEvent e) {

            if (e.getSource().equals(IPTxt)) {

                IPTxt.setText(""); // original code changed
                IPTxt.removeFocusListener(this);

            } else if (e.getSource().equals(portTxt)) {

                portTxt.setText(""); // original code changed
                portTxt.removeFocusListener(this);
            }

        }

        public void focusLost(FocusEvent e) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    class addOnsBtnHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("addOns")) {
                PluginsFrame pf = new PluginsFrame();
                PluginsFrame.pf = pf;
            }
        }
    }

    class GpsBtnHandler implements ActionListener {

        Runnable r = null;
        Runnable r2 = null;
        Thread t = null;
        Thread t2 = null;
        String[] dataOfGps = null;
        boolean state = true;
        boolean state2 = true;

        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Get GPS Data")) {
                trackGpsDataBtn.setEnabled(true);
                state = true;
                getGpsDataBtn.setActionCommand("Stop GPSData");
                getGpsDataBtn.setText("Stop GPSData");
                r = new Runnable() {

                    public void run() {
                        //throw new UnsupportedOperationException("Not supported yet.");
                        while (state) {
                            protocol.sendData('G', (char) 0);

                            long init1 = System.currentTimeMillis() + 1000;
                            //String[] dataOfGps = null;
                            while (true) {
                                if (protocol.gpsData().length >= 7
                                        || System.currentTimeMillis() > init1) {
                                    dataOfGps = protocol.gpsData();
                                    break;
                                }
                            }
                            timeTxt.setText(dataOfGps[0]);
                            dateTxt.setText(dataOfGps[1]);
                            latTxt.setText(dataOfGps[2]);
                            nsewTxt.setText(dataOfGps[3] + dataOfGps[5]);
                            longTxt.setText(dataOfGps[4]);
                            speedTxt.setText(dataOfGps[6]);

                            long init = System.currentTimeMillis() + gpsDelay;
                            while (System.currentTimeMillis() < init) {
                            }
                        }
                    }
                };
                t = new Thread(r);
                t.start();

            } else if (e.getActionCommand().equals("Stop GPSData")) {
                state = false;
                getGpsDataBtn.setActionCommand("Get GPS Data");
                getGpsDataBtn.setText("Get GPS Data");


            } else if (e.getActionCommand().equals("Track GPS on GEarth")) {
                // protocol.sendData('S', (char) 0);
                trackGpsDataBtn.setActionCommand("Stop GEarth Track");
                trackGpsDataBtn.setText("Stop GEarth Track");
                state2 = true;

                r2 = new Runnable() {

                    public void run() {
//                        long init1 = System.currentTimeMillis() + 1000;
//                            //String[] dataOfGps = null;
//                            while (true) {
//                                if (protocol.gpsData().length >= 7
//                                        || System.currentTimeMillis() > init1) {
//                                    dataOfGps = protocol.gpsData();
//                                    break;
//                                }
//                            }
                        double templong = Double.parseDouble(protocol.gpsData()[4]);
                        double templat = Double.parseDouble(protocol.gpsData()[2]);
                        KmlProtocol kml = new KmlProtocol();
                        kml.createKmlFile(templong, templat, "D:\\farouk");
                        //kml.excuteGoogleEarth();
                       
                        //throw new UnsupportedOperationException("Not supported yet.");
                        while (state2) {
                            templong = Double.parseDouble(protocol.gpsData()[4]);
                            templat = Double.parseDouble(protocol.gpsData()[2]);
                            kml.createKmlFile(templong, templat, "D:\\farouk");
                            long init = System.currentTimeMillis() + gpsDelay;
                            while (System.currentTimeMillis() < init) {
                            }
                        }
                    }
                };
                t2 = new Thread(r2);
                t2.start();
                //double templong = Double.parseDouble(protocol.gpsData()[4]);
                //double templat = Double.parseDouble(protocol.gpsData()[2]);
                //kml.createKmlFile(templong, templat, "D:\\farouk.kml");
                //kml.excuteGoogleEarth();
//                int temp = (int) (templong / 100);
//                double temp1 = temp + (((templong / 100) - temp) * 100) / 60;
//                temp = (int) (templat / 100);
//                double temp2 = temp + (((templat / 100) - temp) * 100) / 60;
//                double longtitude = temp1;
//                double latitude = temp2;


            } else if (e.getActionCommand().equals("Stop GEarth Track")) {

                trackGpsDataBtn.setActionCommand("Track GPS on GEarth");
                trackGpsDataBtn.setText("Track GPS on GEarth");
                state2 = false;
            }
        }
    }

    class ConnectBtnHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Connect")) {
                if (protocolsCbx.getSelectedItem().equals("TCP")) {
                    try {

                        // create TCPCOnnection object and send a socket object
                        //with the specified  hostaddress and port
                        protocol = new TCPConnection(
                                IPTxt.getText(), Integer.parseInt(
                                portTxt.getText()));
                        enableComponents();
                    } catch (NumberFormatException nfe) {

                        JOptionPane.showMessageDialog(rootPane, "please enter"
                                + " the port number in integer format");

                    } catch (IOException ioe) {

                        // System.err.println("problem in getting" +
                        //         " writing connection -" + ioe);
                        JOptionPane.showMessageDialog(new JRootPane(),
                                "Wrong or Unknown IP " + ioe.getMessage());

                    }

                } else if (protocolsCbx.getSelectedItem().equals("UDP")) {

                    String addr = IPTxt.getText();
                    int portnum = 0;
                    try {
                        portnum = Integer.parseInt(portTxt.getText());


                        protocol = new UDPConnection(new byte[256],
                                new byte[256].length, addr, portnum);
                        enableComponents();
                    } catch (NumberFormatException nfe) {

                        JOptionPane.showMessageDialog(rootPane, "please enter"
                                + " the port number in integer format");

                    } catch (UnknownHostException uhe) {

                        //System.err.println("host not resolved -" + uhe.getMessage());
                        JOptionPane.showMessageDialog(new JRootPane(),
                                "Wrong or unknown IP " + uhe.getMessage());

                    } catch (SocketException soe) {

                        //System.err.println("socket exception - " + soe);
                        JOptionPane.showMessageDialog(new JRootPane(), soe.getMessage());
                    }
                }
                Thread t = new Thread(WifiRobotGUI.this);
                t.start();
            } else if (e.getActionCommand().equals("DisConnect")) {
                disableComponents();
                protocol.closeConnection();

            }

        }
    }
}
