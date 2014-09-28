package com.kakao.kakaocampus;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
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

	EditText inputUniversity;
	EditText inputPhone;
	EditText inputEmail;
	EditText inputStudent_Id;
	private UserProfile userProfile;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_ka_cam_profile);

		// Get Edit Text Info
		inputUniversity = (EditText) findViewById(R.id.inputUniversity);
		inputPhone = (EditText) findViewById(R.id.inputPhone);
		inputEmail = (EditText) findViewById(R.id.inputEmail);
		inputStudent_Id = (EditText) findViewById(R.id.inputStudent_Id);

		// Update button
		Button btnUpdateKaCamProfile = (Button) findViewById(R.id.updateKaCamProfile);

		// button click event
		btnUpdateKaCamProfile.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
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

		UserManagement.requestUpdateProfile(new UpdateProfileResponseCallback() {
			@Override
			// Access Success!
			protected void onSuccess(final long userId) {
				UserProfile.updateUserProfile(userProfile, properties);
				Toast.makeText(getApplicationContext(), "Update Success, We are going to check your ID", Toast.LENGTH_LONG).show();
				if (userProfile != null)
					userProfile.saveUserToCache();
				finish();
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

}
