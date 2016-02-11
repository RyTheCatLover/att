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
 * Filename         ConsoleManager.java
 * Created on       2010-09-03
 * Last modified on 2016-02-11
 */
package it.sergioferraresi.att.console;

import it.sergioferraresi.att.SystemManagement;
import it.sergioferraresi.att.TestExecutor;
import it.sergioferraresi.att.model.AttInterface;
import it.sergioferraresi.att.model.ProjectMode;
import it.sergioferraresi.att.model.Status;

import java.io.File;

/**
 * Provides the Command Line Interface (CLI) for the Automatic Testing Tool
 * Program.<br/>
 * Provides also some other methods used by the other class to modify the CLI.
 * 
 * @author  Sergio Ferraresi (psf563)
 * @version 1.0 (release 20101209fr)
 */
public class ConsoleManager {
	
	/*
	 * TODO
	 * <program>  Copyright (C) <year>  <name of author>
    This program comes with ABSOLUTELY NO WARRANTY; for details type `show w'.
    This is free software, and you are welcome to redistribute it
    under certain conditions; type `show c' for details.
	 */
	
	
    /**
     * Inits the "Console" Interface according to the passed arguments.
     * @param args an array of Strings that idetifies the arguments passed from
     * the CLI.
     */
    public ConsoleManager(String[] args) {
        // Inits the Automatic Testing Tool.
        SystemManagement.initAutomaticTestingTool(AttInterface.CONSOLE);
        // Gets the consoleModality.
        String consoleModality = args[0];
        // Check if correct Program invokation.
        if ((consoleModality.charAt(0) == '-') && (Character.isLetter(consoleModality.charAt(1)) || (consoleModality.charAt(1) == '-'))) {
            if (consoleModality.equals("-c") || consoleModality.equals("--create")) {
                if ((args.length == 3) && !args[1].isEmpty() && !args[2].isEmpty()) {
                    String projectName = args[1];
                    String projectPath = args[2];
                    if (!projectPath.endsWith(File.separator))
                        projectPath += File.separator;
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Choosed the \"-c\" or the \"--create\" option: creating the \"" + projectName + "\" Project in the \"" + projectPath + "\" path.");
                    // Creates the Project.
                    SystemManagement.manageProject(ProjectMode.CREATE, projectPath + projectName + File.separator);
                    SystemManagement.getConsole().printf("%1$s%n", "The folder tree for the Project was created in: \"" + SystemManagement.getMainWorkingFolder() + "\".");
                } else
                    this.showHelpMessage(Boolean.TRUE);
            } else if (consoleModality.equals("-h") || consoleModality.equals("--help")) {
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Choosed the \"-h\" or the \"--help\" option: displaying the Program usage.");
                    // Shows the Program usage.
                    this.showHelpMessage(Boolean.FALSE);
            } else if (consoleModality.equals("-t") || consoleModality.equals("--tests")) {
                if (args.length > 1) {
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Choosed the \"-t\" or the \"--tests\" option: executing the Tests.");
                    // Executes Tests.
                    String[] files = new String[args.length - 1];
                    System.arraycopy(args, 1, files, 0, files.length);
                    SystemManagement.getConsole().printf("%1$s%n", "Executing Tests.");
                    int returnedValue = TestExecutor.execute(files);
                    SystemManagement.getConsole().printf("%1$s%n", "Execution finished. Check the following reports files:");
                    for (int i = 0; i < TestExecutor.getTestsResults().size(); i++)
                        SystemManagement.getConsole().printf("%1$s%n", TestExecutor.getTestsResults().get(i));
                    SystemManagement.getConsole().printf("%1$s%n", returnedValue);
                    SystemManagement.getConsole().printf("%1$s%n", "Exit value: " + ((returnedValue == Status.PASS.exitCode())? Status.PASS.description():((returnedValue == Status.PENDING.exitCode())? Status.PENDING.description():((returnedValue == Status.FAIL.exitCode())? Status.FAIL.description() : Status.ERROR.description()))));
                    System.exit(returnedValue);
                } else
                    this.showHelpMessage(Boolean.TRUE);
            } else if (consoleModality.equals("-ot")) {
                if (args.length > 3) {
                    String projectName = args[1];
                    String projectPath = args[2];
                    if (!projectPath.endsWith(File.separator))
                        projectPath += File.separator;
                    String[] files = new String[args.length - 3];
                    System.arraycopy(args, 3, files, 0, files.length);
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Choosed the \"-ot\"option: executing the tests stored in \"" + projectName + "\".");
                    // Executes Tests.
                    SystemManagement.getConsole().printf("%1$s%n", "Setting the Project Working Folder to \"" + projectPath + projectName + File.separator + "\". . .");
                    SystemManagement.manageProject(ProjectMode.OPEN, projectPath + projectName + File.separator);
                    SystemManagement.getConsole().printf("%1$s%n", "Executing Tests.");
                    int returnedValue = TestExecutor.execute(files);
                    SystemManagement.getConsole().printf("%1$s%n", "Execution finished. Check the following reports files:");
                    for (int i = 0; i < TestExecutor.getTestsResults().size(); i++)
                        SystemManagement.getConsole().printf("%1$s%n", TestExecutor.getTestsResults().get(i));
                    SystemManagement.getConsole().printf("%1$s%n", returnedValue);
                    SystemManagement.getConsole().printf("%1$s%n", "Exit value: " + ((returnedValue == Status.PASS.exitCode())? Status.PASS.description():((returnedValue == Status.PENDING.exitCode())? Status.PENDING.description():((returnedValue == Status.FAIL.exitCode())? Status.FAIL.description() : Status.ERROR.description()))));
                    System.exit(returnedValue);
                } else
                    this.showHelpMessage(Boolean.TRUE);
            } else
                this.showHelpMessage(Boolean.TRUE);
            SystemManagement.emptyExecutionFolder();
        } else
            this.showHelpMessage(Boolean.TRUE);
    }

    /**
     * Shows the Help Message, and shows a different Help Message in case of
     * error.
     * @param withError a Boolean that identifies the error case if it is setted
     * to true.
     */
    private void showHelpMessage(Boolean withError) {
        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "Showing the Help Message.");
        SystemManagement.getConsole().printf(SystemManagement.NEW_LINE);
        SystemManagement.getConsole().printf("%1$s%n", "\tSYNOPSIS:");
        SystemManagement.getConsole().printf(SystemManagement.NEW_LINE);
        SystemManagement.getConsole().printf("%1$s%n", "\t\tjava -jar att.jar OPTION [[projectName projectPath] | [test1 test2 ...] | [projectName projectPath test1 test2 ...]]\"");
        SystemManagement.getConsole().printf(SystemManagement.NEW_LINE);
        SystemManagement.getConsole().printf("%1$s%n", "\tDESCRIPTION:");
        SystemManagement.getConsole().printf("%1$s%n", "\t\t-c, --create projectName projectPath");
        SystemManagement.getConsole().printf("%1$s%n", "\t\t\tcreate the Project called \"projectName\" in the path \"projectPath\" and exit");
        SystemManagement.getConsole().printf("%1$s%n", "\t\t-h, --help");
        SystemManagement.getConsole().printf("%1$s%n", "\t\t\tdisplay this help and exit");
        SystemManagement.getConsole().printf("%1$s%n", "\t\t-o, --open projectName projectPath");
        SystemManagement.getConsole().printf("%1$s%n", "\t\t\topen the Project called \"projectName\" from the path \"projectPath\" and exit");
        SystemManagement.getConsole().printf("%1$s%n", "\t\t\twith -t, open the Project called \"projectName\" from the path \"projectPath\" and execute Tests");
        SystemManagement.getConsole().printf("%1$s%n", "\t\t-t, --tests test1 test2 ...");
        SystemManagement.getConsole().printf("%1$s%n", "\t\t\topen the Program default directory and execute Tests and exit");
        SystemManagement.getConsole().printf(SystemManagement.NEW_LINE);
        if (withError)
            SystemManagement.manageError(Boolean.TRUE, "Wrong usage of the Console Interface.");
    }
}