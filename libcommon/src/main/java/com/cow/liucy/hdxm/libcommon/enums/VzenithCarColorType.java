package com.cow.liucy.hdxm.libcommon.enums;
public enum VzenithCarColorType {


    LCOLOUR_WHITE(0,"白"),
    LCOLOUR_SILVER(1,"灰(银)"),
    LCOLOUR_YELLOW(2,"黄"),
    LCOLOUR_PINK(3,"粉"),
    LCOLOUR_RED(4,"红"),
    LCOLOUR_GREEN(5,"绿"),
    LCOLOUR_BLUE(6,"蓝"),
    LCOLOUR_BROWN(7,"棕"),
    LCOLOUR_BLACK(8,"黑");


    private int type;
    private String desc;
    VzenithCarColorType(int type, String desc){
        this.setType(type);
        this.setDesc(desc);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * 获取描述
     * @param type
     * @return
     */
    public static String getDescByType(Integer type){
        for (VzenithCarColorType vzenithCarColorType : VzenithCarColorType.values()) {
            if(vzenithCarColorType.getType()==type){
                return vzenithCarColorType.getDesc();
            }
        }
        return "未知";
    }
}