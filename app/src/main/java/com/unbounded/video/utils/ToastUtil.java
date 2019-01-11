package com.unbounded.video.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * 可以覆盖的toast间隔{@link Toast.LENGTH_SHORT}，防止狂点出现多个toast
 */
public class ToastUtil {
	public final static int CENTER = 111111;
	public final static int BOTTOM = 111112;
	private static String oldMsg;
	protected static Toast toast = null;
	private static long oneTime = 0;
	private static long twoTime = 0;

	/**
	 * duration {@link Toast.LENGTH_SHORT}
	 * 
	 * @param context
	 * @param s
	 */
	public static void showToast(Context context, String s, int flag) {
		if (toast == null) {
			toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
			if(flag == CENTER){
				toast.setGravity(Gravity.CENTER, 0, 0);
			}else if(flag == BOTTOM){
				toast.setGravity(Gravity.BOTTOM, 0, 180);
			}
			
			toast.show();
			oneTime = System.currentTimeMillis();
		} else {
			twoTime = System.currentTimeMillis();
			if (s.equals(oldMsg)) {
				if (twoTime - oneTime > Toast.LENGTH_SHORT) {
					toast.show();
				}
			} else {
				oldMsg = s;
				toast.setText(s);
				toast.show();
			}
		}
		oneTime = twoTime;
	}

	public static void showToast(Context context, int resId, int flag) {
		try{
			showToast(context, context.getString(resId), flag);
		}catch (Exception e){

		}

	}

}
