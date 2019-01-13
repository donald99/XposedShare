package d.xposedshare;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            TextView imei = findViewById(R.id.tv_show);
            TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            imei.setText("IMEI:" + telephonyManager.getDeviceId());
        }catch (Exception e){}

    }

    @Override
    protected void onStart() {
        super.onStart();
        requestCodeQRCodePermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        TextView imei = findViewById(R.id.tv_show);
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        imei.setText("imei:" + telephonyManager.getDeviceId());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }

    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;
    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private void requestCodeQRCodePermissions() {
        String[] permissions = {
                Manifest.permission.READ_PHONE_STATE
        };
        if (!EasyPermissions.hasPermissions(this, permissions)) {
            EasyPermissions.requestPermissions(this,
                    "这些权限都需要！！！",
                    REQUEST_CODE_QRCODE_PERMISSIONS, permissions);
        }
    }
}
