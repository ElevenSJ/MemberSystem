package com.sj.widgets.downloadview;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jady.retrofitclient.HttpManager;
import com.jady.retrofitclient.listener.DownloadFileListener;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.lyp.membersystem.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.sj.activity.bean.ForumBean;
import com.sj.activity.bean.StudyHtmlCommonBean;
import com.sj.utils.StringUtils;
import com.yuntongxun.ecdemo.common.utils.FileAccessor;

import java.io.File;
import java.util.logging.Logger;

/**
 * 创建时间: on 2018/5/25.
 * 创建人: 孙杰
 * 功能描述:
 */
public class DownloadAdapter extends RecyclerArrayAdapter<StudyHtmlCommonBean.AttachsBean> {
    Context context;
    public DownloadAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public DownloadRyvHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new DownloadRyvHolder(parent);
    }

    private static class DownloadRyvHolder extends BaseViewHolder<StudyHtmlCommonBean.AttachsBean> {
        private TextView txtName;
        private Button downProgressView;

        public DownloadRyvHolder(ViewGroup parent) {
            super(parent, R.layout.item_dialog_download);
            txtName = $(R.id.txt_name);
            downProgressView = $(R.id.download_view);
        }

        @Override
        public void setData(final StudyHtmlCommonBean.AttachsBean data) {
            super.setData(data);
            String fileEx = "."+StringUtils.getExtensionName(data.getFileUrl());
            final String filePath = FileAccessor.IMESSAGE_FILE+ "/"+new Md5FileNameGenerator().generate(data.getFileUrl())+fileEx;
            Log.i(DownloadAdapter.class.toString(), "filePath: "+filePath);
            txtName.setText((getDataPosition()+1)+"、 "+data.getFileName());
            if (new File(filePath).exists()){
                downProgressView.setText("打开");
            }else{
                downProgressView.setText("下载");
            }
            downProgressView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (downProgressView.getText().toString().equals("下载")){
                        downProgressView.setClickable(false);
                        HttpManager.getInstance().download(data.getFileUrl(), filePath, new DownloadFileListener() {
                            @Override
                            public void onNext(Object o) {

                            }

                            @Override
                            public void onComplete() {

                            }

                            @Override
                            public void updateProgress(float contentRead, long contentLength, boolean completed) {
                                Log.e("download", "updateProgress: "+(contentRead/contentLength*100)+"%");
                                downProgressView.setText((contentRead/contentLength*100)+"%");
                                if (completed){
                                    downProgressView.setClickable(true);
                                    downProgressView.setText("打开");
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("download", "onError: "+e.getMessage());
                                downProgressView.setClickable(true);
                                downProgressView.setText("下载");
                                new File(filePath).delete();
                            }
                        });
                    }else{
                        FileAccessor.openFileByPath(v.getContext(),filePath);
                    }

                }
            });
        }
    }
}
