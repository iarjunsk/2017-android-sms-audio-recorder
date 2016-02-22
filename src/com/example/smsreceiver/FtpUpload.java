package com.example.smsreceiver;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class FtpUpload extends AsyncTask<Void, Void, Void> {
    
	protected Void doInBackground(Void... params) {
		
		FTPClient con = null;

		try {
			con = new FTPClient();  // con is the FTPClient
			con.connect("121.121.121.121"); //set the ip address of the server

			if (con.login("ftp_username", "ftp_password")) {   // checks if login is successful
				
				con.enterLocalPassiveMode(); // important!
				
				con.setFileType(FTP.BINARY_FILE_TYPE);

				String data = Environment.getExternalStorageDirectory().getAbsolutePath() + "/record.3gpp"; // file to upload
				
				FileInputStream in = new FileInputStream(new File(data)); 
				boolean result = con.storeFile("public_html/docs/record.3gpp", in);  // where to store in the server
				in.close();
				
				if (result) {
					Log.v("upload result", "succeeded");
					//Toast.makeText(getApplicationContext(), "upload Sucess",Toast.LENGTH_SHORT).show();
				}
				con.logout();  // logout ftp
				con.disconnect(); //disconnect ftp
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		return null;
    }
    
}