(function() {
    'use strict';
    angular
        .module('euroTicketDemoApp')
        .factory('Ticket', Ticket);

    Ticket.$inject = ['$resource', 'DateUtils'];

    function Ticket ($resource, DateUtils) {
        var resourceUrl =  'api/tickets/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.matchDate = DateUtils.convertDateTimeFromServer(data.matchDate);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
