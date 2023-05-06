package com.freelycar.demo.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollect {
    public static List<Activity> activities = new ArrayList<>();

    //添加活动 (装垃圾)
    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    //移除活动 (移除已经丢掉的垃圾)
    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    //销毁所有活动 (丢垃圾)
    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
    //返回当前栈顶活动
    public static Activity getStackTopActivity(){
        return activities.get(activities.size() - 1);
    }

}

