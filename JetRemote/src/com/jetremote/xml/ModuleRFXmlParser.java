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


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class ModuleRFXmlParser 
{
	public ArrayList<ModuleRF> parseXml(InputStream in)
	{
		//Create a empty link of modules initially
		ArrayList<ModuleRF> modules = new ArrayList<ModuleRF>();
		try 
		{
			//Create default handler instance
			ModuleRFParserHandler handler = new ModuleRFParserHandler();
			
			//Create parser from factory
			XMLReader parser = XMLReaderFactory.createXMLReader();
			
			//Register handler with parser
			parser.setContentHandler(handler);

			//Create an input source from the XML input stream
			InputSource source = new InputSource(in);
			
			//parse the document
			parser.parse(source);
			
			//populate the parsed modules list in above created empty list
			modules = handler.getModules();

		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
		return modules;
	}
}