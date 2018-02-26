package cenco.xz.com.commonlib;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cenco.lib.common.LogUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogUtil.i("message");
    }
}
