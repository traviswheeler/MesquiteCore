/* Mesquite source code.  Copyright 1997-2011 W. Maddison and D. Maddison.
Version 2.75, September 2011.
Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. 
The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.
Perhaps with your help we can be more than a few, and make Mesquite better.

Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.
Mesquite's web site is http://mesquiteproject.org

This source code and its compiled class files are free and modifiable under the terms of 
GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)
*/
package mesquite.lib.duties;

import java.awt.*;
import mesquite.lib.*;


/* ======================================================================== */
/**Presents a single tree window.*/

public abstract class TreeWindowMaker extends MesquiteModule   {

   	 public Class getDutyClass() {
   	 	return TreeWindowMaker.class;
   	 }
	 public String getFunctionIconPath(){
   		 return getRootImageDirectoryPath() + "functionIcons/treeWindow.gif";
   	 }
  	//public abstract void makeTreeWindow(Taxa taxa);
  	public abstract Taxa getTaxa();
  	public abstract MesquiteModule getTreeSource();
  	public abstract TreeDisplay getTreeDisplay();
	public abstract void transposeField();
 	public abstract boolean treeIsEdited();
 	public String getDutyName() {
 		return "Tree Window Maker";
   	 }

   	public boolean isSubstantive(){
   		return false;  
   	}
}


