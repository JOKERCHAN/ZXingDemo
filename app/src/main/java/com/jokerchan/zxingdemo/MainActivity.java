package com.jokerchan.zxingdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.client.android.decode.CaptureActivity;
import com.google.zxing.client.android.encode.QRCodeEncoder;
import com.google.zxing.common.HybridBinarizer;

import java.util.EnumMap;
import java.util.Map;


/**
 * ZXingDemo(二维码扫描)，基于3.2.1的版本,更改为竖屏,解决竖屏图片拉伸和识别问题
 *
 * @author jokerchan
 * @version 1.0
 * @since 2016年4月27日 14:05:35
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_CODE = 0x0;
    Button scannerQRCode, generateQRCode;
    ImageView qrcodeImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scannerQRCode = (Button) findViewById(R.id.qrcode_dencode);
        generateQRCode = (Button) findViewById(R.id.qrcode_encode);
        qrcodeImg = (ImageView) findViewById(R.id.qrcode_img);
        scannerQRCode.setOnClickListener(this);
        generateQRCode.setOnClickListener(this);
        //长按图片识别二维码
        qrcodeImg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                qrcodeImg.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(qrcodeImg.getDrawingCache());
                qrcodeImg.setDrawingCacheEnabled(false);
                decodeQRCode(bitmap);
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.qrcode_dencode: //扫描
                intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.qrcode_encode: //生成
                try {
                    Bitmap mBitmap = QRCodeEncoder.encodeAsBitmap("http://jokerchan.com/", 300);
                    qrcodeImg.setImageBitmap(mBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * 解析二维码图片
     *
     * @param bitmap   要解析的二维码图片
     */
    public final Map<DecodeHintType, Object> HINTS = new EnumMap<>(DecodeHintType.class);

    public void decodeQRCode(final Bitmap bitmap) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    int[] pixels = new int[width * height];
                    bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                    RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
                    Result result = new MultiFormatReader().decode(new BinaryBitmap(new HybridBinarizer(source)), HINTS);
                    return result.getText();
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
            }
        }.execute();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            Toast.makeText(MainActivity.this, scanResult, Toast.LENGTH_LONG).show();
        }
    }

}
