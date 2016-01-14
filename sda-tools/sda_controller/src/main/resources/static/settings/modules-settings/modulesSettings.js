(function () {

    angular.module("sdaController.settings")
            .controller('modulesController',function($scope,$http,$log){
                $scope.moduleSelected=false; 
                $scope.modules=[];
                $scope.sm={};
                
                $http.get('/modules').then(function(resp){
                    $log.debug(resp.data);
                    $scope.modules = resp.data;
                },function(error){
                    $scope.$emit('error','Failed to retrieve modules configurations');
                    $log.error("failed to call api data for sdaConfig");
                    $log.error(error);
                });
                
                $scope.selectModule=function(module){
                    $scope.sm=module;
                    var smFileKeys=[];
                    for(var key in module.confs)
                        smFileKeys.push({fileName:key,active:false});
                    $scope.smFilesList=smFileKeys;
                    $scope.moduleSelected=true;
                }
                
           
                /*
                $scope.save=function(){
                    $log.debug("Sending sdaConfs to update..");
                    $log.debug(JSON.stringify($scope.startupScriptData));
                    $http.put("/sdaConfig",$scope.startupScriptData)
                         .then(function(resp){
                             $scope.$emit('notification','Startup Script configurations update successfully');
                             $scope.startupScriptOldProps = angular.copy($scope.startupScriptProps);
                         },function(error){
                             $log.error(error);
                             $scope.$emit('error','Startup Script configurations update failed');
                         });
                };*/
                
            });
    
})();


