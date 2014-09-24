package com.kakao.kakaocampus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
 
public class AllDataActivity extends ListActivity {
 
    // Progress Dialog
    private ProgressDialog pDialog;
 
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
 
    ArrayList<HashMap<String, String>> dataList;
 
    // url to get all data list
    //private static String url_all_data = "http://api.androidhive.info/android_connect/get_all_data.php";
    // Because of the Error
    //private static String url_all_data = "http://localhost/kakaocampusremotedatabase/get_all_data.php";
    //private static String url_all_data = "http://10.0.2.2/kakaocampusremotedatabase/get_all_data.php";
    private static String url_all_data = "http://kacamdb.bugs3.com/get_all_data.php";
 
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "data";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_USERCONTENTS = "usercontents";
 
    // data JSONArray
    JSONArray data = null;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_data);	//all_data -> activity_all_data로 변경 
        //setContentView(R.layout.activity_all_data);
 
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
 
    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllData extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AllDataActivity.this);
            pDialog.setMessage("Loading data. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        /**
         * getting All data from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL

            Log.i("AllDataActivity", "Before send json");
            JSONObject json = jParser.makeHttpRequest(url_all_data, "POST", params);
            //JSONObject json = jParser.makeHttpRequest(url_all_data, "GET", params);
            Log.i("AllDataActivity", "after send json");
 
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
                	
                	Toast.makeText(getApplicationContext(), "no data", Toast.LENGTH_LONG).show();
                	
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
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all data
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            AllDataActivity.this, dataList,
                            R.layout.list_item, new String[] { TAG_USERNAME,
                                    TAG_USERCONTENTS},
                            new int[] { R.id.username, R.id.usercontents });
                    // updating listview
                    setListAdapter(adapter);
                }
            });
 
        }
 
    }
}


/*
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class AllDataActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.all_data, menu);
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