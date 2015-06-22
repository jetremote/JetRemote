/** Jet Remote :: JetSki Control System to Water Sports Centers
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
	
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

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
import com.jetremote.xml.ModuleRF;
import com.jetremote.xml.ModuleRFXmlParser;

public class Main extends Application {
    
	private static HashMap<String, String> PINMAPPING = new HashMap<String, String>();
	private static HashMap<String, String> STRINGMAPPING = new HashMap<String, String>();
	
	private static DigiPointDevice localXBee;
	
	private static DigiPointNetwork digiPointNetwork;
	
	private static ArrayList<ModuleRF> serials = null;
	

	public static void main(String[] args) {
		
	Properties properties = System.getProperties();
	String port = "/dev/ttyAMA0";
		  
	String currentPorts = properties.getProperty("gnu.io.rxtx.SerialPorts", port);
	  if (currentPorts.equals(port)) {
	     properties.setProperty("gnu.io.rxtx.SerialPorts", port);
	  } else {
	     properties.setProperty("gnu.io.rxtx.SerialPorts", currentPorts + File.pathSeparator + port);
	  }
	  
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
	localXBee = new DigiPointDevice(port, 9600);
		try {
			localXBee.open();
		} catch (XBeeException e) {
			System.out.println("Error opening localXBee " + e);
		}

	if(localXBee.isOpen()) {
	  // Obtain the remote XBee device from the XBee network.
		digiPointNetwork = (DigiPointNetwork) localXBee.getNetwork();
		
		//Locate the file
		File xmlFile = new File("/home/pi/jetremote.xml");
		
		//Create the parser instance
		ModuleRFXmlParser parser = new ModuleRFXmlParser();
		
		//Parse the file
		try {
			serials = parser.parseXml(new FileInputStream(xmlFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	  // Instantiate the remotes XBee devices
		for (int i = 0; i < serials.size(); i++){
			XBee64BitAddress nodeAddress = new XBee64BitAddress(serials.get(i).getSerialHigh()+serials.get(i).getSerialLow());
			digiPointNetwork.addRemoteDevice(new RemoteDigiPointDevice(localXBee, nodeAddress));
		}
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
				final String address = getXBeeAddress(selection);
					Runnable t = new Runnable() {
						
					public void run() {
						try {
							digiPointNetwork.getDevice(address).setDIOValue(IOLine.DIO0_AD0, IOValue.LOW);
						} catch (TimeoutException e) {
							e.printStackTrace();
						} catch (XBeeException e) {
							e.printStackTrace();
						}
					}
					};
					t.run();
				selection = null;
				
			} else if (digit == "B") {
				selection = null;
				for(int i = 0; i < serials.size(); i++){
					try {
						digiPointNetwork.getDevice(serials.get(i).get16BitsAddress()).setDIOValue(IOLine.DIO0_AD0, IOValue.LOW);
					} catch (TimeoutException e) {
						e.printStackTrace();
					} catch (XBeeException e) {
						e.printStackTrace();
					}
				}
			} else if (digit == "D" && selection != null) {
				final String address = getXBeeAddress(selection);
					Runnable t = new Runnable() {

					public void run() {
						try {
							digiPointNetwork.getDevice(address).setDIOValue(IOLine.DIO0_AD0, IOValue.HIGH);
						} catch (TimeoutException e) {
							e.printStackTrace();
						} catch (XBeeException e) {
							e.printStackTrace();
						}
					}
					};
					t.run();
				selection = null;
				
			} else if (digit == "C") {
				selection = null;
				for(int i = 0; i < serials.size(); i++){
					try {
						digiPointNetwork.getDevice(serials.get(i).get16BitsAddress()).setDIOValue(IOLine.DIO0_AD0, IOValue.HIGH);
					} catch (TimeoutException e) {
						e.printStackTrace();
					} catch (XBeeException e) {
						e.printStackTrace();
					}
				}
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
	
	
	private static String getXBeeAddress(String selection){
		String address = null;
		// 	Get XBeeAddress to selected key
		for(int i = 0; i < serials.size();i++){
			if(serials.get(i).getId().equals(selection)){
				address = serials.get(i).getSerialHigh()+serials.get(i).getSerialLow();
			}
		}
		return address;
	}

}