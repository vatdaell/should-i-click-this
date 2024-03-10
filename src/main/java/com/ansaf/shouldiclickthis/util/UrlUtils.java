package com.ansaf.shouldiclickthis.util;

import com.google.common.net.InternetDomainName;

import java.net.URL;

public class UrlUtils {
    public String extractDomain(String urlString) {
        try {
            if(urlString != null){
                if(!(urlString.startsWith("https://") || urlString.startsWith("http://"))){
                    urlString = "http://" + urlString;
                }
                URL url = new URL(urlString);
                String host = url.getHost();
                InternetDomainName domainName = InternetDomainName.from(host);
                if (domainName.isUnderPublicSuffix()) {
                    InternetDomainName topPrivateDomain = domainName.topPrivateDomain();
                    return topPrivateDomain.toString();
                } else {
                    return host;
                }
            }
            throw new Exception("Error processing the URL: " + urlString);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error processing the URL: " + urlString, e);
        }
    }
}
