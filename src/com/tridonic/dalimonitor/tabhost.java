package com.tridonic.dalimonitor;
//File: 	tabhost.java
//design: 	none
//date:		5.08.14
//(c) 2014 by Dario Duff
//Tab host file. Hosts the tabs for the different activity's. 
////////////////////////////////////////////////////////////////////////////////

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TabHost;

//File: 	MainActivity.java
//design: 	activity_main.xml
//date:		22.05.14
//(c) 2014 by Dario Duff
//Main form, allows to connect to Bluetooth device
////////////////////////////////////////////////////////////////////////////////
public class tabhost extends TabActivity {
	private Menu menu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    //Hide the Keyboard
	    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);	
	    //setContentView(R.layout.activity_main);
	  	//set up a Tab host with 2 tabs, each tab has his own activity. 1: MainActivity 2:settings
	    TabHost mTabHost = getTabHost();
	    //Creates the different tabs. Add new Tab like below:
	    mTabHost.addTab(mTabHost.newTabSpec("Connect").setIndicator("Connect").setContent(new Intent(this  ,MainActivity.class )));
	    mTabHost.addTab(mTabHost.newTabSpec("settings").setIndicator("settings").setContent(new Intent(this ,settings.class )));
	    mTabHost.setCurrentTab(0);
	}
}


