function ThemeCommon() {

    this.getPage = function (pages, title) {
        if (pages !== undefined && pages !== null && pages.length !== undefined) {
            for (var i = 0; i < pages.length; i++) {
                var page = pages[i];
                if (page.title === title) {
                    return page;
                }
            }
        }

        return undefined;
    };

    this.getContextPath = function (appName) {
        var contextPath = undefined;

        // find contextPath by appName
        if (appName !== undefined && appName !== null && appName.length > 0) {
            contextPath = this.getValueOfAttributeByQuerySelector(
                    "[multi-app=" + appName + "]",
                    "context-path");

            if (contextPath === undefined) {
                contextPath = this.getValueOfAttributeByQuerySelector(
                        "[ng-app=" + appName + "]",
                        "context-path");
            }
        }
        // find first app context path
        else {
            contextPath = this.getValueOfAttributeByQuerySelector(
                    "[ng-app]",
                    "context-path");
        }

        return contextPath;
    };

    this.getServiceUrl = function (appName, serviceName) {
        return this.getValueOfAttributeByQuerySelector(
                "[multi-app=" + appName + "]",
                serviceName + "-service-url");
    };

    this.getApiServiceUrl = function (serviceName) {
        return this.getValueOfAttributeByQuerySelector(
                "script#api-services",
                serviceName + "-service-url");
    };

    this.getUiUrl = function (appName, uiName) {
        return this.getValueOfAttributeByQuerySelector(
                "[multi-app=" + appName + "]",
                uiName + "-ui-url");
    };

    this.getUiServiceUrl = function (serviceName) {
        return this.getValueOfAttributeByQuerySelector(
                "script#ui-services",
                serviceName + "-ui-url");
    };

    this.getExternalServiceUrl = function (serviceName) {
        return this.getValueOfAttributeByQuerySelector(
                "script#external-services",
                serviceName + "-url");
    };

    this.getValueOfAttributeByQuerySelector = function (selector, attributeName) {
        var el = document.querySelector(selector);
        if (el !== null) {
            return el.getAttribute(attributeName);
        } else {
            return undefined;
        }
    };

    this.goToPath = function (contextPath, separator, path, shouldReload) {
        var url = location.origin + contextPath + separator + path;
        this.goToUrl(url, shouldReload);
    };

    this.goToUrl = function (url, shouldReload) {
        location.href = url;

        if (shouldReload) {
            location.reload();
        }
    };

    this.setPageTitle = function (title) {
        var titleElement = document.querySelector("head title");
        titleElement.textContent = title;
    };

    this.bootstrapMultiApp = function (attrName) {
        var appNodes = document.querySelectorAll("*[" + attrName + "]");

        angular.forEach(appNodes, function (appNode) {
            var appName = angular.element(appNode).attr(attrName);
            angular.bootstrap(appNode, [appName]);
        });
    };

    this.includeCustomStyleSheet = function (pathToCssFile) {
        link = document.createElement('link');
        link.href = pathToCssFile;
        link.rel = "stylesheet";
        link.type = "text/css";
        document.getElementsByTagName("head")[0].appendChild(link);
    };

    this.isNotEmpty = function (value) {
        return !this.isEmpty(value);
    };

    this.isEmpty = function (value) {
        return value === undefined || value === null || value.length === 0;
    };

    this.getLast = function (arr) {
        if (arr !== undefined && arr !== null) {
            var len = arr.length;
            return arr[len - 1];
        } else {
            return null;
        }
    };
}

var vtrThemeCommon = angular.module("vtrThemeCommon", []);

vtrThemeCommon.service("ContextService", function () {
    var themeCommon = new ThemeCommon();

    this.getAppContextPath = function (appName) {
        return themeCommon.getContextPath(appName);
    };

    this.getSitePagesApiServiceUrl = function () {
        return themeCommon.getApiServiceUrl("site-pages");
    };

    this.getLocalizationApiServiceUrl = function () {
        return themeCommon.getApiServiceUrl("localization");
    };

    this.getUserApiServiceUrl = function () {
        return themeCommon.getApiServiceUrl("user");
    };

    this.getAppointmentApiServiceUrl = function () {
        return themeCommon.getApiServiceUrl("appointment");
    };

    this.getCustomerApiServiceUrl = function () {
        return themeCommon.getApiServiceUrl("customer");
    };

    this.getProtocolApiServiceUrl = function () {
        return themeCommon.getApiServiceUrl("protocol");
    };

    this.getIdentificationServiceUrl = function () {
        return themeCommon.getApiServiceUrl("identification");
    };

    this.getBatchApiServiceUrl = function () {
        return themeCommon.getApiServiceUrl("batch");
    };

    this.getPersonnelUiUrl = function () {
        return themeCommon.getUiServiceUrl("personnel");
    };

    this.getCustomerUiUrl = function () {
        return themeCommon.getUiServiceUrl("customer");
    };

    this.getAppointmentAdminUiUrl = function () {
        return themeCommon.getUiServiceUrl("appointment-admin");
    };

    this.getLomakepalveluUrl = function () {
        return themeCommon.getExternalServiceUrl("lomakepalvelu");
    };
});

vtrThemeCommon.service("PageService", function () {
    var themeCommon = new ThemeCommon();

    this.updateHtmlTitle = function (pageName, projectName, applicationName) {
        var pagePart = isNotEmpty(pageName) ? pageName + " - " : "";
        var projectPart = isNotEmpty(projectName) ? projectName + " - " : "";
        var title = pagePart + projectPart + applicationName;

        themeCommon.setPageTitle(title);
    };

    this.updatePathAndReload = function (contextPath, separator, path) {
        themeCommon.goToPath(contextPath, separator, path, true);
    };

    this.updateUrl = function (url) {
        themeCommon.goToUrl(url, false);
    };

    function isNotEmpty(value) {
        return themeCommon.isNotEmpty(value);
    }
});
