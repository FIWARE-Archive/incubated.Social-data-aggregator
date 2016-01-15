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
                };
                
                $scope.toggleModuleEnabled=function(module){
                    module.enabled=!module.enabled;
                    $http.patch('/modules/'+module.id,{fieldName:"enabled",value:module.enabled})
                            .then(null,function(error){
                                $log.error("error on updating module "+ module.label +" enable state");
                                $log.error(error);
                                $scope.$emit('error','module '+module.label+' enable status update failed.'
                                                +'Please check in your global configurations if sda home is setted properly');
                            });
                };
                
           
                $scope.save=function(){
                    var module = $scope.sm;
                    $log.debug("Sending module to update with id "+module.id);
                    $log.debug(JSON.stringify(module));
                    $http.put("/modules/"+module.id,module)
                         .then(function(resp){
                             $scope.$emit('notification','module '+module.label+' configurations update successfully');
                         },function(error){
                             $log.error(error);
                             $scope.$emit('error','module '+module.label+' configurations update failed.'
                                                +'Please check in your global configurations if sda home is setted properly');
                         });
                };
                
            });
    
})();


