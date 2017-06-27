export class HttpMessageHelper {
  static getErrorMessageByStatusCode(status) {
    if (status) {
      if (status == 404) {
        return 'errors.server.notFound'
      }
      else if (status >= 400 && status < 500) {
        return 'errors.server.badRequest'
      }
      else if (status == 503 || status == 504) {
        return 'errors.server.unavailable'
      }
    }
    return 'errors.server.unknownError'
  }
}
