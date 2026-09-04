/* Android and iPad folders before QR codes */
(() => {
  const appleJoinUrl = 'https://ne-kids.tilewithralf.chatgpt.site/?code=';
  const qrImage = payload => 'https://api.qrserver.com/v1/create-qr-code/?size=300x300&format=png&data=' + encodeURIComponent(payload);
  function readFamilyCode() {
    const el = document.getElementById('familyCode');
    const raw = String(window.familyCode || (el ? (el.textContent || el.innerText || '') : ''));
    const digits = raw.replace(/\\D/g, '');
    return digits.length >= 6 ? digits.slice(-6) : '';
  }
  function show(kind, code, target) {
    const apple = kind === 'apple';
    const payload = apple ? appleJoinUrl + code : 'imdone://join?code=' + code;
    const title = apple ? '🍎 iPAD / iPHONE QR' : '🤖 ANDROID QR';
    const note = apple ? 'Scan this QR with the iPad camera.' : 'Scan this QR with the Android camera or I’M DONE scanner.';
    document.getElementById(target).innerHTML = '<div style="background:#fff;border:4px solid ' + (apple ? '#e5b800' : '#4020d0') + ';border-radius:22px;padding:18px;margin-top:14px;text-align:center"><h2 style="margin:0 0 14px">' + title + '</h2><img alt="' + title + '" width="300" height="300" style="display:block;margin:auto;max-width:100%;height:auto" src="' + qrImage(payload) + '"><p class="muted">' + note + '</p></div>';
  }
  window.toggleFamilyQr = function () {
    const box = document.getElementById('familyQr');
    const code = readFamilyCode();
    if (!code) return window.toast('Family code is still loading');
    if (!box.hidden) { box.hidden = true; box.innerHTML = ''; return; }
    box.style.width = '100%'; box.style.display = 'block';
    box.innerHTML = '<div style="display:block;width:100%;padding-top:16px">' +
      '<button class="big-btn" style="width:100%;margin-bottom:14px" onclick="showFamilyQrFolder(\\'android\\',\\'' + code + '\\')">📁 ANDROID FOLDER</button>' +
      '<div id="androidQrFolder" style="width:100%;margin-bottom:22px"></div>' +
      '<button class="big-btn" style="width:100%;margin-bottom:14px" onclick="showFamilyQrFolder(\\'apple\\',\\'' + code + '\\')">📁 iPAD / iPHONE FOLDER</button>' +
      '<div id="appleQrFolder" style="width:100%"></div></div>';
    box.hidden = false;
  };
  window.showFamilyQrFolder = function(kind, code) {
    const target = kind === 'apple' ? 'appleQrFolder' : 'androidQrFolder';
    const other = kind === 'apple' ? 'androidQrFolder' : 'appleQrFolder';
    document.getElementById(other).innerHTML = '';
    show(kind, code, target);
  };
})();
