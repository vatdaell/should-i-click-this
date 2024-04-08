## 1.0.0 (2024-04-07)

> Description

### New Features

* Domain and Link verification with the Phishing.db
* Use of redis to store suspicious domains and links
* Added scheduling to pull data from Phishing.db every hour
* Api routes for Link and Domain verification added for use
  by [Should I click this?](https://github.com/vatdaell/should-i-click-this-frontend) frontend
* Rate limiter added to prevent overusage of Api
