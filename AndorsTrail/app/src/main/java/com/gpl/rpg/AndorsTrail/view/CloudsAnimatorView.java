package com.gpl.rpg.AndorsTrail.view;

import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gpl.rpg.AndorsTrail.R;

public class CloudsAnimatorView extends FrameLayout {

	private static final int DEFAULT_DURATION = 80000;
	private static final float SPEED_VARIANCE = 0.08f;
	private static final float BELOW_SPEED_FACTOR = 0.8f;
	private static final float CENTER_SPEED_FACTOR = 1.0f;
	private static final float ABOVE_SPEED_FACTOR = 1.2f;

	public static enum Layer {
		below,
		center,
		above
	}
	
	private static final int[] belowDrawablesId = new int[]{R.drawable.ts_clouds_s_01, R.drawable.ts_clouds_s_02, R.drawable.ts_clouds_s_03};
	private static final int[] centerDrawablesId = new int[]{R.drawable.ts_clouds_m_01, R.drawable.ts_clouds_m_02};
	private static final int[] aboveDrawablesId = new int[]{R.drawable.ts_clouds_l_01, R.drawable.ts_clouds_l_02, R.drawable.ts_clouds_l_03, R.drawable.ts_clouds_l_04};
	
	private int[] drawableIds = centerDrawablesId;
	private float speedFactor = CENTER_SPEED_FACTOR;
	private int count = 15;
	private ViewGroup layer;
	private int duration = DEFAULT_DURATION;
	private int yMax = 100;
	private float scalingRatio = 1.0f;
	
	private ConcurrentHashMap<ImageView, PausableTranslateAnimation> animations;


	public CloudsAnimatorView(Context context) {
		super(context);
		init();
	}

	public CloudsAnimatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CloudsAnimatorView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	private void init() {
		setFocusable(false);
		inflate(getContext(), R.layout.clouds_animator, this);
		layer =  (ViewGroup) findViewById(R.id.ts_clouds_layer);
	}
	
	public void setCloudsCountAndLayer(int count, Layer layer) {
		this.count = count;
		switch (layer) {
		case above:
			drawableIds = aboveDrawablesId;
			speedFactor = ABOVE_SPEED_FACTOR;
			break;
		case below:
			drawableIds = belowDrawablesId;
			speedFactor = BELOW_SPEED_FACTOR;
			break;
		case center:
			drawableIds = centerDrawablesId;
			speedFactor = CENTER_SPEED_FACTOR;
			break;
		
		}
		animations = new ConcurrentHashMap<ImageView, PausableTranslateAnimation>(count);
	}
	
	private void createCloud() {
		final ImageView iv = new ImageView(getContext());
		iv.setImageDrawable(getResources().getDrawable(drawableIds[(int)(drawableIds.length * Math.random())]));
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((int) (iv.getDrawable().getIntrinsicWidth() * scalingRatio), (int) (iv.getDrawable().getIntrinsicHeight() * scalingRatio));//RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		layer.addView(iv, lp);

		final float y = (float) (Math.random() * yMax) - (int) (iv.getDrawable().getIntrinsicHeight() * scalingRatio);
		float ratio = (float)Math.random();
		final float x = (float) (((1-ratio) * (iv.getDrawable().getMinimumWidth() + layer.getWidth())) - iv.getDrawable().getMinimumWidth());
		final long d = (long)((ratio * duration) / (speedFactor + (Math.random() * SPEED_VARIANCE)));
		
		prepareAnimation(iv, layer, speedFactor, x, y, d);
	}
	
	private void resetCloud(final ImageView iv) {
		final float y = (float) (Math.random() * yMax) - (int) (iv.getDrawable().getIntrinsicHeight() * scalingRatio);
		final float x = -iv.getWidth();
		final long d = (long)(duration / (speedFactor + (Math.random() * SPEED_VARIANCE)));
		
		prepareAnimation(iv, layer, speedFactor, x, y, d);
	}
	
	private void prepareAnimation(final ImageView iv, final ViewGroup layer, final float speedFactor, final float x, final float y, final long d) {
		PausableTranslateAnimation anim = new PausableTranslateAnimation(
				TranslateAnimation.ABSOLUTE, x, TranslateAnimation.ABSOLUTE, layer.getWidth(), 
				TranslateAnimation.ABSOLUTE, y, TranslateAnimation.ABSOLUTE, y);
		
		anim.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				iv.setVisibility(View.VISIBLE);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				iv.setVisibility(View.GONE);
				resetCloud(iv);
			}
		});
		anim.setInterpolator(new LinearInterpolator());
		anim.setDuration(d);
		animations.put(iv, anim);
		iv.startAnimation(anim);
		if (paused) {
			anim.pause();
		}
	}

	public void startAnimation() {
		int i = count;
		while (i-- > 0) {
			createCloud();
		}
	}
	
	
	boolean started = false;
	
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
		if (hasWindowFocus) {
			if (!started) {
				startAnimation();
				started = true;
			}
		}
	}
	
	private boolean paused = false;
	
	public void resumeAnimation() {
		paused = false;
		if (started) {
			for (PausableTranslateAnimation a : animations.values()) {
				a.resume();
			}
		}
	}
	
	public void pauseAnimation() {
		paused = true;
		for (PausableTranslateAnimation a : animations.values()) {
			a.pause();
		}
	}
	
	public void setScalingRatio(float ratio) {
		this.scalingRatio = ratio;
		duration = (int) (DEFAULT_DURATION * getWidth() / (1024 * ratio)); 
		
	}
	
	public void setYMax(int yMax) {
		this.yMax = yMax;
	}

	private static class PausableTranslateAnimation extends TranslateAnimation {
		
		private long elapsedAtPause = 0;
		private boolean paused = false;
		private boolean resume = false;
		
		public PausableTranslateAnimation(int fromXType, float fromXValue, int toXType, float toXValue,
	            int fromYType, float fromYValue, int toYType, float toYValue) {
			super(fromXType, fromXValue, toXType, toXValue, fromYType, fromYValue, toYType, toYValue);
		}
		
		@Override
		public boolean getTransformation(long currentTime, Transformation outTransformation) {
			if (paused && elapsedAtPause == 0) {
				elapsedAtPause = currentTime - getStartTime();
			}
			if (paused) {
				setStartTime(currentTime - elapsedAtPause); 
				if (resume) {
					paused = false;
					resume = false;
				}
			}
			return super.getTransformation(currentTime, outTransformation);
		}
		
		public void pause() {
			elapsedAtPause = 0;
			paused = true;
		}
		
		public void resume() {
			if (paused) resume = true;
		}
	}
	
}
