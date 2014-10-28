package com.tridonic.dalimonitor;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.ToggleButton;


public class send extends Activity {
	send_command send_command = new send_command();
	public SeekBar seekbar;   
	public Switch on_off;
	
	private int time_saver = getTime();
	private int time_saver2 = 0;
/** Called when the activity is first  . */ 
	@Override
	public void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send);
		
		seekbar =(SeekBar) findViewById(R.id.seekBar1);
		on_off 	=(Switch) findViewById(R.id.mainswitch);
		
		send_command.send("255 5");
		on_off.setChecked(true);
		  seekbar.setOnSeekBarChangeListener( new OnSeekBarChangeListener()		//Listen to ProgressBar
		  {
		  public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser){	//Triggers when Progress Bar Progress changes
			  time_saver2 = getTime();
			  if(time_saver2 - time_saver >= 50){	//Nur alles 50ms wird die Helligkeit aktualisiert
				  send_command send_command = new send_command();
			      send_command.send("254 "+ progress);	
			      time_saver = time_saver2;
			  }
		  }
		 
		  public void onStartTrackingTouch(SeekBar seekBar){}

		  public void onStopTrackingTouch(SeekBar seekBar){}
		  });
		  
		  on_off.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			    	send_command command = new send_command();
			    	if (isChecked) {
				        //power on
				    	command.send("255 5");	
				    } else {
				        //Power off
				    	command.send("255 0");
				    }
			    }
			});
	
	}
	private int getTime(){
		//Aktuelle Zeit in MS als rückgabewert
		return (int) System.currentTimeMillis();	
	}
	
}
