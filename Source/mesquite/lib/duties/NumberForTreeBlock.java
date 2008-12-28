/* Mesquite source code.  Copyright 1997-2009 W. Maddison and D. Maddison.Version 2.6, January 2009.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.lib.duties;import java.awt.*;import mesquite.lib.*;/* ======================================================================== *//**Supplies a number for a vector of trees.*/public abstract class NumberForTreeBlock extends MesquiteModule implements NumberForItem  {   	 public Class getDutyClass() {   	 	return NumberForTreeBlock.class;   	 } 	public String getDutyName() { 		return "Number for Tree Block";   	 }   	/** Called to provoke any necessary initialization.  This helps prevent the module's intialization queries to the user from   	happening at inopportune times (e.g., while a long chart calculation is in mid-progress)*/   	public abstract void initialize(TreeVector trees);	public abstract void calculateNumber(TreeVector trees, MesquiteNumber result, MesquiteString resultString);	 	public boolean returnsMultipleValues(){  		return false;  	}	/*===== For NumberForItem interface ======*/   	public void initialize(Object object1, Object object2){		if (object1 instanceof TreeVector)    			initialize((TreeVector)object1);   	}	public  void calculateNumber(Object object1, Object object2, MesquiteNumber result, MesquiteString resultString){		if (result==null)			return;	   	clearResultAndLastResult(result);		if (object1 instanceof TreeVector) {			calculateNumber((TreeVector)object1, result, resultString);		}	} 	public  void calculateNumberInContext(Object object1, Object object2, ItemsSource source, int whichItem, MesquiteNumber result, MesquiteString resultString){	   	clearResultAndLastResult(result);		calculateNumber(object1, object2, result, resultString);		saveLastResult(result);		saveLastResultString(resultString);	}  	public String getNameOfValueCalculated(){ 		return getNameAndParameters();   	}}