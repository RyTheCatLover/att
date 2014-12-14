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
 * Filename         ResultsValidator.java
 * Created on       2010-09-30
 * Last modified on 2014-12-09
 */
package it.sergioferraresi.att.ui;

import it.sergioferraresi.att.SystemManagement;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Provides the grafical validation for reports files (XTR files). It provides
 * a JPanel where is painted the screenshot to check. It uses the mouse buttons
 * to set the new status of the screenshot to "Accepted", "Pending", or
 * "Rejected".
 * 
 * @author  Sergio Ferraresi (psf563)
 * @version 1.0 (release 20101209fr)
 */
public class ResultsValidator extends JPanel implements MouseListener {
    /**
     * A String that identifies the "Accepted" status for a screenshot.
     */
    public static final String ACCEPTED_STATUS = "Accepted";
    /**
     * A String that identifies the "Pending" status for a screenshot. This
     * means that the screenshot needs to be checked in the future.
     */
    public static final String PENDING_STATUS = "Pending";
    /**
     * A String that identifies the "Rejected" status for a screenshot.
     */
    public static final String REJECTED_STATUS = "Rejected";
    private static final long serialVersionUID = 1L;
    /**
     * An array that identifies the screenshots, and their details. It is used
     * to store screenshots temporally, because they need to be checked.
     */
    private ArrayList<ScreenshotDetails> sdArray;
    /**
     * A Boolean that identifies if the report file is a Test Suite (TRUE), or
     * a Test Case (FALSE).
     */
    private Boolean isTS;
    /**
     * A Boolean that identifies if the status of the test is equals to "Error".
     */
    private Boolean isErrorStatus;
    /**
     * A File that identifies the report file.
     */
    private File resultsFile;
    /**
     * The variables used for the DoubleBuffer.
     */
    private Graphics offscreen;
    private Image virtualBuffer;
    /**
     * An Image that identifies the image of the screenshot to check.
     */
    private Image image;
    /**
     * An integer that identifies the index of array of screenshots to check.
     */
    private int SDArrayIndex;
    /**
     * A String that identifies the name of the Test Case.
     */
    private String testCaseName;

    /**
     * Initializes the ResultsValidator. Needs the name of the Test Case, and of
     * the Test Suite (if the Test Case is a part of a Test Suite).
     * @param tsName a String that identifies the name of the Test Suite if the
     * Test Case is not standalone, otherwise null.
     * @param tcName a String that identifies the name of the Test Case.
     */
    public ResultsValidator(String tsName, String tcName) {
        this.isErrorStatus = Boolean.FALSE;
        this.SDArrayIndex = 0;
        this.sdArray = new ArrayList<ScreenshotDetails>();
        /*
         * Validation after the execution. The name does not contains the path.
         * Gets the information from the info file.
         */
        if ((tsName != null) && !tsName.contains("results")) {
            String infoFilePath = null;
            try {
                BufferedReader br = null;
                br = new BufferedReader(new FileReader(new File(SystemManagement.getResultsWorkingFolder() + tsName + File.separator + "test.info")));
                infoFilePath = br.readLine();
                br.close();
            } catch (FileNotFoundException e) {
                SystemManagement.manageError(Boolean.TRUE, "(Results Validator) Error while looking for the info file for the Test Case: " + e.getMessage());
            } catch (IOException e) {
                SystemManagement.manageError(Boolean.TRUE, "(Results Validator) Error while opening the info file for the Test Case: " + e.getMessage());
            }
            // Gets the name of the file to open.
            this.resultsFile = new File(infoFilePath + tsName + SystemManagement.XTR_TYPE);
            this.testCaseName = tcName;
            this.isTS = Boolean.TRUE;
        }
        if ((tsName == null) && !tcName.contains("results")) {
            String infoFilePath = null;
            try {
                BufferedReader br = null;
                br = new BufferedReader(new FileReader(new File(SystemManagement.getResultsWorkingFolder() + tcName + File.separator + "test.info")));
                infoFilePath = br.readLine();
                br.close();
            } catch (FileNotFoundException e) {
                SystemManagement.manageError(Boolean.TRUE, "(Results Validator) Error while looking for the info file for the Test Case: " + e.getMessage());
            } catch (IOException e) {
                SystemManagement.manageError(Boolean.TRUE, "(Results Validator) Error while opening the info file for the Test Case: " + e.getMessage());
            }
            // Gets the name of the file to open.
            this.resultsFile = new File(infoFilePath + tcName + SystemManagement.XTR_TYPE);
            this.testCaseName = null;
            this.isTS = Boolean.FALSE;
        }
        /*
         * Valisdation indipendent from the execution. The name contains the
         * path. Gets the name of the file to open.
         */
        if ((tsName != null) && tsName.contains("results")) {
           this.resultsFile = new File(tsName);
           this.testCaseName = tcName;
           this.isTS = Boolean.TRUE;
           JOptionPane.showMessageDialog(WindowManager.getResultsValidatorFrame(), "<html><head></head><body><p>Test Suite Name: " + this.resultsFile.getName() + "<br/>Test Case Name: " + tcName + SystemManagement.XTR_TYPE + "</p></body></html>", "Results Validator - Test Suite Information", JOptionPane.INFORMATION_MESSAGE);
        }
        if ((tsName == null) && tcName.contains("results")) {
           this.resultsFile = new File(tcName);
           this.testCaseName = null;
           this.isTS = Boolean.FALSE;
        }
        // Gets the name of the file to open.
        if (this.isTS) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Results Validator - " + this.resultsFile.getName() + ") Starting the Results Validator for the Test Case \"" + tcName + "\" of the result file: \"" + this.resultsFile.getAbsolutePath() + "\".");
            try {
                // Useful to open the result file.
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(this.resultsFile);
                doc.getDocumentElement().normalize();
                /**
                 * Gets the status nodes. If the status node is equals to
                 * "ERROR" it is possible to validate the screenshots but the
                 * status will not change.
                 */
                NodeList statusNodes = doc.getElementsByTagName("status");
                if (statusNodes.item(0).getChildNodes().item(0).getNodeValue().equals(SystemManagement.ERROR_STATUS)) {
                    JOptionPane.showMessageDialog(WindowManager.getResultsValidatorFrame(), "<html><head></head><body><p>The Test Suite has the \"" + SystemManagement.ERROR_STATUS + "\" status but it has screenshots to verify. At the end of the validation, the status of the Test Suite will NOT change.</p></body></html>", "Results Validator - Test Suite Information", JOptionPane.INFORMATION_MESSAGE);
                    this.isErrorStatus = Boolean.TRUE;
                }
                // Gets the screenshot nodes.
                NodeList testCaseNodes = doc.getElementsByTagName("testCase");
                NodeList screenshotNodes = null;
                Node screenshotToVerifyNode = null;
                String parentTestCaseName = new String();
                for (int i = 0; i < testCaseNodes.getLength(); i++) {
                    parentTestCaseName = testCaseNodes.item(i).getChildNodes().item(1).getChildNodes().item(0).getNodeValue();
                    if (parentTestCaseName.contains(tcName)) {
                        if (testCaseNodes.item(i).getChildNodes().item(5) != null) {
                            screenshotToVerifyNode = testCaseNodes.item(i).getChildNodes().item(5);
                            screenshotNodes = screenshotToVerifyNode.getChildNodes();
                        }
                    }
                }
                int tmpIndex = 0;
                if ((screenshotNodes != null) && (screenshotNodes.getLength() != 0)) {
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Results Validator - " + this.resultsFile.getAbsolutePath() + ") Gathering information about the screenshots to check.");
                    for (int i = 0; i < screenshotNodes.getLength(); i++) {
                        if (!screenshotNodes.item(i).getNodeName().equals("#text")) {
                            NodeList childNodes = screenshotNodes.item(i).getChildNodes();
                            String stepId = childNodes.item(1).getChildNodes().item(0).getNodeValue();
                            String path = childNodes.item(3).getChildNodes().item(0).getNodeValue();
                            String details = childNodes.item(5).getChildNodes().item(0).getNodeValue();
                            String onRevision = childNodes.item(7).getChildNodes().item(0).getNodeValue();
                            ScreenshotDetails tmp = new ScreenshotDetails(parentTestCaseName, Integer.parseInt(stepId), path, details, onRevision);
                            // We need to check only if the path is the same.
                            if (!this.sdArray.contains(tmp))
                                this.sdArray.add(tmpIndex++, tmp);
                        }
                    }
                    this.updateImageFromResultsFile();
                } else
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Results Validator - " + this.resultsFile.getAbsolutePath() + ") There are not screenshots to check.");
            } catch (ParserConfigurationException ee) {
                SystemManagement.manageError(Boolean.TRUE, "(Results Validator - " + this.resultsFile.getName() + ") The results file \"" + this.resultsFile.getAbsolutePath() + "\" is not a valid results file: " + ee.getMessage());
            } catch (SAXException ee) {
                SystemManagement.manageError(Boolean.TRUE, "(Results Validator - " + this.resultsFile.getName() + ") The results file \"" + this.resultsFile.getAbsolutePath() + "\" is not a valid results file: " + ee.getMessage());
            } catch (IOException ee) {
                SystemManagement.manageError(Boolean.TRUE, "(Results Validator - " + this.resultsFile.getName() + ") The results file \"" + this.resultsFile.getAbsolutePath() + "\" is not a valid results file: " + ee.getMessage());
            }
        } else {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Results Validator - " + this.resultsFile.getName() + ") Starting the Results Validator for the result file: \"" + this.resultsFile.getAbsolutePath() + "\".");
            try {
                // Useful to open the result file.
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(this.resultsFile);
                doc.getDocumentElement().normalize();
                /**
                 * Gets the status node. If the status node is equals to
                 * "ERROR" it is possible to validate the screenshots but the
                 * status will not change.
                 */
                NodeList statusNodes = doc.getElementsByTagName("status");
                if (statusNodes.item(0).getChildNodes().item(0).getNodeValue().equals(SystemManagement.ERROR_STATUS)) {
                    JOptionPane.showMessageDialog(WindowManager.getResultsValidatorFrame(), "<html><head></head><body><p>The Test Case has the \"" + SystemManagement.ERROR_STATUS + "\" status but it has screenshots to verify. At the end of the validation, the status of the Test Case will NOT change.</p></body></html>", "Results Validator - Test Case Information", JOptionPane.INFORMATION_MESSAGE);
                    this.isErrorStatus = Boolean.TRUE;
                }
                // Gets the screenshot nodes.
                NodeList screenshotNodes = doc.getElementsByTagName("screenshot");
                int tmpIndex = 0;
                if (screenshotNodes.getLength() != 0) {
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Results Validator - " + this.resultsFile.getAbsolutePath() + ") Gathering information about the screenshots to check.");
                    for (int i = 0; i < screenshotNodes.getLength(); i++) {
                        Node screenshotToVerifyNode = screenshotNodes.item(i).getParentNode();
                        String parentTestCaseName = screenshotToVerifyNode.getParentNode().getChildNodes().item(1).getChildNodes().item(0).getNodeValue();
                        NodeList childNodes = screenshotNodes.item(i).getChildNodes();
                        String stepId = childNodes.item(1).getChildNodes().item(0).getNodeValue();
                        String path = childNodes.item(3).getChildNodes().item(0).getNodeValue();
                        String details = childNodes.item(5).getChildNodes().item(0).getNodeValue();
                        String onRevision = childNodes.item(7).getChildNodes().item(0).getNodeValue();
                        ScreenshotDetails tmp = new ScreenshotDetails(parentTestCaseName, Integer.parseInt(stepId), path, details, onRevision);
                        // We need to check only if the path is the same.
                        if (!this.sdArray.contains(tmp))
                            this.sdArray.add(tmpIndex++, tmp);
                    }
                    this.updateImageFromResultsFile();
                } else
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Results Validator - " + this.resultsFile.getAbsolutePath() + ") There are not screenshots to check.");
            } catch (ParserConfigurationException ee) {
                SystemManagement.manageError(Boolean.TRUE, "(Results Validator - " + this.resultsFile.getName() + ") The results file \"" + this.resultsFile.getAbsolutePath() + "\" is not a valid results file: " + ee.getMessage());
            } catch (SAXException ee) {
                SystemManagement.manageError(Boolean.TRUE, "(Results Validator - " + this.resultsFile.getName() + ") The results file \"" + this.resultsFile.getAbsolutePath() + "\" is not a valid results file: " + ee.getMessage());
            } catch (IOException ee) {
                SystemManagement.manageError(Boolean.TRUE, "(Results Validator - " + this.resultsFile.getName() + ") The results file \"" + this.resultsFile.getAbsolutePath() + "\" is not a valid results file: " + ee.getMessage());
            }
        }
    }

    /**
     * Updates the JPanel.
     * @param g
     */
    @Override
    public void update(Graphics g) {
        paint(g);
    }

    /**
     * Paints the JPanel.
     * @param g
     */
    @Override
    public void paint(Graphics g) {
        Graphics2D screen2D = (Graphics2D)g;
        this.virtualBuffer = createImage(this.image.getWidth(null), this.image.getHeight(null));
        this.offscreen = this.virtualBuffer.getGraphics();
        this.offscreen.drawImage(this.image, 0, 0, null);
        screen2D.drawImage(this.virtualBuffer, 0, 0, this);
        this.offscreen.dispose();
    }

    /**
     * Allows the User to use the mouse to accept/reject/pend a screenshot.<br/>
     * With a single-left-click, it is possible to accept the screenshot.
     * With a single-right-click, it is possible to reject the screenshot.
     * With a single-center-click (mouse wheel), it is possible to set the
     * pending status for the screenshot.<br/>
     * It also writes the new status in the report file, and updates the
     * screenshot on the JPanel of the ResultsValidator.
     * @param e the MouseEvent.
     */
    public void mouseClicked(MouseEvent e) {
        if ((e.getClickCount() == 1) && (e.getButton() == MouseEvent.BUTTON1)) {
            JOptionPane.showMessageDialog(WindowManager.getResultsValidatorFrame(), "Screenshot accepted.", "Results Validator - Screenshot Window", JOptionPane.INFORMATION_MESSAGE);
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Results Validator - " + this.resultsFile.getName() + ") The screenshot \"" + this.sdArray.get(this.SDArrayIndex - 1).getScreenshotPath() + "\" was accepted: updating the report file.");
            this.writeToXMLAndToArray(ResultsValidator.ACCEPTED_STATUS);
            this.updateImageFromResultsFile();
        }
        if ((e.getClickCount() == 1) && (e.getButton() == MouseEvent.BUTTON2)) {
            JOptionPane.showMessageDialog(WindowManager.getResultsValidatorFrame(), "Screenshot leaved in pending status for future checks.", "Results Validator - Screenshot Window", JOptionPane.INFORMATION_MESSAGE);
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Results Validator - " + this.resultsFile.getName() + ") The screenshot \"" + this.sdArray.get(this.SDArrayIndex - 1).getScreenshotPath() + "\" is in a pending status: updating the report file.");
            this.updateImageFromResultsFile();
        }
        if ((e.getClickCount() == 1) && (e.getButton() == MouseEvent.BUTTON3)) {
            JOptionPane.showMessageDialog(WindowManager.getResultsValidatorFrame(), "Screenshot rejected.", "Results Validator - Screenshot Window", JOptionPane.INFORMATION_MESSAGE);
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Results Validator - " + this.resultsFile.getName() + ") The screenshot \"" + this.sdArray.get(this.SDArrayIndex - 1).getScreenshotPath() + "\" was rejected: updating the report file.");
            this.writeToXMLAndToArray(ResultsValidator.REJECTED_STATUS);
            this.updateImageFromResultsFile();
        }
    }

    /**
     * Not implemented.
     * @param e
     */
    public void mousePressed(MouseEvent e) {}

    /**
     * Not implemented.
     * @param e
     */
    public void mouseReleased(MouseEvent e) {}

    /**
     * Not implemented.
     * @param e
     */
    public void mouseEntered(MouseEvent e) {}

    /**
     * Not implemented.
     * @param e
     */
    public void mouseExited(MouseEvent e) {}

    /**
     * Updates the JPanel of the ResultsValidator with the next screenshot to
     * check.<br/>
     * If there are not screenshots to check, stops the ResultsValidator.
     */
    private void updateImageFromResultsFile() {
        if (!this.sdArray.isEmpty() && (this.SDArrayIndex < this.sdArray.size())) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Results Validator - " + this.resultsFile.getName() + ") Updating the background with the next image.");
            JOptionPane.showMessageDialog(WindowManager.getResultsValidatorFrame(), "<html><head></head><body><h2>Information about the Screenshot</h2><p>Path: " + this.sdArray.get(this.SDArrayIndex).getScreenshotPath() + "<br/>StepId: " + Integer.toString(this.sdArray.get(this.SDArrayIndex).getScreenshotStepId()) + "<br/>Comment: " + this.sdArray.get(this.SDArrayIndex).getScreenshotComment() + "<br/></p></body></html>", "Results Validator", JOptionPane.INFORMATION_MESSAGE);
            String path = this.sdArray.get(this.SDArrayIndex++).getScreenshotPath();
            this.image = new ImageIcon(path).getImage();
            Dimension size = new Dimension(this.image.getWidth(null), this.image.getHeight(null));
            this.setPreferredSize(size);
            this.setMinimumSize(size);
            this.setMaximumSize(size);
            this.setSize(size);
            this.setLayout(null);
            WindowManager.getResultsValidatorFrame().setSize(size);
            WindowManager.getResultsValidatorFrame().setLocation(((SystemManagement.getScreenWidth() - WindowManager.getResultsValidatorFrame().getWidth()) / 2), ((SystemManagement.getScreenHeight() - WindowManager.getResultsValidatorFrame().getHeight()) / 2));
            WindowManager.getResultsValidatorFrame().repaint();
        } else {
            JOptionPane.showMessageDialog(WindowManager.getResultsValidatorFrame(), "There are not images to check: validation fished.", "Results Validator", JOptionPane.INFORMATION_MESSAGE);
            this.updateTestStatus();
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Results Validator - " + this.resultsFile.getName() + ") There are not images to check: validation fished.");
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + this.resultsFile.getName() + ") Stopping the Results Validator.");
            WindowManager.getResultsValidatorFrame().dispose();
            WindowManager.getMainFrame().setState(JFrame.NORMAL);
            // Cleans the System.
            System.gc();
            System.runFinalization();
        }
    }

    /**
     * Writes the new status of the screenshot to the report file.
     * @param stasus
     */
    private void writeToXMLAndToArray(String status) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(this.resultsFile);
            doc.getDocumentElement().normalize();
            // Gets the screenshot nodes.
            NodeList screenshotNodes = doc.getElementsByTagName("screenshot");
            if (screenshotNodes.getLength() != 0)
                for (int i = 0; i < screenshotNodes.getLength(); i++) {
                    NodeList childNodes = screenshotNodes.item(i).getChildNodes();
                    String parentTestCaseName = null;
                    if (this.isTS)
                        parentTestCaseName = screenshotNodes.item(i).getParentNode().getParentNode().getChildNodes().item(1).getChildNodes().item(0).getNodeValue();
                    String path = childNodes.item(3).getChildNodes().item(0).getNodeValue();
                    if (this.isTS) {
                        if (path.equals(this.sdArray.get(this.SDArrayIndex - 1).getScreenshotPath()) && parentTestCaseName.contains(this.testCaseName)) {
                            childNodes.item(7).getChildNodes().item(0).setNodeValue(status);
                            this.sdArray.get(this.SDArrayIndex - 1).setScreenshotOnRevision(status);
                        }
                    } else {
                        if (path.equals(this.sdArray.get(this.SDArrayIndex - 1).getScreenshotPath())) {
                            childNodes.item(7).getChildNodes().item(0).setNodeValue(status);
                            this.sdArray.get(this.SDArrayIndex - 1).setScreenshotOnRevision(status);
                        }
                    }
                }
            // Write the XML Document to File.
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            StreamResult result = new StreamResult(new FileWriter(this.resultsFile, false));
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
            SystemManagement.manageError(Boolean.TRUE, "(Results Validator - " + this.resultsFile.getName() + ") Error while closing the Report File \"" + this.resultsFile.getAbsolutePath() + "\": " + e.getMessage());
        } catch (TransformerException e) {
            SystemManagement.manageError(Boolean.TRUE, "(Results Validator - " + this.resultsFile.getName() + ") Error while closing the Report File \"" + this.resultsFile.getAbsolutePath() + "\": " + e.getMessage());
        } catch (ParserConfigurationException ee) {
            SystemManagement.manageError(Boolean.TRUE, "(Results Validator - " + this.resultsFile.getName() + ") Error while closing the Report File \"" + this.resultsFile.getAbsolutePath() + "\": " + ee.getMessage());
        } catch (SAXException ee) {
            SystemManagement.manageError(Boolean.TRUE, "(Results Validator - " + this.resultsFile.getName() + ") Error while closing the Report File \"" + this.resultsFile.getAbsolutePath() + "\": " + ee.getMessage());
        } catch (IOException e) {
            SystemManagement.manageError(Boolean.TRUE, "(Results Validator - " + this.resultsFile.getName() + ") Error while closing the Report File \"" + this.resultsFile.getAbsolutePath() + "\": " + e.getMessage());
        }
    }

    /**
     * Updates the status of the Test Case, or Test Suite. The rules used are:
     * <ul>
     *   <li>if all the status of the screeshots are setted to "Accepted", the
     *       status of the Test Case is setted to "Pass".<br/>
     *       If the Test Suite is not equals to null, the status of the Test
     *       Suite is setted to "Pass" only if all its Test Cases are passed;</li>
     *   <li>if at least the status of a screenshot of the Test Case is setted
     *       to "Pending", the status of the Test Case is setted to
     *       "Pending".<br/>
     *       If the Test Suite is not equals to null, the status of the Test
     *       Suite is setted to "Pending" if there is a Test Case with the
     *       status setted to "Pending";</li>
     *   <li>if at least the status of a screenshot of the Test Case is setted
     *       to "Rejected", the status of the Test Case is setted to
     *       "Fail".<br/>
     *       If the Test Suite is not equals to null, the status of the Test
     *       Suite is setted to "Fail" if there is a Test Case with the
     *       status setted to "Fail";</li>
     * </ul>
     */
    private void updateTestStatus() {
        /**
         * If the status node of the test is equals to "ERROR", the ATT does not
         * change it.
         */
        if (this.isErrorStatus == Boolean.TRUE) {
            JOptionPane.showMessageDialog(WindowManager.getResultsValidatorFrame(), "Because of the status of the test is equals to \"" + SystemManagement.ERROR_STATUS + "\", it will not change.", "Results Validator", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // What is the new status?
            String newStatus = SystemManagement.PASS_STATUS;
            for (int i = 0; i < this.sdArray.size(); i++) {
                if (!this.sdArray.get(i).getScreenshotOnRevision().equals(ResultsValidator.ACCEPTED_STATUS)) {
                    if (this.sdArray.get(i).getScreenshotOnRevision().equals(ResultsValidator.PENDING_STATUS))
                        newStatus = SystemManagement.PENDING_STATUS;
                    else
                        newStatus = SystemManagement.FAIL_STATUS;
                    break;
                }
            }
            if (this.isTS) {
                try {
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document doc = db.parse(this.resultsFile);
                    doc.getDocumentElement().normalize();
                    // Gets the testCase nodes.
                    NodeList testCaseNodes = doc.getElementsByTagName("testCase");
                    for (int i = 0; i < testCaseNodes.getLength(); i++) {
                        Node thisNode = testCaseNodes.item(i);
                        // Looks for the correct node.
                        if (thisNode.getChildNodes().item(1).getChildNodes().item(0).getNodeValue().contains(this.testCaseName)) {
                            // Writes the new status to the report file.
                            thisNode.getChildNodes().item(3).getChildNodes().item(0).setNodeValue(newStatus);
                        }
                    }
                    /*
                     * What is the new status of the Test Suite?
                     * I need to open the XTR file a check al the status values,
                     * because I could have TC that has not screenshots to
                     * check.
                     */
                    NodeList statusNodes = doc.getElementsByTagName("status");
                    Node tsStatusNode = statusNodes.item(0).getChildNodes().item(0);
                    String tsNewStatus = SystemManagement.PASS_STATUS;
                    for (int i = 1; i < statusNodes.getLength(); i++) {
                        String thisStatusValue = statusNodes.item(i).getChildNodes().item(0).getNodeValue();
                        if (!thisStatusValue.equals(ResultsValidator.ACCEPTED_STATUS)) {
                            if (thisStatusValue.equals(ResultsValidator.PENDING_STATUS))
                                tsNewStatus = SystemManagement.PENDING_STATUS;
                            else
                                tsNewStatus = SystemManagement.FAIL_STATUS;
                            break;
                        }
                    }
                    tsStatusNode.setNodeValue(tsNewStatus);
                    // Writes the XML Document to File.
                    Transformer transformer = TransformerFactory.newInstance().newTransformer();
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                    StreamResult result = new StreamResult(new FileWriter(this.resultsFile, false));
                    DOMSource source = new DOMSource(doc);
                    transformer.transform(source, result);
                } catch (TransformerConfigurationException e) {
                    SystemManagement.manageError(Boolean.TRUE, "(Results Validator - " + this.resultsFile.getName() + ") Error while closing the Report File \"" + this.resultsFile.getAbsolutePath() + "\": " + e.getMessage());
                } catch (TransformerException e) {
                    SystemManagement.manageError(Boolean.TRUE, "(Results Validator - " + this.resultsFile.getName() + ") Error while closing the Report File \"" + this.resultsFile.getAbsolutePath() + "\": " + e.getMessage());
                } catch (ParserConfigurationException ee) {
                    SystemManagement.manageError(Boolean.TRUE, "(Results Validator - " + this.resultsFile.getName() + ") Error while closing the Report File \"" + this.resultsFile.getAbsolutePath() + "\": " + ee.getMessage());
                } catch (SAXException ee) {
                    SystemManagement.manageError(Boolean.TRUE, "(Results Validator - " + this.resultsFile.getName() + ") Error while closing the Report File \"" + this.resultsFile.getAbsolutePath() + "\": " + ee.getMessage());
                } catch (IOException e) {
                    SystemManagement.manageError(Boolean.TRUE, "(Results Validator - " + this.resultsFile.getName() + ") Error while closing the Report File \"" + this.resultsFile.getAbsolutePath() + "\": " + e.getMessage());
                }
            } else {
                try {
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document doc = db.parse(this.resultsFile);
                    doc.getDocumentElement().normalize();
                    // Gets the status nodes.
                    NodeList statusNodes = doc.getElementsByTagName("status");
                    // Writes the new status to the report file.
                    statusNodes.item(0).getChildNodes().item(0).setNodeValue(newStatus);
                    // Writes the XML Document to File.
                    Transformer transformer = TransformerFactory.newInstance().newTransformer();
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                    StreamResult result = new StreamResult(new FileWriter(this.resultsFile, false));
                    DOMSource source = new DOMSource(doc);
                    transformer.transform(source, result);
                } catch (TransformerConfigurationException e) {
                    SystemManagement.manageError(Boolean.TRUE, "(Results Validator - " + this.resultsFile.getName() + ") Error while closing the Report File \"" + this.resultsFile.getAbsolutePath() + "\": " + e.getMessage());
                } catch (TransformerException e) {
                    SystemManagement.manageError(Boolean.TRUE, "(Results Validator - " + this.resultsFile.getName() + ") Error while closing the Report File \"" + this.resultsFile.getAbsolutePath() + "\": " + e.getMessage());
                } catch (ParserConfigurationException ee) {
                    SystemManagement.manageError(Boolean.TRUE, "(Results Validator - " + this.resultsFile.getName() + ") Error while closing the Report File \"" + this.resultsFile.getAbsolutePath() + "\": " + ee.getMessage());
                } catch (SAXException ee) {
                    SystemManagement.manageError(Boolean.TRUE, "(Results Validator - " + this.resultsFile.getName() + ") Error while closing the Report File \"" + this.resultsFile.getAbsolutePath() + "\": " + ee.getMessage());
                } catch (IOException e) {
                    SystemManagement.manageError(Boolean.TRUE, "(Results Validator - " + this.resultsFile.getName() + ") Error while closing the Report File \"" + this.resultsFile.getAbsolutePath() + "\": " + e.getMessage());
                }
            }
        }
    }

    /**
     * This class manages the details of a screenshot.
     */
    private class ScreenshotDetails {
        private int stepId;
        private String comment;
        private String onRevision;
        private String path;
        private String testCaseName;

        /**
         * Initializes all the attributes of the screenshot to null.
         */
        public ScreenshotDetails() {
            this(null, -1, null, null, null);
        }

        /**
         * Initializes the details of the screenshots with those passed.
         * @param testCaseName a String that identifies the name of the Test
         * case of the screenshot.
         * @param stepId an integer that identifies the stepId of the
         * screenshot.
         * @param path a String that identifies the path of the screenshot.
         * @param comment a String that identifies the comment of the 
         * screenshot.
         * @param onRevision a String that identifies the status of the
         * screenshot.
         */
        public ScreenshotDetails(String testCaseName, int stepId, String path, String comment, String onRevision) {
            this.testCaseName = testCaseName;
            this.stepId = stepId;
            this.path = path;
            this.comment = comment;
            this.onRevision = onRevision;
        }

        /**
         * Returns the name of the Test Case of the screenshot.
         * @return a String that identifies the name of the Test Case of the
         * screenshot.
         */
        public String getScreenshotTestCaseName() {
            return this.testCaseName;
        }

        /**
         * Returns the stepId of the screenshot.
         * @return an integer that identifies the stepId of the screenshot.
         */
        public int getScreenshotStepId() {
            return this.stepId;
        }

        /**
         * Returns the path of the screenshot.
         * @return a String that identifies the path of the screenshot.
         */
        public String getScreenshotPath() {
            return this.path;
        }

        /**
         * Returns the comment of the screenshot.
         * @return a String that identifies the comment of the screenshot.
         */
        public String getScreenshotComment() {
            return this.comment;
        }

        /**
         * Returns the status of the screenshot.
         * @return a String that identifies the status of the screenshot.
         */
        public String getScreenshotOnRevision() {
            return this.onRevision;
        }

        /**
         * Sets the name of the Test Case for the screenshot.
         * @param testCaseName a String that identifies the name of the Test
         * Case of the screenshot.
         */
        public void setScreenshotTestCaseName(String testCaseName) {
            this.testCaseName = testCaseName;
        }

        /**
         * Sets the stepId for the screenshot.
         * @param stepId an integer that identifies the stepId of the
         * screenshot.
         */
        public void setScreenshotStepId(int stepId) {
            this.stepId = stepId;
        }

        /**
         * Sets the path for the screenshot.
         * @param path a String that identifies the path of the screenshot.
         */
        public void setScreenshotPath(String path) {
            this.path = path;
        }

        /**
         * Sets the comment for the screenshot.
         * @param comment a String that identifies the comment of the
         * screenshot.
         */
        public void setScreenshotComment(String comment) {
            this.comment = comment;
        }

        /**
         * Sets the status for the screenshot.
         * @param onRevision a String that identifies the status of the
         * screenshot.
         */
        public void setScreenshotOnRevision(String onRevision) {
            this.onRevision = onRevision;
        }

        /**
         * Indicates when two screenshots are equals. Two Screenshot are equals
         * when they comes from the same test case and they have the same path.
         * @param anObject an Object that identifies one of the two screenshots.
         * @return a boolean that identifies if two screenshots are equals
         * (TRUE), or not (FALSE).
         */
        public boolean equals(ScreenshotDetails anObject) {
            return (this.testCaseName.equals(anObject.getScreenshotTestCaseName()) && this.path.equals(anObject.getScreenshotPath()));
        }

        /**
         * Indicates when two screenshots are equals. Two Screenshot are equals
         * when they comes from the same test case and they have the same path.
         * @param anObject an Object that identifies one of the two screenshots.
         * @return a boolean that identifies if two screenshots are equals
         * (TRUE), or not (FALSE).
         */
        @Override
        public boolean equals(Object anObject) {
            return (anObject instanceof ScreenshotDetails)? (this.equals((ScreenshotDetails)anObject)):false;
        }

        /**
         * Calculates the hash code of the screenshot.
         * @return an integer that identifies the hash code of the screenshot.
         */
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 79 * hash + this.stepId;
            hash = 79 * hash + (this.comment != null ? this.comment.hashCode() : 0);
            hash = 79 * hash + (this.onRevision != null ? this.onRevision.hashCode() : 0);
            hash = 79 * hash + (this.path != null ? this.path.hashCode() : 0);
            hash = 79 * hash + (this.testCaseName != null ? this.testCaseName.hashCode() : 0);
            return hash;
        }

        /**
         * Identifies the screenshot.
         * @return a String that identifies the screenshot.
         */
        @Override
        public String toString() {
            return this.testCaseName + " " + this.stepId + " " + this.path + " " + this.comment + " " + this.onRevision;
        }
    }
}