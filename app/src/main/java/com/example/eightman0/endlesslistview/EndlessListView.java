package com.example.eightman0.endlesslistview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class EndlessListView extends ListView {

    public interface LoadMoreListener {
        boolean shouldLoad();
        void loadNextPage();
    }
    private boolean isLoading;
    private ProgressBar progressBar;
    private LoadMoreListener loadMoreListener;
    private int totalCount = 0;
    private int threshold = 1;
    private LinearLayout footerView;

    public EndlessListView(Context context) {
        super(context);
        init();
    }

    public EndlessListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EndlessListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public void setProgressColor(int progressColor) {
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), progressColor), PorterDuff.Mode.SRC_IN);
    }

    public void stopLoading() {
        isLoading = false;
        removeFooterView(footerView);
    }

    private void init() {
        isLoading = false;
        ProgressBar progressBar = new ProgressBar(getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertDpToPixel(30));
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        params.bottomMargin = convertDpToPixel(10);
        progressBar.setLayoutParams(params);
        this.progressBar = progressBar;
        footerView = new LinearLayout(getContext());
        LayoutParams layoutParams = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, convertDpToPixel(40));
        footerView.setGravity(Gravity.CENTER);
        footerView.setLayoutParams(layoutParams);
        footerView.addView(progressBar);
        super.setOnScrollListener(new ScrollListener());
    }

    private class ScrollListener implements OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            boolean loadMore;
            loadMore = (0 != totalItemCount) && ((firstVisibleItem + visibleItemCount) >= (totalItemCount));
            if (!isLoading && loadMore) {
                if (loadMoreListener != null) {
                    if (loadMoreListener.shouldLoad() && (totalCount != totalItemCount) && (totalItemCount % threshold == 0)) {
                        isLoading = true;
                        addFooterView(footerView);
                        loadMoreListener.loadNextPage();
                        totalCount = totalItemCount;
                    }
                }
            }
        }
    }

    public int convertDpToPixel(float dp) {
        Context context = getContext();
        if (context == null) {
            return 10; // context should never be a null
        }
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) px;
    }
}