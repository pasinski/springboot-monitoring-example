/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.consol.RestServiceDemo;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.common.TextFormat;
import io.prometheus.client.hotspot.DefaultExports;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Paths;

@Component
@Path("/")
public class Metrics {

	private final Logger logger = LogManager.getLogger(Metrics.class);

	private final Counter promRequestsTotal = Counter.build()
					.name("requests_total_mp")
					.help("Total number of requests.")
					.register();

	private final Gauge freeDiskSpace = Gauge.build()
            .name("free_disk_space_percent")
            .help("Free disk space")
            .register();
  {
    DefaultExports.initialize();
  }

  @GET()
  @Path("/hello-world")
  @Produces(MediaType.TEXT_PLAIN)
  public String sayHello() {
    promRequestsTotal.inc();
    return "hello, world";
  }

  @GET()
  @Path("/metrics")
  @Produces(MediaType.TEXT_PLAIN)
  public StreamingOutput metrics() {

    File f = Paths.get("/data").toFile();

    logger.info("Starting service for metrics");
    freeDiskSpace.set( f.getFreeSpace() / f.getTotalSpace() * 100 );
    return output -> {
      try (Writer writer = new OutputStreamWriter(output)) {
        TextFormat.write004(writer, CollectorRegistry.defaultRegistry.metricFamilySamples());
      }
    };
  }
}
