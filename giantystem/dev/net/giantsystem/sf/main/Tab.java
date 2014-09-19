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
 * VarTab.java
 *
 * Created on May 19, 2005, 1:39 PM
 */

package net.giantsystem.sf;
import	javax.swing.TransferHandler;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author karve
 */
public class Tab extends javax.swing.JScrollPane {
    public final static String[] NO_ARGS = new String[]{};
	private final static List allVariables = new ArrayList();
    private final String tabTitle;
    private final int tabIndex;
    private final Variable content;
    private final String nameStem;
    private final VarTable table;
    private final Inspector inspector;
	private final int mnemonic;
    private int nameIndex = 0;

    
    
    public Tab(Inspector i, Variable v, String title, String stem, int mn, int index){
        super();
        tabTitle = title;
        nameStem = stem;
        tabIndex = index;
        content = v;
		mnemonic = mn;
        inspector = i;
        table = content.makeVarTable(this);
        setViewportView(table);
    }
	
	public static Object[] getVariables(){
		return allVariables.toArray();
	}
    
    public void updateSelectionDetail(){
        //System.err.println("UPDATE");
        int [] rows = table.getSelectedRows();
        java.awt.Component[] details = new java.awt.Component[rows.length];
        for(int i = 0; i < rows.length; i++)
            details[i] = table.getVariableAt(rows[i]).getDetail();
        
        inspector.showDetails(details);
    }
    
	public void setTransferHandler(TransferHandler t){
		table.setTransferHandler(t);
	}
	
    public VarTable getTable(){
        return table;
    }
    
    public Inspector getInspector(){
        return inspector;
    }
    
    public String getAutoName(){
        return inspector.getField().getName() + nameStem + nameIndex;
    }
    
    public String getTitle(){
        return tabTitle;
    }
    
    public int getTabIndex(){
        return tabIndex;
    }
    
    public boolean belongs(String exp){
        return content.memberP(exp);
    }
    /*
    public boolean addNewVariable(String value){
        return addNewVariable(getAutoName(), value);
    }*/
    
    public boolean addNewVariable(String name, String value){
		Variable v;
		v = content.makeVariable(name, value);

        if(v == null){
            return false;
        }else{
			v.setTab(this);
			if(!v.moveTo(this.getInspector().getField())){
				//sometimes if we move from a too-high degree into a relative
				//ground-field GiANT hangs (doesn't do so in debug mode...)
				//for instance make the number field x^2-3 with relative extension
				//x^3-11, and move p^5 in the last field back to the first
				GiANT.gui.appendConsoleText("Unable to move element (degree may be too high).", true);
				return false;
			}
			v.calcInvariants();
            nameIndex++;
            table.addRow(v.toRow());
            table.changeSelection(table.getRowCount()-1, 0, false, false);
			allVariables.add(v);
            return true;
        }
    }
	
	public boolean addNewVariable(String rhs){
		return addNewVariable(getAutoName(), rhs);
	}
	
	public int getMnemonic(){
		return mnemonic;
	}
	
	public void updateVariables(){
		final int rows = table.getRowCount();
		for(int row = 0; row < rows; row++){
			Variable v = table.getVariableAt(row);
			boolean update = v.update();
			if(update){
				Object[] rowInfo = v.toRow();
				for(int col = 0; col < rowInfo.length; col++)
					table.setValueAt(rowInfo[col], row, col);
			}
		}
	}
    
    public boolean operation(String value){
		return addNewVariable(getAutoName(), value);
    }    
}