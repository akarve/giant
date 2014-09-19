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
 * FreeTree.java
 *
 * Created on June 3, 2005, 10:13 AM
 */

package net.giantsystem.sf;
import java.awt.GridBagConstraints;
import javax.swing.JComponent;
import java.awt.Component;
import java.awt.GridBagLayout;
/**
 * Provides a GUI widget similar to <CODE>JTree</CODE> but children can be 
 * arbitrary Swing components.
 * @author karve
 */
public class FreeTree extends javax.swing.JPanel {
	
	/**
	 * layout used for nodes of this tree
	 */
	public final static GridBagConstraints NODE_GBC = new GridBagConstraints();          
	static{
		NODE_GBC.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		NODE_GBC.gridheight = java.awt.GridBagConstraints.RELATIVE;
		NODE_GBC.fill = java.awt.GridBagConstraints.HORIZONTAL;
		NODE_GBC.anchor = java.awt.GridBagConstraints.NORTH;
		NODE_GBC.insets = new java.awt.Insets(0, 0, 0, 0);
		NODE_GBC.weighty = 0.0;
		NODE_GBC.weightx = 1.0;
	}
	
	
	
	/** Creates new form FreeTree */
	public FreeTree() {
		initComponents();
	}
	
	/**
	 * add a new node
	 * @param title title of new node
	 * @param c contents of new node
	 */
	public void addNode(String title, JComponent c){
		add(new FreeNode(title, c), defaultConstraints());
		doTreeLayout();
	}
	
	/**
	 * set width of this <CODE>FreeTree</CODE>
	 */
	public void setWidth(final int width){
		Component[] nodes = getComponents();
		for(int i = 0; i < nodes.length; i++){
			FreeNode n = (FreeNode)nodes[i];
			JComponent contents = n.getContents();
			if(contents != null){
				java.awt.Dimension dim = contents.getSize();
				if(width < dim.width)//resize on grow is already handled by java
					contents.setSize(width, dim.height);//keep height the same!
			}
		}
		revalidate();
	}
	
	/**
	 * add the given node to this <CODE>FreeTree</CODE>
	 */
	public void addNode(FreeNode f){
		add(f, defaultConstraints());
		doTreeLayout();
	}
	
	/**
	 * arrange the nodes in this tree
	 */
	private void doTreeLayout(){
		//now adjust all the constraints
		Component[] nodes = getComponents();

		GridBagLayout layout = (GridBagLayout)getLayout();
		for(int i = 0; i < nodes.length; i++){
			FreeNode n = (FreeNode)nodes[i];
			GridBagConstraints gbc = (GridBagConstraints)layout.getConstraints(n);
			if(i == nodes.length-1)
				gbc.weighty=1.0;
			else
				gbc.weighty=0.0;
			layout.setConstraints(n, gbc);
		}
		revalidate();
	}
	
	/**
	 * expand/collapse all nodeds
	 * @param b iff <CODE>true</CODE> then exand all nodes
	 */
	public void toggleAll(boolean b){
		Component[] nodes = getComponents();
		for(int i = 0; i < nodes.length; i++){
			FreeNode n = (FreeNode)nodes[i];
			if(b)
				n.expand();
			else
				n.collapse();
		}
	}
	
	/**
	 * 
	 */
	public void toggleNodeAt(int i, boolean b){
		Component[] nodes = getComponents();
		if(i < 0 || i > nodes.length - 1)
			throw new AssertionError(this + "has no node component at index i=" + i);
		FreeNode n = (FreeNode)nodes[i];
		if(b)
			n.expand();
		else
			n.collapse();
	}
	
	/**
	 * provide a <CODE>GridBagConstraints</CODE> object for nodes of this tree
	 */
	private GridBagConstraints defaultConstraints(){
		GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        //gridBagConstraints.gridheight = 1;//java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
		return gridBagConstraints;
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.GridBagLayout());

    }
    // </editor-fold>//GEN-END:initComponents
	
	//See Design view of .form in NetBeans for more info. on the following
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
	
}
