// axios の共通設定
import Axios from 'axios'

let baseURL = ''
if(process.env.NODE_ENV === 'test') {
  baseURL = 'http://localhost:8089' // バックエンドのURL:port を指定する
}

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

export default http
