"use strict";

var app = angular.module(
        "editor", [
            "ngResource",
            "ngRoute",
            "dtkkTheme"
        ]);

app.constant("PATHS", {
    APPLICATION: (new ThemeCommon()).getContextPath("editor"),
    METADATA_API: (new ThemeCommon()).getApiServiceUrl("metadata"),
    MAIN_UI: (new ThemeCommon()).getUiServiceUrl("main")
});