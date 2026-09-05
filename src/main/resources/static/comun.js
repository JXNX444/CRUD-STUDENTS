// ==== Funciones comunes a todos los modulos ====

// Envuelve fetch: si el token de acceso vencio (401), lo renueva con el
// refresh token y reintenta una vez. Si no se puede, manda al login.
async function apiFetch(url, options = {}){
    options.credentials = 'same-origin';
    let res = await fetch(url, options);
    if(res.status === 401){
        const r = await fetch('/api/auth/refresh', { method:'POST', credentials:'same-origin' });
        if(r.ok){ res = await fetch(url, options); }
        else { window.location.href = '/login.html'; throw new Error('Sesion expirada'); }
    }
    return res;
}

// Cierra la sesion: borra las cookies en el servidor y vuelve al login.
async function cerrarSesion(){
    try{ await fetch('/api/auth/logout', { method:'POST', credentials:'same-origin' }); }catch(e){}
    window.location.href = '/login.html';
}

// Notificacion emergente (toast)
let toastTimer;
function toast(msg, esError=false){
    const t = document.getElementById('toast');
    if(!t) return;
    document.getElementById('toastMsg').textContent = msg;
    t.classList.toggle('error', esError);
    t.querySelector('.check').textContent = esError ? '!' : '\u2713';
    t.classList.add('show');
    clearTimeout(toastTimer);
    toastTimer = setTimeout(()=>t.classList.remove('show'), 3200);
}

function val(id){ return document.getElementById(id).value.trim(); }
function escapar(s){ return String(s).replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c])); }

// Marca el link activo del menu segun la pagina actual
document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.sidebar-nav a').forEach(a => {
        if(a.getAttribute('href') === location.pathname) a.classList.add('activo');
    });
});