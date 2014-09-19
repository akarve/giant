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
 * NumberFieldPanel.java
 *
 * Created on April 22, 2005, 3:02 PM
 */

package net.giantsystem.sf;

import java.awt.Point;
import java.awt.Dimension;
import java.awt.Component;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.*;
import javax.swing.JComponent;
/**
 * Provides a java implementation of the "number field" concept.
 * TO DO:  maybe this should inherit from Variable (or make Variable an interface)
 * that would allow for smart playlists / inspectors for numberfields/whole GUI!
 * there is no need fo numberfield to extend JPanel...it just contains one
 * just rip out the non-bean stuff + move to another class, this will be called numberfield view or something
 * ...that can contain a point to the numberfield info object which is a VARIABLE
 * @author karve
 */

public class NumberField extends javax.swing.JPanel implements KASH {
    /**
     * character used to abbreviate "Field"
     */
    public final static char FIELD_CHAR = 'f';
    
    /**
     * running count of how many number fields we've tried to create; used for indexing
     */
    private static int counter = 0;  //this keeps track of numberfields, hands out indexes
    /**
     * column index for invariant values
     */
    public final static int VAL_COL = InfoTable.VAL_COL; //the value column for the info table
    /**
     * column index for invariant names
     */
    public final static int NAME_COL = InfoTable.NAME_COL;
    /**
     * column index for invariant variable names
     */
    public final static int VAR_COL = InfoTable.VAR_COL;    //auto variable names go here
    //for the INVARIANTS[][] tables
    /**
     * row index for degree invariant
     */
    public final static int DEG_ROW = 0;
    /**
     * degree abbrev.
     */
    public final static String DEGREE = "deg";
    /**
     * row index for (power) basis
     */
    public final static int BAS_ROW = 6;
    /**
     * basis abbreviation
     */
    public final static String BASIS = "basis";
    /**
     * row index for discriminant
     */
    public final static int DIS_ROW = 1;
    /**
     * discriminant abbreviation
     */
    public final static String DISC = "disc";
    /**
     * row index for signature
     */
    public final static int SIG_ROW = 2;
    /**
     * signature abbreviation
     */
    public final static String SIG = "sig";
    /**
     * row index for galois group
     */
    public final static int GAL_ROW = 3;
    /**
     * "galois group" abbreviation
     */
    public final static String GALOIS = "gal";
    /**
     * row index for primitve element
     */
    public final static int PRIM_ROW = 4;
    /**
     * primitive element abbreviation
     */
    public final static String PRIM_ELT = "p";
    /**
     * row index for polynomial ring (F[X]) variable
     */
    public final static int VAR_ROW = 5;
    /**
     * polynomial ring variable abbreviation
     */
    public final static String VAR = "x";
    /**
     * row index for integral basis
     */
    public final static int IBAS_ROW = 7;
    /**
     * power basis character
     */
    public final static char B = Graphic.SMALL_ALPHA;//used as power basis character = primitive elt.
	/**
	 * integral basis character
	 */
	public final static char IB = Graphic.SMALL_XI;
	/**
	 * used as abbreviation for the integral basis list
	 * @see VariableDialog
	 */
    public final static char I = 'i';
	/**
	 * used as abbreviation for the fundamental unit list
	 * @see VariableDialog
	 */
    public final static char U = 'u';
    /**
	 * used as abbreviation for the class rep list
	 * @see VariableDialog
	 */
	public final static char C = 'c';
	/**
	 * string identifying unit group node
	 */
	public final static String UNIT_GROUP = "Unit Group";
    /**
     * torsion element abbreviation
     */
    public final static String TORSION = "tor";
    /**
     * row index for the torsion element
     */
    public final static int TOR_ROW = 2;
    /**
     * (greek) character to represent torsion element
     */
    public final static String TOR_CHAR = String.valueOf(Graphic.SMALL_EPSILON);
    /**
     * (greek) character for fundamental units
     */
    public final static String FUNIT_CHAR = String.valueOf(Graphic.SMALL_ETA);
    /**
     * row index for regulator
     */
    public final static int REG_ROW = 5;
    /**
     * regulator abbreviation
     */
    public final static String REG = "reg";
    /**
     * rank abbreviation
     */
    public final static String RANK = "rank";
    /**
     * row index for unit group rank
     */
    public final static int RANK_ROW = 0;
    /**
     * row index for unit group structure
     */
    public final static int STRUCT_ROW = 1;
    /**
     * torsion rank abbreviation
     */
    public final static String TOR_RANK = "trank";
    /**
     * row index for torsion rank
     */
    public final static int TOR_RANK_ROW = 3; 
    /**
     * fundamental unit abbreviation/prefix
     */
    public final static String F_UNITS = "funit";
    /**
     * row index for fundamental units (in table of invariants)
     */
    public final static int F_UNITS_ROW = 4;
    /**
     * string denoting class group
     */
    public final static String CLASS_GROUP = "Class Group";
    /**
     * short prefix for maximal order variable names
     */
    public final static String MAX_ORDER = "max";
	/**
	 * row index for maximal order in invariants table
	 */
	public final static int MAX_ORDER_ROW = 8;
	/**
	 * row index for maximal order discriminant (in field invariant table)
	 */
	public final static int MAX_ORDER_DISC_ROW = MAX_ORDER_ROW + 1;
    /**
     * short prefix for integral basis names
     */
    public final static String INT_BASIS = "i" + BASIS;
	/**
	 * string typesetting the integral basis (greek) character to the power 'n'
	 */
	public final static String IBASIS = KASHTerm.enclose(String.valueOf(Graphic.SMALL_XI) + Graphic.SUPER_N);
    /**
     * class group factors abbreviation for variable names of the same
     */
    public final static String CYCLIC_FACTORS = "clf";
	/**
	 * class group abbreviation
	 */
	public final static String CLASS_G = "cl";
	/**
	 * row index for class group in table
	 */
	public final static int CGROUP_ROW = 1;
	/**
	 * row index for class number in table
	 */
	public final static int CNUMBER_ROW = 0;
	/**
	 * row index for class factors in table
	 */
	public final static int CFACTORS_ROW = 2;
    /**
     * data underlying invariants table for this number field
     */
    private Object[][] INVARIANTS;
    /**
     * this n.f.'s degree
     */
    private int degree;
    /**
     * unique integer id & subscript for this n.f. (e.g. 0, 1 as in F_0, F_1...)
     */
    protected final int index;  //unique ID
	/**
	 * class number
	 */
	private int classNumber = -1;//initialize to impossible value
    /**
     * short name for this number field
     */
    private String name;
    /**
     * gen. poly.
     */
    private String poly;
    /**
     * typeset generating polynomial
     * @see #poly
     * @see Graphic.prettyPolynomial(String)
     */
    private String pretty;
    /**
     * maximal order
     */
    private String maxOrder;
    /**
     * invariants table
     */
    private JTable table;
    /**
     * unit group table
     */
    private JPanel unitTable = new JPanel(new java.awt.BorderLayout());
    /**
     * class group table
     */
    private JPanel classTable = new JPanel(new java.awt.BorderLayout());
    /**
     * <CODE>true</CODE> iff the unit group is calculated
     */
    private boolean unitGroupReady = false;
    /**
     * <CODE>true</CODE> iff the class group is calculated
     */
    private boolean classGroupReady = false;
    /**
     * array with characters representing structure of unit group
     */
    private String[] unitGroupStructure;
	/**
	 * array with characters representing structure of unit group
	 */
	private String[] classGroupStructure;
    //all PROPER subfields (remember we do not allow the same field twice in a city 
    //  (see City class, addField)
    /**
     * set of subfields of <CODE>this</CODE> that are on the GiANT desktop
     */
    private Set subfields = new HashSet();                        
    /**
     * inspector window for this n.f.
     * @see Inspector
     */
    private final Inspector inspector;
    
    /**
     * Creates new form NumberField
     * @param poly generating poly.
     */
    public NumberField(String poly) {
        //strip out all spaces to ensure consistency accross numberfields
        // becuase for hashCode + equals we depend on the the value of poly
        this.poly = poly;    //OPTIM compile pattern?
        //DEBUG
        //System.err.println("poly=" + poly);
        
        initComponents();
        name = getCurrentName();
        index = counter++;  //do we really ever use this?
        setSubscriptText(index);
        degree = GiANT.gui.kash.getDegree(poly);
        pretty = Graphic.prettyPolynomial(poly);
        fractionsLabel.setToolTipText(pretty);
        //do maximalOrder
        maxOrder = getMaxOrderName();
        GiANT.gui.kash.maximalOrder(this);

		String index = Graphic.bracketEnclose(getPrettyName() + ":" +Graphic.Q_RATIONAL);
        INVARIANTS  = new Object[][]{
                        {"Degree " + index, null, name + DEGREE}, 
                        {"Discriminant",  null, name + DISC},
                        {"Signature " + Graphic.SIG_LEGEND, null, name + SIG},
                        {Graphic.Q_RATIONAL + "-Galois Group", null, name + GALOIS},
                        {"Primitive Element", null, name + PRIM_ELT},
                        {"Polynomial Ring Variable", null, name + VAR},
                        {"Power Basis", null, name + BASIS},
                        {"Integral Basis " + IBASIS, null, name + INT_BASIS},
                        {"Maximal Order",null, maxOrder},
						{"  Discriminant",GiANT.gui.kash.maxOrderDisc(this),null}
                      };   
        
        calcInvariants();
        final NumberField me = this;
        table = new InfoTable(INVARIANTS){
            private final NumberField subject = me;
            protected void doubleClickHandler(java.awt.event.MouseEvent evt){
                if(getSelectedRow() == GAL_ROW && getValueAt(GAL_ROW, VAL_COL).toString().equals(InfoTable.DOUBLE_CLICK_CALC)){
                    INVARIANTS[GAL_ROW][VAL_COL] = Graphic.removeDoubleQuotes(GiANT.gui.kash.galoisGroup(INVARIANTS[GAL_ROW][VAR_COL].toString(),me));
                    setValueAt(INVARIANTS[GAL_ROW][VAL_COL], GAL_ROW, VAL_COL);
                }
            }
        };
        //table.setBackground(javax.swing.UIManager.getDefaults().getColor("InternalFrame.background"));
        
        inspector = new Inspector(this);
        GiANT.gui.addFrame(inspector);
    }
    
    /**
     * compute standard invariants
     */
    private void calcInvariants(){

        INVARIANTS[DEG_ROW][VAL_COL] = GiANT.gui.kash.degree(INVARIANTS[DEG_ROW][VAR_COL].toString(), this);
        GiANT.gui.kash.basis(INVARIANTS[BAS_ROW][VAR_COL].toString(), this);
        INVARIANTS[BAS_ROW][VAL_COL] = powerBasis(degree, false);
        INVARIANTS[DIS_ROW][VAL_COL] = GiANT.gui.kash.disc(INVARIANTS[DIS_ROW][VAR_COL].toString(), this);
        INVARIANTS[SIG_ROW][VAL_COL] = GiANT.gui.kash.sig(INVARIANTS[SIG_ROW][VAR_COL].toString(), this);  //below '[2]' gets us the 2nd basis elt
        String primElt = GiANT.gui.kash.primElt(INVARIANTS[PRIM_ROW][VAR_COL].toString(), INVARIANTS[BAS_ROW][VAR_COL].toString() + "[2]");
        INVARIANTS[PRIM_ROW][VAL_COL] = Graphic.prettyPowerBasis(primElt);
        INVARIANTS[VAR_ROW][VAL_COL] = GiANT.gui.kash.polyRingVar(INVARIANTS[VAR_ROW][VAR_COL].toString(), this);
        
        String intBasis = GiANT.gui.kash.integralBasis(INVARIANTS[IBAS_ROW][VAR_COL].toString(), this);
        if(intBasis.equals(KASHTerm.HUH));//do nothing
        else
            intBasis = Graphic.bracketEnclose(Graphic.listToPrettyPowerBasis(intBasis));
        INVARIANTS[IBAS_ROW][VAL_COL] = intBasis;
 
        String galois;
        if(degree < KASHTerm.GALOIS_LIMIT){
            galois = GiANT.gui.kash.galoisGroup(INVARIANTS[GAL_ROW][VAR_COL].toString(),this);
            galois = Graphic.removeDoubleQuotes(galois);
        }else
            galois = InfoTable.DOUBLE_CLICK_CALC;
        INVARIANTS[GAL_ROW][VAL_COL] = galois;

    }

    /**
     * 
     * @return variable name (KASH) for maximal order
     */
    public String getMaxOrderName(){
        return name + MAX_ORDER;
    }
    
    /**
     * 
     * @return table with invariants for unit group
     */
    public JComponent getUnitDetail(){
        return unitTable;
    }
    
    /**
     * 
     * @return table with invariants for unit group
     */
    public JComponent getClassDetail(){
        return classTable;
    }
    
    /**
     * compute unit group
     */
    protected void calcUnitGroup(){
       if(unitGroupReady)
           return;
       Object[][] data  = new Object[][]{
							{"Rank", null, null},
                            {"Structure", null, null},
                            {"Torsion Unit " + KASHTerm.enclose(TOR_CHAR), null, name + TORSION}, 
                            {"Torsion Rank", null, null},
                            {"Fundamental Unit(s) " + KASHTerm.enclose(FUNIT_CHAR + Graphic.SUB_I), null, name + F_UNITS},
							{"Regulator", null, name + REG},
       };
                        
       String torsion = GiANT.gui.kash.torsion(data[TOR_ROW][VAR_COL].toString(), this);
       torsion = Graphic.toList(torsion);//make sure it's in brackets or prettyPowerBasis will bitch
       data[TOR_ROW][VAL_COL] = Graphic.prettyPowerBasis(torsion);
       data[TOR_RANK_ROW][VAL_COL] = GiANT.gui.kash.torsionRank(this);
       
       String fundUnits = GiANT.gui.kash.fundUnits(data[F_UNITS_ROW][VAR_COL].toString(), this);
       String[] fUnits = KASHTerm.splitList(fundUnits);
       
	   StringBuffer fits = new StringBuffer();
       for(int i = 0; i < fUnits.length; i++){
           fits.append(Graphic.prettyPowerBasis(fUnits[i]));
           if(i < fUnits.length - 1)
               fits.append(", ");
       }
       data[F_UNITS_ROW][VAL_COL] = fits.toString();
       data[RANK_ROW][VAL_COL] = Integer.toString(fUnits.length);//rank = # of fund. units
       unitGroupStructure = Graphic.unitGroupStructure(fUnits.length);
       data[STRUCT_ROW][VAL_COL] = KASHTerm.join(unitGroupStructure, Graphic._CROSS_);
       //make sure you do the regulator AFTER getting the fundamental units, else no dice!
       data[REG_ROW][VAL_COL] = GiANT.gui.kash.regulator(data[REG_ROW][VAR_COL].toString(), this);
       
       InfoTable it = new InfoTable(data);
	   //it.setBackground(javax.swing.UIManager.getDefaults().getColor("InternalFrame.background"));
       unitTable.add(it, java.awt.BorderLayout.CENTER);
       unitTable.revalidate();
       unitGroupReady = true;
	   inspector.invariantsTree.toggleNodeAt(Inspector.UNIT_NODE, true);
	   //now fetch the elements tab
	   Tab elts = inspector.getTabWithTitle("Elements");
	   elts.updateVariables();
    }
	
    /**
     * 
     * @return array whose entries are character used to typeset the unit group structure 
     * (starting with torsion, then going on to fundamental units sub i)
     */
    public String[] getUnitGroupStructure(){
		return unitGroupStructure;
	}
	
	/**
	 * 
	 * @return return array whose entries are characters for typesetting the cyclc factors of 
	 * the class group
	 */
	public String getClassGroupFactors(){
		return name + CYCLIC_FACTORS;
	}
	
    /**
     * compute the class group
     */
    protected void calcClassGroup(){
        if(classGroupReady)
           return;

       Object[][] data  = new Object[][]{
                            {"Class Number", null, null},
							{"Structure", null, name + CLASS_G},
                            {"Cyclic Factor(s) " + KASHTerm.enclose(Ideal.C + Graphic.SUB_I), null, name + CYCLIC_FACTORS }
        };	
		String clf = GiANT.gui.kash.classGroup(data[CGROUP_ROW][VAR_COL].toString(), this);
        String structure = parseClassGroup(clf);
		data[CNUMBER_ROW][VAL_COL] = String.valueOf(classNumber);
		//String prefix = Cl + KASHTerm.enclose(getPrettyName()) + Graphic._ISO_;
		data[CGROUP_ROW][VAL_COL] = structure;
		String factors  = GiANT.gui.kash.classGroupFactors(data[CFACTORS_ROW][VAR_COL].toString(), this);
		data[CFACTORS_ROW][VAL_COL] = Ideal.prettyIdealFactors(factors, false);
		InfoTable it = new InfoTable(data);
		//it.setBackground(javax.swing.UIManager.getDefaults().getColor("InternalFrame.background"));
		classTable.add(it, java.awt.BorderLayout.CENTER);
		classTable.revalidate();
		classGroupReady = true;
		inspector.invariantsTree.toggleNodeAt(Inspector.CLASS_NODE, true);
		Tab ideals = inspector.getTabWithTitle("Ideals");
		ideals.updateVariables();
    }
	
	/**
	 * get KASH name for the class group
	 */
	public String getClassGroupName(){
		return name + CLASS_G;
	}
	
    /**
     * 
     * @return <CODE>true</CODE> iff this number field is a unique factorization domain (class 
     * number == 1)
     */
    public boolean isUFD(){
        
        if(!classGroupReady())
            System.err.println("attempting to determine if " + name + " is a UFD before knowing class number");
        return classNumber == 1;
    }
	
	/**
	 * 
	 * @return <CODE>true</CODE> iff the class group has been computed
	 */
	public boolean classGroupReady(){
		return classGroupReady;
	}
    
	/**
	 * 
	 * @param cl KASH representation of class group
	 * @return typeset string representing structure of class group
	 */
	public String parseClassGroup(String cl){
		String[] info = KASHTerm.splitList(cl);
		if(info.length != 2)
			throw new AssertionError("expected class group list of length 2, got:  " + cl);
		classNumber = Integer.parseInt(info[0]);
		String orderList = info[1];
		orderList = orderList.substring(1, orderList.length()-1);//exclude outer [ ]
		orderList = Graphic.removeSpace(orderList);//pack it for easier parsing
		String[] orders = orderList.split(",");
		classGroupStructure = new String[orders.length];
		StringBuffer klass = new StringBuffer();
		for(int i = 0; i < orders.length; i++){
			klass.append("C" + Graphic.toSubscripts(orders[i]));
			if(i < orders.length - 1)
				klass.append(Graphic._CROSS_);
			classGroupStructure[i] = Ideal.C + Graphic.toSubscripts(String.valueOf(i+1));
		}
		return klass.toString();
	}
	
	/**
	 * 
	 * @see #classGroupStructure
	 */
	public String[] getClassGroupStructure(){
		return classGroupStructure;
	}
	
    /**
     * hash number fields by their generating polynomials
     */
    public int hashCode(){
        return poly.hashCode();
    }
    
    /**
     * two n.f.s are equal iff they have the same generating polynomial
     */
    public boolean equals(Object o){
        
        if(o == null)
            return false;
        
        String c = o.getClass().getName();
        if(c.equals(this.getClass().getName())){
            NumberField n = (NumberField)o;
            return this.poly.equals(n.getPoly());
        }else{
            return false;
        }
    }
    
    /**
     * add <CODE>n</CODE> to set of subfields
     * @param n subfield
     */
    protected void addSubfield(NumberField n){
        subfields.add(n);
        subfields.addAll(n.subfields);
    }
    
    /**
     * test whether or not <CODE>n</CODE> is ALREADY in my list of subfields
     * (not a full-blow subfield test)
     * @param n number field
     */
    protected boolean hasSubfield(NumberField n){
        if(n.getDegree() > this.getDegree())
            return false;
        else
            return subfields.contains(n);
    }
    
    /**
     * 
     * @param deg degree over q
     * @param integral use the integral basis character?
     * @return a typeset list of powers of a (greek) character
     */
    public static String powerBasis(int deg, boolean integral){
		char b = B;
		if(integral)
			b = IB;
        StringBuffer basis = new StringBuffer();
        basis.append("1, " + b);
        for(int i = 2; i < deg; i++)
            basis.append(", " + b + Graphic.toSuperscripts(String.valueOf(i)));
        return Graphic.bracketEnclose(basis.toString());
    }
    
    /**
     * test if any of my subfields have <CODE>n</CODE> as a proper subfield
     * @param n n.f.
     * @return <CODE>true</CODE> iff the above condition is fulfilled
     */
    protected boolean subfieldHasProperSubfield(NumberField n){
        Iterator it = subfields.iterator();
        while(it.hasNext()){
            NumberField s = (NumberField)it.next();
            if(s.hasSubfield(n))
                if(s.getDegree() != n.getDegree())
                    return true;
        }
        return false;
    }
    
    /**
     * 
     * @return KASH handle for primitive element
     */
    public String getPrimitiveEltName(){
        return INVARIANTS[PRIM_ROW][VAR_COL].toString();
    }
    
    /**
     * 
     * @return KASH name for variable of my polynomial ring
     */
    public String getVariableName(){
        return INVARIANTS[VAR_ROW][VAR_COL].toString();
    }
    
    /**
     * 
     * @return <CODE>true</CODE> iff i have no proper subfields (excluding Q)
     */
    protected boolean hasNoProperSubfields(){
        Iterator it = subfields.iterator();
        while(it.hasNext()){
            NumberField s = (NumberField)it.next();
            if(s.getDegree() < this.degree)//shouldn't need >...
                return false;
        }
        return true;
    }
    
    /**
     * 
     * @return (KASH) name for this number field TYPESET to include subscripts
     */
    public String getPrettyName(){
        return Graphic.toSubscripts(name.toUpperCase());
    }
    
    /**
     * 
     * @return component to which lines in the tower of fields should be drawn
     */
    protected Component getCenterComponent(){
        return fractionsLabel;
    }
    
	/**
	 * 
	 * @see #inspector
	 */
	public javax.swing.JInternalFrame getInspector(){
		return inspector;
	}
	
    /**
     * 
     * @return number of subfields this n.f. has
     */
    protected int getSubfieldCount(){
        return subfields.size();
    }
    
    /**
     * 
     * @return this class dispenses names for field; the current name is the name that will be
     * given to the next field created
     */
    public static String getCurrentName(){
        return FIELD_CHAR + String.valueOf(counter);
    }
    
    /**
     * 
     * @return absolute degree over Q
     */
    protected int getDegree(){
        return degree;
    }
    
    /**
     * 
     * @return (KASH) name for integral basis
     */
    public String getIntegralBasisName(){
        return name + INT_BASIS;
    }
	
	/**
	 * 
	 * @return (KASH) name for fundamental units list
	 */
	public String getFundamentalUnitsName(){
		return name + F_UNITS;
	}
	
	/**
	 * 
	 * @return <CODE>true</CODE> iff the unit group has been calculated
	 */
	public boolean unitGroupReady(){
		return unitGroupReady;
	}
	
    /**
     * 
     * @return KASH name for this n.f. (actually an Order)
     */
    public String getName(){
        return name;
    }
    
	/**
	 * 
	 * @return typeset generating polynomial for this n.f.
	 */
	public String toString(){
		return Graphic.prettyPolynomial(getPoly());
	}
	
    /**
     * 
     * @return generating polynomial as represented in KASH
     */
    public String getPoly(){
        return poly;
    }
    /**
     * field icons on the desktop have a numerical subscript that is settable with this
     * method
     * @param index desired index
     */
    private void setSubscriptText(int index){
        subscriptsLabel.setText(String.valueOf(index));
    }

    /**
     * have no use for this currently; not sure if this code is necessary...
     */
    public Object[] toRow() {
        return null;
    }

    /**
     * 
     * @return table of invariants for this n.f.
     */
    public JTable getTable() {
        return table;
    }

    /**
     * not implemented!
     * @return value string as given by KASH
     */
    public String getValue() {
        return null;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
	//to learn more about the GUI variables, see Design View via NetBeans
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        textLabel = new javax.swing.JLabel();
        subscriptsLabel = new javax.swing.JLabel();
        fractionsLabel = new javax.swing.JLabel();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(60, 35));
        textLabel.setFont(new java.awt.Font("Lucida Grande", 1, 14));
        textLabel.setText("F");
        textLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        textLabel.setIconTextGap(0);
        add(textLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 6, -1, -1));

        subscriptsLabel.setFont(new java.awt.Font("Lucida Grande", 1, 9));
        add(subscriptsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(31, 16, -1, -1));

        fractionsLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        fractionsLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/numberField.png")));
        fractionsLabel.setToolTipText("Number Field");
        fractionsLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        fractionsLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fractionsLabel.setIconTextGap(0);
        fractionsLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fractionsLabelMouseClicked(evt);
            }
        });

        add(fractionsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 0, 34, 35));

    }
    // </editor-fold>//GEN-END:initComponents

    /**
     * open field inspector whenever the user clicks on this n.f.'s icon
     * @param evt mouse click
     * @see #inspector
     */
    private void fractionsLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fractionsLabelMouseClicked
        // TODO add your handling code here:
        if(inspector.isIcon())
            try{
                inspector.setIcon(false);
            }catch(java.beans.PropertyVetoException e){
                System.err.println("Exception deiconifying inspector:  " + e);
            }
        GiANT.gui.selectAndShow(inspector);
    }//GEN-LAST:event_fractionsLabelMouseClicked
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel fractionsLabel;
    private javax.swing.JLabel subscriptsLabel;
    private javax.swing.JLabel textLabel;
    // End of variables declaration//GEN-END:variables

}