package com.tridonic.dalimonitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.util.Log;

public class file_writer extends Thread {
    public static interface Listener {
        void onConnected();
        void onReceived(byte[] buffer, int length);
        void onDisconnected();
        void onError(IOException e);
    }    
    public static void writeFile(String content, String name){   
    	 Calendar cal = new GregorianCalendar();
    	 //String filename="¦monitor_log_"+cal.get(Calendar.DAY_OF_MONTH)+"-"+cal.get(Calendar.MONTH)+"-"+cal.get(Calendar.YEAR);
    	 String filename = "¦"+name;
    	 //String filename="test";
         String data=content.toString();
         FileOutputStream fos;           
         //Try read file from 
            try {  
                File myFile = new File("/sdcard/"+filename+".dmf");  
                 myFile.createNewFile();  
                 FileOutputStream fOut = new FileOutputStream(myFile);  
                 OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);  
                 myOutWriter.append(data);  
                 myOutWriter.close();  
                 fOut.close();  
                //
              
     //Toast.makeText(getApplicationContext(),filename +"saved",Toast.LENGTH_LONG).show();  
               
              
            } catch (FileNotFoundException e) {e.printStackTrace();}  
            catch (IOException e) {e.printStackTrace();}  
     
       
    }  
    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
          sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }
    
    public static String getStringFromFile (String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();        
        return ret;
    }    
}

