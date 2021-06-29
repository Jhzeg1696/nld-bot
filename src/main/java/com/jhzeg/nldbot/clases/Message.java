package com.jhzeg.nldbot.clases;

import com.jhzeg.nldbot.utilities.HttpRequest;

public class Message {
	
	public static void send(String senderID, String message)
	{
		// Create the http object
		HttpRequest httpRequest = new HttpRequest();
		
		// Send the request to the graph API 
		String response = httpRequest.send(senderID, message);
		
		System.out.println(response);
	}
	
	public static void sendImage(String senderID, String image)
	{
		// Create the http object
		HttpRequest httpRequest = new HttpRequest();
				
		// Send the request to the graph API 
		String response = httpRequest.sendImage(senderID, image);
				
		System.out.println(response);
	}
}
