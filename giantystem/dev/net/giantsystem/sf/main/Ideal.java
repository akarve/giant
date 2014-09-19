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
 * Ideal.java
 *
 * Created on May 19, 2005, 10:18 AM
 */

package net.giantsystem.sf;
import java.util.regex.*;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author karve
 */
public class Ideal extends Variable {
        //VarTable stuff
    public final static String[] ADD_TITLES = {"Principal", "Prime", "Integral"};
    public final static Class[] ADD_TYPES = {BooleanPlus.class, java.lang.Boolean.class, java.lang.Boolean.class};
    public final static int[] ADD_WIDS = {50, 40, 50};
    public final static String[] COL_TITLES = new String[VarTable.STD_COL_TITLES.length + ADD_TITLES.length];
    public final static Class[] COL_TYPES = new Class[VarTable.STD_COL_TYPES.length + ADD_TYPES.length];
    public final static int[] COL_WIDS = new int[VarTable.STD_COL_WIDS.length + ADD_WIDS.length];
    public final static int CLASS_GROUP_LIMIT = 10;
    static{//we are just concatting arrays here...
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
    
    public final static int NORM_ROW = 0;
    public final static int PRINCIPAL_ROW = 1;
	public final static int PRIME_ROW = 3;
	public final static int REP_ROW = 2;
	public final static int PRINCIPAL_COL = 2;
    public final static String NORM = "norm";
    public final static String GEN = "gen";
	public final static String FACT = "fact";
	public final static String REP = "rep";
    public final static int VAL_COL = InfoTable.VAL_COL;
    public final static int NAME_COL = InfoTable.NAME_COL;
    public final static int VAR_COL = InfoTable.VAR_COL;
	//variable for ideal class cyclic factors
    public final static String C = String.valueOf(Graphic.BLACK_H);
    public final static Pattern KASH_ANGLE_ENCLOSED = Pattern.compile("(?s)<(.+)>");//?s matches even newline with .
    public final static Pattern PRIME_FACTOR = Pattern.compile("(?s)<");
	//this pattern accounts for space, unlike Graphic.MULTIPLE_TERMS
	public final static Pattern MULTIPLE_TERMS = Pattern.compile("[^\\s]+\\s*[+-][^\\s]*");
	//public final static Pattern TWO_ELEMENTS = Pattern.compile("(?s)([\d\\[])");
    private Object[][] INVARIANTS;
    private Boolean prime;
	private boolean factored = false;//have we called IdealFactor(this)?
    private BooleanPlus principal = BooleanPlus.UNKNOWN;
    private Boolean integral;
	private String tempGen;
    private String generators;//we always want access to a 2-element rep. of the ideal
    private String powerGenerators;//same as above but in power basis rep.

		//the principal generator here
    public Ideal(){
        
    }
    
    /** Creates a new instance of Ideal */
    public Ideal(String name, String value) {
        super(name, value);
    }

    public static String getConstructor(String args){
        return KASHTerm.funCall("Ideal", args.split(","));
    }
    
    protected void setPrincipal(BooleanPlus principal){
        this.principal = principal;
	}

    public Object[] toRow() {
		//true signals that we are in the maximal order
        String gens = Graphic.listToPrettyPowerBasis(powerGenerators);
        return new Object[] {this,Graphic.angleEnclose(gens), principal, prime, integral};
    }
        
    public VarTable makeVarTable(Tab t){
        return new VarTable(t, COL_TITLES, COL_TYPES, COL_WIDS){
			protected void handleMouseClicked(java.awt.event.MouseEvent evt){
				if(evt.getClickCount() == 2){//double-click
					//which row got the double-click?
					int row = rowAtPoint(new java.awt.Point(evt.getX(), evt.getY()));
					if(row >= 0){//make sure the click is on an existing row
						Ideal i = (Ideal)getVariableAt(row);
						if(i.getPrincipal() == BooleanPlus.UNKNOWN){//iff princpality unknown
							//WARN--this probably is not thread safe;
							//another thread could intervene + use "temp"
							String result = GiANT.gui.kash.principal("temp", i);
							Matcher m = KASHTerm.FALSE.matcher(result);
							if(m.find()){
								i.setPrincipal(BooleanPlus.FALSE);
								setValueAt(BooleanPlus.FALSE, row, PRINCIPAL_COL);
							}else if(result.equals(KASHTerm.HUH)){
								GiANT.gui.appendConsoleText("error determining principality of " + i.getName(), true);
								return; //nothing to do, we still don't know						
							}else{
								i.updateGenerator("temp");
								i.setPrincipal(BooleanPlus.TRUE);
								setValueAt(BooleanPlus.TRUE, row, PRINCIPAL_COL);
								String pgs = i.getPowerGenerators();
								pgs = Graphic.listToPrettyPowerBasis(pgs);
								pgs = Graphic.angleEnclose(pgs);
								setValueAt(pgs, row, InfoTable.VAL_COL);
							}
						}
					}
				}
			}
		
		};
    }
	
	public String getPowerGenerators(){
		return powerGenerators;
	}
	
	private void updateGenerator(String tmp){
		String keep = INVARIANTS[PRINCIPAL_ROW][VAR_COL].toString();
		if(tmp != null){
			generators = GiANT.gui.kash.assignToFunCall(keep, null, new String[]{tmp});
		}
		String[] gens = KASHTerm.splitList(generators);
		if(gens.length < 1 || gens.length > 2)
			throw new AssertionError("expected 1 or 2 elements in the generator list for " + name + " got: " + generators);
		String dest = tab.getInspector().getField().getName();
		powerGenerators = "";
		for(int i = 0; i < gens.length; i++){
				//i+1 since KASH lists are 1-indexed
			String get = keep + Graphic.bracketEnclose(String.valueOf(i+1));
			powerGenerators += GiANT.gui.kash.assignToFunCall("temp", KASHTerm.ELT_MOVE, new String[]{get, dest});
			if(i == 0 && gens.length == 2)
				powerGenerators += ", ";
		}
		powerGenerators = Graphic.bracketEnclose(powerGenerators);//always list form
        INVARIANTS[PRINCIPAL_ROW][VAL_COL] = Graphic.listToPrettyPowerBasis(generators, true);
		if(table != null)
			table.setValueAt(INVARIANTS[PRINCIPAL_ROW][VAL_COL], PRINCIPAL_ROW, VAL_COL);
	}
	
    public Variable makeVariable(String name, String val){
		int comma = val.indexOf(",");
		KASHTerm.Response r = null;
		Ideal i;
		boolean construct = false, principal = false;
		if(comma == -1){// a single expression/arg
			r = GiANT.gui.kash.assign(name, val);
			if(r.error())
				return null;
			else{
				if(!memberP(val)){//name := Ideal(val);
					construct = true;//construc means call Ideal() the ideal constr.
					principal = true;//one arg = principal!
				}//else name := val; (already done above)
				//OPTIM if assigning to another ideal, check to see if that ideal's
				// principality, etc. is known + copy it's info.
			}
		}else//there was a comma; should be two-element argument for Ideal(,)
			construct = true;
		
		if(construct)
			r = GiANT.gui.kash.assign(name, KASHTerm.funCall(KASHTerm.IDEAL_CONSTRUCTOR, new String[]{val}));
		if(r.error())
			return null;
		else{
			i = new Ideal(name, r.getOutput());
			if(principal){
				i.setPrincipal(BooleanPlus.TRUE);
				i.setTempGen(val);
			}
		}
		return i;
	}
			
	public void setTempGen(String x){
		tempGen = x;
	}
    
    public boolean memberP(String exp){
        return GiANT.gui.kash.isIdeal(exp);
    }

    public String getPrettyName() {
        return Graphic.toSubscripts(name);
    }

	public boolean update() {
		boolean change = false;
		if(principal == BooleanPlus.TRUE || principal == BooleanPlus.FALSE){
			//nothing to do
		}else if(principal == BooleanPlus.UNKNOWN){
			if(tab.getInspector().getField().classGroupReady()){
				String temp = GiANT.gui.kash.principalFromClassGroup("temp", this);
				Matcher m = KASHTerm.FALSE.matcher(temp);
				if(m.find()){
					principal = BooleanPlus.FALSE;//do nothing; no principal generator returned
					change = true;
				}else{
					principal = BooleanPlus.TRUE;
					updateGenerator("temp");
					change = true;
				}
			}
		}else
			throw new AssertionError("unexpected value for principal " +  principal +" ideal " + name);

		//now set ideal class rep
		String repName = INVARIANTS[REP_ROW][VAR_COL].toString();
		String rep = GiANT.gui.kash.classRep(repName, this);
		//rep is of the form [ <elt>, [ <ideal-2elt>, <int> ], ...]
		rep = Graphic.removeSpace(rep);//pack it to make parsing easier
		char start = rep.charAt(1);//step over opening [
		int end = 0;//end of <elt> (first item in list)
		boolean trivial = false; //class number 1
		if(start == '['){
			end = rep.indexOf("]") + 2;//step over comma
		}else if (Character.isDigit(start)){
			end = rep.indexOf(",") + 1;
		}else
			throw new AssertionError("Expected classRep list to start with rational or list, but:  " + rep);
		if(end == 0 || end >= rep.length()){
			trivial = true;//class number 1, nothing in the list...
			end = rep.length();
		}
		
		//get rid of coeff. so we can call prettyIdealFactors
		String coeff = rep.substring(1, end-1);//-1 so as not grab comma
		String showTime = KASHTerm.enclose(Graphic.prettyPowerBasis(coeff, true));
	
		if(trivial)
			rep = null;
		else
			rep = "["+rep.substring(end, rep.length());
		String repDecomp = classRep(rep);//prettyIdealFactors(rep, true);
		showTime = repDecomp + Graphic.DOT + showTime;
		
		table.setValueAt(showTime, REP_ROW, VAL_COL);
		table.repaint();
		
		return change;
	}
	
	public String classRep(String rep){
		String[] struct = tab.getInspector().getField().getClassGroupStructure();
		if(rep == null){//class number 1
			if(struct.length != 1)
				throw new AssertionError("Expected class number 1 since argument rep=null");
			return struct[0];
		}

		int i = 0;
		Matcher m = IDEAL_FACTOR.matcher(rep);
		StringBuffer result = new StringBuffer();
		while(m.find()){
			result.append(struct[i++] + Graphic.toSuperscripts(m.group(2)));
			if(i < struct.length)
				result.append(Graphic._CROSS_);
		}
		return result.toString();
	}
	
	public BooleanPlus getPrincipal(){
		return principal;
	}
	
	public String getGeneratorsName(){
		return INVARIANTS[PRINCIPAL_ROW][VAR_COL].toString();
	}

	public boolean moveTo(NumberField n) {
		//notice that, for our purposes, all ideals live in the maximal order
		value = GiANT.gui.kash.assignToFunCall(name, KASHTerm.IDEAL_MOVE, new String[]{name, n.getMaxOrderName()});
		if(value.equals(KASHTerm.HUH))
			return false;
		else
			return true;
	}

	protected void calcInvariants() {
		String[] args = {name};
        INVARIANTS = new Object[][]{
                            {"Norm", null, name + NORM},
                            {"Generator(s)", null, name + GEN},
							{"Ideal Class Rep.", "(class group not computed)", name + REP},
							{"Factorization", InfoTable.DOUBLE_CLICK_CALC, name + FACT}
		};

        INVARIANTS[NORM_ROW][VAL_COL] = GiANT.gui.kash.norm(INVARIANTS[NORM_ROW][VAR_COL].toString(), this);

        integral = Boolean.valueOf(GiANT.gui.kash.integral(this));
        prime = Boolean.valueOf(GiANT.gui.kash.prime(this));
		
		String genHandle = INVARIANTS[PRINCIPAL_ROW][VAR_COL].toString();
		if(principal == BooleanPlus.TRUE){
			//in this case principality set at construct time
			String max = tab.getInspector().getField().getMaxOrderName();
			//move to maxOrder
			GiANT.gui.kash.assignToFunCall(genHandle, KASHTerm.ELT_MOVE, new String[]{tempGen, max});
			//assign it to a list!
			generators = GiANT.gui.kash.assignToFunCall(genHandle, null, new String[]{"["+genHandle+"]"});
			
		}else if(principal == BooleanPlus.UNKNOWN){
			generators = GiANT.gui.kash.generators(genHandle,this);
			String[] count = KASHTerm.splitList(generators);
			if(count.length < 1 || count.length > 2)
				throw new AssertionError("expected 1 or 2 elements in the generator list for " + name + " got: " + generators);
			if(count.length == 1)
				principal = BooleanPlus.TRUE;
		}else
			throw new AssertionError("calcInvariants() expects principal=TRUE or =UNKNOWN, not " + principal);
		updateGenerator(null);
		
		final Ideal me = this;
		table = new InfoTable(INVARIANTS){
            private final Ideal subject = me;
            protected void doubleClickHandler(java.awt.event.MouseEvent evt){
                if(!factored && getSelectedRow() == PRIME_ROW){
                    String rawFactors = GiANT.gui.kash.factor(getValueAt(PRIME_ROW, VAR_COL).toString(),me);
					factored = true;
                    setValueAt(prettyIdealFactors(rawFactors, true), PRIME_ROW, VAL_COL);
                }
            }
        };
		if(tab.getInspector().getField().classGroupReady())
			update();
		
        initDetail();
	}
	
	public final static Pattern IDEAL_FACTOR = Pattern.compile("\\[<([^>]+)>,(-?\\d+)\\]");
	/**
	 * Take the raw KASH output (an ideal factorization)
	 * and typeset it
	 * @param rawFactors list of the form [[<2-elt ideal>,exp]...]
	 * @return a typeset prime ideal factorization
	 */
	public static String prettyIdealFactors(String rawFactors, boolean powers){
		String f = Graphic.removeSpace(rawFactors);
		if(f.length() <= 2)
			return "";//sometimes KASH returns an empty list...
		StringBuffer factorization = new StringBuffer();
		Matcher m = IDEAL_FACTOR.matcher(f);
		while(m.find()){
				//enclose makes it a list of elts, so we can call listToPrettyPowerBasis
			String ideal2gens = Graphic.bracketEnclose(m.group(1));
			factorization.append(Graphic.angleEnclose(Graphic.listToPrettyPowerBasis(ideal2gens, true)));
			if(powers){
				String exponent = m.group(2);
				factorization.append(Graphic.toSuperscripts(exponent));
			}
			factorization.append(Graphic._CROSS);
		}
		int len = factorization.length();
		//pull out the last CROSS so we don't have '2 X 3 X', but '2 X 3'
		factorization.delete(len - 2, len);
		return factorization.toString();
	}
}