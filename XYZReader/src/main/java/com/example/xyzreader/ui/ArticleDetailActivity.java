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
 * An activity showing the article;s detail screen. The activity holds the set of articles that
 * the user can see by swiping left or right between them
 */
public class ArticleDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ArticleDetailActivity.class.getSimpleName();

    private Cursor mCursor;
    private long initialArticleId;

    private ViewPager mPager;
    private MyPagerAdapterTransformer adapterTransformer;
    private MyPagerAdapter mPagerAdapter;
    private FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        getSupportLoaderManager().initLoader(0, null, this);

        View containerView = findViewById(R.id.article_details_activity_root_view);

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        adapterTransformer = new MyPagerAdapterTransformer();
        mPager = findViewById(R.id.article_detail_pager);
        mPager.setPageTransformer(false,
                                  adapterTransformer);
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
                initialArticleId = ItemsContract.Items.getItemId(getIntent().getData());
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

    private void setPagerCurrentItemOnInit() {
        if (initialArticleId > 0) {
            mCursor.moveToFirst();
            do {
                if (initialArticleId == mCursor.getLong(ArticleLoader.Query._ID)) {
                    final int position = mCursor.getPosition();
                    mPager.setCurrentItem(position, false);
                    break;
                }
            } while (mCursor.moveToNext());
            initialArticleId = 0;
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

        setPagerCurrentItemOnInit();
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

    private class MyPagerAdapterTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View view, float position) {
            if(position > -1 && position < 1 ) {
                int pageWidth = view.getWidth();

                View imageView = view.findViewById(R.id.article_detail_bg_photo);
                float alpha = Math.min((1 - Math.abs(position)) + 0.3f, 1);
                view.setAlpha(alpha);

                imageView.setTranslationX(-position * (pageWidth / 2)); //Half the normal speed
            }
        }
    }
}
