/**
 * 
 */
package it.sergioferraresi.att.model;

/**
 * Identifies the XML File Types.<br/>
 * Each XML File Type contains the extension (e.g.: .xtd).
 * 
 * @author psf563
 */
public enum XmlFileType {

	/**
	 * Identifies an "XML for Tests Description" (XTD) file.
	 */
	XTD {
		@Override
		public String extension() {
			return ".xtd"; //$NON-NLS-1$
		}
	},
	/**
	 * Identifies an "XML for Tests Reports" (XTR) file.
	 */
	XTR {
		@Override
		public String extension() {
			return ".xtr"; //$NON-NLS-1$1
		}
	};
	
	public abstract String extension();

}