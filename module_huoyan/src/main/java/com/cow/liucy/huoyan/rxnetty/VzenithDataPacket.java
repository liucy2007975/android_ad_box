package com.cow.liucy.huoyan.rxnetty;







import com.cow.liucy.hdxm.libcommon.logger.AppLogger;
import com.cow.liucy.hdxm.libcommon.utils.ConvertUtil;

import java.io.ByteArrayOutputStream;

/**
 * Created by anjubao on 2018/6/25.
 * 火眼TCP协议
 *
 *      'V'     'Z'    包类型     包序号    数据包长度     data
 *
 *
 * /**
 * server端口： 8131
 * 'V'     'Z'    type     seq    dataLen     data
 * 1       1      1        1       4          N
 * dataLen = (data).length
 * 数据采用小端数据传输
 *
 *
 *
 */

public class VzenithDataPacket {

    /**
     * 数据包头 ，长度8个字节：  0	 1	    2 		3         4 5 6 7    ...
     *                        'V'	'Z'	数据包类型	包序号	 数据长度    数据
     */
    private byte[] header=new byte[8];
    /**
     * 数据包标识,2个字节 固定为'V','Z'
     */
    private byte[] first={'V', 'Z'};
    /**
     * 数据包类型
     */
    private byte type;
    /**
     * 数据包序号
     */
    private byte seq;
    /**
     * 数据长度
     */
    private byte[] len=new byte[4];

    /**
     * 数据部分
     */
    private byte[] data;

    public VzenithDataPacket(){

    }

    /**
     * 根据数据构造包结构
     * @param vzenithDataPacket
     */
    public VzenithDataPacket(byte[] vzenithDataPacket){
        if (vzenithDataPacket.length>=8){
            //数据包头赋值
            System.arraycopy(vzenithDataPacket,0,header,0,header.length);
            //包类型赋值
            type=vzenithDataPacket[2];
            //包序号赋值
            seq=vzenithDataPacket[3];
            //包长度赋值
            System.arraycopy(vzenithDataPacket,4,len,0,len.length);
            //数据部分赋值  判断是否有数据?
            int length= ConvertUtil.bytesToIntBig(len);
//            AppLogger.e(">>>>>length:>>>"+length);
            if (length>0){
                //Data部分有数据
                data=new byte[length];
                System.arraycopy(vzenithDataPacket,header.length,data,0,data.length);
            }
        }else {
            AppLogger.e(">>>>>数据包格式错误");
        }
    }

    /**
     * 将各字节合并为header
     * @return
     */
    public byte[] parseBytes(){
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(first,0,first.length);
            bos.write(type);
            bos.write(seq);
            bos.write(len);
            bos.flush();
            setHeader(bos.toByteArray());
        }catch (Exception e){

        }
        return getHeader();
    }

    /**
     * 获得完整的数据包
     * @return
     */
    public byte[] getPacket(){
        if (data!=null) {
            return ConvertUtil.byteMerger(parseBytes(),data);
        }else {
            return parseBytes();
        }
    }


    public byte[] getHeader() {
        return header;
    }

    public void setHeader(byte[] header) {
        this.header = header;
    }

    public byte[] getLen() {
        return len;
    }

    public void setLen(byte[] len) {
        this.len = len;
    }



    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getFirst() {
        return first;
    }

    public void setFirst(byte[] first) {
        this.first = first;
    }


}
