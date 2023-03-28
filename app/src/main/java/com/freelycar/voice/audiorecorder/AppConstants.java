package com.freelycar.voice.audiorecorder;

public class AppConstants {
    static String TAG = "com.freelycar.music"; // 应用程序标记
    static String INTENT_ACTION_PREVIOUS = TAG + ".intent.action.PREVIOUS"; // 广播频道常量：播放上一首
    static String INTENT_ACTION_PLAY = TAG + ".intent.action.PLAY"; // 广播频道常量：播放
    static String INTENT_ACTION_PLAY_OR_PAUSE = TAG + ".intent.action.PLAY_OR_PAUSE"; // 广播频道常量：播放或暂停
    static String INTENT_ACTION_NEXT = TAG + ".intent.action.NEXT"; // 广播频道常量：播放下一首
    static String INTENT_ACTION_UPDATE_PROGRESS = TAG + ".intent.action.UPDATE_PROGRESS"; // 广播频道常量：更新播放进度
    static String INTENT_ACTION_USER_CHANGE_PROGRESS = TAG + ".intent.action.USER_CHANGE_PROGRESS";//广播频道常量：用户改变播放进度
    static String CONTROL_ICON = "control_icon"; // 控制图标名称常量
    static String DURATION = "duration"; // 播放时长名称常量
    static String CURRENT_POSITION = "current_position"; // 当前播放位置名称常量
    static int PLAY_MODE_ORDER = 0;//播放模式：顺序播放
    static int PLAY_MODE_RANDOM = 1;//播放模式：随机播放
    static int PLAY_MODE_LOOP = 2;//播放模式：单曲循环
}
