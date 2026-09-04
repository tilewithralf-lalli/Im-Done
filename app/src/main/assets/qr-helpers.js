/* Separate Android and iPad QR sections - vertical layout */
(() => {
  const appleJoinUrl = 'https://ne-kids.tilewithralf.chatgpt.site/?code=';
  const qrImage = payload => 'https://api.qrserver.com/v1/create-qr-code/?size=300x300&format=png&data=' + encodeURIComponent(payload);
  function readFamilyCode() {
    const el = document.getElementById('familyCode');
    const raw = String(window.familyCode || (el ? (el.textContent || el.innerText || '') : ''));
    const digits = raw.replace(/\\D/g, '');
    return digits.length >= 6 ? digits.slice(-6) : '';
  }
  function qrCard(kind, code) {
    const apple = kind === 'apple';
    const payload = apple ? appleJoinUrl + code : 'imdone://join?code=' + code;
    const title = apple ? '🍎 iPAD / iPHONE QR' : '🤖 ANDROID QR';
    const note = apple ? 'Scan this QR with the iPad camera.' : 'Scan this QR with the Android camera or I’M DONE scanner.';
    return '<div style="width:100%;box-sizing:border-box;background:#fff;border:4px solid ' + (apple ? '#e5b800' : '#4020d0') + ';border-radius:22px;padding:18px;margin:0 0 24px;text-align:center;display:block"><h2 style="margin:0 0 14px">' + title + '</h2><img alt="' + title + '" width="300" height="300" style="display:block;margin:0 auto;max-width:100%;height:auto" src="' + qrImage(payload) + '"><p class="muted" style="margin:14px 0 0">' + note + '</p></div>';
  }
  window.toggleFamilyQr = function () {
    const box = document.getElementById('familyQr');
    const code = readFamilyCode();
    if (!code) return window.toast('Family code is still loading');
    if (!box.hidden) { box.hidden = true; box.innerHTML = ''; return; }
    box.style.display = 'block'; box.style.width = '100%'; box.innerHTML = '<div style="display:block;width:100%;padding-top:18px">' + qrCard('android', code) + qrCard('apple', code) + '</div>';
    box.hidden = false;
  };
})();
