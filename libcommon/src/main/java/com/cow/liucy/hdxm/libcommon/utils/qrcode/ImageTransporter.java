///**
// * Created by rokey on 4/22/18.
// */
//package com.ajb.smartparking.hdxm.libcommon.utils.qrcode;
//
//import android.graphics.Bitmap;
//
//
//import com.ajb.smartparking.hdxm.libcommon.logger.AppLogger;
//import com.ajb.smartparking.hdxm.libcommon.utils.Constants;
//import com.google.zxing.WriterException;
//
//import java.io.BufferedReader;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.Socket;
//
//import io.reactivex.Flowable;
//import io.reactivex.functions.Consumer;
//import io.reactivex.schedulers.Schedulers;
//
//public class ImageTransporter {
//
//    private String filename;
//    private byte[] fileBytes;
//    private String serverIp;
//    private int serverPort;
//    private static int sequeceNum = 0x12345678;
//    private static final String QR_CODE_IMAGE_TMP_PATH = Constants.APP_DEF_PATH ;
//    private static final int IMAGE_WIDTH = 150;
//    private String filePath="";
//
//
//    private byte[] getFileBytes(String filename) {
//        byte[] bytes = null;
//        File file = new File(filename);
//        int filelen = 0;
//        FileInputStream inputStream=null;
//        if (file.exists()) {
//            AppLogger.e("file exist:" + file.length());
//            filelen = (int)file.length();
//            bytes = new byte[filelen];
//            int offset = 0;
//            try {
//                 inputStream = new FileInputStream(file);
//                int readLen = 0;
//                while (offset < filelen) {
//                    readLen = inputStream.read(bytes, offset, filelen-offset);
//                    offset += readLen;
//                }
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }finally {
//                if (inputStream!=null){
//                    try {
//                        inputStream.close();
//                        inputStream=null;
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//        }
//        return bytes;
//    }
//
//    private byte[] getProtocolHeader(int fileLen) {
//        byte[] header = new byte[12];
//        int i = 0;
//        //FIXED HEADER
//        header[i++] = 0x4e;
//        header[i++] = 0x50;
//        //LEN
//
//        header[i++] = (byte) (fileLen & 0xff);
//        header[i++] = (byte) ((fileLen >> 8) & 0xff);
//        header[i++] = (byte) ((fileLen >> 16) & 0xff);
//        header[i++] = (byte) ((fileLen >> 24) & 0xff);
//
//        //SEQ
//        header[i++] = (byte) ((fileLen>>8) & 0xff);
//        header[i++] = (byte) (fileLen & 0xff);
//        //CMD
//        header[i++] = 2;
//        header[i++] = 0;
//        header[i++] = 0;
//        header[i++] = 1;
//        sequeceNum++;
//        return header;
//    }
//
//    private void sendData() {
//        int filelen = fileBytes.length;
//        try {
//            AppLogger.e("begin create socket!");
//            Socket socket = new Socket(serverIp, serverPort);
//            AppLogger.e("socket create success!");
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
//            PrintWriter writer = new PrintWriter(socket.getOutputStream());
//            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
//            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            byte[] header = getProtocolHeader(filelen);
//            outputStream.write(header, 0, header.length);
//            outputStream.flush();
//            outputStream.write(fileBytes, 0, filelen);
//            outputStream.flush();
//            char[] data = new char[100];
//            int len = in.read(data, 0, 100);
//            in.close();
//            outputStream.close();
//            socket.close();
//            AppLogger.e("recevie data: " + len);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//
//        }
//    }
//
//    public ImageTransporter(String filename, String serverIp, int serverPort) {
//        super();
//        this.filename = filename;
//        this.fileBytes = getFileBytes(filename);
//        this.serverIp = serverIp;
//        this.serverPort = serverPort;
//    }
//
//    public ImageTransporter(byte[] data, String serverIp, int serverPort) {
//        super();
//        fileBytes = data;
//        this.serverIp = serverIp;
//        this.serverPort = serverPort;
//    }
//
//    public static void generatePNGFile(String qrCodeString,String filePath) {
//        try {
//            File file = new File(filePath);
//            if (file.exists()) {
//                file.delete();
//            }
//            FileOutputStream outputStream = new FileOutputStream(file);
//            Bitmap bitmap = EncodingHandler.createQRCode(qrCodeString, IMAGE_WIDTH, null);
//            if (bitmap != null && bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
//                outputStream.flush();
//                outputStream.close();
//            }
//
//
//        } catch (WriterException e) {
//            e.printStackTrace();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 传输二维码图片到指定主机
//     * @param serverIp 主机IP
//     * @param serverPort 主机端口
//     * @param qrCodeString 二维码内容
//     */
//    public static void transport(final String serverIp,final int serverPort, String qrCodeString) {
//        ImageTransporter transporter = new ImageTransporter(QR_CODE_IMAGE_TMP_PATH+serverIp+"qrcode.png", serverIp, serverPort);
//        transporter.filePath=QR_CODE_IMAGE_TMP_PATH+serverIp+"qrcode.png";
//        generatePNGFile(qrCodeString,transporter.filePath);
//        Flowable.just(transporter)
//                .observeOn(Schedulers.io())
//                .subscribe(new Consumer<ImageTransporter>() {
//                    @Override
//                    public void accept(ImageTransporter imageTransporter) throws Exception {
//                        imageTransporter.sendData();
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        AppLogger.e(">>>>>>传输二维码图片到指定主机 throwable："+throwable.getMessage());
//                    }
//                });
//
//    }
//
//
//}