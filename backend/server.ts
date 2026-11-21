import { createServer } from 'node:http';
import { Readable } from 'node:stream';
import { pipeline } from 'node:stream/promises';
import { enquiry } from './enquiries.ts';
import { catalogue } from './catalogue.ts';

createServer(async (request, response) => {
  const result = request.method === 'POST' && request.url === '/enquiries'
    ? await enquiry(request)
    : await catalogue(request.method ?? '', request.url ?? '');
  response.writeHead(result.status, Object.fromEntries(result.headers));
  if (!result.body) {
    response.end();
    return;
  }
  try {
    await pipeline(Readable.from(result.body), response);
  } catch {
    response.destroy();
  }
}).listen(3000, '127.0.0.1', () => {
  console.log('Catalogue backend listening at http://127.0.0.1:3000');
});
