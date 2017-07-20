(function($){
    $(function(){

        $('.button-collapse').sideNav();
        hljs.initHighlightingOnLoad();

        $('.collapsible').collapsible();
        $(".collapsible-body:has(.active)").siblings().click();

    }); // end of document ready
})(jQuery); // end of jQuery name space
