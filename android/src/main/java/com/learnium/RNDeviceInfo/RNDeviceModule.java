package com.learnium.RNDeviceInfo;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings.Secure;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.google.android.gms.iid.InstanceID;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Nullable;

public class RNDeviceModule extends ReactContextBaseJavaModule {

  ReactApplicationContext reactContext;

  public RNDeviceModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNDeviceInfo";
  }

  private String getCurrentLanguage() {
      Locale current = getReactApplicationContext().getResources().getConfiguration().locale;
      if (Build.VERSION.SDK_INT >= 
          Build.VERSION_CODES.LOLLIPOP) {
          return current.toLanguageTag();
      } else {
          StringBuilder builder = new StringBuilder();
          builder.append(current.getLanguage());
          if (current.getCountry() != null) {
              builder.append("-");
              builder.append(current.getCountry());
          }
          return builder.toString();
      }
  }

  private String getCurrentCountry() {
    Locale current = getReactApplicationContext().getResources().getConfiguration().locale;
    return current.getCountry();
  }

  private Boolean isEmulator() {
    return Build.FINGERPRINT.startsWith("generic")
      || Build.FINGERPRINT.startsWith("unknown")
      || Build.MODEL.contains("google_sdk")
      || Build.MODEL.contains("Emulator")
      || Build.MODEL.contains("Android SDK built for x86")
      || Build.MANUFACTURER.contains("Genymotion")
      || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
      || "google_sdk".equals(Build.PRODUCT);
  }

  private Boolean isTablet() {
    int layout = getReactApplicationContext().getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
    return layout == Configuration.SCREENLAYOUT_SIZE_LARGE || layout == Configuration.SCREENLAYOUT_SIZE_XLARGE;
  }

  @ReactMethod
  public void getUptime(Promise promise) {
      WritableMap map = Arguments.createMap();
      map.putString("uptime", String.valueOf(SystemClock.uptimeMillis()));
      promise.resolve(map);
  }

  @Override
  public @Nullable Map<String, Object> getConstants() {
    HashMap<String, Object> constants = new HashMap<String, Object>();

    PackageManager packageManager = this.reactContext.getPackageManager();
    String packageName = this.reactContext.getPackageName();

    constants.put("appVersion", "not available");
    constants.put("buildVersion", "not available");
    constants.put("buildNumber", 0);

    try {
      PackageInfo info = packageManager.getPackageInfo(packageName, 0);
      constants.put("appVersion", info.versionName);
      constants.put("buildNumber", info.versionCode);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }

    String deviceName = "Unknown";

    try {
      BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
      deviceName = myDevice.getName();
    } catch(Exception e) {
      e.printStackTrace();
    }

    constants.put("instanceId", InstanceID.getInstance(this.reactContext).getId());
    constants.put("deviceName", deviceName);
    constants.put("systemName", "Android");
    constants.put("systemVersion", Build.VERSION.RELEASE);
    constants.put("model", Build.MODEL);
    constants.put("brand", Build.BRAND);
    constants.put("deviceBuildDisplay", Build.DISPLAY);
    constants.put("deviceBuildFingerprint", Build.FINGERPRINT);
    constants.put("deviceBuildID", Build.ID);
    constants.put("deviceId", Build.BOARD);
    constants.put("deviceSerial", Build.SERIAL);
    constants.put("deviceLocale", this.getCurrentLanguage());
    constants.put("deviceCountry", this.getCurrentCountry());
    constants.put("uniqueId", Secure.getString(this.reactContext.getContentResolver(), Secure.ANDROID_ID));
    constants.put("systemManufacturer", Build.MANUFACTURER);
    constants.put("bundleId", packageName);
    constants.put("userAgent", System.getProperty("http.agent"));
    constants.put("timezone", TimeZone.getDefault().getID());
    constants.put("isEmulator", this.isEmulator());
    constants.put("isTablet", this.isTablet());

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
      try {
        constants.put("hasSimDetails", true);
        constants.put("dualSim", false);

        List<SubscriptionInfo> subscriptionInfos = SubscriptionManager.from(getReactApplicationContext()).getActiveSubscriptionInfoList();
        for (int i = 0; i < subscriptionInfos.size(); i++) {
          SubscriptionInfo lsuSubscriptionInfo = subscriptionInfos.get(i);
          constants.put("simNumber" + i, lsuSubscriptionInfo.getNumber());
          constants.put("simNetwork" + i, lsuSubscriptionInfo.getCarrierName());
          constants.put("simCountryIso" + i, lsuSubscriptionInfo.getCountryIso());
          constants.put("simSlotIndex" + i, lsuSubscriptionInfo.getSimSlotIndex());
          constants.put("simSlotMcc" + i, lsuSubscriptionInfo.getMcc());
          constants.put("simSlotMnc" + i, lsuSubscriptionInfo.getMnc());
        }

        TelephonyManager tMgr = (TelephonyManager) getReactApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();

        constants.put("phoneNumber", mPhoneNumber);
        constants.put("phoneSimSerialNumber", tMgr.getSimSerialNumber());
        constants.put("slotImei0", tMgr.getDeviceId(0));
        constants.put("subscriberId", tMgr.getSubscriberId());
        constants.put("line1Number", tMgr.getLine1Number());
        constants.put("dataState", tMgr.getDataState());
        constants.put("dataActivity", tMgr.getDataActivity());

        if (tMgr.getPhoneCount() > 1) {
          constants.put("slotImei1", tMgr.getDeviceId(1));
          constants.put("dualSim", true);
        }

      } catch (Exception ex) {
        Log.d("TAG", ex.toString());
      }
    } else {
      constants.put("hasSimDetails", false);
      constants.put("dualSim", false);
    }

    return constants;
  }
}
