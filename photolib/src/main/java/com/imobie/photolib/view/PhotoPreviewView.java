package com.imobie.photolib.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.imobie.photolib.R;
import com.imobie.photolib.adapter.PhotoPreviewAdapter;
import com.imobie.photolib.cache.ImagePreviewCache;
import com.imobie.photolib.cache.PreviewRectImage;
import com.imobie.photolib.utils.DensityUtils;

import java.util.ArrayList;
import java.util.List;

public class PhotoPreviewView extends RelativeLayout implements View.OnClickListener {
    private boolean inView;

    private List<String> files;
    private int current;
    private int previewPosition = -1;
    private boolean exitTouch;

    private Context context;
    private ImageView previewImageView;
    private ImageView toolLeft, toolRight;
    private TextView toolRight2;
    private View backgroundView;

    private ViewPager viewPager;

    private Callback callback;

    private PhotoPreviewAdapter photoPreviewAdapter;

    public PhotoPreviewView(Context context) {
        super(context);

        initView(context);
    }

    public PhotoPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(context);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setData(int previewPosition) {
        this.files = ImagePreviewCache.files;
        this.previewPosition = previewPosition;
        this.current=previewPosition;
        photoPreviewAdapter=new PhotoPreviewAdapter(viewPager,files, (filePath, imageView) -> {
            callback.loadImage(filePath,imageView);
        });
        viewPager.setAdapter(photoPreviewAdapter);
        viewPager.setCurrentItem(current);
        initPreview();
    }

    public void setToolLeftImage(int resId) {
        this.toolLeft.setImageResource(resId);
    }
    public void setToolRightImage(int resId) {
        this.toolRight.setImageResource(resId);
    }
    private void initView(Context context) {
        this.context = context;
        View.inflate(context, R.layout.view_photo_preview, this);

        backgroundView = findViewById(R.id.background_view);

        initViewPager();
        previewImageView = findViewById(R.id.preview_image);
        toolLeft = findViewById(R.id.tool_left);
        toolRight = findViewById(R.id.tool_right);
        toolRight2 = findViewById(R.id.tool_right_2);
        toolLeft.setOnClickListener(this);
        toolRight.setOnClickListener(this);
        toolRight2.setOnClickListener(this);

    }

    private void initViewPager() {

        viewPager = findViewById(R.id.view_pager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                current = position;
                callback.loadImage(files.get(current), (ImageView) previewImageView);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tool_left) {
            callback.clickToolLeft();
        } else if (id == R.id.tool_right) {
            callback.clickToolRight();
        } else if (id == R.id.tool_right_2) {
            callback.clickToolLeft2();
        }
    }


    private int[] getCurrentRect() {
        for (PreviewRectImage image : ImagePreviewCache.previewRectImages) {
            if (image.getPosition() == current)
                return image.getRect();
        }
        return null;
    }

    private void startAnimator(View view, int[] rect) {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator xChangeAnim = ObjectAnimator.ofFloat(view, "x", inView ? rect[0] : 0
                , inView ? 0 : rect[0]);

        ObjectAnimator yChangeAnim = ObjectAnimator.ofFloat(view, "y", inView ? rect[1] : view.getY()
                , inView ? 0 : rect[1]);

        ValueAnimator widthChangeAnim = ValueAnimator.ofInt(inView ? rect[2] : DensityUtils.getScreenW(context)
                , inView ? DensityUtils.getScreenW(context) : rect[2]);
        widthChangeAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setLayoutParams(new RelativeLayout.LayoutParams((Integer) animation.getAnimatedValue()
                        , view.getLayoutParams().height));
            }
        });

        ValueAnimator heightChangeAnim = ValueAnimator.ofInt(inView ? rect[3] : DensityUtils.getScreenH(context)
                , inView ? DensityUtils.getScreenH(context) : rect[3]);
        heightChangeAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setLayoutParams(new RelativeLayout.LayoutParams(view.getLayoutParams().width
                        , (Integer) animation.getAnimatedValue()));
            }
        });

        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(backgroundView, "alpha"
                , inView ? 0f : backgroundView.getAlpha(), inView ? 1f : 0f);

        animatorSet.setDuration(200);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                if (inView) {
                    findViewById(R.id.title_bar).setVisibility(VISIBLE);
                    previewImageView.setVisibility(View.INVISIBLE);
                    viewPager.setVisibility(View.VISIBLE);
                } else {
                    callback.vFinish();
                }
            }
        });

        animatorSet.play(xChangeAnim)
                .with(yChangeAnim)
                .with(widthChangeAnim)
                .with(heightChangeAnim)
                .with(alphaAnim);
        animatorSet.start();
    }

    /**
     * 初始化预览
     */
    private void initPreview() {
        if (containCurrentRect()) {

            previewImageView.setLayoutParams(new RelativeLayout.LayoutParams(getCurrentRect()[2]
                    , getCurrentRect()[3]));
            previewImageView.setX(getCurrentRect()[0]);
            previewImageView.setY(getCurrentRect()[1]);

            callback.loadFirstImage(files.get(current), (ImageView) previewImageView);
        } else {
            previewImageView.setVisibility(View.INVISIBLE);
            viewPager.setVisibility(View.VISIBLE);
        }
    }

    float distanceX, distanceY;
    float touchX, touchY;
    float centerY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                centerY = backgroundView.getHeight() / 2f;
                touchY = ev.getY();
                touchX = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                touchX = ev.getX() - touchX;
                distanceY = ev.getY() - touchY;
                if ((ev.getPointerCount() == 1 && Math.abs(distanceY) > 10 && Math.abs(touchX) < 20) || exitTouch) {
                    previewImageView.setVisibility(View.VISIBLE);
                    viewPager.setVisibility(View.INVISIBLE);
                    previewImageView.setY(previewImageView.getY() + distanceY);
                    touchY = ev.getY();
                    boolean bottom = touchY > centerY;
                    if ((distanceY > 0 && bottom) || (distanceY < 0 && !bottom)) {
                        if (backgroundView.getAlpha() > 0)
                            backgroundView.setAlpha(backgroundView.getAlpha() - 0.01f);
                        if (previewImageView.getScaleX() > 0)
                            previewImageView.setScaleX(previewImageView.getScaleX() - 0.01f);
                        if (previewImageView.getScaleY() > 0)
                            previewImageView.setScaleY(previewImageView.getScaleY() - 0.01f);
                    } else {
                        if (backgroundView.getAlpha() < 1)
                            backgroundView.setAlpha(backgroundView.getAlpha() + 0.02f);
                        if (previewImageView.getScaleX() < 1)
                            previewImageView.setScaleX(previewImageView.getScaleX() + 0.02f);
                        if (previewImageView.getScaleY() < 1)
                            previewImageView.setScaleY(previewImageView.getScaleY() + 0.02f);
                    }
                    if (!exitTouch) {
                        findViewById(R.id.title_bar).setVisibility(INVISIBLE);
                        exitTouch = true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (exitTouch) {
                    exitTouch = false;

                    findViewById(R.id.title_bar).setVisibility(VISIBLE);
                    int lastHeightPoint = backgroundView.getHeight() / 4;
                    if (previewImageView.getY() > lastHeightPoint) {
                        exit();
                    } else {

                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.setDuration(200);
                        animatorSet.play(ObjectAnimator.ofFloat(previewImageView, "y", previewImageView.getY(), 1f))
                                .with(ObjectAnimator.ofFloat(backgroundView, "alpha", backgroundView.getAlpha(), 1f))
                                .with(ObjectAnimator.ofFloat(previewImageView, "scaleX", previewImageView.getScaleX(), 1f))
                                .with(ObjectAnimator.ofFloat(previewImageView, "scaleY", previewImageView.getScaleY(), 1f));
                        animatorSet.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                previewImageView.setVisibility(View.INVISIBLE);
                                viewPager.setVisibility(View.VISIBLE);
                            }
                        });
                        animatorSet.start();
                    }
                }
                break;
        }
        return exitTouch || super.dispatchTouchEvent(ev);
    }

    private void exit() {
        if (containCurrentRect()) {
            inView = false;
            previewImageView.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.INVISIBLE);
            startAnimator(previewImageView, getCurrentRect());
        } else {
            callback.vFinish();
        }
    }

    public boolean containCurrentRect() {
        for (PreviewRectImage image : ImagePreviewCache.previewRectImages) {
            if (image.getPosition() == current)
                return true;
        }
        return false;
    }

    public void onBackPressed() {
        inView = false;
        previewImageView.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.INVISIBLE);
        startAnimator(previewImageView, getCurrentRect());
    }

    public void loadFirstImageAnim() {
        inView = true;
        startAnimator(previewImageView, getCurrentRect());
    }

    public interface Callback {
        void vFinish();

        void loadImage(String filePath, ImageView imageView);

        void clickToolLeft();

        void clickToolRight();

        void clickToolLeft2();

        void loadFirstImage(String filePath, ImageView imageView);
    }
}
