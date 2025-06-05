import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Rate, Counter } from 'k6/metrics';

const TEST_TYPE = __ENV.TEST_TYPE || 'smoke';
const BASE_URL = __ENV.BASE_URL || 'http://spring-app:8080';
const COUPON_ENDPOINT = '/api/v1/coupons/issue/request';
const COUPON_ID = 13;

const successRate = new Rate('success_rate');
const failCount = new Counter('fail_count');
const requestDuration = new Trend('request_duration');

export let options = {};

switch (TEST_TYPE) {
    case 'smoke':
        options = {
            vus: 1,
            duration: '10s',
        };
        break;

    case 'load':
        options = {
            stages: [
                { duration: '30s', target: 2000 },  // ramp-up
                { duration: '1m', target: 2000 },   // steady
                { duration: '30s', target: 0 },    // ramp-down
            ],
            thresholds: {
                http_req_duration: ['p(95)<1000'],
                http_req_failed: ['rate<0.15'],
                'success_rate': ['rate>0.85'],
            },
        };
        break;

    case 'stress':
        options = {
            stages: [
                { duration: '1m', target: 1000 },
                { duration: '1m', target: 1000 },
                { duration: '1m', target: 2000 },
                { duration: '1m', target: 2000 },
                { duration: '1m', target: 3000 },
                { duration: '1m', target: 3000 },
                { duration: '1m', target: 4000 },
                { duration: '1m', target: 4000 },
                { duration: '1m', target: 5000 },
                { duration: '1m', target: 5000 },
                { duration: '30s', target: 0 },
            ],
            thresholds: {
                http_req_duration: ['p(95)<1000'],
                http_req_failed: ['rate<0.15'],
                'success_rate': ['rate>0.85'],
            },
        };
        break;

    case 'peak':
        options = {
            stages: [
                { duration: '10s', target: 100 },    // baseline
                { duration: '10s', target: 100 },   // spike
                { duration: '20s', target: 2000 },
                { duration: '10s', target: 100 },    // drop
                { duration: '20s', target: 0 },
            ],
            thresholds: {
                http_req_duration: ['p(95)<1000'],
                http_req_failed: ['rate<0.15'],
                'success_rate': ['rate>0.85'],
            },
        };
        break;

    default:
        console.error(`Unknown TEST_TYPE: ${TEST_TYPE}`);
        break;
}

export default function () {
    const userId = Math.floor(Math.random() * 100000) + 1; // 유저 아이디 고유하게 생성
    const url = `${BASE_URL}${COUPON_ENDPOINT}`;
    const payload = JSON.stringify({
        "userId": userId,
        "couponId": COUPON_ID
    });

    const headers = { 'Content-Type': 'application/json' };
    const res = http.post(url, payload, { headers });

    const success = check(res, {
        'status is 200': (r) => r.status === 200,
        'total request': (r) => [200, 409].includes(r.status),
    });

    successRate.add(success);
    if (!success) failCount.add(1);
    requestDuration.add(res.timings.duration);

    sleep(1);
}
