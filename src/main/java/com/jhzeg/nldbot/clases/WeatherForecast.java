package com.jhzeg.nldbot.clases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jhzeg.nldbot.utilities.DatabasePool;
import com.jhzeg.nldbot.utilities.HttpRequest;

public class WeatherForecast {
	
	public static HashMap<String, String> getWeatherForecast()
	{
		// Create the object to make the http request
		HttpRequest httpRequest = new HttpRequest();
		
		// HashMap to hold the values
		HashMap<String, String> weatherForecast = new HashMap<String, String>();
		
		// Get the whole json response
		String json = httpRequest.getWeatherForecast();
		
		// Parse the json response to a json object
		JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
		
		/* START CURRENT */
		
		// Get currentWeather object(today)
		JsonObject currenWeatherForecastObject = jsonObject.get("current").getAsJsonObject();
		
		// Get current weather array
		JsonArray currentWeather = currenWeatherForecastObject.get("weather").getAsJsonArray();
		
		//Get the info object of the currentWeather object(today)
		JsonObject currentWeatherInfo = currentWeather.get(0).getAsJsonObject();
		
		/* END CURRENT */
		
		/* START TODAY */
		
		// Get the daily weather array
		JsonArray dailyWeatherArray = jsonObject.get("daily").getAsJsonArray();
		
		// Get the first object in the dailyWeatherArray(today)
		JsonObject todayWeatherForecastObject = dailyWeatherArray.get(0).getAsJsonObject();
		
		// Get the temperature object of the first todayWeatherForecastObject(today)
		JsonObject todayTemperature = todayWeatherForecastObject.get("temp").getAsJsonObject();
		
		// Get today weather array
		JsonArray todayWeather = todayWeatherForecastObject.get("weather").getAsJsonArray();
		
		//Get the info object of the todayWeather object(today)
		JsonObject todayWeatherInfo = todayWeather.get(0).getAsJsonObject();
		
		/* END TODAY */
		
		/* START TOMORROW */
		
		// Get the second object in the dailyWeatherArray(tomorrow)
		JsonObject tomorrowWeatherForecastObject = dailyWeatherArray.get(1).getAsJsonObject();
		
		// Get the temperature object of the second tomorrowWeatherForecastObject(tomorrow)
		JsonObject tomorrowTemperature = tomorrowWeatherForecastObject.get("temp").getAsJsonObject();
		
		// Get today weather array
		JsonArray tomorrowWeather = tomorrowWeatherForecastObject.get("weather").getAsJsonArray();
				
		//Get the info object of the todayWeather object(today)
		JsonObject tomorrowWeatherInfo = tomorrowWeather.get(0).getAsJsonObject();
		
		/* END TOMORROW */
		
		// Format the description
		String todayDescription = "";
		String tomorrowDescription = "";
		if(currentWeatherInfo.get("description").toString().replace("\"", "").equals("nubes"))
		{
			todayDescription = "con nubes";
		}
		
		if(tomorrowWeatherInfo.get("description").toString().replace("\"", "").equals("nubes"))
		{
			tomorrowDescription = "un dia con nubes";
		}
		
		
		
		// Create the response for today weather
		String todayWeatherResponse = "Actualmente tenemos un dia %s y con una temperatura de %s grados centigrados, se espera %s en el resto del dia, la minima es de %s con una maxima de %s grados centigrados";
		todayWeatherResponse = String.format(todayWeatherResponse,currentWeatherInfo.get("description").toString().replace("\"", ""), Math.round(Double.parseDouble(currenWeatherForecastObject.get("temp").toString())),todayWeatherInfo.get("description"),Math.round(Float.parseFloat(todayTemperature.get("min").toString())), Math.round(Float.parseFloat(todayTemperature.get("max").toString())));
				
		// Create the response for tomorrow weather
		String tomorrowWeatherResponse = "Para mañana se espera %s con una temperatura de %s grados centigrados, la minima sera de %s con una maxima de %s grados centigrados";
		tomorrowWeatherResponse = String.format(tomorrowWeatherResponse, tomorrowWeatherInfo.get("description").toString().replace("\"", ""), Math.round(Double.parseDouble(tomorrowTemperature.get("day").toString())),Math.round(Double.parseDouble(tomorrowTemperature.get("min").toString())), Math.round(Double.parseDouble(tomorrowTemperature.get("max").toString())));
		
		weatherForecast.put("como sera el clima el dia de hoy?", todayWeatherResponse);
		weatherForecast.put("como sera el clima el dia de mañana?", tomorrowWeatherResponse);

		return weatherForecast;
		
	}
	
	public static boolean saveWeatherForecast(String userMessage, String botAnswer, String context)
	{
		boolean success = false;
		
		Connection connection = null;
		String query = "INSERT INTO responses (user_message,bot_answer,context) VALUES (?,?,?)";
		
		try
		{
			connection = DatabasePool.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, userMessage);
			preparedStatement.setString(2, botAnswer);
			preparedStatement.setString(3, context);
			
			int queryResult = preparedStatement.executeUpdate();
			
			if(queryResult > 0)
			{
				success = true;
			}
			
		}
		
		catch(SQLException sqlException)
		{
			System.out.println("Exception catched in the saveWeatherForecast method 1st catch block: " + sqlException.getMessage());
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
				System.out.println("Exception catched in the saveWeatherForecast method 2dn catch block: " + sqlException.getMessage());
			}
		}
		
		return success;
	}
	
	public static void main(String[] args)
	{
		HashMap<String, String> weatherForecast = getWeatherForecast();
		WeatherForecast.saveWeatherForecast("como sera el clima el dia de hoy?", weatherForecast.get("como sera el clima el dia de hoy?"), "undef");
		WeatherForecast.saveWeatherForecast("como sera el clima el dia de mañana?", weatherForecast.get("como sera el clima el dia de mañana?"), "undef");
		WeatherForecast.saveWeatherForecast("clima hoy", weatherForecast.get("como sera el clima el dia de hoy?"), "undef");
		WeatherForecast.saveWeatherForecast("clima mañana", weatherForecast.get("como sera el clima el dia de mañana?"), "undef");
		WeatherForecast.saveWeatherForecast("como sera el clima el dia de hoy", weatherForecast.get("como sera el clima el dia de hoy?"), "undef");
		WeatherForecast.saveWeatherForecast("como sera el clima el dia de mañana", weatherForecast.get("como sera el clima el dia de mañana?"), "undef");
		WeatherForecast.saveWeatherForecast("cual sera el clima el dia de hoy?", weatherForecast.get("como sera el clima el dia de hoy?"), "undef");
		WeatherForecast.saveWeatherForecast("cual sera el clima el dia de mañana?", weatherForecast.get("como sera el clima el dia de mañana?"), "undef");
		WeatherForecast.saveWeatherForecast("cual sera el clima el dia de hoy", weatherForecast.get("como sera el clima el dia de hoy?"), "undef");
		WeatherForecast.saveWeatherForecast("cual sera el clima el dia de mañana", weatherForecast.get("como sera el clima el dia de mañana?"), "undef");
		WeatherForecast.saveWeatherForecast("llovera hoy?", weatherForecast.get("como sera el clima el dia de hoy?"), "undef");
		WeatherForecast.saveWeatherForecast("llovera mañana?", weatherForecast.get("como sera el clima el dia de mañana?"), "undef");
		WeatherForecast.saveWeatherForecast("llovera hoy", weatherForecast.get("como sera el clima el dia de hoy?"), "undef");
		WeatherForecast.saveWeatherForecast("llovera mañana", weatherForecast.get("como sera el clima el dia de mañana?"), "undef");

		/*
		// Get the weather forecast json
		HashMap<String, String> weatherForecast = getWeatherForecast();
		
		// Create the string to hold the message
		String weatherForecastStr = "Se espera un dia %s con temperatura de %s grados centigrados";
		
		// Place the actual values
		weatherForecastStr = String.format(weatherForecastStr, weatherForecast.get("main"), weatherForecast.get("temp"));
		
		System.out.println(weatherForecastStr);
		
		//WeatherForecast.saveWeatherForecast();
		*/
	}
	
}
