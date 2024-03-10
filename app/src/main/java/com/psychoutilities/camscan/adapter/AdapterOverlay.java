package com.psychoutilities.camscan.adapter;

import android.app.Activity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.psychoutilities.camscan.R;
import com.psychoutilities.camscan.activity.ActDocEditor;
import com.psychoutilities.camscan.main_utils.UtilsBitmap;

import java.util.ArrayList;

public class AdapterOverlay extends RecyclerView.Adapter<AdapterOverlay.HolderView> {

    public Activity activity;

    public ArrayList<Pair<String, String>> overlayList = new ArrayList<>();

    @Override
    public void onBindViewHolder(HolderView viewHolder, final int i) {
        Pair pair = overlayList.get(i);
        viewHolder.iv_overlay.setImageBitmap(UtilsBitmap.getBitmapFromAsset(viewHolder.itemView.getContext(), (String) pair.first));
        viewHolder.tv_overlay.setText((CharSequence) pair.second);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ActDocEditor) activity).onOverlaySelected(i, (String) ((Pair) overlayList.get(i)).first);
            }
        });
    }
    @Override
    public int getItemCount() {
        return overlayList.size();
    }

    public AdapterOverlay(Activity activity2) {
        activity = activity2;
        overlayList.add(new Pair("overlay/ic_none.png", "None"));
        overlayList.add(new Pair("overlay/overlay_1.jpg", "Overlay 1"));
        overlayList.add(new Pair("overlay/overlay_2.jpg", "Overlay 2"));
        overlayList.add(new Pair("overlay/overlay_3.jpg", "Overlay 3"));
        overlayList.add(new Pair("overlay/overlay_4.jpg", "Overlay 4"));
        overlayList.add(new Pair("overlay/overlay_5.jpg", "Overlay 5"));
        overlayList.add(new Pair("overlay/overlay_6.jpg", "Overlay 6"));
        overlayList.add(new Pair("overlay/overlay_7.jpg", "Overlay 7"));
        overlayList.add(new Pair("overlay/overlay_8.jpg", "Overlay 8"));
        overlayList.add(new Pair("overlay/overlay_9.jpg", "Overlay 9"));
        overlayList.add(new Pair("overlay/overlay_10.jpg", "Overlay 10"));
    }
    @Override
    public HolderView onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new HolderView(LayoutInflater.from(activity).inflate(R.layout.item_color_effect_list_act, viewGroup, false));
    }

    public class HolderView extends RecyclerView.ViewHolder {
        ImageView iv_overlay;
        TextView tv_overlay;

        public HolderView(View view) {
            super(view);
            iv_overlay = (ImageView) view.findViewById(R.id.iv_effect_view);
            tv_overlay = (TextView) view.findViewById(R.id.tv_effect_name);
        }
    }
}
