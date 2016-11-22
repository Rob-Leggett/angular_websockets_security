'use strict';

angular.module('app.services').service('customerService', [ '$http', '$q', 'propertiesConstant', function ($http, $q, propertiesConstant) {
    this.getCustomers = function () {
        var d = $q.defer();

        $http.get(propertiesConstant.RESTFUL_API_URL + '/api/customer')
            .success(function (customers) {
                d.resolve(customers);
            })
            .error(function (data, status, headers, config) {
                d.reject(status);
            });

        return d.promise;
    };

    this.deleteCustomer = function (id) {
        var d = $q.defer();

        $http.delete(propertiesConstant.RESTFUL_API_URL + '/api/customer/' + id)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function (data, status, headers, config) {
                d.reject(status);
            });

        return d.promise;
    };

    this.saveCustomer = function (customer) {
        var d = $q.defer();

        $http.post(propertiesConstant.RESTFUL_API_URL + '/api/customer', customer)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function (data, status, headers, config) {
                d.reject(status);
            });

        return d.promise;
    };
}]);