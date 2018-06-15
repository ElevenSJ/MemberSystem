package com.sj.activity.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.lyp.membersystem.R;
import com.sj.activity.bean.ReplayBean;
import com.sj.utils.ImageUtils;

import cn.carbs.android.expandabletextview.library.ExpandableTextView;


public class ReplayRyvAdapter extends RecyclerArrayAdapter<ReplayBean> {
    Context context;
    public ReplayRyvAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public ReplayRyvHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new ReplayRyvHolder(parent);
    }

    private static class ReplayRyvHolder extends BaseViewHolder<ReplayBean> implements ExpandableTextView.OnExpandListener{
        private SparseArray<Integer> mPositionsAndStates = new SparseArray<>();
        private int etvWidth;
        private ImageView imgIcon;
        private TextView txtName;
        private TextView txtTime;
        private ExpandableTextView txtComment;
        private RatingBar ratingBar;

        public ReplayRyvHolder(ViewGroup parent) {
            super(parent, R.layout.item_story_replay);
            imgIcon = $(R.id.img_icon);
            txtName = $(R.id.txt_name);
            txtTime = $(R.id.txt_time);
            txtComment = $(R.id.txt_comment);
            ratingBar = $(R.id.ratingBar);
            setRatingBarHeight(parent.getContext(),ratingBar,R.drawable.star);
        }

        @Override
        public void setData(final ReplayBean data) {
            super.setData(data);
            if(etvWidth == 0){
                txtComment.post(new Runnable() {
                    @Override
                    public void run() {
                        etvWidth = txtComment.getWidth();
                    }
                });
            }
            ImageUtils.loadImageWithError(data.getAvatar(),R.drawable.ic_launcher,imgIcon);
            txtName.setText(data.getAskName());
            txtTime.setText(data.getAskTime());
            ratingBar.setRating(data.getStarNumber());
            txtComment.setTag(getDataPosition());
            txtComment.setExpandListener(this);
            Integer state = mPositionsAndStates.get(getDataPosition());

            txtComment.updateForRecyclerView(data.getAskcontent().toString(), etvWidth, state== null ? 0 : state);
        }

        @Override
        public void onExpand(ExpandableTextView view) {
            Object obj = view.getTag();
            if(obj != null && obj instanceof Integer){
                mPositionsAndStates.put((Integer)obj, view.getExpandState());
            }
        }

        @Override
        public void onShrink(ExpandableTextView view) {
            Object obj = view.getTag();
            if(obj != null && obj instanceof Integer){
                mPositionsAndStates.put((Integer)obj, view.getExpandState());
            }
        }
    }

    /**
     * 动态设置Ratingbar高度，解决图片在不同分辨率手机拉伸问题
     * @param context
     * @param ratingBar
     * @param resourceId 本地图片资源Id
     */
    public static void setRatingBarHeight(Context context, RatingBar ratingBar, int resourceId) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
        int height = bitmap.getHeight();
        ViewGroup.LayoutParams params = ratingBar.getLayoutParams();
        params.height = height;
        ratingBar.setLayoutParams(params);
    }
}
