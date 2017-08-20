package com.tuuba.ximalayatest;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.tuuba.ximalayatest.R;
import com.tuuba.ximalayatest.utils.MyUtils;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.category.Category;
import com.ximalaya.ting.android.opensdk.model.category.CategoryList;
import com.ximalaya.ting.android.opensdk.model.tag.TagList;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private CommonRequest mXimalaya;
    private String mAppSecret;
    private Context context;

    private Button getCategory;
    private Button getTags;
    private Button getAlbumList;
    private Button getTracks;

    private Map<String, String> specificParams;
    private int categoryId;
    private int type;
    private int calcDimension;
    private String tagName;
    private int albumId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");

        context=this;

        initXimalaya();
        initUI();
        initListener();


        specificParams = new HashMap<String, String>();



    }

    private void initListener() {

        getCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, " getCategory.setOnClickListener onClick: ");
                CommonRequest.getCategories(specificParams, new IDataCallBack<CategoryList>() {
                    @Override
                    public void onSuccess(CategoryList object) {
                        Log.d(TAG, "object = [" + object + "]");
                        Log.d(TAG, "categoryListToString: " + MyUtils.getInstance().categoryListToString(object));
                    }

                    @Override
                    public void onError(int code, String message) {
                        Log.d(TAG, "code = [" + code + "], message = [" + message + "]");
                    }
                });
            }
        });

        getTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "getTags.setOnClickListener onClick: ");
                if (!specificParams.isEmpty()) {
                    specificParams.clear();
                }
                //音乐分类
                categoryId = 2;
                //专辑标签
                type = 0;
                specificParams.put(DTransferConstants.CATEGORY_ID, String.valueOf(categoryId));
                specificParams.put(DTransferConstants.TYPE, String.valueOf(type));
                CommonRequest.getTags(specificParams, new IDataCallBack<TagList>() {

                    @Override
                    public void onSuccess(TagList tagList) {
                        Log.d(TAG, "tagList = [" + tagList + "]");
                        Log.d(TAG, "tagListToString: " + MyUtils.getInstance().tagListToString(tagList));

                    }

                    @Override
                    public void onError(int code, String message) {
                        Log.d(TAG, "code = [" + code + "], message = [" + message + "]");

                    }
                });

            }
        });

        getAlbumList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "getAlbumList.setOnClickListener onClick: ");
                if (!specificParams.isEmpty()) {
                    specificParams.clear();
                }

                //音乐分类
                categoryId = 2;
                //计算维度，现支持最火（1），最新（2），经典或播放最多（3）
                calcDimension = 1;
                //分类下对应的专辑标签，不填则为热门分类
                tagName = "纯音乐";
                specificParams.put(DTransferConstants.CATEGORY_ID, String.valueOf(categoryId));
                specificParams.put(DTransferConstants.CALC_DIMENSION, String.valueOf(calcDimension));
                specificParams.put(DTransferConstants.TAG_NAME, tagName);
                CommonRequest.getAlbumList(specificParams, new IDataCallBack<AlbumList>() {

                    @Override
                    public void onSuccess(AlbumList albumList) {
                        Log.d(TAG, "albumList = [" + albumList + "]");
                        Log.d(TAG, "albumListToString: " + MyUtils.getInstance().albumListToString(albumList));

                    }

                    @Override
                    public void onError(int code, String message) {
                        Log.d(TAG, "code = [" + code + "], message = [" + message + "]");

                    }
                });

            }
        });

        getTracks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                if (!specificParams.isEmpty()) {
                    specificParams.clear();
                }

                albumId = 195926;
                specificParams.put(DTransferConstants.ALBUM_ID, String.valueOf(albumId));
                CommonRequest.getTracks(specificParams, new IDataCallBack<TrackList>() {

                    @Override
                    public void onSuccess(TrackList trackList) {
                        Log.d(TAG, "trackList = [" + trackList + "]");
                        Log.d(TAG, "trackListToString: " + MyUtils.getInstance().trackListToString(trackList));

                    }

                    @Override
                    public void onError(int code, String message) {
                        Log.d(TAG, "code = [" + code + "], message = [" + message + "]");

                    }
                });

            }
        });


    }

    private void initUI() {
        getCategory = (Button) findViewById(R.id.getCategory);
        getTags = (Button) findViewById(R.id.getTags);
        getAlbumList = (Button) findViewById(R.id.getAlbumList);
        getTracks = (Button) findViewById(R.id.getTracks);
    }

    /**
     * init
     */
    private void initXimalaya() {
        Log.d(TAG, "initXimalaya: ");
        mXimalaya = CommonRequest.getInstanse();
        mAppSecret = context.getResources().getString(R.string.app_secret);
        mXimalaya.init(context, mAppSecret);

    }



}
