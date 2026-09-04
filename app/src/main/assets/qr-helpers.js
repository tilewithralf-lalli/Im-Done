/* Separate Android and iPad QR sections */
(() => {
  const appleJoinUrl = 'https://ne-kids.tilewithralf.chatgpt.site/?code=';
  const qrImage = payload => 'https://api.qrserver.com/v1/create-qr-code/?size=300x300&format=png&data=' + encodeURIComponent(payload);
  function readFamilyCode() {
    const el = document.getElementById('familyCode');
    const raw = String(window.familyCode || (el ? (el.textContent || el.innerText || '') : ''));
    const digits = raw.replace(/\\D/g, '');
    return digits.length >= 6 ? digits.slice(-6) : '';
  }
  window.toggleFamilyQr = function () {
    const box = document.getElementById('familyQr');
    const code = readFamilyCode();
    if (!code) return window.toast('Family code is still loading');
    if (!box.hidden) { box.hidden = true; box.innerHTML = ''; return; }
    box.innerHTML = '<div style="display:grid;gap:18px;margin-top:14px">' +
      '<div style="background:#eef0ff;border-radius:18px;padding:18px;text-align:center"><h3 style="margin:0 0 10px">🤖 ANDROID</h3><p class="muted">Use this QR only for Android phones or tablets.</p><button class="big-btn" onclick="showFamilyQrCode(\\'android\\',\\'' + code + '\\')">SHOW ANDROID QR</button></div>' +
      '<div style="background:#fff4d6;border-radius:18px;padding:18px;text-align:center"><h3 style="margin:0 0 10px">🍎 iPAD / iPHONE</h3><p class="muted">Use this QR only for iPad or iPhone.</p><button class="big-btn" onclick="showFamilyQrCode(\\'apple\\',\\'' + code + '\\')">SHOW iPAD QR</button></div>' +
      '</div><div id="chosenFamilyQr" style="text-align:center;margin-top:22px"></div>';
    box.hidden = false;
  };
  window.showFamilyQrCode = function (kind, code) {
    const apple = kind === 'apple';
    const payload = apple ? appleJoinUrl + code : 'imdone://join?code=' + code;
    const title = apple ? 'iPad / iPhone QR' : 'Android QR';
    const note = apple ? 'Scan this QR with the iPad camera.' : 'Scan this QR with the Android app or camera.';
    document.getElementById('chosenFamilyQr').innerHTML = '<div style="background:white;border:4px solid ' + (apple ? '#e5b800' : '#4020d0') + ';border-radius:22px;padding:18px"><h2 style="margin:0 0 12px">' + title + '</h2><img alt="' + title + '" width="300" height="300" src="' + qrImage(payload) + '"><p class="muted">' + note + '</p></div>';
  };
})();
