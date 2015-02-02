/** PiWC :: Boat Control System to WaterSportsCenters
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

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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

public class MainController implements Initializable {
	static Logger LOG = LoggerFactory.getLogger(MainController.class);
	private HashMap<String, CountDownService> mapCounterDown;
	private XBeeDevice localXBee;
	private RemoteXBeeDevice remote2XBee;
	private ObservableList<Integer> items = FXCollections.observableArrayList(1,2,3,4,5,6,7,8,9,10
			,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30);
	private RemoteXBeeDevice selectedNode;
	private RemoteXBeeDevice node1;
	private RemoteXBeeDevice node2;
	private RemoteXBeeDevice node3;
	private RemoteXBeeDevice node4;
    
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
	private XBeeNetwork xbeeNetwork;
	private HashMap<String, RemoteXBeeDevice> mapRemotesXbee;
	
    
	
    private void setSelectedNode(RemoteXBeeDevice node) {
    	//selectedBoat.setStyle("-fx-background-color: white;-fx-text-fill:  #0b75e8; -fx-border-color: #0b75e8");
    	// oldSelectedBoat var value change to newSelectedBoat
    	//this.selectedBoat = bboat;
		// Change style newSelectedBoat
    	//selectedBoat.setStyle("-fx-background-color:  #0b75e8;-fx-text-fill: #FFFFFF;-fx-border-color: #0b75e8");
    	inaccessibleControls(false);
    	if(mapCounterDown!=null &&  mapCounterDown.get(selectedNode.getNodeID())!=null){
    		timeLeft.textProperty().bind(mapCounterDown.get(selectedNode.getNodeID()).messageProperty());
    		timeContract.textProperty().bind(mapCounterDown.get(selectedNode.getNodeID()).titleProperty());
    		inaccessibleControls(true);
    	}  else {
    		timeContract.textProperty().unbind();
    		timeLeft.textProperty().unbind();
    		timeContract.textProperty().set("00:00");
    		timeLeft.textProperty().set("00:00:00");
    	}
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
	    				LOG.info("Nombre del nodo: " + localXBee.getNodeID());
	    				LOG.info("Version del Hardware: " + localXBee.getHardwareVersion());
	    				LOG.info("Version del Firmware: " + localXBee.getFirmwareVersion());
	    				// Obtain the remote XBee device from the XBee network.
    						xbeeNetwork = localXBee.getNetwork();
    						
    					// Instantiate the remotes XBee devices
    						XBee64BitAddress node1Address = new XBee64BitAddress("0013A20040D619F3");
    						node1 = new RemoteXBeeDevice(localXBee, node1Address);
    						xbeeNetwork.addRemoteDevice(node1);
    						
    						XBee64BitAddress node2Address = new XBee64BitAddress("0013A20040D2215A");
    						node2 = new RemoteXBeeDevice(localXBee, node2Address);
    						xbeeNetwork.addRemoteDevice(node2);
    						
    						XBee64BitAddress node3Address = new XBee64BitAddress("0013A20040D220FC");
    						node3 = new RemoteXBeeDevice(localXBee, node3Address);
    						xbeeNetwork.addRemoteDevice(node3);
    						
    						XBee64BitAddress node4Address = new XBee64BitAddress("0013A20040D22151");
    						node4 = new RemoteXBeeDevice(localXBee, node4Address);
    						xbeeNetwork.addRemoteDevice(node4);
    						
    						try {
    								node1.setIOConfiguration(IOLine.DIO0_AD0,  IOMode.DIGITAL_OUT_LOW);
    								node2.setIOConfiguration(IOLine.DIO0_AD0,  IOMode.DIGITAL_OUT_LOW);
    								node3.setIOConfiguration(IOLine.DIO0_AD0,  IOMode.DIGITAL_OUT_LOW);
    								node4.setIOConfiguration(IOLine.DIO0_AD0,  IOMode.DIGITAL_OUT_LOW);
    							} catch (XBeeException e) {
    								LOG.error(e.toString());
    							}
    						// Initialize selected Node
    						if(node1.isRemote()){
    							selectedNode = node1;
    						}
    				}
	    			
	    // Initialize HasMap for CounterDowns
	    	mapCounterDown = new HashMap<String, MainController.CountDownService>();
	    // Inititalize HasMap for RemotesXbee
	    	mapRemotesXbee = new HashMap<String, RemoteXBeeDevice>();
	    	mapRemotesXbee.put("1", node1);
	    	mapRemotesXbee.put("2", node2);
	    	mapRemotesXbee.put("3", node3);
	    	mapRemotesXbee.put("4", node4);
	    	
    				
    	//Initialize ListView
    		listview.setItems(items);
    		listview.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Integer>() {
    		    public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
    		    	selectedNode = mapRemotesXbee.get(newValue.toString());
    		    	LOG.debug("Node to retrieve = node"+newValue);
    		    }
    		});
  		
		
		// Menu Swipe Right
				listview.setOnSwipeRight(new EventHandler<SwipeEvent>() {
					@Override
					public void handle(SwipeEvent event) {
						if(selectedNode.isRemote()){
			            	try {
								selectedNode.setDIOValue(IOLine.DIO0_AD0, IOValue.HIGH);
							} catch (XBeeException e) {
								LOG.error(e.toString());
							}
		            	}
			            event.consume();
					}
				});
		// Menu Swipe Left
				listview.setOnSwipeLeft(new EventHandler<SwipeEvent>() {
					@Override
					public void handle(SwipeEvent event) {
						if(selectedNode.isRemote()){
			            	try {
								selectedNode.setDIOValue(IOLine.DIO0_AD0, IOValue.LOW);
							} catch (XBeeException e) {
								LOG.error(e.toString());
							}
		            	}
			            event.consume();
					}
				});
				
		// Minute5 Button
				minute5.setOnTouchPressed(new EventHandler<TouchEvent>() {
					@Override
					public void handle(TouchEvent event) {
						timeContract.textProperty().setValue(Utils.appendMinutes(5, timeContract.getText()));
			            event.consume();
					}
				});
		// Minute30 Button
				minute30.setOnTouchPressed(new EventHandler<TouchEvent>() {
					@Override
					public void handle(TouchEvent event) {
						timeContract.textProperty().setValue(Utils.appendMinutes(30, timeContract.getText()));
			            event.consume();
					}
				});
		// Clear Button
				clean.setOnTouchPressed(new EventHandler<TouchEvent>() {
					@Override
					public void handle(TouchEvent event) {
						timeContract.setText("00:00");
			            event.consume();
					}
				});
		// Start Button
				start.setOnTouchPressed(new EventHandler<TouchEvent>() {
					@Override
					public void handle(TouchEvent event) {
						if(!timeContract.getText().equals("00:00")){
			        		inaccessibleControls(true);
			        		mapCounterDown.put(selectedNode.getNodeID(), new CountDownService());
			        		mapCounterDown.get(selectedNode.getNodeID()).start();
			        		
			        		// bind service properties to the controls.
			            	timeLeft.textProperty().bind(mapCounterDown.get(selectedNode.getNodeID()).messageProperty());
		        		}
			            event.consume();
					}
				});
		// Stop Button
				stop.setOnTouchPressed(new EventHandler<TouchEvent>() {

					@Override
					public void handle(TouchEvent event) {
						if(!timeLeft.getText().equals("00:00:00")){
			        		inaccessibleControls(false);
			        		CountDownService currentService = mapCounterDown.get(selectedNode.getNodeID());
			        		if(currentService!=null){
			        			currentService.cancel();
			        		}
		            	}
						if(selectedNode.isRemote()){
			            	try {
								selectedNode.setDIOValue(IOLine.DIO0_AD0, IOValue.HIGH);
							} catch (XBeeException e) {
								LOG.error(e.toString());
							}
		            	}
		            	event.consume();
					} 
				});	
    }
}
