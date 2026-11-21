import assert from 'node:assert/strict';
import type { IncomingMessage } from 'node:http';
import { Readable } from 'node:stream';
import test from 'node:test';
import { enquiry } from './enquiries.ts';

const env = { MAIL_FROM: 'app@example.org', SALES_EMAIL: 'sales@example.org' };
function request(email = 'sam@example.org') {
  return Object.assign(Readable.from([Buffer.from(JSON.stringify({
    productId: 12, title: 'Canvas bag', name: 'Sam', email, to: 'other@example.org'
  }))]), { headers: { 'content-type': 'application/json' } }) as IncomingMessage;
}

test('mail uses the configured recipient and customer reply address', async () => {
  let delivered;
  const res = await enquiry(request(), async message => { delivered = message; }, env);
  assert.equal(res.status, 204);
  assert.deepEqual(delivered, { from: env.MAIL_FROM, to: env.SALES_EMAIL, replyTo: 'sam@example.org',
    subject: 'Product enquiry #12', text: 'Sam is interested in Canvas bag. Reply to sam@example.org.' });
});

test('invalid contact, missing settings and failed delivery cannot confirm success', async () => {
  const fail = async () => { throw new Error('provider refused mail'); };
  assert.equal((await enquiry(request('bad'), fail, env)).status, 400);
  assert.equal((await enquiry(request(), fail, {})).status, 503);
  assert.equal((await enquiry(request(), fail, env)).status, 502);
});
