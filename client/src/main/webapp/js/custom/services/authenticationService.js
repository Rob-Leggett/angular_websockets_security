'use strict';

angular.module('app.services').service('authenticationService', ['$http', '$q', 'base64Service', 'storageService', 'storageConstant', 'propertiesConstant', function ($http, $q, base64Service, storageService, storageConstant, propertiesConstant) {
    this.login = function (credentials) {
        var d = $q.defer();

        $http.defaults.headers.common.Authorization = 'Basic ' + base64Service.encode(credentials.email + ':' + credentials.password);

        $http.post(propertiesConstant.RESTFUL_API_URL + '/api/authentication/login', null)
            .success(function (data, status, headers, config) {

                storageService.setSessionItem(storageConstant.AUTH_TOKEN, headers('X-AUTH-TOKEN'));

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

        $http.post(propertiesConstant.RESTFUL_API_URL + '/api/authentication/logout', null)
            .success(function () {

                storageService.removeSessionItem(storageConstant.AUTH_TOKEN);
                storageService.removeSessionItem(storageConstant.USER);

                d.resolve();
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };
}]);