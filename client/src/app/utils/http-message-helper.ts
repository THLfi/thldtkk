export class HttpMessageHelper {
  static getErrorMessageByStatusCode(status) {
    if (status) {
      if (status == 404) {
        return 'errors.server.notFound'
      }
      if (status == 403) {
        return 'errors.server.forbidden'
      }
      if (status == 401) {
        return 'errors.server.unauthorized'
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
