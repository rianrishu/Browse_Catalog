import assert from 'node:assert/strict';
import test from 'node:test';
import { catalogue } from './catalogue.ts';

test('catalogue contains editorial picks and local photo paths', async () => {
  const row = { title: 'Bag', category: 'Travel', description: 'Canvas', price: 18, rating: { rate: 4.8 },
    image: 'https://fakestoreapi.com/img/bag.jpg' };
  const response = await catalogue('GET', '/products', async url => {
    assert.equal(url, 'https://fakestoreapi.com/products');
    return Response.json(Array.from({ length: 20 }, (_, i) => ({ ...row, id: i + 1 })));
  });
  const products = await response.json();
  assert.deepEqual(products.filter((p: { featured: boolean }) => p.featured).map((p: { id: number }) => p.id), [1, 5, 9, 14, 18]);
  assert.equal(products[0].imagePath, '/img/bag.jpg');
  assert.equal(products[0].rating, 4.8);
  assert.ok(!JSON.stringify(products).includes('fakestoreapi.com'));
});

test('photos are served by the backend, without redirecting the client', async () => {
  const response = await catalogue('GET', '/img/bag.jpg', async (url, options) => {
    assert.equal(url, 'https://fakestoreapi.com/img/bag.jpg');
    assert.equal(options?.redirect, 'error');
    return new Response('photo', { headers: { 'Content-Type': 'image/jpeg' } });
  });
  assert.equal(response.headers.get('Content-Type'), 'image/jpeg');
  assert.equal(await response.text(), 'photo');
});

test('invalid paths and invalid upstream responses fail', async () => {
  assert.equal((await catalogue('GET', '/img/../products')).status, 404);
  assert.equal((await catalogue('GET', '/products', async () => Response.json([{}]))).status, 502);
});
