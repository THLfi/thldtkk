"use strict";

angular.module("vtrFilters", [])
        .filter("utc", function () {
            return function (timestamp) {
                var date = new Date(timestamp);
                var dateObject = date.getFullYear() + "-" + ("0" + (date.getMonth() + 1)).slice(-2) + "-" + ("0" + date.getDate()).slice(-2);
                return dateObject;
            };
        });
