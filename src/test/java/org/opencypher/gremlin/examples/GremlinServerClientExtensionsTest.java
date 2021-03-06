package org.opencypher.gremlin.examples;

import static org.opencypher.gremlin.Util.getFile;

import java.util.List;
import java.util.Map;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.junit.Test;
import org.opencypher.gremlin.client.CypherGremlinClient;
import org.opencypher.gremlin.client.CypherResultSet;
import org.opencypher.gremlin.translation.translator.Translator;
import org.opencypher.gremlin.translation.translator.TranslatorFlavor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This test shows how to configure a Cypher Gremlin Server client
 * that allows Cypher extensions in translation.
 */
public class GremlinServerClientExtensionsTest {
    private static final TranslatorFlavor FLAVOR = TranslatorFlavor.gremlinServer();

    private static final Logger logger = LoggerFactory.getLogger(GremlinServerClientExtensionsTest.class);

    /**
     * Note that <a href="https://github.com/opencypher/cypher-for-gremlin/tree/master/tinkerpop/cypher-gremlin-extensions">Gremlin Cypher Extensions</a>
     * should be added to the target Gremlin Server classpath in order for this to work.
     */
    @Test
    public void gremlinServerClient() throws Exception {
        String config = getFile("remote.yaml");

        Cluster cluster = Cluster.open(config);
        Client gremlinClient = cluster.connect();
        CypherGremlinClient cypherGremlinClient =
            CypherGremlinClient.translating(gremlinClient, () -> Translator.builder()
                .gremlinGroovy()
                .enableCypherExtensions()
                .build(FLAVOR));

        String cypher = "RETURN 'test' + toString(1) as result";
        CypherResultSet resultSet = cypherGremlinClient.submit(cypher);
        List<Map<String, Object>> results = resultSet.all();

        logger.info("Result: {}", results);
    }
}
