package jcifs;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.mx.lib.samba.http.MyNanoHTTPD;

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2017/9/7.
 * 联系方式: zmx_final@163.com
 */

public class FileService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            new MyNanoHTTPD(10001).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
