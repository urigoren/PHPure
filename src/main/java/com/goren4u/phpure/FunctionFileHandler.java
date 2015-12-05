package com.goren4u.phpure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public abstract class FunctionFileHandler
{
	protected String[] _fileLines;
	protected static void copyFiles(File source, File dest)
			throws IOException {
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(source);
			output = new FileOutputStream(dest);
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
		} finally {
			input.close();
			output.close();
		}
	}
	protected void GetFileLines(File phpFile)
	{
		String fileContent="";
		try
		{
			fileContent=ReadFile(phpFile);
		} catch (Exception e) { }
		//remove comments
		//fileContent=fileContent.replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)","");
		//{ and } are in their own line
		fileContent=fileContent.replace("{", "\n{\n").replace("}", "\n}\n").replaceAll("\\n\\s+\\n", "\n");
		_fileLines=fileContent.split("\n");
	}
	
	protected String ReadFile(File phpFile) throws IOException
	{
	    BufferedReader br = new BufferedReader(new FileReader(phpFile));
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();

        while (line != null) {
            sb.append(line);
            sb.append("\n");
            line = br.readLine();
        }
        String ret= sb.toString();
        br.close();
        return ret;
	}
}
