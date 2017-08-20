package com.tuuba.ximalayatest;

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tuuba.ximalayatest.R;
import com.tuuba.ximalayatest.adapter.TrackAdapter;
import com.tuuba.ximalayatest.utils.MyUtils;
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
import com.ximalaya.ting.android.opensdk.model.tag.TagList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.appnotification.XmNotificationCreater;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";


    private XmPlayerManager mPlayerManager;
    private CommonRequest mXimalaya;

    private boolean mUpdateProgress = true;

    private String mAppSecret;
    private Context context;

    private Button getCategory;
    private Button getTags;
    private Button getAlbumList;
    private Button getTracks;
    private Button play;
    private Button play2;
    private RecyclerView tracksRecyclerView;

    private TrackAdapter trackAdapter;


    private Map<String, String> specificParams;
    private int categoryId;
    private int type;
    private int calcDimension;
    private String tagName;
    private int albumId;

    //midea play
    private TextView mMessage;
    private ImageButton mBtnPreSound;
    private ImageButton mBtnPlay;
    private ImageButton mBtnNextSound;
    private SeekBar mSeekBar;
    private ImageView mSoundCover;
    private ProgressBar mProgress;

    private List<Album>albums=new ArrayList<>();
    private List<Track> tracks=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");

        context=this;

        initXimalaya();
        initUI();

        LinearLayoutManager manager=new LinearLayoutManager(context);
        tracksRecyclerView.setLayoutManager(manager);
        trackAdapter=new TrackAdapter(context,tracks);
        tracksRecyclerView.setAdapter(trackAdapter);

        initListener();


        specificParams = new HashMap<String, String>();



    }


    /**
     * init
     */
    private void initXimalaya() {
        Log.d(TAG, "initXimalaya: ");
        mXimalaya = CommonRequest.getInstanse();
        mAppSecret = context.getResources().getString(R.string.app_secret);
        mXimalaya.init(context, mAppSecret);

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

                mXimalaya.setDefaultPagesize(50);
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
                Log.d(TAG, "getAlbumList  onClick: ");
                if (!specificParams.isEmpty()) {
                    specificParams.clear();
                }

                //音乐分类
                categoryId = 2;
                //计算维度，现支持最火（1），最新（2），经典或播放最多（3）
                calcDimension = 1;
                //分类下对应的专辑标签，不填则为热门分类
                tagName = "歌单";
                specificParams.put(DTransferConstants.CATEGORY_ID, String.valueOf(categoryId));
                specificParams.put(DTransferConstants.CALC_DIMENSION, String.valueOf(calcDimension));
                specificParams.put(DTransferConstants.TAG_NAME, tagName);
                CommonRequest.getAlbumList(specificParams, new IDataCallBack<AlbumList>() {

                    @Override
                    public void onSuccess(AlbumList albumList) {
                        albums.addAll(albumList.getAlbums());
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
                Log.d(TAG, "getTracks onClick: ");
                if (!specificParams.isEmpty()) {
                    specificParams.clear();
                }

                albumId = 195926;
                specificParams.put(DTransferConstants.ALBUM_ID, String.valueOf(albumId));
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
                Track track=new Track();



                tracks.add(track);
                mPlayerManager.playList(tracks,0);

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
                mXimalaya.setDefaultPagesize(100);
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

    private void initUI() {
        getCategory = (Button) findViewById(R.id.getCategory);
        getTags = (Button) findViewById(R.id.getTags);
        getAlbumList = (Button) findViewById(R.id.getAlbumList);
        getTracks = (Button) findViewById(R.id.getTracks);
        play = (Button) findViewById(R.id.play);
        play2 = (Button) findViewById(R.id.play2);
        tracksRecyclerView= (RecyclerView) findViewById(R.id.stracksRecyclerView);

        mMessage = (TextView) findViewById(R.id.message);
        mBtnPreSound = (ImageButton) findViewById(R.id.pre_sound);
        mBtnPlay = (ImageButton) findViewById(R.id.play_or_pause);
        mBtnNextSound = (ImageButton) findViewById(R.id.next_sound);
        mSeekBar = (SeekBar) findViewById(R.id.seek_bar);
        mSoundCover = (ImageView) findViewById(R.id.sound_cover);
        mProgress = (ProgressBar) findViewById(R.id.buffering_progress);
    }

    private void reflashDataSetChanged(){
        if(trackAdapter!=null){
            trackAdapter.notifyDataSetChanged();
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
