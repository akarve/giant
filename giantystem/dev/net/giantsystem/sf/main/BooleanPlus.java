/*
 * BooleanPlus.java
 *
 * Created on May 23, 2005, 9:32 PM
 */

package net.giantsystem.sf;

import javax.swing.table.*;

import javax.swing.JTable;
import javax.swing.JCheckBox;
import java.awt.Component;

/**
 *
 * @author karve
 */
public class BooleanPlus{
    public final static BooleanPlus TRUE = new BooleanPlus("TRUE"){
        public Boolean getBoolean(){
            return Boolean.TRUE;
        }
    };
    public final static BooleanPlus FALSE = new BooleanPlus("FALSE");
    public final static String UNKNOWN_STRING = "?";
    public final static String UNKNOWN_TOOLTIP ="Double-click to calculate, or compute "+
												"the class group below. ";
    public final static BooleanPlus UNKNOWN = new BooleanPlus("UNKNOWN"){
        public String toString(){
            return UNKNOWN_STRING;
        }
        public String getToolTip(){
            return UNKNOWN_TOOLTIP;
        }
    
    };
    public final static TableCellRenderer RENDERER = new JTable().getDefaultRenderer(Boolean.class);
    
    private final String name;

    /** Creates a new instance of BooleanPlus */
    private BooleanPlus(String name) {
        this.name = name;
    }
    
    public String toString(){
        return "";
    }
    
    public String getToolTip(){
        return null;
    }
    
    public Boolean getBoolean(){
        return Boolean.FALSE;
    }
    
    public static class CellRenderer extends DefaultTableCellRenderer {

        public CellRenderer() { super(); }
        /*
        public void setValue(Object value) {
            setText(value.toString());
            //super.setValue(value);
                //setText("p");
        }*/
               
        public Component getTableCellRendererComponent(JTable table,
                                               Object value,
                                               boolean isSelected,
                                               boolean hasFocus,
                                               int row,
                                               int column){
            
            String text = null;
            BooleanPlus bp = (BooleanPlus)value;
            //setToolTipText(String.valueOf(row));
            //return new TriCheckBox();
            //OPTIM faster ways to do this?
            JCheckBox c = (JCheckBox)RENDERER.getTableCellRendererComponent(table, bp.getBoolean(), isSelected, hasFocus, row, column);
            c.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            c.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            c.setIconTextGap(0);
            c.setText(bp.toString());
            c.setToolTipText(bp.getToolTip());
            
            return c;
        }
                                               
    }
}