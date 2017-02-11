/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package protocols;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.*;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author farouk
 */
public class KmlProtocol {

    File kml,data;
    PrintWriter pw;
    ArrayList<String> longData = new ArrayList<String>();
    ArrayList<String> latData = new ArrayList<String>();
    boolean state = true;

    public KmlProtocol() {
    }

    public void createKmlFile(double longtitude, double latitude,
            String fileLocation) {

        //double templong = Double.parseDouble(longtitude);
        //double templat = Double.parseDouble(latitude);
        int temp = (int) (longtitude / 100);
        double temp1 = temp + (((longtitude / 100) - temp) * 100) / 60;
        temp = (int) (latitude / 100);
        double temp2 = temp + (((latitude / 100) - temp) * 100) / 60;
        longtitude = temp1;
        latitude = temp2;
        longData.add(Double.toString(longtitude));
        latData.add(Double.toString(latitude));
        

        try {
            kml = new File(fileLocation+".kml");
            data = new File(fileLocation+"-data.kml");
            pw = new PrintWriter(data);
            if (data.exists()) {
                data.delete();
                //updateKmlFile(longtitude, latitude);
            }
            pw.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            pw.println("<kml xmlns=\"http://earth.google.com/kml/2.0\">");
            pw.println("<Document>");
            pw.println("<LookAt>");
            pw.println("<longitude>"+longData.get(longData.size() - 1)+"</longitude>");
            pw.println("<latitude>"+latData.get(latData.size() - 1)+"</latitude>");
            pw.println("<range>1000</range>");
            pw.println("<tilt>45</tilt>");
            pw.println("<heading>333.03</heading>");
            pw.println("</LookAt>");
            pw.println("<Placemark>");
            pw.println("<name>Wifi Robot</name>");
            pw.println("<Style>");
            pw.println("<IconStyle>");
            pw.println("<Icon>");
            pw.println("<href>root://icons/palette-4.png</href>");
            pw.println("<x>224</x>");
            pw.println("<y>224</y>");
            pw.println("<w>32</w>");
            pw.println("<h>32</h>");
            pw.println("</Icon>");
            pw.println("</IconStyle>");
            pw.println("</Style>");
            pw.println("<Point>");
            pw.println("<altitudeMode>absolute</altitudeMode>");
            pw.println("<coordinates>"+longData.get(longData.size() - 1)+
                    ","+latData.get(latData.size() - 1)+"</coordinates>");
            pw.println("</Point>");
            pw.println("</Placemark>");
            pw.println("<Placemark>");
            pw.println("<name>Track</name>");
            pw.println("<Style>");
            pw.println("<LineStyle> <color>FF00FF00</color> <width>6</width> </LineStyle>");
            pw.println("</Style>");
            pw.println("<MultiGeometry> <LineString> <tessellate>0</tessellate>");
            pw.println("<altitudeMode>absolute</altitudeMode>"
                    + " <coordinates>");
            for(int i=0;i < longData.size();i++){
                pw.println(longData.get(i)+","+latData.get(i)+",0");
            }
            pw.println("</coordinates>");
            pw.println("</LineString>");
            pw.println(" </MultiGeometry>");
            pw.println("</Placemark>");
            pw.println("</Document></kml>");
            pw.flush();
            pw.close();
            
            pw = new PrintWriter(kml);
            if (kml.exists()) {
                kml.delete();
                //updateKmlFile(longtitude, latitude);
            }
            kml.createNewFile();
            pw.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            pw.println("<kml xmlns=\"http://earth.google.com/kml/2.0\">");
            pw.println("<NetworkLink>");
            pw.println("<name>Wifi Robot</name>");
            pw.println("<flyToView>0</flyToView>");
            pw.println("<Url>");
            pw.println("<href>"+data.toString()+"</href>");
            pw.println("<refreshMode>onInterval</refreshMode>");
            pw.println("<refreshInterval>3</refreshInterval>");
            pw.println("</Url>"+
                    "</NetworkLink>"+
                    "</kml>");
            pw.flush();
            pw.close();

            if(state){
            Runtime.getRuntime().exec("C:\\Program Files\\Google\\"
                    + "Google Earth\\client\\googleearth.exe \""+kml.toString()+"\"");
            state = false;
            }

        } catch (IOException ex) {
            // Logger.getLogger(KmlFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }



    public void excuteGoogleEarth() {
        try {
            Runtime.getRuntime().exec("C:\\Program Files\\Google\\"
                    + "Google Earth\\client\\googleearth.exe \"d:\\farouk.kml\"");
        } catch (IOException ex) {
            Logger.getLogger(KmlProtocol.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
