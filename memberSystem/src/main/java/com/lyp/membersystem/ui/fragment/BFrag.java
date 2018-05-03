package com.lyp.membersystem.ui.fragment;

import java.util.ArrayList;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.lyp.membersystem.R;
import com.lyp.membersystem.adapter.MallFragmentAdapter;
import com.lyp.membersystem.bean.ProductTypeBean;
import com.lyp.membersystem.ui.GoodsActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class BFrag extends Fragment implements OnRefreshListener2<GridView> {

	private View view;
	private PullToRefreshGridView mPullRefreshGridView;
	private GridView mGridView;
	private ArrayList<ProductTypeBean> productTypes;
	private MallFragmentAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getArguments();
		productTypes = (ArrayList<ProductTypeBean>) bundle.get("bfrag");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment, container, false);
		initView();
		return view;
	}

	private void initView() {
		mPullRefreshGridView = (PullToRefreshGridView) view.findViewById(R.id.pull_refresh_grid);
		mPullRefreshGridView.setMode(Mode.DISABLED);
		mPullRefreshGridView.setOnRefreshListener(this);
		mGridView = mPullRefreshGridView.getRefreshableView();
		mGridView.setSelector(android.R.color.transparent);
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent();
				ProductTypeBean productType = productTypes.get(position);
				intent.putExtra("id", productType.getId());
				intent.putExtra("pId", productType.getpId());
				intent.setClass(getActivity(), GoodsActivity.class);
				startActivity(intent);
			}
		});
		adapter = new MallFragmentAdapter(productTypes, getActivity().getApplicationContext());
		mGridView.setAdapter(adapter);
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
	public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {

	}
}
