package za.co.sourlemon.cmdone;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpConnection;

import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/*
		 * Creates a new Intent to start the MRService
		 * IntentService. Passes a URI in the
		 * Intent's "data" field.
		 */
		Intent mServiceIntent = new Intent(getApplicationContext(),
				MessageReceiveService.class);
		startService(mServiceIntent);
		
		new Thread(new Runnable() {
			public void run()
			{
				try {
					Log.d("GET_ID", "Getting id...");
					HttpURLConnection con = (HttpURLConnection)
							new URL("http://192.168.1.63:8000").openConnection();
					con.setRequestMethod("POST");
					con.addRequestProperty("Content-Type", "application/json");
					con.setDoOutput(true);
					con.setDoInput(true);
					con.setReadTimeout(2*1000);
					BufferedOutputStream out = new BufferedOutputStream(con.getOutputStream());
					out.write(("{\"action\":\"register\",\"port\":\"7000\",\"host\":\""+getIP()+"\"}").getBytes());
					out.close();
					InputStream is = con.getInputStream();
					BufferedReader in = new BufferedReader(new InputStreamReader(is));
					final String id = in.readLine();
					runOnUiThread(new Runnable() {
					  public void run() {
						  Toast.makeText(getApplicationContext(), "ID: "+id, Toast.LENGTH_LONG).show();
					  }
					});
					con.disconnect();
					Log.d("GET_ID", "Done.");
				} catch (MalformedURLException e) {
					Log.e("GET_ID", e.toString());
				} catch (IOException e) {
					Log.e("GET_ID", e.toString());
				}
			}
		}).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private String getIP()
	{
		WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
		int ip = wifiInfo.getIpAddress();
		return Formatter.formatIpAddress(ip);
	}
	
}
