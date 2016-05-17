(function() {
    'use strict';
    angular
        .module('euroTicketDemoApp')
        .factory('MatchGroup', MatchGroup);

    MatchGroup.$inject = ['$resource'];

    function MatchGroup ($resource) {
        var resourceUrl =  'api/match-groups/:id';

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
