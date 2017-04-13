function ThemeCommon() {

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

