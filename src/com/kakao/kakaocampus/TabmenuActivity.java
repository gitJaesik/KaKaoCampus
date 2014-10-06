package com.kakao.kakaocampus;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.kakao.APIErrorResult;
import com.kakao.LogoutResponseCallback;
import com.kakao.UserManagement;

public class TabmenuActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tabmenu);

		// ab가 액션바 (액션바는 Title의 확장이라고 생각하시면 되요)
		ActionBar ab = getActionBar();
		// ab의 모드를 탭으로 바꿈
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		ActionBar.Tab tab1 = ab.newTab();
		ActionBar.Tab tab2 = ab.newTab();
		ActionBar.Tab tab3 = ab.newTab();
		ActionBar.Tab tab4 = ab.newTab();

		tab1.setText("내캠퍼스");
		tab2.setText("대학");
		tab3.setText("이것저것");
		tab4.setText("청춘수다");

		TabFragment frag1 = TabFragment.newInstance("test");
		TabFragment2 frag2 = TabFragment2.newInstance("test2");
		TabFragment3 frag3 = TabFragment3.newInstance("test3");
		TabFragment4 frag4 = TabFragment4.newInstance("test4");

		tab1.setTabListener(new TabListener(frag1));
		tab2.setTabListener(new TabListener(frag2));
		tab3.setTabListener(new TabListener(frag3));
		tab4.setTabListener(new TabListener(frag4));

		ab.addTab(tab1);
		ab.addTab(tab2);
		ab.addTab(tab3);
		ab.addTab(tab4);

		if (savedInstanceState != null) {
			int seltab = savedInstanceState.getInt("seltab");
			ab.setSelectedNavigationItem(seltab);
		}

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

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("seltab", getActionBar().getSelectedNavigationIndex());
	}

	private class TabListener implements ActionBar.TabListener {
		private Fragment mFragment;

		public TabListener(Fragment fragment) {
			mFragment = fragment;
		}

		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			ft.add(R.id.tabparent, mFragment, "tag");
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			ft.remove(mFragment);
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}
	}

	public static class TabFragment extends Fragment {
		public static TabFragment newInstance(String text) {
			TabFragment frag = new TabFragment();
			return frag;
		}

		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View linear = inflater.inflate(R.layout.activity_my_campus,
					container, false);

			Button mycampus_button_setting = (Button) linear
					.findViewById(R.id.mycampus_button_setting);
			mycampus_button_setting.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					FragmentManager fm = getFragmentManager();
					FragmentTransaction tr = fm.beginTransaction();
					TabFragment5 campusSetting = TabFragment5
							.newInstance("test");
					tr.addToBackStack(null);
					tr.replace(R.id.tabparent, campusSetting);
					tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
					tr.commit();
				}

			});

			return linear;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			this.getClass();
			super.onActivityCreated(savedInstanceState);
		}
	}

	public static class TabFragment2 extends Fragment {
		public static TabFragment2 newInstance(String text) {
			TabFragment2 frag = new TabFragment2();
			return frag;
		}

		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View linear = inflater.inflate(R.layout.activity_university,
					container, false);

			Button university_univ = (Button) linear
					.findViewById(R.id.university_univ);
			university_univ.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					FragmentManager fm = getFragmentManager();
					FragmentTransaction tr = fm.beginTransaction();
					TabFragment6 universityUniv = TabFragment6
							.newInstance("test");
					tr.replace(R.id.tabparent, universityUniv);
					tr.addToBackStack(null);
					tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
					tr.commit();
				}

			});
			Button university_department = (Button) linear
					.findViewById(R.id.university_department);
			university_department.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					FragmentManager fm = getFragmentManager();
					FragmentTransaction tr = fm.beginTransaction();
					TabFragment7 universityDepartment = TabFragment7
							.newInstance("test");
					tr.replace(R.id.tabparent, universityDepartment);
					tr.addToBackStack(null);
					tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
					tr.commit();
				}

			});
			Button university_gathering = (Button) linear
					.findViewById(R.id.university_gathering);
			university_gathering.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					FragmentManager fm = getFragmentManager();
					FragmentTransaction tr = fm.beginTransaction();
					TabFragment8 universityGathering = TabFragment8
							.newInstance("test");
					tr.replace(R.id.tabparent, universityGathering);
					tr.addToBackStack(null);
					tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
					tr.commit();
				}

			});
			Button university_food = (Button) linear
					.findViewById(R.id.university_food);
			university_food.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					FragmentManager fm = getFragmentManager();
					FragmentTransaction tr = fm.beginTransaction();
					TabFragment9 universityFood = TabFragment9
							.newInstance("test");
					tr.replace(R.id.tabparent, universityFood);
					tr.addToBackStack(null);
					tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
					tr.commit();
				}

			});
			Button university_forest = (Button) linear
					.findViewById(R.id.university_forest);
			university_forest.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					FragmentManager fm = getFragmentManager();
					FragmentTransaction tr = fm.beginTransaction();
					TabFragment10 universityForest = TabFragment10
							.newInstance("test");
					tr.replace(R.id.tabparent, universityForest);
					tr.addToBackStack(null);
					tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
					tr.commit();
				}

			});
			return linear;
		}
	}

	public static class TabFragment3 extends Fragment {
		public static TabFragment3 newInstance(String text) {
			TabFragment3 frag = new TabFragment3();

			return frag;
		}

		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View linear = inflater.inflate(R.layout.activity_etc, container,
					false);
			
			Button etc_growing = (Button) linear
					.findViewById(R.id.etc_growing);
			etc_growing.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					FragmentManager fm = getFragmentManager();
					FragmentTransaction tr = fm.beginTransaction();
					TabFragment11 etcGrowing = TabFragment11
							.newInstance("test");
					tr.replace(R.id.tabparent, etcGrowing);
					tr.addToBackStack(null);
					tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
					tr.commit();
				}

			});
			
			Button etc_playing = (Button) linear
					.findViewById(R.id.etc_playing);
			etc_playing.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					FragmentManager fm = getFragmentManager();
					FragmentTransaction tr = fm.beginTransaction();
					TabFragment12 etcPlaying = TabFragment12
							.newInstance("test");
					tr.replace(R.id.tabparent, etcPlaying);
					tr.addToBackStack(null);
					tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
					tr.commit();
				}

			});
			
			Button etc_travel = (Button) linear
					.findViewById(R.id.etc_travel);
			etc_travel.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					FragmentManager fm = getFragmentManager();
					FragmentTransaction tr = fm.beginTransaction();
					TabFragment13 etcTravel = TabFragment13
							.newInstance("test");
					tr.replace(R.id.tabparent, etcTravel);
					tr.addToBackStack(null);
					tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
					tr.commit();
				}

			});
			
			Button etc_univtip = (Button) linear
					.findViewById(R.id.etc_univtip);
			etc_univtip.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					FragmentManager fm = getFragmentManager();
					FragmentTransaction tr = fm.beginTransaction();
					TabFragment14 etcUnivTip = TabFragment14
							.newInstance("test");
					tr.replace(R.id.tabparent, etcUnivTip);
					tr.addToBackStack(null);
					tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
					tr.commit();
				}

			});
			
			
			return linear;
		}
	}

	public static class TabFragment4 extends Fragment {
		public static TabFragment4 newInstance(String text) {
			TabFragment4 frag = new TabFragment4();
			return frag;
		}

		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			Bundle b = getActivity().getIntent().getExtras();

			View linear = inflater.inflate(R.layout.activity_spring_time,
					container, false);
			
			Button springtime_beat = (Button) linear
					.findViewById(R.id.springtime_beat);
			springtime_beat.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					FragmentManager fm = getFragmentManager();
					FragmentTransaction tr = fm.beginTransaction();
					TabFragment15 springtimeBeat = TabFragment15
							.newInstance("test");
					tr.replace(R.id.tabparent, springtimeBeat);
					tr.addToBackStack(null);
					tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
					tr.commit();
				}

			});
			
			Button springtime_tipshare = (Button) linear
					.findViewById(R.id.springtime_tipshare);
			springtime_tipshare.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					FragmentManager fm = getFragmentManager();
					FragmentTransaction tr = fm.beginTransaction();
					TabFragment16 springtimeTipshare = TabFragment16
							.newInstance("test");
					tr.replace(R.id.tabparent, springtimeTipshare);
					tr.addToBackStack(null);
					tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
					tr.commit();
				}

			});
			
			Button springtime_worried = (Button) linear
					.findViewById(R.id.springtime_worried);
			springtime_worried.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					FragmentManager fm = getFragmentManager();
					FragmentTransaction tr = fm.beginTransaction();
					TabFragment17 springtimeWorried = TabFragment17
							.newInstance("test");
					tr.replace(R.id.tabparent, springtimeWorried);
					tr.addToBackStack(null);
					tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
					tr.commit();
				}

			});
			
			Button springtime_hate = (Button) linear
					.findViewById(R.id.springtime_hate);
			springtime_hate.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					FragmentManager fm = getFragmentManager();
					FragmentTransaction tr = fm.beginTransaction();
					TabFragment18 springtimeHate = TabFragment18
							.newInstance("test");
					tr.replace(R.id.tabparent, springtimeHate);
					tr.addToBackStack(null);
					tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
					tr.commit();
				}

			});

			return linear;
		}
	}

	public static class TabFragment5 extends Fragment {
		public static TabFragment5 newInstance(String text) {
			TabFragment5 frag = new TabFragment5();
			return frag;
		}

		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View linear = inflater.inflate(R.layout.activity_my_campus_setting,
					container, false);

			return linear;
		}
	}
	
	public static class TabFragment6 extends Fragment {
		public static TabFragment6 newInstance(String text) {
			TabFragment6 frag = new TabFragment6();
			return frag;
		}

		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View linear = inflater.inflate(R.layout.activity_university_univ,
					container, false);

			return linear;
		}
	}
	
	public static class TabFragment7 extends Fragment {
		public static TabFragment7 newInstance(String text) {
			TabFragment7 frag = new TabFragment7();
			return frag;
		}

		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View linear = inflater.inflate(R.layout.activity_university_department,
					container, false);

			return linear;
		}
	}
	
	public static class TabFragment8 extends Fragment {
		public static TabFragment8 newInstance(String text) {
			TabFragment8 frag = new TabFragment8();
			return frag;
		}

		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View linear = inflater.inflate(R.layout.activity_university_gathering,
					container, false);

			return linear;
		}
	}
	
	public static class TabFragment9 extends Fragment {
		public static TabFragment9 newInstance(String text) {
			TabFragment9 frag = new TabFragment9();
			return frag;
		}

		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View linear = inflater.inflate(R.layout.activity_university_food,
					container, false);

			return linear;
		}
	}
	
	public static class TabFragment10 extends Fragment {
		public static TabFragment10 newInstance(String text) {
			TabFragment10 frag = new TabFragment10();
			return frag;
		}

		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View linear = inflater.inflate(R.layout.activity_university_forest,
					container, false);

			return linear;
		}
	}
	
	public static class TabFragment11 extends Fragment {
		public static TabFragment11 newInstance(String text) {
			TabFragment11 frag = new TabFragment11();
			return frag;
		}

		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View linear = inflater.inflate(R.layout.activity_etc_growing,
					container, false);

			return linear;
		}
	}
	
	public static class TabFragment12 extends Fragment {
		public static TabFragment12 newInstance(String text) {
			TabFragment12 frag = new TabFragment12();
			return frag;
		}

		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View linear = inflater.inflate(R.layout.activity_etc_playing,
					container, false);

			return linear;
		}
	}
	
	public static class TabFragment13 extends Fragment {
		public static TabFragment13 newInstance(String text) {
			TabFragment13 frag = new TabFragment13();
			return frag;
		}

		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View linear = inflater.inflate(R.layout.activity_etc_travel,
					container, false);

			return linear;
		}
	}
	
	public static class TabFragment14 extends Fragment {
		public static TabFragment14 newInstance(String text) {
			TabFragment14 frag = new TabFragment14();
			return frag;
		}

		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View linear = inflater.inflate(R.layout.activity_etc_univtip,
					container, false);

			return linear;
		}
	}
	
	public static class TabFragment15 extends Fragment {
		public static TabFragment15 newInstance(String text) {
			TabFragment15 frag = new TabFragment15();
			return frag;
		}

		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View linear = inflater.inflate(R.layout.activity_spring_time_beat,
					container, false);

			return linear;
		}
	}
	
	public static class TabFragment16 extends Fragment {
		public static TabFragment16 newInstance(String text) {
			TabFragment16 frag = new TabFragment16();
			return frag;
		}

		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View linear = inflater.inflate(R.layout.activity_spring_time_tipshare,
					container, false);

			return linear;
		}
	}
	
	public static class TabFragment17 extends Fragment {
		public static TabFragment17 newInstance(String text) {
			TabFragment17 frag = new TabFragment17();
			return frag;
		}

		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View linear = inflater.inflate(R.layout.activity_spring_time_worried,
					container, false);

			return linear;
		}
	}
	
	public static class TabFragment18 extends Fragment {
		public static TabFragment18 newInstance(String text) {
			TabFragment18 frag = new TabFragment18();
			return frag;
		}

		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View linear = inflater.inflate(R.layout.activity_spring_time_hate,
					container, false);


			return linear;
		}
	}
}
