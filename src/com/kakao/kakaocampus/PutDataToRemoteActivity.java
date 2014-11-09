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

	private static String url_save_kakaostory_data = "http://kacamdb.bugs3.com/input_data.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	// Progress Dialog
	private ProgressDialog pDialog;

	private String jsonMyStoriesInfo;

	private String usernameIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_put_data_to_remote);

		Intent intent = getIntent();
		usernameIntent = intent.getStringExtra("username");
		// TODO User Profile에 있는 정보 및 사진은 추후 업데이트 예정
		Log.i("PutDataActivity", "Start!!!!!!!!!!!");

		init();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (pDialog != null) {
			pDialog.dismiss();
			pDialog = null;
		}
	}

	public void init() {
		
		// 카카오스토리에 올릴 메세지를 입력 (noteContent)
		final NoteKakaoStoryPostParamBuilder postParamBuilder = new NoteKakaoStoryPostParamBuilder(noteContent);
		
		// Last MyStoryID를 가져오기 위해 카카오캠퍼스에서 카카오스토리로 글을 하나 올림
		requestPost(StoryType.NOTE, postParamBuilder);
		
		// 카카오스토리에 있는 글을 Remote Database에 올림
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

	private void requestPost(final StoryType storyType,
			final BasicKakaoStoryPostParamBuilder postParamBuilder) {
		// 앱이 설치되어 있는 경우 kakao<app_key>://kakaostory?place=1111 로 이동.
		// 앱이 설치되어 있지 않은 경우
		// market://details?id=com.kakao.sample.kakaostory&referrer=kakaostory로
		// 이동
		postParamBuilder.setAndroidExecuteParam(execParam)
				.setIOSExecuteParam(execParam)
				.setAndroidMarketParam(marketParam)
				.setIOSMarketParam(marketParam);
		try {
			final Bundle parameters = postParamBuilder.build();
			KakaoStoryService.requestPost(storyType,
					new MyKakaoStoryHttpResponseHandler<MyStoryInfo>() {
						@Override
						protected void onHttpSuccess(
								final MyStoryInfo myStoryInfo) {
							if (myStoryInfo.getId() != null) {
								// 가장 최근(=현재 카카오스토리에 올린 글) story id를 가져와서 lastMyStoryId에 입력
								lastMyStoryId = myStoryInfo.getId();
								Toast.makeText(
										getApplicationContext(),
										"succeeded to post " + storyType
												+ " on KakaoStory.\nmyStoryId="
												+ lastMyStoryId,
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(
										getApplicationContext(),
										"failed to post "
												+ storyType
												+ " on KakaoStory.\nmyStoryId=null",
										Toast.LENGTH_SHORT).show();
							}
						}
					}, parameters);
		} catch (KakaoParameterException e) {
			Toast.makeText(getApplicationContext(), e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}

	private void requestGetMyStories() {
		final Bundle parameters = new KakaoStoryMyStoriesParamBuilder(lastMyStoryId).build();
		KakaoStoryService.requestGetMyStories( new MyKakaoStoryHttpResponseHandler<MyStoryInfo[]>() {

					@Override // 성공적으로 나의 스토리 Content를 가져왔을 때, myStories 변수가 18개의 최근 글을 갖고 있다.
					protected void onHttpSuccess(final MyStoryInfo[] myStories) {
						// myStories는 배열이다. 배열에 있는 데이터를 가져와서 원격 데이터베이스에 저장하자.

						/* { "1" : { "username" : "jaesikTest", "usercontents" : "test}, "2" : { "username" : "lsetjlsk", "usercontents" : "test"} } */
						JSONObject joOut = new JSONObject();
						Integer jsonNum = 1;
						
						// To categorize kind
						String boardKind = null;

						for (MyStoryInfo myStoryTem : myStories) {
							if (myStoryTem.getContent() != null) {
								// #카카오캠퍼스 태그 구분
								if (myStoryTem.getContent().contains("#카카오캠퍼스")) {
									JSONObject joIn = new JSONObject();

									// #학교, #학부, #맛집, #대나무숲, #무럭무럭, #놀자, #꽃보다여행, #대학지침서, #나두근두근해, #너네이거알아?, #나요즘힘들어, #나이거싫어
									// 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13
									if (myStoryTem.getContent().contains("#학교")) { boardKind = "1"; }
									if (myStoryTem.getContent().contains("#학부")) { boardKind = "2"; }
									if (myStoryTem.getContent().contains("#맛집")) { boardKind = "3"; }
									if (myStoryTem.getContent().contains( "#대나무숲")) { boardKind = "4"; }
									if (myStoryTem.getContent().contains( "#무럭무럭")) { boardKind = "5"; }
									if (myStoryTem.getContent().contains("#놀자")) { boardKind = "6"; }
									if (myStoryTem.getContent().contains( "#꽅보다여행")) { boardKind = "7"; }
									if (myStoryTem.getContent().contains( "#대학지침서")) { boardKind = "8"; }
									if (myStoryTem.getContent().contains( "#나두근두근해")) { boardKind = "9"; }
									if (myStoryTem.getContent().contains( "#너네이거알아?")) { boardKind = "10"; }
									if (myStoryTem.getContent().contains( "#나요즘힘들어")) { boardKind = "11"; }
									if (myStoryTem.getContent().contains( "#나이거싫어")) { boardKind = "12"; }
									try {
										// Id 및 내용 입력 파트
										joIn.put("usercontents", myStoryTem.getContent());
										joIn.put("kind", boardKind);
										joIn.put("kslink", myStoryTem.getUrl());

										if (boardKind == "4") {
											joIn.put("username", "secret");
										} else {
											joIn.put("username", usernameIntent);
										}
										joOut.put(jsonNum.toString(), joIn);
									} catch (JSONException e) {
										e.printStackTrace();
									}
									boardKind = null;
								}
							}
							jsonNum++;
						}
						// jsonMyStoriesInfo에 json type data 저장
						jsonMyStoriesInfo = joOut.toString();
						// 원격 데이터베이스에 저장하기 위해서 HTTP POST를 사용할 수 있게 Async를 호출하는 method를 호출
						jsonFunction();
					}
				}, parameters);
	}

	// 데이터를 JSON을 사용해서 입력하는 함수 만들기
	public void jsonFunction() {
		new CreateNewProduct().execute();
	}

	/**
	 * Background Async Task to Create new product
	 * */
	class CreateNewProduct extends AsyncTask<String, String, String> {

		/*** Before starting background thread Show Progress Dialog **/
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.i("PutDataActivity", "on Pre Execute in CreateNewProduct class");
			pDialog = new ProgressDialog(PutDataToRemoteActivity.this);
			pDialog.setMessage("Creating Product..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			Log.i("PutDataActivity", "do In Background in CreateNewProduct class");

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			Log.i("PutDataActivity", jsonMyStoriesInfo);

			params.add(new BasicNameValuePair("jsonMyStoriesInfo", jsonMyStoriesInfo));

			JSONObject json = jsonParser.makeHttpRequest(url_save_kakaostory_data, "POST", params);

			// check log cat fro response
			Log.d("Create Response", json.toString());

			// check for success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					/*
					 * Intent i = new Intent(getApplicationContext(),
					 * AllProductsActivity.class); startActivity(i);
					 */
					Log.i("PutDataActivity", "on Success in CreateNewProduct class");
				} else {
					// failed to send data
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
			Log.i("PutDataActivity", "on Post Execute in CreateNewProduct class");
			// dismiss the dialog once done
			pDialog.dismiss();
			// TabmenuActivity로 이동
			Intent TabMenu = new Intent(getApplicationContext(), TabmenuActivity.class);
			startActivity(TabMenu);
			finish();
		}
	}

	private abstract class MyKakaoStoryHttpResponseHandler<T> extends
			KakaoStoryHttpResponseHandler<T> {

		@Override
		protected void onHttpSessionClosedFailure(
				final APIErrorResult errorResult) {
			redirectLoginActivity();
		}

		@Override
		protected void onNotKakaoStoryUser() {
			Toast.makeText(getApplicationContext(), "not KakaoStory user",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onFailure(final APIErrorResult errorResult) {
			final String message = "MyKakaoStoryHttpResponseHandler : failure : "
					+ errorResult;
			Logger.getInstance().d(message);
			Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG)
					.show();
		}
	}

	private void redirectLoginActivity() {
		final Intent intent = new Intent(this, KakaoStoryLoginActivity.class);
		startActivity(intent);
		finish();
	}
}
