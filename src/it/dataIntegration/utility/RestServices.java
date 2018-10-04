package it.dataIntegration.utility;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


import org.json.JSONObject;

import it.dataIntegration.model.DbpediaObject;

public class RestServices {
	/*URI per servizio web di estrazione uri dbpedia da notizie, necessita di un token 
	 * e di un valore min_confidence che influenza il numero di uri dbpedia estratti 
	 */
	private static String dandelionUri = "https://api.dandelion.eu/datatxt/nex/v1/?min_confidence=0.7&include=lod&token=39cbe7e7e89e49d08fe3825a694f5ef0";
	/*
	 * Questo metodo sfrutta le API messe a disposizione da dandelion per estrarre URI dbpedia da notizie
	 */
	public static ArrayList<DbpediaObject> getRequest(String urlNotizia) {
		ArrayList<DbpediaObject> list = new ArrayList<DbpediaObject>();
		
		try {
			URL url = new URL(dandelionUri + "&url=" + urlNotizia);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
			br.close();
			//Converto il risultato del servizio web in JSON
			JSONObject myResponse = new JSONObject(response.toString());
			org.json.JSONArray jsonArray = myResponse.getJSONArray("annotations");

			for (int i = 0; i < jsonArray.length(); i++) {
				String uriDbpedia = jsonArray.getJSONObject(i).getJSONObject("lod").getString("dbpedia");
				String nome = jsonArray.getJSONObject(i).getString("title");
				DbpediaObject dpo = new DbpediaObject(nome, uriDbpedia);
				list.add(dpo);
			}
			conn.disconnect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
}
