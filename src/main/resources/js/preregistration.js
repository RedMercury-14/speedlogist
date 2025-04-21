const token = $("meta[name='_csrf']").attr("content");
const regCard = document.querySelector('#but1')
const intCard = document.querySelector('#but2')
const regBtn = document.querySelector('input[name="but1"]')
const intBtn = document.querySelector('input[name="but3"]')

regCard.addEventListener("mouseover", function(e) {
	this.classList.add('active')
	regBtn.classList.remove('hidden')
});
regCard.addEventListener("mouseout", function(e) {
	this.classList.remove('active')
	regBtn.classList.add('hidden')
});


intCard.addEventListener("mouseover", function(e) {
	this.classList.add('active')
	intBtn.classList.remove('hidden')
});
intCard.addEventListener("mouseout", function(e) {
	this.classList.remove('active')
	intBtn.classList.add('hidden')
});
