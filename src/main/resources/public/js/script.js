var searchInput = document.querySelector('#searchInput');
var searchContainer = document.querySelector('#searchInputContainer');


searchInput.addEventListener('focus', (event) => {
   if (!searchContainer.classList.contains('search-shadow')) {
          searchContainer.classList.add('search-shadow');
    }
});

searchInput.addEventListener('blur', (event) => {
  if (searchContainer.classList.contains('search-shadow')) {
            searchContainer.classList.remove('search-shadow');
      }
});

$(".tab").on('click',function(){
   $(this).addClass('active');
});

$(".tab").on('click',function(){
   $(this).removeClass('active');
});


$(".entity-result").each(function(index){
    var idString = $(this).data("id");
    var element = $(this);
    $.get( "/elas4rdf/loadimage", { id: idString} )
      .done(function( data ) {
        if(data != ""){
            element.prepend('<img src="'+data+'">');
        }
      });
});

$(".schema-frequent-class").click(function(){
    var dataToSend={};
    dataToSend.uris = ($(this).data("uris"));
    dataToSend.query = $("#schemaTabContent").data("query");
    dataToSend.type = $(this).data("type");
    $.ajax({
      type: "POST",
      contentType: "application/json",
      url: "/elas4rdf/entitiesForSchema",
      data: JSON.stringify(dataToSend),
      success: function(data){
        $(".schema-right").html("");
        $(".schema-right").append(data);
      }
    });
});

$(".schema-frequent-property").click(function(){

    var predicate = $(this).data("predicate");
    var query = $("#schemaTabContent").data("query");

    $.get("/elas4rdf/triplesForSchema", { query: query, predicate: predicate } )
      .done(function( data ) {
        $(".schema-right").html("");
        $(".schema-right").append(data);
    });
});

$(".image-with-label").each(function(index){
    var idString = $(this).data("id");
    var element = $(this);
    $.get( "/elas4rdf/loadimage", { id: idString} )
      .done(function( data ) {
        if(data != ""){
            element.prepend('<img src="'+data+'">');
        }
      });
});

$(".big-col").each(function(index){
    var idString = $(this).data("id");
    var element = $(this);
    $.get( "/elas4rdf/loadimage", { id: idString} )
      .done(function( data ) {
        if(data != ""){
            element.prepend('<img src="'+data+'">');
        }
      });
});

$("#searchForm").submit(function(e){
    if($(this).find('input[name="query"]').val() == ''){
        e.preventDefault();
    }

});

$("#add10").click(function(){
    $("#graphSizeForm").find('input[name="size"]').val(parseInt($("#graphSizeForm").find('input[name="size"]').val()) + 10);
    $("#graphSizeForm").submit();
});

$("#remove10").click(function(){
    if(parseInt($("#graphSizeForm").find('input[name="size"]').val()) > 10){
        $("#graphSizeForm").find('input[name="size"]').val(parseInt($("#graphSizeForm").find('input[name="size"]').val()) - 10);
        $("#graphSizeForm").submit();
    }
});

function matchAndHighlight(searchTerm) {
    if (searchTerm) {
        //var wholeWordOnly = new RegExp("\\g"+searchTerm+"\\g","ig"); //matches whole word only
        //var anyCharacter = new RegExp("\\g["+searchTerm+"]\\g","ig"); //matches any word with any of search chars characters
        var selector = ".title>a, .description, .title>span";
        var searchTermRegEx = new RegExp('('+searchTerm+')', "ig");
        var matches = $(selector).text().match(searchTermRegEx);
        if (matches != null && matches.length > 0) {

            if (searchTerm === "&") {
                searchTerm = "&amp;";
                searchTermRegEx = new RegExp(searchTerm, "ig");
            }

            $(selector).each(function(i){
                $(this).html($(this).html().replace(searchTermRegEx, '<span class="highlighted">$1</span>'));
            });

            return true;
        }
    }
    return false;
}
function removeHighlight(){
    $('.highlighted').each(function(i){
        $(this).replaceWith($(this).html());
    });
}

function highlight(){
    strings = $("#searchInput").val().split(" ");
    strings.forEach(s => matchAndHighlight(s));
}
$(".settings-icon").click(function(){
    $('#settingsBox').toggle();
});


$('#highlightCheck').change(function() {

    if (this.checked) {
        highlight();
    } else {
        removeHighlight();
    }
});

$('#predicateLabelsCheck').change(function() {
    $("#infovis").empty();
    if (this.checked) {
        init('labeled');
    } else {
        init('line');
    }
    $("#infovisSchema").empty();
        if (this.checked) {
            init('labeled');
        } else {
            init('line');
        }
});
