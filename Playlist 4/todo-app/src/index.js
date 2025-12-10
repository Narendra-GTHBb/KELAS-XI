import React from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import App from "./App";
import checkEnv from "./firebase/checkEnv";

// Run a lightweight env check in development to surface missing Firebase keys
checkEnv();

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
