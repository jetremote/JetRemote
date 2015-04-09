package application.stt;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.TimerTask;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import application.MainController;
import edu.cmu.pocketsphinx.Config;
import edu.cmu.pocketsphinx.Decoder;
import edu.cmu.pocketsphinx.Segment;

public class Recognizer extends TimerTask {
	static Logger LOG = LoggerFactory.getLogger(Recognizer.class);
	// record duration, in milliseconds
    static final long RECORD_TIME = 3000;  

    // path of the wav file
    static File wavFile = new File("/tmp/tmp.wav");
 
    // format of audio file
    static AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
 
    // the line from which audio data is captured
    private static TargetDataLine line;

    private static AudioInputStream ais;
    

	@Override
	public void run() {
		// creates a new thread that waits for a specified
        // of time before stopping
			Thread stopper = new Thread(new Runnable() {

			public void run() {
                try {
                    Thread.sleep(RECORD_TIME);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                try {
					recorder.finish();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (UnsupportedAudioFileException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            }
        });
		
	        stopper.start();
	 
	        // start recording
	        try {
				recorder.start();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
	}
	
	
	public static class recorder {
		 /**
	     * Defines an audio format
	     */
	    static AudioFormat getAudioFormat() {
	        float sampleRate = 16000;
	        int sampleSizeInBits = 16;
	        int channels = 1;
	        boolean signed = true;
	        boolean bigEndian = true;
	        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
	                                             channels, signed, bigEndian);
	        return format;
	    }

	public static void finish() throws IOException, UnsupportedAudioFileException, InterruptedException {
		    line.stop();
		    line.close();
		    System.out.println("Capture Finished");
		    Config c = Decoder.defaultConfig();
	        c.setString("-hmm", "/home/pi/modelo/es-es/");
	        c.setString("-lm", "/home/pi/modelo/jetremote.lm.dmp");
	        c.setString("-dict", "/home/pi/modelo/jetremote_es_sphinx.dic");
	        Decoder d = new Decoder(c);
	        AudioInputStream ais = null;

	        URL testwav = new URL("file:/tmp/tmp.wav");
	        AudioInputStream tmp = AudioSystem.getAudioInputStream(testwav);
	        
	        ais = AudioSystem.getAudioInputStream(getAudioFormat(), tmp);

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
	            System.out.println("Error when reading file.wav" + e.getMessage());
	        }
	        d.endUtt();

			for (Segment seg : d.seg()) {
	        	if(!seg.getWord().equals("<s>")&&!seg.getWord().equals("</s>")&&!seg.getWord().equals("<sil>")){
		    	    System.out.println(seg.getWord());
			    	    MainController.buildCommand(seg.getWord().toUpperCase());
	        	}
	        }
		}

	public static void start() throws IOException, LineUnavailableException {
	            AudioFormat format = getAudioFormat();
	            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
	 
	            if (!AudioSystem.isLineSupported(info)) {
	                System.out.println("Line not supported");
	                System.exit(0);
	            }
	            line = (TargetDataLine) AudioSystem.getLine(info);
	            line.open(format);
	            line.start();
	 
	            ais = new AudioInputStream(line);
	            Runtime.getRuntime().exec("aplay /home/pi/answers/beep-tone.wav");
	            System.out.println("LISTENING...");
	            
	            AudioSystem.write(ais, fileType, wavFile);
		}
	}
}
