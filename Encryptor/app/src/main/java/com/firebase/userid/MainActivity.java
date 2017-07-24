package com.firebase.userid;
import android.support.v7.app.*;
import android.os.*;
import android.graphics.drawable.*;
import android.graphics.*;
import android.text.*;
import android.text.style.*;
import android.support.design.widget.*;
import android.view.animation.*;
import android.view.View.*;
import android.view.*;
import android.content.*;
import android.app.*;
import com.google.firebase.auth.*;
import android.support.v7.widget.*;
import android.widget.*;
import com.google.android.gms.tasks.*;
import android.support.annotation.*;
import com.google.firebase.*;

public class MainActivity extends AppCompatActivity {
	boolean isOpen=false;
	FloatingActionButton fabRoot, fabEncrypt, fabDecrypt;
	TextView txt;
	Animation fabOpen, fabClose, fabClockwise, fabAntiClockwise;
	FirebaseAuth firebaseAuth;
	FirebaseUser user;
	android.support.v7.widget.Toolbar toolbar;

	public void initViews () {
		firebaseAuth.getInstance ();
		toolbar = (android.support.v7.widget.Toolbar) findViewById (R.id.toolbar);
		toolbar.setTitle ("Overview");
		setSupportActionBar (toolbar);
		//	txt = (TextView) findViewById(R.id.txt);
		fabRoot = (FloatingActionButton)findViewById (R.id.fabRoot);
		fabEncrypt = (FloatingActionButton)findViewById (R.id.fabEncrypt);
		fabDecrypt = (FloatingActionButton)findViewById (R.id.fabDecrypt);
		fabOpen = AnimationUtils.loadAnimation (getApplicationContext (), R.anim.fab_open);
		fabClose = AnimationUtils.loadAnimation (getApplicationContext (), R.anim.fab_close);
		fabClockwise = AnimationUtils.loadAnimation (getApplicationContext (), R.anim.rotate_clockwise);
		fabAntiClockwise = AnimationUtils.loadAnimation (getApplicationContext (), R.anim.rotate_anticlockwise);
	}

	public void eventsHandling () {
		fabRoot.setOnClickListener (new OnClickListener (){
				@Override
				public void onClick (View v) {
					//Toast.makeText(MainActivity.this, "click", Toast.LENGTH_SHORT).show();
					if (isOpen) {
						fabEncrypt.startAnimation (fabClose);
						fabDecrypt.startAnimation (fabClose);
						fabRoot.startAnimation (fabAntiClockwise);
						fabEncrypt.setClickable (false);
						fabDecrypt.setClickable (false);
						isOpen = false;
					}
					else {
						fabEncrypt.startAnimation (fabOpen);
						fabDecrypt.startAnimation (fabOpen);
						fabRoot.startAnimation (fabClockwise);
						fabEncrypt.setClickable (true);
						fabDecrypt.setClickable (true);
						isOpen = true;
					}
				}
			});

		
	}

	@Override
	public void onCreate (Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_main);
		initViews ();
		eventsHandling ();

	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		// TODO: Implement this method
		MenuInflater mMenu=getMenuInflater ();
		mMenu.inflate (R.menu.menu, menu);

		return true;
	}



	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder (MainActivity.this);
		android.support.v7.app.AlertDialog dialog;
		// TODO: Implement this method
		int itemId = item.getItemId ();
		switch (itemId) {
			case R.id.action_change_pw:
				final android.support.v7.app.AlertDialog dialog1;
				android.support.v7.app.AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder (MainActivity.this);
				View v1 = getLayoutInflater ().inflate (R.layout.dialog_change_pw, null);
				final EditText edtOldpw = (EditText) v1.findViewById (R.id.edtOldPw);
				final EditText edtNewPw = (EditText) v1.findViewById (R.id.edtNewPw);
				final EditText edtNewPw2 = (EditText) v1.findViewById (R.id.edtNewPw2);
				Button btnDone = (Button) v1.findViewById (R.id.btnDone);
				Button btnCancel = (Button) v1.findViewById (R.id.btnCancel);
				builder1.setView (v1);
				dialog1 = builder1.create ();
				dialog1.setCanceledOnTouchOutside (false);
				dialog1.show ();

				btnDone.setOnClickListener (new OnClickListener () {
						@Override
						public void onClick (View v2) {
							final String oldPw = edtOldpw.getText ().toString ();
							final String newPw=edtNewPw.getText ().toString ();
							final String newPw2=edtNewPw2.getText ().toString ();

							if (oldPw.isEmpty ())
								Toast.makeText (MainActivity.this, "Please input your old password", Toast.LENGTH_SHORT).show ();
							else if (newPw.isEmpty ())
								Toast.makeText (MainActivity.this, "Please input new password", Toast.LENGTH_SHORT).show ();
							else if (newPw2.isEmpty ())
								Toast.makeText (MainActivity.this, "Please confirm new password", Toast.LENGTH_SHORT).show ();
							else if (!newPw.equals (newPw2))
								Toast.makeText (MainActivity.this, "The confirming password does not match", Toast.LENGTH_SHORT).show ();
							else {
								dialog1.dismiss();
								final ProgressDialog pD = new ProgressDialog (MainActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
								pD.setMessage (getString (R.string.please_wait));
								pD.show ();
								user = FirebaseAuth.getInstance ().getCurrentUser ();
								final String email = user.getEmail ();
								AuthCredential credential = EmailAuthProvider.getCredential (email, oldPw);

								user.reauthenticate (credential).addOnCompleteListener (new OnCompleteListener<Void> () {
										@Override
										public void onComplete (@NonNull Task<Void> task) {
											pD.dismiss ();
											if (task.isSuccessful ()) {
												user.updatePassword (newPw).addOnCompleteListener (new OnCompleteListener<Void> () {
														@Override
														public void onComplete (@NonNull Task<Void> task) {
															if (task.isSuccessful ()) {
																dialog1.dismiss ();
																Toast.makeText (MainActivity.this, "Password changed", Toast.LENGTH_SHORT).show ();

															}
														}
													}).addOnFailureListener (new OnFailureListener () {
														@Override
														public void onFailure (@NonNull Exception e) {
															dialog1.show();
															if (e instanceof FirebaseNetworkException) 
																Toast.makeText (MainActivity.this, "A network error has occurred", Toast.LENGTH_LONG).show ();
															else if (e instanceof FirebaseAuthWeakPasswordException)
																Toast.makeText (MainActivity.this, "Password is too weak!", Toast.LENGTH_LONG).show ();
															else
																Toast.makeText (MainActivity.this, "An unexpected error has occurred! Please try again later", Toast.LENGTH_LONG).show ();
														}
													});
											}
											//else 
											//Toast.makeText (MainActivity.this, "The old password is wrong", Toast.LENGTH_SHORT).show ();
										}
									}).addOnFailureListener (new OnFailureListener () {
										@Override
										public void onFailure (@NonNull Exception e) {
											dialog1.show();
											if (e instanceof FirebaseAuthException) {
												String error=((FirebaseAuthException) e).getErrorCode ();
												if (error.equals ("ERROR_INVALID_EMAIL"))
													Toast.makeText (MainActivity.this, "Invalid email detected!", Toast.LENGTH_LONG).show ();
												else if (error.equals ("ERROR_USER_NOT_FOUND") || error.equals ("ERROR_WRONG_PASSWORD"))
													Toast.makeText (MainActivity.this, "The old password is wrong", Toast.LENGTH_LONG).show ();
												else
													Toast.makeText (MainActivity.this, error, Toast.LENGTH_LONG).show ();
											}
											else {
												String error = e.getMessage ().toString ();
												if (error.contains ("A network error"))
													Toast.makeText (MainActivity.this, "A network error has occurred", Toast.LENGTH_LONG).show ();
												else
													Toast.makeText (MainActivity.this, "An unexpected error has occurred! Please try again later", Toast.LENGTH_LONG).show ();
											}
										}
									});
							}
						}

					});

				btnCancel.setOnClickListener (new OnClickListener () {
						@Override
						public void onClick (View v3) {
							dialog1.dismiss ();
						}
					});

				break;

			case R.id.action_about_me:
				builder.setTitle (R.string.about_me);
				builder.setIcon (R.drawable.ic_info_black);
				builder.setMessage (R.string.author_info);
				builder.setPositiveButton ("Dismiss", new DialogInterface.OnClickListener () {
						@Override
						public void onClick (DialogInterface dialog, int which) {
							dialog.dismiss ();
						}
					});
				dialog = builder.create ();
				dialog.setCanceledOnTouchOutside (false);
				dialog.show ();
				break;

			case R.id.action_logout:
				builder.setTitle (R.string.logout);
				builder.setIcon (R.drawable.ic_info_black);
				builder.setMessage (R.string.logout_now);
				builder.setPositiveButton ("Yes", new DialogInterface.OnClickListener () {
						@Override
						public void onClick (DialogInterface dialog, int which) {
							dialog.dismiss ();
							final ProgressDialog pD = ProgressDialog.show (MainActivity.this, getString (R.string.please_wait), getString (R.string.logging_out), true);
							firebaseAuth.getInstance ().signOut ();
							finish ();
						}
					});
				builder.setNegativeButton ("No", new DialogInterface.OnClickListener () {
						@Override
						public void onClick (DialogInterface dialog, int which) {
							dialog.dismiss ();
						}
					});
				dialog = builder.create ();
				dialog.setCanceledOnTouchOutside (false);
				dialog.show ();
				break;
		}
		return super.onOptionsItemSelected (item);
	}



	@Override
	public void onBackPressed () {
		Intent intent = new Intent (Intent.ACTION_MAIN);
		intent.addCategory (Intent.CATEGORY_HOME);
		startActivity (intent);
	}

}
