export class StringUtils {

  static isBlank(text: string): boolean {
    return (!text || /^\s*$/.test(text));
  }

  static isNotBlank(text: string): boolean {
    return !this.isBlank(text)
  }

  static isEmpty(text: string): boolean {
    return (!text || 0 === text.length)
  }

  static isNotEmpty(text: string): boolean {
    return !this.isEmpty(text)
  }

}
