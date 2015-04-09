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
package application.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {
		
	public static String appendMinutes(int m, String parse) {
		Date date = null;
		try {
		date = new SimpleDateFormat("HH:mm").parse(parse);
		} catch (ParseException e) {
		e.printStackTrace();
		}
		Calendar calendarDate = Calendar.getInstance();
		calendarDate.setTime(date);
		calendarDate.add(Calendar.MINUTE, m);
		int hour = calendarDate.get(Calendar.HOUR);
		int minute = calendarDate.get(Calendar.MINUTE);
		String shour = String.valueOf(hour);
		String sminute = String.valueOf(minute);
		if(shour.length()<=1){shour = "0"+shour;}
		if(sminute.length()<=1){sminute="0"+sminute;}
		
		return shour + ":" + sminute;
		}
}
