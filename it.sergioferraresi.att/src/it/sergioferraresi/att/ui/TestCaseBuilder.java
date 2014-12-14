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
 * Filename         TestCaseBuilder.java
 * Created on       2010-09-03
 * Last modified on 2014-12-09
 */
package it.sergioferraresi.att.ui;

import it.sergioferraresi.att.SystemManagement;
import it.sergioferraresi.att.TestExecutor;
import it.sergioferraresi.att.UserActionSimulator;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Provides the environment useful to build Test Cases.
 * 
 * @author  Sergio Ferraresi (psf563)
 * @version 1.0 release 20101209fr)
 */
public class TestCaseBuilder extends JPanel implements ActionListener, MouseListener {
    /**
     * An integer that, if passed to the TestCaseBuilder class, tells to the
     * TestCaseBuilder to start a program on which build the Test Case.
     */
    public static final int EXECUTE_WITH_PROGRAM = 1;
    /**
     * An integer that, if passed to the TestCaseBuilder class, tells to the
     * TestCaseBuilder to build the Test Case without starting a program.
     */
    public static final int EXECUTE_WITHOUT_PROGRAM = 0;
    private static final long serialVersionUID = 1L;
    /**
     * An array of XML nodes that identifies the last command to accept.
     */
    private ArrayList<Node> tbCMDNodesLast;
    /**
     * An array of XML nodes that identifies all the commands to accept.
     */
    private ArrayList<Node> tbCMDNodesAll;
    /**
     * A Boolean that identifies if the user has selected an AOI.
     */
    private Boolean isAOISelected;
    /**
     * A Boolean that identifies if the user has made the first click.
     */
    private Boolean isFirstTime;
    /**
     * A Boolean that identifies if the recording of more commands is started.
     */
    private Boolean isRecordingStarted;
    /**
     * Used by the DoubleBuffering.
     */
    private Graphics offscreen;
    /**
     * An Image that identifies the image to put as background of the
     * TestCaseBuilder.
     */
    private Image image;
    /**
     * Used by the DoubleBuffering.
     */
    private Image virtualBuffer;
    /**
     * Integer values that identify the coordinates of the mouse.
     */
    private int tmpX, tmpY;
    /**
     * Integer values that identify the coordinates of the first point of the
     * AOI.
     */
    private int startX, startY;
    /**
     * Integer values that identify the coordinates of the seconf point of the
     * AOI.
     */
    private int endX, endY;
    /**
     * An integer value that identifies the id of each step wrote in the XTD
     * file.
     */
    private int stepId;
    /**
     * A node that identifies the "screenshotToVerify" node of the XTD file.
     */
    private Node screenshotsToVerifyNode;
    /**
     * A node that identifies the "testCaseSteps" node of the XTD file.
     */
    private Node testCaseStepsNode;
    /**
     * A String that identifies the label of commands that need two mouse
     * clicks.
     */
    private String complexCommand;
    /**
     * String values that identify details about a screenshot.
     */
    private String screenshotComment, screenshotName;
    /**
     * A String that identifies the static folder for static screenshots.
     */
    private String screenshotsTCStaticFolderPath;
    /**
     * URL values that identify the icons for the TestCaseBuilder window.
     */
    private URL greenLight, redLight;
    /**
     * The Robot instance.
     */
    private UserActionSimulator robot;

    /**
     * Builds a Test Case. Starts the TestCaseBuilder window.
     * @param mode an integer value that identifies if to start a program or
     * not. The accepted values are:
     * <ul>
     *   <li><i>TestCaseBuilder.EXECUTE_WITH_PROGRAM</i>, to start a program on
     *          wich create the Test Case;</li>
     *   <li><i>TestCaseBuilder.EXECUTE_WITHOUT_PROGRAM</i>, to start the Test
     *          Case, without opening a program.</li>
     * </ul>
     * @param testCaseName a String that identifies the name of the Test Case.
     */
    public TestCaseBuilder(int mode, String testCaseName) {
        this.greenLight = getClass().getResource("programImages/green_light.png");
        this.redLight = getClass().getResource("programImages/red_light.png");
        WindowManager.getTestCaseBuilderFrame().setIconImage(new ImageIcon(this.redLight).getImage());
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder) Starting the Test Case Builder.");
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder) Test Case name: \"" + testCaseName + "\".");
        this.tmpX = 0;
        this.tmpY = 0;
        this.startX = -1;
        this.startY = -1;
        this.endX = -1;
        this.endY = -1;
        this.stepId = 0;
        this.isFirstTime = Boolean.TRUE;
        this.complexCommand = "";
        this.tbCMDNodesLast = new ArrayList<Node>();
        this.tbCMDNodesAll = new ArrayList<Node>();
        this.screenshotName = "";
        this.screenshotComment = "";
        this.isAOISelected = Boolean.FALSE;
        this.isRecordingStarted = Boolean.FALSE;
        this.screenshotsToVerifyNode = null;
        /*
         * Sets and creates the folders for the Test Case.
         * Creates only the Test Case folder and the staticScreenshot subfolder
         * because does not need to put the images in a "date-hour" folder.
         */
        String tmp = SystemManagement.getScreenshotsWorkingFolder() + testCaseName + File.separator;
        if (!SystemManagement.createFolder(tmp))
            SystemManagement.manageError(Boolean.TRUE, "(Test Case Builder) Error while creating the folder: \"" + tmp + "\".");
        this.screenshotsTCStaticFolderPath =  tmp + "staticScreenshots" + File.separator;
        if (!SystemManagement.createFolder(this.screenshotsTCStaticFolderPath))
            SystemManagement.manageError(Boolean.TRUE, "(Test Case Builder) Error while creating the folder \"" + this.screenshotsTCStaticFolderPath + "\".");

        this.testCaseStepsNode = SystemManagement.createXMLNode("testCaseSteps", null);
        SystemManagement.appendXMLChildToXMLNode(WindowManager.getTestNode(), this.testCaseStepsNode);

        // Starts the Robot.
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder) Creating the Robot.");
        this.robot = new UserActionSimulator();
        // Waits for Main frame iconification.
        this.robot.delay(750);

        if (mode == TestCaseBuilder.EXECUTE_WITH_PROGRAM) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder) Opening the Generic Program: \"" + SystemManagement.getGenericProgramPath() + "\".");
            // Executes the choosed program.
            String[] cmd = null;
            if (SystemManagement.getOSName().contains("Linux") == Boolean.TRUE) {
                cmd = new String[3];
                cmd[0] = "/bin/sh";
                cmd[1] = "-c";
                cmd[2] = SystemManagement.getGenericProgramPath();
            }
            if (SystemManagement.getOSName().contains("Windows") == Boolean.TRUE) {
                cmd = new String[5];
                cmd[0] = "cmd";
                cmd[1] = "/c";
                cmd[2] = "start";
                cmd[3] = "\"\"";
                cmd[4] = "\"" + SystemManagement.getGenericProgramPath() + "\"";
            }
            // Takes a screenshot before the program starts.
            this.robot.takeAnIntelligentWaitScreenshot(new File(SystemManagement.getScreenshotsExecutionWorkingFolder() + "screenshot_" + SystemManagement.getCompactDate() + "_" + SystemManagement.getCompactHour() + ".png"), 0, 0, SystemManagement.getScreenWidth(), SystemManagement.getScreenHeight());
            this.createStepNodeAndAppendDocument(-1, "takeAWaitForChangesScreenshot", null, 0, 0, SystemManagement.getScreenWidth(), SystemManagement.getScreenHeight());
            // Starts the program.
            SystemManagement.executeProgramAndDoNotWait(cmd);
            this.createStepNodeAndAppendDocument(this.stepId++, "startApplication", SystemManagement.getGenericProgramPath());
            // Waits for program loading.
            this.robot.intelligentWait(0, 0, SystemManagement.getScreenWidth(), SystemManagement.getScreenHeight());
            this.createStepNodeAndAppendDocument(-1, "waitForChanges", null, 0, 0, SystemManagement.getScreenWidth(), SystemManagement.getScreenHeight());
        } else
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder) Generic Program not selected.");
        // Takes the fisrt desktop screenshot for the testBuilder backgroud.
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder) Updating the background with the next screenshot.");
        this.takeAnotherScreenshot();
        WindowManager.getTestCaseBuilderFrame().setIconImage(new ImageIcon(this.greenLight).getImage());
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
     * Allows the user to create a Test Case.<br/>
     * If the user does a single-left-click, the TestCaseBuilder opens the
     * "Commands Menu", useful to select a command.<br/>
     * If the user does a single-right-click, the TestCaseBuilder opens the
     * "Comments Menu", useful to insert a comment into the XTD file.<br/>
     * If the user does a single-left-click after another single-left-click,
     * the TestCaseBuilder selects the area identified by the two points.
     * @param e
     */
    public void mouseClicked(MouseEvent e) {
        this.tmpX = e.getX();
        this.tmpY = e.getY();
        if (this.isAOISelected == Boolean.FALSE) {
            // Not AOI case.
            // Insert the command.
            if ((e.getClickCount() == 1) && !e.isControlDown() && !e.isAltDown() && (e.getButton() == MouseEvent.BUTTON1)) {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Left click at coordinates (" + this.tmpX + ", " + this.tmpY + "): \"Menu\" opened.");
                JPopupMenu pm = new JPopupMenu();
                JMenuItem title = new JMenuItem("Commands Menu");
                title.setEnabled(false);
                pm.add(title);
                pm.addSeparator();
                JMenuItem mt = new JMenuItem("Move On (x, y)");
                mt.setToolTipText("Moves the Mouse on a point without clicking on it.");
                pm.add(mt);
                mt.addActionListener(TestCaseBuilder.this);
                JMenuItem maslc = new JMenuItem("Single Left-Click on (x, y)");
                maslc.setToolTipText("Moves the mouse on a point and clicks on it with the left mouse button.");
                pm.add(maslc);
                maslc.addActionListener(TestCaseBuilder.this);
                JMenuItem masrc = new JMenuItem("Single Right-Click on (x, y)");
                masrc.setToolTipText("Moves the mouse on a point and clicks on it with the right mouse button.");
                pm.add(masrc);
                masrc.addActionListener(TestCaseBuilder.this);
                JMenuItem madlc= new JMenuItem("Double Left-Click on (x, y)");
                madlc.setToolTipText("Moves the mouse on a point and clicks two times on it with the left mouse button.");
                pm.add(madlc);
                madlc.addActionListener(TestCaseBuilder.this);
                pm.addSeparator();
                JMenuItem catwe = new JMenuItem("Click and Type (with \"ESC\")");
                catwe.setToolTipText("Clicks on a Text Field and types some keys (like \"hello\").");
                pm.add(catwe);
                catwe.addActionListener(TestCaseBuilder.this);
                JMenuItem cat = new JMenuItem("Click and Type (without \"ESC\")");
                cat.setToolTipText("Clicks on a Text Field and types some keys (like \"hello\").");
                pm.add(cat);
                cat.addActionListener(TestCaseBuilder.this);
                JMenuItem ccat = new JMenuItem("Click, Clean and Type");
                ccat.setToolTipText("Clicks on a Text Field, delete the previous text and types some keys (like \"hello\").");
                pm.add(ccat);
                ccat.addActionListener(TestCaseBuilder.this);
                JMenuItem cctaa = new JMenuItem("Click, Clean, Type and Accept");
                cctaa.setToolTipText("Clicks on a Text Field, delete the previous text, types some keys (like \"hello\") and presses the \"ENTER\" key.");
                pm.add(cctaa);
                cctaa.addActionListener(TestCaseBuilder.this);
                JMenuItem sa = new JMenuItem("Special Action");
                sa.setToolTipText("Does a Special Action, like COPY (CTRL + C), PASTE (CTRL + V), F11, CANC, ... .");
                pm.add(sa);
                sa.addActionListener(TestCaseBuilder.this);
                pm.addSeparator();
                JMenuItem wfns = new JMenuItem("Wait for n Seconds");
                wfns.setToolTipText("Waits for n seconds, and then allows to select a new command.");
                pm.add(wfns);
                wfns.addActionListener(TestCaseBuilder.this);
                pm.addSeparator();
                JMenuItem dd = new JMenuItem("Drag&Drop (or Select Text)");
                dd.setToolTipText("Drags from a start point and drops to an end point.");
                pm.add(dd);
                dd.addActionListener(TestCaseBuilder.this);
                JMenuItem tas = new JMenuItem("Take a Screenshot");
                tas.setToolTipText("<html><head></head><body>Takes a screenshot of an Area Of Interest for a future automatic check by the Automatic Testing Tool Program.<br/>The AOI is identified by a start point and an end point.<br/>This command is useful for an <b>automatic</b> check by the <b>Test Executor</b> when You execute a Test Case.</body></html>");
                pm.add(tas);
                tas.addActionListener(TestCaseBuilder.this);
                JMenuItem tastv = new JMenuItem("Take a Screenshot to Verify");
                tastv.setToolTipText("<html><head></head><body>Takes a screenshot of an Area Of Interest for a future supervised check by the user.<br/>The AOI is identified by a start point and an end point.<br/>This command is useful for a <b>manual</b> check by <b>User</b> when You execute a Test Case.</body></html>");
                pm.add(tastv);
                tastv.addActionListener(TestCaseBuilder.this);
                pm.addSeparator();
                JMenuItem ulc = new JMenuItem("Undo Last Command");
                ulc.setToolTipText("<html><head></head><body><p>Removes the <b>last</b> command from the Command List.</p></body></html>");
                pm.add(ulc);
                ulc.addActionListener(TestCaseBuilder.this);
                JMenuItem aaelc = new JMenuItem("Accept and Execute Last Command (CTRL + SINGLE LEFT CLICK)");
                aaelc.setToolTipText("Accepts the last command, executes it, and takes a new screenshot for the background.");
                pm.add(aaelc);
                aaelc.addActionListener(TestCaseBuilder.this);
                pm.addSeparator();
                JMenu rmtoc = new JMenu("Complex Commands");
                pm.add(rmtoc);
                rmtoc.addActionListener(TestCaseBuilder.this);
                JMenuItem recordingTitle = new JMenuItem("Complex Commands Menu");
                recordingTitle.setEnabled(false);
                rmtoc.add(recordingTitle);
                rmtoc.addSeparator();
                JMenuItem rmi = new JMenuItem("Complex Commands Menu Information");
                rmi.setToolTipText("Gives information about the \"Complex Commands Menu\".");
                rmtoc.add(rmi);
                rmi.addActionListener(TestCaseBuilder.this);
                rmtoc.addSeparator();
                JMenuItem sr = new JMenuItem("Start Recording");
                sr.setToolTipText("<html><head></head><body><p>Starts recording some ATT commands to execute them together.<br/>The command list <b>will be cleared</b>.</p></body></html>");
                rmtoc.add(sr);
                sr.addActionListener(TestCaseBuilder.this);
                JMenuItem ir = new JMenuItem("Interrupt Recording");
                ir.setToolTipText("<html><head></head><body><p>Interrupts the recording.<br/>The command list <b>will be cleared</b>.</p></body></html>");
                rmtoc.add(ir);
                ir.addActionListener(TestCaseBuilder.this);
                rmtoc.addSeparator();
                JMenuItem uac = new JMenuItem("Undo All Commands");
                uac.setToolTipText("<html><head></head><body><p>Removes <b>all</b> the commands from the Command list, and cleans the <b>last</b> command.</p></body></html>");
                rmtoc.add(uac);
                uac.addActionListener(TestCaseBuilder.this);
                JMenuItem aaeac = new JMenuItem("Execute All Commands (CTRL + SINGLE RIGHT CLICK)");
                aaeac.setToolTipText("Executes all the commands, and takes a new screenshot for the background.");
                rmtoc.add(aaeac);
                aaeac.addActionListener(TestCaseBuilder.this);
                pm.addSeparator();
                JMenuItem ri = new JMenuItem("Refresh the Interface");
                ri.setToolTipText("Refreshes the Interface.");
                pm.add(ri);
                ri.addActionListener(TestCaseBuilder.this);
                JMenuItem sbt = new JMenuItem("Stop Building Test");
                sbt.setToolTipText("Stops the Test Case Builder.");
                pm.add(sbt);
                sbt.addActionListener(TestCaseBuilder.this);

                pm.setVisible(true);
                pm.show(WindowManager.getTestCaseBuilderFrame(), this.tmpX, this.tmpY);
            }
            // Insert comment for the previous/next command.
            if ((e.getClickCount() == 1) && !e.isControlDown() && !e.isAltDown() && (e.getButton() == MouseEvent.BUTTON3)) {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Right click at coordinates (" + this.tmpX + ", " + this.tmpY + "): \"Comment Menu\" opened.");
                String tmp = JOptionPane.showInputDialog(WindowManager.getTestCaseBuilderFrame(), "<html><head></head><body><p>Insert a comment for the previous/next command.<br/>If the Comment field is empty, also the comment in the XTD file will be empty.</p></body></html>", "Test Case Builder - Comment Menu", JOptionPane.INFORMATION_MESSAGE);
                // Saving the Comment in the XTD file.
                if (tmp != null)
                    SystemManagement.appendXMLChildToXMLNode(this.testCaseStepsNode, SystemManagement.createXMLComment(tmp));
            }
            // Accept the command.
            if ((e.getClickCount() == 1) && e.isControlDown() && !e.isAltDown() && (e.getButton() == MouseEvent.BUTTON1)) {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Left click at coordinates (" + this.tmpX + ", " + this.tmpY + ") with \"CTRL\" key pressed: accetpt and execute last command.");
                this.acceptAndExecuteLastCommand();
            }
            if ((e.getClickCount() == 1) && e.isControlDown() && !e.isAltDown() && (e.getButton() == MouseEvent.BUTTON3)) {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Right click at coordinates (" + this.tmpX + ", " + this.tmpY + ") with \"CTRL\" key pressed: accetpt and execute all commands.");
                this.executeAllCommands();
            }
        } else {
            // AOI case: end point selected.
            this.endX = this.tmpX;
            this.endY = this.tmpY;
            WindowManager.getTestCaseBuilderFrame().getGraphics().drawOval(this.endX, this.endY, 3, 3);
            WindowManager.getTestCaseBuilderFrame().getGraphics().drawString("End Point", this.endX, this.endY);
            if (this.complexCommand.equals("Drag&Drop (or Select Text)"))
                WindowManager.getTestCaseBuilderFrame().getGraphics().drawLine(this.startX, this.startY, this.endX, this.endY);
            else {
                if (this.startX >= this.endX) {
                    int tmp = this.startX;
                    this.startX = this.endX;
                    this.endX = tmp;
                }
                if (this.startY >= this.endY) {
                    int tmp = this.startY;
                    this.startY = this.endY;
                    this.endY = tmp;
                }
                WindowManager.getTestCaseBuilderFrame().getGraphics().drawRect(this.startX, this.startY, (this.endX - this.startX), (this.endY - this.startY));
            }
            if (this.complexCommand.equals("Drag&Drop (or Select Text)")) {
                this.tbCMDNodesLast.add(this.createStepNode(this.stepId++, "dragAndDrop", null, this.startX, this.startY, this.endX, this.endY));
                if (this.isRecordingStarted)
                    this.tbCMDNodesAll.add(this.createStepNode((this.stepId - 1), "dragAndDrop", null, this.startX, this.startY, this.endX, this.endY));
            }
            if (this.complexCommand.equals("Take a Screenshot")) {
                this.tbCMDNodesLast.add(this.createStepNode(this.stepId++, "takeAndSaveAScreenshot", null, this.startX, this.startY, this.endX, this.endY));
                if (this.isRecordingStarted)
                    this.tbCMDNodesAll.add(this.createStepNode((this.stepId - 1), "takeAndSaveAScreenshot", null, this.startX, this.startY, this.endX, this.endY));
                this.screenshotName = "screenshot" + (this.stepId - 1);
            }
            if (this.complexCommand.equals("Take a Screenshot to Verify")) {
                this.tbCMDNodesLast.add(this.createStepNode(this.stepId++, "takeAScreenshotToVerify", null, this.startX, this.startY, this.endX, this.endY));
                if (this.isRecordingStarted)
                    this.tbCMDNodesAll.add(this.createStepNode((this.stepId - 1), "takeAScreenshotToVerify", null, this.startX, this.startY, this.endX, this.endY));
                if (this.isFirstTime) {
                    /*
                     * Is the first time that I want to take a screenshot for
                     * future checks: I must create the screenshotToVerify node.
                     */
                    this.screenshotsToVerifyNode = SystemManagement.createXMLNode("screenshotsToVerify", null);
                    this.isFirstTime = Boolean.FALSE;
                }
                Node screenNode = SystemManagement.createXMLNode("screen", null);
                SystemManagement.appendXMLChildToXMLNode(screenNode, SystemManagement.createXMLNode("stepId", Integer.toString(this.stepId - 1)));
                SystemManagement.appendXMLChildToXMLNode(screenNode, SystemManagement.createXMLNode("name", this.screenshotName));
                SystemManagement.appendXMLChildToXMLNode(screenNode, SystemManagement.createXMLNode("details", this.screenshotComment));
                SystemManagement.appendXMLChildToXMLNode(this.screenshotsToVerifyNode, screenNode);
            }
            JOptionPane.showMessageDialog(WindowManager.getTestCaseBuilderFrame(), "<html><head></head><body><p>AOI selected:<br/><ul><li>the start point's coordinates are: (" + this.startX + ", " + this.startY + ")</li><li>the end point's coordinates are: (" + this.endX + ", " + this.endY + ")</li></ul></p><p>Now You can undo or accept the last command.</p></body></html>", "Test Case Builder - Select the AOI", JOptionPane.INFORMATION_MESSAGE);
            this.isAOISelected = Boolean.FALSE;
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
     * Manages the GUI Events.
     * @param ae
     */
    public void actionPerformed(ActionEvent ae) {
        String e = ae.getActionCommand();
        if (e.equals("Move On (x, y)")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Clicked on \"Move On (x, y)\".");
            this.startX = this.tmpX;
            this.startY = this.tmpY;
            this.tbCMDNodesLast.add(this.createStepNode(this.stepId++, "moveTo", null, this.startX, this.startY));
            if (this.isRecordingStarted)
                this.tbCMDNodesAll.add(this.createStepNode((this.stepId - 1), "moveTo", null, this.startX, this.startY));
        }

        if (e.equals("Single Left-Click on (x, y)")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Clicked on \"Single Left-Click on (x, y)\".");
            this.startX = this.tmpX;
            this.startY = this.tmpY;
            this.tbCMDNodesLast.add(this.createStepNode(this.stepId++, "moveAndSingleLeftClick", null, this.startX, this.startY));
            if (this.isRecordingStarted)
                this.tbCMDNodesAll.add(this.createStepNode((this.stepId - 1), "moveAndSingleLeftClick", null, this.startX, this.startY));
        }

        if (e.equals("Single Right-Click on (x, y)")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Clicked on \"Single Right-Click on (x, y)\".");
            this.startX = this.tmpX;
            this.startY = this.tmpY;
            this.tbCMDNodesLast.add(this.createStepNode(this.stepId++, "moveAndSingleRightClick", null, this.startX, this.startY));
            if (this.isRecordingStarted)
                this.tbCMDNodesAll.add(this.createStepNode((this.stepId - 1), "moveAndSingleRightClick", null, this.startX, this.startY));
        }

        if (e.equals("Double Left-Click on (x, y)")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Clicked on \"Double Left-Click on (x, y)\".");
            this.startX = this.tmpX;
            this.startY = this.tmpY;
            this.tbCMDNodesLast.add(this.createStepNode(this.stepId++, "moveAndSingleLeftClick", null, this.startX, this.startY));
            if (this.isRecordingStarted)
                this.tbCMDNodesAll.add(this.createStepNode((this.stepId - 1), "moveAndSingleLeftClick", null, this.startX, this.startY));
            this.tbCMDNodesLast.add(this.createStepNode(this.stepId++, "moveAndSingleLeftClick", null, this.startX, this.startY));
            if (this.isRecordingStarted)
                this.tbCMDNodesAll.add(this.createStepNode((this.stepId - 1), "moveAndSingleLeftClick", null, this.startX, this.startY));
        }

        if (e.equals("Click and Type (without \"ESC\")")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Clicked on \"Click and Type (without \"ESC\")\".");
            // Saving the text.
            String tmp = JOptionPane.showInputDialog(WindowManager.getTestCaseBuilderFrame(), "Insert the string to type:", "Test Case Builder - Click and Type (without \"ESC\")", JOptionPane.INFORMATION_MESSAGE);
            if ((tmp != null) && (tmp.length() != 0)) {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Text to type: \"" + tmp + "\".");
                this.startX = this.tmpX;
                this.startY = this.tmpY;
                this.tbCMDNodesLast.add(this.createStepNode(this.stepId++, "moveAndSingleLeftClick", null, this.startX, this.startY));
                if (this.isRecordingStarted)
                    this.tbCMDNodesAll.add(this.createStepNode((this.stepId - 1), "moveAndSingleLeftClick", null, this.startX, this.startY));
                this.tbCMDNodesLast.add(this.createStepNode(this.stepId++, "pressStringKeys", tmp));
                if (this.isRecordingStarted)
                    this.tbCMDNodesAll.add(this.createStepNode((this.stepId - 1), "pressStringKeys", tmp));
            } else
                JOptionPane.showMessageDialog(WindowManager.getTestCaseBuilderFrame(), "You must enter a valid value.", "Test Case Builder - Click and Type (without \"ESC\")", JOptionPane.WARNING_MESSAGE);
        }

        if (e.equals("Click and Type (with \"ESC\")")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Clicked on \"Click and Type (with \"ESC\")\".");
            // Saving the text.
            String tmp = JOptionPane.showInputDialog(WindowManager.getTestCaseBuilderFrame(), "Insert the string to type:", "Test Case Builder - Click and Type (with \"ESC\")", JOptionPane.INFORMATION_MESSAGE);
            if ((tmp != null) && (tmp.length() != 0)) {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Text to type: \"" + tmp + "\".");
                this.startX = this.tmpX;
                this.startY = this.tmpY;
                this.tbCMDNodesLast.add(this.createStepNode(this.stepId++, "moveAndSingleLeftClick", null, this.startX, this.startY));
                if (this.isRecordingStarted)
                    this.tbCMDNodesAll.add(this.createStepNode((this.stepId - 1), "moveAndSingleLeftClick", null, this.startX, this.startY));
                this.tbCMDNodesLast.add(this.createStepNode(this.stepId++, "pressStringKeys", tmp));
                if (this.isRecordingStarted)
                    this.tbCMDNodesAll.add(this.createStepNode((this.stepId - 1), "pressStringKeys", tmp));
                this.tbCMDNodesLast.add(this.createStepNode(this.stepId++, "pressCommandKeys", KeyEvent.VK_ESCAPE + "#" + -1));
                if (this.isRecordingStarted)
                    this.tbCMDNodesAll.add(this.createStepNode((this.stepId - 1), "pressCommandKeys", KeyEvent.VK_ESCAPE + "#" + -1));
            } else
                JOptionPane.showMessageDialog(WindowManager.getTestCaseBuilderFrame(), "You must enter a valid value.", "Test Case Builder - Click and Type (with \"ESC\")", JOptionPane.WARNING_MESSAGE);
        }

        if (e.equals("Click, Clean and Type")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Clicked on \"Click, Clean and Type\".");
            // Saving the text.
            String tmp = JOptionPane.showInputDialog(WindowManager.getTestCaseBuilderFrame(), "Insert the string to type:", "Test Case Builder - Click, Clean and Type", JOptionPane.INFORMATION_MESSAGE);
            if ((tmp != null) && (tmp.length() != 0)) {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Text to type: \"" + tmp + "\".");
                this.startX = this.tmpX;
                this.startY = this.tmpY;
                this.tbCMDNodesLast.add(this.createStepNode(this.stepId++, "moveAndSingleLeftClick", null, this.startX, this.startY));
                if (this.isRecordingStarted)
                    this.tbCMDNodesAll.add(this.createStepNode((this.stepId - 1), "moveAndSingleLeftClick", null, this.startX, this.startY));
                this.tbCMDNodesLast.add(this.createStepNode(this.stepId++, "pressCommandKeys", KeyEvent.VK_END + "#" + -1));
                if (this.isRecordingStarted)
                    this.tbCMDNodesAll.add(this.createStepNode((this.stepId - 1), "pressCommandKeys", KeyEvent.VK_END + "#" + -1));
                this.tbCMDNodesLast.add(this.createStepNode(this.stepId++, "pressCommandKeys", KeyEvent.VK_HOME + "#" + KeyEvent.VK_SHIFT));
                if (this.isRecordingStarted)
                    this.tbCMDNodesAll.add(this.createStepNode((this.stepId - 1), "pressCommandKeys", KeyEvent.VK_HOME + "#" + KeyEvent.VK_SHIFT));
                this.tbCMDNodesLast.add(this.createStepNode(this.stepId++, "pressCommandKeys", KeyEvent.VK_DELETE + "#" + -1));
                if (this.isRecordingStarted)
                    this.tbCMDNodesAll.add(this.createStepNode((this.stepId - 1), "pressCommandKeys", KeyEvent.VK_DELETE + "#" + -1));
                this.tbCMDNodesLast.add(this.createStepNode(this.stepId++, "pressStringKeys", tmp));
                if (this.isRecordingStarted)
                    this.tbCMDNodesAll.add(this.createStepNode((this.stepId - 1), "pressStringKeys", tmp));
            } else
                JOptionPane.showMessageDialog(WindowManager.getTestCaseBuilderFrame(), "You must enter a valid value.", "Test Case Builder - Click, Clean and Type", JOptionPane.WARNING_MESSAGE);
        }

        if (e.equals("Click, Clean, Type and Accept")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Clicked on \"Click, Clean, Type and Accept\".");
            // Saving the text.
            String tmp = JOptionPane.showInputDialog(WindowManager.getTestCaseBuilderFrame(), "Insert the string to type:", "Test Case Builder - Click, Clean, Type and Accept", JOptionPane.INFORMATION_MESSAGE);
            if ((tmp != null) && (tmp.length() != 0)) {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Text to type: \"" + tmp + "\".");
                this.startX = this.tmpX;
                this.startY = this.tmpY;
                this.tbCMDNodesLast.add(this.createStepNode(this.stepId++, "moveAndSingleLeftClick", null, this.startX, this.startY));
                if (this.isRecordingStarted)
                    this.tbCMDNodesAll.add(this.createStepNode((this.stepId - 1), "moveAndSingleLeftClick", null, this.startX, this.startY));
                this.tbCMDNodesLast.add(this.createStepNode(this.stepId++, "pressCommandKeys", KeyEvent.VK_END + "#" + -1));
                if (this.isRecordingStarted)
                    this.tbCMDNodesAll.add(this.createStepNode((this.stepId - 1), "pressCommandKeys", KeyEvent.VK_END + "#" + -1));
                this.tbCMDNodesLast.add(this.createStepNode(this.stepId++, "pressCommandKeys", KeyEvent.VK_HOME + "#" + KeyEvent.VK_SHIFT));
                if (this.isRecordingStarted)
                    this.tbCMDNodesAll.add(this.createStepNode((this.stepId - 1), "pressCommandKeys", KeyEvent.VK_HOME + "#" + KeyEvent.VK_SHIFT));
                this.tbCMDNodesLast.add(this.createStepNode(this.stepId++, "pressCommandKeys", KeyEvent.VK_DELETE + "#" + -1));
                if (this.isRecordingStarted)
                    this.tbCMDNodesAll.add(this.createStepNode((this.stepId - 1), "pressCommandKeys", KeyEvent.VK_DELETE + "#" + -1));
                this.tbCMDNodesLast.add(this.createStepNode(this.stepId++, "pressStringKeys", tmp));
                if (this.isRecordingStarted)
                    this.tbCMDNodesAll.add(this.createStepNode((this.stepId - 1), "pressStringKeys", tmp));
                this.tbCMDNodesLast.add(this.createStepNode(this.stepId++, "pressCommandKeys", KeyEvent.VK_ENTER + "#" + -1));
                if (this.isRecordingStarted)
                    this.tbCMDNodesAll.add(this.createStepNode((this.stepId - 1), "pressCommandKeys", KeyEvent.VK_ENTER + "#" + -1));
            } else
                JOptionPane.showMessageDialog(WindowManager.getTestCaseBuilderFrame(), "You must enter a valid value.", "Test Case Builder - Click, Clean, Type and Accept", JOptionPane.WARNING_MESSAGE);
        }

        if (e.equals("Special Action")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Clicked on \"Special Action\".");
            String[] possibilities = {"Copy (CTRL + C)", "Cut (CTRL + X)", "DELETE", "ENTER", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "F11", "F12", "Paste (CTRL + V)"};
            String s = (String)JOptionPane.showInputDialog(WindowManager.getTestCaseBuilderFrame(), "Choose the Special Action:", "Test Case Builder - Special Action", JOptionPane.PLAIN_MESSAGE, null, possibilities, "ham");
            if (s != null) {
                this.startX = this.tmpX;
                this.startY = this.tmpY;
                ArrayList<Integer> specialActionCommands = new ArrayList<Integer>();
                if (s.equals("Copy (CTRL + C)")) {
                    specialActionCommands.add(0, KeyEvent.VK_C);
                    specialActionCommands.add(1, KeyEvent.VK_CONTROL);
                }
                if (s.equals("Cut (CTRL + X)")) {
                    specialActionCommands.add(0, KeyEvent.VK_X);
                    specialActionCommands.add(1, KeyEvent.VK_CONTROL);
                }
                if (s.equals("DELETE")) {
                    specialActionCommands.add(0, KeyEvent.VK_DELETE);
                    specialActionCommands.add(1, -1);
                }
                if (s.equals("ENTER")) {
                    specialActionCommands.add(0, KeyEvent.VK_ENTER);
                    specialActionCommands.add(1, -1);
                }
                if (s.equals("F1")) {
                    specialActionCommands.add(0, KeyEvent.VK_F1);
                    specialActionCommands.add(1, -1);
                }
                if (s.equals("F2")) {
                    specialActionCommands.add(0, KeyEvent.VK_F2);
                    specialActionCommands.add(1, -1);
                }
                if (s.equals("F3")) {
                    specialActionCommands.add(0, KeyEvent.VK_F3);
                    specialActionCommands.add(1, -1);
                }
                if (s.equals("F4")) {
                    specialActionCommands.add(0, KeyEvent.VK_F4);
                    specialActionCommands.add(1,-1);
                }
                if (s.equals("F5")) {
                    specialActionCommands.add(0, KeyEvent.VK_F5);
                    specialActionCommands.add(1, -1);
                }
                if (s.equals("F6")) {
                    specialActionCommands.add(0, KeyEvent.VK_F6);
                    specialActionCommands.add(1, -1);
                }
                if (s.equals("F7")) {
                    specialActionCommands.add(0, KeyEvent.VK_F7);
                    specialActionCommands.add(1, -1);
                }
                if (s.equals("F8")) {
                    specialActionCommands.add(0, KeyEvent.VK_F8);
                    specialActionCommands.add(1, -1);
                }
                if (s.equals("F9")) {
                    specialActionCommands.add(0, KeyEvent.VK_F9);
                    specialActionCommands.add(1, -1);
                }
                if (s.equals("F10")) {
                    specialActionCommands.add(0, KeyEvent.VK_F10);
                    specialActionCommands.add(1, -1);
                }
                if (s.equals("F11")) {
                    specialActionCommands.add(0, KeyEvent.VK_F11);
                    specialActionCommands.add(1, -1);
                }
                if (s.equals("F12")) {
                    specialActionCommands.add(0, KeyEvent.VK_F12);
                    specialActionCommands.add(1, -1);
                }
                if (s.equals("Paste (CTRL + V)")) {
                    specialActionCommands.add(0, KeyEvent.VK_V);
                    specialActionCommands.add(1, KeyEvent.VK_CONTROL);
                }
                this.tbCMDNodesLast.add(this.createStepNode(this.stepId++, "pressCommandKeys", specialActionCommands.get(0) + "#" + specialActionCommands.get(1)));
                if (this.isRecordingStarted)
                    this.tbCMDNodesAll.add(this.createStepNode((this.stepId - 1), "pressCommandKeys", specialActionCommands.get(0) + "#" + specialActionCommands.get(1)));
            } else
                JOptionPane.showMessageDialog(WindowManager.getTestCaseBuilderFrame(), "The Action is not valid.", "Test Case Builder - Special Action", JOptionPane.WARNING_MESSAGE);
        }
        if (e.equals("Wait for n Seconds")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Clicked on \"Wait for n Seconds\".");
            String time = JOptionPane.showInputDialog(WindowManager.getTestCaseBuilderFrame(), "How many seconds do You want to wait?", "Test Case Builder - Wait for", JOptionPane.INFORMATION_MESSAGE);
            if ((time != null) && (time.length() != 0) ) {
                time = time.trim();
                Boolean isNumber = Boolean.TRUE;
                for (int i = 0; i < time.length(); i++)
                    if (!Character.isDigit(time.charAt(i)) && (time.charAt(i) != '-'))
                        isNumber = Boolean.FALSE;
                if (isNumber) {
                    if (Integer.parseInt(time) > 0) {
                        Integer adjustedTime = Integer.parseInt(time) * 1000;
                        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Number of seconds to wait: " + adjustedTime.toString() + ".");
                        this.tbCMDNodesLast.add(this.createStepNode(this.stepId++, "waitForNSeconds", adjustedTime.toString()));
                        if (this.isRecordingStarted)
                            this.tbCMDNodesAll.add(this.createStepNode((this.stepId - 1), "waitForNSeconds", adjustedTime.toString()));
                    } else
                        JOptionPane.showMessageDialog(WindowManager.getTestCaseBuilderFrame(), "You must enter a positive value (> 1).", "Test Case Builder - Wait for", JOptionPane.WARNING_MESSAGE);
                } else
                    JOptionPane.showMessageDialog(WindowManager.getTestCaseBuilderFrame(), "You must enter an integer value.", "Test Case Builder - Wait for", JOptionPane.WARNING_MESSAGE);
            } else
                JOptionPane.showMessageDialog(WindowManager.getTestCaseBuilderFrame(), "You must enter a valid value.", "Test Case Builder - Wait for", JOptionPane.WARNING_MESSAGE);
        }
        if (e.equals("Take a Screenshot to Verify")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Clicked on \"Take a Screenshot to Verify\".");
            this.screenshotName = JOptionPane.showInputDialog(WindowManager.getTestCaseBuilderFrame(), "Insert the name of the screenshot (without path and extension):", "Test Case Builder - Take a Screenshot to Verify", JOptionPane.INFORMATION_MESSAGE);
            if (this.screenshotName != null) {
                this.screenshotName = this.screenshotName.replaceAll("\\s+", "");
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Name of the screenshot: \"" + this.screenshotName + "\".");
                this.screenshotComment = JOptionPane.showInputDialog(WindowManager.getTestCaseBuilderFrame(), "Insert a description for the screeshot:", "Test Case Builder - Take a Screenshot to Verify", JOptionPane.INFORMATION_MESSAGE);
                if (this.screenshotComment != null) {
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Description for the screenshot: \"" + this.screenshotComment + "\".");
                    this.complexCommand = "Take a Screenshot to Verify";
                    this.startX = this.tmpX;
                    this.startY = this.tmpY;
                    WindowManager.getTestCaseBuilderFrame().getGraphics().drawOval(this.startX, this.startY, 3, 3);
                    WindowManager.getTestCaseBuilderFrame().getGraphics().drawString("Start Point", this.startX, this.startY);
                    this.isAOISelected = Boolean.TRUE;
                    JOptionPane.showMessageDialog(WindowManager.getTestCaseBuilderFrame(), "Start point selected. Now click on the end point.", "Test Case Builder - Take a Screenshot to Verify", JOptionPane.INFORMATION_MESSAGE);
                } else
                    JOptionPane.showMessageDialog(WindowManager.getTestCaseBuilderFrame(), "You must enter the description for the screenshot.", "Test Case Builder - Take a Screenshot to Verify", JOptionPane.WARNING_MESSAGE);
            } else
                JOptionPane.showMessageDialog(WindowManager.getTestCaseBuilderFrame(), "You must enter the name of the screenshot.", "Test Case Builder - Take a Screenshot to Verify", JOptionPane.WARNING_MESSAGE);
        }

        if (e.equals("Drag&Drop (or Select Text)") || e.equals("Take a Screenshot")) {
            if (e.equals("Drag&Drop (or Select Text)")) {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Clicked on \"Drag&Drop (or Select Text)\".");
                this.complexCommand = "Drag&Drop (or Select Text)";
            }
            if (e.equals("Take a Screenshot")) {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Clicked on \"Take a Screenshot\".");
                this.complexCommand = "Take a Screenshot";
            }
            this.startX = this.tmpX;
            this.startY = this.tmpY;
            WindowManager.getTestCaseBuilderFrame().getGraphics().drawOval(this.startX, this.startY, 3, 3);
            WindowManager.getTestCaseBuilderFrame().getGraphics().drawString("Start Point", this.startX, this.startY);
            this.isAOISelected = Boolean.TRUE;
            JOptionPane.showMessageDialog(WindowManager.getTestCaseBuilderFrame(), "Start point selected. Now click on the end point.", "Test Case Builder - Select the AOI", JOptionPane.INFORMATION_MESSAGE);
        }

        if (e.equals("Undo Last Command")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Clicked on \"Undo Last Command\".");
            // Command NOT accepted: cleans the command.
            if (!this.tbCMDNodesLast.isEmpty())
                this.tbCMDNodesLast.clear();
        }

        if (e.equals("Accept and Execute Last Command (CTRL + SINGLE LEFT CLICK)")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Clicked on \"Accept and Execute Last Command (CTRL + SINGLE LEFT CLICK)\".");
            // Accepts and executes the last command.
            this.acceptAndExecuteLastCommand();
        }

        // Begin of the Recording Menu.
        if (e.equals("Complex Commands Menu Information")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Clicked on \"Record more than one Command --> Recording Menu Information\".");
            JOptionPane.showMessageDialog(WindowManager.getTestCaseBuilderFrame(), "<html><head></head><body><h2 alugn=\"center\">Complex Commands Menu Information</h2><p>The \"Complex Commands Menu\" is used to record some ATT instructions for the Test Case Builder.<br/>This Menu records the commands and writes them into the XTD file.<br/>You must use this Menu, when a simple command can not complete an operation. For example, You can use the \"Complex Commands Menu\" when you have to select something from a combo box.<br/>To use the \"Complex Commands Menu\":<br/><ul><li>click on \"Start Recording\";</li><li>use the Test Case Builder normally (select and execute each command);</li><li>click on \"Execute All Commands (CTRL + SINGLE RIGHT CLICK)\" to execute all the recorded commands.</li></ul></p></body></html>", "Test Case Builder - Complex Commands Menu Information", JOptionPane.INFORMATION_MESSAGE);
        }
        if (e.equals("Start Recording")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Clicked on \"Record more than one Command --> Start Recording\".");
            this.isRecordingStarted = Boolean.TRUE;
            // Cleans the command list.
            if (!this.tbCMDNodesAll.isEmpty())
                this.tbCMDNodesAll.clear();
        }
        if (e.equals("Interrupt Recording")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Clicked on \"Record more than one Command --> Interrupt Recording\".");
            this.isRecordingStarted = Boolean.FALSE;
            // Cleans the command list.
            if (!this.tbCMDNodesAll.isEmpty())
                this.tbCMDNodesAll.clear();
        }
        if (e.equals("Undo All Commands")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Clicked on \"Record more than one Command --> Undo All Commands\".");
            if (!this.tbCMDNodesAll.isEmpty())
                this.tbCMDNodesAll.clear();
            if (!this.tbCMDNodesLast.isEmpty())
                this.tbCMDNodesLast.clear();
        }
        if (e.equals("Execute All Commands (CTRL + SINGLE RIGHT CLICK)")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Clicked on \"Record more than one Command --> Execute All Commands (CTRL + SINGLE RIGHT CLICK)\".");
            // Accepts and executes all the commands.
            this.executeAllCommands();
        }
        // End of the Recording Menu.

        if (e.equals("Refresh the Interface")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Clicked on \"Refresh the Interface\".");
            WindowManager.getTestCaseBuilderFrame().toBack();
            WindowManager.getTestCaseBuilderFrame().setState(JFrame.ICONIFIED);
            this.robot.delay(1000);
            this.takeAnotherScreenshot();
            this.robot.delay(1000);
            WindowManager.getTestCaseBuilderFrame().repaint();
            WindowManager.getTestCaseBuilderFrame().setState(JFrame.NORMAL);
            WindowManager.getTestCaseBuilderFrame().toFront();
        }

        if (e.equals("Stop Building Test")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - Mouse) Clicked on \"Stop Building Tets\".");
            if (this.screenshotsToVerifyNode != null)
                SystemManagement.appendXMLChildToXMLNode(WindowManager.getTestNode(), this.screenshotsToVerifyNode);
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder) Test Case Builder finished.");
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder) Closing the XTD File.");
            SystemManagement.closeXMLFile();
            WindowManager.getTestCaseBuilderFrame().dispose();
            WindowManager.getMainFrame().setState(JFrame.NORMAL);
            JOptionPane.showMessageDialog(WindowManager.getMainFrame(), "Test Case Builder finished.", "Test Case Builder - Stop Building Test", JOptionPane.INFORMATION_MESSAGE);
            WindowManager.removeAllJPFromMainFrame();
            WindowManager.setAJPInTheMainFrame(WindowManager.getTestBuilderPanel());
            // Cleans the System.
            System.gc();
            System.runFinalization();
        }
    }

    /**
     * Accepts, writes to the XTD file, and executes last command.
     */
    private void acceptAndExecuteLastCommand() {
        if (!this.tbCMDNodesLast.isEmpty()) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder) Executing the Automatic Testing Tool command.");
            WindowManager.getTestCaseBuilderFrame().setIconImage(new ImageIcon(this.redLight).getImage());
            WindowManager.getTestCaseBuilderFrame().toBack();
            WindowManager.getTestCaseBuilderFrame().setState(JFrame.ICONIFIED);

            this.robot.delay(500);
            // Takes a screenshot before executing the command.
            this.robot.takeAnIntelligentWaitScreenshot(new File(SystemManagement.getScreenshotsExecutionWorkingFolder() + "screenshot_" + SystemManagement.getCompactDate() + "_" + SystemManagement.getCompactHour() + ".png"), 0, 0, SystemManagement.getScreenWidth(), SystemManagement.getScreenHeight());
            if (!this.isRecordingStarted)
                this.createStepNodeAndAppendDocument(-1, "takeAWaitForChangesScreenshot", null, 0, 0, SystemManagement.getScreenWidth(), SystemManagement.getScreenHeight());

            this.robot.delay(500);
            Iterator<Node> iterator = this.tbCMDNodesLast.iterator();
            while (iterator.hasNext()) {
                Node tbCommand = iterator.next();
                // Command accepted: writes it into the XTD file.
                if (!this.isRecordingStarted)
                    SystemManagement.appendXMLChildToXMLNode(this.testCaseStepsNode, tbCommand);
                NodeList commands = tbCommand.getChildNodes();
                Node command = null;
                for (int j = 0; j < commands.getLength(); j++)
                    if (!commands.item(j).getNodeName().equals("#text"))
                        command = commands.item(j);
                /*
                 * The screenshotPath is the ATT screenshot execution folder
                 * because we are creating the Test Case.
                 */
                if (!TestExecutor.executeATTCommand(command, Boolean.TRUE, this.screenshotsTCStaticFolderPath, null, ((this.screenshotName.contains(".png"))? this.screenshotName:(this.screenshotName + ".png"))))
                    SystemManagement.manageError(Boolean.TRUE, "(Test Case Builder) Error while executing the command.");
            }
            this.robot.delay(500);
            // Waits for the execution of the command.
            this.robot.intelligentWait(0, 0, SystemManagement.getScreenWidth(), SystemManagement.getScreenHeight());
            if (!this.isRecordingStarted)
                this.createStepNodeAndAppendDocument(-1, "waitForChanges", null, 0, 0, SystemManagement.getScreenWidth(), SystemManagement.getScreenHeight());
            this.robot.delay(500);
            this.takeAnotherScreenshot();

            this.tbCMDNodesLast.clear();

            this.robot.delay(500);
            WindowManager.getTestCaseBuilderFrame().setIconImage(new ImageIcon(this.greenLight).getImage());
            WindowManager.getTestCaseBuilderFrame().setState(JFrame.NORMAL);
            WindowManager.getTestCaseBuilderFrame().toFront();
            WindowManager.getTestCaseBuilderFrame().repaint();
        } else
            JOptionPane.showMessageDialog(WindowManager.getTestCaseBuilderFrame(), "You must record some command.", "Test Case Builder - Accept and Execute Last Command", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Accepts, writes to the XTD file, and executes all recorded commands.
     */
    private void executeAllCommands() {
        if (this.isRecordingStarted) {
            if (!this.tbCMDNodesAll.isEmpty()) {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder) Executing the Automatic Testing Tool commands.");
                WindowManager.getTestCaseBuilderFrame().setIconImage(new ImageIcon(this.redLight).getImage());
                WindowManager.getTestCaseBuilderFrame().toBack();
                WindowManager.getTestCaseBuilderFrame().setState(JFrame.ICONIFIED);

                Iterator<Node> iterator = this.tbCMDNodesAll.iterator();
                while (iterator.hasNext()) {
                    Node tbCommand = iterator.next();
                    this.robot.delay(500);
                    // Takes a screenshot before executing the command.
                    this.robot.takeAnIntelligentWaitScreenshot(new File(SystemManagement.getScreenshotsExecutionWorkingFolder() + "screenshot_" + SystemManagement.getCompactDate() + "_" + SystemManagement.getCompactHour() + ".png"), 0, 0, SystemManagement.getScreenWidth(), SystemManagement.getScreenHeight());
                    this.createStepNodeAndAppendDocument(-1, "takeAWaitForChangesScreenshot", null, 0, 0, SystemManagement.getScreenWidth(), SystemManagement.getScreenHeight());
                    // Command accepted: writes it into the XTD file.
                    SystemManagement.appendXMLChildToXMLNode(this.testCaseStepsNode, tbCommand);
                    NodeList commands = tbCommand.getChildNodes();
                    Node command = null;
                    for (int j = 0; j < commands.getLength(); j++)
                        if (!commands.item(j).getNodeName().equals("#text"))
                            command = commands.item(j);
                    /*
                     * The screenshotPath is the ATT screenshot execution folder
                     * because we are creating the Test Case.
                     */
                    if (!TestExecutor.executeATTCommand(command, Boolean.TRUE, this.screenshotsTCStaticFolderPath, null, ((this.screenshotName.contains(".png"))? this.screenshotName:(this.screenshotName + ".png"))))
                        SystemManagement.manageError(Boolean.TRUE, "(Test Case Builder) Error while executing the command.");
                    this.robot.delay(500);
                    // Waits for the execution of the command.
                    this.robot.intelligentWait(0, 0, SystemManagement.getScreenWidth(), SystemManagement.getScreenHeight());
                    this.createStepNodeAndAppendDocument(-1, "waitForChanges", null, 0, 0, SystemManagement.getScreenWidth(), SystemManagement.getScreenHeight());
                    this.robot.delay(500);
                    // Takes another screenshot.
                    this.takeAnotherScreenshot();
                }
                this.tbCMDNodesAll.clear();
                this.tbCMDNodesLast.clear();
                this.isRecordingStarted = Boolean.FALSE;

                this.robot.delay(500);
                WindowManager.getTestCaseBuilderFrame().setIconImage(new ImageIcon(this.greenLight).getImage());
                WindowManager.getTestCaseBuilderFrame().setState(JFrame.NORMAL);
                WindowManager.getTestCaseBuilderFrame().toFront();
                WindowManager.getTestCaseBuilderFrame().repaint();
            } else
                JOptionPane.showMessageDialog(WindowManager.getTestCaseBuilderFrame(), "You must record some command.", "Test Case Builder - Execute All Commands", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Takes another screenshot to put as background of the TestCaseBuilder.
     */
    private void takeAnotherScreenshot() {
        this.robot.delay(500);
        if (this.robot.takeAndSaveAScreenshot(new File(SystemManagement.getScreenshotsExecutionWorkingFolder() + "screenshot_" + SystemManagement.getCompactDate() + "_" + SystemManagement.getCompactHour() + ".png"), 0, 0, SystemManagement.getScreenWidth(), SystemManagement.getScreenHeight()) == true) {
            this.image = new ImageIcon(this.robot.getLastScreenshotPath()).getImage();
            Dimension size = new Dimension(this.image.getWidth(null), this.image.getHeight(null));
            this.setPreferredSize(size);
            this.setMinimumSize(size);
            this.setMaximumSize(size);
            this.setSize(size);
        }
    }

    /**
     * Creates a stepNode with the startX, startY, endX and endY attributes.
     * @param stepId an integer that identifies the id of the step node.
     * @param stepName a String that identifies the name of the step node.
     * @param stepValue a String that identifies the value of the step node.
     * @param startX an integer that identifies the value of the startX
     * attribute of the step node.
     * @param startY an integer that identifies the value of the startY
     * attribute of the step node.
     * @param endX an integer that identifies the value of the endX attribute
     * of the step node.
     * @param endY an integer that identifies the value of the endY attribute
     * of the step node.
     * @return a Node that identify the step node.
     */
    private Node createStepNode(int stepId, String stepName, String stepValue, int startX, int startY, int endX, int endY) {
        Node tmp = null;
        if (stepValue != null)
            tmp = SystemManagement.createXMLNode(stepName, stepValue);
        else
            tmp = SystemManagement.createXMLNode(stepName, null);
        SystemManagement.appendXMLAttributeToXMLNode(tmp, SystemManagement.createXMLAttribute("startX", Integer.toString(startX)));
        SystemManagement.appendXMLAttributeToXMLNode(tmp, SystemManagement.createXMLAttribute("startY", Integer.toString(startY)));
        SystemManagement.appendXMLAttributeToXMLNode(tmp, SystemManagement.createXMLAttribute("endX", Integer.toString(endX)));
        SystemManagement.appendXMLAttributeToXMLNode(tmp, SystemManagement.createXMLAttribute("endY", Integer.toString(endY)));
        Node stepNode = SystemManagement.createXMLNode("step", null);
        SystemManagement.appendXMLAttributeToXMLNode(stepNode, SystemManagement.createXMLAttribute("id", Integer.toString(stepId)));
        SystemManagement.appendXMLChildToXMLNode(stepNode, tmp);
        return stepNode;
    }

    /**
     * Creates a stepNode with the x and y attributes.
     * @param stepId an integer that identifies the id of the step node.
     * @param stepName a String that identifies the name of the step node.
     * @param stepValue a String that identifies the value of the step node.
     * @param x an integer that identifies the value of the x attribute of the
     * step node.
     * @param y an integer that identifies the value of the y attribute of the
     * step node.
     * @return a Node that identify the step node.
     */
    private Node createStepNode(int stepId, String stepName, String stepValue, int x, int y) {
        Node tmp = null;
        if (stepValue != null)
            tmp = SystemManagement.createXMLNode(stepName, stepValue);
        else
            tmp = SystemManagement.createXMLNode(stepName, null);
        SystemManagement.appendXMLAttributeToXMLNode(tmp, SystemManagement.createXMLAttribute("x", Integer.toString(x)));
        SystemManagement.appendXMLAttributeToXMLNode(tmp, SystemManagement.createXMLAttribute("y", Integer.toString(y)));
        Node stepNode = SystemManagement.createXMLNode("step", null);
        SystemManagement.appendXMLAttributeToXMLNode(stepNode, SystemManagement.createXMLAttribute("id", Integer.toString(stepId)));
        SystemManagement.appendXMLChildToXMLNode(stepNode, tmp);
        return stepNode;
    }

    /**
     * Creates a stepNode without attributes.
     * @param stepId an integer that identifies the id of the step node.
     * @param stepName a String that identifies the name of the step node.
     * @param stepValue a String that identifies the value of the step node.
     * @return a Node that identify the step node.
     */
    private Node createStepNode(int stepId, String stepName, String stepValue) {
        Node tmp = null;
        if (stepValue != null)
            tmp = SystemManagement.createXMLNode(stepName, stepValue);
        else
            tmp = SystemManagement.createXMLNode(stepName, null);
        Node stepNode = SystemManagement.createXMLNode("step", null);
        SystemManagement.appendXMLAttributeToXMLNode(stepNode, SystemManagement.createXMLAttribute("id", Integer.toString(stepId)));
        SystemManagement.appendXMLChildToXMLNode(stepNode, tmp);
        return stepNode;
    }

    /**
     * Creates a step node and appends it to the XML document (XTD file). The
     * step node must have the startX, startY, endX and endY attributes.
     * @param stepId an integer that identifies the id of the step node.
     * @param stepName a String that identifies the name of the step node.
     * @param stepValue a String that identifies the value of the step node.
     * @param startX an integer that identifies the value of the startX
     * attribute of the step node.
     * @param startY an integer that identifies the value of the startY
     * attribute of the step node.
     * @param endX an integer that identifies the value of the endX attribute
     * of the step node.
     * @param endY an integer that identifies the value of the enY attribute
     * of the step node.
     */
    private void createStepNodeAndAppendDocument(int stepId, String stepName, String stepValue, int startX, int startY, int endX, int endY) {
        SystemManagement.appendXMLChildToXMLNode(this.testCaseStepsNode, this.createStepNode(stepId, stepName, stepValue, startX, startY, endX, endY));
    }

    /**
     * Creates a step node and appends it to the XML document (XTD file). The
     * step node must have the x and y attributes.
     * @param stepId an integer that identifies the id of the step node.
     * @param stepName a String that identifies the name of the step node.
     * @param stepValue a String that identifies the value of the step node.
     * @param x an integer that identifies the value of the x attribute of the
     * step node.
     * @param y an integer that identifies the value of the y attribute of the
     * step node.
     */
    private void createStepNodeAndAppendDocument(int stepId, String stepName, String stepValue, int x, int y) {
        SystemManagement.appendXMLChildToXMLNode(this.testCaseStepsNode, this.createStepNode(stepId, stepName, stepValue, x, y));
    }

    /**
     * Creates a step node and appends it to the XML document (XTD file). The
     * step node must no have attributes.
     * @param stepId an integer that identifies the id of the step node.
     * @param stepName a String that identifies the name of the step node.
     * @param stepValue a String that identifies the value of the step node.
     */
    private void createStepNodeAndAppendDocument(int stepId, String stepName, String stepValue) {
        SystemManagement.appendXMLChildToXMLNode(this.testCaseStepsNode, this.createStepNode(stepId, stepName, stepValue));
    }
}