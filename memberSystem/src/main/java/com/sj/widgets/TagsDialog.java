package com.sj.widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.lyp.membersystem.R;
import com.lyp.membersystem.view.contactsort.ContactSortModel;

import java.util.ArrayList;
import java.util.List;

import me.next.tagview.TagCloudView;

/**
 * 创建时间: on 2018/4/1.
 * 创建人: 孙杰
 * 功能描述:TagsDialog
 */
public class TagsDialog extends Dialog {

    private boolean isCancelable = false;
    private boolean isCanceledOnTouchOutside = false;

    TagCloudView tagCloudView;
    Context mContext;
    ClickResult clickResult;
    List<ContactSortModel.TaglistBean> tags;
    List<ContactSortModel.TaglistBean> allTagList;
    public TagsDialog(Context context) {
        this(context, R.style.transdialog);
    }


    public TagsDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(mContext);
    }

    private void init(final Context context) {
        //设置不可取消，点击其他区域不能取消，实际中可以抽出去封装供外包设置
        setCancelable(isCancelable);
        setCanceledOnTouchOutside(isCanceledOnTouchOutside);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_tags_layout,null);
        tagCloudView = view.findViewById(R.id.tag_cloud_view);
        tagCloudView.setOnTagClickListener(new TagCloudView.OnTagClickListener() {
            @Override
            public void onTagClick(int position) {
                TextView tagTextView = (TextView) tagCloudView.getChildAt(position);
                if (tagTextView.isSelected()){
                    tagTextView.setSelected(false);
                    tagTextView.setTextColor(context.getResources().getColor(R.color.gray));
                    tags.remove(allTagList.get(position));
                }else{
                    tagTextView.setSelected(true);
                    tagTextView.setTextColor(context.getResources().getColor(R.color.main_bg_color));
                    tags.add(allTagList.get(position));
                }

            }
        });
        setContentView(view);
    }

    public void setData(List<ContactSortModel.TaglistBean> tagList,List<ContactSortModel.TaglistBean> chooseTagList,ClickResult clickResult) {
        this.show();
        this.clickResult = clickResult;
        if (tagList!=null){
            allTagList=tagList;
            if (chooseTagList!=null){
                this.tags=chooseTagList;
            }
            List<String> tagStrings = new ArrayList<>();
            for (int i = 0; i < tagList.size(); i++) {
                tagStrings.add(tagList.get(i).getTagName());
            }
            tagCloudView.setTags(tagStrings);
            for (int i =0;i<tagCloudView.getChildCount();i++){
                TextView textView = (TextView) tagCloudView.getChildAt(i);
                textView.setBackground(getContext().getResources().getDrawable(R.drawable.background_tag_selector));
                for (String tag:tagStrings){
                    if (tag.equals(textView.getText().toString())){
                        textView.setSelected(true);
                        textView.setTextColor(mContext.getResources().getColor(R.color.main_bg_color));
                    }else{
                        textView.setSelected(false);
                        textView.setTextColor(mContext.getResources().getColor(R.color.gray));
                    }
                }
            }
        }

    }
    @Override
    public void dismiss() {
        super.dismiss();
        if (clickResult!=null){
            clickResult.getAllResult(tags);
        }
    }
    @Override
    public void show() {
        super.show();
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setAttributes(layoutParams);
    }

    @Override
    public void setCancelable(boolean cancelable) {
        isCancelable = cancelable;
    }

    @Override
    public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        isCanceledOnTouchOutside = canceledOnTouchOutside;
    }

    public interface ClickResult{
        void getAllResult(List<ContactSortModel.TaglistBean> tagList);
    }

}