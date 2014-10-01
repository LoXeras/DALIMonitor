package com.tridonic.dalimonitor;
//File: 	dali_decoder.java
//design: 	-
//date:		1.10.14
//(c) 2014 by Dario Duff
//decodes the DALI Command and the Groups to readable strings.
////////////////////////////////////////////////////////////////////////////////

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

public class dali_decoder extends Thread {
	public static int igrp;
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
	public static final String[] GROUPS = {	"ADD 0","ADD 1","ADD 2","ADD 3","ADD 4","ADD 5","ADD 6","ADD 7","ADD 8","ADD 9",
											"ADD 10","ADD 11","ADD 12","ADD 13","ADD 14","ADD 15","ADD 16","ADD 17","ADD 18","ADD 19",
											"ADD 20","ADD 21","ADD 22","ADD 23","ADD 24","ADD 25","ADD 26","ADD 27","ADD 28","ADD 29",
											"ADD 30","ADD 31","ADD 32","ADD 33","ADD 34","ADD 35","ADD 36","ADD 37","ADD 38","ADD 39",
											"ADD 40","ADD 41","ADD 42","ADD 43","ADD 44","ADD 45","ADD 46","ADD 47","ADD 48","ADD 49",
											"ADD 50","ADD 51","ADD 52","ADD 53","ADD 54","ADD 55","ADD 56","ADD 57","ADD 58","ADD 59",
											"ADD 10","ADD 61","ADD 62","ADD 63",		
	};

    public static interface Listener {
        void onConnected();
        void onReceived(byte[] buffer, int length);
        void onDisconnected();
        void onError(IOException e);
    }     
    public static String decodeCommand(String command) throws Exception {
    	//Nimmt die einzelnen Befehle auseinander und wandelt sie in einen Lesbaren Befehl um
    	char[] cmd = "".toCharArray();
    	if(command.length() != 0){
    		cmd = command.toCharArray();
    	}else{
    		return "";
    	}
    	int strlen = 	command.length();
    	String answer = "";	
    	
    	
    	if(cmd[0] == 83 && cmd[strlen-1] == 69){
    		if(command.contains(" ") == false){
    			command = command.substring(0,command.length()-1);
    			command = command.replace('S', ' ');
    			command = command.trim();
    			answer = "Answer: " + command;
    			
    		}else{
    			String[] seperated = command.split(" ");		//Splitted befehl und wert auf
    			String com = seperated[1].substring(0,seperated[1].length()-1);	//Entfernt das "E"
    			String grp = seperated[0].replace('S', ' ');	//Entfernt das "S"
    			grp = grp.trim();								//Entfernt " "
    			igrp = Integer.parseInt(grp);				//grp in int umwandeln
    			int icom = Integer.parseInt(com);				//com in int umwandelnString fullstring = "";	
    			//Array for all the commands 
    			
    			
    			if(igrp == 254){
    				answer = "DIRECT ARC POWER "+icom;
    			}
    			else{
    				try{
    					answer = COMMANDS[icom];
    				}catch(Exception e){
    					System.out.println(e);
    					answer = "ERROR no such command: "+e;
    				}
    			}			
    		}
    	}
    	return answer;
    }    
    public static String decodeGroup(int set_igrp) throws Exception {
       igrp = set_igrp;
       String answer = "";
       igrp = igrp/2;
       answer = GROUPS[igrp];
       
       return answer;
    }
    public static int getigrp(){
    	return igrp;
    }
}

