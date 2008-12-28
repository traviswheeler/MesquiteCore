/* Mesquite source code.  Copyright 1997-2009 W. Maddison and D. Maddison.Version 2.6, January 2009.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.charMatrices.ColorByState; import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;import mesquite.lib.table.*;import mesquite.categ.lib.*;/* ======================================================================== */public class ColorByState extends DataWindowAssistantI implements CellColorer, CellColorerMatrix {	MesquiteTable table;	CharacterData data;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName){		return true;	}   	 public boolean setActiveColors(boolean active){   	 	setActive(true);		return true;    	 }	/*.................................................................................................................*/   	 public boolean isSubstantive(){   	 	return false;   	 }	/*.................................................................................................................*/	public void setTableAndData(MesquiteTable table, CharacterData data){		this.table = table;		this.data = data;	}	/*.................................................................................................................*/    	 public String getName() {		return "Color By State";   	 }    	 public String getNameForMenuItem() {		return "Character State";   	 }	/*.................................................................................................................*/  	 public String getExplanation() {		return "Colors the cells of a character matrix by their contained character states.";   	 }	/*.................................................................................................................*/   	public void viewChanged(){   	}   	public String getCellString(int ic, int it){   		if (!isActive())   			return null;		return "Colored to show state of character";   	}   	ColorRecord[] legend;   	public ColorRecord[] getLegendColors(){   		if (data == null)   			return null;   		legend = null;   		if (data instanceof DNAData){	   		legend = new ColorRecord[DNAState.maxDNAState+1];   			for (int is = 0; is<=DNAState.maxDNAState; is++) {   				legend[is] = new ColorRecord(DNAData.getDNAColorOfState(is), DNAData.getDefaultStateSymbol(is));   			}   		}   		else if (data instanceof ProteinData){	   		legend = new ColorRecord[ProteinState.maxProteinState+1];   			for (int is = 0; is<=ProteinState.maxProteinState; is++) {   				legend[is] = new ColorRecord(ProteinData.getProteinColorOfState(is), ProteinData.getDefaultStateSymbol(is));   			}   		}   		return legend;   	}   	public String getColorsExplanation(){   		if (data == null)   			return null;   		if (data.getClass() == CategoricalData.class){   			return "Colors of states may vary from character to character";   		}   		return null;   	}	public Color getCellColor(int ic, int it){		if (ic < 0 || it < 0)  			return null;		if (data == null)			return null;		else			return data.getColorOfStates(ic, it);	}	public CompatibilityTest getCompatibilityTest(){		return new CharacterStateTest();	}	public String getParameters(){		if (isActive())			return getName();		return null;	}}	