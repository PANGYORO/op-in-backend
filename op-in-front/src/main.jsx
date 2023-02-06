import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom";

import "./index.css";
import { RecoilRoot } from "recoil";

import Main from "./pages/Main";

ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <RecoilRoot>
      <BrowserRouter>
        {/* <RouterProvider router={pages} /> */}
        <Main />
      </BrowserRouter>
    </RecoilRoot>
  </React.StrictMode>
);
