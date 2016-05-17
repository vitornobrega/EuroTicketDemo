(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .factory('MatchGroupSearch', MatchGroupSearch);

    MatchGroupSearch.$inject = ['$resource'];

    function MatchGroupSearch($resource) {
        var resourceUrl =  'api/_search/match-groups/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
