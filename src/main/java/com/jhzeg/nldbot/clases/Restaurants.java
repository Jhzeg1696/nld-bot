package com.jhzeg.nldbot.clases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jhzeg.nldbot.utilities.DatabasePool;
import com.jhzeg.nldbot.utilities.HttpRequest;

public class Restaurants {
	
	public static JsonArray getRestaurantsByCategory(String category)
	{

		Connection connection = null;
		String query = "SELECT * FROM restaurants WHERE category = ?";
		JsonArray jsonArray = new JsonArray();

		try
		{
			connection = DatabasePool.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, category);
			
			ResultSet resultSet = preparedStatement.executeQuery();
            int numberOfColumns = resultSet.getMetaData().getColumnCount();

			while(resultSet.next())
			{
	            
	            JsonObject jsonObject = new JsonObject();
	            
	            for (int i = 0; i < numberOfColumns; i++) 
	            {
	            
	            	jsonObject.addProperty(resultSet.getMetaData().getColumnLabel(i + 1).toLowerCase(), resultSet.getString(i + 1));
	            }
	            
	            jsonArray.add(jsonObject); 
			}
		}
		
		catch(SQLException sqlException)
		{
			System.out.println("An exception ocurred in the getRestaurantsByCategory method 1st catch block: " + sqlException.getMessage());
		}
		
		finally
		{
			try
			{
				if(connection != null)
				{
					connection.close();
				}
			}
			
			catch(SQLException sqlException)
			{
				System.out.println("An exception ocurred in the getRestaurantsByCategory method 2nd catch block: " + sqlException.getMessage());

			}
		}
		
		return jsonArray;
	}
	
	public static void sendRestaurantsMessage(String userID, String category)
	{
		// Get the JsonArray containing the restaurants data
		JsonArray restaurants = Restaurants.getRestaurantsByCategory(category);
		
		// Create an ArrayList to hold the responses
		ArrayList<String> responses = new ArrayList<String>();
		
		// Check if ArrayList isnt empty
		if(restaurants.size() > 0)
		{
			// Iterate through the JsonArray and append the data to the ArrayList
			for(JsonElement element : restaurants)
			{
				
				// Create a JsonObject to hold each element in the json array
				JsonObject jsonObject = element.getAsJsonObject();
				
				//Create a StringBuilder to construct the response
				StringBuilder strBuilder = new StringBuilder();
				
				// Append the data to the StringBuilder
				strBuilder.append(jsonObject.get("name").toString().replace("\"", ""));
				strBuilder.append("\\n");
				strBuilder.append("Telefono: " + jsonObject.get("phone_number").toString().replace("\"", ""));
				strBuilder.append("\\n");
				strBuilder.append("Direccion: " + jsonObject.get("address").toString().replace("\"", ""));
				strBuilder.append("\\n");
				strBuilder.append("Horario: " + jsonObject.get("schedule").toString().replace("\"", ""));
				strBuilder.append("\\n");
				strBuilder.append("Servicio a domicilio: " + jsonObject.get("delivery").toString().replace("\"", ""));
				strBuilder.append("\\n");
				strBuilder.append("\\n");

				// Append each response to the ArrayList
				responses.add(strBuilder.toString());
			}
			
			// Create a second temp StringBuilder to holde the actual responses
			StringBuilder tempResponse = new StringBuilder();
			
			// Count value to know when to send the data
			int counter = 0;
			Message.send(userID, "Estos son los restaurantes de la ciudad que cuentan con " + category + " en su menu 😁");
			
			// Iterate through the ArrayList and append the response to the temp StringBuilder
			for(int i = 0; i < responses.size(); i++)
			{
				counter++;
				tempResponse.append(responses.get(i));
				
				// If count is odd the send the message
				if(counter % 2 == 0)
				{
					//System.out.println(tempResponse);

					Message.send(userID, tempResponse.toString());
					tempResponse.setLength(0);			
				}
			}
			
			Message.send(userID, "Que más te gustaria buscar?");
		}
		
		else
		{
			Message.send(userID, "Lo siento, no encontre ningun restaurante que cuente con " + category + " en su menú");
		}
	}
}
