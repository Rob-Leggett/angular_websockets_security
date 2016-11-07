'use strict';

angular.module('app').config(['$routeProvider', '$httpProvider', '$locationProvider', function ($routeProvider, $httpProvider, $locationProvider) {

  $locationProvider.html5Mode(true);

  var user = ['$q', '$rootScope', 'userService', 'storageService', 'socketService', 'storageConstant',
    function ($q, $rootScope, userService, storageService, socketService, storageConstant) {
      return $q(function (resolve, reject) {
        var user = storageService.getSessionItem(storageConstant.USER);

        if (user === null || user === undefined) {
          userService.retrieve()
            .then(function (user) {
              storageService.setSessionItem(storageConstant.USER, user);
              resolve($rootScope.user = user);
              socketService.subscribe();
            })
            .catch(function (error) {
              resolve();
            });
        } else {
          resolve($rootScope.user = user);

          socketService.subscribe();
        }
      });
    }];

  var clear = ['$q', '$rootScope', 'storageService', 'socketService', 'storageConstant',
    function ($q, $rootScope, storageService, socketService, storageConstant) {
      return $q(function (resolve, reject) {
        storageService.removeSessionItem(storageConstant.USER);

        delete $rootScope.user;

        socketService.unsubscribe();

        resolve();
      });
    }];

  // ======= router configuration =============

  $routeProvider
    .when('/main', {
      title: 'Main',
      templateUrl: 'html/partials/view/main.html',
      resolve: {
        user: user
      }
    })
    .when('/customer/search', {
      title: 'Customer Search',
      controller: 'CustomerController',
      templateUrl: 'html/partials/view/customer_search.html',
      resolve: {
        user: user
      }
    })
    .when('/login', {
      title: 'Login',
      templateUrl: 'html/partials/view/login.html',
      controller: 'LoginController',
      resolve: {
        clear: clear
      }
    })
    .when('/logout', {
      redirectTo: "/main",
      resolve: {
        clear: clear
      }
    })
    .otherwise({redirectTo: "/main"});

  // ======== http configuration ===============

  $httpProvider.interceptors.push(function ($q, $location, messageService, storageService, storageConstant) {
    return {
      'request': function (request) {
        messageService.clearError();

        var authToken = storageService.getSessionItem(storageConstant.AUTH_TOKEN);

        if (authToken) {
          request.headers['X-AUTH-TOKEN'] = authToken;
        }

        return request;
      },
      'response': function (response) {
        return response;
      },
      'responseError': function (rejection) {
        switch (rejection.status) {
          case 400:
          case 401:
          case 403:
          case 500:
          {
            break;
          }
          default :
          {
            messageService.error("UNKNOWN_ERROR", "An error has occurred, please try again.");

            break;
          }
        }

        return $q.reject(rejection);
      }
    };
  });
}]);