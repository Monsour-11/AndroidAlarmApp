package com.android.monsoursaleh.missionalarm;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewItemDecor extends RecyclerView.ItemDecoration {
    private Drawable mDivider;
    /**
     * Called only once
     * Used for deciding bounds of divider.
     * We decide where the divider is to be drawn and how.
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent,
                       @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);

        // Divider left is distance of child view from parent.
        // Divider right is distance from parent's left to right.
        // -32 is for padding on the right of divider.
        int dividerLeft = 32;
        int dividerRight = parent.getWidth() - 32;

        // Done for top and bottom divider of every view
        // This is because every view is different.
        for (int i = 0; i < parent.getChildCount(); i++) {
            // Don't need divider at bottom of last child.
            if (i != parent.getChildCount() - 1) {
                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                        child.getLayoutParams();

                // Calculate distance of divider to be drawn from top.
                int dividerTop = child.getBottom() + params.bottomMargin;
                int dividerBottom = dividerTop + mDivider.getIntrinsicHeight();
                mDivider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom);
                mDivider.draw(c);
            }
        }
    }

    public RecyclerViewItemDecor(Drawable divider) {
        mDivider = divider;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);


        // No padding for first child because we don't want space above recyclerview
        if (parent.getChildAdapterPosition(view) == 0) {
            return;
        }

        // For each child, add padding on top. Intrinsic height method returns
        // actual size of image, not the size that you set in xml.
        outRect.top = mDivider.getIntrinsicHeight();
    }
}
