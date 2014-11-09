package com.kakao.kakaocampus;

import java.util.Arrays;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.kakao.APIErrorResult;
import com.kakao.KakaoStoryHttpResponseHandler;
import com.kakao.KakaoStoryMyStoriesParamBuilder;
import com.kakao.KakaoStoryService;
import com.kakao.LogoutResponseCallback;
import com.kakao.MeResponseCallback;
import com.kakao.MyStoryInfo;
import com.kakao.UserManagement;
import com.kakao.UserProfile;
import com.kakao.helper.Logger;

public class MainActivity extends Activity {
    private String lastMyStoryId;
    private String jaesikStoryId = "_H5Pf5.IAeVGI06qW0";
    private final String execParam = "place=1111";
    private final String noteContent = "Enjoy Your Campus Life\n" 
    									+ "in KAKAO CAMPUS APP!!!! #카카오캠퍼스";
    private final String marketParam = "referrer=kakaostory";
    private UserProfile userProfile;
    private String university;
    private String phone;
    private String email;
    private String student_id;
    private String null_test;
    private int alertCheck;
    private ProgressDialog pDialog;
    private String userProfileNickName;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Call user information from kakao;
		//In this methods, it'll call other activity if there are no university info
		callRequestMe();

		initView();

	}
	


	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	

	// initiate view code ( button and alertDialog ) 
	public void initView(){

		initButtonView();

        // 포스팅 동의 파트
        openAlert();
        //checkAlertAgree();
	}

	public void initButtonView(){
		
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
            	startActivity(intent);
            }
        });

	}


	private void callRequestMe() {
		
		// 사용자관리에 있는 데이터를 요청
	    UserManagement.requestMe(new MeResponseCallback() {
	        @Override
	        protected void onSuccess(final UserProfile userProfile) {
	        	Map<String, String> kacamProperties = userProfile.getProperties();
	        	userProfileNickName = userProfile.getNickname();

	        	// 대학교, 전화번호, 이메일, 학번이 입력되어있으면 로그인
	        	if(kacamProperties.get("university") != null && kacamProperties.get("phone") != null 
	        			&& kacamProperties.get("email") != null && kacamProperties.get("student_id") != null){
	        		Log.i("MainActivity", "사용자관리에 대학정보 입력 되어있음을 확인");
	        	}else{
	        	// 사용자관리가 입력되어있지 않으면 사용자 관리 작성 페이지로 이동
	        		Intent intent = new Intent(getApplicationContext(), NewKaCamProfileActivity.class);	
	        		startActivity(intent);
	        	}
	        }

	        @Override
	        protected void onNotSignedUp() {
	            // 가입 페이지로 이동
	            //redirectSignupActivity();
	        }

	        @Override
	        protected void onSessionClosedFailure(final APIErrorResult errorResult) {
	            // 다시 로그인 시도
	            redirectLoginActivity();
	        }

	        @Override
	        protected void onFailure(final APIErrorResult errorResult) {
	            // 실패
	            Toast.makeText(getApplicationContext(), "failed to update profile. msg = " + errorResult, Toast.LENGTH_LONG).show();
	        }
	    });
	}

	public void openAlert(){
	      AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	      alertDialogBuilder.setMessage(R.string.checkWannaShareText);
	      alertDialogBuilder.setPositiveButton(R.string.positiveShare, new DialogInterface.OnClickListener() {
			
	         @Override
	         public void onClick(DialogInterface arg0, int arg1) {
	        	 // 카카오스토리에 올려져 있는 게시글을 공유
	        	 Intent putDataToRemoteActivity1 = new Intent(getApplicationContext(), PutDataToRemoteActivity.class);
	        	 putDataToRemoteActivity1.putExtra("username", userProfileNickName);
	        	 startActivity(putDataToRemoteActivity1);
	        	 finish();
	         }
	      });

	      alertDialogBuilder.setNegativeButton(R.string.negativeShare, new DialogInterface.OnClickListener() {
				
	         @Override
	         public void onClick(DialogInterface dialog, int which) {
	        	 // 컨텐츠를 볼 수 있는 페이지로 이동
	        	 Intent tabmenugo = new Intent(getApplicationContext(), TabmenuActivity.class);
	        	 startActivity(tabmenugo);
	        	 finish();
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

		
	public boolean onOptionsItemSelected(MenuItem item){
		switch( item.getItemId()) {
		case R.id.action_settings:
			onClickLogout();	
		}
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
                    // myStories는 배열이다. 배열에 있는 데이터를 가져와서 원격 데이터베이스에 저장하자.
            }
        }, parameters);
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

}
