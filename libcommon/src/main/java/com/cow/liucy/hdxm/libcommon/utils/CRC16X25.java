package com.cow.liucy.hdxm.libcommon.utils;

/**
 * 循环冗余检验：CRC-16-CCITT查表法
 * CRC-16-CCITT/X25
 * 验证地址：http://www.ip33.com/crc.html
 */
public final class CRC16X25 {
    
    public static void main(String[] args) {
        short result= CRC16X25.GetCrc16(new byte[]{0x31,0x32,0x33,0x34});
        System.out.println(Integer.toHexString(result));
    }
    /**
     * 计算给定长度数据的16位CRC
     * 
     * @param data
     *            要计算CRC的字节数组
     * @return CRC值
     */
    public static short GetCrc16(byte[] data) { // 初始化
        int High = 0xFF; // 高字节
        int Low = 0xFF; // 低字节
        if (data != null) {
            for (byte b : data) {
                int Index = Low ^ b;
                Low = High ^ CRC16TABLE_LO[Index];
                High = CRC16TABLE_HI[Index];
            }
        }
        return (short) (~((High << 8) + Low)); // 取反
    }

    /**
     * 检查给定长度数据的16位CRC是否正确
     * 
     * @param data
     *            要校验的字节数组
     * @return true：正确 false：错误
     * 
     *         <reamrks> 字节数组最后2个字节为校验码，且低字节在前面，高字节在后面 </reamrks>
     */
    public static boolean IsCrc16Good(byte[] data) {
        // 初始化
        int High = 0xFF;
        int Low = 0xFF;
        if (data != null) {
            for (byte b : data) {
                int Index = Low ^ b;
                Low = High ^ CRC16TABLE_LO[Index];
                High = CRC16TABLE_HI[Index];
            }
        }

        return (High == 0xF0 && Low == 0xB8);
    }

    /**
     * CRC16查找表高字节
     */
    private static final int[] CRC16TABLE_HI = { 0x00, 0x11, 0x23, 0x32, 0x46,
            0x57, 0x65, 0x74, 0x8C, 0x9D, 0xAF, 0xBE, 0xCA, 0xDB, 0xE9, 0xF8,
            0x10, 0x01, 0x33, 0x22, 0x56, 0x47, 0x75, 0x64, 0x9C, 0x8D, 0xBF,
            0xAE, 0xDA, 0xCB, 0xF9, 0xE8, 0x21, 0x30, 0x02, 0x13, 0x67, 0x76,
            0x44, 0x55, 0xAD, 0xBC, 0x8E, 0x9F, 0xEB, 0xFA, 0xC8, 0xD9, 0x31,
            0x20, 0x12, 0x03, 0x77, 0x66, 0x54, 0x45, 0xBD, 0xAC, 0x9E, 0x8F,
            0xFB, 0xEA, 0xD8, 0xC9, 0x42, 0x53, 0x61, 0x70, 0x04, 0x15, 0x27,
            0x36, 0xCE, 0xDF, 0xED, 0xFC, 0x88, 0x99, 0xAB, 0xBA, 0x52, 0x43,
            0x71, 0x60, 0x14, 0x05, 0x37, 0x26, 0xDE, 0xCF, 0xFD, 0xEC, 0x98,
            0x89, 0xBB, 0xAA, 0x63, 0x72, 0x40, 0x51, 0x25, 0x34, 0x06, 0x17,
            0xEF, 0xFE, 0xCC, 0xDD, 0xA9, 0xB8, 0x8A, 0x9B, 0x73, 0x62, 0x50,
            0x41, 0x35, 0x24, 0x16, 0x07, 0xFF, 0xEE, 0xDC, 0xCD, 0xB9, 0xA8,
            0x9A, 0x8B, 0x84, 0x95, 0xA7, 0xB6, 0xC2, 0xD3, 0xE1, 0xF0, 0x08,
            0x19, 0x2B, 0x3A, 0x4E, 0x5F, 0x6D, 0x7C, 0x94, 0x85, 0xB7, 0xA6,
            0xD2, 0xC3, 0xF1, 0xE0, 0x18, 0x09, 0x3B, 0x2A, 0x5E, 0x4F, 0x7D,
            0x6C, 0xA5, 0xB4, 0x86, 0x97, 0xE3, 0xF2, 0xC0, 0xD1, 0x29, 0x38,
            0x0A, 0x1B, 0x6F, 0x7E, 0x4C, 0x5D, 0xB5, 0xA4, 0x96, 0x87, 0xF3,
            0xE2, 0xD0, 0xC1, 0x39, 0x28, 0x1A, 0x0B, 0x7F, 0x6E, 0x5C, 0x4D,
            0xC6, 0xD7, 0xE5, 0xF4, 0x80, 0x91, 0xA3, 0xB2, 0x4A, 0x5B, 0x69,
            0x78, 0x0C, 0x1D, 0x2F, 0x3E, 0xD6, 0xC7, 0xF5, 0xE4, 0x90, 0x81,
            0xB3, 0xA2, 0x5A, 0x4B, 0x79, 0x68, 0x1C, 0x0D, 0x3F, 0x2E, 0xE7,
            0xF6, 0xC4, 0xD5, 0xA1, 0xB0, 0x82, 0x93, 0x6B, 0x7A, 0x48, 0x59,
            0x2D, 0x3C, 0x0E, 0x1F, 0xF7, 0xE6, 0xD4, 0xC5, 0xB1, 0xA0, 0x92,
            0x83, 0x7B, 0x6A, 0x58, 0x49, 0x3D, 0x2C, 0x1E, 0x0F };

    /**
     * CRC16查找表低字节
     */
    private static final int[] CRC16TABLE_LO = { 0x00, 0x89, 0x12, 0x9B, 0x24,
            0xAD, 0x36, 0xBF, 0x48, 0xC1, 0x5A, 0xD3, 0x6C, 0xE5, 0x7E, 0xF7,
            0x81, 0x08, 0x93, 0x1A, 0xA5, 0x2C, 0xB7, 0x3E, 0xC9, 0x40, 0xDB,
            0x52, 0xED, 0x64, 0xFF, 0x76, 0x02, 0x8B, 0x10, 0x99, 0x26, 0xAF,
            0x34, 0xBD, 0x4A, 0xC3, 0x58, 0xD1, 0x6E, 0xE7, 0x7C, 0xF5, 0x83,
            0x0A, 0x91, 0x18, 0xA7, 0x2E, 0xB5, 0x3C, 0xCB, 0x42, 0xD9, 0x50,
            0xEF, 0x66, 0xFD, 0x74, 0x04, 0x8D, 0x16, 0x9F, 0x20, 0xA9, 0x32,
            0xBB, 0x4C, 0xC5, 0x5E, 0xD7, 0x68, 0xE1, 0x7A, 0xF3, 0x85, 0x0C,
            0x97, 0x1E, 0xA1, 0x28, 0xB3, 0x3A, 0xCD, 0x44, 0xDF, 0x56, 0xE9,
            0x60, 0xFB, 0x72, 0x06, 0x8F, 0x14, 0x9D, 0x22, 0xAB, 0x30, 0xB9,
            0x4E, 0xC7, 0x5C, 0xD5, 0x6A, 0xE3, 0x78, 0xF1, 0x87, 0x0E, 0x95,
            0x1C, 0xA3, 0x2A, 0xB1, 0x38, 0xCF, 0x46, 0xDD, 0x54, 0xEB, 0x62,
            0xF9, 0x70, 0x08, 0x81, 0x1A, 0x93, 0x2C, 0xA5, 0x3E, 0xB7, 0x40,
            0xC9, 0x52, 0xDB, 0x64, 0xED, 0x76, 0xFF, 0x89, 0x00, 0x9B, 0x12,
            0xAD, 0x24, 0xBF, 0x36, 0xC1, 0x48, 0xD3, 0x5A, 0xE5, 0x6C, 0xF7,
            0x7E, 0x0A, 0x83, 0x18, 0x91, 0x2E, 0xA7, 0x3C, 0xB5, 0x42, 0xCB,
            0x50, 0xD9, 0x66, 0xEF, 0x74, 0xFD, 0x8B, 0x02, 0x99, 0x10, 0xAF,
            0x26, 0xBD, 0x34, 0xC3, 0x4A, 0xD1, 0x58, 0xE7, 0x6E, 0xF5, 0x7C,
            0x0C, 0x85, 0x1E, 0x97, 0x28, 0xA1, 0x3A, 0xB3, 0x44, 0xCD, 0x56,
            0xDF, 0x60, 0xE9, 0x72, 0xFB, 0x8D, 0x04, 0x9F, 0x16, 0xA9, 0x20,
            0xBB, 0x32, 0xC5, 0x4C, 0xD7, 0x5E, 0xE1, 0x68, 0xF3, 0x7A, 0x0E,
            0x87, 0x1C, 0x95, 0x2A, 0xA3, 0x38, 0xB1, 0x46, 0xCF, 0x54, 0xDD,
            0x62, 0xEB, 0x70, 0xF9, 0x8F, 0x06, 0x9D, 0x14, 0xAB, 0x22, 0xB9,
            0x30, 0xC7, 0x4E, 0xD5, 0x5C, 0xE3, 0x6A, 0xF1, 0x78 };
}