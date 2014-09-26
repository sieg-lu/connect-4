package com.game.connect4;

import org.json.JSONObject;

/**
 * This is the Weather Info class
 * 
 * @author Yufan Lu
 */
public class WeatherInfo {
	public String title;
	public String city;
	public String description;
	public String windInfo;
	
	/*
	 * FUNC: Constructor(JSONObject)
	 * DESC:
	 * 	parse the JSON node and fill the variables
	 * ARGS:
	 * 	apiObj -- the JSON node holding the weather info
	 */
	public WeatherInfo(JSONObject apiObj) {
		String html = Util.getHTML(apiObj.getString("url"));
		JSONObject weatherInfoObj = new JSONObject(html);
		
		String descLevel1 = apiObj.getJSONObject("desc").getString("level1");
		String descLevel2 = apiObj.getJSONObject("desc").getString("level2");
		
		String windLevel1 = apiObj.getJSONObject("wind").getString("level1");
		String windLevel21 = apiObj.getJSONObject("wind").getString("level21");
		String windLevel22 = apiObj.getJSONObject("wind").getString("level22");
		String windDescription = String.format("%.2f degrees, %.2f mph wind",
				weatherInfoObj.getJSONObject(windLevel1).getDouble(windLevel21),
				weatherInfoObj.getJSONObject(windLevel1).getDouble(windLevel22));
		
		title = "Current Weather";
		city = weatherInfoObj.getString(apiObj.getString("name"));
		description = weatherInfoObj.getJSONArray(descLevel1).getJSONObject(0).getString(descLevel2);
		windInfo = windDescription;
	}
}
