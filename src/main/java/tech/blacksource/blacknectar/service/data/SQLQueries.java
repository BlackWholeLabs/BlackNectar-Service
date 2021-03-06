/*
 * Copyright 2017 BlackSource, LLC.
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

package tech.blacksource.blacknectar.service.data;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.sirwellington.alchemy.annotations.access.Internal;
import tech.sirwellington.alchemy.annotations.access.NonInstantiable;
import tech.sirwellington.alchemy.annotations.arguments.NonEmpty;

import static tech.sirwellington.alchemy.arguments.Arguments.*;
import static tech.sirwellington.alchemy.arguments.assertions.StringAssertions.nonEmptyString;

/**
 * Loads SQL queries into memory.
 *
 * @author SirWellington
 */
@Internal
@NonInstantiable
public final class SQLQueries
{

    private final static Logger LOG = LoggerFactory.getLogger(SQLQueries.class);

    private static final String PATH_PREFIX = "sql/postgresql";

    public static final String CREATE_ADDRESS_TABLE = loadQuery("create_addresses.sql");
    public static final String CREATE_STORES_TABLE = loadQuery("create_stores.sql");

    //Counts
    public static final String CONTAINS_STORE = loadQuery("contains_store.sql");
    public static final String COUNT_IMAGES_FOR_STORE = loadQuery("count_images_for_store.sql");

    //Deletes
    public static final String DELETE_IMAGE = loadQuery("delete_image.sql");
    public static final String DELETE_IMAGES_FOR_STORE = loadQuery("delete_store_images.sql");
    public static final String DELETE_STORE = loadQuery("delete_store.sql");

    //Insert Statements
    public static final String INSERT_GOOGLE_DATA = loadQuery("insert_google_data.sql");
    public static final String INSERT_GOOGLE_PHOTO = loadQuery("insert_google_places_photo.sql");
    public static final String INSERT_STORE = loadQuery("insert_store.sql");
    public static final String INSERT_STORE_IMAGE = loadQuery("insert_store_image.sql");
    public static final String INSERT_IMAGE = loadQuery("insert_image.sql");

    //Queries
    public static final String QUERY_STORES_WITH_LOCATION = loadQuery("query_stores_with_location.sql");
    public static final String QUERY_STORES_WITH_NAME = loadQuery("query_stores_with_name.sql");
    public static final String QUERY_STORES_WITH_ZIPCODE = loadQuery("query_stores_with_zipcode.sql");

    public static final String QUERY_STORES_WITH_NAME_AND_LOCATION = loadQuery("query_stores_with_name_and_location.sql");
    public static final String QUERY_STORES_WITH_NAME_AND_ZIPCODE = loadQuery("query_stores_with_name_and_zipcode.sql");

    public static final String QUERY_IMAGE = loadQuery("query_image.sql");
    public static final String QUERY_IMAGES_FOR_STORE = loadQuery("query_images_for_store.sql");

    //Updates
    public static final String UPDATE_STORE = loadQuery("update_store.sql");

    SQLQueries() throws IllegalAccessException
    {
        throw new IllegalAccessException("cannot instantiate");
    }

    private static String loadQuery(@NonEmpty String queryName) throws RuntimeException
    {
        checkThat(queryName)
                .is(nonEmptyString());

        String path = PATH_PREFIX + "/" + queryName;

        try
        {
            URL resource = Resources.getResource(path);
            String string = Resources.toString(resource, Charsets.UTF_8);

            return string;
        }
        catch (IOException ex)
        {
            LOG.error("Failed to load SQL at: {}", path, ex);
            throw new RuntimeException("Could not load query at: " + path, ex);
        }
    }
}
