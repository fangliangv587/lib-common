package cenco.xz.com.commonlib;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cenco.lib.common.log.LogUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogUtils.d("xz","message log xz");
        LogUtils.i("helloworld");

    }
}
