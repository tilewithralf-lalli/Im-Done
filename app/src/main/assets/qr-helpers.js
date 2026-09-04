/* Family QR helpers */
(() => {
  const appleJoinUrl = 'https://ne-kids.tilewithralf.chatgpt.site/?code=';
  const qrImage = payload => 'https://api.qrserver.com/v1/create-qr-code/?size=240x240&format=png&data=' + encodeURIComponent(payload);
  function readFamilyCode() {
    const el = document.getElementById('familyCode');
    const raw = String(window.familyCode || (el ? (el.textContent || el.innerText || '') : ''));
    const digits = raw.replace(/\D/g, '');
    return digits.length >= 6 ? digits.slice(-6) : '';
  }
  window.toggleFamilyQr = function () {
    const box = document.getElementById('familyQr');
    const code = readFamilyCode();
    if (!code) return window.toast('Family code is still loading');
    if (!box.hidden) { box.hidden = true; box.innerHTML = ''; return; }
    box.innerHTML = '<div style="display:grid;gap:10px"><button class="small-btn" onclick="showFamilyQrCode(\\'android\\',\\'' + code + '\\')">🤖 ANDROID PHONE / TABLET QR</button><button class="small-btn" onclick="showFamilyQrCode(\\'apple\\',\\'' + code + '\\')">🍎 iPAD / iPHONE QR</button></div><div id="chosenFamilyQr" style="text-align:center;margin-top:12px"></div>';
    box.hidden = false;
  };
  window.showFamilyQrCode = function (kind, code) {
    const apple = kind === 'apple';
    const payload = apple ? appleJoinUrl + code : 'imdone://join?code=' + code;
    const title = apple ? 'iPad / iPhone' : 'Android phone / tablet';
    const note = apple ? 'Scan with the iPad camera. It opens the family web app and fills in the family code.' : 'Scan with the installed I’M DONE Android app, or the device camera.';
    document.getElementById('chosenFamilyQr').innerHTML = '<h3 style="margin:8px 0">' + title + '</h3><img alt="I\'M DONE ' + title + ' family join QR code" width="240" height="240" src="' + qrImage(payload) + '"><p class="muted">' + note + '</p>';
  };
})();
