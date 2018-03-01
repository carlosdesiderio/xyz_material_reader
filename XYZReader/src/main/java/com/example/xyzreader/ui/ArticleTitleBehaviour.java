package com.example.xyzreader.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyzreader.R;

/**
 * Provides behaviour for the article title as a direct child of {@link CoordinatorLayout}
 */

public class ArticleTitleBehaviour extends CoordinatorLayout.Behavior<ViewGroup> {
    private static final String TAG = ArticleTitleBehaviour.class.getSimpleName();

    private static final float ALPHA_FACTOR = 1.5f;

    private TextView bigTitle;

    private float collapsedTitleMarginStart;
    private int dependencyStartYPosition;
    private int dependencyEndYPosition;
    private int childViewOffset;
    private float bigTitleY;
    private int maxScroll;


    public ArticleTitleBehaviour(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable
                    .ArticleTitleBehavior);
            collapsedTitleMarginStart = a.getDimension(R.styleable
                    .ArticleTitleBehavior_collapsedTitleMarginStart, 0);

            a.recycle();
        }
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ViewGroup child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ViewGroup child, View dependency) {
        maybeInitChildViews(child);
        maybeInitProperties(child, dependency);

        int newChildY = dependency.getBottom() - childViewOffset;
        float translationFactor = getTranslationFactor(dependency.getBottom());

        child.setAlpha(getAlpha(translationFactor));
        setBigTitleXPos(getXPosition(translationFactor));
        if (newChildY > 0) {
            child.setY(newChildY);
        }

        return super.onDependentViewChanged(parent, child, dependency);
    }

    private void maybeInitChildViews(ViewGroup parent) {
        if (bigTitle == null) {
            bigTitle = parent.findViewById(R.id.article_detail_big_title);
        }
    }

    private void maybeInitProperties(View child, View dependency) {
        if (childViewOffset == 0) {
            childViewOffset = child.getHeight();
        }

        if (bigTitleY == 0) {
            bigTitleY = bigTitle.getY();
        }

        if (dependencyStartYPosition == 0) {
            dependencyStartYPosition = dependency.getBottom();
        }

        if (dependencyEndYPosition == 0) {
            Toolbar bar = dependency.findViewById(R.id.article_detail_toolbar);
            int toolBarHeight = bar.getHeight();
            dependencyEndYPosition = toolBarHeight;
        }

        if (maxScroll == 0) {
            maxScroll = dependencyStartYPosition - dependencyEndYPosition;
        }
    }

    private float getTranslationFactor(int pos) {
        float currentScrollDelta = pos - dependencyEndYPosition;
        float scrollPerc = currentScrollDelta / maxScroll;
        return 1f - scrollPerc;
    }


    private float getXPosition(float translationFactor) {
        return collapsedTitleMarginStart * translationFactor;
    }

    private float getAlpha(float translationFactor) {
        return 1 - (translationFactor * ALPHA_FACTOR);
    }

    private void setBigTitleXPos(float pos) {
        if (bigTitle != null) {
            bigTitle.setX(pos);
        }
    }
}
