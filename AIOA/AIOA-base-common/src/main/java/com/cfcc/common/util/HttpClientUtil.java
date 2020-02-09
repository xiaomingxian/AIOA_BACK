package com.cfcc.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpClientUtil {

    private static final String BOUNDARY = "-------45962402127348";
    private static final String FILE_ENCTYPE = "multipart/form-data";
    public static void main(String[] args) {
        String url = "http://192.168.1.118:8080/AIOA/papertitle/oaTemplate/receiveStream";
        //文件列表，搞了三个本地文件
        List<String> fileList = new ArrayList<>();
        fileList.add("D:\\upFiles\\业务功能_1572707314973.doc");
        fileList.add("D:\\upFiles\\业务功能_1572707314974.doc");
        fileList.add("D:\\upFiles\\业务功能_1572707314975.doc");

        //json字符串，模拟了一个，传图片名字吧
        Map<String,Object> busData = new HashMap<>();
        busData.put("i_id",1120);
        busData.put("i_bus_function",6);
        busData.put("i_bus_model",666);
        String jsonString = JSON.toJSONString(busData);
        JSONObject json = JSONObject.parseObject(jsonString);
        doPostFileStreamAndJsonObj(url, fileList, json);
    }

    public static String doPostFileStreamAndJsonObj(String url, List<String> fileList, JSONObject json) {
        String result = "200";//请求返回参数
        String jsonString = json.toJSONString();//获得jsonstirng,或者toString都可以，只要是json格式，给了别人能解析成json就行
//        System.out.println("================");
//        System.out.println(xml);//可以打印出来瞅瞅
//        System.out.println("================");
        try {
            //开始设置模拟请求的参数，额，不一个个介绍了，根据需要拿
            String boundary = "------WebKitFormBoundaryUey8ljRiiZqhZHBu";
            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("connection", "Keep-Alive");
            //这里模拟的是火狐浏览器，具体的可以f12看看请求的user-agent是什么
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Charsert", "UTF-8");
            //这里的content-type要设置成表单格式，模拟ajax的表单请求
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            // 指定流的大小，当内容达到这个值的时候就把流输出
            conn.setChunkedStreamingMode(10240000);
            //定义输出流，有什么数据要发送的，直接后面append就可以，记得转成byte再append
            OutputStream out = new DataOutputStream(conn.getOutputStream());
            byte[] end_data = ("\r\n--" + boundary + "--\r\n").getBytes();// 定义最后数据分隔线

            StringBuilder sb = new StringBuilder();
            //添加form属性
            sb.append("--");
            sb.append(boundary);
            sb.append("\r\n");
            //这里存放要传输的参数，name = xml
            sb.append("Content-Disposition: form-data; name=\"JsonBusData\"");
            sb.append("\r\n\r\n");
            //把要传的json字符串放进来
            sb.append(jsonString);
            out.write(sb.toString().getBytes("utf-8"));
            out.write("\r\n".getBytes("utf-8"));

            int leng = fileList.size();
            for (int i = 0; i < leng; i++) {
                File file = new File(fileList.get(i));
                if(file.exists()){
                    sb = new StringBuilder();
                    sb.append("--");
                    sb.append(boundary);
                    sb.append("\r\n");
                    //这里的参数啥的是我项目里对方接收要用到的，具体的看你的项目怎样的格式
                    sb.append("Content-Disposition: form-data;name=\"File"
                            + "\";filename=\"" + file.getName() + "\"\r\n");
                    //这里拼接个fileName，方便后面用第一种方式接收（如果是纯文件，不带其他参数，就可以不用这个了，因为Multipart可以直接解析文件）
                    sb.append("FileName:"+ file.getName() + "\r\n");
                    //发送文件是以流的方式发送，所以这里的content-type是octet-stream流
                    sb.append("Content-Type:application/octet-stream\r\n\r\n");
                    byte[] data = sb.toString().getBytes();
                    out.write(data);
                    DataInputStream in = new DataInputStream(new FileInputStream(file));
                    int bytes = 0;
                    byte[] bufferOut = new byte[1024];
                    while ((bytes = in.read(bufferOut)) != -1) {
                        out.write(bufferOut, 0, bytes);
                    }
                    int j = i + 1;
                    if (leng > 1 && j != leng) {
                        out.write("\r\n".getBytes()); // 多个文件时，二个文件之间加入这个
                    }
                    in.close();
                }else{
                    System.out.println("没有发现文件");
                }
            }
            //发送流
            out.write(end_data);
            out.flush();
            out.close();
            // 定义BufferedReader输入流来读取URL的响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                result += line;
            }
//            System.out.println("================");
//            System.out.println(result.toString());//可以把结果打印出来瞅瞅
//            System.out.println("================");
            //后面可以对结果进行解析（如果返回的是格式化的数据的话）
        } catch (Exception e) {
            System.out.println("发送POST请求出现异常！" + e);
            e.printStackTrace();
        }
        return result;
    }


    /**
     *
     * @param urlStr http请求路径
     * @param params 请求参数
     * @param images 上传文件
     * @return
     */
    public static InputStream post(String urlStr, Map<String, String> params,
                                   Map<String, File> files) {
        InputStream is = null;

        try {
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5000);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestMethod("POST");
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type", FILE_ENCTYPE + "; boundary="
                    + BOUNDARY);

            StringBuilder sb = null;
            DataOutputStream dos = new DataOutputStream(con.getOutputStream());;
            if (params != null) {
                sb = new StringBuilder();
                for (String s : params.keySet()) {
                    sb.append("--");
                    sb.append(BOUNDARY);
                    sb.append("\r\n");
                    sb.append("Content-Disposition: form-data; name=\"");
                    sb.append(s);
                    sb.append("\"\r\n\r\n");
                    sb.append(params.get(s));
                    sb.append("\r\n");
                }

                dos.write(sb.toString().getBytes());
            }

            if (files != null) {
                for (String s : files.keySet()) {
                    File f = files.get(s);
                    sb = new StringBuilder();
                    sb.append("--");
                    sb.append(BOUNDARY);
                    sb.append("\r\n");
                    sb.append("Content-Disposition: form-data; name=\"");
                    sb.append(s);
                    sb.append("\"; filename=\"");
                    sb.append(f.getName());
                    sb.append("\"\r\n");
                    sb.append("Content-Type: application/plain");//这里注意！如果上传的不是图片，要在这里改文件格式，比如txt文件，这里应该是text/plain
                    sb.append("\r\n\r\n");
                    dos.write(sb.toString().getBytes());

                    FileInputStream fis = new FileInputStream(f);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fis.read(buffer)) != -1) {
                        dos.write(buffer, 0, len);
                    }
                    dos.write("\r\n".getBytes());
                    fis.close();
                }

                sb = new StringBuilder();
                sb.append("--");
                sb.append(BOUNDARY);
                sb.append("--\r\n");
                dos.write(sb.toString().getBytes());
            }
            dos.flush();

            if (con.getResponseCode() == 200)
                is = con.getInputStream();

            dos.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return is;
    }
}
