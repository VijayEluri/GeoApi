var sage = angular.module('sage', []);
var uploader;

/**-------------------------------------------------\
 * Data Bus                                         |
 *--------------------------------------------------|
 * Allows controllers and views to communicate.     |
 * The ajax response is propagated to the specified |
 * 'view' by emitting an event from the $rootScope. |
 * ------------------------------------------------*/
sage.factory("dataBus", function($rootScope) {
    var dataBus = {};
    dataBus.setBroadcast = function(handle, data) {
        this.handle = handle;
        this.data = data;
        this.broadcastItem();
        return this;
    }

    dataBus.broadcastItem = function() {
        $rootScope.$broadcast(this.handle);
    }
    return dataBus;
});

/**
 * The menu controller broadcasts a toggleView event containing
 * an index. The receiving controllers are preset with an id that
 * corresponds to its index. If the index matches the id then the
 * container for that controller will be visible.
 */
sage.controller('MenuController', function($scope, dataBus){
    $scope.active = 1;
    $scope.toggleView = function(index) {
        dataBus.setBroadcast("toggleView", index);
    }
});

sage.controller('JobAuthController', function($scope, $http) {
    $scope.visible = true;
    $scope.email = "";
    $scope.password = "";
    $scope.error = false;
    $scope.errorMessage = "";

});

sage.controller('JobUploadController', function($scope, $http, dataBus) {
    $scope.id = 1;
    $scope.visible = false;
    $scope.empty = true;
    $scope.processes = [];
    $scope.addProcess = function(process) {
        this.empty = false;
        this.processes.push(process);
    }

    $scope.$on("toggleView", function(){
        $scope.visible = ($scope.id == dataBus.data);
    });
});

sage.controller('JobStatusController', function($scope, $http, dataBus) {
    $scope.id = 2;
    $scope.visible = true;

    $scope.$on("toggleView", function(){
        $scope.visible = ($scope.id == dataBus.data);
    });

})

$(document).ready(function() {

    initVerticalMenu();

    if ($.trim($("#error").html())=="") {
        $("#error").hide();
    }

    uploader = new qq.FileUploader({
        action: contextPath + '/job/upload',
        element: document.getElementById('fileuploader'),
        allowedExtensions:['tsv','csv', 'txt'],
        multiple:false,
        template: '<ul class="qq-upload-list"></ul><div class="qq-uploader">' +
            '<div class="qq-upload-drop-area"><span>Drop file here to upload</span></div>' +
            '<div class="custom-button qq-upload-button"><span><span aria-hidden="true" style="color:teal;" data-icon="&#128228;"></span>Add a file</span></div>' +
            '</div>',
        onSubmit: doSubmit,
        onComplete: doComplete
    });

    function doSubmit(id, fileName) {
        return true;
    }

    function doComplete(id, fileName, uploadResponse) {
        var html="";
        if(uploadResponse.success == true && uploadResponse.process != null) {

            var scope = angular.element($("#upload-container")).scope();
            scope.$apply(function(){
                scope.addProcess(uploadResponse.process);
            });
        }
        else {
            //take care of bad headers or blank files here
            alert(uploadResponse.message);
        }
    }

    $('.custom-submit-button').click(function() {
        var message = validate();
        if(message == "" && canSubmit) {
            $('#form_submit').html("Saving...");

            canSubmit = false;

            $('#uploadForm').submit();
            return true;
        }
        else {
            if(!canSubmit) {
                message += "<br>Select a valid file";
            }
            $("#error").html(message);
            if(!$("#error").is(":visible")) {
                $("#error").slideToggle(500);
            }
            return false;
        }
    });
});