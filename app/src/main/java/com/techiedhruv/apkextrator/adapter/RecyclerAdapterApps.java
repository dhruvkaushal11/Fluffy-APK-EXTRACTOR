package com.techiedhruv.apkextrator.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.techiedhruv.apkextrator.R;
import com.techiedhruv.apkextrator.interfaceCallBack.ExtractCallBack;
import com.techiedhruv.apkextrator.model.AppInfo;

import java.util.List;

public class RecyclerAdapterApps extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int APP_ROW = 0, HEADER_TOP = 1;
    private Context mContext;
    private List<AppInfo> mAppList;
    private ExtractCallBack mExtractCallBack;

    public RecyclerAdapterApps(Context pContext, List<AppInfo> pAppList) {
        this.mExtractCallBack = (ExtractCallBack) pContext;
        this.mAppList = pAppList;
        this.mContext = pContext;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case APP_ROW:
                View totalHeader = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_row, parent, false);
                return new AppViewHolder(totalHeader);
            case HEADER_TOP:
                View blank = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_top, parent, false);
                return new BlankHolder(blank);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof AppViewHolder) {
            AppViewHolder appViewHolder = (AppViewHolder) holder;
            appViewHolder.mAppName.setText(mAppList.get(position - 1).getmAppName());
            appViewHolder.mPackageName.setText(mAppList.get(position - 1).getmPackageName());
            appViewHolder.mAppIcon.setImageDrawable(mAppList.get(position - 1).getmAppIcon());
            appViewHolder.mExtractApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mExtractCallBack.extract(mAppList.get(position - 1).getmPackageName(), mAppList.get(position - 1).getmAppName());
                }
            });
        }

    }

    @Override
    public int getItemCount() {

        return mAppList.size() + 1; // First Element is the header

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER_TOP;
        }
        return APP_ROW;
    }

    private class AppViewHolder extends RecyclerView.ViewHolder {
        TextView mAppName, mPackageName, mExtractApp;
        ImageView mAppIcon;

        AppViewHolder(View view) {
            super(view);
            mAppName = view.findViewById(R.id.app_name);
            mExtractApp = view.findViewById(R.id.extract_app);

            mPackageName = view.findViewById(R.id.package_name);
            mAppIcon = view.findViewById(R.id.app_icon);
        }


    }

    private class BlankHolder extends RecyclerView.ViewHolder {

        BlankHolder(View view) {
            super(view);
        }


    }

}
