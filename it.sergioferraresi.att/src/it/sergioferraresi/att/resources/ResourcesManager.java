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
 * Filename         WindowManager.java
 * Created on       2015-03-27
 * Last modified on 2016-02-11
 */
package it.sergioferraresi.att.resources;

import javax.swing.ImageIcon;

/**
 * TODO
 * TODO XSD
 * @author psf563
 */
public class ResourcesManager {
	
    /**
     * The add arrow image.
     */
	public static ImageIcon IMG_ADD_ARROW = new ImageIcon(ResourcesManager.class.getClassLoader().getResource("imgs/green_arrow.png")); //$NON-NLS-1$

	/**
     * The ATT Program icon.
     */
    public static ImageIcon IMG_ATT_ICON = new ImageIcon(ResourcesManager.class.getClassLoader().getResource("imgs/ATT_logo_icon.png")); //$NON-NLS-1$

	/**
     * The ATT Program logo.
     */
    public static ImageIcon IMG_ATT_LOGO = new ImageIcon(ResourcesManager.class.getClassLoader().getResource("imgs/ATT_logo.png")); //$NON-NLS-1$

	/**
     * The green light image.
     */
    public static ImageIcon IMG_GREEN_LIGHT = new ImageIcon(ResourcesManager.class.getClassLoader().getResource("imgs/green_light.png")); //$NON-NLS-1$

    /**
     * The MEEO S.r.l. logo.
     */
    public static ImageIcon IMG_MEEO_LOGO = new ImageIcon(ResourcesManager.class.getClassLoader().getResource("imgs/MEEO_logo.png")); //$NON-NLS-1$

    /**
	 * The move down arrow image.
	 */
	public static ImageIcon IMG_MOVE_DOWN_ARROW = new ImageIcon(ResourcesManager.class.getClassLoader().getResource("imgs/down_black_arrow.png")); //$NON-NLS-1$

    /**
	 * The move up arrow image.
	 */
	public static ImageIcon IMG_MOVE_UP_ARROW = new ImageIcon(ResourcesManager.class.getClassLoader().getResource("imgs/up_black_arrow.png")); //$NON-NLS-1$

    /**
     * The red light image.
     */
    public static ImageIcon IMG_RED_LIGHT = new ImageIcon(ResourcesManager.class.getClassLoader().getResource("imgs/red_light.png")); //$NON-NLS-1$

    /**
	 * The remove arrow image.
	 */
	public static ImageIcon IMG_REMOVE_ARROW = new ImageIcon(ResourcesManager.class.getClassLoader().getResource("imgs/red_arrow.png")); //$NON-NLS-1$

}