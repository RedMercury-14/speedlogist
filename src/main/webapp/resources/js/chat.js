//для vue js
var messageApi = Vue.resource('/speedlogist/api/user{/id}')
Vue.component('message-row', {
	props: ['message'],
	template: '<div><i>({{message.idUser}})</i>{{message.login}}</div>'
});
Vue.component('messages-list', {
	props: ['messenges'],
	template: '<div><message-row v-for="message in messenges" :key="message.id" :message="message" /></div>',
	created: function() {
		messageApi.get().then(result =>
			result.json().then(data =>
				data.forEach(message => this.messenges.push(message))
			)
		)
	}
});


var app = new Vue({
	el: '#app',
	template: '<messages-list :messenges="messages" />',
	data: {
		messages: []
	}
})