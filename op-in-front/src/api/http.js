import axios from "axios";

// axios 객체 생성
export default axios.create({
  baseURL: import.meta.env.VITE_API_URL, // "http://i8c211.p.ssafy.io:5001/",
  headers: {
    "Content-Type": "application/json;charset=utf-8",
  },
});