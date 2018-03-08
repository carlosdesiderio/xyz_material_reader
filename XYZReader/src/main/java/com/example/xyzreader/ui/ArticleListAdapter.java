package com.example.xyzreader.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.squareup.picasso.Picasso;

/**
 * Adapter for the {@link ArticleListActivity} article list
 */

public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ArticleListViewHolder> {

    private StringUtils stringUtils;
    private Context context;
    private Cursor cursor;

    public ArticleListAdapter(Context context) {
        this.stringUtils = new StringUtils();
        this.context = context;
    }

    @Override
    public long getItemId(int position) {
        cursor.moveToPosition(position);
        cursor.getPosition();
        return cursor.getLong(ArticleLoader.Query._ID);
    }

    @Override
    public ArticleListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_article, parent,
                false);
        final ArticleListViewHolder vh = new ArticleListViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(Intent.ACTION_VIEW,
                        ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition()))));
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(final ArticleListViewHolder holder, final int position) {
        cursor.moveToPosition(position);
        holder.titleView.setText(cursor.getString(ArticleLoader.Query.TITLE));

        String detailsString = stringUtils.getListItemSubtitleString(
                cursor.getString(ArticleLoader.Query.PUBLISHED_DATE),
                cursor.getString(ArticleLoader.Query.AUTHOR));
        holder.subtitleView.setText(detailsString);

        Picasso.with(context)
                .load(cursor.getString(ArticleLoader.Query.THUMB_URL))
                .error(R.drawable.empty_detail)
                .into(holder.thumbnailView);
        holder.thumbnailView.setAspectRatio(cursor.getFloat(ArticleLoader.Query.ASPECT_RATIO));
    }

    @Override
    public int getItemCount() {
        if (cursor != null) {
            return cursor.getCount();
        }
        return 0;
    }

    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    public static class ArticleListViewHolder extends RecyclerView.ViewHolder {
        public DynamicHeightNetworkImageView thumbnailView;
        public TextView titleView;
        public TextView subtitleView;

        public ArticleListViewHolder(View itemView) {
            super(itemView);
            thumbnailView = itemView.findViewById(R.id.article_list_item_thumbnail);
            titleView = itemView.findViewById(R.id.article_list_item_title);
            subtitleView = itemView.findViewById(R.id.article_list_item_subtitle);
        }
    }
}
