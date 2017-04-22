package com.unotrack.device.demo;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unotrack.device.demo.unobeacTag.unobeacTag;
import com.minew.device.enums.ValueIndex;

import java.util.List;


public class ScanListAdapter extends RecyclerView.Adapter<ScanListAdapter.MyViewHolder> {

    private List<unobeacTag> mMinewDevices;

    public void clear() {
        mMinewDevices.clear();
        notifyDataSetChanged();
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.device_item, null);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.setDataAndUi(mMinewDevices.get(position));

        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.itemView, pos);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemLongClick(holder.itemView, pos);
                    return false;
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        if (mMinewDevices != null) {
            return mMinewDevices.size();
        }
        return 0;
    }

    public void setData(List<unobeacTag> minewDevices) {
        this.mMinewDevices = minewDevices;
        notifyDataSetChanged();
    }

    public unobeacTag getData(int position) {
        return mMinewDevices.get(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private unobeacTag mMinewDevice;
        private final TextView mMac;
        private final TextView mDistance;

        public MyViewHolder(View itemView) {
            super(itemView);
            mMac = (TextView) itemView.findViewById(R.id.name);
            mDistance = (TextView) itemView.findViewById(R.id.data);
        }

        public void setDataAndUi(unobeacTag minewDevice) {
            mMinewDevice = minewDevice;
            mMac.setText(minewDevice.mMinewDevice.getValue(ValueIndex.ValueIndex_MacAddress).getStringValue());
            mDistance.setText(minewDevice.mMinewDevice.getValue(ValueIndex.ValueIndex_Distance).getFloatValue() + "M");
        }
    }
}
