package com.example.xyzreader.ui;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.UpdaterService;

import uk.me.desiderio.popularmovies.network.ConnectivityUtils;
import uk.me.desiderio.popularmovies.network.ConnectivityUtils.ConnectivityState;

import static com.example.xyzreader.data.UpdaterService.BROADCAST_ACTION_STATE_CHANGE;
import static uk.me.desiderio.popularmovies.network.ConnectivityUtils.CONNECTED;
import static uk.me.desiderio.popularmovies.network.ConnectivityUtils.DISCONNECTED;
import static uk.me.desiderio.popularmovies.view.ViewUtils.getSnackbar;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details.
 */
public class ArticleListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ConnectivityManager.OnNetworkActiveListener {

    private View rootView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArticleListAdapter articleListAdapter;

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
                if (!isRefreshing) {
                    updateRefreshingUI(isRefreshing);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        rootView = findViewById(android.R.id.content);

        final Toolbar toolbar = findViewById(R.id.article_list_toolbar);
        setSupportActionBar(toolbar);

        swipeRefreshLayout = findViewById(R.id.article_list_swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        RecyclerView mRecyclerView = findViewById(R.id.article_list_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        articleListAdapter = new ArticleListAdapter(this);
        articleListAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(articleListAdapter);
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sglm);

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.addDefaultNetworkActiveListener(this);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                refreshData();
                updateRefreshingUI(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onNetworkActive() {
        showSnackBar(CONNECTED);
    }

    private void showSnackBar(@ConnectivityState int connectivityState) {
        getSnackbar(connectivityState, rootView).show();
    }

    private void refreshData() {
        int isConnected = ConnectivityUtils.checkConnectivity(this);

        switch (isConnected) {
            case CONNECTED:
                startService(new Intent(this, UpdaterService.class));
                break;
            case DISCONNECTED:
                updateRefreshingUI(false);
                showSnackBar(DISCONNECTED);
                break;
        }
    }

    private void updateRefreshingUI(boolean shouldShowRefresing) {
        swipeRefreshLayout.setRefreshing(shouldShowRefresing);
    }

    private boolean hasCursorData(Cursor cursor) {
        return cursor != null && cursor.getCount() > 0;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        articleListAdapter.swapCursor(cursor);
        updateRefreshingUI(!hasCursorData(cursor));

        // load data if database doesn't return empty cursor
        if (!hasCursorData(cursor)) {
            refreshData();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        articleListAdapter.swapCursor(null);
    }
}