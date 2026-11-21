import { createServer } from 'node:http';
import { pathToFileURL } from 'node:url';

const featuredIds = [1, 5, 9, 14, 18];

export async function products() {
  const response = await fetch('https://fakestoreapi.com/products', { signal: AbortSignal.timeout(8000) });
  if (!response.ok) throw new Error('Product source unavailable');
  const rows = await response.json();
  return rows.map(({ id, title, description, category, price, rating }) => {
    if (!Number.isInteger(id) || ![title, description, category].every(v => typeof v === 'string') ||
        !Number.isFinite(price) || !Number.isFinite(rating?.rate)) throw new Error('Invalid product');
    return { id, title, description, category, price, rating: rating.rate, featured: featuredIds.includes(id) };
  });
}

async function deliver({ product, name, email }) {
  const response = await fetch(new URL('/api/v1/send', process.env.MAILPIT_URL || 'http://127.0.0.1:8025'), {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    signal: AbortSignal.timeout(8000),
    body: JSON.stringify({
      From: { Email: 'catalogue@example.test' },
      To: [{ Email: process.env.SALES_EMAIL || 'sales@example.test' }],
      ReplyTo: [{ Email: email }],
      Subject: 'Product enquiry #' + product.id,
      Text: name + ' is interested in ' + product.title + '. Contact: ' + email
    })
  });
  if (!response.ok) throw new Error('Inbox unavailable');
}

export function catalogueHandler(load = products, send = deliver) {
  return async (req, res) => {
    function reply(status, value) {
      res.writeHead(status, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify(value));
    }
    try {
      if (req.method === 'GET' && req.url === '/products') return reply(200, await load());
      if (req.method !== 'POST' || req.url !== '/enquiries') return reply(404, { error: 'Unknown route' });
      if (req.headers['content-type']?.split(';')[0] !== 'application/json') return reply(415, {});
      let size = 0;
      const chunks = [];
      for await (const chunk of req) {
        size += chunk.length;
        if (size > 4096) return reply(413, {});
        chunks.push(chunk);
      }
      let input;
      try { input = JSON.parse(Buffer.concat(chunks).toString()); }
      catch { return reply(400, { error: 'Invalid JSON' }); }
      const name = typeof input?.name === 'string' && input.name.trim();
      const email = typeof input?.email === 'string' && input.email.trim();
      if (!name || name.length > 100 || !email || email.length > 254 ||
          !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email) || !Number.isInteger(input?.productId)) {
        return reply(400, { error: 'Invalid contact or product' });
      }
      const product = (await load()).find(item => item.id === input.productId);
      if (!product) return reply(404, { error: 'Unknown product' });
      await send({ product, name, email });
      reply(201, { delivered: true });
    } catch {
      reply(502, { error: 'Service unavailable' });
    }
  };
}

if (import.meta.url === pathToFileURL(process.argv[1]).href) {
  createServer(catalogueHandler()).listen(Number(process.env.PORT || 3000), '0.0.0.0');
}
