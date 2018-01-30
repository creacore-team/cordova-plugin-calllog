module.exports = {
  getCallLog: function(filters, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "CallLog", "getCallLog", [filters]);
  }
};
  