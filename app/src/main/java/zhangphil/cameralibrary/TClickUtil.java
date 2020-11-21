package zhangphil.cameralibrary;

/**
 * Time:2019/11/6
 * Author:zhaixs
 * Description: 防止快速连续点击
 */

public class TClickUtil {
    private static final int DELAY = 500;
    private static long lastClickTime = 0;


    public static boolean isNotFastClick() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > DELAY) {
            lastClickTime = currentTime;
            return true;
        }
        return false;
    }
}
