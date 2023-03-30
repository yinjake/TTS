package com.freelycar.voice.util;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.blankj.utilcode.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import cn.hutool.core.util.StrUtil;


public class MyLogUtils {
    private static final String TAG = "MyLogUtils";
    private static int sConsoleFilter = 2;
    private static int sFileFilter = 2;
    private static int sStackDeep = 1;

    private static final String ARGS = "args";
    private static final String BOTTOM_BORDER = "└────────────────────────────────────────────────────────────────────────────────────────────────────────────────";
    private static final String BOTTOM_CORNER = "└";
    public static final int V = 2;
    public static final int D = 3;
    public static final int I = 4;
    public static final int W = 5;
    public static final int E = 6;
    public static final int A = 7;
    private static final int FILE = 16;
    private static final int JSON = 32;
    private static final int XML = 48;
    private static final String LEFT_BORDER = "│ ";
    private static final int MAX_LEN = 4000;
    private static final String MIDDLE_BORDER = "├┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄";
    private static final String MIDDLE_CORNER = "├";
    private static final String MIDDLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄";
    private static final String NOTHING = "log nothing";
    private static final String NULL = "null";
    private static final String SIDE_DIVIDER = "────────────────────────────────────────────────────────";
    private static final String TOP_BORDER = "┌────────────────────────────────────────────────────────────────────────────────────────────────────────────────";
    private static final String TOP_CORNER = "┌";
    private static String sDefaultDir;
    public static String sDir;
    private static ExecutorService sExecutor;
    private static final char[] GRADE = {'V', 'D', 'I', 'W', 'E', 'A'};
    private static final String FILE_SEP = System.getProperty("file.separator");
    private static final String LINE_SEP = System.getProperty("line.separator");
    @SuppressLint("SimpleDateFormat")
    private static final Format FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    //new SimpleDateFormat(“yyyy-MM-dd’T’HH:mm:ss”);
    private static final Config CONFIG = new Config();
    private static String sFilePrefix = "util";
    private static String sGlobalTag = null;

    private static boolean sLogSwitch = true;
    private static boolean sLog2ConsoleSwitch = true;
    private static boolean sTagIsSpace = true;
    private static boolean sLogHeadSwitch = true;
    private static boolean sLog2FileSwitch = true;
    private static boolean sLogBorderSwitch = true;
    static boolean res = false;

    public static Config getConfig() {
        return CONFIG;
    }

    public static void v(Object... objArr) {
        log(V, sGlobalTag, objArr);
    }

    public static void vTag(String str, Object... objArr) {
        log(V, str, objArr);
    }

    public static void d(Object... objArr) {
        log(D, sGlobalTag, objArr);
    }

    public static void dTag(String str, Object... objArr) {
        log(D, str, objArr);
    }

    public static void i(Object... objArr) {
        log(I, sGlobalTag, objArr);
    }

    public static void iTag(String str, Object... objArr) {
        log(I, str, objArr);
    }

    public static void w(Object... objArr) {
        log(W, sGlobalTag, objArr);
    }

    public static void wTag(String str, Object... objArr) {
        log(W, str, objArr);
    }

    public static void e(Object... objArr) {
        log(E, sGlobalTag, objArr);
    }

    public static void eTag(String str, Object... objArr) {
        log(E, str, objArr);
    }

    public static void a(Object... objArr) {
        log(A, sGlobalTag, objArr);
    }

    public static void aTag(String str, Object... objArr) {
        log(A, str, objArr);
    }

    public static void file(Object obj) {
        log(19, sGlobalTag, obj);
    }

    public static void file(int i, Object obj) {
        log(i | FILE, sGlobalTag, obj);
    }

    public static void file(String str, Object obj) {
        log(19, str, obj);
    }

    public static void file(int i, String str, Object obj) {
        log(i | FILE, str, obj);
    }

    public static void json(String str) {
        log(36, sGlobalTag, str);
    }

    public static void json(int i, String str) {
        log(i | JSON, sGlobalTag, str);
    }

    public static void json(String str, String str2) {
        log(35, str, str2);
    }

    public static void json(int i, String str, String str2) {
        log(i | JSON, str, str2);
    }

    public static void xml(String str) {
        log(51, sGlobalTag, str);
    }

    public static void xml(int i, String str) {
        log(i | XML, sGlobalTag, str);
    }

    public static void xml(String str, String str2) {
        log(51, str, str2);
    }

    public static void xml(int i, String str, String str2) {
        log(i | XML, str, str2);
    }

    private static void log(int level, String str, Object... objArr) {
        if (sLogSwitch) {
            if (!sLog2ConsoleSwitch && !sLog2FileSwitch) {
                return;
            }
            int consoleLevel = level & 15;
            int fileLevel = level & 240;
            if (consoleLevel < sConsoleFilter && consoleLevel < sFileFilter) {
                return;
            }
            TagHead processTagAndHead = processTagAndHead(str);
            String processBody = processBody(fileLevel, objArr);
            if (sLog2ConsoleSwitch && consoleLevel >= sConsoleFilter) {
                Log.e(TAG,"输出到控制台  ");
                print2Console(consoleLevel, processTagAndHead.tag, processTagAndHead.consoleHead, processBody);
            }
            if ((!sLog2FileSwitch && fileLevel != FILE) || consoleLevel < sFileFilter) {
                return;
            }
            print2File(consoleLevel, processTagAndHead.tag, processTagAndHead.fileHead + processBody);
        }
    }

    private static TagHead processTagAndHead(String str) {
        String str2;
        String substring;
        String str3;
        String name = null;
        if (!sTagIsSpace && !sLogHeadSwitch) {
            str3 = sGlobalTag;
        } else {
            StackTraceElement[] stackTrace = new Throwable().getStackTrace();
            StackTraceElement stackTraceElement = stackTrace[3];
            String fileName = stackTraceElement.getFileName();
            if (fileName == null) {
                substring = stackTraceElement.getClassName();
                String[] split = substring.split("\\.");
                if (split.length > 0) {
                    substring = split[split.length - 1];
                }
                int indexOf = substring.indexOf(36);
                if (indexOf != -1) {
                    substring = substring.substring(0, indexOf);
                }
                str2 = substring + ".java";
            } else {
                int indexOf2 = fileName.indexOf(46);
                str2 = fileName;
                substring = indexOf2 == -1 ? fileName : fileName.substring(0, indexOf2);
            }
            if (!sTagIsSpace || !isSpace(str)) {
                substring = str;
            }
            if (sLogHeadSwitch) {
                String formatter = new Formatter().format("%s, %s(%s:%d)", Thread.currentThread().getName(), stackTraceElement.getMethodName(), str2, Integer.valueOf(stackTraceElement.getLineNumber())).toString();
                String str4 = " [" + formatter + "]: ";
                int i = sStackDeep;
                if (i <= 1) {
                    return new TagHead(substring, new String[]{formatter}, str4);
                }
                int min = Math.min(i, stackTrace.length - 3);
                String[] strArr = new String[min];
                strArr[0] = formatter;
                String formatter2 = new Formatter().format("%" + (name.length() + 2) + "s", "").toString();
                for (int i2 = 1; i2 < min; i2++) {
                    StackTraceElement stackTraceElement2 = stackTrace[i2 + 3];
                    strArr[i2] = new Formatter().format("%s%s(%s:%d)", formatter2, stackTraceElement2.getMethodName(), stackTraceElement2.getFileName(), Integer.valueOf(stackTraceElement2.getLineNumber())).toString();
                }
                return new TagHead(substring, strArr, str4);
            }
            str3 = substring;
        }
        return new TagHead(str3, null, ": ");
    }

    private static String processBody(int i, Object... objArr) {
        String str = null;
        if (objArr != null) {
            if (objArr.length == 1) {
                Object obj = objArr[0];
                if (obj != null) {
                    str = obj.toString();
                }
                if (i == JSON) {
                    assert str != null;
                    str = formatJson(str);
                } else if (i == XML) {
                    str = formatXml(str);
                }
            } else {
                StringBuilder sb = new StringBuilder();
                int length = objArr.length;
                for (int i2 = 0; i2 < length; i2++) {
                    Object obj2 = objArr[i2];
                    sb.append(ARGS).append(StrUtil.BRACKET_START).append(i2).append(StrUtil.BRACKET_END).append(" = ").append(obj2 == null ? str : obj2.toString()).append(LINE_SEP);
                }
                str = sb.toString();
            }
        }
        return (str != null ? str.length() : 0) == 0 ? NOTHING : str;
    }

    private static String formatJson(String str) {
        try {
            if (str.startsWith(StrUtil.DELIM_START)) {
                str = new JSONObject(str).toString(4);
            } else if (str.startsWith(StrUtil.BRACKET_START)) {
                str = new JSONArray(str).toString(4);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }

    private static String formatXml(String str) {
        try {
            StreamSource streamSource = new StreamSource(new StringReader(str));
            StreamResult streamResult = new StreamResult(new StringWriter());
            Transformer newTransformer = TransformerFactory.newInstance().newTransformer();
            newTransformer.setOutputProperty("indent", "yes");
            newTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            newTransformer.transform(streamSource, streamResult);
            return streamResult.getWriter().toString().replaceFirst(">", ">" + LINE_SEP);
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }

    private static void print2Console(int i, String str, String[] strArr, String str2) {
        printBorder(i, str, true);
        printHead(i, str, strArr);
        printMsg(i, str, str2);
        printBorder(i, str, false);
    }

    private static void printBorder(int i, String str, boolean z) {
        if (sLogBorderSwitch) {
            Log.println(i, str, z ? TOP_BORDER : BOTTOM_BORDER);
        }
    }

    private static void printHead(int i, String str, String[] strArr) {
        if (strArr != null) {
            for (String str2 : strArr) {
                if (sLogBorderSwitch) {
                    str2 = LEFT_BORDER + str2;
                }
                Log.println(i, str, str2);
            }
            if (!sLogBorderSwitch) {
                return;
            }
            Log.println(i, str, MIDDLE_BORDER);
        }
    }

    private static void printMsg(int i, String str, String str2) {
        int length = str2.length();
        int i2 = length / MAX_LEN;
        if (i2 <= 0) {
            printSubMsg(i, str, str2);
            return;
        }
        int i3 = 0;
        int i4 = 0;
        while (i3 < i2) {
            int i5 = i4 + MAX_LEN;
            printSubMsg(i, str, str2.substring(i4, i5));
            i3++;
            i4 = i5;
        }
        if (i4 == length) {
            return;
        }
        printSubMsg(i, str, str2.substring(i4, length));
    }

    private static void printSubMsg(int i, String str, String str2) {
        if (!sLogBorderSwitch) {
            Log.println(i, str, str2);
            return;
        }
        String[] split = new String[0];
        if (LINE_SEP != null) {
            split = str2.split(LINE_SEP);
        }
        int length = split.length;
        for (String s : split) {
            Log.println(i, str, LEFT_BORDER + s);
        }
    }

    private static void print2File(int i, String str, String str2) {
        String format = FORMAT.format(new Date(System.currentTimeMillis()));
        String substring = format.substring(0, 5);
        String substring2 = format.substring(6);
        StringBuilder sb = new StringBuilder();
        String str3 = sDir;
        if (str3 == null) {
            str3 = sDefaultDir;
        }
        String sb2 = sb.append(str3).append(sFilePrefix).append(StrUtil.DASHED).append(substring).append(".txt").toString();
        if (!createOrExistsFile(sb2)) {
            Log.d(str, "print2File  createOrExistsFile ");
            return;
        }
        StringBuilder sb3 = new StringBuilder();
        sb3.append(format).append(StrUtil.COLON).append(GRADE[i - 2]).append(StrUtil.SLASH).append(str).append(str2).append(LINE_SEP);
        if (!input2File(sb3.toString(), sb2)) {
            return;
        }
        Log.d(str, "log to " + sb2 + " success!");
    }

    private static boolean createOrExistsFile(String str) {
        File file = new File(str);
        if (file.exists()) {
            return file.isFile();
        }
        if (!createOrExistsDir(file.getParentFile())) {
            return false;
        }
        try {
            boolean createNewFile = file.createNewFile();
            if (createNewFile) {
                printDeviceInfo(str);
            }
            return createNewFile;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void printDeviceInfo(String str) {
        String str2 = "";
        int i = 0;
        try {
            PackageInfo packageInfo = Utils.getApp().getPackageManager().getPackageInfo(Utils.getApp().getPackageName(), 0);
            if (packageInfo != null) {
                str2 = packageInfo.versionName;
                i = packageInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        input2File("************* Log Head ****************\nDevice Manufacturer: " + Build.MANUFACTURER + "\nDevice Model       : " + Build.MODEL + "\nAndroid Version    : " + Build.VERSION.RELEASE + "\nAndroid SDK        : " + Build.VERSION.SDK_INT + "\nApp VersionName    : " + str2 + "\nApp VersionCode    : " + i + "\n************* Log Head ****************\n\n", str);
    }


    private static boolean createOrExistsDir(File file) {
        return file != null && (!file.exists() ? file.mkdirs() : file.isDirectory());
    }


    public static boolean isSpace(String str) {
        if (str == null) {
            return true;
        }
        int length = str.length();
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean input2File(final String str, final String str2) {
        if (sExecutor == null) {
            sExecutor = Executors.newSingleThreadExecutor();
        }
        try {
            return ((Boolean) sExecutor.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    BufferedWriter bufferedWriter;
                    try {
                        bufferedWriter = new BufferedWriter(new FileWriter(str2, true));
                    } catch (IOException e2) {
                        bufferedWriter = null;
                    } catch (Throwable th2) {
                        bufferedWriter = null;
                        throw th2;
                    }
                    try {
                        try {
                            bufferedWriter.write(str);
                            try {
                                bufferedWriter.close();
                            } catch (IOException e3) {
                                e3.printStackTrace();
                            }
                            return true;
                        } catch (Throwable th3) {
                            if (bufferedWriter != null) {
                                try {
                                    bufferedWriter.close();
                                } catch (IOException e4) {
                                    e4.printStackTrace();
                                }
                            }
                            throw th3;
                        }
                    } catch (IOException e5) {
                        e5.printStackTrace();
                        if (bufferedWriter != null) {
                            try {
                                bufferedWriter.close();
                            } catch (IOException e6) {
                                e6.printStackTrace();
                            }
                        }
                        return false;
                    }
                }
            }).get()).booleanValue();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (ExecutionException e2) {
            e2.printStackTrace();
            return false;
        }
    }

    private static boolean input2File1(final String str, final String str2) {
        Log.d(TAG, "input2File test1:    " + str + " - ");
        Log.d(TAG, "input2File test2:    " +str2);
        if (sExecutor == null) {
            sExecutor = Executors.newSingleThreadExecutor();
        }
        try {
            return sExecutor.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    try {
                        BufferedWriter buf = new BufferedWriter(new FileWriter(str2, true));
                        buf.append(str);
                        buf.newLine();
                        buf.close();
                        res = true;
                        Log.d(TAG, "input2File file result:" + res);
                    } catch (IOException e) {
                        res = false;
                        e.printStackTrace();
                    }
                    return res;
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }



    public static class Config {
        private Config() {
            if (MyLogUtils.sDefaultDir != null) {
                return;
            }
            if (!"mounted".equals(Environment.getExternalStorageState()) || Utils.getApp().getExternalCacheDir() == null) {
                MyLogUtils.sDefaultDir = Utils.getApp().getCacheDir() + MyLogUtils.FILE_SEP + "log" + MyLogUtils.FILE_SEP;
            } else {
                MyLogUtils.sDefaultDir = Utils.getApp().getExternalCacheDir() + MyLogUtils.FILE_SEP + "log" + MyLogUtils.FILE_SEP;
            }
        }

        public Config setLogSwitch(boolean z) {
            MyLogUtils.sLogSwitch = z;
            return this;
        }

        public Config setConsoleSwitch(boolean z) {
            MyLogUtils.sLog2ConsoleSwitch = z;
            return this;
        }

        public Config setGlobalTag(String str) {
            if (MyLogUtils.isSpace(str)) {
                MyLogUtils.sGlobalTag = "";
                 MyLogUtils.sTagIsSpace = true;
            } else {
                 MyLogUtils.sGlobalTag = str;
                MyLogUtils.sTagIsSpace = false;
            }
            return this;
        }

        public Config setLogHeadSwitch(boolean z) {
            MyLogUtils.sLogHeadSwitch = z;
            return this;
        }

        public Config setLog2FileSwitch(boolean z) {
             MyLogUtils.sLog2FileSwitch = z;
            return this;
        }

        public Config setDir(String str) {
            if (!MyLogUtils.isSpace(str)) {
                if (!str.endsWith(MyLogUtils.FILE_SEP)) {
                    str = str + MyLogUtils.FILE_SEP;
                }
                MyLogUtils.sDir = str;
            } else {
                MyLogUtils.sDir = null;
            }
            return this;
        }

        public Config setDir(File file) {
            MyLogUtils.sDir = file == null ? null : file.getAbsolutePath() + MyLogUtils.FILE_SEP;
            return this;
        }

        public Config setFilePrefix(String str) {
            if (MyLogUtils.isSpace(str)) {
                MyLogUtils.sFilePrefix = "util";
            } else {
                MyLogUtils.sFilePrefix = str;
            }
            return this;
        }

        public Config setBorderSwitch(boolean z) {
            MyLogUtils.sLogBorderSwitch = z;
            return this;
        }

        public Config setConsoleFilter(int i) {
            MyLogUtils.sConsoleFilter = i;
            return this;
        }

        public Config setFileFilter(int i) {
             MyLogUtils.sFileFilter = i;
            return this;
        }

        public Config setStackDeep(int i) {
            MyLogUtils.sStackDeep = i;
            return this;
        }

        public String toString() {
            return "switch: " + MyLogUtils.sLogSwitch + MyLogUtils.LINE_SEP + "console: " + MyLogUtils.sLog2ConsoleSwitch + MyLogUtils.LINE_SEP + "tag: " + (MyLogUtils.sTagIsSpace ? "null" : MyLogUtils.sGlobalTag) + MyLogUtils.LINE_SEP + "head: " + MyLogUtils.sLogHeadSwitch + MyLogUtils.LINE_SEP + "file: " + MyLogUtils.sLog2FileSwitch + MyLogUtils.LINE_SEP + "dir: " + (MyLogUtils.sDir == null ? MyLogUtils.sDefaultDir : MyLogUtils.sDir) + MyLogUtils.LINE_SEP + "filePrefix: " + MyLogUtils.sFilePrefix + MyLogUtils.LINE_SEP + "border: " + MyLogUtils.sLogBorderSwitch + MyLogUtils.LINE_SEP + "consoleFilter: " + MyLogUtils.GRADE[MyLogUtils.sConsoleFilter - 2] + MyLogUtils.LINE_SEP + "fileFilter: " + MyLogUtils.GRADE[MyLogUtils.sFileFilter - 2] + MyLogUtils.LINE_SEP + "stackDeep: " + MyLogUtils.sStackDeep;
        }
    }


    public static class TagHead {
        String[] consoleHead;
        String fileHead;
        String tag;

        TagHead(String str, String[] strArr, String str2) {
            this.tag = str;
            this.consoleHead = strArr;
            this.fileHead = str2;
        }
    }

    public static void printStackTrace(Throwable th) {
        th.printStackTrace();
    }

    public static void printd(String str, String str2) {
        int length = 2001 - str.length();
        while (str2.length() > length) {
            Log.d(str, str2.substring(0, length));
            str2 = str2.substring(length);
        }
        Log.d(str, str2);
    }
}
