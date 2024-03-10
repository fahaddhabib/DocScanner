package com.psychoutilities.camscan.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.psychoutilities.camscan.R;
import com.psychoutilities.camscan.activity.ActDocEditor;
import com.psychoutilities.camscan.main_utils.Constant;
import com.psychoutilities.camscan.document_view.FilterColor;


public class AdapterColorFilter extends RecyclerView.Adapter<AdapterColorFilter.HolderView> {

    private String[] colorFilterName;
    public Activity activity;
    private FilterColor colorFilter = new FilterColor();

    @Override
    public void onBindViewHolder(HolderView viewHolder, final int i) {
        viewHolder.tv_filter_name.setText(colorFilterName[i]);
        switch (i) {
            case 0:
                viewHolder.iv_filter_view.setImageBitmap(Constant.original);
                break;
            case 1:
                viewHolder.iv_filter_view.setImageBitmap(colorFilter.filter1(activity, Constant.original));
                break;
            case 2:
                viewHolder.iv_filter_view.setImageBitmap(colorFilter.filter2(activity, Constant.original));
                break;
            case 3:
                viewHolder.iv_filter_view.setImageBitmap(colorFilter.filter3(activity, Constant.original));
                break;
            case 4:
                viewHolder.iv_filter_view.setImageBitmap(colorFilter.filter4(activity, Constant.original));
                break;
            case 5:
                viewHolder.iv_filter_view.setImageBitmap(colorFilter.filter5(activity, Constant.original));
                break;
            case 6:
                viewHolder.iv_filter_view.setImageBitmap(colorFilter.filter6(activity, Constant.original));
                break;
            case 7:
                viewHolder.iv_filter_view.setImageBitmap(colorFilter.filter7(activity, Constant.original));
                break;
            case 8:
                viewHolder.iv_filter_view.setImageBitmap(colorFilter.filter8(activity, Constant.original));
                break;
            case 9:
                viewHolder.iv_filter_view.setImageBitmap(colorFilter.filter9(activity, Constant.original));
                break;
            case 10:
                viewHolder.iv_filter_view.setImageBitmap(colorFilter.filter10(activity, Constant.original));
                break;
            case 11:
                viewHolder.iv_filter_view.setImageBitmap(colorFilter.filter11(activity, Constant.original));
                break;
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.filterPosition = i;
                notifyDataSetChanged();
                ((ActDocEditor) activity).onColorFilterSeleced(activity, i);
            }
        });
        if (Constant.filterPosition == i) {
            viewHolder.ly_img.setBackground(activity.getResources().getDrawable(R.drawable.img_border_selected));
            viewHolder.tv_filter_name.setTextColor(activity.getResources().getColor(R.color.black));
            return;
        }
        viewHolder.ly_img.setBackground(activity.getResources().getDrawable(R.drawable.img_border_unselected));
        viewHolder.tv_filter_name.setTextColor(activity.getResources().getColor(R.color.white));
    }
    @Override
    public int getItemCount() {
        return colorFilterName.length;
    }

    public AdapterColorFilter(Activity activity2, String[] strArr) {
        this.activity = activity2;
        this.colorFilterName = strArr;
    }

    @Override
    public HolderView onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new HolderView(LayoutInflater.from(activity).inflate(R.layout.color_filter_listitem_act, viewGroup, false));
    }

    public class HolderView extends RecyclerView.ViewHolder {
        ImageView iv_filter_view;
        LinearLayout ly_img;
        TextView tv_filter_name;

        public HolderView(View view) {
            super(view);
            ly_img = (LinearLayout) view.findViewById(R.id.ly_img);
            iv_filter_view = (ImageView) view.findViewById(R.id.iv_filter_view);
            tv_filter_name = (TextView) view.findViewById(R.id.tv_filter_name);
        }
    }
}
