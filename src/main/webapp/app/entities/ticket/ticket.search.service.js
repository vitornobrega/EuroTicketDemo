(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .factory('TicketSearch', TicketSearch);

    TicketSearch.$inject = ['$resource'];

    function TicketSearch($resource) {
        var resourceUrl =  'api/_search/tickets/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
