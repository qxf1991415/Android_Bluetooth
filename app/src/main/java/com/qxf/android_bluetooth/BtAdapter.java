package com.qxf.android_bluetooth;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/10.
 */

public class BtAdapter extends RecyclerView.Adapter<BtAdapter.BTViewHolder> implements View.OnClickListener{

    private Context context;
    private BTViewHolder btViewHolder;
    private List<String> deviceNames = new ArrayList<>();
    private List<String> deviceMacs = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public void setData(List<String> deviceNames, List<String> deviceMacs) {
        this.deviceNames = deviceNames;
        this.deviceMacs = deviceMacs;
    }

    public BtAdapter(Context context) {
        this.context = context;
    }

    @Override
    public BTViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.item_main, parent, false);
        btViewHolder = new BTViewHolder(inflate);
        return btViewHolder;
    }

    @Override
    public void onBindViewHolder(BTViewHolder holder, int position) {
        btViewHolder.itemView.setTag(position);
        btViewHolder.tvDevice.setText(deviceNames.get(position) + "：" + deviceMacs.get(position));

    }

    @Override
    public int getItemCount() {
        return deviceMacs == null ? 0 : deviceMacs.size();
    }

    public void dataChanged() {
        deviceMacs.clear();
        deviceNames.clear();
        notifyDataSetChanged();
    }

    class BTViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDevice;

        public BTViewHolder(View itemView) {
            super(itemView);
            tvDevice = itemView.findViewById(R.id.tv_device);
        }
    }

    @Override
    public void onClick(View view) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(view ,(int)view.getTag());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    interface OnItemClickListener {
        void onItemClick(View view , int position);
    }
}
