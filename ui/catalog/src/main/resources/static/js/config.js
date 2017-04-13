"use strict";

var app = angular.module("catalog");

app.config(function (PATHS, $routeProvider, $httpProvider, $resourceProvider) {

    var contextPath = PATHS.APPLICATION;
    $routeProvider.when("/index", {
        templateUrl: contextPath + "/partials/index.html",
        controller: "FrontPageController",
        reloadOnSearch: false
    }).when("/", {
        redirectTo: "/index"
    }).otherwise({
        redirectTo: "/"
    });
    // disable caches for IE
    if (!$httpProvider.defaults.headers.get) {
        $httpProvider.defaults.headers.get = {};
    }
    $httpProvider.defaults.headers.get["Cache-Control"] = "no-cache";
    $httpProvider.defaults.headers.get["Pragma"] = "no-cache";
    $resourceProvider.defaults.stripTrailingSlashes = false;
});


