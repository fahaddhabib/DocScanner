package com.psychoutilities.camscan.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.psychoutilities.camscan.R;
import com.psychoutilities.camscan.activity.ActMain;
import com.psychoutilities.camscan.main_utils.Constant;
import com.psychoutilities.camscan.models.ModelDrawer;

import java.util.ArrayList;

public class AdapterDrawerItem extends BaseAdapter {

    public Activity activity;
    private ArrayList<ModelDrawer> arrayList;

    private void initTheme(SwitchCompat switchNightMode) {
        int savedTheme = getSavedTheme();
        if (savedTheme == Constant.THEME_LIGHT) {
            switchNightMode.setChecked(false);
//            setTheme(AppCompatDelegate.MODE_NIGHT_NO,Constant.THEME_LIGHT);
        } else if (savedTheme == Constant.THEME_DARK) {
            switchNightMode.setChecked(true);
//            setTheme(AppCompatDelegate.MODE_NIGHT_YES,Constant.THEME_DARK);
        }
    }

    private int getSavedTheme() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(Constant.KEY_THEME, Constant.THEME_UNDEFINED);
    }


    public void saveTheme(int theme) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(Constant.KEY_THEME, theme).apply();
    }

    private void setTheme(int themeMode, int prefsMode) {
        AppCompatDelegate.setDefaultNightMode(themeMode);
        saveTheme(prefsMode);
    }

    @Override
    public long getItemId(int i) {
        return (long) i;
    }

    public AdapterDrawerItem(Activity activity2, ArrayList<ModelDrawer> arrayList2) {
        activity = activity2;
        arrayList = arrayList2;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return Integer.valueOf(i);
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        HolderView viewHolder = new HolderView();
        View inflate = LayoutInflater.from(activity).inflate(R.layout.list_item_drawer_act, viewGroup, false);
        viewHolder.iv_item_icon =  inflate.findViewById(R.id.iv_item_icon);
        viewHolder.tv_item_name =  inflate.findViewById(R.id.tv_item_name);
        viewHolder.switchNightMode =  inflate.findViewById(R.id.switchNightMode);

        if (arrayList.get(i).getItem_name().equalsIgnoreCase(activity.getResources().getString(R.string.darkTheme))) {
            viewHolder.switchNightMode.setVisibility(View.VISIBLE);
            initTheme(viewHolder.switchNightMode);
        } else {
            viewHolder.switchNightMode.setVisibility(View.GONE);
        }
        viewHolder.iv_item_icon.setImageResource(arrayList.get(i).getItem_icon());
        viewHolder.tv_item_name.setText(arrayList.get(i).getItem_name());
        inflate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ActMain) activity).onDrawerItemSelected(i);
            }
        });

        viewHolder.switchNightMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = activity.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
                if (isChecked) {
                    sharedPreferences.edit().putInt(Constant.KEY_THEME, Constant.THEME_DARK).apply();
                    setTheme(AppCompatDelegate.MODE_NIGHT_YES, Constant.THEME_DARK);
                } else {
                    sharedPreferences.edit().putInt(Constant.KEY_THEME, Constant.THEME_LIGHT).apply();
                    setTheme(AppCompatDelegate.MODE_NIGHT_NO, Constant.THEME_LIGHT);
                }
            }
        });
        return inflate;
    }

    public class HolderView {
        private ImageView iv_item_icon;
        private TextView tv_item_name;
        private SwitchCompat switchNightMode;

        public HolderView() {
        }
    }

}
