package com.example.decryption;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	//SINGLE: location;
	//Chest: Multiple: ACC,Chest
	public final static String SINGLE_UPLOAD_ADDRESS = "http://dslsrv8.cs.missouri.edu/~hw85f/Server/Crt2/writeArrayToFileDec.php";
	public final static String MULTI_UPLOAD_ADDRESS = "http://dslsrv8.cs.missouri.edu/~hw85f/Server/Crt2/dealWithBackup.php";
	private static final String FILE_NAME = "recovery/chestsensor.EQ02_4512006.D_11.txt";
	private static final int ACC_RECORD = 6;
	private static final int CHEST_RECORD = 6;
	private static final int LOC_RECORD = 7;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads()
				.detectDiskWrites()
				.detectNetwork() // or .detectAll() for all detectable problems
				.penaltyLog()
				.build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects()
				.detectLeakedClosableObjects()
				.penaltyLog()
				.penaltyDeath()
				.build());
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final Button btn = (Button) findViewById(R.id.btn);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					decrypt();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	protected void decrypt() throws IOException {
		// TODO Auto-generated method stub
		File dir = Environment.getExternalStorageDirectory();
		File encrypt = new File(dir, FILE_NAME);
		BufferedReader br = new BufferedReader(new FileReader(encrypt));
		String temp = "";
		int record = 0;
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				synchronized (temp) {
					sb.append(line);
					//					sb.append(System.lineSeparator());
					line = br.readLine();
					record++;
					if (record == ACC_RECORD) {
						//												TransmitData t = new TransmitData();
//												t.execute(temp);
						//												t.execute("QSE3pBqEjdFbDOjIvzNmKW74D31ek05jdFO2M4O9soQwGv1+/MHWWnh0Uk1jQejVBm/2OYfRDqnSaj9Wv1abXYtNYS4FFj37jM9GG4/t+t1GPLcyFP1ie+f5h7B2eNAT7B9DiNUlDvHRcHBwCcjJCu5Qy4iD2sB2vxKx1wY1GzhCUDiSd+TxgagBDKyc5KmPJpI4aVEleXIXseCJyK3pFihwY2gnAQuRV1Eh3ZujQHCswpYDhohua5AE02p+9oiDWPEllYGNyzUswZE/5YxH4SgR8elOMGNyhW3zi1txavas7dAh42WeGoyzWtYSYHJaOzUJaAbv/x8+63RC7KwVINQJGf2htqT+zNw+/RxWhnR2TgJPnjGhmVYQNhELYXtevmF9JqD0FmMKuIUNlLV/zyKLgsROO3dWIUwlPm3eeQbrFn7GuUp5Zl6HZWSyJP5+lJo2im7GsHPOiGuKFY4CsEjTzwq3JueJeYrv8YNHblc=");
						temp = sb.toString();
						HttpPost request = new HttpPost(SINGLE_UPLOAD_ADDRESS);
						//						HttpPost request = new HttpPost(MULTI_UPLOAD_ADDRESS);
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						//single
						//						params.add(new BasicNameValuePair("file_name", "chestsensor.1005.EQ02_4512006.D_11"));
						params.add(new BasicNameValuePair("data", temp));
						Log.d("ricky", temp);
						Log.d("ricky", "---------------------------------------");
						try {
							request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
							HttpResponse response = new DefaultHttpClient().execute(request);
						} catch (Exception e)
						{
							e.printStackTrace();
						}
						temp = "";
						record = 0;
						sb.setLength(0);
					}
				}
			}
			//			String everything = sb.toString();
			//			Log.d("ricky", everything);
		} finally {
			br.close();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private class TransmitData extends AsyncTask<String,Void, Boolean>
	{
		@Override
		protected Boolean doInBackground(String... strings) {
			String data = strings[0];
			//			Log.d("ricky", data);
			HttpPost request = new HttpPost(SINGLE_UPLOAD_ADDRESS);
	         List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("data", data));
	         try {

	             request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
	             HttpResponse response = new DefaultHttpClient().execute(request);
				//				Log.d("ricky", String.valueOf(response.getStatusLine().getStatusCode()));
	             if(response.getStatusLine().getStatusCode() == 200){
					String result = EntityUtils.toString(response.getEntity());
					Log.d("ricky", result);
	             }
	             return true;
	         }
	         catch (Exception e)
	         {
	             e.printStackTrace();
	             return false;
	         }
	 	  }

	}
}
