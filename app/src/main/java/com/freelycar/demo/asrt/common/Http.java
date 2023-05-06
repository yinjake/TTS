package com.freelycar.demo.asrt.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;


public class Http {

    public static String sendGet(String url, String param){
        String result = "";
        String urlName = url + "?" + param;
        try{
            URL realUrl = new URL(urlName);

            URLConnection conn = realUrl.openConnection();

            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

            conn.connect();

            Map<String,List<String>> map = conn.getHeaderFields();

            for (String key : map.keySet()) {
                System.out.println(key + "-->" + map.get(key));
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("error" + e);
            e.printStackTrace();
        }
        return result;


    }


    public static String sendPost(String url, String param, String contentType){
        String result = "";
        try{
            URL realUrl = new URL(url);

            URLConnection conn =  realUrl.openConnection();

            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("content-type", contentType); // "application/json"

            conn.setDoOutput(true);
            conn.setDoInput(true);

            PrintWriter out = new PrintWriter(conn.getOutputStream());

            out.print(param);

            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line;
            if((line = in.readLine()) != null) {
                result += line;
            }
            while ((line = in.readLine()) != null) {
                result += "\n" + line;
            }
        } catch (Exception e) {
            System.out.println(" ... " + e);
            e.printStackTrace();
        }
        return result;
    }

    //public static void main(String[] args) throws Exception{
    // GET
    //    String s = HttpRequest.sendGet("http://127.0.0.1:8080/index",null);
    //    System.out.println(s);
    // POST
    //    String s1 = HttpRequest.sendPost("http://localhost:8080/addComment", "questionId=1&content=美丽人生");
    //    System.out.println(s1);
    //}
}
