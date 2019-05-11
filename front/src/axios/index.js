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

export default http
