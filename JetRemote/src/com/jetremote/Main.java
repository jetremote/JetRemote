/** Jet Remote :: JetSki Control System to WaterSportsCenters
Copyright (C) 2015  Javier Hernández Déniz. :: jetremote.canarias@gmail.com
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
**/
package com.jetremote;
	
import java.util.HashMap;

import javafx.application.Application;
import javafx.stage.Stage;

import com.digi.xbee.api.DigiPointDevice;
import com.digi.xbee.api.DigiPointNetwork;
import com.digi.xbee.api.RemoteDigiPointDevice;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOValue;
import com.digi.xbee.api.models.XBee64BitAddress;

public class Main extends Application {
    
	private static HashMap<String, String> PINMAPPING = new HashMap<String, String>();
	private static HashMap<String, String> STRINGMAPPING = new HashMap<String, String>();
	private static HashMap<String, RemoteDigiPointDevice> XBEEMAPPING = new HashMap<String, RemoteDigiPointDevice>();
	
	private static DigiPointDevice localXBee;
	
	private static DigiPointNetwork digiPointNetwork;
	
	private static RemoteDigiPointDevice node6;
	

	public static void main(String[] args) {
		  PINMAPPING.put("00", "1");
	      PINMAPPING.put("01", "2");
	      PINMAPPING.put("02", "3");
	      PINMAPPING.put("03", "A");
	      PINMAPPING.put("10", "4");
	      PINMAPPING.put("11", "5");
	      PINMAPPING.put("12", "6");
	      PINMAPPING.put("13", "B");
	      PINMAPPING.put("20", "7");
	      PINMAPPING.put("21", "8");
	      PINMAPPING.put("22", "9");
	      PINMAPPING.put("23", "C");
	      PINMAPPING.put("30", "*");
	      PINMAPPING.put("31", "0");
	      PINMAPPING.put("32", "#");
	      PINMAPPING.put("33", "D");
	      
	      STRINGMAPPING.put("A", "A");
	      STRINGMAPPING.put("B", "B");
	      STRINGMAPPING.put("C", "C");
	      STRINGMAPPING.put("D", "D");
	      STRINGMAPPING.put("*", "*");
	      STRINGMAPPING.put("#", "#");
	      
		// Initialize a local XBee (coordinator) 
		localXBee = new DigiPointDevice("/dev/ttyUSB0", 9600);
			try {
				localXBee.open();
			} catch (XBeeException e) {
				System.out.println("Error opening localXBee " + e);
			}

		if(localXBee.isOpen()) {
		  // Obtain the remote XBee device from the XBee network.
			digiPointNetwork =   (DigiPointNetwork) localXBee.getNetwork();
			
		  // Instantiate the remotes XBee devices
			XBee64BitAddress node6Address = new XBee64BitAddress("0013A20040A2C795");
			node6 = new RemoteDigiPointDevice(localXBee, node6Address);
			digiPointNetwork.addRemoteDevice(node6);
			
			XBEEMAPPING.put("6", node6);
			}

	    // Selected jetSki
		String selection = null;
		// Key pressed
		String digit = null;
		
		while (digit != "none"){
			digit = PINMAPPING.get(Keypad.getKey());
			
			if(digit!=null){
				System.out.println(digit);
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if (digit == "A" && selection != null) {
					final int nodeId = Integer.valueOf(selection);
					if(XBEEMAPPING.containsKey(String.valueOf(nodeId))) {
						Runnable t = new Runnable() {
	
						public void run() {
							try {
								XBEEMAPPING.get(String.valueOf(nodeId)).setDIOValue(IOLine.DIO0_AD0, IOValue.LOW);
							} catch (TimeoutException e) {
								e.printStackTrace();
							} catch (XBeeException e) {
								e.printStackTrace();
							}
						}
						};
						t.run();
					}
					selection = null;
					
				} else if (digit == "D" && selection != null) {
					final int nodeId = Integer.valueOf(selection);
					if(XBEEMAPPING.containsKey(String.valueOf(nodeId))) {
						Runnable t = new Runnable() {
	
						public void run() {
							try {
								XBEEMAPPING.get(String.valueOf(nodeId)).setDIOValue(IOLine.DIO0_AD0, IOValue.HIGH);
							} catch (TimeoutException e) {
								e.printStackTrace();
							} catch (XBeeException e) {
								e.printStackTrace();
							}
						}
						};
						t.run();
					}
					selection = null;
					
				} else if (digit == "C") {
					selection = null;
				} else if(digit == "*") {
					 selection = null;
				} else if(digit == "#") {
					selection = null;
				} else if(!STRINGMAPPING.containsValue(digit)){
					if(selection==null){
						selection = digit;
					} else {
						selection = selection + digit;
					}
				}
			}
		}
		
		launch(args);
		}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
	}

}