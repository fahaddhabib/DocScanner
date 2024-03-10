package com.psychoutilities.camscan.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.psychoutilities.camscan.R;
import com.psychoutilities.camscan.activity.ActDocEditor;

import java.util.ArrayList;

public class AdapterSignature extends RecyclerView.Adapter<AdapterSignature.HolderView> {

    public Activity activity;
    private ArrayList<String> arrayList;

    public AdapterSignature(Activity activity2, ArrayList<String> arrayList2) {
        activity = activity2;
        arrayList = arrayList2;
    }

    @Override
    public void onBindViewHolder(HolderView viewHolder, final int i) {
        Glide.with(activity).load(arrayList.get(i)).into(viewHolder.iv_signature);
        viewHolder.iv_dlt_signature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ActDocEditor) activity).onDeleteSignature(i);
            }
        });
        viewHolder.iv_signature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ActDocEditor) activity).onClickSignature(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public HolderView onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new HolderView(LayoutInflater.from(activity).inflate(R.layout.list_item_signature_act, viewGroup, false));
    }

    public class HolderView extends RecyclerView.ViewHolder {
        ImageView iv_dlt_signature;
        ImageView iv_signature;

        public HolderView(View view) {
            super(view);
            iv_signature = (ImageView) view.findViewById(R.id.iv_signature);
            iv_dlt_signature = (ImageView) view.findViewById(R.id.iv_dlt_signature);
        }
    }
}
