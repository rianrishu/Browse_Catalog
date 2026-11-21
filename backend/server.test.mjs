import assert from 'node:assert/strict';
import test from 'node:test';
import { Readable } from 'node:stream';
import { enquiryHandler } from './server.mjs';

test('catalogue and enquiry delivery', async () => {
  const product = { id: 1, title: 'Bag' };
  const sent = [];
  const handle = enquiryHandler(async id => id === product.id ? product : null, async enquiry => sent.push(enquiry));
  async function request(method, url, value) {
    const req = Readable.from(value ? [Buffer.from(JSON.stringify(value))] : []);
    Object.assign(req, { method, url, headers: { 'content-type': 'application/json' } });
    const response = {
      writeHead(status) { this.status = status; },
      end(body) { this.json = JSON.parse(body); }
    };
    await handle(req, response);
    return response;
  }
  assert.equal((await request('GET', '/products')).status, 404);
  const post = value => request('POST', '/enquiries', value);
  const form = { name: 'Sam', email: 'sam@example.test', productId: 1 };
  assert.equal((await post({ ...form, email: 'bad' })).status, 400);
  assert.equal((await post({ ...form, productId: 7 })).status, 404);
  assert.equal((await post(form)).status, 201);
  assert.deepEqual(sent, [{ name: form.name, email: form.email, product }]);
});

test('an inbox failure cannot produce a thank you', async () => {
  const handle = enquiryHandler(async () => ({ id: 1 }), async () => { throw Error('Offline'); });
  const req = Readable.from([Buffer.from(JSON.stringify({ productId: 1, name: 'Sam', email: 's@example.test' }))]);
  Object.assign(req, { method: 'POST', url: '/enquiries', headers: { 'content-type': 'application/json' } });
  const res = { writeHead(status) { this.status = status; }, end() {} };
  await handle(req, res);
  assert.equal(res.status, 502);
});

test('a submitted enquiry sends the selected product and customer reply address to sales', async t => {
  const product = { id: 12, title: 'Wool scarf' };
  let message;
  t.mock.method(globalThis, 'fetch', async (url, options) => {
    assert.equal(new URL(url).pathname, '/api/v1/send');
    assert.equal(options.method, 'POST');
    message = JSON.parse(options.body);
    return Response.json({ ID: 'accepted' });
  });
  const req = Readable.from([Buffer.from(JSON.stringify({ productId: 12, name: ' Sam ', email: ' sam@example.test ' }))]);
  Object.assign(req, { method: 'POST', url: '/enquiries', headers: { 'content-type': 'application/json' } });
  const res = { writeHead(status) { this.status = status; }, end(body) { this.json = JSON.parse(body); } };
  await enquiryHandler(async () => product)(req, res);
  assert.equal(res.status, 201);
  assert.deepEqual(res.json, { delivered: true });
  assert.deepEqual(message.To, [{ Email: process.env.SALES_EMAIL || 'sales@example.test' }]);
  assert.deepEqual(message.ReplyTo, [{ Email: 'sam@example.test' }]);
  assert.match(message.Subject, /#12/);
  assert.match(message.Text, /Sam is interested in Wool scarf/);
});
