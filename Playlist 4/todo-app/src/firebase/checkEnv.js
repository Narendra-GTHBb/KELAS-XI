// Simple runtime check for required REACT_APP_ env vars.
// This runs in the browser console (development) to help developers
// remember to set the .env values copied from Firebase Console.

const requiredVars = [
  "REACT_APP_API_KEY",
  "REACT_APP_AUTH_DOMAIN",
  "REACT_APP_DATABASE_URL",
  "REACT_APP_PROJECT_ID",
  "REACT_APP_APP_ID",
];

export default function checkEnv() {
  if (process.env.NODE_ENV === "production") return;

  const missing = requiredVars.filter((name) => !process.env[name]);
  if (missing.length > 0) {
    // Friendly developer console warning
    // eslint-disable-next-line no-console
    console.warn(
      "[Firebase Env] Missing environment variables:",
      missing.join(", ")
    );
    // Also provide actionable hint
    // eslint-disable-next-line no-console
    console.info(
      "Copy `.env.example` to `.env` and fill in your Firebase values. Then restart the dev server."
    );
  } else {
    // eslint-disable-next-line no-console
    console.log(
      "[Firebase Env] All required env vars present (or running in production)."
    );
  }
}
