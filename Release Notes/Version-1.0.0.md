## 1.0.0 (2024-03-21)

> Should I click this? backend is an application backend that extracts data from phishing.db. The
> backend stores suspicious links and domains into redis which is used to verify if the links are
> suspicious or not. The application has a rate limiter to prevent overusage. The application also has
> basic authentication to only allow verified applications to make requests.

### New Features

* Domain and Link verification with the Phishing.db
* Use of redis to store suspicious domains and links
* Added scheduling to pull data from Phishing.db every hour
* Api routes for Link and Domain verification added for use
  by [Should I click this?](https://github.com/vatdaell/should-i-click-this-frontend) frontend
* Rate limiter added to prevent overusage of Api
