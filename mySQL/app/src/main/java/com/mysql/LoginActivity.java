package com.mysql;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.view.View.*;
import android.view.*;
import java.net.*;
import java.io.*;
import android.util.*;
import android.support.v7.app.*;
import android.view.animation.*;
import org.json.*;

public class LoginActivity extends AppCompatActivity {

	EditText edtUserName, edtPw;
	Button btnLogin;
	String userName, pw;
	Animation open;
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_login);
		edtUserName = (EditText) findViewById (R.id.edtUserName);
		edtPw = (EditText) findViewById (R.id.edtPw);
		btnLogin = (Button) findViewById (R.id.btnLogin);
		open = AnimationUtils.loadAnimation (this, R.anim.open);
		edtUserName.startAnimation (open);
		edtPw.startAnimation (open);
		btnLogin.startAnimation (open);
		edtUserName.setVisibility (View.VISIBLE);
		edtPw.setVisibility (View.VISIBLE);
		btnLogin.setVisibility (View.VISIBLE);

		btnLogin.setOnClickListener (new OnClickListener () {
				@Override
				public void onClick (View v) {
					new Task ().execute (edtUserName.getText ().toString (), edtPw.getText ().toString ());
				}
			});
    }

	class Task extends AsyncTask<String, Void, String> {

		ProgressDialog pd = new ProgressDialog (LoginActivity.this);

		@Override
		protected void onPreExecute () {

			pd.setTitle ("Please wait...");
			pd.setCanceledOnTouchOutside(false);
			pd.show ();
			// TODO: Implement this method
			super.onPreExecute ();
		}

		@Override
		protected String doInBackground (String[] p1) {
			// TODO: Implement this method
			userName = p1[0];
			pw = p1[1];

			try {
				URL url = new URL ("http://127.0.0.1:8080/a/test.php");
				
				JSONObject json = new JSONObject ();
				JSONArray arr = new JSONArray ();
				json.put("001", "1");
				json.put("002", "1");
				String str = json.toString();
				HttpURLConnection conn = (HttpURLConnection) url.openConnection ();
				conn.setRequestMethod ("POST");
				conn.setDoOutput (true);
				conn.setDoInput (true);
				conn.setFixedLengthStreamingMode(str.length());
				conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
				conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
				conn.connect();

				OutputStream os = conn.getOutputStream ();
				BufferedWriter bw = new BufferedWriter (new OutputStreamWriter (os, "UTF-8"));
				String data = URLEncoder.encode ("name", "UTF-8") + "=" + URLEncoder.encode (userName, "UTF-8") + "&" + URLEncoder.encode ("id", "UTF-8") + "=" + URLEncoder.encode (pw, "UTF-8");
				bw.write (str);
				bw.flush ();
				bw.close ();
				os.close ();

				InputStream is = conn.getInputStream ();
				BufferedReader br = new BufferedReader (new InputStreamReader (is, "iso-8859-1"));
				String respone = "";
				String line = "";

				while ((line = br.readLine ()) != null) {
					respone += line;
				}

				br.close ();
				is.close ();
				conn.disconnect ();
				return respone;
				//Toast.makeText (MainActivity.this, respone, Toast.LENGTH_LONG).show ();
			}
			//catch (IOException e) {}

			/*catch ( e) {
			 Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
			 }*/
			catch (Exception e) {
				//Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
				Log.e ("LOI", e.getMessage ());
			}
			return null;
		}

		@Override
		protected void onPostExecute (final String result) {
			new Handler ().postDelayed (new Runnable () {
					@Override
					public void run () {
						pd.dismiss ();
						if (result != null)
							Toast.makeText (LoginActivity.this, result, Toast.LENGTH_LONG).show ();
					}
				}, 1000);

			// TODO: Implement this method
			super.onPostExecute (result);
		}

	}

}
