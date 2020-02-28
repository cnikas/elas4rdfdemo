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

$(document).on('load',init());