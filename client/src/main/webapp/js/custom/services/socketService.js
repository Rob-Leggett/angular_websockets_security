'use strict';

angular.module('app.services').service('socketService', ['$rootScope', '$stomp', 'storageService', 'storageConstant', 'propertiesConstant',
    function socketService($rootScope, $stomp, storageService, storageConstant, propertiesConstant) {
        var connection;
        var subscriptions = {};

        this.subscribe = function subscribe() {
            console.log("start subscribing...");
            var authToken = storageService.getSessionItem(storageConstant.AUTH_TOKEN);
            var headers = (authToken) ? {"X-AUTH-TOKEN": authToken} : {};

            connect(headers);

            connection.then(function (frame) {
                if (!(subscriptions.notifications)) {
                    subscriptions.notifications = $stomp.subscribe('/api/user/notifications', function (payload, headers, res) {
                        $rootScope.$apply(function () {
                            $rootScope.notificationCount = payload.length;
                        })
                    }, headers);
                }
            });
        };

        this.unsubscribe = function unsubscribe() {
            console.log("unsubscribing....");
            if (subscriptions.notifications) {
                subscriptions.notifications.unsubscribe();
            }

            if (connection) {
                $stomp.disconnect(function () {
                    delete $rootScope.notificationCount;
                });

                connection = null;
            }

            subscriptions = {};
        };

        function connect(headers) {
            if (!(connection)) {
                connection = $stomp.connect(propertiesConstant.WEBSOCKET_API_URL + '/stomp', headers);
            }
        }
    }]);