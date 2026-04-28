# Warrior Developer — Android App (build with GitHub Actions)

This is the Android Studio project for the **Warrior Developer** browser bot.
You don't need Android Studio installed — push this folder to GitHub and the included workflow will build the APK for you for free.

## What this app does

1. Shows an "Enter Key" screen with the Warrior Developer logo.
2. Sends `{key, device_id}` to your VPS at `http://80.241.210.193:5001/api/validate`.
3. On success, stores authorization on-device (no key prompt next time on this phone).
4. Opens the SuperSaaS VISA URL in a built-in WebView.
5. Auto-clicks the Cloudflare Turnstile checkbox and auto-fills email + password.
6. Submits the form to `?view=free`.

## Build APK with GitHub Actions (no PC tools needed)

### Step 1 — Create a GitHub repo
1. Go to <https://github.com/new>
2. Name it e.g. `WarriorDeveloper`
3. Keep it Private (recommended)
4. Click **Create repository**

### Step 2 — Upload this folder
**Option A — drag & drop in browser:**
1. On your new empty repo page click **uploading an existing file**.
2. Drag the entire contents of this `WarriorDeveloper` folder (NOT the folder itself, the files inside).
3. Click **Commit changes**.

**Option B — Git CLI:**
```bash
cd WarriorDeveloper
git init
git add .
git commit -m "Initial Warrior Developer app"
git branch -M main
git remote add origin https://github.com/<YOUR_USERNAME>/WarriorDeveloper.git
git push -u origin main
```

### Step 3 — Watch the build
1. Go to the **Actions** tab in your repo.
2. You will see a workflow run named **"Build Warrior Developer APK"** that started automatically.
3. Wait ~3–5 minutes for it to finish (green checkmark).

### Step 4 — Download the APK
1. Click the finished workflow run.
2. Scroll down to **Artifacts**.
3. Download **WarriorDeveloper-debug** (a ZIP).
4. Unzip → you get `WarriorDeveloper-debug.apk`.
5. Copy the APK to your phone, install it.

That's it. Every time you push a code change, GitHub re-builds the APK for free.

## Build a tagged Release (auto-published)

```bash
git tag v1.0
git push origin v1.0
```
The workflow detects the `v*` tag and creates a GitHub Release with both debug + release APKs attached.

## Build locally (optional)
If you do have Android Studio or just JDK 17 + Android SDK:
```bash
chmod +x gradlew
./gradlew assembleDebug
```
APK ends up in `app/build/outputs/apk/debug/app-debug.apk`.

## Change the VPS IP later
Edit `app/src/main/java/com/warrior/developer/KeyActivity.java`:
```java
public static final String VPS_BASE = "http://80.241.210.193:5001";
```
Commit & push → GitHub Actions rebuilds the APK automatically.

## Troubleshooting
- **APK installs but says "Connection failed"** → make sure VPS is up and port 5001 is open (`ufw allow 5001/tcp`).
- **Workflow fails on first push** → open Actions tab, click the failed run, read the red step. Most common cause is a typo if you edited files manually. Just push again.
- **Cloudflare page stays loading** → that's fine; the injected JS clicks the checkbox. Wait 5–10 seconds.
