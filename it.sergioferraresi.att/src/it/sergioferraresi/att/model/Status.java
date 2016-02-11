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
 * Filename         Status.java
 * Created on       2015-06-23
 * Last modified on 2016-02-11
 */
package it.sergioferraresi.att.model;

/**
 * Identifies the exit statuses for the ATT Program and the Test Executor.<br/>
 * Each Status contains the description (e.g.: Pass) and the exit code (e.g.: 0).
 * 
 * @author psf563
 */
public enum Status {

	/**
	 * Correct exit status. Test executed and passed.
	 */
	PASS{
		@Override
		public String description() {
			return "Pass"; //$NON-NLS-1$
		}

		@Override
		public int exitCode() {
			return 0;
		}
	},
	/**
	 * Pending exit status. Test executed, but needs to validate the screenshots for the manual validation.
	 */
	PENDING {
		@Override
		public String description() {
			return "Pending"; //$NON-NLS-1$
		}

		@Override
		public int exitCode() {
			return 2;
		}
	},
	/**
	 * Incorrect exit status. Test executed and not passed: needs to validate the not equals screenshots for the automatic validation.
	 */
	FAIL {
		@Override
		public String description() {
			return "Fail"; //$NON-NLS-1$
		}

		@Override
		public int exitCode() {
			return 1;
		}
	},
	/**
	 * Incorrect exit status. Program Error.
	 */
	ERROR {
		@Override
		public String description() {
			return "Error"; //$NON-NLS-1$
		}

		@Override
		public int exitCode() {
			return -1;
		}
	};
	
	public abstract int exitCode();
	
	public abstract String description();

}