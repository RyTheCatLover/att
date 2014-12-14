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
 * Filename         WindowManager.java
 * Created on       2010-08-01
 * Last modified on 2014-12-14
 */
package it.sergioferraresi.att.ui;

import it.sergioferraresi.att.ScriptExecutor;
import it.sergioferraresi.att.SystemManagement;
import it.sergioferraresi.att.TestExecutor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableCellEditor;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Provides the Graphical User Interface (GUI) for the Automatic Testing Tool
 * Program.<br/>
 * Provides also some other methods used by the other class to modify the GUI.
 * 
 * @author  Sergio Ferraresi (psf563)
 * @version 1.0 (release 20101209fr)
 */
public class WindowManager extends JFrame implements ActionListener, WindowListener {
	/*
	 * TODO
	 *The hypothetical commands `show w' and `show c' should show the appropriate parts of the General Public License. Of course, your program's commands might be different; for a GUI interface, you would use an “about box”. 
	 */
	
	
    private static final long serialVersionUID = 1L;
    /**
     * Identify some useful messages used in the Window Interface.
     */
    private final String ON_EXIT = "Do you REALLY want to exit?";
    /**
     * Identify the graphic elements used to create the Window Interface.
     */
    private static JFrame main, testCaseBuilder, resultsValidator;
    private static JLabel statusBar;
    private static JPanel mainJP, testBuilderJP, testSuiteBuilderJP, executeJP, resultsJP;
    /**
     * Identifies the test node used in the XTD File.
     */
    private static Node testNode;
    /**
     * Identifies the GridBagConstraints element for the GridBagLayout.
     */
    private GridBagConstraints gbc;
    /**
     * Identify the graphic elements used to create the Window Interface.
     */
    private JButton addItemToButton, removeItemFromButton, moveUpButton, moveDownButton, executeButton, continueButton, cancelButton;
    private JCheckBox tcSelectProgramToExecuteCB;
    private JComboBox tcStatusCombo;
    private JFileChooser fileChooser;
    private JLabel initialMessage, panelTitle, numberOf, tcFilenameLabel, tcTitleLabel, tcAuthorLabel, tcContributorLabel, tcDateLabel, tcStatusLabel, tcVersionLabel, tcDescriptionLabel, tcPurposeLabel, tcPreconditionsLabel, tcNotesLabel, tcDateValueLabel, tcSelectProgramToExecuteLabel;
    private JList allElementsList, selectedElementsList;
    private JTextArea tcDescriptionTArea, tcPurposeTArea, tcPreconditionsTArea, tcNotesTArea;
    private JTextField tcFilenameText, tcTitleText, tcAuthorText, tcContributorText, tcVersionText;
    /**
     * Identify the formal metadata for an XTD File.
     */
    private String filename, title, author, contributor, date, status, version, description, purpose, preconditions, internalNotes;
    /**
     * Identifies an array of String for temporary use.
     */
    private String[] filenameList;

    /**
     * Identify the images of the arrow images of the Window Interface.
     * TODO 2014-12-05 (psf563): create SharedResources class in *.resources.
     */
    private static ImageIcon addArrow = new ImageIcon(WindowManager.class.getClassLoader().getResource("it/sergioferraresi/att/resources/icon/green_arrow.png")); //$NON-NLS-1$
    private static ImageIcon removeArrow = new ImageIcon(WindowManager.class.getClassLoader().getResource("it/sergioferraresi/att/resources/icon/red_arrow.png")); //$NON-NLS-1$
    private static ImageIcon moveUpArrow = new ImageIcon(WindowManager.class.getClassLoader().getResource("it/sergioferraresi/att/resources/icon/up_black_arrow.png")); //$NON-NLS-1$
    private static ImageIcon moveDownArrow = new ImageIcon(WindowManager.class.getClassLoader().getResource("it/sergioferraresi/att/resources/icon/down_black_arrow.png")); //$NON-NLS-1$

    /**
     * Identify the images of the icon and logo of the ATT Program.
     * TODO 2014-12-05 (psf563): create SharedResources class in *.resources.
     */
    private static ImageIcon ATTIcon = new ImageIcon(WindowManager.class.getClassLoader().getResource("it/sergioferraresi/att/resources/icon/ATT_logo_icon.png"));
    private static ImageIcon ATTLogo = new ImageIcon(WindowManager.class.getClassLoader().getResource("it/sergioferraresi/att/resources/icon/ATT_logo.png")); //$NON-NLS-1$
    private static ImageIcon MEEOLogo = new ImageIcon(WindowManager.class.getClassLoader().getResource("it/sergioferraresi/att/resources/icon/MEEO_logo.png")); //$NON-NLS-1$

    /**
     * Initializes the Main frame, and its elements.
     */
    public WindowManager() {
        super();
        // Adding Main Program Window.
        WindowManager.main = new JFrame();
        WindowManager.main.setTitle("Automatic Testing Tool");
        WindowManager.main.setSize(SystemManagement.getScreenWidth(), SystemManagement.getScreenHeight());
        WindowManager.main.setLocation(0, 0);
        WindowManager.main.setState(JFrame.NORMAL);
        WindowManager.main.addWindowListener(WindowManager.this);
        WindowManager.main.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        // Sets status bar.
        WindowManager.statusBar = new JLabel();
        WindowManager.main.getContentPane().add(WindowManager.statusBar, BorderLayout.SOUTH);
        // Inits the Automatic Testing Tool.
        SystemManagement.initAutomaticTestingTool(SystemManagement.WINDOW_INTERFACE);

        WindowManager.main.setIconImage(WindowManager.ATTIcon.getImage());

        /*
         * MenuBar.
         */
        //File Menu.
        JMenuBar mb = new JMenuBar();
        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        JMenuItem createGenericFile = new JMenuItem("New Generic File");
        file.add(createGenericFile);
        createGenericFile.addActionListener(WindowManager.this);
        JMenuItem modifyGenericFile = new JMenuItem("Open Generic File");
        file.add(modifyGenericFile);
        modifyGenericFile.addActionListener(WindowManager.this);
        file.addSeparator();
        JMenuItem createProject = new JMenuItem("New Project");
        file.add(createProject);
        createProject.addActionListener(WindowManager.this);
        JMenuItem openProject = new JMenuItem("Open Project");
        file.add(openProject);
        openProject.addActionListener(WindowManager.this);
        file.addSeparator();
        JMenuItem exitProgram = new JMenuItem("Exit");
        file.add(exitProgram);
        exitProgram.addActionListener(WindowManager.this);
        mb.add(file);
        // Scripts Menu.
        JMenu scripts = new JMenu("Scripts");
        scripts.setMnemonic(KeyEvent.VK_S);
        JMenuItem createScript = new JMenuItem("New Script");
        scripts.add(createScript);
        createScript.addActionListener(WindowManager.this);
        JMenuItem modifyScript = new JMenuItem("Open Script");
        scripts.add(modifyScript);
        modifyScript.addActionListener(WindowManager.this);
        mb.add(scripts);
        JMenuItem executeScripts = new JMenuItem("Choose and Execute Scripts");
        executeScripts.setMnemonic(KeyEvent.VK_E);
        scripts.add(executeScripts);
        executeScripts.addActionListener(WindowManager.this);
        mb.add(scripts);
        // Tests Menu.
        JMenu tests = new JMenu("Tests");
        tests.setMnemonic(KeyEvent.VK_T);
        JMenuItem testCaseBuilderItem = new JMenuItem("Test Builder");
        tests.add(testCaseBuilderItem);
        testCaseBuilderItem.addActionListener(WindowManager.this);
        JMenuItem modifyAndValidateTestFile = new JMenuItem("Open and Validate Test File");
        tests.add(modifyAndValidateTestFile);
        modifyAndValidateTestFile.addActionListener(WindowManager.this);
        JMenuItem executeTestsMenuItem = new JMenuItem("Execute Tests");
        tests.add(executeTestsMenuItem);
        executeTestsMenuItem.addActionListener(WindowManager.this);
        JMenuItem modifyAndValidateResultsFile = new JMenuItem("Open and Validate Report File");
        tests.add(modifyAndValidateResultsFile);
        modifyAndValidateResultsFile.addActionListener(WindowManager.this);
        JMenuItem validateTestsMenuItem = new JMenuItem("Validate Tests");
        tests.add(validateTestsMenuItem);
        validateTestsMenuItem.addActionListener(WindowManager.this);
        mb.add(tests);
        // Tools Menu.
        JMenu tools = new JMenu("Tools");
        tools.setMnemonic(KeyEvent.VK_O);
        JMenuItem chooseCygwinPath = new JMenuItem("Choose Cygwin Path");
        tools.add(chooseCygwinPath);
        chooseCygwinPath.addActionListener(WindowManager.this);
        /*
         * Control about the OS: Is used the "contains" method because in
         * Windows-based OS, the name is composed by "Windows" plus the specific
         * name of OS (like XP, Vista, ...).
         */
        if (SystemManagement.getOSName().contains("Linux"))
            chooseCygwinPath.setEnabled(false);
        if (SystemManagement.getOSName().contains("Windows")) {
            chooseCygwinPath.setEnabled(true);
            SystemManagement.setCygwinPath(SystemManagement.CYGWIN_PATH);
        }
        JMenuItem chooseTextEditor = new JMenuItem("Choose Text Editor");
        tools.add(chooseTextEditor);
        chooseTextEditor.addActionListener(WindowManager.this);
        JMenuItem openAnOldLog = new JMenuItem("Open Log");
        tools.add(openAnOldLog);
        openAnOldLog.addActionListener(WindowManager.this);
        JMenuItem testConnection = new JMenuItem("Test the Connection");
        tools.add(testConnection);
        testConnection.addActionListener(WindowManager.this);
        mb.add(tools);
        // About Menu.
        JMenu about = new JMenu("About");
        about.setMnemonic(KeyEvent.VK_A);
        JMenuItem aboutMeeo = new JMenuItem("MEEO");
        about.add(aboutMeeo);
        aboutMeeo.addActionListener(WindowManager.this);
        JMenuItem aboutProgram = new JMenuItem("Automating Testing Tool program");
        about.add(aboutProgram);
        aboutProgram.addActionListener(WindowManager.this);
        mb.add(about);
        WindowManager.main.setJMenuBar(mb);

        /*
         * Elements initialization.
         */
        // Initializes some useful elements.
        this.gbc = new GridBagConstraints();
        this.gbc.insets = new Insets(10, 10, 10, 10);
        this.filenameList = new String[0];
        this.filename = null;
        this.title = null;
        this.author = null;
        this.contributor = null;
        this.date = null;
        this.status = null;
        this.version = null;
        this.description = null;
        this.purpose = null;
        this.preconditions = null;
        this.internalNotes = null;
        // Initializes the generic elements.
        this.fileChooser = new JFileChooser();
        this.fileChooser.setMultiSelectionEnabled(false);
        this.panelTitle = new JLabel();
        this.numberOf = new JLabel();
        this.addItemToButton = new JButton(WindowManager.addArrow);
        this.addItemToButton.setActionCommand("Add Selected Item");
        this.addItemToButton.addActionListener(WindowManager.this);
        this.removeItemFromButton = new JButton(WindowManager.removeArrow);
        this.removeItemFromButton.setActionCommand("Remove Selected Item");
        this.removeItemFromButton.addActionListener(WindowManager.this);
        this.moveUpButton = new JButton(WindowManager.moveUpArrow);
        this.moveUpButton.setActionCommand("Move Up Selected Item");
        this.moveUpButton.addActionListener(WindowManager.this);
        this.moveDownButton = new JButton(WindowManager.moveDownArrow);
        this.moveDownButton.setActionCommand("Move Down Selected Item");
        this.moveDownButton.addActionListener(WindowManager.this);
        this.executeButton = new JButton();
        this.executeButton.addActionListener(WindowManager.this);
        this.continueButton = new JButton();
        this.continueButton.addActionListener(WindowManager.this);
        this.cancelButton = new JButton("Cancel");
        this.cancelButton.addActionListener(WindowManager.this);
        WindowManager.testBuilderJP = new JPanel(new GridBagLayout());
        WindowManager.testSuiteBuilderJP = new JPanel(new GridBagLayout());
        WindowManager.executeJP = new JPanel(new GridBagLayout());
        WindowManager.resultsJP = new JPanel(new GridBagLayout());
        this.allElementsList = new JList();
        this.allElementsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.allElementsList.setLayoutOrientation(JList.VERTICAL);
        this.selectedElementsList = new JList();
        this.selectedElementsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.selectedElementsList.setLayoutOrientation(JList.VERTICAL);
        // Initializes the mainJP panel.
        WindowManager.mainJP = new JPanel();
        WindowManager.mainJP.setBounds(0, 0, WindowManager.main.getWidth(), WindowManager.main.getHeight());
        this.initialMessage = new JLabel("<html>"
                + "                         <head></head>"
                + "                         <body>"
                + "                             <br/><br/><br/><br/><br/><br/><br/>"
                + "                             <h2>Welcome to the Window Interface of the Automatic Testing Tool Program!</h2>"
                + "                             <p>This software has the purpose of automate Testing Procedures for General-Purpose Software Applications."
                + "                             <br/><br/><br/>"
                + "                             <h2>How to use the Automatic Testing Tool Program?</h2>"
                + "                             <ul>"
                + "                                 <li>To create or open Projects and files, use the \"File\" Menu;</li>"
                + "                                 <li>To create, open or execute  scripts, use the \"Scripts\" Menu;</li>"
                + "                                 <li>To create, open, execute or validate Tests, use the \"Tests\" Menu;</li>"
                + "                                 <li>To manage the Text Editor, Cygwin, open logs and test the connection to a remote host, use the \"Tools\" Menu;</li>"
                + "                                 <li>To gather information about MEEO and Automatic Testing Tool Program, use the \"About\" Menu.</li>"
                + "                             </ul></p>"
                + "                             <br/><br/><br/>"
                + "                             <p align=\"center\"><img src=\"" + WindowManager.ATTLogo + "\"></img></p>"
                + "                         </body>"
                + "                     </html>");
        this.initialMessage.setBounds(WindowManager.mainJP.getX(), WindowManager.mainJP.getY(), WindowManager.mainJP.getWidth(), (WindowManager.mainJP.getHeight()));
        WindowManager.mainJP.add(this.initialMessage);
        // Refreshing the main frame.
        WindowManager.setAJPInTheMainFrame(WindowManager.mainJP);

        // Copies the testXMLSchema.xtd file in the tests folder.
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Copying the \"testXMLSchema.xsd\" file in the \"tests\" folder.");
        try {
            InputStream is = getClass().getResourceAsStream("../resources/xsd/testXMLSchema.xsd"); //$NON-NLS-1$
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(SystemManagement.getTestsWorkingFolder() + "testXMLSchema.xsd")));
            String line;
            while ((line = br.readLine()) != null) {
                bw.write(line);
                bw.newLine();
            }
            br.close();
            bw.close();
            isr.close();
            is.close();
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "File copied.");
        } catch (FileNotFoundException e) {
            SystemManagement.manageError(Boolean.FALSE, "It is not possible to create the \"testXMLSchema.xsd\" file: " + e.getMessage());
            JOptionPane.showMessageDialog(WindowManager.main, "It is not possible to create the \"testXMLSchema.xsd\" file: " + e.getMessage(), "Automatic Testing Tool", JOptionPane.WARNING_MESSAGE);
        } catch (IOException e) {
            SystemManagement.manageError(Boolean.FALSE, "It is not possible to create the \"testXMLSchema.xsd\" file: " + e.getMessage());
            JOptionPane.showMessageDialog(WindowManager.main, "It is not possible to create the \"testXMLSchema.xsd\" file: " + e.getMessage(), "Automatic Testing Tool", JOptionPane.WARNING_MESSAGE);
        }
        // Copies the reportsXMLSchema.xtd file in the results folder.
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Copying the \"reportsXMLSchema.xsd\" file in the \"results\" folder.");
        try {
            InputStream is = getClass().getResourceAsStream("../resources/xsd/reportsXMLSchema.xsd"); //$NON-NLS-1$
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(SystemManagement.getResultsWorkingFolder() + "reportsXMLSchema.xsd")));
            String line;
            while ((line = br.readLine()) != null) {
                bw.write(line);
                bw.newLine();
            }
            br.close();
            bw.close();
            isr.close();
            is.close();
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "File copied.");
        } catch (FileNotFoundException e) {
            SystemManagement.manageError(Boolean.FALSE, "It is not possible to create the \"testXMLSchema.xsd\" file: " + e.getMessage());
            JOptionPane.showMessageDialog(WindowManager.main, "It is not possible to create the \"testXMLSchema.xsd\" file: " + e.getMessage(), "Automatic Testing Tool", JOptionPane.WARNING_MESSAGE);
        } catch (IOException e) {
            SystemManagement.manageError(Boolean.FALSE, "It is not possible to create the \"testXMLSchema.xsd\" file: " + e.getMessage());
            JOptionPane.showMessageDialog(WindowManager.main, "It is not possible to create the \"testXMLSchema.xsd\" file: " + e.getMessage(), "Automatic Testing Tool", JOptionPane.WARNING_MESSAGE);
        }

        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Automatic Testing Tool started.");
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Now You can interact with the Program.");
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_SEPARATOR_TYPE, null);
    }

    /**
     * Manages the GUI Events.
     * @param ae an ActionEvent element that identifies the Event.
     */
    public void actionPerformed(ActionEvent ae) {
        String e = ae.getActionCommand();
        if (e.equals("New Generic File")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Clicked on \"File --> New Generic File\".");
            // Is there a text editor selected?
            if (SystemManagement.getTextEditorPath() != null) {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Opening the Text Editor to create a Generic File.");
                this.openTextEditor(null);
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Closing the Text Editor.");
            } else {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Text Editor not selected.");
                JOptionPane.showMessageDialog(WindowManager.main, "You must choose the Text Editor before You can create Generic Files.", "New Generic File", JOptionPane.WARNING_MESSAGE);
            }
        }

        if (e.equals("Open Generic File")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Clicked on \"File --> Open Generic File\".");
            // Is there a text editor selected?
            if (SystemManagement.getTextEditorPath() != null) {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Opening the Text Editor to open a Generic File.");
                this.openTextEditor(SystemManagement.getMainWorkingFolder());
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Closing the Text Editor.");
            } else {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Text Editor not selected.");
                JOptionPane.showMessageDialog(WindowManager.main, "You must choose the Text Editor before You can open Generic Files.", "Open Generic File", JOptionPane.WARNING_MESSAGE);
            }
        }

        if (e.equals("New Project")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Clicked on \"File --> New Project\".");
            // Creates the Project's Folder tree.
            String projectName = JOptionPane.showInputDialog(WindowManager.main, "What is the name of the Project?", "Create Project Folder Tree", JOptionPane.INFORMATION_MESSAGE);
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Project name: \"" + projectName + "\".");
            if (projectName != null) {
                JOptionPane.showMessageDialog(WindowManager.main, "Choose the folder where to save the Project.", "Create Project Folder Tree", JOptionPane.INFORMATION_MESSAGE);
                this.fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
                this.fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "\t(File Chooser) Opening the File Chooser on: \"" + this.fileChooser.getCurrentDirectory() + "\".");
                int returnVal = this.fileChooser.showOpenDialog(WindowManager.main);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    // Retriving and saving the filename.
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "\t(File Chooser) Project Folder selected: \"" + this.fileChooser.getSelectedFile().getAbsolutePath() + "\".");
                    // On exit...
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "\t(File Chooser) Closing the File Chooser.");
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Project Path: \"" + this.fileChooser.getSelectedFile().getAbsolutePath() + "\".");
                    // Creates the Project.
                    SystemManagement.manageProject(SystemManagement.CREATE_PROJECT_MODE, this.fileChooser.getSelectedFile().getAbsolutePath() + File.separator + projectName + File.separator);
                    JOptionPane.showMessageDialog(WindowManager.main, "Project \"" + SystemManagement.getMainWorkingFolder() + "\" created.", "Create Project Folder Tree", JOptionPane.INFORMATION_MESSAGE);
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Project \"" + SystemManagement.getMainWorkingFolder() + "\" created.");
                }
            } else
                JOptionPane.showMessageDialog(WindowManager.main, "The name of the Project is not valid.", "Create Project Folder Tree", JOptionPane.WARNING_MESSAGE);
        }

        if (e.equals("Open Project")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Clicked on \"File --> Open Project\".");
            // Chooses the Project's Folder tree.
            JOptionPane.showMessageDialog(WindowManager.main, "Choose the Project Main Folder.", "Open Project", JOptionPane.INFORMATION_MESSAGE);
            this.fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            this.fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "\t(File Chooser) Opening the File Chooser on: \"" + this.fileChooser.getCurrentDirectory() + "\".");
            int returnVal = this.fileChooser.showOpenDialog(WindowManager.main);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                // Retriving and saving the filename.
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "\t(File Chooser) Project Folder selected: \"" + this.fileChooser.getSelectedFile().getAbsolutePath() + "\".");
                // On exit...
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "\t(File Chooser) Closing the File Chooser.");
                // Opens the Project.
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Project Path: \"" + this.fileChooser.getSelectedFile().getAbsolutePath() + File.separator + "\".");
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Opening Project.");
                SystemManagement.manageProject(SystemManagement.OPEN_PROJECT_MODE, this.fileChooser.getSelectedFile().getAbsolutePath() + File.separator);
                JOptionPane.showMessageDialog(WindowManager.main, "Project \"" + SystemManagement.getMainWorkingFolder() + "\" opened.", "Open Project", JOptionPane.INFORMATION_MESSAGE);
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Project \"" + SystemManagement.getMainWorkingFolder() + "\" opened.");
            }
        }

        if (e.equals("Exit")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Clicked on \"File --> Exit\".");
            // Do You REALLY want to exit?
            if ( JOptionPane.showConfirmDialog(WindowManager.main, this.ON_EXIT, "Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION ) {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Stopping Automatic Testing Tool.");
                /*
                 * Does not need to close the log file:
                 * SystemManagement.appendToLogAndToInterface() just do this.
                 */
                SystemManagement.emptyExecutionFolder();
                System.exit(SystemManagement.PASS_EXIT_STATUS);
            }
        }

        if (e.equals("New Script")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Clicked on \"Scripts --> New Script\".");
            // Is there a text editor selected?
            if (SystemManagement.getTextEditorPath() != null) {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Opening the Text Editor to create a Script.");
                this.openTextEditor(null);
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Closing the Text Editor.");
            } else {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Text Editor not selected.");
                JOptionPane.showMessageDialog(WindowManager.main, "You must choose the Text Editor before You can create Scripts.", "New Script", JOptionPane.WARNING_MESSAGE);
            }
        }

        if (e.equals("Open Script")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Clicked on \"Scripts --> Open Script\".");
            // Is there a text editor selected?
            if (SystemManagement.getTextEditorPath() != null) {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Opening the Text Editor to modify a Script.");
                this.openTextEditor(SystemManagement.getScriptsWorkingFolder());
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Closing the Text Editor.");
            } else {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Text Editor not selected.");
                JOptionPane.showMessageDialog(WindowManager.main, "You must choose the Text Editor before You can modify Scripts.", "Open Script", JOptionPane.WARNING_MESSAGE);
            }
        }

        if (e.equals("Choose and Execute Scripts")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Clicked on \"Scripts --> Choose and Execute Scripts\".");
            // Removes all elements from the main frame.
            WindowManager.removeAllJPFromMainFrame();
            // Removes all elements from the panel.
            WindowManager.executeJP.removeAll();
            this.filenameList = new String[0];

            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Scripts Executor) Looking for Script files in \"" + SystemManagement.getScriptsWorkingFolder() + "\".");
            // Have we some script files?
            File dirScripts = new File(SystemManagement.getScriptsWorkingFolder());
            File[] dirScriptsFiles = dirScripts.listFiles();
            if (dirScriptsFiles.length != 0) {
                ArrayList<File> dirScriptsFilesFinal = new ArrayList<File>();
                for (int i = 0; i < dirScriptsFiles.length; i++)
                    // Gets only files, not directorys.
                    if (dirScriptsFiles[i].isFile() && !dirScriptsFiles[i].getAbsolutePath().endsWith("~"))
                        dirScriptsFilesFinal.add(dirScriptsFiles[i].getAbsoluteFile());
                // Sorting the files in ascendind order.
                Collections.sort(dirScriptsFilesFinal);

                this.filenameList = new String[dirScriptsFilesFinal.size()];
                for (int i = 0; i < this.filenameList.length; i++)
                    this.filenameList[i] = dirScriptsFilesFinal.get(i).getName();
            }
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Scripts Executor) Found " + this.filenameList.length + " Script files in \"" + SystemManagement.getScriptsWorkingFolder() + "\".");

            // Initializes the executeJP panel.
            this.panelTitle.setText("<html>"
                    + "                 <head></head>"
                    + "                 <body>"
                    + "                     <h2 align=\"center\">Execute Scripts</h2>"
                    + "                     <br/>"
                    + "                     <p>Choose the Scripts to execute.</p>"
                    + "                 </body>"
                    + "             </html>");
            this.setGrigBagConstraints(this.gbc, 0, 0, 3, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
            WindowManager.executeJP.add(this.panelTitle, this.gbc);

            if (this.filenameList.length != 0)
                this.numberOf.setText("There are " + this.filenameList.length + " Script Files in the \"" + SystemManagement.getScriptsWorkingFolder() + "\" Project's folder. Which Scripts do You want to execute?");
            else
                this.numberOf.setText("There are " + this.filenameList.length + " Script Files in the \"" + SystemManagement.getScriptsWorkingFolder() + "\" Project's folder. You must create a New Project or open a Existing Project.");
            this.setGrigBagConstraints(this.gbc, 0, 1, 4, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
            WindowManager.executeJP.add(this.numberOf, this.gbc);

            this.allElementsList.setListData(filenameList);
            JScrollPane ls = new JScrollPane(this.allElementsList);
            ls.setSize(new Dimension(400, 400));
            ls.setMaximumSize(new Dimension(400, 400));
            ls.setMinimumSize(new Dimension(400, 400));
            ls.setPreferredSize(new Dimension(400, 400));
            this.setGrigBagConstraints(this.gbc, 0, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
            WindowManager.executeJP.add(ls, this.gbc);

            JPanel buttonsJP = new JPanel(new GridBagLayout());
            GridBagConstraints buttonsGBC = new GridBagConstraints();
            buttonsGBC.insets = new Insets(10, 10, 10, 10);
            this.setGrigBagConstraints(buttonsGBC, 0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
            buttonsJP.add(this.addItemToButton, buttonsGBC);
            this.setGrigBagConstraints(buttonsGBC, 0, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
            buttonsJP.add(this.removeItemFromButton, buttonsGBC);
            this.setGrigBagConstraints(this.gbc, 1, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
            WindowManager.executeJP.add(buttonsJP, this.gbc);

            this.selectedElementsList.setModel(new DefaultListModel());
            JScrollPane ls2 = new JScrollPane(this.selectedElementsList);
            ls2.setSize(new Dimension(400, 400));
            ls2.setMaximumSize(new Dimension(400, 400));
            ls2.setMinimumSize(new Dimension(400, 400));
            ls2.setPreferredSize(new Dimension(400, 400));
            this.setGrigBagConstraints(this.gbc, 2, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
            WindowManager.executeJP.add(ls2, this.gbc);

            JPanel moveButtonsJP = new JPanel(new GridBagLayout());
            GridBagConstraints moveButtonsGBC = new GridBagConstraints();
            moveButtonsGBC.insets = new Insets(10, 10, 10, 10);
            this.setGrigBagConstraints(moveButtonsGBC, 0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
            moveButtonsJP.add(this.moveUpButton, moveButtonsGBC);
            this.setGrigBagConstraints(moveButtonsGBC, 0, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
            moveButtonsJP.add(this.moveDownButton, moveButtonsGBC);
            this.setGrigBagConstraints(this.gbc, 3, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
            WindowManager.executeJP.add(moveButtonsJP, this.gbc);

            JPanel finalButtonsJP = new JPanel(new GridBagLayout());
            GridBagConstraints finalButtonsGBC = new GridBagConstraints();
            finalButtonsGBC.insets = new Insets(10, 10, 10, 10);
            this.executeButton.setText("Execute Scripts");
            this.setGrigBagConstraints(finalButtonsGBC, 0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE);
            finalButtonsJP.add(this.executeButton, finalButtonsGBC);
            this.setGrigBagConstraints(finalButtonsGBC, 1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
            finalButtonsJP.add(this.cancelButton, finalButtonsGBC);
            this.setGrigBagConstraints(this.gbc, 2, 3, 2, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE);
            WindowManager.executeJP.add(finalButtonsJP, this.gbc);
            // Refreshing the main frame.
            WindowManager.setAJPInTheMainFrame(WindowManager.executeJP);
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "\"Choose and Execute Scripts\" Interface loaded.");
        }

        if (e.equals("Return to the \"Choose and Execute Scripts\" Menu")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Returned to the \"Scripts --> Choose and Execute Scripts\" Menu.");
            // Removes all elements from the main frame.
            WindowManager.removeAllJPFromMainFrame();
            this.panelTitle.setText("<html>"
                    + "                 <head></head>"
                    + "                 <body>"
                    + "                     <h2 align=\"center\">Execute Scripts</h2>"
                    + "                     <br/>"
                    + "                     <p>Choose the Scripts to execute.</p>"
                    + "                 </body>"
                    + "             </html>");
            this.setGrigBagConstraints(this.gbc, 0, 0, 3, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
            WindowManager.executeJP.add(this.panelTitle, this.gbc);

            JPanel finalButtonsJP = new JPanel(new GridBagLayout());
            GridBagConstraints finalButtonsGBC = new GridBagConstraints();
            finalButtonsGBC.insets = new Insets(10, 10, 10, 10);
            this.executeButton.setText("Execute Scripts");
            this.setGrigBagConstraints(finalButtonsGBC, 0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE);
            finalButtonsJP.add(this.executeButton, finalButtonsGBC);
            this.setGrigBagConstraints(finalButtonsGBC, 1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
            finalButtonsJP.add(this.cancelButton, finalButtonsGBC);
            this.setGrigBagConstraints(this.gbc, 2, 3, 2, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE);
            WindowManager.executeJP.add(finalButtonsJP, this.gbc);
            // Refreshing the main frame.
            WindowManager.setAJPInTheMainFrame(WindowManager.executeJP);
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "\"Choose and Execute Scripts\" Interface loaded.");
        }

        if (e.equals("Test Builder")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Clicked on \"Tests --> Test Builder\".");
            // Removes all elements from the main frame.
            WindowManager.removeAllJPFromMainFrame();
            // Removes all elements from the panel.
            WindowManager.testBuilderJP.removeAll();

            // Initializes the testCaseBuilderJP panel.
            this.panelTitle.setText("<html>"
                    + "                 <head></head>"
                    + "                 <body>"
                    + "                     <h2 align=\"center\">Test Builder</h2>"
                    + "                     <br/>"
                    + "                     <p>Fill the Test Case/Suite's information and then start the Test Case Builder or the Test Suite Builder.</p>"
                    + "                     <p>Remember: the fields can not contain characters like &lt;, &gt;, &#39;, &quot; or accented letters.</p>"
                    + "                 </body>"
                    + "             </html>");
            this.setGrigBagConstraints(this.gbc, 0, 0, 2, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
            WindowManager.testBuilderJP.add(this.panelTitle, this.gbc);

            this.tcFilenameLabel = new JLabel("<html>"
                    + "                             <head></head>"
                    + "                             <body>"
                    + "                                 <b><u>Unique</u></b> Name of the XTD File:"
                    + "                             </body>"
                    + "                        </html>");
            this.setGrigBagConstraints(this.gbc, 0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE);
            WindowManager.testBuilderJP.add(this.tcFilenameLabel, this.gbc);
            this.tcFilenameText = new JTextField();
            this.setGrigBagConstraints(this.gbc, 1, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL);
            WindowManager.testBuilderJP.add(this.tcFilenameText, this.gbc);

            this.tcTitleLabel = new JLabel("Title:");
            this.setGrigBagConstraints(this.gbc, 0, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE);
            WindowManager.testBuilderJP.add(this.tcTitleLabel, this.gbc);
            this.tcTitleText = new JTextField();
            this.setGrigBagConstraints(this.gbc, 1, 2, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL);
            WindowManager.testBuilderJP.add(this.tcTitleText, this.gbc);

            this.tcAuthorLabel = new JLabel("Author:");
            this.setGrigBagConstraints(this.gbc, 0, 3, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE);
            WindowManager.testBuilderJP.add(this.tcAuthorLabel, this.gbc);
            this.tcAuthorText = new JTextField();
            this.setGrigBagConstraints(this.gbc, 1, 3, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL);
            WindowManager.testBuilderJP.add(this.tcAuthorText, this.gbc);

            this.tcContributorLabel = new JLabel("Contributor (optional):");
            this.setGrigBagConstraints(this.gbc, 0, 4, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE);
            WindowManager.testBuilderJP.add(this.tcContributorLabel, this.gbc);
            this.tcContributorText = new JTextField();
            this.setGrigBagConstraints(this.gbc, 1, 4, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL);
            WindowManager.testBuilderJP.add(this.tcContributorText, this.gbc);

            this.tcDateLabel = new JLabel("Creation Date:");
            this.setGrigBagConstraints(this.gbc, 0, 5, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE);
            WindowManager.testBuilderJP.add(this.tcDateLabel, this.gbc);

            this.tcDateValueLabel = new JLabel(SystemManagement.getCompactDate().substring(0, 4) + "-" + SystemManagement.getCompactDate().substring(4, 6) + "-" + SystemManagement.getCompactDate().substring(6, 8));
            this.setGrigBagConstraints(this.gbc, 1, 5, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE);
            WindowManager.testBuilderJP.add(this.tcDateValueLabel, this.gbc);

            this.tcStatusLabel = new JLabel("Status:");
            this.setGrigBagConstraints(this.gbc, 0, 6, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE);
            WindowManager.testBuilderJP.add(this.tcStatusLabel, this.gbc);
            String[] statusArray = {"accepted", "draft", "pendingBugfix", "rejected"};
            this.tcStatusCombo = new JComboBox(statusArray);
            this.tcStatusCombo.setSelectedIndex(0);
            this.setGrigBagConstraints(this.gbc, 1, 6, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL);
            WindowManager.testBuilderJP.add(this.tcStatusCombo, this.gbc);

            this.tcVersionLabel = new JLabel("Version:");
            this.setGrigBagConstraints(this.gbc, 0, 7, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE);
            WindowManager.testBuilderJP.add(this.tcVersionLabel, this.gbc);
            this.tcVersionText = new JTextField();
            this.setGrigBagConstraints(this.gbc, 1, 7, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL);
            WindowManager.testBuilderJP.add(this.tcVersionText, this.gbc);

            this.tcDescriptionLabel = new JLabel("Description:");
            this.setGrigBagConstraints(this.gbc, 0, 8, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE);
            WindowManager.testBuilderJP.add(this.tcDescriptionLabel, this.gbc);
            this.tcDescriptionTArea = new JTextArea();
            this.tcDescriptionTArea.setLineWrap(true);
            JScrollPane sp6 = new JScrollPane();
            sp6.setViewportView(this.tcDescriptionTArea);
            this.setGrigBagConstraints(this.gbc, 1, 8, 1, 1, 0.5, 0.5, GridBagConstraints.EAST, GridBagConstraints.BOTH);
            WindowManager.testBuilderJP.add(sp6, this.gbc);

            this.tcPurposeLabel = new JLabel("Purpose:");
            this.setGrigBagConstraints(this.gbc, 0, 9, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE);
            WindowManager.testBuilderJP.add(this.tcPurposeLabel, this.gbc);
            this.tcPurposeTArea = new JTextArea();
            this.tcPurposeTArea.setLineWrap(true);
            JScrollPane sp7 = new JScrollPane();
            sp7.setViewportView(this.tcPurposeTArea);
            this.setGrigBagConstraints(this.gbc, 1, 9, 1, 1, 0.5, 0.5, GridBagConstraints.EAST, GridBagConstraints.BOTH);
            WindowManager.testBuilderJP.add(sp7, this.gbc);

            this.tcPreconditionsLabel = new JLabel("Preconditions (optional):");
            this.setGrigBagConstraints(this.gbc, 0, 10, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE);
            WindowManager.testBuilderJP.add(this.tcPreconditionsLabel, this.gbc);
            this.tcPreconditionsTArea = new JTextArea();
            this.tcPreconditionsTArea.setLineWrap(true);
            JScrollPane sp8 = new JScrollPane();
            sp8.setViewportView(this.tcPreconditionsTArea);
            this.setGrigBagConstraints(this.gbc, 1, 10, 1, 1, 0.5, 0.5, GridBagConstraints.EAST, GridBagConstraints.BOTH);
            WindowManager.testBuilderJP.add(sp8, this.gbc);

            this.tcNotesLabel = new JLabel("Internal Notes (optional):");
            this.setGrigBagConstraints(this.gbc, 0, 11, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE);
            WindowManager.testBuilderJP.add(this.tcNotesLabel, this.gbc);
            this.tcNotesTArea = new JTextArea();
            this.tcNotesTArea.setLineWrap(true);
            JScrollPane sp9 = new JScrollPane();
            sp9.setViewportView(this.tcNotesTArea);
            this.setGrigBagConstraints(this.gbc, 1, 11, 1, 1, 0.5, 0.5, GridBagConstraints.EAST, GridBagConstraints.BOTH);
            WindowManager.testBuilderJP.add(sp9, this.gbc);

            this.tcSelectProgramToExecuteLabel = new JLabel("Do You want to select an application to start?");
            this.setGrigBagConstraints(this.gbc, 0, 12, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE);
            WindowManager.testBuilderJP.add(this.tcSelectProgramToExecuteLabel, this.gbc);
            this.tcSelectProgramToExecuteCB = new JCheckBox();
            this.tcSelectProgramToExecuteCB.setSelected(true);
            this.setGrigBagConstraints(this.gbc, 1, 12, 1, 1, 0.5, 0.5, GridBagConstraints.EAST, GridBagConstraints.BOTH);
            WindowManager.testBuilderJP.add(this.tcSelectProgramToExecuteCB, this.gbc);


            JPanel internalPanel = new JPanel(new GridBagLayout());
            GridBagConstraints internalGBC = new GridBagConstraints();
            this.setGrigBagConstraints(internalGBC, 0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE);
            internalGBC.insets = new Insets(0, 10, 0, 10);
            this.executeButton.setText("Execute Test Case Builder");
            internalPanel.add(this.executeButton, internalGBC);
            this.continueButton.setText("Build Test Suite");
            this.setGrigBagConstraints(internalGBC, 1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
            internalPanel.add(this.continueButton, internalGBC);
            this.setGrigBagConstraints(internalGBC, 2, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE);
            internalPanel.add(this.cancelButton, internalGBC);
            this.setGrigBagConstraints(this.gbc, 0, 13, 2, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
            WindowManager.testBuilderJP.add(internalPanel, this.gbc);
            // Refreshing the main frame.
            WindowManager.setAJPInTheMainFrame(WindowManager.testBuilderJP);
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "\"Test Builder\" Interface loaded.");
        }

        if (e.equals("Return to the \"Test Builder\" Menu")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Returned to the \"Tests --> Test Builder\" Menu.");
            // Removes all elements from the main frame.
            WindowManager.removeAllJPFromMainFrame();
            this.panelTitle.setText("<html>"
                    + "                 <head></head>"
                    + "                 <body>"
                    + "                     <h2 align=\"center\">Test Builder</h2>"
                    + "                     <br/>"
                    + "                     <p>Fill the Test Case/Suite's information and then start the Test Case Builder or the Test Suite Builder.</p>"
                    + "                     <p>Remember: the fields can not contain characters like \"&lt;\" or \"&gt;\".</p>"
                    + "                 </body>"
                    + "             </html>");
            this.setGrigBagConstraints(this.gbc, 0, 0, 2, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
            WindowManager.testBuilderJP.add(this.panelTitle, this.gbc);
            JPanel internalPanel = new JPanel(new GridBagLayout());
            GridBagConstraints internalGBC = new GridBagConstraints();
            this.setGrigBagConstraints(internalGBC, 0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE);
            internalGBC.insets = new Insets(0, 10, 0, 10);
            this.executeButton.setText("Execute Test Case Builder");
            internalPanel.add(this.executeButton, internalGBC);
            this.continueButton.setText("Build Test Suite");
            this.setGrigBagConstraints(internalGBC, 1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
            internalPanel.add(this.continueButton, internalGBC);
            this.setGrigBagConstraints(internalGBC, 2, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE);
            internalPanel.add(this.cancelButton, internalGBC);
            this.setGrigBagConstraints(this.gbc, 1, 13, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL);
            WindowManager.testBuilderJP.add(internalPanel, this.gbc);
            // Refreshing the main frame.
            WindowManager.setAJPInTheMainFrame(WindowManager.testBuilderJP);
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "\"Test Builder\" Interface loaded.");
        }

        if (e.equals("Open and Validate Test File")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Clicked on \"Tests --> Open and Validate Test File\".");
            // Is there a text editor selected?
            if (SystemManagement.getTextEditorPath() != null) {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Opening the Text Editor to Open and Validate Test Files.");
                this.openTextEditor(SystemManagement.getTestsWorkingFolder());
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Closing the Text Editor.");
            } else {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Text Editor not selected.");
                JOptionPane.showMessageDialog(WindowManager.main, "You must choose the Text Editor before You can modify Test Files.", "Open and Validate Test Files", JOptionPane.WARNING_MESSAGE);
            }
        }

        if (e.equals("Execute Tests")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Clicked on \"Tests --> Execute Tests\".");
            // Removes all elements from the main frame.
            WindowManager.removeAllJPFromMainFrame();
            // Removes all elements from the panel.
            WindowManager.executeJP.removeAll();
            this.filenameList = new String[0];
            this.allElementsList.setListData(new Object[0]);
            this.selectedElementsList.setListData(new Object[0]);

            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor) Looking for Test files in \"" + SystemManagement.getTestsWorkingFolder() + "\".");
            // Have we some test files?
            File dirTests = new File(SystemManagement.getTestsWorkingFolder());
            File[] dirTestsFilesTemp = dirTests.listFiles();
            if (dirTestsFilesTemp.length != 0) {
                ArrayList<File> dirTestsFilesFinal = new ArrayList<File>();
                for (int i = 0; i < dirTestsFilesTemp.length; i++)
                    // Gets only files, not directorys.
                    if (dirTestsFilesTemp[i].isFile())
                        // Gets only xtd files, not files with other extensions.
                        if (dirTestsFilesTemp[i].getName().endsWith(".xtd") && !dirTestsFilesTemp[i].getAbsolutePath().endsWith(".xtd~"))
                            dirTestsFilesFinal.add(dirTestsFilesTemp[i].getAbsoluteFile());
                // Sorting the files in ascendind order.
                Collections.sort(dirTestsFilesFinal);

                this.filenameList = new String[dirTestsFilesFinal.size()];
                for (int i = 0; i < this.filenameList.length; i++)
                    this.filenameList[i] = dirTestsFilesFinal.get(i).getName();
            }
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor) Found " + this.filenameList.length + " Test files in \"" + SystemManagement.getTestsWorkingFolder() + "\".");

            // Initializes the executeTestsJP panel.
            this.setGrigBagConstraints(this.gbc, 0, 0, 3, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
            this.panelTitle.setText("<html>"
                    + "                 <head></head>"
                    + "                 <body>"
                    + "                     <h2 align=\"center\">Execute Tests</h2>"
                    + "                     <br/>"
                    + "                     <p>Choose the Tests to execute.</p>"
                    + "                 </body>"
                    + "              </html>");
            WindowManager.executeJP.add(this.panelTitle, this.gbc);

            if (this.filenameList.length != 0)
                this.numberOf.setText("There are " + this.filenameList.length + " Test Files in the \"" + SystemManagement.getTestsWorkingFolder() + "\" Project's folder. Which Tests do You want to execute?");
            else
                this.numberOf.setText("There are " + this.filenameList.length + " Test Files in the \"" + SystemManagement.getTestsWorkingFolder() + "\" Project's folder. You must create a New Project or open a Existing Project.");
            this.setGrigBagConstraints(this.gbc, 0, 1, 4, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
            WindowManager.executeJP.add(this.numberOf, this.gbc);

            this.allElementsList.setListData(this.filenameList);
            JScrollPane ls = new JScrollPane(this.allElementsList);
            ls.setSize(new Dimension(400, 400));
            ls.setMaximumSize(new Dimension(400, 400));
            ls.setMinimumSize(new Dimension(400, 400));
            ls.setPreferredSize(new Dimension(400, 400));
            this.setGrigBagConstraints(this.gbc, 0, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
            WindowManager.executeJP.add(ls, this.gbc);

            JPanel buttonsJP = new JPanel(new GridBagLayout());
            GridBagConstraints buttonsGBC = new GridBagConstraints();
            buttonsGBC.insets = new Insets(10, 10, 10, 10);
            this.setGrigBagConstraints(buttonsGBC, 0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
            buttonsJP.add(this.addItemToButton, buttonsGBC);
            this.setGrigBagConstraints(buttonsGBC, 0, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
            buttonsJP.add(this.removeItemFromButton, buttonsGBC);
            this.setGrigBagConstraints(this.gbc, 1, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
            WindowManager.executeJP.add(buttonsJP, this.gbc);

            this.selectedElementsList.setModel(new DefaultListModel());
            JScrollPane ls2 = new JScrollPane(this.selectedElementsList);
            ls2.setSize(new Dimension(400, 400));
            ls2.setMaximumSize(new Dimension(400, 400));
            ls2.setMinimumSize(new Dimension(400, 400));
            ls2.setPreferredSize(new Dimension(400, 400));
            this.setGrigBagConstraints(this.gbc, 2, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
            WindowManager.executeJP.add(ls2, this.gbc);

            JPanel moveButtonsJP = new JPanel(new GridBagLayout());
            GridBagConstraints moveButtonsGBC = new GridBagConstraints();
            moveButtonsGBC.insets = new Insets(10, 10, 10, 10);
            this.setGrigBagConstraints(moveButtonsGBC, 0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
            moveButtonsJP.add(this.moveUpButton, moveButtonsGBC);
            this.setGrigBagConstraints(moveButtonsGBC, 0, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
            moveButtonsJP.add(this.moveDownButton, moveButtonsGBC);
            this.setGrigBagConstraints(this.gbc, 3, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
            WindowManager.executeJP.add(moveButtonsJP, this.gbc);

            JPanel finalButtonsJP = new JPanel(new GridBagLayout());
            GridBagConstraints finalButtonsGBC = new GridBagConstraints();
            finalButtonsGBC.insets = new Insets(10, 10, 10, 10);
            this.executeButton.setText("Execute");
            this.setGrigBagConstraints(finalButtonsGBC, 0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE);
            finalButtonsJP.add(this.executeButton, finalButtonsGBC);
            this.setGrigBagConstraints(finalButtonsGBC, 1, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE);
            finalButtonsJP.add(this.cancelButton, finalButtonsGBC);
            this.setGrigBagConstraints(this.gbc, 2, 3, 2, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE);
            WindowManager.executeJP.add(finalButtonsJP, this.gbc);
            // Refreshing the main frame.
            WindowManager.setAJPInTheMainFrame(WindowManager.executeJP);
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor) \"Execute Tests\" Interface loaded.");
        }

        if (e.equals("Return to the \"Execute Tests\" Menu")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Returned to the \"Tests --> Execute Tests\" menu.");
            // Removes all elements from the main frame.
            WindowManager.removeAllJPFromMainFrame();
            JPanel finalButtonsJP = new JPanel(new GridBagLayout());
            GridBagConstraints finalButtonsGBC = new GridBagConstraints();
            finalButtonsGBC.insets = new Insets(10, 10, 10, 10);
            this.executeButton.setText("Execute");
            this.setGrigBagConstraints(finalButtonsGBC, 0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE);
            finalButtonsJP.add(this.executeButton, finalButtonsGBC);
            this.setGrigBagConstraints(finalButtonsGBC, 1, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE);
            finalButtonsJP.add(this.cancelButton, finalButtonsGBC);
            this.setGrigBagConstraints(this.gbc, 2, 3, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE);
            WindowManager.executeJP.add(finalButtonsJP, this.gbc);
            // Refreshing the main frame.
            WindowManager.setAJPInTheMainFrame(WindowManager.executeJP);
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "\"Execute Tests\" Interface loaded.");
        }

        if (e.equals("Open and Validate Report File")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Clicked on \"Tests --> Open and Validate Report File\".");
            // Is there a text editor selected?
            if (SystemManagement.getTextEditorPath() != null) {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Opening the Text Editor to Open and Validate Report File.");
                this.openTextEditor(SystemManagement.getResultsWorkingFolder());
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Closing the Text Editor.");
            } else {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Text Editor not selected.");
                JOptionPane.showMessageDialog(WindowManager.main, "You must choose the Text Editor before You can modify Report Files.", "Open and Validate Report Files", JOptionPane.WARNING_MESSAGE);
            }
        }

        if (e.equals("Validate Tests")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Returned to the \"Tests --> Validate Tests\" menu.");
            JOptionPane.showMessageDialog(WindowManager.main, "Choose the report file of the Test Case or Test Suite to validate.", "Results Validator", JOptionPane.INFORMATION_MESSAGE);
            this.fileChooser.setCurrentDirectory(new File(SystemManagement.getResultsWorkingFolder()));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("XML for Tests Reports Files [.xtr]", "xtr");
            this.fileChooser.setFileFilter(filter);
            this.fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            // Openiong the File Chooser to choose the file (script or utility file or test) to modify.
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "\t(File Chooser) Opening the File Chooser on: \"" + this.fileChooser.getCurrentDirectory() + "\".");
            int returnVal = this.fileChooser.showOpenDialog(WindowManager.main);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                // Retriving and saving the filename.
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "\t(File Chooser) File XTR selected: \"" + this.fileChooser.getSelectedFile().getAbsolutePath() + "\".");
                // On exit...
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "\t(File Chooser) Exit from the File Chooser.");
                // Removing the filter
                this.fileChooser.removeChoosableFileFilter(filter);
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Results Validator) XTR File: \"" + this.fileChooser.getSelectedFile().getAbsolutePath() + "\".");
                /*
                 * Starts the TC/TS validation if the TC/TS status is equals to
                 * "pending" or "fail" and if there is at least one screenshot with
                 * the status equals to "pending".
                 */
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Results Validator) Checking if the XTR file has screenshots to validate.");
                try {
                    // Useful to open the result file.
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document doc = db.parse(this.fileChooser.getSelectedFile().getAbsolutePath());
                    doc.getDocumentElement().normalize();
                    // Gets the status of the TC or the TS.
                    NodeList statusNodes = doc.getElementsByTagName("status");
                    String parentNodeName = statusNodes.item(0).getParentNode().getNodeName();
                    Boolean hasAPendingScreenshot = Boolean.FALSE;
                    NodeList screenshotNodes = doc.getElementsByTagName("screenshot");
                    if (screenshotNodes.getLength() != 0) {
                        String screenshotStatus = null;
                        for (int i = 0; i < screenshotNodes.getLength(); i++) {
                            screenshotStatus = screenshotNodes.item(i).getChildNodes().item(7).getChildNodes().item(0).getNodeValue();
                            if (screenshotStatus.equals(ResultsValidator.PENDING_STATUS))
                                hasAPendingScreenshot = Boolean.TRUE;
                        }
                        if (hasAPendingScreenshot) {
                            // Validates screenshots.
                            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Results Validator) There are screenshots to validate.");
                            if (parentNodeName.equals("testSuite")) {
                                // Results.
                                WindowManager.removeAllJPFromMainFrame();
                                // Removes all elements from the panel.
                                WindowManager.resultsJP.removeAll();

                                // Initializes the resultsJP panel.
                                this.panelTitle.setText("<html><head></head><body><h2 align=\"center\">Test To Validate</h2></body></html>");
                                this.setGrigBagConstraints(this.gbc, 0, 0, 3, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE);
                                WindowManager.resultsJP.add(this.panelTitle, this.gbc);

                                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Results Validator) Creating the Results Table.");
                                // Creates the results table
                                Object[] columnNames = {"Test Suite Name", "Test Case Name", "Test Success Status", "Screenshots to Check"};
                                ArrayList<Object[]> dataAL = new ArrayList<Object[]>();
                                // Test Suite case.
                                String tsStatus = null;
                                String tsName = null;
                                String tcVerify = null;
                                try {
                                    // Useful to open the result file.
                                    DocumentBuilderFactory dbf2 = DocumentBuilderFactory.newInstance();
                                    DocumentBuilder db2 = dbf2.newDocumentBuilder();
                                    Document doc2 = db2.parse(this.fileChooser.getSelectedFile());
                                    doc.getDocumentElement().normalize();
                                    // Gets the TS name.
                                    tsName = this.fileChooser.getSelectedFile().getAbsolutePath();
                                    // Gets the TS status.
                                    NodeList statusNodes2 = doc2.getElementsByTagName("status");
                                    tsStatus = statusNodes2.item(0).getChildNodes().item(0).getNodeValue();
                                    // Gets the TS child nodes (TC).
                                    NodeList tcNodes = doc.getElementsByTagName("testCase");
                                    for (int j = 0; j < tcNodes.getLength(); j++) {
                                        // Writes the TS name only for the first TC.
                                        Object[] tmpA = new Object[4];
                                        if (j == 0)
                                            tmpA[0] = (tsStatus.equals(SystemManagement.PASS_STATUS))? "<html><head></head><body><p color=\"green\">" + tsName.substring((tsName.lastIndexOf(File.separator) + 1)) + "</p></body></html>":((tsStatus.equals(SystemManagement.PENDING_STATUS))? "<html><head></head><body><p color=\"#FF9900\">" + tsName.substring((tsName.lastIndexOf(File.separator) + 1)) + "</p></body></html>":"<html><head></head><body><p color=\"red\"><b>" + tsName.substring((tsName.lastIndexOf(File.separator) + 1)) + "</b></p></body></html>");
                                        else
                                            tmpA[0] = "";
                                        // Gets TC name.
                                        String tcName = tcNodes.item(j).getChildNodes().item(1).getChildNodes().item(0).getNodeValue();
                                        tcName = tcName.substring(tcName.lastIndexOf(File.separator) + 1);
                                        tmpA[1] = tcName;
                                        // Gets TC status.
                                        String tcStatus  = tcNodes.item(j).getChildNodes().item(3).getChildNodes().item(0).getNodeValue();
                                        tmpA[2] = (tcStatus.equals(SystemManagement.PASS_STATUS))? "<html><head></head><body><p color=\"green\">" + SystemManagement.PASS_STATUS + "</p></body></html>":((tcStatus.equals(SystemManagement.PENDING_STATUS)? "<html><head></head><body><p color=\"#FF9900\">" + SystemManagement.PENDING_STATUS + "</p></body></html>":((tcStatus.equals(SystemManagement.FAIL_STATUS)? "<html><head></head><body><p color=\"red\"><b>" + SystemManagement.FAIL_STATUS + "</b></p></body></html>":"<html><head></head><body><p color=\"red\"><b>" + SystemManagement.ERROR_STATUS + "</b></p></body></html>"))));
                                        /*
                                         * Gets all the screenshotsToVerify nodes.
                                         * Useful to avoid null Exception.
                                         */
                                        NodeList verifyNodes = doc.getElementsByTagName("screenshot");
                                        if (verifyNodes.getLength() == 0)
                                            tcVerify = "<html><head></head><body><p color=\"green\">No Screenshots To Check</p></body></html>";
                                        else {
                                            // Checks for screenshots to verify.
                                            if (tcNodes.item(j).getChildNodes().getLength() > 5) {
                                                int screenshotsNumber = 0;
                                                for (int n = 0; n < tcNodes.item(j).getChildNodes().item(5).getChildNodes().getLength(); n++)
                                                    if (!tcNodes.item(j).getChildNodes().item(5).getChildNodes().item(n).getNodeName().equals("#text"))
                                                        screenshotsNumber++;
                                                if (screenshotsNumber != 0)
                                                    tcVerify = "<html><head></head><body><p id=\"" + tsName + "#" + tcName.substring((tcName.lastIndexOf(File.separator) + 1), tcName.lastIndexOf('.')) + "\" color=\"red\"><b>Check Screenshots (" + screenshotsNumber + ")</b></p></body></html>";
                                                else
                                                    tcVerify = "<html><head></head><body><p color=\"green\">No Screenshots To Check</p></body></html>";
                                            } else
                                                tcVerify = "<html><head></head><body><p color=\"green\">No Screenshots To Check</p></body></html>";
                                        }
                                        tmpA[3] = tcVerify;
                                        dataAL.add(tmpA);
                                    }
                                    Object[][] data = new Object[dataAL.size()][4];
                                    for (int i = 0; i < data.length; i++)
                                        System.arraycopy(dataAL.get(i), 0, data[i], 0, data[i].length);
                                    JTable resultsTable = new JTable(data, columnNames);
                                    resultsTable.setPreferredScrollableViewportSize(new Dimension((WindowManager.main.getWidth() - 40), 500));
                                    resultsTable.setFillsViewportHeight(true);
                                    resultsTable.setShowGrid(true);
                                    resultsTable.setDragEnabled(false);
                                    resultsTable.setColumnSelectionAllowed(false);
                                    resultsTable.setRowSelectionAllowed(false);
                                    resultsTable.setCellSelectionEnabled(false);
                                    resultsTable.getColumnModel().getColumn(3).setCellEditor(new ResultsTableEditor());
                                    JScrollPane pane = new JScrollPane(resultsTable);
                                    this.setGrigBagConstraints(this.gbc, 0, 1, 3, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE);
                                    WindowManager.resultsJP.add(pane, this.gbc);
                                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Results Validator) Results Table created.");

                                    JPanel finalButtonsJP = new JPanel(new GridBagLayout());
                                    GridBagConstraints finalButtonsGBC = new GridBagConstraints();
                                    finalButtonsGBC.insets = new Insets(10, 10, 10, 10);
                                    this.executeButton.setText("Open a Report File");
                                    this.setGrigBagConstraints(finalButtonsGBC, 0, 0, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE);
                                    finalButtonsJP.add(this.executeButton, finalButtonsGBC);
                                    this.setGrigBagConstraints(finalButtonsGBC, 1, 0, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE);
                                    finalButtonsJP.add(this.cancelButton, finalButtonsGBC);
                                    this.setGrigBagConstraints(this.gbc, 0, 2, 3, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE);
                                    WindowManager.resultsJP.add(finalButtonsJP, this.gbc);
                                    // Refreshing the main frame.
                                    WindowManager.setAJPInTheMainFrame(WindowManager.resultsJP);
                                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Results Validator) \"Results\" Interface loaded.");
                                } catch (ParserConfigurationException ee) {
                                    SystemManagement.manageError(Boolean.TRUE, "(Test Executor) The report file \"" + this.fileChooser.getSelectedFile().getAbsolutePath() + "\" is not a valid results file: " + ee.getMessage());
                                } catch (SAXException ee) {
                                    SystemManagement.manageError(Boolean.TRUE, "(Test Executor) The report file \"" + this.fileChooser.getSelectedFile().getAbsolutePath() + "\" is not a valid results file: " + ee.getMessage());
                                } catch (IOException ee) {
                                    SystemManagement.manageError(Boolean.TRUE, "(Test Executor) The report file \"" + this.fileChooser.getSelectedFile().getAbsolutePath() + "\" is not a valid results file: " + ee.getMessage());
                                }
                            } else {
                                JOptionPane.showMessageDialog(WindowManager.main, "<html><head></head><body><p>To interact with the ATT Results Validator, You can:<ul><li>use a single left click to accept the screenshot and pass to the next screenshot;</li><li>use a single right click to reject the screenshot and pass to the next screenshot;</li><li>use a single central click (or wheel click) to set the pending status to the screenshot and pass to the next screenshot.</li></ul></p><p>When the screenshots are finished, the program will return to the Results Table.</p></body></html>", "Results Validator", JOptionPane.INFORMATION_MESSAGE);
                                WindowManager.main.setState(JFrame.ICONIFIED);
                                WindowManager.resultsValidator = new JFrame("Automatic Testing Tool - Test Results Validator");
                                WindowManager.resultsValidator.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Results Validator) Starting the Results Validator.");
                                ResultsValidator panel = new ResultsValidator(null, this.fileChooser.getSelectedFile().getAbsolutePath());
                                WindowManager.resultsValidator.getContentPane().add(panel);
                                WindowManager.resultsValidator.pack();
                                WindowManager.resultsValidator.addMouseListener(panel);
                                WindowManager.resultsValidator.setVisible(true);
                                WindowManager.resultsValidator.toFront();
                            }
                        } else {
                            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Results Validator) The Test does not have screenshots to validate.");
                            JOptionPane.showMessageDialog(WindowManager.main, "The Test does not have screenshots to validate.", "Results Validator - Screenshots Status", JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Results Validator) The Test does not have screenshots.");
                        JOptionPane.showMessageDialog(WindowManager.main, "The Test does not have screenshots.", "Results Validator - Screenshots Status", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (ParserConfigurationException ee) {
                    SystemManagement.manageError(Boolean.TRUE, "(Test Executor) The report file \"" + this.fileChooser.getSelectedFile().getAbsolutePath() + "\" is not a valid report file: " + ee.getMessage());
                } catch (SAXException ee) {
                    SystemManagement.manageError(Boolean.TRUE, "(Test Executor) The report file \"" + this.fileChooser.getSelectedFile().getAbsolutePath() + "\" is not a valid report file: " + ee.getMessage());
                } catch (IOException ee) {
                    SystemManagement.manageError(Boolean.TRUE, "(Test Executor) The report file \"" + this.fileChooser.getSelectedFile().getAbsolutePath() + "\" is not a valid report file: " + ee.getMessage());
                }
            }
        }

        if (e.equals("Choose Cygwin Path")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Clicked on \"Tools --> Choose Cygwin Path\".");
            FileNameExtensionFilter filter = null;
            /**
             * Don't need to check the OS because this menu item is enabled only
             * in Windows.
             */
            if (SystemManagement.getOSName().contains("Windows XP"))
                this.fileChooser.setCurrentDirectory(new File(SystemManagement.WINDOWSXP_GENERIC_PROGRAM_PATH));
            else
                this.fileChooser.setCurrentDirectory(new File(SystemManagement.WINDOWS_GENERIC_PROGRAM_PATH));
            this.fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            filter = new FileNameExtensionFilter("Windows EXE Files", "exe");
            this.fileChooser.setFileFilter(filter);

            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Choosing the Cygwin Path.");
            // Openiong the File Chooser to choose the cygwin application.
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "\t(File Chooser) Opening the File Chooser on: \"" + this.fileChooser.getCurrentDirectory() + "\".");
            // Retriving and saving the cygwin path name.
            int returnVal = this.fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                SystemManagement.setCygwinPath(this.fileChooser.getSelectedFile().getAbsolutePath());
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "\t(File Chooser) Cygwin Path: \"" + this.fileChooser.getSelectedFile().getAbsolutePath() + "\".");
            }
            // On exit... Removing the file filter
            this.fileChooser.removeChoosableFileFilter(filter);
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "\t(File Chooser) Closing the File Chooser.");
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Cygwin Path: \"" + SystemManagement.getCygwinPath() + "\".");
        }

        if (e.equals("Choose Text Editor")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Clicked on \"Tools --> Choose Text Editor\".");
            FileNameExtensionFilter filter = null;
            /*
             * Control about the OS: Is used the "contains" method because in
             * Windows-based OS, the name is composed by "Windows" plus the
             * specific name of OS (like XP, Vista, ...).
             */
            if (SystemManagement.getOSName().contains("Linux"))
                this.fileChooser.setCurrentDirectory(new File(SystemManagement.LINUX_DEFAULT_EDITOR_PATH));
            if (SystemManagement.getOSName().contains("Windows")) {
                this.fileChooser.setCurrentDirectory(new File(SystemManagement.WINDOWS_DEFAULT_EDITOR_PATH));
                filter = new FileNameExtensionFilter("Windows EXE Files", "exe");
                this.fileChooser.setFileFilter(filter);
            }

            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Choosing the Text Editor Path.");
            // Openiong the File Chooser to choose the text editor
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "\t(File Chooser) Opening the File Chooser on: \"" + this.fileChooser.getCurrentDirectory() + "\".");
            // Retriving and saving the text editor name
            this.fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int returnVal = this.fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                SystemManagement.setTextEditorPath(this.fileChooser.getSelectedFile().getAbsolutePath());
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "\t(File Chooser) Text Editor selected: \"" + this.fileChooser.getSelectedFile().getAbsolutePath() + "\".");
            }
            // On exit... Removing the file filter
            if (SystemManagement.getOSName().contains("Windows"))
                this.fileChooser.removeChoosableFileFilter(filter);
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "\t(File Chooser) Closing the File Chooser.");
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Text Editor Path: \"" + SystemManagement.getTextEditorPath() + "\".");
        }

        if (e.equals("Open Log")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Clicked on \"Tools --> Open Log\".");
            // Is there a text editor selected?
            if (SystemManagement.getTextEditorPath() != null) {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Opening the Text Editor to Open a Log File.");
                this.openTextEditor(SystemManagement.getLogWorkingFolder());
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Closing the Text Editor.");
            } else {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Text Editor not selected.");
                JOptionPane.showMessageDialog(WindowManager.main, "You must choose the Text Editor before You can open a Log File.", "Open Log", JOptionPane.WARNING_MESSAGE);
            }
        }

        if (e.equals("Test the Connection")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Clicked on \"Tools --> Test the Connection\".");
            String siteString = JOptionPane.showInputDialog(WindowManager.main, "Insert the site to ping (e.g.: http://www.google.com):", "Test Connection", JOptionPane.INFORMATION_MESSAGE);
            if (siteString != null) {
                if (!siteString.startsWith("http://") && !siteString.startsWith("https://"))
                    siteString = "http://" + siteString;
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Connection) Site: \"" + siteString + "\".");
                if (siteString.matches("https?://[\\w]+.[\\w]+.[\\w.]+[/\\w]+")) {
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Connection) The form of the URL is accepted: testing connection.");
                    try {
                        URL site = new URL(siteString);
                        URLConnection conn = site.openConnection();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String inputLine = null;
                        inputLine = reader.readLine();
                        reader.close();
                        if (!inputLine.isEmpty()) {
                            JOptionPane.showMessageDialog(WindowManager.main, siteString + " is accessible.", "Test Connection", JOptionPane.INFORMATION_MESSAGE);
                            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Connection) \"" + siteString + "\" is accessible.");
                        } else {
                            JOptionPane.showMessageDialog(WindowManager.main, siteString + " is NOT accessible.", "Test Connection", JOptionPane.WARNING_MESSAGE);
                            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Connection) \"" + siteString + "\" is NOT accessible.");
                        }
                    } catch (MalformedURLException ee) {
                        SystemManagement.manageError(Boolean.TRUE, "(Test Connection) The URL is malformed: " + ee.getMessage());
                    } catch (IOException ee) {
                        SystemManagement.manageError(Boolean.TRUE, "(Test Connection) Input/Output exception: " + ee.getMessage());
                    }
                } else {
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Connection) The form of the URL is rejected.");
                    JOptionPane.showMessageDialog(WindowManager.main, "The site has a not acceptable form.", "Test Connection", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        if (e.equals("MEEO")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Clicked on \"About --> MEEO\".");
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "About message opened.");
            JOptionPane.showMessageDialog(WindowManager.main, "<html>"
                    + "                                             <head></head>"
                    + "                                             <body>"
                    + "                                                 <p align=\"center\"><img src=\"" + WindowManager.MEEOLogo + "\"></img></p>"
                    + "                                                 <h2 align=\"center\"><b>M</b>eteorological and <b>E</b>nvironmental <b>E</b>arth <b>O</b>bservation S.r.l.</h2>"
                    + "                                                 <p align=\"left\">Site: <a href=\"http://www.meeo.it\">http://www.meeo.it</a>"
                    + "                                                 <br/>Info: <a href=\"mailto:info@meeo.it\">info@meeo.it</a>"
                    + "                                                 <br/></p>"
                    + "                                             </body>"
                    + "                                         </html>", "MEEO", JOptionPane.PLAIN_MESSAGE);
            /*JFrame mess = new JFrame("MEEO");
            JEditorPane aboutATT = new JEditorPane("text/html", this.ABOUT_MEEO);
            aboutATT.setEditable(false);
            aboutATT.setOpaque(false);
            aboutATT.addHyperlinkListener(new HyperlinkListener() {
                public void hyperlinkUpdate(HyperlinkEvent hle) {
                    if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
                        System.out.println(hle.getURL());
                        Desktop desktop = null;
                        if (Desktop.isDesktopSupported()) {
                            desktop = Desktop.getDesktop();
                            if (desktop.isSupported(Desktop.Action.BROWSE))
                                try {
                                    System.out.println("Ciao");
                                    desktop.browse(hle.getURL().toURI());
                                } catch (URISyntaxException ex) {
                                    ex.printStackTrace();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                        }
                    }
                }
            });
            mess.add(aboutATT);
            mess.setSize(500, 200);
            mess.setLocation(((SystemManagement.getScreenWidth() - mess.getWidth()) / 2), ((SystemManagement.getScreenHeight() - mess.getHeight()) / 2));
            mess.setVisible(true);*/
        }

        if (e.equals("Automating Testing Tool program")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Clicked on \"About --> Automating Testing Tool program\".");
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "About message opened.");
            JOptionPane.showMessageDialog(WindowManager.main, "<html>"
                    + "                                             <head></head>"
                    + "                                             <body>"
                    + "                                                 <p align=\"center\"><img src=\"" + WindowManager.ATTLogo + "\"></img></p>"
                    + "                                                 <h2 align=\"center\"><b>A</b>utomatic <b>T</b>esting <b>T</b>ool</h2>"
                    + "                                                 <p align=\"left\">Version: 1.0 (release 20101209fr)"
                    + "                                                 <br/>Author:  Sergio Ferraresi (email: <a href=\"mailto:dev@sergioferraresi.it\">dev@sergioferraresi.it</a>)"
                    + "                                                 <br/></p>"
                    + "                                             </body>"
                    + "                                         </html>", "Automatic Testing Tool program", JOptionPane.PLAIN_MESSAGE);
            /*JFrame mess = new JFrame();
            JEditorPane aboutATT = new JEditorPane("text/html", this.ABOUT_ATT);
            aboutATT.setEditable(false);
            aboutATT.setOpaque(false);
            aboutATT.addHyperlinkListener(new HyperlinkListener() {
                public void hyperlinkUpdate(HyperlinkEvent hle) {
                    if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
                        System.out.println(hle.getURL());
                        Desktop desktop = null;
                        if (Desktop.isDesktopSupported()) {
                            desktop = Desktop.getDesktop();
                            if (desktop.isSupported(Desktop.Action.MAIL))
                                try {
                                    desktop.mail(hle.getURL().toURI());
                                } catch (URISyntaxException ex) {
                                    ex.printStackTrace();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                        }
                    }
                }
            });
            mess.add(aboutATT);
            mess.setVisible(true);*/
        }

        /**
         * Buttons
         */
        if (e.equals("Cancel")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Clicked on the \"Cancel\" button.");
            // Removes all elements from the main frame.
            WindowManager.removeAllJPFromMainFrame();
            // Refreshing the main frame.
            WindowManager.setAJPInTheMainFrame(WindowManager.mainJP);
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "\t(Cancel Button) \"Main\" Interface loaded.");
        }

        if (e.equals("Execute Scripts")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Clicked on the \"Execute Scripts\" button.");
            if (this.selectedElementsList.getModel().getSize() != 0) {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Scripts Executor) Executing Scripts.");
                Object[] scriptsList = new Object[this.selectedElementsList.getModel().getSize()];
                for (int i = 0; i < this.selectedElementsList.getModel().getSize(); i++)
                    scriptsList[i] = SystemManagement.getScriptsWorkingFolder() + this.selectedElementsList.getModel().getElementAt(i);
                int returnedValue = ScriptExecutor.execute(scriptsList);
                if (returnedValue == SystemManagement.PASS_EXIT_STATUS)
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Scripts Executor) Scripts Execution finished.");
                if (returnedValue == SystemManagement.FAIL_EXIT_STATUS)
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Scripts Executor) Scripts Execution interrupted.");
                JOptionPane.showMessageDialog(WindowManager.main, "Scripts Execution finished.", "Scripts Executor", JOptionPane.INFORMATION_MESSAGE);

                // Results.
                WindowManager.removeAllJPFromMainFrame();
                // Removes all elements from the panel.
                WindowManager.resultsJP.removeAll();

                // Initializes the resultsJP panel.
                this.panelTitle.setText("<html><head></head><body><h2 align=\"center\">Scripts Execution Report</h2></body></html>");
                this.setGrigBagConstraints(this.gbc, 0, 0, 3, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE);
                WindowManager.resultsJP.add(this.panelTitle, this.gbc);

                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Scripts Executor) Creating the Results Table.");
                // Creates the results table
                Object[] columnNames = {"Script Name", "Output Stream", "Error Stream"};
                Object[][] data = new Object[ScriptExecutor.getOutputMessages().size()][3];
                for (int i = 0; i < data.length; i++) {
                    data[i][0] = ScriptExecutor.getOutputMessages().get(i)[0];
                    data[i][1] = ScriptExecutor.getOutputMessages().get(i)[1];
                    data[i][2] = ScriptExecutor.getOutputMessages().get(i)[2];
                }

                JTable resultsTable = new JTable(data, columnNames);
                resultsTable.setPreferredScrollableViewportSize(new Dimension((WindowManager.main.getWidth() - 40), 500));
                resultsTable.setSize(new Dimension((WindowManager.main.getWidth() - 40), 500));
                resultsTable.setPreferredSize(new Dimension((WindowManager.main.getWidth() - 40), 500));
                resultsTable.setMinimumSize(new Dimension((WindowManager.main.getWidth() - 40), 500));
                resultsTable.setMaximumSize(new Dimension((WindowManager.main.getWidth() - 40), 500));
                resultsTable.setFillsViewportHeight(true);
                resultsTable.setShowGrid(true);
                resultsTable.setDragEnabled(false);
                resultsTable.setColumnSelectionAllowed(false);
                resultsTable.setRowSelectionAllowed(false);
                resultsTable.setCellSelectionEnabled(false);
                JScrollPane pane = new JScrollPane(resultsTable);
                this.setGrigBagConstraints(this.gbc, 0, 1, 3, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE);
                WindowManager.resultsJP.add(pane, this.gbc);
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Scripts Executor) Results Table created.");

                JPanel finalButtonsJP = new JPanel(new GridBagLayout());
                GridBagConstraints finalButtonsGBC = new GridBagConstraints();
                finalButtonsGBC.insets = new Insets(10, 10, 10, 10);
                this.continueButton.setText("Return to the \"Choose and Execute Scripts\" Menu");
                this.setGrigBagConstraints(finalButtonsGBC, 0, 0, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE);
                finalButtonsJP.add(this.continueButton, finalButtonsGBC);
                this.setGrigBagConstraints(finalButtonsGBC, 1, 0, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE);
                finalButtonsJP.add(this.cancelButton, finalButtonsGBC);
                this.setGrigBagConstraints(this.gbc, 0, 2, 3, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE);
                WindowManager.resultsJP.add(finalButtonsJP, this.gbc);
                // Refreshing the main frame.
                WindowManager.setAJPInTheMainFrame(WindowManager.resultsJP);
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Scripts Executor) \"Results\" Interface loaded.");
            } else {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Scripts Executor) There are not Scripts to execute.");
                JOptionPane.showMessageDialog(WindowManager.main, "You must choose some scripts before You can execute them.", "Scripts Executor", JOptionPane.WARNING_MESSAGE);
            }
        }

        if (e.equals("Execute Test Case Builder")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Clicked on the \"Execute Test Case Builder\" button.");
            Boolean exists = Boolean.FALSE;
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder) Checking if the filename contains special characters.");
            if (!this.tcFilenameText.getText().contains("<") && !this.tcFilenameText.getText().contains(">") && !this.tcFilenameText.getText().contains("\"") && !this.tcFilenameText.getText().contains("\'")) {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder) The filename does not contain special characters.");
                this.filename = this.tcFilenameText.getText().trim();
                File localFilename = new File(SystemManagement.getTestsWorkingFolder() + this.filename + ".xtd");
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder) Checking if the file exists.");
                if (localFilename.exists()) {
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder) The file already exists.");
                    if (JOptionPane.showConfirmDialog(WindowManager.main, "The file already exists. Do You want to overwrite it?", "Test Case Builder", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        // Deletes File.
                        File file = new File(SystemManagement.getTestsWorkingFolder() + this.filename + ".xtd");
                        file.delete();
                        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder) File deleted.");
                    } else {
                        exists = Boolean.TRUE;
                        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder) The name of the file must be changed.");
                        JOptionPane.showMessageDialog(WindowManager.main, "Change the name of the the Test, please.", "Test Case Builder", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                if (exists == Boolean.FALSE) {
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder) The file does not exist.");
                    // Gets elements value and trims them.
                    this.title = this.tcTitleText.getText().trim();
                    this.author = this.tcAuthorText.getText().trim();
                    this.contributor = this.tcContributorText.getText();
                    this.date = this.tcDateValueLabel.getText();
                    this.status = (String)this.tcStatusCombo.getSelectedItem();
                    this.version = this.tcVersionText.getText().trim();
                    this.description = this.tcDescriptionTArea.getText().trim();
                    this.purpose = this.tcPurposeTArea.getText().trim();
                    this.preconditions = this.tcPreconditionsTArea.getText();
                    this.internalNotes = this.tcNotesTArea.getText();
                    // Check testBuilderJP fields.
                    if ((this.filename.length() != 0) && (this.title.length() != 0) && (this.author.length() != 0) && (this.version.length() != 0) && (this.description.length() != 0) && (this.purpose.length() != 0)) {
                        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder) Creating the XTD file: \"" + this.filename + "\".");
                        SystemManagement.createXMLFile(SystemManagement.getTestsWorkingFolder(), this.filename, SystemManagement.XTD_TYPE);
                        // Root Node.
                        Node rootNode = SystemManagement.createXMLNode("testDescription", null);
                        SystemManagement.appendXMLChildToXMLNode(null, rootNode);
                        SystemManagement.appendXMLAttributeToXMLNode(rootNode, SystemManagement.createXMLAttribute("id", this.filename));
                        SystemManagement.appendXMLAttributeToXMLNode(rootNode, SystemManagement.createXMLAttribute("xmlns:xs", "http://www.w3c.org/2001/XMLSchema"));
                        SystemManagement.appendXMLAttributeToXMLNode(rootNode, SystemManagement.createXMLAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance"));
                        SystemManagement.appendXMLChildToXMLNode(rootNode, SystemManagement.createXMLComment("If You want to validate the XTD file with other applications, You should uncomment the following line and remove the previous \">\"."));
                        SystemManagement.appendXMLChildToXMLNode(rootNode, SystemManagement.createXMLComment("xsi:schemaLocation = \"null testXMLSchema.xsd\">"));
                        // Formal Metadata Node.
                        Node formalMetadataNode = SystemManagement.createXMLNode("formalMetadata", null);
                        SystemManagement.appendXMLChildToXMLNode(rootNode, formalMetadataNode);
                        SystemManagement.appendXMLChildToXMLNode(formalMetadataNode, SystemManagement.createXMLNode("title", this.title));
                        SystemManagement.appendXMLChildToXMLNode(formalMetadataNode, SystemManagement.createXMLNode("creator", this.author));
                        if (this.contributor.length() != 0) {
                            this.contributor = this.contributor.trim();
                            SystemManagement.appendXMLChildToXMLNode(formalMetadataNode, SystemManagement.createXMLNode("contributor", this.contributor));
                        }
                        SystemManagement.appendXMLChildToXMLNode(formalMetadataNode, SystemManagement.createXMLNode("date", this.date));
                        SystemManagement.appendXMLChildToXMLNode(formalMetadataNode, SystemManagement.createXMLNode("status", this.status));
                        SystemManagement.appendXMLChildToXMLNode(formalMetadataNode, SystemManagement.createXMLNode("version", this.version));
                        // Technologies Node.
                        Node technologiesNode = SystemManagement.createXMLNode("technologies", null);
                        SystemManagement.appendXMLChildToXMLNode(rootNode, technologiesNode);
                        Node technicalSpecNode = SystemManagement.createXMLNode("technicalSpec", null);
                        SystemManagement.appendXMLChildToXMLNode(technologiesNode, technicalSpecNode);
                        SystemManagement.appendXMLChildToXMLNode(technicalSpecNode, SystemManagement.createXMLNode("specName", "Automatic Testing Tool"));
                        // Test Case Node.
                        WindowManager.testNode = SystemManagement.createXMLNode("test", null);
                        SystemManagement.appendXMLChildToXMLNode(rootNode, WindowManager.testNode);
                        SystemManagement.appendXMLChildToXMLNode(WindowManager.testNode, SystemManagement.createXMLNode("description", this.description));
                        SystemManagement.appendXMLChildToXMLNode(WindowManager.testNode, SystemManagement.createXMLNode("purpose", this.purpose));
                        if (this.preconditions.length() != 0) {
                            this.preconditions = this.preconditions.trim();
                            SystemManagement.appendXMLChildToXMLNode(WindowManager.testNode, SystemManagement.createXMLNode("preconditions", this.preconditions));
                        }
                        if (this.internalNotes.length() != 0) {
                            this.internalNotes = this.internalNotes.trim();
                            SystemManagement.appendXMLChildToXMLNode(WindowManager.testNode, SystemManagement.createXMLNode("internalNotes", this.internalNotes));
                        }
                        FileNameExtensionFilter filter = null;
                        if (JOptionPane.showConfirmDialog(WindowManager.main, "<html><head></head><body><p>Do You REALLY want to start the Test Case Builder?</p><p>To interact with it, You can:<ul><li>use a single left click to open the \"Commands Menu\";</li><li>use a single right click to add a comment to the previous/next command;</li><li>use the \"Esc\" key to exit from the \"Commands Menu\".</li></ul></p><p><b>Remember</b>: when the Automatic Testing Tool Program is executing the command, <b>DO NOT</b> use the mouse.</p></body></html>", "Test Case Builder", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION ) {
                            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_SEPARATOR_TYPE, null);
                            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder) Starting the Test Case Builder.");
                            int returnVal = -1;
                            if (this.tcSelectProgramToExecuteCB.isSelected()) {
                                JOptionPane.showMessageDialog(WindowManager.main, "Now You will choose the Program to execute.", "Test Case Builder", JOptionPane.INFORMATION_MESSAGE);
                                /*
                                 * Control about the OS: Is used the "contains" method because in
                                 * Windows-based OS, the name is composed by "Windows" plus the
                                 * specific name of OS (like XP, Vista, ...).
                                 */
                                if (SystemManagement.getOSName().contains("Linux"))
                                    this.fileChooser.setCurrentDirectory(new File(SystemManagement.LINUX_GENERIC_PROGRAM_PATH));
                                if (SystemManagement.getOSName().contains("Windows")) {
                                    if (SystemManagement.getOSName().contains("Windows XP"))
                                        this.fileChooser.setCurrentDirectory(new File(SystemManagement.WINDOWSXP_GENERIC_PROGRAM_PATH));
                                    else
                                        this.fileChooser.setCurrentDirectory(new File(SystemManagement.WINDOWS_GENERIC_PROGRAM_PATH));
                                    filter = new FileNameExtensionFilter("Windows EXE Files", "exe");
                                    this.fileChooser.setFileFilter(filter);
                                }

                                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder) Choosing the program to execute.");
                                // Opening the File Chooser to choose the program to execute
                                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - File Chooser) Opening the File Chooser on: \"" + this.fileChooser.getCurrentDirectory() + "\".");
                                // Retriving and saving the program name
                                this.fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                                returnVal = this.fileChooser.showOpenDialog(WindowManager.testCaseBuilder);
                                if (returnVal == JFileChooser.APPROVE_OPTION) {
                                    if (this.fileChooser.getSelectedFile().exists()) {
                                        SystemManagement.setGenericProgramPath(this.fileChooser.getSelectedFile().getAbsolutePath());
                                        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - File Chooser) Program selected: \"" + SystemManagement.getGenericProgramPath() + "\".");
                                    } else
                                        returnVal = -1;
                                } else
                                    returnVal = -1;
                                // On exit... Removing the file filter
                                if (SystemManagement.getOSName().contains("Windows"))
                                    this.fileChooser.removeChoosableFileFilter(filter);
                                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder - File Chooser) Closing the File Chooser.");
                            }

                            if ((returnVal != -1) || !this.tcSelectProgramToExecuteCB.isSelected()) {
                                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder) Program Path: \"" + SystemManagement.getGenericProgramPath() + "\"");
                                WindowManager.main.setState(JFrame.ICONIFIED);
                                WindowManager.testCaseBuilder = new JFrame("Automatic Testing Tool - Test Case Builder");
                                WindowManager.testCaseBuilder.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                                WindowManager.testCaseBuilder.setUndecorated(true);
                                TestCaseBuilder panel = null;
                                if (this.tcSelectProgramToExecuteCB.isSelected()) {
                                    panel = new TestCaseBuilder(TestCaseBuilder.EXECUTE_WITH_PROGRAM, this.filename);
                                } else {
                                    panel = new TestCaseBuilder(TestCaseBuilder.EXECUTE_WITHOUT_PROGRAM, this.filename);
                                }
                                WindowManager.testCaseBuilder.getContentPane().add(panel);
                                WindowManager.testCaseBuilder.pack();
                                WindowManager.testCaseBuilder.addMouseListener(panel);
                                WindowManager.testCaseBuilder.setVisible(true);
                                WindowManager.testCaseBuilder.toFront();
                            } else {
                                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder) The program is not valid.");
                                JOptionPane.showMessageDialog(WindowManager.main, "The program is not valid.", "Test Case Builder", JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    } else
                        JOptionPane.showMessageDialog(WindowManager.main, "You must fill all the not optional fields before execute the Test Builder.", "Test Case Builder", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Case Builder) The filename contains special characters.");
                JOptionPane.showMessageDialog(WindowManager.main, "The filename can not contains \"<\", \">\", \"\"\" and \"\'\" characters.", "Test Case Builder", JOptionPane.WARNING_MESSAGE);
            }
        }

        if (e.equals("Build Test Suite")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Clicked on the \"Build Test Suite\" button.");
            Boolean exists = Boolean.FALSE;
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Suite Builder) Checking if the filename contains special characters.");
            if (!this.tcFilenameText.getText().contains("<") && !this.tcFilenameText.getText().contains(">") && !this.tcFilenameText.getText().contains("\"") && !this.tcFilenameText.getText().contains("\'")) {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Suite Builder) The filename does not contain special characters.");
                this.filename = this.tcFilenameText.getText().trim();
                File localFilename = new File(SystemManagement.getTestsWorkingFolder() + this.filename + ".xtd");
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Suite Builder) Checking if the file exists.");
                if (localFilename.exists()) {
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Suite Builder) The file already exists.");
                    if (JOptionPane.showConfirmDialog(WindowManager.main, "The file already exists. Do You want to overwrite it?", "Test Suite Builder", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        // Deletes File.
                        File file = new File(SystemManagement.getTestsWorkingFolder() + this.filename + ".xtd");
                        file.delete();
                        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Suite Builder) File deleted.");
                    } else {
                        exists = Boolean.TRUE;
                        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Suite Builder) The name of the file must be changed.");
                        JOptionPane.showMessageDialog(WindowManager.main, "Change the name of the the Test, please.", "Test Suite Builder", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                if (exists == Boolean.FALSE) {
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Suite Builder) The file does not exist.");
                    // Gets elements value and trims them.
                    this.title = this.tcTitleText.getText().trim();
                    this.author = this.tcAuthorText.getText().trim();
                    this.contributor = this.tcContributorText.getText();
                    this.date = this.tcDateValueLabel.getText();
                    this.status = (String)this.tcStatusCombo.getSelectedItem();
                    this.version = this.tcVersionText.getText().trim();
                    this.description = this.tcDescriptionTArea.getText().trim();
                    this.purpose = this.tcPurposeTArea.getText().trim();
                    this.preconditions = this.tcPreconditionsTArea.getText();
                    this.internalNotes = this.tcNotesTArea.getText();
                    // Check testBuilderJP fields.
                    if ((this.filename.length() != 0) && (this.title.length() != 0) && (this.author.length() != 0) && (this.version.length() != 0) && (this.description.length() != 0) && (this.purpose.length() != 0)) {
                        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Suite Builder) Creating the XTD file: \"" + this.filename + "\".");
                        SystemManagement.createXMLFile(SystemManagement.getTestsWorkingFolder(), this.filename, SystemManagement.XTD_TYPE);
                        // Root Node.
                        Node rootNode = SystemManagement.createXMLNode("testDescription", null);
                        SystemManagement.appendXMLChildToXMLNode(null, rootNode);
                        SystemManagement.appendXMLAttributeToXMLNode(rootNode, SystemManagement.createXMLAttribute("id", this.filename));
                        SystemManagement.appendXMLAttributeToXMLNode(rootNode, SystemManagement.createXMLAttribute("xmlns:xs", "http://www.w3c.org/2001/XMLSchema"));
                        SystemManagement.appendXMLAttributeToXMLNode(rootNode, SystemManagement.createXMLAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance"));
                        SystemManagement.appendXMLChildToXMLNode(rootNode, SystemManagement.createXMLComment("If You want to validate the XTD file with other applications, You should uncomment the following line and remove the previous \">\"."));
                        SystemManagement.appendXMLChildToXMLNode(rootNode, SystemManagement.createXMLComment("xsi:schemaLocation = \"null testXMLSchema.xsd\">"));
                        // Formal Metadata Node.
                        Node formalMetadataNode = SystemManagement.createXMLNode("formalMetadata", null);
                        SystemManagement.appendXMLChildToXMLNode(rootNode, formalMetadataNode);
                        SystemManagement.appendXMLChildToXMLNode(formalMetadataNode, SystemManagement.createXMLNode("title", this.title));
                        SystemManagement.appendXMLChildToXMLNode(formalMetadataNode, SystemManagement.createXMLNode("creator", this.author));
                        if (this.contributor.length() != 0) {
                            this.contributor = this.contributor.trim();
                            SystemManagement.appendXMLChildToXMLNode(formalMetadataNode, SystemManagement.createXMLNode("contributor", this.contributor));
                        }
                        SystemManagement.appendXMLChildToXMLNode(formalMetadataNode, SystemManagement.createXMLNode("date", this.date));
                        SystemManagement.appendXMLChildToXMLNode(formalMetadataNode, SystemManagement.createXMLNode("status", this.status));
                        SystemManagement.appendXMLChildToXMLNode(formalMetadataNode, SystemManagement.createXMLNode("version", this.version));
                        // Technologies Node.
                        Node technologiesNode = SystemManagement.createXMLNode("technologies", null);
                        SystemManagement.appendXMLChildToXMLNode(rootNode, technologiesNode);
                        Node technicalSpecNode = SystemManagement.createXMLNode("technicalSpec", null);
                        SystemManagement.appendXMLChildToXMLNode(technologiesNode, technicalSpecNode);
                        SystemManagement.appendXMLChildToXMLNode(technicalSpecNode, SystemManagement.createXMLNode("specName", "Automatic Testing Tool"));
                        // Test Case Node.
                        WindowManager.testNode = SystemManagement.createXMLNode("test", null);
                        SystemManagement.appendXMLChildToXMLNode(rootNode, WindowManager.testNode);
                        SystemManagement.appendXMLChildToXMLNode(WindowManager.testNode, SystemManagement.createXMLNode("description", this.description));
                        SystemManagement.appendXMLChildToXMLNode(WindowManager.testNode, SystemManagement.createXMLNode("purpose", this.purpose));
                        if (this.preconditions.length() != 0) {
                            this.preconditions = this.preconditions.trim();
                            SystemManagement.appendXMLChildToXMLNode(WindowManager.testNode, SystemManagement.createXMLNode("preconditions", this.preconditions));
                        }
                        if (this.internalNotes.length() != 0) {
                            this.internalNotes = this.internalNotes.trim();
                            SystemManagement.appendXMLChildToXMLNode(WindowManager.testNode, SystemManagement.createXMLNode("internalNotes", this.internalNotes));
                        }
                        // Removes all elements from the panel.
                        WindowManager.removeAllJPFromMainFrame();
                        this.filenameList = new String[0];
                        this.allElementsList.setListData(new Object[0]);
                        this.selectedElementsList.setListData(new Object[0]);

                        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Suite Builder) Looking for Test Case files in \"" + SystemManagement.getTestsWorkingFolder() + "\".");
                        // Have we some test files?
                        File dirTests = new File(SystemManagement.getTestsWorkingFolder());
                        File[] dirTestsFilesTemp = dirTests.listFiles();
                        if (dirTestsFilesTemp.length != 0) {
                            ArrayList<File> dirTestsFilesFinal = new ArrayList<File>();
                            for (int i = 0; i < dirTestsFilesTemp.length; i++) {
                                /**
                                 * Gets only files, not directorys and separates Test Case
                                 * Files from Test Suites Files.
                                 */
                                if (dirTestsFilesTemp[i].isFile() && !dirTestsFilesTemp[i].getAbsolutePath().endsWith("~")) {
                                    try {
                                        BufferedReader tmp = new BufferedReader(new FileReader(dirTestsFilesTemp[i].getAbsolutePath()));
                                        String line = null;
                                        while ((line = tmp.readLine()) != null) {
                                            if (line.contains("<testCaseSteps>") || line.contains("<testCaseSteps/>")) {
                                                dirTestsFilesFinal.add(dirTestsFilesTemp[i].getAbsoluteFile());
                                                break;
                                            }
                                        }
                                    } catch (IOException ee) {
                                        SystemManagement.manageError(Boolean.TRUE, "(Test Suite Builder) Error while looking for Test Case files: " + ee.getMessage());
                                    }
                                }
                            }
                            // Sorting the files in ascendind order.
                            Collections.sort(dirTestsFilesFinal);

                            this.filenameList = new String[dirTestsFilesFinal.size()];
                            for (int i = 0; i < this.filenameList.length; i++)
                                this.filenameList[i] = dirTestsFilesFinal.get(i).getName();
                        }
                        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Suite Builder) Found " + this.filenameList.length + " Test files in \"" + SystemManagement.getTestsWorkingFolder() + "\".");

                        // Initializes the testSuiteBuilderJP panel.
                        this.setGrigBagConstraints(this.gbc, 0, 0, 4, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
                        this.panelTitle.setText("<html><head></head><body><h2 align=\"center\">Test Suite Builder</h2><br/><p>Choose the Test Cases or the Test Suites to add to the Test Suite.</p></body></html>");
                        WindowManager.testSuiteBuilderJP.add(this.panelTitle, this.gbc);

                        if (this.filenameList.length != 0)
                            this.numberOf.setText("There are " + this.filenameList.length + " Test Files in the \"" + SystemManagement.getTestsWorkingFolder() + "\" Project's folder. Which Tests do You want to include in the Test Suite?");
                        else
                            this.numberOf.setText("There are " + this.filenameList.length + " Test Files in the \"" + SystemManagement.getTestsWorkingFolder() + "\" Project's folder. You must create a New Project or open a Existing Project.");
                        this.setGrigBagConstraints(this.gbc, 0, 1, 4, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
                        WindowManager.testSuiteBuilderJP.add(this.numberOf, this.gbc);

                        this.allElementsList.setListData(this.filenameList);
                        JScrollPane ls = new JScrollPane(this.allElementsList);
                        ls.setSize(new Dimension(400, 400));
                        ls.setMaximumSize(new Dimension(400, 400));
                        ls.setMinimumSize(new Dimension(400, 400));
                        ls.setPreferredSize(new Dimension(400, 400));
                        this.setGrigBagConstraints(this.gbc, 0, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
                        WindowManager.testSuiteBuilderJP.add(ls, this.gbc);

                        JPanel buttonsJP = new JPanel(new GridBagLayout());
                        GridBagConstraints buttonsGBC = new GridBagConstraints();
                        buttonsGBC.insets = new Insets(10, 10, 10, 10);
                        this.setGrigBagConstraints(buttonsGBC, 0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
                        buttonsJP.add(this.addItemToButton, buttonsGBC);
                        this.setGrigBagConstraints(buttonsGBC, 0, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
                        buttonsJP.add(this.removeItemFromButton, buttonsGBC);
                        this.setGrigBagConstraints(this.gbc, 1, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
                        WindowManager.testSuiteBuilderJP.add(buttonsJP, this.gbc);

                        this.selectedElementsList.setModel(new DefaultListModel());
                        JScrollPane ls2 = new JScrollPane(this.selectedElementsList);
                        ls2.setSize(new Dimension(400, 400));
                        ls2.setMaximumSize(new Dimension(400, 400));
                        ls2.setMinimumSize(new Dimension(400, 400));
                        ls2.setPreferredSize(new Dimension(400, 400));
                        this.setGrigBagConstraints(this.gbc, 2, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
                        WindowManager.testSuiteBuilderJP.add(ls2, this.gbc);

                        JPanel moveButtonsJP = new JPanel(new GridBagLayout());
                        GridBagConstraints moveButtonsGBC = new GridBagConstraints();
                        moveButtonsGBC.insets = new Insets(10, 10, 10, 10);
                        this.setGrigBagConstraints(moveButtonsGBC, 0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
                        moveButtonsJP.add(this.moveUpButton, moveButtonsGBC);
                        this.setGrigBagConstraints(moveButtonsGBC, 0, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
                        moveButtonsJP.add(this.moveDownButton, moveButtonsGBC);
                        this.setGrigBagConstraints(this.gbc, 3, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
                        WindowManager.testSuiteBuilderJP.add(moveButtonsJP, this.gbc);

                        JPanel finalButtonsJP = new JPanel(new GridBagLayout());
                        GridBagConstraints finalButtonsGBC = new GridBagConstraints();
                        finalButtonsGBC.insets = new Insets(10, 10, 10, 10);
                        this.executeButton.setText("Build");
                        this.setGrigBagConstraints(finalButtonsGBC, 0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE);
                        finalButtonsJP.add(this.executeButton, finalButtonsGBC);
                        this.continueButton.setText("Return to the \"Test Builder\" Menu");
                        this.setGrigBagConstraints(finalButtonsGBC, 1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
                        finalButtonsJP.add(this.continueButton, finalButtonsGBC);
                        this.setGrigBagConstraints(finalButtonsGBC, 2, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE);
                        finalButtonsJP.add(this.cancelButton, finalButtonsGBC);
                        this.setGrigBagConstraints(this.gbc, 2, 3, 2, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE);
                        WindowManager.testSuiteBuilderJP.add(finalButtonsJP, this.gbc);
                        // Refreshing the main frame.
                        WindowManager.setAJPInTheMainFrame(WindowManager.testSuiteBuilderJP);
                        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Suite Builder) \"Test Suite Builder\" Interface loaded.");
                    } else
                        JOptionPane.showMessageDialog(WindowManager.main, "You must fill all the not optional fields before execute the Test Builder.", "Test Suite Builder", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Suite Builder) The filename contains special characters.");
                JOptionPane.showMessageDialog(WindowManager.main, "The filename can not contains \"<\", \">\", \"\"\" and \"\'\" characters.", "Test Suite Builder", JOptionPane.WARNING_MESSAGE);
            }
        }

        if (e.equals("Add Selected Item")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Clicked on the \"Add selected Item\" button.");
            Object[] scriptsToAdd = null;
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Gathering information about the files to add.");
            if (!this.allElementsList.isSelectionEmpty()) {
                DefaultListModel listModel = new DefaultListModel();
                DefaultListModel allList = new DefaultListModel();
                // Gets all items in the all scripts's list.
                for (int i = 0; i < this.allElementsList.getModel().getSize(); i++)
                    allList.addElement(this.allElementsList.getModel().getElementAt(i));
                // Gets selected scripts to add.
                scriptsToAdd = this.allElementsList.getSelectedValues();
                if (this.selectedElementsList.getModel().getSize() != 0) {
                    // Gets all previously selected scripts.
                    for (int i = 0; i < this.selectedElementsList.getModel().getSize(); i++)
                        listModel.addElement(this.selectedElementsList.getModel().getElementAt(i));
                }
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Adding files.");
                // Adds new scripts to old scripts and removes old scripts from the list.
                for (int i = 0; i < scriptsToAdd.length; i++)
                    if (!listModel.contains(scriptsToAdd[i])) {
                        listModel.addElement(scriptsToAdd[i]);
                        allList.removeElement(scriptsToAdd[i]);
                    }
                if (this.selectedElementsList.getModel().getSize() != 0) {
                    // Removes all list's elements.
                    this.allElementsList.setListData(new Object[0]);
                    this.selectedElementsList.setListData(new Object[0]);
                }
                // Adds the new elements.
                this.allElementsList.setModel(allList);
                this.selectedElementsList.setModel(listModel);
            } else {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "There are not files to add.");
                JOptionPane.showMessageDialog(WindowManager.main, "You must choose one or more elements before add them.", "Add Selected Item", JOptionPane.WARNING_MESSAGE);
            }
        }

        if (e.equals("Remove Selected Item")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Clicked on the \"Remove selected Item\" button.");
            Object[] scriptsToRemove = null;
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Gathering information about the files to remove.");
            if (!this.selectedElementsList.isSelectionEmpty()) {
                DefaultListModel listModel = new DefaultListModel();
                DefaultListModel selectedList = new DefaultListModel();
                // Gets all items in the selected scripts's list.
                for (int i = 0; i < this.selectedElementsList.getModel().getSize(); i++)
                    selectedList.addElement(this.selectedElementsList.getModel().getElementAt(i));
                // Gets selected scripts to remove.
                scriptsToRemove = this.selectedElementsList.getSelectedValues();
                if (this.allElementsList.getModel().getSize() != 0) {
                    // Gets all previously selected scripts.
                    for (int i = 0; i < this.allElementsList.getModel().getSize(); i++)
                        listModel.addElement(this.allElementsList.getModel().getElementAt(i));
                }
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Removing files.");
                // Adds new scripts to old scripts and removes old scripts from the list.
                for (int i = 0; i < scriptsToRemove.length; i++)
                    if (!listModel.contains(scriptsToRemove[i])) {
                        listModel.addElement(scriptsToRemove[i]);
                        selectedList.removeElement(scriptsToRemove[i]);
                    }
                if (this.allElementsList.getModel().getSize() != 0) {
                    // Removes all list's elements.
                    this.selectedElementsList.setListData(new Object[0]);
                    this.allElementsList.setListData(new Object[0]);
                }
                // Sort the lists.
                ArrayList<String> tmp = new ArrayList<String>();
                Object[] arrayTMP = listModel.toArray();
                for (int i = 0; i < arrayTMP.length; i++)
                    tmp.add(i, arrayTMP[i].toString());
                Collections.sort(tmp);
                DefaultListModel tmpp = new DefaultListModel();
                listModel.removeAllElements();
                for (int i = 0; i < tmp.size(); i++)
                    listModel.add(i, tmp.get(i));
                // Adds the new elements.
                this.selectedElementsList.setModel(selectedList);
                this.allElementsList.setModel(listModel);
            } else {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "There are not files to remove.");
                JOptionPane.showMessageDialog(WindowManager.main, "You must choose one or more elements before add them.", "Remove Selected Item", JOptionPane.WARNING_MESSAGE);
            }
        }

        if (e.equals("Move Up Selected Item")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Clicked on the \"Move Up Selected Item\" button.");
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Gathering information about the files to move up.");
            if (!this.selectedElementsList.isSelectionEmpty()) {
                DefaultListModel listModel = new DefaultListModel();
                int index = this.selectedElementsList.getSelectedIndex();
                // Only if the items is not the first element.
                if (index != 0) {
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Moving up the files.");
                    // Gets all items between 0 and (index - 1).
                    for (int i = 0; i < (index - 1); i++)
                        listModel.addElement(this.selectedElementsList.getModel().getElementAt(i));
                    // Gets the selected item at index position.
                    listModel.addElement(this.selectedElementsList.getModel().getElementAt(index));
                    // Gets the items at (index - 1) position.
                    listModel.addElement(this.selectedElementsList.getModel().getElementAt(index - 1));
                    // Gets all items between (index + 1) and list.lenght.
                    for (int i = (index + 1); i < this.selectedElementsList.getModel().getSize(); i++)
                        listModel.addElement(this.selectedElementsList.getModel().getElementAt(i));
                    // Removes all list's elements.
                    if (this.selectedElementsList.getModel().getSize() != 0)
                        this.selectedElementsList.setListData(new Object[0]);
                    // Adds the new elements.
                    this.selectedElementsList.setModel(listModel);
                    // Selects the moved items.
                    this.selectedElementsList.setSelectedIndex(index - 1);
                } else
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "The file is at the top of the list.");
            } else {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "There are not files to move up.");
                JOptionPane.showMessageDialog(WindowManager.main, "You must choose only one before move it up.", "Move Up Selected Item", JOptionPane.WARNING_MESSAGE);
            }
        }

        if (e.equals("Move Down Selected Item")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Clicked on the \"Move Down Selected Item\" button.");
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Gathering information about the files to move down.");
            if (!this.selectedElementsList.isSelectionEmpty()) {
                DefaultListModel listModel = new DefaultListModel();
                int index = this.selectedElementsList.getSelectedIndex();
                // Only if the items is not the last element.
                if (index != (this.selectedElementsList.getModel().getSize() - 1)) {
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Moving down the files.");
                    // Gets all items between 0 and index.
                    for (int i = 0; i < index; i++)
                        listModel.addElement(this.selectedElementsList.getModel().getElementAt(i));
                    // Gets the items at (index + 1) position.
                    listModel.addElement(this.selectedElementsList.getModel().getElementAt(index + 1));
                    // Gets the selected item at index position.
                    listModel.addElement(this.selectedElementsList.getModel().getElementAt(index));
                    // Gets all items between (index + 1) and list.lenght.
                    for (int i = (index + 2); i < this.selectedElementsList.getModel().getSize(); i++)
                        listModel.addElement(this.selectedElementsList.getModel().getElementAt(i));
                    // Removes all list's elements.
                    if (this.selectedElementsList.getModel().getSize() != 0)
                        this.selectedElementsList.setListData(new Object[0]);
                    // Adds the new elements.
                    this.selectedElementsList.setModel(listModel);
                    // Selects the moved items.
                    this.selectedElementsList.setSelectedIndex(index + 1);
                } else
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "The file is at the bottom of the list.");
            } else {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "There are not files to move down.");
                JOptionPane.showMessageDialog(WindowManager.main, "You must choose only one before move it up.", "Move Down Selected Item", JOptionPane.WARNING_MESSAGE);
            }
        }

        if (e.equals("Build")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Clicked on the \"Build\" button.");
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Suite Builder) Gathering the information about the Test Cases.");
            // Checks the selectedElementsList is empty.
            if (this.selectedElementsList.getModel().getSize() != 0) {
                int returnVal = JOptionPane.showConfirmDialog(WindowManager.main, "<html><head></head><body><p>Do You really want to start the Test Suite Builder?</p><p>Check if the Test Case Files order is correct.</p></body></html>", "Test Suite Builder", JOptionPane.YES_NO_OPTION);
                if (returnVal == JOptionPane.OK_OPTION) {
                    DefaultListModel dlm = new DefaultListModel();
                    // Test Suite Refs Node.
                    Node testSuiteRefsNode = SystemManagement.createXMLNode("testSuiteRefs", null);
                    SystemManagement.appendXMLChildToXMLNode(WindowManager.testNode, testSuiteRefsNode);
                    // Inserts the references to the Test Case files.
                    for (int i = 0; i < this.selectedElementsList.getModel().getSize(); i++)
                        dlm.add(i, this.selectedElementsList.getModel().getElementAt(i));
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Suite Builder) Writing the information to the Test Suite File.");
                    for (int i = 0; i < dlm.getSize(); i++) {
                        // External File Node.
                        SystemManagement.appendXMLChildToXMLNode(testSuiteRefsNode, SystemManagement.createXMLNode("externalFile", SystemManagement.getTestsWorkingFolder() + dlm.elementAt(i).toString()));
                    }
                    SystemManagement.closeXMLFile();
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Suite Builder) Test Suite File created.");
                    JOptionPane.showMessageDialog(WindowManager.main, "The Test Suite file is ready for execution.", "Test Suite Builder", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Suite Builder) There are not Test Case files.");
                JOptionPane.showMessageDialog(WindowManager.main, "You must select one or more Test Case Files before create a Test Suite File.", "Test Suite Builder", JOptionPane.WARNING_MESSAGE);
            }
        }

        if (e.equals("Execute")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Clicked on the \"Execute\" button.");
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor) Gathering the information about the Test Cases.");
            // We need to select some tests before their execution...
            if (this.selectedElementsList.getModel().getSize() != 0) {
                // Do You REALLY want to start the tests execution?
                if ( JOptionPane.showConfirmDialog(WindowManager.main, "<html><head></head><body><p>Do You REALLY want to start the tests execution?<br/>Did You have executed the necessary scripts?</p><p><b>Remember</b>: when the Automatic Testing Tool Program is executing the test, <b>DO NOT</b> use the mouse.</p></body></html>", "Test Executor", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION ) {
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_SEPARATOR_TYPE, null);
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor) Started Test Executor.");
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_SEPARATOR_TYPE, null);

                    WindowManager.main.setState(JFrame.ICONIFIED);

                    // Executes choosed Tests.
                    Object[] tmp = new Object[this.selectedElementsList.getModel().getSize()];
                    for (int i = 0; i < this.selectedElementsList.getModel().getSize(); i++)
                        tmp[i] = SystemManagement.getTestsWorkingFolder() + this.selectedElementsList.getModel().getElementAt(i);
                    TestExecutor.execute(tmp);

                    // Wait for tests execution.
                    WindowManager.main.setState(JFrame.NORMAL);
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Tests Executor) The Test Execution is finished. Check the results.");
                    JOptionPane.showMessageDialog(WindowManager.main, "The Test Exeution is finished. Check the results.", "Test Executor", JOptionPane.INFORMATION_MESSAGE);
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_SEPARATOR_TYPE, null);
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_SEPARATOR_TYPE, null);

                    // Results.
                    WindowManager.removeAllJPFromMainFrame();
                    // Removes all elements from the panel.
                    WindowManager.resultsJP.removeAll();

                    // Initializes the resultsJP panel.
                    this.panelTitle.setText("<html><head></head><body><h2 align=\"center\">Tests Execution Report</h2><br/><p>All the reports file are in the \"" + SystemManagement.getResultsWorkingFolder() + "\" folder:</p></body></html>");
                    this.setGrigBagConstraints(this.gbc, 0, 0, 3, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE);
                    WindowManager.resultsJP.add(this.panelTitle, this.gbc);

                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor) Creating the Results Table.");
                    // Creates the results table
                    Object[] columnNames = {"Test Suite Name", "Test Case Name", "Test Success Status", "Screenshots to Check"};
                    ArrayList<Object[]> dataAL = new ArrayList<Object[]>();

                    for (int i  = 0; i < TestExecutor.getTestsResults().size(); i++) {
                        Boolean isTS = Boolean.FALSE;
                        // Is TC or TS?
                        try {
                            BufferedReader br = new BufferedReader(new FileReader(TestExecutor.getTestsResults().get(i)));
                            String line = null;
                            while ((line = br.readLine()) != null) {
                                if (line.contains("<testSuite>"))
                                    isTS = Boolean.TRUE;
                            }
                        } catch (IOException ee) {
                            SystemManagement.manageError(Boolean.TRUE, "(Test Executor) Is a \"Test Suite\" or \"Test Case\" file? " + ee.getMessage());
                        }
                        if (isTS) {
                            // Test Suite case.
                            String tsStatus = null;
                            String tsName = null;
                            String tcVerify = null;
                            try {
                                // Useful to open the result file.
                                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                                DocumentBuilder db = dbf.newDocumentBuilder();
                                Document doc = db.parse(TestExecutor.getTestsResults().get(i));
                                doc.getDocumentElement().normalize();
                                // Gets the TS name.
                                NodeList nameNodes = doc.getElementsByTagName("name");
                                tsName = nameNodes.item(0).getChildNodes().item(0).getNodeValue();
                                // Gets the TS status.
                                NodeList statusNodes = doc.getElementsByTagName("status");
                                tsStatus = statusNodes.item(0).getChildNodes().item(0).getNodeValue();
                                // Gets the TS child nodes (TC).
                                NodeList tcNodes = doc.getElementsByTagName("testCase");
                                for (int j = 0; j < tcNodes.getLength(); j++) {
                                    // Writes the TS name only for the first TC.
                                    Object[] tmpA = new Object[4];
                                    if (j == 0)
                                        tmpA[0] = (tsStatus.equals(SystemManagement.PASS_STATUS))? "<html><head></head><body><p color=\"green\">" + tsName.substring((tsName.lastIndexOf(File.separator) + 1)) + "</p></body></html>":((tsStatus.equals(SystemManagement.PENDING_STATUS))? "<html><head></head><body><p color=\"#FF9900\">" + tsName.substring((tsName.lastIndexOf(File.separator) + 1)) + "</p></body></html>":"<html><head></head><body><p color=\"red\"><b>" + tsName.substring((tsName.lastIndexOf(File.separator) + 1)) + "</b></p></body></html>");
                                    else
                                        tmpA[0] = "";
                                    // Gets TC name.
                                    String tcName = tcNodes.item(j).getChildNodes().item(1).getChildNodes().item(0).getNodeValue();
                                    tcName = tcName.substring(tcName.lastIndexOf(File.separator) + 1);
                                    tmpA[1] = tcName;
                                    // Gets TC status.
                                    String tcStatus  = tcNodes.item(j).getChildNodes().item(3).getChildNodes().item(0).getNodeValue();
                                    tmpA[2] = (tcStatus.equals(SystemManagement.PASS_STATUS))? "<html><head></head><body><p color=\"green\">" + SystemManagement.PASS_STATUS + "</p></body></html>":((tcStatus.equals(SystemManagement.PENDING_STATUS))? "<html><head></head><body><p color=\"#FF9900\">" + SystemManagement.PENDING_STATUS + "</p></body></html>":((tcStatus.equals(SystemManagement.FAIL_STATUS))? "<html><head></head><body><p color=\"red\"><b>" + SystemManagement.FAIL_STATUS + "</b></p></body></html>":"<html><head></head><body><p color=\"red\"><b>" + SystemManagement.ERROR_STATUS + "</b></p></body></html>"));
                                    /*
                                     * Gets all the screenshotsToVerify nodes.
                                     * Useful to avoid null Exception.
                                     */
                                    NodeList verifyNodes = doc.getElementsByTagName("screenshot");
                                    if (verifyNodes.getLength() == 0)                                    
                                        tcVerify = "<html><head></head><body><p color=\"green\">No Screenshots To Check</p></body></html>";
                                    else {
                                        // Checks for screenshots to verify.
                                        if (tcNodes.item(j).getChildNodes().getLength() > 5) {
                                            int screenshotsNumber = 0;
                                            for (int n = 0; n < tcNodes.item(j).getChildNodes().item(5).getChildNodes().getLength(); n++)
                                                if (!tcNodes.item(j).getChildNodes().item(5).getChildNodes().item(n).getNodeName().equals("#text"))
                                                    screenshotsNumber++;
                                            if (screenshotsNumber != 0)
                                                tcVerify = "<html><head></head><body><p id=\"" + tsName.substring((tsName.lastIndexOf(File.separator) + 1), tsName.lastIndexOf('.')) + "#" + tcName.substring((tcName.lastIndexOf(File.separator) + 1), tcName.lastIndexOf('.')) + "\" color=\"red\"><b>Check Screenshots (" + screenshotsNumber + ")</b></p></body></html>";
                                            else
                                                tcVerify = "<html><head></head><body><p color=\"green\">No Screenshots To Check</p></body></html>";
                                        } else
                                            tcVerify = "<html><head></head><body><p color=\"green\">No Screenshots To Check</p></body></html>";
                                    }
                                    tmpA[3] = tcVerify;
                                    dataAL.add(tmpA);
                                }
                            } catch (ParserConfigurationException ee) {
                                SystemManagement.manageError(Boolean.TRUE, "(Test Executor) The report file \"" + TestExecutor.getTestsResults().get(i) + "\" is not a valid report file: " + ee.getMessage());
                            } catch (SAXException ee) {
                                SystemManagement.manageError(Boolean.TRUE, "(Test Executor) The rreport file \"" + TestExecutor.getTestsResults().get(i) + "\" is not a valid report file: " + ee.getMessage());
                            } catch (IOException ee) {
                                SystemManagement.manageError(Boolean.TRUE, "(Test Executor) The report file \"" + TestExecutor.getTestsResults().get(i) + "\" is not a valid report file: " + ee.getMessage());
                            }
                        } else {
                            //Test Case case.
                            // Gets the status of the TC from the report file.
                            String tcStatus = null;
                            String tcName = null;
                            String tcVerify = null;
                            try {
                                // Useful to open the result file.
                                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                                DocumentBuilder db = dbf.newDocumentBuilder();
                                Document doc = db.parse(TestExecutor.getTestsResults().get(i));
                                doc.getDocumentElement().normalize();
                                // Gets the TC name.
                                NodeList nameNodes = doc.getElementsByTagName("name");
                                tcName = nameNodes.item(0).getChildNodes().item(0).getNodeValue();
                                // Gets the TC status.
                                NodeList statusNodes = doc.getElementsByTagName("status");
                                tcStatus = statusNodes.item(0).getChildNodes().item(0).getNodeValue();
                                // Checks for screenshots to verify.
                                NodeList verifyNodes = doc.getElementsByTagName("screenshot");
                                if (verifyNodes.getLength() != 0)
                                    tcVerify = "<html><head></head><body><p id=\"null#" + tcName.substring((tcName.lastIndexOf(File.separator) + 1), tcName.lastIndexOf('.')) + "\" color=\"red\"><b>Check Screenshots (" + verifyNodes.getLength() + ")</b></p></body></html>";
                                else
                                    tcVerify = "<html><head></head><body><p color=\"green\">No Screenshots To Check</p></body></html>";
                            } catch (ParserConfigurationException ee) {
                                SystemManagement.manageError(Boolean.TRUE, "(Test Executor) The report file \"" + TestExecutor.getTestsResults().get(i) + "\" is not a valid report file: " + ee.getMessage());
                            } catch (SAXException ee) {
                                SystemManagement.manageError(Boolean.TRUE, "(Test Executor) The report file \"" + TestExecutor.getTestsResults().get(i) + "\" is not a valid report file: " + ee.getMessage());
                            } catch (IOException ee) {
                                SystemManagement.manageError(Boolean.TRUE, "(Test Executor) The report file \"" + TestExecutor.getTestsResults().get(i) + "\" is not a valid report file: " + ee.getMessage());
                            }
                            Object[] tmpA = new Object[4];
                            tmpA[0] = "N/A";
                            tmpA[1] = (tcStatus.equals(SystemManagement.PASS_STATUS))? "<html><head></head><body><p color=\"green\">" + tcName.substring((tcName.lastIndexOf(File.separator) + 1)) + "</p></body></html>":((tcStatus.equals(SystemManagement.PENDING_STATUS))? "<html><head></head><body><p color=\"#FF9900\">" + tcName.substring((tcName.lastIndexOf(File.separator) + 1)) + "</p></body></html>":"<html><head></head><body><p color=\"red\"><b>" + tcName.substring((tcName.lastIndexOf(File.separator) + 1)) + "</b></p></body></html>");
                            tmpA[2] = (tcStatus.equals(SystemManagement.PASS_STATUS))? "<html><head></head><body><p color=\"green\">" + SystemManagement.PASS_STATUS + "</p></body></html>":((tcStatus.equals(SystemManagement.PENDING_STATUS))? "<html><head></head><body><p color=\"#FF9900\">" + SystemManagement.PENDING_STATUS + "</p></body></html>":((tcStatus.equals(SystemManagement.FAIL_STATUS))? "<html><head></head><body><p color=\"red\"><b>" + SystemManagement.FAIL_STATUS + "</b></p></body></html>":"<html><head></head><body><p color=\"red\"><b>" + SystemManagement.ERROR_STATUS + "</b></p></body></html>"));
                            tmpA[3] = tcVerify;
                            dataAL.add(tmpA);
                        }
                    }
                    Object[][] data = new Object[dataAL.size()][4];
                    for (int i = 0; i < data.length; i++)
                        System.arraycopy(dataAL.get(i), 0, data[i], 0, data[i].length);

                    JTable resultsTable = new JTable(data, columnNames);
                    resultsTable.setPreferredScrollableViewportSize(new Dimension((WindowManager.main.getWidth() - 40), 500));
                    resultsTable.setSize(new Dimension((WindowManager.main.getWidth() - 40), 500));
                    resultsTable.setPreferredSize(new Dimension((WindowManager.main.getWidth() - 40), 500));
                    resultsTable.setMinimumSize(new Dimension((WindowManager.main.getWidth() - 40), 500));
                    resultsTable.setMaximumSize(new Dimension((WindowManager.main.getWidth() - 40), 500));
                    resultsTable.setFillsViewportHeight(true);
                    resultsTable.setShowGrid(true);
                    resultsTable.setDragEnabled(false);
                    resultsTable.setColumnSelectionAllowed(false);
                    resultsTable.setRowSelectionAllowed(false);
                    resultsTable.setCellSelectionEnabled(false);
                    resultsTable.getColumnModel().getColumn(3).setCellEditor(new ResultsTableEditor());
                    JScrollPane pane = new JScrollPane(resultsTable);
                    this.setGrigBagConstraints(this.gbc, 0, 1, 3, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE);
                    WindowManager.resultsJP.add(pane, this.gbc);
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor) Results Table created.");

                    JPanel finalButtonsJP = new JPanel(new GridBagLayout());
                    GridBagConstraints finalButtonsGBC = new GridBagConstraints();
                    finalButtonsGBC.insets = new Insets(10, 10, 10, 10);
                    this.executeButton.setText("Open a Report File");
                    this.setGrigBagConstraints(finalButtonsGBC, 0, 0, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE);
                    finalButtonsJP.add(this.executeButton, finalButtonsGBC);
                    this.continueButton.setText("Return to the \"Execute Tests\" Menu");
                    this.setGrigBagConstraints(finalButtonsGBC, 1, 0, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE);
                    finalButtonsJP.add(this.continueButton, finalButtonsGBC);
                    this.setGrigBagConstraints(finalButtonsGBC, 2, 0, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE);
                    finalButtonsJP.add(this.cancelButton, finalButtonsGBC);
                    this.setGrigBagConstraints(this.gbc, 0, 2, 3, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE);
                    WindowManager.resultsJP.add(finalButtonsJP, this.gbc);
                    // Refreshing the main frame.
                    WindowManager.setAJPInTheMainFrame(WindowManager.resultsJP);
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor) \"Results\" Interface loaded.");
                }
            } else {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor) There are not Test files.");
                JOptionPane.showMessageDialog(WindowManager.main, "You must choose some Tests before You can execute them!", "Test Executor", JOptionPane.WARNING_MESSAGE);
            }
        }

        if (e.equals("Open a Report File")) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Clicked on the \"Open a Report File\" button.");
            // Is there a text editor selected?
            if (SystemManagement.getTextEditorPath() != null) {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor) Opening the Text Editor to Open a Report File.");
                this.openTextEditor(SystemManagement.getResultsWorkingFolder());
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor) Closing the Text Editor.");
            } else {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor) Text Editor not selected.");
                JOptionPane.showMessageDialog(WindowManager.main, "You must choose the Text Editor before You can open the Report File!", "Test Executor", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * Manages the Window Opened Event.
     * @param e an WindowEvent element that identifies the Event.
     */
    public void windowOpened(WindowEvent e) {
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Window) The main window has been opened.");
    }

    /**
     * Manages the Window Closing Event.
     * @param e an WindowEvent element that identifies the Event.
     */
    public void windowClosing(WindowEvent e) {
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Window) Clicked on \"Close Window\".");
        // Closing the Program...
        if ( JOptionPane.showConfirmDialog(WindowManager.main, this.ON_EXIT, "Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION ) {
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Stopping Automatic Testing Tool.");
            /*
             * Does not need to close the log file:
             * SystemManagement.appendToLogAndToInterface() just do this.
             */
            SystemManagement.emptyExecutionFolder();
            System.exit(SystemManagement.PASS_EXIT_STATUS);
        }
    }

    /**
     * Not implemented.
     * @param e an WindowEvent element that identifies the Event.
     */
    public void windowClosed(WindowEvent e) {}

    /**
     * Manages the Window Iconified Event.
     * @param e an WindowEvent element that identifies the Event.
     */
    public void windowIconified(WindowEvent e) {
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Window) The main window has been iconified.");
    }

    /**
     * Manages the Window Deiconified event.
     * @param e an WindowEvent element that identifies the Event.
     */
    public void windowDeiconified(WindowEvent e) {
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Window) The main window has been deiconified.");
    }

    /**
     * Not implemented.
     * @param e an WindowEvent element that identifies the Event.
     */
    public void windowActivated(WindowEvent e) {
        WindowManager.main.repaint();
    }

    /**
     * Not implemented.
     * @param e an WindowEvent element that identifies the Event.
     */
    public void windowDeactivated(WindowEvent e) {
        WindowManager.main.repaint();
    }

    /**
     * Returns the Main JFrame that contains the Main Program.
     * @return a JFrame element that identifies the Main JFrame.
     * TODO 2014-12-05 (psf563): was protected
     */
    public static JFrame getMainFrame() {
        return WindowManager.main;
    }

    /**
     * Returns the TestCaseBuilder JFrame where is drown the screenshot of the
     * Desktop.
     * @return a JFrame element that identifies the TestCaseBuilder JFrame.
     */
    protected static JFrame getTestCaseBuilderFrame() {
        return WindowManager.testCaseBuilder;
    }

    /**
     * Returns the TestResultsValidator JFrame that contains the screenshots to
     * check.
     * @return a JFrame element that identifies the TestResultsValidator JFrame.
     */
    protected static JFrame getResultsValidatorFrame() {
        return WindowManager.resultsValidator;
    }

    /**
     * Returns the Status Bar of the Main JFrame.
     * The Status Bar is a JLabel.
     * @return a JLabel element that identifies the Status Bar of the Main
     * JFrame.
     * TODO 2014-12-05 (psf563): was protected
     */
    public static JLabel getStatusBar() {
        return WindowManager.statusBar;
    }

    /**
     * Returns the JPanel of the Test Builder.
     * @return a JPanel element that identifies the JPanel of the Test Builder.
     */
    protected static JPanel getTestBuilderPanel() {
        return WindowManager.testBuilderJP;
    }

    /**
     * Returns the XTD test Node to append the child nodes.
     * @return a Node element that identifies the XTD test Node.
     */
    protected static Node getTestNode() {
        return WindowManager.testNode;
    }

    /**
     * Removes all the JPanel from the Main JFrame.
     */
    protected static void removeAllJPFromMainFrame() {
        WindowManager.main.remove(WindowManager.mainJP);
        WindowManager.main.remove(WindowManager.testBuilderJP);
        WindowManager.main.remove(WindowManager.testSuiteBuilderJP);
        WindowManager.main.remove(WindowManager.executeJP);
        WindowManager.main.remove(WindowManager.resultsJP);
    }

    /**
     * Sets the JPanel jp as JPanel of the Main JFrame, and refreshes the Main
     * JFrame.
     * @param jp a JPanel element that identifies the JPanel to set as JPanel of
     * the Main JFrame.
     */
    protected static void setAJPInTheMainFrame(JPanel jp) {
        WindowManager.main.add(jp);
        WindowManager.main.repaint();
        WindowManager.main.setVisible(true);
    }

    /**
     * Sets the Main JPanel as JPanel of the Main JFrame, and refreshes the Main
     * JFrame.
     */
    protected static void setMainJPToMainFrame() {
        WindowManager.main.add(WindowManager.mainJP);
        WindowManager.main.repaint();
    }

    /**
     * Runs a Program. It is used to execute the Text Editor to create a file,
     * or to modify a file; and to execute a Generic Program.
     * If the type is equals to SystemManagement.GENERIC_PROGRAM_TYPE, it will
     * execute the Generic Program. In this case the dirPath must be null.
     * If the type is equals to SystemManagement.TEXT_EDITOR_PROGRAM_TYPE, it
     * will execute the Text Editor. If dirPath is equals to null, it will open
     * the Text Editor to create a file. If dirPath is not equals to null, it
     * will open the Text Editor to modify the specified file.
     * @param type a String that identifies the the Program Type. The accepted
     * values are:
     * <ul>
     *   <li><i>SystemManagement.TEXT_EDITOR_PROGRAM_TYPE</i>, used to identify
     *       a Generic Program;</li>
     *   <li><i>SystemManagement.GENERIC_PROGRAM_TYPE</i>, used to identify
     *       a Text Editor.</li>
     * </ul>
     * @param dirPath the path of the file to modify. The accepted values are:
     * null and a valid path.
     */
    private void openTextEditor(String dirPath) {
        String path = null;
        Boolean canExecuteLogFile = Boolean.FALSE;
        Boolean canReadLogFile = Boolean.FALSE;
        Boolean canWriteLogFile = Boolean.FALSE;
        Boolean isFileSelected = Boolean.TRUE;
        FileNameExtensionFilter filter = null;
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "\tOpening the Text Editor.");

        // Open the text editor to modify a file.
        if (dirPath != null) {
            // Sets the folder path
            this.fileChooser.setCurrentDirectory(new File(dirPath));
            /*
             * If You want to open a test file, You could only open files with
             * "xtd" extension.
             */
            if (dirPath.equals(SystemManagement.getTestsWorkingFolder())) {
                filter = new FileNameExtensionFilter("XML for Tests Description Files [.xtd]", "xtd");
                this.fileChooser.setFileFilter(filter);
            }
            /*
             * If You want to open a report file, You could only open files
             * with xml" extension.
             */
            if (dirPath.equals(SystemManagement.getResultsWorkingFolder())) {
                filter = new FileNameExtensionFilter("XML for Tests Reports Files [.xtr]", "xtr");
                this.fileChooser.setFileFilter(filter);
            }
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "\t(Text Editor - File Chooser) Opening the File Chooser on: \"" + this.fileChooser.getCurrentDirectory() + "\".");
            this.fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            // Openiong the File Chooser to choose the file (script or utility file or test) to modify.
            int returnVal = this.fileChooser.showOpenDialog(WindowManager.main);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                // Retriving and saving the filename.
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "\t(Text Editor - File Chooser) File to modify: \"" + this.fileChooser.getSelectedFile().getAbsolutePath() + "\".");
                // On exit...
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "\t(Text Editor - File Chooser) Exit from the File Chooser.");
                /**
                 * If You want to open a log, You could only open it in
                 * read-only mode.
                 * Saves previous rwx values.
                 */
                if (dirPath.equals(SystemManagement.getLogWorkingFolder())) {
                    canExecuteLogFile = this.fileChooser.getSelectedFile().canExecute();
                    canReadLogFile = this.fileChooser.getSelectedFile().canRead();
                    canWriteLogFile = this.fileChooser.getSelectedFile().canWrite();
                    this.fileChooser.getSelectedFile().setReadOnly();
                }
            } else
                isFileSelected = Boolean.FALSE;
        }

        if (filter != null) {
            // Removing the filter
            this.fileChooser.removeChoosableFileFilter(filter);
        }

        if (isFileSelected == Boolean.TRUE) {
            /*
             * Control about the OS: Is used the "contains" method because in
             * Windows-based OS, the name is composed by "Windows" plus the
             * specific name of OS (like XP, Vista, ...).
             */
            String[] cmd = null;
            // GNU/Linux case.
            if (SystemManagement.getOSName().contains("Linux")) {
                cmd = new String[3];
                cmd[0] = "/bin/sh";
                cmd[1] = "-c";
                // Opening the Text Editor to create a file.
                if (dirPath == null) {
                    cmd[2] = SystemManagement.getTextEditorPath();
                    path = SystemManagement.getTextEditorPath();
                }
                // Opening the Text Editor to modify a file.
                if (dirPath != null) {
                    cmd[2] = SystemManagement.getTextEditorPath() + " " + this.fileChooser.getSelectedFile().getAbsolutePath();
                    path = SystemManagement.getTextEditorPath();
                }
            }
            // Windows case.
            if (SystemManagement.getOSName().contains("Windows")) {
                // Opening the text editor to create a file.
                if (dirPath == null) {
                    cmd = new String[5];
                    cmd[0] = "cmd";
                    cmd[1] = "/c";
                    cmd[2] = "start";
                    cmd[3] = "\"\"";
                    cmd[4] = "\"" + SystemManagement.getTextEditorPath() + "\"";
                    path = SystemManagement.getTextEditorPath();
                }
                // Opening the Text Editor to modify a file.
                if (dirPath != null) {
                    cmd = new String[6];
                    cmd[0] = "cmd";
                    cmd[1] = "/c";
                    cmd[2] = "start";
                    cmd[3] = "\"\"";
                    cmd[4] = "\"" + SystemManagement.getTextEditorPath() + "\"";
                    cmd[5] = this.fileChooser.getSelectedFile().getAbsolutePath();
                    path = SystemManagement.getTextEditorPath();
                }
            }

            if (cmd != null) {
                // Starting the program.
                String[] returnedMessage = SystemManagement.executeProgramAndWait(cmd);
                if (returnedMessage != null) {
                    if (dirPath != null) {
                        // Restoring previous rwx values for the Log File.
                        if (dirPath.equals(SystemManagement.getLogWorkingFolder())) {
                            this.fileChooser.getSelectedFile().setExecutable(canExecuteLogFile);
                            this.fileChooser.getSelectedFile().setReadable(canReadLogFile);
                            this.fileChooser.getSelectedFile().setWritable(canWriteLogFile);
                        }
                        /*
                         * If the User has opened a Test to modify it, it checks
                         * if it is a valid XML for the XML Schema.
                         */
                        if (dirPath.equals(SystemManagement.getTestsWorkingFolder())) {
                            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "\t(XTD Validator) Validating the XTD file: \"" + this.fileChooser.getSelectedFile().getAbsolutePath() + "\".");
                            try {
                                SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                                Schema schema = factory.newSchema(new File(SystemManagement.getTestsWorkingFolder() + "testXMLSchema.xsd"));
                                Validator validator = schema.newValidator();
                                DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                                Document document = parser.parse(this.fileChooser.getSelectedFile());
                                validator.validate(new DOMSource(document));
                                JOptionPane.showMessageDialog(WindowManager.main, "\"" + this.fileChooser.getSelectedFile().getAbsolutePath() + "\" is valid.", "Test Validator", JOptionPane.INFORMATION_MESSAGE);
                            } catch (SAXException ee) {
                                JOptionPane.showMessageDialog(WindowManager.main, "\t(XTD Validator) Can not validate \"" + this.fileChooser.getSelectedFile().getName() + "\":\n" + this.addANewlineToSemiColons(ee.getMessage()) , "Test Validator", JOptionPane.ERROR_MESSAGE);
                            } catch (ParserConfigurationException ee) {
                                JOptionPane.showMessageDialog(WindowManager.main, "\t(XTD Validator) Can not validate \"" + this.fileChooser.getSelectedFile().getName() + "\":\n" + this.addANewlineToSemiColons(ee.getMessage()) , "Test Validator", JOptionPane.ERROR_MESSAGE);
                            } catch (IOException ee) {
                                JOptionPane.showMessageDialog(WindowManager.main, "\t(XTD Validator) Can not validate \"" + this.fileChooser.getSelectedFile().getName() + "\":\n" + this.addANewlineToSemiColons(ee.getMessage()) , "Test Validator", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                        /*
                         * If the User has opened a Report File, checks if it
                         * is a valid XML for the XML Schema.
                         */
                        if (dirPath.equals(SystemManagement.getResultsWorkingFolder())) {
                            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "\t(XTR Validator) Validating the XTR file: \"" + this.fileChooser.getSelectedFile().getAbsolutePath() + "\".");
                            try {
                                SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                                Schema schema = factory.newSchema(new File(SystemManagement.getResultsWorkingFolder() + "reportsXMLSchema.xsd"));
                                Validator validator = schema.newValidator();
                                DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                                Document document = parser.parse(this.fileChooser.getSelectedFile());
                                validator.validate(new DOMSource(document));
                                JOptionPane.showMessageDialog(WindowManager.main, "\"" + this.fileChooser.getSelectedFile().getAbsolutePath() + "\" is valid.", "Report File Validator", JOptionPane.INFORMATION_MESSAGE);
                            } catch (SAXException ee) {
                                JOptionPane.showMessageDialog(WindowManager.main, "\t(XTR Validator) Can not validate \"" + this.fileChooser.getSelectedFile().getName() + "\":\n" + this.addANewlineToSemiColons(ee.getMessage()) , "Test Validator", JOptionPane.ERROR_MESSAGE);
                            } catch (ParserConfigurationException ee) {
                                JOptionPane.showMessageDialog(WindowManager.main, "\t(XTR Validator) Can not validate \"" + this.fileChooser.getSelectedFile().getName() + "\":\n" + this.addANewlineToSemiColons(ee.getMessage()) , "Test Validator", JOptionPane.ERROR_MESSAGE);
                            } catch (IOException ee) {
                                JOptionPane.showMessageDialog(WindowManager.main, "\t(XTR Validator) Can not validate \"" + this.fileChooser.getSelectedFile().getName() + "\":\n" + this.addANewlineToSemiColons(ee.getMessage()) , "Test Validator", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                    // The returnedMessages contains the error message.
                    //if (returnedMessage[1].length() != 0) {
                    //    SystemManagement.manageError(Boolean.FALSE, "\tHas occourred a problem with the Text Editor.");
                    //    JOptionPane.showMessageDialog(WindowManager.main, "Has occourred a problem with the Text Editor:\n" + returnedMessage[1], "\tText Editor - Error Stream", JOptionPane.WARNING_MESSAGE);
                    //}
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "\tText Editor closed.");
                } else
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "\t(Text Editor) Has occourred a while problem executing the program.");
            } else
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "\t(Text Editor) Has occourred a problem while executing the program.");
        }
    }

    /**
     * Sets the GridBagConstraints's values for the GridBagConstraints passed.
     * @param gbc a GridBagConstraints element that identifies the
     * GridBagConstraints to set.
     * @param gridX an integer that identifies the gridx value for the
     * GridBagConstraints. This value identifies the x element coordinate in the
     * GridBagLayout.
     * @param gridY an integer that identifies the gridy value for the
     * GridBagConstraints. This value identifies the y element coordinate in the
     * GridBagLayout.
     * @param gridWidth an integer that identifies the gridwidth value for the
     * GridBagConstraints. This value identifies the element width
     * (cells number) in the GridBagLayout.
     * @param gridHeight an integer that identifies the gridheight value for the
     * GridBagConstraints's gridheight. This value identifies the element height
     * (cells number) in the GridBagLayout.
     * @param weightX an integer that identifies the weightx value for the
     * GridBagConstraints.
     * @param weightY an integer that identifies the weighty value for the
     * GridBagConstraints.
     * @param anchor an integer that identifies the anchor value for the
     * GridBagConstraints. This value identifies the element anchor in the
     * GridBagLayout.
     * @param fill an integer that identifies the fill value for the
     * GridBagConstraints.
     */
    private void setGrigBagConstraints(GridBagConstraints gbc, int gridX, int gridY, int gridWidth, int gridHeight, double weightX, double weightY, int anchor, int fill) {
        gbc.gridx = gridX;
        gbc.gridy = gridY;
        gbc.gridwidth = gridWidth;
        gbc.gridheight = gridHeight;
        gbc.weightx = weightX;
        gbc.weighty = weightY;
        gbc.anchor = anchor;
        gbc.fill = fill;
    }

    /**
     * Refactors the input String replacing a new line when it founds a
     * semicolon ";".
     * @param s a String that identifies the String to refactor.
     * @return a String that identifies the String refactored.
     */
    private String addANewlineToSemiColons(String s) {
        // Replaces all ";" characters with ";\n".
        Pattern semicolonPattern = Pattern.compile(";");
        Matcher m1 = semicolonPattern.matcher(s);
        String tmp1 = m1.replaceAll(";\n");
        return tmp1;
        // Replaces all "." characters with ".\n".
        /*Pattern fullStopPattern = Pattern.compile(".");
        Matcher m2 = fullStopPattern.matcher(tmp1);
        return m2.replaceAll(".\n");*/
    }

    /**
     * This Class manages the "Screenshots to Check" column.
     */
    private class ResultsTableEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
        private static final long serialVersionUID = 1L;
        private JButton button;
        private int columnSelected, rowSelected;
        private String cellValue;
        protected static final String buttonActionCommand = "bac";

        /**
         * Set up the editor (from the table's point of view), which is a button.
         * This button brings up the color chooser dialog, which is the editor from
         * the user's point of view.
         */
        public ResultsTableEditor() {
            this.button = new JButton();
            this.button.setActionCommand(ResultsTableEditor.buttonActionCommand);
            this.button.addActionListener(ResultsTableEditor.this);
            this.button.setBorderPainted(false);
            this.columnSelected = -1;
            this.rowSelected = -1;
            this.cellValue = null;
        }

        /**
         * Handles events from the editor button and from
         * the dialog's OK button.
         * @param e an ActionEvent element that identifies the Event.
         */
        public void actionPerformed(ActionEvent e) {
            if (ResultsTableEditor.buttonActionCommand.equals(e.getActionCommand())) {
                //The user has clicked the cell, so bring up the dialog.
                this.button.setBackground(Color.WHITE);
                //Make the renderer reappear.
                fireEditingStopped();
                // Gets the TC and TS paths.
                Pattern pattern = Pattern.compile("[\\/.?\\w]+#[\\/.?\\w]+");
                Matcher matcher = pattern.matcher(this.cellValue);
                String tsPath = null;
                String tcPath = null;
                while (matcher.find()) {
                    String tmp = matcher.group();
                    String[] tmp2 = tmp.split("#");
                    tsPath = (tmp2[0].equals("null"))? null:tmp2[0];
                    tcPath = (tmp2[1].equals("null"))? null:tmp2[1];
                }
                JOptionPane.showMessageDialog(WindowManager.main, "<html><head></head><body><p>To interact with the ATT Results Validator, You can:<ul><li>use a single left click to accept the screenshot and pass to the next screenshot;</li><li>use a single right click to reject the screenshot and pass to the next screenshot;</li><li>use a single central click (or wheel click) to set the pending status to the screenshot and pass to the next screenshot.</li></ul></p><p>When the screenshots are finished, the program will return to the Results Table.</p></body></html>", "Results Validator", JOptionPane.INFORMATION_MESSAGE);
                WindowManager.main.setState(JFrame.ICONIFIED);
                WindowManager.resultsValidator = new JFrame("Automatic Testing Tool - Test Results Validator");
                WindowManager.resultsValidator.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Test Executor) Starting the Results Validator.");
                ResultsValidator panel = new ResultsValidator(tsPath, tcPath);
                WindowManager.resultsValidator.getContentPane().add(panel);
                WindowManager.resultsValidator.pack();
                WindowManager.resultsValidator.addMouseListener(panel);
                WindowManager.resultsValidator.setVisible(true);
                WindowManager.resultsValidator.toFront();
            }
        }

        /**
         * Implements the one CellEditor method that AbstractCellEditor does not.
         * @return
         */
        public Object getCellEditorValue() {
            return "<html><head></head><body><p color=\"green\">Screenshots checked<p></body></html>";
        }

        /**
         * Implement the one method defined by TableCellEditor.
         * @param table
         * @param value
         * @param isSelected
         * @param row
         * @param column
         * @return
         */
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.cellValue = value.toString();
            this.rowSelected = row;
            this.columnSelected = column;
            if (!value.toString().equals("<html><head></head><body><p color=\"green\">Screenshots checked<p></body></html>") && !value.toString().equals("<html><head></head><body><p color=\"green\">No Screenshots To Check</p></body></html>"))
                return this.button;
            else
                return null;
        }
    }
}