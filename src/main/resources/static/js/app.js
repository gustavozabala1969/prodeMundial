// =============================================
// app.js — Lógica principal Prode Mundial 2026
// =============================================

let currentUser = null;
let currentTab  = 'pronosticos';
let allMatches  = [];
let allGrupos  = [];
let allFechasTopes  = [];
let pendingSaves = {};   // matchId -> {home, away}

// ---- INIT ----

document.addEventListener('DOMContentLoaded', () => {
  const token = localStorage.getItem('token');
  const user  = localStorage.getItem('user');
  if (!token || !user) {
    window.location.href = '/login.html';
    return;
  }
  currentUser = JSON.parse(user);
  renderNav();
  document.getElementById('tabs').style.display = 'flex';
  if (currentUser.admin) {
    document.querySelectorAll('.admin-only').forEach(el => el.style.display = '');
  }
  loadGroupMatches();
  // loadFechaTopePredictions("GROUP");
  loadFechasTopes();
  // await mostrarFechaTopePredictions("GROUP");
});

// ---- NAV ----

function renderNav() {
  document.getElementById('navUser').textContent = '👤 ' + currentUser.name;
  document.getElementById('navLinks').innerHTML = `
    <a href="/login.html" onclick="doLogout(event)">Salir</a>
  `;
}

function doLogout(e) {
  e.preventDefault();
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  window.location.href = '/login.html';
}

// ---- TABS ----

function switchTab(tab) {
  currentTab = tab;
  document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
  document.querySelectorAll('.tab').forEach(t => {
    if (t.getAttribute('onclick') && t.getAttribute('onclick').includes("'" + tab + "'")) {
      t.classList.add('active');
    }
  });
  document.querySelectorAll('.tab-content').forEach(el => el.style.display = 'none');
  document.getElementById('tab-' + tab).style.display = 'block';

  if (tab === 'ranking')  loadRanking();
  if (tab === 'comparar') loadUsers();
  if (tab === 'admin')    loadAdminMatches();
}

// ---- PRONÓSTICOS ----

async function loadGroupMatches() {
  try {
    allMatches = await api.get('/api/matches/group');
    renderStats();
    renderMatches();
  } catch (e) {
    showToast('Error cargando partidos: ' + e.message, 'error');
  }
}

function renderStats() {
  const total   = allMatches.length;
  const done    = allMatches.filter(m => m.myPredHome !== null && m.myPredHome !== undefined).length;
  const finished = allMatches.filter(m => m.status === 'FINISHED').length;
  const myPts   = allMatches.reduce((s, m) => s + (m.myPoints || 0), 0);

  document.getElementById('statsGrid').innerHTML = `
    <div class="stat-card"><div class="stat-num">${done}</div><div class="stat-lbl">pronosticados</div></div>
    <div class="stat-card"><div class="stat-num">${total - done}</div><div class="stat-lbl">pendientes</div></div>
    <div class="stat-card"><div class="stat-num">${finished}</div><div class="stat-lbl">jugados</div></div>
    <div class="stat-card"><div class="stat-num">${myPts}</div><div class="stat-lbl">mis puntos</div></div>
  `;

  mostrarFechaTopePredictions("GROUP");
}

/*
function renderMatches() {
  const container = document.getElementById('matchesContainer');
  // Agrupar por grupo
  const groups = {};
  allMatches.forEach(m => {
    if (!groups[m.groupName]) groups[m.groupName] = [];
    groups[m.groupName].push(m);
  });

  let html = '';
  Object.entries(groups).forEach(([group, matches]) => {
    html += `<div class="group-header">Grupo ${group}</div>`;
    matches.forEach(m => { html += renderMatchCard(m); });
  });
  container.innerHTML = html;
}
*/

function renderMatches() {
  const container = document.getElementById('matchesContainer');

  // Agrupar por grupo
  const groups = {};

  allMatches.forEach(m => {
    if (!groups[m.groupName]) {
      groups[m.groupName] = [];
    }
    groups[m.groupName].push(m);
  });

  let html = '';

  Object.entries(groups).forEach(([group, matches]) => {

    html += `
      <div class="group-section">
        <div class="group-header">Grupo ${group}</div>

        <div class="group-columns">
    `;

    // Crear columnas de 2 partidos
    for (let i = 0; i < matches.length; i += 2) {

      html += `<div class="match-column">`;

      html += renderMatchCard(matches[i]);

      if (matches[i + 1]) {
        html += renderMatchCard(matches[i + 1]);
      }

      html += `</div>`;
    }

    html += `
        </div>
      </div>
    `;
  });

  container.innerHTML = html;
  mostrarFechaTopePredictions("GROUP");
}

function renderMatchCard(m) {
  const finished  = m.status === 'FINISHED';
  const hasPred   = m.myPredHome !== null && m.myPredHome !== undefined;
  const pts       = m.myPoints;

  let ptsHtml = '';
  if (finished && hasPred) {
    const cls = pts === 3 ? 'badge-3' : pts === 1 ? 'badge-1' : 'badge-0';
    const lbl = pts === 3 ? '🎯 Exacto (+3)' : pts === 1 ? '✅ Resultado (+1)' : '❌ Incorrecto (0)';
    ptsHtml = `<span class="result-badge ${cls}">${lbl}</span>`;
  } else if (!finished && hasPred) {
    ptsHtml = `<span class="result-badge badge-pending">⏳ Pendiente</span>`;
  }

  const realScore = finished
    ? `<strong>${m.homeScore} - ${m.awayScore}</strong> <small style="color:#888">(resultado real)</small>`
    : '';

  const phase = `${m.phase}`;
  const fecha = `<strong>Fecha ${m.phase}</strong>`;

  const predHome = hasPred ? m.myPredHome : '';
  const predAway = hasPred ? m.myPredAway : '';

  const fechaTope = allFechasTopes.find(f => f.phase === m.phase)?.fechaTopePrediction;
  const vencido = fechaTope && new Date() > new Date(fechaTope);
  const disabledAttr = finished || vencido ? 'disabled' : '';
  const buttonDisabled = finished || vencido ? 'disabled' : '';

  return `
  <div class="match-card ${finished ? 'finished' : ''}" id="card-${m.id}">
    <div class="match-meta">
      <span>📅 ${formatDate(m.matchDate)} ${m.matchTime}hs</span>
      <span class="texto-${m.phase}">${fecha}</span>
      <span>${ptsHtml}</span>
    </div>
    <div class="match-row">
      <div class="team">
        <span class="team-name">${m.homeTeam}</span>
      </div>
      <div class="score-zone">
        <input class="score-input" type="number" min="0" max="20"
          id="h-${m.id}" value="${predHome}" placeholder="0"
          ${disabledAttr}
          onchange="markChanged(${m.id})">
        <span class="score-vs">-</span>
        <input class="score-input" type="number" min="0" max="20"
          id="a-${m.id}" value="${predAway}" placeholder="0"
          ${disabledAttr}
          onchange="markChanged(${m.id})">
      </div>
      <div class="team right">
        <span class="team-name">${m.awayTeam}</span>
      </div>
    </div>
    ${finished ? `<div style="text-align:center;font-size:13px;color:#888;margin-top:-4px">${realScore}</div>` : ''}
    ${!finished ? `
    <div class="save-btn-row">
      ${false ? `<span class="team-flag">${m.homeFlag}</span>` : ''}
      <small style="color:#999;font-size:12px">${hasPred ? 'Pronóstico guardado' : 'Sin pronóstico aún'}</small>
      <button class="btn btn-green btn-sm" id="btn-${m.id}" onclick="savePred(${m.id})" ${buttonDisabled}>
        ${vencido
          ? '⛔ Cerrado'
          : hasPred
            ? '✏️ Actualizar'
            : '💾 Guardar'}
      </button>
      ${false ? `<span class="team-flag">${m.awayFlag}</span>` : ''}
    </div>` : ''}
  </div>`;
}

function markChanged(matchId) {
  const btn = document.getElementById('btn-' + matchId);
  if (btn) { btn.textContent = '💾 Guardar'; btn.className = 'btn btn-green btn-sm'; }
}

async function savePred(matchId) {
  const hInput = document.getElementById('h-' + matchId);
  const aInput = document.getElementById('a-' + matchId);
  const btn    = document.getElementById('btn-' + matchId);

  const home = parseInt(hInput.value);
  const away = parseInt(aInput.value);

  if (isNaN(home) || isNaN(away) || home < 0 || away < 0) {
    showToast('Ingresá un resultado válido.', 'error');
    return;
  }

  btn.disabled = true;
  btn.textContent = 'Guardando...';

  try {
    await api.post('/api/predictions', { matchId, homeScore: home, awayScore: away });
    btn.textContent = '✅ Guardado';
    btn.className = 'btn btn-saved btn-sm';
    showToast('Pronóstico guardado ✓', 'success');
    // Actualizar estado local
    const match = allMatches.find(m => m.id === matchId);
    if (match) { match.myPredHome = home; match.myPredAway = away; }
    renderStats();
    setTimeout(() => { btn.disabled = false; btn.textContent = '✏️ Actualizar'; btn.className = 'btn btn-green btn-sm'; }, 2500);
  } catch(e) {
    showToast(e.message, 'error');
    btn.disabled = false;
    btn.textContent = '💾 Guardar';
  }
}

// ---- RANKING ----

async function loadRanking() {
  const container = document.getElementById('rankingContainer');
  container.innerHTML = '<div class="loading">Cargando ranking...</div>';
  try {
    const ranking = await api.get('/api/ranking');
    if (!ranking.length) {
      container.innerHTML = '<div class="empty-state"><div class="icon">🏆</div>Todavía no hay puntos acumulados.</div>';
      return;
    }
    const medals = ['🥇', '🥈', '🥉'];
    let rows = ranking.map(r => {
      const medal = medals[r.position - 1] || '';
      const posCls = r.position <= 3 ? `pos-${r.position}` : '';
      const isMe = r.userId === currentUser.id ? 'style="background:#f0fff8"' : '';
      return `<tr ${isMe}>
        <td><span class="${posCls}">${medal || r.position}</span></td>
        <td><strong>${esc(r.name)}</strong>${r.userId === currentUser.id ? ' <span style="font-size:11px;color:var(--green)">(vos)</span>' : ''}</td>
        <td style="text-align:center"><strong style="font-size:16px;color:var(--green)">${r.totalPoints}</strong></td>
        <td style="text-align:center">${r.exactCount}</td>
        <td style="text-align:center">${r.resultCount}</td>
        <td style="text-align:center">${r.predCount}</td>
      </tr>`;
    }).join('');

    container.innerHTML = `
      <table class="ranking-table">
        <thead>
          <tr>
            <th>#</th>
            <th>Jugador</th>
            <th style="text-align:center">Puntos</th>
            <th style="text-align:center">🎯 Exactos</th>
            <th style="text-align:center">✅ Resultado</th>
            <th style="text-align:center">📋 Cargados</th>
          </tr>
        </thead>
        <tbody>${rows}</tbody>
      </table>
      <p style="font-size:12px;color:#999;margin-top:.75rem;text-align:right">
        Puntos: 🎯 Exacto = 3pts · ✅ Resultado = 1pt · ❌ Incorrecto = 0pts
      </p>
    `;
  } catch(e) {
    container.innerHTML = `<div class="alert alert-danger">Error: ${e.message}</div>`;
  }
}

// ---- COMPARAR ----

async function loadUsers() {
  try {
    const users = await api.get('/api/users');
    const sel = document.getElementById('compareSelect');
    if (!users.length) {
      sel.innerHTML = '<option>No hay otros jugadores aún</option>';
      return;
    }
    sel.innerHTML = users.map(u => `<option value="${u.id}">${esc(u.name)}</option>`).join('');
  } catch(e) {}
}

async function loadCompare() {
  const otherId = document.getElementById('compareSelect').value;
  const container = document.getElementById('compareContainer');
  if (!otherId) return;
  container.innerHTML = '<div class="loading">Cargando comparación...</div>';
  try {
    const data = await api.get('/api/compare/' + otherId);
    const otherName = document.getElementById('compareSelect').selectedOptions[0].text;
    renderCompare(data, otherName);
  } catch(e) {
    container.innerHTML = `<div class="alert alert-danger">${e.message}</div>`;
  }
}

async function loadComparePronosticado() {
  const otherId = document.getElementById('compareSelect').value;
  const container = document.getElementById('compareContainer');
  if (!otherId) return;
  container.innerHTML = '<div class="loading">Cargando comparación...</div>';
  try {
    const data = await api.get('/api/comparePronosticado/' + otherId);
    const otherName = document.getElementById('compareSelect').selectedOptions[0].text;
    renderCompare(data, otherName);
  } catch(e) {
    container.innerHTML = `<div class="alert alert-danger">${e.message}</div>`;
  }
}

function renderCompare(data, otherName) {
  const container = document.getElementById('compareContainer');
  if (!data.length) {
    container.innerHTML = '<div class="empty-state"><div class="icon">⏳</div>No hay partidos finalizados aún para comparar.</div>';
    return;
  }

  let myTotal = 0, otherTotal = 0;
  data.forEach(r => { myTotal += r.myPoints || 0; otherTotal += r.otherPoints || 0; });

  const rows = data.map(r => {
    const myPred    = r.myHome !== null && r.myHome !== undefined ? `${r.myHome}-${r.myAway}` : '-';
    const otherPred = r.otherHome !== null ? `${r.otherHome}-${r.otherAway}` : '-';
    const myPts     = r.myPoints !== null ? r.myPoints : '-';
    const otherPts  = r.otherPoints !== null ? r.otherPoints : '-';

    let rowCls = '';
    if (r.myPoints > r.otherPoints)     rowCls = 'win-me';
    else if (r.myPoints < r.otherPoints) rowCls = 'win-other';
    else if (r.myPoints !== null)        rowCls = 'win-tie';

    return `<tr class="${rowCls}">
      <td class="match-col">P.${r.matchId} - ${r.homeFlag}${r.homeTeam} vs ${r.awayFlag}${r.awayTeam}</td>
      <td style="color:var(--green);font-weight:600">${r.realHome}-${r.realAway}</td>
      <td>${myPred}</td>
      <td><span class="result-badge badge-${myPts}">${myPts === 3 ? '🎯 3' : myPts === 1 ? '✅ 1' : myPts === 0 ? '❌ 0' : '-'}</span></td>
      <td>${otherPred}</td>
      <td><span class="result-badge badge-${otherPts}">${otherPts === 3 ? '🎯 3' : otherPts === 1 ? '✅ 1' : otherPts === 0 ? '❌ 0' : '-'}</span></td>
    </tr>`;
  }).join('');

  const winner = myTotal > otherTotal ? '🏆 ¡Ganás vos!' : myTotal < otherTotal ? `🏆 Gana ${esc(otherName)}` : '🤝 Empate';

  container.innerHTML = `
    <div style="display:flex;gap:1rem;margin-bottom:1rem">
      <div class="stat-card" style="flex:1;border-left:4px solid var(--green)">
        <div class="stat-num">${myTotal}</div>
        <div class="stat-lbl">Tus puntos</div>
      </div>
      <div class="stat-card" style="flex:1">
        <div class="stat-num">${otherTotal}</div>
        <div class="stat-lbl">Puntos de ${esc(otherName)}</div>
      </div>
      <div class="stat-card" style="flex:1;background:var(--green-l)">
        <div style="font-size:16px;font-weight:700;color:var(--green);padding-top:.5rem">${winner}</div>
      </div>
    </div>
    <div style="overflow-x:auto">
      <table class="compare-table">
        <thead>
          <tr>
            <th class="match-col">Partido</th>
            <th>Resultado</th>
            <th>Tu pronóstico</th>
            <th>Tus pts</th>
            <th>${esc(otherName)}</th>
            <th>Sus pts</th>
          </tr>
        </thead>
        <tbody>${rows}</tbody>
      </table>
    </div>
    <p style="font-size:12px;color:#bbb;margin-top:.75rem">
      🟢 Ganás el partido · 🔴 Gana ${esc(otherName)} · 🟡 Empate de puntos
    </p>
  `;
}

// ---- ADMIN ----

async function loadAdminMatches() {
  const container = document.getElementById('adminContainer');
  container.innerHTML = '<div class="loading">Cargando partidos...</div>';
  try {
    const matches = await api.get('/api/admin/matches');
    // Agrupar por fase
    const phases = { F1: 'Fecha 1', F2: 'Fecha 2', F3: 'Fecha 3', GROUP: 'Fase de grupos', ROUND_OF_32: 'Dieciseisavos', ROUND_OF_16: 'Octavos', QUARTER: 'Cuartos', SEMI: 'Semifinales', TERCERO: 'Tercero', FINAL: 'Final' };
    const grouped = {};
    matches.forEach(m => {
      if (!grouped[m.phase]) grouped[m.phase] = [];
      grouped[m.phase].push(m);
    });

    let html = '';
    Object.entries(phases).forEach(([phase, label]) => {
      if (!grouped[phase]) return;
      html += `<div class="group-header">${label}</div>`;
      grouped[phase].forEach(m => {
        const finished = m.status === 'FINISHED';
        html += `
        <div class="admin-match-row">
          <div class="admin-match-teams">
            ${m.homeFlag} ${esc(m.homeTeam)} vs ${m.awayFlag} ${esc(m.awayTeam)}
            <div style="font-size:12px;color:#999">${formatDate(m.matchDate)} ${m.matchTime}hs</div>
          </div>
          ${finished
            ? `<span class="result-badge badge-3">✅ ${m.homeScore}-${m.awayScore}</span>`
            : `<div class="admin-score-zone">
                <input class="admin-score-input" type="number" min="0" max="30" id="adh-${m.id}" placeholder="0">
                <span>-</span>
                <input class="admin-score-input" type="number" min="0" max="30" id="ada-${m.id}" placeholder="0">
                <button class="btn btn-green btn-sm" onclick="saveResult(${m.id})">Guardar</button>
               </div>`
          }
        </div>`;
      });
    });
    container.innerHTML = html || '<div class="empty-state">No hay partidos.</div>';
  } catch(e) {
    container.innerHTML = `<div class="alert alert-danger">${e.message}</div>`;
  }
}

async function saveResult(matchId) {
  const home = parseInt(document.getElementById('adh-' + matchId).value);
  const away = parseInt(document.getElementById('ada-' + matchId).value);
  if (isNaN(home) || isNaN(away)) { showToast('Ingresá ambos goles', 'error'); return; }
  try {
    await api.post('/api/admin/result', { matchId, homeScore: home, awayScore: away });
    showToast('Resultado guardado y puntos calculados ✓', 'success');
    loadAdminMatches();
    // Refrescar partidos locales si estamos en la tab de pronósticos
    loadGroupMatches();
  } catch(e) {
    showToast(e.message, 'error');
  }
}

// ---- HELPERS ----

function formatDate(dateStr) {
  if (!dateStr) return '';
  const d = new Date(dateStr + 'T00:00:00');
  return d.toLocaleDateString('es-AR', { day: '2-digit', month: '2-digit' });
}

function formatearDateTime(dateTimeStr) {

  if (!dateTimeStr) return '';
  const fechaTexto = dateTimeStr;
  const fecha = fechaTexto.replace('T', ' ');
  const partes = fecha.split(' ');
  const fechaPartes = partes[0].split('-');
  const fechaFormateada =
    fechaPartes[2] + '/' +
    fechaPartes[1] + '/' +
    fechaPartes[0] + ' ' +
    partes[1];
  return fechaFormateada;
}

function esc(str) {
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

let toastTimer;
function showToast(msg, type = 'success') {
  const el = document.getElementById('toast');
  el.textContent = msg;
  el.className = 'toast show ' + type;
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => el.classList.remove('show'), 3000);
}

async function loadFechaTopePredictions(phase) {
  try {
    if (phase == "GROUP") {
      const url = "/api/predictions/tope/" + phase;
      const fechaTopePrediction = await api.get(url);
      //const url = "/api/predictions/tope/" + phase;
      //const fechaTopePrediction = await api.get(url);
      //const url = "/api/predictions/tope/" + phase;
      //const fechaTopePrediction = await api.get(url);
      document.getElementById('fechaTopeGrupo').innerText = formatearDateTime(fechaTopePrediction.fechaTopePrediction);
    }
  } catch (e) {
    showToast('Error cargar fecha tope: ' + e.message, 'error');
  }
}

function mostrarFechaTopePredictions(phase) {
  try {
    if (phase === "GROUP") {
      const fechaTopeF1 = allFechasTopes.find(f => f.phase === "F1")?.fechaTopePrediction;
      document.getElementById('vencF1').innerText = formatearDateTime(fechaTopeF1);
      //console.log("fechaTopeF1:" + fechaTopeF1);
      const fechaTopeF2 = allFechasTopes.find(f => f.phase === "F2")?.fechaTopePrediction;
      document.getElementById('vencF2').innerText = formatearDateTime(fechaTopeF2);
      const fechaTopeF3 = allFechasTopes.find(f => f.phase === "F3")?.fechaTopePrediction;
      document.getElementById('vencF3').innerText = formatearDateTime(fechaTopeF3);
      //const textoFechaTope = "Fecha 1: " + formatearDateTime(fechaTopeF1) + "  - Fecha 2:" + formatearDateTime(fechaTopeF2) + "  - Fecha 3:" + formatearDateTime(fechaTopeF3);
      //document.getElementById('fechaTopeGrupo').innerText = textoFechaTope;
    }
  } catch (e) {
    showToast('Error cargar fecha tope: ' + e.message, 'error');
  }
}

async function loadFechasTopes() {
  try {
    allFechasTopes = await api.get('/api/predictions/fechasTopes');
    //renderStats();
    //renderMatches();
  } catch (e) {
    showToast('Error cargando las fechas topes: ' + e.message, 'error');
  }
}

function countryCodeToFlag(code) {
  if (!code) return '';
  
  return code
    .toUpperCase()
    .replace(/./g, char =>
      String.fromCodePoint(127397 + char.charCodeAt())
    );
}
