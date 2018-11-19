public class NotchJudgeUtil {
/**
 * OPPO
 *
 * @param context Context
 * @return hasNotch
 */
public static boolean hasNotchInOppo(Context context) {
    return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
}

/**
 * VIVO
 * <p>
 * android.util.FtFeature
 * public static boolean isFeatureSupport(int mask);
 * <p>
 * 参数:
 * 0x00000020表示是否有凹槽;
 * 0x00000008表示是否有圆角。
 *
 * @param context Context
 * @return hasNotch
 */
private static boolean hasNotchInVivo(Context context) {
    boolean hasNotch = false;
    try {
        ClassLoader cl = context.getClassLoader();
        Class ftFeature = cl.loadClass("android.util.FtFeature");
        Method[] methods = ftFeature.getDeclaredMethods();
        if (methods != null) {
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                if (method.getName().equalsIgnoreCase("isFeatureSupport")) {
                    hasNotch = (boolean) method.invoke(ftFeature, 0x00000020);
                    break;
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
        hasNotch = false;
    }
    return hasNotch;
}

/**
 * HUAWEI
 * com.huawei.android.util.HwNotchSizeUtil
 * public static boolean hasNotchInScreen()
 *
 * @param context Context
 * @return hasNotch
 */
public static boolean hasNotchInHuawei(Context context) {
    boolean hasNotch = false;
    try {
        ClassLoader cl = context.getClassLoader();
        Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
        Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
        hasNotch = (boolean) get.invoke(HwNotchSizeUtil);
    } catch (Exception e) {
        e.printStackTrace();
    }
    return hasNotch;
}

/**
 * Mi
 * @param context
 * @return SystemProperties 如果无法导入，请配置gradle
 * 进行android.os.SystemProperties隐藏类的导入
 */
public static boolean hasNotchInMi(Context context) {
    if (SystemProperties.getInt("ro.miui.notch", 0) == 1) {
        return true;
    } else {
        return false;
    }

}
}
