## 2.0.0 (2024-04-08)

```
This release introduces significant enhancements focused on optimizing backend operations and broadening the scope of security verification. To accommodate local testing, the use of docker-compose up is now recommended for running KeyDb, a proactive shift in response to recent changes in the Redis license. Users are advised to update their spring.data.redis.url to align with this new setup. With KeyDb, the application expects to see improved performance, positioning itself well for future adaptability, especially in scenarios where third-party Redis services may no longer be viable. Furthermore, the integration of the OpenPhish community data feed extends the application's capability to identify suspicious links, enriching its database with a wider array of potential threats. A new /api/consolidated route has been added, enabling a comprehensive verification search across both OpenPhish and Phishing.db databases, thereby enhancing the overall efficacy of the platform in safeguarding against online phishing threats
```

### Upgrade Steps

* For local testing, usie `docker-compose up` to run keydb.
* Make sure to update `spring.data.redis.url` accordingly.

### New Features

* Due to recent [redis license](https://redis.com/legal/licenses/)
  changes, [KeyDb](https://docs.keydb.dev/) is used as a drop in replacement. KeyDb promises better
  performance and will be used in the future if third party redis services are stopped.
* Added [OpenPhish data community feed](https://openphish.com/phishing_feeds.html) for more links
  that are suspicious.
* Added an `/api/consolidated` route to do a verification search on OpenPhish and Phishing.db
  databases
