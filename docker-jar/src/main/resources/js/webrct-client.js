// import {SockJS, Stomp} from '../sockjs.min'

// WebSocket客户端
let stompClient = null
// 发起人ID
let inputCallerId = document.querySelector("input#callerId")
// 呼叫人ID
let inputReplyId = document.querySelector("input#replyId")
// 登录登出按钮
let loginButton = document.querySelector("button#login")
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
    disconnect()
    loginButton.textContent = '登录'
    endCall()
}

// 发送数据到信令服务器
function sendToSignalServer(msgType, customData) {
    const data = {
        msgType: msgType,
        callDeviceId: callerId,
        replyId: replierId,
        iceCandidate: customData
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
            sendToSignalServer(3, data)
        }
    }
    rtcPeerConnection.oniceconnectionstatechange = (even) => {
        console.log('oniceconnectionstatechange', rtcPeerConnection.iceConnectionState)
    }
    rtcPeerConnection.ontrack = (event) => {
        if (remoteStream === null) {
            remoteStream = new MediaStream()
            remoteVideo.srcObject = remoteVideo
            remoteVideo.style.display = 'inline-block'
        }
    }
}

async function getLocalMediaStream() {
    if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
        console.log('GetUserMedia is not supported!')
        return null
    } else if (localStream) {
        console.log('LocalStream already exist.')
        return localStream
    } else {
        const constraints = {
            video: true,
            audio: true
        }
        return navigator.mediaDevices.getUserMedia(constraints)
    }
}

function attachMediaStreamToLocalVideo() {
    localVideo.srcObject = localStream
    localVideo.muted = true
    localVideo.style.display = 'inline-block'
}

function attachMediaStreamToPeerConnection() {
    localStream.getTracks().forEach(track => {
        rtcPeerConnection.addTrack(track)
    })
}

function closeLocalMediaStream() {
    if (localStream != null) {
        localStream.getTracks().forEach(track => {
            track.stop()
        })
        localStream = null
    }
}

function closeRtcPeerConnection() {
    if (rtcPeerConnection) {
        rtcPeerConnection.close()
        rtcPeerConnection = null
    }
}

function handleOfferSdp(data) {
    rtcPeerConnection.setRemoteDescription(new RTCPeerConnection(data))
        .then(() => {
            createAnswer()
        })
        .catch(err => {
            console.log('RtcPeerConnection setRemoteDescription failed', err)
        })
}

function handleAnswerSdp(data) {
    rtcPeerConnection.setRemoteDescription(new RTCSessionDescription(data))
        .then(() => console.log('RtcPeerConnection setRemoteDescription success'))
}

function handleCandidate(data) {
    rtcPeerConnection.addIceCandidate(new RTCIceCandidate(data))
        .then(() => console.log('RtcPeerConnection addIceCandidate success'))
}

// 呼叫
async function startCall(isInitiator) {
    replierId = inputReplyId.value
    if (replierId === '') {
        alert('被呼叫人的ID不能为空')
        return
    }
    createRtcPeerConnection()
    localStream = await getLocalMediaStream()
    attachMediaStreamToLocalVideo()
    attachMediaStreamToPeerConnection()
    sendToSignalServer(1, null)
    if (isInitiator) {
        // 发起呼叫
        sendToSignalServer(1)
    } else {
        // 接收呼叫
        sendToSignalServer(2)
    }
}

// 挂断
function endCall() {
    closeLocalMediaStream()
    closeRtcPeerConnection()
    localVideo.style.display = 'none'
    remoteVideo.style.display = 'none'
    remoteStream = null
    replierId = null
}

// 媒体协商
function createOffer() {
    const offerOption = {
        offerToReceiveAudio: true,
        offerToReceiveVideo: true
    }
    rtcPeerConnection.createOffer(offerOption)
        .then(desc => {
            rtcPeerConnection.setLocalDescription(desc).then(() => console.log('SetLocalDescription success'))
            const data = {
                sdp: desc
            }
            sendToSignalServer(4, data)
        })
        .catch(err => {
            console.log(`CreateOffer failed, error name: ${err.name}, error message: ${err.message}`)
        })
}

function createAnswer() {
    rtcPeerConnection.createAnswer()
        .then(desc => {
            rtcPeerConnection.setLocalDescription(desc).then(() => console.log('SetLocalDescription success'))
            const data = {
                sdp: desc
            }
            sendToSignalServer(5, data)
        })
        .catch(err => {
            console.log(`CreateAnswer failed, error name: ${err.name}, error message: ${err.message}`)
        })
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
                startCall(false)
                isBusy = true
            }
            break
        case 2:
            // 对方接收通话请求
            createOffer()
            break
        case 3:
            // 网络协商
            handleCandidate()
            break
        case 4:
            // 媒体协商Offer
            handleOfferSdp(message.iceCandidate.sdp)
            break
        case 5:
            // 媒体协商Offer
            handleAnswerSdp(message.iceCandidate.sdp)
            break
    }

}

// 开启socket连接
function connect() {
    const socket = new SockJS('http://47.121.30.131:8989/ws/stomp');
    stompClient = Stomp.over(socket);
    // stompClient.connect(headers, connectCallback, errorCallback);
    // 设置心跳
    stompClient.heartbeat.outgoing = 5000;
    stompClient.heartbeat.incoming = 5000;
    stompClient.connect({}, function (frame) {
        console.log('Connect signal server success')
        loginButton.textContent = '登出'
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
        }, {});
    }
    console.log("Disconnect from signal server");
}

// subscribe
function subscribe() {
    console.log("subscribe");
    stompClient.subscribe(subscribeTopic, function (data) {
        const message = data.body;
        onMessage(message)
    }, {})
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