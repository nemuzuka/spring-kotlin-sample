// axios の共通設定
import Axios from 'axios'
import baseURL from '../url'

const http = Axios.create({
  // for cors
  withCredentials: true,
  headers:{
    'Content-Type': 'application/json',
    'Accept': '*/*',
    'Access-Control-Allow-Credentials': true
  },
  data: {},
  baseURL: baseURL,
  responseType: 'json',
})

http.interceptors.response.use(
  (response) => {return response},
  (error) => {
  console.log(error)

  if (error.response.status === 401) {
    // 認証エラー時の処理
    window.location = "/#/login"
  } else if (error.response.status === 500) {
    // システムエラー時の処理
    window.location = "/#/error"
  }
  return Promise.reject(error)
})

export default http
