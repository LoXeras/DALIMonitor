package com.tridonic.dalimonitor;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class BluetoothThread extends Thread {
	
	
	  //Context c;
	  //  public BluetoothThread(Context context){
	  //       c= context;
	  //   }
	    
	    //call with: 
    private Listener listener;
    private static ListView mainListView ;
	private static ArrayAdapter<String> listAdapter ;
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	  
	private BluetoothAdapter btAdapter = null;
	private Set<BluetoothDevice>pairedDevices;
	private BluetoothDevice device = null;
	private BluetoothSocket btSocket;

    /**
     * Listsner interface
     */
    public static interface Listener {
        void onConnected();
        void onReceived(byte[] buffer, int length);
        void onDisconnected();
        void onError(IOException e);
    }    
    public void connect(String SID){    	
    	boolean success;		
 		String message = SID;
 	    //intext.setText(getCurrentTimeStamp());			//Read out the actual time of the system			
 		btAdapter = BluetoothAdapter.getDefaultAdapter();
 		try{
 	    device = btAdapter.getRemoteDevice(message.toString());	//Set device to connect
 		}
 		catch(Exception e){
 		//	Toast.makeText(c,""+e,Toast.LENGTH_LONG).show();
 		}		
 		try {
         	btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);        	
         } catch (IOException e) {
         //	Toast.makeText(c,"Socket listen() failed",Toast.LENGTH_LONG).show();
         }			
 		btAdapter.cancelDiscovery();					//Cancel discovery for more battery life
 		try {
 			btSocket.connect();	//
 		} catch (IOException e) {
 			success = false;
 			//Toast.makeText(c,"connection failed",Toast.LENGTH_LONG).show();
 			e.printStackTrace();
 			while(true){
 			;}		
 		}
 		Thread receiveThread = new Thread(){
 			
	        public void run(){
	        	try {
	        		while(true){
	        			Thread.sleep(1000);
	        			
	        		}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
	        }
 		};receiveThread.setDaemon(true);
 		receiveThread.start();
    }
    public BluetoothSocket get_connection(){
    	return btSocket;
    }
    
  
}

