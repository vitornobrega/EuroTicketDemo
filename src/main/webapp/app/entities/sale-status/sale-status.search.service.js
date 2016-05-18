(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .factory('SaleStatusSearch', SaleStatusSearch);

    SaleStatusSearch.$inject = ['$resource'];

    function SaleStatusSearch($resource) {
        var resourceUrl =  'api/_search/sale-statuses/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
