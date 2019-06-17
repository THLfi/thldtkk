import {StringUtils} from './string-utils';

describe('StringUtils', () => {
  describe('isBlank', () => {
    it('handles empty text', () => {
      expect(StringUtils.isBlank("")).toBeTruthy();
    });
    it('handles text with only spaces', () => {
      expect(StringUtils.isBlank("       ")).toBeTruthy();
    });
    it('handles null and undefined', () => {
      expect(StringUtils.isBlank(null)).toBeTruthy();
      expect(StringUtils.isBlank(undefined)).toBeTruthy();
    });
  });
  describe('isNotBlank', () => {
    it('handles null and undefined', () => {
      expect(StringUtils.isNotBlank(null)).toBeFalsy();
      expect(StringUtils.isNotBlank(undefined)).toBeFalsy();
    });
  });
});
