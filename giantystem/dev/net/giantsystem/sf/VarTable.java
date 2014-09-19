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
 * VarTable.java
 *
 * Created on May 17, 2005, 6:33 PM
 */

package net.giantsystem.sf;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

/**
 * A table for displaying <CODE>Variable</CODE>s
 * @author karve
 * @see Variable
 */
public class VarTable extends javax.swing.JTable {
    /**
     * The column titles common to all <CODE>Variable</CODE>s
     */
    public final static String[] STD_COL_TITLES = {"Name", "Power Basis Representation "};
    /**
     * The column types common to all <CODE>Variable</CODE>s
     */
    public final static Class[] STD_COL_TYPES = {java.lang.Object.class, java.lang.Object.class};
    /**
     * The column widths common to all <CODE>Variable</CODE>s
     */
    public final static int[] STD_COL_WIDS = {40, 280};
    //the first two columns are the same for all variable types; name + value
    /**
     * The index of the column in which variable names can be found
     */
    public final static int NAME_COL = 0;
    /**
     * The index of the column in which variable values can be found
     */
    public final static int VAL_COL = 1;
    /**
     * The underlying <CODE>TableModel</CODE>
     */
    private final DefaultTableModel model;
    /**
     * The <CODE>Tab</CODE> object in which this <CODE>VarTable</CODE> lives
     */
    private final Tab tab;//the tab this table lives in
	/**
	 * The <CODE>JTabbedPane</CODE> in which this <CODE>VarTable</CODE>'s <CODE>Tab</CODE>
	 * lives
	 * @see Tab
	 * @see Inspector
	 */
	private final javax.swing.JTabbedPane tabbedPane;
    
    /**
     * Creates new form BeanForm
     * @param t <CODE>Tab</CODE> in which I will live
     * @param names Names for column headings
     * @param types Types (Java Classes really) to be found in each column
     * @param wids The widths of my columns
     */
    public VarTable(final Tab t, final String[] names, final Class[] types, final int[] wids) {
        initComponents();
		//cross-platform; want to be sure unicode math chars are present
		//removesetFont(new java.awt.Font("Lucida Grande", 0, 12));
        tab = t;
		tabbedPane = t.getInspector().getTabbedPane();
        
        setDefaultRenderer(BooleanPlus.class, new BooleanPlus.CellRenderer());
        model = new DefaultTableModel(null, names){
            Class[] typesCopy = types;
            
            public Class getColumnClass(int columnIndex) {
                return typesCopy [columnIndex];
            }
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        setModel(model);
        
        for(int i = 0; i < wids.length; i++){
            javax.swing.table.TableColumn column = getColumnModel().getColumn(i);
            column.setPreferredWidth(wids[i]);
        }
        

    }
    
    /**
     * Add a row to this table
     * @param rowData Array of Objects having the proper length and type to be displayed in this table
     */
    protected void addRow(Object[] rowData){
        model.addRow(rowData);
    }
    
    /**
     * Insert a row at a specified index
     * @param i insertion index
     * @param rowData array of objects forming the row to be inerted
     * @see #addRow
     */
    protected void insertRow(int i, Object[] rowData){
        model.insertRow(i, rowData);
    }
    
    /**
     * This event is called whenever the selection changes; used to update invariant
     * information, etc., on the fly
     * @param e The selection event that issued this call
     */
    public void valueChanged(javax.swing.event.ListSelectionEvent e){
        super.valueChanged(e);
        //System.err.println("VALUE");
        if(tab != null)// && !e.getValueIsAdjusting())//tab != null is to prevent constructor/startup woes            tab.updateSelectionDetail();
            tab.updateSelectionDetail();//getvalueadjusting messes up multiple select + show feature
    }
    
    /**
     * Fetch the <CODE>Variable</CODE> at the specified row (if it exists)
     * @param r row index
     * @return The <CODE>Variable</CODE> that lives in the specified row, <CODE>null</CODE> if 
     * no such <CODE>Variable</CODE> or row
     */
    public Variable getVariableAt(int r){
        if(r < 0 || r >= getRowCount())
            return null;
        else
            return (Variable) getValueAt(r, NAME_COL);
    }
    
    /**
     * Get the KASH name of the variable at the given row index
     * @param r row index
     * @return The specified <CODE>Variable</CODE> name; <CODE>null</CODE> if no such row is
     * available
     */
    public String getNameAt(int r){
        if(r < 0 || r >= getRowCount())
            return null;
        else{
            Variable v = (Variable)getValueAt(r, NAME_COL);            return v.getName();
        }
    }
	
	/**
	 * Which <CODE>Tab</CODE> do I live in
	 * @return The <CODE>Tab</CODE> object in which the invoking object lives
	 */
	public Tab getTab(){
		return tab;
	}
    
    /**
     * Does nothing by default. Subclasses can override this method to handle user clicks
     * on a <CODE>VarTable</CODE>
     * @param evt The sponsoring <CODE>MouseEvent</CODE>
     */
    protected void handleMouseClicked(java.awt.event.MouseEvent evt) {
        return;
    }
    /*
	private void handleMouseMoved(java.awt.event.MouseEvent evt){
			//int tab = tabbedPane.indexAtLocation(evt.getX(), evt.getY());
			System.err.println(evt.getX() + "," +evt.getY());
			if(evt.getButton() != java.awt.event.MouseEvent.NOBUTTON){
				System.err.println("it's on");
				int tab = tabbedPane.indexAtLocation(evt.getX(), evt.getY());
				if(tab > 0)//no drops on elements panel allowed
					tabbedPane.setSelectedIndex(tab);
			}
	}*/
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
        setDragEnabled(true);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

    }
    // </editor-fold>//GEN-END:initComponents

	/**
	 * Dispatches the event to handleMouseClicked
	 * @param evt The click that started it all
	 * @see #handleMouseClicked
	 */
	private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
		handleMouseClicked(evt);
	}//GEN-LAST:event_formMouseClicked
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
}
