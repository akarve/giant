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
 * InspectorFrame.java
 *
 * Created on April 18, 2005, 1:11 PM
 */

package net.giantsystem.sf;
import javax.swing.*;
import java.util.*;
import java.awt.GridBagConstraints;
import java.lang.AssertionError;
import javax.swing.border.TitledBorder;
import java.awt.event.KeyEvent;
/**
 *
 * @author  karve
 */
public class Inspector extends javax.swing.JInternalFrame{
    //prefixes for automatic variable names...
    public final static int NAME_COL = 0;
    public final static int VAL_COL = 1;    
    public final static int IRRED_COL = 2;
    public final static GridBagConstraints DETAIL_GBC = new GridBagConstraints();          
    static{
        DETAIL_GBC.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        DETAIL_GBC.fill = java.awt.GridBagConstraints.BOTH;
        DETAIL_GBC.anchor = java.awt.GridBagConstraints.NORTH;
        DETAIL_GBC.insets = new java.awt.Insets(0, 0, 2, 0);//give a little space b/w details
        DETAIL_GBC.weightx = 1.0;
        DETAIL_GBC.weighty = 1.0;
   }
    
    public static final int DEG_ROW = 0;
    public static final int BASIS_ROW = 1;
    public static final int DISC_ROW = 2;
    public static final int SIG_ROW = 3;
    public static final int GALOIS_ROW = 4;
    public final static int PRIM_ELT_ROW = 5;
    public final static int POL_VAR_ROW = 6;
	
	public final static int UNIT_NODE = 2;
	public final static int CLASS_NODE = 3;
	public final static int NOTES_NODE = 4;
	public final static String CLICK_CALC = " (click to compute)";
    
	private final NumberField field;
    private final Tab[] TABS;   //the tab object for the tabpile on top of the window!
    private final Set varDetails = new HashSet();
    private boolean initialized = false;

    /** Creates new form InspectorFrame */
    protected Inspector(NumberField n) {
        initComponents();
        field = n;
        setTitle(field.getPrettyName() + " : " + Graphic.prettyPolynomial(field.getPoly()));

        int i = 0;
        TABS = new Tab[]{   new Tab(this, new Element(), "Elements", "elt", KeyEvent.VK_E, i++),
                            new Tab(this, new Polynomial(), "Polynomials", "pol", KeyEvent.VK_P, i++),
                            new Tab(this, new Ideal(), "Ideals", "idl", KeyEvent.VK_I, i++) };
                            
        TABS[0].setTransferHandler(new ElementTransferHandler());
		TABS[1].setTransferHandler(new PolynomialTransferHandler());
		TABS[2].setTransferHandler(new IdealTransferHandler());
		tabbedPane.setDropTarget(new java.awt.dnd.DropTarget(tabbedPane, new java.awt.dnd.DropTargetAdapter(){
			public void drop(java.awt.dnd.DropTargetDropEvent dtde){
				java.awt.Point cursor = dtde.getLocation();
				final int tab = tabbedPane.indexAtLocation(cursor.x, cursor.y);
				if(tab < 0){
					dtde.rejectDrop();
					GiANT.gui.appendConsoleText("Drop lost. Please drop directly onto one of the tabs.", true);
				}else{
					Tab selected = (Tab)tabbedPane.getComponentAt(tab);
					VarTable table = selected.getTable();
					javax.swing.TransferHandler handler = table.getTransferHandler();
					boolean problem = false;
					if(handler != null){
						if(handler.canImport(table, dtde.getCurrentDataFlavors())){
							table.getTransferHandler().importData(table, dtde.getTransferable());
							tabbedPane.setSelectedIndex(tab);
						}else
							problem = true;
					}else
						problem = true;//no handler; handler = null
					if(problem){
						dtde.rejectDrop();
						GiANT.gui.appendConsoleText("\nUnsupported drop for " + tabbedPane.getTitleAt(tab), true);
					}else
						dtde.acceptDrop(dtde.getDropAction());//i'm not sure what getDropAction() is all about...
				}
			}
		}));

		for(i = 0; i < TABS.length; i++){
            tabbedPane.addTab(TABS[i].getTitle(), TABS[i]);
			//doesn't work on OS X...tabbedPane.setMnemonicAt(i, TABS[i].getMnemonic());
		}
							
		invariantsTree.addNode("Current Selection", selectionDetailsPanel);
        invariantsTree.addNode("Number Field", field.getTable());
		invariantsTree.addNode(new FreeNode("Unit Group" + CLICK_CALC, field.getUnitDetail(), false){
			private final NumberField nf = field;
			private boolean done = false;
			public void expand(){
				if(!done){
					nf.calcUnitGroup();
					setTitle("Unit Group");
					setEnabled(true);
					done = true;
				}
				super.expand();
			}
		});
		
		invariantsTree.addNode(new FreeNode("Class Group" + CLICK_CALC, field.getClassDetail(), false){
			private final NumberField nf = field;
			private boolean done = false;
			public void expand(){
				if(!done){
					nf.calcClassGroup();
					setTitle("Class Group");
					setEnabled(true);
					done = true;
				}
				super.expand();
			}
		});
		invariantsTree.addNode("Notes", notesTextArea);
		invariantsTree.toggleNodeAt(NOTES_NODE, true);
        //splitPane.setDividerLocation(500);  //magic!
        tabbedPane.setSelectedIndex(1); //see next comment
        initialized = true;
        tabbedPane.setSelectedIndex(0); //generates stateChanged to enable correct buttons
	}
    
    protected NumberField getField(){
        return field;
    }
	
	public JTabbedPane getTabbedPane(){
		return tabbedPane;
	}

	public Tab getTabWithTitle(String title){
		int i = tabbedPane.indexOfTab(title);
		if(i < 0)
			throw new AssertionError("No tab with title=" + title);
		return TABS[i];
	}
	
    public Tab getSelectedTab(){
        return TABS[tabbedPane.getSelectedIndex()];
    }
	
	public void selectTab(String title){
		int t = tabbedPane.indexOfTab(title);
		tabbedPane.setSelectedIndex(t);
		if(t < 0)
			throw new AssertionError("no tab with title=" + title);
	}
    
    private void operation(final String op){

        final Tab tab = getSelectedTab();
        
        VarTable tbl = tab.getTable();
        int[] rows = tbl.getSelectedRows();
        if(rows.length <= 0){
            GiANT.gui.appendConsoleText("\nNo arguments given to" + op +". Please select at least one variable.", true);
            return;
        }else{
            String[] args = new String[rows.length];
            for(int i = 0; i < rows.length; i++)
                args[i] = tbl.getNameAt(rows[i]);

            String eqn;
            if(args.length == 1){
                if(op.equals(KASHTerm.MINUS))
                    eqn = op + args[0]; //negate element
                else if(op.equals(KASHTerm.PLUS) || op.equals(KASHTerm.TIMES))
                    eqn = args[0] + op + args[0]; //square element or ADD it to itself
                else if(op.equals(KASHTerm.DIVIDE))
                    eqn = "1" + op + args[0];//invert element
                else{
                    GiANT.gui.appendConsoleText("\nInsufficient number of arguments for" + op + ".", true);
                    return;
                }
            }else{
                eqn = KASHTerm.join(args, op);
            }
            tab.operation(eqn);
        }
    }
    
    protected void showDetails(java.awt.Component[] details){
        //System.err.println("SHOW");
        selectionDetailsPanel.removeAll();
        if(details.length == 0)
            selectionDetailsPanel.add(selectionInvalidPanel, DETAIL_GBC);
        else
            for(int i = 0; i < details.length; i++)
                selectionDetailsPanel.add(details[i], DETAIL_GBC);
        
        selectionDetailsPanel.revalidate();    //both calls need for proper update on os x    
        selectionDetailsPanel.repaint();        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        selectionInvalidPanel = new javax.swing.JPanel();
        noSelectionLabel = new javax.swing.JLabel();
        selectionDetailsPanel = new javax.swing.JPanel();
        invariantsLabel = new javax.swing.JLabel();
        notesTextArea = new javax.swing.JTextArea();
        basePanel = new javax.swing.JPanel();
        splitPane = new javax.swing.JSplitPane();
        variablesPanel = new javax.swing.JPanel();
        tabbedPane = new javax.swing.JTabbedPane();
        variableSouthPanel = new javax.swing.JPanel();
        buttonAreaPanel = new javax.swing.JPanel();
        buttonPanel = new javax.swing.JPanel();
        newButton = new javax.swing.JButton();
        newOpSeparator = new javax.swing.JSeparator();
        plusButton = new javax.swing.JButton();
        minusButton = new javax.swing.JButton();
        timesButton = new javax.swing.JButton();
        divideButton = new javax.swing.JButton();
        invariantsArea = new javax.swing.JPanel();
        invariantsScrollPane = new javax.swing.JScrollPane();
        invariantsPanel = new javax.swing.JPanel();
        invariantsTree = new net.giantsystem.sf.FreeTree();

        selectionInvalidPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        noSelectionLabel.setFont(new java.awt.Font("Lucida Grande", 0, 12));
        noSelectionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        noSelectionLabel.setText("Select one or more items above to view the invariants.");
        noSelectionLabel.setEnabled(false);
        noSelectionLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        selectionInvalidPanel.add(noSelectionLabel);

        selectionDetailsPanel.setLayout(new java.awt.GridBagLayout());

        invariantsLabel.setText("Invariants\u2935   ");
        invariantsLabel.setAlignmentY(0.0F);
        invariantsLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        invariantsLabel.setIconTextGap(0);
        invariantsLabel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        notesTextArea.setBackground(new java.awt.Color(251, 254, 208));
        notesTextArea.setFont(new java.awt.Font("Chalkboard", 0, 14));
        notesTextArea.setLineWrap(true);
        notesTextArea.setRows(4);
        notesTextArea.setTabSize(4);
        notesTextArea.setWrapStyleWord(true);
        notesTextArea.setMargin(new java.awt.Insets(2, 2, 2, 2));

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setPreferredSize(new java.awt.Dimension(500, 375));
        try {
            setSelected(true);
        } catch (java.beans.PropertyVetoException e1) {
            e1.printStackTrace();
        }
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                formComponentHidden(evt);
            }
        });
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameActivated(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        basePanel.setLayout(new java.awt.BorderLayout());

        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setOneTouchExpandable(true);
        variablesPanel.setLayout(new java.awt.BorderLayout());

        tabbedPane.setName("Number Field");
        tabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabbedPaneStateChanged(evt);
            }
        });

        variablesPanel.add(tabbedPane, java.awt.BorderLayout.CENTER);

        variableSouthPanel.setLayout(new java.awt.BorderLayout());

        buttonAreaPanel.setLayout(new java.awt.GridBagLayout());

        buttonPanel.setLayout(new javax.swing.BoxLayout(buttonPanel, javax.swing.BoxLayout.X_AXIS));

        newButton.setText("New");
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(newButton);

        newOpSeparator.setOrientation(javax.swing.SwingConstants.VERTICAL);
        buttonPanel.add(newOpSeparator);

        plusButton.setText("+");
        plusButton.setMaximumSize(new java.awt.Dimension(60, 29));
        plusButton.setMinimumSize(new java.awt.Dimension(45, 29));
        plusButton.setPreferredSize(new java.awt.Dimension(45, 29));
        plusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plusButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(plusButton);

        minusButton.setText("-");
        minusButton.setMaximumSize(new java.awt.Dimension(60, 29));
        minusButton.setMinimumSize(new java.awt.Dimension(45, 29));
        minusButton.setPreferredSize(new java.awt.Dimension(45, 29));
        minusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minusButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(minusButton);

        timesButton.setText("\u00d7");
        timesButton.setMaximumSize(new java.awt.Dimension(60, 29));
        timesButton.setMinimumSize(new java.awt.Dimension(45, 29));
        timesButton.setPreferredSize(new java.awt.Dimension(45, 29));
        timesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timesButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(timesButton);

        divideButton.setText("/");
        divideButton.setMaximumSize(new java.awt.Dimension(60, 29));
        divideButton.setMinimumSize(new java.awt.Dimension(45, 29));
        divideButton.setPreferredSize(new java.awt.Dimension(45, 29));
        divideButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                divideButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(divideButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        buttonAreaPanel.add(buttonPanel, gridBagConstraints);

        variableSouthPanel.add(buttonAreaPanel, java.awt.BorderLayout.SOUTH);

        variablesPanel.add(variableSouthPanel, java.awt.BorderLayout.SOUTH);

        splitPane.setLeftComponent(variablesPanel);

        invariantsArea.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        invariantsArea.setToolTipText("Invariants and Notes");
        invariantsScrollPane.setAutoscrolls(true);
        invariantsScrollPane.setOpaque(false);
        invariantsPanel.setLayout(new java.awt.BorderLayout());

        invariantsPanel.setAutoscrolls(true);
        invariantsPanel.add(invariantsTree, java.awt.BorderLayout.CENTER);

        invariantsScrollPane.setViewportView(invariantsPanel);

        invariantsArea.add(invariantsScrollPane);

        splitPane.setRightComponent(invariantsArea);

        basePanel.add(splitPane, java.awt.BorderLayout.CENTER);

        getContentPane().add(basePanel, java.awt.BorderLayout.CENTER);

        pack();
    }
    // </editor-fold>//GEN-END:initComponents

	private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
		java.awt.Dimension frameDim = getSize();
		//java.awt.Dimension panelDim = invariantsPanel.getSize();
		//invariantsPanel.setSize(frameDim.width-4, panelDim.height);
		invariantsTree.setWidth(frameDim.width);
	}//GEN-LAST:event_formComponentResized

    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden
        GiANT.gui.requestDesktopFocus(true);
    }//GEN-LAST:event_formComponentHidden

    private void tabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabbedPaneStateChanged
        if(!initialized)
            return;
        
        final int t = tabbedPane.getSelectedIndex();
        Tab tab = TABS[t];
        String title = tab.getTitle();
        VarTable tbl = tab.getTable();
        tab.updateSelectionDetail();

        if(title.equals("Elements") ||  title.equals("Class Group") || title.equals("Unit Group")){
            plusButton.setEnabled(true);
            minusButton.setEnabled(true);
            timesButton.setEnabled(true);
			plusButton.setText("+");
            timesButton.setText(String.valueOf(Graphic.TIMES));
            divideButton.setEnabled(true);
        }else if(title.equals("Polynomials")){
            plusButton.setEnabled(true);
            minusButton.setEnabled(true);
            timesButton.setEnabled(true);
            timesButton.setText(String.valueOf(Graphic.TIMES));
			plusButton.setText("+");
            divideButton.setEnabled(false);
        }else if(title.equals("Ideals")){
            plusButton.setEnabled(true);
            minusButton.setEnabled(false);
            timesButton.setEnabled(true);
            timesButton.setText(String.valueOf(Graphic.INTERSECT));
            divideButton.setEnabled(false);
        }else{
            plusButton.setEnabled(false);
            minusButton.setEnabled(false);
            timesButton.setEnabled(false);
            plusButton.setText("+");
			timesButton.setText(String.valueOf(Graphic.TIMES));
            divideButton.setEnabled(false);
            System.err.println("Unexpected tab title");
        }
    }//GEN-LAST:event_tabbedPaneStateChanged

    private void timesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timesButtonActionPerformed
        operation(KASHTerm.TIMES);
    }//GEN-LAST:event_timesButtonActionPerformed

    private void plusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plusButtonActionPerformed
        operation(KASHTerm.PLUS);
    }//GEN-LAST:event_plusButtonActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        GiANT.gui.desktop.setSelectedFrame(this);
        //DEBUGSystem.err.println("FRAME");
    }//GEN-LAST:event_formComponentShown

    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated
        GiANT.gui.desktop.setSelectedFrame(this);
                //DEBUGSystem.err.println("FRAME");
    }//GEN-LAST:event_formInternalFrameActivated

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
        GiANT.gui.showNewVariableDialog(getSelectedTab());
    }//GEN-LAST:event_newButtonActionPerformed

    private void divideButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_divideButtonActionPerformed
        operation(KASHTerm.DIVIDE);
    }//GEN-LAST:event_divideButtonActionPerformed

    private void minusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minusButtonActionPerformed
        operation(KASHTerm.MINUS);
    }//GEN-LAST:event_minusButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel basePanel;
    private javax.swing.JPanel buttonAreaPanel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton divideButton;
    private javax.swing.JPanel invariantsArea;
    private javax.swing.JLabel invariantsLabel;
    private javax.swing.JPanel invariantsPanel;
    private javax.swing.JScrollPane invariantsScrollPane;
    protected net.giantsystem.sf.FreeTree invariantsTree;
    private javax.swing.JButton minusButton;
    private javax.swing.JButton newButton;
    private javax.swing.JSeparator newOpSeparator;
    private javax.swing.JLabel noSelectionLabel;
    private javax.swing.JTextArea notesTextArea;
    private javax.swing.JButton plusButton;
    private javax.swing.JPanel selectionDetailsPanel;
    private javax.swing.JPanel selectionInvalidPanel;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JButton timesButton;
    private javax.swing.JPanel variableSouthPanel;
    private javax.swing.JPanel variablesPanel;
    // End of variables declaration//GEN-END:variables
    
}
