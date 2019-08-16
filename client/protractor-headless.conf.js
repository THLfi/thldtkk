protractor = require('./protractor.conf');
var config = protractor.config;

config.capabilities = {
  browserName: 'chrome',
  chromeOptions: {
     args: [ "--headless", "--disable-gpu" ]
   }
};

exports.config = config;
