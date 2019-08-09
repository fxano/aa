package com.xl.admin.idandfr.utils;


import android.util.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by ShawnXiao on 2018/9/5.
 */

public class ConnectUtil {

    //外网地址
    public static  String URL_NAME = "http://118.250.161.108:1440/hengyangshi/android/";

    //上传身份证信息接口
    public static final String UPLOAD_INFO = "uploudRecord.shtml";


    public static final String LOGIN = "login.shtml";

    public static final String REQUEST_INFO = "findBySerialNumber.shtml";


    /**
     * 根据IP地址获取MAC地址
     *
     * @return
     */
    public static String getLocalMacAddressFromIp() {
        String strMacAddr = null;
        try {
            //获得IpD地址
            InetAddress ip = getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
        } catch (Exception e) {

        }

        return strMacAddr;
    }


    /**
     * 获取移动设备本地IP
     *
     * @return
     */
    private static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            //列举
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface.getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {//是否还有元素
                NetworkInterface ni = (NetworkInterface) en_netInterface.nextElement();//得到下一个元素
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();//得到一个ip地址的列举
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1)
                        break;
                    else
                        ip = null;
                }

                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {

            e.printStackTrace();
        }
        return ip;
    }


    public static void savePicture(StringBuffer base64Code)throws Exception{
       byte[] b = Base64.decode(base64Code.toString().getBytes() , Base64.DEFAULT);
        //生成jpeg图片
            File file = new File("/storage/emulated/0");
            if (!file.exists()) {//判断文件目录是否存在，如不存在则新建
                file.mkdirs();
                file.createNewFile();
            }
//        fileNames[0] = fileName;
//        fileNames[1] = imgFilePath.replaceAll("/", "@");
//        fileNames[2] = fictitiousPath;
        OutputStream out = new FileOutputStream(file.getName());
        out.write(b);
        out.flush();
        out.close();
    }

}
