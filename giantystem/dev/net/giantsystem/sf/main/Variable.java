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
 *
 * @author karve
 */

/* this class is for variables with a GUI counterpart i.e. "visible" variables */

public abstract class Variable implements KASH, Row{
    
    protected String name;
    protected String value;
    protected InfoTable table;
    protected JPanel tablePanel;
    protected TitledBorder border;
	protected Tab tab;//the tab this variable belongs to
    
    public Variable(){
        
    }
    
    public Variable(String n, String v){
        name = n;
        value = Graphic.removeBackslash(v);//long lines can have \ in them...
        
        tablePanel = new JPanel();
        border = new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(0, 0, 0, 0)), "", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("Lucida Grande", 0, 13));
        
        tablePanel.setLayout(new java.awt.BorderLayout());
        tablePanel.setBorder(border);
    }
	
	public void setTab(Tab t){
		tab = t;
	}
    
    public String getName() {
        return name;
    }
    
	public Tab getTab(){
		return tab;
	}
	
    protected void initDetail(){
        tablePanel.add(table, java.awt.BorderLayout.CENTER);
        border.setTitle(toString());
    }

    public JPanel getDetail(){
        return tablePanel;
    }
    
    public String toString(){
        return Graphic.toSubscripts(name);//this way we can add the object itself to tables...
    }

    //OPTIM, calls can get expensive (maybe...) cache value somewhere?
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
    public abstract String getPrettyName();

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
	 */
	public abstract boolean moveTo(NumberField n);
	
	/**
	 * The variable prepares it's invariants + the appropriate table
	 */
	protected abstract void calcInvariants();
    
    public static Object[] concat(Object[] a, Object[] b){
        Object[] nu = new Object[a.length + b.length];
        
        int i = 0;
        while(i < a.length)
            nu[i] = a[i++];
        
        for (int j = 0; j < b.length; j++)
            nu[i+j] = b[j];
        
        return nu;
    }
    
    public boolean memberP(String exp){
        return true;
    }
    
    //these are predicates for the operation buttons on the GUI
    /* i'll stick with the state of Inspector.operate() for now...
    public static boolean canAdd(){
        return true;
    }
    
    public static boolean canSubtract(){
        return true;
    }
    
    public static boolean canTimes(){
        return true;
    }
    
    public static boolean canDivide(){
        return true;
    }
    
    public static boolean canIntersect(){
        return true;
    }*/
    public abstract VarTable makeVarTable(Tab t);
    
    public abstract Variable makeVariable(String name, String value);

}