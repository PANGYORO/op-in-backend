## **๐Introduction**

> ์คํ์์ค ์ปจํธ๋ฆฌ๋ทฐํฐ๋ค์ ์ํ ์ปค๋ฎค๋ํฐ op-in  ์ ์๊ฒํฉ๋๋ค.
op-in์ ์คํ์์ค ๊ธฐ์ฌ์์ ๊ธฐ์ฌ์๋ฅผ ์ํด ๊ธฐ์ฌ์์ ์ํ ์ปค๋ฎค๋ํฐ ์๋น์ค์๋๋ค
op-in ์ git-hub ์ ๋ํฌ์งํ ๋ฆฌ๋ฅผ ๊ธฐ๋ฐ์ผ๋ก POST, QnA ๋ฅผ ์์ฑํ๊ณ  ์ํตํ๋ฉด์
์คํ์์ค ๊ธฐ์ฌ์ ์กฐ๊ธ๋ ํ์ฑํ ํด๋ณด๊ณ  ์ถ์ด ๋ง๋ค์ด์ง ์๋น์ค์๋๋ค.
>



## โจ Feature

### ๋ก๊ทธ์ธ/๋ก๊ทธ์์

### ํ ํฝ ์ ํ

### ๊ธฐ์ฌ๋ํฌ ์ถ์ฒ

### ๋ด ๋ํฌ์งํ ๋ฆฌ ์กฐํ

### ํฌ์คํธ ์์ฑ

### QnA  ์์ฑ

### ํํ ๋ฆฌ์ผ

## ๐คณDeveloped by

|   **Name**   |                ๋ฐ์ฑ์                 |                ์ ๋ฏผ์ง                |                  ์ด๋์ค                   |               ์กฐ์ฑ์ฑ                |                 ๊น๋ช์ง                  |               ๊น์ฐฝ์                |
| :----------: | :-----------------------------------: | :----------------------------------: | :---------------------------------------: | :---------------------------------: | :-------------------------------------: | :---------------------------------: |
| **Position** |          PM<br>FrontEnd           |          Backend           |           BackEnd            |        Frontend         |            Backend<br>release            |         FrontEnd         |
|   **Git**    | [GitHub](https://github.com/swany0509) | [GitHub](https://github.com/jellyKKing) | [GitHub](https://github.com/Djunnni) | [GitHub](https://github.com/chodone) | [GitHub](https://github.com/ManduTheCat) | [GitHub](https://github.com/kcy0521) |

## ์ํคํ์ณ

![img](mdIMG\์ต์ข๋ณธ.png)

## ๐๊ธฐ์  ์คํ
| Tech         | Stack                                  |
| ------------ | -------------------------------------- |
| **Language** | Java, JavaScript                       |
| **Backend**  | Spring Boot, JPA, Spring Security, JWT, Spring Batch |
| **Frontend** | React, Tailwind, Recoil                 |
| **Database** | Mysql                                |
| **Server**   | AWS EC2, Nginx, S3.                         |
| **DevOps**   | Git, Docker, Jenkins, SonarQube                           |
## ๐Package Structure

### ๐ฅBackend
```
โโโ auth
โย ย  โโโโ  ์ธ์ฆ ๊ด๋ จ ์๋น์ค, ์ปจํธ๋กค๋ฌ
โโโ batch
โ   โโโโ  ์คํ๋ง ๋ฐฐ์น ๊ด๋ จ ํจํค์ง
โโโ config
โโโ constant
โ     โโโโ GIT ์์ฒญ API
โโโ event
โย ย  โโโโ  ์ด๋ฒคํธ ๋๋ฉ์ธ ์๋น์ค, ์ปจํธ๋กค๋ฌ
โโโ exception
โย ย  โโโ  exceptionAdvice๊ด๋ จ
โโโ member
โย ย  โโโโ  ์ธ์ฆ ๊ด๋ จ ์๋น์ค, ์ปจํธ๋กค๋ฌ
โโโ persistence
โ   โโโโ  ์ํฐํฐ, ๋ํฌ์งํ ๋ฆฌ ์ธํฐํ์ด์ค
โโโ repo
โโโ search
    โโโโ  ๊ฒ์ ๊ด๋ จ ์๋น์ค, ์ปจํธ๋กค๋ฌ
```

### ๐จFrontend
```
โโโ api
โย ย  โโโ http.js
โโโ assets
โโโ components
โย ย  โโโ edu
โย ย  โโโ event
โย ย  โโโ modals
โย ย  โโโ repository
โย ย  โโโ user
โโโ constants
โโโ hooks
โโโ index.css
โโโ main.jsx
โโโ pages
โย ย  โโโ dashboard
โย ย  โโโ education
โย ย  โย ย  โโโ documents
โย ย  โย ย  โโโ tutorial
โย ย  โโโ repository
โย ย  โย ย  โโโ Recommand
โย ย  โย ย  โโโ following
โย ย  โย ย  โโโ main
โย ย  โโโ search
โย ย  โโโ user
โโโ recoil
```

### ๐ฅConvention
## Front
### ESLInt
```javascript
module.exports = {
    "env": {
        "browser": true,
        "es2021": true,
        "node": true
    },
    "extends": [
        "eslint:recommended",
        "plugin:react/recommended"
    ],
    "overrides": [
    ],
    "parserOptions": {
        "ecmaVersion": "latest",
        "sourceType": "module"
    },
    "plugins": [
        "react"
    ],
    "rules": {
        "react/prop-types": "off",
    },
}
```
### prettier
```javascript
{
  "tabWidth": 2,
  "semi": true,
  "trailingComma": "all",
  "printWidth": 80
}
```
## BackEnd