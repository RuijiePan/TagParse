package com.ruijie.tagparse;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.tv_text);
        //String text = "<myfont color='red' size='50px'>" + "要显示的数据" + "</myfont>" + "<myfont color='0xffffffff' size='25px'>" + "asdasda" + "</myfont>";
        Spanned spanned = Html.fromHtml(getString(R.string.text), null, new TagParseHandle(this));
        mTextView.setText(spanned);
        //mTextView.setText(Html.fromHtml(getResources().getString(R.string.text), null, new TagParseHandle(this)));
    }
}
