'use strict';

angular.module('app.services').service('authenticationService', ['$http', '$q', 'base64Service', 'storageService', 'storageConstant', 'propertiesConstant', function ($http, $q, base64Service, storageService, storageConstant, propertiesConstant) {
    this.login = function (credentials) {
        var d = $q.defer();

        $http.defaults.headers.common.Authorization = 'Basic ' + base64Service.encode(credentials.email + ':' + credentials.password);

        $http.post(propertiesConstant.RESTFUL_API_URL + '/api/authentication/login', null)
            .success(function (data, status, headers, config) {
                storageService.setSessionItem(storageConstant.AUTH_TOKEN, headers('X-AUTH-TOKEN'));
                storageService.setLocalItem(storageConstant.AUTH_TOKEN, headers('X-AUTH-TOKEN'));
                console.log("X-AUTH-TOKEN="+headers('X-AUTH-TOKEN'));
                delete $http.defaults.headers.common.Authorization;

                d.resolve();
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.logout = function () {
        var d = $q.defer();

        var authToken = storageService.getLocalItem(storageConstant.AUTH_TOKEN);

        var headers = (authToken) ? {"X-AUTH-TOKEN": authToken} : {};

        $http.get(propertiesConstant.RESTFUL_API_URL + '/api/authentication/logout/validate?status=success', headers)
            .success(function () {

                storageService.removeSessionItem(storageConstant.AUTH_TOKEN);
                storageService.removeSessionItem(storageConstant.USER);
                storageService.removeLocalItem(storageConstant.AUTH_TOKEN);
                storageService.removeLocalItem(storageConstant.USER);

                d.resolve();
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };
}]);