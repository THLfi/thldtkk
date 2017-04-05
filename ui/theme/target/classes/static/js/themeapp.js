"use strict";

var vtrTheme = angular.module("vtrTheme", [
    "vtrApiResources",
    "vtrApiServices",
    "vtrConstants"
]);

vtrTheme.controller('ThemeHeaderController', function (
        $rootScope, $scope, $location,
        SitePagesResource, PersonNameResource, THEME_COMMON) {

    $rootScope.$watch("forceReload", function (value) {
        if (value === true) {
            initController();
        }
    });

    $scope.quickLinks = [];
    $scope.subPages = [];
    $scope.pages = [];
    $scope.personName = "";

    var themeCommon = new ThemeCommon();

    initController();

    function initController() {
        SitePagesResource.query().$promise.then(function (sitePages) {
            $scope.quickLinks = themeCommon.getPage(sitePages, "quick-links").subpages;
            $scope.subPage = themeCommon.getPage($scope.quickLinks);
            $scope.pages = sitePages;
        });

        $scope.personName = PersonNameResource.get();
    }

    $scope.getSubpages = function (page) {
        return page.subpages || [];
    };

    $scope.hasSubpages = function (page) {
        return THEME_COMMON.isNotEmpty($scope.getSubpages(page));
    };

    $scope.getClass = function (path) {
        if (path === null) {
            return "";
        } else {
            if (path.endsWith("/1") || path.endsWith("/")) {
                path = path.substr(0, path.lastIndexOf("/"));
            }
            path = path.substr(path.lastIndexOf("/"), path.length);
            var cssClass = ($location.path().substr(0, path.length) === path) ? 'active' : '';
            if (cssClass !== 'active' && path.indexOf("myevents") > -1) {
                if ($location.path().indexOf("update") > -1 || $location.path().indexOf("confirm") > -1) {
                    cssClass = 'active';
                }
            }
            return cssClass;
        }
    };
});

vtrTheme.controller('ThemeNavigationController', function (
        $rootScope,
        $scope,
        $route,
        $location,
        SitePagesResource,
        PersonNameResource,
        AuthorityService,
        ProjectSearchService,
        ProjectSelector,
        CategoryService,
        ProjectFetcher) {

    $rootScope.$watch("forceReload", function (value) {
        if (value === true) {
            initController();
        }
    });

    var themeCommon = new ThemeCommon();

    $scope.loginPage = undefined;
    $scope.logoutPage = undefined;
    $scope.createAppointmentPage = undefined;
    $scope.scheduleAppointmentsPage = undefined;
    $scope.appointmentManagement = undefined;
    $scope.quickLinks = [];
    $scope.pages = [];
    $scope.isPersonnel = false;
    $scope.personName = "";
    $scope.loading = false;
    $scope.availableProjects = null;

    $scope.availableCategories = [];
    ProjectFetcher.getProject($scope);

    initController();

    function initController() {
        SitePagesResource.query().$promise.then(function (sitePages) {
            var loginPages = themeCommon.getPage(sitePages, "login-pages").subpages;

            $scope.loginPage = themeCommon.getPage(loginPages, "login");
            $scope.logoutPage = themeCommon.getPage(loginPages, "logout");

            $scope.appointmentManagement = themeCommon.getPage(sitePages, "appointment-management");
            if ($scope.appointmentManagement !== undefined) {
                var appointmentPages = themeCommon.getPage(sitePages, "appointment-management").subpages;
                $scope.createAppointmentPage = themeCommon.getPage(appointmentPages, "create appointment");
                $scope.scheduleAppointmentsPage = themeCommon.getPage(appointmentPages, "schedule appointments");
            }

            $scope.quickLinks = themeCommon.getPage(sitePages, "quick-links").subpages;

            $scope.pages = sitePages;
        });

        $scope.personName = PersonNameResource.get();

        AuthorityService.isPersonnel().then(function (response) {
            $scope.isPersonnel = response.data.personnel;
            if ($scope.isPersonnel) {
                if (!$scope.projectId) {
                    $scope.loading = true;
                    $scope.availableProjects = ProjectSearchService.get();
                    $scope.availableProjects.$promise.finally(function () {
                        $scope.loading = false;
                    });
                }
            }
        });
    }


    $scope.selectProject = function (project) {
        $scope.loading = true;
        ProjectSelector.selectProject(project.projectId, $scope);
        $scope.availableCategories = CategoryService.query({projectId: project.projectId});
        $scope.loading = false;
        $route.reload(true);
    };

    $scope.isShowCalendarManagement = function () {
        return ($scope.appointmentManagement !== undefined);
    };

    $scope.isShowLogin = function () {
        return ($scope.loginPage !== undefined);
    };

    $scope.isShowLogout = function () {
        return ($scope.logoutPage !== undefined);
    };

    $scope.getClass = function (path) {
        if (path === null) {
            return "";
        } else {
            if (path.endsWith("/1") || path.endsWith("/")) {
                path = path.substr(0, path.lastIndexOf("/"));
            }
            path = path.substr(path.lastIndexOf("/"), path.length);
            var cssClass = ($location.path().substr(0, path.length) === path) ? 'active' : '';
            if (cssClass !== 'active' && path.indexOf("myevents") > -1) {
                if ($location.path().indexOf("update") > -1 || $location.path().indexOf("confirm") > -1) {
                    cssClass = 'active';
                }
            }
            return cssClass;
        }
    };
});

vtrTheme.controller('ThemeFooterController', function ($rootScope, $scope, $translate) {

    $translate($rootScope.projectAbbr + "-contact-information-email").then(
            function (translation) {
                $scope.contactInformationEmail = translation;
            },
            function () {
                $scope.contactInformationEmail = "";
            }
    );

    $translate($rootScope.projectAbbr + "-contact-information-earphone").then(
            function (translation) {
                $scope.contactInformationEarphone = translation;
            },
            function () {
                $scope.contactInformationEarphone = "";
            }
    );
});

vtrTheme.directive("themeProgressBar", function () {
    return {
        templateUrl: "directives/theme_progressbar.html"
    };
});

vtrTheme.directive("themeSpinner", function () {
    return {
        templateUrl: "directives/theme_spinner.html"
    };
});

vtrTheme.directive("themeMessageBox", function () {
    return {
        restrict: "E",
        scope: {
            messageClass: "@",
            isVisible: "=",
            title: "@",
            message: "@"
        },
        link: function (scope) {
            scope.closeBox = function () {
                scope.isVisible = false;
            };
        },
        templateUrl: "directives/theme_messagebox.html"
    };
});
