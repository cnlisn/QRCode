package com.lisn.qrcode;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xys.libzxing.zxing.activity.CaptureActivity;
import com.xys.libzxing.zxing.encoding.EncodingUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private int mRequextCode = 88;
    private EditText mEt_ewm;
    private EditText et_txm;
    private ImageView mImage;
    private TextView mShow_text;
    private CheckBox mCheck_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button bt = (Button) findViewById(R.id.bt);
        bt.setOnClickListener(this);
        Button make_ewm = (Button) findViewById(R.id.make_ewm);
        make_ewm.setOnClickListener(this);
        Button make_txm = (Button) findViewById(R.id.make_txm);
        make_txm.setOnClickListener(this);
        mEt_ewm = (EditText) findViewById(R.id.et_ewm);
        et_txm = (EditText) findViewById(R.id.et_txm);
        mImage = (ImageView) findViewById(R.id.image);

        mShow_text = (TextView) findViewById(R.id.show_text);
        mCheck_logo = (CheckBox) findViewById(R.id.check_Logo);


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bt) {
            Intent intent = new Intent(this, CaptureActivity.class);
            startActivityForResult(intent, mRequextCode);
        } else if (view.getId() == R.id.make_txm) {
            make_txm();
        } else if (view.getId() == R.id.make_ewm) {
            make_ewm();
        }
    }

    private void make_ewm() {
        String input = mEt_ewm.getText().toString();

        if(input.equals("")){
            Toast.makeText(MainActivity.this, "输入为空，请重新输入", Toast.LENGTH_SHORT).show();
        }else{

            Bitmap bitmap = EncodingUtils.createQRCode(input,700,700,
                    mCheck_logo.isChecked()? BitmapFactory.decodeResource(getResources(),R.mipmap.c_100):null);
            mShow_text.setVisibility(View.INVISIBLE);
            mImage.setImageBitmap(bitmap);
        }
    }

    /*生成条形码*/
    private void make_txm() {
        String input = et_txm.getText().toString();

        if (input.equals("")) {
            Toast.makeText(MainActivity.this, "输入的内容为空，请重新输入", Toast.LENGTH_SHORT).show();
        } else if (isAllDig(input)) {
            Toast.makeText(MainActivity.this, "输入的内容不能包括中文，请重新输入", Toast.LENGTH_SHORT).show();
        } else {
            Ecoad ecoad = new Ecoad(700, 500);
            try {
                mShow_text.setText(input);
                mShow_text.setVisibility(View.VISIBLE);
                Bitmap bitmap = ecoad.bitmap1(input);
                mImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*判断是否是中文 是返回true 否返回false*/
    public boolean isAllDig(String input){
        for (int i=0;i<input.length();i++){
            int c = input.charAt(i);
            if(c>19968 && c<40623){   //中文范围
                return true;
            }
        }
        return  false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == mRequextCode) {
            if (resultCode == RESULT_OK) {
                final String result = data.getStringExtra("result");
                String width = data.getStringExtra("width");
                String height = data.getStringExtra("height");
                Log.e("---", result + " ; " + height + " ; " + width);

                try {
                    if (result.startsWith("http://") || result.startsWith("https://")) {
                        zhixing(result, Intent.ACTION_VIEW, "扫描到一个网址,是否打开");
                    } else if (result.startsWith("tel:")) {
                        zhixing(result, Intent.ACTION_DIAL, "扫描到一个电话号码,是否打开");
                    } else if (result.startsWith("smsto:")) {
                        zhixing(result, Intent.ACTION_SENDTO, "扫描到一个短信号码,是否打开");
                    } else if (result.startsWith("mailto:")) {
                        zhixing(result, Intent.ACTION_SENDTO, "扫描到一个邮件地址,是否打开");
                    } else if (result.startsWith("market://")) {
                        zhixing(result, Intent.ACTION_VIEW, "扫描到一个应用,是否打开");
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("扫描到一段文字")
                                .setMessage(result)
                                .setPositiveButton("确定", null)
                                .show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*根据扫描结果执行对应操作*/
    private void zhixing(final String result, final String action, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(result)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(action);
                        intent.setData(Uri.parse(result));
                        startActivity(intent);
                    }
                })
                .show();
    }
}
