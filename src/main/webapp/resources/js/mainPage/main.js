/* =======================================================================================
    Template : Cargoy
    Create :   1 August 2019
   ========================================================================================== */


/* ===============================================
    Function Call - Call Function Ones
   =============================================== */

jQuery(document).ready(function(){
	"use strict";
	
	// here all ready functions
	
	  loader();
	  scroll_top();
    services_carousel();

});

/* ===============================================
    1. Preloader - Themplate Preloader
   =============================================== */
function loader() {
   "use strict";
   setTimeout(function () {
     $('#loader-wrapper').fadeOut();
   }, 500);
};

/* ===============================================
    2. Scrolling Top - Button to scroll up
   =============================================== */
function scroll_top(){
    "use strict";
	var offset = 300,
		offset_opacity = 1200,
		scroll_top_duration = 700,
		$back_to_top = $('.cd-top');

	$(window).scroll(function(){
		( $(this).scrollTop() > offset ) ? $back_to_top.addClass('cd-is-visible') : $back_to_top.removeClass('cd-is-visible cd-fade-out');
		if( $(this).scrollTop() > offset_opacity ) { 
			$back_to_top.addClass('cd-fade-out');
		}
	});

	$back_to_top.on('click', function(event){
		event.preventDefault();
		$('body,html').animate({
			scrollTop: 0 ,
		 	}, scroll_top_duration
		);
	});

};

/* ===============================================
    4. COUNTER
   =============================================== */
$('.counter').each(function() {
  var $this = $(this),
      countTo = $this.attr('data-count');
  
  $({ countNum: $this.text()}).animate({
    countNum: countTo
  },

  {

    duration: 8000,
    easing:'linear',
    step: function() {
      $this.text(Math.floor(this.countNum));
    },
    complete: function() {
      $this.text(this.countNum);
      //alert('finished');
    }

  });  
});

/* ===============================================
    11. SERVICES CAROUSEL
   =============================================== */
function services_carousel(){ 
    $('.services-carousel').slick({
        slidesToShow: 3,
        slidesToScroll: 1,
        autoplay: true,
        autoplaySpeed: 3000,
        arrows: false,
        dots: false,
        pauseOnHover: false,
        responsive: [{
            breakpoint: 992,
            settings: {
                slidesToShow: 2
            }
        }, {
            breakpoint: 576,
            settings: {
                slidesToShow:1
            }
        }]
    });
};
