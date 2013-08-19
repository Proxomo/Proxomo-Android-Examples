package com.example.proxomoexample;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
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

		//Create new Person
		String id = postData("https://service.proxomo.com/v09/json/security/person/create?username=mytest12&password=test&role=User", token, "");
		//Parse ID response *IMPORTANT*
		try {
			JSONObject jObj = new JSONObject(id);
			id = jObj.getString("PersonID");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Set status
		status.setText(id);

		//Get all persons for this app ID, and print them onto the token text area.
		tokentext.setText(request("https://service.proxomo.com/v09/json/security/persons"));
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
