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

		//Create new table named MyFirstTable
		//Put an object with ID 'testObj' and put it in a partition called "Test"
		String id = postData("https://sandbox.proxomo.com/v09/json/customdata", token, "{\"ID\":\"testObj\",\"TableName\":\"MyFirstTable\", \"PartitionKey\":\"Test\", \"customProperty\":\"test\"}");
		//Grab the ID and print it - should be testObj
		status.setText(id);

		//Request a search of the table MyFirstTable where customProperty = test - Only return 100 results max
		//Remember to attach the partitionKey!
		//Print the results
		tokentext.setText(request("https://sandbox.proxomo.com/v09/json/customdata/search/table/myFirstTable?q=customProperty%20eq%20'test'&maxresults=100&partitionkey=Test"));
		//Delete the object with the ID 'testObj'
		delete("https://sandbox.proxomo.com/v09/json/customdata/table/myFirstTable/" + id + "&partitionkey=Test");
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
			Log.e("error", "delete", e);
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
	        Log.v("POST", output);
	    } catch (ClientProtocolException e) {
	    	Log.e("error", "request", e);
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    }
	    return output;
	}

}
