package com.cow.liucy.hdxm.libcommon.enums;
public enum QianYiPlateColorType {


    LC_UNKNOWN(0,"未知颜色"),
    LC_BLUE(1,"蓝色"),
    LC_YELLOW(2,"黄色"),
    LC_WHITE (3,"白色"),
    LC_BLACK(4,"黑色"),
    LC_GREEN(5,"绿色"),
    LC_YELLOW_GREEN(6,"黄绿色"),
    LC_OTHER(7,"其他黑");


    private int type;
    private String desc;
    QianYiPlateColorType(int type, String desc){
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
        for (QianYiPlateColorType qianYiPlateColorType : QianYiPlateColorType.values()) {
            if(qianYiPlateColorType.getType()==type){
                return qianYiPlateColorType.getDesc();
            }
        }
        return "未知";
    }
}