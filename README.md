#Introduction
GiANT (Graphical Algebraic Number Theory) is a graphical interface for working with number fields. It is
written in Java and runs on top of the computer algebra system [KASH 2.5](http://www.math.tu-berlin.de/~kant/kash.html).

#Installation
1. GiANT requires Java 1.4* and a working installation of [KASH 2.5](http://www.math.tu-berlin.de/%7Ekant/download.html).

2. Once KASH is installed, download the latest copy of "giant.kash" file, available on our SourceForge page under "Files". Place a copy of "giant.kash" in the directory on your computer called "KASH_2.5", which should also be the directory that contains the executable "kash".

3. Launch Giant by typing something like the following:
    java -jar GiANT.jar /Users/foo/kashdir

The last argument "/Users/foo/kashdir" is the path to the DIRECTORY in which the kash executable can be found (not the path of the executable itself)

#Motivation
Algorithms for dedicated computer algebra systems have developed
rapidly. Nevertheless, user interfaces for these feature-rich systems
have traditionally focused on command-line interaction. GiANT is a
graphical user interface for computer algebra. It dynamically creates
interactive diagrams, typesets formulas, and supports drag-and-drop
manipulation of elements and polynomials. The result is a visual
workspace designed to support mathematical intuition and reduce
cognitive load versus text-only algebra systems.

#Screenshots, video
* [towers of fields](http://giantsystem.sourceforge.net/images/tower.png)
* [working with the class group](http://giantsystem.sourceforge.net/images/classGroup.png)
* [building towers of fields (video)](http://giantsystem.sourceforge.net/images/towers.mov)
* [working with fields using drag-and-drop (video)](http://giantsystem.sourceforge.net/images/drag-and-drop.mov).

#Paper, history
If you use GiANT for research or development, please cite the paper: [GiANT: Graphical Algebraic Number
Theory] (http://jtnb.cedram.org/cedram-bin/article/JTNB_2006__18_3_721_0.pdf), [Aneesh Karve](http://pages.cs.wisc.edu/~karve/) and [Sebastian Pauli](http://www.math.tu-berlin.de/~pauli/), [Journal of Number Theory--Bordeaux (JTNB)](http://almira.math.u-bordeaux.fr/jtnb/jtnb_english.html), Tome 18, no. 3 (2006), p. 721-727.

GiANT was introduced at the [MP60 Number Theory Conference](http://www.math.tu-berlin.de/~kant/MP60/), Technical University of Berlin, 10 June 2005. GiANT was featured at [Algebra and Computation 2005](http://tnt.math.metro-u.ac.jp/ac/2005/index.en.html),
Tokyo Metropolitan University, 16 November 2005.


#Compatibility 
GiANT runs on Java 1.4 or higher. It has been tested on Mac OS X and some pure UNIX flavors, but not so much on Windows. GiANT requires a working installation of [KASH 2.5](http://www.math.tu-berlin.de/~kant/download.html), which is free.


#Future development
See the concluding remarks in the [GiANT paper](http://jtnb.cedram.org/cedram-bin/article/JTNB_2006__18_3_721_0.pdf)
for ideas.

#License
GiANT source is open under the GNU General Public License (GPL).

#Acknowledgments
Special thanks to the [German Academic Exchange (DAAD)](http://www.daad.org/) and the [KANT Group at TU-Berlin](http://www.math.tu-berlin.de/~kant) for their support during the creation of GiANT.

#Authors
GiANT was created by [Aneesh Karve](http://www.aneeshkarve.com) and co-designed by [Sebastian Pauli](http://www.math.tu-berlin.de/~pauli/)
