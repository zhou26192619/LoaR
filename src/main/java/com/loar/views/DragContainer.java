package com.loar.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * 现在看到的实现方式有两种思路：
 * 1、是在复写draw()方法，在画完后，进行拖拽控件的绘制,感觉更好性能
 * 2、是利用ViewGroupOverlay元素来再表面进行拖拽控件的显示
 * <p>
 * 尽量不要内置可滚动的控件
 * <p>
 * Created by Justsy on 2017/3/30.
 */
public class DragContainer extends FrameLayout {
    public static final String DRAG_DESCRIPTION = "$canDrag$";
    public static final String TARGET_DESCRIPTION = "$canTarget$";
    private int mStartX;
    private int mLastX;
    private int mStartY;
    private int mLastY;
    private String status = "normal";
    private View dragView;//拖拽的控件
    private Drawable shadow; //拖拽的图标
    private View targetView;//目标控件
    private View intoView;//移动时进入的控件
    private long downTime;
    private int rough;

    public DragContainer(@NonNull Context context) {
        this(context, null);
    }

    public DragContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragContainer(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DragContainer(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        rough = (int) (50 * context.getResources().getDisplayMetrics().density);
    }

    /**
     * @param event
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int y = mLastY = (int) event.getRawY();//相对于view的y值，getRawY()是相对屏幕
        int x = mLastX = (int) event.getRawX();//相对于view的x值，getRawX()是相对屏幕
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            status = "normal";
            dragView = null;
            targetView = null;
            mLastX = mStartX = x;
            mLastY = mStartY = y;
            downTime = System.currentTimeMillis();
            //查找被点中的控件
            iterViewPosition(this);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (dragView == null) {
                return false;
            }
            if (isDraging()) {
                return true;
            } else {
                //优化长按事件和滚动事件冲突
                if (Math.abs(x - mStartX) <= rough && Math.abs(y - mStartY) <= rough) {
                    if (System.currentTimeMillis() - downTime > 600) {
                        status = "dragging";
                        addShadow(mLastX, mLastY);
                        if (onMDragListener != null)
                            onMDragListener.startDrag(dragView, mLastX, mLastY);
                    }
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            //结束后恢复所有东西
            reset();
        }
        return super.onInterceptTouchEvent(event);
    }

    /**
     * 拦截条件
     *
     * @param event
     * @return
     */
    private boolean dragCondition(MotionEvent event) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getRawY();//相对于view的y值，getRawY()是相对屏幕  这边可能会有个隐藏bug
        int x = (int) event.getRawX();//相对于view的x值，getRawX()是相对屏幕
        if (isDraging()) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {

            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                addShadow(x, y);
                intoView = null;
                intoTheView(this, x, y);//找到进入的view
                if (onMDragListener != null) onMDragListener.dragging(dragView, x, y, targetView);
                if ((intoView == null || intoView != targetView) && targetView != null) {
                    if (onMDragListener != null)
                        onMDragListener.out(dragView, x, y, targetView);
                }
                if (intoView != null && intoView != targetView) {
                    if (onMDragListener != null) {
                        onMDragListener.into(dragView, x, y, intoView);
                    }
                }
                targetView = intoView;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (onMDragListener != null) onMDragListener.endDrag(dragView, targetView);
                reset();
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    private boolean isDraging() {
        return "dragging".equalsIgnoreCase(status);
    }

    private void addShadow(int x, int y) {
        getOverlay().clear();
        int[] loc = new int[2];
        getLocationOnScreen(loc);
        shadow = dragShadow.shadow(dragView, x, y, loc[0], loc[1], targetView);
        if (shadow != null) getOverlay().add(shadow);
    }

    private void reset() {
        status = "normal";
        dragView = null;
        targetView = null;
        getOverlay().clear();
    }

    public void iterViewPosition(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            int[] location = new int[2];
            child.getLocationOnScreen(location);
            if (child.getContentDescription() != null && child.getContentDescription().toString().contains(DRAG_DESCRIPTION)) {
                if (inDrag(mStartX, mStartY, location, child.getWidth(), child.getHeight())) {
                    dragView = child;
                }
            }
            if (child instanceof ViewGroup) {
                iterViewPosition((ViewGroup) child);
            }
        }
    }

    /**
     * 找到目标控件
     *
     * @param parent
     * @param x
     * @param y
     */
    public void intoTheView(ViewGroup parent, int x, int y) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            int[] location = new int[2];
            child.getLocationOnScreen(location);
            if (child.getContentDescription() != null && child.getContentDescription().toString().contains(TARGET_DESCRIPTION)) {
                if (inDrag(x, y, location, child.getWidth(), child.getHeight())) {
                    intoView = child;
                }
            }
            if (child instanceof ViewGroup) {
                intoTheView((ViewGroup) child, x, y);
            }
        }
    }

    /**
     * 判断是否被点中
     *
     * @param location
     * @param width
     * @param height   @return
     */
    public boolean inDrag(int x, int y, int[] location, int width, int height) {
        if (x >= location[0] && x <= location[0] + width
                && y >= location[1] && y <= location[1] + height) {
            return true;
        }
        return false;
    }


    OnMDragListener onMDragListener;

    public void setOnMDragListener(DragContainer.OnMDragListener onMDragListener) {
        this.onMDragListener = onMDragListener;
    }

    public interface OnMDragListener {
        void endDrag(View dragView, @Nullable View targetView);

        void dragging(View dragView, int x, int y, @Nullable View targetView);

        void startDrag(View dragView, int x, int y);

        /**
         * 移动到某个view中
         *
         * @param dragView
         * @param x
         * @param y
         * @param intoView 进入的控件
         */
        void into(View dragView, int x, int y, View intoView);

        /**
         * 移出控件是调用的方法
         *
         * @param dragView
         * @param x
         * @param y
         * @param preView  从此控件移出去
         */
        void out(View dragView, int x, int y, View preView);
    }


    DragShadow dragShadow = new MDragShadow();

    public void setDragShadow(DragShadow dragShadow) {
        this.dragShadow = dragShadow;
    }

    interface DragShadow {
        Drawable shadow(View dragView, int x, int y, int xOffset, int yOffset, @Nullable View targetView);
    }

    public static class MDragShadow implements DragShadow {

        /**
         * 这边有个bug，因为获取控件坐标的时候都是获取的相对屏幕的坐标，而拖拽图标是相对容器的， 所以设置拖拽图标的要减去偏移量
         *
         * @param dragView
         * @param x
         * @param y
         * @param xOffset    容器x轴偏移量
         * @param yOffset    容器y轴偏移量
         * @param targetView
         * @return
         */
        public Drawable shadow(View dragView, int x, int y, int xOffset, int yOffset, View targetView) {
            Drawable dd = null;
            if (dragView != null) {
                dragView.buildDrawingCache();
                dd = new BitmapDrawable(null, dragView.getDrawingCache());
                dd.setBounds(x - dragView.getWidth() / 2 - xOffset, y - dragView.getHeight() / 2 - yOffset, x + dragView.getWidth() / 2 - xOffset, y + dragView.getHeight() / 2 - yOffset);
            }
            return dd;
        }
    }

    ;
}
