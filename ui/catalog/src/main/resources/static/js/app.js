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


app.controller("DataSetsController", function ($scope, $http, PATHS) {
    $http.get(PATHS.METADATA_API + "/termed/datasets?max=9999")
        .then(function(response) {
            $scope.datasets = response.data;
        });
});
