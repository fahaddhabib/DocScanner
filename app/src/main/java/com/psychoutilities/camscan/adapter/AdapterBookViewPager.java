package com.psychoutilities.camscan.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.github.chrisbanes.photoview.PhotoView;
import com.psychoutilities.camscan.R;
import com.psychoutilities.camscan.models.ModelBook;

import java.util.ArrayList;

public class AdapterBookViewPager extends PagerAdapter {
    private Activity activity;
    private ArrayList<ModelBook> arrayList;
    private LayoutInflater layoutInflater;

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup viewGroup, int i) {
        View inflate = layoutInflater.inflate(R.layout.item_viewpager_act, viewGroup, false);
        ((RequestBuilder) Glide.with(activity).load(arrayList.get(i).getBitmap()).placeholder(R.color.bg_color)).into((ImageView) (PhotoView) inflate.findViewById(R.id.book_page));
        viewGroup.addView(inflate);
        return inflate;
    }

    @Override
    public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
        viewGroup.removeView((RelativeLayout) obj);
    }

    public AdapterBookViewPager(Activity activity2, ArrayList<ModelBook> arrayList2) {
        this.activity = activity2;
        this.arrayList = arrayList2;
        this.layoutInflater = (LayoutInflater) activity2.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

}
