var app = angular.module("editor");

app.service('Dataset', function($http, PATHS) {
    
    this.getDatasets = function() {        
        return $http.get(PATHS.METADATA_API+ "/termed/datasets").then(
                function(response){
                    return angular.fromJson(response.data);
                });

    };
});