package zhangphil.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;

import androidx.annotation.Nullable;

import zhangphil.cameralibrary.JCameraView;
import zhangphil.cameralibrary.listener.ClickListener;
import zhangphil.cameralibrary.listener.JCameraListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.kareluo.TFullScreenActivity;
import me.kareluo.imaging.IMGEditActivity;

public class TakePhotoActivity extends TFullScreenActivity {
    private static final int REQUEST_CODE = 100;
    //照片存放位置
    public static final String PHOTO_SAVE_DIR = TFileUtils.getDir("photo");

    private JCameraView mJCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ti_activity_take_photo);

        initVideo();
    }

    private void initVideo() {

        mJCameraView = (JCameraView) findViewById(R.id.cameraview);

        /**
         * 设置视频保存路径
         */
        mJCameraView.setSaveVideoPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "TCamera");
        /**
         * JCameraView监听
         */
        mJCameraView.setJCameraLisenter(new JCameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                // 进入图片编辑页面
                Intent intent = new Intent(TakePhotoActivity.this, IMGEditActivity.class);
                if (bitmap != null){
                    bitmap = addTextWatermark(bitmap);
                }
                IMGEditActivity.ImageHolder.getInstance().setBitmap(bitmap);
                String filePath = PHOTO_SAVE_DIR + "TINET_" + SystemClock.currentThreadTimeMillis() + ".png";
                intent.putExtra(IMGEditActivity.EXTRA_IMAGE_SAVE_PATH, filePath);
                startActivityForResult(intent, REQUEST_CODE);
            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame) {
                //获取成功录像后的视频路径
                Intent data = new Intent();
                data.putExtra("take_photo", false);
                data.putExtra("path", url);
                setResult(RESULT_OK, data);
                finish();
            }
        });

        //左边按钮点击事件
        mJCameraView.setLeftClickListener(new ClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (REQUEST_CODE == requestCode && RESULT_OK == resultCode && data != null) {
            setResult(RESULT_OK, data);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mJCameraView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mJCameraView.onPause();
    }

    /**
     * 给一张Bitmap添加水印文字。
     *
     * @param bitmap 源图片
     * @return 已经添加水印后的Bitmap。
     */
    public static Bitmap addTextWatermark(Bitmap bitmap) {
        if (isEmptyBitmap(bitmap))
            return null;
        Bitmap ret = bitmap.copy(bitmap.getConfig(), true);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Canvas canvas = new Canvas(ret);

        // 总宽度定为图片1/3，其他宽度都根据这个值计算, 位置定在左下
        float targetWidth = ret.getWidth() / 3f;
        float targetHeight = targetWidth / 4;

        // 画边框
        float borderWidth = targetWidth / 100;
        if (borderWidth < 1) borderWidth = 1;
        RectF borderRect = new RectF();
        borderRect.left = 0;
        borderRect.top = ret.getHeight() - targetHeight;
        borderRect.right = borderRect.left + targetWidth;
        borderRect.bottom = borderRect.top + targetHeight;
        paint.setColor(Color.parseColor("#A7A8AC"));
        paint.setStrokeWidth(borderWidth);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(borderRect, paint);
        // 画背景
        paint.setColor(Color.parseColor("#B36B6F7E"));
        paint.setStyle(Paint.Style.FILL);
        RectF backgroundRect = new RectF();
        backgroundRect.left = borderRect.left + borderWidth;
        backgroundRect.top = borderRect.top + borderWidth;
        backgroundRect.right = backgroundRect.left + (targetWidth - borderWidth);
        backgroundRect.bottom = backgroundRect.top + (targetHeight - borderWidth);
        canvas.drawRect(borderRect, paint);

        // 画文字
        String content = getFormatData();
        float textSize = targetWidth * 0.75f / 8; // 按10个字符算
        //https://blog.csdn.net/u010661782/article/details/52805939
        paint.setTextSize(textSize);
        paint.setColor(Color.WHITE);
        paint.setTypeface(Typeface.create("PingFangSC", Typeface.NORMAL));
        Rect bounds = new Rect();
        paint.getTextBounds(content, 0, content.length(), bounds);
        // 居中显示
        float x = (borderRect.left + borderWidth) + (targetWidth - bounds.width()) / 2f;
        float y = (borderRect.bottom - borderWidth) - (targetHeight - bounds.height()) / 2f;
        canvas.drawText(content, x, y, paint);
        if (!bitmap.isRecycled())
            bitmap.recycle();
        return ret;
    }

    private static String getFormatData() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());
        return simpleDateFormat.format(new Date());
    }

    /**
     * Bitmap对象是否为空。
     */
    public static boolean isEmptyBitmap(Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }
}
