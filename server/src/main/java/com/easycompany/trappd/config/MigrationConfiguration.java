package com.easycompany.trappd.config;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MigrationConfiguration {

  @Autowired
  public MigrationConfiguration(DataSource dataSource) {

    Flyway.configure()
        .baselineOnMigrate(true)
        .dataSource(dataSource)
        .locations("classpath:/db/migration")
        .load()
        .migrate();
  }
}
