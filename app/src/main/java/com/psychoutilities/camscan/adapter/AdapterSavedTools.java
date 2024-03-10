package com.psychoutilities.camscan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.psychoutilities.camscan.R;
import com.psychoutilities.camscan.models.ModelSavedTool;
import com.psychoutilities.camscan.models.ToolTypeSaved;

import java.util.ArrayList;

public class AdapterSavedTools extends RecyclerView.Adapter<AdapterSavedTools.HolderView> {

    public SelectedOnSavedTool onSavedToolSelected;

    public ArrayList<ModelSavedTool> savedToolsList = new ArrayList<>();

    public interface SelectedOnSavedTool {
        void onSavedToolSelected(ToolTypeSaved savedToolType);
    }

    public AdapterSavedTools(SelectedOnSavedTool onSavedToolSelected2) {
        onSavedToolSelected = onSavedToolSelected2;
        savedToolsList.add(new ModelSavedTool(R.drawable.ssic_edit, ToolTypeSaved.EDIT, R.string.edit));
        savedToolsList.add(new ModelSavedTool(R.drawable.ssic_open_pdf, ToolTypeSaved.OPENPDF, R.string.open_pdf));
        savedToolsList.add(new ModelSavedTool(R.drawable.ssic_name, ToolTypeSaved.NAME, R.string.name));
        savedToolsList.add(new ModelSavedTool(R.drawable.ssic_rotate, ToolTypeSaved.ROTATE, R.string.rotate));
        savedToolsList.add(new ModelSavedTool(R.drawable.ssic_note, ToolTypeSaved.NOTE, R.string.note));
        savedToolsList.add(new ModelSavedTool(R.drawable.ssic_img_to_text, ToolTypeSaved.ImageToText, R.string.image_to_text));
        savedToolsList.add(new ModelSavedTool(R.drawable.ssic_share, ToolTypeSaved.SHARE, R.string.share));
        savedToolsList.add(new ModelSavedTool(R.drawable.ssic_delete, ToolTypeSaved.DELETE, R.string.delete));
    }

    @Override
    public HolderView onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new HolderView(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_saved_tools_act, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(HolderView viewHolder, final int i) {
        viewHolder.iv_toolIcon.setImageResource(savedToolsList.get(i).getSaved_tool_icon());
        viewHolder.txtIconName.setText(savedToolsList.get(i).getIcon_name());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSavedToolSelected.onSavedToolSelected(((ModelSavedTool) savedToolsList.get(i)).getSavedToolType());
            }
        });
    }

    @Override
    public int getItemCount() {
        return savedToolsList.size();
    }

    public class HolderView extends RecyclerView.ViewHolder {
        ImageView iv_toolIcon;
        TextView txtIconName;

        public HolderView(View view) {
            super(view);
            iv_toolIcon = (ImageView) view.findViewById(R.id.iv_toolIcon);
            txtIconName = (TextView) view.findViewById(R.id.txtIconName);
        }
    }
}
