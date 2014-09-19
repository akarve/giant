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
 * Variable.java
 *
 * Created on May 17, 2005, 1:10 AM
 */

package net.giantsystem.sf;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import net.giantsystem.sf.KASHTerm.Response;
/**
 * The base type for <CODE>Element</CODE>s, <CODE>Polynomial</CODE>s, and <CODE>Ideal</CODE>s. (TO DO:  make NumberField 
 * a subclass; this would allow for nice, general behavior as we have started to aim for 
 * in the GlobalInspector class).
 * @author karve
 * @see Element
 * @see Variable
 * @see Polynomial
 * @see GlobalInspector
 */

/* this class is for variables with a GUI counterpart i.e. "visible" variables */

public abstract class Variable implements KASH, Row{
    
    /**
     * The KASH handle (shell name) for this <CODE>Variable</CODE>
     */
    protected String name;
    /**
     * The evaluation string returned by KASH when creating/evaluating this variable
     * (or it's name).
     */
    protected String value;
    /**
     * Table for this <CODE>Variable</CODE>'s invariants
     */
    protected InfoTable table;
    /**
     * The invariants table lives inside of a <CODE>JPanel</CODE>
     * @see #table
     */
    protected JPanel tablePanel;
    /**
     * The border for <CODE>tablePanel</CODE>; allows us to write this <CODE>Variable</CODE>'s
     * name above its invariants table
     */
    protected TitledBorder border;
	/**
	 * The <CODE>Tab</CODE> in which this <CODE>Variable</CODE> lives.
	 */
	protected Tab tab;//the tab this variable belongs to
    
    /**
     * No-arg constructor
     */
    public Variable(){
        
    }
    
    /**
     * Create a new Variable according to the arguments given
     * @param n name for the new variable @see name
     * @param v value for the new <CODE>Variable</CODE> @see value
     */
    public Variable(String n, String v){
        name = n;
        value = Graphic.removeBackslash(v);//long lines can have \ in them...
        
        tablePanel = new JPanel();
        border = new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(0, 0, 0, 0)), "", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("Lucida Grande", 0, 13));
        
        tablePanel.setLayout(new java.awt.BorderLayout());
        tablePanel.setBorder(border);
    }
	
	/**
	 * Set the parent tab to <CODE>t</CODE>
	 * @param t the parent Tab
	 */
	public void setTab(Tab t){
		tab = t;
	}
    
    /**
     * Name in the associated KASH shell session
     * @return This variable's name in the associated KASH shell session 
     */
    public String getName() {
        return name;
    }
    
	/**
	 * <CODE>Variable</CODE>s live in <CODE>VarTable</CODE>s live in <CODE>Tab</CODE>s
	 * (live in <CODE>Inspector</CODE>s)
	 * @return the <CODE>Tab</CODE> in which this <CODE>Variable</CODE> is displayed in the GUI
	 */
	public Tab getTab(){
		return tab;
	}
	
    /**
     * Each <CODE>Variable</CODE> has a "detail" associated with it (basically a table 
     * of its invariants). Here we prep said table.
     */
    protected void initDetail(){
        tablePanel.add(table, java.awt.BorderLayout.CENTER);
        border.setTitle(toString());
    }

    /**
     * Get table of invariants. To avoid convusion it is worth noting that, at present,
     * this table lives inside of a <CODE>JPanel</CODE>
     * @see #initDetail
     * @return table of invariants
     */
    public JPanel getDetail(){
        return tablePanel;
    }
    
    /**
     * The <CODE>Object.toString()</CODE> is called by <CODE>JTable</CODE> to determine
     * how <CODE>Object</CODE>s it contains will display. So here we return a typeset version
     * of the KASH name for this <CODE>Variable</CODE>
     * @return Typeset name (typesetting consists of making all numbers subscripts).
     */
    public String toString(){
        return Graphic.toSubscripts(name);//this way we can add the object itself to tables...
    }

    //OPTIM, calls can get expensive (maybe...) cache value somewhere?
    /**
     * Return <CODE>value</CODE>
     * @see #value
     * @return value
     */
    public String getValue(){
        return value;
        /*in case things get fancier later
        KASHTerm.Response r = GiANT.gui.kash.eval(name);
        if(r.error())
            return KASHTerm.HUH;
        else
            return r.getOutput();
         */
    }
    
    //a string with unicode typesetting
    /**
     * The prettyName() <CODE>String</CODE> differs from the name of a variable in that 
     * it may use non-word characters, such as subscript numbers, that are not available
     * for use in the KASH shell.
     * @return The <CODE>name</CODE> of this variable with optional type-setting
     * @see #name
     */
    public abstract String getPrettyName();

    /**
     * How this <CODE>Variable</CODE> should be displayed in table
     * @return An array of <CODE>Object</CODE>s whose <CODE>toString()</CODE> method will determine
     * how this <CODE>Variable</CODE> looks in a table
     */
    public abstract Object[] toRow();
	
	/**
	 * Used for optional computations + their results (such as the unit
	 * group)
	 * @see <CODE>toRow()</CODE>
	 * @return the column index in the calling Variable's
	 * toRow() representation that needs to be updated
	 */
	public boolean update(){
		return false;
	}

	/**
	 * used to move new variable to the specified field to make sure all 
	 * elements are in the same basis representation (this is useful
	 * for operations); all overriding methods probably rely on a 
	 * KASH function like EltMove, PolyMove, IdealMove...
	 * @param n the NumberField to move this Variable into
	 * @return <CODE>true</CODE> if the move was successful, <CODE>false</CODE> otherwise
	 */
	public abstract boolean moveTo(NumberField n);
	
	/**
	 * The variable prepares it's invariants + the appropriate table
	 */
	protected abstract void calcInvariants();
    
    /**
     * Concrete subclasses will implement this method, a membership test. For example the
     * <CODE>Element</CODE> class's version of this method can be used to test if a given
     * <CODE>Variable</CODE> is an <CODE>Element</CODE> or not.
     * @param exp A KASH expression
     * @return <CODE>true</CODE> iff <CODE>exp</CODE> is an instance of this class; <CODE>false</CODE>
     * otherwise.
     */
    public boolean memberP(String exp){
        return true;
    }
    
    /**
     * Each <CODE>Tab</CODE> contains exactly one type of <CODE>Variable</CODE>.
     * Each <CODE>Variable</CODE> type has its own table view. This method returns said
     * table.
     * @param t The <CODE>Tab</CODE> in which the returned table will reside
     * @return Table fit for <CODE>Variable</CODE>s of this type
     * @see Tab
     */
    public abstract VarTable makeVarTable(Tab t);
    
    /**
     * Create a new <CODE>Variable</CODE>
     * @param name The name of the new <CODE>Variable</CODE>
     * @param value The value to which the name of the new <CODE>Variable</CODE> should be assigned
     * @return The Java object representing the <CODE>Variable</CODE> created according to 
     * <CODE>name</CODE> and <CODE>value</CODE>
     */
    public abstract Variable makeVariable(String name, String value);

}