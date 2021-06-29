package com.jhzeg.nldbot.clases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.jhzeg.nldbot.utilities.DatabasePool;

public class User {
	
	public static boolean userExists(String senderID)
	{
		boolean userExists = false;
		
		Connection connection = null;
		String query = "SELECT * FROM conversation WHERE user_id = ?";
		
		try
		{
			connection = DatabasePool.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, senderID);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next())
			{
				userExists = true;
			}
		}
		
		catch(SQLException sqlException)
		{
			System.out.println("Exception catched in the userExists method 1st catch block: " + sqlException.getMessage());
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
				System.out.println("Exception catched in the userExists method 2nd catch block: " + sqlException.getMessage());
			}
		}
		
		return userExists;
	}
	
	public static boolean saveUser(String senderID, String userMessage, String botAnswer, String context)
	{
		boolean savedSuccessfuly = false;
		
		Connection connection = null;
		String query = "INSERT INTO conversation (user_id, last_user_message, last_bot_message, context) VALUES (?,?,?,?)";
		
		try
		{
			connection = DatabasePool.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, senderID);
			preparedStatement.setString(2, userMessage);
			preparedStatement.setString(3, botAnswer);
			preparedStatement.setString(4, context);

			
			int queryResult = preparedStatement.executeUpdate();
			
			if(queryResult > 0)
			{
				savedSuccessfuly = true;
			}
		}
		
		catch(SQLException sqlException)
		{
			System.out.println("Exception catched in the saveUser method 1st catch block: " + sqlException.getMessage());
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
				System.out.println("Exception catched in the saveUser method 2nd catch block: " + sqlException.getMessage());
			}
		}
		
		return savedSuccessfuly;
	}
	
	public static boolean updateUser(String senderID, String userMessage, String botAnswer, String context)
	{
		boolean updatedSuccessfuly = false;
		
		Connection connection = null;
		String query = "UPDATE conversation SET last_user_message = ?, last_bot_message = ?, context = ? WHERE user_id = ?";
		
		try
		{
			connection = DatabasePool.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, userMessage);
			preparedStatement.setString(2, botAnswer);
			preparedStatement.setString(3, context);
			preparedStatement.setString(4, senderID);

			
			int queryResult = preparedStatement.executeUpdate();
			
			if(queryResult > 0)
			{
				updatedSuccessfuly = true;
			}
		}
		
		catch(SQLException sqlException)
		{
			System.out.println("Exception catched in the updateUser method 1st catch block: " + sqlException.getMessage());
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
				System.out.println("Exception catched in the updateUser method 2nd catch block: " + sqlException.getMessage());
			}
		}
		
		return updatedSuccessfuly;
	}
}
