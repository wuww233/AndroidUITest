package com.www233.uitest.buttonpagescroll;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import java.util.Objects;

public class GridPageSnapHelper extends SnapHelper {
    private static final String TAG = "GridPageSnapHelper";
    private int currentPositionVertical = 0, currentPositionHorizontal = 0;
    private int ROW, page_limit, all_item = 0;
    private OrientationHelper mHorizontalHelper, mVerticalHelper;

    /**
     * 初始化
     *
     * @param ROW        不可移动的方向上有多少列/行
     * @param page_limit 每一页最多有多少控件
     */
    public GridPageSnapHelper(int ROW, int page_limit) {

        // TODO: 如果recyclerview的宽高是WRAP_CONTENT的话，自动根据这两个值计算宽高
        // TODO: 如果recyclerview的宽高是MATCH_PARENT的话，自动根据view的宽高和Row/page_limit计算另一个值
        this.ROW = ROW;
        this.page_limit = page_limit;
    }

    @Override
    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) throws IllegalStateException {
        super.attachToRecyclerView(recyclerView);
        if (recyclerView != null) {
            all_item = Objects.requireNonNull(recyclerView.getAdapter()).getItemCount();
            recyclerView.addItemDecoration(new ButtonPageScrollDecoration());
        }
    }

    @NonNull
    private OrientationHelper getVerticalHelper(@NonNull RecyclerView.LayoutManager layoutManager) {
        if (mVerticalHelper == null || mVerticalHelper.getLayoutManager() != layoutManager) {
            mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager);
        }
        return mVerticalHelper;
    }

    @NonNull
    private OrientationHelper getHorizontalHelper(
            @NonNull RecyclerView.LayoutManager layoutManager) {
        if (mHorizontalHelper == null || mHorizontalHelper.getLayoutManager() != layoutManager) {
            mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
        }
        return mHorizontalHelper;
    }

    static class Direction {
        static final int HORIZONTAL = 0, VERTICAL = 1;
    }

    int distanceToNextPage(@NonNull RecyclerView.LayoutManager layoutManager, OrientationHelper helper, @NonNull View targetView, int direction) {
        int position = layoutManager.getPosition(targetView);
        int currentPosition = (direction == Direction.HORIZONTAL ? currentPositionHorizontal : currentPositionVertical);
        int currentPageStart = currentPosition;
        int ans;
        if (Math.abs(position - currentPosition) >= page_limit) {   // 目标view已加载出来，不用再计算
            ans = helper.getDecoratedStart(targetView) - helper.getStartAfterPadding();
            currentPageStart = position;
        } else {    // 移动半块及以上时会滑动页面
            int dis = Math.abs(helper.getDecoratedStart(targetView) - helper.getStartAfterPadding());
            if (position < currentPosition - ROW
                    || dis <= (helper.getDecoratedMeasurement(targetView) / 2) && position < currentPosition) {  // 左移
                currentPageStart = currentPosition - page_limit;

            } else if (position >= currentPosition + ROW
                    || dis >= (helper.getDecoratedMeasurement(targetView) / 2) && position >= currentPosition) {    // 右移
                currentPageStart = currentPosition + page_limit;
            }

            int columnWidth = helper.getDecoratedMeasurement(targetView);
            int distance = ((position - currentPageStart) / ROW) * columnWidth;
            final int childStart = helper.getDecoratedStart(targetView);

            ans = childStart - distance;
        }

        if (direction == Direction.HORIZONTAL)
            currentPositionHorizontal = currentPageStart;
        else
            currentPositionVertical = currentPageStart;

        return ans;
    }

    @Nullable
    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
        int[] out = new int[2];
        if (layoutManager.canScrollHorizontally()) {
            out[0] = distanceToNextPage(layoutManager, getHorizontalHelper(layoutManager), targetView, Direction.HORIZONTAL);
        }
        if (layoutManager.canScrollVertically()) {
            out[1] = distanceToNextPage(layoutManager, getVerticalHelper(layoutManager), targetView, Direction.VERTICAL);
        }
        return out;
    }

    private View findNextPage(RecyclerView.LayoutManager layoutManager, OrientationHelper helper)
    {

        final int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return null;
        }

        View closestChild = null;
        final int start = helper.getStartAfterPadding();
        int closest = Integer.MAX_VALUE;

        for (int i = 0; i < childCount; i++) {
            final View child = layoutManager.getChildAt(i);
            if (child == null) continue;

            int childStart = helper.getDecoratedStart(child)
                    + (helper.getDecoratedMeasurement(child));
            int distance = Math.abs(childStart - start);

            if (distance < closest) {
                closest = distance;
                closestChild = child;
            }

        }
        return closestChild;
    }
    @Nullable
    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager.canScrollVertically()) {
            return findNextPage(layoutManager, getVerticalHelper(layoutManager));
        } else if (layoutManager.canScrollHorizontally()) {
            return findNextPage(layoutManager, getHorizontalHelper(layoutManager));
        }
        return null;
    }

    private int findNextPagePosition(int velocity, int currentPosition)
    {
        if (velocity > 0)   // 向右滑动
        {
            if (currentPosition + page_limit > all_item)
                return currentPosition;
            else
                return currentPosition + page_limit;

        } else if (velocity < 0) // 向左滑动
        {
            if (currentPosition - page_limit < 0)
                return currentPosition;
            else
                return currentPosition - page_limit;
        }
        return RecyclerView.NO_POSITION;
    }
    @Nullable
    private OrientationHelper getOrientationHelper(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager.canScrollVertically()) {
            return getVerticalHelper(layoutManager);
        } else if (layoutManager.canScrollHorizontally()) {
            return getHorizontalHelper(layoutManager);
        } else {
            return null;
        }
    }
    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {

        final int itemCount = layoutManager.getItemCount();
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION;
        }

        final OrientationHelper orientationHelper = getOrientationHelper(layoutManager);
        if (orientationHelper == null) {
            return RecyclerView.NO_POSITION;
        }

        if (layoutManager.canScrollVertically()) {
            return findNextPagePosition(velocityY, currentPositionVertical);
        } else if (layoutManager.canScrollHorizontally()) {
            return findNextPagePosition(velocityX, currentPositionHorizontal);
        } else {
            return RecyclerView.NO_POSITION;
        }

    }


    private class ButtonPageScrollDecoration extends RecyclerView.ItemDecoration {
        int last_line_st = all_item - ((all_item - 1) % ROW) - 1;

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int position = parent.getChildAdapterPosition(view);
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            if (position >= last_line_st) { // 最后一列/列边距到末尾
                int line = (position % page_limit) / ROW + 1;
                int all_line = page_limit / ROW;

                assert layoutManager != null;
                if(layoutManager.canScrollHorizontally())
                    outRect.set(outRect.left, outRect.top, outRect.right + parent.getWidth() / all_line * (all_line - line), outRect.bottom);
                if(layoutManager.canScrollVertically())
                    outRect.set(outRect.left, outRect.top, outRect.right, outRect.bottom + parent.getHeight() / all_line * (all_line - line));
            }

        }
    }
}
