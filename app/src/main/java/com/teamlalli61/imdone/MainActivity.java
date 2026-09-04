package com.teamlalli61.imdone;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    private static final String API_KEY = "AIzaSyD7wVjCinzQW-zRhEVrlOSg5oButqzqYVw";
    private static final String PROJECT_ID = "im-done-eafd3";
    private static final String PREFS = "im_done_device";
    private static final String CHANNEL_ID = "chores";
    private static final String FIRESTORE = "https://firestore.googleapis.com/v1/projects/"
            + PROJECT_ID + "/databases/(default)/documents/";

    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());
    private WebView webView;
    private SharedPreferences prefs;
    private boolean polling;

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        createNotificationChannel();
        requestNotificationPermission();
        webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.setVerticalScrollBarEnabled(false);
        webView.addJavascriptInterface(new Bridge(), "ImDone");
        setContentView(webView);
        webView.loadUrl("file:///android_asset/index.html");
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Chore reminders", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Reminders for today's chores");
            channel.enableVibration(true);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
        }
    }

    private void runJs(String javascript) {
        main.post(() -> webView.evaluateJavascript(javascript, null));
    }

    private void callJs(String function, Object... values) {
        StringBuilder javascript = new StringBuilder("window.").append(function).append("(");
        for (int index = 0; index < values.length; index++) {
            if (index > 0) javascript.append(',');
            Object value = values[index];
            if (value instanceof Boolean || value instanceof Number) javascript.append(value);
            else javascript.append(JSONObject.quote(value == null ? "" : String.valueOf(value)));
        }
        runJs(javascript.append(')').toString());
    }

    private String now() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        format.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        return format.format(new Date());
    }

    private JSONObject stringField(String value) throws Exception {
        return new JSONObject().put("stringValue", value == null ? "" : value);
    }

    private JSONObject timestampField() throws Exception {
        return new JSONObject().put("timestampValue", now());
    }

    private JSONObject document(JSONObject fields) throws Exception {
        return new JSONObject().put("fields", fields);
    }

    private String field(JSONObject document, String name) {
        return document.optJSONObject("fields") == null ? ""
                : document.optJSONObject("fields").optJSONObject(name) == null ? ""
                : document.optJSONObject("fields").optJSONObject(name).optString("stringValue", "");
    }

    private JSONObject request(String method, String address, String body, String token,
                               String contentType) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(address).openConnection();
        connection.setRequestMethod(method);
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(20000);
        connection.setRequestProperty("Accept", "application/json");
        if (token != null && !token.isEmpty())
            connection.setRequestProperty("Authorization", "Bearer " + token);
        if (body != null) {
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", contentType);
            try (OutputStream output = connection.getOutputStream()) {
                output.write(body.getBytes(StandardCharsets.UTF_8));
            }
        }
        int status = connection.getResponseCode();
        InputStream stream = status >= 200 && status < 300
                ? connection.getInputStream() : connection.getErrorStream();
        StringBuilder response = new StringBuilder();
        if (stream != null) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) response.append(line);
            }
        }
        JSONObject json = response.length() == 0 ? new JSONObject() : new JSONObject(response.toString());
        if (status < 200 || status >= 300) {
            String message = json.optJSONObject("error") == null ? "Connection failed"
                    : json.optJSONObject("error").optString("message", "Connection failed");
            throw new Exception(message);
        }
        return json;
    }

    private JSONObject postJson(String address, JSONObject body, String token) throws Exception {
        return request("POST", address, body.toString(), token, "application/json; charset=utf-8");
    }

    private JSONObject patchJson(String path, JSONObject body, String masks) throws Exception {
        String address = FIRESTORE + path + (masks == null ? "" : "?" + masks);
        return request("PATCH", address, body.toString(), validToken(),
                "application/json; charset=utf-8");
    }

    private JSONObject getDocument(String path) throws Exception {
        return request("GET", FIRESTORE + path, null, validToken(), "application/json");
    }

    private void saveTokens(JSONObject response) {
        long seconds = response.optLong("expiresIn", response.optLong("expires_in", 3600));
        String idToken = response.optString("idToken", response.optString("id_token", ""));
        String refreshToken = response.optString("refreshToken", response.optString("refresh_token", ""));
        String userId = response.optString("localId", response.optString("user_id", ""));
        prefs.edit().putString("idToken", idToken).putString("refreshToken", refreshToken)
                .putString("userId", userId)
                .putLong("expiresAt", System.currentTimeMillis() + seconds * 1000L).apply();
    }

    private String validToken() throws Exception {
        String token = prefs.getString("idToken", "");
        if (!token.isEmpty() && prefs.getLong("expiresAt", 0) > System.currentTimeMillis() + 120000)
            return token;
        String refresh = prefs.getString("refreshToken", "");
        if (refresh.isEmpty()) throw new Exception("Please sign in again.");
        String body = "grant_type=refresh_token&refresh_token="
                + URLEncoder.encode(refresh, "UTF-8");
        JSONObject result = request("POST", "https://securetoken.googleapis.com/v1/token?key="
                        + API_KEY, body, null, "application/x-www-form-urlencoded");
        saveTokens(result);
        return prefs.getString("idToken", "");
    }

    private String friendlyError(Exception error) {
        String message = error.getMessage() == null ? "Something went wrong." : error.getMessage();
        if (message.contains("INVALID_LOGIN_CREDENTIALS") || message.contains("INVALID_PASSWORD"))
            return "Wrong email or password.";
        if (message.contains("EMAIL_EXISTS")) return "That family account already exists.";
        if (message.contains("WEAK_PASSWORD")) return "Password needs at least 6 characters.";
        if (message.contains("EMAIL_NOT_FOUND")) return "That family account was not found.";
        if (message.contains("PERMISSION_DENIED")) return "The family connection was refused.";
        if (message.contains("Unable to resolve host") || message.contains("timed out"))
            return "No internet right now. Your changes are saved on this device.";
        return message.replace('_', ' ');
    }

    private JSONObject authenticate(String action, String email, String password) throws Exception {
        JSONObject body = new JSONObject().put("returnSecureToken", true);
        if (email != null) body.put("email", email.trim());
        if (password != null) body.put("password", password);
        JSONObject response = postJson("https://identitytoolkit.googleapis.com/v1/accounts:"
                + action + "?key=" + API_KEY, body, null);
        saveTokens(response);
        return response;
    }

    private void createFamily(String email, String initialState) throws Exception {
        String userId = prefs.getString("userId", "");
        String code = String.format(Locale.US, "%06d", 100000 + new Random().nextInt(900000));
        JSONObject familyFields = new JSONObject()
                .put("ownerUid", stringField(userId))
                .put("familyEmail", stringField(email))
                .put("familyCode", stringField(code))
                .put("state", stringField(initialState))
                .put("updatedAt", timestampField());
        patchJson("families/" + userId, document(familyFields), null);
        JSONObject codeFields = new JSONObject()
                .put("familyId", stringField(userId))
                .put("ownerUid", stringField(userId))
                .put("createdAt", timestampField());
        patchJson("familyCodes/" + code, document(codeFields), null);
        prefs.edit().putString("role", "parent").putString("familyId", userId)
                .putString("familyCode", code).remove("childId").apply();
        callJs("onSession", "parent", email, code, userId, "");
    }

    private void loadParentSession(String email) throws Exception {
        String userId = prefs.getString("userId", "");
        JSONObject family = getDocument("families/" + userId);
        String code = field(family, "familyCode");
        prefs.edit().putString("role", "parent").putString("familyId", userId)
                .putString("familyCode", code).remove("childId").apply();
        callJs("onSession", "parent", email, code, userId, "");
    }

    private void uploadState(String state) throws Exception {
        String familyId = prefs.getString("familyId", "");
        if (familyId.isEmpty()) throw new Exception("Family is not connected.");
        JSONObject fields = new JSONObject().put("state", stringField(state))
                .put("updatedAt", timestampField());
        patchJson("families/" + familyId, document(fields),
                "updateMask.fieldPaths=state&updateMask.fieldPaths=updatedAt");
    }

    private void pollFamily() {
        if (!polling) return;
        io.execute(() -> {
            try {
                String pending = prefs.getString("pendingState", "");
                if (!pending.isEmpty()) {
                    uploadState(pending);
                    if (pending.equals(prefs.getString("pendingState", "")))
                        prefs.edit().remove("pendingState").apply();
                }
                String familyId = prefs.getString("familyId", "");
                if (!familyId.isEmpty()) {
                    String state = field(getDocument("families/" + familyId), "state");
                    if (!state.isEmpty()) callJs("receiveCloudState", state);
                }
            } catch (Exception ignored) {
                // Offline is normal. Local data remains saved and is retried on the next poll.
            }
            main.postDelayed(this::pollFamily, 3500);
        });
    }

    public class Bridge {
        @JavascriptInterface
        public void checkAuth() {
            io.execute(() -> {
                String role = prefs.getString("role", "");
                if (role.isEmpty() || prefs.getString("refreshToken", "").isEmpty()) {
                    callJs("onSignedOut");
                    return;
                }
                try {
                    validToken();
                    callJs("onSession", role, prefs.getString("email", ""),
                            prefs.getString("familyCode", ""), prefs.getString("familyId", ""),
                            prefs.getString("childId", ""));
                } catch (Exception error) {
                    callJs("onLoginError", friendlyError(error));
                }
            });
        }

        @JavascriptInterface
        public void createAccount(String email, String password, String initialState) {
            io.execute(() -> {
                try {
                    authenticate("signUp", email, password);
                    prefs.edit().putString("email", email.trim()).apply();
                    createFamily(email.trim(), initialState);
                } catch (Exception error) {
                    callJs("onLoginError", friendlyError(error));
                }
            });
        }

        @JavascriptInterface
        public void signIn(String email, String password) {
            io.execute(() -> {
                try {
                    authenticate("signInWithPassword", email, password);
                    prefs.edit().putString("email", email.trim()).apply();
                    loadParentSession(email.trim());
                } catch (Exception error) {
                    callJs("onLoginError", friendlyError(error));
                }
            });
        }

        @JavascriptInterface
        public void updateAccountDetails(String familyName, String newEmail,
                                         String currentPassword, String newPassword,
                                         String currentState) {
            io.execute(() -> {
                try {
                    String oldEmail = prefs.getString("email", "");
                    if (oldEmail.isEmpty()) throw new Exception("Please sign in again.");
                    if (currentPassword == null || currentPassword.length() < 6)
                        throw new Exception("Enter the current password.");
                    authenticate("signInWithPassword", oldEmail, currentPassword);

                    JSONObject update = new JSONObject()
                            .put("idToken", validToken())
                            .put("returnSecureToken", true)
                            .put("email", newEmail.trim());
                    if (newPassword != null && !newPassword.isEmpty()) {
                        if (newPassword.length() < 6)
                            throw new Exception("New password needs at least 6 characters.");
                        update.put("password", newPassword);
                    }
                    JSONObject response = postJson(
                            "https://identitytoolkit.googleapis.com/v1/accounts:update?key="
                                    + API_KEY, update, null);
                    saveTokens(response);
                    String savedEmail = response.optString("email", newEmail.trim());
                    prefs.edit().putString("email", savedEmail).apply();

                    String familyId = prefs.getString("familyId", "");
                    JSONObject fields = new JSONObject()
                            .put("familyEmail", stringField(savedEmail))
                            .put("familyName", stringField(familyName.trim()))
                            .put("state", stringField(currentState))
                            .put("updatedAt", timestampField());
                    patchJson("families/" + familyId, document(fields),
                            "updateMask.fieldPaths=familyEmail&updateMask.fieldPaths=familyName"
                                    + "&updateMask.fieldPaths=state&updateMask.fieldPaths=updatedAt");
                    callJs("onAccountUpdated", savedEmail, familyName.trim());
                } catch (Exception error) {
                    callJs("onAccountUpdateError", friendlyError(error));
                }
            });
        }

        @JavascriptInterface
        public void sendPasswordResetEmail() {
            io.execute(() -> {
                try {
                    String email = prefs.getString("email", "");
                    if (email.isEmpty()) throw new Exception("No account email is connected.");
                    JSONObject body = new JSONObject()
                            .put("requestType", "PASSWORD_RESET")
                            .put("email", email);
                    postJson("https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode?key="
                            + API_KEY, body, null);
                    callJs("onPasswordResetSent");
                } catch (Exception error) {
                    callJs("onAccountUpdateError", friendlyError(error));
                }
            });
        }

        @JavascriptInterface
        public void beginChildJoin(String familyCode) {
            io.execute(() -> {
                String code = familyCode.replaceAll("\\D", "");
                try {
                    if (code.length() != 6) throw new Exception("Enter the 6-number family code.");
                    authenticate("signUp", null, null);
                    String userId = prefs.getString("userId", "");
                    String familyId = field(getDocument("familyCodes/" + code), "familyId");
                    if (familyId.isEmpty()) throw new Exception("That family code was not found.");
                    JSONObject memberFields = new JSONObject()
                            .put("uid", stringField(userId))
                            .put("familyCode", stringField(code))
                            .put("childId", stringField(""))
                            .put("joinedAt", timestampField());
                    patchJson("families/" + familyId + "/members/" + userId,
                            document(memberFields), null);
                    String state = field(getDocument("families/" + familyId), "state");
                    callJs("onJoinChoices", familyId, code, state);
                } catch (Exception error) {
                    callJs("onJoinError", friendlyError(error));
                }
            });
        }

        @JavascriptInterface
        public void finishChildJoin(String familyId, String familyCode, String childId) {
            io.execute(() -> {
                try {
                    String userId = prefs.getString("userId", "");
                    JSONObject fields = new JSONObject().put("childId", stringField(childId))
                            .put("linkedAt", timestampField());
                    patchJson("families/" + familyId + "/members/" + userId,
                            document(fields), "updateMask.fieldPaths=childId&updateMask.fieldPaths=linkedAt");
                    prefs.edit().putString("role", "child").putString("familyId", familyId)
                            .putString("familyCode", familyCode).putString("childId", childId)
                            .remove("email").apply();
                    callJs("onSession", "child", "", familyCode, familyId, childId);
                } catch (Exception error) {
                    callJs("onJoinError", friendlyError(error));
                }
            });
        }

        @JavascriptInterface
        public void startSync(String localState) {
            polling = false;
            io.execute(() -> {
                try {
                    String familyId = prefs.getString("familyId", "");
                    String state = field(getDocument("families/" + familyId), "state");
                    if (state.isEmpty()) uploadState(localState);
                    else callJs("receiveCloudState", state);
                } catch (Exception error) {
                    prefs.edit().putString("pendingState", localState).apply();
                    callJs("cloudError", friendlyError(error));
                }
                polling = true;
                main.postDelayed(MainActivity.this::pollFamily, 1000);
            });
        }

        @JavascriptInterface
        public void saveState(String json) {
            prefs.edit().putString("pendingState", json).apply();
            io.execute(() -> {
                try {
                    uploadState(json);
                    if (json.equals(prefs.getString("pendingState", "")))
                        prefs.edit().remove("pendingState").apply();
                } catch (Exception error) {
                    callJs("cloudError", friendlyError(error));
                }
            });
        }

        @JavascriptInterface
        public void signOutDevice() {
            polling = false;
            prefs.edit().clear().apply();
            callJs("onSignedOut");
        }

        @JavascriptInterface
        public void scheduleChores(String json) {
            try {
                JSONArray chores = new JSONArray(json);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                for (int index = 0; index < chores.length(); index++)
                    scheduleDailyAlarm(alarmManager, chores.getJSONObject(index));
            } catch (Exception error) {
                callJs("cloudError", "A reminder time could not be saved.");
            }
        }

        @JavascriptInterface
        public void snooze(String choreId, String title, int minutes) {
            int safeMinutes = Math.max(1, Math.min(minutes, 120));
            Intent intent = reminderIntent(choreId + "-snooze", title);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    MainActivity.this, (choreId + "-snooze").hashCode() & 0x7fffffff,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            ((AlarmManager) getSystemService(ALARM_SERVICE)).setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + safeMinutes * 60_000L, pendingIntent);
        }
    }

    private Intent reminderIntent(String choreId, String title) {
        return new Intent(this, ChoreNotificationReceiver.class)
                .putExtra("choreId", choreId).putExtra("title", title);
    }

    private void scheduleDailyAlarm(AlarmManager alarmManager, JSONObject chore) throws Exception {
        String choreId = chore.optString("id",
                String.valueOf(chore.optString("title", "chore").hashCode()));
        String title = chore.optString("title", "your chore");
        String time = chore.optString("time", "4:00 PM");
        Date parsed = new SimpleDateFormat("h:mm a", Locale.US).parse(time.toUpperCase(Locale.US));
        if (parsed == null) return;
        Calendar parsedTime = Calendar.getInstance();
        parsedTime.setTime(parsed);
        Calendar due = Calendar.getInstance();
        due.set(Calendar.HOUR_OF_DAY, parsedTime.get(Calendar.HOUR_OF_DAY));
        due.set(Calendar.MINUTE, parsedTime.get(Calendar.MINUTE));
        due.set(Calendar.SECOND, 0);
        due.set(Calendar.MILLISECOND, 0);
        if (due.getTimeInMillis() <= System.currentTimeMillis()) due.add(Calendar.DAY_OF_YEAR, 1);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, choreId.hashCode() & 0x7fffffff, reminderIntent(choreId, title),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, due.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        polling = false;
        io.shutdownNow();
        super.onDestroy();
    }
}
