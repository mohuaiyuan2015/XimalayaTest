package com.tuuba.ximalayatest.utils;

import android.util.Log;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.category.Category;
import com.ximalaya.ting.android.opensdk.model.category.CategoryList;
import com.ximalaya.ting.android.opensdk.model.tag.Tag;
import com.ximalaya.ting.android.opensdk.model.tag.TagList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.List;

/**
 * Created by YF-04 on 2017/8/17.
 */

public class MyUtils {

    private static final String TAG = "MyUtils";

    /**
     * one hour (ms)
     */
    private static final int ONE_HOUR = 1 * 60 * 60 * 1000;
    /**
     * one minute(ms)
     */
    private static final int ONE_MIN = 1 * 60 * 1000;
    /**
     * one second (ms)
     */
    private static final int ONE_SECOND = 1 * 1000;

    private static MyUtils myUtils=new MyUtils();

    public  static MyUtils getInstance(){
        return myUtils;
    }


    /**HH:mm:ss*/
    public static String formatTime(long ms) {
        StringBuilder sb = new StringBuilder();
        int hour = (int) (ms / ONE_HOUR);
        int min = (int) ((ms % ONE_HOUR) / ONE_MIN);
        int sec = (int) (ms % ONE_MIN) / ONE_SECOND;
        if (hour == 0) {
//			sb.append("00:");
        } else if (hour < 10) {
            sb.append("0").append(hour).append(":");
        } else {
            sb.append(hour).append(":");
        }
        if (min == 0) {
            sb.append("00:");
        } else if (min < 10) {
            sb.append("0").append(min).append(":");
        } else {
            sb.append(min).append(":");
        }
        if (sec == 0) {
            sb.append("00");
        } else if (sec < 10) {
            sb.append("0").append(sec);
        } else {
            sb.append(sec);
        }
        return sb.toString();
    }

    /**
     * HH:mm:ss
     * @param seconds:s
     * @return
     */
    public static String formatTime(int seconds) {
        StringBuilder sb = new StringBuilder();
        int oneHour=60*60;
        int oneMin=60;

        int hour = seconds / oneHour;
        int min = (seconds % oneHour) / oneMin;
        int sec = seconds%oneMin;
        if (hour == 0) {
//			sb.append("00:");
        } else if (hour < 10) {
            sb.append("0").append(hour).append(":");
        } else {
            sb.append(hour).append(":");
        }
        if (min == 0) {
            sb.append("00:");
        } else if (min < 10) {
            sb.append("0").append(min).append(":");
        } else {
            sb.append(min).append(":");
        }
        if (sec == 0) {
            sb.append("00");
        } else if (sec < 10) {
            sb.append("0").append(sec);
        } else {
            sb.append(sec);
        }
        return sb.toString();
    }

    public String categoryListToString(CategoryList categoryList){
        Log.d(TAG, "categoryListToString: ");
        StringBuffer stringBuffer=new StringBuffer();
        List<Category> categories=categoryList.getCategories();
        stringBuffer.append("{");
        int size=categories.size();
        Log.d(TAG, "categories.size: "+categories.size());
        for(int i=0;i<size;i++){
            stringBuffer.append("[");
            Category category =categories.get(i);
            long id=category.getId();
            stringBuffer.append("id="+id+",");
            String kind=category.getKind();
            stringBuffer.append("kind="+kind+",");
            String categoryName=category.getCategoryName();
            stringBuffer.append("categoryName="+categoryName+",");

            stringBuffer.append("]");
        }
        stringBuffer.append("}");

        return stringBuffer.toString();
    }

    public String tagListToString(TagList tagList){
        Log.d(TAG, "tagListToString: ");
        StringBuffer stringBuffer=new StringBuffer();
        List<Tag> tags =tagList.getTagList();
        stringBuffer.append("{");
        int size=tags.size();
        Log.d(TAG, "categories.size: "+tags.size());
        for(int i=0;i<size;i++){
            stringBuffer.append("[");
            Tag tag =tags.get(i);
            String kind=tag.getKind();
            stringBuffer.append("kind="+kind+",");
            String tagName=tag.getTagName();
            stringBuffer.append("tagName="+tagName+",");

            stringBuffer.append("]");
        }
        stringBuffer.append("}");

        return stringBuffer.toString();

    }

    public String albumListToString(AlbumList albumList){
        Log.d(TAG, "albumListToString: ");
        StringBuffer stringBuffer=new StringBuffer();
        List<Album> albums =albumList.getAlbums();
        stringBuffer.append("{");
        int size=albums.size();
        Log.d(TAG, "categories.size: "+albums.size());
        for(int i=0;i<size;i++){
            stringBuffer.append("[");
            Album album =albums.get(i);
            long id=album.getId();
            stringBuffer.append("id="+id+",");
            String title=album.getAlbumTitle();
            stringBuffer.append("title="+title+",");
            long includeTrackCount=album.getIncludeTrackCount();
            stringBuffer.append("includeTrackCount="+includeTrackCount+",");

            stringBuffer.append("]");
        }
        stringBuffer.append("}");

        return stringBuffer.toString();

    }

    public String trackListToString(TrackList trackList){
        Log.d(TAG, "albumListToString: ");
        StringBuffer stringBuffer=new StringBuffer();
        List<Track>tracks=trackList.getTracks();
        stringBuffer.append("{");
        int size=tracks.size();
        Log.d(TAG, "categories.size: "+tracks.size());
        for(int i=0;i<size;i++){
            stringBuffer.append("[");
            Track track=tracks.get(i);

//            long trackActivityId=track.getTrackActivityId();
//            stringBuffer.append("trackActivityId="+trackActivityId+",");
//
//            int priceTypeId=track.getPriceTypeId();
//            stringBuffer.append("priceTypeId="+priceTypeId+",");
//
//            long liveRoomId=track.getLiveRoomId();
//            stringBuffer.append("liveRoomId="+liveRoomId+",");
//
//            String templateUrl=track.getTemplateUrl();
//            stringBuffer.append("templateUrl="+templateUrl+",");

            long dataId=track.getDataId();
            stringBuffer.append("dataId="+dataId+",");
//
            int orderNum=track.getOrderNum();
            stringBuffer.append("orderNum="+orderNum+",");
//
//            long radioId=track.getRadioId();
//            stringBuffer.append("radioId="+radioId+",");
//
//            long programId=track.getProgramId();
//            stringBuffer.append("programId="+programId+",");
//
//            long scheduleId=track.getScheduleId();
//            stringBuffer.append("scheduleId="+scheduleId+",");
//
//            String sequenceId=track.getSequenceId();
//            stringBuffer.append("sequenceId="+sequenceId+",");
//
//            long templateId=track.getTemplateId();
//            stringBuffer.append("templateId="+templateId+",");

            String title=track.getTrackTitle();
            stringBuffer.append("title="+title+",");

            int duration=track.getDuration();
            stringBuffer.append("duration="+duration+",");

            stringBuffer.append("]");
        }
        stringBuffer.append("}");

        return stringBuffer.toString();

    }

}
