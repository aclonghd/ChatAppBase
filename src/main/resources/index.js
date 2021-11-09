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
var hasARecord = false;
var fileList = new Array();

// Cac bien o trong Java gom co myJavaMember, clientSocket. Trong do myJavaMember la class Client, clientSocket la class ClientSocketHandler.

function callFunction(){
    homePage.classList.add("d-none");
    loading.classList.remove("d-none");
    myJavaMember.connectToServer();
}

function callSearch() {
    clientSocket.search();
}

/*  
Cach de hien thi tin nhan hay giu lieu gui di/nhan:
    Dai khai de hien thi doan tin nhan gui di hoac nhan duoc thi tao xe cho mot tag div co id="chatArea" 
    hien thi thong qua thuoc tinh innerHTML. Cach thuc hien thi:
        + Nhung tat ca du lieu vao cac the div da css san roi chuyen sang chuoi va duoc luu vao bien data
        (bien data da khoi tao tu dau, data la mot chuoi)
        + div co id="chatArea" se in ra chuoi do thong qua thuoc tinh innerHTML = data.
    Tom lai de hien thi du lieu thi ta cho vao cac the div da css san, roi chuyen no thanh cac the div thanh chuoi
    roi noi chuoi vao bien data bang toan tu +=. Cuoi cung thi dung thuoc tinh innerHTML de hien thi data.
*/

/* 
    Ham ben duoi thuc thi gui doan tin nhan di
    + Tao mot bien co thuoc tinh la type, msg, .... Them thuoc tinh gi vao thi tuy nhung phai co thuoc tinh type de ham append() xu ly.
    + Bien day duoc dung o duoi la msg. sau khi tao bien xong thi cho no vao mang msgArray roi goi ham append() de xu ly hien thi doan tin nhan vua gui.
    + Tat nhien la sau khi gui di thi ta se clear cac gia tri co trong input hay trong uploadInput.
*/
function sendMsg(event) {
    event.preventDefault();
    if (hasARecord == true) { // Truong hop co doan ghi am
        if (flag == 1) {
            myJavaMember.stopPlayRecord();
            document.getElementById("audio").classList.remove("active");
            clearInterval(interval);
        }
        let name = uuidv4();
        let msg = {
            type: 'myMsg audio',
            msg: name,
            time: time,
        }
        msgArray.push(msg);
        // cac dong ben duoi giong voi ham thuc thi tat ghi am va an di thanh ghi am
        hasARecord = false;
        document.getElementById("audio").classList.remove("active");
        clearInterval(intervalTimeline);
        clearTimeout(timeOut);
        loadPercent = -100;
        timeline.removeAttribute("style");
        timeline.classList.add("d-none");
        if (!playIcon.classList.contains("d-none")) playIcon.classList.add("d-none");
        if(!pauseIcon.classList.contains("d-none")) pauseIcon.classList.add("d-none");
        if (stopIcon.classList.contains("d-none")) stopIcon.classList.remove("d-none");
        timer.innerHTML = "0:00";
        // gui du lieu den cho server
        clientSocket.sendAudio(name, myJavaMember.getBufferRecord());
        append();

        processMap[name] = -100;
        durationMap[name] = time;
        arrIntervalTmeline[name] = 0;
        arrIntervals[name] = 0;

    }
    if (input.value != '') { // Truong hop co doan tin nhan
        
        let msg = {
            type: 'myMsg',
            msg: input.value,
        }
        
        msgArray.push(msg);
        append();
        clientSocket.sendMessage(input.value);
        // clear tin nhan trong phan input
        input.value = '';
    }  
    if (fileList.length > 0) { // Truong hop co file upload
        for (let i = 0; i < fileList.length; i++){
            let fileType = getFileType(fileList[i].name);
            let msg = {
                type: null,
                msg: null,
                size: null,
            };
            if (fileType == (".jpg" || ".png" || ".jpeg" || ".bmp")) {
                msg = {
                    type: 'myMsg image',
                    msg: fileList[i].name,
                    src: URL.createObjectURL(fileList[i]),
                }
            }
            else {
                msg = {
                    type: 'myMsg attachment',
                    msg: fileList[i].name,
                    size: getSizeFile(fileList[i].size),
                }
            }
            msgArray.push(msg);
            sendDataFile(fileList[i]);
        }
        // clear du lieu hien thi file dinh kem trong inputBar
        fileList.splice(0, fileList.length);
        removeAttchement();
        
        append();
    }
}
/*
    Ham ben duoi co tac dung de phan loai cac dang tin nhan gui di: tin nhan thuong (mot chuoi String), 
    file dinh kem, doan ghi am, va anh. Ham chi lay cac du lieu trong mang msgArray hien thi duoc duoi dang String nhu la:
    tin nhan, ten file, dung luong file,... , roi cong chuoi vao chuoi data. Sau khi cong xong thi se xoa mang msgArray
    de tranh tinh trang lap lai doan du lieu. Cuoi cung thi innerHTML = data thoi
*/
function append() {
    for (let i = 0; i < msgArray.length; i++){
        if (msgArray[i].type == 'myMsg') { /*TH1: Doan tin nhan thuong do ben client gui di*/
            data +=
                '<div class="test w-100 d-flex justify-content-end my-2">\n'
                + '<div class="my message">\n'
                + msgArray[i].msg
                + '</div>\n'
                + '</div>\n'
        } else if(msgArray[i].type == 'yourMsg') { /*TH2: Doan tin nhan thuong do ben client nhan duoc*/ 
            data +=
                '<div class="test w-100 d-flex my-2">\n'
                + '<div class="your message">\n'
                + msgArray[i].msg
                + '</div>\n'
                + '</div>\n'
        } else if(msgArray[i].type == 'myMsg attachment') {  /*TH3: Doan tin nhan la file dinh kem do client gui di*/
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
                            <div class="d-flex align-content-end w-100 ms-2" style="word-wrap: break-word; word-break: break-all;">
                                <span>${msgArray[i].msg}</span>
                            </div>
                        </div>
                    </div>`
        } else if(msgArray[i].type == 'yourMsg attachment') { /*TH4: Doan tin nhan la file dinh kem do client nhan dc*/
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
                            <div class="d-flex align-content-end w-100 ms-2" style="word-wrap: break-word; word-break: break-all;">
                                <span>${msgArray[i].msg}</span>
                            </div>
                        </div>
                    </div>`
        } else if (msgArray[i].type =='myMsg audio') { /*TH5: Doan tin nhan la audio ghi am do client gui di*/ 
            data += `<div class="test w-100 d-flex justify-content-end my-2">
                <div class="my message audioMsg" id="${msgArray[i].msg}">
                    <div class="playButton" onclick="audioFunction(this)">
                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor"
                            class="bi bi-play-fill" viewBox="0 0 16 16">
                            <path
                                d="m11.596 8.697-6.363 3.692c-.54.313-1.233-.066-1.233-.697V4.308c0-.63.692-1.01 1.233-.696l6.363 3.692a.802.802 0 0 1 0 1.393z" />
                        </svg>
                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor"
                            class="bi bi-pause-fill d-none" viewBox="0 0 16 16">
                            <path
                                d="M5.5 3.5A1.5 1.5 0 0 1 7 5v6a1.5 1.5 0 0 1-3 0V5a1.5 1.5 0 0 1 1.5-1.5zm5 0A1.5 1.5 0 0 1 12 5v6a1.5 1.5 0 0 1-3 0V5a1.5 1.5 0 0 1 1.5-1.5z" />
                        </svg>
                    </div>
                    <div class="ghost"></div>
                    <div class="timeline d-none"></div>
                    <div class="timeCoundown">${msgArray[i].time.toString().toHHMMSS()}</div>
                </div>
            </div>`
        } else if (msgArray[i].type =='yourMsg audio') { /*TH6: Doan tin nhan la audio ghi am do client nhan dc*/
            data += `<div class="test w-100 d-flex my-2">
                <div class="your message audioMsg" id="${msgArray[i].msg}">
                    <div class="playButton" onclick="audioFunction(this)">
                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor"
                            class="bi bi-play-fill" viewBox="0 0 16 16">
                            <path
                                d="m11.596 8.697-6.363 3.692c-.54.313-1.233-.066-1.233-.697V4.308c0-.63.692-1.01 1.233-.696l6.363 3.692a.802.802 0 0 1 0 1.393z" />
                        </svg>
                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor"
                            class="bi bi-pause-fill d-none" viewBox="0 0 16 16">
                            <path
                                d="M5.5 3.5A1.5 1.5 0 0 1 7 5v6a1.5 1.5 0 0 1-3 0V5a1.5 1.5 0 0 1 1.5-1.5zm5 0A1.5 1.5 0 0 1 12 5v6a1.5 1.5 0 0 1-3 0V5a1.5 1.5 0 0 1 1.5-1.5z" />
                        </svg>
                    </div>
                    <div class="ghost"></div>
                    <div class="timeline d-none"></div>
                    <div class="timeCoundown">${msgArray[i].time.toString().toHHMMSS()}</div>
                </div>
            </div>`
        } else if (msgArray[i].type =='myMsg image') { /*TH7: Doan tin nhan la hinh anh do client gui di*/
            data += `<div class="test w-100 d-flex justify-content-end my-2">
                <div class="my message imageMsg" id="${msgArray[i].msg}">
                    <div class="imageContainer">
                        <img src="${msgArray[i].src}">
                    </div>
                </div>
            </div>`;
        } else if (msgArray[i].type =='yourMsg image') { /*TH8: Doan tin nhan la hinh anh do client nhan dc*/
            data += `<div class="test w-100 d-flex my-2">
                <div class="your message imageMsg" id="${msgArray[i].msg}">
                    <div class="imageContainer">
                        <img src="${msgArray[i].src}">
                    </div>
                </div>
            </div>`;
        } else { /*TH con lai: Thong bao do server gui den*/ 
            data +=
                '<div class="messageAlert test">\n'
                + '<div class= "underline"></div>\n'
                + '<div class="fw-bold fs-6 text-nowrap mx-2 text-capitalize">'
                + msgArray[i].msg
                + '</div>\n'
                + '<div class= "underline"></div>\n'
                + '</div>\n'
        }
        /* Con nhieu truong hop nua nhu la hinh anh, emoji. */
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
        size: getSizeFile(fileSize),
    }
    msgArray.push(msg);
    append();
}

function displayAudioMsg(filename, fileSize) {
    let msg = {
        type: 'yourMsg audio',
        msg: filename,
        time: fileSize,
    }
    msgArray.push(msg);
    append();
    processMap[filename] = -100;
    durationMap[filename] = fileSize;
    arrIntervalTmeline[filename] = 0;
    arrIntervals[filename] = 0;

}

function displayImageMsg(filename) {
    let dataBytes = new Uint8Array(clientSocket.getCacheDataImg());
    let src = URL.createObjectURL(new Blob([dataBytes], {type: "image/jpeg"}));
    let msg = {
        type: 'yourMsg image',
        msg: filename,
        src: src,
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

function getSizeFile(fileSize) {
    let nBytes = fileSize;
    let sOutput = nBytes + "Bytes";
    // optional code for multiples approximation
    const aMultiples = ["Kb", "Mb", "Gb", "Tb"];
    let nApprox, nMultiple;
    for (nMultiple = 0, nApprox = nBytes / 1024; nApprox >= 1; nApprox /= 1024, nMultiple++) {
        sOutput = nApprox.toFixed(2) + "" + aMultiples[nMultiple];
    }
    if ((nApprox*1024).toFixed(2) > 25 && nMultiple == 2 || nMultiple > 2) return -1;
    else return sOutput;
  }
  
function sendDataFile(file) {
    fileData = new Blob([file]);
    console.log(file);
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
    var files = uploadInput.files;
    fileList = Array.from(files);
    removeAttchement();
    addonToggle();
    uploadInput.removeAttribute("accept");
    console.log(fileList);
    for (let i = 0; i < fileList.length; i++){
        if (getSizeFile(fileList[i].size) == -1) {
            var options = {
                keyboard: true,
                focus: false,
            }
            var myModal = new bootstrap.Modal(document.getElementById('myModal'), options);
            myModal.show();
            document.getElementById("uploadInput").value = "";
            fileList.splice(0, fileList.length);
            return;
        }
    }
    document.getElementById("uploadInput").value = "";
    var inputBar = document.getElementById("inputBar");
    toggleDisplayNone(inputBar);
    var html = '';
    for (let i = 0; i < fileList.length; i++){
        let filetype = getFileType(fileList[i].name);
        if (filetype == (".jpg" || ".png" || ".jpeg" || ".bmp")) {
            let src = URL.createObjectURL(fileList[i]);
            html += `<div class="previewContainer">
                            <div class="removeAttIcon" onclick="closeAttachment(this, '${fileList[i].name}')">
                                <div class="con">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-x-circle"
                                        viewBox="0 0 16 16">
                                        <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z" />
                                        <path
                                            d="M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708z" />
                                    </svg>
                                </div>
                            </div>
                            <img src="${src}">
                        </div>`
        }
        else {
            html += `<div class="attachment">
                            <div class="removeAttIcon" onclick="closeAttachment(this, '${fileList[i].name}')">
                                <div class="con">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-x-circle"
                                        viewBox="0 0 16 16">
                                        <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z" />
                                        <path
                                            d="M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708z" />
                                    </svg>
                                </div>
                            </div>
                            <div class="d-flex flex-column justify-content-center align-items-center">
                                <div class="attIcon">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="25" height="25" fill="currentColor"
                                        class="bi bi-file-earmark-text" viewBox="0 0 16 16">
                                        <path
                                            d="M5.5 7a.5.5 0 0 0 0 1h5a.5.5 0 0 0 0-1h-5zM5 9.5a.5.5 0 0 1 .5-.5h5a.5.5 0 0 1 0 1h-5a.5.5 0 0 1-.5-.5zm0 2a.5.5 0 0 1 .5-.5h2a.5.5 0 0 1 0 1h-2a.5.5 0 0 1-.5-.5z" />
                                        <path
                                            d="M9.5 0H4a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V4.5L9.5 0zm0 1v2A1.5 1.5 0 0 0 11 4.5h2V14a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1h5.5z" />
                                    </svg>
                                </div>
                            </div>
                            <div class="d-flex align-content-end w-100 ms-2 fw-bold">
                                <span>${fileList[i].name}</span>
                            </div>
                        </div>`
        }
    }
    inputBar.insertAdjacentHTML("afterbegin", html)
}
function closeAttachment(div, filename) {
    var inputBar = document.getElementById("inputBar");
    for (let i = 0; i < fileList.length; i++){
        if (fileList[i].name == filename) fileList.splice(i, 1);
    }
    if (fileList.length == 0) {
        toggleDisplayNone(inputBar);
        toggleDisplayNone(document.getElementById("addonBtn"));
    }
    inputBar.removeChild(div.parentNode);
}

function removeAttchement() {
    var inputBar = document.getElementById("inputBar");
    var ele = document.getElementsByClassName("attachment");
    var ele2 = document.getElementsByClassName("previewContainer");
    while (ele.length > 0) {
        inputBar.removeChild(ele[0]);
    }
    while (ele2.length > 0) {
        inputBar.removeChild(ele2[0]);
    }
    if (fileList.length == 0) {
        toggleDisplayNone(inputBar);
        
    }
    toggleDisplayNone(document.getElementById("addonBtn"));
}

function getFileType(filename) {
    let i;
    for (i = filename.length - 1; i >= 0; i--){
        if (filename[i] == '.') break;
    }
    return (filename.slice(i, filename.length)).toLowerCase();
}
// Code ben duoi la phan ghi am va code script cho cac chuc nang cua audio nhu la phat/tam ngung
var audio = document.querySelector('audio'),
    startBtnRecord = document.getElementById("recordBtn"),
    ctrMediaBtn = document.getElementById("ctrMediaBtn"),
    stopIcon = document.getElementById("stopIcon"),
    playIcon = document.getElementById("playIcon"),
    pauseIcon = document.getElementById("pauseIcon"),
    closeAudioBtn = document.getElementById("closeAudioBtn"),
    timeline = document.getElementById("timeline"),
    timer = document.getElementById("timer");
var flag, interval, time, intervalTimeline, timeOut, loadPercent = -100;

// Ham thuc thi ghi am va hien thi thanh audio ghi am
startBtnRecord.addEventListener('click', () => {
    document.getElementById("audio").classList.add("active");
    addonToggle();
    myJavaMember.startRecording();
    hasARecord = true;
    flag = 1;
    time = 0;
    interval = setInterval(() => {
        timer.innerHTML = (time + 1).toString().toHHMMSS();
        time++;
    }, 1000)
});

// Ham thuc hien chuc nang cua thanh audio ghi am
// Dung ghi am va phat lai doan vua ghi am
ctrMediaBtn.addEventListener('click', () => {
    switch (flag) {
        case 1: {
            toggleDisplayNone(stopIcon);
            toggleDisplayNone(playIcon);
            clearInterval(interval);
            flag = 2;

            myJavaMember.stopRecording();
            
            break;
        }
        case 2: {
            toggleDisplayNone(playIcon);
            toggleDisplayNone(pauseIcon);
            
            if (playIcon.classList.contains("d-none")) {
                myJavaMember.playRecord();
                if (timeline.classList.contains("d-none")) timeline.classList.remove("d-none");
                intervalTimeline = setInterval(() => {
                    
                    timeline.style.transform = `translateX(${loadPercent}%)`;
                    if (loadPercent == 0) {
                        clearInterval(intervalTimeline);
                        timeOut = setTimeout(() => {
                            timeline.removeAttribute("style");
                            timeline.classList.add("d-none");
                            toggleDisplayNone(playIcon);
                            toggleDisplayNone(pauseIcon);
                            loadPercent = -100;
                            flag = 2;
                        }, 500) 
                        
                    } 
                    loadPercent++;
                    
                }, time * 10);
            } else {
                myJavaMember.pauseRecord();
            }
            flag = 3;
            break;
        } case 3: {
            toggleDisplayNone(playIcon);
            toggleDisplayNone(pauseIcon);
            clearInterval(intervalTimeline);
            clearTimeout(timeOut);
            if (playIcon.classList.contains("d-none")) {
                myJavaMember.resumeRecord();
                if (timeline.classList.contains("d-none")) timeline.classList.remove("d-none");
                intervalTimeline = setInterval(() => {
                    timeline.style.transform = `translateX(${loadPercent}%)`;
                    if (loadPercent == 0) {
                        clearInterval(intervalTimeline);
                        timeOut = setTimeout(() => {
                            timeline.removeAttribute("style");
                            timeline.classList.add("d-none");
                            toggleDisplayNone(playIcon);
                            toggleDisplayNone(pauseIcon);
                            loadPercent = -100;
                            flag = 2;
                        }, 500) 
                        
                    } 
                    loadPercent++;
                    
                }, time * 10);
            } else {
                myJavaMember.pauseRecord();
            }
            flag = 3;
            break;
        }
    }
})

// Ham thuc thi tat ghi am khi bam vao dau (X) va an di thanh ghi am
closeAudioBtn.addEventListener('click', () => {
    myJavaMember.stopPlayRecord();
    hasARecord = false;
    document.getElementById("audio").classList.remove("active");
    clearInterval(interval);
    clearInterval(intervalTimeline);
    clearTimeout(timeOut);
    loadPercent = -100;
    timeline.removeAttribute("style");
    timeline.classList.add("d-none");
    if (!playIcon.classList.contains("d-none")) playIcon.classList.add("d-none");
    if(!pauseIcon.classList.contains("d-none")) pauseIcon.classList.add("d-none");
    if (stopIcon.classList.contains("d-none")) stopIcon.classList.remove("d-none");
    timer.innerHTML = "0:00";
});

// Them prototype cho lop String
// De hien thi phut, giay cua audio
String.prototype.toHHMMSS = function () {
    var sec_num = parseInt(this, 10);
    var hours   = Math.floor(sec_num / 3600);
    var minutes = Math.floor((sec_num - (hours * 3600)) / 60);
    var seconds = sec_num - (hours * 3600) - (minutes * 60);

    if (hours >= 1) { hours = hours; minutes = "0" + minutes; return hours+ ':' + minutes+':'+seconds}
    if (minutes < 10) {minutes = minutes;}
    if (seconds < 10) {seconds = "0"+seconds;}
    return minutes+':'+seconds;
}

function toggleDisplayNone(element) {
    if (element.classList.contains("d-none")) element.classList.remove("d-none");
    else element.classList.add("d-none");
}

var processMap = {};
var durationMap = {};
var arrIntervalTmeline = {};
var arrIntervals = {};

// Ham thuc hien chuc nang cua Audio 
// Phat/tam ngung (play/pause) am thanh cua audio nhan duoc/gui di
function audioFunction(element) {
    let playIc = element.childNodes[1];
    let pauseIc = element.childNodes[3];
    let timeL = element.parentNode.childNodes[5];
    let timeC = element.parentNode.childNodes[7];
    let uuid = element.parentNode.getAttribute("id");
    let tme = clientSocket.getAudioDurationWithUUID(uuid);
    let intervalTmeline = arrIntervalTmeline[uuid], intervals = arrIntervals[uuid], lodPercent = processMap[uuid];
    toggleDisplayNone(playIc);
    toggleDisplayNone(pauseIc);
    if (lodPercent != -100 && playIc.classList.contains("d-none")) {
        clientSocket.resumeAudioWithUUID(uuid);
        if (timeL.classList.contains("d-none")) timeL.classList.remove("d-none");
        intervalTmeline = setInterval(() => {
            timeL.style.transform = `translateX(${lodPercent}%)`;
            if (lodPercent == 0) {
                clearInterval(intervalTmeline);
                tmeOut = setTimeout(() => {
                    timeL.removeAttribute("style");
                    timeL.classList.add("d-none");
                    // toggleDisplayNone(playIc);
                    // toggleDisplayNone(pauseIc);
                    playIc.classList.remove("d-none");
                    pauseIc.classList.add("d-none");
                    processMap[uuid] = -100;
                    timeC.innerHTML = (tme).toString().toHHMMSS();
                }, 500) 
                
            } 
            lodPercent++;
            processMap[uuid] = lodPercent;
        }, tme * 10);
        intervals = setInterval(() => {
            timeC.innerHTML = (durationMap[uuid] - 1).toString().toHHMMSS();
            durationMap[uuid]--;
            if (durationMap[uuid] <= 0) {
                durationMap[uuid] = tme;
                clearInterval(intervals);
            }
        }, 1000);
        arrIntervalTmeline[uuid] = intervalTmeline;
        arrIntervals[uuid] = intervals;
    }
    else if (playIc.classList.contains("d-none")) {
        clientSocket.playAudioWithUUID(uuid);
        if (timeL.classList.contains("d-none")) timeL.classList.remove("d-none");
        intervalTmeline = setInterval(() => {
            timeL.style.transform = `translateX(${lodPercent}%)`;
            if (lodPercent == 0) {
                clearInterval(intervalTmeline);
                tmeOut = setTimeout(() => {
                    timeL.removeAttribute("style");
                    timeL.classList.add("d-none");
                    // toggleDisplayNone(playIc);
                    // toggleDisplayNone(pauseIc);
                    playIc.classList.remove("d-none");
                    pauseIc.classList.add("d-none");
                    processMap[uuid] = -100;
                    timeC.innerHTML = (tme).toString().toHHMMSS();
                }, 500) 
                
            } 
            lodPercent++;
            processMap[uuid] = lodPercent;
        }, tme * 10);
        intervals = setInterval(() => {
            timeC.innerHTML = (durationMap[uuid] - 1).toString().toHHMMSS();
            durationMap[uuid]--;
            if (durationMap[uuid] <= 0) {
                durationMap[uuid] = tme;
                clearInterval(intervals);
            }
        }, 1000);
        arrIntervalTmeline[uuid] = intervalTmeline;
        arrIntervals[uuid] = intervals;
    } else {
        clearInterval(arrIntervalTmeline[uuid]);
        clearInterval(arrIntervals[uuid]);
        clientSocket.pauseAudioWithUUID(uuid);
    }
    
}
// Ham sinh ngau nhien ma UUID
function uuidv4() {
  return ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g, c =>
    (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
  );
}