package com.unbounded.video.utils.rotate3DAnimation;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

public class RotateAnimationUtil {

	private ViewGroup context;

	private View[] views;

	public RotateAnimationUtil(ViewGroup context, View... views) {
		super();
		this.context = context;
		this.views = views;
	}

	/**
	 * 应用自定义的Rotate3DAnimation动画
	 * 
	 * @param flag
	 *            当前控件的顺序坐标
	 * @param startDegress
	 *            开始的角度
	 * @param endDegress
	 *            结束的角度
	 */
	public void applyRotateAnimation(int flag, float startDegress,
			float endDegress) {
		final float centerX = context.getWidth() / 2.0f;
		final float centerY = context.getHeight() / 2.0f;

		Rotate3DAnimation rotate = new Rotate3DAnimation(startDegress,
				endDegress, centerX, centerY, 310.0f, true);
		rotate.setDuration(300);
		rotate.setFillAfter(false);
		rotate.setInterpolator(new DecelerateInterpolator());

		rotate.setAnimationListener(new DisplayNextView(flag));
		context.startAnimation(rotate);
	}

	private final class DisplayNextView implements Animation.AnimationListener {

		private final int mFlag;

		private DisplayNextView(int flag) {
			mFlag = flag;
		}

		public void onAnimationStart(Animation animation) {

		}

		// 动画结束

		public void onAnimationEnd(Animation animation) {
			context.post(new SwapViews(mFlag));
		}

		public void onAnimationRepeat(Animation animation) {

		}
	}

	/**
	 * 新开一个线程动画结束后再开始一次动画效果实现翻屏特效
	 * 
	 * @author yangzhiqiang
	 * 
	 */
	private final class SwapViews implements Runnable {

		private final int mFlag;

		public SwapViews(int mFlag) {
			this.mFlag = mFlag;
		}

		public void run() {
			final float centerX = context.getWidth() / 2.0f;
			final float centerY = context.getHeight() / 2.0f;
			Rotate3DAnimation rotation;
			if (mFlag > -1) {
				views[0].setVisibility(View.GONE);
				views[1].setVisibility(View.VISIBLE);
				views[1].requestFocus();
				rotation = new Rotate3DAnimation(270, 360, centerX, centerY,
						310.0f, false);
			} else {
				views[1].setVisibility(View.GONE);
				views[0].setVisibility(View.VISIBLE);
				views[0].requestFocus();
				rotation = new Rotate3DAnimation(90, 0, centerX, centerY,
						310.0f, false);
			}
			rotation.setDuration(300);
			rotation.setFillAfter(false);
			rotation.setInterpolator(new DecelerateInterpolator());
			// 开始动画
			context.startAnimation(rotation);

		}

	}

}
