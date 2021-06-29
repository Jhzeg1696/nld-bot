package com.jhzeg.nldbot.controllers;

import java.awt.List;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jhzeg.nldbot.clases.BotAnswers;
import com.jhzeg.nldbot.clases.Conversation;
import com.jhzeg.nldbot.clases.Menu;
import com.jhzeg.nldbot.clases.Message;
import com.jhzeg.nldbot.clases.Restaurants;
import com.jhzeg.nldbot.clases.User;
import com.jhzeg.nldbot.utilities.FileManipulator;
import com.jhzeg.nldbot.utilities.HttpRequest;

@RestController
public class WebhookController {
	
	@GetMapping("/webhook")
	public ResponseEntity webhook(@RequestParam(value = "hub.mode", defaultValue = "empty") String hubMode,
								  @RequestParam( value = "hub.verify_token", defaultValue = "empty") String hubVerifyToken,
								  @RequestParam(value = "hub.challenge", defaultValue = "empty") String hubChallenge)
	{
		// Your verify token. Should be a random string.
		String VERIFY_TOKEN = "fb_nld_bot";
		
		// Checks if a token and mode is in the query string of the request
		if(!hubMode.isEmpty() && !hubVerifyToken.isEmpty())
		{
			// Checks the mode and token sent is correct
			if(hubMode.equals("subscribe") && hubVerifyToken.equals(VERIFY_TOKEN))
			{
				System.out.println("Webhook verified");
				return ResponseEntity.status(200).body(hubChallenge);
			}
			// If not return 403 Forbidden code
			else
			{
				System.out.println("Webhook verification failed");
				return ResponseEntity.status(403).body("Forbidden");
			}
		}
		// If not return 400 Bad Request code
		else
		{
			return ResponseEntity.status(400).body("Bad Request");
		}
	}
	
	@PostMapping("/webhook")
	public ResponseEntity webhook(RequestEntity<String> requestEntity) 
	{
		// Getting the post data body
		JsonObject body = new Gson().fromJson(requestEntity.getBody(), JsonObject.class);		
		//System.out.println("THIS IS THE BODY OF THE POST" + body.toString());
		
		// Checks if this is an event from a page subscription
		if(body.get("object").toString().equals("\"page\"")) 
		{
			// Get the entry array
			JsonArray entry = body.get("entry").getAsJsonArray();
			
			// Get the messaging array
			JsonArray messaging = entry.get(0).getAsJsonObject().get("messaging").getAsJsonArray();
			
			// Loop through the elements inside the messaging array
			for(JsonElement element : messaging)
			{				
				// Get the sender object
				JsonObject sender = element.getAsJsonObject().get("sender").getAsJsonObject();
				
				// Get the message object
				JsonObject message = element.getAsJsonObject().get("message").getAsJsonObject();
				
				// Check if the event is a message or postback 
				if(element.getAsJsonObject().has("message"))
				{
					if(element.getAsJsonObject().get("message").getAsJsonObject().has("text"))
					{	
						if(stripAccents(message.get("text").toString().toLowerCase().replace("\"", "")).equals("seria todo") || 
								stripAccents(message.get("text").toString().toLowerCase().replace("\"", "")).equals("salir"))
						{
							Conversation.releaseContext(sender.get("id").toString().replace("\"", ""));	
						}
						
						if(stripAccents(message.get("text").toString().toLowerCase().replace("\"", "")).contains("menu"))
						{
							Message.send(sender.get("id").toString().replace("\"", ""), "Estoy buscando el menú, dame un segundo 🤓");
							String menuImageUrl = Menu.getUrl(sender.get("id").toString().replace("\"", ""),message.get("text").toString().replace("\"", ""));
							//Message.sendImage(sender.get("id").toString().replace("\"", ""), menuImageUrl);
							Conversation.releaseContext(sender.get("id").toString().replace("\"", ""));	
						}
						
						if(Conversation.getContext(sender.get("id").toString().replace("\"", "").toString()).equals("restaurantes"))
						{
							// Get the bot answer to the user message
							HashMap<String, String> botAnswer = BotAnswers.getResponseToMessage(sender.get("id").toString().replace("\"", "").toString(), stripAccents(message.get("text").toString().toLowerCase().replace("\"", "")), "restaurantes");
							
							// Check the last bot answer to determinate the response
							if(Conversation.getLastBotMessage(sender.get("id").toString().replace("\"", "")).equals("Que te gustaria comer?"))
							{
								// Search for restaurants with the desired category 
								Restaurants.sendRestaurantsMessage(sender.get("id").toString().replace("\"", ""),message.get("text").toString().replace("\"", ""));
								
								/*
								if(!User.userExists(sender.get("id").toString().replace("\"", "")))
								{
									User.saveUser(sender.get("id").toString().replace("\"", ""), botAnswer.get("userMessage"), botAnswer.get("botAnswer"), 
												  botAnswer.get("conversationContext"));
								}
								
								else
								{
									User.updateUser(sender.get("id").toString().replace("\"", ""), botAnswer.get("userMessage"), botAnswer.get("botAnswer"), 
												  botAnswer.get("conversationContext"));
								}
								*/
								// If the element is a message pass the event to the appropriate handler function
								//handleMessage(sender.get("id").toString(),"Buscaste: " + botAnswer.get("userMessage"));
							}
							
							else
							{
								// If the element is a message pass the event to the appropriate handler function
								handleMessage(sender.get("id").toString(), botAnswer.get("botAnswer"));
								
								if(!User.userExists(sender.get("id").toString().replace("\"", "")))
								{
									User.saveUser(sender.get("id").toString().replace("\"", ""), botAnswer.get("userMessage"), botAnswer.get("botAnswer"), 
												  botAnswer.get("conversationContext"));
								}
								
								else
								{
									User.updateUser(sender.get("id").toString().replace("\"", ""), botAnswer.get("userMessage"), botAnswer.get("botAnswer"), 
												  botAnswer.get("conversationContext"));
								}
							}
							
							
						}
						
						else 
						{
							if(stripAccents(message.get("text").toString().toLowerCase().replace("\"", "")).equals("quiero buscar restaurantes"))
							{
								Conversation.updateContext(sender.get("id").toString().replace("\"", ""), "restaurantes");	
								
								// Get the bot answer to the user message
								HashMap<String, String> botAnswer = BotAnswers.getResponseToMessage(sender.get("id").toString().replace("\"", "").toString(), stripAccents(message.get("text").toString().toLowerCase().replace("\"", "")), "restaurantes");
								
								// If the element is a message pass the event to the appropriate handler function
								handleMessage(sender.get("id").toString(), botAnswer.get("botAnswer"));
								
								if(!User.userExists(sender.get("id").toString().replace("\"", "")))
								{
									User.saveUser(sender.get("id").toString().replace("\"", ""), botAnswer.get("userMessage"), botAnswer.get("botAnswer"), 
												  botAnswer.get("conversationContext"));
								}
								
								else
								{
									User.updateUser(sender.get("id").toString().replace("\"", ""), botAnswer.get("userMessage"), botAnswer.get("botAnswer"), 
												  botAnswer.get("conversationContext"));
								}
							}
							
							else if(stripAccents(message.get("text").toString().toLowerCase().replace("\"", "")).contains("menu"))
							{
								Conversation.updateContext(sender.get("id").toString().replace("\"", ""), "menu");	
								
								System.out.println("inside");
								// Get the bot answer to the user message
								HashMap<String, String> botAnswer = BotAnswers.getResponseToMessage(sender.get("id").toString().replace("\"", "").toString(), stripAccents(message.get("text").toString().toLowerCase().replace("\"", "")), "menu");
								
								// If the element is a message pass the event to the appropriate handler function
								//handleMessage(sender.get("id").toString(), botAnswer.get("botAnswer"));
								
								if(!User.userExists(sender.get("id").toString().replace("\"", "")))
								{
									User.saveUser(sender.get("id").toString().replace("\"", ""), botAnswer.get("userMessage"), botAnswer.get("botAnswer"), 
												  botAnswer.get("conversationContext"));
								}
								
								else
								{
									User.updateUser(sender.get("id").toString().replace("\"", ""), botAnswer.get("userMessage"), botAnswer.get("botAnswer"), 
												  botAnswer.get("conversationContext"));
								}
							}
							
							else
							{
								Conversation.updateContext(sender.get("id").toString().replace("\"", ""), "undef");	
								
								// Get the bot answer to the user message
								HashMap<String, String> botAnswer = BotAnswers.getResponseToMessage(sender.get("id").toString().replace("\"", "").toString(), stripAccents(message.get("text").toString().toLowerCase().replace("\"", "")), "undef");
								
								// If the element is a message pass the event to the appropriate handler function
								handleMessage(sender.get("id").toString(), botAnswer.get("botAnswer"));
								
								if(!User.userExists(sender.get("id").toString().replace("\"", "")))
								{
									User.saveUser(sender.get("id").toString().replace("\"", ""), botAnswer.get("userMessage"), botAnswer.get("botAnswer"), 
												  botAnswer.get("conversationContext"));
								}
								
								else
								{
									User.updateUser(sender.get("id").toString().replace("\"", ""), botAnswer.get("userMessage"), botAnswer.get("botAnswer"), 
												  botAnswer.get("conversationContext"));
								}

							}
						}
					}
					
					else
					{
						JsonObject attachmentsObject = element.getAsJsonObject().get("message").getAsJsonObject();
						JsonArray attachmentsArray = attachmentsObject.get("attachments").getAsJsonArray(); 
						for(JsonElement attachmentsElement : attachmentsArray)
						{
							String fileUrl = attachmentsElement.getAsJsonObject().get("payload").getAsJsonObject().get("url").toString().replace("\"", "");
							String saveFile = FileManipulator.saveFile(fileUrl.replace("\"", ""), sender.get("id").toString().replace("\"", ""));
							System.out.println(saveFile);
						}
						
					}
				}
				
				if(element.getAsJsonObject().has("postback"))
				{
					//If the element is a postback pass the event to the appropriate handler function
					System.out.println("Is a postback message");
				}
			}
			// Return a 200 OK code response to all events
			return ResponseEntity.status(200).body("EVENT_RECEIVED");
		}
		
		else
		{
		    // Return a 404 Not Found code if event is not from a page subscription
			return ResponseEntity.status(404).body("null");
		}
	}
	
	public void handleMessage(String senderID, String receivedMessage)
	{
		//String response = "";
		ArrayList<String> responsesArrayList = new ArrayList<String>();

		// Variable to hold how many restaurants are
		int howManyRestaurants = 0;

		// Check if the message contains text
		if(receivedMessage instanceof String) 
		{	
			callSendApi(senderID, receivedMessage.replace("\"", ""));
		}
		
	}
	
	public void callSendApi(String senderID, String message)
	{
		HttpRequest httpRequest = new HttpRequest();
		
		// Sending the request to the graph API 
		String response = httpRequest.send(senderID, message);
		
		// TEST SEND IMAGE
		//String responseImage = httpRequest.sendImage(senderID, message);
	}
	
	public static String stripAccents(String s) 
	{
	    /*Salvamos las ñ*/
	    s = s.replace('ñ', '\001');
	    s = s.replace('Ñ', '\002');
	    s = Normalizer.normalize(s, Normalizer.Form.NFD);
	    s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
	    /*Volvemos las ñ a la cadena*/
	    s = s.replace('\001', 'ñ');
	    s = s.replace('\002', 'Ñ');

	    return s;
	}   
}
