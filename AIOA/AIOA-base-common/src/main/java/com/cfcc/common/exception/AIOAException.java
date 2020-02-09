package com.cfcc.common.exception;

public class AIOAException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public AIOAException(String message){
		super(message);
	}
	
	public AIOAException(Throwable cause)
	{
		super(cause);
	}
	
	public AIOAException(String message, Throwable cause)
	{
		super(message,cause);
	}
}
