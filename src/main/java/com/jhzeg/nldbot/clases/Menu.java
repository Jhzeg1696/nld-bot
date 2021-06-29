package com.jhzeg.nldbot.clases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.jhzeg.nldbot.utilities.DatabasePool;

public class Menu {
	public static String getUrl(String userID, String restaurant)
	{

		String response = "null";
		boolean find = false;
		Connection connection = null;
		String query = "SELECT bot_answer FROM responses WHERE user_message = ?";
		//String response = "";
		
		try
		{
			connection = DatabasePool.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, restaurant);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next())
			{
				find = true;
				Message.sendImage(userID, resultSet.getString("bot_answer"));
			}
			
			if(!find)
			{
				Message.send(userID, "Lo siento, no encontre el menú del restaurante que buscabas");
			}
			
			/*
			
			if(resultSet.next())
			{
				response = resultSet.getString("bot_answer");
			}
			
			else
			{
				response = "Ocurrio un error";
			}
			*/
		}
		
		catch(SQLException sqlException)
		{
			System.out.println("An exception ocurred in the getUrl method 1st catch block: " + sqlException.getMessage());
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
				System.out.println("An exception ocurred in the getUrl method 2nd catch block: " + sqlException.getMessage());

			}
		}
		
		return response;
	}
}
