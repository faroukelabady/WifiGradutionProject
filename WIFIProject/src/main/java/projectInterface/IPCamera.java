/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package projectInterface;

import java.net.*;
import com.sun.image.codec.jpeg.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import javax.swing.*;
/**
 *
 * @author farouk
 */
public class IPCamera extends JFrame implements Runnable {

    private Image image;
    private JPEGImageDecoder decoder;
    private DataInputStream dis;
    private BufferedInputStream in;
    private InputStream is;
    private Draw video;
    private boolean streamOn = true;
    static  boolean recordOn = false;
    private JPanel pnl = new JPanel();
    private JButton playPauseBtn,recordBtn;
    private URL url,url2;
    private ImageIcon play, pause, record, stop;

    
    IPCamera() {

        super("Video");
        String cr = System.getProperty("user.dir");
        play = new ImageIcon(cr + "\\Play-Normal-icon.png");
        pause = new ImageIcon(cr + "\\Pause-Normal-Red-icon.png");
        stop = new ImageIcon(cr + "\\Stop-Normal-Blue-icon.png");
        record = new ImageIcon(cr + "\\Record-Normal-icon.png");
        Authenticator.setDefault(new  MyAuthenticator());

        try {
            // first start a connection to sent the username
            // and password
            url = new URL ("http://192.168.1.100/");
            url.getContent();
            // second open a connection to the video
            Authenticator.setDefault(null);
            url2 = new URL("http://192.168.1.100/video.cgi");
            // catch the data stream that comes from the video
            // and wrap it into datastream
            is = url2.openStream();
            in = new BufferedInputStream(is);
            dis = new DataInputStream(in);  
        } catch (MalformedURLException mue) {
           // System.err.println("Error-no legal protocol could be " +
           //         "found in " + mue);
            JOptionPane.showMessageDialog(rootPane,"No legal protocol could " +
                    "be found");
            dispose();
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(rootPane,"Error- cannot read from"+
                    " URL");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane,"general error has accured"+e);
            dispose();
        }

        // Intialize the frame
        video = new Draw();
        playPauseBtn = new JButton(pause);
        playPauseBtn.setActionCommand("pause");
        recordBtn =new JButton(record);
        Container c = getContentPane();
        c.setLayout(new BorderLayout());
        pnl.setLayout(new GridLayout());
        pnl.add(playPauseBtn, BorderLayout.WEST);
        pnl.add(recordBtn, BorderLayout.EAST);
        c.add(video,BorderLayout.CENTER);
        c.add(pnl,BorderLayout.SOUTH);

        playPauseBtn.addActionListener(new playPauseListener());
        recordBtn.addActionListener(new RecordListener());
    
        //setResizable(false);
        setSize(320,300);
        show();


    }

   void display() {

        while(streamOn) {

            try {
                //parent.repaint();
                readLine(3, dis); //discard the first 4 lines for D-Link DCS-900
                decoder = JPEGCodec.createJPEGDecoder(dis);
                image = decoder.decodeAsBufferedImage();
                readLine(3, dis);
                repaint();
               
               
                 //ip.repaint();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(rootPane, "Cannot read the" +
                        "the JPEG data please make sure the image is formated" +
                        " properly " + ex.getMessage());
                streamOn = false;
                PluginsFrame.removeTrace();
                dispose();
                break;
            } catch (ImageFormatException ex) {
                JOptionPane.showMessageDialog(rootPane, "Cannot read the" +
                        "the JPEG image please make sure the image is formated" +
                        " properly " + ex.getLocalizedMessage());
                streamOn = false;
                PluginsFrame.removeTrace();
                dispose();
                break;
            } catch(IllegalArgumentException iae){
                 JOptionPane.showMessageDialog(rootPane, "No data to read"+ iae.getMessage());
                 streamOn = false;
                PluginsFrame.removeTrace();
                 dispose();
                 break;
            }

        }
    }

   



    public void run() {
        
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                PluginsFrame.removeTrace();
                dispose();
            }

        });
        
        while (true){
//           try{
               display();
//           } catch(Exception e){
//               JOptionPane.showMessageDialog(rootPane, "Camera unplugged");
//               streamOn = false;
//                PluginsFrame.removeTrace();
//               dispose();
//           }

        }
        
    }


    public void readLine(int n, DataInputStream dis) {
    //used to strip out the header lines
        for (int i=0; i<n;i++) {
            readLine(dis);
        }
    }
    public void readLine(DataInputStream dis) {
        try {
            boolean end = false;
            String lineEnd = "\n"; //assumes that the end of the line is marked with this
            byte[] lineEndBytes = lineEnd.getBytes();
           //System.out.println("lineEndBytes....."+lineEndBytes);
            byte[] byteBuf = new byte[lineEndBytes.length];
            //System.out.println("byteBuf......."+byteBuf);
            while(!end) {
            //dis.read(byteBuf,0,lineEndBytes.length);
                String t = "";
                if(byteBuf != null) {
                dis.read(byteBuf,0,lineEndBytes.length);
                t = new String(byteBuf);
                }
              ////  System.out.print(t); //uncomment if you want to see what the lines actually look like
                if(t.equals(lineEnd))
                end=true;
            }
        }
        catch(NullPointerException npe)
        {
            JOptionPane.showMessageDialog(rootPane, "please confirm  the"+
                    " Camera is connected and powered on");
            streamOn = false;
            PluginsFrame.removeTrace();
            dispose();
        }
        catch(IOException ioe) {
            JOptionPane.showMessageDialog(rootPane, "Cannot read data "+
                    "from the Camera");
            streamOn = false;
            PluginsFrame.removeTrace();
            dispose();
        }
    }
    class Draw extends JPanel {

       Draw () {

        }
        
        @Override
        public void paint(Graphics g) {
            //super.paint(g);
            if (image != null)
            g.drawImage(image, 0, 0, this);
        }
    }

    class playPauseListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
           if(e.getActionCommand().equals("pause")) {
               playPauseBtn.setActionCommand("play");
               playPauseBtn.setIcon(play);
               try {
                    BufferedImage bi = (BufferedImage) image; // retrieve image
                    File outputfile = new File("d:\\saved.mov");
                    ImageIO.write(bi, "jpg", outputfile);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
               streamOn = false;
           } else {
               playPauseBtn.setActionCommand("pause");
               playPauseBtn.setIcon(pause);
               // release system resources and clear chached data
               // in memory so image object return to it's first state
               image.flush(); 
               streamOn = true;
           }
        }
    }
    
    class RecordListener implements ActionListener {
        
        public void actionPerformed(ActionEvent e) {
            
            if(e.getActionCommand().equals("record")) {
                recordBtn.setActionCommand("stop");
                recordBtn.setIcon(stop);
                recordOn = true;
                Thread t = new Thread(new Recorder());
                t.start();
               // new VideoRecord(image);      
            } else {
                recordBtn.setActionCommand("record");
                recordBtn.setIcon(record);
                recordOn = false;
            }
        }
    }

 
    class MyAuthenticator extends Authenticator {

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication("admin","".toCharArray());
        }
    }

}



    



