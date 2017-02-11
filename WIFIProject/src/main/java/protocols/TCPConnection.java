/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package protocols;

import java.io.*;
import java.net.*;

/**
 *
 * @author farouk
 *  TCPConnection implements ConnectionPRoctocol interface
 *  it provides the basic functionailty to send and receve
 *  data through using TCP protocol
 */
public class TCPConnection implements ConnectionProtocol {

    // private parameters
    private Socket TCPSocket; // client connection
    private ServerSocket TCPServerSocket; // server connection
    private PrintWriter pw;
    String line1 = "";
    // end of parameters

    public TCPConnection(String hostname, int port) throws IOException {
        //try {

            // Get a Socket for TCPConnection
            TCPSocket = new Socket(hostname, port);
            TCPServerSocket = new ServerSocket(port);
            // create printwriter object and assign the socket output
            // stream to it using outputstreamwriter class
            pw = new PrintWriter(new OutputStreamWriter(TCPSocket.getOutputStream()));

        //}
    }

    // send data to the server the data more likely will be bytes
    // it returns an integer for
    public boolean sendData(char format, char data) {

        // send the data to the server
        pw.print(format);
        pw.print(data);
        pw.flush();
        //pw.close();
        // System.out.println("data send");
        return true;

    }

    public boolean closeConnection() {
        try {
            pw.close();
            TCPSocket.close();
            TCPServerSocket.close();
            return true;
        } catch (IOException ex) {
            //Logger.getLogger(TCPConnection.class.getName()).log(Level.SEVERE,
            //null, ex);
            return false;
        } catch (NullPointerException npe) {
            return false;
        }
        //return true;
    }

    public void recieveData() throws IOException {
        //throw new UnsupportedOperationException("Not supported yet.");
        String line = "";
        for (;;) {
            try {
            // get the next tcp client
            Socket client = TCPServerSocket.accept();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(client.getInputStream()));

            for (;;) {
                line = reader.readLine();
                // Check for end of data
                if (line == null) {
                    break;
                } else {
                    System.out.println(line);
                    line1 = line;
                }
            }
            reader.close();
            client.close();
            client = null;
            } catch(IOException ioe) {
                ioe.printStackTrace();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

    }

    public String[] gpsData() {

        return line1.split(",");
    }

    public boolean setParameters() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
