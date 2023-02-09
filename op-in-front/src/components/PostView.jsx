import React, { useState, useEffect } from "react";
import Prism from "prismjs";
import http from "@api/http";
import RepoPostDetail from './repository/RepoPostDetail';
import { useLocation } from "react-router-dom";
// 여기 css를 수정해서 코드 하이라이팅 커스텀 가능
import "prismjs/themes/prism.css";
import Status from './repository/Status';

import "@toast-ui/editor/dist/toastui-editor.css";
import { Viewer } from "@toast-ui/react-editor";

import "@toast-ui/editor-plugin-code-syntax-highlight/dist/toastui-editor-plugin-code-syntax-highlight.css";
import codeSyntaxHighlight from "@toast-ui/editor-plugin-code-syntax-highlight";
// import { result } from 'lodash';

export default function PostView() {
  const location = useLocation();
  const postcontent = location.state;
  // const [myrepo, setMyInfo] = useState("");

  // async function getRepo() {
  //   await http
  //     .post("repo/", {
  //       email: repoData,
  //     })
  //     .then((response) => {
  //       // alert(response.data.nickname);
  //       console.log(response.code);
  //       setMyInfo(response.code);
  //     })
  //     .catch(() => alert("error"));
  //   // console.log(myinfo);
  // }

  // useEffect(() => {
  //   getRepo();
  // }, []);

  return (
    <div className="w-full m-4 p-6 bg-white h-screen shadow-lg rounded-2xl dark:bg-gray-700 ">
      <div>제목</div>
      <div> 생성시간</div>
      <div>좋아요 수</div>

      <div>
        <RepoPostDetail/>
      </div>
      
      <Viewer
        initialValue={postcontent}
        plugins={[[codeSyntaxHighlight, { highlighter: Prism }]]}
      />
    </div>
  );
}
