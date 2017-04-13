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
