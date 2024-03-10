package com.psychoutilities.camscan.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.psychoutilities.camscan.R;
import com.psychoutilities.camscan.activity.ActDocEditor;
import com.psychoutilities.camscan.main_utils.Constant;

import java.util.ArrayList;

public class AdapterWatermarkFont extends RecyclerView.Adapter<AdapterWatermarkFont.HolderView> {

    public Activity activity;

    public ArrayList<Integer> arrayList;

    public AdapterWatermarkFont(Activity activity2, ArrayList<Integer> arrayList2) {
        activity = activity2;
        arrayList = arrayList2;
    }

    @Override
    public void onBindViewHolder(HolderView viewHolder, final int i) {
        viewHolder.tv_fontStyle.setTypeface(ResourcesCompat.getFont(activity, arrayList.get(i).intValue()));
        viewHolder.tv_fontStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ActDocEditor) activity).onWatermarkFontClick(((Integer) arrayList.get(i)).intValue());
                Constant.selectedWatermarkFont = i;
                notifyDataSetChanged();
            }
        });
        if (i == 0) {
            viewHolder.tv_fontStyle.setText("None");
        } else {
            viewHolder.tv_fontStyle.setText("Sample");
        }
        if (Constant.selectedWatermarkFont == i) {
            viewHolder.rl_main.setBackground(activity.getResources().getDrawable(R.drawable.selected_font_bg));
            viewHolder.tv_fontStyle.setTextColor(activity.getResources().getColor(R.color.white));
            return;
        }
        viewHolder.rl_main.setBackground(activity.getResources().getDrawable(R.drawable.unselected_font_bg));
        viewHolder.tv_fontStyle.setTextColor(activity.getResources().getColor(R.color.txt_color));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public HolderView onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new HolderView(LayoutInflater.from(activity).inflate(R.layout.font_list_item_watermark_act, viewGroup, false));
    }

    public class HolderView extends RecyclerView.ViewHolder {
        RelativeLayout rl_main;
        TextView tv_fontStyle;

        public HolderView(View view) {
            super(view);
            tv_fontStyle = (TextView) view.findViewById(R.id.tv_fontStyle);
            rl_main = (RelativeLayout) view.findViewById(R.id.rl_main);
        }
    }
}
