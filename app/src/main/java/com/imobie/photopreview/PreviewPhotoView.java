package com.imobie.photopreview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class PreviewPhotoView extends RelativeLayout {
    private PhotoBean photoBean;
    private ImageView previewImageView;
    private View backgroundView;
    private Context context;
    private int[] locations;
    private int previewWidth, previewHeight;
    private boolean inView;
    private boolean multipleTouch;

    private Callback callback;

    private int animatorTime=500;

    public PreviewPhotoView(Context context) {
        super(context);
        initView(context);
    }

    public PreviewPhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public int getAnimatorTime() {
        return animatorTime;
    }

    public void setAnimatorTime(int animatorTime) {
        this.animatorTime = animatorTime;
    }

    private void initView(Context context) {
        this.context = context;

        backgroundView = new View(context);
        backgroundView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT));
        backgroundView.setBackgroundColor(Color.BLACK);
        backgroundView.setAlpha(0);
        addView(backgroundView);

        previewImageView = new ImageView(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(CENTER_IN_PARENT);
        previewImageView.setLayoutParams(lp);


        addView(previewImageView);

        setOnTouchListener(new OnTouchListener() {
            float distanceY;
            float touchY;
            float centerY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        centerY = getHeight() / 2f;
                        touchY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        distanceY = event.getY() - touchY;
                        if (event.getPointerCount() == 1) {
                            previewImageView.setY(previewImageView.getY() + distanceY);
                            touchY = event.getY();
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
								if (previewImageView.getScaleX() <= 1)
									previewImageView.setScaleX(previewImageView.getScaleX() + 0.02f);
								if (previewImageView.getScaleY() <= 1)
									previewImageView.setScaleY(previewImageView.getScaleY() + 0.02f);
							}
                        } else {
                            multipleTouch = true;
                            //多点触摸图片进行放大缩小

                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if(multipleTouch) {
                            multipleTouch = false;
                            //执行多点触摸图片恢复动画
                            Toast.makeText(context,"Please use other open source image scaling libraries!",Toast.LENGTH_SHORT).show();
                            break;
                        }
                        int lastHeightPoint = DensityUtils.getScreenH(context) / 4;
                        if (previewImageView.getY() > lastHeightPoint) {
                            exit();
                        } else {
                            AnimatorSet animatorSet = new AnimatorSet();
                            animatorSet.setDuration(animatorTime);
                            animatorSet.play(ObjectAnimator.ofFloat(previewImageView, "y", previewImageView.getY(), 1f))
                                    .with(ObjectAnimator.ofFloat(backgroundView, "alpha", backgroundView.getAlpha(), 1f))
                                    .with(ObjectAnimator.ofFloat(previewImageView, "scaleX", previewImageView.getScaleX(), 1f))
                                    .with(ObjectAnimator.ofFloat(previewImageView, "scaleY", previewImageView.getScaleY(), 1f));
                            animatorSet.start();
                        }

                        break;
                }
                return true;
            }
        });

    }

    private void exit() {
        inView = false;
        startAnimator();
    }

    public void startPreview(View previewView, PhotoBean photoBean) {
        inView = true;
        this.photoBean = photoBean;
        previewWidth = previewView.getWidth();
        previewHeight = previewView.getHeight();
        previewImageView.setLayoutParams(new RelativeLayout.LayoutParams(previewWidth, previewHeight));
        locations = new int[2];
        previewView.getLocationInWindow(locations);
        previewImageView.setX(locations[0]);
        previewImageView.setY(locations[1]);
        ImageLoader.getInstance().displayImage("file://" + photoBean.get_data()
                , previewImageView
                , new DisplayImageOptions.Builder()
                        .considerExifParams(true)
                        .build(), new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        startAnimator();
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
    }

    private void startAnimator() {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator xChangeAnim = ObjectAnimator.ofFloat(previewImageView, "x", inView ? locations[0] : 0
                , inView ? 0 : locations[0]);

        ObjectAnimator yChangeAnim = ObjectAnimator.ofFloat(previewImageView, "y", inView ? locations[1] : previewImageView.getY()
                , inView ? 0 : locations[1]);

        ValueAnimator widthChangeAnim = ValueAnimator.ofInt(inView ? previewWidth : DensityUtils.getScreenW(context)
                , inView ? DensityUtils.getScreenW(context) : previewWidth);
        widthChangeAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                previewImageView.setLayoutParams(new RelativeLayout.LayoutParams((Integer) animation.getAnimatedValue()
                        , previewImageView.getLayoutParams().height));
            }
        });

        ValueAnimator heightChangeAnim = ValueAnimator.ofInt(inView ? previewHeight : DensityUtils.getScreenH(context)
                , inView ? DensityUtils.getScreenH(context) : previewHeight);
        heightChangeAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                previewImageView.setLayoutParams(new RelativeLayout.LayoutParams(previewImageView.getLayoutParams().width
                        , (Integer) animation.getAnimatedValue()));
            }
        });

        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(backgroundView, "alpha"
                , inView ? 0f : backgroundView.getAlpha(), inView ? 1f : 0f);

        animatorSet.setDuration(animatorTime);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                if (inView) {
                    ImageLoader.getInstance().displayImage("file://" + photoBean.get_data()
                            , previewImageView, new DisplayImageOptions.Builder()
                                    .considerExifParams(true)
                                    .build());
                } else {
                    callback.removeView(PreviewPhotoView.this);
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


    public boolean onBack() {
        if (inView) {
            exit();
            return true;
        }
        return false;
    }


    public interface Callback {
        void removeView(View view);
    }
}
