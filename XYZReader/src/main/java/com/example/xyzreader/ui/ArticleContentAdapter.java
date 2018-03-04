package com.example.xyzreader.ui;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyzreader.R;

/**
 * RecyclerView adapter for the article
 * Due to the length, the article generates problems when set as a text of a TextView.
 * In order to avoid this, the article is broken into paragraphs which are stored in a list.
 * The article is shown as a list where each paragraphs is one element of the list.
 */

public class ArticleContentAdapter extends RecyclerView.Adapter<ArticleContentAdapter.ParagraphViewHolder> {

    private static final String TAG = ArticleContentAdapter.class.getSimpleName();

    private String[] articleParagraphs;

    @Override
    public ParagraphViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .list_item_article_paragraph, parent, false);

        return new ParagraphViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ParagraphViewHolder holder, int position) {
        String paragraph = articleParagraphs[position];
        holder.paragraphTextView.setText(paragraph);
    }

    @Override
    public int getItemCount() {
        if(articleParagraphs != null) {
            return articleParagraphs.length;
        }
        return 0;
    }

    public void swapTextContent(String articleString) {
        this.articleParagraphs = articleString.split("(\r\n|\n)");
        notifyDataSetChanged();
    }

    public class ParagraphViewHolder extends ViewHolder {
        public TextView paragraphTextView;

        public ParagraphViewHolder(View itemView) {
            super(itemView);
            AssetManager assetManager = itemView.getResources().getAssets();

            paragraphTextView = itemView.findViewById(R.id.paragraph_text_view);
            paragraphTextView.setTypeface(Typeface.createFromAsset(assetManager,
                    "Rosario-Regular.ttf"));

        }
    }
}
