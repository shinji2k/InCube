/**
 * 
 */
package com.crscic.incube.exception;


public class ParseXMLException extends Exception
{
	private static final long serialVersionUID = -8548203948005122887L;
	
	public ParseXMLException(String errMsg)
	{
		super(errMsg);
	}
	
	public ParseXMLException()
	{
		super();
	}
}
