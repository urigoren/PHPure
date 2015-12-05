package com.goren4u.phpure;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;


public class Runner
{

	private static long sumLines(ArrayList<FunctionDetails> funcs)
	{
		long ret=0;
		for (FunctionDetails f : funcs)
		{
			ret+=f.LineEnd-f.LineStart;
		}		
		return ret;
	}
	
	public static void main(String[] args)
	{
		String basePath;
	    boolean doThePatching=false;
		
	    if (args.length<=0)
	    {
	    	System.out.println("Please specify source directory");
	    	return;
	    }
	    basePath=args[0];
	    String loggerPath=basePath+"/logger.txt";
	    
	    System.out.println("Scanning "+basePath+" ...");
	    
		//scan all the php file in the folder tree
		FileScanner phpScanner = new FileScanner(".php");
		
		ArrayList<File> all = phpScanner.Scan(basePath);
	    ArrayList<FunctionDetails> allFuncs=new ArrayList<FunctionDetails>();
	    
	    //for each file, list all the function it has
	    for (Iterator<File> iterator = all.iterator(); iterator.hasNext();) {
	    	File f=iterator.next();
			FunctionScanner func= new FunctionScanner(f);
			func.addFunctionList(allFuncs);
		}
	    
	    /*
	    System.out.println("All Functions");
	    System.out.println(allFuncs);
	    */
	    
	    //filter out only the pure functions
	    FunctionPurityTest purify=new FunctionPurityTest(allFuncs);
	    ArrayList<FunctionDetails> pureFuncs=purify.getFunctionList();
	    
	    System.out.println("Pure Functions");
	    System.out.println(pureFuncs);
	    
	    //calculate coverage rate
	    double coverageRate=sumLines(pureFuncs)*100.0/sumLines(allFuncs);
	    System.out.println("Expected Code Coverage: "+coverageRate+"%");
	    
	    //filter all the files that has a pure function within them
	    all=purify.getFileList();

	    if (doThePatching)
	    {
		    //patch the pure files
		    FunctionPatcher patch=null;
		    try
		    {
			    for (Iterator<File> iterator = all.iterator(); iterator.hasNext();)
			    {
			    	File f=iterator.next();
			    	patch=new FunctionPatcher(f);
			    	patch.Backup();
			    	System.out.println("Patching "+f.getAbsolutePath()+"\n");
					patch.PatchFunctions(pureFuncs,loggerPath);
				}
			}
		    catch (IOException e)
		    {
				e.printStackTrace();
			}
	    }
	    System.out.println("DONE!");

	}

}
