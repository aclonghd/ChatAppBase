const homePage = document.getElementById("homePage");
const loading = document.getElementById("loading");
const chatPage = document.getElementById("chatPage");
const chatArea = document.getElementById("chatArea");
const input = document.getElementById("input");
var msgArray = new Array();
var data = '';


function callFunction(){
    homePage.classList.add("d-none");
    loading.classList.remove("d-none");
    myJavaMember.connectToServer();
}

function sendMsg(event) {
    event.preventDefault();
    if (input.value != '') {
        
        var msg = {
            type: 'myMsg',
            msg: input.value,
        }
        msgArray.push(msg);
        append();
        chattingProcess.sendMessage(input.value);
        input.value = '';
        
    }  
}

function append() {
    for (let i = 0; i < msgArray.length; i++){
        if (msgArray[i].type == 'myMsg') {
            data +=
                '<div class="test w-100 d-flex justify-content-end my-2">\n'
                + '<div class="my message">\n'
                + msgArray[i].msg
                + '</div>\n'
                + '</div>\n'
        } else if(msgArray[i].type == 'yourMsg') {
            data +=
                '<div class="test w-100 d-flex my-2">\n'
                + '<div class="your message">\n'
                + msgArray[i].msg
                + '</div>\n'
                + '</div>\n'
        } else {
            data +=
                '<div class="w-100 my-2 border-success border-bottom border-top text-center">\n'
                + '<div class="text-success text-uppercase fw-bold h4">'
                + msgArray[i].msg
                + '</div>\n'
                + '</div>\n'
        }
    }
    msgArray.splice(0, msgArray.length);
    chatArea.innerHTML = data;
}

function recevieMsg(message) {
    var msg = {
        type: 'yourMsg',
        msg: message,
    }
    msgArray.push(msg);
    append();
}
function alertMsg(message) {
    var msg = {
        type: 'alert',
        msg: message,
    }
    msgArray.push(msg);
    append();
}

function connectSuccess() {
    loading.classList.add("d-none");
    chatPage.classList.remove("d-none");
    alertMsg("Kết nối thành công");
}

function test() {
    var msg = {
        type: 'yourMsg',
        msg: 'DAY LA TEST',
    }
    msgArray.push(msg);
    append();
}

function exit() {
    chatPage.classList.add("d-none");
    homePage.classList.remove("d-none");
    clientSocket.closeSocket(); 
}

function error() {
    loading.classList.add("d-none");
    homePage.classList.remove("d-none");
}