package projectInterface;
/*
 * @(#)JpegImagesToMovie.java	1.3 01/03/13
 *
 * Copyright (c) 1999-2001 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */
 
/*
 * Some public domain modifications by Andy Dyble for avi etc by self and from  
 * other internet resources 19/11/2008. Reads editable 20 ip cam network jpg files. 
 * Supported formats tested for compression options. This code could be much improved 
 * further to these amendments. Absolutely no warranties. www.exactfutures.com
 */
 

import com.sun.media.controls.MonitorAdapter;
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.Dimension;
import javax.media.*;
import javax.media.control.*;
import javax.media.protocol.*;
import javax.media.protocol.DataSource;
import javax.media.datasink.*;
import javax.media.format.VideoFormat;
import javax.swing.*;
import javax.swing.JFrame;
 
/**
 * This program takes a list of JPEG image files and convert them into
 * a QuickTime movie.
 */
public class Recorder implements ControllerListener, DataSinkListener , Runnable{
 
    public boolean doIt(int width, int height, int frameRate, Vector inFiles, MediaLocator outML, String outputURL)
	{
	ImageDataSource ids = new ImageDataSource(width, height, frameRate, inFiles);
 
	Processor p;
        MonitorControl m;
	try {
	    System.err.println("- create processor for the image datasource ...");
	    p = Manager.createProcessor(Manager.createCloneableDataSource(ids));
            m = new MonitorAdapter(null, p);
            m.setEnabled(true);
	} catch (Exception e) {
	    JOptionPane.showMessageDialog(new JRootPane(),"Cannot record a processor from the data source.");
	    return false;
	}
 
	p.addControllerListener(this);
 
	// Put the Processor into configured state so we can set
	// some processing options on the processor.
	p.configure();
	if (!waitForState(p, p.Configured)) {
	    System.err.println("Failed to configure the processor.");
	    return false;
	}
 
///
	// Set the output content descriptor to QuickTime. 
	if(outputURL.endsWith(".avi") || outputURL.endsWith(".AVI"))
	{
		p.setContentDescriptor(new ContentDescriptor(FileTypeDescriptor.MSVIDEO));
	}
	if(outputURL.endsWith(".mov") || outputURL.endsWith(".MOV"))
	{
		p.setContentDescriptor(new ContentDescriptor(FileTypeDescriptor.QUICKTIME));
	}
 
	// Query for the processor for supported formats.
	// Then set it on the processor.
	TrackControl tcs[] = p.getTrackControls();
	
///
//	for(int i=0;i<tcs.length;i++)
//	{
//		System.out.println("TrackControl "+i+" "+tcs[i]);
//	}
	
	Format f[] = tcs[0].getSupportedFormats();
	if (f == null || f.length <= 0) {
	    System.err.println("The mux does not support the input format: " + tcs[0].getFormat());
	    return false;
	}
 
///
//	for(int i=0;i<f.length;i++)
//	{
//		System.out.println("Supported Format "+i+" "+f[i]);
//	}
//
//	tcs[0].setFormat(f[0]);
///
	if(outputURL.endsWith(".avi") || outputURL.endsWith(".AVI"))	// must be VideoFormat
	{
		System.err.println("Setting the track format to: "	// INDEO50 CINEPAK f[0] etc
			+ tcs[0].setFormat(new VideoFormat(VideoFormat.INDEO50)));
	}
	if(outputURL.endsWith(".mov") || outputURL.endsWith(".MOV"))
	{
		System.err.println("Setting the track format to: "	// JPEG CINEPAK RGB f[0] etc
			+ tcs[0].setFormat(new VideoFormat(VideoFormat.JPEG)));
	}
 
	//System.err.println("Setting the track format to: " + f[0]);
 
	// We are done with programming the processor.  Let's just
	p.realize();
	if (!waitForState(p, Controller.Realized)) {
	    System.err.println("Failed to realize the processor.");
	    return false;
	}

	// Now, we'll need to create a DataSink.
	DataSink dsink;
	if ((dsink = createDataSink(p, outML)) == null) {
	    System.err.println("Failed to create a DataSink for the given output MediaLocator: " + outML);
	    return false;
	}

	dsink.addDataSinkListener(this);
	fileDone = false;

	System.err.println("start processing...");
        

	// OK, we can now start the actual transcoding.
	try {
	    p.start();
	    dsink.start();
	} catch (IOException e) {
	    //System.err.println("IO error during processing");
            JOptionPane.showMessageDialog(new JRootPane(),"IO error during processing");
	    return false;
	}
 
	// Wait for EndOfStream event.
	waitForFileDone();
 
	// Cleanup.
	try {
	    dsink.close();
	} catch (Exception e) {
            JOptionPane.showMessageDialog(new JRootPane(),"error cannot save the file");
        }
	p.removeControllerListener(this);
 
	System.err.println("...done processing.");
 
	return true;
    }
 
 
    /**
     * Create the DataSink.
     */
    DataSink createDataSink(Processor p, MediaLocator outML) {
 
	DataSource ds;
 
	if ((ds = p.getDataOutput()) == null) {
	    System.err.println("Something is really wrong: the processor does not have an output DataSource");
	    return null;
	}
 
	DataSink dsink;
 
	try {
	    System.err.println("- create DataSink for: " + outML);
	    dsink = Manager.createDataSink(ds, outML);
	    dsink.open();
	} catch (Exception e) {
	    //System.err.println("Cannot create the DataSink: " + e);
            JOptionPane.showMessageDialog(new JRootPane(),"Cannot create the DataSink: " + e);
	    return null;
	}
 
	return dsink;
    }
 
 
    Object waitSync = new Object();
    boolean stateTransitionOK = true;
 
    /**
     * Block until the processor has transitioned to the given state.
     * Return false if the transition failed.
     */
    boolean waitForState(Processor p, int state) {
	synchronized (waitSync) {
	    try {
		while (p.getState() < state && stateTransitionOK)
		    waitSync.wait();
	    } catch (Exception e) {}
	}
	return stateTransitionOK;
    }
 
 
    /**
     * Controller Listener.
     */
    public void controllerUpdate(ControllerEvent evt) {
 
	if (evt instanceof ConfigureCompleteEvent ||
	    evt instanceof RealizeCompleteEvent ||
	    evt instanceof PrefetchCompleteEvent) {
	    synchronized (waitSync) {
		stateTransitionOK = true;
		waitSync.notifyAll();
	    }
	} else if (evt instanceof ResourceUnavailableEvent) {
	    synchronized (waitSync) {
		stateTransitionOK = false;
		waitSync.notifyAll();
	    }
	} else if (evt instanceof EndOfMediaEvent) {
	    evt.getSourceController().stop();
	    evt.getSourceController().close();
	}
    }
 
 
    Object waitFileSync = new Object();
    boolean fileDone = false;
    boolean fileSuccess = true;
 
    /**
     * Block until file writing is done. 
     */
    boolean waitForFileDone() {
        long currentTime = System.currentTimeMillis();
        long maxTime = currentTime + 5000; // 5 second timeout
	synchronized (waitFileSync) {
	    try {
		while (!fileDone){
		    waitFileSync.wait();
                    while(currentTime < maxTime)
                        currentTime = System.currentTimeMillis();
                }
	    } catch (Exception e) {}
	}
	return fileSuccess;
    }
 
 
    /**
     * Event handler for the file writer.
     */
    public void dataSinkUpdate(DataSinkEvent evt) {
 
	if (evt instanceof EndOfStreamEvent) {
	    synchronized (waitFileSync) {
		fileDone = true;
		waitFileSync.notifyAll();
	    }
	} else if (evt instanceof DataSinkErrorEvent) {
	    synchronized (waitFileSync) {
		fileDone = true;
		fileSuccess = false;
		waitFileSync.notifyAll();
	    }
	}
    }
///
    public static void createInputFiles(Vector files) {
		// Create a file for the directory 
		File file = new File("images"); 
 
		// Get the file list... 
		String s[] = file.list(); 
 
		files.removeAllElements();	// if any set from arguments
 
		for(int i=0;i<s.length;i++)
		{
			if(s[i].indexOf(".jp")!=-1)
			{
				files.addElement("images"+File.separator+s[i]);	// and to sort if required
			}
			else
			{
				System.out.println((i+1)+": "+s[i]+" - ignored");
			}
		}
    }

    public void run() {
        Recorder imageToMovie = new Recorder();
	imageToMovie.doIt(320, 240, 30, new Vector(), new MediaLocator(
                "file:\\d:\\test2.mov"), "file:\\d:\\test2.mov");
    }
 
     
   
    ///////////////////////////////////////////////
    //
    // Inner classes.
    ///////////////////////////////////////////////
 
 
    /**
     * A DataSource to read from a list of JPEG image files and
     * turn that into a stream of JMF buffers.
     * The DataSource is not seekable or positionable.
     */
    class ImageDataSource extends PullBufferDataSource {
 
	ImageSourceStream streams[];
 
	ImageDataSource(int width, int height, int frameRate, Vector images) {
	    streams = new ImageSourceStream[1];
	    streams[0] = new ImageSourceStream(width, height, frameRate, images);
	}
 
	public void setLocator(MediaLocator source) {
	}
 
	public MediaLocator getLocator() {
	    return null;
	}
 
	/**
	 * Content type is of RAW since we are sending buffers of video
	 * frames without a container format.
	 */
	public String getContentType() {
	    return ContentDescriptor.RAW;
	}
 
	public void connect() {
	}
 
	public void disconnect() {
	}
 
	public void start() {
	}
 
	public void stop() {
	}
 
	/**
	 * Return the ImageSourceStreams.
	 */
	public PullBufferStream[] getStreams() {
	    return streams;
	}
 
	/**
	 * We could have derived the duration from the number of
	 * frames and frame rate.  But for the purpose of this program,
	 * it's not necessary.
	 */
	public Time getDuration() {
	    return DURATION_UNKNOWN;
	}
 
	public Object[] getControls() {
	    return new Object[0];
	}
 
	public Object getControl(String type) {
	    return null;
	}
    }
 
 
    /**
     * The source stream to go along with ImageDataSource.
     */
    class ImageSourceStream implements PullBufferStream {
 
	Vector images;
	int width, height;
	VideoFormat format;
 	int nextImage = 0;	// index of the next image to be read.
	boolean ended = false;
        ///
	float frameRate;
	long seqNo = 0;
        ///
 
	public ImageSourceStream(int width, int height, int frameRate, Vector images) {
	    this.width = width;
	    this.height = height;
	    this.images = images;
            this.frameRate = (float)frameRate;
 	    format = new VideoFormat(VideoFormat.JPEG,
				new Dimension(width, height),
				Format.NOT_SPECIFIED,
				Format.byteArray,
				(float)frameRate);
	}
 
	/**
	 * We should never need to block assuming data are read from files.
	 */
	public boolean willReadBlock() {
	    return false;
	}
 
        byte[] input_buffer;
        byte[] imgbytearray;
        byte[] imgusebyte;
        int camWidth = 320;
        int camHeight = 240;
        URL url,url2;
        long timeStamp;
        long timeStamp0=System.currentTimeMillis();
        long resultTimeStamp;
 
	public boolean urlFetchJPG()
	{
		String ipcamserver = "192.168.1.100";	// examples 146.176.65.10 , 62.16.100.204 , 194.168.163.96 , 84.93.217.139 , 194.177.131.229 , 148.61.171.201
 
		try
		{
                    
                    Authenticator.setDefault(new  MyAuthenticator());

                    
                    // first start a connection to sent the username
                    // and password
                    url2 = new URL ("http://192.168.1.100/");
                    url2.getContent();
                    // second open a connection to the video
                    Authenticator.setDefault(null);
                    url = new URL("http://"+ipcamserver+"/image.jpg");
		}
		catch(Exception e){
                    JOptionPane.showMessageDialog(new JRootPane(),"please"+
                            " check the URL if it's correct." + e);

                }
 
		try
		{
			if(input_buffer==null)input_buffer = new byte[81920];
			if(imgbytearray==null)imgbytearray = new byte[camWidth*camHeight*3];
 
			URLConnection uc = url.openConnection();
			uc.setUseCaches(false);
			//uc.setDoInput(true);
			//uc.setRequestProperty("accept","image/jpeg,text/html,text/plain");
 
			BufferedInputStream in;
			int bytes_read;
			int sumread=0;
 
			in = new BufferedInputStream(uc.getInputStream());
 
			while((bytes_read=in.read(input_buffer, 0, 81920))!=-1)
			{
				if(sumread+bytes_read>imgbytearray.length)
				{
					byte[] imgbytearraytemp = new byte[sumread+bytes_read+81920];
					System.arraycopy(imgbytearray,0,imgbytearraytemp,0,imgbytearray.length);
				}
 
				System.arraycopy(input_buffer,0,imgbytearray,sumread,bytes_read);
				sumread=sumread+bytes_read;
			}
			
			in.close();
			
			imgusebyte = new byte[sumread];
			System.arraycopy(imgbytearray,0,imgusebyte,0,sumread);
		}
		catch(Exception e){
                    //System.out.println("Exception urlFetchJPG "+e);
                    JOptionPane.showMessageDialog(new JRootPane(),"cannot get the jpeg image");
                }
 
		return true;
	}
 
	/**
	 * This is called from the Processor to read a frame worth
	 * of video data.
	 */
 	public void read(Buffer buf) throws IOException {
 
	    // Check if we've finished all the frames.
           // Buffer b = new Buffer();
            // b.copy(buf);
            //nextImage >= 20
	    if (!IPCamera.recordOn) //images.size())
		{
		// We are done.  Set EndOfMedia.
		System.err.println("Done reading all images.");
		buf.setEOM(true);
		buf.setOffset(0);
		buf.setLength(0);
		ended = true;
		return;
	    }

 	    nextImage++;

  	    byte data[] = null;
            urlFetchJPG();
            data = imgusebyte;
 	    buf.setData(data);
	    buf.setOffset(0);
	    buf.setLength(data.length);
	    buf.setFormat(format);
	    buf.setFlags(buf.getFlags() | buf.FLAG_KEY_FRAME);
            timeStamp = System.currentTimeMillis();
            resultTimeStamp = timeStamp - timeStamp0;
            long time = resultTimeStamp * 0xf4240L;
            buf.setTimeStamp(time);
            buf.setSequenceNumber(seqNo++);
 	}
 
	/**
	 * Return the format of each video frame.  That will be JPEG.
	 */
	public Format getFormat() {
	    return format;
	}
 
	public ContentDescriptor getContentDescriptor() {
	    return new ContentDescriptor(ContentDescriptor.RAW);
	}
 
	public long getContentLength() {
	    return 0;
	}
 
	public boolean endOfStream() {
	    return ended;
	}
 
	public Object[] getControls() {
	    return new Object[0];
	}
 
	public Object getControl(String type) {
	    return null;
	}
    }
}


class MyAuthenticator extends Authenticator {

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {

        return new PasswordAuthentication("admin","".toCharArray());

        }

    }