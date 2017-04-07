package com.loar.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.loar.R;
import com.loar.util.DensityUtil;

/**
 * 图片文字混合控件,暂未实现完全，基本可用(时间有限)
 * <p>
 * 泛型为遮盖层数据
 * Created by LoaR on 2016/10/24.
 */

public class ShortView<T> extends View {

    private final int ORIENTATION_LEFT = 0;//左
    private final int ORIENTATION_UP = 1;//上
    private final int ORIENTATION_RIGHT = 2;//右
    private final int ORIENTATION_DOWN = 3;//下


    private Context context;


    private float gap = 30;//文字和图片的间隙
    private int orientation = ORIENTATION_DOWN;//文字相对图片的方向
    private Bitmap image;//图片
    private float imageHeight = 0;//图片高度
    private float imageWidth = 0;//图片宽度
    private int width;//控件宽度
    private int height;//控件高度
    private float totalWordWidth; //文字绘制所需的总宽度
    private float textSize = 12;//文字大小
    private String text;
    private int textColor = Color.WHITE;
    private Paint paint = new Paint();
    private float textHeight;
    private Rect src; //图片源
    private Rect dest;//图片目标地址
    private float textBaseY;//文字基线高度
    private float radian;//角度
    private STATUS status = STATUS.NORMAL;//控件状态
    float endTextX = 0;//文字的右边界
    float startTextX = 0;//文字的左边界
    float startTextY = 0;//文字的上边界
    int startOffsetX = 0;//内容的左偏移量 ，居中显示时有用
    int startOffsetY = 0;//内容的上偏移量，居中显示时有用
    boolean isTextCenter = true;
    private int lines;
    private boolean isCenter = true;

    public void setLines(int lines) {
        this.lines = lines;
    }

    public void setCenter(boolean center) {
        isCenter = center;
    }

    public ShortView(Context context) {
        this(context, null);
    }

    public ShortView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShortView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ShortView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this.context = context;

        initPaint();
        gap = DensityUtil.dip2px(context, 8);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.short_view);
            gap = a.getDimensionPixelSize(R.styleable.short_view_gap, 8);
            imageHeight = a.getDimensionPixelSize(R.styleable.short_view_imageHeight, 0);
            imageWidth = a.getDimensionPixelSize(R.styleable.short_view_imageWidth, 0);
            setText(a.getString(R.styleable.short_view_text));
            setTextOption(a.getDimensionPixelSize(R.styleable.short_view_textSize, 11),
                    a.getColor(R.styleable.short_view_textColor, Color.WHITE));

            orientation = a.getInt(R.styleable.short_view_orientation, ORIENTATION_DOWN);
            a.recycle();
        }
        if (defStyleAttr != 0) {

        }

    }

    /**
     * 刷新数据
     */
    public void initPaint() {
        try {
            paint.setAntiAlias(true);
            paint.setColor(textColor);
            paint.setDither(true);
            paint.setTextSize(textSize);
            textHeight = getFontHeight(textSize);//文字的高度
            textBaseY = textHeight / 2 + (Math.abs(paint.ascent()) - paint.descent()) / 2;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setShadow(float radius, float dx, float dy, int shadowColor) {
        paint.setShadowLayer(radius, dx, dy, shadowColor);
    }

    public void setText(String text) {
        this.text = text;
        postInvalidate();
    }

    public void setTextOption(int textSize, int textColor) {
        this.textSize = textSize;
        this.textColor = textColor;
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        textHeight = getFontHeight(textSize);//文字的高度
        textBaseY = textHeight / 2 + (Math.abs(paint.ascent()) - paint.descent()) / 2;
        postInvalidate();
    }

    public float getGap() {
        return gap;
    }

    public void setGap(float gap) {
        if (this.gap != gap) {
            this.gap = gap;
            postInvalidate();
        }
    }

    public void setImageHeight(float imageHeight) {
        this.imageHeight = imageHeight;
    }

    public void setImageWidth(float imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        if (this.orientation != orientation) {
            this.orientation = orientation;
            postInvalidate();
        }
    }

    /**
     * 设置图片，会对图片重新拷贝一份
     *
     * @param logo
     */
    public void setImage(Bitmap logo) {
        if (this.image != logo) {
            this.image = logo;
            fixImagRect();
            postInvalidate();
        }
    }

    public Bitmap getImage() {
        return image;
    }

    public String getText() {
        return text;
    }

    public void setWidth(int width) {
        if (this.width != width) {
            this.width = width;
            postInvalidate();
        }
    }

    public void setHeight(int height) {
        if (this.height != height) {
            this.height = height;
            postInvalidate();
        }
    }

    /**
     * 有可能为空
     *
     * @return
     */
    public Rect getDest() {
        return dest;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getSize(widthMeasureSpec);
        height = getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    /**
     * 只在尺寸变化时调用一次
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        Log.e("onSizeChanged", width + " == " + height);
        fixImagRect();//确定图片的绘制区域
    }

    private void fixImagRect() {
        if (image != null) {
            src = new Rect(0, 0, image.getWidth(), image.getHeight());
            //确定图片的宽高
            if (imageWidth == 0) {
                imageWidth = image.getWidth();
            }
            if (imageHeight == 0) {
                imageHeight = image.getHeight();
            }
        }
        if (orientation == ORIENTATION_LEFT) {
            dest = new Rect((int) (width - getPaddingRight() - imageWidth), getPaddingTop(),
                    width - getPaddingRight(), (int) (imageHeight + getPaddingTop()));

        } else if (orientation == ORIENTATION_UP) {
            dest = new Rect((int) (width / 2 - imageWidth / 2), (int) (height - imageHeight - getPaddingBottom()),
                    (int) (width / 2 + imageWidth / 2), (height - getPaddingBottom()));

        } else if (orientation == ORIENTATION_RIGHT) {
            dest = new Rect(getPaddingLeft(), getPaddingTop(), (int) imageWidth, (int) imageHeight);

        } else if (orientation == ORIENTATION_DOWN) {
            if (width > 163) {
                Log.e("", "");
            }
            if (isCenter) {
                int contentHeight = height - getPaddingTop() - getPaddingBottom();
                if (lines > 0) {
                    startOffsetY = (int) ((contentHeight - imageHeight - lines * textHeight - gap) / 2);
                    startOffsetY = startOffsetY < 0 ? 0 : startOffsetY;
                }
            }
            dest = new Rect((int) (width / 2 - imageWidth / 2), getPaddingTop() + startOffsetY,
                    (int) (width / 2 + imageWidth / 2), (int) (imageHeight + startOffsetY + getPaddingTop()));

        }
    }

    /**
     * //计算起始和结束的坐标
     */
    private void computeTextArea() {
        if (orientation == ORIENTATION_LEFT) {
            startTextY = getPaddingTop() + textBaseY;
            endTextX = width - getPaddingRight() - gap - imageWidth;
            startTextX = getPaddingLeft() + startOffsetX;

        } else if (orientation == ORIENTATION_UP) {
            startTextY = getPaddingTop() + textBaseY;
            if (!isTextCenter || totalWordWidth >= width - getPaddingLeft() - getPaddingRight()) {
                startTextX = getPaddingLeft();
                endTextX = width - getPaddingRight();
            } else {
                startTextX = (width - totalWordWidth) / 2;
                endTextX = startTextX + totalWordWidth;
            }

        } else if (orientation == ORIENTATION_RIGHT) {
            startTextY = getPaddingTop() + textBaseY;
            endTextX = width - getPaddingRight();
            startTextX = gap + imageWidth + getPaddingLeft() + startOffsetX;

        } else if (orientation == ORIENTATION_DOWN) {
            startTextY = getPaddingTop() + textBaseY + imageHeight + gap + startOffsetY;
            if (!isTextCenter || totalWordWidth >= width - getPaddingLeft() - getPaddingRight()) {
                startTextX = getPaddingLeft();
                endTextX = width - getPaddingRight();
            } else {
                startTextX = (width - totalWordWidth) / 2;
                endTextX = startTextX + totalWordWidth;
            }
        }
    }

    /**
     * //计算文字串连所需的总宽度
     */
    private float measureTotalText() {
        float totalWordWidth = 0;
        for (int i = 0; i < text.length(); i++) {
            char a = text.charAt(i);
            float wordSize = paint.measureText(String.valueOf(a));
            totalWordWidth += wordSize;
        }
        return totalWordWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();

        //设置显示区域
        canvas.clipRect(getPaddingLeft(), getPaddingTop(), width - getPaddingRight(), height - getPaddingBottom());

        //计算文字的总大小
        totalWordWidth = measureTotalText();
        //计算文字的起始结束区域
        computeTextArea();
        float x = startTextX;
        float y = startTextY;
        float total = totalWordWidth;
        int ls = 1;
        for (int i = 0; i < text.length(); i++) {
            char a = text.charAt(i);
            float wordSize = paint.measureText(String.valueOf(a));
            if (x + wordSize > endTextX) {
                if (lines >= ls) {
                    break;
                }
                y += textHeight;
                x = startTextX;
                ls++;
                if (isTextCenter && total < width - getPaddingLeft() - getPaddingRight()) {
                    x = (width - total) / 2;
                }
            }
            canvas.drawText(String.valueOf(a), x, y, paint);
            x += wordSize;
            total -= wordSize;
        }

        canvas.restore();
        if (image != null) {
            if (orientation == ORIENTATION_UP) {
                dest.top = (int) Math.min(y + gap, height - imageHeight - getPaddingBottom());
                dest.bottom = (int) (dest.top + imageHeight);
            }
            canvas.drawBitmap(image, src, dest, paint);
            if (cover != null) {
//                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
                cover.drawCover(this, canvas, dest, paint, status, coverData);
                //如果调用了，结束后设置回颜色
                paint.setColor(textColor);
//                paint.setXfermode(null);
            }
        }
    }

    public float getFontHeight(float fontSize) {
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (float) (Math.ceil(fm.descent - fm.top) + 2);
    }

    private int getSize(int measureSpec) {
        int mySize = 0;

        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        switch (mode) {
            case MeasureSpec.UNSPECIFIED: {//如果没有指定大小，就设置为默认大小
                break;
            }
            case MeasureSpec.AT_MOST: {//如果测量模式是最大取值为size
                //我们将大小取最大值,你也可以取其他值
                mySize = size;
                break;
            }
            case MeasureSpec.EXACTLY: {//如果是固定的大小，那就不要去改变它
                mySize = size;
                break;
            }
        }
        return mySize;
    }


    private Cover cover;

    public void setCover(Cover cover) {
        this.cover = cover;
    }

    public interface Cover<T> {
        void drawCover(ShortView shortView, Canvas canvas, Rect dest, Paint paint, STATUS status, T coverData);
    }

    private T coverData;

    public void setCoverData(T t) {
        this.coverData = t;
    }

    public enum STATUS {
        NORMAL, HANG_UP;
    }
}
