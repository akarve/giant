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
 * KASH.java
 *
 * Created on May 18, 2005, 5:24 PM
 */

package net.giantsystem.sf;

/**
 * Interface for objects that also live in the KASH shell as a variable
 * @author karve
 */
public interface KASH{
    /**
     * Fetch the KASH variable's name
     * @return KASH variable name
     */
    public String getName();    //KASH variable name
    /**
     * Fetch the KASH variable's value
     * @return KASH value; this is usually the string returned by KASH when the name returned by <CODE>getName()</CODE> is evaluated in the shell
     */
    public String getValue();   //KASH's eval(getName())
}
