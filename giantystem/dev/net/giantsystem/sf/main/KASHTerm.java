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
 * Terminal2.java
 *
 * Created on April 27, 2005, 10:17 AM
 */

package net.giantsystem.sf;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.regex.*;

/**
 *
 * @author  karve
 */
public class KASHTerm extends javax.swing.JInternalFrame {
    //private String KASH_DIR;// = "/Users/karve/Desktop/PROJECTS/KASH/KASH_2.5";
    //public final static String KASH_DIR = "/net/kant/V4/bin/";
    //public final static String[] START_KASH = new String[]{KASH_DIR + "/kash", "giant.kash"};
    public final static String KASH_PROMPT = "kash> ";
    public final static String KASH_PROMPT_B = "> ";
    public final static String DETAILS = " (View/KASH for details.) ";
    
    //sometimes we want to find the kash prompt
        //turn on DOTALL with (?s)
    public final static Pattern KASH_P = Pattern.compile("(?s)(.*)" + KASH_PROMPT);
    public final static Pattern ERROR = Pattern.compile("(?i)error");
    public final static Pattern TRUE = Pattern.compile("(?i)true");
    public final static Pattern FALSE = Pattern.compile("(?i)false");
    public final static Pattern GEN_POLY = Pattern.compile("Generating polynomial:");
    public static final Pattern SYNTAX_ERR = Pattern.compile("Syntax error.*");//to find error messages   
    public static final Pattern QUIT = Pattern.compile("\\s*quit\\s*;\\s*");
    
    public final static String[] SSH_MACH = {"ssh", "mach.math.tu-berlin.de", " -lkarve"};
    
    public final static int UP_ARROW = java.awt.event.KeyEvent.VK_UP;
    public final static int DOWN_ARROW = java.awt.event.KeyEvent.VK_DOWN;
    
    public final static int GALOIS_LIMIT = 2;

    
    public final static String EQ = " := ";
    public final static String END  = ";";
    public final static String COMMA = ", ";
    public final static String JOIN = ", ";
    public final static String HUH = "?" + DETAILS;
    
    //Function names
    public final static String NEW_NF = "Order";
    public final static String POLY_DEG = "PolyDeg";
    public final static String ORDER_DEG = "OrderDegAbs";
    public final static String BASIS = "Basis";
    public final static String DISC = "OrderDisc";
    public final static String SIG = "OrderSig";
    public final static String SUBFLD = "OrderIsSubfield";
    public final static String GALOIS_GROUP = "Galois";
    public static final String POLY_ALG = "PolyAlg";
    public static final String POLY = "Poly";
    public static final String TRACE = "Trace";
    public static final String NORM = "Norm";
    public static final String IDEAL_GENERATORS = "GiantIdealGenerators";
    public static final String MIN_POLY = "MinPoly";
    public static final String POLY_DISC = "PolyDisc";
	public static final String POLY_MOVE = "PolyMove";
    public static final String REDUCIBLE_P = "PolyIsIrreducible";
    public static final String PRIME_P = "IdealIsPrime";
    public static final String PRINCIPAL = "GiantIdealIsPrincipal";
	public static final String PRINCIPAL_CL = "GiantIdealIsPrincipalCl";
    public static final String INTEGRAL_P = "IdealIsIntegral";
	public static final String CLASS_REP = "IdealClassRep";
    public static final String POLY_P = "IsPoly";
    public static final String ELT_P = "IsElt";
	public static final String ELT_MOVE = "EltMove";
    public static final String IDEAL_P = "IsIdeal";
	public static final String IDEAL_FACTOR = "IdealFactor";
    public static final String IDEAL_CONSTRUCTOR = "Ideal";
	public static final String IDEAL_MOVE = "IdealMove";
    public static final String TORSION = "OrderTorsionUnit";
    public static final String TORSION_RANK = "OrderTorsionUnitRank";
    public static final String FUND_UNITS = "GiantOrderUnitsFund";
	public static final String CLASS_GROUP = "OrderClassGroup";
	public static final String CLASS_FACTORS2 = "GiantClassGroupFactors";//WIHTOUT orders	
	public static final String CLASS_FACTORS = "OrderClassGroupCyclicFactors";//WITH orders
    public static final String INTEGRAL_P_ELT = "EltIsIntegral";
    public final static String MAX_ORDER = "OrderMaximal";
    public final static String REG = "OrderReg";
	public final static String ABS_ORDER = "OrderAbs";
	public final static String UNIT_REP = "EltUnitDecompose";
        //operators
    public final static String PLUS = " + ";
    public final static String MINUS = " - ";
    public final static String TIMES = " * ";
    public final static String DIVIDE = " / ";
    
    private IO io;//= new IO();
    private ArrayList commandHistory = new ArrayList();
    private boolean first = true;
        
	//args is the arg[] vector from main
    public KASHTerm(String[] args) {
		String kashDir = args[0];//the user tells us where kash lives
		io = new IO(kashDir);
        initComponents();
    }
	
    public static String enclose(String s){
        return "(" + s + ")";
    }
	
    public static String funCall(String fname, String[] args){
        if(fname == null)
            return join(args);
        return fname + enclose(join(args));
    }
    
    public Response funExec(String fname, String[]args){
        return tell(funCall(fname, args) + END);
    }
    
    public String funExecToString(String fname, String[]args){
        Response r = tell(funCall(fname, args) + END);
        if(!r.error())
            return r.getOutput();
        else
            return HUH;
    }
    
    public String basis(String nm, NumberField n){
        return assignToFunCall(nm, BASIS, new String[]{n.getName()});
    }
    
    public String integralBasis(String nm, NumberField n){
        return assignToFunCall(nm, BASIS, new String[]{n.getMaxOrderName()});
    }    
    
    public boolean integral(Element e){
        Response r = funExec(INTEGRAL_P_ELT, new String[]{e.getName()});
        return trueString(r.getOutput());
    }
    
    public String disc(String nm, NumberField n){
        return assignToFunCall(nm, DISC, new String[]{n.getName()});
    }
    
    public String classRep(String nm, Ideal i){
        return assignToFunCall(nm, CLASS_REP, new String[]{i.getName(), "\"gen\""});
    }
	
	public String maxOrderDisc(NumberField n){
        return assignToFunCall("temp", DISC, new String[]{n.getMaxOrderName()});
    }
	
    public String unitRep(String nm, Element e, NumberField f){
		//if we don't do the move first, EltUnitDecompose may complain that the element
		// is not integral
		String move = funCall(ELT_MOVE, new String[]{e.getName(), f.getMaxOrderName()});
        return assignToFunCall(nm, UNIT_REP, new String[]{move, "\"expons\""});
    }
	
    public String regulator(String nm, NumberField n){
        return assignToFunCall(nm, REG, new String[]{n.getMaxOrderName()});
    }
    //OPTIM you allocate tons of String[] arrays...use static one
    public String torsionRank(NumberField n){
        return assignToFunCall("temp", TORSION_RANK, new String[]{n.getMaxOrderName()});
    }    
    
    public String disc(String nm, Polynomial p){
        return assignToFunCall(nm, POLY_DISC, new String[]{p.getName()});
    }
    
	public String fundUnits(String nm, NumberField n){
        return assignToFunCall(nm, FUND_UNITS, new String[]{n.getMaxOrderName(), n.getName()});
	}
    
	public String classGroup(String nm, NumberField n){
		return assignToFunCall(nm, CLASS_GROUP, new String[] {n.getMaxOrderName()});
	}    
	
	public String classGroupFactors(String nm, NumberField n){
		String[] arg = new String[] {n.getMaxOrderName()};
		assignToFunCall(nm, CLASS_FACTORS2, arg);//class group factors w/o orders
		return funExecToString(CLASS_FACTORS, arg);//WITH orders
	}  
    public String torsion(String nm, NumberField n){
        return assignToFunCall(nm, TORSION, new String[]{n.getMaxOrderName()});
    }
    
    public String generators(String nm, Ideal idl){
        return assignToFunCall(nm, IDEAL_GENERATORS, new String[]{idl.getName()});
    }
    
    public String primElt(String nm, String val){
        return assignToFunCall(nm, null, new String[]{val});
    }
    
    public String degree(String nm, NumberField n){
        return assignToFunCall(nm, ORDER_DEG, new String[]{n.getName()});
    }
    
    public String sig(String nm, NumberField n){
        return assignToFunCall(nm, SIG, new String[]{n.getName()});
    }
    
    public String norm(String nm, Variable v){
        return assignToFunCall(nm, NORM, new String[]{v.getName()});
    }
	
	
    
    public String maximalOrder(NumberField n){
        return assignToFunCall(n.getMaxOrderName(), MAX_ORDER, new String[]{n.getName()});
    }
    
    public String trace(String nm, Variable v){
        return assignToFunCall(nm, TRACE, new String[]{v.getName()});
    }
    
    /**
     * Return the integral basis representation of <CODE>e</CODE> in <CODE>n</CODE>
     * @param nm the name to be given to the integral basis rep.
     * @param e the element whose representation we will return
     * @param n the number field whose integral basis we are
     * concerned with
     * @return 
     */
    public String integralBasis(String nm, Element e, NumberField n){
        return assignToFunCall(nm, ELT_MOVE, new String[]{e.getName(), n.getMaxOrderName()});
    }
	
    public String minPoly(String nm, Element e){
        return assignToFunCall(nm, MIN_POLY, new String[]{e.getName()});
    }
    
    public String principal(String nm, Ideal i){
        return assignToFunCall(nm, PRINCIPAL, new String[]{i.getName()});
    }
	
	public String principalFromClassGroup(String nm, Ideal i){
        return assignToFunCall(nm, PRINCIPAL_CL, new String[]{i.getName()});
    }
    
    //TO DO, pretty print string, add subscripts...?
    protected String galoisGroup(String nm, NumberField n){
        return assignToFunCall(nm, GALOIS_GROUP, new String[]{n.getName()});
    }
    
    
    public String assignToFunCall(String var, String func, String[] args){
        String f = funCall(func, args);
        Response r = assign(var, f);
        if(!r.error())
            return r.getOutput();
        else
			return HUH;
    }
    
        
    public String polyRingVar(String nm, NumberField n){
        String[] args = {n.getName()};
        String alg = funCall(POLY_ALG, args);   //univariate ring over  n
        
        String[] args2 = {alg, "[1,0]"};        //polynomial of deg=1, no constant term
        return assignToFunCall(nm, POLY, args2);
    }
    
    public boolean isPoly(String exp){
        Response r = funExec(POLY_P, new String[]{exp});
        if(!r.error())
            return trueString(r.getOutput());
        else return false;
    }
    
    public boolean isElement(String exp){
        Response r = funExec(ELT_P, new String[]{exp});
        if(!r.error())
            return trueString(r.getOutput());
        else return false;
    }
    
    public boolean isIdeal(String exp){
        Response r = funExec(IDEAL_P, new String[]{exp});
        if(!r.error())
            return trueString(r.getOutput());
        else return false;
    }    
    
    
    public Response eval(String varName){
        return tell(varName + END);
    }
	
	public String evalToString(String eval){
		Response r = eval(eval);
		if(r.error())
			return HUH;
		else
			return r.getOutput();
	}
	
	
    
    public static String join(String[] arr){
        return join(arr, JOIN);
    }
    
    public static String join(String[] arr, String j){
        StringBuffer buff = new StringBuffer();
        
        int i = 0;
        for(i = 0; i < arr.length-1; i++)
            buff.append(arr[i] + j);
        //add the last element WITHOUT JOIN
        buff.append(arr[i]);
        
        return buff.toString();        
    }

    private int histCursor = 0; //indexes into the above arrow when using arrows for command history
    
    //we write to the terminal
    protected void appendText(String msg){
        textDisplayArea.append(msg);
        textDisplayArea.setCaretPosition(textDisplayArea.getDocument().getLength());
    }
    
    protected void setInputEnabled(boolean flag){
        textInputField.setEnabled(flag);
    }
    //@deprecated
    public static boolean hasKASHPrompt(String s){
        if(s != null){
        //TO DO...there can be problems with this...e.g. with answers containing >
            return (s.endsWith(KASH_PROMPT) || s.endsWith(KASH_PROMPT_B));
        }else{
        // null string is not a KASH Prompt
            return false;
        }
    }
    
    public static boolean trueString(String s){
        s = s.trim();
        Matcher m = TRUE.matcher(s);
        boolean t = m.find();
        
        m = FALSE.matcher(s);
        boolean f = m.find();
        
        if(t && f)  //this should be done with execptions!!!
            throw new AssertionError("String contains 'true' and 'false'");
        else if(!t && !f)
            throw new AssertionError("String contains neither 'true' nor 'false'");
        else
            return t;
    }
    
    protected Response listen(){
        //as soon as a new listen is issued the current error is irrelevant
        //GiANT.gui.appendConsoleText("\n",false); 
        return io.listen();

    }

    public void close(){
        tell("quit;");

        try{
            io.kashIn.write(("exit\n").toCharArray());
            io.kashIn.flush();
        }catch(Exception e){
            System.err.println("Problem closing shell:  " + e);
        }   
        
        try{
            io.kashOut.close();
            io.kashErr.close();
        }catch(Exception e){
            System.err.println("Error closing streams:  " + e);
        }
        

    }
   
    protected Response newNumberField(String name, String poly){
        String[] args = {poly};
        Response r = tell(name + EQ + funCall(NEW_NF, args) + END);
        Matcher m = GEN_POLY.matcher(r.getOutput());
        String parse = m.replaceAll(""); //get rid of beginning part; e.g. >>>Generating polynomial:<<< x^3 + 2*x^2 - 2*x - 19
        r.parsed = Graphic.removeSpace(parse);
        
        return r;
    }
	
	protected Response relativeExtension(String name, String poly){
		String order = funCall(NEW_NF, new String[]{poly});
		//conver the relative order into an absolute one
		String absOrder = funCall(ABS_ORDER,  new String[]{order});
		Response r = tell(name + EQ + absOrder + END);
        Matcher m = GEN_POLY.matcher(r.getOutput());
        String parse = m.replaceAll(""); //get rid of beginning part; e.g. >>>Generating polynomial:<<< x^3 + 2*x^2 - 2*x - 19
        r.parsed = Graphic.removeSpace(parse);
        
        return r;
    }
    
    protected int getDegree(String poly){
        String[] args = {poly};
        Response r = tell(funCall(POLY_DEG, args) + END);
        if(!r.error()){
            String answer = r.getOutput();
            return Integer.parseInt(answer);
            //do nothing
        }else{
            return -1;
        }
    }
    
    public Response assign(String var, String value){
        return tell(var + EQ + value + END);
    }
    /*
    public String assignReturnsString(String var, String value){
        Response r = assign(var, value);
        if(!r.error())
            return r.getOutput();
        else
            return HUH;
    }*/

    
    public boolean irreducible(Polynomial p){
        String[] args = {p.getName()};
        Response r = tell(funCall(REDUCIBLE_P, args) + END);
        return  trueString(r.getOutput());
    }
    
	
    public boolean prime(Ideal i){
        String[] args = {i.getName()};
        Response r = tell(funCall(PRIME_P, args) + END);
        return  trueString(r.getOutput());
    }
    
	public String factor(String nm, Ideal i){
		return assignToFunCall(nm, IDEAL_FACTOR, new String[]{i.getName()});
	}
	/*
    public BooleanPlus prime(Ideal i){
        String[] args = {i.getName()};
        Response r = tell(funCall(PRIME_P, args) + END);
        BooleanPlus answer = BooleanPlus.UNKNOWN;
        try{
            boolean ans = trueString(r.getOutput());
            if(ans)
                answer = BooleanPlus.TRUE;
            else
                answer = BooleanPlus.FALSE;
        }catch(AssertionError e){
            
        }finally{
            return answer;
        }
    }*/
        
    public boolean integral(Ideal i){
        String[] args = {i.getName()};
        Response r = funExec(INTEGRAL_P, args);
        return  trueString(r.getOutput());
    }
    /*
    private void renderText(Graphics g){
        //mac os x, why doesn't this work?
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        //textDisplayArea.super.paintComponent(g);
    }*/
    
    //@return is n1 a subfield of n2?
    protected boolean isSubfield(NumberField n1, NumberField n2){
        String[] args = {n1.getName(), n2.getName()};
        Response r = tell(funCall(SUBFLD, args) + END);
        if(!r.error()){
            return trueString(r.getOutput());
        }else{
            throw new AssertionError("Error testing subfields");
        }
    }
    //split a KASH-style list of the form [x,y,z...], where all entries
    //are either lists or rational numbers!
    public static String[] splitList(String list){

        String[] split = new String[0];
        String elts = Graphic.removeSpace(list);//make life easier
        if(elts.length() <= 2)//handle empty lists
            if(elts.length() < 2)//should have at least two chars:  [ and ]
                throw new AssertionError("malformed list:  " + list);
            else
                return split;//list is empty, nothing to split
        elts = elts.substring(1, elts.length());//strip opening '['
        ArrayList temp = new ArrayList();
        
        doSplit:
        while(!elts.equals("]")){//the closing brace of the list we are splitting
            int border, start=0;
            if(elts.startsWith("["))//starts with a list
                start = elts.indexOf("]");//find end of list
            border = elts.indexOf(",", start);//find the end of current element
            if(border == -1){//end of list
                temp.add(elts.substring(0, elts.length()-1));//-1 = forget the last ] in there
                break doSplit;
            }else{
                temp.add(elts.substring(0, border));
                elts = elts.substring(border+1, elts.length());//+1 to step over ','
            }
        }
        return (String[])temp.toArray(split);
    }
    
    //@return is n1 a superfield of n2?
    protected boolean isSuperfield(NumberField n1, NumberField n2){
        return isSubfield(n2,n1);
    }
    /*
    protected boolean isomorphic(NumberField n1, NumberField n2){
        return isSuperfield(n1, n2) && isSubfield(n1,n2);
    }*/
    
    /**
     * Send cmd to the current KASH environment
     * @param cmd the command string to send to KASH; same as if using KASH from a normal shell
     * @param f the calling function (function whose answer we are listening for)
     * @return void
     */
    protected Response tell(String cmd){

        if(!cmd.endsWith(END)){
            cmd += END;
            System.err.println(cmd + "\n^Received KASH command without terminator ';'");
        }
        Matcher m = QUIT.matcher(cmd);
        if(m.matches()){
            GiANT.gui.appendConsoleText("\nPlease quit GiANT using the main menu.", true);
            //GiANT.gui.setInputEnabled(true);
            return null;
        }else{
            //echo user's own input
            appendText(cmd + "\n");
            try{
                io.kashIn.write((cmd + "\n").toCharArray());
                io.kashIn.flush();
            }catch(Exception e){
                System.err.println("Problem sending text to KASH:  " + e);
            }
            // start listening for a response from KASH
            return io.listen();
        }
    }                    
    
    /**
     * class IO
     * Provides a java interface to UNIX shell process (e.g. KASH) I/O. 
     */
    class IO{
        //public final static String SHELL = "bash";
        public static final long SNOOZE = 100;
        public static final long NAP = 250;

        private Process kash;
        private Reader kashOut;
        private BufferedReader kashErr;
        private OutputStreamWriter kashIn;

        private IO(String kashDir){
			    String[] startKASH = new String[]{kashDir + "/kash", "giant.kash"};
            try {
                // ask the Runtime to start KASH
                kash = Runtime.getRuntime().exec(startKASH, null, new File(kashDir));
                //kash = Runtime.getRuntime().exec(SSH_MACH, null, new File(KASH_DIR));
            }catch (Exception e){
                System.err.println("Unable to start KASH via Runtime:  " + e);
                //System.exit(-1);
            }

            try{
                //KASH OUTPUT Streams
                //buffering kashOut causes problems; the KASH prompt, e.g., never shows up
                kashOut = new InputStreamReader(kash.getInputStream()); 
                kashErr = new BufferedReader(new InputStreamReader(kash.getErrorStream()));             
            }catch(Exception e){
                System.err.println("Unable to get Input/Error stream from KASH:  " + e);
                System.exit(-1);
            }

            try{
                kashIn = new OutputStreamWriter(kash.getOutputStream());
            }catch(Exception e){
                System.err.println("Unable to open OStream to KASH:  " + e);
                System.exit(-1);
            } 
        }
        
        private Response listen(){
            GiANT.gui.setInputEnabled(false);
            GiANT.gui.appendConsoleText("\n", false);//clear any previous error msgs
            //TO DO (minor), this puts extra blank lines into the conslole...
            String output = null;
            String finalError = null;
            StringBuffer error = new StringBuffer();
            Matcher m = KASH_P.matcher("");
            try{
                StringBuffer b = new StringBuffer();
                //listen until KASH returns a prompt
                while(!m.matches()){
                    //read char by char (buffering not possible...see above instantiation of kashOut
                    // -1 = end of stream
                    char c;
                    while(kashOut.ready()){
                        c = (char)kashOut.read();
                        b.append(c);
                    }
                    m.reset(b.toString());
                }
                output = b.toString();
            }catch(IOException e){
                System.err.println("kashOut stream:  " + e);
            }try{
                //now read out of the error stream
                //this is a bit of a hack in that it assumes all errors terminated with "\n"
                String s = null;
                while(kashErr.ready()){
                    //echo the error to our java KASH and to the GUI
                    s = kashErr.readLine();
                    appendText(s + "\n");
                    error.append(s);
                    //DEBUG
                    //System.err.println(s);
                    //TO DO...the Error text displayed by KASH is not always germane (e.g. it gives the
                    // wrong signatures for functions...so you may want to say something about that
                }
                finalError = error.toString();
                if(finalError.length() > 0)
                    GiANT.gui.appendConsoleText("\n" + finalError + DETAILS, true);
            }catch(IOException io){
                System.err.println("kashErr stream:  " + io);
            }
            //show the KASH prompt AFTER the error (if any)
            appendText(output);
            //DEBUG
                                //DEBUG
            //System.err.println(output);
            //System.err.println("group="+m.group(1).trim()+"***\n\n");
            Matcher m2 = ERROR.matcher(output);    //syntax errors don't go to stderr!
            if(m2.find()){
                error.append(output);
                GiANT.gui.appendConsoleText("\n"+Graphic.removeBackslash(m2.group()) + DETAILS, true);
            }
            GiANT.gui.setInputEnabled(true);
            //DEBUG
            //System.err.println("ERROR=" + error.toString());
            //System.err.println("OUTPUT=" + output);
            //m.group(1).trim() is minus kash prompt
            return new Response(m.group(1), finalError);
        }
    }
    
    protected class Response{
        
        private String error;
        private String output;
        private String parsed; //sometimes we want to parse the output...it goes here!
        
        private Response(String output, String error){
            this.error = error;
            this.output = output;
            parsed = Graphic.removeBackslash(output).trim();
        }
        
        protected boolean error(){
            return error.length() != 0;
        }
        
        protected String getError(){
            return error;
        }
        
        protected String getOutput(){
            return parsed;
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

        mainPanel = new javax.swing.JPanel();
        textDisplayScrollPane = new javax.swing.JScrollPane();
        textDisplayArea = new javax.swing.JTextArea();
        textInputPanel = new javax.swing.JPanel();
        textInputField = new javax.swing.JTextField();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setIconifiable(true);
        setResizable(true);
        setTitle("KASH");
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                formComponentHidden(evt);
            }
        });

        mainPanel.setLayout(new java.awt.BorderLayout());

        textDisplayScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        textDisplayScrollPane.setAutoscrolls(true);
        textDisplayArea.setBackground(new java.awt.Color(0, 0, 0));
        textDisplayArea.setColumns(80);
        textDisplayArea.setEditable(false);
        textDisplayArea.setFont(new java.awt.Font("Courier", 0, 11));
        textDisplayArea.setForeground(new java.awt.Color(51, 255, 0));
        textDisplayArea.setLineWrap(true);
        textDisplayArea.setRows(24);
        textDisplayArea.setTabSize(4);
        textDisplayArea.setDragEnabled(false);
        textDisplayArea.setMargin(new java.awt.Insets(2, 2, 2, 2));
        textDisplayScrollPane.setViewportView(textDisplayArea);

        mainPanel.add(textDisplayScrollPane, java.awt.BorderLayout.CENTER);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        textInputPanel.setLayout(new java.awt.GridBagLayout());

        textInputPanel.setFocusable(false);
        textInputField.setFont(new java.awt.Font("Monaco", 0, 12));
        textInputField.setFocusCycleRoot(true);
        textInputField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textInputFieldActionPerformed(evt);
            }
        });
        textInputField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textInputFieldKeyPressed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        textInputPanel.add(textInputField, gridBagConstraints);

        getContentPane().add(textInputPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }
    // </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        textInputField.grabFocus();
    }//GEN-LAST:event_formComponentShown

    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden
        GiANT.gui.requestDesktopFocus(true);
    }//GEN-LAST:event_formComponentHidden

    private void textInputFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textInputFieldKeyPressed
        final int code = evt.getKeyCode();
        boolean up = false;
        boolean down = false;
        
        up = code == UP_ARROW;
        down = code == DOWN_ARROW;
        
        if((up || down) && !commandHistory.isEmpty()){ //see if user pressed one of the up or down arrows
            //make sure there is a command history
           if(up)
               histCursor--;
           else
               histCursor++;

           int i = java.lang.Math.abs(histCursor % commandHistory.size());
           String hist = (String)commandHistory.get(i);
           textInputField.setText(hist);
           textInputField.setCaretPosition(hist.length());
        }

        if(code == java.awt.event.KeyEvent.VK_ESCAPE)
            setVisible(false);
    }//GEN-LAST:event_textInputFieldKeyPressed

    private void textInputFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textInputFieldActionPerformed
        //prevent other user input
        //GiANT.gui.setInputEnabled(false);
        //fetch input string
        String cmd = textInputField.getText();
        //clear field
        textInputField.setText("");
        //handle command history
        commandHistory.add(cmd);
        histCursor = commandHistory.size(); //reset the history cursor
        //send command to shell
        tell(cmd);
        //keep focus
        textInputField.grabFocus();//focus model needs attention? TODO
        textInputField.setCaretPosition(0);
    }//GEN-LAST:event_textInputFieldActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel mainPanel;
    protected javax.swing.JTextArea textDisplayArea;
    private javax.swing.JScrollPane textDisplayScrollPane;
    private javax.swing.JTextField textInputField;
    private javax.swing.JPanel textInputPanel;
    // End of variables declaration//GEN-END:variables
    
}
