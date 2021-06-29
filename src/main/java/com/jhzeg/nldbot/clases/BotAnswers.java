package com.jhzeg.nldbot.clases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.jhzeg.nldbot.utilities.DatabasePool;

public class BotAnswers {

	public static HashMap<String, String> getResponseToMessage(String userID, String message, String context)
	{
		HashMap<String, String> response = new HashMap<String, String>();
		Connection connection = null;
		String query = "SELECT * FROM responses WHERE user_message = ? AND context = ?";
		//String response = "";
		
		try
		{
			connection = DatabasePool.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, message);
			preparedStatement.setString(2, context);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			
			if(resultSet.next())
			{
				response.put("userMessage", resultSet.getString("user_message"));
				response.put("botAnswer", resultSet.getString("bot_answer"));
				response.put("conversationContext", resultSet.getString("context"));
			}
			
			
			else
			{
				if(Conversation.getContext(userID).equals("restaurantes"))
				{
					response.put("userMessage", message);
					response.put("botAnswer", "Lo siento, no comprendo lo que dices, si quieres dejar de buscar restaurantes escribe: salir");
					response.put("conversationContext", context);
				}
				else
				{
					response.put("userMessage", message);
					response.put("botAnswer", "Lo siento, no comprendo lo que dices");
					response.put("conversationContext", context);
				}
				
			}
		}
		
		catch(SQLException sqlException)
		{
			System.out.println("An exception ocurred in the getResponseToMessage method 1st catch block: " + sqlException.getMessage());
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
				System.out.println("An exception ocurred in the getResponseToMessage method 2nd catch block: " + sqlException.getMessage());

			}
		}
		
		return response;
	}
	
	
	
}
