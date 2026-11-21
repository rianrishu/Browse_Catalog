const store = 'https://fakestoreapi.com';
const featuredIds = new Set([1, 5, 9, 14, 18]);
const photoPath = /^\/img\/[\w.-]+\.(?:jpe?g|png|webp)$/i;

function product(row: unknown) {
  if (!row || typeof row !== 'object' ||
      !('id' in row) || typeof row.id !== 'number' || !Number.isSafeInteger(row.id) ||
      !('title' in row) || typeof row.title !== 'string' ||
      !('description' in row) || typeof row.description !== 'string' ||
      !('category' in row) || typeof row.category !== 'string' ||
      !('price' in row) || typeof row.price !== 'number' || !Number.isFinite(row.price) ||
      !('rating' in row) || !row.rating || typeof row.rating !== 'object' ||
      !('rate' in row.rating) || typeof row.rating.rate !== 'number' || !Number.isFinite(row.rating.rate)) {
    throw new Error('Invalid product');
  }
  const path = 'image' in row && typeof row.image === 'string' && row.image.startsWith(store + '/img/')
    ? row.image.slice(store.length) : '';
  return {
    id: row.id, title: row.title, description: row.description, category: row.category,
    price: row.price, rating: row.rating.rate, featured: featuredIds.has(row.id),
    imagePath: photoPath.test(path) ? path : null
  };
}

export async function catalogue(method: string, path: string, load: typeof fetch = fetch): Promise<Response> {
  if (method !== 'GET') return new Response(null, { status: 405, headers: { Allow: 'GET' } });
  if (path !== '/products' && !photoPath.test(path)) return new Response(null, { status: 404 });
  try {
    const upstream = await load(store + path, { signal: AbortSignal.timeout(8000), redirect: 'error' });
    if (!upstream.ok) return new Response(null, { status: upstream.status === 404 ? 404 : 502 });
    if (path === '/products') {
      const rows: unknown = await upstream.json();
      if (!Array.isArray(rows)) throw new Error('Invalid catalogue');
      return Response.json(rows.map(product));
    }
    const type = upstream.headers.get('content-type')?.split(';')[0];
    if (!type || !['image/jpeg', 'image/png', 'image/webp'].includes(type)) throw new Error('Invalid photo');
    return new Response(upstream.body, { headers: { 'Content-Type': type, 'Cache-Control': 'max-age=86400' } });
  } catch {
    return Response.json({ error: 'Product source unavailable' }, { status: 502 });
  }
}
