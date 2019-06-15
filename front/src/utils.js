import Moment from 'moment'

export default class Utils {
  /**
   * ダイアログOpen,
   * @param {String} id ID要素.
   */
  static openDialog(id) {
    document.querySelector('html').classList.add('is-clipped');
    document.getElementById(id).classList.add('is-active');
  }

  /**
   * ダイアログClose.
   * @param {String} id ID要素
   */
  static closeDialog(id) {
    document.querySelector('html').classList.remove('is-clipped');
    document.getElementById(id).classList.remove('is-active');
  }

  /**
   * 日付文字列変換.
   * @param epochMillis 対象時間(Epoc ミリ秒)
   */
  static dateToString(epochMillis) {
    if(epochMillis === null) {
      return ""
    }
    const moment = Moment(epochMillis)
    return moment.format("YYYY-MM-DD")
  }
}
