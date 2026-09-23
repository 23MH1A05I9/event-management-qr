# DevOps Setup — event-management-qr

No Docker anywhere in this pipeline. Stack: GitHub Actions (CI) + Jenkins (build/test/deploy) + Railway (hosting + MySQL).

---

## 0. Rotate your secrets first

Your uploaded project had a real MySQL password and a real Gmail app password hardcoded in
`application.properties`. Both are now removed from the file and replaced with environment
variables — but the old ones are still "live" until you change them:

- Change the MySQL `root` password (or better, create a dedicated non-root DB user for this app).
- Go to your Google Account → Security → App Passwords, revoke `etlegnaitxtgdeci`, and generate a new one.

Do this before pushing this code anywhere public.

---

## 1. Local development

```
copy .env.example .env
```

Fill in `.env` with your real local MySQL/Gmail values. Since Spring Boot doesn't read `.env`
files natively, either:
- set these as actual Windows environment variables before running, or
- pass them as JVM args: `mvnw.cmd spring-boot:run -DDB_PASSWORD=... -DMAIL_PASSWORD=...`

`.env` is gitignored — it will never be committed.

---

## 2. GitHub Actions (CI)

Already set up at `.github/workflows/ci.yml`. It runs automatically on every push/PR to `main`:
builds with Maven, runs tests, uploads the `.jar` as a build artifact. Nothing to configure —
just push to GitHub and check the **Actions** tab.

---

## 3. Railway (hosting + database) — replaces Render

Render has no native Java support (it requires a Dockerfile), so this project deploys to
**Railway**, which builds Java/Maven projects directly — no Dockerfile, no local Docker.

1. Create a Railway account → **New Project** → **Deploy from GitHub repo** → select this repo.
   Railway auto-detects it's a Maven project via Nixpacks and builds it.
2. In the same project, click **+ New** → **Database** → **Add MySQL**. Railway provisions a
   managed MySQL instance and gives you connection variables.
3. On your app service, go to **Variables** and add:
   - `DB_URL` = `jdbc:mysql://<railway-mysql-host>:<port>/<db-name>?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true`
   - `DB_USERNAME`, `DB_PASSWORD` — copy these from the MySQL service's own Variables tab
   - `MAIL_USERNAME`, `MAIL_PASSWORD` — your new Gmail address / app password
   - `NIXPACKS_JDK_VERSION` = `17` (matches this project's Java version)
4. Railway sets `PORT` automatically — the app already reads it (`server.port=${PORT:8080}`).
5. Generate a Railway API token: **Account Settings → Tokens → Create Token**. You'll need this
   for Jenkins in the next step.

---

## 4. Jenkins (build, test, deploy)

One-time setup:

1. Install **Node.js** on the machine running Jenkins (needed for the Railway CLI). Confirm with
   `node -v` in the same environment Jenkins runs in.
2. In Jenkins: **Manage Jenkins → Credentials → System → Global credentials → Add Credentials**
   - Kind: `Secret text`
   - Secret: your Railway API token from step 3.5 above
   - ID: `railway-token` (must match exactly — the Jenkinsfile references this ID)
3. **New Item → Pipeline** → under **Pipeline**, set "Definition" to *Pipeline script from SCM*,
   point it at this repo, branch `main`, script path `Jenkinsfile`.
4. Run the job. Stages: Checkout → Build → Test → Deploy to Railway (deploy only runs on `main`).

Why two secrets stores: Jenkins' credential store secures the token used *during the pipeline*
(the Railway deploy token). The app's own runtime secrets (DB/mail passwords) live in Railway's
dashboard, because that's where the app actually runs — Jenkins has no way to inject env vars
into Railway's running process.

---

## 5. Division of labor

| Tool | Job |
|---|---|
| GitHub Actions | Fast CI feedback on every push/PR — build + test only |
| Jenkins | Full pipeline — build, test, and deploy to Railway on `main` |
| Railway | Hosts the app + managed MySQL, no Docker involved |

---

## 6. Suggested next steps (optional, not built yet)

- Add Spring Boot Actuator (`spring-boot-starter-actuator`) for a `/actuator/health` endpoint —
  useful for uptime checks.
- Add a `CHANGELOG.md` or use GitHub Releases tied to tags for versioning.
