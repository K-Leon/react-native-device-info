/**
 * @providesModule react-native-device-info
 */

var RNDeviceInfo = require('react-native').NativeModules.RNDeviceInfo;

module.exports = {
  getUniqueID: function () {
    return RNDeviceInfo.uniqueId;
  },
  getInstanceID: function() {
    return RNDeviceInfo.instanceId;
  },
  getDeviceId: function () {
    return RNDeviceInfo.deviceId;
  },
  getManufacturer: function () {
    return RNDeviceInfo.systemManufacturer;
  },
  getModel: function () {
    return RNDeviceInfo.model;
  },
  getBrand: function () {
    return RNDeviceInfo.brand;
  },
  getSystemName: function () {
    return RNDeviceInfo.systemName;
  },
  getSystemVersion: function () {
    return RNDeviceInfo.systemVersion;
  },
  getBundleId: function() {
    return RNDeviceInfo.bundleId;
  },
  getBuildNumber: function() {
    return RNDeviceInfo.buildNumber;
  },
  getVersion: function() {
    return RNDeviceInfo.appVersion;
  },
  getReadableVersion: function() {
    return RNDeviceInfo.appVersion + "." + RNDeviceInfo.buildNumber;
  },
  getDeviceName: function() {
    return RNDeviceInfo.deviceName;
  },
  getUserAgent: function() {
    return RNDeviceInfo.userAgent;
  },
  getDeviceLocale: function() {
    return RNDeviceInfo.deviceLocale;
  },
  getDeviceCountry: function() {
    return RNDeviceInfo.deviceCountry;
  },
  getTimezone: function() {
    return RNDeviceInfo.timezone;
  },
  isEmulator: function() {
    return RNDeviceInfo.isEmulator;
  },
  isTablet: function() {
    return RNDeviceInfo.isTablet;
  },
  getSerial: function () {
    return RNDeviceInfo.deviceSerial
  },
  deviceBuildDisplay: function() {
    return RNDeviceInfo.deviceBuildDisplay;
  },
  deviceBuildFingerprint: function() {
    return RNDeviceInfo.deviceBuildFingerprint;
  },
  deviceBuildID: function () {
    return RNDeviceInfo.deviceBuildID
  },
  getUptime: async function () {
    let uptime = await RNDeviceInfo.getUptime()
    return uptime.uptime
  }
};
