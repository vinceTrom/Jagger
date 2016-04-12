package com.vtromeur.jagger.ui;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

/**
 * Created by Vince on 12/04/16.
 */
public class CustomRecyclerViewLayoutManager extends LinearLayoutManager {

    public CustomRecyclerViewLayoutManager(Context context) {
        super(context);
    }

    public CustomRecyclerViewLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public void smoothScrollToPosition(final RecyclerView recyclerView, RecyclerView.State state, int position) {
        LinearSmoothScroller smoothScroller = new LinearSmoothScroller(recyclerView.getContext()){

            @Override
            public PointF computeScrollVectorForPosition(int targetPosition){
                return CustomRecyclerViewLayoutManager.this.computeScrollVectorForPosition(targetPosition);
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics){
                return super.calculateSpeedPerPixel(displayMetrics) * 1.5f;
            }

        };
        smoothScroller.setTargetPosition(position);

        startSmoothScroll(smoothScroller);
    }
}