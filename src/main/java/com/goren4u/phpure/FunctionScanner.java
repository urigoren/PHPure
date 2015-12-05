package com.goren4u.phpure;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FunctionScanner extends FunctionFileHandler
{
	private String _filePath;
	private ArrayList<FunctionDetails> _functions;
	private Pattern _functionPattern;
	private Pattern _functionCall;
	public FunctionScanner(File phpFile)
	{
		_functionPattern = Pattern.compile("function[\\s]+([\\w]+)[\\s]*\\(([\\$\\w\\,\\s=]*)\\)");
		_functionCall=Pattern.compile("(\\w+)[\\s]*\\([\\w,]*\\)");
		
		_filePath=phpFile.getAbsolutePath();
		GetFileLines(phpFile);
		DetectFunctions();
	}
	
	public  ArrayList<FunctionDetails> getFunctionList()	{ return _functions;}
	public  void addFunctionList(ArrayList<FunctionDetails> flist)
	{
		if (flist==null)
			flist=new ArrayList<FunctionDetails>();
		flist.addAll(_functions);
	}
	
	private String[] scanFunctionCalls(String input)
	{
		Matcher callMatch=_functionCall.matcher(input);
		TreeSet<String> set=new TreeSet<String>();
		while (callMatch.find())
		{
			set.add(callMatch.group(1));
		}
		String[] ret=new String[set.size()];
		set.toArray(ret);
		return ret;
	}
	
	private void DetectFunctions()
	{
		_functions=new ArrayList<FunctionDetails>();
		for (int i=0;i<_fileLines.length;i++)
		{
			_fileLines[i]=_fileLines[i].trim();
			if (!_fileLines[i].contains("function "))
				continue;
			Matcher funcMatch=_functionPattern.matcher(_fileLines[i]);
			if (funcMatch.find())
			{
				String funcName=funcMatch.group(1);
				if (_fileLines[i+1].equals("{"))
				{
					FunctionDetails funcDetails=new FunctionDetails();
					funcDetails.FilePath=_filePath;
					funcDetails.Name=funcName;
					funcDetails.Declaration=funcMatch.group(0);
					funcDetails.Args=removeDefaultArgs(funcMatch.group(2));
					funcDetails.LineStart=i;
					int curly_brace_count=1;
					i=i+2;
					while ((curly_brace_count>0) && (i<_fileLines.length))
					{
						if (_fileLines[i].equals("}"))
							curly_brace_count--;
						else if (_fileLines[i].equals("{"))
							curly_brace_count++;
						i++;
					}
					if (i<_fileLines.length)
					{
						funcDetails.LineEnd=i-1;
						StringBuilder sb=new StringBuilder();
						for (int j=funcDetails.LineStart+2;j<funcDetails.LineEnd;j++)
						{
							sb.append(_fileLines[j]);
							sb.append("\n");
						}
						funcDetails.Body=sb.toString();
						funcDetails.functionCalls=scanFunctionCalls(funcDetails.Body);
						_functions.add(funcDetails);
					}
				}
			}
		}
	}
	private String removeDefaultArgs(String Args)
	{
		String[] ArgsArr=Args.split(",");
		String ret="";
		for (int i=0;i<ArgsArr.length;i++)
		{
			if (i>0)
				ret+=",";
			ret+=ArgsArr[i].split("=")[0].trim();
		}
		return ret;
	}
}
