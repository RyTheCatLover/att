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
 * Filename         UserActionSimulator.java
 * Created on       2010-07-29
 * Last modified on 2016-02-11
 */

package it.sergioferraresi.att;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.*;

/**
 * Provides the mothods useful to simulate and automate the user interaction
 * with the System.
 *
 * @author  Sergio Ferraresi (psf563)
 * @version 1.0 (release 20101209fr)
 */
public class UserActionSimulator {
    /**
     * Identifies the java.awt.Robot instance.
     */
    private Robot robot;
    /**
     * Identifies the default delay for the Simulator.
     */
    private int defaultDelay;
    /**
     * Identifies the last screenshot took by the Simulator.
     */
    private File lastScreenshot;
    /**
     * Identifies the last screenshot took by the Simulator for the
     * intelligentWait() method.
     */
    private String lastIntelligentWaitScreenshot;



    /**
     * Initalizes the Robot with the default delay of 500 ms.
     */
    public UserActionSimulator () {
        this(500);
    }

    /**
     * Initalizes the Robot with the delay passed.
     * @param newDelay an integer that identifies the new defalut Robot delay.
     */
    public UserActionSimulator (int newDelay) {
        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            SystemManagement.manageError(Boolean.TRUE, "(Simulator) Error while creating the Robot: " + e.getMessage());
        }
        this.defaultDelay = 500;
        this.lastScreenshot = null;
        this.lastIntelligentWaitScreenshot = null;
    }

    /**
     * Sets the default delay used by the Simulator to millis.
     * @param millis an integer that identifies the new delay used by the
     * Simulator.
     */
    public void setDefautlDelay (int millis) {
        this.defaultDelay = millis;
    }

    /**
     * Returns the default delay used by the Simulator.
     * @return the delay used by the Simulator.
     */
    public int getDefaultDelay () {
        return this.defaultDelay;
    }

    /**
     * Waits for millis milliseconds.
     * @param millis an integer that identifies the number of milliseconds to
     * wait.
     */
    public void delay (int millis) {
        this.robot.delay(millis);
    }

    /**
     * Returns the Robot of the Simulator.
     * @return a Robot element that identifies the Robot of the Simulator.
     */
    public Robot getRobot () {
        return this.robot;
    }

    /**
     * Returns the path of the last screenshot took by the Simulator.
     * @return a String that identifies the last screenshot took by the
     * Simulator.
     */
    public String getLastScreenshotPath() {
        return this.lastScreenshot.getAbsolutePath();
    }

    /**
     * Returns the path of the last screenshot took by the Simulator for the
     * intelligentWait() method.
     * @return a String that identifies the last screenshot took by the
     * Simulator for the intelligentWait() method.
     */
    public String getLastIntelligentWaitScreenshotPath() {
        return this.lastIntelligentWaitScreenshot;
    }

    /**
     * Moves the mouse to (x, y). It does not click on (x, y).
     * @param x an integer that identifies the x coordinate of the point.
     * @param y an integer that identifies the y coordinate of the point.
     */
    public void moveToXY (int x, int y) {
        this.robot.mouseMove(x, y);
    }

    /**
     * Clicks with the left mouse button on the point where the Robot previously
     * moved to.
     */
    public void singleLeftClick () {
        this.robot.mousePress(InputEvent.BUTTON1_MASK);
        this.robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    /**
     * Moves the mouse to (x, y) and simulates the left-click of the mouse.
     * @param x an integer that identifies the x coordinate of point to click.
     * @param y an integer that identifies the y coordinate of point to click.
     */
    public void moveAndSingleLeftClick (int x, int y) {
        this.moveToXY(x, y);
        this.singleLeftClick();
    }

    /**
     * Clicks with the right mouse button on the point where the Robot
     * previously moved to.
     */
    public void singleRightClick () {
        this.robot.mousePress(InputEvent.BUTTON3_MASK);
        this.robot.mouseRelease(InputEvent.BUTTON3_MASK);
    }

    /**
     * Moves the mouse to (x, y) and simulates the right-click of the mouse.
     * @param x an integer that identifies the x coordinate of point to click.
     * @param y an integer that identifies the y coordinate of point to click.
     */
    public void moveAndsingleRightClick (int x, int y) {
        this.moveToXY(x, y);
        this.singleRightClick();
    }

    /**
     * Simulates the drag&drop from the source point (sourceX, sourceY) to the
     * destination point (destX, destY).<br/>
     * Needs a delay (minimum 500ms) before a after the method call.
     * @param sourceX an integer that identifies the x coordinate of the source
     * point.
     * @param sourceY an integer that identifies the y coordinate of the source
     * point.
     * @param destX an integer that identifies the x coordinate of the
     * destination point.
     * @param destY an integer that identifies the y coordinate of the
     * destination point.
     */
    public void dragAndDrop (int sourceX, int sourceY, int destX, int destY) {
        this.moveToXY(sourceX, sourceY);
        this.robot.mousePress(InputEvent.BUTTON1_MASK);
        this.delay(500);
        this.moveToXY(destX, destY);
        this.robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    /**
     * Keys the String passed.
     * @param keys a String that identifies the String to key.
     * @return false if an error occour, true otherwise.
     */
    public Boolean pressKeys (String keys) {
        ArrayList<Integer> keyCodes = new ArrayList<Integer>();
        //int keyCode = -1;
        Boolean isShiftActivated = Boolean.FALSE;
        Boolean isAltActivated = Boolean.FALSE;
        Boolean isControlActivated = Boolean.FALSE;
        Boolean isUpperCaseActivated = Boolean.FALSE;
        for (int i = 0; i < keys.length(); i++) {
            char character = keys.charAt(i);
            SystemManagement.appendToLogAndToInterface(SystemManagement.LOG_TEXT_TYPE, String.valueOf(character));
            if (Character.isSpaceChar(character))
                keyCodes.add(KeyEvent.VK_SPACE);
            else if (Character.isLetter(character)) {
                if (SystemManagement.IS_OS_WINDOWS)
                    isAltActivated = Boolean.TRUE;
                if (SystemManagement.IS_OS_LINUX) {
                    this.robot.keyPress(KeyEvent.VK_CONTROL);
                    this.robot.keyPress(KeyEvent.VK_SHIFT);
                    this.robot.keyPress(KeyEvent.VK_U);
                    this.robot.keyRelease(KeyEvent.VK_U);
                    isControlActivated = Boolean.TRUE;
                    isShiftActivated = Boolean.TRUE;
                }
                if (SystemManagement.IS_OS_WINDOWS) {
                    String intValue = String.valueOf((int)character);
                    for (int j = 0; j < intValue.length(); j++) {
                        char tmp = intValue.charAt(j);
                        switch(tmp) {
                            case '0': {
                                keyCodes.add(KeyEvent.VK_NUMPAD0);
                                break;
                            }
                            case '1': {
                                keyCodes.add(KeyEvent.VK_NUMPAD1);
                                break;
                            }
                            case '2': {
                                keyCodes.add(KeyEvent.VK_NUMPAD2);
                                break;
                            }
                            case '3': {
                                keyCodes.add(KeyEvent.VK_NUMPAD3);
                                break;
                            }
                            case '4': {
                                keyCodes.add(KeyEvent.VK_NUMPAD4);
                                break;
                            }
                            case '5': {
                                keyCodes.add(KeyEvent.VK_NUMPAD5);
                                break;
                            }
                            case '6': {
                                keyCodes.add(KeyEvent.VK_NUMPAD6);
                                break;
                            }
                            case '7': {
                                keyCodes.add(KeyEvent.VK_NUMPAD7);
                                break;
                            }
                            case '8': {
                                keyCodes.add(KeyEvent.VK_NUMPAD8);
                                break;
                            }
                            case '9': {
                                keyCodes.add(KeyEvent.VK_NUMPAD9);
                                break;
                            }
                        }
                    }
                }
                if (SystemManagement.IS_OS_LINUX) {
                    String hexValue = Integer.toHexString((int)character);
                    for (int j = 0; j < hexValue.length(); j++) {
                        if (Character.isDigit(hexValue.charAt(j)))
                            keyCodes.add((int)hexValue.charAt(j));
                        else
                            keyCodes.add((int)Character.toUpperCase(hexValue.charAt(j)));
                    }
                }
            } else if (Character.isDigit(character))
                keyCodes.add((int)character);
            else {
                if (SystemManagement.IS_OS_WINDOWS)
                    isAltActivated = Boolean.TRUE;
                if (SystemManagement.IS_OS_LINUX) {
                    this.robot.keyPress(KeyEvent.VK_CONTROL);
                    this.robot.keyPress(KeyEvent.VK_SHIFT);
                    this.robot.keyPress(KeyEvent.VK_U);
                    this.robot.keyRelease(KeyEvent.VK_U);
                    isControlActivated = Boolean.TRUE;
                    isShiftActivated = Boolean.TRUE;
                }
                switch(character) {
                    case '!': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD3);
                            keyCodes.add(KeyEvent.VK_NUMPAD3);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_2);
                            keyCodes.add(KeyEvent.VK_1);
                        }
                        break;
                    }
                    case '"': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD3);
                            keyCodes.add(KeyEvent.VK_NUMPAD4);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_2);
                            keyCodes.add(KeyEvent.VK_2);
                        }
                        break;
                    }
                    case '#': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD3);
                            keyCodes.add(KeyEvent.VK_NUMPAD5);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_2);
                            keyCodes.add(KeyEvent.VK_3);
                        }
                        break;
                    }
                    case '$': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD3);
                            keyCodes.add(KeyEvent.VK_NUMPAD6);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_2);
                            keyCodes.add(KeyEvent.VK_4);
                        }
                        break;
                    }
                    case '%': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD3);
                            keyCodes.add(KeyEvent.VK_NUMPAD7);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_2);
                            keyCodes.add(KeyEvent.VK_5);
                        }
                        break;
                    }
                    case '&': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD3);
                            keyCodes.add(KeyEvent.VK_NUMPAD8);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_2);
                            keyCodes.add(KeyEvent.VK_6);
                        }
                        break;
                    }
                    case '\'': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD3);
                            keyCodes.add(KeyEvent.VK_NUMPAD9);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_2);
                            keyCodes.add(KeyEvent.VK_7);
                        }
                        break;
                    }
                    case '(': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD4);
                            keyCodes.add(KeyEvent.VK_NUMPAD0);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_2);
                            keyCodes.add(KeyEvent.VK_8);
                        }
                        break;
                    }
                    case ')': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD4);
                            keyCodes.add(KeyEvent.VK_NUMPAD1);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_2);
                            keyCodes.add(KeyEvent.VK_9);
                        }
                        break;
                    }
                    case '*': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD4);
                            keyCodes.add(KeyEvent.VK_NUMPAD2);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_2);
                            keyCodes.add(KeyEvent.VK_A);
                        }
                        break;
                    }
                    case '+': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD4);
                            keyCodes.add(KeyEvent.VK_NUMPAD3);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_2);
                            keyCodes.add(KeyEvent.VK_B);
                        }
                        break;
                    }
                    case ',': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD4);
                            keyCodes.add(KeyEvent.VK_NUMPAD4);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_2);
                            keyCodes.add(KeyEvent.VK_C);
                        }
                        break;
                    }
                    case '-': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD4);
                            keyCodes.add(KeyEvent.VK_NUMPAD5);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_2);
                            keyCodes.add(KeyEvent.VK_D);
                        }
                        break;
                    }
                    case '.': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD4);
                            keyCodes.add(KeyEvent.VK_NUMPAD6);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_2);
                            keyCodes.add(KeyEvent.VK_E);
                        }
                        break;
                    }
                    case '/': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD4);
                            keyCodes.add(KeyEvent.VK_NUMPAD7);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_2);
                            keyCodes.add(KeyEvent.VK_F);
                        }
                        break;
                    }
                    case ':': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD5);
                            keyCodes.add(KeyEvent.VK_NUMPAD8);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_3);
                            keyCodes.add(KeyEvent.VK_A);
                        }
                        break;
                    }
                    case ';': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD5);
                            keyCodes.add(KeyEvent.VK_NUMPAD9);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_3);
                            keyCodes.add(KeyEvent.VK_B);
                        }
                        break;
                    }
                    case '<': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD6);
                            keyCodes.add(KeyEvent.VK_NUMPAD0);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_3);
                            keyCodes.add(KeyEvent.VK_C);
                        }
                        break;
                    }
                    case '=': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD6);
                            keyCodes.add(KeyEvent.VK_NUMPAD1);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_3);
                            keyCodes.add(KeyEvent.VK_D);
                        }
                        break;
                    }
                    case '>': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD6);
                            keyCodes.add(KeyEvent.VK_NUMPAD2);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_3);
                            keyCodes.add(KeyEvent.VK_E);
                        }
                        break;
                    }
                    case '?': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD6);
                            keyCodes.add(KeyEvent.VK_NUMPAD3);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_3);
                            keyCodes.add(KeyEvent.VK_F);
                        }
                        break;
                    }
                    case '@': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD6);
                            keyCodes.add(KeyEvent.VK_NUMPAD4);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_4);
                            keyCodes.add(KeyEvent.VK_0);
                        }
                        break;
                    }
                    case '[': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD9);
                            keyCodes.add(KeyEvent.VK_NUMPAD1);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_5);
                            keyCodes.add(KeyEvent.VK_B);
                        }
                        break;
                    }
                    case '\\': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD9);
                            keyCodes.add(KeyEvent.VK_NUMPAD2);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_5);
                            keyCodes.add(KeyEvent.VK_C);
                        }
                        break;
                    }
                    case ']': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD9);
                            keyCodes.add(KeyEvent.VK_NUMPAD3);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_5);
                            keyCodes.add(KeyEvent.VK_D);
                        }
                        break;
                    }
                    case '^': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD9);
                            keyCodes.add(KeyEvent.VK_NUMPAD4);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_5);
                            keyCodes.add(KeyEvent.VK_E);
                        }
                        break;
                    }
                    case '_': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD9);
                            keyCodes.add(KeyEvent.VK_NUMPAD5);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_5);
                            keyCodes.add(KeyEvent.VK_F);
                        }
                        break;
                    }
                    case '`': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD9);
                            keyCodes.add(KeyEvent.VK_NUMPAD6);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_6);
                            keyCodes.add(KeyEvent.VK_0);
                        }
                        break;
                    }
                    case '{': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD1);
                            keyCodes.add(KeyEvent.VK_NUMPAD2);
                            keyCodes.add(KeyEvent.VK_NUMPAD3);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_7);
                            keyCodes.add(KeyEvent.VK_B);
                        }
                        break;
                    }
                    case '|': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD1);
                            keyCodes.add(KeyEvent.VK_NUMPAD2);
                            keyCodes.add(KeyEvent.VK_NUMPAD4);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_7);
                            keyCodes.add(KeyEvent.VK_C);
                        }
                        break;
                    }
                    case '}': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD1);
                            keyCodes.add(KeyEvent.VK_NUMPAD2);
                            keyCodes.add(KeyEvent.VK_NUMPAD5);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_7);
                            keyCodes.add(KeyEvent.VK_D);
                        }
                        break;
                    }
                    case '~': {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD1);
                            keyCodes.add(KeyEvent.VK_NUMPAD2);
                            keyCodes.add(KeyEvent.VK_NUMPAD6);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_7);
                            keyCodes.add(KeyEvent.VK_E);
                        }
                        break;
                    }
//                    case '£': { TODO
//                        if (SystemManagement.IS_OS_WINDOWS) {
//                            keyCodes.add(KeyEvent.VK_NUMPAD1);
//                            keyCodes.add(KeyEvent.VK_NUMPAD5);
//                            keyCodes.add(KeyEvent.VK_NUMPAD6);
//                        }
//                        if (SystemManagement.IS_OS_LINUX) {
//                            keyCodes.add(KeyEvent.VK_9);
//                            keyCodes.add(KeyEvent.VK_C);
//                        }
//                        break;
//                    }
                    default: {
                        if (SystemManagement.IS_OS_WINDOWS) {
                            keyCodes.add(KeyEvent.VK_NUMPAD6);
                            keyCodes.add(KeyEvent.VK_NUMPAD3);
                        }
                        if (SystemManagement.IS_OS_LINUX) {
                            keyCodes.add(KeyEvent.VK_3);
                            keyCodes.add(KeyEvent.VK_F);
                        }
                        SystemManagement.manageError(Boolean.FALSE, "(Simulator) Key not yet implemented.");
                        break;
                    }
                }
            }
            if (!keyCodes.isEmpty()) {
                //if (isShiftActivated)
                //    this.robot.keyPress(KeyEvent.VK_SHIFT);
                if (isAltActivated)
                    this.robot.keyPress(KeyEvent.VK_ALT);
                //if (isControlActivated)
                //    this.robot.keyPress(KeyEvent.VK_CONTROL);
                for (int j = 0; j < keyCodes.size(); j++) {
                    this.robot.keyPress(keyCodes.get(j));
                    this.robot.keyRelease(keyCodes.get(j));
                }
                keyCodes.clear();
                if (isShiftActivated)
                    this.robot.keyRelease(KeyEvent.VK_SHIFT);
                if (isAltActivated)
                    this.robot.keyRelease(KeyEvent.VK_ALT);
                if (isControlActivated)
                    this.robot.keyRelease(KeyEvent.VK_CONTROL);
                isShiftActivated = Boolean.FALSE;
                isAltActivated = Boolean.FALSE;
                isControlActivated = Boolean.FALSE;
            }
            this.robot.delay(100);
        }
        return Boolean.TRUE;
    }

    /**
     * Press a special key, like "HOME" or "CTRL + C".
     * @param keyCode an integer that identifies the key code to press.
     * @param keyCodeActivated an integer that identifies the key code to press
     * before the keyCode.
     */
    public void pressKey(int keyCode, int keyCodeActivated) {
        if (keyCodeActivated != -1)
            this.robot.keyPress(keyCodeActivated);
        this.robot.keyPress(keyCode);
        this.robot.keyRelease(keyCode);
        if (keyCodeActivated != -1)
            this.robot.keyRelease(keyCodeActivated);
    }

    /**
     * Takes and saves a screenshot from the start point (startX, startY) to the
     * end point (endX, endY).
     * @param filename a String that identifies the path to save the screenshot.
     * @param startX an integer that identifies the x coordinate of the start
     * point.
     * @param startY an integer that identifies the y coordinate of the start
     * point.
     * @param endX an integer that identifies the x coordinate of the end point.
     * @param endY an integer that identifies the y coordinate of the end point.
     * @return false if the method founds some errors, true otherwise.
     */
    public Boolean takeAndSaveAScreenshot (File filename, int startX, int startY, int endX, int endY) {
        BufferedImage image = new BufferedImage((endX - startX), (endY- startY), BufferedImage.TYPE_INT_RGB);
        image = this.robot.createScreenCapture(new Rectangle(startX, startY, (endX - startX), (endY - startY)));
        try {
            ImageIO.write(image, "png", filename);
            this.lastScreenshot = filename;
        } catch (IOException e) {
            SystemManagement.manageError(Boolean.FALSE, "(Simulator) Error while saving the image \"" + filename + "\": " + e.getMessage());
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * Takes a temporary screenshot of the desktop and compares it with the last
     * screenshot saved.
     * @param startX an integer that identifies the x coordinate of the start
     * point.
     * @param startY an integer that identifies the y coordinate of the start
     * point.
     * @param endX an integer that identifies the x coordinate of the end point.
     * @param endY an integer that identifies the y coordinate of the end point.
     * @return an integer that identifies the the proportion of inequality.
     */
    public int takeAndCompareScreenshot (int startX, int startY, int endX, int endY) {
        BufferedImage newScreenShot = new BufferedImage((endX - startX), (endY- startY), BufferedImage.TYPE_INT_RGB);
        newScreenShot = this.robot.createScreenCapture(new Rectangle(startX, startY, (endX - startX), (endY - startY)));
        BufferedImage lastScreenShot;
        try {
            lastScreenShot = ImageIO.read(new File(this.lastIntelligentWaitScreenshot));
        } catch (IOException e) {
            SystemManagement.manageError(Boolean.FALSE, "(Simulator) Error while reading the image \"" + this.lastIntelligentWaitScreenshot + "\": " + e.getMessage());
            return -1;
        }
        int[] newPixels = new int[newScreenShot.getWidth() * newScreenShot.getHeight()];
        int[] oldPixels = new int[lastScreenShot.getWidth() * lastScreenShot.getHeight()];
        PixelGrabber newImg = new PixelGrabber(newScreenShot.getSource(), 0, 0, newScreenShot.getWidth(), newScreenShot.getHeight(), newPixels, 0, newScreenShot.getWidth());
        PixelGrabber oldImg = new PixelGrabber(lastScreenShot.getSource(), 0, 0, lastScreenShot.getWidth(), lastScreenShot.getHeight(), oldPixels, 0, lastScreenShot.getWidth());

        newImg.startGrabbing();
        oldImg.startGrabbing();

        int howMuchTheyAreNotEquals = 0;
        for (int i = 0; i < Math.max(newPixels.length, oldPixels.length); i++)
            if (newPixels[i] != oldPixels[i])
                howMuchTheyAreNotEquals++;
        return ((100 * howMuchTheyAreNotEquals) / Math.max(newPixels.length, oldPixels.length));
    }

    /**
     * Takes and saves a screenshot for the intelligentWait() method from the
     * start point (startX, startY) to the end point (endX, endY).
     * @param filename a String that identifies the path to save the screenshot.
     * @param startX an integer that identifies the x coordinate of the start
     * point.
     * @param startY startY an integer that identifies the y coordinate of the start
     * point.
     * @param endX an integer that identifies the x coordinate of the end point.
     * @param endY an integer that identifies the y coordinate of the end point.
     * @return false if the method founds some errors, true otherwise.
     */
    public Boolean takeAnIntelligentWaitScreenshot(File filename, int startX, int startY, int endX, int endY) {
        BufferedImage image = new BufferedImage((endX - startX), (endY- startY), BufferedImage.TYPE_INT_RGB);
        image = this.robot.createScreenCapture(new Rectangle(startX, startY, (endX - startX), (endY - startY)));
        try {
            ImageIO.write(image, "png", filename);
            this.lastIntelligentWaitScreenshot = filename.getAbsolutePath();
        } catch (IOException e) {
            SystemManagement.manageError(Boolean.FALSE, "(Simulator) Error while saving the image \"" + filename + "\": " + e.getMessage());
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * Block the program execution untill the desktop change.<br/>
     * Before calling this method, is necessary to call the
     * takeAnIntelligentWaitScreenshot method to save a screeenshot before it
     * changes.
     * @param startX an integer that identifies the x coordinate of the start
     * point.
     * @param startY an integer that identifies the y coordinate of the start
     * point.
     * @param endX an integer that identifies the x coordinate of the end point.
     * @param endY an integer that identifies the y coordinate of the end point.
     */
    public void intelligentWait (int startX, int startY, int endX, int endY) {
        this.robot.delay(500);
        Boolean secondPeriod = Boolean.FALSE;
        int areEquals = this.takeAndCompareScreenshot(startX, startY, endX, endY);
        /*
         * If the images are equals we could be both in the First and in the
         * Third Period.
         * If the images are not equals we are in the Second Period and the
         * program is loading.
         */
        if (areEquals == 0) {
            // Is the program in the pre-loading time? Or, is it already loaded?
            int time = 0;
            int areTheyEquals = 100;
            //while ((time < 10) && !secondPeriod) {
            while ((time < 7) && !secondPeriod) {
                this.takeAnIntelligentWaitScreenshot(new File(SystemManagement.getScreenshotsExecutionWorkingFolder() + "screenshot_" + SystemManagement.getCompactDate() + "_" + SystemManagement.getCompactHour() + ".png"), startX, startY, endX, endY);
                areTheyEquals = this.takeAndCompareScreenshot(startX, startY, endX, endY);
                /*
                 * If the images are equals, we increase the time variable
                 * because we probably are in the Third Period: we have to check
                 * this.
                 * If the images are not equals, we are in the First Period and
                 * we need to to pass to the Second Period.
                 */
                //if (areTheyEquals < 6)
                if (areTheyEquals == 0)
                    time++;
                else
                    secondPeriod = Boolean.TRUE;
            }
        } else
            secondPeriod = Boolean.TRUE;
        if (secondPeriod) {
            // Second Period.
            int time = 0;
            int areTheyEquals = 100;
            while (time < 4) {
                this.takeAnIntelligentWaitScreenshot(new File(SystemManagement.getScreenshotsExecutionWorkingFolder() + "screenshot_" + SystemManagement.getCompactDate() + "_" + SystemManagement.getCompactHour() + ".png"), startX, startY, endX, endY);
                areTheyEquals = this.takeAndCompareScreenshot(startX, startY, endX, endY);
                if (areTheyEquals < 6)
                    time++;
            }
            // Third Period.
        }
        this.robot.delay(500);
    }

    /**
     * Tells if two UserActionSimulator are equals.
     * @param anObject an UserActionSimulator element that identifies the
     * UserActionSimulator to check.
     * @return true if they are equals, false otherwise.
     */
    public boolean equals(UserActionSimulator anObject) {
        return (this.robot.equals(anObject.getRobot()) && (this.defaultDelay == anObject.getDefaultDelay()));
    }

    /**
     * Tells if two UserActionSimulator are equals.
     * @param anObject an Object element that identifies the UserActionSimulator
     * to check.
     * @return true if they are equals, false otherwise.
     */
    @Override
    public boolean equals(Object anObject) {
        return (anObject instanceof UserActionSimulator)? (this.equals((UserActionSimulator)anObject)):false;
    }

    /**
     * Returns the hash code of the instance.
     * @return an integere that identifies the hash code of the instance.
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.robot != null ? this.robot.hashCode() : 0);
        hash = 89 * hash + this.defaultDelay;
        return hash;
    }

    /**
     * Returns a description of the instance.
     * @return a String that identifies the description of the instance.
     */
    @Override
    public String toString() {
        return this.robot.toString() + " " + this.defaultDelay;
    }
}