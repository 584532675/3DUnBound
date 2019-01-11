package com.unbounded.video.fragment;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stereo.Interaction.InteractionManager;
import com.stereo.angle.AngleActivity;
import com.stereo.util.ActionCallback;
import com.unbounded.video.BaseFragment;
import com.unbounded.video.R;
import com.unbounded.video.activity.LocalPicInfoActivity;
import com.unbounded.video.activity.PicUploadActivity;
import com.unbounded.video.adapter.PicLocalAdapter;
import com.unbounded.video.bean.LocalPic;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.utils.FileUtil;
import com.unbounded.video.utils.GetPath;
import com.unbounded.video.utils.LocalPicRun;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.unbounded.video.utils.ToastUtil;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.zhuge.analysis.stat.ZhugeSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjf on 2017/7/21 0021.
 */

public class PicLocalFragment extends BaseFragment {
    //使用照相机拍照获取图片
    public static final int SELECT_PIC_BY_TACK_PHOTO = 2;
    //裁剪照片
    public static final int REQUESTCODE_CUTTING = 3;
    public Uri photoUri;
    SwipeRefreshLayout swipeRefreshLayout;
    RelativeLayout rela1;
    private GridView gv;
    PicLocalAdapter adapter;
    List<LocalPic> pics = new ArrayList<LocalPic>();
    int isFirst = 1;

    Dialog goSetcameraDialog;
    RxPermissions rxPermissions;
    String photoname,picPath,phoneNum;

    Dialog activeDialog;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.SUCCESS_FLAG:
                    pics = (List<LocalPic>) msg.obj;
                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    progressDiddismiss();

                    break;
                case Constants.PERMISSION_ERROR_FLAG:
                    progressDiddismiss();
                    swipeRefreshLayout.setRefreshing(false);
                    toSetDialog();

                    break;

            }
        }
    };

    @Override
    protected int getContentView() {
        return R.layout.fragment_piclocal;
    }

    /**
     * 首次加载
     */
    private void initFirst(){
        progressDid();
        if (pics != null) {
            pics.clear();
        }
        initPics();
    }
    /**
     * 是否当前可见的生命周期方法
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser == true){
            if(isFirst == 1){
                initFirst();
                isFirst = 0;
            }
        }
    }

    @Override
    public void initDatas() {
        super.initDatas();

        rxPermissions = new RxPermissions(getActivity());
    }

    /**
     *
     */
    private void initPics(){
        pics.add(null);
        LocalPicRun run = new LocalPicRun(getActivity(), mHandler, Constants.SUCCESS_FLAG, pics);
        Thread thread = new Thread(run);
        thread.start();
    }

    @Override
    public void initView(View view) {
        super.initView(view);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.piclocal_swiperefresh);
        gv = (GridView) view.findViewById(R.id.piclocal_gv);
        rela1 = (RelativeLayout) view.findViewById(R.id.piclocal_rela1);

        rela1.setPadding(0, 0, 0,tabsmrelaHeight);

        adapter = new PicLocalAdapter(PicLocalFragment.this, getActivity(), pics, onelinepicimgWidth);
        gv.setAdapter(adapter);


    }

    @Override
    public void initEvent() {
        super.initEvent();
        //
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                pics.clear();
                adapter.notifyDataSetChanged();

                initPics();
            }
        });

        //
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(position == Constants.ZERO){
                    takePhoto();
                }else {
                    boolean isJiaoZhun = SharedPrefsUtil.getValue(getActivity(), com.stereo.util.Constants.JIAOZHUNSUCCESS, false);

                    if(isJiaoZhun == true){
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), LocalPicInfoActivity.class);
                        intent.putExtra("localPicBean",pics.get(position));
                        startActivity(intent);
                    }else{
                        activeDia(pics.get(position));
                    }

                }
            }
        });
    }
    /**
     * 激活对话框
     */
    private void activeDia(final LocalPic pic){
        View diaView = View.inflate(getActivity(), R.layout.notice_dialog, null);
        activeDialog = new Dialog(getActivity(), R.style.dialog);
        activeDialog.setContentView(diaView);
        activeDialog.setCanceledOnTouchOutside(false);
        activeDialog.setCancelable(false);

        Button setafterbtn = (Button) activeDialog.findViewById(R.id.notice_yesbtn);
        Button tonowbtn = (Button) activeDialog.findViewById(R.id.notice_canclebtn);
        TextView titletv = (TextView) activeDialog.findViewById(R.id.notice_tv1);
        TextView contenttv = (TextView) activeDialog.findViewById(R.id.notice_tv2);

        titletv.setVisibility(View.GONE);

        contenttv.setText(R.string.activebefore);
        setafterbtn.setText(R.string.setafter);
        tonowbtn.setText(R.string.nowactive);

        setafterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activeDialog.dismiss();

                Intent intent = new Intent();
                intent.setClass(getActivity(), LocalPicInfoActivity.class);
                intent.putExtra("localPicBean",pic);
                startActivity(intent);
            }
        });
        //
        tonowbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activeDialog.dismiss();
                Intent intent = new Intent();
                intent.setClass(getActivity(), AngleActivity.class);
                startActivity(intent);

                /**
                 * 去校准
                 */
                phoneNum = SharedPrefsUtil.getValue(getActivity(), Constants.Phone_FLAG, Constants.Phone_default);
                JSONObject personObject = null;
                try {
                    personObject = new JSONObject();
                    personObject.put("phoneNum", phoneNum);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ZhugeSDK.getInstance().track(getActivity(),"去校准",personObject);
            }
        });


        activeDialog.show();

        WindowManager.LayoutParams params = activeDialog.getWindow().getAttributes();
        params.width = screenWidth * 3 / 4;
        // params.height = 200 ;
        activeDialog.getWindow().setAttributes(params);
    }



    /**
     * 拍照获取图片
     */

    public void takePhoto() {
        //执行拍照前，应该先判断SD卡是否存在
        String SDState = Environment.getExternalStorageState();
        if(SDState.equals(Environment.MEDIA_MOUNTED))
        {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//"android.media.action.IMAGE_CAPTURE"
//            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");//"android.media.action.IMAGE_CAPTURE"
            /***
             * 需要说明一下，以下操作使用照相机拍照，拍照后的图片会存放在相册中的
             * 这里使用的这种方式有一个好处就是获取的图片是拍照后的原图
             * 如果不实用ContentValues存放照片路径的话，拍照后获取的图片为缩略图不清晰
             */
            ContentValues values = new ContentValues();
            photoUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
            /**-----------------*/
            startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
        }else{
            ToastUtil.showToast(getActivity(), R.string.Word_nosd, ToastUtil.CENTER);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == -1) {
                    progressDid();
                    //显示扫描到的内容
                    String sanString = data.getStringExtra("result").trim();
                    //调用激活接口激活
                    InteractionManager.getInstance(getActivity(), new ActionCallback() {
                        /**
                         * 激活请求接口
                         *  resultCode
                         *  1.激活成功
                         *  2.解密失败
                         *  3.网络超时
                         *  4.接受的参数为空
                         *  5.解密失败，解密后的序列号非全字符串
                         *  6.序列号拆分失败
                         *  7.根据解密后机型号没有获取到对应的机型
                         *  8.机型不匹配
                         *  9.根据拆分后的序列号没有查到对应的参数
                         *  10.调用加密算法失败
                         *  11.序列号不存在
                         *  12.json解析异常
                         *
                         */
                        @Override
                        public void onRequset(int returnCode) {
                            Log.i("PlayActivity", "onRequset: " + returnCode);

                            /**
                             * 激活结果
                             */
                            phoneNum = SharedPrefsUtil.getValue(getActivity(), Constants.Phone_FLAG, Constants.Phone_default);
                            JSONObject personObject = null;
                            try {
                                personObject = new JSONObject();
                                personObject.put("phoneNum", phoneNum);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ZhugeSDK.getInstance().track(getActivity(),"激活结果总次数",personObject);

                            switch (returnCode){
                                case 1:
//                                    ToastUtil.showToast(getActivity(), "激活成功,请开始眼球追踪校准",ToastUtil.BOTTOM);

                                    Intent intent = new Intent();
                                    intent.putExtra("activeSuccess",true);
                                    intent.setClass(getActivity(), AngleActivity.class);
                                    startActivity(intent);

                                    /**
                                     * 激活成功
                                     */
                                    ZhugeSDK.getInstance().track(getActivity(),"激活成功",personObject);
                                    ZhugeSDK.getInstance().track(getActivity(),"进入校准界面总次数",personObject);

                                    break;
                                case 2:
//                                    ToastUtil.showToast(getActivity(), "解密失败", ToastUtil.CENTER);
                                    Log.v("InteractionManager 2","解密失败");
                                    break;
                                case 3:
//                                    ToastUtil.showToast(getActivity(), "网络超时", ToastUtil.CENTER);
                                    Log.v("InteractionManager 3","网络超时");
                                    break;
                                case 4:
                                    Log.v("InteractionManager 4","接受的参数为空");
//                                    ToastUtil.showToast(getActivity(), "接受的参数为空", ToastUtil.CENTER);
                                    break;
                                case 5:
                                    Log.v("InteractionManager 5","解密失败，解密后的序列号非全字符串");
//                                    ToastUtil.showToast(getActivity(), "解密失败，解密后的序列号非全字符串", ToastUtil.CENTER);
                                    break;
                                case 6:
                                    Log.v("InteractionManager 6","序列号拆分失败");
//                                    ToastUtil.showToast(getActivity(), "序列号拆分失败", ToastUtil.CENTER);
                                    break;
                                case 7:
                                    Log.v("InteractionManager 7","根据解密后机型号没有获取到对应的机型");
//                                    ToastUtil.showToast(getActivity(), "根据解密后机型号没有获取到对应的机型", ToastUtil.CENTER);
                                    break;
                                case 8:
                                    Log.v("InteractionManager 8","机型不匹配");
//                                    ToastUtil.showToast(getActivity(), "机型不匹配", ToastUtil.CENTER);
                                    break;
                                case 9:
                                    Log.v("InteractionManager 9","根据拆分后的序列号没有查到对应的参数");
//                                    ToastUtil.showToast(getActivity(), "根据拆分后的序列号没有查到对应的参数", ToastUtil.CENTER);
                                    break;
                                case 10:
                                    Log.v("InteractionManager 10","调用加密算法失败");
//                                    ToastUtil.showToast(getActivity(), "调用加密算法失败", ToastUtil.CENTER);
                                    break;
                                case 11:
                                    Log.v("InteractionManager 11","序列号不存在");
//                                    ToastUtil.showToast(getActivity(), "序列号不存在", ToastUtil.CENTER);
                                    break;
                                case 12:
                                    Log.v("InteractionManager 12","json解析异常");
//                                    ToastUtil.showToast(getActivity(), "json解析异常", ToastUtil.CENTER);
                                    break;
                                case 13:
                                    Log.v("InteractionManager 13","暂不支持此机型");
//                                    ToastUtil.showToast(getActivity(), "暂不支持此机型", ToastUtil.CENTER);
                                    break;
                            }

                            progressDiddismiss();
                        }
                    }).interAction(sanString);
                }
                break;
            case SELECT_PIC_BY_TACK_PHOTO:// 调用相机拍照
                String str = GetPath.getPath(getActivity(),photoUri);
                File file = new File(str);
                if(!(file.exists())){
                    return;
                }
                startPhotoZoom(photoUri);

                break;
            case REQUESTCODE_CUTTING:// 取得裁剪后的图片
                if (data != null) {
                    setPicToView(data);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        String str = GetPath.getPath(getActivity(), uri);
        photoname = str.substring(str.lastIndexOf("/") + 1);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 4);
        intent.putExtra("aspectY", 3);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 400);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    /**
     * 显示裁剪后的图片
     */

    public void setPicToView(Intent data){
        Bundle extras = data.getExtras();

        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            picPath = FileUtil.saveFile(getActivity(), "localpic"+photoname, photo);

            LocalPic localPic = new LocalPic(-1, "localpic"+photoname, "", "", picPath, 0);
            Intent intent = new Intent();
            intent.setClass(getActivity(), PicUploadActivity.class);
            intent.putExtra("localPicBean",localPic);
            startActivity(intent);
        }
    }

}
