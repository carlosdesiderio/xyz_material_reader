package com.example.xyzreader.ui;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.data.UpdaterService;
import com.squareup.picasso.Picasso;

import static com.example.xyzreader.data.UpdaterService.BROADCAST_ACTION_STATE_CHANGE;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArticleListAdapter articleListAdapter;

    private StringUtils stringUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        stringUtils = new StringUtils();

        mSwipeRefreshLayout = findViewById(R.id.article_list_swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        RecyclerView mRecyclerView = findViewById(R.id.article_list_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        articleListAdapter = new ArticleListAdapter();
        articleListAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(articleListAdapter);
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sglm);

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mRefreshingReceiver,
                new IntentFilter(BROADCAST_ACTION_STATE_CHANGE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mRefreshingReceiver);
    }

    private void refreshData() {
        startService(new Intent(this, UpdaterService.class));
    }

    private void updateRefreshingUI(boolean shouldShowRefresing) {
        mSwipeRefreshLayout.setRefreshing(shouldShowRefresing);
    }

    private boolean hasCursorData(Cursor cursor) {
        return cursor != null && cursor.getCount() > 0;
    }

    /**
     * update data refresh status
     * we are only interested to know when data has ended loading
     * in order to hide refresh layout loading bar
     */
    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BROADCAST_ACTION_STATE_CHANGE)) {
                boolean isRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING,
                        false);
                if(!isRefreshing) {
                    updateRefreshingUI(isRefreshing);
                }
            }
        }
    };

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        articleListAdapter.swapCursor(cursor);
        updateRefreshingUI(!hasCursorData(cursor));

        // load data if database doesn't return empty cursor
        if(!hasCursorData(cursor)) {
            refreshData();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        articleListAdapter.swapCursor(null);
    }

    // TODO: 28/02/2018 put this adapter in its own class
    private class ArticleListAdapter extends RecyclerView.Adapter<ViewHolder> {
        private Cursor mCursor;

        public ArticleListAdapter() {}

        @Override
        public long getItemId(int position) {
            mCursor.moveToPosition(position);
            mCursor.getPosition();
            return mCursor.getLong(ArticleLoader.Query._ID);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_article, parent,
                    false);
            final ViewHolder vh = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition()))));
                }
            });
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            mCursor.moveToPosition(position);
            holder.titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));

            String detailsString = stringUtils.getListItemSubtitleString(
                    mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE),
                    mCursor.getString(ArticleLoader.Query.AUTHOR));
            holder.subtitleView.setText(detailsString);

            Picasso.with(ArticleListActivity.this).load(mCursor.getString(ArticleLoader.Query
                    .THUMB_URL)).into(holder.thumbnailView);
            holder.thumbnailView.setAspectRatio(mCursor.getFloat(ArticleLoader.Query.ASPECT_RATIO));
        }


        @Override
        public int getItemCount() {
            if(mCursor != null) {
                return mCursor.getCount();
            }
            return 0;
        }

        public void swapCursor(Cursor cursor) {
            this.mCursor = cursor;
            notifyDataSetChanged();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public DynamicHeightNetworkImageView thumbnailView;
        public TextView titleView;
        public TextView subtitleView;

        public ViewHolder(View itemView) {
            super(itemView);
            thumbnailView = itemView.findViewById(R.id.article_list_item_thumbnail);
            titleView = itemView.findViewById(R.id.article_list_item_title);
            subtitleView = itemView.findViewById(R.id.article_list_item_subtitle);
        }
    }
}
