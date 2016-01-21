(function(){
    
    var sdaControllerApp = angular.module("sdaController",['sdaController.settings','sdaController.dashboard']);
    
    sdaControllerApp.config(function($urlRouterProvider){
        $urlRouterProvider.otherwise("/dashboard");
    });
    
    sdaControllerApp.controller('sdaAppController',function($scope,$rootScope,$log){
        $scope.alert={class: "",message:"",show:false};
        $scope.$on('notification',function(event,message){
            $log.debug("received notification!");
            $scope.alert.class="alert-info";
            $scope.alert.message=message;
            $scope.alert.show=true;
        });
        $scope.$on('error',function(event,message){
            $log.debug("received error!");
            $scope.alert.class="alert-danger";
            $scope.alert.message=message;
            $scope.alert.show=true;
        });
    }); 
    
})();