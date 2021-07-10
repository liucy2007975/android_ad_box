package com.cow.liucy.hdxm.libcommon.enums;
public enum VzenithPlateType {


    LT_UNKNOWN(0,"未知车牌"),
    LT_BLUE(1,"蓝牌小汽车"),
    LT_BLACK(2,"黑牌小汽车"),
    LT_YELLOW(3,"单排黄牌"),
    LT_YELLOW2(4,"黄排黄牌（大车位牌，农用车）"),
    LT_POLICE(5,"警车车牌"),
    LT_ARMPOL(6,"武警车牌"),
    LT_INDIVI(7,"个性化车牌"),
    LT_ARMY(8,"单排军车牌"),
    LT_ARMY2(9,"双排军车牌"),
    LT_EMBASSY(10,"使馆车牌"),
    LT_HONGKONG(11,"香港进出中国大陆车牌"),
    LT_TRACTOR(12,"农用车牌"),
    LT_COACH(13,"教练车牌"),
    LT_MACAO(14,"澳门进出中国大陆车牌"),
    LT_ARMPOL2(15,"双层武警车牌"),
    LT_ARMPOL_ZONGDUI(16,"武警总队车牌"),
    LT_ARMPOL2_ZONGDUI(17,"双层武警总队车牌");


    private int type;
    private String desc;
    VzenithPlateType(int type, String desc){
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
        for (VzenithPlateType vzenithPlateType : VzenithPlateType.values()) {
            if(vzenithPlateType.getType()==type){
                return vzenithPlateType.getDesc();
            }
        }
        return "未知";
    }
}