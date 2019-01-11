package com.unbounded.video.view.banner.cycleview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * com.best.bestedu.framework.utils.common
 * create for
 * Created by Administrator on 2016/7/13.
 */
public class ImageUtils {
    /**
     *
     * @param srcPath
     * @param desWidthHeight
     * @return 得到压缩后图片的byte
     * @throws Exception
     */
    public static byte[] compressImageGetBytes(String srcPath, int desWidthHeight) throws Exception {
        byte[] bytes = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Bitmap bitmap = compressImage(srcPath, desWidthHeight);
            // 将新图片压缩
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);

            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            bytes = baos.toByteArray();
        }catch (Exception e){
            e.printStackTrace();
            //ToastUtil.showErrorToast("图片压缩出错");
        }finally {
            baos.close();
        }
        return bytes;
    }

    public static Bitmap compressImage(String srcPath, int desWidthHeight)
            throws Exception {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 只获取图片边界
        newOpts.inJustDecodeBounds = true;
        // 首选编解码方式
        newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // 系统需要内存时,自动回收该bitmap
        newOpts.inPurgeable = true;
        // 和inPurgeable=true一起使用,可共享引用
        newOpts.inInputShareable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        // 原图的宽度和高度
        int srcWidth = newOpts.outWidth;
        int srcHeight = newOpts.outHeight;

        // 处理完的宽度和高度
        int desWidth = srcWidth;
        int desHeight = srcHeight;

        // be=1表示不缩放
        float be = 1.0f;
        if (srcWidth <= desWidthHeight && srcHeight <= desWidthHeight) {

            be = 1.0f;

            Log.d("ImageUtils", "ImageUtils" + "==>compressImage:图片无需处理");
        } else if (srcWidth > srcHeight) {
            be = srcWidth * 1.0f / desWidthHeight;
            // 宽度为期望的宽高
            desWidth = desWidthHeight;
            // 高度按比例缩小
            desHeight = (int) (srcHeight / be);

            Log.d("ImageUtils", "ImageUtils" + "==>compressImage:宽大于高");
        } else if (srcHeight > srcWidth) {
            be = srcHeight * 1.0f / desWidthHeight;
            // 高度为期望的宽高
            desHeight = desWidthHeight;
            // 宽度按比例缩小
            desWidth = (int) (desWidth / be);

            Log.d("ImageUtils", "ImageUtils" + "==>compressImage:高大于宽");
        }

        Log.d("ImageUtils", "ImageUtils" + "==>compressImage,原图宽高:" + srcWidth + "," + srcHeight
                + ",处理完后的图片宽高:" + desWidth + "," + desHeight + ",缩放系数:" + be);

        // 获取完整的图片
        newOpts.inJustDecodeBounds = false;
        newOpts.inSampleSize = (int) be;

        try {
            bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            newOpts.inSampleSize <<= 1;
            bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        }

        if (be != 1.0f) {
            // 创建缩略图,并回收原图
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, desWidth,
                    desHeight, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        int orientation=getOrientation(srcPath);
        Log.d("ImageUtils", "ImageUtils" + "==>图片方向:"+orientation);
        //旋转图片
        bitmap=rotateToPortrait(bitmap, orientation);

        return bitmap;
    }

    /**
     * drawable转bitmap
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitamp(Drawable drawable)
    {
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        bitmap = Bitmap.createBitmap(w,h,config);
        //注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 获取图片的方向
     *
     * @param path
     *            图片路径
     * @return 返回代表方向的整数
     */
    public static int getOrientation(String path) {
        int orientation = -1;
        try {
            ExifInterface EXIF = new ExifInterface(path);
            orientation = EXIF
                    .getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return orientation;
    }

    /**
     * 根据原图,将图片旋转成竖向
     *
     * @param bitmap
     *            原图
     * @param ori
     *            图片当前方向
     * @return 返回旋转后的图片
     */
    public static Bitmap rotateToPortrait(Bitmap bitmap, int ori) {
        Matrix matrix = new Matrix();
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        if ((ori == 6) || (ori == 8) || (ori == 5) || (ori == 7)) {
            int tmp = w;
            w = h;
            h = tmp;
        }
        switch (ori) {
            case 6 :
                matrix.setRotate(90.0F, w / 2.0F, h / 2.0F);
                break;
            case 3 :
                matrix.setRotate(180.0F, w / 2.0F, h / 2.0F);
                break;
            case 8 :
                matrix.setRotate(270.0F, w / 2.0F, h / 2.0F);
                break;
            case 2 :
                matrix.preScale(-1.0F, 1.0F);
                break;
            case 4 :
                matrix.preScale(1.0F, -1.0F);
                break;
            case 5 :
                matrix.setRotate(90.0F, w / 2.0F, h / 2.0F);
                matrix.preScale(1.0F, -1.0F);
                break;
            case 7 :
                matrix.setRotate(270.0F, w / 2.0F, h / 2.0F);
                matrix.preScale(1.0F, -1.0F);
                break;
            case 1 :
            default :
                return bitmap;
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
    }

    public static void saveBitmap(Bitmap bm, String cache, String picName) {
        FileOutputStream out = null;
        File f = new File(cache, picName);
        if (f.exists()) {
            f.delete();
        }
        try {
            out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
        } catch (FileNotFoundException e) {
            //XL.d(ImageUtils.class.getSimpleName(), e.toString());
        } catch (IOException e) {
            //XL.d(ImageUtils.class.getSimpleName(), e.toString());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e2) {
                //XL.d(ImageUtils.class.getSimpleName(), e2.toString());
            }
        }
    }

    public static void saveBitmapFile(Bitmap bitmap, String path){
        File file=new File(path);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyImage2Data(Context context, Integer PicID)
    {
        try
        {
            //计算图片存放全路径
            String localDir = context.getExternalFilesDir(null) + File.separator + "app_code" + File.separator;
            String fileName = localDir + "app_code.jpg";
            File dir = new File(localDir);
            //如果文件夹不存在，创建一个（只能在应用包下面的目录，其他目录需要申请权限 OWL）
            if(!dir.exists()) {
                dir.mkdirs();
            }

            // 获得封装  文件的InputStream对象
            InputStream is = context.getResources().openRawResource(PicID);

            FileOutputStream fos = new FileOutputStream(fileName);

            byte[] buffer = new byte[8192];
            int count = 0;

            // 开始复制Logo图片文件
            while((count=is.read(buffer)) > 0)
            {
                fos.write(buffer, 0, count);
            }
            fos.close();
            is.close();

        } catch(Exception e)
        {
            e.printStackTrace();
        }
    }

}
