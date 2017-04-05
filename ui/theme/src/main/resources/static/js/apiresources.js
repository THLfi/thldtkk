"use strict";

var vtrApiResources = angular.module("vtrApiResources", ["vtrThemeCommon"]);

vtrApiResources.factory("SitePagesResource", function ($resource, ContextService) {
    var serviceUrl = ContextService.getSitePagesApiServiceUrl();
    return $resource(serviceUrl + "/logistiikka");
});

vtrApiResources.factory("PersonNameResource", function ($resource, ContextService) {
    var serviceUrl = ContextService.getUserApiServiceUrl();
    return $resource(serviceUrl + "/person-name", {}, {
        get: {method: "GET", isArray: false}
    });
});

vtrApiResources.factory("ProjectSearchService", function ($resource, ContextService) {
    return $resource(ContextService.getCustomerApiServiceUrl() + "/available-projects-for-professional/", {}, {
        get: {method: "GET", isArray: true}
    });
});
vtrApiResources.factory("CategoryService", function ($resource, ContextService) {
    return $resource(ContextService.getAppointmentApiServiceUrl() + "/project/:projectId/available-categories/");
});