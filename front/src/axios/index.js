// axios の共通設定
import Axios from 'axios'

const http = Axios.create({
  // for cors
  withCredentials: true,
  headers:{
    'Content-Type': 'application/json',
    'Accept': '*/*'
  },
  data: {}
//  baseURL: 'http://localhost:8089', // バックエンドのURL:port を指定する
})

export default http
