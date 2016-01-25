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
			con = new FTPClient();
			con.connect("31.170.161.76");

			if (con.login("a5693422", "test123")) {
				con.enterLocalPassiveMode(); // important!
				con.setFileType(FTP.BINARY_FILE_TYPE);

				String data = Environment.getExternalStorageDirectory().getAbsolutePath() + "/record.3gpp";

				FileInputStream in = new FileInputStream(new File(data));

				boolean result = con.storeFile("public_html/docs/record.3gpp", in);

				in.close();
				if (result) {
					Log.v("upload result", "succeeded");
					//Toast.makeText(getApplicationContext(), "upload result",Toast.LENGTH_SHORT).show();
				}
				con.logout();
				con.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		return null;
    }
    
}