package com.testAsyncTask;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.view.View.*;
import android.view.*;
import android.content.*;

public class MainActivity extends Activity 
{
	
	EditText edt;
	Button btn;
	LinearLayout lnl;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		edt = (EditText) findViewById(R.id.edt);
		btn = (Button) findViewById(R.id.btn);
		lnl = (LinearLayout) findViewById(R.id.lnl);
		
		btn.setOnClickListener(new OnClickListener () {
			@Override
			public void onClick(View v) {
				int n = Integer.parseInt(edt.getText().toString());
				ButtonTask task = new ButtonTask ();
				task.execute(n);
			}
		});
    }
	
	class ButtonTask extends AsyncTask<Integer, Integer, Void> {
		
		boolean check;

		@Override
		protected void onPreExecute () {
			// TODO: Implement this method
			super.onPreExecute ();
			lnl.removeAllViews();
		}

		@Override
		protected void onProgressUpdate (Integer... values) {
			// TODO: Implement this method
			super.onProgressUpdate (values);
			int value = values[0];
			Button button = new Button (MainActivity.this);
			button.setLayoutParams(new LinearLayout.LayoutParams (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			button.setText(value+"");
			lnl.addView(button);/*
			AlertDialog.Builder builder = new AlertDialog.Builder (MainActivity.this);
			builder.setMessage("Choose");
			builder.setPositiveButton ("Yes", new DialogInterface.OnClickListener () {
				@Override
				public void onClick (DialogInterface dialog, int i) {
					check = true;
					dialog.dismiss();
				}
			});
			
			AlertDialog dialog = builder.create();
			dialog.setCanceledOnTouchOutside(false);
			dialog.setCancelable(false);
			dialog.show();*/
			
		}

		@Override
		protected Void doInBackground (Integer... p1) {
			// TODO: Implement this method
			int n = p1[0];
			for (int i = 0; i < n; i++) {
				check = false;
				publishProgress(i);
			/*	while (!check) {
					
				}*/
			}
			return null;
		}

		@Override
		protected void onPostExecute (Void result) {
			// TODO: Implement this method
			super.onPostExecute (result);
		}
		
	}
	
}
