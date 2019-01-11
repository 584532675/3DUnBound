package com.unbounded.video.utils.videoRecord;

import android.hardware.Camera.Size;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PreviewSizeutil {

	private PreviewSizeutil() {

	}

	public static PreviewSizeutil getInstance() {

		return new PreviewSizeutil();
	}
	

	
	/**
	 * 
	 * 获取相机合适的预览尺寸
	 * @param list
	 * @param th
	 * @return
	 */
	public Size getCameraPreviewSize(List<Size> list, int th) {
		CameraSizeComparator sizeComparator = new CameraSizeComparator();
		Collections.sort(list, sizeComparator);
		Size size = null;
		for (int i = 0; i < list.size(); i++) {
			size = list.get(i);
			if ((size.width > th) && equalRate(size, 1.3f)) {
				break;
			}
		}
		return size;
	}

	public static boolean equalRate(Size size, float rate) {
		float r = (float) (size.width) / (float) (size.height);
        return Math.abs(r - rate) <= 0.2;
	}

	public class CameraSizeComparator implements Comparator<Size> {
		// 按升序排列
		@Override
		public int compare(Size lhs, Size rhs) {
			if (lhs.width == rhs.width) {
				return 0;
			} else if (lhs.width > rhs.width) {
				return 1;
			} else {
				return -1;
			}
		}

	}

}
