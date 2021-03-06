package com.marklogic.hub.core;

import com.marklogic.hub.DatabaseKind;
import com.marklogic.hub.HubConfig;
import com.marklogic.hub.HubTestBase;
import com.marklogic.hub.error.DataHubConfigurationException;
import com.marklogic.hub.impl.HubConfigImpl;
import org.apache.commons.io.FileUtils;
//import org.apache.htrace.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;


import com.fasterxml.jackson.databind.ObjectMapper;


public class HubConfigTest extends HubTestBase {


    private static File projectPath = new File(PROJECT_PATH);

    @Before
    public void setup() throws IOException {
        FileUtils.deleteDirectory(projectPath);
        HubConfig config = getHubFlowRunnerConfig();
        config.initHubProject();
    }

    private void deleteProp(String key) {
        try {
            File gradleProperties = new File(projectPath, "gradle.properties");
            Properties props = new Properties();
            FileInputStream fis = new FileInputStream(gradleProperties);
            props.load(fis);
            fis.close();
            props.remove(key);
            FileOutputStream fos = new FileOutputStream(gradleProperties);
            props.store(fos, "");
            fos.close();
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void writeProp(String key, String value) {
        try {
            File gradleProperties = new File(projectPath, "gradle.properties");
            Properties props = new Properties();
            FileInputStream fis = new FileInputStream(gradleProperties);
            props.load(fis);
            fis.close();
            props.put(key, value);
            FileOutputStream fos = new FileOutputStream(gradleProperties);
            props.store(fos, "");
            fos.close();
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testLoadBalancerProps() {
        deleteProp("mlLoadBalancerHosts");
        assertNull(getHubFlowRunnerConfig().getLoadBalancerHosts());

        writeProp("mlLoadBalancerHosts", "");
        assertNull(getHubFlowRunnerConfig().getLoadBalancerHosts());

        writeProp("mlLoadBalancerHosts", "host1,host2");
        HubConfig config = getHubFlowRunnerConfig();
        assertEquals(2, config.getLoadBalancerHosts().length);
        assertEquals("host1", config.getLoadBalancerHosts()[0]);
        assertEquals("host2", config.getLoadBalancerHosts()[1]);

        writeProp("mlLoadBalancerHosts", "host1");
        config = getHubFlowRunnerConfig();
        assertEquals(1, config.getLoadBalancerHosts().length);
        assertEquals("host1", config.getLoadBalancerHosts()[0]);
    }


    @Test
    public void testHubInfo() {

        HubConfig config = getHubFlowRunnerConfig();
        ObjectMapper objmapper = new ObjectMapper();

        try {

            JsonNode jsonNode = objmapper.readTree(config.getInfo());

            assertTrue(jsonNode.get("stagingDbName").asText().equals(config.getDbName(DatabaseKind.STAGING)));

            assertTrue(jsonNode.get("stagingHttpName").asText().equals(config.getHttpName(DatabaseKind.STAGING)));

            assertTrue(jsonNode.get("finalForestsPerHost").asInt() == config.getForestsPerHost(DatabaseKind.FINAL));

            assertTrue(jsonNode.get("finalPort").asInt() == config.getPort(DatabaseKind.FINAL));

        }
        catch (Exception e)
        {
            throw new DataHubConfigurationException("Your datahub configuration could not serialize");
        }
    }


}
