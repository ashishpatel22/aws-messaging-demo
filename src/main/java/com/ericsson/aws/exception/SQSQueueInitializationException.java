package com.ericsson.aws.exception;

/**
 * 
 * @author ekumaaa
 *
 */
public class SQSQueueInitializationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8123073143769156259L;

	public SQSQueueInitializationException(String message) {
		super(message);
	}

	public SQSQueueInitializationException(String message, Throwable ex) {
		super(message, ex);
	}
}
