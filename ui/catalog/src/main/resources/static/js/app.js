"use strict";

var app = angular.module(
        "catalog", [
            "ngResource",
            "ngRoute",
            "dtkkTheme"
        ]);

app.constant("PATHS", {
    APPLICATION: (new ThemeCommon()).getContextPath("catalog"),
    METADATA_API: (new ThemeCommon()).getApiServiceUrl("metadata"),
    MAIN_UI: (new ThemeCommon()).getUiServiceUrl("main")
});

app.controller("FrontPageController", function () {

});

app.controller("BrowseDataSetsController", function ($scope, dataSetService) {
    dataSetService.getAllDataSets().then(function(dataSets) {
        $scope.dataSets = dataSets;
    });
});

app.controller("ViewDataSetController", function ($scope, $routeParams, dataSetService) {
    dataSetService.getDataSet($routeParams.dataSetId).then(function(dataSet) {
        $scope.dataSet = dataSet;
    });
});
