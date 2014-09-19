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
 * Polynomial.java
 *
 * Created on May 17, 2005, 2:04 AM
 */

package net.giantsystem.sf;
import java.util.regex.*;
/**
 * Represents the polynomials in the univariate polynomial ring of a number field
 * @author karve
 */
public class Polynomial extends Variable{
    //VarTable stuff
    public final static String[] ADD_TITLES = {"Irred."};
    public final static Class[] ADD_TYPES = {java.lang.Boolean.class};
    public final static int[] ADD_WIDS = {20};
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
        //add class-unique custom columns/types
        for(int i = 0; i < ADD_TITLES.length; i++){
           COL_TITLES[offset + i] = ADD_TITLES[i]; 
           COL_TYPES[offset + i] = ADD_TYPES[i]; 
           COL_WIDS[offset + i] = ADD_WIDS[i]; 
        }
    }
    public final static String NAME_STEM = "pol";
    public final static int VAL_COL = InfoTable.VAL_COL;
    public final static int NAME_COL = InfoTable.NAME_COL;
    public final static int VAR_COL = InfoTable.VAR_COL;
    public final static int DISC_ROW = 0;
    public final static String DISC = "disc";
    public final static Pattern ZERO = Pattern.compile("\\s*0\\s*");
    private Object[][] INVARIANTS;
    private Boolean irreducible = Boolean.FALSE;    //make compiler happy
    
    public Polynomial(){
        
    }
    /** Creates a new instance of Element */
    public Polynomial(String name, String value){
        super(name, value);
    }
    
    public VarTable makeVarTable(Tab t){
        return new VarTable(t, COL_TITLES, COL_TYPES, COL_WIDS);
    }
    
    public Variable makeVariable(String name, String val){
        KASHTerm.Response r = GiANT.gui.kash.assign(name, val);
        //see if the given val is valid
        if(!r.error()){
            if(memberP(val) && ! r.getOutput().equals("0")){//otherwise PolyDisc will hang!
                return new Polynomial(name, r.getOutput());
            }else
                GiANT.gui.appendConsoleText(val + " = " + r.getOutput() + " not a Polynomial", true);
        }
        return null;
    }
    
    public boolean memberP(String exp){
        return GiANT.gui.kash.isPoly(exp);
    }
    
    public Object[] toRow(){
        return new Object[]{this, getPrettyName(), irreducible};
    }

    public String getPrettyName() {
        return Graphic.prettyPolynomial(value);
    }

	public boolean moveTo(NumberField n) {
		value = GiANT.gui.kash.assignToFunCall(name, KASHTerm.POLY_MOVE, new String[]{name, n.getName()});
		if(value.equals(KASHTerm.HUH))
			return false;
		else
			return true;
	}

	protected void calcInvariants() {
		String[] args = {name};
        INVARIANTS = new Object[][]{{"Discriminant", null, name + DISC}};
        String disc = GiANT.gui.kash.disc(INVARIANTS[DISC_ROW][VAR_COL].toString(), this);
        INVARIANTS[DISC_ROW][VAL_COL] = Graphic.prettyPowerBasis(disc);
		irreducible = Boolean.valueOf(GiANT.gui.kash.irreducible(this));
        table = new InfoTable(INVARIANTS);
        initDetail();
	}
}
