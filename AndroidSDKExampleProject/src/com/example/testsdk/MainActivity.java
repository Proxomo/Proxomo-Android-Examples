package com.example.testsdk;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.proxomoandroidsdk.ProxomoApi;
import com.proxomoandroidsdk.ProxomoResult;
import com.proxomoandroidsdk.definitions.AppData;
import com.proxomoandroidsdk.definitions.CustomData;
import com.proxomoandroidsdk.definitions.Event;
import com.proxomoandroidsdk.definitions.EventParticipant;
import com.proxomoandroidsdk.definitions.Friend;
import com.proxomoandroidsdk.definitions.GeoCode;
import com.proxomoandroidsdk.definitions.Location;
import com.proxomoandroidsdk.definitions.Person;
import com.proxomoandroidsdk.definitions.PersonLogin;
import com.proxomoandroidsdk.definitions.Token;
import com.proxomoandroidsdk.enums.Enums.FriendResponse;

public class MainActivity extends Activity {
	AppData obj;
	TextView tv;
	MyData md;
	Checkpoint event_people;
	Checkpoint chkpLocation;
	Checkpoint finalchk;
	Person person1 = new Person();
	Person person2 = new Person();
	Event event1 = new Event();
	Event event2 = new Event();
	Location loc1 = new Location();
	Location loc2 = new Location();
	GeoCode gc1 = new GeoCode();
	GeoCode gc2 = new GeoCode();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		event_people = new Checkpoint(4, new ProxomoResult<Object>(){
			public void result(String s, Object...e)
			{
				print("Event-People Checkpoint Reached!");
				doLocationTesting();
				doFriendsTesting();
			}
		});
		finalchk = new Checkpoint(2, new ProxomoResult<Object>(){
			public void result(String s, Object...e)
			{
				print("--------TESTS FINISHED!--------");
			}
		});
		chkpLocation = new Checkpoint(2, new ProxomoResult<Object>(){
			public void result(String s, Object...e)
			{
				print("chkpLocation Checkpoint Reached!");
				doGeoCodeTesting();
			}
		});

		setContentView(R.layout.activity_main);
		tv = (TextView)findViewById(R.id.text1);
		ProxomoResult<Token> r = new ProxomoResult<Token>(){
			public void result(String authResults, Token...authExtras)
			{
				print("Token: " + authExtras[0].getAccessToken());
				doEventTesting();
				doPeopleTesting();
			}
		};
		ProxomoApi pxapi = new ProxomoApi("[YOUR APPLICATION ID HERE]", "[YOUR API KEY HERE]", r);


	}

	public void doEventTesting()
	{
		event1.setCity("Arlington");
		event1.setAddress1("312 Tabor Dr.");
		event1.setState("Texas");
		event1.add(new ProxomoResult<Event>(){
			public void result(String results, Event...extras)
			{
				print("Event 1 Created: " + event1.getID());
				event1.setEventName("Test Event 1");
				event1.update(new ProxomoResult<Event>(){
					public void result(String results, Event...extras)
					{
						event1.setEventName("Event 1 GET FAIL");
						event1.get(event1.getID(), new ProxomoResult<Event>(){
							public void result(String results, Event...extras)
							{
								print("Event 1 Get: " + event1.getEventName());
								event_people.checkin();
							}
						});
					}
				});
			}
		});
		event2.setCity("Fort Worth");
		event2.setAddress1("4701 American Blvd.");
		event2.setState("Texas");
		Event.add(event2, new ProxomoResult<Event>(){
			public void result(String results, Event...extras)
			{
				event2 = extras[0];
				print("Event 2 Created: " + event2.getID());
				event2.setEventName("Test Event 2");
				Event.update(event2, new ProxomoResult<Event>(){
					public void result(String results, Event...extras)
					{
						event2 = extras[0];
						event2.setEventName("Event 2 GET FAIL");
						Event.getById(event2.getID(), new ProxomoResult<Event>(){
							public void result(String results, Event...extras)
							{
								event2 = extras[0];
								print("Event 2 Get: " + event2.getEventName());
								event_people.checkin();
							}
						});
					}
				});
			}
		});
	}

	public void doPeopleTesting()
	{
		person1.setUserName("Person111228" + Math.ceil(Math.random()*100));

		Person.add(person1.getUserName(), "TestPass", "User", new ProxomoResult<PersonLogin>(){
			public void result(String results, PersonLogin...extras)
			{
				person1.setID(extras[0].getPersonID());
				print("Person 1 Created: " + person1.getID());
				person1.setFirstName("Jake");
				person1.setFullName("Test Person 1");
				person1.update(new ProxomoResult<Person>(){
					public void result(String results, Person...extras)
					{
						person1.setFullName("Person 1 GET FAIL");
						person1.get(person1.getID(), new ProxomoResult<Person>(){
							public void result(String results, Person...extras)
							{
								print("Person 1 Get: " + person1.getFullName());
								event_people.checkin();
							}
						});
					}
				});
			}
		});
		person2.setUserName("Person222128"  + Math.ceil(Math.random()*100));

		Person.add(person2.getUserName(), "TestPass2", "User", new ProxomoResult<PersonLogin>(){
			public void result(String results, PersonLogin...extras)
			{
				person2.setID(extras[0].getPersonID());
				print("Person 2 Created: " + person2.getID());
				person2.setFirstName("Jake");
				person2.setFullName("Test Person 2");
				Person.update(person2, new ProxomoResult<Person>(){
					public void result(String results, Person...extras)
					{
						person2.setFullName("Person 2 GET FAIL");
						Person.getById(person2.getID(), new ProxomoResult<Person>(){
							public void result(String results, Person...extras)
							{
								person2 = extras[0];
								print("Person 2 Get: " + person2.getFullName());
								event_people.checkin();
							}
						});
					}
				});
			}
		});
	}

	public void doLocationTesting()
	{
		loc1.setName("loc1");

		Location.add(loc1, new ProxomoResult<Location>(){
			public void result(String results, Location...extras)
			{
				loc1.setID(extras[0].getID());
				print("Location 1 Created: " + loc1.getID());
				loc1.setAddress1("300 Tabor Dr.");
				loc1.setAddress2("");
				loc1.setCity("Arlington");
				loc1.setState("Texas");
				loc1.setZip("76002");
				loc1.update(new ProxomoResult<Location>(){
					public void result(String results, Location...extras)
					{
						loc1.setAddress1("Location 1 GET FAIL");
						loc1.get(new ProxomoResult<Location>(){
							public void result(String results, Location...extras)
							{
								print("Location 1 Get: " + loc1.getName());
								chkpLocation.checkin();
							}
						});
					}
				});
			}
		});
		loc2.setName("loc2");

		loc2.add(new ProxomoResult<Location>(){
			public void result(String results, Location...extras)
			{
				loc2.setID(extras[0].getID());
				print("Location 2 Created: " + loc2.getID());
				loc2.setAddress1("300 Kissame Dr.");
				loc2.setAddress2("");
				loc2.setCity("Arlington");
				loc2.setState("Texas");
				loc2.setZip("76002");
				loc2.update(new ProxomoResult<Location>(){
					public void result(String results, Location...extras)
					{
						loc2.setAddress1("Location 2 GET FAIL");
						loc2.get(new ProxomoResult<Location>(){
							public void result(String results, Location...extras)
							{
								print("Location 2 Get: " + loc2.getName());
								chkpLocation.checkin();
							}
						});
					}
				});
			}
		});
	}

	public void doGeoCodeTesting()
	{

		GeoCode.getByAddress(loc1.getFullAddress(), new ProxomoResult<GeoCode>(){
			public void result(String results, GeoCode...extras)
			{
				gc1 = extras[0];
				print("GeoCode from Location 1: Lat-" + gc1.getLatitude() + " Lng-" + gc1.getLongitude());

				GeoCode.reverseLookup(gc1.getLatitude(), gc1.getLongitude(), new ProxomoResult<Location>(){
					public void result(String results, Location...extras)
					{
						loc1 = extras[0];
						print("Reverse lookup Location 1 Address:" + loc1.getAddress1());
						finalchk.checkin();
					}
				});
			}
		});
		GeoCode.getByAddress(loc2.getFullAddress(), new ProxomoResult<GeoCode>(){
			public void result(String results, GeoCode...extras)
			{
				gc2 = extras[0];
				print("GeoCode from Location 2: Lat-" + gc2.getLatitude() + " Lng-" + gc2.getLongitude());

				GeoCode.reverseLookup(gc2.getLatitude(), gc2.getLongitude(), new ProxomoResult<Location>(){
					public void result(String results, Location...extras)
					{
						loc2 = extras[0];
						print("Reverse lookup Location 2 Address:" + loc2.getAddress1());
						finalchk.checkin();
					}
				});
			}
		});
	}

	public void doFriendsTesting()
	{
		person1.inviteFriend(person2.getID(), new ProxomoResult<EventParticipant>(){
			public void result(String results, EventParticipant...extras)
			{
				print("Person 1 Invited Person 2");
				Friend.respond(FriendResponse.Accept, person1.getID(), person2.getID(), new ProxomoResult<Friend>(){
					public void result(String results, Friend...extras)
					{
						print("Person 2 Accepts the friendship!");
					}
				});
			}
		});

	}

	public void print(String msg)
	{
		tv.setText(tv.getText() + "\n" + msg);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	private class MyData extends CustomData{
		public String prop1;

		public MyData(String prop1){
			this.prop1 = prop1;
		}
	}
	private class Checkpoint{
		private int checkins = 0;
		private int points = 0;
		private ProxomoResult<Object> callback;

		public Checkpoint(int points, ProxomoResult<Object> callback)
		{
			this.callback = callback;
			this.points = points;
		}

		public void checkin()
		{
			if(++checkins >= points)
				callback.result("", (Object)null);
		}

	}
}
