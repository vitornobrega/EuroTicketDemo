(function() {
    'use strict';
    angular
        .module('euroTicketDemoApp')
        .factory('Sale', Sale);

    Sale.$inject = ['$resource', 'DateUtils'];

    function Sale ($resource, DateUtils) {
        var resourceUrl =  'api/sales/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.saleDate = DateUtils.convertDateTimeFromServer(data.saleDate);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
