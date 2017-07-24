package com.firebase.userid;

import android.app.ProgressDialog;
import android.app.ActionBar;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import android.view.View;
import android.view.View.*;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import java.security.*;
import android.util.*;
import android.content.pm.PackageManager.*;
import com.google.android.gms.tasks.*;
import com.google.firebase.auth.*;
import android.net.*;
import java.net.*;
import java.io.*;
import android.view.animation.*;
import android.widget.*;
import android.support.v7.app.*;
import android.graphics.drawable.*;
import android.graphics.*;
import android.text.*;
import com.google.firebase.*;


public class LoginActivity extends AppCompatActivity {

	private static final String TAG = "LoginActivity";
	EditText edtUname, edtPw;
	TextView txtForgotPw, txtReg;
	Button btnLogin;
	ImageView imgLogo;
	FirebaseAuth firebaseAuth;
	FirebaseUser user;

	Animation Open;


	public void userAuth (String uName, String pW) {
		final ProgressDialog pD = new ProgressDialog (LoginActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);


		pD.setTitle (R.string.please_wait);
		pD.setMessage (getString (R.string.logging_in));
		pD.show ();
		//pD.show (LoginActivity.this, getString (R.string.please_wait), getString (R.string.logging_in), true);
		firebaseAuth.signInWithEmailAndPassword (uName.trim (), pW.trim ()).addOnCompleteListener (new OnCompleteListener<AuthResult> () {
				@Override
				public void onComplete (@NonNull Task<AuthResult> task) {
					pD.dismiss ();
					if (task.isSuccessful ()) {
						user = firebaseAuth.getInstance ().getCurrentUser ();
						if (user.isEmailVerified ()) {
							edtPw.setText (null);
							startActivity (new Intent (LoginActivity.this, MainActivity.class));
						}
						else {
							firebaseAuth.getInstance ().signOut ();
							Toast.makeText (LoginActivity.this, getString (R.string.email_not_verified), Toast.LENGTH_SHORT).show ();
						}
					}
//							else
//								Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
				}
			}).addOnFailureListener (new OnFailureListener () {
				@Override
				public void onFailure (@NonNull Exception e) {
					if (e instanceof FirebaseAuthException) {
						String error=((FirebaseAuthException) e).getErrorCode ();
						if (error.equals ("ERROR_INVALID_EMAIL"))
							Toast.makeText (LoginActivity.this, "Invalid email detected!", Toast.LENGTH_LONG).show ();
						else if (error.equals ("ERROR_USER_NOT_FOUND") || error.equals ("ERROR_WRONG_PASSWORD"))
							Toast.makeText (LoginActivity.this, "Wrong email address or password!", Toast.LENGTH_LONG).show ();
					}
					else {
						String error = e.getMessage ().toString ();
						if (error.contains ("A network error"))
							Toast.makeText (LoginActivity.this, "A network error has occurred", Toast.LENGTH_LONG).show ();
						else
							Toast.makeText (LoginActivity.this, "An unexpected error has occurred! Please try again later", Toast.LENGTH_LONG).show ();
					}
				}
			});
	}

	public void initViews () {
		edtUname = (EditText) findViewById (R.id.edtUname);
		edtPw = (EditText) findViewById (R.id.edtPw);
		btnLogin = (Button) findViewById (R.id.btnLogin);
		txtForgotPw = (TextView) findViewById (R.id.txtForgotPw);
		txtForgotPw = (TextView) findViewById (R.id.txtForgotPw);
		txtReg = (TextView) findViewById (R.id.txtReg);
		imgLogo = (ImageView) findViewById (R.id.imgLogo);
		firebaseAuth = FirebaseAuth.getInstance ();
		user = firebaseAuth.getCurrentUser ();

		Open = AnimationUtils.loadAnimation (getApplicationContext (), R.anim.open);
		Handler handler=new Handler ();
		handler.postDelayed (new Runnable (){
				@Override
				public void run () {
					edtUname.startAnimation (Open);
					edtPw.startAnimation (Open);
					btnLogin.startAnimation (Open);
					txtForgotPw.startAnimation (Open);
					txtReg.startAnimation (Open);
					edtUname.setVisibility (View.VISIBLE);
					edtPw.setVisibility (View.VISIBLE);
					btnLogin.setVisibility (View.VISIBLE);
					txtForgotPw.setVisibility (View.VISIBLE);
					txtReg.setVisibility (View.VISIBLE);
				}
			}, 700);

		if (user != null)
			handler.postDelayed (new Runnable () {
					@Override
					public void run () {
						startActivity (new Intent (LoginActivity.this, MainActivity.class));
					}
				}, 600);
	}

	public void eventsHandling () {

		edtUname.addTextChangedListener (new TextWatcher () {

				@Override
				public void afterTextChanged (Editable s) {}

				@Override    
				public void beforeTextChanged (CharSequence s, int start,
											   int count, int after) {
				}

				@Override    
				public void onTextChanged (CharSequence s, int start,
										   int before, int count) {
					//Toast.makeText(getApplicationContext(), "text changed", Toast.LENGTH_SHORT).show();
					if (s.length () != 0)
						txtForgotPw.setText (R.string.forgot_pw);
					else
						txtForgotPw.setText (null);
				}
			});

		btnLogin.setOnClickListener (new OnClickListener (){
				@Override
				public void onClick (View v) {
					String uName, pW;
					uName = edtUname.getText ().toString ();
					pW = edtPw.getText ().toString ();
					if (uName.isEmpty () || pW.isEmpty ())
						Toast.makeText (getApplicationContext (), "You must fill both fields", Toast.LENGTH_LONG).show ();
					else {
						userAuth (uName, pW);
					}
				}
			});

		txtForgotPw.setOnClickListener (new OnClickListener () {
				@Override
				public void onClick (View v) {

					final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder (LoginActivity.this, R.style.MyDialogTheme);
					final android.support.v7.app.AlertDialog aDialog;
					builder.setMessage (R.string.confirm_reset_pw);


					builder.setPositiveButton ("Yes", new DialogInterface.OnClickListener () {
							@Override
							public void onClick (DialogInterface dialog, int which) {
								dialog.dismiss();
								final ProgressDialog pD = new ProgressDialog (LoginActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
								pD.setMessage (getString (R.string.please_wait));
								pD.show ();
								
								String emailAddress = edtUname.getText ().toString ().trim ();
								firebaseAuth.sendPasswordResetEmail (emailAddress)
									.addOnCompleteListener (new OnCompleteListener<Void> () {
										@Override
										public void onComplete (@NonNull Task<Void> task) {
											pD.dismiss();
											if (task.isSuccessful ())
												Toast.makeText (getApplicationContext (), getString (R.string.reset_pw_email), Toast.LENGTH_SHORT).show ();
										}
									}).addOnFailureListener (new OnFailureListener () {
										@Override
										public void onFailure (@NonNull Exception e) {
											e.getClass ().toString ();
											if (e instanceof FirebaseException) {
												String error = ((FirebaseException) e).getMessage ();
												if (error.equals("An internal error has occurred. [ INVALID_EMAIL ]"))
													Toast.makeText (LoginActivity.this, "Invalid email detected!", Toast.LENGTH_LONG).show ();
												else if (error.equals("There is no user record corresponding to this identifier. The user may have been deleted."))
													Toast.makeText (LoginActivity.this, "Can't find this email", Toast.LENGTH_LONG).show ();
												else if (error.equals("An internal error has occurred. [ <<Network Error>> ]"))
													Toast.makeText (LoginActivity.this, "A network error has occurred", Toast.LENGTH_LONG).show ();
												else
													Toast.makeText (LoginActivity.this, "Something went wrong! Please try again later", Toast.LENGTH_LONG).show ();
												 
											}
											else {
												Toast.makeText (LoginActivity.this, "Something went wrong! Please try again later", Toast.LENGTH_LONG).show ();
											}
										}
									});
							}
						});
	
					builder.setNegativeButton ("No", new DialogInterface.OnClickListener () {
							@Override
							public void onClick (DialogInterface dialog, int which) {
								dialog.dismiss ();
							}
						});
					aDialog = builder.create ();
					aDialog.setCanceledOnTouchOutside (false);
					aDialog.show ();
					
				}
			});

		txtReg.setOnClickListener (new OnClickListener () {
				@Override
				public void onClick (View v) {
					final AlertDialog dialog1;
					AlertDialog.Builder builder = new AlertDialog.Builder (LoginActivity.this);
					View v1 = getLayoutInflater ().inflate (R.layout.dialog_register, null);
					final EditText edtRegEmail = (EditText) v1.findViewById (R.id.edtRegEmail);
					final EditText edtRegPw = (EditText) v1.findViewById (R.id.edtRegPw);
					final EditText edtRegPw2 = (EditText) v1.findViewById (R.id.edtRegPw2);
					Button btnReg = (Button) v1.findViewById (R.id.btnReg);
					Button btnCancel = (Button) v1.findViewById (R.id.btnCancel);
					builder.setView (v1);
					dialog1 = builder.create ();
					dialog1.setCanceledOnTouchOutside (false);
					dialog1.show ();

					btnCancel.setOnClickListener (new OnClickListener () {
							@Override
							public void onClick (View v2) {
								dialog1.dismiss ();
							}
						});

					btnReg.setOnClickListener (new OnClickListener () {
							@Override
							public void onClick (View v3) {
								final String regEmail=edtRegEmail.getText ().toString ();
								final String regPw=edtRegPw.getText ().toString ();
								final String regPw2=edtRegPw2.getText ().toString ();

								if (regEmail.isEmpty ())
									Toast.makeText (LoginActivity.this, "Please input your email address", Toast.LENGTH_SHORT).show ();
								else if (regPw.isEmpty ())
									Toast.makeText (LoginActivity.this, "Please input your password", Toast.LENGTH_SHORT).show ();
								else if (regPw2.isEmpty ())
									Toast.makeText (LoginActivity.this, "Please confirm your password", Toast.LENGTH_SHORT).show ();
								else if (!regPw.equals (regPw2))
									Toast.makeText (LoginActivity.this, "The confirming password does not match", Toast.LENGTH_SHORT).show ();
								else {
									dialog1.dismiss ();
									final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder (LoginActivity.this, R.style.MyDialogTheme);
									final android.support.v7.app.AlertDialog aDialog;
									builder.setTitle (R.string.confirm_registration);
									builder.setMessage (R.string.confirm_email_exists);


									builder.setPositiveButton ("Yes", new DialogInterface.OnClickListener () {
											@Override
											public void onClick (DialogInterface dialog, int which) {
												dialog.dismiss ();
												final String tmp1 = regEmail.trim (), tmp2 = regPw.trim ();
												final ProgressDialog pD = new ProgressDialog (LoginActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
												pD.setTitle (R.string.please_wait);
												pD.setMessage (getString (R.string.registering));
												pD.show ();
												firebaseAuth.createUserWithEmailAndPassword (tmp1, tmp2).addOnCompleteListener (new OnCompleteListener<AuthResult> () {
														@Override
														public void onComplete (@NonNull Task<AuthResult> task) {
															pD.dismiss ();
															if (task.isSuccessful ()) {
																Toast.makeText (getApplicationContext (), getString (R.string.registration_complete), Toast.LENGTH_SHORT).show ();
																dialog1.dismiss ();
																user = firebaseAuth.getInstance ().getCurrentUser ();
																user.sendEmailVerification ().addOnCompleteListener (new OnCompleteListener<Void> () {
																		@Override
																		public void onComplete (@NonNull Task<Void> task) {
																			if (task.isSuccessful ()) {
																				Toast.makeText (getApplicationContext (), "Verification email has been sent. Please check your inbox!", Toast.LENGTH_LONG).show ();
																			}
																		}
																	});
															}
															else
																dialog1.show ();
															//Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
														}
													}).addOnFailureListener (new OnFailureListener () {
														@Override
														public void onFailure (@NonNull Exception e) {
															dialog1.show ();
															if (e instanceof FirebaseAuthException) {
																String error = ((FirebaseAuthException) e).getErrorCode ();
																//Toast.makeText (LoginActivity.this, error, Toast.LENGTH_LONG).show ();
																if (error.equals ("ERROR_INVALID_EMAIL"))
																	Toast.makeText (LoginActivity.this, "Invalid email detected!", Toast.LENGTH_LONG).show ();
																else if (error.equals ("ERROR_WEAK_PASSWORD"))
																	Toast.makeText (LoginActivity.this, "Password is too weak!", Toast.LENGTH_LONG).show ();
																else if (error.equals ("ERROR_EMAIL_ALREADY_IN_USE"))
																	Toast.makeText (LoginActivity.this, "This email is in use!", Toast.LENGTH_LONG).show ();
															}
															else {
																String error = e.getMessage ().toString ();
																if (error.contains ("A network error"))
																	Toast.makeText (LoginActivity.this, "A network error has occurred", Toast.LENGTH_LONG).show ();
																else
																	Toast.makeText (LoginActivity.this, "An unexpected error has occurred! Please try again later", Toast.LENGTH_LONG).show ();
															}
														}
													});
											}
										});
									builder.setNegativeButton ("No", new DialogInterface.OnClickListener () {
											@Override
											public void onClick (DialogInterface dialog, int which) {
												dialog.dismiss ();
												dialog1.show ();
											}
										});
									aDialog = builder.create ();
									aDialog.setCanceledOnTouchOutside (false);
									aDialog.show ();
								}
							}
						});
				}
			});
	}

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_login);
		initViews ();


		eventsHandling ();

    }

	@Override
	protected void onResume () {


		// TODO: Implement this method
		super.onResume ();
	}

	@Override
	public void onBackPressed () {

		super.onBackPressed ();
	}

}
