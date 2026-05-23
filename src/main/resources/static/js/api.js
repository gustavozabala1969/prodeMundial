// =============================================
// api.js — Cliente HTTP con JWT automático
// =============================================

const api = {
  baseUrl: '',   // mismo origen (Spring sirve el HTML)

  _headers() {
    const h = { 'Content-Type': 'application/json' };
    const token = localStorage.getItem('token');
    if (token) h['Authorization'] = 'Bearer ' + token;
    return h;
  },

  async _fetch(method, path, body) {
    const opts = { method, headers: this._headers() };
    if (body) opts.body = JSON.stringify(body);

    const res = await fetch(this.baseUrl + path, opts);

    if (res.status === 401 || res.status === 403) {
      // Token expirado o sin permiso
      if (res.status === 401) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '/login.html';
        return;
      }
    }

    let data;
    const ct = res.headers.get('content-type') || '';
    if (ct.includes('application/json')) {
      data = await res.json();
    } else {
      data = { message: await res.text() };
    }

    if (!res.ok) {
      throw new Error(data.error || data.message || 'Error en la solicitud');
    }
    return data;
  },

  get(path)         { return this._fetch('GET',    path); },
  post(path, body)  { return this._fetch('POST',   path, body); },
  put(path, body)   { return this._fetch('PUT',    path, body); },
  delete(path)      { return this._fetch('DELETE', path); },
};
