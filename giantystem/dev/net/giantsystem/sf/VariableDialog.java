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
 * VariableDialog.java
 *
 * Created on April 19, 2005, 10:05 AM
 */

package net.giantsystem.sf;
import java.util.regex.*;
//import apple.laf.*;
import javax.swing.border.TitledBorder;

/**
 * Provide a window for creating new variables within the currently selected field
 * @author karve
 */
public class VariableDialog extends javax.swing.JInternalFrame {

    /**
     * Prefix for title of the window provided by this class
     */
    public static final String PREFIX = "New in ";
    /**
     * The destinaton for the new variable; an instance of <CODE>Inspector</CODE>. This
     * signifies which field we are working in (inspectors and fields are 1:1).
     */
    public static Inspector dest;  //WARN:  is this threadsafe?
    /**
     * short description for primitive element abbrev.
     */
    public final static String P_SHORT = NumberField.PRIM_ELT + " := primitive element ";
    /**
     * short description for polynomial ring var. abbrev.
     */
    public final static String X_SHORT = NumberField.VAR + " := variable of univariate polynomial ring";
    /**
     * short description for integral basis abbrev.
     */
    public final static String I_SHORT = NumberField.I + " := integral basis list";
    /**
     * short description for class field factors abbrev.
     */
    public final static String C_SHORT = NumberField.C + "[i] := i-th class group factor's generator (iff computed)";
	/**
	 * integral basis character as a <CODE>String</CODE>
	 */
	public final static String I_STRING = String.valueOf(NumberField.I);
	/**
	 * short description of abbreviation for fundamental units abbrev.
	 */
	public final static String U_SHORT = NumberField.U + "[i] := i-th fundamental unit (iff computed)";
    /**
     * fundamental unit character (e.g. eta) as a <CODE>String</CODE>
     */
    public final static String U_STRING = String.valueOf(NumberField.U);
	/**
	 * class group factors character as a <CODE>String</CODE>
	 */
	public final static String C_STRING = String.valueOf(NumberField.C);
	/**
	 * polynomial ring notation
	 */
	public final static String X_RING = "[X]";
    /**
     * row index at which primitive elt. abbrev. is found
     */
    public final static int P_ROW = 0;
    /**
     * row index at which poly. rin var. abbrev. is found
     */
    public final static int X_ROW = 1;
    /**
     * row index at which integral basis abbrev. is found
     */
    public final static int I_ROW = 2;
	/**
	 * row index at which fundamental unit abbrev. is found
	 */
	public final static int U_ROW = 3;
	/**
	 * row index at which class group factors abbrev. is found
	 */
	public final static int C_ROW = 4;
    /**
     * column index for short descriptions
     */
    public final static int SHORT_COL = 0;
	
	//public final static Pattern LONE_RATIONAL = Pattern.compile("\\s*\\d+\\s*(/\\s*\\d+)?\\s*");
	//any rational not in a bracketed list! or followed by ','
	//public final static Pattern LONE_RATIONAL = Pattern.compile("([\\W&&[^/^]]\\d+(/\\d+)?)([\\W&&[^,\\]])"); 
	/**
	 * matches a rational argument not multiplied by some algebraic element in KASH;
	 * by catching these the user can just type '1/2' to generate an ideal. we auto-
	 * matically multiply the lone rational by some algebraic element to specify which
	 * field we are in and avoid complaints from KASH
	 */
	public final static Pattern LONE_RATIONAL = Pattern.compile("(\\s*\\d+(/\\d+)?)\\s*");
	/**
	 * matches two rational arguments separated by a comma and not multiplied by some
	 * algebraic element in KASH; by catching these the user can just type '1/2, 1/3' 
	 * to generate an ideal. We then automatically multiply the lone rationals by some 
	 * algebraic element to specify which number field we are in and thus 
	 * avoid complaints from KASH
	 */
	public final static Pattern TWO_RATIONALS = Pattern.compile("(\\s*\\d+(/\\d+)?)(,\\d+(/\\d+)?)\\s*");    
    /**
     * newly created variables will live in some instance of <CODE>Tab</CODE>; this 
     * field specifies the destination <CODE>Tab</CODE>; the tab itself lives in the 
     * destination inspector specified by <CODE>dest</CODE>
     * @see #dest
     */
    private Tab tab;
    /** Creates new form VariableDialog */
    public VariableDialog() {
        initComponents();
		abbreviationsTable.setValueAt(P_SHORT + KASHTerm.enclose(Graphic.POWER), P_ROW, SHORT_COL);
        abbreviationsTable.setValueAt(I_SHORT,I_ROW, SHORT_COL);
		abbreviationsTable.setValueAt(X_SHORT,X_ROW, SHORT_COL);
		abbreviationsTable.setValueAt(U_SHORT,U_ROW, SHORT_COL);
		abbreviationsTable.setValueAt(C_SHORT,C_ROW, SHORT_COL);
    }
    
    /**
     * Set the destination tab (tells us <I>where</I> to create the new <CODE>Variable</CODE>)
     * @see #tab
     * @param t a tab
     */
    public void setTab(Tab t){
        tab = t;
        String field = tab.getInspector().getField().getPrettyName();
        setTitle(PREFIX + field + " " + tab.getTitle());

        TitledBorder tb = (TitledBorder)tipsArea.getBorder();
        tb.setTitle("Abbreviations (valid only in this window)");
        nameField.setText(t.getAutoName());

    }
    
    /**
     * name for the new variable
     * @return string in the GUI field for the new variable's name
     * @see #nameField
     */
    public String getName(){
        return nameField.getText();
    }
    
    /**
     * Hide the window when the user hits <esc>
     * @param evt key event
     */
    private void handleEscapeKey(java.awt.event.KeyEvent evt){
        int code = evt.getKeyCode();

        if(code == java.awt.event.KeyEvent.VK_ESCAPE)
            setVisible(false);
    }
    
    /**
     * get the value of the new variable
     * @return string in the GUI field for the new variable's value
     * @see #valueField
     */
    public String getValue(){
        return valueField.getText();
    }
    
    /**
     * Called when the user hits <return> in the <CODE>valueField</CODE> or clicks 'New'
     * @see #valueField
     * @param evt sponsoring event
     */
    private void makeVariable(java.awt.event.ActionEvent evt){
        final String name = nameField.getText();
        String val = valueField.getText();
		//check for any raw rationals...to prevent KASH error multiply by 1
			//i.e. by default rationals do not belong to the field at hand!
			//in KASH you can't do Ideal(2/3);
		final NumberField field = tab.getInspector().getField();
		final String basis = field.getIntegralBasisName();
		final String one = "*" + basis + "[1] ";//that space is KEY to preserve padding

		String fixed = " " + Graphic.removeSpace(val) + " ";//avoid trouble matching boundaries
		Matcher m = LONE_RATIONAL.matcher(fixed);
		Matcher m2 = TWO_RATIONALS.matcher(fixed);
		if(m.matches())
			fixed = m.group(1)+one;//prevent KASH error from creating element from bare rationals
		else if(m2.matches())
			fixed = m2.group(1)+one + m2.group(3)+one;//prevent KASH error from creating element from bare rationals
		else{
			StringBuffer buff = new StringBuffer();
			String replace = field.getPrimitiveEltName();
			//do regex to swap abbreviations for real names in KASH
				//p
			fixed = Graphic.convert(fixed,NumberField.PRIM_ELT,replace);
			replace = field.getVariableName();
				//x
			fixed = Graphic.convert(fixed, NumberField.VAR, replace);
				//i
			replace = basis;
			fixed = Graphic.convert(fixed, I_STRING, replace);
				//u
			if(field.unitGroupReady()){
				replace = tab.getInspector().getField().getFundamentalUnitsName();
				fixed = Graphic.convert(fixed, U_STRING, replace);
			}

			if(field.classGroupReady()){
				replace = field.getClassGroupFactors();
				fixed = Graphic.convert(fixed, C_STRING, replace);
			}

			fixed.replaceAll("\\+", " \\+ ");//looks confusing otherwise
			fixed.replaceAll("-", " - ");//it's going to come out in the shell
			fixed = fixed.trim();
		}
        boolean ok = tab.addNewVariable(name, fixed);
        if(ok){
            setVisible(false);
            nameField.setText("");
            valueField.setText("");
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup = new javax.swing.ButtonGroup();
        basePanel = new javax.swing.JPanel();
        fieldPanel = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        valueLabel = new javax.swing.JLabel();
        valueField = new javax.swing.JTextField();
        tipsArea = new javax.swing.JPanel();
        abbreviationsTable = new javax.swing.JTable();
        buttonPanel = new javax.swing.JPanel();
        cancelButton = new javax.swing.JButton();
        createButton = new javax.swing.JButton();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setTitle("New Variable");
        setPreferredSize(new java.awt.Dimension(500, 250));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                formComponentHidden(evt);
            }
        });

        basePanel.setLayout(new java.awt.BorderLayout());

        basePanel.setFocusable(false);
        fieldPanel.setLayout(new java.awt.GridBagLayout());

        fieldPanel.setFocusable(false);
        nameLabel.setText("Name");
        nameLabel.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 5);
        fieldPanel.add(nameLabel, gridBagConstraints);

        nameField.setFont(new java.awt.Font("Courier", 0, 13));
        nameField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                nameFieldKeyPressed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        fieldPanel.add(nameField, gridBagConstraints);

        valueLabel.setText("Value");
        valueLabel.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 5);
        fieldPanel.add(valueLabel, gridBagConstraints);

        valueField.setFont(new java.awt.Font("Courier", 0, 13));
        valueField.setToolTipText("Enter any valid KASH expression");
        valueField.setFocusCycleRoot(true);
        valueField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                valueFieldActionPerformed(evt);
            }
        });
        valueField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                valueFieldKeyPressed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        fieldPanel.add(valueField, gridBagConstraints);

        tipsArea.setLayout(new java.awt.BorderLayout());

        tipsArea.setBorder(new javax.swing.border.TitledBorder(""));
        tipsArea.setFocusable(false);
        abbreviationsTable.setBackground(javax.swing.UIManager.getDefaults().getColor("InternalFrame.background"));
        abbreviationsTable.setFont(new java.awt.Font("Courier", 0, 12));
        abbreviationsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                ""
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        abbreviationsTable.setFocusable(false);
        abbreviationsTable.setShowHorizontalLines(false);
        abbreviationsTable.setShowVerticalLines(false);
        tipsArea.add(abbreviationsTable, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 10);
        fieldPanel.add(tipsArea, gridBagConstraints);

        basePanel.add(fieldPanel, java.awt.BorderLayout.CENTER);

        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        buttonPanel.setFocusable(false);
        cancelButton.setText("Cancel");
        buttonGroup.add(cancelButton);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(cancelButton);

        createButton.setText("New");
        buttonGroup.add(createButton);
        createButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(createButton);

        basePanel.add(buttonPanel, java.awt.BorderLayout.SOUTH);

        getContentPane().add(basePanel, java.awt.BorderLayout.CENTER);

        pack();
    }
    // </editor-fold>//GEN-END:initComponents

    /**
     * called when the window is hidden
     * @param evt closing event
     */
    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden

        GiANT.gui.selectAndShow(tab.getInspector());
        tab.getTable().grabFocus();
    }//GEN-LAST:event_formComponentHidden

    /**
     * called when keys are pressed while <CODE>valueField</CODE> has focus
     */
    private void valueFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_valueFieldKeyPressed
        handleEscapeKey(evt);
    }//GEN-LAST:event_valueFieldKeyPressed

    /**
     * what to do when the user clicks 'New'
     */
    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createButtonActionPerformed
        makeVariable(evt);
    }//GEN-LAST:event_createButtonActionPerformed

    /**
     * called when the user hits <enter> while in <CODE>valueField</CODE>
     */
    private void valueFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_valueFieldActionPerformed
        makeVariable(evt);
    }//GEN-LAST:event_valueFieldActionPerformed

    /**
     * handle key presses sent to <CODE>nameField</CODE>
     */
    private void nameFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nameFieldKeyPressed
        handleEscapeKey(evt);
    }//GEN-LAST:event_nameFieldKeyPressed

    /**
     * called whenever the window is shown
     */
    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        valueField.grabFocus();
    }//GEN-LAST:event_formComponentShown

    /**
     * called when the 'Cancel' button is clicked
     */
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    //see NetBeans form (Design view of this file) for more info. on the
	//following variables
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable abbreviationsTable;
    private javax.swing.JPanel basePanel;
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton createButton;
    private javax.swing.JPanel fieldPanel;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JPanel tipsArea;
    private javax.swing.JTextField valueField;
    private javax.swing.JLabel valueLabel;
    // End of variables declaration//GEN-END:variables
    
}
