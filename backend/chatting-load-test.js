import { check } from 'k6';
import http from 'k6/http';
import ws from 'k6/ws';
import { sleep } from 'k6';
import exec from 'k6/execution';

// 테스트 설정 변수들
const CONFIG = {
    USER_COUNT: 1,                // 총 테스트 유저 수
    CHAT_MIN_INTERVAL: 1,          // 채팅 최소 간격 (초)
    CHAT_MAX_INTERVAL: 5,          // 채팅 최대 간격 (초)
    CHAT_DURATION: 60,             // 채팅 지속 시간 (초)
    MESSAGES: [                    // 랜덤 채팅 메시지 풀
        "안녕하세요!",
        "반갑습니다~",
        "테스트 중입니다.",
        "좋은 하루 되세요!",
        "화이팅!",
    ],
    ROOM_IDS: [                    // 참여 가능한 방 ID들
        "ae79ccda-b999-487c-87ed-eff593373895",
        "5e131c74-6e16-4198-a87c-e569a0b4dc7d",
        "38fbb7a3-8f9a-4c4c-8ab2-6e3c285b0412",
        "ae79ccda-b999-487c-87ed-eff593373895",
        "d5547481-0a0e-4c66-b695-a9dd4fa7c2b2"
    ]
};

export const options = {
    stages: [
        { duration: '10s', target: CONFIG.USER_COUNT },
        { duration: `${CONFIG.CHAT_DURATION}s`, target: CONFIG.USER_COUNT },
        { duration: '10s', target: 0 }
    ],
    thresholds: {
        checks: ['rate==1.0']
    }
};

const users = Array.from({ length: CONFIG.USER_COUNT }, (_, i) => ({
    username: `testuser${i + 1}`,
    email: `testuser${i + 1}@test.com`,
    password: 'Password123!',
    passwordCheck: 'Password123!'
}));

// 랜덤 함수들
function getRandomInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function getRandomMessage() {
    return CONFIG.MESSAGES[getRandomInt(0, CONFIG.MESSAGES.length - 1)];
}

function getRandomRoomId() {
    return CONFIG.ROOM_IDS[getRandomInt(0, CONFIG.ROOM_IDS.length - 1)];
}

export default function () {
    const userIndex = (__VU - 1) % users.length;
    const user = users[userIndex];
    const baseUrl = 'http://localhost:8080';
    let authToken;

    // 1. 회원가입
    console.log(`\n[회원가입 시도] ${user.username}`);
    const signupRes = http.post(`${baseUrl}/api/auth/signup`, JSON.stringify({
        username: user.username,
        email: user.email,
        password: user.password,
        passwordCheck: user.passwordCheck
    }), {
        headers: { 'Content-Type': 'application/json' }
    });

    console.log(`[회원가입 응답] 상태: ${signupRes.status}`);
    console.log(`[회원가입 응답] 본문: ${signupRes.body}`);
    
    const signupSuccess = check(signupRes, {
        '회원가입 성공': (r) => r.status === 200 || r.status === 400
    });

    if (!signupSuccess) {
        console.error(`\n회원가입 실패: ${user.username}`);
        console.error(`상태 코드: ${signupRes.status}`);
        console.error(`응답 내용: ${signupRes.body}`);
        exec.test.abort();
        return;
    }

    sleep(2);

    // 2. 로그인
    console.log(`\n[로그인 시도] ${user.username}`);
    const loginRes = http.post(`${baseUrl}/api/auth/login`, JSON.stringify({
        username: user.username,
        password: user.password
    }), {
        headers: { 'Content-Type': 'application/json' }
    });

    console.log(`[로그인 응답] 상태: ${loginRes.status}`);
    console.log(`[로그인 응답] 본문: ${loginRes.body}`);

    const loginSuccess = check(loginRes, {
        '로그인 성공': (r) => r.status === 200
    });

    if (!loginSuccess) {
        console.error(`\n로그인 실패: ${user.username}`);
        console.error(`상태 코드: ${loginRes.status}`);
        console.error(`응답 내용: ${loginRes.body}`);
        exec.test.abort();
        return;
    }

    try {
        const responseBody = JSON.parse(loginRes.body);
        authToken = responseBody.data?.token || responseBody.token;
        
        if (!authToken) {
            console.error('\n토큰 없음');
            console.error(`응답 본문: ${loginRes.body}`);
            exec.test.abort();
            return;
        }
        
        console.log(`[인증] 토큰 정상 수신`);
    } catch (e) {
        console.error(`\n로그인 응답 파싱 실패: ${e}`);
        console.error(`응답 본문: ${loginRes.body}`);
        exec.test.abort();
        return;
    }

    sleep(2);

    // 3. 방 참가
    const targetRoomId = getRandomRoomId();
    console.log(`\n[방 참가 시도] ${user.username} -> RoomID: ${targetRoomId}`);
    
    const joinRoomRes = http.post(`${baseUrl}/api/room/${targetRoomId}/join`, 
        null,
        {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            }
        }
    );

    console.log(`[방 참가 응답] 상태: ${joinRoomRes.status}`);
    console.log(`[방 참가 응답] 본문: ${joinRoomRes.body}`);

    const joinRoomSuccess = check(joinRoomRes, {
        '방 참가 성공': (r) => r.status === 200
    });

    if (!joinRoomSuccess) {
        console.error(`\n방 참가 실패: ${user.username}`);
        console.error(`상태 코드: ${joinRoomRes.status}`);
        console.error(`응답 내용: ${joinRoomRes.body}`);
        exec.test.abort();
        return;
    }

    // 4. 채팅 시작
    const url = 'ws://localhost:8080/ws-stomp';
    const response = ws.connect(url, 
        {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        }, 
        function (socket) {
            socket.on('open', () => {
                console.log(`\n[웹소켓 연결 성공] ${user.username}`);
                
                // STOMP CONNECT
                socket.send('CONNECT\naccept-version:1.2\n\n\x00');

                // STOMP SUBSCRIBE
                socket.send(`SUBSCRIBE\nid:sub-${Math.random()}\ndestination:/sub/chat/room/${targetRoomId}\n\n\x00`);

                // 채팅 메시지 전송 루프
                const intervalId = setInterval(() => {
                    const message = getRandomMessage();
                    const body = JSON.stringify({
                        from: user.username,
                        to: targetRoomId,
                        message: message,
                        timestamp: new Date().toISOString()
                    });

                    // STOMP SEND
                    socket.send(
                        `SEND\ndestination:/pub/chat/message/${targetRoomId}\ncontent-type:application/json\ncontent-length:${body.length}\n\n${body}\x00`
                    );

                    console.log(`[채팅 전송] ${user.username}: ${message}`);
                }, getRandomInt(CONFIG.CHAT_MIN_INTERVAL * 1000, CONFIG.CHAT_MAX_INTERVAL * 1000));

            // 채팅 종료 타이머
            setTimeout(() => {
                clearInterval(intervalId);
                socket.close();
            }, CONFIG.CHAT_DURATION * 1000);
        });

        socket.on('message', (data) => {
            console.log('Received:', data);
        });
    });

    sleep(CONFIG.CHAT_DURATION);
    console.log(`\n[테스트 완료] ${user.username}`);
}