/** Jet Remote :: Boat Control System to WaterSportsCenters
Copyright (C) 2014  Javier Hdez. :: movidroid@gmail.com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
package application;
**/
package application;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.SwipeEvent;
import javafx.scene.input.TouchEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import application.stt.Speech2Text;
import application.util.Utils;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOMode;
import com.digi.xbee.api.io.IOValue;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.utils.ByteUtils;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class MainController implements Initializable {
	
	static Logger LOG = LoggerFactory.getLogger(MainController.class);
	private HashMap<String, CountDownService> mapCounterDown;
	private ObservableList<Integer> items = FXCollections.observableArrayList(1,2,3,4,5,6,7,8,9,10
			,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30);
	// For the Xbee's
	private XBeeDevice localXBee;
	private XBeeNetwork xbeeNetwork;
	private RemoteXBeeDevice selectedNode;
	private RemoteXBeeDevice node8;
	private RemoteXBeeDevice node2;
	private RemoteXBeeDevice node3;
	private static HashMap<String, RemoteXBeeDevice> mapRemotesXbee;
	protected byte[] rssi;
	
	@FXML
	ListView<Integer> listview;
	@FXML
    Button start;
    @FXML
    Button stop;
    @FXML
    Button minute5;
    @FXML
    Button minute30;
    @FXML
    Button clean;
    @FXML
    Label timeContract;
    @FXML
    Label timeLeft;
    @FXML
    Label DB;
	

    // For the Recognizer
	private enum Words {APAGA, ARRANCA, EMERGENCIA}
	private static String voiceCommand;
	private static String voiceCommandValue;
	private static Map<String, String> commandMap;
	private static Map<String, Integer> commandValueMap;

	public static String getVoiceCommand() {
		return voiceCommand;
	}

	public static  void setVoiceCommand(String command) {
		voiceCommand = command;
	}

	public static String getVoiceCommandValue() {
		return voiceCommandValue;
	}

	public static void setVoiceCommandValue(String voiceCommandValue) {
		MainController.voiceCommandValue = voiceCommandValue;
	}
	
	  
	public void inaccessibleControls(boolean b) {
		minute5.setDisable(b);
		minute30.setDisable(b);
		clean.setDisable(b);
		start.setDisable(b);
	}

	
	// CountDownService class
	public class CountDownService extends Service<Void> {
		Date date;
		int seconds;
		int hours;
		int minutes;
		int counter;
		String currentTime;
		
		@Override
		protected Task<Void> createTask() {
			try {
				date = new SimpleDateFormat("HH:mm").parse(timeContract.getText());
			} catch (ParseException e1) {
				LOG.error(e1.toString());
			}
			counter = (int) TimeUnit.MILLISECONDS.toSeconds(date.getTime());
			return new Task<Void>() {
				private Thread thread;

				@SuppressWarnings("static-access")
				@Override
				protected Void call() throws Exception {
					updateMessage("START!");
					updateTitle(timeContract.getText());
					for (int i = 0; i <= counter;) {
			              try {
							thread.sleep(1000);
						} catch (InterruptedException e) {
							thread.interrupt();
							e.printStackTrace();
						}
				              hours = counter / 3600;
				              minutes = (counter % 3600) / 60;
				              seconds = counter % 60;
				              currentTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
				              updateMessage(currentTime);				              
				              counter--;
			            }
					return null;
				}
				
				@Override
				protected void cancelled() {
					inaccessibleControls(false);
					timeContract.textProperty().unbind();
					timeContract.setText("00:00");
					updateMessage("00:00:00");
					mapCounterDown.remove(selectedNode.getNodeID());
					super.cancelled();
				}

				@Override
				protected void succeeded() {
					inaccessibleControls(false);
					timeContract.textProperty().unbind();
					timeContract.setText("00:00");
					updateMessage("00:00:00");
					mapCounterDown.remove(selectedNode.getNodeID());

					super.succeeded();
				}
			};
		}
	};
	
	
    public void initialize(URL location, ResourceBundle resources) {
    			// Initialize a local XBee (coordinator) 
    				localXBee = new XBeeDevice("/dev/ttyUSB0", 9600);
	    				try {
	    					localXBee.open();
	    				} catch (XBeeException e) {
	    					LOG.error("Error opening localXBee " + e);
	    				}

	    			if(localXBee.isOpen()) {
	    				// Obtain the remote XBee device from the XBee network.
    						xbeeNetwork = localXBee.getNetwork();
    						
    					// Instantiate the remotes XBee devices
    						XBee64BitAddress node8Address = new XBee64BitAddress("0013A20040D22151");
    						node8 = new RemoteXBeeDevice(localXBee, node8Address);
    						xbeeNetwork.addRemoteDevice(node8);
    						
    						XBee64BitAddress node2Address = new XBee64BitAddress("0013A20040D2215A");
    						node2 = new RemoteXBeeDevice(localXBee, node2Address);
    						xbeeNetwork.addRemoteDevice(node2);
    						
    						XBee64BitAddress node3Address = new XBee64BitAddress("0013A20040D220FC");
    						node3 = new RemoteXBeeDevice(localXBee, node3Address);
    						xbeeNetwork.addRemoteDevice(node3);
    						
    						try {
    								node8.setIOConfiguration(IOLine.DIO0_AD0,  IOMode.DIGITAL_OUT_LOW);
    								node2.setIOConfiguration(IOLine.DIO0_AD0,  IOMode.DIGITAL_OUT_LOW);
    								node3.setIOConfiguration(IOLine.DIO0_AD0,  IOMode.DIGITAL_OUT_LOW);
    							} catch (XBeeException e) {
    								LOG.error(e.toString());
    							}
    						// Initialize selected Node
    							selectedNode = node2;
    				}
	    			
	    // Initialize HashMap for CounterDowns
	    	mapCounterDown = new HashMap<String, MainController.CountDownService>();
	    // Inititalize HashMap for RemotesXbee
	    	mapRemotesXbee = new HashMap<String, RemoteXBeeDevice>();
	    	mapRemotesXbee.put("8", node8);
	    	mapRemotesXbee.put("2", node2);
    				
    	//Initialize ListView
    		listview.setItems(items);
    		listview.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Integer>() {
    		    public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
    		    	try {
						rssi = localXBee.getParameter("DB");
						DB.setText(ByteUtils.byteArrayToInt(rssi) + " Db");
					} catch (XBeeException e) {
						e.printStackTrace();
					}
    		    	if(newValue!=null){
    		    		selectedNode = mapRemotesXbee.get(newValue.toString());
    		    	}
    		    }
    		});
    		
    		
		// Menu Swipe Right
				listview.setOnSwipeRight(new EventHandler<SwipeEvent>() {
					public void handle(SwipeEvent event) {
			            	try {
								selectedNode.setDIOValue(IOLine.DIO0_AD0, IOValue.HIGH);
							} catch (XBeeException e) {
								LOG.error(e.toString());
							}
			            event.consume();
					}
				});
		// Menu Swipe Left
				listview.setOnSwipeLeft(new EventHandler<SwipeEvent>() {
					public void handle(SwipeEvent event) {
			            	try {
								selectedNode.setDIOValue(IOLine.DIO0_AD0, IOValue.LOW);
							} catch (XBeeException e) {
								LOG.error(e.toString());
		            	}
			            event.consume();
					}
				});
				
		// Minute5 Button
				minute5.setOnTouchPressed(new EventHandler<TouchEvent>() {
					public void handle(TouchEvent event) {
						timeContract.textProperty().setValue(Utils.appendMinutes(5, timeContract.getText()));
			            event.consume();
					}
				});
		// Minute30 Button
				minute30.setOnTouchPressed(new EventHandler<TouchEvent>() {
					public void handle(TouchEvent event) {
						timeContract.textProperty().setValue(Utils.appendMinutes(30, timeContract.getText()));
			            event.consume();
					}
				});
		// Clear Button
				clean.setOnTouchPressed(new EventHandler<TouchEvent>() {
					public void handle(TouchEvent event) {
						timeContract.setText("00:00");
			            event.consume();
					}
				});
		// Start Button
				start.setOnTouchPressed(new EventHandler<TouchEvent>() {
					public void handle(TouchEvent event) {
						try {
							selectedNode.setDIOValue(IOLine.DIO0_AD0, IOValue.LOW);
						} catch (XBeeException e) {
							LOG.error(e.toString());
						}
						if(!timeContract.getText().equals("00:00")){
			        		inaccessibleControls(true);
			        		mapCounterDown.put(selectedNode.getNodeID(), new CountDownService());
			        		mapCounterDown.get(selectedNode.getNodeID()).start();
			        		
			        		// bind service properties to the controls.
			            	//timeLeft.textProperty().bind(mapCounterDown.get(selectedNode.getNodeID()).messageProperty());
		        		}
						try {
							rssi = localXBee.getParameter("DB");
							DB.setText(ByteUtils.byteArrayToInt(rssi) + " Db");
						} catch (XBeeException e) {
							e.printStackTrace();
						}
			            event.consume();
					}
				});
				
		// Start ALL
		    	start.setOnSwipeLeft(new EventHandler<SwipeEvent>() {

		    		public void handle(SwipeEvent event) {
						for(String key : mapRemotesXbee.keySet()){
							try {
								mapRemotesXbee.get(key).setDIOValue(IOLine.DIO0_AD0, IOValue.LOW);
							} catch (XBeeException e) {
								e.printStackTrace();
							}
						}
					}
				});
		    	
		    	start.setOnSwipeRight(new EventHandler<SwipeEvent>() {

		    		public void handle(SwipeEvent event) {
						for(String key : mapRemotesXbee.keySet()){
							try {
								mapRemotesXbee.get(key).setDIOValue(IOLine.DIO0_AD0, IOValue.LOW);
							} catch (XBeeException e) {
								e.printStackTrace();
							}
						}
					}
				});
		    	
		// Stop Button
				stop.setOnTouchPressed(new EventHandler<TouchEvent>() {

					public void handle(TouchEvent event) {
						if(!timeLeft.getText().equals("00:00:00")){
			        		inaccessibleControls(false);
			        		CountDownService currentService = mapCounterDown.get(selectedNode.getNodeID());
			        		if(currentService!=null){
			        			currentService.cancel();
			        		}
		            	}
			            	try {
								selectedNode.setDIOValue(IOLine.DIO0_AD0, IOValue.HIGH);
								LOG.debug("STOP");
							} catch (XBeeException e) {
								LOG.error(e.toString());
							}
			            	try {
								rssi = localXBee.getParameter("DB");
								DB.setText(ByteUtils.byteArrayToInt(rssi) + " Db");
							} catch (XBeeException e) {
								e.printStackTrace();
							}
		            	event.consume();
					} 
				});	
    
    
    // Stop ALL
    	stop.setOnSwipeLeft(new EventHandler<SwipeEvent>() {

			public void handle(SwipeEvent event) {
				for(String key : mapRemotesXbee.keySet()){
					try {
						mapRemotesXbee.get(key).setDIOValue(IOLine.DIO0_AD0, IOValue.HIGH);
					} catch (XBeeException e) {
						e.printStackTrace();
					}
				}
			}
		});
    	
    	stop.setOnSwipeRight(new EventHandler<SwipeEvent>() {

    		public void handle(SwipeEvent event) {
				for(String key : mapRemotesXbee.keySet()){
					try {
						mapRemotesXbee.get(key).setDIOValue(IOLine.DIO0_AD0, IOValue.HIGH);
					} catch (XBeeException e) {
						e.printStackTrace();
					}
				}
			}
		});
    
    // Load native Pocketsphinx library
    	System.loadLibrary("pocketsphinx_jni");
   	 
    // Maps for Speech2Text
    	commandMap = new HashMap<String, String>();
    	commandMap.put("APAGA", "HIGH");
    	commandMap.put("ARRANCA", "LOW");
    	commandMap.put("EMERGENCIA", "HIGH");
    	commandValueMap = new HashMap<String, Integer>();
    	commandValueMap.put("UNO", 1);
    	commandValueMap.put("DOS", 2);
    	commandValueMap.put("TRES", 3);
    	commandValueMap.put("CUATRO", 4);
    	commandValueMap.put("CINCO", 5);
    	commandValueMap.put("SEIS", 6);
    	commandValueMap.put("SIETE", 7);
    	commandValueMap.put("OCHO", 8);
    	commandValueMap.put("NUEVE", 9);
    	commandValueMap.put("DIEZ", 10);
    	commandValueMap.put("ONCE", 11);
    	commandValueMap.put("DOCE", 12);
    	commandValueMap.put("TRECE", 13);
    	commandValueMap.put("CATORCE", 14);
    	commandValueMap.put("QUINCE", 15);
    	commandValueMap.put("DIECISEIS", 16);
    	commandValueMap.put("DIECISIETE", 17);
    	commandValueMap.put("DIECIOCHO", 18);
    	commandValueMap.put("DIECINUEVE", 19);
    	commandValueMap.put("VEINTE", 20);
    	commandValueMap.put("VEINTIUNO", 21);
    	commandValueMap.put("VEINTIDOS", 22);
    	commandValueMap.put("VEINTITRES", 23);
    	commandValueMap.put("VEINTICUATRO", 24);
    	commandValueMap.put("VEINTICINCO", 25);
    	commandValueMap.put("VEINTISEIS", 26);
    	commandValueMap.put("VEINTISIETE", 27);
    	commandValueMap.put("VEINTIOCHO", 28);
    	commandValueMap.put("VEINTINUEVE", 29);
    	commandValueMap.put("TREINTA", 30);
    	
		// Provision gpio pin #29 as an input pin
    	// http://pi4j.com/pins/model-b-plus.html
    	final Speech2Text s2t = new Speech2Text();
    	final GpioController gpio = GpioFactory.getInstance();
    	final GpioPinDigitalInput micButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_29, 
                PinPullResistance.PULL_DOWN);
    	
    	 micButton.addListener(new GpioPinListenerDigital() {
             public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                 if(micButton.isHigh()==true) {
                	 s2t.capture.start();
                 } else if (micButton.isLow()==true) {
                	 s2t.capture.stop();
                 }
             }
         });
    }  // end Initialize.
    
    
	public static void buildCommand(String str) throws IOException, InterruptedException {
		if(commandMap.containsKey(str)==true) {
			switch (Words.valueOf(str)) {
			case APAGA:
					setVoiceCommand("APAGA");
				break;
			case ARRANCA:
					setVoiceCommand("ARRANCA");
				break;
			case EMERGENCIA:
					emergency();
					setVoiceCommand(null);
				break;
		    }
		} 
		else if (!voiceCommand.equals(null) && commandValueMap.containsKey(str)==true && mapRemotesXbee.containsKey(commandValueMap.get(str))==true) {
			setVoiceCommandValue(str);
			String status = commandMap.get(getVoiceCommand());
			int number = commandValueMap.get(str);
				try {
					mapRemotesXbee.get(number).setDIOValue(IOLine.DIO0_AD0, IOValue.valueOf(status));
					System.out.println(number + " " + status);
				} catch (TimeoutException e) {
					LOG.error(e.toString());
				} catch (XBeeException e) {
					LOG.error(e.toString());
				}
				setVoiceCommand(null);
				setVoiceCommandValue(null);
		} else if (mapRemotesXbee.containsKey(commandValueMap.get(str))==false && !voiceCommand.equals(null)){
				setVoiceCommand(null);
				setVoiceCommandValue(null);
		}
	} 
	

	private static void emergency() throws IOException {
			for(String key : mapRemotesXbee.keySet()){
				try {
					mapRemotesXbee.get(key).setDIOValue(IOLine.DIO0_AD0, IOValue.HIGH);
				} catch (XBeeException e) {
					e.printStackTrace();
				}
			}
	}
}