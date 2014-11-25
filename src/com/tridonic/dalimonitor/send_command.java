package com.tridonic.dalimonitor;
//File: 	send_command.java
//design: 	--
//date:		4.11.14
//(c) 2014 by Dario Duff
//Ist für das senden von commands an das verbundene Bluetooth gerät zust$ndig.
////////////////////////////////////////////////////////////////////////////////
import java.io.IOException;
import java.io.IOException;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;

public class send_command extends Thread {
	
	private BluetoothSocket btSocket;
	private OutputStream outStream = null;
	
	
	public void send(String msg){
		//Sends a command to DEvice
		
		BluetoothThread BluetoothThread;
		BluetoothThread = MainActivity.get_thread();	//get bluetooth thread 
		btSocket = BluetoothThread.get_connection();	//Get bluetooth connection
		byte[] msgBufferTermination = {0xA,0xD};		//Msg termination
		byte[] msgBuffer = msg.getBytes();  			//Translate message to byte
		
	    try {
			outStream = btSocket.getOutputStream();		//get stream
			outStream.write(msgBuffer);					//send to device
		    outStream.write(msgBufferTermination); 		//send termination to device
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    
	}

}
