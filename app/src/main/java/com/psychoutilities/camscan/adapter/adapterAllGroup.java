package com.psychoutilities.camscan.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.psychoutilities.camscan.R;
import com.psychoutilities.camscan.activity.ActMain;
import com.psychoutilities.camscan.models.ModelDB;

import java.util.ArrayList;

public class adapterAllGroup extends RecyclerView.Adapter<adapterAllGroup.HolderView> {

    public Activity activity;

    public ArrayList<ModelDB> arrayList;
    private String current_mode;

    @Override
    public HolderView onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        if (current_mode.equals("Grid")) {
            view = LayoutInflater.from(activity).inflate(R.layout.item_grid_group_act, viewGroup, false);
        } else {
            view = LayoutInflater.from(activity).inflate(R.layout.item_list_group_act, viewGroup, false);
        }
        return new HolderView(view);
    }

    @Override
    public void onBindViewHolder(HolderView viewHolder, final int i) {

        if (arrayList.get(i).getGroup_first_img() == null) {
            viewHolder.iv_group_folder_img.setImageResource(R.drawable.ic_folder_plus_icon);
            viewHolder.iv_group_folder_img.setVisibility(View.VISIBLE);
            viewHolder.iv_group_first_img.setVisibility(View.GONE);
        } else if (arrayList.get(i).getGroup_first_img().isEmpty()) {
            viewHolder.iv_group_folder_img.setImageResource(R.drawable.ic_folder_plus_icon);
            viewHolder.iv_group_folder_img.setVisibility(View.VISIBLE);
            viewHolder.iv_group_first_img.setVisibility(View.GONE);
        } else {
            viewHolder.iv_group_folder_img.setVisibility(View.GONE);
            viewHolder.iv_group_first_img.setVisibility(View.VISIBLE);
            Glide.with(activity)
                    .load(arrayList.get(i).getGroup_first_img())
                    .into(viewHolder.iv_group_first_img);

        }
        viewHolder.tv_group_name.setText(arrayList.get(i).getGroup_name());
        viewHolder.tv_group_date.setText(arrayList.get(i).getGroup_date());
        viewHolder.rl_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ActMain) activity).clickOnListItem(((ModelDB) arrayList.get(i)).getGroup_name());
            }
        });
        viewHolder.iv_group_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ActMain) activity).clickOnListMore(arrayList.get(i),((ModelDB) arrayList.get(i)).getGroup_name(), ((ModelDB) arrayList.get(i)).getGroup_date());
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public adapterAllGroup(Activity activity2, ArrayList<ModelDB> arrayList2, String str) {
        this.activity = activity2;
        this.arrayList = arrayList2;
        this.current_mode = str;
    }

    public void filterList(ArrayList<ModelDB> arrayList2) {
        this.arrayList = arrayList2;
        notifyDataSetChanged();
    }

    public class HolderView extends RecyclerView.ViewHolder {
        ImageView iv_group_first_img;
        ImageView iv_group_more;
        ImageView iv_group_folder_img;
        RelativeLayout rl_group;
        TextView tv_group_date;
        TextView tv_group_name;

        public HolderView(View view) {
            super(view);
            rl_group = (RelativeLayout) view.findViewById(R.id.rl_group);
            iv_group_first_img = (ImageView) view.findViewById(R.id.iv_group_first_img);
            tv_group_name = (TextView) view.findViewById(R.id.tv_group_name);
            tv_group_date = (TextView) view.findViewById(R.id.tv_group_date);
            iv_group_more = (ImageView) view.findViewById(R.id.iv_group_more);
            iv_group_folder_img = (ImageView) view.findViewById(R.id.iv_group_folder_img);
        }
    }
}
