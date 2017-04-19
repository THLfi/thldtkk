app.controller("FrontPageController", function ($scope, Dataset) {
    Dataset.getDatasets().then(
            function(result){
               $scope.datasets = result; 
            });
});

