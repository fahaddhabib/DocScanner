package com.psychoutilities.camscan.adapter;

import android.app.Activity;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.psychoutilities.camscan.R;
import com.psychoutilities.camscan.activity.ActDocEditor;
import com.psychoutilities.camscan.main_utils.Constant;

import java.util.ArrayList;

public class AdapterColorEffect extends RecyclerView.Adapter<AdapterColorEffect.HolderView> {

    public Activity activity;
    private ArrayList<Pair<String, ColorMatrixColorFilter>> arrayList = new ArrayList<>();

    @Override
    public void onBindViewHolder(HolderView viewHolder, final int i) {
        viewHolder.iv_effect_view.setImageBitmap(Constant.original);
        viewHolder.iv_effect_view.setColorFilter((ColorFilter) arrayList.get(i).second);
        viewHolder.tv_effect_name.setText((CharSequence) arrayList.get(i).first);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ActDocEditor) activity).onColorEffectClick(i);
            }
        });
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public AdapterColorEffect(Activity activity2) {
        activity = activity2;
        arrayList.add(new Pair("None", Constant.coloreffect[0]));
        arrayList.add(new Pair("Color 1", Constant.coloreffect[1]));
        arrayList.add(new Pair("Color 2", Constant.coloreffect[2]));
        arrayList.add(new Pair("Color 3", Constant.coloreffect[3]));
        arrayList.add(new Pair("Color 4", Constant.coloreffect[4]));
    }
    @Override
    public HolderView onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new HolderView(LayoutInflater.from(activity).inflate(R.layout.item_color_effect_list_act, viewGroup, false));
    }

    public class HolderView extends RecyclerView.ViewHolder {

        public ImageView iv_effect_view;

        public TextView tv_effect_name;

        public HolderView(View view) {
            super(view);
            iv_effect_view = (ImageView) view.findViewById(R.id.iv_effect_view);
            tv_effect_name = (TextView) view.findViewById(R.id.tv_effect_name);
        }
    }
}
