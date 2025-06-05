import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

export let options = {
    stages: [
        { duration: '2s', target: 0 },    // 준비
        { duration: '1s', target: 1000 },  // 급증
        { duration: '10s', target: 300 }, // 유지
        { duration: '5s', target: 0 },    // 감소
    ]
};

export default function () {
    //상품 조회
    let orderUrl = 'http://host.docker.internal:8080/api/products';
    const res1 = http.get(orderUrl);
    check(res1, { '상품 조회 성공 status is 200': (r) => r.status === 200 });

    //인기 상품 조회
    let popOrderUrl = 'http://host.docker.internal:8080/api/popularProduct/today';
    const res2 = http.get(popOrderUrl);
    check(res2, { '인기 상품 조회 성공 status is 200': (r) => r.status === 200 });

    //주문
    const url = 'http://host.docker.internal:8080/api/orders';
    const payload  = JSON.stringify({
        userId: randomIntBetween(1, 500),
        items: [
            {
                productId: randomIntBetween(1, 10)
                ,quantity: 1
            }
        ]
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const res = http.post(url, payload, params);

    // 응답 코드가 4xx 또는 5xx면 실패로 기록
    if (res.status >= 400) {
        errorCount.add(1);
    }

    check(res, {
        '주문 성공 status is 200': (r) => r.status === 200,
    });

    sleep(1); // 각 유저가 1초 대기
}