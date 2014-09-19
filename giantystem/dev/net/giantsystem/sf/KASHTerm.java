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
 * Provides GUI and I/O services to the KASH shell
 * @author karve
 */
public class KASHTerm extends javax.swing.JInternalFrame {
    //private String KASH_DIR;// = "/Users/karve/Desktop/PROJECTS/KASH/KASH_2.5";
    //public final static String KASH_DIR = "/net/kant/V4/bin/";
    //public final static String[] START_KASH = new String[]{KASH_DIR + "/kash", "giant.kash"};
    /**
     * The prompt that KASH returns after evaluating an expression
     * (something like "kash> ")
     */
    public final static String KASH_PROMPT = "kash> ";
    /**
     * Somtimes KASH will only return "> "
     * @deprecated not used
     */
    public final static String KASH_PROMPT_B = "> ";
    /**
     * Append this text to GUI error messages
     */
    public final static String DETAILS = " (View/KASH for details.) ";
    
    //sometimes we want to find the kash prompt
        //turn on DOTALL with (?s)
    /**
     * Regex pattern to match KASH_PROMPT
     */
    public final static Pattern KASH_P = Pattern.compile("(?s)(.*)" + KASH_PROMPT);
    /**
     * regex pattern to match "error"
     */
    public final static Pattern ERROR = Pattern.compile("(?i)error");
    /**
     * regex pattern to match "true"
     */
    public final static Pattern TRUE = Pattern.compile("(?i)true");
    /**
     * regex pattern to match "false"
     */
    public final static Pattern FALSE = Pattern.compile("(?i)false");
    /**
     * regex pattern to match text returned by KASH 2.5 on creating an order:
     * "Generating Polynomial: " so that we can parse for this text
     */
    public final static Pattern GEN_POLY = Pattern.compile("Generating polynomial:");
    /**
     * regex pattern to match text "syntax error"
     */
    public static final Pattern SYNTAX_ERR = Pattern.compile("Syntax error.*");//to find error messages   
    /**
     * regex pattern to match "quit"
     */
    public static final Pattern QUIT = Pattern.compile("\\s*quit\\s*;\\s*");
    
    /**
     * key code for up arrow
     */
    public final static int UP_ARROW = java.awt.event.KeyEvent.VK_UP;
    /**
     * key code for down arrow
     */
    public final static int DOWN_ARROW = java.awt.event.KeyEvent.VK_DOWN;
	/**
	 * keeps track of position in the <CODE>commandHistory</CODE> list to support command
	 * history in the java accessible KASH shell (i.e. the GUI face of the KASHTerm class)
	 * @see #commandHistory
	 */
	private int histCursor = 0;
    /**
     * number field degree at which we stop auto-computing the Galois group (for performance
     * reasons)
     */
    public final static int GALOIS_LIMIT = 2;    
    /**
     * KASH assignment operator
     */
    public final static String EQ = " := ";
    /**
     * KASH statement terminator
     */
    public final static String END  = ";";
    /**
     * ,
     */
    public final static String COMMA = ", ";
    /**
     * If no character is given to join(), use this one
     * @see #join(String[], String)
     */
    public final static String JOIN = ", ";
    /**
     * Used to signal a KASH error
     * @see #funExecToString
     */
    public final static String HUH = "?" + DETAILS;
    
    //Function names
    /**
     * KASH function name used to create number fields
     */
    public final static String NEW_NF = "Order";
    /**
     * KASH function name to determine a polynomial's degree
     */
    public final static String POLY_DEG = "PolyDeg";
    /**
     * KASH function for determining order degree
     */
    public final static String ORDER_DEG = "OrderDegAbs";
    /**
     * KASH function for power basis
     */
    public final static String BASIS = "Basis";
    /**
     * KASH function for discriminant
     */
    public final static String DISC = "OrderDisc";
    /**
     * KASH function for order signature
     */
    public final static String SIG = "OrderSig";
    /**
     * KASH subfield test for orders
     */
    public final static String SUBFLD = "GiantIsSubfield";//KASH OrderIsSubfield() is slow (bug?)
    /**
     * KASH function for galois group of an order
     */
    public final static String GALOIS_GROUP = "Galois";
    /**
     * KASH function to create a polynomial algebra
     */
    public static final String POLY_ALG = "PolyAlg";
    /**
     * KASH function to define polynomials
     */
    public static final String POLY = "Poly";
    /**
     * KASH trace function
     */
    public static final String TRACE = "Trace";
    /**
     * KASH norm function
     */
    public static final String NORM = "Norm";
    /**
     * KASH function to fetch ideal's (2) generators
     */
    public static final String IDEAL_GENERATORS = "GiantIdealGenerators";
    /**
     * KASH function for minimal polynomial
     */
    public static final String MIN_POLY = "MinPoly";
    /**
     * KASH function for polynomial disc.
     */
    public static final String POLY_DISC = "PolyDisc";
	/**
	 * KASH function to move polynomials
	 */
	public static final String POLY_MOVE = "PolyMove";
    /**
     * KASH test for polynomial reducibility
     */
    public static final String REDUCIBLE_P = "PolyIsIrreducible";
    /**
     * KASH ideal primality test
     */
    public static final String PRIME_P = "IdealIsPrime";
    /**
     * KASH ideal principality test
     */
    public static final String PRINCIPAL = "GiantIdealIsPrincipal";
	/**
	 * KASH ideal principality test using the class group (iff available)
	 */
	public static final String PRINCIPAL_CL = "GiantIdealIsPrincipalCl";
    /**
     * KASH test for integrality of an ideal
     */
    public static final String INTEGRAL_P = "IdealIsIntegral";
	/**
	 * KASH function to fetch ideal class representative of a given ideal
	 */
	public static final String CLASS_REP = "IdealClassRep";
    /**
     * KASH test for polynomial-hood
     */
    public static final String POLY_P = "IsPoly";
    /**
     * KASH test for element-hood
     */
    public static final String ELT_P = "IsElt";
	/**
	 * KASH function to move elements between orders
	 */
	public static final String ELT_MOVE = "EltMove";
    /**
     * KASH ideal predicate
     */
    public static final String IDEAL_P = "IsIdeal";
	/**
	 * KASH ideal factorization
	 */
	public static final String IDEAL_FACTOR = "IdealFactor";
    /**
     * KASH ideal constructor
     */
    public static final String IDEAL_CONSTRUCTOR = "Ideal";
	/**
	 * KASH fxn to move ideals between orders
	 */
	public static final String IDEAL_MOVE = "IdealMove";
    /**
     * KASH fxn to fetch torsion unit
     */
    public static final String TORSION = "OrderTorsionUnit";
    /**
     * KASH fxn to fetch torsion rank
     */
    public static final String TORSION_RANK = "OrderTorsionUnitRank";
    /**
     * KASH fxn to calculate fundamental units
     */
    public static final String FUND_UNITS = "GiantOrderUnitsFund";
	/**
	 * KASH fxn to calculate class group
	 */
	public static final String CLASS_GROUP = "OrderClassGroup";
	/**
	 * GiANT KASH fxn to get the cyclic factors of the given class group
	 * (GiANT fxns usually just change the format of the data from KASH)
	 */
	public static final String CLASS_FACTORS2 = "GiantClassGroupFactors";//WIHTOUT orders	
	/**
	 * Standard KASH fxn to get the cyclic factors of the given class group
	 */
	public static final String CLASS_FACTORS = "OrderClassGroupCyclicFactors";//WITH orders
    /**
     * KASH test for integrality of an element
     */
    public static final String INTEGRAL_P_ELT = "EltIsIntegral";
    /**
     * KASH Maximal Order constructor
     */
    public final static String MAX_ORDER = "OrderMaximal";
    /**
     * KASH regulator function
     */
    public final static String REG = "OrderReg";
	/**
	 * KASH fxn to define absolute orders (used to place relative orders on the desktop)
	 */
	public final static String ABS_ORDER = "OrderAbs";
	/**
	 * KASH fxn for torsion X fundamental unit decomposition of a unit
	 */
	public final static String UNIT_REP = "EltUnitDecompose";
        //operators
    /**
     * + operator
     */
    public final static String PLUS = " + ";
    /**
     * - operator
     */
    public final static String MINUS = " - ";
    /**
     * x operator
     */
    public final static String TIMES = " * ";
    /**
     * / operator
     */
    public final static String DIVIDE = " / ";
    
    /**
     * The object that handles I/O to GiANT's KASH session
     */
    private IO io;//= new IO();
    /**
     * A list of commands sent to KASH by the user via the GUI command-line for the shell
     */
    private ArrayList commandHistory = new ArrayList();
        
    /**
     * constructor
     * @param args The command-line argument string given to GiANT.jar at startup
     */
    public KASHTerm(String[] args) {
		String kashDir = args[0];//the user tells us where kash lives
		io = new IO(kashDir);
        initComponents();
    }
	
    /**
     * Wrap the arguemnt in parenthesis
     * @param s The string to enclose
     * @return ( + <CODE>s</CODE> + )
     */
    public static String enclose(String s){
        return "(" + s + ")";
    }
	
    /**
     * WRITE a function call to give to KASH (but don't perform the call)
     * @param fname function name
     * @param args arguments to <CODE>fname</CODE>
     * @return KASH's response
     * @see #funExec
     */
    public static String funCall(String fname, String[] args){
        if(fname == null)
            return join(args);
        return fname + enclose(join(args));
    }
    
    /**
     * Make the specified KASH function call
     * @param fname function name
     * @param args arguments to <CODE>fname</CODE>
     * @return KASH's response
     * @see #funCall
     */
    public Response funExec(String fname, String[]args){
        return tell(funCall(fname, args) + END);
    }
    
    /**
     * Like funExec except returns a <CODE>String</CODE>
     * @param fname KASH function to call
     * @param args <CODE>fname</CODE>'s arguments
     * @return HUH iff there was an error; the KASH output string otherwise
     * @see #HUH
     * @see #funExec
     */
    public String funExecToString(String fname, String[]args){
        Response r = tell(funCall(fname, args) + END);
        if(!r.error())
            return r.getOutput();
        else
            return HUH;
    }
    
    /**
     * Assign a variable to the power basis of the given number field
     * @param nm the variable name for the basis of <CODE>nm</CODE>
     * @param n a number field
     * @return the KASH power basis of <CODE>n</CODE> OR HUH if there is an error
     */
    public String basis(String nm, NumberField n){
        return assignToFunCall(nm, BASIS, new String[]{n.getName()});
    }
    
    /**
     * Capture the integral basis of the given nubmer field with the given name
     * @param nm name of the integral basis (in KASH)
     * @param n number field
     * @return the integral basis
     */
    public String integralBasis(String nm, NumberField n){
        return assignToFunCall(nm, BASIS, new String[]{n.getMaxOrderName()});
    }    
    
    /**
     * Element predicate
     * @param e an KASH expression
     * @return true iff <CODE>e</CODE> is an element
     */
    public boolean integral(Element e){
        Response r = funExec(INTEGRAL_P_ELT, new String[]{e.getName()});
        return trueString(r.getOutput());
    }
    
    /**
     * Capture the discriminant of an order
     * @param nm name under which to capture the returned value
     * @param n number field
     * @return discriminant of <CODE>n</CODE>
     */
    public String disc(String nm, NumberField n){
        return assignToFunCall(nm, DISC, new String[]{n.getName()});
    }
    
    /**
     * Store the class respresentative of the given ideal under the given name
     * @param nm a variable name for KASH to use
     * @param i an ideal
     * @return <CODE>i</CODE>'s ideal class representative
     */
    public String classRep(String nm, Ideal i){
        return assignToFunCall(nm, CLASS_REP, new String[]{i.getName(), "\"gen\""});
    }
	
	/**
	 * Discriminant of a <CODE>n</CODE>'s maximal order
	 * @param n a number field
	 * @return discriminant of the maximal order of <CODE>n</CODE>
	 */
	public String maxOrderDisc(NumberField n){
        return assignToFunCall("temp", DISC, new String[]{n.getMaxOrderName()});
    }
	
    /**
     * Store the unit representation of an element
     * @param nm Name under which to store the representation
     * @param e element
     * @param f parent field of <CODE>e</CODE>
     * @return <CODE>e</CODE>'s unit decomposition
     */
    public String unitRep(String nm, Element e, NumberField f){
		//if we don't do the move first, EltUnitDecompose may complain that the element
		// is not integral
		String move = funCall(ELT_MOVE, new String[]{e.getName(), f.getMaxOrderName()});
        return assignToFunCall(nm, UNIT_REP, new String[]{move, "\"expons\""});
    }
	
    /**
     * Store the regulator of a unit group
     * @param nm variable name (in KASH) under which to store the regulator
     * @param n number field in which the unit group lives
     * @return regulator
     */
    public String regulator(String nm, NumberField n){
        return assignToFunCall(nm, REG, new String[]{n.getMaxOrderName()});
    }
    //OPTIM you allocate tons of String[] arrays...use static one
    /**
     * Rank of the torsion unit in <CODE>n</CODE>
     * @param n number field
     * @return Rank of the torsion unit in <CODE>n</CODE>
     */
    public String torsionRank(NumberField n){
        return assignToFunCall("temp", TORSION_RANK, new String[]{n.getMaxOrderName()});
    }    
    
    /**
     * Store the given polynomial's discriminant
     * @param nm name for the discriminant  
     * @param p polynomial
     * @return <CODE>p</CODE>'s discriminant
     */
    public String disc(String nm, Polynomial p){
        return assignToFunCall(nm, POLY_DISC, new String[]{p.getName()});
    }
    
	/**
	 * Store the fundamental units of a number field
	 * @param nm name under which to store the fundamental units in KASH
	 * @param n the number field
	 * @return fundamental units list
	 */
	public String fundUnits(String nm, NumberField n){
        return assignToFunCall(nm, FUND_UNITS, new String[]{n.getMaxOrderName(), n.getName()});
	}
    
	/**
	 * Store the class group of the given number field under the given name
	 * @param nm KASH name for the class group
	 * @param n number field
	 * @return class group
	 */
	public String classGroup(String nm, NumberField n){
		return assignToFunCall(nm, CLASS_GROUP, new String[] {n.getMaxOrderName()});
	}    
	
	/**
	 * Calculate, store the cyclic factors of the class group
	 * @param nm name under which to store the return value
	 * @param n number field
	 * @return cyclic factors of the class group
	 */
	public String classGroupFactors(String nm, NumberField n){
		String[] arg = new String[] {n.getMaxOrderName()};
		assignToFunCall(nm, CLASS_FACTORS2, arg);//class group factors w/o orders
		return funExecToString(CLASS_FACTORS, arg);//WITH orders
	}  
    /**
     * Calculate, Store, the torsion unit
     * @param nm name under which to store
     * @param n number field
     * @return torsion unit
     */
    public String torsion(String nm, NumberField n){
        return assignToFunCall(nm, TORSION, new String[]{n.getMaxOrderName()});
    }
    
    /**
     * Calculate, store, the (2) generators of an ideal
     * @param nm KASH name under which to store the return value
     * @param idl ideal
     * @return generators of <CODE>idl</CODE>
     */
    public String generators(String nm, Ideal idl){
        return assignToFunCall(nm, IDEAL_GENERATORS, new String[]{idl.getName()});
    }
    
    /**
     * Calculate & store the primitive element of a given number field
     * @param nm KASH name under which to store the return value
     * @param val value of the primitive element
     * @return the primitive element as represented by KASH
     */
    public String primElt(String nm, String val){
        return assignToFunCall(nm, null, new String[]{val});
    }
    
    /**
     * Capture the degree of the give number field in a KASH variable
     * @param nm variable name under which to store the degree of <CODE>n</CODE>
     * @param n number field
     * @return degree of <CODE>n</CODE>
     */
    public String degree(String nm, NumberField n){
        return assignToFunCall(nm, ORDER_DEG, new String[]{n.getName()});
    }
    
    /**
     * Capture the signature (real & complex embeddings) of the given number field
     * @param nm variable name for signature
     * @param n number field
     * @return signature (as KASH list of the form "[X, Y]"
     */
    public String sig(String nm, NumberField n){
        return assignToFunCall(nm, SIG, new String[]{n.getName()});
    }
    
    /**
     * Capture the Norm of an element
     * @param nm variable name for the return value
     * @param v variable
     * @return norm
     */
    public String norm(String nm, Variable v){
        return assignToFunCall(nm, NORM, new String[]{v.getName()});
    }
	
	
    
    /**
     * Capture the maximal order of <CODE>n</CODE> in an automatic variable name
     * @param n number field
     * @return KASH output resulting from the creation of <CODE>n</CODE>'s maximal order
     */
    public String maximalOrder(NumberField n){
        return assignToFunCall(n.getMaxOrderName(), MAX_ORDER, new String[]{n.getName()});
    }
    
    /**
     * Capture the Trace of the given object
     * @param nm name under which to capture the trace
     * @param v variable
     * @return Tr(<CODE>v</CODE>)
     */
    public String trace(String nm, Variable v){
        return assignToFunCall(nm, TRACE, new String[]{v.getName()});
    }
    
    /**
     * Return the integral basis representation of <CODE>e</CODE> in <CODE>n</CODE>
     * @param nm the name to be given to the integral basis rep.
     * @param e the element whose representation we will return
     * @param n the number field whose integral basis we are
     * concerned with
     * @return integral basis integral basis representation of <CODE>e</CODE> 
	 * in <CODE>n</CODE>
     */
    public String integralBasis(String nm, Element e, NumberField n){
        return assignToFunCall(nm, ELT_MOVE, new String[]{e.getName(), n.getMaxOrderName()});
    }
	
    /**
     * Store the minimal polynomial of the given element
     * @param nm name for the min. poly.
     * @param e element whose min. poly. we are looking for
     * @return minimal poly.
     */
    public String minPoly(String nm, Element e){
        return assignToFunCall(nm, MIN_POLY, new String[]{e.getName()});
    }
    
    /**
     * Find the principal generator of <CODE>i</CODE> whenever possible
     * @param nm shell name under which to store the return value
     * @param i an ideal
     * @return <CODE>i</CODE>'s principal generator; <CODE>false</CODE> if <CODE>i</CODE> is not principal; (also see KASH documentation)
     */
    public String principal(String nm, Ideal i){
        return assignToFunCall(nm, PRINCIPAL, new String[]{i.getName()});
    }
	
	/**
	 * Like principal(), but this test is faster. Can only be called if the class group
	 * for the field in which <CODE>i</CODE> resides has already been computed.
	 * @param nm shell name under which to store the return value
	 * @param i ideal
	 * @return whenever possible <CODE>i</CODE>'s principal generator; <CODE>false</CODE> if <CODE>i</CODE> is not principal; (also see KASH documentation)
	 * @see #principal
	 */
	public String principalFromClassGroup(String nm, Ideal i){
        return assignToFunCall(nm, PRINCIPAL_CL, new String[]{i.getName()});
    }
    
    //TO DO, pretty print string, add subscripts...?
    /**
     * Calculate, store the Galois group of a number field
     * @param nm name under which to store the return value
     * @param n a number field
     * @return String describing the structure of <CODE>n</CODE>'s Galois group
     */
    protected String galoisGroup(String nm, NumberField n){
        return assignToFunCall(nm, GALOIS_GROUP, new String[]{n.getName()});
    }
    
    
    /**
     * Assign <CODE>var</CODE> to the given function call (in KASH)
     * @param var variable name
     * @param func KASH function to call
     * @param args args for <CODE>func</CODE>
     * @return KASH result for the assignment
     */
    public String assignToFunCall(String var, String func, String[] args){
        String f = funCall(func, args);
        Response r = assign(var, f);
        if(!r.error())
            return r.getOutput();
        else
			return HUH;
    }
    
        
    /**
     * Capture the variable for the univariate polynomial ring of the given number field
     * @param nm variable name for the return value
     * @param n number field
     * @return KASH representation for X as in <CODE>n</CODE>[X]
     */
    public String polyRingVar(String nm, NumberField n){
        String[] args = {n.getName()};
        String alg = funCall(POLY_ALG, args);   //univariate ring over  n
        
        String[] args2 = {alg, "[1,0]"};        //polynomial of deg=1, no constant term
        return assignToFunCall(nm, POLY, args2);
    }
    
    /**
     * Polynomial predicate
     * @param exp KASH expression
     * @return <CODE>true</CODE> iff exp defines a polynomial in KASH (note that 0 is NOT 
     * a polynomia in KASH)
     */
    public boolean isPoly(String exp){
        Response r = funExec(POLY_P, new String[]{exp});
        if(!r.error())
            return trueString(r.getOutput());
        else return false;
    }
    
    /**
     * Element predicate
     * @param exp KASH expression
     * @return <CODE>true</CODE> iff <CODE>exp</CODE> is an element
     */
    public boolean isElement(String exp){
        Response r = funExec(ELT_P, new String[]{exp});
        if(!r.error())
            return trueString(r.getOutput());
        else return false;
    }
    
    /**
     * Ideal predicate
     * @param exp KASH expression
     * @return <CODE>true</CODE> iff <CODE>exp</CODE> is an ideal
     */
    public boolean isIdeal(String exp){
        Response r = funExec(IDEAL_P, new String[]{exp});
        if(!r.error())
            return trueString(r.getOutput());
        else return false;
    }    
        
    /**
     * Call KASH eval on the given string, return the results
     * @param varName a KASH expression or variable name
     * @return result of eval(<CODE>varName</CODE>) in KASH
     */
    public Response eval(String varName){
        return tell(varName + END);
    }
	
	/**
     * Similar to eval(), except a string, and not a Response object is 
	 * returned
     * @param eval a KASH expression
     * @return result of eval(<CODE>eval</CODE>) in KASH
	 * @see #eval
     */
	public String evalToString(String eval){
		Response r = eval(eval);
		if(r.error())
			return HUH;
		else
			return r.getOutput();
	}
	
	
    
    /**
     * Join the array using the default character
     * @see #JOIN
     * @param arr Array to join
     * @return A string of the array elements joined by the <CODE>JOIN</CODE> character
     */
    public static String join(String[] arr){
        return join(arr, JOIN);
    }
    
    /**
     * Join the given array with the given character
     * @param arr The array of strings to join
     * @param j The character to join with
     * @return The contents of <CODE>arr</CODE> joined by <CODE>j</CODE>
     */
    public static String join(String[] arr, String j){
        StringBuffer buff = new StringBuffer();
        
        int i = 0;
        for(i = 0; i < arr.length-1; i++)
            buff.append(arr[i] + j);
        //add the last element WITHOUT JOIN
        buff.append(arr[i]);
        
        return buff.toString();        
    }


    
    //we write to the terminal
    /**
     * Add text to the end of the text display area (where KASH output goes)
     * @param msg text to append
     * @see #textDisplayArea
     */
    protected void appendText(String msg){
        textDisplayArea.append(msg);
        textDisplayArea.setCaretPosition(textDisplayArea.getDocument().getLength());
    }
    
    /**
     * Disable/Enable user input for this object
     * @param flag iff <CODE>flag == true</CODE> enable input; otherwise disable it
     */
    protected void setInputEnabled(boolean flag){
        textInputField.setEnabled(flag);
    }
    /*@deprecated
	 */
    /**
     * Check if a given string contains a KASH return prompt (used in determining when
     * the shell has finished the most recent request)
     * @param s string to search for the prompt
     * @return <CODE>true</CODE> iff <CODE>s</CODE> contains a kash prompt
     * @deprecated this is now done, differently, with regex
     * @see #KASH_P
     * @see #KASH_PROMPT
     */
    public static boolean hasKASHPrompt(String s){
        if(s != null){
        //TO DO...there can be problems with this...e.g. with answers containing >
            return (s.endsWith(KASH_PROMPT) || s.endsWith(KASH_PROMPT_B));
        }else{
        // null string is not a KASH Prompt
            return false;
        }
    }
    
    /**
     * Does the argument contain the word "true"?
     * @see #TRUE
     * @see #FALSE
     * @param s string to search
     * @return <CODE>true</CODE> iff <CODE>s</CODE> matches the pattern <CODE>TRUE</CODE> and does
     * NOT match the pattern <CODE>FALSE</CODE>; exception thrown if neither or both patterns
     * are matched
     */
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
    
    /**
     * Wait until KASH responds until the last query; post response
     * @return KASH's response
     * @see KASHTerm.Response
     */
    protected Response listen(){
        return io.listen();

    }

    /**
     * Shut down all I/O streams to the KASH process
     */
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
   
    /**
     * Create, store a new number field
     * @param name name for the number field
     * @param poly generating polynomial
     * @return resulting KASH output
     */
    protected Response newNumberField(String name, String poly){
        String[] args = {poly};
        Response r = tell(name + EQ + funCall(NEW_NF, args) + END);
        Matcher m = GEN_POLY.matcher(r.getOutput());
        String parse = m.replaceAll(""); //get rid of beginning part; e.g. >>>Generating polynomial:<<< x^3 + 2*x^2 - 2*x - 19
        r.parsed = Graphic.removeSpace(parse);
        
        return r;
    }
	
	/**
	 * Form a relative extension
	 * @param name name for the extension
	 * @param poly generating polynomial (from the polynomial ring of another number field)
	 * @return resulting KASH output
	 */
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
    
    /**
     * Determine degree of the given polynomial
     * @param poly polynomial
     * @return degree of <CODE>poly</CODE>
     */
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
    
    /**
     * Assign one quantity to another in KASH
     * @param var the left-hand side of the assignment
     * @param value the right-hand side of the assignment
     * @return result given by KASH
     */
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

    
    /**
     * Reducibility predicate for polynomials
     * @param p polynomial
     * @return <CODE>true</CODE> iff <CODE>p</CODE> is irreducible
     */
    public boolean irreducible(Polynomial p){
        String[] args = {p.getName()};
        Response r = tell(funCall(REDUCIBLE_P, args) + END);
        return  trueString(r.getOutput());
    }
    
	
    /**
     * Test if an ideal is prime
     * @param i ideal
     * @return <CODE>true</CODE> iff <CODE>i</CODE> is a prime ideal
     */
    public boolean prime(Ideal i){
        String[] args = {i.getName()};
        Response r = tell(funCall(PRIME_P, args) + END);
        return  trueString(r.getOutput());
    }
    
	/**
	 * Factor an ideal
	 * @param nm name under which to store the list of factors returned by KASH
	 * @param i ideal
	 * @return factors of <CODE>i</CODE>
	 */
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
        
    /**
     * Test if an ideal is integral
     * @param i an ideal
     * @return <CODE>true</CODE> iff <CODE>i</CODE> is integral
     */
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
    /**
     * Subfield test
     * @param n1 number field
     * @param n2 number field
     * @return <CODE>true</CODE> iff n1 <= n2
     */
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
    /**
     * Split KASH lists into their constituent elements
     * @param list KASH list
     * @return <CODE>list</CODE> split into it's elements
     */
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
    /**
     * Superfield test for a pair of number fields
     * @param n1 number field
     * @param n2 number field
     * @return <CODE>true</CODE> iff n1 >= n2
     */
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
        /**
         * The System-level object for the kash process itself
         */
        private Process kash;
        /**
         * Output stream FROM KASH
         */
        private Reader kashOut;
        /**
         * Error stream FROM KASH
         */
        private BufferedReader kashErr;
        /**
         * Stream for sending information TO (into) KASH
         */
        private OutputStreamWriter kashIn;

        /**
         * constructor
         * @see KASHTerm.IO
         * @param kashDir local path to the kash executable
         */
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
        
        /**
         * Collect characters in the pipe FROM KASH until it returns a prompt (e.g. "kash> ")
         * @return A <CODE>Response</CODE> containing the information returned by KASH in response 
         * to the command that preceded this call to <CODE>listen()</CODE>
         * @see KASHTerm.Response
         */
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
    
    /**
     * For each <CODE>listen()</CODE> call KASH issues a response
     * @see #listen()
     */
    protected class Response{
        
        /**
         * Error string from KASH
         */
        private String error;
        /**
         * Raw output string from KASH
         */
        private String output;
        /**
         * Occasionally we wish to parse the output a bit; it goes here. This value is returned
         * by <CODE>getOutput()</CODE>
         * @see #getOutput
         */
        private String parsed; //sometimes we want to parse the output...it goes here!
        
        /**
         * Constructor
         * @param output output information from KASH
         * @param error error information from KASH
         */
        private Response(String output, String error){
            this.error = error;
            this.output = output;
            parsed = Graphic.removeBackslash(output).trim();
        }
        
        /**
         * Was there an error?
         * @return <CODE>true</CODE> iff there was an error
         */
        protected boolean error(){
            return error.length() != 0;
        }
        
        /**
         * Get the error string associated with this repsonse
         * @return the error information returned by KASH
         */
        protected String getError(){
            return error;
        }
        
        /**
         * Get the output string associated with this repsonse
         * @return the output information returned by KASH
         */
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

    /**
     * Code to execute when the KASH window is shown
     * @param evt show event
     */
    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        textInputField.grabFocus();
    }//GEN-LAST:event_formComponentShown

    /**
     * Execute special code when this window is hidden
     * @param evt hide event
     */
    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden
        GiANT.gui.requestDesktopFocus(true);
    }//GEN-LAST:event_formComponentHidden

    /**
     * Handle key presses (like up/down arrows) for <CODE>textInputField</CODE>
     * @param evt incoming key event
     */
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

    /**
     * Called when the user hits return in the input field
     * @see #textInputField
     * @param evt The event that started it all
     */
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
    /**
     * The base Panel for the KASH window shown in GiANT
     */
    private javax.swing.JPanel mainPanel;
    /**
     * KASH output is displayed here
     */
    protected javax.swing.JTextArea textDisplayArea;
    /**
     * Scroll the KASH session output
     */
    private javax.swing.JScrollPane textDisplayScrollPane;
    /**
     * User input to KASH comes in here
     */
    private javax.swing.JTextField textInputField;
    /**
     * Lower Panel for KASH window in GiANT
     */
    private javax.swing.JPanel textInputPanel;
    // End of variables declaration//GEN-END:variables
    
}
