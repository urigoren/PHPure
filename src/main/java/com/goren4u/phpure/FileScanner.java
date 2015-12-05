package com.goren4u.phpure;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;


public class FileScanner
{
	private String _extension;
	public FileScanner(String extension)
	{
		_extension=extension;
	}
	
	public ArrayList<File> Scan(String BaseDir)
	{
	    ArrayList<File> allFiles = new ArrayList<File>();
	    addTree(new File(BaseDir), allFiles);
	    return allFiles;
	}

	private void addTree(File file, Collection<File> all) {
	    File[] children = file.listFiles();
	    if (children != null) {
	        for (File child : children)
	        {
	        	if (child.isFile() && child.getName().endsWith(_extension))
	        		all.add(child);
	            addTree(child, all);
	        }
	    }
	}
}
