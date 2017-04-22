package com.unotrack.device.demo;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;


public class DetilListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> mData;
    private String[] mNameArray;
    DatabaseHandler db;
    public static final int VIEW_TYPE_ONE = 1;
    public static final int VIEW_TYPE_TWO = 2;


    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;
    private OnSeekBarchangeListener mOnSeekBarchangeListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnSeekBarchangeListener {
        void onProgressChanged(View view, int position, int progress);

        void onStopTrackingTouch(View view, int position, int progress);
    }

    public void setOnProgressChangeListener(OnSeekBarchangeListener mOnSeekBarchangeListener) {
        this.mOnSeekBarchangeListener = mOnSeekBarchangeListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_ONE:
                viewHolder = new ViewHolder1(View.inflate(parent.getContext(), R.layout.device_item, null));
                break;
            case VIEW_TYPE_TWO:
                viewHolder = new ViewHolder2(View.inflate(parent.getContext(), R.layout.device_item2, null));
                break;
        }
        //db= new DatabaseHandler(viewHolder.itemView.getContext());
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        int itemViewType = holder.getItemViewType();
        switch (itemViewType) {
            case VIEW_TYPE_ONE:
                ((ViewHolder1) holder).setDataAndUi(mData.get(position));
                break;
            case VIEW_TYPE_TWO:
                ((ViewHolder2) holder).setDataAndUi(mData.get(position));
                break;
        }

        // 如果设置了回调，则设置点击事件
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, pos);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemLongClick(holder.itemView, pos);
                    return false;
                }
            });
        }
        if (mOnSeekBarchangeListener != null) {
            if (holder instanceof ViewHolder2) {
                SeekBar seekBar = (SeekBar) holder.itemView.findViewById(R.id.data);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        int pos = holder.getLayoutPosition();
                        mOnSeekBarchangeListener.onProgressChanged(holder.itemView, pos, progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        int pos = holder.getLayoutPosition();
                        mOnSeekBarchangeListener.onStopTrackingTouch(holder.itemView, pos, seekBar.getProgress());
                    }
                });
            }
        }

    }


    @Override
    public int getItemCount() {
        if (mNameArray != null) {
            return mNameArray.length;
        }
        return 0;
    }

    public void setNameArray(String[] nameArray) {
        mNameArray = nameArray;
    }

    public void setData(List<String> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public String getData(int position) {
        return mData.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 13 || position == 14) {
            return VIEW_TYPE_TWO;
        } else {
            return VIEW_TYPE_ONE;
        }
    }

   // public class ViewHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener {
   public class ViewHolder1 extends RecyclerView.ViewHolder  {

        private final TextView mName;
        private final TextView mData;
//        private final EditText mdeviceName;
//        private final Button b;


        public ViewHolder1(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.name);
            mData = (TextView) itemView.findViewById(R.id.data);
//            mdeviceName=(EditText) itemView.findViewById(R.id.devicename);
//            b=(Button) itemView.findViewById(R.id.setname);
//            db=new DatabaseHandler(itemView.getContext());
//            b.setOnClickListener(this);
        }

        public void setDataAndUi(String data) {
            mName.setText(mNameArray[getPosition()]);
            mData.setText(data);

           // StoredData sd=db.getEntry(mName.getText().toString());


           // mdeviceName.setText(sd.getvalue());
        }

//        @Override
//        public void onClick(View view) {
//
//            db.addEntry(new StoredData(mName.getText().toString(), mdeviceName.getText().toString()));
//        }
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder {

        private final TextView mName;
        private final SeekBar mData;

        public ViewHolder2(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.name);
            mData = (SeekBar) itemView.findViewById(R.id.data);
        }

        public void setDataAndUi(String data) {
            mName.setText(mNameArray[getPosition()]);
            int progress = Integer.parseInt(data);
            if (getPosition() == 13) {
                mData.setMax(7);
                if (progress == -1) {
                    progress = 6;
                }
                mData.setProgress(progress);
            }
            if (getPosition() == 14) {
                mData.setMax(8);
                if (progress == -1) {
                    progress = 8;
                }
                mData.setProgress(progress);
            }
        }
    }
}
