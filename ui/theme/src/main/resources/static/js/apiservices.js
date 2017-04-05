"use strict";

var vtrApiServices = angular.module("vtrApiServices", ["vtrThemeCommon"]);

vtrApiServices.service("AuthenticationService",
        ["$http", "ContextService", function ($http, ContextService) {

                var serviceUrl = ContextService.getUserApiServiceUrl();

                this.sendLogin = function (username, password, optionalProject) {
                    var credentials = "username=" + username + "&password=" + password;

                    var optionalProjectParameter = "";
                    if ((optionalProject !== undefined || optionalProject !== null)
                            && optionalProject.length > 0) {
                        optionalProjectParameter = "&project=" + optionalProject;
                    }

                    return $http({
                        method: "POST",
                        url: serviceUrl + "/login",
                        headers: {
                            "Content-Type": "application/x-www-form-urlencoded"
                        },
                        data: credentials + optionalProjectParameter
                    });
                };

                this.sendLogout = function () {
                    return $http({
                        method: "POST",
                        url: serviceUrl + "/logout",
                        headers: {
                            "Content-Type": "application/x-www-form-urlencoded"
                        }
                    });
                };

                this.isAuthenticated = function () {
                    return $http({
                        method: "GET",
                        url: serviceUrl + "/is-authenticated"
                    });
                };

                this.getLandingPage = function () {
                    return $http({
                        method: "GET",
                        url: serviceUrl + "/landing-page"
                    });
                };

                this.getLoginPage = function () {
                    return $http({
                        method: "GET",
                        url: serviceUrl + "/login-page"
                    });
                };
            }]);

vtrApiServices.service("AuthorityService",
        ["$http", "ContextService", function ($http, ContextService) {

                var serviceUrl = ContextService.getUserApiServiceUrl();

                this.isAdmin = function () {
                    return $http({
                        method: "GET",
                        url: serviceUrl + "/is-admin"
                    });
                };

                this.isPersonnel = function () {
                    return $http({
                        method: "GET",
                        url: serviceUrl + "/is-personnel"
                    });
                };

                this.isCustomer = function () {
                    return $http({
                        method: "GET",
                        url: serviceUrl + "/is-customer"
                    });
                };

            }]);

vtrApiServices.service("ProjectService",
        ["$http", "ContextService", function ($http, ContextService) {

                var serviceUrl = ContextService.getUserApiServiceUrl();

                this.getProject = function () {
                    return $http({
                        method: "GET",
                        url: serviceUrl + "/project"
                    });
                };

                this.selectProject = function (projectId) {
                    return $http({
                        method: "POST",
                        url: serviceUrl + "/project/" + projectId + "/select"
                    });
                };
            }]);

vtrApiServices.service("ProjectSelector", function (ProjectService, ProjectFetcher) {

    this.selectProject = function (projectId, scope) {
        var selection = ProjectService.selectProject(projectId);
        selection.then(function successCallback(response) {
            ProjectFetcher.getProject(scope);
        });
    };
});

vtrApiServices.service("ProjectFetcher", function (ProjectService) {

    this.getProject = function (scope) {
        var project = ProjectService.getProject();
        project.then(function successCallback(response) {
            scope.project = response.data;
        }, function errorCallback(response) {
            return null;
        });
    };
});
