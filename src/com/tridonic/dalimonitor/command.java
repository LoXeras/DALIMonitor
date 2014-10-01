package com.tridonic.dalimonitor;
//File: 	command.java
//design: 	command.xml
//date:		22.05.14
//(c) 2014 by Dario Duff
//Connects to the bloetooth device that was selected in the main Activity, Displays the incomming messages from Bluetoth Device.
////////////////////////////////////////////////////////////////////////////////

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import ar.com.daidalos.afiledialog.FileChooserActivity;

public class command extends Activity {	
	
	
	public static String endsave;
	public static String beginsave;
	public static boolean success = true;
	
	
	private static final int BUFFER_SIZE = 4096;
    private boolean isClosing;
    public byte[] receiveBuffer = new byte[BUFFER_SIZE];
    	
	private int mState = 3;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    
    private BluetoothAdapter btAdapter = null;
	private Set<BluetoothDevice>pairedDevices;
	private BluetoothDevice device = null;
	private BluetoothSocket btSocket;
	public static boolean scroll = true;
	
	 //private InputStream inStream;
	 private static EditText intext;
	 
	
	 public static final int STATE_NONE = 0;          // we're doing nothing
	 public static final int STATE_LISTEN = 1;        //  listening for incoming connections
	 public static final int STATE_CONNECTING = 2;    //  initiating an outgoing connection
	 public static final int STATE_CONNECTED = 3;     //  connected to a remote device
	 public static final int STATE_DISCONNECTED = 4;  //  disconnected from a remote device
	 
	 private static ListView mainListView ;
	 private static ArrayAdapter<String> listAdapter ;
	 public static boolean pause = false;
	 private Menu menu;
	 public static int igrp;
	 public static String filePath = "";
	 ArrayList<String> message_list = new ArrayList<String>();
	 
	 public static final String[] COMMANDS = {
			/*0-9*/			"OFF","UP","DOWN","STEP UP","STEP DOWN","RECALL MAX LEVEL","RECALL MIN LEVEL","STEP DOWN AND OFF","ON AND STEP UP","ENABLE DAPC SEQUENCE",
			/*10*15*/		"NO COM","NO COM","NO COM","NO COM","NO COM","NO COM",
			/*16-24*/		"GOTO SCENE 0","GOTO SCENE 1","GOTO SCENE 2","GOTO SCENE 3","GOTO SCENE 4","GOTO SCENE 5","GOTO SCENE 6","GOTO SCENE 7","GOTO SCENE 8",
			/*25-31*/		"GOTO SCENE 9","GOTO SCENE 10","GOTO SCENE 11","GOTO SCENE 12","GOTO SCENE 13","GOTO SCENE 14","GOTO SCENE 15",
			/*32-33*/		"RESET","STORE ACTUAL LEVELI N DTR",
			/*34-41*/		"NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM",
			/*42-45*/		"STORE DTR AS MAX LEVEL","STORE DTR AS MIN LEVEL","STORE DTR AS SYSTEM FAIL LEVEL","STORE DTR AS POWER ON LEVEL",
			/*46-47*/		"STORE DTR AS FADE TIME","STORE DTR AS FADE RATE",
			/*48-63*/		"NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM",
			/*64-69*/		"STORE DTR AS SCENE 0","STORE DTR AS SCENE 1","STORE DTR AS SCENE 2","STORE DTR AS SCENE 3","STORE DTR AS SCENE 4","STORE DTR AS SCENE 5",
			/*70-75*/		"STORE DTR AS SCENE 6","STORE DTR AS SCENE 7","STORE DTR AS SCENE 8","STORE DTR AS SCENE 9","STORE DTR AS SCENE 10","STORE DTR AS SCENE 11",
			/*76-79*/		"STORE DTR AS SCENE 12","STORE DTR AS SCENE 13","STORE DTR AS SCENE 14","STORE DTR AS SCENE 15",
			/*80-86*/		"REMOVE FROM SCENE 0","REMOVE FROM SCENE 1","REMOVE FROM SCENE 2","REMOVE FROM SCENE 3","REMOVE FROM SCENE 4","REMOVE FROM SCENE 5","REMOVE FROM SCENE 6",
			/*87-93*/		"REMOVE FROM SCENE 7","REMOVE FROM SCENE 8","REMOVE FROM SCENE 9","REMOVE FROM SCENE 10","REMOVE FROM SCENE 11","REMOVE FROM SCENE 12","REMOVE FROM SCENE 13",
			/*94-95*/		"REMOVE FROM SCENE 14","REMOVE FROM SCENE 15",
			/*96-104*/		"ADD TO GROUP 0","ADD TO GROUP 1","ADD TO GROUP 2","ADD TO GROUP 3","ADD TO GROUP 4","ADD TO GROUP 5","ADD TO GROUP 6","ADD TO GROUP 7","ADD TO GROUP 8",
			/*105-111*/		"ADD TO GROUP 9","ADD TO GROUP 10","ADD TO GROUP 11","ADD TO GROUP 12","ADD TO GROUP 13","ADD TO GROUP 14","ADD TO GROUP 15",
			/*112-118*/		"REMOVE FROM GROUP 0","REMOVE FROM GROUP 1","REMOVE FROM GROUP 2","REMOVE FROM GROUP 3","REMOVE FROM GROUP 4","REMOVE FROM GROUP 5","REMOVE FROM GROUP 6",
			/*119-125*/		"REMOVE FROM GROUP 7","REMOVE FROM GROUP 8","REMOVE FROM GROUP 9","REMOVE FROM GROUP 10","REMOVE FROM GROUP 11","REMOVE FROM GROUP 12","REMOVE FROM GROUP 13",
			/*126-127*/		"REMOVE FROM GROUP 14","REMOVE FROM GROUP 15",
			/*128*/			"STORE DTR AS SHORT ADDRESS",
			/*129-143*/		"NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM",
			/*144-150*/		"QUERY STATUS","QUERY CONTROL GEAR","QUERY LAMP FAIL", "QUERY LAMP POWER ON", "QUERY LIMIT ERROR","QUERY RESET state","QUERY MISSING SHORT ADDRESS",
			/*151-157*/		"QUERY VERSION NUMBER","QUERY CONTENT DTR","QUERY DEVICE TYPE","QUERY PHYSICAL MINIMUM LEVEL","QUERY POWER FAIL","QUERY CONTENT DTR1","QUERY CONTENT DTR2",
			/*158-159*/		"NO COM","NO COM",
			/*160-165*/		"query actual level","QUERY MAX LEVEL","QUERY MIN LEVEL","QUERY POWER ON LEVEL","QUERY SYSTEM FAIL LEVEL","QUERY FADE TIME/FADE RATE",
			/*166-175*/		"NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM",
			/*176-182*/		"QUERY SCENE LEVEL 0","QUERY SCENE LEVEL 1","QUERY SCENE LEVEL 2","QUERY SCENE LEVEL 3","QUERY SCENE LEVEL 4","QUERY SCENE LEVEL 5","QUERY SCENE LEVEL 6",
			/*182-189*/		"QUERY SCENE LEVEL 7","QUERY SCENE LEVEL 8","QUERY SCENE LEVEL 9","QUERY SCENE LEVEL 10","QUERY SCENE LEVEL 11","QUERY SCENE LEVEL 12","QUERY SCENE LEVEL 13",
			/*190-191*/		"QUERY SCENE LEVEL 14","QUERY SCENE LEVEL 15",
			/*192-197*/		"QUERY GROUPS 0-1","QUERY GROUPS 8-15","QUERY RANDOM ADDRESS (H)","QUERY RANDOM ADDRESS (M)","QUERY RANDOM ADDRESS (l)","READ MEMORY LOCATION",
			/*198-*/		"NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM",
			/*223*/			"NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM","NO COM",
			/*224*/			"QUERY APP EXT","QUERY APP EXT","QUERY APP EXT","QUERY APP EXT","QUERY APP EXT","QUERY APP EXT","QUERY APP EXT","QUERY APP EXT","QUERY APP EXT",
			/*-*/			"QUERY APP EXT","QUERY APP EXT","QUERY APP EXT","QUERY APP EXT","QUERY APP EXT","QUERY APP EXT","QUERY APP EXT","QUERY APP EXT","QUERY APP EXT",
							"QUERY APP EXT","QUERY APP EXT","QUERY APP EXT","QUERY APP EXT","QUERY APP EXT","QUERY APP EXT","QUERY APP EXT","QUERY APP EXT","QUERY APP EXT",
			/*254*/			"QUERY APP EXT","QUERY APP EXT","QUERY APP EXT","QUERY APP EXT",
			/*255*/			"QUERY EXTENDED VERSION NUMBER"
							};
	 
	 
	 private InputStream inStream = null;
	 byte[] readBuffer = new byte[1024];
	 static  Handler handler = new Handler(){
			  @Override
			  public void handleMessage(Message msg){
				  Bundle bundle = msg.getData();
				  String string = bundle.getString("answer");				 
				  String content = string;
				  String result = "";
				  if(content != ""){
					  result = content;
					  if(content.contains("Error") == false){
						  try {
							result = dali_decoder.decodeCommand(content);
							igrp = dali_decoder.getigrp();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					  }
				  }
				 
				  if(result != ""){					  
					 itemadder(result,true);
				  }
			  }
	 	};	 
	 
	 // Callback interface for selected directory
	 	public interface ChosenDirectoryListener{   
	 		public void onChosenDir(String chosenDir);
	 	}  	
	 	
	/** Called when the activity is first  . */ 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		notif();
		boolean success = true;
		super.onCreate(savedInstanceState);
	    if (savedInstanceState != null) {
	        Toast.makeText(this, savedInstanceState.getString("s"),
	                Toast.LENGTH_LONG).show();
	    }
	    setContentView(R.layout.command);    		//Set the content
	    
	    
	    Context context = this.getApplicationContext();
	    	NotificationManager notificationManager = (NotificationManager) context
	    	    .getSystemService(NOTIFICATION_SERVICE);
	    	
	 // Find the ListView resource.     
	    mainListView = (ListView) findViewById( R.id.mainListView ); 						//List View	   
	//    ArrayList<String> message_list = new ArrayList<String>(); 	
	    ArrayList<String> grouplist = new ArrayList<String>(); //Array list
	    listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, message_list);		//Array Adapter to add Items
		Intent intent = getIntent();		
		BluetoothThread BluetoothThread;
		BluetoothThread = MainActivity.get_thread();		
	//	BluetoothThread.connect(message);
		btSocket = BluetoothThread.get_connection();
		if(success == true){
			igrp = 0;
			itemadder("Link established!",true);
			itemadder("Start listening for Data.",true);
			beginListenForData();//Connect to choosen device
			//beginn listening or data write something 			
		}	
	}
	
	
	//******************************************************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		this.menu = menu;
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.command_actions, menu);	
	    return super.onCreateOptionsMenu(menu);
	}
	/***********************************/
	public static void itemadder(String content, boolean time){
		//Adds an item to the ListView
		//Param:  	Content
		//Return:	none
		//String msg = "";
		//msg = dali_encryptor(content);
		
		String grp = "B CAST";
		if(content.contains("DIRECT ARC POWER") == false){
			try {
				grp = dali_decoder.decodeGroup(igrp);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
		if(time == true){
			listAdapter.add(getCurrentTimeStamp() +":   " + grp + "\t\t\t" + content); 
		}
		else{
			listAdapter.add(content); 
		}
		if (pause == false){
			mainListView.setAdapter( listAdapter );
			mainListView.smoothScrollToPosition(listAdapter.getCount() - 1);	
		}
		//secondListView.smoothScrollToPosition(listAdapter.getCount() - 1);
	}
	/************************************/
	//When HW-button option is clicked
	//@Override
	//public boolean onKeyDown(int keycode, KeyEvent e) {
	  //  switch(keycode) {
	    //    case KeyEvent.KEYCODE_MENU:
	      //  	Toast.makeText(getApplicationContext(),"BTN",Toast.LENGTH_LONG).show(); //Commen
	        //    return true;
	    //}

	//    return super.onKeyDown(keycode, e);
	//}
	/*********************************/		
	/////////////////////////////////////////////
	//Listens to open file dialog and saves the choosen path
	//////////////////////////////////////
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            boolean fileCreated = false;   
            Bundle bundle = data.getExtras();
            if(bundle != null){
                    if(bundle.containsKey(FileChooserActivity.OUTPUT_NEW_FILE_NAME)) {
                            fileCreated = true;
                            File folder = (File) bundle.get(FileChooserActivity.OUTPUT_FILE_OBJECT);
                            String name = bundle.getString(FileChooserActivity.OUTPUT_NEW_FILE_NAME);
                            filePath = folder.getAbsolutePath() + "/" + name;
                            
                    } else {
                            fileCreated = false;
                            File file = (File) bundle.get(FileChooserActivity.OUTPUT_FILE_OBJECT);
                            filePath = file.getAbsolutePath();
                    }
            }
            itemadder("Selected Item: "+filePath.toString(),true);
            String message = fileCreated? "File created" : "File opened";
            message += ": " + filePath;
            if(filePath != ""){
            	loadhandler();
            }
        }
    }
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	    	case R.id.action_load:
	    		LoadFile();
	    		return true;	    	  
	        case R.id.action_clear:
	        	clearlist();
	            return true;
	        case R.id.action_save:
	        	SaveFile();
	        	
	            return true;	   
	        case R.id.action_as: 	        	
	        	AutoScroll();
	           return true;
	      //  case R.id.action_load:
	        //	return true;  
	        
	        default:
	            return super.onOptionsItemSelected(item);		//Return selected item
	    }
	}	
		
	public void LoadFile() {
		File sdcard = Environment.getExternalStorageDirectory();
		Intent intent = new Intent(this, FileChooserActivity.class);
		intent.putExtra(FileChooserActivity.INPUT_REGEX_FILTER, ".*dmf");
	    intent.putExtra(FileChooserActivity.INPUT_START_FOLDER, sdcard);
	    this.startActivityForResult(intent, 0);
					
	}
	public void loadhandler(){		
		clearlist(); 
		String[] splitpath =  filePath.split("¦");
        try{
               File f = new File(splitpath[0]+"/","¦"+splitpath[1]);
               InputStream fileIS = new FileInputStream(f);
               BufferedReader buf = new BufferedReader(new InputStreamReader(fileIS));
               String readString = new String();
               itemadder("Start importing File",true);
               while((readString = buf.readLine())!= null){
                  itemadder(readString,false);
               }
               itemadder("End of file",true);
            } catch (FileNotFoundException e) {
               e.printStackTrace();
            } catch (IOException e){
               e.printStackTrace();
            }
	}

	public void AutoScroll() {
		//Handles Auto scroll Option
		pause = !pause; 
    	Resources res = getResources();
    	if(!pause == true){	        		
    		menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_pause));	//Aendert das icon
    	}else{
    		menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_play));		//Aendert das icon
    	}
		
	}
	private void clearlist(){
		//Clear the List view.
		igrp = 0;
    	listAdapter.clear();		//Delete Content in List
    	mainListView.setAdapter( listAdapter );	//Reload Adapter
    	itemadder("Data Cleared!",true);		
	}
	private void SaveFile() {
		//Save the File in given folder
		//Intent intent = new Intent(this, FileChooserActivity.class);
    	//intent.putExtra(FileChooserActivity.INPUT_FOLDER_MODE, true);
        //this.startActivityForResult(intent, 0);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);	//new alert
    	alert.setTitle("Enter Filename");
    	alert.setMessage("Enter a file name!");
    	// Set an EditText view to get user input 
    	final EditText input = new EditText(this);
    	alert.setView(input);
    	alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
    	public void onClick(DialogInterface dialog, int whichButton) {
    		String value = input.getText().toString();    	  
    		String content = "";    	
      		for(String s : message_list){			//Reads out the items from the List
      			content += s + "\n";	   			//Create the content     		
      		}	       		
      		// Do something with value!
      		file_writer.writeFile(content,value);	//Writes the File
      		itemadder("File saved as: "+value+".dmf",true);
    	 }
    	});

    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	  public void onClick(DialogInterface dialog, int whichButton) {
    	    itemadder("Saving file aborded cause of user.",true);
    	  }
    	});

    	alert.show();
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    
	    outState.putString("s", "hello");
	}
	
	//listens for incomming data in the in stream!
	public void beginListenForData(){
		Thread receiveThread = new Thread(){
	        public void run(){
	        	try {	            	
	        		inStream = btSocket.getInputStream();		//Get Input stream of BT socket
	              //  inStream = new BufferedOutputStream(socket.getOutputStream());	    
	                // Keep listening to the InputStream until an exception occurs
	                while (true){
	                	if((mState == STATE_CONNECTED)){
	                		if((inStream.available() > 0)){	                	
	    		               // Read from the InputStream	                			
	    		               int rBcurLength = 0;
	    		               // Thread.sleep(30);	    		               
	    		               String total = "";
	    		               String x = "";	    		               
	    		               BufferedReader r = new BufferedReader(new InputStreamReader(inStream));
	    		               x = r.readLine();	                	
	    		                Message msg = handler.obtainMessage();
	    		        		Bundle bundle = new Bundle();	    		        	
	    		        		//String receivedString = new String(receiveBuffer);	//Create buffer	    		        			
	    		        		String receivedString = new String(x);
	    		        		if(receivedString == ""){
	    		        			receivedString = "Error string empty";
	    		        		}
	    		        		receivedString = receivedString.trim();
	    		        		bundle.putString("answer", receivedString);	//Put the answers in a Bundle
	    		                msg.setData(bundle);
	    		                handler.sendMessage(msg);
	    		                receiveBuffer = new byte[BUFFER_SIZE];
	                		}
	                	}
	                }
	            }catch (Exception e) {
	                if (isClosing)
	                	Toast.makeText(getApplicationContext(),"Thread terminated:" + e.getMessage() + ".",Toast.LENGTH_LONG).show();
	                    return;        
	            } 
	        }
		};		
		receiveThread.start();
	}
	public static String getCurrentTimeStamp(){
		//Get the current time of the Device/Timestamp
		//return time as String(hh:mm:ss)
		String mytime = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
		return mytime;

	}
	
	
	public void notif(){
		NotificationCompat.Builder mBuilder =
	    new NotificationCompat.Builder(this)
	    .setSmallIcon(R.drawable.ic_launcher)
	    .setContentTitle("DALI Monitor")
	    .setContentText("running...");
		//Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, command.class);
	
		//The stack builder object will contain an artificial back stack for the
		//started Activity.
		//This ensures that navigating backward from the Activity leads out of
		//your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		//Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(command.class);
		//Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		    stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		//mId allows you to update the notification later on.
		mNotificationManager.notify(123, mBuilder.build());
	}
}