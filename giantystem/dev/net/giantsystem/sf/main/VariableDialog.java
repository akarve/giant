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
import apple.laf.*;
import javax.swing.border.TitledBorder;

/**
 *
 * @author  karve
 */
public class VariableDialog extends javax.swing.JInternalFrame {

    public static final String PREFIX = "New in ";
    public static Inspector dest;  //WARN:  is this threadsafe?
    //public final static Pattern P_ALL = Pattern.compile("(?i)(\\W+)p(\\W+)");
    //public final static Pattern X_ALL = Pattern.compile("(?i)(\\W+)x(\\W+)");
    
    public final static String P_SHORT = NumberField.PRIM_ELT + " := primitive element ";
    public final static String X_SHORT = NumberField.VAR + " := variable of univariate polynomial ring";
    public final static String I_SHORT = NumberField.I + " := integral basis list";
    public final static String C_SHORT = NumberField.C + " := class group factors list (iff computed)";
	public final static String I_STRING = String.valueOf(NumberField.I);
	public final static String U_SHORT = NumberField.U + " := fundamental units list (iff computed)";
	public final static String XU_SHORT = NumberField.U + " := unavailable; the unit group has not been computed";
    public final static String U_STRING = String.valueOf(NumberField.U);
	public final static String C_STRING = String.valueOf(NumberField.C);
	public final static String X_RING = "[X]";
    public final static int P_ROW = 0;
    public final static int X_ROW = 1;
    public final static int I_ROW = 2;
	public final static int U_ROW = 3;
	public final static int C_ROW = 4;
    public final static int SHORT_COL = 0;
	
	//public final static Pattern LONE_RATIONAL = Pattern.compile("\\s*\\d+\\s*(/\\s*\\d+)?\\s*");
	//any rational not in a bracketed list! or followed by ','
	//public final static Pattern LONE_RATIONAL = Pattern.compile("([\\W&&[^/^]]\\d+(/\\d+)?)([\\W&&[^,\\]])"); 
	public final static Pattern LONE_RATIONAL = Pattern.compile("(\\s*\\d+(/\\d+)?)\\s*");
	public final static Pattern TWO_RATIONALS = Pattern.compile("(\\s*\\d+(/\\d+)?)(,\\d+(/\\d+)?)\\s*");    
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
    
    public void setTab(Tab t){
        tab = t;
        String field = tab.getInspector().getField().getPrettyName();
        setTitle(PREFIX + field + " " + tab.getTitle());

        TitledBorder tb = (TitledBorder)tipsArea.getBorder();
        tb.setTitle("Abbreviations for " + field + " (valid only in this window)");
        nameField.setText(t.getAutoName());

    }
    
    public String getName(){
        return nameField.getText();
    }
    
    private void handleEscapeKey(java.awt.event.KeyEvent evt){
        int code = evt.getKeyCode();

        if(code == java.awt.event.KeyEvent.VK_ESCAPE)
            setVisible(false);
    }
    
    public String getValue(){
        return valueField.getText();
    }
    
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
        setPreferredSize(new java.awt.Dimension(400, 250));
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

        nameField.setFont(new java.awt.Font("Monaco", 0, 13));
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

        valueField.setFont(new java.awt.Font("Monaco", 0, 13));
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
        abbreviationsTable.setFont(new java.awt.Font("Monaco", 0, 11));
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

        createButton.setText("Create");
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

    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden

        GiANT.gui.selectAndShow(tab.getInspector());
        tab.getTable().grabFocus();
    }//GEN-LAST:event_formComponentHidden

    private void valueFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_valueFieldKeyPressed
        handleEscapeKey(evt);
    }//GEN-LAST:event_valueFieldKeyPressed

    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createButtonActionPerformed
        makeVariable(evt);
    }//GEN-LAST:event_createButtonActionPerformed

    private void valueFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_valueFieldActionPerformed
        makeVariable(evt);
    }//GEN-LAST:event_valueFieldActionPerformed

    private void nameFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nameFieldKeyPressed
        handleEscapeKey(evt);
    }//GEN-LAST:event_nameFieldKeyPressed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        valueField.grabFocus();
    }//GEN-LAST:event_formComponentShown

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    
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
