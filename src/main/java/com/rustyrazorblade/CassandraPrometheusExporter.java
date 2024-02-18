package com.rustyrazorblade;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.hotspot.DefaultExports;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.GzipHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.apache.cassandra.metrics.CassandraMetricsRegistry;

import java.lang.instrument.Instrumentation;


public class CassandraPrometheusExporter {
    public static void premain(String agentArgs, Instrumentation inst) {
        // does this need to run in a separate thread?

        // create new thread
        Thread thread = new Thread(() -> {
            do {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Waiting for metrics to be ready");
            } while (CassandraMetricsRegistry.Metrics == null);

            System.out.println("Metrics are ready, starting server");

            CollectorRegistry.defaultRegistry.register(new FilteredDropwizardExports(CassandraMetricsRegistry.Metrics));
            // Expose Prometheus metrics.
            Server server = new Server(1234);
            ServletContextHandler context = new ServletContextHandler();
            context.setContextPath("/");

//            GzipHandler gzipHandler = new GzipHandler();
//            gzipHandler.setMimeTypes("text/plain") ;
//            gzipHandler.setHandler(context);

            server.setHandler(context);
            context.addServlet(new ServletHolder(new MetricsServlet()), "/metrics");

            // Add metrics about CPU, JVM memory etc.
            // Start the webserver.
            try {
                server.start();
                server.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.setName("CassandraPrometheusExporter");
        thread.start();

    }
}