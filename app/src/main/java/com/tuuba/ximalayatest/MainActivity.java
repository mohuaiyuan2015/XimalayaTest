package com.tuuba.ximalayatest;

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tuuba.trackdto.library.TrackDTO;
import com.tuuba.ximalayatest.R;
import com.tuuba.ximalayatest.adapter.TrackAdapter;
//import com.tuuba.ximalayatest.dto.TrackDTO;
import com.tuuba.ximalayatest.utils.CommonRequestManager;
import com.tuuba.ximalayatest.utils.MyUtils;
import com.ximalaya.ting.android.opensdk.auth.utils.StringUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.album.SubordinatedAlbum;
import com.ximalaya.ting.android.opensdk.model.category.Category;
import com.ximalaya.ting.android.opensdk.model.category.CategoryList;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.model.tag.Tag;
import com.ximalaya.ting.android.opensdk.model.tag.TagList;
import com.ximalaya.ting.android.opensdk.model.track.SearchTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.appnotification.XmNotificationCreater;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static int defaultPageCount=50;

    private XmPlayerManager mPlayerManager;
//    private CommonRequest mXimalaya;
    private CommonRequestManager manager;

    private boolean mUpdateProgress = true;

    private String mAppSecret;
    private Context context;
    private MyHandler myHandler;

    private Button getCategory;
    private Button getTags;
    private Button getAlbumList;
    private Button getTracks;
    private Button getAllTracks;
    private Button play;
    private Button play2;
    private Button getDebugMsg;
    private EditText voiceCategory;
    private EditText voiceTagname;
    private EditText voiceDimension;
    private Button getVoicesTags;
    private Button getVoicesList;
    private EditText songNameEditText;
    private Button searchSong;
    private Button relevancy;

    private String songName;

    private RecyclerView tracksRecyclerView;

    private TrackAdapter trackAdapter;

    private Map<String, String> specificParams;
    private int categoryId;
    private int type;
    private int calcDimension;
    private String tagName;
    private int page;
    private long albumId;

    //midea play
    private TextView mMessage;
    private ImageButton mBtnPreSound;
    private ImageButton mBtnPlay;
    private ImageButton mBtnNextSound;
    private SeekBar mSeekBar;
    private ImageView mSoundCover;
    private ProgressBar mProgress;

    private List<Category> categories=new ArrayList<>();
    private List<Tag> tags=new ArrayList<>();
    private List<Album>albums=new ArrayList<>();
    private List<Track> tracks=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");

        context=this;
        myHandler=new MyHandler();

        initXimalaya();
        initUI();

        LinearLayoutManager manager=new LinearLayoutManager(context);
        tracksRecyclerView.setLayoutManager(manager);
        trackAdapter=new TrackAdapter(context,tracks);
        tracksRecyclerView.setAdapter(trackAdapter);

        tracksRecyclerView.setNestedScrollingEnabled(false);

        initListener();

        specificParams = new HashMap<String, String>();

    }


    /**
     * init
     */
    private void initXimalaya() {
        Log.d(TAG, "initXimalaya: ");
//        mXimalaya = CommonRequest.getInstanse();
//        mAppSecret = context.getResources().getString(R.string.app_secret);
//        mXimalaya.init(context, mAppSecret);

        mAppSecret = context.getResources().getString(R.string.app_secret);
        manager=CommonRequestManager.getInstanse();
        manager.init(context,mAppSecret);


        mPlayerManager = XmPlayerManager.getInstance(context);
        Notification mNotification = XmNotificationCreater.getInstanse(context).initNotification(this.getApplicationContext(), MainActivity.class);

        // 如果之前贵方使用了 `XmPlayerManager.init(int id, Notification notification)` 这个初始化的方式
        // 请参考`4.8 播放器通知栏使用`重新添加新的通知栏布局,否则直接升级可能导致在部分手机播放时崩溃
        // 如果不想使用sdk内部搞好的notification,或者想自建notification 可以使用下面的  init()函数进行初始化
//        mPlayerManager.init((int) System.currentTimeMillis(), mNotification);
		mPlayerManager.init();
        mPlayerManager.addPlayerStatusListener(mPlayerStatusListener);
        mPlayerManager.addAdsStatusListener(mAdsListener);
        mPlayerManager.addOnConnectedListerner(new XmPlayerManager.IConnectListener() {
            @Override
            public void onConnected() {
                mPlayerManager.removeOnConnectedListerner(this);

                //mohuaiyuan 201708
//                mXimalaya.setDefaultPagesize(defaultPageCount);
                manager.setDefaultPagesize(defaultPageCount);

                mPlayerManager.setPlayMode(XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP);
//                mPlayerManager.setPlayMode(XmPlayListControl.PlayMode.PLAY_MODEL_LIST);
                Toast.makeText(context, "播放器初始化成功", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initListener() {

        getCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, " getCategory onClick: ");

                CommonRequest.getCategories(specificParams, new IDataCallBack<CategoryList>() {
                    @Override
                    public void onSuccess(CategoryList object) {
                        categories.addAll(object.getCategories());
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
                Log.d(TAG, "getTags onClick: ");
                if (!specificParams.isEmpty()) {
                    specificParams.clear();
                }
                //音乐分类
                categoryId = 2;
                //0-专辑标签，1-声音标签
                type = 0;
                getVoiceTags(categoryId,type);

            }
        });

        getAlbumList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "getAlbumList  onClick: ");
                //mohuaiyuan 201708
//                getAlbumList();
                getVoiceList(false,2,1,"");
            }
        });

        getTracks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "getTracks onClick: ");
                if(!tracks.isEmpty()){
                    tracks.clear();
                    reflashDataSetChanged();
                }
                //mohuaiyuan 201708
//                getAlbumList(true);
                getVoiceList(true,2,1,"");
            }
        });

        getAllTracks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "getAllTracks onClick: " );
                for(int i=0;i<albums.size();i++){
                    getTracksByAlbum(albums.get(i));
                }
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "play  onClick: ");

                if(tracks !=null && !tracks.isEmpty()){
                    Log.d(TAG, "tarcks.size(): "+tracks.size());
                    mPlayerManager.playList(tracks,1);
                }

            }
        });

        play2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "play2 onClick: ");
                if(!tracks.isEmpty()){
                    tracks.clear();
                    reflashDataSetChanged();
                }

                Map<String,String>options=new HashMap<String, String>();
                //trackTitle='班得瑞 - 清晨',
//                String play_url_32="http://fdfs.xmcdn.com/group17/M08/34/81/wKgJKVfolsrBBWdwAAt16UVvunI417.mp3";
//                String play_url_64="http://fdfs.xmcdn.com/group17/M08/34/81/wKgJKVfolsvTNfCwABbrYRAUPJU718.mp3";
//                String play_url_24_m4a="http://audio.xmcdn.com/group20/M05/34/97/wKgJJ1folx2QaW31AAjirsris1o806.m4a";
//                String play_url_64_m4a="http://audio.xmcdn.com/group20/M05/34/97/wKgJJ1folx2QaW31AAjirsris1o806.m4a";

                //trackTitle='班得瑞 - Endless Horizon',
//                String play_url_32="http://fdfs.xmcdn.com/group24/M00/5B/7A/wKgJMFgzv5-x_3E4ABBudDl09fA573.mp3";
//                String play_url_64="http://fdfs.xmcdn.com/group24/M01/5B/8D/wKgJNVgzv6KCzNHrACDcKlFVKf4758.mp3";
//                String play_url_24_m4a="http://audio.xmcdn.com/group23/M04/5B/9F/wKgJNFgzv5rQiXvQAAy60hBmTu4436.m4a";
//                String play_url_64_m4a="http://audio.xmcdn.com/group24/M00/5B/8D/wKgJNVgzv5zhiy8NACFFNeKAFFw089.m4a";

                //漂洋过海来看你
//                String play_url_32="http://fdfs.xmcdn.com/group29/M09/B2/83/wKgJXVlLPMmwx9RpABEnW-U2VVw911.mp3";
//                String play_url_64="http://fdfs.xmcdn.com/group29/M09/B2/82/wKgJXVlLPMKiWkuCACJN9yxN1M0482.mp3";
//                String play_url_24_m4a="http://audio.xmcdn.com/group28/M05/B4/14/wKgJSFlLPwyCoNbzAA1J_BozPJg641.m4a";
//                String play_url_64_m4a="http://audio.xmcdn.com/group29/M09/B2/7C/wKgJWVlLPMOgbRdFACK7q-Ca5mo930.m4a";

                //trackTitle='《于谦做手术》郭德纲 于谦'
//                String play_url_32="http://fdfs.xmcdn.com/group26/M08/51/CD/wKgJRljoUVaAEK_SAFzp4F7Qs1Q801.mp3";
//                String play_url_64="http://fdfs.xmcdn.com/group27/M00/4D/72/wKgJR1joUVqS3PbRALnTLuVY3Zo822.mp3";
//                String play_url_24_m4a="http://audio.xmcdn.com/group26/M08/51/E5/wKgJWFjoUW-AenCJAEfn32EXkaY896.m4a";
//                String play_url_64_m4a="http://audio.xmcdn.com/group26/M04/51/D0/wKgJRljoUZ3R0gidALwMKmiYw8o743.m4a";

                //beyond 真的爱你
                String play_url_32="http://fdfs.xmcdn.com/group9/M04/22/52/wKgDZlboEhbAZLxFABEBSwrVj1Y718.mp3";
                String play_url_64="http://fdfs.xmcdn.com/group16/M01/22/16/wKgDbFboD1uCY1yBACIB8iUP0W0677.mp3";
                String play_url_24_m4a="http://audio.xmcdn.com/group12/M09/22/10/wKgDW1boEbCzLhAxAA0jykb6puI748.m4a";
                String play_url_64_m4a="http://audio.xmcdn.com/group16/M00/22/1F/wKgDalboD16BK_oeACJmbrSsmck708.m4a";


                options.put(CommonRequestManager.PLAY_URL_32,play_url_32);
                options.put(CommonRequestManager.PLAY_URL_64,play_url_64);
                options.put(CommonRequestManager.PLAY_URL_24_M4a,play_url_24_m4a);
                options.put(CommonRequestManager.PLAY_URL_64_M4a,play_url_64_m4a);

                Track track=manager.initTrack(options);

                //mohuaiyuan 201708
                TrackDTO trackDTO1=null;
                try {
                    trackDTO1=new TrackDTO(track);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String trackToString=trackDTO1.toJsonString();
                Log.d(TAG, "track to json: "+trackToString);

                TrackDTO trackDTO=null;
                try {
                    trackDTO= new TrackDTO(trackToString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "trackDTO2 kind: "+trackDTO.getKind());
                Log.d(TAG, "trackDTO2 getPlayUrl32: "+trackDTO.getPlayUrl32());
                Log.d(TAG, "trackDTO2 getPlayUrl64: "+trackDTO.getPlayUrl64());
                Log.d(TAG, "trackDTO2 getPlayUrl24M4a: "+trackDTO.getPlayUrl24M4a());
                Log.d(TAG, "trackDTO2 getPlayUrl64M4a: "+trackDTO.getPlayUrl64M4a());

//                String trackTitle="班得瑞 - 清晨";
//                track.setTrackTitle(trackTitle);
//
//                int duration=187;
//                track.setDuration(duration);

                //mohuaiyuan 201708 暂时注释
//                tracks.add(track);

                //mohuaiyuan 201708
                Track newTrack=null;
                try {
                    newTrack=trackDTO.createTrack();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                tracks.add(newTrack);

                reflashDataSetChanged();

            }
        });


        getDebugMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "getDebugMsg onClick: ");
                Log.d(TAG, "categories.size(): "+categories.size());
                Log.d(TAG, "tags.size(): "+tags.size());
                Log.d(TAG, "albums.size(): "+albums.size());
                Log.d(TAG, "tracks.size(): "+tracks.size());
            }
        });

        getVoicesTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "getVoicesTags onClick: ");
                categoryId = 2;
                type = 1;

                try {
                    String temp=voiceCategory.getText().toString();
                    if(temp!=null && temp.length()>0){
                        categoryId = Integer.valueOf(temp);
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                getVoiceTags(categoryId, type);


            }
        });

        getVoicesList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "getVoices onClick: ");
                if(!tracks.isEmpty()){
                    tracks.clear();
                    reflashDataSetChanged();
                }
                categoryId = 2;
                tagName="";
                calcDimension=1;

                try {
                    String temp=voiceCategory.getText().toString();
                    if(temp!=null && temp.length()>0){
                        categoryId = Integer.valueOf(temp);
                    }

                    tagName=voiceTagname.getText().toString();

                    temp=voiceDimension.getText().toString();
                    if(temp!=null && temp.length()>0){
                        calcDimension=Integer.valueOf(temp);
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                getVoiceList(true,categoryId,calcDimension,tagName);

            }
        });

        searchSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, " searchSong onClick: ");

                if(!tracks.isEmpty()){
                    tracks.clear();
                    reflashDataSetChanged();
                }

                songName="小苹果";
                String temp=songNameEditText.getText().toString();
                if(temp!=null && temp.length()>0){
                    songName=temp;
                }
                Log.d(TAG, "songName: "+songName);
                searchSongByName(songName);
            }
        });

        relevancy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, " relevancy onClick: ");

                songName="小苹果";
                String str=songNameEditText.getText().toString();
                if(str!=null && str.length()>0){
                    songName=str;
                }
                getRelevancyWorld(songName);
            }
        });


        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mPlayerManager.seekToByPercent(seekBar.getProgress() / (float) seekBar.getMax());
                mUpdateProgress = true;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mUpdateProgress = false;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
        });

        mBtnPreSound.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mPlayerManager.playPre();
                //mohuaiyuan 201708
//                mXimalaya.setDefaultPagesize(100);
            }
        });

        mBtnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mPlayerManager.isPlaying()) {
                    mPlayerManager.pause();
                } else {
                    mPlayerManager.play();
                }
            }
        });

        mBtnNextSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayerManager.playNext();
            }
        });

        trackAdapter.setOnItemClickListener(new TrackAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Log.d(TAG, "trackAdapter onItemClick: ");
                if(tracks !=null && !tracks.isEmpty()){
                    Log.d(TAG, "tarcks.size(): "+tracks.size());
                    mPlayerManager.playList(tracks,position);
//                    mPlayerManager.play(position);
                }
            }
        });

    }

    private void getRelevancyWorld(String songName) {
        Log.d(TAG, "getRelevancyWorld: ");

        if (!specificParams.isEmpty()) {
            specificParams.clear();
        }
        specificParams.put(DTransferConstants.SEARCH_KEY, songName);
        CommonRequest.getSuggestWord(specificParams, new IDataCallBack<SuggestWords>() {
            @Override
            public void onSuccess(SuggestWords suggestWords) {
                Log.d(TAG, "suggestWords: "+suggestWords);

            }

            @Override
            public void onError(int code, String message) {
                Log.d(TAG, "code = [" + code + "], message = [" + message + "]");

            }
        });

    }

    private void searchSongByName(String songName) {
        Log.d(TAG, "searchSongByName: ");

        categoryId=2;
        //排序条件：2-最新，3-最多播放，4-最相关（默认）
        calcDimension=4;

        if (!specificParams.isEmpty()) {
            specificParams.clear();
        }
        specificParams.put(DTransferConstants.SEARCH_KEY, songName);
        specificParams.put(DTransferConstants.CATEGORY_ID, String.valueOf(categoryId));
        specificParams.put(DTransferConstants.CALC_DIMENSION,String.valueOf(calcDimension));

        successCount=0;

        CommonRequest.getSearchedTracks(specificParams, new IDataCallBack<SearchTrackList>() {

            @Override
            public void onSuccess(SearchTrackList searchTrackList) {
//                tracks.addAll(searchTrackList.getTracks());
//                reflashDataSetChanged();
                Log.d(TAG, "searchTrackList.getCategoryId: "+searchTrackList.getCategoryId());
                Log.d(TAG, "searchTrackList.getTagName: "+searchTrackList.getTagName());
                Log.d(TAG, "searchTrackList.getParams: "+searchTrackList.getParams());
                Log.d(TAG, "searchTrackList.getTotalPage: "+searchTrackList.getTotalPage());
                Log.d(TAG, "tracks.size(): " + tracks.size());

                final int totalPage=searchTrackList.getTotalPage();
                for(int page=0;page<totalPage;page++){
                    specificParams.put(DTransferConstants.PAGE,String.valueOf(page+1));
                    CommonRequest.getSearchedTracks(specificParams, new IDataCallBack<SearchTrackList>() {
                        @Override
                        public void onSuccess(SearchTrackList searchTrackList) {

                            List<Track> tempTrack=searchTrackList.getTracks();
                            List<Track> resultTrack=new ArrayList<Track>();
                            Log.d(TAG, "tempTrack.size(): "+tempTrack.size());
                            Iterator iterator=tempTrack.iterator();
                            while (iterator.hasNext()){
                                Track track= (Track) iterator.next();
                                int duration=track.getDuration();
                                //mohuaiyuan 过滤掉  时间小于1分钟和大于 6分钟的歌曲
                                if (duration>60 && duration<360){
                                    resultTrack.add(track);
                                }

                            }
                            Log.d(TAG, "resultTrack.size(): "+resultTrack.size());
                            tracks.addAll(resultTrack);
                            reflashDataSetChanged();



                            successCount++;
                            if(successCount==totalPage && successCount!=0){

                                Collections.sort(tracks, new Comparator<Track>() {
                                    @Override
                                    public int compare(Track o1, Track o2) {
                                        if (o1.getPlayCount()>o2.getPlayCount()){
                                            return -1;
                                        }else if (o1.getPlayCount()==o2.getPlayCount()){
                                            return 0;
                                        }else {
                                            return 1;
                                        }
                                    }
                                });
                                reflashDataSetChanged();

                                playTrack();
                            }


                        }

                        @Override
                        public void onError(int code, String message) {
                            Log.d(TAG, "code = [" + code + "], message = [" + message + "]");

                        }
                    });
                }


//                for (int i = 0; i < tracks.size(); i++) {
//                    Log.d(TAG, "tracks.getPlayUrl32: " + tracks.get(i).getPlayUrl32());
//                    Log.d(TAG, "tracks.getPlayUrl64: " + tracks.get(i).getPlayUrl64());
//                    Log.d(TAG, "tracks.getPlayUrl24M4a: " + tracks.get(i).getPlayUrl24M4a());
//                    Log.d(TAG, "tracks.getPlayUrl64M4a: " + tracks.get(i).getPlayUrl64M4a());
//                    Log.d(TAG, "---------------------------------------------: ");
//                }

            }

            @Override
            public void onError(int code, String message) {
                Log.d(TAG, "code = [" + code + "], message = [" + message + "]");

            }
        });

    }

    private void playTrack(){
        int position=-1;
        String keyString=songNameEditText.getText().toString();
        for(int i=0;i<tracks.size();i++){
            if (tracks.get(i).getTrackTitle().contains(keyString)){
                position=i;
                break;
            }
        }
        Log.d(TAG, "position: "+position);

        if (position==-1){
            position=0;
        }

        playTrack(position);

    }

    private void playTrack(int position) {

        mPlayerManager.playList(tracks,position);

    }

    private void initUI() {
        getCategory = (Button) findViewById(R.id.getCategory);
        getTags = (Button) findViewById(R.id.getTags);
        getAlbumList = (Button) findViewById(R.id.getAlbumList);
        getTracks = (Button) findViewById(R.id.getTracks);
        getAllTracks = (Button) findViewById(R.id.getAllTracks);
        play = (Button) findViewById(R.id.play);
        play2 = (Button) findViewById(R.id.play2);
        getDebugMsg = (Button) findViewById(R.id.getDebugMsg);

        voiceCategory= (EditText) findViewById(R.id.voiceCategory);
        voiceTagname= (EditText) findViewById(R.id.voiceTagname);
        voiceDimension= (EditText) findViewById(R.id.voiceDimension);
        getVoicesTags= (Button) findViewById(R.id.getVoiceTags);
        getVoicesList= (Button) findViewById(R.id.getVoicesList);

        songNameEditText= (EditText) findViewById(R.id.songName);
        searchSong= (Button) findViewById(R.id.searchSong);
        relevancy= (Button) findViewById(R.id.relevancy);

        tracksRecyclerView= (RecyclerView) findViewById(R.id.stracksRecyclerView);

        mMessage = (TextView) findViewById(R.id.message);
        mBtnPreSound = (ImageButton) findViewById(R.id.pre_sound);
        mBtnPlay = (ImageButton) findViewById(R.id.play_or_pause);
        mBtnNextSound = (ImageButton) findViewById(R.id.next_sound);
        mSeekBar = (SeekBar) findViewById(R.id.seek_bar);
        mSoundCover = (ImageView) findViewById(R.id.sound_cover);
        mProgress = (ProgressBar) findViewById(R.id.buffering_progress);
    }

     private static final int TO_GET_TRICK=23;
    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case TO_GET_TRICK:
                    Random random=new Random();
                    int index=random.nextInt(albums.size());
                    //mohuaiyuan 201708
//                    index=0;
                    albumId =  albums.get(index).getId();
                    getTracks(albumId);

                    break;
                default:
            }
        }
    }

    private void reflashDataSetChanged(){
        if(trackAdapter!=null){
            trackAdapter.notifyDataSetChanged();
        }
    }


    /**
     *
     * @param categoryId:category id
     * @param type: 0 album tag; 1 voice tag
     */
    private void getVoiceTags(int categoryId,int type){
        Log.d(TAG, "getTags: ");
        //音乐分类
//        categoryId = 2;
        //0-专辑标签，1-声音标签
//        type = 0;

        if (categoryId<0){
            Log.e(TAG, "cagetory<0 :");
            return;
        }
        if(type>1 || type<0){
            Log.e(TAG, "type>1 || type<0: " );
            return;
        }
        Log.d(TAG, "categoryId: "+categoryId);
        Log.d(TAG, "type: "+type);

        specificParams.put(DTransferConstants.CATEGORY_ID, String.valueOf(categoryId));
        specificParams.put(DTransferConstants.TYPE, String.valueOf(type));
        CommonRequest.getTags(specificParams, new IDataCallBack<TagList>() {

            @Override
            public void onSuccess(TagList tagList) {
                tags.addAll(tagList.getTagList());
                Log.d(TAG, "tagList = [" + tagList + "]");
                Log.d(TAG, "tagListToString: " + MyUtils.getInstance().tagListToString(tagList));

            }

            @Override
            public void onError(int code, String message) {
                Log.d(TAG, "code = [" + code + "], message = [" + message + "]");

            }
        });
    }

    private void getVoiceList(int categoryId){
        Log.d(TAG, "getVoiceList: ");

        int calcDimension=1;
        String tagName="";
        getVoiceList(false ,categoryId ,calcDimension,tagName);

    }

    private void getVoiceList(boolean isGetTrack,int categoryId , int calcDimension ,String tagName){
        Log.d(TAG, "getVoiceList: ");
        if (!specificParams.isEmpty()) {
            specificParams.clear();
        }

        if (categoryId<0){
            Log.e(TAG, "cagetory<0 :");
            return;
        }
        if (calcDimension!=1 && calcDimension!=2 && calcDimension!=3){
            Log.e(TAG, "calcDimension!=1 && calcDimension!=2 && calcDimension!=3 :");
            return;
        }
        Log.d(TAG, "isGetTrack: "+isGetTrack);
        Log.d(TAG, "categoryId: "+categoryId);
        Log.d(TAG, "calcDimension: "+calcDimension);
        Log.d(TAG, "tagName: "+tagName);


        //音乐分类
//        categoryId = 2;
        //计算维度，现支持最火（1），最新（2），经典或播放最多（3）
//        calcDimension = 1;
        //分类下对应的专辑标签，不填则为热门分类
//        tagName = "歌单";

        //返回第几页，必须大于等于1，不填默认为1
//        page = 3000;
        specificParams.put(DTransferConstants.CATEGORY_ID, String.valueOf(categoryId));
        specificParams.put(DTransferConstants.CALC_DIMENSION, String.valueOf(calcDimension));
//        specificParams.put(DTransferConstants.PAGE, String.valueOf(page));
        if(tagName!=null && tagName.length()>0){
            specificParams.put(DTransferConstants.TAG_NAME, tagName);
        }

        getVoiceList(specificParams,isGetTrack);

    }

    private void getVoiceList(final Map<String, String> specificParams, final boolean isGetTrack){
        Log.d(TAG, "getVoiceList: ");
        if (!albums.isEmpty()){
            albums.clear();
        }
        //init
        successCount=0;

        CommonRequest.getAlbumList(specificParams, new IDataCallBack<AlbumList>() {

            @Override
            public void onSuccess(AlbumList albumList) {
//                albums.addAll(albumList.getAlbums());
//                Log.d(TAG, "albumList = [" + albumList + "]");
//                Log.d(TAG, "albumListToString: " + MyUtils.getInstance().albumListToString(albumList));
                final int totalPage= albumList.getTotalPage();

                for(int i=0;i<totalPage;i++){
                    specificParams.put(DTransferConstants.PAGE,String.valueOf(i+1));
                    CommonRequest.getAlbumList(specificParams, new IDataCallBack<AlbumList>() {
                        @Override
                        public void onSuccess(AlbumList albumList) {
                            albums.addAll(albumList.getAlbums());
                            Log.d(TAG, "albumList = [" + albumList + "]");
                            successCount++;
                            Log.d(TAG, "successCount: "+successCount);
                            if(isGetTrack && successCount==totalPage && successCount!=0 ) {
                                Message message = new Message();
                                message.what = TO_GET_TRICK;
                                myHandler.sendMessage(message);
                            }
                        }

                        @Override
                        public void onError(int code, String message) {
                            Log.d(TAG, "code = [" + code + "], message = [" + message + "]");


                        }
                    });
                }

            }

            @Override
            public void onError(int code, String message) {
                Log.d(TAG, "code = [" + code + "], message = [" + message + "]");
                Toast.makeText(context,"message = [" + message + "]",Toast.LENGTH_SHORT).show();

            }
        });

    }

    private int successCount=0;
    private void getAlbumList() {
        getAlbumList(false);
    }
    private void getAlbumList(boolean isGetTrack) {
        Log.d(TAG, "getAlbumList: ");

        if (!specificParams.isEmpty()) {
            specificParams.clear();
        }

        //音乐分类
        categoryId = 2;
        //计算维度，现支持最火（1），最新（2），经典或播放最多（3）
        calcDimension = 1;
        //分类下对应的专辑标签，不填则为热门分类
//        tagName = "歌单";

        //返回第几页，必须大于等于1，不填默认为1
//        page = 3000;
        specificParams.put(DTransferConstants.CATEGORY_ID, String.valueOf(categoryId));
        specificParams.put(DTransferConstants.CALC_DIMENSION, String.valueOf(calcDimension));
//        specificParams.put(DTransferConstants.PAGE, String.valueOf(page));
//        specificParams.put(DTransferConstants.TAG_NAME, tagName);
        getAlbumList(specificParams,isGetTrack);
    }

    private void getAlbumList(final Map<String, String> specificParams, final boolean isGetTrack){
        Log.d(TAG, "getAlbumList: ");
        if (!albums.isEmpty()){
            albums.clear();
        }
        //init
        successCount=0;


        CommonRequest.getAlbumList(specificParams, new IDataCallBack<AlbumList>() {

            @Override
            public void onSuccess(AlbumList albumList) {
//                albums.addAll(albumList.getAlbums());
//                Log.d(TAG, "albumList = [" + albumList + "]");
//                Log.d(TAG, "albumListToString: " + MyUtils.getInstance().albumListToString(albumList));
                final int totalPage= albumList.getTotalPage();

                for(int i=0;i<totalPage;i++){
                    specificParams.put(DTransferConstants.PAGE,String.valueOf(i+1));
                    CommonRequest.getAlbumList(specificParams, new IDataCallBack<AlbumList>() {
                        @Override
                        public void onSuccess(AlbumList albumList) {
                            albums.addAll(albumList.getAlbums());
                            Log.d(TAG, "albumList = [" + albumList + "]");
                            successCount++;
                            Log.d(TAG, "successCount: "+successCount);
                            if(isGetTrack && successCount==totalPage && successCount!=0 ) {
                                Message message = new Message();
                                message.what = TO_GET_TRICK;
                                myHandler.sendMessage(message);
                            }
                        }

                        @Override
                        public void onError(int code, String message) {
                            Log.d(TAG, "code = [" + code + "], message = [" + message + "]");

                        }
                    });
                }

            }

            @Override
            public void onError(int code, String message) {
                Log.d(TAG, "code = [" + code + "], message = [" + message + "]");

            }
        });

    }

    private void getTracks(long albumId){

        Album album=null;
        for(int i=0;i<albums.size();i++){
            if(albums.get(i).getId()==albumId){
                album=albums.get(i);
                break;
            }
        }

        getTracksByAlbum(album);

    }

    private  void  getTracksByAlbum(Album album){
        Log.d(TAG, "getTracksByAlbum: ");
        if(album==null){
            Log.e(TAG, "album==null: ");
            return;
        }

        int pageCount=0;
        int includeTrackCount=(int)album.getIncludeTrackCount();
        pageCount=includeTrackCount/defaultPageCount;
        if(includeTrackCount%defaultPageCount!=0){
            pageCount++;
        }
        Log.d(TAG, "pageCount: "+pageCount);

        for(int page=0;page<pageCount;page++){
            if (!specificParams.isEmpty()) {
                specificParams.clear();
            }
            specificParams.put(DTransferConstants.ALBUM_ID, String.valueOf(albumId));
            specificParams.put(DTransferConstants.PAGE,String.valueOf(page+1) );

            CommonRequest.getTracks(specificParams, new IDataCallBack<TrackList>() {

                @Override
                public void onSuccess(TrackList trackList) {
                    tracks.addAll(trackList.getTracks());
                    reflashDataSetChanged();
                    Log.d(TAG, "trackList = [" + trackList + "]");
                    Log.d(TAG, "trackListToString: " + MyUtils.getInstance().trackListToString(trackList));

                }

                @Override
                public void onError(int code, String message) {
                    Log.d(TAG, "code = [" + code + "], message = [" + message + "]");

                }
            });

        }

    }

    private IXmPlayerStatusListener mPlayerStatusListener = new IXmPlayerStatusListener() {

        @Override
        public void onSoundPrepared() {
            Log.i(TAG, "onSoundPrepared");
            mSeekBar.setEnabled(true);
            mProgress.setVisibility(View.GONE);
        }

        @Override
        public void onSoundSwitch(PlayableModel laModel, PlayableModel curModel) {
            Log.i(TAG, "onSoundSwitch index:" + curModel);
            PlayableModel model = mPlayerManager.getCurrSound();
            if (model != null) {
                String title = null;
                String coverUrl = null;
                if (model instanceof Track) {
                    Track info = (Track) model;
                    title = info.getTrackTitle();
                    coverUrl = info.getCoverUrlLarge();
                } else if (model instanceof Schedule) {
                    Schedule program = (Schedule) model;
                    title = program.getRelatedProgram().getProgramName();
                    coverUrl = program.getRelatedProgram().getBackPicUrl();
                } else if (model instanceof Radio) {
                    Radio radio = (Radio) model;
                    title = radio.getRadioName();
                    coverUrl = radio.getCoverUrlLarge();
                }
                mMessage.setText(title);
                x.image().bind(mSoundCover ,coverUrl);
            }
            updateButtonStatus();
        }


        private void updateButtonStatus() {
            if (mPlayerManager.hasPreSound()) {
                mBtnPreSound.setEnabled(true);
            } else {
                mBtnPreSound.setEnabled(false);
            }
            if (mPlayerManager.hasNextSound()) {
                mBtnNextSound.setEnabled(true);
            } else {
                mBtnNextSound.setEnabled(false);
            }
        }

        @Override
        public void onPlayStop() {
            Log.i(TAG, "onPlayStop");
            mBtnPlay.setImageResource(R.drawable.widget_play_normal);
        }

        @Override
        public void onPlayStart() {
            Log.i(TAG, "onPlayStart");
            mBtnPlay.setImageResource(R.drawable.widget_pause_normal);
        }

        @Override
        public void onPlayProgress(int currPos, int duration) {
            String title = "";
            PlayableModel info = mPlayerManager.getCurrSound();
            if (info != null) {
                if (info instanceof Track) {
                    title = ((Track) info).getTrackTitle();
                } else if (info instanceof Schedule) {
                    title = ((Schedule) info).getRelatedProgram().getProgramName();
                } else if (info instanceof Radio) {
                    title = ((Radio) info).getRadioName();
                }
            }
            mMessage.setText(title + "[" + MyUtils.getInstance().formatTime(currPos) + "/" + MyUtils.getInstance().formatTime(duration) + "]");
            if (mUpdateProgress && duration != 0) {
                mSeekBar.setProgress((int) (100 * currPos / (float) duration));
            }
        }

        @Override
        public void onPlayPause() {
            Log.i(TAG, "onPlayPause");
            mBtnPlay.setImageResource(R.drawable.widget_play_normal);
        }

        @Override
        public void onSoundPlayComplete() {
            Log.i(TAG, "onSoundPlayComplete");
            mBtnPlay.setImageResource(R.drawable.widget_play_normal);
        }

        @Override
        public boolean onError(XmPlayerException exception) {
            Log.i(TAG, "onError " + exception.getMessage());
            mBtnPlay.setImageResource(R.drawable.widget_play_normal);
            return false;
        }

        @Override
        public void onBufferProgress(int position) {
            mSeekBar.setSecondaryProgress(position);
        }

        public void onBufferingStart() {
            mSeekBar.setEnabled(false);
            mProgress.setVisibility(View.VISIBLE);
        }

        public void onBufferingStop() {
            mSeekBar.setEnabled(true);
            mProgress.setVisibility(View.GONE);
        }

    };

    private IXmAdsStatusListener mAdsListener = new IXmAdsStatusListener() {

        @Override
        public void onStartPlayAds(Advertis ad, int position) {
            Log.i(TAG, "onStartPlayAds, Ad:" + ad.getName() + ", pos:" + position);
            if (ad != null) {
                x.image().bind(mSoundCover ,ad.getImageUrl());
            }
        }

        @Override
        public void onStartGetAdsInfo() {
            Log.i(TAG, "onStartGetAdsInfo");
            mBtnPlay.setEnabled(false);
            mSeekBar.setEnabled(false);
        }

        @Override
        public void onGetAdsInfo(AdvertisList ads) {
            Log.i(TAG, "onGetAdsInfo " + (ads != null));
        }

        @Override
        public void onError(int what, int extra) {
            Log.i(TAG, "onError what:" + what + ", extra:" + extra);
        }

        @Override
        public void onCompletePlayAds() {
            Log.i(TAG, "onCompletePlayAds");
            mBtnPlay.setEnabled(true);
            mSeekBar.setEnabled(true);
            PlayableModel model = mPlayerManager.getCurrSound();
            if (model != null && model instanceof Track) {
                x.image().bind(mSoundCover ,((Track) model).getCoverUrlLarge());
            }
        }

        @Override
        public void onAdsStopBuffering() {
            Log.i(TAG, "onAdsStopBuffering");
        }

        @Override
        public void onAdsStartBuffering() {
            Log.i(TAG, "onAdsStartBuffering");
        }
    };

    @Override
    protected void onDestroy() {
        if (mPlayerManager != null) {
            mPlayerManager.removePlayerStatusListener(mPlayerStatusListener);
        }
        XmPlayerManager.release();
        super.onDestroy();
    }
}
