package com.jhzeg.nldbot.clases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.jhzeg.nldbot.utilities.DatabasePool;

public class Conversation {
	
	public static String getContext(String userID)
	{
		String response = "null";
		Connection connection = null;
		String query = "SELECT context FROM conversation WHERE user_id = ?";
		//String response = "";
		
		try
		{
			connection = DatabasePool.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, userID);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			
			if(resultSet.next())
			{
				response = resultSet.getString("context");
			}
			
			else
			{
				response = "Ocurrio un error";
			}
		}
		
		catch(SQLException sqlException)
		{
			System.out.println("An exception ocurred in the getContext method 1st catch block: " + sqlException.getMessage());
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
				System.out.println("An exception ocurred in the getContext method 2nd catch block: " + sqlException.getMessage());

			}
		}
		
		return response;
	}
	
	public static boolean updateContext(String userID, String context)
	{
		boolean response = false;
		Connection connection = null;
		String query = "UPDATE conversation SET context = ? WHERE user_id = ?";
		//String response = "";
		
		try
		{
			connection = DatabasePool.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, context);
			preparedStatement.setString(2, userID);
			
			int queryResult = preparedStatement.executeUpdate();
			
			if(queryResult > 1)
			{
				response = true;
			}
		}
		
		catch(SQLException sqlException)
		{
			System.out.println("An exception ocurred in the getContext method 1st catch block: " + sqlException.getMessage());
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
				System.out.println("An exception ocurred in the getContext method 2nd catch block: " + sqlException.getMessage());

			}
		}
		
		return response;
	}
	
	public static boolean releaseContext(String userID)
	{
		boolean response = false;
		Connection connection = null;
		String query = "UPDATE conversation SET context = 'undef' WHERE user_id = ?";
		//String response = "";
		
		try
		{
			connection = DatabasePool.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, userID);
			
			int queryResult = preparedStatement.executeUpdate();
			
			if(queryResult > 1)
			{
				response = true;
			}
		}
		
		catch(SQLException sqlException)
		{
			System.out.println("An exception ocurred in the releaseContext method 1st catch block: " + sqlException.getMessage());
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
				System.out.println("An exception ocurred in the releaseContext method 2nd catch block: " + sqlException.getMessage());

			}
		}
		
		return response;
	}
	
	public static String getLastBotMessage(String userID)
	{
		String response = "null";
		Connection connection = null;
		String query = "SELECT last_bot_message FROM conversation WHERE user_id = ?";
		//String response = "";
		
		try
		{
			connection = DatabasePool.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, userID);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			
			if(resultSet.next())
			{
				response = resultSet.getString("last_bot_message");
			}
			
			else
			{
				response = "Ocurrio un error";
			}
		}
		
		catch(SQLException sqlException)
		{
			System.out.println("An exception ocurred in the getLastBotMessage method 1st catch block: " + sqlException.getMessage());
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
				System.out.println("An exception ocurred in the getLastBotMessage method 2nd catch block: " + sqlException.getMessage());

			}
		}
		
		return response;
	}
}
