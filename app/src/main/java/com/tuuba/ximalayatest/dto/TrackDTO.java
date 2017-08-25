package com.tuuba.ximalayatest.dto;

import com.google.gson.Gson;
import com.ximalaya.ting.android.opensdk.model.track.Track;

/**
 * Created by YF-04 on 2017/8/24.
 */

public class TrackDTO {
    public static final String DEFAULT_TRACK_KIND="track";
    private String kind;
    private String playUrl32;
    private String playUrl64;
    private String playUrl24M4a;
    private String playUrl64M4a;

    public TrackDTO(){

    }

    public TrackDTO(Track track)throws Exception{
        if (track==null){
            throw new Exception("TrackDTO init Error!");
        }
        boolean initState=false;
        this.kind=DEFAULT_TRACK_KIND;

        String play_url_32=track.getPlayUrl32();
        if(play_url_32!=null){
            initState=true;
            this.playUrl32=play_url_32;
        }
        String play_url_64=track.getPlayUrl64();
        if(play_url_64!=null){
            initState=true;
            this.playUrl64=play_url_64;
        }
        String play_url_24_m4a=track.getPlayUrl24M4a();
        if(play_url_24_m4a!=null){
            initState=true;
            this.playUrl24M4a=play_url_24_m4a;
        }
        String play_url_64_m4a=track.getPlayUrl64M4a();
        if(play_url_64_m4a!=null){
            initState=true;
            this.playUrl64M4a=play_url_64_m4a;
        }
        if(!initState){
            throw new Exception("TrackDTO init Error!");
        }

    }

    public TrackDTO(String json)throws Exception{
        Gson gson=new Gson();
        TrackDTO trackDTO=gson.fromJson(json,TrackDTO.class);
        this.kind=DEFAULT_TRACK_KIND;
        this.playUrl32=trackDTO.getPlayUrl32();
        this.playUrl64=trackDTO.getPlayUrl64();
        this.playUrl24M4a=trackDTO.getPlayUrl24M4a();
        this.playUrl64M4a=trackDTO.getPlayUrl64M4a();
        if(playUrl32==null && playUrl64==null && playUrl24M4a==null && playUrl64M4a==null){
            throw  new Exception("init TrackDTO error!");
        }

    }

    public Track createTrack()throws Exception{
        Track track=new Track();
        boolean initTrack=false;
        track.setKind(DEFAULT_TRACK_KIND);
        if (playUrl32!=null){
            initTrack=true;
            track.setPlayUrl32(playUrl32);
        }
        if(playUrl64!=null){
            initTrack=true;
            track.setPlayUrl64(playUrl64);
        }
        if (playUrl24M4a!=null){
            initTrack=true;
            track.setPlayUrl24M4a(playUrl24M4a);
        }
        if(playUrl64M4a!=null){
            initTrack=true;
            track.setPlayUrl64M4a(playUrl64M4a);
        }
        if(!initTrack){
            throw  new Exception("init Track error!");
        }

        return track;

    }

    public String toJsonString(){
        Gson gson=new Gson();
        String trackToString=gson.toJson(this);
        return trackToString;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getPlayUrl32() {
        return playUrl32;
    }

    public void setPlayUrl32(String playUrl32) {
        this.playUrl32 = playUrl32;
    }

    public String getPlayUrl64() {
        return playUrl64;
    }

    public void setPlayUrl64(String playUrl64) {
        this.playUrl64 = playUrl64;
    }

    public String getPlayUrl24M4a() {
        return playUrl24M4a;
    }

    public void setPlayUrl24M4a(String playUrl24M4a) {
        this.playUrl24M4a = playUrl24M4a;
    }

    public String getPlayUrl64M4a() {
        return playUrl64M4a;
    }

    public void setPlayUrl64M4a(String playUrl64M4a) {
        this.playUrl64M4a = playUrl64M4a;
    }
}
