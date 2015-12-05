package com.goren4u.phpure;

public class FunctionDetails
{
	public String FilePath;
	public String Body;
	public String Name;
	public String Args;
	public String Declaration;
	public String[] functionCalls;
	public int isPure;
	public int LineEnd;
	public int LineStart;
	@Override
	public String toString()
	{
		return Declaration.replace("function ","");
	}
}
