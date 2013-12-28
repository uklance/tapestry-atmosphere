package org.lazan.t5.atmosphere.demo.pages;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

public class ChatDemo {
	@Property
	private String message;
	
	@Inject
	private Block messageBlock;
	
	public Block onChat1(String message) {
		this.message = message;
		return messageBlock;
	}
	
	public Block onChat2(String message) {
		this.message = message;
		return messageBlock;
	}
}
