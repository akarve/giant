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
 * City2.java
 *
 * Created on April 28, 2005, 3:11 PM
 */

package net.giantsystem.sf;


import javax.swing.*;
import java.util.ArrayList;
import java.awt.*;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author  karve
 */
public class City extends javax.swing.JPanel {
    //stuff to draw lines b/w fields
    public final static float LINE_WIDTH = 1.5f;
    public final static float LINE_WIDTH2 = 1.6f;
    public final static int CAP = BasicStroke.CAP_BUTT;
    public final static int JOIN = BasicStroke.JOIN_MITER;
    public final static float MITER_LIMIT = 10.0f;//swing default value
    public final static float[] DASH = {7.0f, 4.0f, 3.0f, 4.0f, 3.0f, 4.0f};
    public final static float DASH_PHASE = 0.0f;
    public final static float[] DASH2 = null;//{5.0f, 1.5f};
    public final static Stroke OUT_LINE = new BasicStroke(LINE_WIDTH, CAP, JOIN, MITER_LIMIT, DASH, DASH_PHASE);
    public final static Stroke IN_LINE = new BasicStroke(LINE_WIDTH2, CAP, JOIN, MITER_LIMIT, DASH2, DASH_PHASE);
    public final static java.awt.Insets FIELD_INSETS = new java.awt.Insets(3, 3, 3, 3);
    /*
    public final static Color C1 = new Color(153, 255, 255);
    public final static Color C2 = new Color(255, 153, 153);
    public final static Color C3 = new Color(255, 255, 153);
    public final static Color C4 = new Color(153, 255, 153);
    public final static Color[] COLORS = {C1, C2, C3, C4};
    */
    //some light colors...b/c sometimes lines between towers cross other fields,
    // giving the false impression of subfield-hood
    //...having different line colors per tower makes the likelihood of ambiguity lower
    public final static Color C1 = new Color(204, 255, 255); //blue
    public final static Color C2 = new Color(255, 255, 204); //yellow
    public final static Color C3 = new Color(204, 204, 204); //gray     //(204, 255, 204);    //green
    public final static Color C4 = new Color(255, 204, 204); //red      //(204, 255, 204);    //green
    public final static Color C5 = new Color(255, 255, 255); //white
    public final static Color[] COLORS = {C1, C2, C3, C4, C5};
    
    public final static String STOP = "linesLayer";
    public final static String ELL  = "..."; //ellipsis
    
    public final static int MAX_POPULATION = 50;
    
            //list of towers in left-to-right order
	private ArrayList towers = new ArrayList();
   
    private Set allFields = new HashSet();
    
    private int maxDegree = 1;  //maximum degree over all numberfields in this city
    private int population = 0;
    
    //used for drawing lines between numberfields
    protected Point getCenterLocation(Component c){
            
        Point p1 = new Point();
        Point p2 = new Point();
    
        //start with the relative location...
        c.getLocation(p1);
        //get to the middle of the fractionsLabel
        Dimension d = c.getSize();
        p1.x += d.width/2;
        p1.y += d.height/2;
        
        Component parent = c.getParent();
        //System.err.println("parent=" + parent);
        
        while(parent != null){
            parent.getLocation(p2);
            p1.x += p2.x;
            p1.y += p2.y;
            
            if(STOP.equals(parent.getName()))
                break;
            
            parent = parent.getParent();
        }
        return p1;
    }
    
    //don't think we need this method...delete it
    protected int numberOfTowers(){
        return towers.size();
    }
    
	public Set getFields(){
		return allFields;
	}
	
    private void drawLines2(Graphics g){
        java.awt.Graphics2D g2 = (Graphics2D) g;
        
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        
        final Point Q = getCenterLocation(QLabel);
        

        for(int i = 0; i < towers.size(); i++){         
            Tower t = (Tower) towers.get(i);
            g2.setColor(COLORS[i % COLORS.length]);
            boolean bottom = false;
            for(int j = 0; j < t.floors.size(); j++){
                if(j == t.floors.size() - 1)
                    bottom = true;
                Tower.Floor f = (Tower.Floor) t.getFloor(j);
                //iterate over all fields this floor
                Component[] fields = f.getComponents();        
                for(int k = 0; k < fields.length; k++){
                    NumberField a = (NumberField) fields[k];    //line origin
                    Point p1 = getCenterLocation(a.getCenterComponent());   //get center
                    
                    for(int l = 0; l < towers.size(); l++){
                        if(i==l)    //use different colors for drawing within/without towers
                            g2.setStroke(IN_LINE);
                        else
                            g2.setStroke(OUT_LINE);
                        Tower t2 = (Tower) towers.get(l);
                        //pen color changes according to destination tower
                        //that way all 'sets of subsets' are monochrome
                        //g2.setColor(COLORS[l % COLORS.length]);
                        floors:
                        for(int m = 0; m < t2.floors.size(); m++){
                            Tower.Floor f2 = (Tower.Floor) t2.getFloor(m);
                            //iterate over all fields this floor
                            boolean done = false;
                            Component[] fields2 = f2.getComponents();        
                            for(int n = 0; n < fields2.length; n++){
                                NumberField b = (NumberField) fields2[n];   //line destination
                                if(i==l & j==m){//we are within a floor{
                                    if(k == n);//do nothing (don't draw to yourself)
                                    else{//to do, maybe have different lines for isomorphy?
                                        Point p2 = getCenterLocation(b.getCenterComponent());   //get center
                                        g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                                        done = true;
                                    }
                                }else if(a.hasSubfield(b) && !a.subfieldHasProperSubfield(b)){                                    
                                    Point p2 = getCenterLocation(b.getCenterComponent());   //get center
                                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                                    done = true;
                                }
                            }
                            // go to the next tower; only need to draw to ONE floor per tower
                            if(done)
                                break floors; 
                        }
                    }
                    if(bottom && a.hasNoProperSubfields()){ //bottom of tower, no lines out of tower...
                        g2.setColor(COLORS[i % COLORS.length]);
                        g2.setStroke(IN_LINE);
                        g2.drawLine(p1.x, p1.y, Q.x, Q.y);//then draw to Q!
                    }
                }
            }
        }
    }
    
    /** Creates new form City */
    public City() {
        initComponents();
		towerLayer.setTransferHandler(new CityTransferHandler());
		towerLayer.setDropTarget(new java.awt.dnd.DropTarget(towerLayer, new java.awt.dnd.DropTargetAdapter(){
			public void drop(java.awt.dnd.DropTargetDropEvent dtde){
				javax.swing.TransferHandler handler = towerLayer.getTransferHandler();
				boolean problem = false;
				if(handler != null)
					if(handler.canImport(towerLayer, dtde.getCurrentDataFlavors()))
						handler.importData(towerLayer, dtde.getTransferable());
					else
						problem = true;
				else
					problem = true;//no handler; handler = null
				if(problem){
					dtde.rejectDrop();
					GiANT.gui.appendConsoleText("\nUnsupported drop to desktop.", true);
				}else
					dtde.acceptDrop(dtde.getDropAction());//i'm not sure about getAction()?
			}
		}));
    }
	
    //TODO handle deletion of numberfields
    /* tower policy (informal description of how towers are built:
     * height is a function of degree, and is consistent across towers
     * a numberfield is added to a tower iff it is a superfield of all fields having the next lowest degree
     * (and vice versa for subfield of all fields having next higher degree)
     */
    protected void addField(NumberField newguy){
        boolean fatal = false;
        if(population >= MAX_POPULATION){
            GiANT.gui.appendConsoleText("\nUnable to add numberfield; maximum population of " + MAX_POPULATION + " reached.",true);
            fatal = true;
        }else if(allFields.contains(newguy)){            
            GiANT.gui.appendConsoleText("\nUnable to add numberfield. It duplicates one already displayed.",true);
            fatal = true;
        }
        
        if(fatal)
            return;
        //we will need to know all subfields of this cat...
        determineSubfields(newguy);
        //System.err.println("entering addField()");
        //TODO do not allow duplicate (isomorphic) numberfields to be added

        boolean homeless = true;  //we have yet to find a place for newguy
        boolean redraw = false;
        //update maxDegree
        final int newDeg = newguy.getDegree();
        if(newDeg > maxDegree){
            maxDegree = newDeg;
            redraw = true;
        }
            
        //search all towers, left to right
        towers:
        for(int i=0; i < towers.size(); i++){
            Tower t = (Tower) towers.get(i);
            
            //boolean flags for existence of [proper] superfields / subfields
            boolean hasSuper = false;
            boolean hasSub = false;
            
            final int insert = t.findInsertionIndex(newguy);
            
            boolean hasEqual = false;
            Tower.Floor f = null;
            if(insert == t.getHeight()){
                //nothing to check; we are outside of the current tower array
            }else{
                f = t.getFloor(insert);
                if(f.getDegree() == newDeg)
                    hasEqual = true;
            }
            
            Tower.Floor above = null;
            Tower.Floor below = null;
            if(insert == 0){    //we are at the top of the tower
                hasSuper = true;  //super is C
                int fetch = 0;
                
                if(hasEqual)
                    fetch = 1;
                
                if(fetch >= t.getHeight()){
                    hasSub = true;  //there are no towers below, sub is Q
                }else{
                    below = t.getFloor(fetch);
                    if(below.allSubfieldsOf(newguy))
                        hasSub = true;
                }
            }else if(insert == t.getHeight()){  //we are at the very bottom of the tower
                hasSub = true;    //sub is Q itself
                
                above = t.getFloor(t.getHeight()-1);
                
                if(above.allSuperFieldsOf(newguy))
                    hasSuper = true;
            }else{  //we are somewhere WITHIN the tower
                if(hasEqual){
                    above = t.getFloor(insert-1);
                    if(insert == t.getHeight() - 1)
                        hasSub = true;  //sub is Q
                    else
                        below = t.getFloor(insert+1);
                }else{
                    above = t.getFloor(insert-1);
                    below = t.getFloor(insert);
                }
                
                if(above.allSuperFieldsOf(newguy))
                    hasSuper = true;
                if(hasSub){
                    //do nothing
                }else{//test for the same
                    if(below.allSubfieldsOf(newguy))
                        hasSub = true;
                }
            }
            
            if(hasSub && hasSuper){
                if(hasEqual)//kkl
                    if(!f.allSubfieldsOf(newguy))//could also use allSuperfieldsOf(), we need to make sure all fields this floor are isomorphic to newguy...
                        continue towers;         //
                homeless = false;
                t.add(insert, hasEqual, newguy);
                break towers;
            }
        }
        
        if(homeless){ //then build a new tower for newguy
            Tower newT = newTower();
            newT.add(0,false,newguy);
        }
        
        population++;   //one more citizen!
        allFields.add(newguy); //add him to the set of all fields
        //if(redraw) //OPTIM
        redraw();
        GiANT.gui.repaint();
        //System.err.println("exiting addField()");
    }
    
    //of all numberfield current in the city, which are subfields?
    private void determineSubfields(NumberField n){
        //Progress Bar
        GiANT.gui.appendConsoleText("\nDetermining sub- and superfield relations for new field" + ELL, false);        
        GiANT.gui.showProgressBar(true);

        //DEBUG
        //System.err.println("subfields start for " + n.getName());
        int i = 0;
        final float size = (float) allFields.size();
        final int max = GiANT.gui.getProgressMax();
        
        Iterator it = allFields.iterator();
        while(it.hasNext()){
            //Progress Bar
            float index = (float) i++;
            int progress = (int)(max*(index/size));
            //DEBUG
            //System.err.println("\tprogress=" + progress);
            GiANT.gui.setProgress(progress);
            
            NumberField test = (NumberField)it.next();
            
            if(n.hasSubfield(test) || test.hasSubfield(n)){
                //do nothing
            }else if(GiANT.gui.kash.isSubfield(test,n)){
                n.addSubfield(test); //also adds all subfields of test to subfields of this
                if(test.getDegree() == n.getDegree()){
                    test.addSubfield(n);
                }
            }else if(GiANT.gui.kash.isSubfield(n,test)){
                test.addSubfield(n); //also adds all subfields of n to subfields of this
            }
        }
        //Progress Bar
        GiANT.gui.showProgressBar(false);
        GiANT.gui.appendConsoleText("done.\n",false);
        //System.err.println("subfields end for " + n.getName());
    }
    
    private void redraw(){
        towerLayer.removeAll(); //clear the view
        //OPTIM...you don't need to removeAll...just use setBounds and then revalidate! or?

        for(int i=0; i < towers.size(); i++){
            Tower t = (Tower) towers.get(i);
            for(int j=0; j < t.floors.size(); j++){
                Tower.Floor f = t.getFloor(j);
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridwidth = 1;//f.getComponents().length;
                gbc.gridheight = 1;
                gbc.fill = java.awt.GridBagConstraints.NONE;//NONE
                gbc.anchor = java.awt.GridBagConstraints.CENTER;
                gbc.weightx = 1.0;//0.
                gbc.weighty = 1.0;//0.
                gbc.insets = FIELD_INSETS;
                gbc.gridx = i;
                gbc.gridy = maxDegree - f.getDegree();
                towerLayer.add(f, gbc);
            }
        }        
        towerLayer.revalidate();
    }
    
    // add a tower to the list "towers"
    private Tower newTower(){
        Tower t = new Tower();
        towers.add(t);
        return t;
    }
    
    private class Tower {

        //i.e. from higher degree to lower degree
        private ArrayList floors = new java.util.ArrayList();
        
        //assuming n goes in this tower, where does it belong (which floor)
        //by virtue of this method the floors array is always sorted top floors to bottom ones,
        //OPTIM you can do a binary search here
        private int findInsertionIndex(NumberField n){
            int i=0;
            for(i=0; i < floors.size(); i++){
                Floor f = (Floor) floors.get(i);
                if(f.getDegree() <= n.getDegree())
                    break;
            }
            return i;
        }
        
        private int getHeight(){
            return floors.size();
        }
        
        private Floor getFloor(int i){
            return (Floor) floors.get(i);
        }
        
        private void add(int i, boolean eql, NumberField n){
            Floor f;
            if(eql){
                f = (Floor) floors.get(i);
                f.add(n);
            }else{
                f = new Floor(n);
                f.add(n);
                floors.add(i,f);
            }
         }
        
        private class Floor extends JPanel {
            private int degree = 1;

            private Floor(NumberField n){
                super();
                //DEBUG use
                /*
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        floorMouseEntered(evt);
                    }
                });*/
                setLayout(new BoxLayout(this, javax.swing.BoxLayout.X_AXIS));
                setFocusable(false);
                setOpaque(false);

                degree = n.getDegree();
                add(n);
            }
            
            //DEBUG fxn
            private void floorMouseEntered(java.awt.event.MouseEvent evt){
                GridBagLayout gbl = (GridBagLayout)towerLayer.getLayout();
                GridBagConstraints c = gbl.getConstraints(this);
                
                System.err.println("FLOOR:\n" + "\tdeg=" + getDegree() + "\tx=" + c.gridx + "\ty=" + c.gridy + "\n\n*****\n");
            }

            private int getDegree(){
                return degree;
            }

            public boolean allSubfieldsOf(NumberField n){

                Component[] all = getComponents();
                for(int i = 0; i < all.length; i++){
                    NumberField maybeSub = (NumberField) all[i];
                    
                    if(!n.hasSubfield(maybeSub))
                        return false;
                }
                return true;
            }

            public boolean allSuperFieldsOf(NumberField n){
                Component[] all = getComponents();
                for(int i = 0; i < all.length; i++){
                    NumberField maybeSuper = (NumberField) all[i];
                    //DEBUG:
                    //System.err.println("\tSuperfield Test:" + maybeSuper.getPoly() + ", " + n.getPoly());
                    if(!maybeSuper.hasSubfield(n))
                        return false;
                }
                return true;
            }
            
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

        linesLayer = new JPanel(){
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                drawLines2(g);
            }
        };
        CPanel = new javax.swing.JPanel();
        CLabel = new javax.swing.JLabel();
        towerLayer = new javax.swing.JPanel();
        ZQPanel = new javax.swing.JPanel();
        QLabel = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        setFocusable(false);
        linesLayer.setLayout(new java.awt.BorderLayout());

        linesLayer.setFocusable(false);
        linesLayer.setName("linesLayer");
        linesLayer.setOpaque(false);
        CPanel.setLayout(new java.awt.GridBagLayout());

        CPanel.setFocusable(false);
        CPanel.setOpaque(false);
        CLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        CLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/C.png")));
        CLabel.setToolTipText("Complex Field");
        CLabel.setAlignmentX(0.5F);
        CLabel.setAlignmentY(0.0F);
        CLabel.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        CPanel.add(CLabel, gridBagConstraints);

        linesLayer.add(CPanel, java.awt.BorderLayout.NORTH);

        towerLayer.setLayout(new java.awt.GridBagLayout());

        towerLayer.setFocusable(false);
        towerLayer.setName("towerLayer");
        towerLayer.setOpaque(false);
        linesLayer.add(towerLayer, java.awt.BorderLayout.CENTER);

        ZQPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        ZQPanel.setFocusable(false);
        ZQPanel.setOpaque(false);
        QLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Q.png")));
        QLabel.setToolTipText("Rational Field");
        QLabel.setFocusable(false);
        QLabel.setIconTextGap(0);
        ZQPanel.add(QLabel);

        linesLayer.add(ZQPanel, java.awt.BorderLayout.SOUTH);

        add(linesLayer, java.awt.BorderLayout.CENTER);

    }
    // </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel CLabel;
    private javax.swing.JPanel CPanel;
    private javax.swing.JLabel QLabel;
    private javax.swing.JPanel ZQPanel;
    private javax.swing.JPanel linesLayer;
    private javax.swing.JPanel towerLayer;
    // End of variables declaration//GEN-END:variables
    
}
