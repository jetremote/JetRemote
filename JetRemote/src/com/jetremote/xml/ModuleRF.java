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
package com.jetremote.xml;

/**
 * Model class. Its instances will be populated using SAX parser. 
 * */
public class ModuleRF 
{
	//XML attribute id
	private String id;
	//XML element serialNumberHigh
	private String serialHigh;
	//XML element serialNumberLow
	private String serialLow;
	//XML element model
	private String model;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getSerialHigh() {
		return serialHigh;
	}
	public void setSerialHigh(String serialHigh) {
		this.serialHigh = serialHigh;
	}
	public String getSerialLow() {
		return serialLow;
	}
	public void setSerialLow(String serialLow) {
		this.serialLow = serialLow;
	}
	public String getFullAddress(){
		return serialHigh+serialLow;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	@Override
	public String toString() {
		return this.id + ":" + this.serialHigh + this.serialLow ;
	}
}