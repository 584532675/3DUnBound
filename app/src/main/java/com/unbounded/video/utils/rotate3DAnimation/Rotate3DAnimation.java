package com.unbounded.video.utils.rotate3DAnimation;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class Rotate3DAnimation extends Animation {

	private final float mFormDegress;

	private final float mToDegress;

	private final float mCenterX;

	private final float mCenterY;

	/**
	 * 控制z轴的一个常量值,主要是控制动画的升降距离
	 */
	private final float mDepthz;

	/**
	 * 控制z轴是像上移动还是向下移动,从而实现升降效果
	 */
	private final boolean mReverse;

	private Camera mCamera;

	public Rotate3DAnimation(float formDegress, float toDegress, float centerX,
			float centerY, float depthz, boolean reverse) {
		super();
		this.mFormDegress = formDegress;
		this.mToDegress = toDegress;
		this.mCenterX = centerX;
		this.mCenterY = centerY;
		this.mDepthz = depthz;
		this.mReverse = reverse;
	}

	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		mCamera = new Camera();
	}

	/**
	 * interpolatedTime 取值范围是0-1之间当每次，当动画启动后会系统会不停的调用applyTransformation方法，
	 * 并改变interpolatedTime的值
	 */
	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		final float formDegress = mFormDegress;
		// 通过差点值计算出转变的角度
		float degress = formDegress
				+ ((mToDegress - formDegress) * interpolatedTime);
		final float centerX = mCenterX;
		final float centerY = mCenterY;
		final Camera camera = mCamera;

		// 得到当前矩阵
		Matrix matrix = t.getMatrix();
		// 报错当前屏幕的状态
		camera.save();
		// 判断是降还是升
		if (mReverse) {
			// 正向改变Z轴角度
			camera.translate(0.0f, 0.0f, mDepthz * interpolatedTime);
		} else {
			// 反向改变Z轴角度
			camera.translate(0.0f, 0.0f, mDepthz * (1.0f - interpolatedTime));
		}
		// 旋转Y轴角度
		camera.rotateY(degress);
		// 把当前改变后的矩阵信息复制给Transformation的Matrix
		camera.getMatrix(matrix);
		// 根据改变后的矩阵信息从新恢复屏幕
		camera.restore();

		// 让动画在屏幕中间运行
		matrix.preTranslate(-centerX, -centerY);
		matrix.postTranslate(centerX, centerY);
	}
}
