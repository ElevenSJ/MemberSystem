package com.lyp.membersystem.ui.fragment;

import java.util.ArrayList;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lyp.membersystem.R;
import com.lyp.membersystem.adapter.CashCardFragAdapter;
import com.lyp.membersystem.bean.MemberCardBean;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class CashCardFrag extends Fragment implements OnRefreshListener2<ListView> {

	private View view;
	private PullToRefreshListView mPullRefreshListView;
	private ListView mListView;
	private ArrayList<MemberCardBean> cashCardList;
	private CashCardFragAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getArguments();
		cashCardList = (ArrayList<MemberCardBean>) bundle.get("cashcardfrage");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.member_card_fragment, container, false);
		initView();
		return view;
	}

	private void initView() {
		mPullRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_listview);
		mPullRefreshListView.setMode(Mode.DISABLED);
		mPullRefreshListView.setOnRefreshListener(this);
		mListView = mPullRefreshListView.getRefreshableView();
		mListView.setSelector(android.R.color.transparent);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
			}
		});
		adapter = new CashCardFragAdapter(cashCardList, getActivity().getApplicationContext());
		mListView.setAdapter(adapter);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		
	}

}
