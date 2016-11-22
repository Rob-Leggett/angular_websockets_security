'use strict';

angular.module('app.services').service('userService', ['$http', '$q', 'propertiesConstant', function ($http, $q, propertiesConstant) {
    this.retrieve = function retrieve() {
        var d = $q.defer();

        $http.get(propertiesConstant.RESTFUL_API_URL + '/api/user')
            .success(function (user) {
                d.resolve(user);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };
}]);