package com.example.xyzreader.ui;

import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

    private Cursor cursor;
    private long itemId;

    private View rootView;
    private TextView titleTextView;
    private TextView dateTextView;
    private TextView authorTextView;
    private ArticleContentAdapter bodyViewAdapter;
    private ImageView backdropImageView;
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
            itemId = getArguments().getLong(ARG_ITEM_ID);
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
        rootView = inflater.inflate(R.layout.fragment_article_detail, container, false);

        backdropImageView = rootView.findViewById(R.id.article_detail_bg_photo);

        titleTextView = rootView.findViewById(R.id.article_detail_big_title);
        dateTextView = rootView.findViewById(R.id.article_detail_date_text_field);
        authorTextView = rootView.findViewById(R.id.article_detail_author_text_field);

        RecyclerView bodyView = rootView.findViewById(R.id.article_detail_recycler_view);
        bodyView.setHasFixedSize(true);

        bodyViewAdapter = new ArticleContentAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getActivity(),
                LinearLayoutManager.VERTICAL,
                false);
        bodyView.setLayoutManager(layoutManager);
        bodyView.setAdapter(bodyViewAdapter);

        collapsingBar = rootView.findViewById(R.id.article_detail_collapsing);

        final Toolbar toolbar = rootView.findViewById(R.id.article_detail_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(getActivity());
            }
        });
        
        setRootViewVisibility(View.GONE);
        return rootView;
    }

    /**
     * hides root view onCreateView then show it when data is available
     */
    private void setRootViewVisibility(int visibility) {
        rootView.setVisibility(visibility);
    }

    // TODO: 02/03/2018 set color of title bg base on pictures
    private void bindViews() {
        if(rootView != null && cursor != null) {
            String title = cursor.getString(ArticleLoader.Query.TITLE);
            collapsingBar.setTitle(title);
            titleTextView.setText(title);

            String authorString = cursor.getString(ArticleLoader.Query.AUTHOR);
            String dateString = stringUtils.getArticleDetailDateString(
                    cursor.getString(ArticleLoader.Query.PUBLISHED_DATE));

            dateTextView.setText(dateString);
            authorTextView.setText(authorString);

            new ArticleFormattingAsyncTask().execute(cursor.getString(ArticleLoader.Query.BODY));

            // TODO: 01/03/2018 default image empty_detail.png
            Picasso.with(getActivity())
                    .load(cursor.getString(ArticleLoader.Query.PHOTO_URL))
                    .into(backdropImageView);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newInstanceForItemId(getActivity(), itemId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (!isAdded()) {
            if (cursor != null) {
                cursor.close();
            }
            return;
        }
        this.cursor = cursor;

        if (this.cursor != null && !this.cursor.moveToFirst()) {
            this.cursor.close();
            this.cursor = null;
        }

        bindViews();
        setRootViewVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        cursor = null;
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