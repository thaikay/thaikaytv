package com.thaikaytv.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.thaikaytv.app.models.Channel;
import java.util.List;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ViewHolder> {
    private List<Channel> channels;
    private Context context;
    private OnChannelClickListener listener;
    private OnChannelLongClickListener longClickListener;

    public interface OnChannelClickListener {
        void onChannelClick(Channel channel);
    }

    public interface OnChannelLongClickListener {
        void onChannelLongClick(Channel channel);
    }

    public ChannelAdapter(Context context, List<Channel> channels) {
        this.context = context;
        this.channels = channels;
    }

    public void setOnChannelClickListener(OnChannelClickListener listener) {
        this.listener = listener;
    }

    public void setOnChannelLongClickListener(OnChannelLongClickListener listener) {
        this.longClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_channel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Channel channel = channels.get(position);
        holder.channelName.setText(channel.getName());

        if (!channel.getLogo().isEmpty()) {
            Glide.with(context)
                .load(channel.getLogo())
                .placeholder(R.drawable.ic_tv)
                .error(R.drawable.ic_tv)
                .into(holder.channelLogo);
        } else {
            holder.channelLogo.setImageResource(R.drawable.ic_tv);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onChannelClick(channel);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onChannelLongClick(channel);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return channels.size();
    }

    public void updateChannels(List<Channel> newChannels) {
        this.channels = newChannels;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView channelLogo;
        TextView channelName;

        ViewHolder(View itemView) {
            super(itemView);
            channelLogo = itemView.findViewById(R.id.channel_logo);
            channelName = itemView.findViewById(R.id.channel_name);
        }
    }
}
