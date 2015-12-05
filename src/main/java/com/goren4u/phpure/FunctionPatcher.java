package com.goren4u.phpure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class FunctionPatcher extends FunctionFileHandler
{
	private File _phpFile;
	public FunctionPatcher(File phpFile)
	{
		_phpFile=phpFile;
		GetFileLines(phpFile);
	}
	private File BackupFile()
	{
		return new File(_phpFile.getAbsolutePath()+".uri");
	}
	public void Backup() throws IOException
	{
		if (BackupFile().exists())
			return;
		copyFiles(_phpFile,BackupFile());
	}
	public void Restore(boolean keepBackup) throws IOException
	{
		copyFiles(BackupFile(),_phpFile);
		if (!keepBackup)
			BackupFile().delete();
	}
	public void PatchFunctions(ArrayList<FunctionDetails> pureFuncs,String loggerPath) throws IOException
	{
        BufferedWriter writer = null;
        File outFile = _phpFile;

        writer = new BufferedWriter(new FileWriter(outFile));
		
        //write the wrapper functions
        writer.write("<?php\n");
        writer.write("//PHPure auto-generated code\n");
        //save function
        writer.write("if (!function_exists('phpure_save')) {\n");
        writer.write("function phpure_save($ppure) {\n");
        //save all the super globals
		String[] superGlobals={"SERVER","GET","POST","FILES","COOKIE","SESSION","REQUEST","ENV"};
		for (String sg : superGlobals)
		{
			writer.write("$pp=array();\n");
			writer.write("foreach ($_"+sg+" as $k=>$v)\n");
			writer.write("{\n\t$pp[$k]=$v;\n}\n");
			writer.write("$ppure['"+sg+"']=$pp;\n");
		}
		//save the logged data to a file
		writer.write("$seperator=\"\\n~~~~~~~~~~~~~~~~\\n\";\n");
		writer.write("if (!array_key_exists('phpure', $_REQUEST))\n");
		writer.write("{$_REQUEST['phpure']=$seperator;}\n");
		writer.write("$file=fopen('"+loggerPath+"','a');\n");
		writer.write("if ($file) {\n");
		writer.write("fwrite($file,$_REQUEST['phpure']);\n");
		writer.write("fwrite($file,serialize($ppure));\n");
		writer.write("$_REQUEST['phpure']=$seperator;\n");
		writer.write("fclose($file);\n} else {\n");
		writer.write("$_REQUEST['phpure'].=serialize($ppure).$seperator;\n");
        writer.write("}}}\n");
        
		for (int j=0;j<pureFuncs.size();j++)
		{
			FunctionDetails fd=pureFuncs.get(j);
			if (_phpFile.getAbsolutePath()!=fd.FilePath)
				continue;
			writer.write(fd.Declaration+"\n{\n");
			writer.write("$ppure=array();\n");
			writer.write("$ppure['function']='"+fd.Name+"';\n");
			writer.write("$ppure['path']='"+fd.FilePath.replace("\\", "\\\\")+"';\n");
			writer.write("$ppure['args']=array("+fd.Args+");\n");
			writer.write("ob_start();\n");
			writer.write("$ppure['retval']="+fd.Name+"_phpure("+fd.Args+");\n");
			writer.write("$ppure['echo']=ob_get_flush();\n");
			writer.write("phpure_save($ppure);\n");
			writer.write("return $ppure['retval'];\n");
			writer.write("}\n");
		}
		writer.write("//End of PHPure auto-generated code\n");
		writer.write("?>");
		
		//write original file content
		for(int i=0;i<_fileLines.length;i++)
		{
			String line=_fileLines[i];
			for (int j=0;j<pureFuncs.size();j++)
			{
				FunctionDetails fd=pureFuncs.get(j);
				if (_phpFile.getAbsolutePath()!=fd.FilePath)
					continue;
				String FuncName=pureFuncs.get(j).Name;
				if (i==fd.LineStart)
				{
					//replace function declaration
					line=line.replace(FuncName, FuncName+"_phpure");
				}
				//replace any function reference as string, i.e. function_exists("functionName")
				line=line.replace("'"+FuncName+"'", "'"+FuncName+"_phpure'");
				line=line.replace("\""+FuncName+"\"", "'"+FuncName+"_phpure'");
			}
			writer.write(line+"\n");
		}
		
        writer.close();
	}

}
