package com.cow.liucy.hdxm.libcommon.enums;
public enum IceCarColorType {


    LCOLOUR_UNKONEW(0,"白"),
    LCOLOUR_RED(1,"红"),
    LCOLOUR_GREEN(2,"绿"),
    LCOLOUR_BLUE(3,"蓝"),
    LCOLOUR_YELLOW(4,"黄"),
    LCOLOUR_WHITE(5,"白"),
    LCOLOUR_SILVER(6,"灰"),
    LCOLOUR_BLACK(7,"黑"),
    LCOLOUR_ZI(8,"紫"),
    LCOLOUR_BROWN(9,"棕"),
    LCOLOUR_PINK(10,"粉");

    private int type;
    private String desc;
    IceCarColorType(int type, String desc){
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
        for (IceCarColorType iceCarColorType : IceCarColorType.values()) {
            if(iceCarColorType.getType()==type){
                return iceCarColorType.getDesc();
            }
        }
        return "未知";
    }
}