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
 *
 * @author  karve
 */
//TO DO:  maybe this should inherit from Variable (or make Variable an interface)
//that would allow for smart playlists / inspectors for numberfields/whole GUI!
//there is no need fo numberfield to extend JPanel...it just contains one
//just rip out the non-bean stuff + move to another class, this will be called numberfield view or something
//...that can contain a point to the numberfield info object which is a VARIABLE
public class NumberField extends javax.swing.JPanel implements KASH {
    public final static char FIELD_CHAR = 'f';
    
    private static int counter = 0;  //this keeps track of numberfields, hands out indexes

    public final static int VAL_COL = InfoTable.VAL_COL; //the value column for the info table
    public final static int NAME_COL = InfoTable.NAME_COL;
    public final static int VAR_COL = InfoTable.VAR_COL;    //auto variable names go here
    //for the INVARIANTS[][] tables
    public final static int DEG_ROW = 0;
    public final static String DEGREE = "deg";
    public final static int BAS_ROW = 6;
    public final static String BASIS = "basis";
    public final static int DIS_ROW = 1;
    public final static String DISC = "disc";
    public final static int SIG_ROW = 2;
    public final static String SIG = "sig";
    public final static int GAL_ROW = 3;
    public final static String GALOIS = "gal";
    public final static int PRIM_ROW = 4;
    public final static String PRIM_ELT = "p";
    public final static int VAR_ROW = 5;
    public final static String VAR = "x";
    public final static int IBAS_ROW = 7;
    public final static char B = Graphic.SMALL_ALPHA;//used as power basis character = primitive elt.
	public final static char IB = Graphic.SMALL_XI;//used as power basis character = primitive elt.
    public final static char I = 'i';//see VariableDialog; used as abbreviation for the integral basis list
    public final static char U = 'u';//see VariableDialog; used as abbreviation for the fundamental unit list
    public final static char C = 'c';//see VariableDialog; used as abbreviation for the class rep list
	public final static String UNIT_GROUP = "Unit Group";
    public final static String TORSION = "tor";
    public final static int TOR_ROW = 2;
    public final static String TOR_CHAR = String.valueOf(Graphic.SMALL_EPSILON);
    public final static String FUNIT_CHAR = String.valueOf(Graphic.SMALL_ETA);
    public final static int REG_ROW = 5;
    public final static String REG = "reg";
    public final static String RANK = "rank";
    public final static int RANK_ROW = 0;
    public final static int STRUCT_ROW = 1;
    public final static String TOR_RANK = "trank";
    public final static int TOR_RANK_ROW = 3; 
    public final static String F_UNITS = "funit";
    public final static int F_UNITS_ROW = 4;
    public final static String CLASS_GROUP = "Class Group";
    public final static String MAX_ORDER = "max";
	public final static int MAX_ORDER_ROW = 8;
	public final static int MAX_ORDER_DISC_ROW = MAX_ORDER_ROW + 1;
    public final static String INT_BASIS = "i" + BASIS;
	public final static String IBASIS = KASHTerm.enclose(String.valueOf(Graphic.SMALL_XI) + Graphic.SUPER_N);
    public final static String CYCLIC_FACTORS = "clf";
	public final static String CLASS_G = "cl";
	public final static String Cl = "Cl";
	public final static int CGROUP_ROW = 1;
	public final static int CNUMBER_ROW = 0;
	public final static int CFACTORS_ROW = 2;
	public final static String CLICK_CALC = "(Click to compute)";
    private Object[][] INVARIANTS; //data underlying InfoTable
    
    private int degree;
    protected final int index;  //unique ID
	private int classNumber = -1;//initialize to impossible value
    private String name;
    private String poly;
    private String pretty;
    private String maxOrder;
    private JTable table;
    private JPanel unitTable = new JPanel(new java.awt.BorderLayout());
    private JPanel classTable = new JPanel(new java.awt.BorderLayout());
    private boolean unitGroupReady = false;
    private boolean classGroupReady = false;
    private String[] unitGroupStructure;
	private String[] classGroupStructure;
    //all PROPER subfields (remember we do not allow the same field twice in a city 
    //  (see City class, addField)
    private Set subfields = new HashSet();                        
    private final Inspector inspector;
    
    /** Creates new form NumberFieldPanel */
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

    public String getMaxOrderName(){
        return name + MAX_ORDER;
    }
    
    public JComponent getUnitDetail(){
        return unitTable;
    }
    
    public JComponent getClassDetail(){
        return classTable;
    }
    
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
	
    public String[] getUnitGroupStructure(){
		return unitGroupStructure;
	}
	
	public String getClassGroupFactors(){
		return name + CYCLIC_FACTORS;
	}
	
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
	
	public String getClassGroupName(){
		return name + CLASS_G;
	}
	
	
	public boolean classGroupReady(){
		return classGroupReady;
	}
    
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
	
	public String[] getClassGroupStructure(){
		return classGroupStructure;
	}
	
    public int hashCode(){
        return poly.hashCode();
    }
    
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
    
    protected void addSubfield(NumberField n){
        subfields.add(n);
        subfields.addAll(n.subfields);
    }
    
    protected boolean hasSubfield(NumberField n){
        if(n.getDegree() > this.getDegree())
            return false;
        else
            return subfields.contains(n);
    }
    
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
    
    public String getPrimitiveEltName(){
        return INVARIANTS[PRIM_ROW][VAR_COL].toString();
    }
    
    public String getVariableName(){
        return INVARIANTS[VAR_ROW][VAR_COL].toString();
    }
    
    protected boolean hasNoProperSubfields(){
        Iterator it = subfields.iterator();
        while(it.hasNext()){
            NumberField s = (NumberField)it.next();
            if(s.getDegree() < this.degree)//shouldn't need >...
                return false;
        }
        return true;
    }
    
    public String getPrettyName(){
        return Graphic.toSubscripts(name.toUpperCase());
    }
    
    protected Component getCenterComponent(){
        return fractionsLabel;
    }
    
	public javax.swing.JInternalFrame getInspector(){
		return inspector;
	}
	
    protected int getSubfieldCount(){
        return subfields.size();
    }
    
    public static String getCurrentName(){
        return FIELD_CHAR + String.valueOf(counter);
    }
    
    protected int getDegree(){
        return degree;
    }
    
    public String getIntegralBasisName(){
        return name + INT_BASIS;
    }
	
	public String getFundamentalUnitsName(){
		return name + F_UNITS;
	}
	
	public boolean unitGroupReady(){
		return unitGroupReady;
	}
	
    public String getName(){
        return name;
    }
    
	public String toString(){
		StringBuffer display = new StringBuffer();
		//display.append(getPrettyName());
		//display.append(":  ");
		display.append(Graphic.prettyPolynomial(getPoly()));
		return display.toString();
	}
    public String getPoly(){
        return poly;
    }
    
    private void setSubscriptText(int index){
        //String name = field + Graphic.toSubscripts(String.valueOf(index));
        //textLabel.setText(name);
        //textLabel.setText("F??");
        subscriptsLabel.setText(String.valueOf(index));
        //DEBUG
        //GiANT.gui.setConsoleText("***" + name, true);
    }

    public Object[] toRow() {
        return null;//have no use for this currently
    }

    public JTable getTable() {
        return table;
    }

    public String getValue() {
        return null;//not implemented (yet)
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
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