package net.mnafian.androidannotationsample.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.mnafian.androidannotationsample.Item.NewsFeedItem;
import net.mnafian.androidannotationsample.R;

import java.util.List;

/**
 * Created by mnafian on 4/29/15.
 */
public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.NewsFeedHolder> {

    private List<NewsFeedItem> newsList;
    private Context mContext;

    public NewsFeedAdapter(Context context, List<NewsFeedItem> newsList) {
        super();
        this.mContext = context;
        this.newsList = newsList;
    }

    @Override
    public NewsFeedHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.news_feed_data_item, parent, false);
        NewsFeedHolder viewHolder = new NewsFeedHolder(mContext, itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NewsFeedHolder holder, int position) {
        NewsFeedItem newsfeed = newsList.get(position);
        holder.viewTittle.setText(newsfeed.getNewsTittle());
        holder.viewDate.setText(newsfeed.getNewsDate());
        holder.viewContent.setText(newsfeed.getNewsContent());
        Glide.with(mContext)
                .load(newsfeed.getNewsImageUrl())
                .into(holder.viewImageThumbnail);
    }

    @Override
    public int getItemCount() {
        return (null != newsList ? newsList.size() : 0);
    }

    public class NewsFeedHolder extends RecyclerView.ViewHolder {
        public TextView viewTittle;
        public TextView viewContent;
        public TextView viewDate;
        public ImageView viewImageThumbnail;

        public NewsFeedHolder(final Context context, View itemView) {
            super(itemView);
            viewTittle = (TextView) itemView.findViewById(R.id.news_tittle);
            viewDate = (TextView) itemView.findViewById(R.id.news_date);
            viewContent = (TextView) itemView.findViewById(R.id.news_content);
            viewImageThumbnail = (ImageView) itemView.findViewById(R.id.news_photo);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(newsList.get(getAdapterPosition()).getNewsUrl()));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.getApplicationContext().startActivity(i);
                }
            });


        }
    }
}
