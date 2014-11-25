package com.tridonic.dalimonitor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;


public class device_search {
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
	
	public static boolean pause = false;
	public static int igrp;
	public static String filePath = "";
	private static ArrayAdapter<String> listAdapter ;
	public int daliaddress = 0;
	
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
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				  }
			  }
				 /*
				  if(result != ""){					  
					 itemadder(result,true);*/
				  
				  
		 }
	};
		  
	public void onCreate(Bundle savedInstanceState) {							   		
				BluetoothThread BluetoothThread;
				BluetoothThread = MainActivity.get_thread();		
			//	BluetoothThread.connect(message);
				btSocket = BluetoothThread.get_connection();						
	}	
	
	public ArrayAdapter<String> search(){
		send_command send_command = new send_command();
	    send_command.send("254 ");	//INSERT COMMAND TO IDENTIFY DEVICES
		while(daliaddress != 254){}
		
		
		return listAdapter;
	}

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
	    		               // Thread.sleep(30);
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
	                	return;        
	            } 
	        }
		};		
		receiveThread.start();
	}
	
	
	
	
}
