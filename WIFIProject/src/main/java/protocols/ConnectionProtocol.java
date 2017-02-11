/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package protocols;

import java.io.IOException;

/**
 *
 * @author farouk
 *  a ConnctionProctocol interface defines
 *  the methods to be used to connect to the
 *  server and send & recieve data from it
 */


public interface ConnectionProtocol {


    boolean sendData(char M, char data);
    boolean closeConnection();
    void recieveData() throws IOException;
    String[] gpsData();
    boolean setParameters();
    
}
