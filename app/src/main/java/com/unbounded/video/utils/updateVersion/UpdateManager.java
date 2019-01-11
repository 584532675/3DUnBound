package com.unbounded.video.utils.updateVersion;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.unbounded.video.BuildConfig;
import com.unbounded.video.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 *@author coolszy
 *@date 2012-4-26
 *@blog http://blog.92coding.com
 */

public class UpdateManager
{
	/* 下载中 */
	static final int DOWNLOAD = 1;
	/* 下载结束 */
	static final int DOWNLOAD_FINISH = 2;
	//下载保存文件名
	String apkName = "VideoPlayerAS.apk";
	/* 下载保存路径 */
	String mSavePath;
	/* 记录进度条数量 */
	int progress;
	/* 是否取消更新 */
	boolean cancelUpdate = false;
	
	int screenwidth,screenHeight;
	boolean isupdate = false;
	boolean forceupdate = false;
	String apkUpdateUrl;
	String updateExplain;
	
	Context mContext;
	/* 更新进度条 */
	ProgressBar mProgress;
	Dialog mDownloadDialog;

	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			// 正在下载
			case DOWNLOAD:
				// 设置进度条位置
				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				// 安装文件
				installApk();
				break;
			default:
				break;
			}
		}
    };

	//
	public UpdateManager(int screenwidth, int screenHeight, boolean isupdate,
						 boolean forceupdate, String apkUpdateUrl, Context mContext, String updateExplain) {
		super();
		this.screenwidth = screenwidth;
		this.screenHeight = screenHeight;
		this.isupdate = isupdate;
		this.forceupdate = forceupdate;
		this.apkUpdateUrl = apkUpdateUrl;
		this.mContext = mContext;
		this.updateExplain = updateExplain;

	}

	/**
	 * 检测软件是否要更新
	 */
	public void checkUpdate(){
		if (isupdate == true){
			if(forceupdate == true){
				//强制升级
				//Toast.makeText(mContext, "强制更新", Toast.LENGTH_SHORT).show();
				// 显示下载对话框
				showDownloadDialog();
				
			}else if(forceupdate == false){
				// 显示更新提示对话框
				//Toast.makeText(mContext, "提示更新", Toast.LENGTH_SHORT).show();
				showNoticeDialog();
			}
		} else {
			//Toast.makeText(mContext, "已是最新版本", Toast.LENGTH_SHORT).show();
		}
	}

	
	
	
	
	/**
	 * 显示软件更新对话框
	 */
	private void showNoticeDialog()
	{
		View diaView= View.inflate(mContext, R.layout.noticeupdate_dialog1, null);
		final Dialog noticeupdatedialog = new Dialog(mContext, R.style.dialog);
		noticeupdatedialog.setContentView(diaView);
		noticeupdatedialog.setCanceledOnTouchOutside(false);
		noticeupdatedialog.setCancelable(false);

		ImageView topimg = (ImageView) noticeupdatedialog.findViewById(R.id.noticeupdate_topimg);
		TextView yesbtn = (TextView) noticeupdatedialog.findViewById(R.id.noticeupdate_yestv);
		TextView canclebtn = (TextView) noticeupdatedialog.findViewById(R.id.noticeupdate_cancletv);
		TextView explaintv = (TextView) noticeupdatedialog.findViewById(R.id.noticeupdate_tv2);


		ViewGroup.LayoutParams params1 = topimg.getLayoutParams();
		params1.width = screenwidth*3/4;
		params1.height = (screenwidth*3/4) * 397/613;
		topimg.setLayoutParams(params1);

		explaintv.setText(updateExplain.replace("\\n", "\n"));
		
		canclebtn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				noticeupdatedialog.dismiss();
				
			}
		});
		
		yesbtn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				noticeupdatedialog.dismiss();
				// 显示下载对话框
				showDownloadDialog();
				
			}
		});
		
		noticeupdatedialog.show();
		
		WindowManager.LayoutParams params = noticeupdatedialog.getWindow().getAttributes();
		params.width = screenwidth*3/4;
		noticeupdatedialog.getWindow().setAttributes(params);
		
	}

	/**
	 * 显示软件下载对话框
	 */
	private void showDownloadDialog()
	{
		
		View diaView= View.inflate(mContext, R.layout.downloadapk_dialog, null);
		mDownloadDialog = new Dialog(mContext, R.style.dialog);
		mDownloadDialog.setContentView(diaView);
		mDownloadDialog.setCanceledOnTouchOutside(false);
		mDownloadDialog.setCancelable(false); 
		
		Button downloadcanclebtn = (Button) mDownloadDialog.findViewById(R.id.download_canclebtn);
		mProgress = (ProgressBar) mDownloadDialog.findViewById(R.id.download_progress1);
		
		if(forceupdate == true){
			downloadcanclebtn.setVisibility(View.GONE);
		}
		
		downloadcanclebtn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				//设置取消状态
				cancelUpdate = true;
				mDownloadDialog.dismiss();
				
			}
		});
		
		mDownloadDialog.show();
		
		WindowManager.LayoutParams params = mDownloadDialog.getWindow().getAttributes();
		params.width = screenwidth*3/4;
		 //params.height = 200 ;
		mDownloadDialog.getWindow().setAttributes(params);
		
		downloadApk();
		
	}

	/**
	 * 下载apk文件
	 */
	private void downloadApk()
	{
		// 启动新线程下载软件
		new downloadApkThread().start();
	}

	/**
	 * 下载文件线程
	 * 
	 * @author coolszy
	 *@date 2012-4-26
	 *@blog http://blog.92coding.com
	 */
	private class downloadApkThread extends Thread
	{
		@Override
		public void run()
		{
			try
			{
				// 判断SD卡是否存在，并且是否具有读写权限
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				{
					// 获得存储卡的路径
					String sdpath = Environment.getExternalStorageDirectory() + "/";
					mSavePath = sdpath + "moyan/version";
					URL url = new URL(apkUpdateUrl);
					// 创建连接
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.connect();
					// 获取文件大小
					int length = conn.getContentLength();

					Log.e("info", "length="+length);

					// 创建输入流
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// 判断文件目录是否存在
					if (!file.exists())
					{
						file.mkdir();
					}
					File apkFile = new File(mSavePath, apkName);
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// 缓存
					byte buf[] = new byte[1024];
					// 写入到文件中
					do
					{
						int numread = is.read(buf);
						count += numread;
						// 计算进度条位置
						progress = (int) (((float) count / length) * 100);
						// 更新进度
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0)
						{
							// 下载完成
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// 写入文件
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// 点击取消就停止下载.
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			// 取消下载对话框显示
			mDownloadDialog.dismiss();
		}
	}

    /**
	 * 安装APK文件
	 */
	private void installApk()
	{
		File apkFile = new File(mSavePath, apkName);
		if (!apkFile.exists())
		{
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkFile.toString()), "application/vnd.android.package-archive");
		mContext.startActivity(i);


		Intent intent =new Intent(Intent.ACTION_VIEW);

		if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N) {
			Uri contentUri = FileProvider.getUriForFile(mContext,BuildConfig.APPLICATION_ID + ".fileProvider",apkFile);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			intent.setDataAndType(contentUri,"application/vnd.android.package-archive");

		}else{
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(apkFile),"application/vnd.android.package-archive");

		}

		mContext.startActivity(intent);

	}
}
