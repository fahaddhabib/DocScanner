package com.psychoutilities.camscan.models;

public class ModelSavedTool {
    private ToolTypeSaved savedToolType;
    private int saved_tool_icon;
    private int icon_name;

    public ModelSavedTool(int i, ToolTypeSaved savedToolType2, int icon_name) {
        this.saved_tool_icon = i;
        this.savedToolType = savedToolType2;
        this.icon_name = icon_name;
    }


    public void setSaved_tool_icon(int saved_tool_icon) {
        this.saved_tool_icon = saved_tool_icon;
    }

    public int getIcon_name() {
        return icon_name;
    }

    public void setIcon_name(int icon_name) {
        this.icon_name = icon_name;
    }

    public int getSaved_tool_icon() {
        return this.saved_tool_icon;
    }

    public ToolTypeSaved getSavedToolType() {
        return this.savedToolType;
    }

    public void setSavedToolType(ToolTypeSaved savedToolType) {
        this.savedToolType = savedToolType;
    }
}
