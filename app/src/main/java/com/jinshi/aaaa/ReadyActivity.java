package com.jinshi.aaaa;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinshi.yifuguojiwapapp.mt4webh5.R;

public class ReadyActivity extends Activity implements View.OnClickListener {
    private TextView tvOpen, tvXieyi;
    private Dialog dialog;
    private ImageView ivExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_ready);
        Log.e("ready", "thisis ready");
        init();
    }

    private void init() {
        tvOpen = findViewById(R.id.tv_open);
        tvXieyi = findViewById(R.id.tv_xieyi);
        tvXieyi.setOnClickListener(this);
        tvOpen.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_open:
                startActivity(new Intent(getApplicationContext(), RegisterWebAcitivity.class));
                break;
            case R.id.tv_xieyi:
                createDialog();
                dialog.show();
                break;
            case R.id.iv_exit:
                dialog.dismiss();
                break;
        }
    }

    private void createDialog() {
        dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        View view = LayoutInflater.from(this).inflate(R.layout.acitivity_xieyi, null);
        ivExit = view.findViewById(R.id.iv_exit);
        ivExit.setOnClickListener(this);
        dialog.setContentView(view);
        dialog.show();
    }
}
