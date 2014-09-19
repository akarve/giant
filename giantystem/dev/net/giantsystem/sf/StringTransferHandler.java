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
 * StringTransferHandler.java
 *
 * Created on June 4, 2005, 1:08 PM
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
/* based on code from the java website:
 * StringTransferHandler.java is used by the 1.4
 * ExtendedDnDDemo.java example.
 */
import java.awt.datatransfer.*;
import javax.swing.*;
import java.io.IOException;

/**
 * The core class for drag-and-drop, which currently avoids flavors, and does everything
 * via strings
 */
public abstract class StringTransferHandler extends TransferHandler {
    
    /**
     * Package data into a <CODE>String</CODE> for export
     * @param c The source component (I think)
     * @return Data to be exported
     */
    protected abstract String exportString(JComponent c);
    /**
     * Bring a <CODE>String</CODE> in
     * @param c The receiving component
     * @param str The incoming <CODE>String</CODE>
     */
    protected abstract void importString(JComponent c, String str);
    //protected abstract void cleanup(JComponent c, boolean remove);
    //we are doing COPY only, so no need for cleanup
    /**
     * Create transferable data
     * @param c The source component
     * @return The data to be moved
     */
    protected Transferable createTransferable(JComponent c) {
        return new StringSelection(exportString(c));
    }
    
    /**
     * What kind of action is taking place (e.g. COPY, MOVE...)?
     * @param c Source component?
     * @return Relevant actions
     */
    public int getSourceActions(JComponent c) {
        return COPY;//changed from COPY_OR_MOVE
    }
    
    /**
     * Bring transferable data in
     * @param c Importing component
     * @param t Transferable data
     * @return Whether or not the transfer was successful
     */
    public boolean importData(JComponent c, Transferable t) {
        if (canImport(c, t.getTransferDataFlavors())) {
			GiANT.gui.appendConsoleText("\nDrop received. Computing result.",false);
            try {
                String str = (String)t.getTransferData(DataFlavor.stringFlavor);
                importString(c, str);
                return true;
            } catch (UnsupportedFlavorException ufe) {
				System.err.println(ufe);
            } catch (IOException ioe) {
				System.err.println(ioe);
            }
        }

        return false;
    }
    
    /**
     * Can this component import the following types of data?
     * @param c Component to query
     * @param flavors DataFlavors taken by <CODE>c</CODE>
     * @return <CODE>true</CODE> iff <CODE>c</CODE> can import one or more of <CODE>flavors</CODE>
     */
    public boolean canImport(JComponent c, DataFlavor[] flavors) {
        for (int i = 0; i < flavors.length; i++) {
            if (DataFlavor.stringFlavor.equals(flavors[i])) {
                return true;
            }
        }
        return false;
    }
}