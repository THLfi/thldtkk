"use strict";

var app = angular.module("catalog");

app.service("dataSetService", ["$http", "$q", "PATHS", function($http, $q, PATHS) {
    var fetchDataSets = function(service, deferred) {
        $http.get(PATHS.METADATA_API + "/termed/datasets?max=99999")
            .then(function(response) {
                    deferred.resolve(response.data);
                    service._dataSets = response.data;
                    service._dataSetsLastFetched = Date.now();
                },
                function(response) {
                    deferred.reject({ message: response.statusText });
                });
    }

    var isCacheExpired = function(service) {
        return service._dataSetsLastFetched === undefined
            || Date.now() > (service._dataSetsLastFetched + 30000)
    }

    this.getAllDataSets = function() {
        var deferred = $q.defer();

        if (this._dataSets === undefined) {
            fetchDataSets(this, deferred);
        }
        else if (isCacheExpired(this)) {
            fetchDataSets(this, deferred);
        }
        else {
            deferred.resolve(this._dataSets);
        }

        return deferred.promise;
    }

    this.getDataSet = function(dataSetId) {
        var deferred = $q.defer();

        this.getAllDataSets().then(function(dataSets) {
            angular.forEach(dataSets, function(dataSet) {
                if (dataSetId == dataSet.id) {
                    deferred.resolve(dataSet);
                }
            });
            deferred.reject({ message: "Data set not found" });
        },
        function(reason) {
            deferred.reject(reason);
        });

        return deferred.promise;
    }
}]);
