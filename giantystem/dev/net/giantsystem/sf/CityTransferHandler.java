/*
 *GiANT - Graphical Algebra System
 *
 *Copyright (C) 2005  Aneesh Karve, e33nflow@users.sourceforge.net
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.*/
/*
 * CityTransferHandler.java
 *
 * Created on June 6, 2005, 12:38 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package net.giantsystem.sf;

/**
 * Import a single polynomial and try to form the
 * relative extension defined by it.
 * @see VariableTransferHandler
 * @see StringTransferHandler
 * @author karve
 */
public class CityTransferHandler extends VariableTransferHandler{
	
	/** Creates a new instance of CityTransferHandler */
	public CityTransferHandler() {
	}

	/**
	 * Work with the dropped polynomial
	 * @param c The component receving the drop
	 * @param str the incoming <CODE>String</CODE>
	 */
	protected void importString(javax.swing.JComponent c, String str) {
		
		String [] polys = str.split(ROW_SEPARATOR);
		if(polys.length > 1)
			GiANT.gui.appendConsoleText("\nDrop contains multiple items, taking the first...", true);
		for(int i = 0; i < 1; i++){
			String poly[] = polys[i].split(COLUMN_SEPARATOR);
			String polyName = poly[NAME_COL];
			if(GiANT.gui.kash.isPoly(polyName)){
				String fieldName = NumberField.getCurrentName();
				KASHTerm.Response r = GiANT.gui.kash.relativeExtension(fieldName, polyName);        
		        if(r.error()){
					System.err.println("Error creating new numberfield:  " + r.getError());
				}else{
					NumberField n = new NumberField(r.getOutput());
					GiANT.gui.addField(n);
				}
			}else{
				GiANT.gui.appendConsoleText("\nYou can only drop Polynomials onto the desktop.", true);
				return;//don't import any other data types
			}
		}
	}
	
}
