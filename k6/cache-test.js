import http from 'k6/http';
import { check } from 'k6';
import { Trend } from 'k6/metrics';

const responseTime = new Trend('response_time');

export let options = {
    vus: 100,
    duration: '30s',
};

const BASE_URL = 'http://localhost:8080';

export default function () {
    const res = http.get(`${BASE_URL}/api/v1/bestsellers`);

    responseTime.add(res.timings.duration);

    check(res, {
        'status is 200': (r) => r.status === 200,
        'response time < 500ms': (r) => r.timings.duration < 500,
    });
}
