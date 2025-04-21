
var numContract = document.querySelector('#numContractFromServer').value;
document.querySelector('input[name=numContract]').value = numContract.split(' от ')[0];
document.querySelector('input[name=dateContract]').value = numContract.split(' от ')[1];