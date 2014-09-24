package com.kakao.kakaocampus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.kakao.APIErrorResult;
import com.kakao.BasicKakaoStoryPostParamBuilder;
import com.kakao.KakaoParameterException;
import com.kakao.KakaoStoryHttpResponseHandler;
import com.kakao.KakaoStoryMyStoriesParamBuilder;
import com.kakao.KakaoStoryService;
import com.kakao.KakaoStoryService.StoryType;
import com.kakao.LogoutResponseCallback;
import com.kakao.MyStoryInfo;
import com.kakao.NoteKakaoStoryPostParamBuilder;
import com.kakao.UpdateProfileResponseCallback;
import com.kakao.UserManagement;
import com.kakao.UserProfile;
import com.kakao.helper.Logger;

public class MainActivity extends Activity {
    private String lastMyStoryId;
    //private String jaesikStoryId = "_H5Pf5.jB7s4CCoQW0";
    private String jaesikStoryId = "_H5Pf5.IAeVGI06qW0";
    private final String execParam = "place=1111";
    private final String noteContent = "Enjoy Your Campus Life\n" 
    									+ "in KAKAO CAMPUS APP!!!! #카카오캠퍼스";
    private final String marketParam = "referrer=kakaostory";
    private UserProfile userProfile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		requestUpdateProfile();
		
		initView();

	}
	
	// init view code ( button and alertDialog ) 
	public void initView(){
		
		Button logoutButton = (Button) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLogout();
            }
        });

        // TODO 최근 포스팅 정보는 필요하지 않다. 대신에 이 함수를 바탕으로 모아둔 포스팅 자료를 불러온다.
        // 최근 포스팅 정보.
        Button getPostsButton = (Button) findViewById(R.id.get_posts_button);
        getPostsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	lastMyStoryId = jaesikStoryId;
                if (lastMyStoryId == null) {
                    Toast.makeText(getApplicationContext(), "post first then get info", Toast.LENGTH_SHORT).show();
                } else {
                    requestGetMyStories();
                }
            }
        });

        // TODO access to the database to get all data
        // 최근 포스팅 정보.
        Button goToAllBoard = (Button) findViewById(R.id.gotoallboard);
        goToAllBoard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            	Intent intent = new Intent(getApplicationContext(), AllDataActivity.class);	
            	// I don't know why I can't use this
            	startActivity(intent);
            	finish();
            }
        });

        // 포스팅 동의 파트
        openAlert();
	}


	// Update UserProfile for input University, Phone, Email, and StudentID
	private void requestUpdateProfile() {
        userProfile = UserProfile.loadFromCache();
	    final Map<String, String> properties = new HashMap<String, String>();
	    properties.put("nickname", "피재식");
	    properties.put("university", "soongsil");
	    properties.put("phone", "01020544620");
	    properties.put("email", "maguire1815@gmail.com");
	    properties.put("student_id", "20092469");

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
	            redirectLoginActivity();
	        }

	        @Override
	        protected void onFailure(final APIErrorResult errorResult) {
	            Toast.makeText(getApplicationContext(), "failed to update profile. msg = " + errorResult, Toast.LENGTH_LONG).show();
	        }
	    }, properties);
	}



	public void openAlert(){

	      AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	      alertDialogBuilder.setMessage(R.string.checkWannaShareText);
	      alertDialogBuilder.setPositiveButton(R.string.positiveShare, 
	      new DialogInterface.OnClickListener() {
			
	         @Override
	         public void onClick(DialogInterface arg0, int arg1) {
	        	 /*
	            Intent positveActivity = new Intent(getApplicationContext(),com.example.alertdialog.PositiveActivity.class);
	            startActivity(positveActivity);
	            */
	        	 Toast.makeText(getApplicationContext(), "Positive", Toast.LENGTH_SHORT).show();
	        	 final NoteKakaoStoryPostParamBuilder postParamBuilder = new NoteKakaoStoryPostParamBuilder(noteContent);
	        	 requestPost(StoryType.NOTE, postParamBuilder);
	         }
	      });
	      alertDialogBuilder.setNegativeButton(R.string.negativeShare, 
	      new DialogInterface.OnClickListener() {
				
	         @Override
	         public void onClick(DialogInterface dialog, int which) {
	        	 /*
	            Intent negativeActivity = new Intent(getApplicationContext(),com.example.alertdialog.NegativeActivity.class);
	            startActivity(negativeActivity);
	            */
	        	 Toast.makeText(getApplicationContext(), "negative", Toast.LENGTH_SHORT).show();
			 }
	      });
		    
	      AlertDialog alertDialog = alertDialogBuilder.create();
	      alertDialog.show();
		    
   }

	   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
	      // Inflate the menu; this adds items to the action bar if it is present.
	      getMenuInflater().inflate(R.menu.main, menu);
	      return true;
   }

	private void onClickLogout() {
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            protected void onSuccess(final long userId) {
                redirectLoginActivity();
            }

            @Override
            protected void onFailure(final APIErrorResult apiErrorResult) {
                redirectLoginActivity();
            }
        });
    }

	private void redirectLoginActivity() {
		final Intent intent = new Intent(this, KakaoStoryLoginActivity.class);
		startActivity(intent);
		finish();
	}

    private void requestGetMyStories() {
        final Bundle parameters = new KakaoStoryMyStoriesParamBuilder(lastMyStoryId).build();
        KakaoStoryService.requestGetMyStories(new MyKakaoStoryHttpResponseHandler<MyStoryInfo[]>() {
            @Override
            protected void onHttpSuccess(final MyStoryInfo[] myStories) {
                Toast.makeText(getApplicationContext(), "succeeded to get my posts from KakaoStory." +
                    "\ncount=" + myStories.length +
                    "\nstories=" + Arrays.toString(myStories), Toast.LENGTH_SHORT).show();
                    //*/
            	/*
            	TextView setT = (TextView) findViewById(R.id.editText1);
            	setT.setText(Arrays.toString(myStories));
            	*/

            	//public static final int editText1=0x7f070018;


            }
        }, parameters);
    }

    private void requestPost(final StoryType storyType, final BasicKakaoStoryPostParamBuilder postParamBuilder) {
        // 앱이 설치되어 있는 경우 kakao<app_key>://kakaostory?place=1111 로 이동.
        // 앱이 설치되어 있지 않은 경우 market://details?id=com.kakao.sample.kakaostory&referrer=kakaostory로 이동
        postParamBuilder.setAndroidExecuteParam(execParam).setIOSExecuteParam(execParam).setAndroidMarketParam(marketParam).setIOSMarketParam(marketParam);
        try {
            final Bundle parameters = postParamBuilder.build();
            KakaoStoryService.requestPost(storyType, new MyKakaoStoryHttpResponseHandler<MyStoryInfo>() {
                @Override
                protected void onHttpSuccess(final MyStoryInfo myStoryInfo) {
                    if(myStoryInfo.getId() != null) {
                        lastMyStoryId = myStoryInfo.getId();
                        Toast.makeText(getApplicationContext(), "succeeded to post " + storyType + " on KakaoStory.\nmyStoryId=" + lastMyStoryId, Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(getApplicationContext(), "failed to post " + storyType + " on KakaoStory.\nmyStoryId=null", Toast.LENGTH_SHORT).show();
                    }
                }
            }, parameters);
        } catch (KakaoParameterException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private abstract class MyKakaoStoryHttpResponseHandler<T> extends KakaoStoryHttpResponseHandler<T> {

        @Override
        protected void onHttpSessionClosedFailure(final APIErrorResult errorResult) {
            redirectLoginActivity();
        }

        @Override
        protected void onNotKakaoStoryUser() {
            Toast.makeText(getApplicationContext(), "not KakaoStory user", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onFailure(final APIErrorResult errorResult) {
            final String message = "MyKakaoStoryHttpResponseHandler : failure : " + errorResult;
            Logger.getInstance().d(message);
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

	
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	*/
}
