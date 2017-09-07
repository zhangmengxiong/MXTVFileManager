package com.mx.dlna;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.mx.dlna.dms.MediaServer;
import com.mx.dlna.util.NetUtils;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;

import java.util.ArrayList;

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2017/9/6.
 * 联系方式: zmx_final@163.com
 */
public class DlnaScan {
    private static final String TAG = DlnaScan.class.getSimpleName();
    private Context mContext;
    private IDlnaScan dlnaScan;

    private AndroidUpnpService upnpService;
    private MediaServer mediaServer;
    private DeviceListRegistryListener deviceListRegistryListener;
    private ArrayList<DeviceItem> mDevList = new ArrayList<DeviceItem>();
    private ArrayList<DeviceItem> mDmrList = new ArrayList<DeviceItem>();

    public DlnaScan(Context context) {
        mContext = context;
        deviceListRegistryListener = new DeviceListRegistryListener();
    }

    public void scan(IDlnaScan dlnaScan) {
        this.dlnaScan = dlnaScan;
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetUtils.init(mContext);
                mContext.bindService(
                        new Intent(mContext, AndroidUpnpServiceImpl.class),
                        serviceConnection, Context.BIND_AUTO_CREATE);
            }
        }).start();
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            mDevList.clear();
            mDmrList.clear();

            upnpService = (AndroidUpnpService) service;
            if (dlnaScan != null && upnpService != null) {
                dlnaScan.onStart(upnpService);
            }
            Log.v(TAG, "Connected to UPnP Service");

            if (mediaServer == null) {
                try {
                    mediaServer = new MediaServer(mContext);
                    upnpService.getRegistry()
                            .addDevice(mediaServer.getDevice());
                    DeviceItem localDevItem = new DeviceItem(
                            mediaServer.getDevice());

                    deviceListRegistryListener.deviceAdded(localDevItem);
                } catch (Exception ex) {
                    // TODO: handle exception
                    Log.e(TAG, "Creating demo device failed", ex);
                    Toast.makeText(mContext, "Creating demo device failed", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // xgf
            for (Device device : upnpService.getRegistry().getDevices()) {
                if (device.getType().getNamespace().equals("schemas-upnp-org")
                        && device.getType().getType().equals("MediaServer")) {
                    final DeviceItem display = new DeviceItem(device, device
                            .getDetails().getFriendlyName(),
                            device.getDisplayString(), "(REMOTE) "
                            + device.getType().getDisplayString());
                    deviceListRegistryListener.deviceAdded(display);
                }
            }

            // Getting ready for future device advertisements
            upnpService.getRegistry().addListener(deviceListRegistryListener);
            // Refresh device list
            upnpService.getControlPoint().search();
        }

        public void onServiceDisconnected(ComponentName className) {
            upnpService = null;
        }
    };

    private class DeviceListRegistryListener extends DefaultRegistryListener {

		/* Discovery performance optimization for very slow Android devices! */

        @Override
        public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
        }

        @Override
        public void remoteDeviceDiscoveryFailed(Registry registry, final RemoteDevice device, final Exception ex) {
        }

		/*
         * End of optimization, you can remove the whole block if your Android
		 * handset is fast (>= 600 Mhz)
		 */

        @Override
        public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
            Log.v(TAG, "remoteDeviceAdded: " + device.toString());
            if (device.getType().getNamespace().equals("schemas-upnp-org")
                    && device.getType().getType().equals("MediaServer")) {
                final DeviceItem display = new DeviceItem(device, device
                        .getDetails().getFriendlyName(),
                        device.getDisplayString(), "(REMOTE) "
                        + device.getType().getDisplayString());
                deviceAdded(display);
            }

            if (device.getType().getNamespace().equals("schemas-upnp-org")
                    && device.getType().getType().equals("MediaRenderer")) {
                final DeviceItem dmrDisplay = new DeviceItem(device, device
                        .getDetails().getFriendlyName(),
                        device.getDisplayString(), "(REMOTE) "
                        + device.getType().getDisplayString());
                dmrAdded(dmrDisplay);
            }
        }

        @Override
        public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
            Log.v(TAG, "remoteDeviceRemoved: " + device.toString());
            final DeviceItem display = new DeviceItem(device,
                    device.getDisplayString());
            deviceRemoved(display);

            if (device.getType().getNamespace().equals("schemas-upnp-org")
                    && device.getType().getType().equals("MediaRenderer")) {
                final DeviceItem dmrDisplay = new DeviceItem(device, device
                        .getDetails().getFriendlyName(),
                        device.getDisplayString(), "(REMOTE) "
                        + device.getType().getDisplayString());
                dmrRemoved(dmrDisplay);
            }
        }

        @Override
        public void localDeviceAdded(Registry registry, LocalDevice device) {
            Log.v(TAG, "localDeviceAdded: " + device.toString());
            final DeviceItem display = new DeviceItem(device, device
                    .getDetails().getFriendlyName(), device.getDisplayString(),
                    "(REMOTE) " + device.getType().getDisplayString());
            deviceAdded(display);
        }

        @Override
        public void localDeviceRemoved(Registry registry, LocalDevice device) {
            Log.v(TAG, "localDeviceRemoved: " + device.toString());
            final DeviceItem display = new DeviceItem(device,
                    device.getDisplayString());
            deviceRemoved(display);
        }

        void deviceAdded(final DeviceItem di) {
//            Log.v(TAG, "deviceAdded: " + di.getDevice().toString());
            if (!mDevList.contains(di)) {
                mDevList.add(di);
            }
            if (dlnaScan != null) {
                dlnaScan.findDevice(di);
            }
        }

        void deviceRemoved(final DeviceItem di) {
//            Log.v(TAG, "deviceRemoved: " + di.getDevice().toString());
            mDevList.remove(di);

            if (dlnaScan != null) {
                dlnaScan.removeDevice(di);
            }
        }

        void dmrAdded(final DeviceItem di) {
//            Log.v(TAG, "dmrAdded: " + di.getDevice().toString());
            if (!mDmrList.contains(di)) {
                mDmrList.add(di);
            }
            if (dlnaScan != null) {
                dlnaScan.findMediaRenderer(di);
            }
        }

        void dmrRemoved(final DeviceItem di) {
//            Log.v(TAG, "dmrRemoved: " + di.getDevice().toString());
            if (mDmrList != null) mDmrList.remove(di);

            if (dlnaScan != null) {
                dlnaScan.removeMediaRenderer(di);
            }
        }
    }
}
