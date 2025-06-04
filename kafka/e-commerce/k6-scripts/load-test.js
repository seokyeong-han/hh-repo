import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

export let options = {
    scenarios: { //10초동안 100명의 사용자가 요청반복
        peak_load: {
            executor: 'constant-vus',
            vus: 100, // 동시 사용자 수
            duration: '10s', // 짧은 시간에 몰아치기
        }
    }
};

export default function () {
    const url = 'http://host.docker.internal:8080/api/orders';

    const payload = JSON.stringify({
        userId: randomIntBetween(1, 500),
        items: [
            { productId: 10, quantity: 1 }
        ]
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const res = http.post(url, payload, params);

    check(res, {
        'status is 200': (r) => r.status === 200,
    });

    sleep(1); // 각 유저가 1초 대기
}