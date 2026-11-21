import assert from 'node:assert/strict';
import test from 'node:test';
import { catalogueServer } from './server.mjs';

test('catalogue and enquiry delivery', async t => {
  const product = { id: 1, title: 'Bag' };
  const sent = [];
  const server = catalogueServer(async () => [product], async enquiry => sent.push(enquiry));
  await new Promise(resolve => server.listen(0, '127.0.0.1', resolve));
  t.after(() => server.close());
  const base = 'http://127.0.0.1:' + server.address().port;
  assert.deepEqual(await (await fetch(base + '/products')).json(), [product]);
  const post = input => fetch(base + '/enquiries', {
    method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(input)
  });
  const form = { name: 'Sam', email: 'sam@example.test', productId: 1 };
  assert.equal((await post({ ...form, email: 'bad' })).status, 400);
  assert.equal((await post({ ...form, productId: 7 })).status, 404);
  assert.equal((await post(form)).status, 201);
  assert.deepEqual(sent, [{ name: form.name, email: form.email, product }]);
});
