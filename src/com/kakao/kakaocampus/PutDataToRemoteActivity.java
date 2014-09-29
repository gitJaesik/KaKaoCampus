package com.kakao.kakaocampus;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.kakao.APIErrorResult;
import com.kakao.BasicKakaoStoryPostParamBuilder;
import com.kakao.KakaoParameterException;
import com.kakao.KakaoStoryHttpResponseHandler;
import com.kakao.KakaoStoryMyStoriesParamBuilder;
import com.kakao.KakaoStoryService;
import com.kakao.KakaoStoryService.StoryType;
import com.kakao.MyStoryInfo;
import com.kakao.NoteKakaoStoryPostParamBuilder;
import com.kakao.helper.Logger;

public class PutDataToRemoteActivity extends ActionBarActivity {
    private final String execParam = "place=1111";
    private final String noteContent = "Enjoy Your Campus Life\n" 
    									+ "in KAKAO CAMPUS APP!!!! #카카오캠퍼스";
    private final String marketParam = "referrer=kakaostory";
    private String lastMyStoryId;
    
    // JSONParser
    JSONParser jsonParser = new JSONParser();
    // url to create new product
    private static String url_create_product = "http://api.androidhive.info/android_connect/create_product.php";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    // Progress Dialog
    private ProgressDialog pDialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_put_data_to_remote);
		
		Log.i("MainActivity", "Start!!!!!!!!!!!");
		init();
	}
	
	public void init(){
		final NoteKakaoStoryPostParamBuilder postParamBuilder = new NoteKakaoStoryPostParamBuilder(noteContent);
		requestPost(StoryType.NOTE, postParamBuilder);
		requestGetMyStories();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.put_data_to_remote, menu);
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

    private void requestGetMyStories() {
        final Bundle parameters = new KakaoStoryMyStoriesParamBuilder(lastMyStoryId).build();
        KakaoStoryService.requestGetMyStories(new MyKakaoStoryHttpResponseHandler<MyStoryInfo[]>() {
            @Override
            protected void onHttpSuccess(final MyStoryInfo[] myStories) {
            	/*
                Toast.makeText(getApplicationContext(), "succeeded to get my posts from KakaoStory." +
                    "\ncount=" + myStories.length +
                    "\nstories=" + Arrays.toString(myStories), Toast.LENGTH_SHORT).show();
                    */
                    // myStories는 배열이다. 배열에 있는 데이터를 가져와서 원격 데이터베이스에 저장하자.
            	for(MyStoryInfo myStoryTem : myStories){
            		//myStoryTem.getContent();
	        		Log.i("MainActivity", "Contents   ==>   " + myStoryTem.getContent());
            	}
            	
            	//TODO User Profile에 있는 정보 및 사진은 추후 업데이트 예정 

            }
        }, parameters);
    }


    // 데이터를 JSON을 사용해서 입력하는 함수 만들기 
    public void jsonFunction(){
        new CreateNewProduct().execute();
    }

    /**
     * Background Async Task to Create new product
     * */
    class CreateNewProduct extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PutDataToRemoteActivity.this);
            pDialog.setMessage("Creating Product..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
        	/*
            String name = inputName.getText().toString();
            String price = inputPrice.getText().toString();
            String description = inputDesc.getText().toString();
            */
 
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            /*
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("price", price));
            params.add(new BasicNameValuePair("description", description));
            */

            params.add(new BasicNameValuePair("username", "JaesikTest"));
            params.add(new BasicNameValuePair("usercontents", "contents test"));
            //params.add(new BasicNameValuePair("description", description));
 
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_product,
                    "POST", params);
 
            // check log cat fro response
            Log.d("Create Response", json.toString());
 
            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // successfully created product
                	/*
                    Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);
                    startActivity(i);
                    */
 
                	Log.i("putting data", "putting success");
                    // closing this screen
                    //finish();
                } else {
                    // failed to create product
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
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

	private void redirectLoginActivity() {
		final Intent intent = new Intent(this, KakaoStoryLoginActivity.class);
		startActivity(intent);
		finish();
	}
}
