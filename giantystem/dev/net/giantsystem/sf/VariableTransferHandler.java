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
 * VariableTransferHandler.java
 *
 * Created on June 4, 2005, 1:09 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package net.giantsystem.sf;

/**
 *
 * @author karve
 */
/* some code based on ListTransferHandler.java from sun's website
 * (see ExtendedDnDDemo.java example)
 */
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import javax.swing.table.*;

/**
 * Provides a base class and commen <CODE>exportString()</CODE> method for 
 * its subclasses
 * @see PolynomialTransferHandler
 * @see VariableTransferHandler
 * @see ElementTransferHandler
 */
public abstract class VariableTransferHandler extends StringTransferHandler {
    /**
     * indices of rows selected in the exporting <CODE>VarTable</CODE>
     * @see VarTable
     */
    private int[] indices = null;
	/**
	 * character used to separate rows
	 */
	public static final String ROW_SEPARATOR = "\n";
	/**
	 * character used to separate columns
	 */
	public static final String COLUMN_SEPARATOR = "\t";
	/**
	 * column index in which variable names are found
	 */
	public static final int NAME_COL = VarTable.NAME_COL;
            
    //Bundle up the selected items in the list
    //as a single string, for export.
    /**
     * Bundle up the selected items in the table as a single string, for export.
     */
    protected String exportString(JComponent c) {
        JTable table = (JTable)c;
		//TableModel model = table.getModel();
        indices = table.getSelectedRows();
		final int rows = indices.length;
		final int cols = table.getColumnCount();
        
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++){
				Object val = table.getValueAt(indices[i], j);
				String out;
				if(val == null)
					out = "";
				else if(j == NAME_COL){
					Variable v = (Variable) val;
					out = v.getName();
				}else
					out = val.toString();
				buff.append(out);
				if (j < cols - 1) 
					buff.append(COLUMN_SEPARATOR);
			}
			if (i < rows - 1)
				buff.append(ROW_SEPARATOR);
        }
        return buff.toString();
    }

    //Take the incoming string and wherever there is a
    //newline, break it into a separate item in the list.
    /**
     * Each subclass overrides this method to provide custom import behavior
     */
    protected abstract void importString(JComponent c, String str);
		//check if name string is of appropriate type
		//make new variable(s) based on this string
		//don't allow user to drop data onto the same table...
}