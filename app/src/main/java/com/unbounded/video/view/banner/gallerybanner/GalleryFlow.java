package com.unbounded.video.view.banner.gallerybanner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

public class GalleryFlow extends Gallery {

   public GalleryFlow(Context context)  
   {  
       super(context);  
       this.setStaticTransformationsEnabled(true);  
   }  
 
   public GalleryFlow(Context context, AttributeSet attrs)  
   {  
       super(context, attrs);  
       this.setStaticTransformationsEnabled(true);  
   }  
 
   public GalleryFlow(Context context, AttributeSet attrs, int defStyle)  
   {  
       super(context, attrs, defStyle);  
       this.setStaticTransformationsEnabled(true);  
   }  
 
   private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
       return e2.getX() > e1.getX();
   }
   @Override  
   public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)  
   {
       if (e1.getX() - e2.getX() < 0.0F){
           onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT,null);
       }else{
           onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT,null);
       }

       if (e1.getX() - e2.getX() < 0.0F){
           onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT,null);
       }else{
           onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT,null);
       }

       return true;

       /*
	   int kEvent;
       if (isScrollingLeft(e1, e2)) {
           kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
       } else {
           kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
       }
       onKeyDown(kEvent, null);

       if (this.getSelectedItemPosition() == 1) {// 实现后退功能
           this.setSelection(VideoRecommendFragment.bannerSize);
       }
       return false;
       */
   }
   
   @Override
   public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
           float distanceY) {
       // TODO Auto-generated method stub
       return super.onScroll(e1, e2, distanceX, distanceY);
   }
}
