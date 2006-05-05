/* Mesquite.  Copyright 1997-2005 W. Maddison and D. Maddison. Version 1.06, August 2005.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.assoc.TaxonListAssoc;/*~~  */import mesquite.lists.lib.*;import java.util.*;import java.awt.*;import java.awt.event.*;import mesquite.lib.*;import mesquite.assoc.lib.*;import mesquite.lib.duties.*;import mesquite.lib.table.*;/* ======================================================================== */public class TaxonListAssoc extends TaxonListAssistant {	Taxa taxa, otherTaxa;	MesquiteTable table=null;	MesquiteMenuItemSpec m0, m1, m2, m3, m4;	AssociationSource associationTask;	MesquiteWindow containingWindow;	TaxaAssociation association;	AssocEditor panel;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {		associationTask = (AssociationSource)hireEmployee(commandRec, AssociationSource.class, "Source of taxon associations");		if (associationTask == null)			return sorry(commandRec, getName() + " couldn't start because no source of taxon associations obtained.");		Frame f = containerOfModule();		if (f instanceof MesquiteWindow){			containingWindow = (MesquiteWindow)f;			containingWindow.addSidePanel(panel = new AssocEditor(this), 120);		}		return true;	}	public boolean isPrerelease(){		return false;	}	public boolean canHireMoreThanOnce(){		return true;	}	boolean ignoreWhitespace=true;	boolean ignoreCase = true;	/*.................................................................................................................*/	public boolean queryOptions() {		MesquiteInteger buttonPressed = new MesquiteInteger(1);		ExtensibleDialog dialog = new ExtensibleDialog(MesquiteTrunk.mesquiteTrunk.containerOfModule(), "Auto-assign Options",buttonPressed);  //MesquiteTrunk.mesquiteTrunk.containerOfModule()				Checkbox ignoreWhiteCheckBox= dialog.addCheckBox("ignore whitespace (spaces, tabs, etc.)", ignoreWhitespace);		Checkbox ignoreCaseCheckBox= dialog.addCheckBox("ignore case", ignoreCase);//		SingleLineTextField clustalOptionsField = queryFilesDialog.addTextField("Clustal options:", clustalOptions, 26, true);		dialog.completeAndShowDialog(true);		if (buttonPressed.getValue()==0)  {			ignoreCase = ignoreCaseCheckBox.getState();			ignoreWhitespace = ignoreWhiteCheckBox.getState();			//storePreferences();		}		dialog.dispose();		return (buttonPressed.getValue()==0);	}	/*.................................................................................................................*/	private void autoAssign(boolean ignoreWhitespace, boolean ignoreCase, CommandRecord commandRec){		boolean changed = false;		if (taxa!=null && association != null) {			Taxa otherTaxa = association.getOtherTaxa(taxa);			for (int it=0; it<taxa.getNumTaxa(); it++)				for (int ito = 0; ito<otherTaxa.getNumTaxa(); ito++){					String name = taxa.getTaxonName(it);					String nameOther = otherTaxa.getTaxonName(ito);					if (name == null || nameOther == null)						continue;					boolean matches = name.equals(nameOther);					if (!matches && ignoreCase)						matches = name.equalsIgnoreCase(nameOther);					if (!matches && ignoreWhitespace) {						String strippedName = StringUtil.removeCharacters(name, StringUtil.defaultWhitespace);						String strippedNameOther = StringUtil.removeCharacters(nameOther, StringUtil.defaultWhitespace);						matches = strippedName.equals(strippedNameOther);						if (!matches && ignoreCase)							matches = strippedName.equalsIgnoreCase(strippedNameOther);					}					if (matches){						//association.zeroAllAssociations(taxa.getTaxon(it));						association.setAssociation(taxa.getTaxon(it), otherTaxa.getTaxon(ito), true);						changed = true;					}				}						if (changed) association.notifyListeners(this, new Notification(MesquiteListener.VALUE_CHANGED));		}	}	/*.................................................................................................................*/	private void setAssociate(Taxon taxon, boolean add, boolean append, CommandRecord commandRec){		if (table !=null && taxa!=null && association != null) {			boolean changed=false;			if (add){				Taxa otherTaxa = association.getOtherTaxa(taxa);				if (taxon == null)					taxon = otherTaxa.userChooseTaxon(containerOfModule(), "Select the taxon to be associated with the selected rows");				if (taxon == null)					return;			}			if (employer!=null && employer instanceof ListModule) {				int c = ((ListModule)employer).getMyColumn(this);				for (int i=0; i<taxa.getNumTaxa(); i++) {					if (table.isCellSelectedAnyWay(c, i)) {						Taxon t = taxa.getTaxon(i);						if (!append)							association.zeroAllAssociations(t);						if (add)							association.setAssociation(t, taxon, true);						changed = true;					}				}			}			if (changed) {				association.notifyListeners(this, new Notification(MesquiteListener.UNKNOWN), commandRec);  				parametersChanged(null, commandRec);			}		}	}	MesquiteInteger pos = new MesquiteInteger(0);	/*.................................................................................................................*/	public Snapshot getSnapshot(MesquiteFile file) { 		Snapshot temp = new Snapshot();		temp.addLine("getAssociationsTask " + associationTask); 		return temp;	}	/*.................................................................................................................*/	public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {		if (checker.compare(this.getClass(), "Gets the current associations module", null, commandName, "getAssociationsTask")) {			return associationTask;		}		else  if (checker.compare(this.getClass(), "Automatically sets associates if there is an exact match of names", null, commandName, "autoAssignExact")) {			if (queryOptions())				autoAssign(ignoreWhitespace, ignoreCase, commandRec);		}		else  if (checker.compare(this.getClass(), "Sets which other taxon is associated with these; replaces existing", null, commandName, "setAssociate")) {			setAssociate(null, true, false, commandRec);		}		else if (checker.compare(this.getClass(), "Sets which other taxon is associated with these; adds to existing", null, commandName, "addAssociate")) {			setAssociate(null, true, true, commandRec);		}		else if (checker.compare(this.getClass(), "Creates a new taxon and adds to existing", null, commandName, "createAssociate")) {			if (association == null)				return null;			Taxa otherTaxa = association.getOtherTaxa(taxa);			otherTaxa.addTaxa(otherTaxa.getNumTaxa()-1, 1, false);			Taxon t = otherTaxa.getTaxon(otherTaxa.getNumTaxa()-1);			String n = MesquiteString.queryString(containerOfModule(), "Name of Taxon", "Name the new taxon", "Taxon");			t.setName(n);			otherTaxa.notifyListeners(this, new Notification(MesquiteListener.PARTS_ADDED), commandRec);			setAssociate(t, true, true, commandRec);		}		else if (checker.compare(this.getClass(), "Deletes associations", null, commandName, "removeAssociates")) {			setAssociate(null, false, false, commandRec);		}		else			return super.doCommand(commandName, arguments, commandRec, checker);		return null;	}	public boolean isShowing(TaxaAssociation assoc){		return assoc == association;	}	/*.................................................................................................................*/	public void setTableAndTaxa(MesquiteTable table, Taxa taxa, CommandRecord commandRec){		deleteMenuItem(m0);		deleteMenuItem(m1);		deleteMenuItem(m2);		deleteMenuItem(m3);		deleteMenuItem(m4);		m0 = addMenuItem(null, "Auto-assign Matches...", makeCommand("autoAssignExact", this));		m1 = addMenuItem(null, "Assign Associate...", makeCommand("setAssociate", this));		m2 = addMenuItem(null, "Add Associate...", makeCommand("addAssociate", this));		m3 = addMenuItem(null, "Remove Associates", makeCommand("removeAssociates", this));		m4 = addMenuItem(null, "Create New Associated Taxon...", makeCommand("createAssociate", this));		if (this.taxa != null)			taxa.removeListener(this);		this.taxa = taxa;		if (this.taxa != null)			taxa.addListener(this);		this.table = table;		resetAssociation(commandRec);	}	void resetAssociation(CommandRecord commandRec){		association = associationTask.getCurrentAssociation(taxa, commandRec); 		if (association == null)			association = associationTask.getAssociation(taxa, 0, commandRec); 		if (this.otherTaxa != null)			otherTaxa.removeListener(this);		if (association == null)			otherTaxa=null; 		else if (association.getTaxa(0)== taxa)			otherTaxa = association.getTaxa(1);		else			otherTaxa = association.getTaxa(0);		if (this.otherTaxa != null)			otherTaxa.addListener(this);		panel.setAssociation(association, otherTaxa, taxa);	}	/*.................................................................................................................*/	public void employeeParametersChanged(MesquiteModule employee, MesquiteModule source, Notification notification, CommandRecord commandRec) {		resetAssociation(commandRec);		parametersChanged(notification, commandRec);	}	public void changed(Object caller, Object obj, Notification notification, CommandRecord commandRec){		if (Notification.appearsCosmetic(notification)){			if (panel != null)				panel.reset();			return;		}		if (panel != null)			panel.prepareList();		outputInvalid(commandRec);		parametersChanged(notification, commandRec);	}	public String getTitle() {		if (otherTaxa != null && otherTaxa.getName() != null && !otherTaxa.getName().startsWith("Untitled"))			return otherTaxa.getName();		return "Associates";	}	Taxon[] associates;	public String getStringForTaxon(int ic){		if (taxa!=null) {			//if (association==null)			//	resetAssociation(CommandRecord.nonscriptingRecord);			if (association==null)				return "?";			if (associates==null ||  associates.length < otherTaxa.getNumTaxa())				associates = new Taxon[otherTaxa.getNumTaxa()];			associates = association.getAssociates(taxa.getTaxon(ic), associates);			if (associates!= null) {				String s = "";				boolean first = true;				for (int i=0; i<associates.length; i++)					if (associates[i]!=null){						if (!first)							s += ", ";						s += associates[i].getName();						first = false;					}				return s;			}			return "-";		}		return "?";	}	public boolean useString(int ic){		return true;	}	/*public void drawInCell(int ic, Graphics g, int x, int y,  int w, int h, boolean selected){		if (taxa==null || g==null)			return;		TaxaPartition part = (TaxaPartition)taxa.getCurrentSpecsSet(TaxaPartition.class);		Color c = g.getColor();		boolean colored = false;		if (part!=null) {			TaxaGroup tg = part.getTaxaGroup(ic);			if (tg!=null){				Color cT = tg.getColor();				if (cT!=null){					g.setColor(cT);					g.fillRect(x+1,y+1,w-1,h-1);					colored = true;				}			}		}		if (!colored){ 			if (selected)				g.setColor(Color.black);			else				g.setColor(Color.white);			g.fillRect(x+1,y+1,w-1,h-1);		}		String s = getStringForRow(ic);		if (s!=null){			FontMetrics fm = g.getFontMetrics(g.getFont());			if (fm==null)				return;			int sw = fm.stringWidth(s);			int sh = fm.getMaxAscent()+ fm.getMaxDescent();			if (selected)				g.setColor(Color.white);			else				g.setColor(Color.black);			g.drawString(s, x+(w-sw)/2, y+h-(h-sh)/2);			g.setColor(c);		}	}	 */	public String getWidestString(){		return "88888888888  ";	}	/*.................................................................................................................*/	public String getName() {		return "Associated Taxa";	}	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */	public boolean requestPrimaryChoice(){		return true;  	}	public void endJob() {		if (panel != null && containingWindow != null)			containingWindow.removeSidePanel(panel);		if (this.taxa != null)			taxa.removeListener(this);		if (this.otherTaxa != null)			otherTaxa.removeListener(this);		super.endJob();	}	/*.................................................................................................................*/	/** returns an explanation of what the module does.*/	public String getExplanation() {		return "Lists what other taxa (e.g. contained or containing) are associated with these." ;	}}/*=======================================*/class AssocEditor extends MousePanel implements ActionListener{	java.awt.List list;	Font df = new Font("Dialog", Font.BOLD, 12);	TaxaAssociation assoc;	Taxa otherTaxa, taxa;	TaxonListAssoc ownerModule;	Button button, rbutton;	TextArea text;	int titleH = 18;	int nameH = 30;	int buttonH = 25;	public AssocEditor(TaxonListAssoc ownerModule){		super();		setLayout(null);		text = new TextArea(" ", 50, 50, TextArea.SCROLLBARS_NONE);		add(text);		text.setVisible(true);		text.setBounds(0, titleH, getBounds().width, nameH);		text.setBackground(Color.darkGray);		text.setForeground(Color.white);		button = new Button("<< Assign");		add(button);		button.setVisible(true);		button.setLocation(8, titleH + nameH+2);		button.setSize(60, buttonH-8);		rbutton = new Button(" - ");		add(rbutton);		rbutton.setVisible(true);		rbutton.setLocation(70, titleH + nameH+2);		rbutton.setSize(16, buttonH-8);		//button.setBackground(Color.green);		list = new java.awt.List();		add(list);		list.setLocation(0,titleH + nameH + buttonH);		list.setSize(getBounds().width, getBounds().height-titleH + nameH + buttonH);		list.setVisible(true);		setBackground(Color.darkGray);		list.setBackground(Color.white);		this.ownerModule  = ownerModule;		list.setMultipleMode(true);		button.addActionListener(this);		rbutton.addActionListener(this);	}	public void actionPerformed(ActionEvent e){		if (e.getSource() == button)			assignAssociates();		else if (e.getSource() == rbutton)			removeAssociates();	}	void assignAssociates(){		if (assoc == null)			return;		int[] selectedInList = list.getSelectedIndexes();		if (selectedInList == null)			return;		for (int it= 0; it< taxa.getNumTaxa(); it++){			if (taxa.getSelected(it)){				assoc.zeroAllAssociations(taxa.getTaxon(it));				for (int ito = 0; ito<selectedInList.length; ito++)					assoc.setAssociation(taxa.getTaxon(it), otherTaxa.getTaxon(selectedInList[ito]), true);			} 		}		assoc.notifyListeners(this, new Notification(MesquiteListener.VALUE_CHANGED));	}	void removeAssociates(){		if (assoc == null)			return;		for (int it= 0; it< taxa.getNumTaxa(); it++){			if (taxa.getSelected(it)){				Debugg.println("remove " + it);				assoc.zeroAllAssociations(taxa.getTaxon(it));			}		}		assoc.notifyListeners(this, new Notification(MesquiteListener.VALUE_CHANGED));	}	void setAssociation(TaxaAssociation assoc, Taxa otherTaxa, Taxa taxa){		this.assoc = assoc;		text.setText(assoc.getName());		this.otherTaxa = otherTaxa;		this.taxa = taxa;		prepareList();	}	void reset(){		for (int i= 0; i<otherTaxa.getNumTaxa(); i++)			list.deselect(i);		if (assoc == null)			return;		for (int it= 0; it< taxa.getNumTaxa(); it++){			if (taxa.getSelected(it)){				Taxon[] associates = assoc.getAssociates(taxa.getTaxon(it));				if (associates != null)					for (int ito = 0; ito < associates.length; ito++)						list.select(associates[ito].getNumber());			}		}		repaint();		button.repaint();	}	boolean isAssignedSomewhere(int ito){		Taxon oT = otherTaxa.getTaxon(ito);		for (int it= 0; it< taxa.getNumTaxa(); it++){			Taxon[] associates = assoc.getAssociates(taxa.getTaxon(it));			if (associates != null)				for (int i = 0; i < associates.length; i++)					if (associates[i] == oT)						return true;		}		return false;	}	void prepareList(){		if (otherTaxa != null && assoc != null){			list.removeAll();			for (int i= 0; i<otherTaxa.getNumTaxa(); i++){				if (isAssignedSomewhere(i))					list.add("      " + otherTaxa.getTaxonName(i));				else					list.add(otherTaxa.getTaxonName(i));			}		}		reset();	}	public void setSize(int w, int h){		super.setSize(w, h);		list.setSize(w, h-titleH + nameH + buttonH);		text.setBounds(0, titleH, getBounds().width, nameH);		repaint();		text.repaint();		list.repaint();	}	public void setBounds(int x, int y, int w, int h){		super.setBounds(x, y, w, h);		list.setSize(w, h-titleH + nameH + buttonH);		text.setBounds(0, titleH, getBounds().width, nameH);		repaint();		text.repaint();		list.repaint();	}	public void paint(Graphics g){		g.setFont(df);		g.setColor(Color.white);		g.drawString(ownerModule.getTitle(), 8, 16);	}}