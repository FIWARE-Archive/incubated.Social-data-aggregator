(function () {

    angular.module("sdaController.settings")
            .controller('globalSettingsController',function($scope,$http,$log){
                $scope.sdaHome={};
        
                $http.get('/globalConfig').then(function(resp){
                    $log.debug(resp.data);
                    $scope.globalConfs = resp.data;
                    $scope.sdaHome = $scope.globalConfs.sdaHome;
                    $scope.oldValue=$scope.sdaHome.value+"";
                },function(error){
                    $log.error("failed to call api data for globalSettings");
                    $log.error(error);
                });
                
                $scope.reset=function(){
                    $scope.sdaHome.value=$scope.oldValue;
                };
                
                $scope.save=function(){
                    $log.debug("Sending globalConfs to update..");
                    $log.debug(JSON.stringify($scope.globalConfs));
                    $http.put("/globalConfig",$scope.globalConfs)
                         .then(function(resp){
                             $log.debug("entro in resp success");
                             $scope.$emit('notification','Global configurations update successfully');
                             $scope.oldValue=$scope.sdaHome.value+"";
                         },function(error){
                             $log.debug("entro in log error!");
                             $log.error(error);
                             $scope.$emit('error','Global configurations update failed');
                         });
                };
            });
    
})();


