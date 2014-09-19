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
 * IdealTransferHandler.java
 *
 * Created on June 5, 2005, 4:53 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package net.giantsystem.sf;

/**
 * Implements custom import behavior for Ideals tabs
 * @see VariableTransferHandler
 */
public class IdealTransferHandler extends VariableTransferHandler{
	
	/** Creates a new instance of IdealTransferHandler */
	public IdealTransferHandler() {
	}

	protected void importString(javax.swing.JComponent c, String str) {
		VarTable table = (VarTable)c;
		final Tab tab = table.getTab();

			//TO DO...make this work for a drag of several elements
			//since it comes from the elements tab assume the type is Elt
				//i.e. no need for IsElt
			String [] elts = str.split(ROW_SEPARATOR);
			if(elts.length > 2 || elts.length < 1){
				GiANT.gui.appendConsoleText("\nIdeals accepts only 1 or 2 Elements from a drop.", true);
				return;
			}
			String args = "";
			for(int i = 0; i < elts.length; i++){
				String elt[] = elts[i].split(COLUMN_SEPARATOR);
				String eltName = elt[NAME_COL];
				if(GiANT.gui.kash.isElement(eltName)){
					if(i == 1 && !args.equals(""))
						args += ",";
					args += eltName;
				}else{
					GiANT.gui.appendConsoleText("\nYou can only drop Elements onto the Ideals tab.", true);
					return;//don't import any other data types
				}
			}
			if(!args.equals(""))
				tab.addNewVariable(args);
	}
	
}
