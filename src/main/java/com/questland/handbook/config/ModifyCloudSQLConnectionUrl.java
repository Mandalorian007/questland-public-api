package com.questland.handbook.config;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Component;

@Component
public class ModifyCloudSQLConnectionUrl implements BeanPostProcessor {
    /*
    Spring Cloud GCP -> Cloud MySQL starter does not allow for direct updates to the
    jdbc url anymore so this is a hack intended to forcefully update the connection settings for MySQL
     */

    @Override
    public final Object postProcessAfterInitialization(final Object bean, final String beanName) {
        if (bean instanceof DataSourceProperties) {
            final DataSourceProperties properties = (DataSourceProperties) bean;
            String url = properties.getUrl();
            //Only modify a CloudSQL url
            if (url != null && url.contains("socketFactory")) {
                url = url + "&rewriteBatchedStatements=true";
            }
            properties.setUrl(url);
        }
        return bean;
    }
}
