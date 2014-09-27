package com.kakao.kakaocampus;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kakao.APIErrorResult;
import com.kakao.UpdateProfileResponseCallback;
import com.kakao.UserManagement;
import com.kakao.UserProfile;
 
public class NewKaCamProfileActivity extends Activity {
 
    // Progress Dialog
    private ProgressDialog pDialog;
 
    JSONParser jsonParser = new JSONParser();
    EditText inputUniversity;
    EditText inputPhone;
    EditText inputEmail;
    EditText inputStudent_Id;
    private UserProfile userProfile;
 
    // url to create new product
    //private static String url_create_product = "http://api.androidhive.info/android_connect/create_product.php";
 
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ka_cam_profile);
 
        // Edit Text
        inputUniversity = (EditText) findViewById(R.id.inputUniversity);
        inputPhone = (EditText) findViewById(R.id.inputPhone);
        inputEmail = (EditText) findViewById(R.id.inputEmail);
        inputStudent_Id = (EditText) findViewById(R.id.inputStudent_Id);
 
        // Create button
        Button btnUpdateKaCamProfile = (Button) findViewById(R.id.updateKaCamProfile);
 
        // button click event
        btnUpdateKaCamProfile.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View view) {
                // creating new product in background thread
                //new UpdateKaCamProfile().execute();
            	requestUpdateProfile();
            }
        });
    }
	// Update UserProfile for input University, Phone, Email, and StudentID
    
	private void requestUpdateProfile() {
		String university = inputUniversity.getText().toString();
		String phone = inputPhone.getText().toString();
		String email = inputEmail.getText().toString();
		String student_id = inputStudent_Id.getText().toString();

        userProfile = UserProfile.loadFromCache();
	    final Map<String, String> properties = new HashMap<String, String>();
	    properties.put("university", university);
	    properties.put("phone", phone);
	    properties.put("email", email);
	    properties.put("student_id", student_id);

	    /*
	    properties.put("nickname", "피재식");
	    properties.put("university", "soongsil");
	    properties.put("phone", "01020544620");
	    properties.put("email", "maguire1815@gmail.com");
	    properties.put("student_id", "20092469");
	    */

	    UserManagement.requestUpdateProfile(new UpdateProfileResponseCallback() {
	        @Override
	        protected void onSuccess(final long userId) {
	            UserProfile.updateUserProfile(userProfile, properties);
	            if (userProfile != null)
	                userProfile.saveUserToCache();
                    //Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    //startActivity(i);
                    finish();
	            //showProfile();
	        }

	        @Override
	        protected void onSessionClosedFailure(final APIErrorResult errorResult) {
	            //redirectLoginActivity();
	        }

	        @Override
	        protected void onFailure(final APIErrorResult errorResult) {
	            Toast.makeText(getApplicationContext(), "failed to update profile. msg = " + errorResult, Toast.LENGTH_LONG).show();
	        }
	    }, properties);
	}
	

    /**
     * Background Async Task to Create new product
     * */
    class UpdateKaCamProfile extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NewKaCamProfileActivity.this);
            pDialog.setMessage("Creating KaCamProfile..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            String university = inputUniversity.getText().toString();
            String phone = inputPhone.getText().toString();
            String email = inputEmail.getText().toString();
            String student_id = inputStudent_Id.getText().toString();

            userProfile = UserProfile.loadFromCache();
            final Map<String, String> properties = new HashMap<String, String>();
            //properties.put("nickname", "피재식");
            properties.put("university", university);
            properties.put("phone", phone);
            properties.put("email", email);
            properties.put("student_id", student_id);

            UserManagement.requestUpdateProfile(new UpdateProfileResponseCallback() {
            	@Override
            	protected void onSuccess(final long userId) {
            		UserProfile.updateUserProfile(userProfile, properties);
            		if (userProfile != null)
            			userProfile.saveUserToCache();
            		//showProfile();
            	}

            	@Override
            	protected void onSessionClosedFailure(final APIErrorResult errorResult) {
            		//redirectLoginActivity();
            	}

            	@Override
            	protected void onFailure(final APIErrorResult errorResult) {
            		Toast.makeText(getApplicationContext(), "failed to update profile. msg = " + errorResult, Toast.LENGTH_LONG).show();
            	}
            }, properties);

            /*
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", university));
            params.add(new BasicNameValuePair("price", phone));
            params.add(new BasicNameValuePair("description", email));
            params.add(new BasicNameValuePair("description", student_id));
             */

            // getting JSON Object
            // Note that create product url accepts POST method
            // Sending POST part
            /*
            JSONObject json = jsonParser.makeHttpRequest(url_create_product, "POST", params);
 
            // check log cat fro response
            Log.d("Create Response", json.toString());
 
            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // successfully created product
                    Intent i = new Intent(getApplicationContext(), AllKaCamProfilesActivity.class);
                    startActivity(i);
 
                    // closing this screen
                    finish();
                } else {
                    // failed to create product
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            */
 
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }
 
    }
}




/*
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class NewKaCamProfileActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_ka_cam_profile);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_ka_cam_profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
*/