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


package tech.blacksource.blacknectar.service.api;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import tech.aroma.client.Aroma;
import tech.blacksource.blacknectar.service.exceptions.BadArgumentException;
import tech.blacksource.blacknectar.service.exceptions.BlackNectarAPIException;
import tech.blacksource.blacknectar.service.exceptions.OperationFailedException;
import tech.blacksource.blacknectar.service.stores.Store;
import tech.sirwellington.alchemy.annotations.arguments.Required;
import tech.blacksource.blacknectar.service.stores.StoreDataSource;


/*
 * TODO: Rename this interface to StoreDataSource.
 */
/**
 * The BlackNectarService serves as the Backbone for the REST API.
 * 
 * It allows for querying EBT stores by name and location.
 * 
 * @author SirWellington
 */
public interface BlackNectarService 
{
    /**
     * The default radius, in meters, used in queries where a radius is not provided.
     * <p>
     * 5 Kilometers.
     */
    public double DEFAULT_RADIUS = 5_000;
    
    /**
     * Adds a Store to the repository.
     * 
     * @param store The store to add.
     * @throws BadArgumentException If the argument is null or invalid.
     */
    public void addStore(@Required Store store) throws BlackNectarAPIException;

    /**
     * Get all of the EBT stores in the country.
     * 
     * @return  All of the Stores.
     */
    default List<Store> getAllStores() throws BlackNectarAPIException
    {
        return getAllStores(0);
    }
    
    /**
     * Get all of the EBT stores, with a specified limit.
     * 
     * @param limit A limit on the query, so that no more than {@code limit} stores are returned. Must be {@code >= 0}. A value of 0 means
     *              no limit.
     *
     * @return 
     */
    List<Store> getAllStores(int limit) throws BlackNectarAPIException;
    
    /**
     * Searches for stores that match the given criteria.
     * 
     * @param request
     * @return
     * 
     * @throws OperationFailedException 
     */
    List<Store> searchForStores(@Required BlackNectarSearchRequest request) throws BlackNectarAPIException;
    
    /**
     * Creates a new in-memory service that performs all operations in-memory.
     * 
     * @return 
     */
    static BlackNectarService newMemoryService()
    {
        List<Store> stores = StoreDataSource.FILE.getAllStores();
        GeoCalculator formula = GeoCalculator.HARVESINE;

        return new MemoryBlackNectarService(stores, formula);
    }

    
    /**
     * Creates a new SQL-backed Service that performs all operations against 
     * a JDBC connection.
     * 
     * @param connection The JDBC connection, must be open.
     * 
     * @return
     * 
     * @throws SQLException 
     */
    static BlackNectarService newSQLService(@Required Connection connection) throws SQLException
    {
        return newSQLService(Aroma.create(),
                             connection,
                             GeoCalculator.HARVESINE);
    }
    
    /**
     * Creates a new SQL-backed Service that performs all operations against 
     * a JDBC connection.
     * 
     * @param aroma
     * @param connection The JDBC connection, must be open.
     * @param geoCalculator Used to make Geodetic calculations
     * 
     * @return
     * 
     * @throws SQLException 
     */
    static BlackNectarService newSQLService(@Required Aroma aroma,
                                            @Required Connection connection,
                                            @Required GeoCalculator geoCalculator) throws SQLException
    {
        return new SQLBlackNectarService(aroma, connection, geoCalculator, SQLStoreMapper.INSTANCE);
    }
}
