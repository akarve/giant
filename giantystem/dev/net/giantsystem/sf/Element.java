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
 * Element.java
 *
 * Created on May 17, 2005, 1:12 AM
 */

package net.giantsystem.sf;
import java.util.regex.*;
import javax.swing.table.DefaultTableModel;
/**
 * Represents the elements of a number field
 * @author karve
 */
public class Element extends Variable{
    //VarTable stuff
    /**
     * Additional titles that sould be present in the Elements
     * table beyond the standard ones specified by <CODE>VarTable</CODE>
     * @see #ADD_TYPES
	 * @see VarTable
     */
    public final static String[] ADD_TITLES = {"Integral", "Unit"};
    /**
     * Additional types that sould be present in the Elements
     * table beyond the standard ones specified by <CODE>VarTable</CODE>.
     * @see #ADD_TITLES
     */
    public final static Class[] ADD_TYPES = {java.lang.Boolean.class, java.lang.Boolean.class};
    /**
     * The widths of the custom columns specified by ADD_TYPES and ADD_TITLES
     */
    public final static int[] ADD_WIDS = {20, 20};
    /**
     * The COL_* variables are combinations of the standard columns with the class-
     * specific ones defined above
     * @see VarTable
     */
    public final static String[] COL_TITLES = new String[VarTable.STD_COL_TITLES.length + ADD_TITLES.length];
    public final static Class[] COL_TYPES = new Class[VarTable.STD_COL_TYPES.length + ADD_TYPES.length];
    public final static int[] COL_WIDS = new int[VarTable.STD_COL_WIDS.length + ADD_WIDS.length];
    static{
        //copy standard info.
        for(int i = 0; i < VarTable.STD_COL_TITLES.length; i++){
           COL_TITLES[i] = VarTable.STD_COL_TITLES[i]; 
           COL_TYPES[i] = VarTable.STD_COL_TYPES[i]; 
           COL_WIDS[i] = VarTable.STD_COL_WIDS[i];
        }
        final int offset = VarTable.STD_COL_TITLES.length;
        //add class-uniqe custom columns/types
        for(int i = 0; i < ADD_TITLES.length; i++){
           COL_TITLES[offset + i] = ADD_TITLES[i]; 
           COL_TYPES[offset + i] = ADD_TYPES[i]; 
           COL_WIDS[offset + i] = ADD_WIDS[i]; 
        }
    }
    //pre:  argument to following pattern comes in trimmed (as by KASHTerm.Response)
    /**
     * Regex Pattern to match +1 or -1
     */
    public final static Pattern PLUS_MINUS_ONE = Pattern.compile("\\A-?\\s*1\\z");
    /**
     * Column index for table entry values
     */
    public final static int VAL_COL = InfoTable.VAL_COL;
    /**
     * Column index for table entry names
     */
    public final static int NAME_COL = InfoTable.NAME_COL;
    /**
     * Column index for variable names in INVARIANTS[][]
     * @see #INVARIANTS
     */
    public final static int VAR_COL = InfoTable.VAR_COL;
    
    /**
     * The *_ROW constants are integers that give the row index for the 
     * appropriate variable in
     * @see #INVARIANTS
     */
    public final static int TRACE_ROW = 1;
    public final static int NORM_ROW = 0;
    public final static int POLY_ROW = 2; 
	public final static int UNIT_ROW = 4; 
	public final static int IBASIS_ROW = 3; 
    
    /**
     * These Strings are suffixes used to generate names that are automatically
     * assigned to invariant quantities (eg f1elt0tr is the trace of the 'first'
     * element made in the field f1).
     */
    public final static String TRACE = "tr";
    public final static String NORM = "n";
    public final static String POLY = "pol"; 
	public final static String IBASIS = "irep";   
    public final static String UNIT_REP = "urep";   
    
    /**
     * An 2D array containing information on invariants. It may sometimes be the case 
     * that values in this array are not as up-to-date (or correct) as values in the
     * table display), so be careful reading information out of here.
     */
    private Object[][] INVARIANTS; 
    
    /**
     * This <CODE>Element</CODE>'s minimal polynomial
     */
    private String poly;
    /**
     * Is this element integral or not?
     */
    private boolean integral = false;
    /**
     * If this element is integral, is it a unit (invertible)?
     */
    private boolean unit = false;
    //public final static int NUM_INVARS = 3;
    /**
     * No-arg constructor; used only by the <CODE>Tab</CODE> class to provide a 
     * representative object for each <CODE>Tab</CODE> (each <CODE>Tab</CODE> contains 
     * a table whose entries are a single variable type (Elements, Polynomials,
     * Ideals)).
     * @see Tab
     */
    public Element(){
        
    }
    /**
     * Creates a new instance of Element
     * @param name The KASH name of this element
     * @param val The evaluation of this variable by KASH (i.e. what <name>; would
     * return in KASH)
     */
    public Element(String name, String val){
        super(name, val);
    }
    
    /**
     * Return a <CODE>VarTable</CODE> with the right dimensions, column names, and column types for
     * <CODE>Element</CODE>s
     * @param t The Tab in which the created table will reside in
     * @return <CODE>VarTable</CODE> with the right dimensions, column names, and column types for
     * <CODE>Element</CODE>s
     */
    public VarTable makeVarTable(Tab t){
        return new VarTable(t, COL_TITLES, COL_TYPES, COL_WIDS);
    }
	
	/**
	 * If this element is in the unit group, return a pretty string of its
	 * decomposition into torsion times fundamental units
	 */
	public void calcUnitRep(){
		final String[] unitGroupStructure = tab.getInspector().getField().getUnitGroupStructure();
		final String varName = table.getValueAt(UNIT_ROW, VAR_COL).toString();
		final String kashRep = GiANT.gui.kash.unitRep(varName, this, tab.getInspector().getField());
		String rep;
		if(kashRep.equals(KASHTerm.HUH))
			rep = kashRep;
		else{//we got a normal answer; no errors
			String[] exponents = KASHTerm.splitList(kashRep);
			StringBuffer b = new StringBuffer();
			for(int i = 0; i < exponents.length; i++){
				b.append(unitGroupStructure[i] + Graphic.toSuperscripts(exponents[i]));
				if(i < exponents.length - 1)
					b.append(Graphic._CROSS_);
			}
			rep = b.toString();
		}
		table.setValueAt(rep, UNIT_ROW, VAL_COL);
	}
    
    /**
     * Make a new <CODE>Element</CODE>
     * @param name name of the new element
     * @param val r.h.s. of the assignment statement 'name := val;' in KASH
     * @return new Element
     */
    public Variable makeVariable(String name, String val){
        if(memberP(val)){

            KASHTerm.Response r = GiANT.gui.kash.assign(name, val);
            if(!r.error())
                return new Element(name, r.getOutput());
        }else
            GiANT.gui.appendConsoleText(val + " not an Element", true);
        return null;
    }
    
    /**
     * Use KASH to apply the IsElt() function to the given argument
     * @return true or false according to IsElt(<CODE>exp</CODE>) called in KASH
     * @param exp a KASH expression
     */
    public boolean memberP(String exp){
        return GiANT.gui.kash.isElement(exp);
    }

    /**
     * See Variable.getPrettyName() for more (link below)
     * @return Typeset version of this element's name
     */
    public String getPrettyName() {
        return Graphic.toSubscripts(name);
    }
    
    /**
     * Am I an invertible element of the maximal order?
     * @return <CODE>true</CODE> iff an only if the invoking <CODE>Element</CODE> is in the ring
     * of integers of it's parent <CODE>NumberField</CODE>, <CODE>false</CODE> otherwise
     */
    public boolean inUnitGroup(){
        return integral && unit;
    }
    
    public Object[] toRow(){
        return new Object[]{this, Graphic.prettyPowerBasis(value), Boolean.valueOf(integral), Boolean.valueOf(unit)};
    }

	public boolean update() {
		if(inUnitGroup() && tab.getInspector().getField().unitGroupReady())
			calcUnitRep();
		return false;
	}

	public boolean moveTo(NumberField n) {
		value = GiANT.gui.kash.assignToFunCall(name, KASHTerm.ELT_MOVE, new String[]{name, n.getName()});
		if(value.equals(KASHTerm.HUH))
			return false;
		else
			return true;
	}

	public void calcInvariants() {
		INVARIANTS = new Object[][]{
							{"Norm", null, name + NORM}, 
							{"Trace", null, name + TRACE}, 
							{"Minimal Polynomial", null, name + POLY},
							{"Integral Basis Rep.", null, name + IBASIS}
						};
                     
        String norm = GiANT.gui.kash.norm(INVARIANTS[NORM_ROW][VAR_COL].toString(), this);
        INVARIANTS[NORM_ROW][VAL_COL] = norm;
        integral = GiANT.gui.kash.integral(this);
        //if(!integral) //for us units are only integers?
            //unit = true;//it's a field!
        if(integral){
            Matcher m = PLUS_MINUS_ONE.matcher(norm);
            if(m.matches())
                unit = true;
        }
        INVARIANTS[TRACE_ROW][VAL_COL] = GiANT.gui.kash.trace(INVARIANTS[TRACE_ROW][VAR_COL].toString(), this);
        poly = GiANT.gui.kash.minPoly(INVARIANTS[POLY_ROW][VAR_COL].toString(), this);;
        INVARIANTS[POLY_ROW][VAL_COL] = Graphic.prettyPolynomial(poly);
        String iBasis = GiANT.gui.kash.integralBasis(INVARIANTS[IBASIS_ROW][VAR_COL].toString(), this, tab.getInspector().getField());
        INVARIANTS[IBASIS_ROW][VAL_COL] = Graphic.prettyPowerBasis(iBasis, true);
		
		table = new InfoTable(INVARIANTS);
		
		if(inUnitGroup()){
			DefaultTableModel m = (DefaultTableModel)table.getModel();
			m.addRow(new Object[]{"Unit Decomposition", "(unit group not computed)", name + UNIT_REP});
		}
		if(tab.getInspector().getField().unitGroupReady())
			update();
        initDetail();
	}
}
