// WebSocket客户端
let stompClient = null
// 发起人ID
let inputCallerId = document.querySelector("input#callerId")
// 呼叫人ID
let inputReplyId = document.querySelector("input#replyId")
// 登录登出按钮
let loginButton =  document.querySelector("button#login")
// 呼叫挂断按钮
let callButton = document.querySelector("button#call")
// 本地视频
let localVideo = document.querySelector("video#local_video")
// 远端视频
let remoteVideo = document.querySelector("video#remote_video")
// 在线的用户
let ulPeers = document.querySelector("ul#peer_list")
// 接收人ID
let replierId = null
// 发起人ID
let callerId = null
// 是否在通话中
let rtcPeerConnection = null
let localStream = null
let remoteStream = null
let isBusy = false
// 重试次数
const tryTimes = 0

const iceConfig = {
    iceServers: [
        {
            urls: 'stun:47.121.30.131:3478'
        },
        {
            urls: 'turn:47.121.30.131:3478',
            username: 'username1',
            credential: 'password1'
        }
    ]
}

const subscribeTopic = '/user/voice/zhangsan'
const sendTopic = '/ws/app/voice/zhangsan'

// 登录
function login() {
    callerId = inputCallerId.value
    if (callerId === '') {
        alert('用户名不能为空')
        return
    }
    // 与信令服务器建立连接
    connect()
}

// 登出
function logout() {

}

// 发送数据到信令服务器
function sendToSignalServer(msgType, customData) {
    const data = {
        msgType: msgType,
        callDeviceId: callerId,
        replyId: replierId,
        data: customData
    }
    stompClient.send(sendTopic, {}, JSON.stringify(data))
}

function createRtcPeerConnection() {
    if (rtcPeerConnection) {
        console.log('Peer connection has already been created.')
        return
    }
    rtcPeerConnection = new RTCPeerConnection(iceConfig)
    rtcPeerConnection.onicecandidate = (event) => {
        if (event.candidate) {
            // 网络协商
            const data = {
                candidate: event.candidate
            }
            sendToSignalServer(10, data)
        }
    }
    rtcPeerConnection.oniceconnectionstatechange = (even) => {
        console.log('oniceconnectionstatechange', rtcPeerConnection.iceConnectionState)
    }
    rtcPeerConnection.ontrack = (event) => {
        if (remoteStream === null) {
            remoteStream = new MediaStream()
        }
    }
}

// 呼叫
function startCall() {
    replierId = inputReplyId.value
    if (replierId === '') {
        alert('被呼叫人的ID不能为空')
        return
    }

    sendToSignalServer(1, null)
}

// 挂断
function endCall() {

}

function onMessage(message) {
    console.log('Receive signal server message' + message)
    const msgType = message.msgType
    if (!msgType) {
        return
    }
    switch (msgType) {
        case 1:
            // 接收到通话请求
            if (isBusy) {
                // 正在通话中
            } else {
                // 接收通话请求
                sendToSignalServer(2, null)
                isBusy = true
            }
            break
        case 2:
            // 对方接收通话请求
            break
        case 3:

    }

}

// 开启socket连接
function connect() {
    const socket  = new SockJS('http://47.121.30.131:8989/ws/stomp');
    stompClient = Stomp.over(socket);
    // stompClient.connect(headers, connectCallback, errorCallback);
    // 设置心跳
    stompClient.heartbeat.outgoing = 5000;
    stompClient.heartbeat.incoming = 5000;
    stompClient.connect({}, function (frame) {
        console.log('Connect signal server success')
        loginButton.textContent = '登出'
        setConnected(true)
        // 订阅消息
        subscribe()
        // 注册
        register()
    }, errorCallback => {
        console.log('=============error==============' + errorCallback);
        reconnect()
    });
}

// 尝试重连服务器
function reconnect() {
    if (tryTimes > 10) {
        console.log('重试次数已达上限')
        return;
    }
    setTimeout(() => connect(), 3000);
}

function register() {
    const data = {
        msgType: 0
    }
    stompClient.send(sendTopic, {}, JSON.stringify(data))
}

function unregister() {
    const data = {
        msgType: 8
    }
    stompClient.send(sendTopic, {}, JSON.stringify(data))
}

// 断开socket连接
function disconnect() {
    if (stompClient != null) {
        unregister()
        stompClient.disconnect(function (frame) {
            setConnected(false);
        }, {});
    }
    console.log("Disconnect from signal server");
}

// send to server
function sendTo() {
    const data = {
        msgType: 1,
        callDeviceId: 'zhangsan',
        callName: '张三',
        replyDeviceId: 'lisi',
        replyName: '李四'
    }
    stompClient.send(sendTopic, {}, JSON.stringify(data))
}

// subscribe
// 客户端订阅的时候需要将参数传给服务器，key值为"param"，value为JsonString
function subscribe() {
    console.log("subscribe");
    stompClient.subscribe(subscribeTopic, function (data) {
        const message = data.body;
        const rt = {
            messageId: data.headers.messageId
        }
        messageList.append("<li>" + 'subscribe' + message + "</li>");
        data.ack(rt);
    }, {});
}

loginButton.onclick = () => {
    if (loginButton.textContent === '登录') {
        login()
    } else if (loginButton.textContent === '登出') {
        logout()
    } else {
        alert('未知操作')
    }
}

callButton.onclick = () => {
    if (callButton.textContent === '呼叫') {
        startCall()
    } else if (callButton.textContent === '挂断') {
        endCall()
    } else {
        alert('未知操作')
    }
}