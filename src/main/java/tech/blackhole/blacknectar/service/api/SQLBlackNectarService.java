/*
 * Copyright 2016 BlackWholeLabs.
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

 
package tech.blackhole.blacknectar.service.api;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sir.wellington.alchemy.collections.lists.Lists;
import tech.aroma.client.Aroma;
import tech.aroma.client.Urgency;
import tech.blackhole.blacknectar.service.exceptions.OperationFailedException;
import tech.blackhole.blacknectar.service.stores.Store;
import tech.sirwellington.alchemy.annotations.arguments.Required;

import static tech.sirwellington.alchemy.arguments.Arguments.checkThat;
import static tech.sirwellington.alchemy.arguments.assertions.Assertions.notNull;
import static tech.sirwellington.alchemy.arguments.assertions.BooleanAssertions.falseStatement;
import static tech.sirwellington.alchemy.arguments.assertions.NumberAssertions.greaterThanOrEqualTo;

/**
 *
 * @author SirWellington
 */
final class SQLBlackNectarService implements BlackNectarService
{
    private final static Logger LOG = LoggerFactory.getLogger(SQLBlackNectarService.class);
    
    private final Aroma aroma;
    private final Connection connection;

    SQLBlackNectarService(@Required Aroma aroma, @Required Connection connection) throws SQLException
    {
        checkThat(aroma, connection)
            .are(notNull());

        this.aroma = aroma;

        boolean isClosed = connection.isClosed();
        checkThat(isClosed)
            .usingMessage("connection is closed")
            .throwing(SQLException.class)
            .is(falseStatement());

        this.connection = connection;
    }

    @Override
    public List<Store> getAllStores(int limit)
    {
        checkThat(limit)
            .usingMessage("limit must be >= 0")
            .is(greaterThanOrEqualTo(0));

        Statement statement = createStatementToGetAllStores(limit);

        return Lists.emptyList();
    }

    @Override
    public List<Store> searchForStores(BlackNectarSearchRequest request) throws OperationFailedException
    {
        checkThat(request)
            .usingMessage("request missing")
            .is(notNull());
        
        String query = createQueryForRequest(request);

        return Lists.emptyList();
    }

    void addStore(@Required Store store) throws OperationFailedException
    {
        checkThat(store).is(notNull());

        String insertStatement = createSQLToInsertStore();
        PreparedStatement statement = tryToPrepareStatement(insertStatement, "could not save Store to Database: " + store);

        try
        {
            prepareStatementForStore(statement, store);
        }
        catch (SQLException ex)
        {
            LOG.error("Failed to prepare statement to insert Store [{}]", store, ex);

            aroma.begin().titled("SQL Failed")
                .text("Could not prepare statement to insert Store: [{}]", store, ex)
                .withUrgency(Urgency.HIGH)
                .send();

            throw new OperationFailedException(ex);
        }

        try
        {
            int count = statement.executeUpdate();
            LOG.info("Successfully executed statement to insert Store. Received count {} for store [{}]", count, store);
        }
        catch (SQLException ex)
        {
            LOG.error("Failed to execute statement to insert Store: [{}]", store, ex);

            aroma.begin().titled("SQL Failed")
                .text("Could not execute SQL to insert Store [{}]", store, ex)
                .withUrgency(Urgency.HIGH)
                .send();

            throw new OperationFailedException(ex);
        }

    }

    private Statement createStatementToGetAllStores(int limit) throws OperationFailedException
    {
        String sql = createSQLTOGetAllStores(limit);

        try
        {
            return connection.prepareStatement(sql);
        }
        catch (SQLException ex)
        {
            LOG.error("Failed to create statement to get all stores with limit {}", limit);
            aroma.begin().titled("SQL Failed")
                .text("Could not create statement to get all stores: {}", ex)
                .send();
            throw new OperationFailedException(ex);
        }
    }

    private String createSQLTOGetAllStores(int limit)
    {
        if (limit > 0)
        {
            return "SELECT * FROM stores";
        }
        else
        {
            return "SELECT * FROM stores LIMIT " + limit;
        }
    }

    private String createSQLToInsertStore()
    {
        return "INSERT INTO Stores(store_name, latitude, longitude, address, address_line_two, city, state, county, zip, local_zip)\n" +
               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    private PreparedStatement tryToPrepareStatement(String insertStatement, String message)
    {
        try
        {
            return connection.prepareStatement(insertStatement);
        }
        catch (SQLException ex)
        {
            LOG.error(message, ex);

            aroma.begin().titled("SQL Failed")
                .text(message)
                .withUrgency(Urgency.HIGH)
                .send();

            throw new OperationFailedException(ex);
        }
    }

    void prepareStatementForStore(PreparedStatement statement, Store store) throws SQLException
    {
        statement.setString(1, store.getName());
        statement.setDouble(2, store.getLocation().getLatitude());
        statement.setDouble(3, store.getLocation().getLongitude());
        statement.setString(4, store.getAddress().getAddressLineOne());
        statement.setString(5, store.getAddress().getAddressLineTwo());
        statement.setString(6, store.getAddress().getCity());
        statement.setString(7, store.getAddress().getState());
        statement.setString(8, store.getAddress().getCounty());
        statement.setString(9, "" + store.getAddress().getZip5());
        statement.setString(10, "" + store.getAddress().getZip4());
    }

    String getStatementToCreateTable()
    {
        return "CREATE TABLE IF NOT EXISTS Stores\n" +
               "(\n" +
               "    store_name text,\n" +
               "    latitude numeric,\n" +
               "    longitude numeric,\n" +
               "    address text,\n" +
               "    address_line_two text,\n" +
               "    city text,\n" +
               "    state text,\n" +
               "    county text,\n" +
               "    zip text,\n" +
               "    local_zip text,\n" +
               "\n" +
               "    PRIMARY KEY(store_name, latitude, longitude)\n" +
               ");\n" +
               "";
    }

    private String createQueryForRequest(BlackNectarSearchRequest request)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    static class Keys
    {

        static final String STORE_NAME = "store_name";
        static final String LATITUDE = "latitude";
        static final String LONGITUDE = "longitude";
        static final String ADDRESS = "address";
        static final String ADDRESS_LINE_TWO = "address_line_two";
        static final String CITY = "city";
        static final String STATE = "state";
        static final String COUNTY = "county";
        static final String ZIP_CODE = "zip_code";
        static final String LOCAL_ZIP_CODE = "local_zip_code";
    }

}
