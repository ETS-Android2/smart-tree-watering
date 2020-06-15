package com.mahfuznow.iotree;

import android.app.Activity;
import android.graphics.Point;

class Utils {
    public static Point getScreenSize(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size;
    }

}
