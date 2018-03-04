package com.example.xyzreader.ui;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;

import static android.support.v4.view.ViewPager.SCROLL_STATE_DRAGGING;
import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;
import static android.support.v4.view.ViewPager.SCROLL_STATE_SETTLING;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ArticleDetailActivity.class.getSimpleName();

    private Cursor mCursor;
    // TODO: 28/02/2018 see if I can improve the logic around mStartId
    private long mStartId;

    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;
    private FloatingActionButton fab;
    private View containerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        getSupportLoaderManager().initLoader(0, null, this);

        containerView = findViewById(R.id.article_details_activity_root_view);

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager = findViewById(R.id.article_detail_pager);
        mPager.setAdapter(mPagerAdapter);

        fab = findViewById(R.id.article_detail_share_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentItemPos = mPager.getCurrentItem();
                mCursor.moveToPosition(currentItemPos);
                String name = mCursor.getString(ArticleLoader.Query.TITLE);
                String author = mCursor.getString(ArticleLoader.Query.AUTHOR);

                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from
                        (ArticleDetailActivity.this)
                        .setType("text/plain")
                        .setChooserTitle(name)
                        .setSubject(name)
                        .setText(getString(R.string.shared_article_message, author))
                        .getIntent(), getString(R.string.action_share)));
            }
        });


        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getData() != null) {
                mStartId = ItemsContract.Items.getItemId(getIntent().getData());
            }
        }


        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position,
                                       float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

                switch (state) {
                    case SCROLL_STATE_IDLE:
                        fab.show();
                        break;
                    case SCROLL_STATE_DRAGGING:
                        fab.hide();
                        break;
                    case SCROLL_STATE_SETTLING:
                        break;
                }

            }
        });
    }

    private void setPagerPositionOnInit() {
        if (mStartId > 0) {
            mCursor.moveToFirst();
            do {
                if (mStartId == mCursor.getLong(ArticleLoader.Query._ID)) {
                    final int position = mCursor.getPosition();
                    mPager.setCurrentItem(position, false);
                    break;
                }
            } while (mCursor.moveToNext());
            mStartId = 0;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mCursor = cursor;
        mPagerAdapter.notifyDataSetChanged();

        setPagerPositionOnInit();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        mPagerAdapter.notifyDataSetChanged();
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            mCursor.moveToPosition(position);
            return ArticleDetailFragment.newInstance(mCursor.getLong(ArticleLoader.Query._ID));
        }

        @Override
        public int getCount() {
            return (mCursor != null) ? mCursor.getCount() : 0;
        }
    }
}
