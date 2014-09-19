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
 * VarTab.java
 *
 * Created on May 19, 2005, 1:39 PM
 */

package net.giantsystem.sf;
import	javax.swing.TransferHandler;
import java.util.ArrayList;
import java.util.List;
/**
 * Represents the contents of a tab in an <CODE>InpsectorFrame</CODE>. Currently
 * there are three types of <CODE>Tab</CODE> used in GiANT:  Elements, Polynomials, Ideals.
 * The container hierarchy looks something like this:  <CODE>Inspector</CODE>
 * > <CODE>Tab</CODE> > <CODE>VarTable</CODE>
 * @author karve
 * @see Inspector
 * @see VarTable
 */
public class Tab extends javax.swing.JScrollPane {
    /**
     * 0-length (empty) array of type <CODE>String</CODE>
     */
    public final static String[] NO_ARGS = new String[]{};
	/**
	 * A list of all variables created through the GUI and found in a <CODE>VarTable</CODE>
	 * which itself is found in a <CODE>Tab</CODE>
	 */
	private final static List allVariables = new ArrayList();
    /**
     * My text title
     */
    private final String tabTitle;
    /**
     * My index in the <CODE>JTabbedPane</CODE> of the parent <CODE>Inspector</CODE>
     */
    private final int tabIndex;
    /**
     * A factory object used by this <CODE>Tab</CODE> to create new variables on demand 
     * from the user.
     */
    private final Variable content;
    /**
     * Used when auto-naming variables. For instance if a <CODE>Tab</CODE> contains Elements
     * the nameStem will be "elt".
     */
    private final String nameStem;
    /**
     * The <CODE>VarTable</CODE> that lives in this <CODE>Tab</CODE>
     */
    private final VarTable table;
    /**
     * The <CODE>Inspector</CODE> in which this <CODE>Tab</CODE> resides
     */
    private final Inspector inspector;
	/**
	 * Was to be used to set accelerator keys for switching tabs; did not work on OS X,
	 * @see GiANT
	 * @deprecated I opted for a different method via the <CODE>GiANT</CODE> class
	 */
	private final int mnemonic;
    /**
     * Used in auto-naming variables; simply runs over the non-negative integers as new
     * variables are created (elt0, elt1, elt2...).
     */
    private int nameIndex = 0;

    
    
    /**
     * Constructor
     * @param i Inspector in which I live
     * @param v <CODE>Variable</CODE> type for this <CODE>Tab</CODE>
     * @param title Tab title (e.g. Elements, Polynomials...)
     * @param stem Name stem used in auto-naming variables
     * @param mn Accelerator mnemonic for tab switching
     * @param index My index in the <CODE>JTabbedPane</CODE> I live in
     */
    public Tab(Inspector i, Variable v, String title, String stem, int mn, int index){
        super();
        tabTitle = title;
        nameStem = stem;
        tabIndex = index;
        content = v;
		mnemonic = mn;
        inspector = i;
        table = content.makeVarTable(this);
        setViewportView(table);
    }
	
	/**
	 * All variables defined via the GUI
	 * @return array of all variables defined via the GUI
	 */
	public static Object[] getVariables(){
		return allVariables.toArray();
	}
    
    /**
     * Called when the selection changes to update the "Current Selection" invariants
     */
    public void updateSelectionDetail(){
        //System.err.println("UPDATE");
        int [] rows = table.getSelectedRows();
        java.awt.Component[] details = new java.awt.Component[rows.length];
        for(int i = 0; i < rows.length; i++)
            details[i] = table.getVariableAt(rows[i]).getDetail();
        
        inspector.showDetails(details);
    }
    
	/**
	 * Support for drag-n-drop
	 * @param t The transfer handler to use
	 */
	public void setTransferHandler(TransferHandler t){
		table.setTransferHandler(t);
	}
	
    /**
     * Return the <CODE>VarTable</CODE> contained by and unique to this <CODE>Tab</CODE>
     * @return <CODE>VarTable</CODE> contained by and unique to this <CODE>Tab</CODE>
     */
    public VarTable getTable(){
        return table;
    }
    
    /**
     * Which <CODE>Inspector</CODE> is this Tab found in?
     * @return the <CODE>Inspector</CODE> in which this <CODE>Tab</CODE> lives
     */
    public Inspector getInspector(){
        return inspector;
    }
    
    /**
     * The tab is responsible for dispensing automatic variables names (like elt1, elt2...)
     * @return The currently available auto-name
     */
    public String getAutoName(){
        return inspector.getField().getName() + nameStem + nameIndex;
    }
    
    /**
     * This <CODE>Tab</CODE>'s title
     * @return My title (e.g. Polynomials)
     */
    public String getTitle(){
        return tabTitle;
    }
    
    /**
     * Index at which this Tab resides in the parent <CODE>JTabbedPane</CODE>
     * @return index at which this Tab resides in the parent <CODE>JTabbedPane</CODE>
     */
    public int getTabIndex(){
        return tabIndex;
    }
    
    /**
     * Test whether or not the given expression evaluates, in KASH, to a variable of the 
     * type belonging to this <CODE>Tab</CODE>
     * @param exp KASH expression
     * @return <CODE>true</CODE> iff the expression represents a varible of the same type as <CODE>content</CODE>
     * @see #content
     */
    public boolean belongs(String exp){
        return content.memberP(exp);
    }
    /*
    public boolean addNewVariable(String value){
        return addNewVariable(getAutoName(), value);
    }*/
    
    /**
     * Used for adding new variables to this <CODE>Tab</CODE>'s table
     * @param name Name for the new variable
     * @param value Value of this variable
     * @return <CODE>true</CODE> iff the addition of the requested variables was successful; 
     * <CODE>false</CODE> otherwise
     */
    public boolean addNewVariable(String name, String value){
		Variable v;
		v = content.makeVariable(name, value);

        if(v == null){
            return false;
        }else{
			v.setTab(this);
			if(!v.moveTo(this.getInspector().getField())){
				//sometimes if we move from a too-high degree into a relative
				//ground-field GiANT hangs (doesn't do so in debug mode...)
				//for instance make the number field x^2-3 with relative extension
				//x^3-11, and move p^5 in the last field back to the first
				GiANT.gui.appendConsoleText("Unable to move element.", true);
				return false;
			}
			v.calcInvariants();
            nameIndex++;
            table.addRow(v.toRow());
            table.changeSelection(table.getRowCount()-1, 0, false, false);
			allVariables.add(v);
            return true;
        }
    }
	
	/**
	 * Add a new variable with an auto-generated name
	 * @param rhs The value of the new variable
	 * @return <CODE>true</CODE> iff the new variable is successfully added
	 */
	public boolean addNewVariable(String rhs){
		return addNewVariable(getAutoName(), rhs);
	}
	//
	/**
	 * Get the accelerator key for this tab
	 * @deprecated Not used; tab switching via the keyboard is done differntly
	 * @see #Tab(Inspector, Variable, java.lang.String, java.lang.String, int, int)
	 * @return The accelerator key menmonic
	 */
	public int getMnemonic(){
		return mnemonic;
	}
	
	/**
	 * Called when the data underying the table changes and the view needs to update to
	 * reflect the same
	 */
	public void updateVariables(){
		final int rows = table.getRowCount();
		for(int row = 0; row < rows; row++){
			Variable v = table.getVariableAt(row);
			boolean update = v.update();
			if(update){
				Object[] rowInfo = v.toRow();
				for(int col = 0; col < rowInfo.length; col++)
					table.setValueAt(rowInfo[col], row, col);
			}
		}
	}
    
    /**
     * Called when the user generates new variables using the operator buttons in 
     * <CODE>Inspector</CODE>
     * @param value The expression representing the new variable
     * @return <CODE>true</CODE> iff the requested variable is successfully created and added to this
     * <CODE>Tab</CODE>'s table
     */
    public boolean operation(String value){
		return addNewVariable(getAutoName(), value);
    }    
}