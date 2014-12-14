/**
 * The aim of the Automatic Testing Tool (ATT) program is to automate as much 
 * as possible cross-platform testing procedures.
 * 
 * ***************************************************************************
 * 
 * Copyright (C) 2010-2014  Sergio Ferraresi
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
 * Filename         SystemManagement.java
 * Created on       2010-08-01
 * Last modified on 2014-12-09
 */
package it.sergioferraresi.att;

import it.sergioferraresi.att.ui.WindowManager;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Provides the methods useful to create the folder tree used by the Program and
 * generics folders, provides information about the System (Operative System
 * architecture, name, and Version, System date and System hour in different
 * formats, information about the screen), manages the Log File, manages the XTD
 * and XTR files, manages errors, manages the program execution, and manages
 * some default paths (like the Text Editor path).
 * Provides also methods to set the ATT interface type (Window or Console), to
 * init the ATT, and to manage the Projects.
 *
 * @author  Sergio Ferraresi (psf563)
 * @version 1.0 (release 20101209fr)
 */
public class SystemManagement {
    /**
     * Identifies the Cygwin Windows default application path. It is used to
     * interprete Bash Scripts under Windows Systems.
     */
    public static final String CYGWIN_PATH = "C:\\cygwin\\bin\\bash";
    /**
     * Identifies the GNU/Linux default path where the User may find the Text
     * Editor.
     */
    public static final String LINUX_DEFAULT_EDITOR_PATH = "/usr/bin/";
    /**
     * Identifies the Windows default path where the User may find the Text
     * Editor.
     */
    public static final String WINDOWS_DEFAULT_EDITOR_PATH = "C:\\Windows\\System32\\";
    /**
     * Identifies the GNU/Linux default path where the User may find a Generic
     * Program.
     */
    public static final String LINUX_GENERIC_PROGRAM_PATH = "/usr/bin/";
    /**
     * Identifies the Windows default path where the User may find a Generic
     * Program.
     */
    public static final String WINDOWS_GENERIC_PROGRAM_PATH = "C:\\Program Files\\";
    /**
     * Identifies the default path where the User may find a Generic Program for
     * Windows XP Systems.
     */
    public static final String WINDOWSXP_GENERIC_PROGRAM_PATH = "C:\\Programmi\\";
    /**
     * Identifies the "Create Project" mode for the Project.
     */
    public static final String CREATE_PROJECT_MODE = "Create";
    /**
     * Identifies the "Open Project" mode for the Project.
     */
    public static final String OPEN_PROJECT_MODE = "Open";
    /**
     * Identifies the correct exit status for the ATT Program and for the Test
     * Executor. Test executed and passed.
     */
    public static final int PASS_EXIT_STATUS = 0;
    /**
     * Identifies the pending exit status for the ATT Program and for the Test
     * Executor. Test executed, but needs to validate the screenshots for the
     * manual validation.
     */
    public static final int PENDING_EXIT_STATUS = 2;
    /**
     * Identifies the uncorrect exit status for the ATT Program and for the Test
     * Executor. Test executed and not passed: needs to validate the not equals 
     * screenshots for the automatic validation.
     */
    public static final int FAIL_EXIT_STATUS = 1;
    /**
     * Identifies the uncorrect exit status for the ATT Program and for the
     * Test Executor. Error made by the Program.
     */
    public static final int ERROR_EXIT_STATUS = -1;
    /**
     * The String that identifies the "Pass" status.
     */
    public static final String PASS_STATUS = "Pass";
    /**
     * The String that identifies the "Pending" status.
     */
    public static final String PENDING_STATUS = "Pending";
    /**
     * The String that identifies the "Fail" status.
     */
    public static final String FAIL_STATUS = "Fail";
    /**
     * The String that identifies the "Error" status.
     */
    public static final String ERROR_STATUS = "Error";
    /**
     * Identifies an "XML for Tests Reports" (XTR) file.
     */
    public static final String XTR_TYPE = ".xtr";
    /**
     * Identifies an "XML for Tests Description" (XTD) file.
     */
    public static final String XTD_TYPE = ".xtd";
    /**
     * Identifies the Window Execution Mode for the ATT Program.
     */
    public static final String WINDOW_INTERFACE = "Window";
    /**
     * Identifies the Console Execution Mode for the ATT Program.
     */
    public static final String CONSOLE_INTERFACE = "Console";
    /**
     * Identifies the text type for the Log. It is used to write a text in the
     * Log File.
     */
    public static final int LOG_TEXT_TYPE = 1;
    /**
     * Identifies the separator type for the Log. It is used to write a
     * separator in the Log File.
     */
    public static final int LOG_SEPARATOR_TYPE = 0;
    /**
     * Identifies the "log" relative path.
     */
    protected static final String LOG_FOLDER_PATH = "log" + File.separator;
    /**
     * Identifies the "screenshots" relative path.
     */
    protected static final String SCREENSHOTS_FOLDER_PATH = "screenshots" + File.separator;
    /**
     * Identifies the relative path that contains "execution" screenshots.
     */
    protected static final String SCREENSHOTS_EXECUTION_FOLDER_PATH = "screenshots" + File.separator + "execution" + File.separator;
    /**
     * Identifies the "scripts" relative path.
     */
    protected static final String SCRIPTS_FOLDER_PATH = "scripts" + File.separator;
    /**
     * Identifies the "results" relative path.
     */
    protected static final String RESULTS_FOLDER_PATH = "results" + File.separator;
    /**
     * Identifies the "tests" relative path.
     */
    protected static final String TESTS_FOLDER_PATH = "tests" + File.separator;
    /**
     * Identifies the Log separator. It is write to to the Log when the
     * Log type is equals to LOG_SEPARATOR_TYPE.
     */
    private static final String LOG_SEPARATOR = "";
    /**
     * Identifies the Extended Date Format: YYYY/MM/DD.
     */
    private static final String EXTENDED_DATE_FORMAT = "yyyy/MM/dd";
    /**
     * Identifies the Extended Hour Format: hh:mm:ss:SSSS[AM|PM].
     */
    private static final String EXTENDED_HOUR_FORMAT = "KK:mm:ss:SSSa";
    /**
     * Identifies the Compact Date Format: YYYYMMDD.
     */
    private static final String COMPACT_DATE_FORMAT = "yyyyMMdd";
    /**
     * Identifies the Compact Hour Format: [AM|PM]hhmmss.
     */
    private static final String COMPACT_HOUR_FORMAT = "aKKmmss";
    /**
     * Identifies the MEA minimum width: 1200px.
     */
    private static final int MEA_MINIMUM_WIDTH = 1200;
    /**
     * Identifies the MEA minimum height: 700px.
     */
    private static final int MEA_MINIMUM_HEIGHT = 700;
    /**
     * Identifies the Log File path.
     */
    private static BufferedWriter logFile = null;
    /**
     * Identifies the Log File path for the Project.
     */
    private static BufferedWriter projectLogFile = null;
    /**
     * Tells to the ATT Program if it is using a Project.
     */
    private static Boolean usingProject = Boolean.FALSE;
    /**
     * Identifies the console for the ATT Console Executing Mode and for the
     * error management.
     */
    private static Console console = null;
    /**
     * Identifies the XML File.
     */
    private static Document xmlFile = null;
    /**
     * Identifies the ATT Execution Mode.
     */
    private static String ATTInterface = null;
    /**
     * Identifies the "main" absolute path where the ATT Program is working.
     */
    private static String mainWorkingFolder = null;
    /**
     * Identifies the "log" absolute path where the ATT Program is working.
     */
    private static String logWorkingFolder = null;
    /**
     * Identifies the "results" absolute path where the ATT Program is working.
     */
    private static String resultsWorkingFolder = null;
    /**
     * Identifies the "scripts" absolute path where the ATT Program is working.
     */
    private static String scriptsWorkingFolder = null;
    /**
     * Identifies the "screenshots" absolute path where the ATT Program is
     * working.
     */
    private static String screenshotsWorkingFolder = null;
    /**
     * Identifies the "execution" absolute path where the ATT Program is
     * working.
     */
    private static String screenshotsExecutionWorkingFolder = null;
    /**
     * Identifies the "tests" absolute path where the ATT Program is working.
     */
    private static String testsWorkingFolder = null;
    /**
     * Identifies the architecture of the Operative System.
     */
    private static String osArch = System.getProperty("os.arch");
    /**
     * Identifies the name of the Operative System.
     */
    private static String osName = System.getProperty("os.name");
    /**
     * Identifies the version of the Operative System.
     */
    private static String osVers = System.getProperty("os.version");
    /**
     * Identifies the new line character of the Operative System.
     */
    private static String newline = System.getProperty("line.separator");
    /**
     * Identifies the name of the Log File.
     */
    private static String logFilename = null;
    /**
     * Identifies the name of the XML File.
     */
    private static String xmlFilename = null;
    /**
     * Identifies the path of the XML File.
     */
    private static String xmlPath = null;
    /**
     * Identifies the path of the Text Editor.
     */
    private static String textEditorPath = null;
    /**
     * Identifies the path of the Cygwin Program used to execute Bash Scripts
     * under Windows.
     */
    private static String cygwinPath = null;
    /**
     * Identifies the path of the Generic Program.
     */
    private static String genericProgramPath = null;
    /**
     * Identifies the default Toolkit useful to get information about the
     * System.
     */
    private static Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
    /**
     * Identifies the size of the screen.
     */
    private static Dimension screenSize = SystemManagement.defaultToolkit.getScreenSize();



    /**
     * Not implemented.
     */
    private SystemManagement() {}

    /**
     * Returns the architecture of the Operative System.
     * @return a String that identifies the architecture of the Operative System.
     */
    public static String getOSArchitecture() {
        return SystemManagement.osArch;
    }

    /**
     * Returns the name of the Operative System.
     * @return a String that identifies the name of the Operative System.
     */
    public static String getOSName() {
        return SystemManagement.osName;
    }

    /**
     * Returns the version of the Operative System.
     * @return a String that identifies the version of the Operative System.
     */
    public static String getOSVersion() {
        return SystemManagement.osVers;
    }

    /**
     * Returns the new line character of the Operative System.
     * @return a String that identifies the new line character of the Operative
     * System.
     */
    public static String getOSNewLineCharacter() {
        return SystemManagement.newline;
    }

    /**
     * Creates a folder named folderName.
     * @param folderPath a String that identifies the path of the folder to
     * create. It must contains the path where to create the folder, and the
     * name of the folder.
     * @return true if the folder has been created, false otherwise.
     */
    public static Boolean createFolder(String folderPath) {
        if (new File(folderPath).exists() == false)
            if (new File(folderPath).mkdir() == false) {
                SystemManagement.manageError(Boolean.FALSE, "(System Management) Error while creating the folder \"" + folderPath + "\".");
                return Boolean.FALSE;
            }
        return Boolean.TRUE;
    }

    /**
     * Creates and closes the Log File.
     * @param logPath a String that identifies the path of the Log File. It must
     * contains the path, and its name.
     */
    public static void createAndCloseLogFile(String logPath) {
        // Removes the "log/" prefix.
        SystemManagement.logFilename = logPath.substring((logPath.lastIndexOf(File.separator) + 1), logPath.length());
        try {
            SystemManagement.logFile = new BufferedWriter(new FileWriter(logPath, true));
            SystemManagement.logFile.close();
        } catch (IOException e) {
            SystemManagement.manageError(Boolean.TRUE, "(System Management) Error while creting and closing the Log File \"" + logPath + "\": " + e.getMessage());
        }
    }

    /**
     * Returns a BufferedWriter of the Log file.
     * @return a BufferedWriter element that identifies the Log file.
     */
    public static BufferedWriter getLogFile() {
        return SystemManagement.logFile;
    }

    /**
     * Returns the name of the Log File.
     * @return a String that identifies the name of the Log File.
     */
    public static String getLogFilename() {
        return SystemManagement.logFilename;
    }

    /**
     * Appends text to the Log File.
     * @param text a String that identifies the text to append to the Log File.
     */
    public static void appendAndCloseLogFile(String text) {
        try {
            SystemManagement.logFile = new BufferedWriter(new FileWriter(SystemManagement.getLogWorkingFolder() + SystemManagement.logFilename, true));
            SystemManagement.logFile.write(text, 0, text.length());
            // win/linux
            SystemManagement.logFile.newLine();
            SystemManagement.logFile.close();
            File tmp = new File(SystemManagement.getMainWorkingFolder() + SystemManagement.LOG_FOLDER_PATH);
            if (SystemManagement.usingProject && tmp.exists()) {
                if (SystemManagement.projectLogFile == null) {
                    // Creates the log file beacuse it does not exist.
                    try{
                        File input = new File(SystemManagement.getLogWorkingFolder() + SystemManagement.logFilename);
                        File output = new File(SystemManagement.getMainWorkingFolder() + SystemManagement.LOG_FOLDER_PATH + SystemManagement.logFilename);
                        InputStream in = new FileInputStream(input);
                        //For Overwrite the file.
                        OutputStream out = new FileOutputStream(output);
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = in.read(buf)) > 0)
                            out.write(buf, 0, len);
                        in.close();
                        out.close();
                    } catch(FileNotFoundException e){
                        SystemManagement.manageError(Boolean.TRUE, "(System Management) Error while appending to the Project Log File \"" + text + "\" and closing the Log File \"" + SystemManagement.getMainWorkingFolder() + SystemManagement.LOG_FOLDER_PATH + SystemManagement.logFilename + "\": " + e.getMessage());
                    } catch(IOException e){
                        SystemManagement.manageError(Boolean.TRUE, "(System Management) Error while appending to the Project Log File \"" + text + "\" and closing the Log File \"" + SystemManagement.getMainWorkingFolder() + SystemManagement.LOG_FOLDER_PATH + SystemManagement.logFilename + "\": " + e.getMessage());
                    }
                } else {
                    // Append to the log file.
                    SystemManagement.projectLogFile = new BufferedWriter(new FileWriter(SystemManagement.getMainWorkingFolder() + SystemManagement.LOG_FOLDER_PATH + SystemManagement.logFilename, true));
                    SystemManagement.projectLogFile.write(text, 0, text.length());
                    // win/linux
                    SystemManagement.projectLogFile.newLine();
                    SystemManagement.projectLogFile.close();
                }
            }
        } catch (IOException e) {
            SystemManagement.manageError(Boolean.TRUE, "(System Management) Error while appending to the Log File \"" + text + "\" and closing the Log File \"" + SystemManagement.logFilename + "\": " + e.getMessage());
        }
    }

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
    public static void createXMLFile(String path, String filename, String type) {
        SystemManagement.xmlFilename = (type.equals(SystemManagement.XTD_TYPE))? (filename + SystemManagement.XTD_TYPE):(filename + SystemManagement.XTR_TYPE);
        SystemManagement.xmlPath = path;
        // Creates the doc element, which will contain the XTD's nodes.
        try {
            DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = fact.newDocumentBuilder();
            SystemManagement.xmlFile = parser.newDocument();
        } catch (ParserConfigurationException e) {
            SystemManagement.manageError(Boolean.TRUE, "(System Management) Error while creating the XML File \"" + filename + "\": " + e.getMessage());
        }
    }

    /**
     * Creates and returns an attribute for the XML File.
     * @param attributeName a String that identifies the name of the attrribute
     * to create.
     * @param attributeValue a String that identifies the value of the
     * attribute to create.
     * @return an Attr element that contains the created attribute.
     */
    public static Attr createXMLAttribute(String attributeName, String attributeValue) {
        Attr tmp = SystemManagement.xmlFile.createAttribute(attributeName);
        tmp.setValue(attributeValue);
        return tmp;
    }

    /**
     * Creates and returns a node for the XML File.
     * @param nodeName a String that identifies the name of the node to create.
     * @param nodeValue a String that identifies the value of the node to
     * create.
     * @return a Node element that contains the created node.
     */
    public static Node createXMLNode(String nodeName, String nodeValue) {
        Node tmp = SystemManagement.xmlFile.createElement(nodeName);
        if (nodeValue != null)
            tmp.setTextContent(nodeValue);
        return tmp;
    }

    /**
     * Creates and returns a comment node for the XML File.
     * @param commentValue a String that identifies the value of the comment
     * node to create.
     * @return a Comment element that contains the created comment.
     */
    public static Comment createXMLComment(String commentValue) {
        return SystemManagement.getXMLFile().createComment(commentValue);
    }

    /**
     * Appends an XML attribute to an XML node.
     * @param parent a Node element that identifies the parent node where to
     * append the attribute.
     * @param attribute an Attr element that identifies the attribute to append.
     */
    public static void appendXMLAttributeToXMLNode(Node parent, Attr attribute) {
        NamedNodeMap attributes = parent.getAttributes();
        attributes.setNamedItem(attribute);
    }

    /**
     * Appends an XML node to another XML node.<br/>
     * If the parent is equals to <i>null</i>, the child node will be appended
     * to the XML Document. It is used when it is necessary to append the root
     * node to the XML Document.<br/>
     * If the parent is not equals to <i>null</i>, the child node will be
     * appended to the parent node.
     * @param parent a Node element that identifies the parent node where to
     * append the node.
     * @param child a Node element that identifies the node to append.
     */
    public static void appendXMLChildToXMLNode(Node parent, Node child) {
        if (parent == null)
            // Root Node case.
            SystemManagement.xmlFile.appendChild(child);
        else
            // Other Node case.
            parent.appendChild(child);
    }

    /**
     * Closes the XML File.
     */
    public static void closeXMLFile() {
        try {
            // Write the XML Document to File.
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            StreamResult result = new StreamResult(new FileWriter(new File(SystemManagement.getXMLPath() + SystemManagement.getXMLFilename()), false));
            DOMSource source = new DOMSource(SystemManagement.getXMLFile());
            transformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
            SystemManagement.manageError(Boolean.TRUE, "(System Management) Error while closing the XML File \"" + SystemManagement.xmlFilename + "\": " + e.getMessage());
        } catch (TransformerException e) {
            SystemManagement.manageError(Boolean.TRUE, "(System Management) Error while closing XML the File \"" + SystemManagement.xmlFilename + "\": " + e.getMessage());
        } catch (IOException e) {
            SystemManagement.manageError(Boolean.TRUE, "(System Management) Error while closing XML the File \"" + SystemManagement.xmlFilename + "\": " + e.getMessage());
        }
    }

    /**
     * Returns the XML File.
     * @return a Document element that identifies the XML Document.
     */
    public static Document getXMLFile() {
        return SystemManagement.xmlFile;
    }

    /**
     * Returns the name of the XML File.
     * @return a String that identifies the name of the XML File. It contains
     * the name and the extension of the XML File.
     */
    public static String getXMLFilename() {
        return SystemManagement.xmlFilename;
    }

    /**
     * Returns the path of the XML File.
     * @return a String that identifies the path of the XML File. It does not
     * contain the name and the extension of the XML File.
     */
    public static String getXMLPath() {
        return SystemManagement.xmlPath;
    }

    /**
     * Appends to the Log and to the Standard Output of the ATT interface the
     * message passed.
     * @param type a integer value that identifies the type of the text to
     * append. The accepted values are:
     * <ul>
     *   <li><i>SystemManagement.LOG_TEXT_TYPE</i>, used to append the passed
     *       text to the Log File and to the Standard Output of the ATT
     *       interface;</li>
     *   <li><i>SystemManagement.LOG_SEPARATOR_TYPE</i>, used to append a
     *       separator to the Log File. In this case the passed text will be
     *       ignored.</li>
     * </ul>
     * @param text a String that identifies the text to append to the Log File
     * and to the Standard Output of the ATT interface.
     */
    public static void appendToLogAndToInterface(int type, String text) {
        if (type == SystemManagement.LOG_TEXT_TYPE) {
            SystemManagement.appendAndCloseLogFile("[ " + SystemManagement.getExtendedDate() + " " + SystemManagement.getExtendedHour() + " ] " + text);
            // Updates the status bar.
            if (SystemManagement.ATTInterface.equals(SystemManagement.WINDOW_INTERFACE))
                WindowManager.getStatusBar().setText(text.trim());
        }
        if (type == SystemManagement.LOG_SEPARATOR_TYPE)
            SystemManagement.appendAndCloseLogFile(SystemManagement.LOG_SEPARATOR);
    }

    /**
     * Returns the System date in the extended format: YYYY/MM/DD.
     * @return a String that identify the System date in the extended format:
     * YYYY/MM/DD.
     */
    public static String getExtendedDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(SystemManagement.EXTENDED_DATE_FORMAT);
        return sdf.format(calendar.getTime());
    }

    /**
     * Returns the System hour in the extended format: hh:mm:ss:SSSS[AM|PM].
     * @return a String that identify the System hour in the extended format:
     * hh:mm:ss:SSSS[AM|PM].
     */
    public static String getExtendedHour() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(SystemManagement.EXTENDED_HOUR_FORMAT);
        return sdf.format(calendar.getTime());
    }

    /**
     * Returns the System date in the compact format: YYYYMMDD.
     * @return a String that identify the System date in the compact format:
     * YYYYMMDD.
     */
    public static String getCompactDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(SystemManagement.COMPACT_DATE_FORMAT);
        return sdf.format(calendar.getTime());
    }

    /**
     * Returns the System hour in the compact format: [AM|PM]hhmmss.
     * @return a String that identify the System hour in the compact format:
     * [AM|PM]hhmmss.
     */
    public static String getCompactHour() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(SystemManagement.COMPACT_HOUR_FORMAT);
        return sdf.format(calendar.getTime());
    }

    /**
     * Returns the screen width.
     * @return an integer that identifies the screen width.
     */
    public static int getScreenWidth() {
        return (int)SystemManagement.screenSize.getWidth();
    }

    /**
     * Returns the screen height.
     * @return an integer that identifies the screen height.
     */
    public static int getScreenHeight() {
        return (int)SystemManagement.screenSize.getHeight();
    }

    /**
     * Checks if the screen size is lesser than the minimum MEA Screen
     * Requirements.
     * @return true if the screen size respect the minimum MEA Screen
     * Requirements, false otherwise.
     */
    public static Boolean checkScreenSize() {
        if (((int)SystemManagement.screenSize.getWidth() <= SystemManagement.MEA_MINIMUM_WIDTH) && ((int)SystemManagement.screenSize.getHeight() <= SystemManagement.MEA_MINIMUM_HEIGHT))
            return Boolean.FALSE;
        else
            return Boolean.TRUE;
    }

    /**
     * Returns the Text Editor path.
     * @return a String that identifies the path where is situated the Text
     * Editor.
     */
    public static String getTextEditorPath() {
        return SystemManagement.textEditorPath;
    }

    /**
     * Sets the new Text Editor path.
     * @param path a String that identifies the new path of the Text Editor.
     */
    public static void setTextEditorPath(String path) {
        SystemManagement.textEditorPath = path;
    }

    /**
     * Returns the Cygwin Program path.
     * @return a String that identifies the path where is situated Cygwin
     * Program.
     */
    public static String getCygwinPath() {
        return SystemManagement.cygwinPath;
    }

    /**
     * Sets the new Cygwin Program path.
     * @param path a String that identifies the new path of the Cygwin Program.
     */
    public static void setCygwinPath(String path) {
        SystemManagement.cygwinPath = path;
    }

    /**
     * Returns the Generic Program path.
     * @return a String that identifies the path where is situated the Generic
     * Program.
     */
    public static String getGenericProgramPath() {
        return SystemManagement.genericProgramPath;
    }

    /**
     * Sets the new Generic Program path.
     * @param path a String that identifies the new path of the Generic Program.
     */
    public static void setGenericProgramPath(String path) {
        SystemManagement.genericProgramPath = path;
    }

    /**
     * Finalizes the ATT Program when an error occourr. Writes a message to the
     * Standard System Output and to the Log File.
     * @param andExit a Boolean that identifies if the ATT Program will close.
     * If it is equals to true, the ATT Program will close. If it is equals to
     * false, the ATT Program will not close.
     * @param message a String that identifies the message to write to the Log
     * File and to the Standard System Output.
     */
    public static void manageError(Boolean andExit, String message) {
        SystemManagement.console.printf("%1$s%n", "AN ERROR HAS OCCURRED! " + message);
        SystemManagement.appendAndCloseLogFile("AN ERROR HAS OCCURRED! " + message);
        if (andExit)
            System.exit(SystemManagement.ERROR_EXIT_STATUS);
    }

    /**
     * Executes a Program and waits for its execution.
     * @param program an array of Strings that identifies the set of command to
     * execute. Example:
     *      commands[0] = "/bin/sh"<br/>
     *      commands[1] = "-c"<br/>
     *      commands[2] = "/usr/bin/firefox"
     * @return if the program has an output message and/or an error message,
     * returns a array of String types that contains the output stream in
     * String[0] and the error stream in Stream[1]. If the program has not
     * messages in the output stream or in the error stream, returns an empty
     * String. null otherwise.
     */
    public static String[] executeProgramAndWait(String[] program) {
        /*
         * returnedMessage[0] contains the output stream of the program
         * returnedMessage[1] contains the error stream of the program
         */
        String[] returnedMessage = {"", ""};
        Process prc = null;
        // Start the program...
        try {
            prc = Runtime.getRuntime().exec(program);
            int c;
            InputStream processOutputStream = prc.getInputStream();
            while((c = processOutputStream.read()) != -1)
                returnedMessage[0] += (char)c;
            processOutputStream.close();
            InputStream processErrorStream = prc.getErrorStream();
            while((c = processErrorStream.read()) != -1)
                returnedMessage[1] += (char)c;
            processErrorStream.close();
        } catch (IOException ex) {
            SystemManagement.manageError(Boolean.FALSE, "(System Management - Execute Program) Error while executing \"" + program + "\": " + ex.getMessage());
            return null;
        }
        return returnedMessage;
    }

    /**
     * Executes a Program and does not wait for its execution.
     * @param program an array of Strings that identifies the set of command to
     * execute. Example:
     *      commands[0] = "/bin/sh"<br/>
     *      commands[1] = "-c"<br/>
     *      commands[2] = "/usr/bin/firefox"
     * @return a String that identifies the error, null otherwise.
     */
    public static String executeProgramAndDoNotWait(String[] program) {
        // Start the program...
        String returnedMessage = null;
        try {
            Process prc = Runtime.getRuntime().exec(program);
            int c;
            InputStream processErrorStream = prc.getErrorStream();
            if (processErrorStream.available() != 0) {
                returnedMessage = "";
                while((c = processErrorStream.read()) != -1)
                    returnedMessage += (char)c;
                processErrorStream.close();
            }
        } catch (IOException ex) {
            SystemManagement.manageError(Boolean.FALSE, "(System Management - Execute Program) Error while opening \"" + program + "\": " + ex.getMessage());
            return ex.getMessage();
        }
        return returnedMessage;
    }

    /**
     * Returns the Default Toolkit.
     * @return a Toolkit element that identifies the Default Toolkit.
     */
    public static Toolkit getDefaultToolkit() {
        return SystemManagement.defaultToolkit;
    }

    /**
     * Returns the execution mode of the ATT Program.
     * @return a String that identifies the execution mode of the ATT Program.
     */
    public static String getATTInterface() {
        return SystemManagement.ATTInterface;
    }

    /**
     * Sets the execution mode of the ATT Program.
     * @param ATTinterface a String that identifies the execution mode of the
     * ATT Program. The accepted values are:
     * <ul>
     *   <li><i>SystemManagement.WINDOW_INTERFACE</i>, used to identify the
     *       interface based on the Graphical User Interface (GUI);</li>
     *   <li><i>SystemManagement.CONSOLE_INTERFACE</i>, used to identify the
     *       interface based on the Command Line Interface (CLI).</li>
     * </ul>
     */
    public static void setATTInterface(String ATTinterface) {
        SystemManagement.ATTInterface = ATTinterface;
    }

    /**
     * Inits the ATT Program. It sets the execution mode of the Program, creates
     * the Log File, creates the folders used by the program, and checks for the
     * size of the screen.
     * @param ATTInterface a String that identifies the execution mode of the
     * ATT Program. The accepted values are:
     * <ul>
     *   <li><i>SystemManagement.WINDOW_INTERFACE</i>, used to identify the
     *       interface based on the Graphical User Interface (GUI);</li>
     *   <li><i>SystemManagement.CONSOLE_INTERFACE</i>, used to identify the
     *       interface based on the Command Line Interface (CLI).</li>
     * </ul>
     */
    public static void initAutomaticTestingTool(String ATTInterface) {
        // Sets the type of the interface.
        SystemManagement.setATTInterface(ATTInterface);
        SystemManagement.console = System.console();
        if (ATTInterface.equals(SystemManagement.WINDOW_INTERFACE))
            WindowManager.getStatusBar().setText("Setting the Automatic Testing Tool interface to \"" + SystemManagement.getATTInterface() + "\".");
        if (ATTInterface.equals(SystemManagement.CONSOLE_INTERFACE))
            SystemManagement.console.printf("%1$s%n", "Setting the Automatic Testing Tool interface to \"" + SystemManagement.getATTInterface() + "\".");
        // Sets the program folder.
        SystemManagement.setMainWorkingFolder(System.getProperty("user.dir") + File.separator);
        if (ATTInterface.equals(SystemManagement.WINDOW_INTERFACE))
            WindowManager.getStatusBar().setText("Setting the main working folder to \"" + SystemManagement.getMainWorkingFolder() + "\".");
        if (ATTInterface.equals(SystemManagement.CONSOLE_INTERFACE))
            SystemManagement.console.printf("%1$s%n", "Setting the main working folder to \"" + SystemManagement.getMainWorkingFolder() + "\".");
        // Creates the LOG folder.
        if (ATTInterface.equals(SystemManagement.WINDOW_INTERFACE))
            WindowManager.getStatusBar().setText("Creating the log folder.");
        if (ATTInterface.equals(SystemManagement.CONSOLE_INTERFACE))
            SystemManagement.console.printf("%1$s%n", "Creating the log folder.");
        // Log folder.
        SystemManagement.setLogWorkingFolder(SystemManagement.getMainWorkingFolder() + SystemManagement.LOG_FOLDER_PATH);
        if (!SystemManagement.createFolder(SystemManagement.getLogWorkingFolder())) {
            if (ATTInterface.equals(SystemManagement.WINDOW_INTERFACE))
                JOptionPane.showMessageDialog(WindowManager.getMainFrame(), "Error while creating the \"" + SystemManagement.getLogWorkingFolder() + "\" folder.", SystemManagement.getATTInterface() + " Manager - Init the Automatic Testing Tool", JOptionPane.ERROR_MESSAGE);
            if (ATTInterface.equals(SystemManagement.CONSOLE_INTERFACE))
                SystemManagement.console.printf("%1$s%n", "Error while creating the \"" + SystemManagement.getLogWorkingFolder() + "\" folder.");
            System.exit(SystemManagement.ERROR_EXIT_STATUS);
        }
        // Creates the LOG file.
        if (ATTInterface.equals(SystemManagement.WINDOW_INTERFACE))
            WindowManager.getStatusBar().setText("Creating the log file.");
        if (ATTInterface.equals(SystemManagement.CONSOLE_INTERFACE))
            SystemManagement.console.printf("%1$s%n", "Creating the log file.");
        SystemManagement.createAndCloseLogFile(SystemManagement.LOG_FOLDER_PATH + "log_" + SystemManagement.getCompactDate() + "_" + SystemManagement.getCompactHour() + ".log");
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Log file \"" + SystemManagement.getLogFilename() + "\" created in the \"" + SystemManagement.getLogWorkingFolder() + "\" folder.");
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_SEPARATOR_TYPE, null);
        // Log file created: starting writing to Log file.
        if (ATTInterface.equals(SystemManagement.CONSOLE_INTERFACE))
            SystemManagement.console.printf("%1$s%n", "Log File: \"" + SystemManagement.getLogWorkingFolder() + SystemManagement.getLogFilename() + "\"");
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Automatic Testing Tool Interface:\t" + SystemManagement.getATTInterface());
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_SEPARATOR_TYPE, null);
        if (ATTInterface.equals(SystemManagement.CONSOLE_INTERFACE)) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Showing information about the ATT Program.");
            SystemManagement.console.printf("%1$s%n", "Welcome to the Console Interface of the Automatic Testing Tool Program! This software has the purpose of automate Testing Procedures for General-Purpose Software Applications.");
            SystemManagement.console.printf("%1$s%n", "ATT version: 1.0 (release 20101209fr)");
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_SEPARATOR_TYPE, null);
        }
        // Checks the screen size.
        if (SystemManagement.checkScreenSize() == false) {
            if (ATTInterface.equals(SystemManagement.WINDOW_INTERFACE))
                JOptionPane.showMessageDialog(WindowManager.getMainFrame(), "The dimensions of Your screen are smaller than the MEA minimum requirements.", SystemManagement.getATTInterface() + " Manager - Init the Automatic Testing Tool", JOptionPane.WARNING_MESSAGE);
            if (ATTInterface.equals(SystemManagement.CONSOLE_INTERFACE))
                SystemManagement.console.printf("%1$s%n", "The dimensions of Your screen are smaller than the MEA minimum requirements.");
            SystemManagement.manageError(Boolean.FALSE, "The dimensions of Your screen are smaller than the MEA minimum requirements.");
        }

        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Gathering Information.");
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "  --> System Date:\t\t\t" + SystemManagement.getExtendedDate());
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "  --> System Hour:\t\t\t" + SystemManagement.getExtendedHour());
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "  --> Operating System Name:\t\t" + SystemManagement.getOSName());
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "  --> Operating System Version:\t\t" + SystemManagement.getOSVersion());
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "  --> Operating System Architecture:\t" + SystemManagement.getOSArchitecture());
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_SEPARATOR_TYPE, null);

        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Creating the Automatic Testing Tool folder tree under the \"" + SystemManagement.getMainWorkingFolder() + "\" folder.");
        // Screenshot folder.
        SystemManagement.setScreenshotsWorkingFolder(SystemManagement.getMainWorkingFolder() + SystemManagement.SCREENSHOTS_FOLDER_PATH);
        if (!SystemManagement.createFolder(SystemManagement.getScreenshotsWorkingFolder()))
            SystemManagement.manageError(Boolean.TRUE, "Error while creating the \"" + SystemManagement.getScreenshotsWorkingFolder() + "\" folder.");
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "  --> Created \"" + SystemManagement.getScreenshotsWorkingFolder() + "\".");
        // Screenshot execution folder.
        SystemManagement.setScreenshotsExecutionWorkingFolder(SystemManagement.getMainWorkingFolder() + SystemManagement.SCREENSHOTS_EXECUTION_FOLDER_PATH);
        if (!SystemManagement.createFolder(SystemManagement.getScreenshotsExecutionWorkingFolder()))
            SystemManagement.manageError(Boolean.TRUE, "Error while creating the \"" + SystemManagement.getScreenshotsExecutionWorkingFolder() + "\" folder.");
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "      --> Created the folders under \"" + SystemManagement.getScreenshotsWorkingFolder() + "\".");
        // Scripts folder
        SystemManagement.setScriptsWorkingFolder(SystemManagement.getMainWorkingFolder() + SystemManagement.SCRIPTS_FOLDER_PATH);
        if (!SystemManagement.createFolder(SystemManagement.getScriptsWorkingFolder()))
            SystemManagement.manageError(Boolean.TRUE, "Error while creating the \"" + SystemManagement.getScriptsWorkingFolder() + "\" folder.");
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "  --> Created \"" + SystemManagement.getScriptsWorkingFolder() + "\".");
        // Tests folder.
        SystemManagement.setTestsWorkingFolder(SystemManagement.getMainWorkingFolder() + SystemManagement.TESTS_FOLDER_PATH);
        if (!SystemManagement.createFolder(SystemManagement.getTestsWorkingFolder()))
            SystemManagement.manageError(Boolean.TRUE, "Error while creating the \"" + SystemManagement.getTestsWorkingFolder() + "\" folder.");
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "  --> Created \"" + SystemManagement.getTestsWorkingFolder() + "\".");
        // Results folder.
        SystemManagement.setResultsWorkingFolder(SystemManagement.getMainWorkingFolder() + SystemManagement.RESULTS_FOLDER_PATH);
        if (!SystemManagement.createFolder(SystemManagement.getResultsWorkingFolder()))
            SystemManagement.manageError(Boolean.TRUE, "Error while creating the \"" + SystemManagement.getResultsWorkingFolder() + "\" folder.");
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "  --> Created \"" + SystemManagement.getResultsWorkingFolder() + "\".");
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_SEPARATOR_TYPE, null);

        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Starting the Automatic Testing Tool.");
        if (ATTInterface.equals(SystemManagement.CONSOLE_INTERFACE)) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Automatic Testing Tool started.");
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_SEPARATOR_TYPE, null);
        }
    }

    /**
     * Returns the Console Interface.
     * @return a Console element that identifies the Console Interface.
     */
    public static Console getConsole() {
        return SystemManagement.console;
    }

    /**
     * Returns the "main" absolute path where the ATT Program is working.
     * @return a String that identifies the "main" absolute path where the ATT 
     * Program is working.
     */
    public static String getMainWorkingFolder() {
        return SystemManagement.mainWorkingFolder;
    }

    /**
     * Returns the "log" absolute path where the ATT Program is working.
     * @return a String that identifies the "log" absolute path where the ATT
     * Program is working.
     */
    public static String getLogWorkingFolder() {
        return SystemManagement.logWorkingFolder;
    }

    /**
     * Returns the "results" absolute path where the ATT Program is working.
     * @return a String that identifies the "results" absolute path where the
     * ATT Program is working.
     */
    public static String getResultsWorkingFolder() {
        return SystemManagement.resultsWorkingFolder;
    }

    /**
     * Returns the "screenshots" absolute path where the ATT Program is working.
     * @return a String that identifies the "screenshots" absolute path where
     * the ATT Program is working.
     */
    public static String getScreenshotsWorkingFolder() {
        return SystemManagement.screenshotsWorkingFolder;
    }

    /**
     * Returns the "execution" absolute path where the ATT Program is working.
     * @return a String that identifies the "execution" absolute path where the
     * ATT Program is working.
     */
    public static String getScreenshotsExecutionWorkingFolder() {
        return SystemManagement.screenshotsExecutionWorkingFolder;
    }

    /**
     * Returns the "scripts" absolute path where the ATT Program is working.
     * @return a String that identifies the "scripts" absolute path where the ATT
     * Program is working.
     */
    public static String getScriptsWorkingFolder() {
        return SystemManagement.scriptsWorkingFolder;
    }

    /**
     * Returns the "tests" absolute path where the ATT Program is working.
     * @return a String that identifies the "scripts" absolute path where the
     * ATT Program is working.
     */
    public static String getTestsWorkingFolder() {
        return SystemManagement.testsWorkingFolder;
    }

    /**
     * Sets the "main" absolute path where the ATT Program will work.
     * @param path a String that identifies the "main" absolute path where the
     * ATT Program will work.
     */
    public static void setMainWorkingFolder(String path) {
        SystemManagement.mainWorkingFolder = path;
    }

    /**
     * Sets the "log" absolute path where the ATT Program will work.
     * @param path a String that identifies the "log" absolute path where the
     * ATT Program will work.
     */
    public static void setLogWorkingFolder(String path) {
        SystemManagement.logWorkingFolder = path;
    }

    /**
     * Sets the "results" absolute path where the ATT Program will work.
     * @param path a String that identifies the "results" absolute path where the
     * ATT Program will work.
     */
    public static void setResultsWorkingFolder(String path) {
        SystemManagement.resultsWorkingFolder = path;
    }

    /**
     * Sets the "screenshots" absolute path where the ATT Program will work.
     * @param path a String that identifies the "screenshots" absolute path where the
     * ATT Program will work.
     */
    public static void setScreenshotsWorkingFolder(String path) {
        SystemManagement.screenshotsWorkingFolder = path;
    }

    /**
     * Sets the "execution" absolute path where the ATT Program will work.
     * @param path a String that identifies the "execution" absolute path where the
     * ATT Program will work.
     */
    public static void setScreenshotsExecutionWorkingFolder(String path) {
        SystemManagement.screenshotsExecutionWorkingFolder = path;
    }

    /**
     * Sets the "scripts" absolute path where the ATT Program will work.
     * @param path a String that identifies the "scripts" absolute path where the
     * ATT Program will work.
     */
    public static void setScriptsWorkingFolder(String path) {
        SystemManagement.scriptsWorkingFolder = path;
    }

    /**
     * Sets the "tests" absolute path where the ATT Program will work.
     * @param path a String that identifies the "tests" absolute path where the
     * ATT Program will work.
     */
    public static void setTestsWorkingFolder(String path) {
        SystemManagement.testsWorkingFolder = path;
    }

    /**
     * Allows to create a new Project or open an existing Project.
     * @param createOrOpenProject a String that identifies the mode to manage
     * the Project. The accepted values are:
     * <ul>
     *   <li><i>SystemManagement.CREATE_PROJECT_MODE</i>, used to identify the
     *       "Create Project" mode;</li>
     *   <li><i>SystemManagement.OPEN_PROJECT_MODE</i>, used to identify the
     *       "Open Project" mode.</li>
     * </ul>
     * @param projectPath a String that identifies the path of the Project. It
     * must contain the path and the name of the Project.
     */
    public static void manageProject(String createOrOpenProject, String projectPath) {
        SystemManagement.usingProject = Boolean.TRUE;
        // Main Project Folder.
        if (createOrOpenProject.equals(SystemManagement.CREATE_PROJECT_MODE))
            if (!SystemManagement.createFolder(projectPath))
                SystemManagement.manageError(Boolean.TRUE, "(Create Project) Error while creating the \"" + projectPath + "\" folder.");
        SystemManagement.setMainWorkingFolder(projectPath);
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(" + createOrOpenProject + " Project) Project Path: \"" + SystemManagement.getMainWorkingFolder() + "\".");
        // Log Project Folder.
        // Does not set the Project Log Path.
        if (createOrOpenProject.equals(SystemManagement.CREATE_PROJECT_MODE))
            if (!SystemManagement.createFolder(SystemManagement.getMainWorkingFolder() + SystemManagement.LOG_FOLDER_PATH))
                SystemManagement.manageError(Boolean.TRUE, "(Create Project) Error while creating the \"" + SystemManagement.getMainWorkingFolder() + SystemManagement.LOG_FOLDER_PATH + "\" folder.");
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(" + createOrOpenProject + " Project) Project Log Path: \"" + SystemManagement.getMainWorkingFolder() + SystemManagement.LOG_FOLDER_PATH + "\".");
        // Results Project Folder.
        SystemManagement.setResultsWorkingFolder(SystemManagement.getMainWorkingFolder() + SystemManagement.RESULTS_FOLDER_PATH);
        if (createOrOpenProject.equals(SystemManagement.CREATE_PROJECT_MODE))
            if (!SystemManagement.createFolder(SystemManagement.getResultsWorkingFolder()))
                SystemManagement.manageError(Boolean.TRUE, "(Create Project) Error while creating the \"" + SystemManagement.getResultsWorkingFolder() + "\" folder.");
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(" + createOrOpenProject + " Project) Project Results Path: \"" + SystemManagement.getResultsWorkingFolder() + "\".");
        // Screenshots Project Folder.
        SystemManagement.setScreenshotsWorkingFolder(SystemManagement.getMainWorkingFolder() + SystemManagement.SCREENSHOTS_FOLDER_PATH);
        if (createOrOpenProject.equals(SystemManagement.CREATE_PROJECT_MODE))
            if (!SystemManagement.createFolder(SystemManagement.getScreenshotsWorkingFolder()))
                SystemManagement.manageError(Boolean.TRUE, "(Create Project) Error while creating the \"" + SystemManagement.getScreenshotsWorkingFolder() + "\" folder.");
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(" + createOrOpenProject + " Project) Project Screenshot Path: \"" + SystemManagement.getScreenshotsWorkingFolder() + "\".");
        // Screenshots Execution Project Folder.
        SystemManagement.setScreenshotsExecutionWorkingFolder(SystemManagement.getMainWorkingFolder() + SystemManagement.SCREENSHOTS_EXECUTION_FOLDER_PATH);
        if (createOrOpenProject.equals(SystemManagement.CREATE_PROJECT_MODE))
            if (!SystemManagement.createFolder(SystemManagement.getScreenshotsExecutionWorkingFolder()))
                SystemManagement.manageError(Boolean.TRUE, "(Create Project) Error while creating the \"" + SystemManagement.getScreenshotsExecutionWorkingFolder() + "\" folder.");
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(" + createOrOpenProject + " Project) Project Screenshot Execution Path: \"" + SystemManagement.getScreenshotsExecutionWorkingFolder() + "\".");
        // Scripts Project Folder.
        SystemManagement.setScriptsWorkingFolder(SystemManagement.getMainWorkingFolder() + SystemManagement.SCRIPTS_FOLDER_PATH);
        if (createOrOpenProject.equals(SystemManagement.CREATE_PROJECT_MODE))
            if (!SystemManagement.createFolder(SystemManagement.getScriptsWorkingFolder()))
                SystemManagement.manageError(Boolean.TRUE, "(Create Project) Error while creating the \"" + SystemManagement.getScriptsWorkingFolder() + "\" folder.");
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(" + createOrOpenProject + " Project) Project Scripts Path: \"" + SystemManagement.getScriptsWorkingFolder() + "\".");
        // Tests Project Folder.
        SystemManagement.setTestsWorkingFolder(SystemManagement.getMainWorkingFolder() + SystemManagement.TESTS_FOLDER_PATH);
        if (createOrOpenProject.equals(SystemManagement.CREATE_PROJECT_MODE))
            if (!SystemManagement.createFolder(SystemManagement.getTestsWorkingFolder()))
                SystemManagement.manageError(Boolean.TRUE, "(Create Project) Error while creating the \"" + SystemManagement.getTestsWorkingFolder() + "\" folder.");
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(" + createOrOpenProject + " Project) Project Tests Path: \"" + SystemManagement.getTestsWorkingFolder() + "\".");
    }

    /**
     * Removes all the files from the "execution" folder under the "screenshot"
     * folder of the Projcet.
     */
    public static void emptyExecutionFolder() {
        File executionFolder = new File(SystemManagement.getScreenshotsExecutionWorkingFolder());
        File[] filesToDelete = executionFolder.listFiles();
        for (int i = 0; i < filesToDelete.length; i++) {
            if (filesToDelete[i].isFile() && filesToDelete[i].getName().contains(".png")) {
                filesToDelete[i].delete();
            }
        }
    }
}