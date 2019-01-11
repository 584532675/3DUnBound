package com.unbounded.video.constants;

/**
 * Created by Administrator on 2017/7/15 0015.
 */

public class Constants {
    /**
     * 屏幕尺寸
     */
    public final static String SCREEN_WIDTH = "screenWidth";
    public final static String SCREEN_HEIGHT = "screenHeight";
    public final static int ZERO = 0;
    public final static String Result_Success = "1";
    public final static String Result_Fail = "0";
    /**
     *  1dp
     */
    public final static String One_Dp = "oneDp";

    public final static String PHONE_TYPE = "phoneType";
    //是否第一次启动app
    public final static String IsFirstEnter_Flag = "isFirstEnter";
    //已读消息id的列表
    public final static String HaveReaded_Flag = "haveReaded";
    /**
     * 访问正确返回标记
     */
    public final static int SUCCESS_FLAG = 111110;
    /**
     * 网络接口访问正确返回
     */
    public final static int INTERNET_SUCCESS_FLAG = 111111;
    /**
     * 网络接口访问超时
     */
    public final static int INTERNET_ERROR_FLAG = 111112;
    public final static int PERMISSION_ERROR_FLAG = 111113;

    /**
     * 网络连接状态
     */
    public static final int WIFI_FLAG = 111115;
    public static final int GPR_FLAG = 111116;
    /**
     * 视频免费与否
     */
    public static final String Free_Flag= "FREE";
    public static final String NotFree_Flag = "NOT_FREE";


    /**
     * 成功字符串
     */
    public final static String SUCCESS_STR = "success";
    public final static String ERROR_STR = "error";
    /**
     * 存储视频历史记录标记
     */
    public final static String VIDEOHISTORY_FLAG = "videoHistoryJson";
    /**
     * 视频下载列表标记
     */
    public final static String VIDEODOWN_FLAG = "videoDownJson";
    /**
     * 极光唯一标识
     */
    public final static String Regid_FLAG = "regid";
    public final static String UserId_FLAG = "userId";
    public final static String UserImg_FLAG = "userimg";
    public final static String Phone_FLAG = "phone";
    public final static String Sex_FLAG = "sex";
    public final static String Autograph_FLAG = "autograph";
    public final static String UserCase_FLAG = "userCase";
    public final static String Userfans_FLAG = "userfans";
    public final static String Account_FLAG = "account";
    public final static String Username_FLAG = "username";

    public final static String VideoInfoUpdate = "videoInfoUpdate";
    public final static String PicInfoUpdate = "picInfoUpdate";
    public final static String UserInfoUpdate = "userInfoUpdate";

    public final static String YouTubeKye = "3qPSLLWMG4";
    public final static String Phone_default = "null";

    /*有赞商城登陆成功返回的参数*/
    public final static String ACCESS_TOKEN = "access_token";
    public final static String COOKIE_KEY = "cookie_key";
    public final static String COOKIE_VALUE = "cookie_value";
    public final static String IS_LOGIN = "is_login";
    public static  int  baseview = 0;

    public static tuning CalbStateEnum = tuning.AREA;
    public static String MALLACTIVITY_TYPE="mallactivity_type";
    //更新baseview的广播
    public static final String UPDATE_BASEVIEW_BROADCAST = "com.unbounded.video.baseview";
    public static final String YOU_ZAN_MALL_CLIENT_ID= "3ead8a3f36e369f271";
    public static final String YOU_ZAN_MALL_CLIENT_SECRE= "632c355c0533494001bfd32b21fef65a";
    public static final int YOU_ZAN_MALL_SHOP_ID=40514317;
    public static final String Mopic ="Mopic";

    public static final String  IS_FIRST_SPLASH= "isFirstSplash";

    public static final String PLAYER_NUMBER = "playerNumber";
    public static final String USER_REDUCE_TIME = "user_reduce_time";
    public static final String TIME_USER_ID = "time_user_id";

    public static final String START_USER_TIME = "start_user_id";
    public static final String MODEL = "model";
//    /**
//     * 检测激活文件是否存在进行3d播放
//     */
//    public final static String PALY_3D ="play_3d";

}
