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
 * Filename         ScriptExecutor.java
 * Created on       2010-10-08
 * Last modified on 2016-02-11
 */
package it.sergioferraresi.att;

import it.sergioferraresi.att.model.Status;

import java.io.File;
import java.util.ArrayList;

/**
 * Provides methods useful to execute a Scripts.
 *
 * @author  Sergio Ferraresi (psf563)
 * @version 1.0 (release 20101209fr)
 */
public class ScriptExecutor {
    /**
     * Identifies the output and error streams of the executed scripts.
     */
    private static ArrayList<String[]> outputMessages = new ArrayList<String[]>();
    /**
     * Not implemented.
     */
    private ScriptExecutor() {}

    /**
     * Returns the output and error streams of the executed scripts.
     * @return an array of String thta identifies the output and error streams
     * of the executed scripts.
     */
    public static ArrayList<String[]> getOutputMessages() {
        return ScriptExecutor.outputMessages;
    }

    /**
     * Executes a list of Scripts.
     * @param list an array of Object that contains the list of Scripts to
     * execute.
     * @return an integer that identifies the exit status for the scripts
     * execution. The accepted values are:
     * <ul>
     *   <li><i>SystemManagement.PASS_EXIT_STATUS</i>, if the scrips is
     *       executed and passed without errors;</li>
     *   <li><i>SystemManagement.FAIL_EXIT_STATUS</i>, if the script is
     *       executed and not passed: it has encountered an error durig its
     *       execution. The error is of the script;</li>
     *   <li><i>SystemManagement.ERROR_EXIT_STATUS</i>, if the script is not
     *       executed. Error made by the Program.</li>
     * </ul>
     */
    public static int execute(Object[] list) {
        for (int i = 0; i < list.length; i++) {
            String scriptFilename = list[i].toString();
            File scriptFile = new File(scriptFilename);
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Script Executor) Cheching if the script exists.");
            if (!scriptFile.exists()) {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Script Executor) The script \"" + scriptFile.getName() + "\" does not exist.");
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_SEPARATOR_TYPE, null);
                return Status.FAIL.exitCode();
            } else {
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Script Executor) The script \"" + scriptFile.getName() + "\" exists.");
                scriptFile.setExecutable(true, false);
                SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Script Executor) Executing script: \"" + scriptFile.getAbsolutePath() + "\".");
                /*
                 * Control about the OS: Is used the "contains" method because in
                 * Windows-based OS, the name is composed by "Windows" plus the
                 * specific name of OS (like XP, Vista, ...).
                 */
                String[] cmd = null;
                // GNU/Linux case.
                if (SystemManagement.IS_OS_LINUX) {
                    cmd = new String[3];
                    cmd[0] = "/bin/sh";
                    cmd[1] = "-c";
                    cmd[2] = scriptFile.getAbsolutePath();
                }
                // Windows case.
                if (SystemManagement.IS_OS_WINDOWS) {
                    cmd = new String[7];
                    cmd[0] = "cmd";
                    cmd[1] = "/c";
                    cmd[2] = "start";
                    cmd[3] = "\"\"";
                    cmd[4] = "\"" + SystemManagement.getCygwinPath() + "\"";
                    cmd[5] = "--login";
                    cmd[6] = "\"" + scriptFile.getAbsolutePath() + "\"";
                }
                String[] returnedMessages = SystemManagement.executeProgramAndWait(cmd);
                String[] tmp = new String[3];
                Boolean error = Boolean.FALSE;
                if (returnedMessages != null) {
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Script Executor) Script Execution finished.");
                    tmp[0] = scriptFile.getName();
                    if (returnedMessages[0].length() == 0) {
                        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Script Executor) No Output.");
                        tmp[1] = "No Output for the Script.";
                    } else {
                        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Script Executor) Output Stream: " + returnedMessages[0]);
                        tmp[1] = returnedMessages[0];
                    }
                    if (returnedMessages[1].length() == 0) {
                        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Script Executor) No Error.");
                        tmp[2] = "No Errors for the Script.";
                    } else {
                        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Script Executor) Error while executing the script: " + cmd.toString());
                        SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Script Executor) Error Stream: " + returnedMessages[1]);
                        tmp[2] = returnedMessages[1];
                        error = Boolean.TRUE;
                    }
                    ScriptExecutor.outputMessages.add(tmp);
                    if (error)
                        return Status.FAIL.exitCode();
                } else {
                    SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, "(Script Executor) Error while executing the script: " + cmd.toString());
                    tmp[0] = scriptFile.getName();
                    tmp[1] = "Error while executing the Script.";
                    ScriptExecutor.outputMessages.add(tmp);
                    return Status.ERROR.exitCode();
                }
            }
        }
        return Status.PASS.exitCode();
    }
}