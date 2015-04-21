package application.stt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import application.MainController;
import edu.cmu.pocketsphinx.Config;
import edu.cmu.pocketsphinx.Decoder;
import edu.cmu.pocketsphinx.Segment;

public class Speech2Text {
	
	public Capture capture = new Capture();

	private static Config config;

	static AudioInputStream ais;
	
	static String errStr;

	private static Decoder d;

	  /**
	   * Reads data from the input channel and writes to the output stream
	   */
	  public static class Capture implements Runnable {
		  
		  TargetDataLine line;

		  Thread thread;

		  public void start() {
			      thread = new Thread(this);
			      thread.setName("Capture");
			      thread.start();
			    }
		   
		    public void stop() {
		        thread = null;
		      }

		      private void shutDown(String message) {
		        if ( thread != null) {
		          thread = null;
		          System.err.println(errStr);
		        }
		      }

		public void run() {
			
		    ais = null;
		      
			/**
		    * Defines an audio format
		    */
				AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
		        float rate = 16000.0f;
		        int sampleSizeInBits = 16;
		        int channels = 1;
		        boolean bigEndian = true;
		        AudioFormat format = new AudioFormat(encoding, rate, sampleSizeInBits,
		                                             channels,(sampleSizeInBits / 8)
		                                             * channels, rate, bigEndian);
		        
		        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
	
		        if (!AudioSystem.isLineSupported(info)) {
		          shutDown("Line matching " + info + " not supported.");
		          return;
		        }
		        
		        try {
		            line = (TargetDataLine) AudioSystem.getLine(info);
		            line.open(format, line.getBufferSize());
		          } catch (LineUnavailableException ex) {
		            shutDown("Unable to open the line: " + ex);
		            return;
		          } catch (SecurityException ex) {
		            shutDown(ex.toString());
		            return;
		          } catch (Exception ex) {
		            shutDown(ex.toString());
		            return;
		          }
	        
	        // recognizer the captured audio data
		        ByteArrayOutputStream out = new ByteArrayOutputStream();
		        int frameSizeInBytes = format.getFrameSize();
		        int bufferLengthInFrames = line.getBufferSize() / 8;
		        int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
		        byte[] data = new byte[bufferLengthInBytes];
		        int numBytesRead;
	
		        line.start();
	
		        while (thread != null) {
		          if ((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) {
		            break;
		          }
		          out.write(data, 0, numBytesRead);
		        }

	        // we reached the end of the stream.
	        // stop and close the line.
		        line.stop();
		        line.close();
		        line = null;

	        // stop and close the output stream
		        try {
		          out.flush();
		          out.close();
		        } catch (IOException ex) {
		          System.out.println( "S2T: " + ex.toString());
		        }
	        
		    // load bytes into the audio input stream for recognition with CMU Pocketsphinx.
		        byte audioBytes[] = out.toByteArray();
		        ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
		        ais = new AudioInputStream(bais, format, audioBytes.length / frameSizeInBytes);
		        
		  	  	config = Decoder.defaultConfig();
		        config.setString("-hmm", "/home/pi/modelo/es-es");
		        config.setString("-lm", "/home/pi/modelo/jetremote.lm.dmp");
		        config.setString("-dict", "/home/pi/modelo/jetremote_es_sphinx.dic");
		        
		        d = new Decoder(config);
		        d.startUtt();
		        
		        d.setRawdataSize(300000);
		        byte[] b = new byte[4096];
		        try {
		            int nbytes;
		            while ((nbytes = ais.read(b)) >= 0) {
		                ByteBuffer bb = ByteBuffer.wrap(b, 0, nbytes);
		                short[] s = new short[nbytes/2];
		                bb.asShortBuffer().get(s);
		                d.processRaw(s, nbytes/2, false, false);
		            }
		        } catch (IOException e) {
		            System.out.println("Error when reading AudioInputStream: " + e.getMessage());
		        }
		        d.endUtt();		        

		        for (Segment seg : d.seg()) {
		        	if(!seg.getWord().equals("<s>")&&!seg.getWord().equals("</s>")&&!seg.getWord().equals("<sil>")){
						try {
							MainController.buildCommand(seg.getWord().toUpperCase());
						} catch (IOException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
		    	    System.out.println(seg.getWord().toUpperCase());
		        	}
		        }
			}
	  }
}