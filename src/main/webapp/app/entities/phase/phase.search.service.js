(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .factory('PhaseSearch', PhaseSearch);

    PhaseSearch.$inject = ['$resource'];

    function PhaseSearch($resource) {
        var resourceUrl =  'api/_search/phases/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
