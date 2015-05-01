package com.jetremote.xbee;

import com.digi.xbee.api.RemoteDigiPointDevice;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOValue;

public class XBeeThread extends Thread{
	private RemoteDigiPointDevice device;
	private IOValue value;

	public XBeeThread(RemoteDigiPointDevice device, IOValue value) {
		this.device = device;
		this.value = value;
	}

	public Thread newThread(Runnable r) {

		try {
			device.setDIOValue(IOLine.DIO0_AD0, value);
		} catch (TimeoutException e) {
			System.out.println("Error trying to set the DIO value >> " +  e.toString());
		} catch (XBeeException e) {
			System.out.println("Error trying to set the DIO value >> " +  e.toString());
		}
		
		return null;
	}

}
