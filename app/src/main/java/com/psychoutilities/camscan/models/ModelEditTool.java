package com.psychoutilities.camscan.models;

public class ModelEditTool {
    private ToolTypeEdit editToolType;
    private int tool_icon;
    private String icon_name;

    public ModelEditTool(int i, ToolTypeEdit editToolType2, String icon_name) {
        this.tool_icon = i;
        this.editToolType = editToolType2;
        this.icon_name = icon_name;
    }

    public int getTool_icon() {
        return this.tool_icon;
    }


    public void setTool_icon(int tool_icon) {
        this.tool_icon = tool_icon;
    }

    public String getIcon_name() {
        return icon_name;
    }

    public void setIcon_name(String icon_name) {
        this.icon_name = icon_name;
    }

    public ToolTypeEdit getEditToolType() {
        return this.editToolType;
    }

    public void setEditToolType(ToolTypeEdit editToolType) {
        this.editToolType = editToolType;
    }

}
