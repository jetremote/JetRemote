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


import java.util.ArrayList;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class ModuleRFParserHandler extends DefaultHandler
{
	//This is the list which shall be populated while parsing the XML. 
    private ArrayList<ModuleRF> moduleList = new ArrayList<ModuleRF>();
    
    //As we read any XML element we will push that in this stack
    private Stack<String> elementStack = new Stack<String>();
    
    //As we complete one module block in XML, we will push the Module instance in moduleList 
    private Stack<ModuleRF> objectStack = new Stack<ModuleRF>();

    public void startDocument() throws SAXException
    {
        //System.out.println("start of the document   : ");
    }

    public void endDocument() throws SAXException
    {
        //System.out.println("end of the document document     : ");
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
    	//Push it in element stack
        this.elementStack.push(qName);

        //If this is start of 'module' element then prepare a new Module instance and push it in object stack
        if ("module".equals(qName))
        {
            //New Module instance
        	ModuleRF module = new ModuleRF();
            
            //Set all required attributes in any XML element here itself
            if(attributes != null && attributes.getLength() == 1)
            {
            	module.setId(attributes.getValue(0));
            }
            this.objectStack.push(module);
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException
    {	
    	//Remove last added </module> element
        this.elementStack.pop();

        //Module instance has been constructed so pop it from object stack and push in moduleList
        if ("module".equals(qName))
        {
            ModuleRF object = this.objectStack.pop();
            this.moduleList.add(object);
        }
    }

    /**
     * This will be called everytime parser encounter a value node
     * */
    public void characters(char[] ch, int start, int length) throws SAXException
    {
        String value = new String(ch, start, length).trim();

        if (value.length() == 0)
        {
            return; // ignore white space
        }
        
        //handle the value based on to which element it belongs
        if ("serialhigh".equals(currentElement()))
        {
            ModuleRF module = (ModuleRF) this.objectStack.peek();
            module.setSerialHigh(value);
        }
        else if ("seriallow".equals(currentElement()))
        {
            ModuleRF module = (ModuleRF) this.objectStack.peek();
            module.setSerialLow(value);
        } 
        else if ("model".equals(currentElement()))
        {
            ModuleRF module = (ModuleRF) this.objectStack.peek();
            module.setModel(value);
        } 
    }
    
    /**
     * Utility method for getting the current element in processing
     * */
    private String currentElement()
    {
        return this.elementStack.peek();
    }
    
    //Accessor for moduleList object
    public ArrayList<ModuleRF> getModules()
    {
    	return moduleList;
    }
}