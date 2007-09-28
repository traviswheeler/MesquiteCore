/* Mesquite source code.  Copyright 1997-2007 W. Maddison and D. Maddison.Version 2.0, September 2007.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.lib;import java.awt.*;import java.util.*;import java.io.*;import mesquite.lib.duties.*;/* ======================================================================== */public class ShellScriptUtil  {	    	/*.................................................................................................................*/    	public static String getWriteStringAsFile(String path, String contents) {    		if (MesquiteTrunk.isWindows())    			return "echo \"" + contents + "\" > " + StringUtil.protectForWindows(path) + StringUtil.lineEnding();    		else    			return "echo \"" + contents + "\" > " + StringUtil.protectForUnix(path) + StringUtil.lineEnding();    	}    	    	/*.................................................................................................................*    	String getAliasCommand(String alias, String expansion){    		return "alias " +StringUtil.protectForUnix(alias) + " " + StringUtil.protectForUnix(expansion) +StringUtil.lineEnding();    	}    	/*.................................................................................................................*/    	public static String getLinkCommand(String path, String aliasPath){    		if (MesquiteTrunk.isWindows())    			return "shortcut -f -t " +StringUtil.protectForWindows(path) + " -n " + StringUtil.protectForUnix(aliasPath) +StringUtil.lineEnding();    		else    			return "ln  " +StringUtil.protectForUnix(path) + " " + StringUtil.protectForUnix(aliasPath) +StringUtil.lineEnding();    	}    	/*.................................................................................................................*/    	public static String getChangeDirectoryCommand(String directory){    		String directoryString;    		if (MesquiteTrunk.isWindows()) {    			directoryString = StringUtil.protectForWindows(directory);    		} else {    			directoryString = StringUtil.protectForUnix(directory);    		}    		return "cd " + directoryString +StringUtil.lineEnding();    	}    	/*.................................................................................................................*/    	public static String getRemoveCommand(String filePath){    		if (MesquiteTrunk.isWindows())    			return "del -f " + StringUtil.protectForWindows(filePath) +StringUtil.lineEnding();    		else    			return "rm -f " + StringUtil.protectForUnix(filePath) +StringUtil.lineEnding();    	}    	/*.................................................................................................................*/    	public static String getSetFileTypeCommand(String filePath){    		if (MesquiteTrunk.isMacOSX())    			return "/Developer/Tools/setFile -t TEXT " + StringUtil.protectForUnix(filePath) +StringUtil.lineEnding();    		else    			return "";    	}    	/*.................................................................................................................*/    	public static String getOpenDirectoryCommand(String directoryPath){    		if (MesquiteTrunk.isMacOSX())    			return "open " + StringUtil.protectForUnix(directoryPath) +StringUtil.lineEnding();    		else    			return "";    	}    /*.................................................................................................................*/    	public static Process executeScript(String scriptPath){ 	    		  Process proc;    		  try {    		    if (MesquiteTrunk.isMacOSX())    		      proc = Runtime.getRuntime().exec(new String[] {"open", "-a","/Applications/Utilities/Terminal.app", scriptPath} );    		    else if (MesquiteTrunk.isLinux()) {    		    	// remove double slashes or things won't execute properly    		    	scriptPath = scriptPath.replaceAll("//", "/");    		    	proc = Runtime.getRuntime().exec(scriptPath);    		    } else {    		    	scriptPath = "\"" + scriptPath + "\"";    		    	String[] cmd = {"cmd", "/c", scriptPath};    		    	proc = Runtime.getRuntime().exec(cmd);    		    }    		  }  catch (IOException e) {    		    MesquiteMessage.println("Script execution failed.");    		    return null;    		  }    		  if (proc != null) {    			  StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");    			  StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT");    			  errorGobbler.start();    			  outputGobbler.start();    		  }    		  return proc;    		}    	/*.................................................................................................................*/    	public static boolean setScriptFileToBeExecutable(String scriptPath) throws IOException {    		Process proc;    		try {    			if (!MesquiteTrunk.isWindows())    				Runtime.getRuntime().exec(new String[] {"chmod", "+x", scriptPath } );    		}		catch (IOException e) {			MesquiteMessage.println("Script cannot be set to be executable.");			return false;		}		return true;    	}    	/*.................................................................................................................*/    	public static String getDefaultRunningFilePath(){    		Random rng = new Random(System.currentTimeMillis());    		String runningFilePath = MesquiteModule.prefsDirectory  + MesquiteFile.fileSeparator + "running" + MesquiteFile.massageStringToFilePathSafe(MesquiteTrunk.getUniqueIDBase() + Math.abs(rng.nextInt()));    		return runningFilePath;    	}   	/*.................................................................................................................*/     	/** executes a shell script at "scriptPath".  If runningFilePath is not blank and not null, then Mesquite will create a file there that will     	 * serve as a flag to Mesquite that the script is running.   */     	public static boolean executeAndWaitForShell(String scriptPath, String runningFilePath, String runningFileMessage, boolean appendRemoveCommand, String name, String logFilePath, LogFileProcessor logFileProcessor){     		try{     			ShellScriptUtil.setScriptFileToBeExecutable(scriptPath);     			if (!StringUtil.blank(runningFilePath)) {     				if (StringUtil.blank(runningFileMessage))     					MesquiteFile.putFileContents(runningFilePath, "Script running...", true);     				else     					MesquiteFile.putFileContents(runningFilePath, runningFileMessage, true);     				if (appendRemoveCommand && MesquiteFile.fileExists(runningFilePath))     					MesquiteFile.appendFileContents(scriptPath, StringUtil.lineEnding() + ShellScriptUtil.getRemoveCommand(runningFilePath), true);  //append remove command to guarantee that the runningFile is deleted     			}     			Process proc = ShellScriptUtil.executeScript(scriptPath);     			if (proc==null) {     				return false;     			}     			else if (!StringUtil.blank(runningFilePath))   // is file at runningFilePath; watch for its disappearance     				while (MesquiteFile.fileExists(runningFilePath)){     					if (logFileProcessor!=null)     						logFileProcessor.processLogFile(logFilePath);     	     			try {     						Thread.sleep(200);     					}     					catch (InterruptedException e){     						MesquiteMessage.notifyProgrammer("InterruptedException in shell script executed by " + name);     						return false;     					}     				}     		}     		catch (IOException e){     			MesquiteMessage.warnProgrammer("IOException in shell script executed by " + name);     			return false;    		}				if (logFileProcessor!=null)						logFileProcessor.processCompletedLogFile(logFilePath);    		return true;    	}      	/*.................................................................................................................*/     	/** executes a shell script at "scriptPath".  If runningFilePath is not blank and not null, then Mesquite will create a file there that will     	 * serve as a flag to Mesquite that the script is running.   */     	public static boolean executeAndWaitForShell(String scriptPath, String runningFilePath, String runningFileMessage, boolean appendRemoveCommand, String name){     		return executeAndWaitForShell( scriptPath,  runningFilePath,  runningFileMessage,  appendRemoveCommand,  name, null, null);    	}   	/*.................................................................................................................*/      	public static boolean executeAndWaitForShell(String scriptPath, String name){     		String runningFilePath = null;     		if (!StringUtil.blank(scriptPath))     			runningFilePath=getDefaultRunningFilePath();     		return executeAndWaitForShell(scriptPath, runningFilePath, null, true, name);    	}       	/*.................................................................................................................*/      	public static boolean executeLogAndWaitForShell(String scriptPath, String name, String logFilePath, LogFileProcessor logFileProcessor){     		String runningFilePath = null;     		if (!StringUtil.blank(scriptPath))     			runningFilePath=getDefaultRunningFilePath();     		return executeAndWaitForShell(scriptPath, runningFilePath, null, true, name, logFilePath, logFileProcessor);    	}}class StreamGobbler extends Thread {	InputStream is;	String type;	StreamGobbler(InputStream is, String type) {		this.is = is;		this.type = type;	}	public void run() {		try {			InputStreamReader isr = new InputStreamReader(is);			BufferedReader br = new BufferedReader(isr);			String line = null;			while ((line = br.readLine()) != null) {			}		} catch (IOException ioe) {			ioe.printStackTrace();		}	}}