package com.kakao.kakaocampus;

import android.app.*;
import android.app.ActionBar.Tab;
import android.os.*;
import android.view.*;
import android.widget.*;

public class ActionTab extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actiontab);

		// ab가 액션바 (액션바는 Title의 확장이라고 생각하시면 되요) 
		ActionBar ab = getActionBar();
		// ab의 모드를 탭으로 바꿈 
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// action bar를 3개 만든다.
		ActionBar.Tab tab = ab.newTab();

		// 밑에 내용이 들어가는 부분입니다.
		String Cap = "Tabtest"; 
		String Aap = "12341234"; 

		// tab에 Tab1~3 을 입력.
		tab.setText(Cap);

		// 탭 Fragment를 생성.
		TabFragment frag = TabFragment.newInstance(Aap);

		// 탭 프레그먼트를 탭 리스너에 생성 
		tab.setTabListener(new TabListener(frag));

		// 탭을 action bar에 추가 
		ab.addTab(tab);

		if (savedInstanceState != null) {
			int seltab = savedInstanceState.getInt("seltab");
			ab.setSelectedNavigationItem(seltab);
		}

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

			// Hash 처럼 저장 
			Bundle args = new Bundle();
			
			// Tab1을 저장
			args.putString("text", text);
			
			// 새로운 fragment에 Tab1을 저장 
			frag.setArguments(args);

			return frag;
		}

		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			String text = "";
			
			// Tab 1을 불러옴 
			Bundle args = getArguments();
			if (args != null) {
				text = args.getString("text");
			}

			View linear = inflater.inflate(R.layout.actiontabfragment, container, false);
			TextView textview = (TextView)linear.findViewById(R.id.content);
			textview.setText(text);
			
			/*
			Button btn = (Button)linear.findViewById(R.id.btn_ok);
			btn.setOnClickListener(new );
			*/

			return linear;
		}
	}
}


