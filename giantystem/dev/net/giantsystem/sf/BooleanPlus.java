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
 * Similar to the java Boolean type but adds a third state,
 * <CODE>UNKNOWN</CODE>. Contains support for rendering in JTables. Used, 
 * for example, to indicate the principality of an ideal.
 * @author karve
 */
public class BooleanPlus{
    /**
     * The BooleanPlus value <CODE>TRUE</CODE>
     */
    public final static BooleanPlus TRUE = new BooleanPlus("TRUE"){
        public Boolean getBoolean(){
            return Boolean.TRUE;
        }
    };
    /**
     * The BooleanPlus value <CODE>FALSE</CODE>
     */
    public final static BooleanPlus FALSE = new BooleanPlus("FALSE");
    /**
     * The string to display in JTables when value is <CODE>UNKNOWN</CODE>.
     */
    public final static String UNKNOWN_STRING = "?";
    /**
     * The tooltip used in JTables when value is <CODE>UNKNOWN</CODE>
     */
    public final static String UNKNOWN_TOOLTIP ="Double-click to calculate, or compute "+
												"the class group below. ";
    /**
     * The BooleanPlus value <CODE>UNKNOWN</CODE>
     */
    public final static BooleanPlus UNKNOWN = new BooleanPlus("UNKNOWN"){
        public String toString(){
            return UNKNOWN_STRING;
        }
        public String getToolTip(){
            return UNKNOWN_TOOLTIP;
        }
    
    };
    /**
     * The TableCellRenderer used for BooleanPlus objects
     */
    public final static TableCellRenderer RENDERER = new JTable().getDefaultRenderer(Boolean.class);
    
    /**
     * The name (type) of the BooleanPlus variable.
     * By design there are only three possible types 
     * enforced by a private constructor.
     */
    private final String name;

    /**
     * Private constructor not meant to be used other
     * than to create the three primitive types.
     * @param name The name (type) of the BooleanPlus
     * to be created
     */
    private BooleanPlus(String name) {
        this.name = name;
    }
    
    /**
     * Overrides Object.toString()
     * @return Always returns the empty string ""
     */
    public String toString(){
        return "";
    }
    
    /**
     * Return the tooltip String
     * @return Tooltip String
     */
    public String getToolTip(){
        return null;
    }
    
    /**
     * Used by the CellRenderer to get a blank 
     * checkbox as you would for Boolean.FALSE
     * @return always returns Boolean.FALSE
     */
    public Boolean getBoolean(){
        return Boolean.FALSE;
    }
    
    /**
     * Extends <CODE>javax.swing.table.DefaultTableCellRenderer</CODE> to implement a
     * <CODE>TableCellRenderer</CODE> for <CODE>BooleanPlus</CODE>
     */
    public static class CellRenderer extends DefaultTableCellRenderer {

        /**
         * Calls the no-arg constructor for DefaultTableCellRenderer
         */
        public CellRenderer() { super(); }
        /*
        public void setValue(Object value) {
            setText(value.toString());
            //super.setValue(value);
                //setText("p");
        }*/
               
        /**
         * Overrides <CODE>DefaultTableCellRenderer</CODE>'s method to give us the desired 
         * behavior for a <CODE>BoolenPlus</CODE> <CODE>TableCellRenderer</CODE>
         */
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