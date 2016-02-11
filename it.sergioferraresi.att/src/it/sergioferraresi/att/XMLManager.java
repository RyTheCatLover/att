/**
 * The aim of the Automatic Testing Tool (ATT) program is to automate as much 
 * as possible cross-platform testing procedures.
 * 
 * ***************************************************************************
 * 
 * Copyright (C) 2010-2016  Sergio Ferraresi
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * ***************************************************************************
 * 
 * Contacts:
 * dev@sergioferraresi.it
 * 
 * ***************************************************************************
 * 
 * Information about the file:
 * Filename         XMLManager.java
 * Created on       2015-07-17
 * Last modified on 2016-02-11
 */
package it.sergioferraresi.att;

import it.sergioferraresi.att.model.XmlFileType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Document;

/**
 * TODO
 * @author psf563
 */
public class XMLManager {
	
    /**
     * Identifies the XML File.
     */
    private Document xmlFile = null;

    /**
     * Identifies the name of the XML File.
     */
    private String xmlFilename = null;

    /**
     * Identifies the path of the XML File.
     */
    private String xmlPath = null;

    /**
     * TODO
     */
	private XMLStreamWriter	xMLStreamWriter;

    /**
     * Creates an XML File.
     * @param path a String that identifies the path where to create the XML
     * File.
     * @param filename a String that identifies the name of the XML File.
     * @param type a String that identifies the type of the XML. The accepted
     * values are:
     * <ul>
     *   <li><i>SystemManagement.XTD_TYPE</i>, used to create an XML for Tests
     *       Description (XTD) File;</li>
     *   <li><i>SystemManagement.XTR_TYPE</i>, used to create an XML for Tests
     *       Reports (XTR) File.</li>
     * </ul>
     */
    public void createXMLFile(String path, String filename, XmlFileType type) {
        this.xmlFilename = filename + type.extension();
        this.xmlPath = path;
        // Creates the doc element, which will contain the XTD's nodes.
        try {
            DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = fact.newDocumentBuilder();
            this.xmlFile = parser.newDocument();
            
            XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();	
            this.xMLStreamWriter = xMLOutputFactory.createXMLStreamWriter(new FileWriter(new File(this.xmlPath + this.xmlFilename), false));

            this.xMLStreamWriter.writeStartDocument();
        } catch (ParserConfigurationException e) {
            SystemManagement.manageError(Boolean.TRUE, "(System Management) Error while creating the XML File \"" + this.xmlFilename + "\": " + e.getMessage());
        } catch (XMLStreamException e) {
        	SystemManagement.manageError(Boolean.TRUE, "(System Management) Error while creating the XML File \"" + this.xmlFilename + "\": " + e.getMessage());
		} catch (IOException e) {
			SystemManagement.manageError(Boolean.TRUE, "(System Management) Error while creating the XML File \"" + this.xmlFilename + "\": " + e.getMessage());
		}
    }

    public void openElementToAppend(final String elementName) {
    	try {
			this.xMLStreamWriter.writeStartElement(elementName);
		} catch (XMLStreamException e) {
			SystemManagement.manageError(Boolean.TRUE, "(System Management) Error while openening the element \"" + elementName + "\" to append to the XML File \"" + this.xmlFilename + "\": " + e.getMessage());
		}
    }

    public void closeElementToAppend() {
    	try {
			this.xMLStreamWriter.writeEndElement();
		} catch (XMLStreamException e) {
			SystemManagement.manageError(Boolean.TRUE, "(System Management) Error while openening the element to append to the XML File \"" + this.xmlFilename + "\": " + e.getMessage());
		}
    }

    /**
     * Closes the XML File.
     */
    public void closeXMLFile() {
        try {
            // Write the XML Document to File. TODO
        	this.xMLStreamWriter.writeEndDocument();
        	this.xMLStreamWriter.flush();
            this.xMLStreamWriter.close();
        } catch (XMLStreamException e) {
            SystemManagement.manageError(Boolean.TRUE, "(System Management) Error while closing the XML File \"" + this.xmlFilename + "\": " + e.getMessage());
        }
    }

}