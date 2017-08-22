package com.tuuba.ximalayatest.utils;

import android.content.Context;
import android.util.Log;

import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.category.Category;
import com.ximalaya.ting.android.opensdk.model.category.CategoryList;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YF-04 on 2017/8/21.
 */

public class CommonRequestManager {

    private static final String TAG = "CommonRequestManager";

    private static CommonRequestManager manager=new CommonRequestManager();
    private  CommonRequest commonRequest;

    private Map<String, String> specificParams;
//    private Map<Integer,String>errorInfo=new HashMap<>();
//    private List<Category> categories=new ArrayList<>();


    private CommonRequestManager(){
        commonRequest=CommonRequest.getInstanse();
        specificParams=new HashMap<>();

    }

    public static CommonRequestManager getInstanse(){
        return manager;
    }

    public void init(Context context, String appsecret){
        commonRequest.init(context,appsecret);
    }

    public void setDefaultPagesize(int size) {
        commonRequest.setDefaultPagesize(size);
    }


    public static final String TRACK_KIND="track";
    public static final String PLAY_URL_32="play_url_32";
    public static final String PLAY_URL_64="play_url_64";
    public static final String PLAY_URL_24_M4a="play_url_24_m4a";
    public static final String PLAY_URL_64_M4a="play_url_64_m4a";

    /**
     *init a base track
     * @param options:play_url_32 ,play_url_64 ,play_url_24_m4a and play_url_64_m4a,those options can not be all null.
     * @return track if is success,null while those options are all null.
     */
    public Track initTrack(Map<String,String>options){

        if (options==null || options.isEmpty()){
            return null;
        }

        Track track=new Track();
        track.setKind(TRACK_KIND);

        boolean initState=false;

        String play_url_32=options.get(PLAY_URL_32);
        if(play_url_32!=null){
            initState=true;
            track.setPlayUrl32(play_url_32);
        }

        String play_url_64=options.get(PLAY_URL_64);
        if(play_url_64!=null){
            initState=true;
            track.setPlayUrl64(play_url_64);
        }

        String play_url_24_m4a=options.get(PLAY_URL_24_M4a);
        if(play_url_24_m4a!=null){
            initState=true;
            track.setPlayUrl24M4a(play_url_24_m4a);
        }

        String play_url_64_m4a=options.get(PLAY_URL_64_M4a);
        if(play_url_64_m4a!=null){
            initState=true;
            track.setPlayUrl64M4a(play_url_64_m4a);
        }

        if(!initState){
            return null;
        }

        return track;
    }

}
