package com.tuuba.ximalayatest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tuuba.ximalayatest.R;
import com.tuuba.ximalayatest.utils.MyUtils;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by YF-04 on 2017/8/18.
 */

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> {

    private static final String TAG = "TrackAdapter";

    private Context context;
    private List<Track> list;
    private OnItemClickListener onItemClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView titleTextView;
        private TextView durationTextView;
        private TextView playCountTextView;
        private TextView favouriteCountTextView;
        private TextView downloadCountTextView;

        public ViewHolder(View view) {
            super(view);
            titleTextView= (TextView) view.findViewById(R.id.title);
            durationTextView= (TextView) view.findViewById(R.id.duration);
            playCountTextView= (TextView) view.findViewById(R.id.playCount);
            favouriteCountTextView= (TextView) view.findViewById(R.id.favouriteCount);
            downloadCountTextView= (TextView) view.findViewById(R.id.downloadCount);
        }
    }

   public TrackAdapter(Context context,List<Track> list){
       this.list=list;
       this.context=context;
   }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.track_item_layout,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemClickListener!=null){
                    onItemClickListener.onItemClick(view,(Integer) view.getTag());
                }
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ViewHolder cellViewHolder = (ViewHolder) holder;

        cellViewHolder.itemView.setTag(position);

        Track track=list.get(position);
        holder.titleTextView.setText(context.getResources().getString(R.string.track_title)+track.getTrackTitle());
        holder.durationTextView.setText(context.getResources().getString(R.string.duration)+MyUtils.formatTime(track.getDuration()));
        holder.playCountTextView.setText(context.getResources().getString(R.string.play_count)+track.getPlayCount());
        holder.favouriteCountTextView.setText(context.getResources().getString(R.string.favourite_count)+track.getFavoriteCount());
        holder.downloadCountTextView.setText(context.getResources().getString(R.string.download_count)+track.getDownloadCount());

    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }


    public interface OnItemClickListener{
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
