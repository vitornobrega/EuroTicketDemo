(function() {
    'use strict';
    angular
        .module('euroTicketDemoApp')
        .factory('SaleStatus', SaleStatus);

    SaleStatus.$inject = ['$resource'];

    function SaleStatus ($resource) {
        var resourceUrl =  'api/sale-statuses/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
