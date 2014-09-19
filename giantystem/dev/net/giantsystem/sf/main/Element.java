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
 *
 * @author karve
 */
public class Element extends Variable{
    //VarTable stuff
    public final static String[] ADD_TITLES = {"Integral", "Unit"};
    public final static Class[] ADD_TYPES = {java.lang.Boolean.class, java.lang.Boolean.class};
    public final static int[] ADD_WIDS = {20, 20};
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
    public final static Pattern PLUS_MINUS_ONE = Pattern.compile("\\A-?\\s*1\\z");
    public final static int VAL_COL = InfoTable.VAL_COL;
    public final static int NAME_COL = InfoTable.NAME_COL;
    public final static int VAR_COL = InfoTable.VAR_COL;
    
    public final static int TRACE_ROW = 1;
    public final static int NORM_ROW = 0;
    public final static int POLY_ROW = 2; 
	public final static int UNIT_ROW = 4; 
	public final static int IBASIS_ROW = 3; 
    
    public final static String TRACE = "tr";
    public final static String NORM = "n";
    public final static String POLY = "pol"; 
	public final static String IBASIS = "irep";   
    public final static String UNIT_REP = "urep";   
    
    private Object[][] INVARIANTS; 
    
    private String poly;
    private boolean integral = false;
    private boolean unit = false;
    //public final static int NUM_INVARS = 3;
    public Element(){
        
    }
    /** Creates a new instance of Element */
    public Element(String name, String val){
        super(name, val);

    }
    
    public VarTable makeVarTable(Tab t){
        return new VarTable(t, COL_TITLES, COL_TYPES, COL_WIDS);
    }
	
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
    
    public Variable makeVariable(String name, String val){
        if(memberP(val)){

            KASHTerm.Response r = GiANT.gui.kash.assign(name, val);
            if(!r.error())
                return new Element(name, r.getOutput());
        }else
            GiANT.gui.appendConsoleText(val + " not an Element", true);
        return null;
    }
    
    public boolean memberP(String exp){
        return GiANT.gui.kash.isElement(exp);
    }

    public String getPrettyName() {
        return Graphic.toSubscripts(name);
    }
    
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