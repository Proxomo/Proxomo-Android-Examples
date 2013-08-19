package com.example.proxomoexample;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {
	private String mToken = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Get Token using our application and API key from Proxomo.com and save it for later use
		//Get raw Data
		String token = request("https://service.proxomo.com/v09/json/security/accesstoken/get?applicationid=[YOUR APP ID]&proxomoAPIKey=[YOUR API KEY]");

		//Convert raw token data into token string and save to member variable
		JSONObject jObject;
		try {
			jObject = new JSONObject(token);
			token = jObject.getString("AccessToken");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mToken = token;

		//Get status Text View for printing purposes
		TextView status = (TextView)findViewById(R.id.status);

		//Print Token
		TextView tokentext = (TextView)findViewById(R.id.token);
		tokentext.setText(token);

		//Create a location with Category "Test" and a type of "PipelineMarker"
		String id = postData("https://service.proxomo.com/v09/json/location", token, "{\"Category\":{\"Category\":\"Test\", \"Type\":\"PipelineMarker\", \"SubCategory\":\"Gas\"},\"Longitude\": 100,\"Address1\": \"100 Requiem Rd.\",\"City\": \"Dallas\",\"Latitude\": 0,\"LocationSecurity\": 1,\"Name\": \"Test Location Name\"}").replace("\"", "");

		//Create AppData associated with the new location
		String i = postData("https://service.proxomo.com/v09/json/location/"+id+"/appdata", token, "{\"Key\":\"myKey\",\"Value\":\"myValue\",\"ObjectType\":\"PROXOMO\"}").replace("\"","");

		//Get the new appdata and print it
		tokentext.setText(request("https://service.proxomo.com/v09/json/location/"+id+"/appdata/" + i));

		//Make a small edit to the value and update the appdata.
		//Remember to include the ID!
		put("https://service.proxomo.com/v09/json/location/"+id+"/appdata", token, "{\"ID\":\""+i+"\", \"Key\":\"myKey\",\"Value\":\"myValue22222\",\"ObjectType\":\"PROXOMO\"}");

		//Get all locations for the Application and print them
		status.setText(request("https://service.proxomo.com/v09/json/location"));

		//Delete the location appdata
		delete("https://service.proxomo.com/v09/json/location/"+id+"/appdata/" + i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@SuppressWarnings("finally")
	public String request(String url)
	{
		//Replace quotes and spaces with nothing to make sure that the url is valid
		//Often times JSON values without keys will be wrapped in quotes
		//these are not allowed in the url, and java will not strip them by default.
		url = url.replace("\"", "");
		url = url.replace(" ", "");
		String output = "";
		try {
		    DefaultHttpClient httpClient = new DefaultHttpClient();
		    HttpGet httpGet = new HttpGet(url);

		    //Set the Authorization header to our App Token
		    httpGet.setHeader("Authorization", mToken);

		    //Execute call and get response
		    HttpResponse httpResponse = httpClient.execute(httpGet);

		    //parse reponse into a string
		    HttpEntity httpEntity = httpResponse.getEntity();
		    output = EntityUtils.toString(httpEntity);
		}
		catch(Exception e)
		{
			Log.e("error", "request", e);
		}
		finally{
			return output;
		}
	}

	@SuppressWarnings("finally")
	public String delete(String url)
	{
		//Replace quotes and spaces with nothing to make sure that the url is valid
		//Often times JSON values without keys will be wrapped in quotes
		//these are not allowed in the url, and java will not strip them by default.
		url = url.replace("\"", "");
		url = url.replace(" ", "");
		String output = "";
		try {
		    DefaultHttpClient httpClient = new DefaultHttpClient();
		    HttpDelete httpDelete = new HttpDelete(url);

		    //Set the Authorization header to our App Token
		    httpDelete.setHeader("Authorization", mToken);

		    //Execute call and get response
		    HttpResponse httpResponse = httpClient.execute(httpDelete);

		    //parse reponse into a string
		    HttpEntity httpEntity = httpResponse.getEntity();
		    output = EntityUtils.toString(httpEntity);
		}
		catch(Exception e)
		{
			Log.e("error", "request", e);
		}
		finally{
			return output;
		}
	}

	public String put(String surl, String token, String data)
	{
		URI url = null;
		String output = "";
		try {
			url = new URI(surl);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpClient client = new DefaultHttpClient();
		HttpPut put= new HttpPut(surl);

		StringEntity se = null;
		try {
			se = new StringEntity(data);
			se.setContentType("application/json;charset=UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		put.setHeader("Content-Type", "application/json");
		put.setHeader("Authorization", token);
		put.setEntity(se);

		try {
			HttpResponse response = client.execute(put);
			output = EntityUtils.toString(response.getEntity());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	public String postData(String url, String token, String data) {
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(url);
	    String output = "";
	    try {
	        // Add your data
	    	StringEntity se = new StringEntity(data);

	        //sets the post request as the resulting string
	    	httppost.setHeader("Authorization", token);
	    	httppost.setHeader("Content-Type", "application/json");
	        httppost.setEntity(se);


	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        output = EntityUtils.toString(response.getEntity());
	    } catch (ClientProtocolException e) {
	    	Log.e("error", "request", e);
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    }
	    return output;
	}

}
