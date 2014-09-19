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
 * GlobalInspector.java
 *
 * Created on June 5, 2005, 5:14 PM
 */

package net.giantsystem.sf;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.ToolTipManager;
import javax.swing.ImageIcon;
import javax.swing.Icon;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;

/**
 *
 * @author  karve
 */
public class GlobalInspector extends javax.swing.JInternalFrame {
	private final Icon fieldIcon = new ImageIcon(getClass().getResource("/images/fieldNode.png"));
	private final Icon eltIcon = new ImageIcon(getClass().getResource("/images/eltNode.png"));
	private final Icon polyIcon = new ImageIcon(getClass().getResource("/images/polyNode.png"));
	private final Icon idealIcon = new ImageIcon(getClass().getResource("/images/idealNode.png"));
	
	private int degreeFloor = 0;
	private boolean initialized = false;
	/** Creates new form GlobalInspector */
	public GlobalInspector() {
		initComponents();
		allBox.doClick();
		//splitPane.setDividerLocation(500);//magic
		initialized = true;
	}
	
	private void refreshTree(){
		treeScrollPane.setViewportView(buildTree());
	}
	private javax.swing.JTree buildTree(){

		java.util.ArrayList nodes = new java.util.ArrayList();
		if(fieldsBox.isSelected()){
			java.util.Set fields = GiANT.gui.getFields();
			java.util.Iterator it = fields.iterator();
			while(it.hasNext()){
				NumberField n = (NumberField)it.next();
				if(n.getDegree() > degreeFloor)
					nodes.add(new VarNode(n, fieldIcon));
			}
		}
		boolean elts = eltsBox.isSelected() ;
		boolean polys = polysBox.isSelected();
		boolean ideals = idealsBox.isSelected();
		if(elts || polys || ideals){
			Object[] vars = Tab.getVariables();
			for(int i = 0; i < vars.length; i++){
				Variable v = (Variable)vars[i];
				String type = v.getTab().getTitle();
				if(type.equals("Elements")){
					if(elts)
						nodes.add(new VarNode(v, eltIcon));
				}else if(type.equals("Polynomials")){
					if(polys)
						nodes.add(new VarNode(v, polyIcon));
				}else if(type.equals("Ideals")){
					if(ideals)
						nodes.add(new VarNode(v, idealIcon));
				}else
					throw new AssertionError("Unexpected variable type=" + type);
			}
		}
		JTree tree = new JTree(nodes.toArray());
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setCellRenderer(new NodeRenderer());
		tree.addTreeSelectionListener(new SelectionListener(tree));
		return tree;
	}
	
	private static class VarNode{
		private Object content;
		private Icon icon;
		private String toString = null;
		private javax.swing.JInternalFrame inspector;
		
		private VarNode(Variable v, Icon i){
			content = v;
			icon = i;
			inspector = v.getTab().getInspector();
			Object[] row = v.toRow();
			toString = row[0] + ":  " + row[1];
		}
		
		private VarNode(NumberField n, Icon i){
			icon = i;
			content = n;
			inspector = n.getInspector();
		}
		
		public String toString(){
			if(toString == null)
				return content.toString();
			else
				return toString;
		}
	}
	
	public static class SelectionListener implements TreeSelectionListener{
		private final JTree tree;
		public SelectionListener(JTree t){
			tree = t;
		}
		
		public void valueChanged(TreeSelectionEvent e) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
				tree.getLastSelectedPathComponent();
			if(node == null)
				return;
			VarNode vn = (VarNode)node.getUserObject();
			GiANT.gui.selectAndShow(vn.inspector);
		}
	}
	
	public static class NodeRenderer extends DefaultTreeCellRenderer{

		public Component getTreeCellRendererComponent(
							JTree tree,
							Object value,
							boolean sel,
							boolean expanded,
							boolean leaf,
							int row,
							boolean hasFocus) {

			super.getTreeCellRendererComponent(
							tree, value, sel,
							expanded, leaf, row,
							hasFocus);
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			VarNode vn = (VarNode)node.getUserObject();
			setIcon(vn.icon);
			//setToolTipText("This book is in the Tutorial series."); 

			return this;
		}
	}
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        splitPane = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        treeScrollPane = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        refreshButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        showPanel = new javax.swing.JPanel();
        allBox = new javax.swing.JCheckBox();
        fieldsBox = new javax.swing.JCheckBox();
        eltsBox = new javax.swing.JCheckBox();
        polysBox = new javax.swing.JCheckBox();
        idealsBox = new javax.swing.JCheckBox();
        criteriaPanel = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox();
        jComboBox2 = new javax.swing.JComboBox();
        criteriaField = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Global Inspector");
        setAutoscrolls(true);
        setPreferredSize(new java.awt.Dimension(400, 400));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(1.0);
        splitPane.setOneTouchExpandable(true);
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel1.add(treeScrollPane, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 0));

        refreshButton.setText("Refresh");
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        jPanel3.add(refreshButton);

        jPanel1.add(jPanel3, java.awt.BorderLayout.SOUTH);

        splitPane.setLeftComponent(jPanel1);

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));

        jPanel2.setBorder(new javax.swing.border.TitledBorder("Show"));
        showPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        allBox.setText("All");
        allBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allBoxActionPerformed(evt);
            }
        });

        showPanel.add(allBox);

        fieldsBox.setText("Fields");
        fieldsBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldsBoxActionPerformed(evt);
            }
        });

        showPanel.add(fieldsBox);

        eltsBox.setText("Elements");
        eltsBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eltsBoxActionPerformed(evt);
            }
        });

        showPanel.add(eltsBox);

        polysBox.setText("Polynomials");
        polysBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                polysBoxActionPerformed(evt);
            }
        });

        showPanel.add(polysBox);

        idealsBox.setText("Ideals");
        idealsBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idealsBoxActionPerformed(evt);
            }
        });

        showPanel.add(idealsBox);

        jPanel2.add(showPanel);

        criteriaPanel.setLayout(new javax.swing.BoxLayout(criteriaPanel, javax.swing.BoxLayout.X_AXIS));

        criteriaPanel.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)), "Criteria (this feature is only partially implemented)"));
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Degree" }));
        criteriaPanel.add(jComboBox1);

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "is greater than" }));
        criteriaPanel.add(jComboBox2);

        criteriaField.setMaximumSize(new java.awt.Dimension(2147483647, 22));
        criteriaField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                criteriaFieldActionPerformed(evt);
            }
        });

        criteriaPanel.add(criteriaField);

        jButton1.setText("+");
        jButton1.setEnabled(false);
        jButton1.setIconTextGap(0);
        jButton1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton1.setMaximumSize(new java.awt.Dimension(45, 29));
        jButton1.setMinimumSize(new java.awt.Dimension(20, 29));
        jButton1.setPreferredSize(new java.awt.Dimension(30, 29));
        criteriaPanel.add(jButton1);

        jButton2.setText("-");
        jButton2.setEnabled(false);
        jButton2.setIconTextGap(0);
        jButton2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton2.setMaximumSize(new java.awt.Dimension(45, 29));
        jButton2.setMinimumSize(new java.awt.Dimension(20, 29));
        jButton2.setPreferredSize(new java.awt.Dimension(30, 29));
        criteriaPanel.add(jButton2);

        jPanel2.add(criteriaPanel);

        splitPane.setRightComponent(jPanel2);

        getContentPane().add(splitPane, java.awt.BorderLayout.CENTER);

        pack();
    }
    // </editor-fold>//GEN-END:initComponents

	private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
		refreshTree();
	}//GEN-LAST:event_refreshButtonActionPerformed

	private void allBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allBoxActionPerformed

		if(allBox.isSelected()){
			eltsBox.setSelected(true);
			eltsBox.setEnabled(false);
			idealsBox.setSelected(true);
			idealsBox.setEnabled(false);
			polysBox.setSelected(true);
			polysBox.setEnabled(false);
			fieldsBox.setSelected(true);
			fieldsBox.setEnabled(false);
		}else{
			eltsBox.setEnabled(true);
			idealsBox.setEnabled(true);
			polysBox.setEnabled(true);
			fieldsBox.setEnabled(true);
		}
		if(initialized)
			refreshTree();
	}//GEN-LAST:event_allBoxActionPerformed

	private void idealsBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idealsBoxActionPerformed
		refreshTree();
	}//GEN-LAST:event_idealsBoxActionPerformed

	private void polysBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_polysBoxActionPerformed
		refreshTree();
	}//GEN-LAST:event_polysBoxActionPerformed

	private void eltsBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eltsBoxActionPerformed
		refreshTree();
	}//GEN-LAST:event_eltsBoxActionPerformed

	private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
		refreshTree();
	}//GEN-LAST:event_formComponentShown

	private void criteriaFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_criteriaFieldActionPerformed
		String number = criteriaField.getText().trim();
		int deg=1;
		boolean problem = false;
		try{
			deg = Integer.parseInt(number);
		}catch(NumberFormatException e){
			problem = true;
		}
		if(!problem){
			degreeFloor = deg;
			refreshTree();
		}
	}//GEN-LAST:event_criteriaFieldActionPerformed

	private void fieldsBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldsBoxActionPerformed
		refreshTree();
	}//GEN-LAST:event_fieldsBoxActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox allBox;
    private javax.swing.JTextField criteriaField;
    private javax.swing.JPanel criteriaPanel;
    private javax.swing.JCheckBox eltsBox;
    private javax.swing.JCheckBox fieldsBox;
    private javax.swing.JCheckBox idealsBox;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JCheckBox polysBox;
    private javax.swing.JButton refreshButton;
    private javax.swing.JPanel showPanel;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JScrollPane treeScrollPane;
    // End of variables declaration//GEN-END:variables
	
}
