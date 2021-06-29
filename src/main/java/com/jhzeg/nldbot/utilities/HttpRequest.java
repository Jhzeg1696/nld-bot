package com.jhzeg.nldbot.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class HttpRequest {
	// Graph URL
	private String URL = "https://graph.facebook.com/v2.6/me/messages?access_token=EAANZASxM4AXEBAD43SGh98eeHZAPQMyFRYmJpu9uJXgY3yokcTJek7YS4MiZC1M02cwsHltKDIdeFTA06jDiUFslvepqpsjDqHkbPpTdYRah4pGk2QWE7nS4LwnTyFZCpRo4ycfuj94tI5NZBtfOB4c9pFs1FGZCQ0qsrz5JzPXAzWdfAlaP63";
	
	public String send(String senderID, String message)
	{
		String response; 
		// Construct the message body
		String requestBody = "{\"recipient\": {\"id\": %s },  \"message\": {\"text\":  \"%s\"} }";
		
		// Format the message body
		requestBody = String.format(requestBody, senderID, message);
		
		try 
		{
			URL url = new URL (URL);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			
			// Set the headers
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json; utf-8");
			connection.setRequestProperty("Accept", "application/json");
			connection.setDoOutput(true);
			
			// Send the json data
			OutputStream outputStream = connection.getOutputStream();
			byte[] input = requestBody.getBytes("utf-8");
		    outputStream.write(input, 0, input.length);	
		    BufferedReader br = new BufferedReader( new InputStreamReader(connection.getInputStream(), "utf-8"));
		    
		    StringBuilder serverResponse = new StringBuilder();
			String responseLine = null;
			
			while ((responseLine = br.readLine()) != null) 
			{
				serverResponse.append(responseLine.trim());
			}
			
			// Get the response from the server
			response = serverResponse.toString(); 
		}	
		
		// Catch the exceptions
		catch(MalformedURLException urlException) 
		{ 
			return "Lo siento, ocurrio el siguiente error: " + urlException.getMessage().toString();
		}
		
		catch(IOException ioException) 
		{ 
			return  "Lo siento, ocurrio el siguiente error: " + ioException.getMessage().toString();
		}
		
		// Return the response
		return response;
	}
	
	public String sendImage(String senderID, String imageUrl)
	{
		// Variable to hold the response
		String response; 
		
		// Get the imageUrl from the database
		//String imageUrl = "http://ticketstalamas.com/test/6-carta-de-renuncia-voluntaria.pdf";
		
		if(!imageUrl.isEmpty())
		{
			// Construct the message body
			String requestBody = "{\"recipient\": {\"id\": \"%s\" },  \"message\": {\"attachment\": {\"type\": \"image\", \"payload\": {\"url\": \"%s\", \"is_reusable\": true } } } }";
			
			// Format the message body
			requestBody = String.format(requestBody, senderID, imageUrl);
			System.out.println("THIS IS THE REQUEST A BODY " + requestBody);

			
			try 
			{
				URL url = new URL (URL);
				HttpURLConnection connection = (HttpURLConnection)url.openConnection();
				
				// Set the headers
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/json; utf-8");
				connection.setRequestProperty("Accept", "application/json");
				connection.setDoOutput(true);
				
				// Send the json data
				OutputStream outputStream = connection.getOutputStream();
				byte[] input = requestBody.getBytes("utf-8");
				outputStream.write(input, 0, input.length);	
			    BufferedReader br = new BufferedReader( new InputStreamReader(connection.getInputStream(), "utf-8"));
			    
			    StringBuilder serverResponse = new StringBuilder();
				String responseLine = null;
				
				while ((responseLine = br.readLine()) != null) 
				{
					serverResponse.append(responseLine.trim());
				}
				
				// Get the response from the server
				response = serverResponse.toString(); 
			}	
			
			// Catch the exceptions
			catch(MalformedURLException urlException) 
			{ 
				return "Lo siento, ocurrio el siguiente error: " + urlException.getMessage().toString();
			}
			
			catch(IOException ioException) 
			{ 
				return  "Lo siento, ocurrio el siguiente error: " + ioException.getMessage().toString();
			}
			
		}
		
		else 
		{
			send(senderID, "Lo siento, no cuento con el menu de el restaurante que buscas.");
			response = "No tengo esa imagen";
		}
		
		return response;
	}
	
	public String getWeatherForecast()
	{
		StringBuilder response = new StringBuilder();
		
		try
		{
			//String url = "http://api.openweathermap.org/data/2.5/weather?q=Nuevo Laredo&appid=2d9e9c8d84a8fb557675d5932c42547c&units=metric&lang=es";
			String url = "https://api.openweathermap.org/data/2.5/onecall?lat=27.4763&lon=-99.5164&exclude=hourly,minutely&appid=2d9e9c8d84a8fb557675d5932c42547c&units=metric&lang=es";
			
	        HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();

	        // optional default is GET
	        httpClient.setRequestMethod("GET");

	        //add request header
	        httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");

	        int responseCode = httpClient.getResponseCode();
	        System.out.println("\nSending 'GET' request to URL : " + url);
	        System.out.println("Response Code : " + responseCode);

	        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpClient.getInputStream()))) 
	        {

	            response = new StringBuilder();
	            String line;

	            while ((line = bufferedReader.readLine()) != null) 
	            {
	                response.append(line);
	            }

	            //print result
	           // System.out.println(response.toString());

	        }
		}
		
		catch(IOException ioException)
		{
			System.out.println("Catched an exception in the getWeatherForecast method: " + ioException.getMessage());
		}
		
		return response.toString();
	}
	
}
