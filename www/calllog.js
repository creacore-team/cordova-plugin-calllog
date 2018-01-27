module.exports = {
  getCallLog: function(dateFrom, dateTo, filters, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "CallLog", "getCallLog", [dateFrom, dateTo, filters]);
  }
};
  