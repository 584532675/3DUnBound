package com.unbounded.video.constants;

/**
 * Created by zjf on 2017/7/21 0021.
 */

public class HttpConstants {
    /*
    3D新视界：http://horizon.moyansz.com/unbounded
    库盟：http://kumeng.moyansz.com
    国美：http://guomei.moyansz.com/unbounded
    */

//    public static String base_url1 = "http://10.10.50.7:8081";  //测试服务器
    public static String base_url1 = "http://unbounded.moyansz.com";  //正式服务器

//    public static String base_url1 = "http://guomei.moyansz.com";  //国美正式服务器
//    public static String base_url1 = "http://112.74.171.171:8093";  //国美测试服务器
//    public static String base_url1 = "http://10.10.40.21:8096/unbounded-1.2.7";


    //热播，最新影片    shelvesCurrentPage shelvesselectStart  jinshanyunCurrentPage  jinshanyunSelectStart
    //rjz start 修改精选影片为最新影片 3D无界为第一个，3D新视界为第二个
    public static String NewandHot_VideosUrl = base_url1 + "/unbounded/video/jinshanyunvideo";
//    public static String NewandHot_VideosUrl = base_url1 + "/unbounded/video/selectvideoShelvesNew";
    //rjz  add 校准视频接口
    public static String Calibration_VideosUrl = base_url1 + "/unbounded/video/getCourse";
    //rjz end 校准视频接口
    //rjz e修改精选影片为最新影片
    //热播换一批    shelvesCurrentPage shelvesselectStart   jinshanyunCurrentPage
    public static String NextHot_VideosUrl = base_url1 + "/unbounded/video/selectvideoShelves?comeFrom=gm";
    //最新影片更多  jinshanyunCurrentPage   jinshanyunSelectStart
    public static String NewMore_VideosUrl = base_url1 + "/unbounded/video/jinshanyunvideo";
    //点击首页 电影 动画 等按钮     currentPage selectStart  videoType = 动画
    public static String VideoType_getRecommend_VideosUrl = base_url1 + "/unbounded/video/getRecommend";
    //rjz 添加，新增推荐接口
    public static String VideoType_VideosUrl = base_url1 + "/unbounded/video/selectvideoType";
    //判断验证码是否正确   code（验证码）  msgId（标识）
    public static String CheckCode_Url = base_url1 + "/unbounded/register/checkCode";
    //注册第二步  phone(手机号)  password（密码）regid（极光推送的标识）
    public static String InsertNoCode_Url = base_url1 + "/unbounded/register/insertNoCode";
    //忘记密码第二步  phone(手机号)  password（密码）
    public static String UpdatePswByPhone_Url = base_url1 + "/unbounded/register/updatePswByPhone";
    //最新影片列表
//    public static String SelectvideoShelvesNew_Url = base_url1 + "/unbounded/video/selectvideoShelvesNew?comeFrom=gm";
    //版本升级
    public static String VersionUpdate_Url = base_url1 + "/unbounded/version/versionUpdate";
    //3D东东顶部
    public static String SelectBanner_Url = base_url1 + "/unbounded/video/selectBanner";

    public static String Resolve_video ="https://steakovercooked.com/api/video/?cached&hash=1f9cb767067c72db03d1a2d5c62cd2b2&video=";


    //推荐视频   currentPage  selectStart
    public static String Recommend_VideosUrl = base_url1 + "/unbounded/video/selectRecommend";
    //广告
    public static String Banner_Url = base_url1 + "/unbounded/advert/select";

    //影视，原创视频   type：movie(影视)/original(原创)/3d（3D冬冬）  currentPage  selectStart   report(0收费    1免费)
    public static String SelectInfoByType_VideosUrl = base_url1 + "/unbounded/video/selectInfoByType";
    //youtube视频
    public static String YouTube_VideosUrl = "http://phonecover.moyansz.com:8090/unbounded/video/selectInfoByType";
//    public static String SelectInfoByType_VideosUrl = "http://10.10.40.2:8080" + "/unbounded/video/selectInfoByType";

    //视频详情    id：视频ID   userId:用户id
    public static String selectInfoByID_VideosUrl = base_url1 + "/unbounded/video/selectInfoByID";

    //视频搜索   name:关键字  currentPage   selectStart
    public static String Select_VideosUrl = base_url1 + "/unbounded/video/select";

    //删除视频   id：视频ID      返回：{"result": "success"}/{"result": "error"}
    public static String Delete_VideosUrl = base_url1 + "/unbounded/video/delete";

    //评论列表   id：视频ID  currentPage   selectStart
    public static String Discuss_listUrl = base_url1 + "/unbounded/comment/evaluateSelect";

    //视频举报接口
    public static String Discuss_Url = base_url1 + "/unbounded/comment/evaluateInsert";

    //视频举报   userId：用户ID   videoId：视频ID    type：video(视频)/image(图片)
    public static String UpdateReport_Url = base_url1 + "/unbounded/video/updateReport";

    //收藏   collectorId：收藏的视频的ID，userId：用户的ID，collectorType：收藏的类型(视频)，type：分类（image/video）
    public static String Collect_Url = base_url1 + "/unbounded/collector/collector";
    //收藏列表
    public static String Collectlist_Url = base_url1 + "/unbounded/collector/select";
    //删除收藏   当在收藏列表中时传id（收藏uuid）    当在详情中取消（userId用户id   collectorId视频id）
    public static String Deletecollect_Url = base_url1 + "/unbounded/collector/delete";

    //关注    userId：用户ID，followId：关注的人的ID
    public static String Follow_Url = base_url1 + "/unbounded/follow/inster";
    //关注列表   userId：用户的ID，currentPage:第几页,selectStart：开始的ID（第一页传""）
    public static String Followlist_Url = base_url1 + "/unbounded/follow/select";
    //取消关注
    public static String Deletefollowlist_Url = base_url1 + "/unbounded/follow/delete";

    //点赞   userId：用户ID，likeid：点赞的视频的ID，type：分类（image/video）
    public static String Like_Url = base_url1 + "/unbounded/like/inster";
    //取消点赞
    public static String deleteLike_Url = base_url1 + "/unbounded/like/delete";
    //上传视频
    public static String insertVideo_Url = base_url1 + "/unbounded/video/insert";
    //增加播放次数
    public static String addnumVideo_Url = base_url1 + "/unbounded/video/addNum";



    //上传图片  name:图片名称，userid:用户的ID，uploadType:视频类型，introduction:视频简介，type:视频种类，3DType：3D类型
    public static String insertImg_Url = base_url1 + "/unbounded/images/insert";
    //推荐图片列表   currentPage:第几页,selectStart：开始的ID（第一页传""）
    public static String selectRecommendImg_Url = base_url1 + "/unbounded/images/selectRecommend";
    //影视/原创图片列表   type：movie(影视)/original(原创)   currentPage  selectStart
    public static String selectInfoByTypeImg_Url = base_url1 + "/unbounded/images/selectInfoByType";
    //搜索图片列表   name:关键字,currentPage:第几页,selectStart：开始的ID（第一页传""）
    public static String selectByNameImg_Url = base_url1 + "/unbounded/images/selectByName";
    //图片详情   id:图片ID，userId:用户的ID
    public static String selectByIdImg_Url = base_url1 + "/unbounded/images/selectById";


    //查询用户上传的视频   userId：用户的ID，currentPage:第几页,selectStart：开始的ID（第一页传""）
    public static String selectVideoInfoByUserId_Url = base_url1 + "/unbounded/video/selectInfoByUserId";
    //查询用户上传的图片   userId：用户的ID，currentPage:第几页,selectStart：开始的ID（第一页传""）
    public static String selectImageInfoByUserId_Url = base_url1 + "/unbounded/images/select";


    //登录
    public static String Login_Url = base_url1 + "/unbounded/register/login";
    //获取验证码(注册时)
    public static String GetCode_Url = base_url1 + "/unbounded/register/send";
    //获取验证码(忘记密码)
    public static String ForgetpswGetCode_Url = base_url1 + "/unbounded/register/sendCode";
    //更改手机号(下一步按钮验证)
    public static String UpdatePhonecheckCode_Url = base_url1 + "/unbounded/register/checkCode";
    //新手机下一步(更改绑定)
    public static String UpdatePhoneByCode_Url = base_url1 + "/unbounded/register/updatePhoneByCode";
    //注册
    public static String Register_Url = base_url1 + "/unbounded/register/insert";
    //注销
    public static String Exit_Url = base_url1 + "/unbounded/register/exit";
    //个人信息
    public static String UserInfo_Url = base_url1 + "/unbounded/register/selectByUserid";
    //更新头像
    public static String UpdateUserImg_Url = base_url1 + "/unbounded/register/updateImg";
    //更新个人信息
    public static String UpdateUserInfo_Url = base_url1 + "/unbounded/register/update";
    //忘记密码
    public static String ForgetPsw_Url = base_url1 + "/unbounded/register/updatePsdByCode";
    //修改密码
    public static String ChangePsw_Url = base_url1 + "/unbounded/register/updatePsdByUserid";

    //我的消息   userId   currentPage   selectStart
    public static String PushMsg_Url = base_url1 + "/unbounded/pushMsg/select";
    //意见反馈
    public static String FeedBack_Url = base_url1 + "/unbounded/feedback/insert";



    //3D冬冬
    public static String ThreeDDRecommend_Url = "http://app.api.3dov.cn/app/ios/getIosRecommend";
    public static String YouZan_Mall_Token ="https://uic.youzan.com/sso/open/initToken";
    public static String YouZan_Mall_Login ="https://uic.youzan.com/sso/open/login";
    public static String YouZan_Mall_Login_OUT ="https://uic.youzan.com/sso/open/logout";

    public static String SEND_CODE = base_url1+"/unbounded/messageSend/sendCode";
    public static String NEW_LOGIN =base_url1+ "/unbounded/messageSend/login";

    /*举报接口*/
    public static String report_Img_Url = base_url1 + "/unbounded/video/updateReport";
    /*使用时间接口*/
    public static String USER_TIME = base_url1 + "/unbounded/messageSend/updateOnlineTime";
    /*开机广告接口*/
    public static String SPLASH_ADVERTISING = base_url1 + "/unbounded/video/getOpenAdvertising";
}
