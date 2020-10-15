package org.jboss.feedsagg.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.Bean;

import org.bson.Document;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

import com.mongodb.client.MongoClient;

import io.quarkus.arc.Arc;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;

@Readiness
@ApplicationScoped
public class ReactiveMongoHealthCheck implements HealthCheck {

    private static final String DEFAULT_CLIENT = "__default__";
    private Map<String, MongoClient> clients = new HashMap<>();
    private Map<String, ReactiveMongoClient> reactiveClients = new HashMap<>();

    @PostConstruct
    protected void init() {
        Set<Bean<?>> beans = Arc.container().beanManager().getBeans(MongoClient.class);
        for (Bean<?> bean : beans) {
            if (bean.getName() == null) {
                // this is the default mongo client: retrieve it by type
                MongoClient defaultClient = Arc.container().instance(MongoClient.class).get();
                clients.put(DEFAULT_CLIENT, defaultClient);
            } else {
                MongoClient client = (MongoClient) Arc.container().instance(bean.getName()).get();
                clients.put(bean.getName(), client);
            }
        }

        Set<Bean<?>> beansReactive = Arc.container().beanManager().getBeans(ReactiveMongoClient.class);
        for (Bean<?> bean : beansReactive) {
            if (bean.getName() == null) {
                // this is the default mongo client: retrieve it by type
                ReactiveMongoClient defaultClient = Arc.container().instance(ReactiveMongoClient.class).get();
                reactiveClients.put(DEFAULT_CLIENT, defaultClient);
            } else {
                ReactiveMongoClient client = (ReactiveMongoClient) Arc.container().instance(bean.getName()).get();
                reactiveClients.put(bean.getName(), client);
            }
        }

    }

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder builder = HealthCheckResponse.named("MongoDB connection health check").up();
        for (Map.Entry<String, MongoClient> client : clients.entrySet()) {
            boolean isDefault = DEFAULT_CLIENT.equals(client.getKey());
            MongoClient mongoClient = client.getValue();
            try {
                Document document = mongoClient.getDatabase("admin").runCommand(new Document("ping", 1));
                String mongoClientName = isDefault ? "default" : client.getKey();
                builder.up().withData(mongoClientName, document.toJson());
            } catch (Exception e) {
                return builder.down().withData("reason", e.getMessage()).build();
            }
        }

        for (Map.Entry<String, ReactiveMongoClient> client : reactiveClients.entrySet()) {
            boolean isDefault = DEFAULT_CLIENT.equals(client.getKey());
            ReactiveMongoClient mongoClient = client.getValue();
            try {
                // consider instead of indefinitely perform atMost(duration)
                Document document = mongoClient.getDatabase("admin").runCommand(new Document("ping", 1)).await().indefinitely();

                String mongoClientName = isDefault ? "default" : client.getKey();
                builder.up().withData(mongoClientName, document.toJson());
            } catch (Exception e) {
                return builder.down().withData("reason", e.getMessage()).build();
            }
        }
        return builder.build();
    }
}
