/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package protocols;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.*;

/**
 *
 * @author farouk
 *  TCPConnection implements ConnectionPRoctocol interface
 *  it provides the basic functionailty to send and receve
 *  data through using TCP protocol
 */
public class UDPConnection implements ConnectionProtocol {

    // private variables
    private DatagramSocket UDPSocket;
    private DatagramPacket UDPPacket;
    private DatagramPacket UDPPacketReceived;
    private byte[] buffer = new byte[256];
    private ByteArrayOutputStream bout;
    private PrintStream pout;
    String line1 = "";
    // end of parameters

    public UDPConnection(byte[] buf, int length, String hostAddrress,
                         int port) throws UnknownHostException,SocketException {
        //try {
            // get a Datagram socket and packet for using UDP connections
            UDPSocket = new DatagramSocket(port);  
            UDPPacket = new DatagramPacket(buf, length);
            UDPPacket.setAddress(InetAddress.getByName(hostAddrress));
            UDPPacket.setPort(port);
       // }

        System.out.println("objects created");
    }

    public boolean closeConnection() {

        try{
        UDPSocket.close();
            return true;
        } catch (NullPointerException npe) {
            return false;
        }

    }
    // send data to the server the data more likely will be bytes
    // it returns an integer for

    public boolean sendData(char format, char data) {
        bout = new ByteArrayOutputStream();
        pout = new PrintStream(bout);
        pout.print(format);
        pout.print(data);
        pout.flush();
        buffer = bout.toByteArray();
        UDPPacket.setData(buffer, 0, buffer.length);

        //pout.close();
        //UDPSocket.connect(, data);
        if (data == '1'){
            System.out.println("up");
        }else if (data == '2') {
            System.out.println("reverse");
        }else if (data == '4'){
            System.out.println("right");
        }else if (data == '8') {
            System.out.println("left");
        }
//        switch(data){
//            case 1:{
//                System.out.println("up");
//                break;
//            }
//            case 2:{
//                System.out.println("reverse");
//                break;
//            }
//            case 4:{
//                System.out.println("right");
//                break;
//            }
//            case 8:{
//                System.out.println("left");
//                break;
//            }
//        }

        try {
            UDPSocket.send(UDPPacket);
            //UDPPacket.setData(new byte[10]);
            pout.close();
            bout.close();
            pout = null;
            bout = null;
            System.out.println("ok");
            return true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    public void recieveData()  throws IOException {
        UDPPacketReceived = new DatagramPacket(buffer, buffer.length);

        String line = "";
        for (;;) {

                if(UDPSocket.isClosed())break;
                UDPSocket.receive(UDPPacketReceived);
                ByteArrayInputStream bin = new ByteArrayInputStream(
                        UDPPacketReceived.getData(), 0,
                        UDPPacketReceived.getLength());
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(bin));
                for (;;) {
                    line = reader.readLine();
                    // Check for end of data
                    if (line == null) {
                        break;
                        //continue;
                    } else {
                        System.out.println(line);
                        line1 = line;
                        //break;
                        //line.split(",");
                    }
                }
                //UDPSocket.send(UDPPacketReceived);
        }

    }

    public String[] gpsData() {

        String[] test = line1.split(",");
        if (test.length == 7) {
            test[2] = test[2].substring(0,10);
            test[4] = test[4].substring(0, 11);
        }
        //System.out.println(test.length);
        return test;
    }

    public boolean setParameters() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
