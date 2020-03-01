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
    $.get( "/loadimage", { id: idString} )
      .done(function( data ) {
        if(data != ""){
            element.prepend('<img src="'+data+'">');
        }
      });
});