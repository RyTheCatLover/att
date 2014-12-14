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
 * Filename         TestExecutor.java
 * Created on       2010-09-20
 * Last modified on 2014-12-09
 */
package it.sergioferraresi.att;

import it.sergioferraresi.att.ui.ResultsValidator;

import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Provides methods useful to execute a Test Case, or a Test Suite, or a single
 * ATT command.
 * 
 * @author  Sergio Ferraresi (psf563)
 * @version 1.0 (release 20101209fr)
 */
public class TestExecutor {
    /**
     * Identifies an array of Strings that contains the path of the Report
     * Files to check after the execution.
     */
    private static ArrayList<String> testsResults = new ArrayList<String>();
    /**
     * Identifies an array of Files that contains the path of the Screenshots to
     * check after the execution.
     */
    private static ArrayList<File[]> testExecutorScreenshotsToCheck;
    /**
     * An integer that identifies the stepId of the last ATT command executed.
     */
    private static int stepId = -1;
    /**
     * Identifies the Node that contains the list of screenshots to verify.
     */
    private static Node screenshotsToVerifyNode;
    /**
     * Identifies the filename of the Test Case.
     */
    private static String testCaseFilename;
    /**
     * Identifies the Robot.
     */
    private static UserActionSimulator simulator = new UserActionSimulator();



    /**
     * Not implemented.
     */
    private TestExecutor() {}

    /**
     * Returns the array that contains the path of the Report Files.
     * @return an ArrayList that contains the path of the Report Files.
     */
    public static ArrayList<String> getTestsResults() {
        return TestExecutor.testsResults;
    }

    /**
     * Executes a list of Test Case Files or Test Suite Files.
     * @param list an array of Object that contains the list of Test Case Files
     * or Test Suite Files to execute.
     * @return an integer that identifies the exit status for the Test Case
     * execution. The accepted values are:
     * <ul>
     *   <li><i>SystemManagement.PASS_EXIT_STATUS</i>, if the Test Case is
     *       executed and passed without errors;</li>
     *   <li><i>SystemManagement.PENDING_EXIT_STATUS</i>, if the Test Case is
     *       executed, but needs to validate the screenshots for the manual
     *       validation;</li>
     *   <li><i>SystemManagement.FAIL_EXIT_STATUS</i>, if the Test Case is
     *       executed and not passed: needs to validate the not equals
     *       screenshots for the automatic validation;</li>
     *   <li><i>SystemManagement.ERROR_EXIT_STATUS</i>, if the Test Case is not
     *       executed. Error made by the Program.</li>
     * </ul>
     */
    public static int execute(Object[] list) {
        Boolean thereIsAPending = Boolean.FALSE;
        // Opens the correct XTD file.
        for (int i = 0; i < list.length; i++) {
            Boolean isTC = Boolean.FALSE;
            Boolean ATTExecutableFile = Boolean.FALSE;
            String testFilename = list[i].toString();
            File testFile = new File(testFilename);
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor) Cheching if the XTD file exists.");
            if (!testFile.exists()) {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor) The XTD file \"" + testFile.getName() + "\" does not exist.");
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_SEPARATOR_TYPE, null);
                return SystemManagement.FAIL_EXIT_STATUS;
            } else {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor) The XTD file \"" + testFile.getName() + "\" exists.");
                // Is TC or TS?
                try {
                    BufferedReader br = new BufferedReader(new FileReader(testFile));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        if (line.contains("<specName>Automatic Testing Tool</specName>"))
                            ATTExecutableFile = Boolean.TRUE;
                        if (line.contains("<testCaseSteps>") || line.contains("<testCaseSteps/>"))
                            isTC = Boolean.TRUE;
                    }
                } catch (IOException ee) {
                    SystemManagement.manageError(Boolean.FALSE, "(Test Executor) Is a \"Test Suite\" or \"Test Case\" file? " + ee.getMessage());
                    return SystemManagement.ERROR_EXIT_STATUS;
                }
                if (ATTExecutableFile) {
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor) The XTD file is an Automatic Testing Tool executable file.");
                    // XML search.
                    try {
                        // Useful to open the XTD file.
                        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                        DocumentBuilder db = dbf.newDocumentBuilder();
                        Document doc = db.parse(testFile);
                        doc.getDocumentElement().normalize();
                        if (!isTC) {
                            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor) The XTD file is a Test Suite.");
                            /*
                             * Sets the working folder for the Test suite.
                             * If the test is a Test Suite, the ATT Program will
                             * create the folder tree where to save the report
                             * file for the Test Suite and the folder of each 
                             * Test Case.
                             */
                            String tmp1 = SystemManagement.getResultsWorkingFolder() + testFile.getName().substring(0, (testFile.getName().length() - SystemManagement.XTD_TYPE.length())) + File.separator;
                            // Creates the folders for the Test Suite.
                            if (!SystemManagement.createFolder(tmp1)) {
                                SystemManagement.manageError(Boolean.FALSE, "(Test Executor - " + testFile.getAbsolutePath() + ") Error while creating \"" + tmp1 + "\".");
                                return SystemManagement.ERROR_EXIT_STATUS;
                            }
                            String tmp2 = tmp1 + SystemManagement.getCompactDate() + File.separator;
                            // Creates the folders for the Test Suite.
                            if (!SystemManagement.createFolder(tmp2)) {
                                SystemManagement.manageError(Boolean.FALSE, "(Test Executor - " + testFile.getAbsolutePath() + ") Error while creating \"" + tmp2 + "\".");
                                return SystemManagement.ERROR_EXIT_STATUS;
                            }
                            String tsTMPFolderPath = tmp2 + SystemManagement.getCompactHour() + File.separator;
                            // Creates the folders for the Test Suite.
                            if (!SystemManagement.createFolder(tsTMPFolderPath)) {
                                SystemManagement.manageError(Boolean.FALSE, "(Test Executor - " + testFile.getAbsolutePath() + ") Error while creating \"" + tsTMPFolderPath + "\".");
                                return SystemManagement.ERROR_EXIT_STATUS;
                            }
                            /*
                             * Creates the report file in the correct working
                             * folder. This file has the same name of the XTD
                             * file.
                             */
                            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + testFile.getAbsolutePath() + ") Creating the results file named \"" + testFile.getName().substring(0, (testFile.getName().length() - SystemManagement.XTD_TYPE.length())) + ".xml\" in the \"" + tsTMPFolderPath + "\" folder.");
                            SystemManagement.createXMLFile(tsTMPFolderPath, testFile.getName().substring(0, (testFile.getName().length() - SystemManagement.XTD_TYPE.length())), SystemManagement.XTR_TYPE);
                            Node rootNode = SystemManagement.createXMLNode("testsResultsDescription", null);
                            SystemManagement.appendXMLChildToXMLNode(null, rootNode);
                            SystemManagement.appendXMLAttributeToXMLNode(rootNode, SystemManagement.createXMLAttribute("xmlns:xs", "http://www.w3c.org/2001/XMLSchema"));
                            SystemManagement.appendXMLAttributeToXMLNode(rootNode, SystemManagement.createXMLAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance"));
                            SystemManagement.appendXMLChildToXMLNode(rootNode, SystemManagement.createXMLComment("If You want to validate the XTD file with other applications, You should uncomment the following line and remove the previous \">\"."));
                            SystemManagement.appendXMLChildToXMLNode(rootNode, SystemManagement.createXMLComment("xsi:schemaLocation = \"null reportsXMLSchema.xsd\">"));
                            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + testFile.getAbsolutePath() + ") Started the execution of the Test Suite: \"" + testFile.getName() + "\".");

                            /*
                             * Writes the info file in the folder of the Test 
                             * Case.
                             * This file contains the path where the results 
                             * validator can found the XTR file.
                             */
                            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + testFile.getAbsolutePath() + ") Creating the info file for the Test Suite.");
                            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(tmp1 + "test.info"), false));
                            String path = SystemManagement.getXMLPath();
                            bw.write(path, 0, path.length());
                            bw.close();

                            // Test Suite case: gets the Test Cases to execute.
                            NodeList nodeList = doc.getElementsByTagName("externalFile");
                            Boolean isTSPassed = Boolean.TRUE;
                            Boolean atLeastOneTCIsInPendingStatus = Boolean.FALSE;
                            Boolean atLeastOneTCIsInFailStatus = Boolean.FALSE;
                            Boolean atLeastOneTCIsInErrorStatus = Boolean.FALSE;
                            /*
                             * Test Suite Case: adds the "testSuite" Node to
                             * the report file.
                             */
                            Node tsNode = SystemManagement.createXMLNode("testSuite", null);
                            SystemManagement.appendXMLChildToXMLNode(rootNode, tsNode);
                            SystemManagement.appendXMLChildToXMLNode(tsNode, SystemManagement.createXMLNode("name", testFilename));
                            /*
                             * Creates a node list because we need to append the
                             * Test Suite's "status" before append the Test Cases.
                             */
                            ArrayList<Node> tcNodes = new ArrayList<Node>();
                            //for (int j = 0; j < nodeList.getLength(); j++) {
                            int j = 0;
                            int returnedValue = SystemManagement.PASS_EXIT_STATUS;
                            long startTimeTS = Calendar.getInstance().getTimeInMillis();
                            while ((j < nodeList.getLength()) && ((returnedValue == SystemManagement.PASS_EXIT_STATUS) || (returnedValue == SystemManagement.PENDING_EXIT_STATUS))) {
                                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + testFile.getAbsolutePath() + ") Started the execution of the Test Case: \"" + nodeList.item(j).getTextContent() + "\".");

                                /*
                                 * Creates a folder under the Test Suite working
                                 * folder for the screenshot files of the Test
                                 * Case.
                                 */
                                String screenshotsTCFolderPath = tsTMPFolderPath + nodeList.item(j).getTextContent().substring((nodeList.item(j).getTextContent().lastIndexOf(File.separator) + 1), nodeList.item(j).getTextContent().lastIndexOf('.')) + File.separator;
                                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + nodeList.item(j).getTextContent() + ") Creating \"" + screenshotsTCFolderPath + "\".");
                                // Creates the folders for the Test Case.
                                if (!SystemManagement.createFolder(screenshotsTCFolderPath)) {
                                    SystemManagement.manageError(Boolean.FALSE, "(Test Executor - " + nodeList.item(j).getTextContent() + ") Error while creating \"" + screenshotsTCFolderPath + "\".");
                                    return SystemManagement.ERROR_EXIT_STATUS;
                                }
                                String screenshotsTCStaticFolderPath = SystemManagement.getScreenshotsWorkingFolder() + nodeList.item(j).getTextContent().substring((nodeList.item(j).getTextContent().lastIndexOf(File.separator) + 1), nodeList.item(j).getTextContent().lastIndexOf('.')) + File.separator + "staticScreenshots" + File.separator;
                                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + nodeList.item(j).getTextContent() + ") Opening \"" + screenshotsTCStaticFolderPath + "\".");
                                String screenshotsTCTMPDateFolderPath = screenshotsTCFolderPath + SystemManagement.getCompactDate() + File.separator;
                                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + nodeList.item(j).getTextContent() + ") Creating \"" + screenshotsTCTMPDateFolderPath + "\".");
                                if (!SystemManagement.createFolder(screenshotsTCTMPDateFolderPath)) {
                                    SystemManagement.manageError(Boolean.FALSE, "(Test Executor - " + nodeList.item(j).getTextContent() + ") Error while creating \"" + screenshotsTCTMPDateFolderPath + "\".");
                                    return SystemManagement.ERROR_EXIT_STATUS;
                                }
                                String screenshotsTCTMPFolderPath = screenshotsTCTMPDateFolderPath + SystemManagement.getCompactHour() + File.separator;
                                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + nodeList.item(j).getTextContent() + ") Creating \"" + screenshotsTCTMPFolderPath + "\".");
                                if (!SystemManagement.createFolder(screenshotsTCTMPFolderPath)) {
                                    SystemManagement.manageError(Boolean.FALSE, "(Test Executor - " + nodeList.item(j).getTextContent() + ") Error while creating \"" + screenshotsTCTMPFolderPath + "\".");
                                    return SystemManagement.ERROR_EXIT_STATUS;
                                }

                                // Executes the Test Case.
                                TestExecutor.screenshotsToVerifyNode = null;
                                TestExecutor.testExecutorScreenshotsToCheck = new ArrayList<File[]>();
                                long startTimeTC = Calendar.getInstance().getTimeInMillis();
                                returnedValue = TestExecutor.executeTestCase(nodeList.item(j).getTextContent(), screenshotsTCStaticFolderPath, screenshotsTCTMPFolderPath);
                                long currentTimeTC = System.currentTimeMillis();
                                SimpleDateFormat dateFormatTC = new SimpleDateFormat("HH:mm:ss");
                                dateFormatTC.setTimeZone(TimeZone.getTimeZone("GMT"));
                                long elapsedTC = currentTimeTC - startTimeTC;
                                /*
                                 * Creates the XML Node for the report file and
                                 * appends the "name", the "status", and the
                                 * screenshots to check.
                                 */
                                tcNodes.add(j, SystemManagement.createXMLNode("testCase", null));
                                SystemManagement.appendXMLChildToXMLNode(tcNodes.get(j), SystemManagement.createXMLNode("name", nodeList.item(j).getTextContent()));
                                if (returnedValue == SystemManagement.PASS_EXIT_STATUS) {
                                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + nodeList.item(j).getTextContent() + ") Finished the execution of: \"" + nodeList.item(j).getTextContent() + "\". The Test Case is PASSED.");
                                    SystemManagement.appendXMLChildToXMLNode(tcNodes.get(j), SystemManagement.createXMLNode("status", SystemManagement.PASS_STATUS));
                                }
                                if (returnedValue == SystemManagement.PENDING_EXIT_STATUS) {
                                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + nodeList.item(j).getTextContent() + ") Finished the execution of: \"" + nodeList.item(j).getTextContent() + "\". The Test Case is in a PENDING status: there are screenshots to check.");
                                    SystemManagement.appendXMLChildToXMLNode(tcNodes.get(j), SystemManagement.createXMLNode("status", SystemManagement.PENDING_STATUS));
                                    atLeastOneTCIsInPendingStatus = Boolean.TRUE;
                                    TestExecutor.testExecutorScreenshotsToCheck.clear();
                                }
                                if (returnedValue == SystemManagement.FAIL_EXIT_STATUS) {
                                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + nodeList.item(j).getTextContent() + ") Interrupted the execution of: \"" + nodeList.item(j).getTextContent() + "\". The Test Case is NOT passed: probably, there are screenshots to check.");
                                    SystemManagement.appendXMLChildToXMLNode(tcNodes.get(j), SystemManagement.createXMLNode("status", SystemManagement.FAIL_STATUS));
                                    atLeastOneTCIsInFailStatus = Boolean.TRUE;
                                    isTSPassed = Boolean.FALSE;
                                    TestExecutor.testExecutorScreenshotsToCheck.clear();
                                }
                                if (returnedValue == SystemManagement.ERROR_EXIT_STATUS) {
                                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + nodeList.item(j).getTextContent() + ") Interrupted the execution of: \"" + nodeList.item(j).getTextContent() + "\". Error while executing the Test Case.");
                                    SystemManagement.appendXMLChildToXMLNode(tcNodes.get(j), SystemManagement.createXMLNode("status", SystemManagement.ERROR_STATUS));
                                    atLeastOneTCIsInErrorStatus = Boolean.TRUE;
                                    isTSPassed = Boolean.FALSE;
                                    TestExecutor.testExecutorScreenshotsToCheck.clear();
                                }
                                if (TestExecutor.screenshotsToVerifyNode != null)
                                    SystemManagement.appendXMLChildToXMLNode(tcNodes.get(j), TestExecutor.screenshotsToVerifyNode);
                                // Append the elapsedTime node.
                                SystemManagement.appendXMLChildToXMLNode(tcNodes.get(j), SystemManagement.createXMLNode("elapsedTime", String.valueOf(dateFormatTC.format(new Date(elapsedTC)))));
                                j++;
                                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_SEPARATOR_TYPE, null);
                            }
                            long currentTimeTS = System.currentTimeMillis();
                            SimpleDateFormat dateFormatTS = new SimpleDateFormat("HH:mm:ss");
                            dateFormatTS.setTimeZone(TimeZone.getTimeZone("GMT"));
                            long elapsedTS = currentTimeTS - startTimeTS;
                            // Appends the "status" Node to the Test Suite.
                            int value = SystemManagement.ERROR_EXIT_STATUS;
                            if (isTSPassed && !atLeastOneTCIsInPendingStatus) {
                                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + testFile.getAbsolutePath() + ") Finished the execution of: \"" + testFile.getName() + "\". The Test Suite is PASSED.");
                                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_SEPARATOR_TYPE, null);
                                SystemManagement.appendXMLChildToXMLNode(tsNode, SystemManagement.createXMLNode("status", SystemManagement.PASS_STATUS));
                                value = SystemManagement.PASS_EXIT_STATUS;
                            }
                            if (isTSPassed && atLeastOneTCIsInPendingStatus) {
                                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + testFile.getAbsolutePath() + ") Finished the executio of: \"" + testFile.getName() + "\". The Test Suite is in a PENDING status: there are screenshots to check.");
                                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_SEPARATOR_TYPE, null);
                                SystemManagement.appendXMLChildToXMLNode(tsNode, SystemManagement.createXMLNode("status", SystemManagement.PENDING_STATUS));
                                value = SystemManagement.PENDING_EXIT_STATUS;
                                thereIsAPending = Boolean.TRUE;
                            }
                            if (!isTSPassed && atLeastOneTCIsInFailStatus) {
                                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + testFile.getAbsolutePath() + ") Finished the execution of: \"" + testFile.getName() + "\". The Test Suite is NOT passed: probably, there are screenshots to check.");
                                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_SEPARATOR_TYPE, null);
                                SystemManagement.appendXMLChildToXMLNode(tsNode, SystemManagement.createXMLNode("status", SystemManagement.FAIL_STATUS));
                                return SystemManagement.FAIL_EXIT_STATUS;
                            }
                            if (!isTSPassed && atLeastOneTCIsInErrorStatus) {
                                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + testFile.getAbsolutePath() + ") Finished the execution of: \"" + testFile.getName() + "\". Error while executing the Test Suite.");
                                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_SEPARATOR_TYPE, null);
                                SystemManagement.appendXMLChildToXMLNode(tsNode, SystemManagement.createXMLNode("status", SystemManagement.ERROR_STATUS));
                                return SystemManagement.ERROR_EXIT_STATUS;
                            }
                            // Now appends the Test Cases to the Test Suite.
                            for (int k = 0; k < tcNodes.size(); k++)
                                SystemManagement.appendXMLChildToXMLNode(tsNode, tcNodes.get(k));

                            // Appends the elapsedTime node.
                            SystemManagement.appendXMLChildToXMLNode(tsNode, SystemManagement.createXMLNode("elapsedTime", String.valueOf(dateFormatTS.format(new Date(elapsedTS)))));

                            // Closes the report files.
                            SystemManagement.closeXMLFile();
                            TestExecutor.testsResults.add(SystemManagement.getXMLPath() + SystemManagement.getXMLFilename());

                            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_SEPARATOR_TYPE, null);
                        } else {
                            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor) The XTD file is a Test Case.");

                            /*
                             * Creates a folder under "results" folder for the 
                             * screenshot files of the Test Case.
                             */
                            String screenshotsTCFolderPath = SystemManagement.getResultsWorkingFolder() + testFilename.substring((testFilename.lastIndexOf(File.separator) + 1), testFilename.lastIndexOf('.')) + File.separator;
                            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + testFilename + ") Creating \"" + screenshotsTCFolderPath + "\".");
                            // Creates the folders for the Test Case.
                            if (!SystemManagement.createFolder(screenshotsTCFolderPath)) {
                                SystemManagement.manageError(Boolean.FALSE, "(Test Executor - " + testFilename + ") Error while creating \"" + screenshotsTCFolderPath + "\".");
                                return SystemManagement.ERROR_EXIT_STATUS;
                            }
                            String screenshotsTCStaticFolderPath = SystemManagement.getScreenshotsWorkingFolder() + testFilename.substring((testFilename.lastIndexOf(File.separator) + 1), testFilename.lastIndexOf('.')) + File.separator + "staticScreenshots" + File.separator;
                            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + testFilename + ") Opening \"" + screenshotsTCStaticFolderPath + "\".");
                            String screenshotsTCTMPDateFolderPath = screenshotsTCFolderPath + SystemManagement.getCompactDate() + File.separator;
                            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + testFilename + ") Creating \"" + screenshotsTCTMPDateFolderPath + "\".");
                            if (!SystemManagement.createFolder(screenshotsTCTMPDateFolderPath)) {
                                SystemManagement.manageError(Boolean.FALSE, "(Test Executor - " + testFilename + ") Error while creating \"" + screenshotsTCTMPDateFolderPath + "\".");
                                return SystemManagement.ERROR_EXIT_STATUS;
                            }
                            String screenshotsTCTMPFolderPath = screenshotsTCTMPDateFolderPath + SystemManagement.getCompactHour() + File.separator;
                            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + testFilename + ") Creating \"" + screenshotsTCTMPFolderPath + "\".");
                            if (!SystemManagement.createFolder(screenshotsTCTMPFolderPath)) {
                                SystemManagement.manageError(Boolean.FALSE, "(Test Executor - " + testFilename + ") Error while creating \"" + screenshotsTCTMPFolderPath + "\".");
                                return SystemManagement.ERROR_EXIT_STATUS;
                            }

                            /*
                             * Creates the report file in the correct working
                             * folder. This file has the same name of the XTD
                             * file.
                             */
                            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor) Creating the report file named \"" + testFile.getName().substring(0, (testFile.getName().length() - SystemManagement.XTD_TYPE.length())) + ".xml\" in the \"" + screenshotsTCTMPFolderPath + "\" folder.");
                            SystemManagement.createXMLFile(screenshotsTCTMPFolderPath, testFile.getName().substring(0, (testFile.getName().length() - SystemManagement.XTD_TYPE.length())), SystemManagement.XTR_TYPE);
                            Node rootNode = SystemManagement.createXMLNode("testsResultsDescription", null);
                            SystemManagement.appendXMLChildToXMLNode(null, rootNode);
                            SystemManagement.appendXMLAttributeToXMLNode(rootNode, SystemManagement.createXMLAttribute("xmlns:xs", "http://www.w3c.org/2001/XMLSchema"));
                            SystemManagement.appendXMLAttributeToXMLNode(rootNode, SystemManagement.createXMLAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance"));
                            SystemManagement.appendXMLChildToXMLNode(rootNode, SystemManagement.createXMLComment("If You want to validate the XTD file with other applications, You should uncomment the following line and remove the previous \">\"."));
                            SystemManagement.appendXMLChildToXMLNode(rootNode, SystemManagement.createXMLComment("xsi:schemaLocation = \"null reportsXMLSchema.xsd\">"));
                            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor) Started the execution of the Test Case: \"" + testFile.getName() + "\".");

                            /*
                             * Writes the info file in the folder of the Test 
                             * Case.
                             * This file contains the path where the results 
                             * validator can found the XTR file.
                             */
                            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor) Creating the info file for the Test Case.");
                            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(screenshotsTCFolderPath + "test.info"), false));
                            String path = SystemManagement.getXMLPath();
                            bw.write(path, 0, path.length());
                            bw.close();

                            // Test Case case.
                            TestExecutor.screenshotsToVerifyNode = null;
                            TestExecutor.testExecutorScreenshotsToCheck = new ArrayList<File[]>();
                            long startTimeTC = Calendar.getInstance().getTimeInMillis();
                            int returnedValue = TestExecutor.executeTestCase(testFilename, screenshotsTCStaticFolderPath, screenshotsTCTMPFolderPath);
                            long currentTimeTC = System.currentTimeMillis();
                            SimpleDateFormat dateFormatTC = new SimpleDateFormat("HH:mm:ss");
                            dateFormatTC.setTimeZone(TimeZone.getTimeZone("GMT"));
                            long elapsedTC = currentTimeTC - startTimeTC;
                            /*
                             * Test Case case: adds a "testCase" Node to the
                             * report file.
                             */
                            Node tcNode = SystemManagement.createXMLNode("testCase", null);
                            SystemManagement.appendXMLChildToXMLNode(rootNode, tcNode);
                            SystemManagement.appendXMLChildToXMLNode(tcNode, SystemManagement.createXMLNode("name", testFilename));
                            if (returnedValue == SystemManagement.PASS_EXIT_STATUS) {
                                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor) Finished the execution of: \"" + testFilename + "\". Test Case PASSED.");
                                SystemManagement.appendXMLChildToXMLNode(tcNode, SystemManagement.createXMLNode("status", SystemManagement.PASS_STATUS));
                            }
                            if (returnedValue == SystemManagement.PENDING_EXIT_STATUS) {
                                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor) Finished the execution of: \"" + testFilename + "\". Test Case is in PENDING status: there are screenshots to check.");
                                SystemManagement.appendXMLChildToXMLNode(tcNode, SystemManagement.createXMLNode("status", SystemManagement.PENDING_STATUS));
                                TestExecutor.testExecutorScreenshotsToCheck.clear();
                                thereIsAPending = Boolean.TRUE;
                            }
                            if (returnedValue == SystemManagement.FAIL_EXIT_STATUS) {
                                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor) Interrupted the execution of: \"" + testFilename + "\". Test Case NOT passed.");
                                SystemManagement.appendXMLChildToXMLNode(tcNode, SystemManagement.createXMLNode("status", SystemManagement.FAIL_STATUS));
                                TestExecutor.testExecutorScreenshotsToCheck.clear();
                            }
                            if (returnedValue == SystemManagement.ERROR_EXIT_STATUS) {
                                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor) Interrupted the execution of: \"" + testFilename + "\". Test Case NOT passed.");
                                SystemManagement.appendXMLChildToXMLNode(tcNode, SystemManagement.createXMLNode("status", SystemManagement.ERROR_STATUS));
                                TestExecutor.testExecutorScreenshotsToCheck.clear();
                            }
                            if (TestExecutor.screenshotsToVerifyNode != null)
                                SystemManagement.appendXMLChildToXMLNode(tcNode, TestExecutor.screenshotsToVerifyNode);
                            // Appends the elapsedTime node.
                            SystemManagement.appendXMLChildToXMLNode(tcNode, SystemManagement.createXMLNode("elapsedTime", String.valueOf(dateFormatTC.format(new Date(elapsedTC)))));

                            // Closes the rreport files.
                            SystemManagement.closeXMLFile();
                            TestExecutor.testsResults.add(SystemManagement.getXMLPath() + SystemManagement.getXMLFilename());

                            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_SEPARATOR_TYPE, null);
                        }
                    } catch (ParserConfigurationException ee) {
                        SystemManagement.manageError(Boolean.FALSE, "(Test Executor) Is a \"Test Suite\" or \"Test Case\" file? " + ee.getMessage());
                        return SystemManagement.ERROR_EXIT_STATUS;
                    } catch (SAXException ee) {
                        SystemManagement.manageError(Boolean.FALSE, "(Test Executor) Is a \"Test Suite\" or \"Test Case\" file? " + ee.getMessage());
                        return SystemManagement.ERROR_EXIT_STATUS;
                    } catch (IOException ee) {
                        SystemManagement.manageError(Boolean.FALSE, "(Test Executor) Is a \"Test Suite\" or \"Test Case\" file? " + ee.getMessage());
                        return SystemManagement.ERROR_EXIT_STATUS;
                    }
                } else {
                    /*
                     * Creates a folder under "results" folder for the
                     * screenshot files of the Test Case.
                     */
                    String screenshotsTCFolderPath = SystemManagement.getResultsWorkingFolder() + testFilename.substring((testFilename.lastIndexOf(File.separator) + 1), testFilename.lastIndexOf('.')) + File.separator;
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + testFilename + ") Creating \"" + screenshotsTCFolderPath + "\".");
                    // Creates the folders for the Test Case.
                    if (!SystemManagement.createFolder(screenshotsTCFolderPath)) {
                        SystemManagement.manageError(Boolean.FALSE, "(Test Executor - " + testFilename + ") Error while creating \"" + screenshotsTCFolderPath + "\".");
                        return SystemManagement.ERROR_EXIT_STATUS;
                    }
                    String screenshotsTCStaticFolderPath = SystemManagement.getScreenshotsWorkingFolder() + testFilename.substring((testFilename.lastIndexOf(File.separator) + 1), testFilename.lastIndexOf('.')) + File.separator + "staticScreenshots" + File.separator;
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + testFilename + ") Opening \"" + screenshotsTCStaticFolderPath + "\".");
                    String screenshotsTCTMPDateFolderPath = screenshotsTCFolderPath + SystemManagement.getCompactDate() + File.separator;
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + testFilename + ") Creating \"" + screenshotsTCTMPDateFolderPath + "\".");
                    if (!SystemManagement.createFolder(screenshotsTCTMPDateFolderPath)) {
                        SystemManagement.manageError(Boolean.FALSE, "(Test Executor - " + testFilename + ") Error while creating \"" + screenshotsTCTMPDateFolderPath + "\".");
                        return SystemManagement.ERROR_EXIT_STATUS;
                    }
                    String screenshotsTCTMPFolderPath = screenshotsTCTMPDateFolderPath + SystemManagement.getCompactHour() + File.separator;
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + testFilename + ") Creating \"" + screenshotsTCTMPFolderPath + "\".");
                    if (!SystemManagement.createFolder(screenshotsTCTMPFolderPath)) {
                        SystemManagement.manageError(Boolean.FALSE, "(Test Executor - " + testFilename + ") Error while creating \"" + screenshotsTCTMPFolderPath + "\".");
                        return SystemManagement.ERROR_EXIT_STATUS;
                    }

                    /*
                     * Creates the report file in the correct working folder.
                     * This file has the same name of the XTD file.
                     * The report file is created here because we need to insert
                     * also the "not-ATT-executable-files" as fail Test Cases.
                     */
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor) Creating the report file named \"" + testFile.getName().substring(0, (testFile.getName().length() - SystemManagement.XTD_TYPE.length())) + ".xml\" in the \"" + screenshotsTCTMPFolderPath + "\" folder.");
                    SystemManagement.createXMLFile(screenshotsTCTMPFolderPath, testFile.getName().substring(0, (testFile.getName().length() - SystemManagement.XTD_TYPE.length())), SystemManagement.XTR_TYPE);
                    Node rootNode = SystemManagement.createXMLNode("testsResultsDescription", null);
                    SystemManagement.appendXMLChildToXMLNode(null, rootNode);
                    SystemManagement.appendXMLAttributeToXMLNode(rootNode, SystemManagement.createXMLAttribute("xmlns:xs", "http://www.w3c.org/2001/XMLSchema"));
                    SystemManagement.appendXMLAttributeToXMLNode(rootNode, SystemManagement.createXMLAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance"));
                    SystemManagement.appendXMLChildToXMLNode(rootNode, SystemManagement.createXMLComment("If You want to validate the XTD file with other applications, You should uncomment the following line and remove the previous \">\"."));
                    SystemManagement.appendXMLChildToXMLNode(rootNode, SystemManagement.createXMLComment("xsi:schemaLocation = \"null reportsXMLSchema.xsd\">"));
                    Node tcNode = SystemManagement.createXMLNode("testCase", null);
                    SystemManagement.appendXMLChildToXMLNode(rootNode, tcNode);
                    SystemManagement.appendXMLChildToXMLNode(tcNode, SystemManagement.createXMLNode("name", testFilename));
                    SystemManagement.appendXMLChildToXMLNode(tcNode, SystemManagement.createXMLNode("status", SystemManagement.FAIL_STATUS));
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor) The XTD file \"" + testFile.getName() + "\" is not an ATT Executable File: NOT passed.");
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_SEPARATOR_TYPE, null);
                    // Appends the elapsedTime node.
                    SystemManagement.appendXMLChildToXMLNode(tcNode, SystemManagement.createXMLNode("elapsedTime", "00:00:00"));
                    // Closes the report files.
                    SystemManagement.closeXMLFile();
                    TestExecutor.testsResults.add(SystemManagement.getXMLPath() + SystemManagement.getXMLFilename());

                    /*
                     * Writes the info file in the folder of the Test Case.
                     * This file contains the path where the results validator
                     * can found the XTR file.
                     */
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor) Creating the info file for the Test Case.");
                    try {
                        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(screenshotsTCFolderPath + "test.info"), false));
                        String path = SystemManagement.getXMLPath();
                        bw.write(path, 0, path.length());
                        bw.close();
                    } catch (IOException ee) {
                        SystemManagement.manageError(Boolean.FALSE, "(Test Executor) Error while creating the info file for the Test Case. " + ee.getMessage());
                        return SystemManagement.ERROR_EXIT_STATUS;
                    }
                    return SystemManagement.FAIL_EXIT_STATUS;
                }
            }
        }
        if (thereIsAPending)
            return SystemManagement.PENDING_EXIT_STATUS;
        else
            return SystemManagement.PASS_EXIT_STATUS;
    }

    /**
     * Executes a single Test Case File.
     * @param testCaseFilename a String that identifies the the absolute path of
     * the Test Case.
     * @param tcStaticFolderPath a String that identifies the path of the static
     * folder for the Test Case.
     * @param tcTMPFolderPath a String that identifies the path of the temporary
     * folder for the Test Case.
     * @return an integer that identifies the exit status for the Test Case
     * execution. The accepted values are:
     * <ul>
     *   <li><i>SystemManagement.PASS_EXIT_STATUS</i>, if the Test Case is
     *       executed and passed without errors;</li>
     *   <li><i>SystemManagement.PENDING_EXIT_STATUS</i>, if the Test Case is 
     *       executed, but needs to validate the screenshots for the manual
     *       validation;</li>
     *   <li><i>SystemManagement.FAIL_EXIT_STATUS</i>, if the Test Case is
     *       executed and not passed: needs to validate the not equals
     *       screenshots for the automatic validation;</li>
     *   <li><i>SystemManagement.ERROR_EXIT_STATUS</i>, if the Test Case is not
     *       executed. Error made by the Program.</li>
     * </ul>
     */
    public static int executeTestCase(String testCaseFilename, String tcStaticFolderPath, String tcTMPFolderPath) {
        TestExecutor.testCaseFilename = testCaseFilename;
        // Opens the Test Case File.
        File tc = new File(TestExecutor.testCaseFilename);
        // XML search.
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(tc);
            doc.getDocumentElement().normalize();
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + testCaseFilename + ") Retriving the Automatic Testing Tool commands.");
            // Gets all "step" elements.
            NodeList stepList = doc.getElementsByTagName("step");
            // For each "step" element...
            int i = 0;
            Boolean commandWithError = Boolean.FALSE;
            while((i < stepList.getLength()) && !commandWithError) {
                // ...gets child elements.
                NodeList commands = stepList.item(i).getChildNodes();
                for (int j = 0; j < commands.getLength(); j++) {
                    if (!commands.item(j).getNodeName().equals("#text")) {
                        // ...gets the element.
                        Node command = commands.item(j);
                        String screenshotName = TestExecutor.getScreenshotName(doc, command) + ".png";
                        // Executes the command.
                        if (!TestExecutor.executeATTCommand(command, Boolean.FALSE, tcStaticFolderPath, tcTMPFolderPath, screenshotName)) {
                            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + testCaseFilename + ") Error while executing the Automatic Testing Tool command at step \"" + command.getParentNode().getAttributes().getNamedItem("id").getNodeValue() + "\".");
                            commandWithError = Boolean.TRUE;
                        }
                    }
                }
                i++;
            }
            /*
             * Looks for the "screenshotToVerify" Node in the XTD file and
             * appends it to the report file.
             */
            // Gets the "screen" Nodes.
            NodeList screenNodes = doc.getElementsByTagName("screen");
            if ((screenNodes.getLength() != 0) || (!TestExecutor.testExecutorScreenshotsToCheck.isEmpty()))
                TestExecutor.screenshotsToVerifyNode = SystemManagement.createXMLNode("screenshotsToVerify", null);
            if (screenNodes.getLength() != 0) {
                /*
                 * Copyies all the screenshots information from the XTD file to
                 * the report file.
                 */
                for (int j = 0; j < screenNodes.getLength(); j++) {
                    NodeList screenChildNodes = screenNodes.item(j).getChildNodes();
                    String screenshotStepId = screenChildNodes.item(1).getChildNodes().item(0).getNodeValue();
                    String name = screenChildNodes.item(3).getChildNodes().item(0).getNodeValue();
                    String details = screenChildNodes.item(5).getChildNodes().item(0).getNodeValue();
                    /*
                     * Creates the "screenshot" Node for the report file and
                     * appends it to the "screenshotsToVerify" Node.
                     */
                    if (Integer.parseInt(screenshotStepId) <= TestExecutor.stepId) {
                        Node tmp = SystemManagement.createXMLNode("screenshot", null);
                        SystemManagement.appendXMLChildToXMLNode(TestExecutor.screenshotsToVerifyNode, tmp);
                        SystemManagement.appendXMLChildToXMLNode(tmp, SystemManagement.createXMLNode("stepId", screenshotStepId));
                        SystemManagement.appendXMLChildToXMLNode(tmp, SystemManagement.createXMLNode("path", tcTMPFolderPath + name + ".png"));
                        SystemManagement.appendXMLChildToXMLNode(tmp, SystemManagement.createXMLNode("details", details));
                        SystemManagement.appendXMLChildToXMLNode(tmp, SystemManagement.createXMLNode("onRevision", ResultsValidator.PENDING_STATUS));
                    }
                }
            }
            // Takes the screenshots mades by the Test Executer.
            if (!TestExecutor.testExecutorScreenshotsToCheck.isEmpty()) {
                for (int j = 0; j < TestExecutor.testExecutorScreenshotsToCheck.size(); j++) {
                    Node tmp = SystemManagement.createXMLNode("screenshot", null);
                    //SystemManagement.appendXMLChildToXMLNode(TestExecutor.screenshotsToverifyNode, tmp);
                    SystemManagement.appendXMLChildToXMLNode(TestExecutor.screenshotsToVerifyNode, tmp);
                    SystemManagement.appendXMLChildToXMLNode(tmp, SystemManagement.createXMLNode("stepId", "00"));
                    SystemManagement.appendXMLChildToXMLNode(tmp, SystemManagement.createXMLNode("path", TestExecutor.testExecutorScreenshotsToCheck.get(j)[0].getAbsolutePath()));
                    SystemManagement.appendXMLChildToXMLNode(tmp, SystemManagement.createXMLNode("details", "This screenshot is not equals to this one: " + TestExecutor.testExecutorScreenshotsToCheck.get(j)[1].getAbsolutePath()));
                    SystemManagement.appendXMLChildToXMLNode(tmp, SystemManagement.createXMLNode("onRevision", ResultsValidator.PENDING_STATUS));
                }
            }
            if (!commandWithError && TestExecutor.testExecutorScreenshotsToCheck.isEmpty() && (screenNodes.getLength() == 0))
                return SystemManagement.PASS_EXIT_STATUS;
            if (!commandWithError && TestExecutor.testExecutorScreenshotsToCheck.isEmpty() && (screenNodes.getLength() != 0))
                return SystemManagement.PENDING_EXIT_STATUS;
            if (!commandWithError && !TestExecutor.testExecutorScreenshotsToCheck.isEmpty())
                return SystemManagement.FAIL_EXIT_STATUS;
            if (commandWithError)
                return SystemManagement.ERROR_EXIT_STATUS;
        } catch (ParserConfigurationException ee) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + testCaseFilename + ") Error while parsing the XTD file: " + ee.getMessage());
            return SystemManagement.ERROR_EXIT_STATUS;
        } catch (SAXException ee) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + testCaseFilename + ") Error while parsing the XTD file: " + ee.getMessage());
            return SystemManagement.ERROR_EXIT_STATUS;
        } catch (IOException ee) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + testCaseFilename + ") Error while parsing the XTD file: " + ee.getMessage());
            return SystemManagement.ERROR_EXIT_STATUS;
        }
        return SystemManagement.ERROR_EXIT_STATUS;
    }

    /**
     * Executes an ATT command.
     * @param command a Node that identifies the command to execute.
     * @param buildingTestCase a Boolean that identifies if the ATT is building
     * or executing a Test Case.
     * @param screenshotsTestCaseStaticFolder a String that identifies the path
     * of the static folder for the Test Case.
     * @param screenshotsTestCaseTMPFolder a String that identifies the path of
     * the temporary folder for the Test Case.
     * @param screenshotName a String that identifies the name of the
     * screenshot.
     * @return false if an error occour during the execution of the ATT command,
     * true otherwise.
     */
    public static Boolean executeATTCommand(Node command, Boolean buildingTestCase, String screenshotsTestCaseStaticFolder, String screenshotsTestCaseTMPFolder, String screenshotName) {
        Boolean returnedValue = Boolean.TRUE;
        if (buildingTestCase)
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - Test Case Builder) Gathering information about the Automatic Testing Tool command.");
        else
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + TestExecutor.testCaseFilename + ") Gathering information about the Automatic Testing Tool command.");
        // Gets the node name.
        String commandName = command.getNodeName();
        // Gets the command stepId.
        String commandStepId = command.getParentNode().getAttributes().getNamedItem("id").getNodeValue();
        if (Integer.parseInt(commandStepId) != -1)
            TestExecutor.stepId = Integer.parseInt(commandStepId);
        // Gets node value (if possible), otherwise null.
        String commandValue = null;
        if (command.getChildNodes().getLength() != 0)
             commandValue = command.getChildNodes().item(0).getNodeValue();
        // Gets node attributes (if possible).
        NamedNodeMap commandAttributes = null;
        int x = -1;
        int y = -1;
        int startX = -1;
        int startY = -1;
        int endX = -1;
        int endY = -1;
        if (command.hasAttributes()) {
            commandAttributes = command.getAttributes();
            x = (commandAttributes.getNamedItem("x") != null)? Integer.parseInt(commandAttributes.getNamedItem("x").getNodeValue()) : (-1);
            y = (commandAttributes.getNamedItem("y") != null)? Integer.parseInt(commandAttributes.getNamedItem("y").getNodeValue()) : (-1);
            startX = (commandAttributes.getNamedItem("startX") != null)? Integer.parseInt(commandAttributes.getNamedItem("startX").getNodeValue()) : (-1);
            startY = (commandAttributes.getNamedItem("startY") != null)? Integer.parseInt(commandAttributes.getNamedItem("startY").getNodeValue()) : (-1);
            endX = (commandAttributes.getNamedItem("endX") != null)? Integer.parseInt(commandAttributes.getNamedItem("endX").getNodeValue()) : (-1);
            endY = (commandAttributes.getNamedItem("endY") != null)? Integer.parseInt(commandAttributes.getNamedItem("endY").getNodeValue()) : (-1);
            // Needs to draw a correct rectangle.
            if (!commandName.equals("dragAndDrop")) {
                if ((startX != -1) && (endX != -1) && (startX >= endX)) {
                    int tmp = startX;
                    startX = endX;
                    endX = tmp;
                }
                if ((startY != -1) && (endY != -1) && (startY >= endY)) {
                    int tmp = startY;
                    startY = endY;
                    endY = tmp;
                }
            }
        }
        // If command is equals to the "startApplication" ATT's command...
        if (commandName.equals("startApplication")) {
            String[] cmd = null;
            if (SystemManagement.getOSName().contains("Linux") == Boolean.TRUE) {
                cmd = new String[3];
                cmd[0] = "/bin/sh";
                cmd[1] = "-c";
                cmd[2] = commandValue;
            }
            if (SystemManagement.getOSName().contains("Windows") == Boolean.TRUE) {
                cmd = new String[5];
                cmd[0] = "cmd";
                cmd[1] = "/c";
                cmd[2] = "start";
                cmd[3] = "\"\"";
                cmd[4] = "\"" + commandValue + "\"";
            }
            File tmp = new File(commandValue);
            if (tmp.exists()) {
                String returnedMessage = SystemManagement.executeProgramAndDoNotWait(cmd);
                if (returnedMessage == null) {
                    returnedValue = Boolean.TRUE;
                    if (buildingTestCase)
                        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - Test Case Builder) Executing the following Automatic Testing Tool command: \"startApplication\". Application: \"" + commandValue + "\".");
                    else
                        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + TestExecutor.testCaseFilename + ") Executing the following Automatic Testing Tool command: \"startApplication\". Application: \"" + commandValue + "\".");
                } else
                    returnedValue = Boolean.FALSE;
            } else
                returnedValue = Boolean.FALSE;
        }
        // If command is equals to the "moveTo" ATT's command...
        if (commandName.equals("moveTo")) {
            TestExecutor.simulator.moveToXY(x, y);
            if (buildingTestCase)
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - Test Case Builder) Executing the following Automatic Testing Tool command: \"moveTo\". Coordinates: (" + x + ", " + y + ").");
            else
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + TestExecutor.testCaseFilename + ") Executing the following Automatic Testing Tool command: \"moveTo\". Coordinates: (" + x + ", " + y + ").");
        }
        // If command is equals to the "singleLeftClick" ATT's command...
        if (commandName.equals("singleLeftClick")) {
            TestExecutor.simulator.singleLeftClick();
            if (buildingTestCase)
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - Test Case Builder) Executing the following Automatic Testing Tool command: \"singleLeftClick\".");
            else
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + TestExecutor.testCaseFilename + ") Executing the following Automatic Testing Tool command: \"singleLeftClick\".");
        }
        // If command is equals to the "singleRightClick" ATT's command...
        if (commandName.equals("singleRightClick")) {
            TestExecutor.simulator.singleRightClick();
            if (buildingTestCase)
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - Test Case Builder) Executing the following Automatic Testing Tool command: \"singleRightClick\".");
            else
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + TestExecutor.testCaseFilename + ") Executing the following Automatic Testing Tool command: \"singleRightClick\".");
        }
        // If command is equals to the "moveAndSingleLeftClick" ATT's command...
        if (commandName.equals("moveAndSingleLeftClick")) {
            TestExecutor.simulator.moveAndSingleLeftClick(x, y);
            if (buildingTestCase)
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - Test Case Builder) Executing the following Automatic Testing Tool command: \"moveAndSingleLeftClick\". Coordinates: (" + x + ", " + y + ").");
            else
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + TestExecutor.testCaseFilename + ") Executing the following Automatic Testing Tool command: \"moveAndSingleLeftClick\". Coordinates: (" + x + ", " + y + ").");
        }
        // If command is equals to the "moveAndSingleRightClick" ATT's command...
        if (commandName.equals("moveAndSingleRightClick")) {
            TestExecutor.simulator.moveAndsingleRightClick(x, y);
            if (buildingTestCase)
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - Test Case Builder) Executing the following Automatic Testing Tool command: \"moveAndSingleRightClick\". Coordinates: (" + x + ", " + y + ").");
            else
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + TestExecutor.testCaseFilename + ") Executing the following Automatic Testing Tool command: \"moveAndSingleRightClick\". Coordinates: (" + x + ", " + y + ").");
        }
        // If command is equals to the "pressStringKeys" ATT's command...
        if (commandName.equals("pressStringKeys")) {
            returnedValue = TestExecutor.simulator.pressKeys(commandValue);
            if (buildingTestCase)
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - Test Case Builder) Executing the following Automatic Testing Tool command: \"pressStringKeys\". Keys: \"" + commandValue + "\".");
            else
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + TestExecutor.testCaseFilename + ") Executing the following Automatic Testing Tool command: \"pressStringKeys\". Keys: \"" + commandValue + "\".");
            // CI HO PERSO 2 MESI DIETRO!!! MA TI HO TROVATO!!!
            //TestExecutor.simulator.pressKey(KeyEvent.VK_ESCAPE, -1);
        }
        // If command is equals to the "pressCommandKeys" ATT's command...
        if (commandName.equals("pressCommandKeys")) {
            String[] tmpStr = commandValue.split("#");
            TestExecutor.simulator.pressKey(Integer.parseInt(tmpStr[0]), Integer.parseInt(tmpStr[1]));
            if (buildingTestCase)
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - Test Case Builder) Executing the following Automatic Testing Tool command: \"pressCommandKeys\". Keys: \"" + Integer.parseInt(tmpStr[0]) + ", " + Integer.parseInt(tmpStr[1]) + "\".");
            else
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + TestExecutor.testCaseFilename + ") Executing the following Automatic Testing Tool command: \"pressCommandKeys\". Keys: \"" + Integer.parseInt(tmpStr[0]) + ", " + Integer.parseInt(tmpStr[1]) + "\".");
        }
        // If command is equals to the "waitForNSeconds" ATT's command...
        if (commandName.equals("waitForNSeconds")) {
            TestExecutor.simulator.delay(Integer.parseInt(commandValue));
            if (buildingTestCase)
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - Test Case Builder) Executing the following Automatic Testing Tool command: \"waitForNSeconds\". Time (ms): \"" + Integer.parseInt(commandValue) + "\".");
            else
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + TestExecutor.testCaseFilename + ") Executing the following Automatic Testing Tool command: \"waitForNSeconds\". Time (ms): \"" + Integer.parseInt(commandValue) + "\".");
        }
        // If command is equals to the "dragAndDrop" ATT's command...
        if (commandName.equals("dragAndDrop")) {
            TestExecutor.simulator.dragAndDrop(startX, startY, endX, endY);
            if (buildingTestCase)
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - Test Case Builder) Executing the following Automatic Testing Tool command: \"dragAndDrop\". Coordinates: from (" + startX + ", " + startY + ") to (" + endX + ", " + endY + ").");
            else
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + TestExecutor.testCaseFilename + ") Executing the following Automatic Testing Tool command: \"dragAndDrop\". Coordinates: from (" + startX + ", " + startY + ") to (" + endX + ", " + endY + ").");
        }
        // If command is equals to the "takeAndSaveAScreenshot" ATT's command...
        if (commandName.equals("takeAndSaveAScreenshot")) {
            File screenshotFile = null;
            if (buildingTestCase) {
                screenshotFile = new File(screenshotsTestCaseStaticFolder + screenshotName);
                if (buildingTestCase)
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - Test Case Builder) Executing the following Automatic Testing Tool command: \"takeAndSaveAScreenshot\". Screenshot filename: \"" + screenshotsTestCaseStaticFolder + screenshotFile.getName() + "\". Coordinates: from (" + startX + ", " + startY + ") to (" + endX + ", " + endY + ").");
                else
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + TestExecutor.testCaseFilename + ") Executing the following Automatic Testing Tool command: \"takeAndSaveAScreenshot\". Screenshot filename: \"" + screenshotsTestCaseStaticFolder + screenshotFile.getName() + "\". Coordinates: from (" + startX + ", " + startY + ") to (" + endX + ", " + endY + ").");
            } else {
                screenshotFile = new File(screenshotsTestCaseTMPFolder + screenshotName);
                if (buildingTestCase)
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - Test Case Builder) Executing the following Automatic Testing Tool command: \"takeAndSaveAScreenshot\". Screenshot filename: \"" + screenshotsTestCaseTMPFolder + screenshotFile.getName() + "\". Coordinates: from (" + startX + ", " + startY + ") to (" + endX + ", " + endY + ").");
                else
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + TestExecutor.testCaseFilename + ") Executing the following Automatic Testing Tool command: \"takeAndSaveAScreenshot\". Screenshot filename: \"" + screenshotsTestCaseTMPFolder + screenshotFile.getName() + "\". Coordinates: from (" + startX + ", " + startY + ") to (" + endX + ", " + endY + ").");
            }
            returnedValue = TestExecutor.simulator.takeAndSaveAScreenshot(screenshotFile, startX, startY, endX, endY);
            if (!buildingTestCase) {
                /*
                 * Checks if the screenshot took during the Test Case Execution
                 * by the takeAndSaveAScreenshot command, and the screenshot
                 * took during the Test Case Buildiing by the same command are
                 * equals.
                 */
                File staticFile = new File(screenshotsTestCaseStaticFolder + screenshotName);
                if (!TestExecutor.imageComparator(screenshotFile, staticFile)) {
                    File[] tmp = {screenshotFile, staticFile};
                    TestExecutor.testExecutorScreenshotsToCheck.add(tmp);
                    returnedValue = Boolean.FALSE;
                }
            }
        }
        if (commandName.equals("takeAScreenshotToVerify")) {
            File screenshotFile = null;
            if (buildingTestCase) {
                screenshotFile = new File(SystemManagement.getScreenshotsExecutionWorkingFolder() + screenshotName);
                returnedValue = TestExecutor.simulator.takeAndSaveAScreenshot(screenshotFile, startX, startY, endX, endY);
                if (buildingTestCase)
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - Test Case Builder) Executing the following Automatic Testing Tool command: \"takeAScreenshotToVerify\". Screenshot filename: \"" + SystemManagement.getScreenshotsExecutionWorkingFolder() + screenshotFile.getName() + "\". Coordinates: from (" + startX + ", " + startY + ") to (" + endX + ", " + endY + ").");
                else
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + TestExecutor.testCaseFilename + ") Executing the following Automatic Testing Tool command: \"takeAScreenshotToVerify\". Screenshot filename: \"" + SystemManagement.getScreenshotsExecutionWorkingFolder() + screenshotFile.getName() + "\". Coordinates: from (" + startX + ", " + startY + ") to (" + endX + ", " + endY + ").");
            } else {
                screenshotFile = new File(screenshotsTestCaseTMPFolder + screenshotName);
                returnedValue = TestExecutor.simulator.takeAndSaveAScreenshot(screenshotFile, startX, startY, endX, endY);
                if (buildingTestCase)
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - Test Case Builder) Executing the following Automatic Testing Tool command: \"takeAScreenshotToVerify\". Screenshot filename: \"" + screenshotsTestCaseTMPFolder + screenshotFile.getName() + "\". Coordinates: from (" + startX + ", " + startY + ") to (" + endX + ", " + endY + ").");
                else
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + TestExecutor.testCaseFilename + ") Executing the following Automatic Testing Tool command: \"takeAScreenshotToVerify\". Screenshot filename: \"" + screenshotsTestCaseTMPFolder + screenshotFile.getName() + "\". Coordinates: from (" + startX + ", " + startY + ") to (" + endX + ", " + endY + ").");
            }
        }
        // If command is equals to the "takeAWaitForChangesScreenshot" ATT's command...
        if (commandName.equals("takeAWaitForChangesScreenshot")) {
            TestExecutor.simulator.takeAnIntelligentWaitScreenshot(new File(SystemManagement.getScreenshotsExecutionWorkingFolder() + "screenshot_" + SystemManagement.getCompactDate() + "_" + SystemManagement.getCompactHour() + ".png"), startX, startY, endX, endY);
            if (buildingTestCase)
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - Test Case Builder) Executing the following Automatic Testing Tool command: \"takeAWaitForChangesScreenshot\". Coordinates: from (" + startX + ", " + startY + ") to (" + endX + ", " + endY + ").");
            else
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + TestExecutor.testCaseFilename + ") Executing the following Automatic Testing Tool command: \"takeAWaitForChangesScreenshot\". Coordinates: from (" + startX + ", " + startY + ") to (" + endX + ", " + endY + ").");
        }
        // If command is equals to the "waitForChanges" ATT's command...
        if (commandName.equals("waitForChanges")) {
            TestExecutor.simulator.intelligentWait(startX, startY, endX, endY);
            if (buildingTestCase)
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - Test Case Builder) Executing the following Automatic Testing Tool command: \"waitForChanges\". Coordinates: from (" + startX + ", " + startY + ") to (" + endX + ", " + endY + ").");
            else
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + TestExecutor.testCaseFilename + ") Executing the following Automatic Testing Tool command: \"waitForChanges\". Coordinates: from (" + startX + ", " + startY + ") to (" + endX + ", " + endY + ").");
        }
        return returnedValue;
    }

    /**
     * Looks for the name of a screenshot in the XML file.
     * @param doc a Document element that identifies the document where to find
     * the name of the screenshot.
     * @param command a Node element that identifies the ATT command that had
     * generate a screenshot.
     * @return a String that identifies the name of the screenshot, null
     * otherwise.
     */
    private static String getScreenshotName(Document doc, Node command) {
        // Gets the node name.
        String commandName = command.getNodeName();
        if (!commandName.equals("takeAndSaveAScreenshot") && !commandName.equals("takeAScreenshotToVerify"))
            return null;
        else {
            // Gets the command stepId.
            String commandStepId = command.getParentNode().getAttributes().getNamedItem("id").getNodeValue();
            String screenshotName = null;
            /*
             * If we have a "takeAndSaveAScreenshot" command, the name of the
             * screenshot has the folowing form:
             *      screenshot<stepId>
             * where <stepId> is a non negative integer.
             */
            if (commandName.equals("takeAndSaveAScreenshot"))
                screenshotName = "screenshot" + commandStepId;
            /*
             * If we have a "takeAScreenshotToVerify" command, we have to find
             * the screenshot name in the "screenshotsToVerify" tag. We could do
             * this, beacuse we have the stepId.
             */
            if(commandName.equals("takeAScreenshotToVerify")) {
                NodeList screenList = doc.getElementsByTagName("screen");
                for (int i = 0; i < screenList.getLength(); i++) {
                    if (screenList.item(i).getChildNodes().item(1).getChildNodes().item(0).getNodeValue().equals(commandStepId))
                        screenshotName = screenList.item(i).getChildNodes().item(3).getChildNodes().item(0).getNodeValue();
                }
            }
            return screenshotName;
        }
    }

    /**
     * Compares two images.
     * @param firstScreenshot a File element that identifies the first image to
     * compare.
     * @param secondScreenshot a File element that identifies the second image
     * to compare.
     * @return true if the images are equal, false otherwise.
     */
    private static Boolean imageComparator(File firstScreenshot, File secondScreenshot) {
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + TestExecutor.testCaseFilename + ") Comparing images.");
        BufferedImage fs;
        BufferedImage ss;
        try {
            fs = ImageIO.read(firstScreenshot);
        } catch (IOException e) {
            SystemManagement.manageError(Boolean.FALSE, "(Test Executor - " + TestExecutor.testCaseFilename + ") Error while reading the image \"" + firstScreenshot.getAbsolutePath() + "\": " + e.getMessage());
            return Boolean.FALSE;
        }
        try {
            ss = ImageIO.read(secondScreenshot);
        } catch (IOException e) {
            SystemManagement.manageError(Boolean.FALSE, "(Test Executor - " + TestExecutor.testCaseFilename + ") Error while reading the image \"" + secondScreenshot.getAbsolutePath() + "\": " + e.getMessage());
            return Boolean.FALSE;
        }
        int[] firstPixels = new int[fs.getWidth() * fs.getHeight()];
        int[] secondPixels = new int[ss.getWidth() * ss.getHeight()];
        PixelGrabber firstImg = new PixelGrabber(fs.getSource(), 0, 0, fs.getWidth(), fs.getHeight(), firstPixels, 0, fs.getWidth());
        PixelGrabber secondImg = new PixelGrabber(ss.getSource(), 0, 0, ss.getWidth(), ss.getHeight(), secondPixels, 0, ss.getWidth());

        firstImg.startGrabbing();
        secondImg.startGrabbing();

        for (int i = 0 ; i < (Math.max(firstPixels.length, secondPixels.length)) ; i++ )
            if (firstPixels[i] != secondPixels[i]) {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + TestExecutor.testCaseFilename + ") The images \"" + firstScreenshot.getAbsolutePath() + "\" and \"" + secondScreenshot.getAbsolutePath() + "\" are NOT equals.");
                return Boolean.FALSE;
            }
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor - " + TestExecutor.testCaseFilename + ") The images \"" + firstScreenshot.getAbsolutePath() + "\" and \"" + secondScreenshot.getAbsolutePath() + "\" are equals.");
        return Boolean.TRUE;
    }
}