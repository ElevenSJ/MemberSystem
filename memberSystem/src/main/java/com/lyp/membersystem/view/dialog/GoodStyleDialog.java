package com.lyp.membersystem.view.dialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lyp.membersystem.R;
import com.lyp.membersystem.bean.TagBean;
import com.lyp.membersystem.view.FlowRadioGroup;

import android.app.Dialog;
import android.content.Context;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

public class GoodStyleDialog {
	private Context context;
	private Dialog dialog;
	private TextView txt_title;
	private TextView txt_ok;
	private TextView txt_cancel;
	private LinearLayout lLayout_content;
	private ScrollView sLayout_content;
	private boolean showTitle = false;
	private Map<String, List<TagBean>> styleItemMap;
	private Map<String, FlowRadioGroup> styleItems;
	private Map<String, String> checkedList;
	private Display display;
	private OnButtonClickListener OKlistener;

	public GoodStyleDialog(Context context) {
		this.context = context;
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
	}

	public GoodStyleDialog builder() {
		// 获取Dialog布局
		View view = LayoutInflater.from(context).inflate(R.layout.good_style_diagle, null);

		// 设置Dialog最小宽度为屏幕宽度
		view.setMinimumWidth(display.getWidth());

		// 获取自定义Dialog布局中的控件
		sLayout_content = (ScrollView) view.findViewById(R.id.sLayout_content);
		lLayout_content = (LinearLayout) view.findViewById(R.id.lLayout_content);
		txt_title = (TextView) view.findViewById(R.id.txt_title);
		txt_ok = (TextView) view.findViewById(R.id.txt_ok);
		txt_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String productSkuIds = null;
				String tag = null;
				if (styleItemMap == null) {
					OKlistener.onClick(productSkuIds, tag);
					dialog.dismiss();
					return;
				}
				for (String key : styleItemMap.keySet()) {
					List<TagBean> list = styleItemMap.get(key);
					FlowRadioGroup radioGroup = styleItems.get(key);
					RadioButton radioButton = (RadioButton) radioGroup
							.findViewById(radioGroup.getCheckedRadioButtonId());
					TagBean tagBean = (TagBean) radioButton.getTag();
					if (productSkuIds == null) {
						productSkuIds = tagBean.getId();
					} else {
						productSkuIds = productSkuIds + "," + tagBean.getId();
					}
					if (tag == null) {
						tag = tagBean.getTagName();
					} else {
						tag = tag + "," + tagBean.getTagName();
					}
				}
				OKlistener.onClick(productSkuIds, tag);
				dialog.dismiss();
			}
		});
		txt_cancel = (TextView) view.findViewById(R.id.txt_cancel);
		txt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		// 定义Dialog布局和参数
		dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
		dialog.setContentView(view);
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.x = 0;
		lp.y = 0;
		dialogWindow.setAttributes(lp);

		return this;
	}

	public GoodStyleDialog setOkButton(OnButtonClickListener listener) {
		OKlistener = listener;
		return this;
	}

	public interface OnButtonClickListener {
		void onClick(String productSkuIds, String tag);
	}

	public GoodStyleDialog setTitle(String title) {
		showTitle = true;
		txt_title.setVisibility(View.VISIBLE);
		txt_title.setTextSize(20);
		txt_title.setText(title);
		return this;
	}

	public GoodStyleDialog setCancelable(boolean cancel) {
		dialog.setCancelable(cancel);
		return this;
	}

	public GoodStyleDialog setCanceledOnTouchOutside(boolean cancel) {
		dialog.setCanceledOnTouchOutside(cancel);
		return this;
	}

	/**
	 * 
	 * @param strItem
	 *            条目名称
	 * @param styleList
	 * @return
	 */
	public GoodStyleDialog addStyleItem(String strItem, List<TagBean> styleList) {
		if (styleItemMap == null) {
			styleItemMap = new HashMap<String, List<TagBean>>();
		}
		if (styleItems == null) {
			styleItems = new HashMap<String, FlowRadioGroup>();
		}
		if (checkedList == null) {
			checkedList = new HashMap<String, String>();
		}
		styleItemMap.put(strItem, styleList);
		FlowRadioGroup radioGroup = (FlowRadioGroup) LayoutInflater.from(context)
				.inflate(R.layout.radio_group_layout, null);
		styleItems.put(strItem, radioGroup);
		return this;
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				context.getResources().getDisplayMetrics());
	}

	/** 设置条目布局 */
	private void setStyleItems() {
		if (styleItemMap == null || styleItemMap.size() <= 0) {
			return;
		}
		int size = styleItemMap.size();

		// TODO 高度控制，非最佳解决办法
		// 添加条目过多的时候控制高度
		if (size >= 4) {
			LinearLayout.LayoutParams params = (LayoutParams) sLayout_content.getLayoutParams();
			params.height = display.getHeight() / 2;
			sLayout_content.setLayoutParams(params);
		}

		for (String itemStr : styleItemMap.keySet()) {
			List<TagBean> list = styleItemMap.get(itemStr);
			TextView textView = new TextView(context);
			textView.setText("选择" + itemStr);
			textView.setTextSize(18);
			textView.setTextColor(context.getColor(R.color.actionsheet_gray));
//			textView.setPadding(dp2px(10), 0, dp2px(10), 0);
			textView.setGravity(Gravity.CENTER_VERTICAL);
			// 高度
			float scale = context.getResources().getDisplayMetrics().density;
			int height = (int) (45 * scale + 0.5f);
			textView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, height));
			lLayout_content.addView(textView);
			FlowRadioGroup radioGroup = styleItems.get(itemStr);
			radioGroup.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
//			radioGroup.setPadding(dp2px(10), 0, dp2px(10), 0);
			for (int i = 0; i < list.size(); i++) {
				RadioButton button = (RadioButton) LayoutInflater.from(context)
				    .inflate(R.layout.radio_button_layout, null);
				
//				button.setBackgroundResource(R.drawable.check_green_small_selector);
//				button.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
//				button.setTextColor(context.getColor(R.color.white));
//				button.setTextSize(16);
//				button.setGravity(Gravity.CENTER);
				int buttonHeight = (int) (45 * scale + 0.5f);
				int buttonWidth = (int) (80 * scale + 0.5f);
				LayoutParams layoutParams = new LinearLayout.LayoutParams(buttonWidth, buttonHeight);
				button.setLayoutParams(layoutParams);
				TagBean tagBean = list.get(i);
				button.setTag(tagBean);
				button.setText(tagBean.getTagName());
				radioGroup.addView(button);
			}
			lLayout_content.addView(radioGroup);
		}

	}

	public void show() {
		setStyleItems();
		dialog.show();
	}

}
