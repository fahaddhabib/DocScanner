package com.psychoutilities.camscan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.psychoutilities.camscan.R;
import com.psychoutilities.camscan.models.ModelEditTool;
import com.psychoutilities.camscan.models.ToolTypeEdit;

import java.util.ArrayList;

public class AdapterEditTools extends RecyclerView.Adapter<AdapterEditTools.HolderView> {


    @Override
    public HolderView onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new HolderView(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_edit_tools_act, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(HolderView viewHolder, final int i) {
        viewHolder.iv_toolIcon.setImageResource(toolsList.get(i).getTool_icon());
        viewHolder.txtIconName.setText(toolsList.get(i).getIcon_name());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onToolSelected.onToolSelected(((ModelEditTool) toolsList.get(i)).getEditToolType());
            }
        });
    }

    @Override
    public int getItemCount() {
        return toolsList.size();
    }

    public ToolOnSelected onToolSelected;

    public ArrayList<ModelEditTool> toolsList = new ArrayList<>();

    public interface ToolOnSelected {
        void onToolSelected(ToolTypeEdit editToolType);
    }

    public AdapterEditTools(ToolOnSelected onToolSelected2) {
        onToolSelected = onToolSelected2;
        toolsList.add(new ModelEditTool(R.drawable.ic_color_filter, ToolTypeEdit.COLORFILTER,"Color Filter"));
        toolsList.add(new ModelEditTool(R.drawable.ic_adjust, ToolTypeEdit.ADJUST,"Adjust"));
        toolsList.add(new ModelEditTool(R.drawable.ic_highlight_edit, ToolTypeEdit.HIGHLIGHT,"Highlight"));
        toolsList.add(new ModelEditTool(R.drawable.ic_picture, ToolTypeEdit.PICTURE,"Picture"));
        toolsList.add(new ModelEditTool(R.drawable.ic_sign, ToolTypeEdit.SIGNATURE,"Signature"));
        toolsList.add(new ModelEditTool(R.drawable.ic_watermark, ToolTypeEdit.WATERMARK,"Watermark"));
        toolsList.add(new ModelEditTool(R.drawable.ic_text, ToolTypeEdit.TEXT,"Text"));
        toolsList.add(new ModelEditTool(R.drawable.ic_overlay, ToolTypeEdit.OVERLAY,"Overlay"));
        toolsList.add(new ModelEditTool(R.drawable.ic_color_effect, ToolTypeEdit.COLOREFFECT,"Color Effect"));
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
