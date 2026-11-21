import type { IncomingMessage } from 'node:http';

interface Message {
  from: string;
  to: string;
  replyTo: string;
  subject: string;
  text: string;
}

const address = /^[^\s@,;<>"]+@[^\s@,;<>"]+\.[^\s@,;<>"]+$/;
function validAddress(value: unknown): value is string {
  return typeof value === 'string' && value.length <= 254 && address.test(value);
}

async function sendSmtp(message: Message) {
  const { SMTP_HOST: host, SMTP_USER: user, SMTP_PASSWORD: pass } = process.env;
  const port = Number(process.env.SMTP_PORT || 587);
  if (!host || !user || !pass || ![465, 587].includes(port)) throw new Error('Invalid SMTP settings');
  const { createTransport } = await import('nodemailer');
  const transport = createTransport({
    host, port, secure: port === 465, requireTLS: true, auth: { user, pass },
    connectionTimeout: 8000, socketTimeout: 10000
  });
  const result = await transport.sendMail(message);
  if (!result.accepted.length) throw new Error('Recipient rejected');
}

export async function enquiry(req: IncomingMessage, send = sendSmtp, env = process.env): Promise<Response> {
  if (req.headers['content-type']?.split(';')[0] !== 'application/json') return new Response(null, { status: 415 });
  const chunks: Buffer[] = [];
  let size = 0;
  let data;
  try {
    for await (const chunk of req) {
      size += chunk.length;
      if (size > 4096) return new Response(null, { status: 413 });
      chunks.push(chunk);
    }
    data = JSON.parse(Buffer.concat(chunks).toString());
  } catch {
    return new Response(null, { status: 400 });
  }
  const { productId, title, name, email } = data ?? {};
  if (!Number.isSafeInteger(productId) || productId < 1 ||
      typeof title !== 'string' || !title.trim() || title.length > 512 ||
      typeof name !== 'string' || !name.trim() || name.length > 100 || !validAddress(email)) {
    return new Response(null, { status: 400 });
  }
  const { MAIL_FROM: from, SALES_EMAIL: to } = env;
  if (!validAddress(from) || !validAddress(to)) return new Response(null, { status: 503 });
  try {
    await send({ from, to, replyTo: email, subject: 'Product enquiry #' + productId,
      text: name.trim() + ' is interested in ' + title.trim() + '. Reply to ' + email + '.' });
    return new Response(null, { status: 204 });
  } catch {
    return Response.json({ error: 'Mail provider did not confirm acceptance' }, { status: 502 });
  }
}
