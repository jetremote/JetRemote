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

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalMultipurpose;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;


public class Keypad {

	/** The gpio. */
   private final static GpioController gpio = GpioFactory.getInstance();
   
   /** The COLUMN pins */
   private static final Pin COLUMN_1 = RaspiPin.GPIO_04;
   private static final Pin COLUMN_2 = RaspiPin.GPIO_03;
   private static final Pin COLUMN_3 = RaspiPin.GPIO_02;
   private static final Pin COLUMN_4 = RaspiPin.GPIO_01;
   
   /** The Digital COLUMN 1. */
   private static GpioPinDigitalMultipurpose theCOLUMN_1 = gpio
		   .provisionDigitalMultipurposePin(COLUMN_1, PinMode.DIGITAL_INPUT);

   /** The Digital COLUMN 2. */
   private static GpioPinDigitalMultipurpose theCOLUMN_2 = gpio
		   .provisionDigitalMultipurposePin(COLUMN_2, PinMode.DIGITAL_INPUT);

   /** The Digital COLUMN 3. */
   private static GpioPinDigitalMultipurpose theCOLUMN_3 = gpio
		   .provisionDigitalMultipurposePin(COLUMN_3, PinMode.DIGITAL_INPUT);

   /** The Digital COLUMN 4. */
   private static GpioPinDigitalMultipurpose theCOLUMN_4 = gpio
		   .provisionDigitalMultipurposePin(COLUMN_4, PinMode.DIGITAL_INPUT);
   
   /** The Digital Column Array **/
   private final static GpioPinDigitalMultipurpose theColumns[] = {
		   theCOLUMN_1,
		   theCOLUMN_2,
		   theCOLUMN_3,
		   theCOLUMN_4
   };
   
   /** The ROW pins */
   private static final Pin ROW_1 = RaspiPin.GPIO_08;
   private static final Pin ROW_2 = RaspiPin.GPIO_07;
   private static final Pin ROW_3 = RaspiPin.GPIO_06;
   private static final Pin ROW_4 = RaspiPin.GPIO_05;
   
   /** The Digital ROW 1. */
   private static GpioPinDigitalMultipurpose theROW_1 = gpio
		   .provisionDigitalMultipurposePin(ROW_1, PinMode.DIGITAL_INPUT);

   /** The Digital ROW 2. */
   private static GpioPinDigitalMultipurpose theROW_2 = gpio
   			.provisionDigitalMultipurposePin(ROW_2, PinMode.DIGITAL_INPUT);

   /** The Digital ROW 3. */
   private static GpioPinDigitalMultipurpose theROW_3 = gpio
   			.provisionDigitalMultipurposePin(ROW_3, PinMode.DIGITAL_INPUT);

   /** The Digital ROW 4. */
   private static GpioPinDigitalMultipurpose theROW_4 = gpio
		   	.provisionDigitalMultipurposePin(ROW_4, PinMode.DIGITAL_INPUT);
   
   /** The Digital Row Array **/
   private final static GpioPinDigitalMultipurpose theRows[] = {
		   theROW_1,
		   theROW_2,
		   theROW_3,
		   theROW_4
   };
   
   /** The RowId */
   private static int rowVal;
   
   /** The ColId */
   private static int colVal;
   
   
   /**
    * Get KEY
    * @return 
    */
	static String getKey() {
		// Provisioning and Set all columns as output low
		for (final GpioPinDigitalMultipurpose myTheCol : theColumns)
		{
			myTheCol.setMode(PinMode.DIGITAL_OUTPUT);
			myTheCol.setState(PinState.LOW);
		}
		// Provisioning and Set all rows as input pull-up
		for (final GpioPinDigitalMultipurpose myTheRow : theRows)
		{
			myTheRow.setMode(PinMode.DIGITAL_INPUT);
			myTheRow.setPullResistance(PinPullResistance.PULL_UP);
		}
		// Scan rows for pushed key/button
        // A valid key press should set "rowVal" between 0 and 3.
		rowVal = -1;
		for (int myR = 0; myR < theRows.length; myR++)
		{
			GpioPinDigitalMultipurpose tmpRead = theRows[myR];
			if (tmpRead.isLow()) 
			{
				rowVal = myR;
			}
		}
		// if rowVal is not between 0 and 3 then no button was pressed and we can exit
		if (rowVal < 0 || rowVal > 3)
		{
			exit();
			return null;
		}
		// Convert columns to input pull-down
		for (final GpioPinDigitalMultipurpose myTheCol : theColumns)
		{
			myTheCol.setMode(PinMode.DIGITAL_INPUT);
			myTheCol.setPullResistance(PinPullResistance.PULL_DOWN);
		}
		
		// Switch the i-th row found from scan to output
		theRows[rowVal].setMode(PinMode.DIGITAL_OUTPUT);
		theRows[rowVal].setState(PinState.HIGH);
		
		// Scan columns for still-pushed key/button
        // A valid key press should set "colVal"  between 0 and 3.
        colVal = -1;
        for (int myC = 0; myC < theColumns.length; myC++)
		{
			GpioPinDigitalMultipurpose tmpRead = theColumns[myC];
			if (tmpRead.isHigh()) 
			{
				colVal = myC;
			}
		}
        // if colVal is not between 0 and 3 then no button was pressed and we can exit
 		if (colVal < 0 || colVal > 3)
 		{
 			exit();
 			return null;
 		}
 		
 		// Return the value of the key pressed
        exit();
        return String.valueOf(rowVal)+String.valueOf(colVal);
		
	} // end getKey().
	
	
	 private static void exit() {
		 // Reinitialize all rows and columns as input at exit
		 for (final GpioPinDigitalMultipurpose myTheRow : theRows)
			{
				myTheRow.setMode(PinMode.DIGITAL_INPUT);
				myTheRow.setPullResistance(PinPullResistance.PULL_UP);
			}
		 for (final GpioPinDigitalMultipurpose myTheColumn : theColumns)
			{
				myTheColumn.setMode(PinMode.DIGITAL_INPUT);
				myTheColumn.setPullResistance(PinPullResistance.PULL_UP);
			}
	 }
	  
}