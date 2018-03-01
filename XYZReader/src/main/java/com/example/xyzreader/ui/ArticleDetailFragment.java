package com.example.xyzreader.ui;

import android.database.Cursor;
import android.support.v4.app.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.squareup.picasso.Picasso;

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link ArticleListActivity} in two-pane mode (on
 * tablets) or a {@link ArticleDetailActivity} on handsets.
 */
public class ArticleDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ArticleDetailFragment";

    public static final String ARG_ITEM_ID = "item_id";
    public static final String LIST_TOP_PADDING = "\r\n";

    private Cursor mCursor;
    private long mItemId;

    private View mRootView;
    private TextView titleTextView;
    private TextView dateTextView;
    private TextView authorTextView;
    private ArticleAdapter bodyViewAdapter;
    private ImageView mPhotoView;
    private CollapsingToolbarLayout collapsingBar;

    private StringUtils stringUtils;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {
    }

    public static ArticleDetailFragment newInstance(long itemId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_ITEM_ID, itemId);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        stringUtils = new StringUtils();

        if (getArguments() != null && getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getLong(ARG_ITEM_ID);
        }
    }

    private ArticleDetailActivity getActivityCast() {
        return (ArticleDetailActivity) getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // In support library r8, calling initLoader for a fragment in a FragmentPagerAdapter in
        // the fragment's onCreate may cause the same LoaderManager to be dealt to multiple
        // fragments because their mIndex is -1 (haven't been added to the activity yet). Thus,
        // we do this in onActivityCreated.
        getLoaderManager().initLoader(0, null, this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);

        mPhotoView = mRootView.findViewById(R.id.article_detail_bg_photo);

        titleTextView = mRootView.findViewById(R.id.article_detail_big_title);
        dateTextView = mRootView.findViewById(R.id.article_detail_date_text_field);
        authorTextView = mRootView.findViewById(R.id.article_detail_author_text_field);

        RecyclerView bodyView = mRootView.findViewById(R.id.article_detail_recycler_view);
        bodyView.setHasFixedSize(true);

        bodyViewAdapter = new ArticleAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getActivity(),
                LinearLayoutManager.VERTICAL,
                false);
        bodyView.setLayoutManager(layoutManager);
        bodyView.setAdapter(bodyViewAdapter);

        collapsingBar = mRootView.findViewById(R.id.article_detail_collapsing);

        final Toolbar toolbar = mRootView.findViewById(R.id.article_detail_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(getActivity());
            }
        });

        // TODO: 27/02/2018 see if that can be integrated otherwise delete
       // bodyView.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Rosario-Regular.ttf"));

        setRootViewVisibility(View.GONE);
        return mRootView;
    }

    /**
     * hides root view onCreateView then show it when data is available
     */
    private void setRootViewVisibility(int visibility) {
        mRootView.setVisibility(visibility);
    }

    private void bindViews() {
        if(mRootView != null && mCursor != null) {
            String title = mCursor.getString(ArticleLoader.Query.TITLE);
            collapsingBar.setTitle(title);
            titleTextView.setText(title);

            String authorString = mCursor.getString(ArticleLoader.Query.AUTHOR);
            String dateString = stringUtils.getArticleDetailDateString(
                    mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE));

            dateTextView.setText(dateString);
            authorTextView.setText(authorString);

            new ArticleFormattingAsyncTask().execute(mCursor.getString(ArticleLoader.Query.BODY));

            Picasso.with(getActivity())
                    .load(mCursor.getString(ArticleLoader.Query.PHOTO_URL))
                    .into(mPhotoView);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newInstanceForItemId(getActivity(), mItemId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (!isAdded()) {
            if (cursor != null) {
                cursor.close();
            }
            return;
        }
        mCursor = cursor;

        if (mCursor != null && !mCursor.moveToFirst()) {
            mCursor.close();
            mCursor = null;
        }

        bindViews();
        setRootViewVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        bindViews();
    }

    /**
     * removes duplicated text inside square brackets which appear in raw article string returned
     * by the server and italizes text between asterics
     */
    private class ArticleFormattingAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
             return strings[0]
                     .replaceAll("\\*([^\\*]*)\\*", "<i>$1</i>")
                     .replaceAll("\\[([^]]+)\\]", "");
        }

        @Override
        protected void onPostExecute(String cleanArticleString) {
            bodyViewAdapter.swapTextContent(LIST_TOP_PADDING + cleanArticleString);
        }
    }
}