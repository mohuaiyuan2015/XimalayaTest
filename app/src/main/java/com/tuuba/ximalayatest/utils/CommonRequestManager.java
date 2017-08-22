package com.tuuba.ximalayatest.utils;

import android.content.Context;
import android.util.Log;

import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.category.Category;
import com.ximalaya.ting.android.opensdk.model.category.CategoryList;

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


}
