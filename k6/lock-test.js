import http from 'k6/http';
import { check } from 'k6';

export let options = {
    target: 179,
    duration: '30s',
    // iterations: 500,
};

const BASE_URL = 'http://localhost:8080';
const FIXED_COUPON_ID = 1; // 테스트 대상 쿠폰 ID

export default function () {
    // 각 VU와 iteration 정보를 기반으로 고유 userId 생성
    const userId = __VU;
    const payload = JSON.stringify({
        userId: userId,
        couponId: FIXED_COUPON_ID,
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const res = http.post(`${BASE_URL}/api/v1/coupons/issue`, payload, params);

    check(res, {
        'status is 200': (r) => r.status === 200,
    })
}
