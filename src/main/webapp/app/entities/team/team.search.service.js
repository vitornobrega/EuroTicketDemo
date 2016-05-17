(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .factory('TeamSearch', TeamSearch);

    TeamSearch.$inject = ['$resource'];

    function TeamSearch($resource) {
        var resourceUrl =  'api/_search/teams/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
