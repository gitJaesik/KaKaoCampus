package com.kakao.kakaocampus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.APIErrorResult;
import com.kakao.LogoutResponseCallback;
import com.kakao.UserManagement;
 
public class GetSelectedDataActivity extends ListActivity {
 
    // Progress Dialog
    private ProgressDialog pDialog;
 
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
 
    ArrayList<HashMap<String, String>> dataList;
 
    //private static String url_all_data = "http://kacamdb.bugs3.com/get_all_data.php";
    private static String url_all_data = "http://kacamdb.bugs3.com/get_selected_data.php";
 
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "data";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_USERCONTENTS = "usercontents";
 
    // data JSONArray
    JSONArray data = null;
    
    String kind;
    String titleName;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_selected_data);

		Intent intent = getIntent();
		kind = intent.getStringExtra("kind");
		titleName = intent.getStringExtra("titleName");
		
		TextView titleSelected = (TextView)findViewById(R.id.textViewTitle1);
		titleSelected.setText(titleName);
		
 
        // Hashmap for ListView
        dataList = new ArrayList<HashMap<String, String>>();
 
        // Loading data in Background Thread
        new LoadAllData().execute();
 
        // Get listview
        ListView lv = getListView();
 
        // on seleting single product
        // launching Edit Product Screen
        
        //jaesik modify
        /* 
        lv.setOnItemClickListener(new OnItemClickListener() {
 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // getting values from selected ListItem
                String username = ((TextView) view.findViewById(R.id.username)).getText()
                        .toString();
 
                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        EditProductActivity.class);
                // sending username to next activity
                in.putExtra(TAG_USERNAME, username);
 
                // starting new activity and expecting some response back
                startActivityForResult(in, 100);
            }
        });
        */
 
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


    // Response from Edit Product Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted product
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
 
    }

    class LoadAllData extends AsyncTask<String, String, String> {
 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(GetSelectedDataActivity.this);
            pDialog.setMessage("Loading data. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL

            // POST KIND
			params.add(new BasicNameValuePair("kind", kind));

            JSONObject json = jParser.makeHttpRequest(url_all_data, "POST", params);
            //JSONObject json = jParser.makeHttpRequest(url_all_data, "GET", params);
 
            // Check your log cat for JSON reponse
            Log.d("All Data: ", json.toString());
 
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // data found
                    // Getting Array of Data
                    data = json.getJSONArray(TAG_PRODUCTS);
 
                    // looping through All Data
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject c = data.getJSONObject(i);
 
                        // Storing each json item in variable
                        String id = c.getString(TAG_USERNAME);
                        String usercontents = c.getString(TAG_USERCONTENTS);
 
                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                        map.put(TAG_USERNAME, id);
                        map.put(TAG_USERCONTENTS, usercontents);
 
                        // adding HashList to ArrayList
                        dataList.add(map);
                    }
                } else {
                	
                	// jaeisk modify
                	/*
                    // no data found
                    // Launch Add New product Activity
                    Intent i = new Intent(getApplicationContext(),
                            NewProductActivity.class);
                    // Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    */
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all data
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                	
                	//ListView listv = (ListView)findViewById(R.id.list);
                    //ListAdapter adapter = new SimpleAdapter( GetSelectedDataActivity.this, dataList, R.layout.list_item, new String[] { TAG_USERNAME, TAG_USERCONTENTS}, new int[] { R.id.username, R.id.usercontents });
                    ListAdapter adapter = new SimpleAdapter(GetSelectedDataActivity.this, dataList, R.layout.listview1_item,
                    		new String[] { TAG_USERNAME, TAG_USERCONTENTS}, new int[] { R.id.textView100, R.id.textView200 });
                    // updating listview
                    setListAdapter(adapter);

                	Toast.makeText(getApplicationContext(), "kind is : " + kind, Toast.LENGTH_LONG).show();
                }
            });
 
        }
 
    }
}