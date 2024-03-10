package com.psychoutilities.camscan.adapter;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.psychoutilities.camscan.R;
import com.psychoutilities.camscan.activity.ActDocEditor;
import com.psychoutilities.camscan.main_utils.Constant;
import com.itextpdf.text.html.HtmlTags;

import java.util.ArrayList;

public class AdapterFont extends RecyclerView.Adapter<AdapterFont.HolderView> {

    public Activity activity;
    public ArrayList<String> arrayList;

    @Override
    public HolderView onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new HolderView(LayoutInflater.from(activity).inflate(R.layout.list_item_font_act, viewGroup, false));
    }
    @Override
    public void onBindViewHolder(HolderView viewHolder, final int i) {
        AssetManager assets = activity.getAssets();
        viewHolder.tv_fontStyle.setTypeface(Typeface.createFromAsset(assets, "font/" + arrayList.get(i)));
        viewHolder.rl_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(HtmlTags.FONT, "onClick: " + ((String) arrayList.get(i)));
                AssetManager assets = activity.getAssets();
                ((ActDocEditor) activity).onFontClick(Typeface.createFromAsset(assets, "font/" + ((String) arrayList.get(i))));
                Constant.selectedFont = i;
                notifyDataSetChanged();
            }
        });
        if (i == 0) {
            viewHolder.tv_fontStyle.setText("None");
        } else {
            viewHolder.tv_fontStyle.setText("Sample");
        }
        if (Constant.selectedFont == i) {
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

    public AdapterFont(Activity activity2, ArrayList<String> arrayList2) {
        activity = activity2;
        arrayList = arrayList2;
    }

    public class HolderView extends RecyclerView.ViewHolder {
        RelativeLayout rl_main;
        TextView tv_fontStyle;

        public HolderView(View view) {
            super(view);
            rl_main = (RelativeLayout) view.findViewById(R.id.rl_main);
            tv_fontStyle = (TextView) view.findViewById(R.id.tv_fontStyle);
        }
    }
}
