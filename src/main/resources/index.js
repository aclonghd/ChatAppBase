const homePage = document.getElementById("homePage");
const loading = document.getElementById("loading");
const chatPage = document.getElementById("chatPage");
const chatArea = document.getElementById("chatArea");
const input = document.getElementById("input");
const addon = document.getElementById("addon");
const uploadInput = document.getElementById("uploadInput");
var msgArray = new Array();
var data = '';
var attachmentArray = new Array();

function callFunction(){
    homePage.classList.add("d-none");
    loading.classList.remove("d-none");
    myJavaMember.connectToServer();
}

function callSearch() {
    clientSocket.search();
}

function sendMsg(event) {
    event.preventDefault();
    if (input.value != '') {
        
        let msg = {
            type: 'myMsg',
            msg: input.value,
        }
        
        msgArray.push(msg);
        if (uploadInput.files.length > 0) {
            for (let i = 0; i < uploadInput.files.length; i++){
                let msg = {
                    type: 'myMsg attachment',
                    msg: uploadInput.files[i].name,
                    size: updateSizeFile(uploadInput.files[i].size),
                }
                msgArray.push(msg);
                sendDataFile(uploadInput.files[i]);
            }
        }
        removeAttchement();
        document.getElementById("uploadInput").value = "";
        append();
        clientSocket.sendMessage(input.value);

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
        } else if(msgArray[i].type == 'myMsg attachment') {
            data += `<div class="test w-100 d-flex justify-content-end my-2">
                        <div class="attachmentMsg my message" id="${msgArray[i].msg}" onclick="downloadFiles(this)">
                            <div class="d-flex flex-column justify-content-center align-items-center">
                                <div class="attIcon">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="25" height="25" fill="currentColor" class="bi bi-file-earmark-text"
                                        viewBox="0 0 16 16">
                                        <path d="M5.5 7a.5.5 0 0 0 0 1h5a.5.5 0 0 0 0-1h-5zM5 9.5a.5.5 0 0 1 .5-.5h5a.5.5 0 0 1 0 1h-5a.5.5 0 0 1-.5-.5zm0 2a.5.5 0 0 1 .5-.5h2a.5.5 0 0 1 0 1h-2a.5.5 0 0 1-.5-.5z" />
                                        <path d="M9.5 0H4a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V4.5L9.5 0zm0 1v2A1.5 1.5 0 0 0 11 4.5h2V14a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1h5.5z" />
                                    </svg>
                                </div>
                                <label for="" class="fileSize">${msgArray[i].size}</label>
                            </div>
                            <div class="d-flex align-content-end w-100 ms-2">
                                <a>${msgArray[i].msg}</a>
                            </div>
                        </div>
                    </div>`
        } else if(msgArray[i].type == 'yourMsg attachment') {
            data += `<div class="test w-100 d-flex my-2">
                        <div class="attachmentMsg your message" id="${msgArray[i].msg}" onclick="downloadFiles(this)">
                            <div class="d-flex flex-column justify-content-center align-items-center">
                                <div class="attIcon">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="25" height="25" fill="currentColor" class="bi bi-file-earmark-text"
                                        viewBox="0 0 16 16">
                                        <path d="M5.5 7a.5.5 0 0 0 0 1h5a.5.5 0 0 0 0-1h-5zM5 9.5a.5.5 0 0 1 .5-.5h5a.5.5 0 0 1 0 1h-5a.5.5 0 0 1-.5-.5zm0 2a.5.5 0 0 1 .5-.5h2a.5.5 0 0 1 0 1h-2a.5.5 0 0 1-.5-.5z" />
                                        <path d="M9.5 0H4a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V4.5L9.5 0zm0 1v2A1.5 1.5 0 0 0 11 4.5h2V14a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1h5.5z" />
                                    </svg>
                                </div>
                                <label for="" class="fileSize">${msgArray[i].size}</label>
                            </div>
                            <div class="d-flex align-content-end w-100 ms-2">
                                <a>${msgArray[i].msg}</a>
                            </div>
                        </div>
                    </div>`
        } else {
            data +=
                '<div class="messageAlert test">\n'
                + '<div class= "underline"></div>\n'
                + '<div class="fw-bold fs-6 text-nowrap mx-2 text-capitalize">'
                + msgArray[i].msg
                + '</div>\n'
                + '<div class= "underline"></div>\n'
                + '</div>\n'
        }
    }
    msgArray.splice(0, msgArray.length);
    chatArea.innerHTML = data;
    chatArea.scrollTop = chatArea.scrollHeight - chatArea.clientHeight;
}

function displayMsg(message) {
    var msg = {
        type: 'yourMsg',
        msg: message,
    }
    msgArray.push(msg);
    append();
}
function alertMsg(message) {
    if (chatPage.classList.contains("d-none")) {
        loading.classList.add("d-none");
        chatPage.classList.remove("d-none");
    }
    var msg = {
        type: 'alert',
        msg: message,
    }
    msgArray.push(msg);
    append();
}

function displayAttaMsg(filename, fileSize) {
    let msg = {
        type: 'yourMsg attachment',
        msg: filename,
        size: updateSizeFile(fileSize),
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
    data = '';
}

function error() {
    loading.classList.add("d-none");
    homePage.classList.remove("d-none");
}

function displayLoading(loadingMsg) {
    document.getElementById("loadingMsg").innerHTML = loadingMsg;
    loading.classList.remove("d-none");
    chatPage.classList.add("d-none");
}

function addonToggle(){
    if (addon.classList.contains("active")) {
        addon.classList.remove("active");
        document.getElementById("addonBtn").classList.remove("active");
    }
    else {
        addon.classList.add("active");
        document.getElementById("addonBtn").classList.add("active");
    }
}

function updateSizeFile(fileSize) {
    let nBytes = fileSize;
    let sOutput = nBytes + "bytes";
    // optional code for multiples approximation
    const aMultiples = ["Kb", "Mb", "Gb", "Tb"];
    for (nMultiple = 0, nApprox = nBytes / 1024; nApprox >= 1; nApprox /= 1024, nMultiple++) {
      sOutput = nApprox.toFixed(2) + "" + aMultiples[nMultiple];
    }
    return sOutput;
  }
  
function sendDataFile(file) {
    fileData = new Blob([file]);
    var promise = new Promise(function getBuffer(resolve) {
        var reader = new FileReader();
        reader.readAsArrayBuffer(fileData);
        reader.onload = function() {
            var arrayBuffer = reader.result
            var bytes = new Uint8Array(arrayBuffer);
            resolve(bytes);
        }
    });
    promise.then(function (bytes) {
        clientSocket.sendFile(new FileOBject(file.name, file.size, bytes.toString(), file.type));
        // document.getElementById(`${file.name}`).onclick = () => {
        //     myJavaMember.copyFile(new FileOBject(file.name, file.size, bytes.toString(), file.type));
        // };
        
    }).catch(function(err) {
      console.log('Error: ',err);
    });   
}

function downloadFiles(div) {
    var filename = div.getAttribute("id");
    console.log(filename)
    clientSocket.downloadFile(filename);
}

class FileOBject{
    constructor(filename, fileSize, dataBytes, fileType) {
        this.filename = filename;
        this.fileSize = fileSize;
        this.dataBytes = dataBytes;
        this.fileType = fileType;
    }
    
}

uploadInput.onchange = () => {
    removeAttchement();
    addonToggle();
    var files = uploadInput.files;
    var inputBar = document.getElementById("inputBar");
    var html = '';
    for (let i = 0; i < files.length; i++){
        html += '<div class="attachmnt">' +
                    '<label for="" class="me-3">'+ files[i].name +'</label>' +
                    '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-x-circle" onclick="closeAttachment(this)"' +
                        'viewBox="0 0 16 16">' +
                        '<path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z" />' +
                        '<path d="M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708z" />' +
                    '</svg>' +
                '</div>'
    }
    
    
    inputBar.insertAdjacentHTML("afterbegin", html)
}

function closeAttachment(div) {
    var inputBar = document.getElementById("inputBar");
    inputBar.removeChild(div.parentNode);
}

function removeAttchement() {
    var inputBar = document.getElementById("inputBar");
    var ele = document.getElementsByClassName("attachmnt");
    while (ele.length > 0) {
        inputBar.removeChild(ele[0]);
    }
    
}