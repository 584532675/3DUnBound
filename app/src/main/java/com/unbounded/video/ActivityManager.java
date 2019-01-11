package com.unbounded.video;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity管理类1
 */

public class ActivityManager {
    private static List<Activity> activityList = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    public static Activity getTopActivity() {
        if (activityList.isEmpty()) {
            return null;
        } else {
            return activityList.get(activityList.size()-1);
        }
    }
}