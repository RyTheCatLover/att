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
 * Filename         Main.java
 * Created on       2010-07-29
 * Last modified on 2016-02-11
 */
package it.sergioferraresi.att;

import it.sergioferraresi.att.console.ConsoleManager;
import it.sergioferraresi.att.ui.WindowManager;

/**
 * Starts the main program.<br/>
 * If the User wants to start the "Window" modality, he must invoke the ATT
 * program without passing any argument.
 * If the User wants to start the "Console" modality, he must invoke the ATT
 * program passing some arguments according with its usage.
 *
 * @author  Sergio Ferraresi (psf563)
 * @version 1.0 (release 20101209fr)
 */
public class Main {

    /**
     * Starts the main program.
     * @param args an array of Strings taht identifies the command line
     * arguments.
     */
    public static void main(String[] args) {
        // Launches the Main Program.
        if (args.length == 0) {
            WindowManager windowManager = new WindowManager();
        } else {
            ConsoleManager consoleManager = new ConsoleManager(args);
        }
        // Cleans the System.
        System.gc();
        System.runFinalization();
    }
}