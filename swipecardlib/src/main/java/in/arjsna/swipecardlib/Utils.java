package in.arjsna.swipecardlib;

import ohos.agp.utils.Rect;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import java.util.Arrays;
import java.util.StringJoiner;

/**
 * Utils.
 */
public class Utils {
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(HiLog.LOG_APP, 0x00201, "-MainAbility-");

    /**
     * logs the method name from which it was called.
     *
     * @param params params
     */
    public static void entry_log(Object... params) {
        Throwable stack = new Throwable().fillInStackTrace();
        StackTraceElement[] trace = stack.getStackTrace();
        StringJoiner sj;

        sj = new StringJoiner(",");

        for (Object ignored : params) {
            sj.add(Arrays.toString(params));
        }
        HiLog.debug(LABEL_LOG, "entry_log" + "ENTRY " + String.format(
                " - %s.%s()",
                trace[1].getClassName(),
                trace[1].getMethodName()
        ));
    }

    /**
     * Returns true if (x,y) is inside the rectangle. The left and top are
     * considered to be inside, while the right and bottom are not. This means
     * that for a x,y to be contained: left <= x < right and top <= y < bottom.
     * An empty rectangle never contains any point.
     *
     * @param r rectangle to compare against
     * @param x The X coordinate of the point being tested for containment
     * @param y The Y coordinate of the point being tested for containment
     * @return true if (x,y) are contained by the rectangle, where containment
     * means left <= x < right and top <= y < bottom
     */
    public static boolean rectContains(Rect r, int x, int y) {
        return r.left < r.right && r.top < r.bottom  // check for empty first
                && x >= r.left && x < r.right && y >= r.top && y < r.bottom;
    }
}
