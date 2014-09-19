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
package net.giantsystem.sf;
//
//  Graphic.java
//  GiANT
//
//  Created by Aneesh Karve on Wed Mar 16 2005.
//  Copyright (c) 2005 __MyCompanyName__. All rights reserved.
//

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.regex.*;
import java.awt.geom.Rectangle2D;
import javax.swing.JPopupMenu;
import java.awt.Point;
import java.awt.Dimension;

/**
 *
 * @author  karve
 * services to make things pretty; mostly typesetting
 */

public abstract class Graphic {
	//Unicode characters for math
	//superscripts
    public final static char SUPER_0 = '\u2070';
    public final static char SUPER_1 = '\u00B9';
    public final static char SUPER_2 = '\u00B2';
    public final static char SUPER_3 = '\u00B3';
    public final static char SUPER_4 = '\u2074';
    public final static char SUPER_5 = '\u2075';
    public final static char SUPER_6 = '\u2076';
    public final static char SUPER_7 = '\u2077';    
    public final static char SUPER_8 = '\u2078';
    public final static char SUPER_9 = '\u2079';
	public final static char SUPER_PLUS = '\u207a';
	public final static char SUPER_MINUS = '\u207b';
	public final static char SUPER_N = '\u207f';
	//subscripts
    public final static char SUB_0 = '\u2080';
    public final static char SUB_1 = '\u2081';
    public final static char SUB_2 = '\u2082';
    public final static char SUB_3 = '\u2083';
    public final static char SUB_4 = '\u2084';
    public final static char SUB_5 = '\u2085';
    public final static char SUB_6 = '\u2086';
    public final static char SUB_7 = '\u2087';    
    public final static char SUB_8 = '\u2088';
    public final static char SUB_9 = '\u2089';
	public final static char SUB_PLUS = '\u208a';
	public final static char SUB_MINUS = '\u208b';
	public final static char SUB_I = '\u1d62';
    //doublestruck letters
    public final static char R_REAL = '\u211D';
    public final static char C_COMPLEX = '\u2102';
    public final static char Q_RATIONAL = '\u211A';
    public final static char Z_INTEGERS = '\u2124';
	//math relations
    public final static char ELEMENT_OF = '\u2208';
	public final static char APPROX_EQUAL = '\u2245';
	public final static char ISOM = APPROX_EQUAL;
	public final static String _ISO_ = " " + ISOM + " ";
	//operations
	public final static char SQRT = '\u221A';
	public final static char CUBE_ROOT = '\u221A';
	public final static char FOURTH_ROOT = '\u221A';
	public final static char CIRCLED_TIMES = '\u2297';
	public final static char CIRCLED_CROSS = CIRCLED_TIMES;
	public final static char CROSS = '\u00d7';
    public final static String _CROSS_ = " " + Character.toString(CROSS) + " ";
	public final static String _CROSS = " " + Character.toString(CROSS);
	public final static char DOT = '\u0387';			//GREEK ANO TELEIA
    public final static String _DOT_ = " " + DOT + " ";
	public final static char INTERSECT = '\u2229';
	public final static char UNION = '\u222A';
    public final static char TIMES = CROSS;
	//special letters (foreign language)
		//script
	public final static char SCRIPT_SMALL_O = '\u2134'; //for maximal orders
		//black letter (for ideals)
	public final static char BLACK_I = '\u2111';
	public final static char BLACK_R = '\u211C';
	public final static char BLACK_H = '\u211C';

		//other
	public final static char SMALL_DIGAMMA = '\u03DD';  //like 'F' (for number fields?)
		//greek
	public final static char SMALL_XI = '\u03BE';
	public final static char SMALL_PI = '\u03C0';
	public final static char SMALL_ALPHA = '\u03B1';
	public final static char SMALL_BETA = '\u03B1';
    public final static char SMALL_ETA = '\u03b7';
    public final static char SMALL_EPSILON = '\u03b5';
	public final static char SMALL_OMEGA = '\u03C9';	//can loop b/w small_alpha + small_omega
    public final static char EMBED_ARROW = '\u21AA';
    public final static String SIG_LEGEND = "[" + EMBED_ARROW + R_REAL + ", " + EMBED_ARROW + C_COMPLEX + "]";
    //pointing brackets for generators, etc.
    public final static char L_ANGLE = '\u3008';//"?"; //'?';//'\u3008';
    public final static char R_ANGLE = '\u3009';//'?';//\u3009';
	//VARIABLES
	//regex patterns
		//search out exponents in user input (e.g. X^2 ...)
    public final static Pattern RP_EXPONENT = Pattern.compile("\\^(\\d+)");
    public final static Pattern SPACE = Pattern.compile("\\s+");
    public final static Pattern QUOTE = Pattern.compile("\"");
    public final static Pattern LONE_ONE = Pattern.compile("(\\W)1([\\W&&[^+-/]])");
    public final static Pattern STAR = Pattern.compile("\\*");
    public final static Pattern MINUS = Pattern.compile("-");
    public final static Pattern LEADING_MINUS = Pattern.compile("\\A(\\(?) (-) ");
    public final static Pattern PLUS = Pattern.compile("\\+");
    public final static Pattern MULTIPLE_TERMS = Pattern.compile("\\w[+-]\\w");
    public final static Pattern RATIONAL = Pattern.compile("-?\\d+\\s*(/\\s*\\d+)?");
	    //public final static Pattern RATIONAL = Pattern.compile("\\d+(/\\d+)?");
    //deprecatedpublic final static Pattern POWER_BASIS_REP = Pattern.compile("\\[(.+)\\](.*)");
        //match lists enclosed by brackets and containing numbers and commas within
    public final static Pattern ZERO = Pattern.compile("0");
    public final static Pattern KASH_LIST = Pattern.compile("(?s)\\[(.+)\\]\\s*(/\\d+)?");
		//a TIGHT_LIST is a just a KASH_LIST with all non-leading space stripped out
	public final static Pattern TIGHT_LIST = Pattern.compile("\\[([\\d,-]+)\\](/\\d+)?");
        //that means [....]/number
    //public final static Pattern BACKSLASH = Pattern.compile("\\$");
            //mac java regex has a bug

	//special characters for power basis, integral power basis
	public final static String POWER = String.valueOf(NumberField.B);
    public final static String POWER_TEMP = underscore(POWER);
	public final static String INTEGRAL = String.valueOf(NumberField.IB);
	public final static String INTEGRAL_TEMP = underscore(INTEGRAL);
    public final static char STAR_CHAR = '*';
	//every graphic can have a context menu
	JPopupMenu popup;
	//store gra
	
    /*
     * Takes out whitespace, '*', and uses unicode superscripts to typeset a handsome poly
     * @return pretty sting
     * @param awkward - a string like "5*x^5+7..."
     */
    //pre...use a word character (regex \w for the name) or else trouble
    public static String prettyPolynomial(String awkward){
        String temp = " " + Graphic.removeSpace(awkward); //don't have to worry about boundary conds. in following regex
        
        //new code
		boolean basis = false;
        Matcher m = TIGHT_LIST.matcher(temp);
        while(m.find()){
			String replace = basisListToPoly(m.group());
			Matcher mult = MULTIPLE_TERMS.matcher(replace);
			if(m.group(2) != null || mult.find()) //was there a scalar (m.group(2)) OR
				//more than one term? if so we need brackets for order of ops clarity
				replace = Graphic.bracketEnclose(replace);
            temp = m.replaceFirst(replace);
            m.reset(temp);
			//m = TIGHT_LIST.matcher(temp);
			basis = true;
        }
		temp = temp.replaceAll("\\+-", "-");//somtimes we get back to back ops
		temp = temp.replaceAll("-\\+", "-");
        //end new code        
        
        //get rid of any lone 1s; i.e. don't write 1*x^2...
        //this loop must come before removeSpace        
        m = LONE_ONE.matcher(temp);
        while(m.find()){
            temp = m.replaceFirst(m.group(1) + m.group(2));//squeeze out the '1'
            m.reset(temp);//CH
        }
        
        //get rid of any spaces
        temp = temp.trim();//pull out leading space we introduced above        
        //get rid of any \s
        //temp = removeBackslash(temp); //handled by removeSpace...hmm...
        //get rid of any *s
        m = STAR.matcher(temp);
        temp = m.replaceAll("");
        //pad +, -
        m = MINUS.matcher(temp);
        temp = m.replaceAll(" - ");
        m = LEADING_MINUS.matcher(temp);
        if(m.find())
            temp = m.replaceFirst(m.group(1) + m.group(2));//don't replace leading minus else -x + 2 -> - x + 2
        m = PLUS.matcher(temp);
        temp = m.replaceAll(" + ");

        //look for exponents of the form "^XXX" where X = some digit
        m = RP_EXPONENT.matcher(temp);
        while(m.find()){
            temp = m.replaceFirst(toSuperscripts(m.group(1)));
            m.reset(temp);
        }
		
		if(basis)//we hit a basis list above; do the final replace
			return temp.replaceAll(POWER_TEMP, POWER);
		else
			return temp;
    }
    
    //enclose in brackets if not already
    public static String toList(String s){
        if(isList(s))
            return s;
        else return "[" + s + "]";
    }
    
    public static boolean isList(String lst){
        lst = lst.trim();
        Matcher m = KASH_LIST.matcher(lst);
        return m.matches();
    }
    
    public static String bracketEnclose(String s){
        return "[" + s + "]";
    }
    
    public static String underscore(String s){
        return "_"+s+"_";
    }
    //input string should have no spaces in it
    public static boolean hasMultipleTerms(String tightPoly){
        Matcher m = MULTIPLE_TERMS.matcher(tightPoly);
        if(m.matches())
            return true;
        return false;
    }
    
    //REGEX DOESN'T WORK! matching backslashes (trying) can drive a man to drink
    public static String removeBackslash(String in){

        //Matcher m = BACKSLASH.matcher(in);
        StringBuffer b = new StringBuffer();
        char[] look = in.toCharArray();
        for (int i = 0; i < look.length; i++){
            char x = look[i];
            if(x == '\\'){
                i++;//skip the next char too; it's something wierd
                //do nothing
            }else
                b.append(x);
        }
        return b.toString();
    }
	
	public static String listToPrettyPowerBasis(String list){
		return listToPrettyPowerBasis(list, false);
	}
    
    public static String listToPrettyPowerBasis(String list, boolean integral){
        if(!isList(list)){
            System.err.println("listToPrettyPowerBasis() - this is not a list:  " + list);
            return list;
        }
        String[] elts = KASHTerm.splitList(list);
        StringBuffer result = new StringBuffer();
        for(int i = 0; i < elts.length; i++){
            String elt = elts[i];
            if(!isList(elt))
                result.append(elt);
            else
                result.append(prettyPowerBasis(elt, integral));
            if(i < elts.length-1)
                result.append(", ");
        }
        return result.toString();
    }
	
    public static String prettyPowerBasis(String basis, boolean integral){
		String pretty = prettyPolynomial(basisListToPoly(basis, integral));
		if(integral)
			return pretty.replaceAll(INTEGRAL_TEMP, INTEGRAL);
		else
			return pretty.replaceAll(POWER_TEMP, POWER);
	}
	
	//convenience method
	public static String prettyPowerBasis(String basis){
		return prettyPowerBasis(basis, false);
	}
	
    //input:  a single powerbasis list [<rational>,...]<rational>
	private static String basisListToPoly(String help){
		return basisListToPoly(help, false);
	}
	
    //input:  a single powerbasis list [<rational>,...]<rational>
    private static String basisListToPoly(String help, boolean integral){
        if(help.equals(KASHTerm.HUH))
            return help;
        String temp = removeSpace(help);
        Matcher m = KASH_LIST.matcher(temp);
        if(!m.matches()){
            Matcher m2 = RATIONAL.matcher(temp);
            if(!m2.matches())
                System.err.println("Unexpected format for a single element's KASH basis representaion:  " + help);
            return temp;
        }
        temp = m.group(1);//strip outer brackets
        String[] coefficients = temp.split(",");
        StringBuffer result = new StringBuffer();
        boolean empty = true;
        if(!coefficients[0].equals("0")){//if there is a constant term
            result.append(coefficients[0]);//the constant term we append without an alpha
            empty = false;
        }
		String tmp, var;
		if(integral){//are we doing a power basis or an integral power basis?
			tmp = INTEGRAL_TEMP;
			var = INTEGRAL;
		}else{
			tmp = POWER_TEMP;
			var = POWER;
		}
        for(int i = 1; i < coefficients.length; i++){//start at 1, we did 0 above
            String coeff = coefficients[i];
            boolean zero = coeff.equals("0");
            if(!zero){
                if(!empty){
                    if(!coeff.startsWith("-"))
                        result.append("+");
                }
                result.append(coeff);
                result.append("*");
                result.append(tmp);//our dummy primitive element

                if(i > 1){//don't write x^1
                    result.append("^");
                    result.append(i);
                }
                empty = false;
            }
        }
        String poly = result.toString();//the raw polynomial
		String scalar = m.group(2);
        if(scalar == null)//no fractional scalar for poly
             return poly;
        else{
            m = MULTIPLE_TERMS.matcher(poly);//match poly and NOT pretty again b/c var is not a word char.
            if(m.find())//we have more than one term in the polynomial
                poly = KASHTerm.enclose(poly);//put it into "()"s b/c WHOLE POLY is over scalar          
            return poly + scalar;//if only one term, don't need ()s from enclose()
        }
    }
        
    //in comes a valid KASH string with operators, etc. and we replace
    //any occurences of a with those of old with nu...old must be a variable name
    //made of WORD characters (regex \w) or else things might not work
    public static String convert(String exp, String old, String nu){
        String good = exp; //don't have to worry about \A and \z! (boundary conditions)
        String pattern = "(?i)(\\W+)" + old + "(\\W+)";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(good);
        while(m.find()){
            good = m.replaceFirst(m.group(1) + nu + m.group(2));
            m.reset(good);//CH
        } 
        return good;
    } 
    
    public static String[] unitGroupStructure(int rank){
        String[] structure = new String[rank+1];//+1 for torsion
        int i = 0;
        structure[i++] = NumberField.TOR_CHAR;//torsion
        while(i <= rank){
            String sub = toSubscripts(Integer.toString(i));
            structure[i++] = NumberField.FUNIT_CHAR + sub;
        }
        return structure;
    }
    //return the s minus all whitespace
    public static String removeSpace(String s){
        Matcher m = SPACE.matcher(s);
        return m.replaceAll("");
    }
    
    public static String removeDoubleQuotes(String s){
        Matcher m = QUOTE.matcher(s);
        return m.replaceAll("");
    }
    
    /*
     * this turns any digit character into its unicode superscript.
     * @return unicode superscript version
     * @param a digit character
     */
    private static String unicodeScripts(String digs, boolean sup){
        StringBuffer buff = new StringBuffer();
        for(int i=0; i < digs.length(); i++){
            if(sup)
                buff.append(superScript(digs.charAt(i)));
            else
                buff.append(subScript(digs.charAt(i)));
        }
        return buff.toString();
    }
    
    public static String toSubscripts(String subs){
        return unicodeScripts(subs, false);
    }
    
    public static String toSuperscripts(String supers){
        return unicodeScripts(supers, true);
    }
    
    public static String angleEnclose(String s){
        return L_ANGLE + s + R_ANGLE;
    }
	 
    public static char superScript(char x){
        switch(x){
            case '0': return SUPER_0;
            case '1': return SUPER_1;
            case '2': return SUPER_2;
            case '3': return SUPER_3;
            case '4': return SUPER_4;
            case '5': return SUPER_5;
            case '6': return SUPER_6;
            case '7': return SUPER_7;
            case '8': return SUPER_8;
            case '9': return SUPER_9;
			case '-': return SUPER_MINUS;
			case '+': return SUPER_PLUS;
            //default: System.err.println("non-digit char received by superScript() in class Graphic");
        }
        //this is a hack return value
        return x;
    }
    
    public static char subScript(char x){
        switch(x){
            case '0': return SUB_0;
            case '1': return SUB_1;
            case '2': return SUB_2;
            case '3': return SUB_3;
            case '4': return SUB_4;
            case '5': return SUB_5;
            case '6': return SUB_6;
            case '7': return SUB_7;
            case '8': return SUB_8;
            case '9': return SUB_9;
			case '-': return SUB_MINUS;
			case '+': return SUB_PLUS;
            //default: System.err.println("non-digit char received by subScript() in class Graphic");
        }
        //this is a hack return value
        return x;
    }
	 
	 /* handle contextual menus for this item (if any)
	 */
    /*
	 void maybeDoPopup(java.awt.event.MouseEvent e){
		 if(e.isPopupTrigger()){
            popup.show(e.getComponent(), e.getX(), e.getY());
		 } 
	 }
     */	 
}