var token = $("meta[name='_csrf']").attr("content");
document.querySelector('#but1').addEventListener("mousedown", () => {
	console.log("this is but 1");
	document.querySelector('.content').innerHTML = `<input type="submit" value="У меня есть номер договора" name="but1">
<br><br>
	<input type="submit" value="У меня нет номера договора" name="but2">`;
});


document.querySelector('#but2').addEventListener("mousedown", () => {
	console.log("this is but 2");
	document.querySelector('.content').innerHTML = `<input type="submit" value="Регистрация" name="but3">`;
});
