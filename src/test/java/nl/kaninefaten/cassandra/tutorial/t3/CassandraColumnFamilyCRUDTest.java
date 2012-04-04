package nl.kaninefaten.cassandra.tutorial.t3;

import static org.junit.Assert.fail;

import java.util.List;

import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;

import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * HelloWorld Unit test for an embedded cassandra server.
 * <p>
 * <ul>
 * 	<li>This class creates an embedded server starts it.
 * 	<li>Creates a ColumnFamily
 * 	<li>Reads  a ColumnFamily.
 * 	<li>Updates a ColumnFamily.
 * 	<li>Deletes a ColumnFamily.
 *  <li>Stops de server and exits the jvm
 * </ul>
 * <p>
 * The code is demonstration code on how the hector api can be used.
 * 
 * @author Patrick van Amstel
 * @date 2012 04 02
 *
 */
public class CassandraColumnFamilyCRUDTest  {

	/** Name of test cluster*/
	public static String clusterName = "TestCluster";
	
	/** Name and port of test cluster*/
	public static String host = "localhost:9171";
	
	/** Name of key space to create*/
	public static String keyspaceName = "keySpaceName";
	
	/** Name of the column family*/
	public static String columnFamilyName = "AColumnFamily";
	
	/** Cluster to talk to*/
	private static Cluster cluster = null;
	
	// Before these unit tests are called initialize the junit class with this server
	@BeforeClass
	public static void start() throws Throwable {
		EmbeddedCassandraServerHelper.startEmbeddedCassandra();

		// Gets the thrift client with name an host
		cluster = HFactory.getOrCreateCluster(clusterName, host);

		KeyspaceDefinition newKeyspace = HFactory.createKeyspaceDefinition(keyspaceName, ThriftKsDef.DEF_STRATEGY_CLASS, 1, null);

		cluster.addKeyspace(newKeyspace);
	}

	// Break down the server when unit tests are executed.
	@AfterClass
	public static void stop(){
		EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
	}
		
	@Test
	public void testCreate() {
		// Create
		try {
			ColumnFamilyDefinition userColumnFamilyDefinition = HFactory.createColumnFamilyDefinition(keyspaceName, columnFamilyName, ComparatorType.UTF8TYPE);
			cluster.addColumnFamily(userColumnFamilyDefinition);
		} catch (Throwable t) {
			fail(t.toString());
		}
	}
	
	@Test
	public void testRead() {
		// Read
		try {
			List<ColumnFamilyDefinition> columnFamilyDefinitions = cluster.describeKeyspace(keyspaceName).getCfDefs();

			ColumnFamilyDefinition foundColumnFamilyDefinition = null;

			for (ColumnFamilyDefinition columnFamilyDefinition : columnFamilyDefinitions) {
				if (columnFamilyName.equals(columnFamilyDefinition.getName())) {
					foundColumnFamilyDefinition = columnFamilyDefinition;
				}
			}

			if (foundColumnFamilyDefinition == null) {
				fail("Could not find columnFamilyDefinition with name " + columnFamilyName);
			}
		} catch (Throwable t) {
			fail(t.toString());
		}
	}

	@Test
	public void testUpdate() {
		
		try {
			
			// Find the column definition
			List<ColumnFamilyDefinition> columnFamilyDefinitions = cluster.describeKeyspace(keyspaceName).getCfDefs();

			ColumnFamilyDefinition foundColumnFamilyDefinition = null;

			for (ColumnFamilyDefinition columnFamilyDefinition : columnFamilyDefinitions) {
				if (columnFamilyName.equals(columnFamilyDefinition.getName())) {
					foundColumnFamilyDefinition = columnFamilyDefinition;
				}
			}
			if (foundColumnFamilyDefinition == null) {
				fail("Could not find columnFamilyDefinition with name " + columnFamilyName);
			}

			// Change the definition
			foundColumnFamilyDefinition.setComment("Commentaar");
			// Change the definition
			cluster.updateColumnFamily(foundColumnFamilyDefinition);
			
			// Find the new one
			foundColumnFamilyDefinition = null;
			List<ColumnFamilyDefinition> updatedColumnFamilyDefinitions = cluster.describeKeyspace(keyspaceName).getCfDefs();
			for (ColumnFamilyDefinition columnFamilyDefinition : updatedColumnFamilyDefinitions) {
				if (columnFamilyName.equals(columnFamilyDefinition.getName())) {
					foundColumnFamilyDefinition = columnFamilyDefinition;
				}
			}
			if (foundColumnFamilyDefinition == null) {
				fail("Could not find columnFamilyDefinition with name " + columnFamilyName);
			}
			// Test update
			if (!foundColumnFamilyDefinition.getComment().equals("Commentaar")){
				fail("The is not succesful");
			}
			
		} catch (Throwable t) {
			fail(t.toString());
		}
	}
	
	@Test
	public void testDelete() {
		
		try {
			// delete / drop
			cluster.dropColumnFamily(keyspaceName, columnFamilyName);
			
			// Try to find
			List<ColumnFamilyDefinition> columnFamilyDefinitions = cluster.describeKeyspace(keyspaceName).getCfDefs();

			ColumnFamilyDefinition foundColumnFamilyDefinition = null;

			for (ColumnFamilyDefinition columnFamilyDefinition : columnFamilyDefinitions) {
				if (columnFamilyName.equals(columnFamilyDefinition.getName())) {
					foundColumnFamilyDefinition = columnFamilyDefinition;
				}
			}

			// If found fail because delete failed
			if (foundColumnFamilyDefinition != null) {
				fail("Could find columnFamilyDefinition with name " + columnFamilyName);
			}
		} catch (Throwable t) {
			fail(t.toString());
		}
	}
	
}
