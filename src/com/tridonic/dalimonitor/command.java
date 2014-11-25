package com.tridonic.dalimonitor;
//File: 	command.java
//design: 	command.xml
//date:		22.05.14
//(c) 2014 by Dario Duff
//Displays the incomming messages from Bluetoth Device.
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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import ar.com.daidalos.afiledialog.FileChooserActivity;

	public class command extends Activity {
	public static boolean success = true;	
	private static final int BUFFER_SIZE = 4096;
    private boolean isClosing;
    public byte[] receiveBuffer = new byte[BUFFER_SIZE];    	
	private int mState = 3;    
	private BluetoothSocket btSocket;
	public static boolean scroll = true;	
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
	private InputStream inStream = null;
	byte[] readBuffer = new byte[1024];		//Lese Buffer
	static  Handler handler = new Handler(){
			  @Override
			  public void handleMessage(Message msg){
				  Bundle bundle = msg.getData();
				  String string = bundle.getString("answer");				 	//Holt msg aus dem Bundle 
				  String content = string;			
				  String result = "";											
				  if(content != ""){
					  result = content;
					  if(content.contains("Error") == false){					//Nur wenn fehlerlos ausgeben
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
	 	
	/** Called when the activity is first created  . */ 
	@Override
	public void onCreate(Bundle savedInstanceState) {	
		//wird Beim ersten start der activity aufgrufen
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
			beginListenForData();			//Begin to read imput stream
			//beginn listening or data write something 			
		}	
	}	
	
	//******************************************************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Wird beim ersten start aufgerufen und erstellt das menu
		getActionBar().setDisplayHomeAsUpEnabled(true);
		this.menu = menu;
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.command_actions, menu);			//Erstellt menu
	    return super.onCreateOptionsMenu(menu);
	}
	/***********************************/
	public static void itemadder(String content, boolean time){
		//Adds an item to the ListView
		//Param:  	Content
		//Return:	none
		//String msg = "";
		//msg = dali_encryptor(content);
		///Adds a new item to the actual list view. Refreshes
		String grp = "B CAST";
		if(content.contains("DIRECT ARC POWER") == false){	//<---- DIRECT ARC POWER FUNKTION KONTROLLIEREN
			try {
				grp = dali_decoder.decodeGroup(igrp);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
		if(time == true){
			listAdapter.add(getCurrentTimeStamp() +":   " + grp + "\t\t\t" + content); 		//Fügt Gruppe+Befehl hinzu. 
		}
		else{
			listAdapter.add(content); //Keine Zeit angabe wird für das Laden von FIles verwendet
		}
		if (pause == false){}
		//secondListView.smoothScrollToPosition(listAdapter.getCount() - 1);
	}
	
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
	@SuppressWarnings("deprecation")
	public void showInfo() {
		//Shof infos about the device and the app version.
		final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE); 	//Create TelephonManager to read out manufacteur etc.
		AlertDialog alertDialog = new AlertDialog.Builder(this).create(); 		//Creates an alert dialogue
		alertDialog.setTitle("Info");
		alertDialog.setMessage("Device: "+Build.MANUFACTURER + " "+ Build.MODEL+"\nAndroid Version: "+android.os.Build.VERSION.RELEASE+"\nApp Version: v1.0.8\n\n(c) 2014 by Dario Duff(LoXeras Dev.)");
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() { //
		public void onClick(DialogInterface dialog, int which) {//DO SOMETHING
			
		}
		});
		alertDialog.setIcon(R.drawable.ic_action_help);
		alertDialog.show();
		
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
               itemadder("Start importing File",true);		//Adds message to list
               while((readString = buf.readLine())!= null){	//Reads line from file
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
		//Wenn autoscroll aktiv ist geht das list view bei aktualisierung mit
		//wennd eaktiviert bleibt das list view stehen
		pause = !pause; 
    	Resources res = getResources();
    	if(!pause == true){	        		
    		menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_pause));	//Aendert das icon
    	}else{
    		menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_play));		//Aendert das icon
    	}
		
	}
	public void clearlist(){
		//Clear the List view.
		igrp = 0;
    	listAdapter.clear();		//Delete Content in List
    	mainListView.setAdapter( listAdapter );	//Reload Adapter
    	itemadder("Data Cleared!",true);		
	}
	public void SaveFile() {
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
	    		        		if(receivedString == ""){	//Error handling
	    		        			receivedString = "Error string empty";
	    		        		}
	    		        		receivedString = receivedString.trim();		//Delete all the spaces 
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
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		//mId allows you to update the notification later on.
		mNotificationManager.notify(123, mBuilder.build());
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
		//command co = new command();
	    switch (item.getItemId()) {	
	    	case R.id.action_load:
	    		LoadFile();
	    		return true;	    	  
	        case R.id.action_clear:
	        	clearlist();
	            return true;	        case R.id.action_save:
	        	SaveFile();	        	
	            return true;	   
	        case R.id.action_as: 	        	
	        	AutoScroll();
	           return true;	           
	        case R.id.action_info:
	        	showInfo();
	        	return true;
	        
	        default:
	            return super.onOptionsItemSelected(item);		//Return selected item
	    }
	}	
	
}