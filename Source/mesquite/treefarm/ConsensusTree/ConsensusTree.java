/* Mesquite source code, Treefarm package.  Copyright 1997-2005 W. Maddison, D. Maddison and P. Midford. Version 1.06, August 2005.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.treefarm.ConsensusTree;/*~~  */import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.duties.*;/* ======================================================================== */public class ConsensusTree extends TreeSource {	int currentTree=0;	TreeSourceDefinite treeSource;	Taxa oldTaxa=null;	Consenser consenser = null;	MesquiteString treeSourceName, consenserName;	MesquiteCommand tlsC, cC;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {		treeSource= (TreeSourceDefinite)hireNamedEmployee(commandRec, TreeSourceDefinite.class, StringUtil.tokenize("#StoredTrees"));		if (treeSource==null)			treeSource= (TreeSourceDefinite)hireEmployee(commandRec, TreeSourceDefinite.class, "Source of Trees for consensus");		tlsC = makeCommand("setTreeSource",  this);		cC = makeCommand("setConsenser", this);		if (treeSource==null)			return sorry(commandRec, getName() + " couldn't start because no source of trees obtained");		treeSource.setHiringCommand(tlsC);		treeSourceName = new MesquiteString();		if (numModulesAvailable(TreeSourceDefinite.class)>1) {			MesquiteSubmenuSpec mss = addSubmenu(null, "Tree Source", tlsC, TreeSourceDefinite.class);			mss.setSelected(treeSourceName);		}		consenser= (Consenser)hireEmployee(commandRec, Consenser.class, "Consensus calculator");		if (consenser==null)			return sorry(commandRec, getName() + " couldn't start because no consensus module obtained.");		consenser.setHiringCommand(cC);		consenserName = new MesquiteString();		if (numModulesAvailable(Consenser.class)>1) {			MesquiteSubmenuSpec mss = addSubmenu(null, "Consensus module", cC, Consenser.class);			mss.setSelected(consenserName);		} 		return true;  	 }  	 MesquiteInteger pos = new MesquiteInteger(0);	/*.................................................................................................................*/  	 public Snapshot getSnapshot(MesquiteFile file) {    	 	Snapshot temp = new Snapshot();   	 		temp.addLine("setTreeSource ", treeSource);   	 		temp.addLine("setConsenser ", consenser);  	 	return temp;  	 }	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {     	 	if (checker.compare(this.getClass(), "Sets the module supplying tree blocks", "[name of module]", commandName, "setTreeSource")) {     	 		TreeSourceDefinite temp = (TreeSourceDefinite)replaceEmployee(commandRec, TreeSourceDefinite.class, arguments, "Source of trees", treeSource);	 			if (temp!=null) {	 				treeSource = temp;					treeSource.setHiringCommand(tlsC);					treeSourceName.setValue(treeSource.getName());		    	 		parametersChanged(null, commandRec);	 			}	 			return temp;	    	 	}	   	 	else	    	 	if (checker.compare(this.getClass(), "Sets the module doing a consensus", "[name of module]", commandName, "setConsenser")) {	    	 		Consenser temp = (Consenser)replaceEmployee(commandRec, Consenser.class, arguments, "Consensus module", consenser);		 			if (temp!=null) {		 				consenser = temp;		 				consenser.setHiringCommand(tlsC);						consenserName.setValue(consenser.getName());			    	 		parametersChanged(null, commandRec);		 			}		 			return temp;		    	 	}		   	 	else    	 		return super.doCommand(commandName, arguments, commandRec, checker); //   	 	return null;    	 }	/*.................................................................................................................*/  	public void setPreferredTaxa(Taxa taxa){   		oldTaxa = taxa;  	}	/*.................................................................................................................*/   	/** Called to provoke any necessary initialization.  This helps prevent the module's intialization queries to the user from   	happening at inopportune times (e.g., while a long chart calculation is in mid-progress)*/   	public void initialize(Taxa taxa, CommandRecord commandRec){   		if (treeSource!=null)   			treeSource.initialize(taxa, commandRec); //WHEN this module takes in tree from a source, should ask to initialize that source   	}	/*.................................................................................................................*/   	public Tree getTree(Taxa taxa, int ic, CommandRecord commandRec) {     		oldTaxa = taxa;   		Trees trees = new TSourceWrapper(treeSource, taxa);   		for (int i= 0; i<trees.getNumberOfTrees(); i++)   			System.out.println("tree " + i + " " + trees.getTree(i).writeTree());   		Tree tree = consenser.consense(trees, commandRec);   		if (tree instanceof MesquiteTree)   			((MesquiteTree)tree).setName("Consensus tree");   		return tree;   	}	/*.................................................................................................................*/   	public int getNumberOfTrees(Taxa taxa, CommandRecord commandRec) {    	 	return 1;   	}   	/*.................................................................................................................*/   	public String getTreeNameString(Taxa taxa, int itree, CommandRecord commandRec) {   		return "Consensus tree";   	}	/*.................................................................................................................*/   	public String getParameters() {   		return"Consensus tree of trees from " + treeSource.getName();   	}	/*.................................................................................................................*/    	 public String getName() {		return "Consensus tree";   	 }	/*.................................................................................................................*/  	 public String getExplanation() {		return "Supplies consensus tree from a block of trees.";   	 } 	/*.................................................................................................................*/  	public void employeeParametersChanged(MesquiteModule employee, MesquiteModule source, Notification notification, CommandRecord commandRec) {  		if (commandRec.scripting())  			return;  		parametersChanged(null, commandRec); 	}  	 	/*.................................................................................................................*/  	 public CompatibilityTest getCompatibilityTest() {  	 	return new ConsCompatibilityTest();  	 }   	public boolean isPrerelease(){   		return false;   	}	public boolean requestPrimaryChoice() { return true; } //WPM 06 set to true}class ConsCompatibilityTest extends CompatibilityTest{	public  boolean isCompatible(Object obj, MesquiteProject project, EmployerEmployee prospectiveEmployer){		if (prospectiveEmployer != null)			return prospectiveEmployer.numModulesAvailable(Consenser.class)>0;		else			return MesquiteTrunk.mesquiteTrunk.numModulesAvailable(Consenser.class)>0;	}}	class TSourceWrapper implements Trees {		TreeSourceDefinite source;		Taxa taxa;   		TSourceWrapper(TreeSourceDefinite list, Taxa taxa){   			this.source = list;   			this.taxa = taxa;   		}   		/** Get the taxa to which the trees applies */   		public Taxa getTaxa(){   			return taxa;   		}   		   		public Tree getTree(int i){   			return source.getTree(taxa, i, CommandRecord.getRecNSIfNull());   		}   		   		public int getNumberOfTrees(){   			return source.getNumberOfTrees(taxa, CommandRecord.getRecNSIfNull());   		}  		   	}