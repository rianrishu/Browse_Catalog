import assert from 'node:assert/strict';
import test from 'node:test';
import { Readable } from 'node:stream';
import { catalogueHandler, products } from './server.mjs';

test('catalogue and enquiry delivery', async () => {
  const product = { id: 1, title: 'Bag' };
  const sent = [];
  const handle = catalogueHandler(async () => [product], async enquiry => sent.push(enquiry));
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
  assert.deepEqual((await request('GET', '/products')).json, [product]);
  const post = value => request('POST', '/enquiries', value);
  const form = { name: 'Sam', email: 'sam@example.test', productId: 1 };
  assert.equal((await post({ ...form, email: 'bad' })).status, 400);
  assert.equal((await post({ ...form, productId: 7 })).status, 404);
  assert.equal((await post(form)).status, 201);
  assert.deepEqual(sent, [{ name: form.name, email: form.email, product }]);
});

test('the store adapter exposes five editor picks', async t => {
  const rows = Array.from({ length: 20 }, (_, index) => ({
    id: index + 1, title: 'Bag', description: 'Canvas', category: 'Bags', price: 5, rating: { rate: 4 }
  }));
  t.mock.method(globalThis, 'fetch', async () => Response.json(rows));
  const catalogue = await products();
  assert.deepEqual(catalogue.filter(row => row.featured).map(row => row.id), [1, 5, 9, 14, 18]);
  assert.equal(catalogue[0].rating, 4);
});

test('an inbox failure cannot produce a thank you', async () => {
  const handle = catalogueHandler(async () => [{ id: 1 }], async () => { throw Error('Offline'); });
  const req = Readable.from([Buffer.from(JSON.stringify({ productId: 1, name: 'Sam', email: 's@example.test' }))]);
  Object.assign(req, { method: 'POST', url: '/enquiries', headers: { 'content-type': 'application/json' } });
  const res = { writeHead(status) { this.status = status; }, end() {} };
  await handle(req, res);
  assert.equal(res.status, 502);
});
