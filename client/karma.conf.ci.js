// Karma configuration file, see link for more information
// https://karma-runner.github.io/0.13/config/configuration-file.html

// This configuration file can be used to test on Jenkins. Although it's not
// currently enabled, it should work once Chrome or Chromium is installed and
// properly configured on the CI server according to puppeteer's instructions

const puppeteer = require('puppeteer');
process.env.CHROMIUM_BIN = puppeteer.executablePath();

module.exports = function (config) {
  config.set({
    basePath: '',
    frameworks: ['jasmine', '@angular/cli'],
    plugins: [
      require('karma-jasmine'),
      require('karma-chrome-launcher'),
      require('karma-jasmine-html-reporter'),
      require('karma-coverage-istanbul-reporter'),
      require('@angular/cli/plugins/karma'),
      require('karma-junit-reporter'),
    ],
    client:{
      clearContext: false // leave Jasmine Spec Runner output visible in browser
    },
    files: [
      { pattern: './src/test.ts', watched: false }
    ],
    preprocessors: {
      './src/test.ts': ['@angular/cli']
    },
    mime: {
      'text/x-typescript': ['ts','tsx']
    },
    coverageIstanbulReporter: {
      reports: [ 'html', 'lcovonly' ],
      fixWebpackSourcePaths: true
    },
    angularCli: {
      environment: 'dev'
    },
    reporters: ['dots', 'junit'],
    junitReporter: {
      outputFile: 'test-results.xml'
    },
    port: 9876,
    colors: true,
    logLevel: config.LOG_INFO,
    autoWatch: true,
    browsers: ['ChromiumHeadlessNoSandbox'],
    customLaunchers: {
        ChromiumHeadlessNoSandbox: {
            base: 'ChromiumHeadless',
            flags: [
              // If CI fails to provide sandboxed chrome environment, one of
              // these flags might help.
              //
              // NOTE: Disabling sandboxing is not recommended as it opens a
              // door for malicious code that can come from compromised npm
              // packages.
              //
              // '--no-sandbox',
              // '--disable-setuid-sandbox',
              // '--disable-gpu',
              // '--no-proxy-server'
            ]
          }
    },
    singleRun: true
  });
};
