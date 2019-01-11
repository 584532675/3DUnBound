package com.unbounded.video.utils;


import java.util.List;
import com.google.gson.Gson;
import com.unbounded.video.bean.VideoDownBean;
import com.unbounded.video.bean.VideoHistory;


public class JsonUtil {
	static Gson gson=null;
	
    static{
        if(gson==null){
            gson=new Gson();
        }
        
    }
    public JsonUtil(){}
    
    /**
     * 将对象转换成json格式
     * @param ts
     * @return
     */
    public static String objectToJson(Object ts){
        String jsonStr=null;
        if(gson!=null){
            jsonStr=gson.toJson(ts);
        }
        
        return jsonStr;
    }

    /**
     * 将json格式转换成list对象(播放历史记录)
     */
    public static List<VideoHistory> historyJsonToList(String jsonStr){
        List<VideoHistory> objList=null;
        if(gson!=null){
            java.lang.reflect.Type type=new com.google.gson.reflect.TypeToken<List<VideoHistory>>(){}.getType();
            objList=gson.fromJson(jsonStr, type);
        }
        return objList;
    }
    /**
     * 将json格式转换成list对象(视频下载列表)
     */
    public static List<VideoDownBean> videoDownJsonToList(String jsonStr){
        List<VideoDownBean> objList=null;
        if(gson!=null){
            java.lang.reflect.Type type=new com.google.gson.reflect.TypeToken<List<VideoDownBean>>(){}.getType();
            objList=gson.fromJson(jsonStr, type);
        }
        return objList;
    }
    /**
     * 将json格式转换成list对象(消息id列表)
     */
    public static List<String> msgIdsJsonToList(String jsonStr){
        List<String> objList=null;
        if(gson!=null){
            java.lang.reflect.Type type=new com.google.gson.reflect.TypeToken<List<String>>(){}.getType();
            objList=gson.fromJson(jsonStr, type);
        }
        return objList;
    }

}
