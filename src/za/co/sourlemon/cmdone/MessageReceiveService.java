package za.co.sourlemon.cmdone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class MessageReceiveService extends IntentService {

	static int mId = 0;
	
	public MessageReceiveService() {
		super("MessageReceiveService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d("MRS","Handling...");
		try {
			ServerSocket server = new ServerSocket(7000);
			try
			{
				while (true)
				{
					Log.d("MRS","Attempting!");
					final Socket client = server.accept();
					new Thread(new Runnable() {
						public void run()
						{
							try
							{
								BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
								try
								{
									NotificationCompat.Builder mBuilder =
									        new NotificationCompat.Builder(getApplicationContext())
									        .setSmallIcon(R.drawable.ic_launcher)
									        .setContentTitle("Task Done")
									        .setContentText(in.readLine());
									// Creates an explicit intent for an Activity in your app
									Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);

									NotificationManager mNotificationManager =
									    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
									// mId allows you to update the notification later on.
									mNotificationManager.notify(mId++, mBuilder.build());
								}
								finally
								{
									in.close();
								}
							}
							catch (IOException e)
							{
								Log.e("CLIENT", e.getMessage());
							}
						}
					}).start();
				}
			}
			finally
			{
				server.close();
			}
		} catch (IOException e) {
			Log.e("SERVER", e.getMessage());
		}
	}

}
