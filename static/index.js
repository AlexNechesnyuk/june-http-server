angular.module('app', []).controller('indexController', function ($scope, $http) {
    const contextPath = '';

    $scope.fillTable = function () {
        $http.get(contextPath + '/items')
            .then(function (response) {
                $scope.ProductsList = response.data;
            });
    };

    $scope.submitCreateNewProduct = function () {
        $http.post(contextPath + '/items', $scope.newProduct)
            .then(function (response) {
                $scope.fillTable();
            });
    };

    $scope.deleteProductById = function(productId) {
        $http({
            url: contextPath + '/items?id=' + productId,
            method: "DELETE"
        }).then(function (response) {
            $scope.fillTable();
        });
    }

    $scope.fillTable();
});