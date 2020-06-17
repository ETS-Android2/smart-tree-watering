package com.mahfuznow.iotree.util;

import android.app.Activity;
import android.graphics.Point;

public class DeviceUtils {
    public static Point getScreenSize(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size;
    }

}
